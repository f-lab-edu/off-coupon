package com.flab.offcoupon;

public class RabbitMQTestInitializer {

//    static void initializeRabbitMQ(RabbitMQContainer container) {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(container.getHost());
//        factory.setPort(container.getAmqpPort());
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//            createExchangeAndQueue(channel);
//        } catch (IOException | TimeoutException e) {
//            throw new RuntimeException("Failed to create RabbitMQ queue", e);
//        }
//    }
//
//    static void createExchangeAndQueue(Channel channel) throws IOException {
//        channel.exchangeDeclare("x.alarm.work", BuiltinExchangeType.FANOUT);
//        channel.exchangeDeclare("x.alarm.dead", BuiltinExchangeType.FANOUT);
//
//        HashMap<String, Object> argumentsMap = new HashMap<>();
//        argumentsMap.put("x-dead-letter-exchange", "x.alarm.dead");
//        channel.queueDeclare("q.alarm.work", false, false, false, argumentsMap);
//        channel.queueBind("q.alarm.work", "x.alarm.work", "");
//
//        channel.queueDeclare("q.alarm.dead", false, false, false, null);
//        channel.queueBind("q.alarm.dead", "x.alarm.dead", "");
//    }
}

