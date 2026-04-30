package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("legal_document")
public class LegalDocument {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String documentNo;

    private String documentName;

    private String documentType;

    private Long caseId;

    private Long templateId;

    private Integer version;

    private String documentContent;

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
}
