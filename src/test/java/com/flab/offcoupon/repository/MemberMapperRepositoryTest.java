package com.flab.offcoupon.repository;

import com.flab.offcoupon.dto.request.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class MemberMapperRepositoryTest {

    @Autowired
    private MemberMapperRepository memberMapperRepository;
    @Test
    @Transactional
    @DisplayName("[SUCCESS] 회원 가입 성공")
    void save() {
        // given
        MemberMapperDTO mapperDTO = MemberMapperDTO.create("test",",1234", "name", "20021223", "01075805060");
        Member entity = Member.create(mapperDTO.getEmail(),mapperDTO.getPassword(), mapperDTO.getName(), mapperDTO.getBirthdate(), mapperDTO.getPhone());
        //when
        memberMapperRepository.save(entity);
    }

    @Test
    @Transactional
    @DisplayName("[SUCCESS] 중복 유저 찾기")
    void existMemberByEmail() {
        // given
        String email = "sejin@email.com"; // 웹서버 올라갈때 Insert된 이메일
        //when
        boolean isDuplicated = memberMapperRepository.existMemberByEmail(email);
        assertThat(isDuplicated).isTrue();
    }
}