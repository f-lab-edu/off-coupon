package com.flab.offcoupon.repository;

import com.flab.offcoupon.controller.api.MemberMapperDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapperRepository {
    void save(MemberMapperDTO memberMapperDTO);
}
