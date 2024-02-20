package com.flab.offcoupon.service;

import com.flab.offcoupon.dto.request.SignupMemberRequestDto;
import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.dto.response.SignupMemberResponseDto;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.repository.MemberRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.ErrorMessage.*;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public ResponseDTO<SignupMemberResponseDto> signUp(SignupMemberRequestDto signupMemberRequestDto) {
        validateEmailNotDuplicated(signupMemberRequestDto.getEmail());
        Member entity = toEntity(signupMemberRequestDto);
        memberRepository.save(entity);
        return ResponseDTO.getSuccessResult(new SignupMemberResponseDto(entity));
    }

    @Transactional(readOnly = true)
    public void validateEmailNotDuplicated(String email) {
        boolean isDuplicated = memberRepository.existMemberByEmail(email);
        if (isDuplicated) {
            throw new MemberBadRequestException(DUPLICATED_EMAIL);
        }
    }

    private Member toEntity(SignupMemberRequestDto signupMemberRequestDto) {
        return Member.create(
                signupMemberRequestDto.getEmail(),
                passwordEncoder.encode(signupMemberRequestDto.getPassword()),
                signupMemberRequestDto.getName(),
                signupMemberRequestDto.getBirthdate(),
                signupMemberRequestDto.getPhone(),
                signupMemberRequestDto.getRole());
    }
}
