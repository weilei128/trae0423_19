package com.lawfirm.casemanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.casemanagement.entity.Client;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientMapper extends BaseMapper<Client> {
}
