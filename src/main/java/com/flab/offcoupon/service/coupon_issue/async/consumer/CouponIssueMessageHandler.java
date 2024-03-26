package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.domain.entity.Coupon;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.CountByCouponIdVo;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.exception.coupon.CouponIssueException;
import com.flab.offcoupon.exception.coupon.CouponNotFoundException;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_ISSUE_NOT_EXIST;
import static com.flab.offcoupon.exception.coupon.CouponErrorMessage.COUPON_NOT_EXIST;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponIssueMessageHandler {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final RedisRepository redisRepository;

    /**
     * 각 쿠폰 발행 이력을 저장합니다.
     *
     * @param message 메시지를 통해 받은 쿠폰 발행 정보
     */
    @Transactional
    public void saveEachCouponIssueHistory(CouponIssueMessageForQueue message) {
        CouponIssue couponIssue = CouponIssue.create(message.memberId(), message.couponId());
        couponIssueRepository.save(couponIssue);
        log.info("쿠폰 히스토리 저장 완료");
    }

    @Transactional
    public void ddd() {
        List<CountByCouponIdVo> countByCouponIdVo = countTotalCouponIssueForToday();
        if (!countByCouponIdVo.isEmpty()) {
            totalUpdateIssuedCouponAndDeleteRequest(countByCouponIdVo);
            updateCheckFlagAboutCompletedCouponIssue();
        }
    }

    /**
     * MySQL에 저장된 총 발행된 수량을 업데이트하고, <br>
     * Redis에 저장된 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    private void totalUpdateIssuedCouponAndDeleteRequest(List<CountByCouponIdVo> countByCouponIdVoList) {
        countByCouponIdVoList.forEach(countByCouponIdVo -> {
            totalUpdateIssuedQuantity(countByCouponIdVo);
            removeIssuedCouponRequestInRedis(countByCouponIdVo.couponId());
        });
    }

    /**
     * 총 발행된 쿠폰 수량을 업데이트합니다.
     */
    private void totalUpdateIssuedQuantity(CountByCouponIdVo countByCouponIdVo) {
        Coupon coupon = couponRepository.findCouponById(countByCouponIdVo.couponId())
                .orElseThrow(() -> new CouponNotFoundException(COUPON_NOT_EXIST));
        couponRepository.updateTotalIssuedCouponQuantity(
                new UpdateTotalIssuedQuantityVo(countByCouponIdVo.couponId(),
                        coupon.getIssuedQuantity() + countByCouponIdVo.count()));
    }

    /**
     * Redis에 저장된 Set타입의 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    private void removeIssuedCouponRequestInRedis(long couponId) {
        redisRepository.delete(getIssueRequestKey(couponId));
    }

    /**
     * 오늘 발급된 쿠폰의 총 발급 수량을 확인합니다.
     * @return List<CountByCouponIdVo> 오늘 발급된 쿠폰의 총 발급 수량 및 쿠폰 ID List
     */
    private List<CountByCouponIdVo> countTotalCouponIssueForToday() {
        // 쿠폰 발급 이력을 확인하는 로직
        List<CountByCouponIdVo> countByCouponIdVo = couponIssueRepository.countCouponIdForToday();
        return countByCouponIdVo;
    }

    /**
     * 쿠폰 발급 완료 후 반정규화 칼럼(Coupon테이블의 issued_quantity)를 업데이트 했음을 checkFlag에 반영합니다.
     */
    private void updateCheckFlagAboutCompletedCouponIssue() {
        List<Long> longs = couponIssueRepository.couponIssueIdListForToday();
        longs.forEach(ids -> {
            CouponIssue couponIssue = couponIssueRepository.findCouponIssueById(ids)
                    .orElseThrow(() -> new CouponIssueException(COUPON_ISSUE_NOT_EXIST));
            couponIssueRepository.updateCheckFlag(couponIssue.updateCheckFlag(couponIssue));
        });
    }
}
