package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String feeNo;

    private Long caseId;

    private Long contractId;

    private Long clientId;

    private String feeType;

    private String feeCategory;

    private String feeName;

    private BigDecimal amount;

    private String currency;

    private LocalDate paymentDate;

    private String payerPayee;

    private String bankAccount;

    private String invoiceStatus;

    private String invoiceNo;

    private String remark;

    private String filePath;

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

    @TableField(exist = false)
    private String clientName;
}
