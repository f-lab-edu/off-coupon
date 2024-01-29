package com.flab.offcoupon.repository;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import org.springframework.transaction.annotation.Transactional;


@ExtendWith(MockitoExtension.class)
@MybatisTest
class MemberMapperRepositoryTest {

    @Autowired
    private MemberMapperRepository memberMapperRepository;
    @Test
    @Transactional
    @DisplayName("[SUCCESS] 회원 가입 성공")
    public void save() {
        // given
        MemberMapperDTO mapperDTO = MemberMapperDTO.create("test",",1234", "name", "20021223", "01075805060");
        Member entity = Member.toEntity(mapperDTO);
        //when
        memberMapperRepository.save(entity);
    }
}