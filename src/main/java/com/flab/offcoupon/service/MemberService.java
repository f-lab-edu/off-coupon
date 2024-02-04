package com.flab.offcoupon.service;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.repository.MemberMapperRepository;
import com.flab.offcoupon.util.ResponseDTO;
import com.flab.offcoupon.util.bcrypt.BCryptPasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.flab.offcoupon.exception.ErrorMessage.DUPLICATED_EMAIL;

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

    @Transactional(readOnly = true)
    public void validateEmailNotDuplicated(String email) {
        boolean isDuplicated = memberMapperRepository.existMemberByEmail(email);
        if (isDuplicated) {
            throw new MemberBadRequestException(DUPLICATED_EMAIL);
        }
    }
    private Member toEntity(MemberMapperDTO memberMapperDTO) {
        Member entity = Member.create(
                memberMapperDTO.getEmail(),
                encryptPassword(memberMapperDTO.getPassword()),
                memberMapperDTO.getName(),
                memberMapperDTO.getBirthDate(),
                memberMapperDTO.getPhone());
        return entity;
    }
    private String encryptPassword(String password) {
        return BCryptPasswordEncryptor.encrypt(password);
    }
}
