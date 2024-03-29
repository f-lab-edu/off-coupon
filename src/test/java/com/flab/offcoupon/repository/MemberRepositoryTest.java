package com.flab.offcoupon.repository;

import com.flab.offcoupon.domain.entity.Member;
import com.flab.offcoupon.domain.entity.Role;
import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.repository.mysql.MemberRepository;
import com.flab.offcoupon.setup.SetupUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    private SetupUtils setupUtils = new SetupUtils();
    @BeforeEach
    void setUp() {
        setupUtils.setUpMember(memberRepository);
    }
    @Test
    @DisplayName("[SUCCESS] 회원 가입 성공")
    void save() {
        // given
        SignupMemberRequestDto mapperDTO = SignupMemberRequestDto.create("test",",1234", "name", LocalDate.parse("2002-12-23"), "01075805060", Role.ROLE_USER);
        Member entity = Member.create(mapperDTO.getEmail(),mapperDTO.getPassword(), mapperDTO.getName(), mapperDTO.getBirthdate(), mapperDTO.getPhone(), mapperDTO.getRole());
        //when
        memberRepository.save(entity);
    }

    @Test
    @DisplayName("[SUCCESS] 중복 유저 찾기")
    void existMemberByEmail() {
        // given
        String email = "test@gmail.com";
        //when
        boolean isDuplicated = memberRepository.existMemberByEmail(email);
        assertThat(isDuplicated).isTrue();
    }
}