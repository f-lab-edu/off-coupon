package com.flab.offcoupon.service.coupon_issue.async.consumer;

import com.flab.offcoupon.component.rabbitmq.MessageQueueCountChecker;
import com.flab.offcoupon.component.sse.SseAlertSender;
import com.flab.offcoupon.dto.request.rabbit_mq.CouponIssueMessageForQueue;
import com.flab.offcoupon.repository.mysql.CouponIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.flab.offcoupon.util.CouponRabbitMQConstants.QUEUE_NAME;

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
    private final MessageQueueCountChecker messageQueueCountChecker;
    private final SseAlertSender sseAlertSender;
    private final CouponIssueMessageHandler couponIssueMessageHandler;
    private final RedissonLockHandler redissonLockHandler;

    /**
     * 3초마다 메시지 큐를 확인하여 메시지가 있는지 여부를 판단하고 쿠폰 이력을 INSERT합니다.
     */
    @Scheduled(fixedDelay = 3000)
    private void consumeCouponIssueMessage() {
        if (existCouponIssueQueueTarget()) {
            CouponIssueMessageForQueue message = (CouponIssueMessageForQueue) rabbitTemplate.receiveAndConvert(QUEUE_NAME);
            log.info("'coupon-issue.queue'에 메시지가 있습니다. message: {}", message);
            couponIssueMessageHandler.saveEachCouponIssueHistory(message);
            sseAlertSender.pushSseMessage(message.memberId(),"쿠폰이 발급 완료되었습니다. memberId : %s");
        }
    }

    /**
     * 10초마다 오늘 발급된 쿠폰의 총 발급 수량을 조회해서 반정규화된 칼럼을 업데이트합니다.
     */
    @Scheduled(fixedDelay = 10000)
    private void updateTotalCouponIssueCount() {
        redissonLockHandler.asyncIssueCoupon();
    }

    /**
     * 메시지 큐에 메시지가 있는지 확인합니다.
     *
     * @return 메시지 큐에 메시지가 존재하는지 여부
     * @see MessageQueueCountChecker#getMessageCount(String)
     */
    private boolean existCouponIssueQueueTarget() {
        return messageQueueCountChecker.getMessageCount(QUEUE_NAME) > 0;
    }

}
