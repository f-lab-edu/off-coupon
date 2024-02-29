package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.entity.Event;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface EventRepository {

    Optional<Event> findEventById(long eventId);


}
