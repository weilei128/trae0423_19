package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("case_info")
public class CaseInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String caseNo;

    private String caseName;

    private Long caseTypeId;

    private String caseLevel;

    private Long clientId;

    private String oppositeParty;

    private String oppositeAgent;

    private String oppositeLawFirm;

    private String court;

    private LocalDate caseDate;

    private String judge;

    private Long leadLawyerId;

    private String assistLawyerIds;

    private String caseStatus;

    private String caseStage;

    private BigDecimal amount;

    private String description;

    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String clientName;

    @TableField(exist = false)
    private String leadLawyerName;

    @TableField(exist = false)
    private String caseTypeName;
}
