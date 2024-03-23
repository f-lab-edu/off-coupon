package com.flab.offcoupon.component.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ로 메시지를 전송하는 프로듀서 클래스입니다.
 */
@RequiredArgsConstructor
@Component
public class Producer {

    private final RabbitTemplate rabbitTemplate;
    /**
     * 지정된 exchange와 routeKey를 사용하여 메시지를 RabbitMQ에 전송합니다.
     *
     * @param exchange RabbitMQ에 사용될 exchange
     * @param routeKey 메시지가 전달될 routeKey
     * @param object 전송할 메시지 객체
     */
    public void producer(String exchange, String routeKey, Object object) {
        rabbitTemplate.convertAndSend(exchange, routeKey, object);
    }
}
