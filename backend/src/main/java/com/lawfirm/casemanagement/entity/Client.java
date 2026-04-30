package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("client")
public class Client {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String clientName;

    private String clientType;

    private String idCard;

    private String phone;

    private String email;

    private String address;

    private String industry;

    private String legalPerson;

    private Long createUserId;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
