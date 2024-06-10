package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Member;
import com.flab.offcoupon.exception.member.MemberNotFoundException;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

import static com.flab.offcoupon.exception.member.MemberErrorMessage.NOT_EXIST_MEMBER;

@Mapper
public interface MemberRepository {
    void save(Member member);
    boolean existMemberByEmail(String email);
    Optional<Member> findMemberByEmail(String email);
    Optional<Member> findMemberById(long memberId);
    /**
     * 멤버 ID로 유저를 조회해서 Member 객체를 반환합니다.<br>
     *
     * @param memberId 유저 ID
     * @return 유저 객체
     */

    default Member getMemberById(long memberId) {
        return findMemberById(memberId).orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));
    }
}
