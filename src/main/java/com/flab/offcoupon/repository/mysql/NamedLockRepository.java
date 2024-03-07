package com.flab.offcoupon.repository.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NamedLockRepository {

    int getLock(@Param("lockName") String lockName);
    int releaseLock(@Param("lockName") String lockName);
}
