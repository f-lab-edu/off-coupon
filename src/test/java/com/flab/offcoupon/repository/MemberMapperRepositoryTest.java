package com.flab.offcoupon.repository;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class MemberMapperRepositoryTest {
    @Autowired
    private MemberMapperRepository memberMapperRepository;
    @Test
    @Transactional
    @DisplayName("Mybatis 회원가입 테스트")
    public void userSaveTest() {
        MemberMapperDTO mapperDTO = MemberMapperDTO.create("test",",1234", "name", "20021223", "01075805060");
        Member entity = Member.toEntity(mapperDTO);
        memberMapperRepository.save(entity);
    }

}