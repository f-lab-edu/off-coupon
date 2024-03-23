package com.flab.offcoupon.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RabbitMQ 에서 사용하는 exchange, queue, routing key 를 생성하는 유틸리티 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRabbitMQUtils {
    /**
     * RabbitMQ에서 사용하는 Exchange의 이름입니다.
     * 메시지를 받고 적절한 Queue로 라우팅하는 역할을 담당합니다.
     */
    public static final String EXCHANGE_NAME = "coupon-issue.exchange";
    /**
     * RabbitMQ에서 사용하는 Queue의 이름입니다.
     * 실제로 메시지를 보관하는 공간입니다.
     * 여러 개의 Queue를 사용할 수 있습니다.
     */
    public static final String QUEUE_NAME = "coupon-issue.queue";
    /**
     * RabbitMQ에서 사용하는 Routing Key입니다.
     * Exchange가 메시지를 적절한 Queue로 라우팅할 때 사용됩니다.
     */
    public static final String ROUTING_KEY = "coupon-issue.key";

}
