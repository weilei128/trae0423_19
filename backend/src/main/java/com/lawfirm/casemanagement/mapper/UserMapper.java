package com.lawfirm.casemanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.casemanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
