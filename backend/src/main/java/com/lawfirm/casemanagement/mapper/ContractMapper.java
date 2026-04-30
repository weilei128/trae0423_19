package com.lawfirm.casemanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.casemanagement.entity.Contract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {
    
    @Select("SELECT c.*, ci.case_name, cl.client_name " +
            "FROM contract c " +
            "LEFT JOIN case_info ci ON c.case_id = ci.id " +
            "LEFT JOIN client cl ON c.client_id = cl.id " +
            "WHERE c.deleted = 0 AND c.id = #{id}")
    Contract getContractDetailById(Long id);

    @Select("SELECT sign_status, COUNT(*) as count FROM contract WHERE deleted = 0 GROUP BY sign_status")
    List<Map<String, Object>> countByStatus();
}
