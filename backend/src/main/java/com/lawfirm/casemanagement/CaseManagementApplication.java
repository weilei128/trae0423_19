package com.lawfirm.casemanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lawfirm.casemanagement.mapper")
public class CaseManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CaseManagementApplication.class, args);
    }
}
