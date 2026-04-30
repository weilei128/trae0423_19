package com.lawfirm.casemanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            System.out.println("检查并初始化数据库表...");
            
            if (!checkTableExists("user")) {
                System.out.println("数据库表不存在，开始初始化...");
                executeInitScript();
                System.out.println("数据库表初始化完成！");
            } else {
                System.out.println("数据库表已存在，跳过初始化");
            }
        } catch (Exception e) {
            System.out.println("数据库初始化异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkTableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeInitScript() {
        try {
            ClassPathResource resource = new ClassPathResource("db/init.sql");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            
            StringBuilder currentStatement = new StringBuilder();
            String line;
            List<String> statements = new ArrayList<>();
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                currentStatement.append(line).append(" ");
                
                if (line.endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (!sql.isEmpty()) {
                        statements.add(sql.substring(0, sql.length() - 1));
                    }
                    currentStatement = new StringBuilder();
                }
            }
            
            reader.close();
            
            for (String sql : statements) {
                try {
                    if (sql.toUpperCase().startsWith("INSERT")) {
                        System.out.println("跳过 INSERT 语句（将通过 DataInitializer 处理）: " + sql.substring(0, 50) + "...");
                        continue;
                    }
                    jdbcTemplate.execute(sql);
                    System.out.println("执行 SQL 成功: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                } catch (Exception e) {
                    System.out.println("执行 SQL 失败（可能已存在）: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.out.println("执行初始化脚本失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
