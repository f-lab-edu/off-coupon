package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.component.rabbitmq.MessageQueueCountChecker;
import com.flab.offcoupon.component.sse.SseAlertSender;
import com.flab.offcoupon.domain.entity.CouponIssue;
import com.flab.offcoupon.domain.vo.persistence.couponissue.UpdateTotalIssuedQuantityVo;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import com.flab.offcoupon.repository.mysql.CouponRepository;
import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.flab.offcoupon.util.CouponRabbitMQUtils.QUEUE_NAME;
import static com.flab.offcoupon.util.CouponRedisUtils.getIssueRequestKey;

/**
 * RabbitMQ를 이용한 비동기 쿠폰 발행을 처리하는 컨슈머 클래스입니다. <br>
 * Scheduled 어노테이션을 사용하여 주기적으로 메시지 큐에 메시지가 있는지 확인하고, 메시지가 존재할 경우 이를 처리합니다.
 * <ol>
 *     <li>메시지 큐에 메시지가 있는 경우, 메시지를 받아와서 쿠폰 발행 이력을 저장후 SSE 알림을 보냅니다.</li>
 *     <li>메시지 큐에 메시지가 없는 경우, MySQL에 저장된 총 발행된 수량을 업데이트합니다.</li>
 *     <li>Redis에 저장된 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.</li>
 * </ol>
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CouponIssueConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;
    private final MessageQueueCountChecker messageQueueCountChecker;
    private final RedisRepository redisRepository;
    private final SseAlertSender sseAlertSender;
    /**
     * 쿠폰 발급 메세지를 저장하기 위한 변수
     * 스케줄링이 매번 호출되기 때문에 Queue가 비어있을 경우 couponId를 찾기 위해 static으로 선언했습니다.
     */
    private static CouponIssueMessageForQueue message;
    /**
     * 3초마다 메시지 큐를 확인하여 메시지가 있는지 여부를 판단하고 처리합니다.
     *
     * @throws IOException 메시지 큐 조회 중 발생하는 예외
     */
    @Transactional
    @Scheduled(fixedDelay = 3000)
    public void consumeCouponIssueMessage() throws IOException {
        if (existCouponIssueQueueTarget()) {
            message = (CouponIssueMessageForQueue) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
            log.info("'coupon-issue.queue'에 메시지가 있습니다. message: {}", message);
            saveEachCouponIssueHistory(message);
            sseAlertSender.pushSseMessage(message.memberId(),"쿠폰이 발급 완료되었습니다. memberId : %s");
        } else {
            log.info("'coupon-issue.queue'가 비어있습니다.");
            totalUpdateIssuedCouponAndDeleteRequest();
        }
    }
    /**
     * 메시지 큐에 메시지가 있는지 확인합니다.
     *
     * @return 메시지 큐에 메시지가 존재하는지 여부
     * @throws IOException 메시지 큐 조회 중 발생하는 예외
     */
    private boolean existCouponIssueQueueTarget() throws IOException {
        return messageQueueCountChecker.getMessageCount(QUEUE_NAME) > 0;
    }
    /**
     * 각 쿠폰 발행 이력을 저장합니다.
     *
     * @param message 메시지를 통해 받은 쿠폰 발행 정보
     */
    private void saveEachCouponIssueHistory(CouponIssueMessageForQueue message) {
        CouponIssue couponIssue = CouponIssue.create(message.couponId(), message.memberId());
        couponIssueRepository.save(couponIssue);
        log.info("쿠폰 히스토리 저장 완료");
    }
    /**
     * MySQL에 저장된 총 발행된 수량을 업데이트하고, <br>
     * Redis에 저장된 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    private void totalUpdateIssuedCouponAndDeleteRequest() {
        if (message != null) {
            totalUpdateIssuedQuantity();
            removeIssuedCouponRequestInRedis();
            message = null;
        }
    }
    /**
     * 총 발행된 쿠폰 수량을 업데이트합니다.
     */
    private void totalUpdateIssuedQuantity() {
        Long totalCnt = redisRepository.sCard(getIssueRequestKey(message.couponId()));
        if (totalCnt != null) {
            couponRepository.updateTotalIssuedCouponQuantity(new UpdateTotalIssuedQuantityVo(message.couponId(), totalCnt));
        }
    }

    /**
     * Redis에 저장된 Set타입의 요청 중 메시지로 들어온 쿠폰ID와 관련된 데이터를 삭제합니다.
     */
    private void removeIssuedCouponRequestInRedis() {
        redisRepository.delete(getIssueRequestKey(message.couponId()));
    }
}
