package com.flab.offcoupon.service.cache;

import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class EventCacheService {

    private final EventRepository eventRepository;

    // @Cacheable은 cahcing된 데이터가 있으면 반환하고
    // 없으면 데이터베이스에서 조회한 후 redis에 cache를 저장하고 반환합니다.
    @Cacheable(key = "#eventId",value = "event")
    public EventRedisEntity getEvent(long eventId) {
        Event event = findEvent(eventId);
        return new EventRedisEntity(event);
    }
    @Transactional(readOnly = true)
    public Event findEvent(long eventId) {
        return eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
    }
}
