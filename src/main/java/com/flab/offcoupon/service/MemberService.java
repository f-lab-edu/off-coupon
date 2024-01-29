package com.flab.offcoupon.service;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import com.flab.offcoupon.domain.Member;
import com.flab.offcoupon.repository.MemberMapperRepository;
import com.flab.offcoupon.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberMapperRepository memberMapperRepository;
    public ResponseDTO signUp(MemberMapperDTO memberMapperDTO) {

        Member entity = Member.toEntity(memberMapperDTO);
        memberMapperRepository.save(entity);
        return ResponseDTO.getSuccessResult(memberMapperDTO);
    }
}
