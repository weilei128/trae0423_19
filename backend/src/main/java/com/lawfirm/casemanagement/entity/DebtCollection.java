package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("debt_collection")
public class DebtCollection {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private Long contractId;

    private Long clientId;

    private BigDecimal totalDebt;

    private BigDecimal outstandingDebt;

    private LocalDate dueDate;

    private Integer collectionCount;

    private LocalDate lastCollectionDate;

    private String lastCollectionResult;

    private LocalDate nextCollectionDate;

    private String status;

    private String remark;

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
