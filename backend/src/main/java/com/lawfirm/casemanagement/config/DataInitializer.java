package com.lawfirm.casemanagement.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lawfirm.casemanagement.entity.CaseType;
import com.lawfirm.casemanagement.entity.User;
import com.lawfirm.casemanagement.mapper.CaseTypeMapper;
import com.lawfirm.casemanagement.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CaseTypeMapper caseTypeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            initUsers();
        } catch (Exception e) {
            System.out.println("用户初始化跳过（表可能不存在）: " + e.getMessage());
        }
        try {
            initCaseTypes();
        } catch (Exception e) {
            System.out.println("案件类型初始化跳过（表可能不存在）: " + e.getMessage());
        }
    }

    private void initUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User existAdmin = userMapper.selectOne(wrapper);
        
        if (existAdmin == null) {
            String defaultPassword = "123456";
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            
            List<User> users = Arrays.asList(
                createUser("admin", encodedPassword, "系统管理员", "13800138000", "admin@lawfirm.com", "PARTNER"),
                createUser("partner1", encodedPassword, "张合伙人", "13800138001", "partner1@lawfirm.com", "PARTNER"),
                createUser("lawyer1", encodedPassword, "李律师", "13800138002", "lawyer1@lawfirm.com", "LAWYER"),
                createUser("assistant1", encodedPassword, "王助理", "13800138003", "assistant1@lawfirm.com", "ASSISTANT")
            );
            
            for (User user : users) {
                userMapper.insert(user);
            }
            
            System.out.println("========================================");
            System.out.println("默认用户已初始化，密码: 123456");
            System.out.println("admin (合伙人)  : admin / 123456");
            System.out.println("partner1 (合伙人): partner1 / 123456");
            System.out.println("lawyer1 (律师)  : lawyer1 / 123456");
            System.out.println("assistant1 (助理): assistant1 / 123456");
            System.out.println("========================================");
        }
    }

    private void initCaseTypes() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM case_type", 
                Integer.class
            );
            
            if (count == null || count == 0) {
                insertDefaultCaseTypes();
            } else {
                Integer activeCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM case_type WHERE deleted = 0", 
                    Integer.class
                );
                
                if (activeCount == null || activeCount == 0) {
                    jdbcTemplate.update("DELETE FROM case_type");
                    insertDefaultCaseTypes();
                }
            }
        } catch (Exception e) {
            System.out.println("检查案件类型表失败，尝试直接插入: " + e.getMessage());
            try {
                insertDefaultCaseTypes();
            } catch (Exception e2) {
                System.out.println("插入案件类型失败: " + e2.getMessage());
            }
        }
    }

    private void insertDefaultCaseTypes() {
        System.out.println("正在初始化案件类型数据...");
        
        List<CaseType> caseTypes = Arrays.asList(
            createCaseType("民事案件", 0L, 1),
            createCaseType("刑事案件", 0L, 2),
            createCaseType("行政案件", 0L, 3),
            createCaseType("执行案件", 0L, 4)
        );
        
        for (CaseType caseType : caseTypes) {
            try {
                caseTypeMapper.insert(caseType);
            } catch (Exception e) {
                System.out.println("插入案件类型失败 [" + caseType.getTypeName() + "]: " + e.getMessage());
            }
        }
        
        System.out.println("案件类型已初始化: 民事案件、刑事案件、行政案件、执行案件");
    }

    private User createUser(String username, String password, String realName, String phone, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(1);
        return user;
    }

    private CaseType createCaseType(String typeName, Long parentId, Integer sort) {
        CaseType caseType = new CaseType();
        caseType.setTypeName(typeName);
        caseType.setParentId(parentId);
        caseType.setSort(sort);
        return caseType;
    }
}
