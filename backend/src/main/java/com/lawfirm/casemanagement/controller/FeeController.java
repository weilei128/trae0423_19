package com.lawfirm.casemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.casemanagement.common.Result;
import com.lawfirm.casemanagement.entity.FeeRecord;
import com.lawfirm.casemanagement.mapper.FeeRecordMapper;
import com.lawfirm.casemanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    @Autowired
    private FeeRecordMapper feeRecordMapper;

    private String generateFeeNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "FEE-" + dateStr + "-" + System.currentTimeMillis();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Page<FeeRecord>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) String feeName,
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) Long clientId) {
        
        Page<FeeRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FeeRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(feeType)) {
            wrapper.eq(FeeRecord::getFeeType, feeType);
        }
        if (StringUtils.hasText(feeName)) {
            wrapper.like(FeeRecord::getFeeName, feeName);
        }
        if (caseId != null) {
            wrapper.eq(FeeRecord::getCaseId, caseId);
        }
        if (clientId != null) {
            wrapper.eq(FeeRecord::getClientId, clientId);
        }
        
        wrapper.orderByDesc(FeeRecord::getCreateTime);
        Page<FeeRecord> result = feeRecordMapper.selectPage(page, wrapper);
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<FeeRecord> getById(@PathVariable Long id) {
        FeeRecord feeRecord = feeRecordMapper.getFeeDetailById(id);
        if (feeRecord == null) {
            return Result.error("费用记录不存在");
        }
        return Result.success(feeRecord);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<FeeRecord> create(@RequestBody FeeRecord feeRecord, 
                                     @AuthenticationPrincipal UserPrincipal principal) {
        feeRecord.setFeeNo(generateFeeNo());
        feeRecord.setStatus("CONFIRMED");
        feeRecord.setInvoiceStatus("NOT_ISSUED");
        feeRecord.setCreateUserId(principal.getId());
        feeRecordMapper.insert(feeRecord);
        return Result.success(feeRecord);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<FeeRecord> update(@PathVariable Long id, @RequestBody FeeRecord feeRecord) {
        FeeRecord existRecord = feeRecordMapper.selectById(id);
        if (existRecord == null) {
            return Result.error("费用记录不存在");
        }
        
        feeRecord.setId(id);
        feeRecordMapper.updateById(feeRecord);
        return Result.success(feeRecord);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTNER')")
    public Result<Void> delete(@PathVariable Long id) {
        FeeRecord feeRecord = feeRecordMapper.selectById(id);
        if (feeRecord == null) {
            return Result.error("费用记录不存在");
        }
        
        feeRecordMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/stats/summary")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        BigDecimal totalIncome = feeRecordMapper.getTotalIncome();
        BigDecimal totalExpense = feeRecordMapper.getTotalExpense();
        
        summary.put("totalIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        summary.put("totalExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);
        summary.put("netProfit", totalIncome != null && totalExpense != null 
            ? totalIncome.subtract(totalExpense) : BigDecimal.ZERO);
        summary.put("feeTypeSummary", feeRecordMapper.summaryByType());
        
        return Result.success(summary);
    }

    @GetMapping("/stats/monthly")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<?>> getMonthlyIncome(@RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") Integer year) {
        return Result.success(feeRecordMapper.monthlyIncomeSummary(year));
    }
}
