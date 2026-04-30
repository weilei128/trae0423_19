package com.lawfirm.casemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.casemanagement.common.Result;
import com.lawfirm.casemanagement.entity.CaseInfo;
import com.lawfirm.casemanagement.entity.CaseType;
import com.lawfirm.casemanagement.mapper.CaseInfoMapper;
import com.lawfirm.casemanagement.mapper.CaseTypeMapper;
import com.lawfirm.casemanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseInfoMapper caseInfoMapper;

    @Autowired
    private CaseTypeMapper caseTypeMapper;

    private String generateCaseNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "CASE-" + dateStr + "-" + System.currentTimeMillis();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Page<CaseInfo>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String caseName,
            @RequestParam(required = false) String caseNo,
            @RequestParam(required = false) String caseStatus,
            @RequestParam(required = false) Long leadLawyerId) {
        
        Page<CaseInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<CaseInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(caseName)) {
            wrapper.like(CaseInfo::getCaseName, caseName);
        }
        if (StringUtils.hasText(caseNo)) {
            wrapper.like(CaseInfo::getCaseNo, caseNo);
        }
        if (StringUtils.hasText(caseStatus)) {
            wrapper.eq(CaseInfo::getCaseStatus, caseStatus);
        }
        if (leadLawyerId != null) {
            wrapper.eq(CaseInfo::getLeadLawyerId, leadLawyerId);
        }
        
        wrapper.orderByDesc(CaseInfo::getCreateTime);
        Page<CaseInfo> result = caseInfoMapper.selectPage(page, wrapper);
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<CaseInfo> getById(@PathVariable Long id) {
        CaseInfo caseInfo = caseInfoMapper.getCaseDetailById(id);
        if (caseInfo == null) {
            return Result.error("案件不存在");
        }
        return Result.success(caseInfo);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<CaseInfo> create(@RequestBody CaseInfo caseInfo, 
                                    @AuthenticationPrincipal UserPrincipal principal) {
        caseInfo.setCaseNo(generateCaseNo());
        caseInfo.setCaseStatus("NEW");
        caseInfo.setCreateUserId(principal.getId());
        caseInfoMapper.insert(caseInfo);
        return Result.success(caseInfo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<CaseInfo> update(@PathVariable Long id, @RequestBody CaseInfo caseInfo) {
        CaseInfo existCase = caseInfoMapper.selectById(id);
        if (existCase == null) {
            return Result.error("案件不存在");
        }
        
        caseInfo.setId(id);
        caseInfoMapper.updateById(caseInfo);
        return Result.success(caseInfo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTNER')")
    public Result<Void> delete(@PathVariable Long id) {
        CaseInfo caseInfo = caseInfoMapper.selectById(id);
        if (caseInfo == null) {
            return Result.error("案件不存在");
        }
        
        caseInfoMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        CaseInfo caseInfo = caseInfoMapper.selectById(id);
        if (caseInfo == null) {
            return Result.error("案件不存在");
        }
        
        caseInfo.setCaseStatus(status);
        caseInfoMapper.updateById(caseInfo);
        return Result.success();
    }

    @GetMapping("/stats/by-status")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<?>> getStatsByStatus() {
        return Result.success(caseInfoMapper.countByStatus());
    }

    @GetMapping("/stats/by-type")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<?>> getStatsByType() {
        return Result.success(caseInfoMapper.countByCaseType());
    }

    @GetMapping("/stats/by-lawyer")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<?>> getStatsByLawyer() {
        return Result.success(caseInfoMapper.countByLawyer());
    }

    @GetMapping("/types")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<CaseType>> getCaseTypes() {
        LambdaQueryWrapper<CaseType> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CaseType::getSort);
        List<CaseType> types = caseTypeMapper.selectList(wrapper);
        return Result.success(types);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<CaseInfo>> getAllCases() {
        LambdaQueryWrapper<CaseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(CaseInfo::getCreateTime);
        List<CaseInfo> cases = caseInfoMapper.selectList(wrapper);
        return Result.success(cases);
    }
}
