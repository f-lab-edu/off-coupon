package com.flab.offcoupon.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.flab.offcoupon.util.CouponRabbitMQUtils.*;

/**
 * RabbitMQ 구성을 정의하는 설정 클래스입니다.
 */
@Configuration
public class RabbitMqConfig {
    /**
     * Exchange를 빈으로 등록합니다.
     *
     * @return DirectExchange 객체
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }
    /**
     * Queue를 빈으로 등록합니다.
     *
     * @return Queue 객체
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    /**
     * Exchange에서 Queue로 바인딩을 설정합니다.
     *
     * @param directExchange DirectExchange 객체
     * @param queue          Queue 객체
     * @return Binding 객체
     */
    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with(ROUTING_KEY);
    }

    /**
     * RabbitTemplate은 메시지를 주고 받을 때 사용하는 메시지 템플릿입니다.
     * RabbitTemplate은 메시지 컨버터를 설정할 수 있습니다.
     * 메세지 컨버터는 ObjectMapper를 통해 DTO -> JSON | JSON -> DTO 로 변환하는 역할을 합니다.
     *
     * @param connectionFactory
     * @param messageConverter
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
    /**
     * Jackson2JsonMessageConverter를 빈으로 등록합니다.
     *
     * @param objectMapper ObjectMapper 객체
     * @return Jackson2JsonMessageConverter 객체
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
