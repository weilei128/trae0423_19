package com.lawfirm.casemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.casemanagement.common.Result;
import com.lawfirm.casemanagement.entity.Contract;
import com.lawfirm.casemanagement.mapper.ContractMapper;
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
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractMapper contractMapper;

    private String generateContractNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "CONTRACT-" + dateStr + "-" + System.currentTimeMillis();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Page<Contract>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String contractName,
            @RequestParam(required = false) String contractNo,
            @RequestParam(required = false) String signStatus,
            @RequestParam(required = false) Long caseId) {
        
        Page<Contract> page = new Page<>(current, size);
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(contractName)) {
            wrapper.like(Contract::getContractName, contractName);
        }
        if (StringUtils.hasText(contractNo)) {
            wrapper.like(Contract::getContractNo, contractNo);
        }
        if (StringUtils.hasText(signStatus)) {
            wrapper.eq(Contract::getSignStatus, signStatus);
        }
        if (caseId != null) {
            wrapper.eq(Contract::getCaseId, caseId);
        }
        
        wrapper.orderByDesc(Contract::getCreateTime);
        Page<Contract> result = contractMapper.selectPage(page, wrapper);
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Contract> getById(@PathVariable Long id) {
        Contract contract = contractMapper.getContractDetailById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        return Result.success(contract);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<Contract> create(@RequestBody Contract contract, 
                                    @AuthenticationPrincipal UserPrincipal principal) {
        contract.setContractNo(generateContractNo());
        contract.setSignStatus("DRAFT");
        contract.setVersion(1);
        contract.setCreateUserId(principal.getId());
        contractMapper.insert(contract);
        return Result.success(contract);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<Contract> update(@PathVariable Long id, @RequestBody Contract contract) {
        Contract existContract = contractMapper.selectById(id);
        if (existContract == null) {
            return Result.error("合同不存在");
        }
        
        contract.setId(id);
        contract.setVersion(existContract.getVersion() + 1);
        contractMapper.updateById(contract);
        return Result.success(contract);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTNER')")
    public Result<Void> delete(@PathVariable Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        
        contractMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/sign")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER')")
    public Result<Void> signContract(@PathVariable Long id) {
        Contract contract = contractMapper.selectById(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }
        
        contract.setSignStatus("SIGNED");
        contract.setSignDate(LocalDate.now());
        contractMapper.updateById(contract);
        return Result.success();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<?>> getStats() {
        return Result.success(contractMapper.countByStatus());
    }
}
