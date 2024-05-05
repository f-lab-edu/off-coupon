//package com.flab.offcoupon;
//
//import com.rabbitmq.client.BuiltinExchangeType;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.testcontainers.containers.RabbitMQContainer;
//
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//
//import static com.flab.offcoupon.util.CouponRabbitMQConstants.*;
//
//public class RabbitMQTestInitializer {
//
//    static void initializeRabbitMQ(RabbitMQContainer container) {
//        CachingConnectionFactory factory = new CachingConnectionFactory();
//        factory.setHost(container.getHost());
//        factory.setPort(container.getAmqpPort());
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//
//        try (Connection connection = factory.createConnection(); Channel channel = connection.createChannel()) {
//            createExchangeAndQueue(channel);
//        } catch (IOException | TimeoutException e) {
//            throw new RuntimeException("Failed to create RabbitMQ queue", e);
//        }
//    }
//
//    static void createExchangeAndQueue(Channel channel) throws IOException {
//        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
//    }
//}
//
