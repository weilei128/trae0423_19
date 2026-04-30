package com.lawfirm.casemanagement.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lawfirm.casemanagement.common.Result;
import com.lawfirm.casemanagement.entity.Client;
import com.lawfirm.casemanagement.mapper.ClientMapper;
import com.lawfirm.casemanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientMapper clientMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Page<Client>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String clientType,
            @RequestParam(required = false) String phone) {
        
        Page<Client> page = new Page<>(current, size);
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(clientName)) {
            wrapper.like(Client::getClientName, clientName);
        }
        if (StringUtils.hasText(clientType)) {
            wrapper.eq(Client::getClientType, clientType);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(Client::getPhone, phone);
        }
        
        wrapper.orderByDesc(Client::getCreateTime);
        Page<Client> result = clientMapper.selectPage(page, wrapper);
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Client> getById(@PathVariable Long id) {
        Client client = clientMapper.selectById(id);
        if (client == null) {
            return Result.error("客户不存在");
        }
        return Result.success(client);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Client> create(@RequestBody Client client, 
                                  @AuthenticationPrincipal UserPrincipal principal) {
        client.setCreateUserId(principal.getId());
        clientMapper.insert(client);
        return Result.success(client);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<Client> update(@PathVariable Long id, @RequestBody Client client) {
        Client existClient = clientMapper.selectById(id);
        if (existClient == null) {
            return Result.error("客户不存在");
        }
        
        client.setId(id);
        clientMapper.updateById(client);
        return Result.success(client);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARTNER')")
    public Result<Void> delete(@PathVariable Long id) {
        Client client = clientMapper.selectById(id);
        if (client == null) {
            return Result.error("客户不存在");
        }
        
        clientMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PARTNER','LAWYER','ASSISTANT')")
    public Result<List<Client>> listAll() {
        LambdaQueryWrapper<Client> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Client::getCreateTime);
        List<Client> clients = clientMapper.selectList(wrapper);
        return Result.success(clients);
    }
}
