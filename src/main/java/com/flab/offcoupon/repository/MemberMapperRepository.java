package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapperRepository {
    void save(Member member);
}
