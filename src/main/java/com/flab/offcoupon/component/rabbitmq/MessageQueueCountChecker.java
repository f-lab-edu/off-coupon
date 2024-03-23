package com.flab.offcoupon.component.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
public class MessageQueueCountChecker {
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private String rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;
    private final String URL = "http://"+rabbitmqHost+":"+15672+"/api/queues/%2F/";

    /**
     * RabbitMQ의 특정 큐에 있는 메시지 수를 조회합니다.
     *
     * @See https://www.rabbitmq.com/client-libraries/java-api-guide#exchanges-and-queues
     * @See Client-Provided Connection Name, Passive Declaration
     * @param queueName
     * @return
     * @throws IOException
     */
    public int getMessageCount(String queueName) throws IOException {
        // RabbitMQ 연결을 설정합니다.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost); // RabbitMQ 호스트 설정
        factory.setUsername(rabbitmqUsername); // RabbitMQ 사용자 이름 설정
        factory.setPassword(rabbitmqPassword); // RabbitMQ 사용자 비밀번호 설정

        // RabbitMQ에 연결합니다.
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 큐를 passively 선언하여 존재 여부를 확인합니다.
            AMQP.Queue.DeclareOk queueDeclareOk = channel.queueDeclarePassive(queueName);

            // 큐에 대한 정보를 얻습니다.
            int messageCount = queueDeclareOk.getMessageCount(); // 메시지 수를 얻습니다.
            System.out.println("messageCount = " + messageCount);
            return messageCount;
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
