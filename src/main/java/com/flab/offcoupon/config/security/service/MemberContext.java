package com.flab.offcoupon.config.security.service;

import com.flab.offcoupon.domain.entity.Member;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@EqualsAndHashCode
public class MemberContext extends User {

    private Member member;
    public MemberContext(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getEmail(),member.getPassword(), authorities);
        this.member = member;
    }
}