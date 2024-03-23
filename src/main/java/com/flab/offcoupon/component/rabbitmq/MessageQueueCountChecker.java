package com.flab.offcoupon.component.rabbitmq;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ의 특정 큐에 있는 메시지 수를 조회하는 컴포넌트 클래스입니다.
 */
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

    /**
     * RabbitMQ의 특정 큐에 있는 메시지 수를 조회합니다.
     *
     * @param queueName 조회할 큐의 이름
     * @return 큐에 있는 메시지 수
     * @throws IOException IOException 예외 발생시
     *
     * @See <a href="https://www.rabbitmq.com/client-libraries/java-api-guide#client-provided-names">참고한 RabbitMQ 공식문서</a>
     * @See Client-Provided Connection Name, Passive Declaration
     */
    public int getMessageCount(String queueName) throws IOException {
        // RabbitMQ 연결 설정
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitmqHost);
        factory.setUsername(rabbitmqUsername);
        factory.setPassword(rabbitmqPassword);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // queueDeclarePassive : RabbitMQ에게 특정 큐가 존재하는지 확인 요청하는 메서드
            DeclareOk queueDeclareOk = channel.queueDeclarePassive(queueName);
            return queueDeclareOk.getMessageCount();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
