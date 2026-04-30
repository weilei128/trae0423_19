package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("evidence")
public class Evidence {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private String evidenceName;

    private String evidenceType;

    private String evidenceNo;

    private LocalDate submitDate;

    private String submitCourt;

    private String evidenceSource;

    private String description;

    private String filePath;

    private Integer isOriginal;

    private String status;

    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String caseName;
}
