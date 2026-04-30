package com.lawfirm.casemanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.casemanagement.entity.CaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CaseInfoMapper extends BaseMapper<CaseInfo> {
    
    @Select("SELECT c.*, cl.client_name, u.real_name as lead_lawyer_name, ct.type_name as case_type_name " +
            "FROM case_info c " +
            "LEFT JOIN client cl ON c.client_id = cl.id " +
            "LEFT JOIN user u ON c.lead_lawyer_id = u.id " +
            "LEFT JOIN case_type ct ON c.case_type_id = ct.id " +
            "WHERE c.deleted = 0 AND c.id = #{id}")
    CaseInfo getCaseDetailById(@Param("id") Long id);

    @Select("SELECT case_status, COUNT(*) as count FROM case_info WHERE deleted = 0 GROUP BY case_status")
    List<Map<String, Object>> countByStatus();

    @Select("SELECT ct.type_name, COUNT(*) as count " +
            "FROM case_info c " +
            "LEFT JOIN case_type ct ON c.case_type_id = ct.id " +
            "WHERE c.deleted = 0 " +
            "GROUP BY c.case_type_id")
    List<Map<String, Object>> countByCaseType();

    @Select("SELECT u.real_name, COUNT(*) as count " +
            "FROM case_info c " +
            "LEFT JOIN user u ON c.lead_lawyer_id = u.id " +
            "WHERE c.deleted = 0 AND u.deleted = 0 " +
            "GROUP BY c.lead_lawyer_id")
    List<Map<String, Object>> countByLawyer();
}
