package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("case_progress")
public class CaseProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private String progressType;

    private LocalDate progressDate;

    private String progressTitle;

    private String progressContent;

    private Integer important;

    private String nextStep;

    private LocalDateTime remindDate;

    private String remindUserIds;

    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String caseName;

    @TableField(exist = false)
    private String createUserName;
}
