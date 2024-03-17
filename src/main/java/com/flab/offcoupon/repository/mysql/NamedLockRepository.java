package com.flab.offcoupon.repository.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NamedLockRepository {
    /**
     * 락 획득을 시도합니다.
     * 락 획득 성공 : 1, 락 획득 실패 : 0 or null
     * @param lockName
     * @return int
     */
    Integer getLock(@Param("lockName") String lockName);
    /**
     * 락 반납을 시도합니다.
     * 락 반납 성공 : 1, 락 반납 실패 : 0 or null
     * @param lockName
     * @return int
     */
    Integer releaseLock(@Param("lockName") String lockName);
}
