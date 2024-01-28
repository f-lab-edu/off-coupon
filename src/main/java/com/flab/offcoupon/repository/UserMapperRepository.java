package com.flab.offcoupon.repository;

import com.flab.offcoupon.controller.api.UserMapperDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapperRepository {
    void save(UserMapperDTO userMapperDTO);
}
