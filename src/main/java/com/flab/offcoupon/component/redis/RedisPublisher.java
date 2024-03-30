package com.flab.offcoupon.component.redis;

import com.flab.offcoupon.repository.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Lazy
public class RedisPublisher {

    private final RedisRepository redisRepository;
    public void publish(String topic, Object message){
        redisRepository.publish(topic, message);
    }
}
