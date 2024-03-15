package com.flab.offcoupon.service.cache;

import com.flab.offcoupon.domain.entity.Event;
import com.flab.offcoupon.domain.redis.EventRedisEntity;
import com.flab.offcoupon.exception.event.EventNotFoundException;
import com.flab.offcoupon.repository.mysql.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.flab.offcoupon.exception.event.EventErrorMessage.EVENT_NOT_EXIST;

/**
 * 이벤트 캐시 서비스를 제공하는 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class EventCacheService {

    private final EventRepository eventRepository;

    /**
     * @Cacheable 어노테이션은 이벤트 ID를 기반으로 이벤트 정보에 대한 캐시를 생성합니다.
     * 이벤트 캐시에 데이터가 있다면 조회하고, 없을 경우 데이터베이스에서 가져와서 캐시에 저장합니다.
     *
     * @param eventId 조회할 이벤트의 ID
     * @return 이벤트 정보를 담은 EventRedisEntity 객체
     */
    @Cacheable(key = "#eventId",value = "event")
    public EventRedisEntity getEvent(long eventId) {
        Event event = findEvent(eventId);
        return new EventRedisEntity(event);
    }

    /**
     * 이벤트 ID를 기반으로 데이터베이스에서 이벤트 정보를 조회하는 메서드입니다.
     *
     * @param eventId 조회할 이벤트의 ID
     * @return 조회된 이벤트 객체
     * @throws EventNotFoundException 이벤트가 존재하지 않을 경우 발생하는 예외
     */
    private Event findEvent(long eventId) {
        return eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_EXIST.formatted(eventId)));
    }
}
