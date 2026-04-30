package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("contract")
public class Contract {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String contractNo;

    private String contractName;

    private Long caseId;

    private Long clientId;

    private Long templateId;

    private String contractType;

    private LocalDate signDate;

    private LocalDate expireDate;

    private BigDecimal totalFee;

    private String paymentMethod;

    private String paymentSchedule;

    private String signStatus;

    private String contractContent;

    private String filePath;

    private Integer version;

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
    private String clientName;
}
