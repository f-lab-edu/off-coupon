package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        CouponIssue couponIssue = CouponIssue.create(message.memberId(),message.couponId());
        couponIssueRepository.save(couponIssue);
        log.info("쿠폰 히스토리 저장 완료");
    }
    /**
     * MySQL에 저장된 총 발행된 수량을 업데이트하고, <br>
     * Redis에 저장된 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    @Transactional
    public void totalUpdateIssuedCouponAndDeleteRequest(CouponIssueMessageForQueue message) {
        if (message != null) {
            totalUpdateIssuedQuantity(message);
            removeIssuedCouponRequestInRedis(message);
            CouponIssueConsumer.message = null;
        }
    }
    /**
     * 총 발행된 쿠폰 수량을 업데이트합니다.
     */
    private void totalUpdateIssuedQuantity(CouponIssueMessageForQueue message) {
        Long totalCnt = redisRepository.sCard(getIssueRequestKey(message.couponId()));
        if (totalCnt != null) {
            couponRepository.updateTotalIssuedCouponQuantity(new UpdateTotalIssuedQuantityVo(message.couponId(), totalCnt));
        }
    }

    /**
     * Redis에 저장된 Set타입의 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    private void removeIssuedCouponRequestInRedis(CouponIssueMessageForQueue message) {
        redisRepository.delete(getIssueRequestKey(message.couponId()));
    }
}
