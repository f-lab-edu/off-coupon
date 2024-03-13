package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Event;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface EventRepository {
    void save(Event event);
    Optional<Event> findEventById(long eventId);


}
