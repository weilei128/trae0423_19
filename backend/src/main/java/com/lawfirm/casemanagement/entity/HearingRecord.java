package com.lawfirm.casemanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("hearing_record")
public class HearingRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private LocalDate hearingDate;

    private LocalTime hearingTime;

    private String court;

    private String courtRoom;

    private String judge;

    private String hearingType;

    private String hearingContent;

    private String judgeOpinion;

    private LocalDate nextHearingDate;

    private String filePath;

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
