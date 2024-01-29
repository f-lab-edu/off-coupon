package com.flab.offcoupon.service;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.exception.member.MemberBadRequestException;
import com.flab.offcoupon.repository.MemberMapperRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.flab.offcoupon.exception.Constant.DUPLICATED_EMAIL;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapperRepository memberMapperRepository;

    public ResponseDTO signUp(MemberMapperDTO memberMapperDTO) {
        checkDuplicatedEmail(memberMapperDTO.getEmail());
        Member entity = Member.toEntity(memberMapperDTO);
        memberMapperRepository.save(entity);
        return ResponseDTO.getSuccessResult(memberMapperDTO);
    }

    private void checkDuplicatedEmail(String email) {
        int count = memberMapperRepository.countByEmail(email);
        if (count != 0) {
            throw new MemberBadRequestException(DUPLICATED_EMAIL);
        }
    }
}
