package com.flab.offcoupon.service;

import com.flab.offcoupon.dto.request.LoginMemberRequestDto;
import com.flab.offcoupon.dto.request.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.exception.member.MemberNotFoundException;
import com.flab.offcoupon.exception.member.PasswordNotMatchException;
import com.flab.offcoupon.repository.MemberMapperRepository;
import com.flab.offcoupon.util.ResponseDTO;
import com.flab.offcoupon.util.bcrypt.BCryptPasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.ErrorMessage.*;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapperRepository memberMapperRepository;
    @Transactional
    public ResponseDTO signUp(MemberMapperDTO memberMapperDTO) {
        validateEmailNotDuplicated(memberMapperDTO.getEmail());
        Member entity = toEntity(memberMapperDTO);
        memberMapperRepository.save(entity);
        return ResponseDTO.getSuccessResult(memberMapperDTO);
    }

    public Member login(LoginMemberRequestDto loginMemberRequestDto) {
        Member member = findMemberByEmail(loginMemberRequestDto);
        validatePassword(loginMemberRequestDto, member);
        return member;
    }

    @Transactional(readOnly = true)
    public void validateEmailNotDuplicated(String email) {
        boolean isDuplicated = memberMapperRepository.existMemberByEmail(email);
        if (isDuplicated) {
            throw new MemberBadRequestException(DUPLICATED_EMAIL);
        }
    }

    private Member toEntity(MemberMapperDTO memberMapperDTO) {
        return Member.create(
                memberMapperDTO.getEmail(),
                encryptPassword(memberMapperDTO.getPassword()),
                memberMapperDTO.getName(),
                memberMapperDTO.getBirthdate(),
                memberMapperDTO.getPhone());
    }
    private String encryptPassword(String password) {
        return BCryptPasswordEncryptor.encrypt(password);
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(LoginMemberRequestDto loginMemberRequestDto) {
        Member member = memberMapperRepository.findMemberByEmail(loginMemberRequestDto.getEmail())
                .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_EMAIL));
        return member;
    }
    private static void validatePassword(LoginMemberRequestDto loginMemberRequestDto, Member member) {
        if (!BCryptPasswordEncryptor.match(loginMemberRequestDto.getPassword(), member.getPassword())) {
            throw new PasswordNotMatchException(WRONG_PSWD);
        }
    }
}
