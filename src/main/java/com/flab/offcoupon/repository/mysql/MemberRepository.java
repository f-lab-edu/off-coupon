package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberRepository {
    void save(Member member);
    boolean existMemberByEmail(String email);
    Optional<Member> findMemberByEmail(String email);
}
