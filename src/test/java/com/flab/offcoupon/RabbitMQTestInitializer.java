package com.flab.offcoupon;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.testcontainers.containers.RabbitMQContainer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.flab.offcoupon.util.CouponRabbitMQConstants.*;

public class RabbitMQTestInitializer {

    static void initializeRabbitMQ(RabbitMQContainer container) {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(container.getHost());
        factory.setPort(container.getAmqpPort());
        factory.setUsername(container.getAdminUsername());
        factory.setPassword(container.getAdminPassword());

        try (Connection connection = factory.getRabbitConnectionFactory().newConnection()) {
            Channel channel = connection.createChannel();
            createExchangeAndQueue(channel);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException("RabbitMQ queue 생성에 실패했습니다.", e);
        }
    }

    static void createExchangeAndQueue(Channel channel) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
    }
}

