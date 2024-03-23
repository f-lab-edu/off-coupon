package com.flab.offcoupon.config.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("coupon-issue.exchange");
    }
    @Bean
    public Queue queue() {
        return new Queue("coupon-issue.queue");
    }

//    @Bean
//    public Queue queue() {
//        // 지연 큐로 설정
//        return QueueBuilder.durable("coupon-issue.queue")
//                .withArgument("x-delayed-type", "direct")
//                .build();
//    }


    // exchange에서 queue로 바인딩
    @Bean
    public Binding binding(DirectExchange directExchange, Queue queue) {
        return BindingBuilder.bind(queue).to(directExchange).with("coupon-issue.key");
    }

    /**
     * RabbitTemplate은 메시지를 주고 받을 때 사용하는 메시지 템플릿입니다.
     * RabbitTemplate은 메시지 컨버터를 설정할 수 있습니다.
     * 메세지 컨버터는 Object Mapper를 통해 DTO를 JSON으로 변환하거나 JSON을 DTO로 변환하는 역할을 합니다.
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
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
