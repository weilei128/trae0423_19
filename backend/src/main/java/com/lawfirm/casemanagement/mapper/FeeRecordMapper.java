package com.lawfirm.casemanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lawfirm.casemanagement.entity.FeeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface FeeRecordMapper extends BaseMapper<FeeRecord> {
    
    @Select("SELECT f.*, ci.case_name, cl.client_name " +
            "FROM fee_record f " +
            "LEFT JOIN case_info ci ON f.case_id = ci.id " +
            "LEFT JOIN client cl ON f.client_id = cl.id " +
            "WHERE f.deleted = 0 AND f.id = #{id}")
    FeeRecord getFeeDetailById(@Param("id") Long id);

    @Select("SELECT fee_type, SUM(amount) as total, COUNT(*) as count " +
            "FROM fee_record WHERE deleted = 0 GROUP BY fee_type")
    List<Map<String, Object>> summaryByType();

    @Select("SELECT MONTH(payment_date) as month, SUM(amount) as total " +
            "FROM fee_record " +
            "WHERE deleted = 0 AND fee_type = 'INCOME' AND YEAR(payment_date) = #{year} " +
            "GROUP BY MONTH(payment_date)")
    List<Map<String, Object>> monthlyIncomeSummary(@Param("year") int year);

    @Select("SELECT SUM(amount) FROM fee_record WHERE deleted = 0 AND fee_type = 'INCOME'")
    BigDecimal getTotalIncome();

    @Select("SELECT SUM(amount) FROM fee_record WHERE deleted = 0 AND fee_type = 'EXPENSE'")
    BigDecimal getTotalExpense();
}
