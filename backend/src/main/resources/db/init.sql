-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `role` VARCHAR(20) NOT NULL COMMENT '角色：PARTNER-合伙人，LAWYER-律师，ASSISTANT-助理',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 客户表
CREATE TABLE IF NOT EXISTS `client` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户ID',
    `client_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
    `client_type` VARCHAR(20) DEFAULT 'PERSON' COMMENT '客户类型：PERSON-个人，COMPANY-企业',
    `id_card` VARCHAR(50) COMMENT '身份证号/统一社会信用代码',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '电子邮箱',
    `address` VARCHAR(500) COMMENT '联系地址',
    `industry` VARCHAR(100) COMMENT '所属行业（企业客户）',
    `legal_person` VARCHAR(50) COMMENT '法定代表人（企业客户）',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_client_name` (`client_name`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- 案件类型表
CREATE TABLE IF NOT EXISTS `case_type` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类型ID',
    `type_name` VARCHAR(100) NOT NULL COMMENT '类型名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案件类型表';

-- 案件表
CREATE TABLE IF NOT EXISTS `case_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '案件ID',
    `case_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '案件编号',
    `case_name` VARCHAR(200) NOT NULL COMMENT '案件名称',
    `case_type_id` BIGINT NOT NULL COMMENT '案件类型ID',
    `case_level` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '案件级别：NORMAL-普通，IMPORTANT-重要，EMERGENCY-紧急',
    `client_id` BIGINT NOT NULL COMMENT '客户ID',
    `opposite_party` VARCHAR(200) COMMENT '对方当事人',
    `opposite_agent` VARCHAR(100) COMMENT '对方代理人',
    `opposite_law_firm` VARCHAR(200) COMMENT '对方律所',
    `court` VARCHAR(200) COMMENT '受理法院',
    `case_date` DATE COMMENT '立案日期',
    `judge` VARCHAR(50) COMMENT '承办法官',
    `lead_lawyer_id` BIGINT COMMENT '主办律师ID',
    `assist_lawyer_ids` VARCHAR(500) COMMENT '协办律师/助理ID，逗号分隔',
    `case_status` VARCHAR(20) DEFAULT 'NEW' COMMENT '案件状态：NEW-新建，PROCESSING-审理中，CLOSED-已结案，SUSPENDED-中止',
    `case_stage` VARCHAR(50) COMMENT '案件阶段：如一审、二审、执行等',
    `amount` DECIMAL(18,2) COMMENT '涉案金额',
    `description` TEXT COMMENT '案件描述',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_no` (`case_no`),
    INDEX `idx_client_id` (`client_id`),
    INDEX `idx_lead_lawyer` (`lead_lawyer_id`),
    INDEX `idx_case_status` (`case_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案件表';

-- 委托合同表
CREATE TABLE IF NOT EXISTS `contract` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '合同ID',
    `contract_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
    `contract_name` VARCHAR(200) NOT NULL COMMENT '合同名称',
    `case_id` BIGINT NOT NULL COMMENT '案件ID',
    `client_id` BIGINT NOT NULL COMMENT '客户ID',
    `template_id` BIGINT COMMENT '使用的模板ID',
    `contract_type` VARCHAR(50) COMMENT '合同类型',
    `sign_date` DATE COMMENT '签订日期',
    `expire_date` DATE COMMENT '到期日期',
    `total_fee` DECIMAL(18,2) COMMENT '约定律师费总额',
    `payment_method` VARCHAR(200) COMMENT '付款方式',
    `payment_schedule` TEXT COMMENT '付款计划（JSON）',
    `sign_status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '签署状态：DRAFT-草稿，SIGNED-已签署',
    `contract_content` LONGTEXT COMMENT '合同内容',
    `file_path` VARCHAR(500) COMMENT '合同文件路径',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_contract_no` (`contract_no`),
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='委托合同表';

-- 合同模板表
CREATE TABLE IF NOT EXISTS `contract_template` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(200) NOT NULL COMMENT '模板名称',
    `template_type` VARCHAR(50) COMMENT '模板类型',
    `file_path` VARCHAR(500) NOT NULL COMMENT '模板文件路径',
    `description` TEXT COMMENT '模板说明',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同模板表';

-- 案件进度节点表
CREATE TABLE IF NOT EXISTS `case_progress` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '进度ID',
    `case_id` BIGINT NOT NULL COMMENT '案件ID',
    `progress_type` VARCHAR(50) COMMENT '进度类型',
    `progress_date` DATE COMMENT '进度日期',
    `progress_title` VARCHAR(200) NOT NULL COMMENT '进度标题',
    `progress_content` TEXT COMMENT '进度内容',
    `important` TINYINT DEFAULT 0 COMMENT '是否重要',
    `next_step` VARCHAR(500) COMMENT '下一步计划',
    `remind_date` DATETIME COMMENT '提醒时间',
    `remind_user_ids` VARCHAR(500) COMMENT '提醒人ID，逗号分隔',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_progress_date` (`progress_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案件进度节点表';

-- 开庭记录表
CREATE TABLE IF NOT EXISTS `hearing_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `case_id` BIGINT NOT NULL COMMENT '案件ID',
    `hearing_date` DATE COMMENT '开庭日期',
    `hearing_time` TIME COMMENT '开庭时间',
    `court` VARCHAR(200) COMMENT '法院',
    `court_room` VARCHAR(100) COMMENT '法庭',
    `judge` VARCHAR(100) COMMENT '法官',
    `hearing_type` VARCHAR(50) COMMENT '庭审类型：一审、二审、执行听证等',
    `hearing_content` TEXT COMMENT '庭审内容',
    `judge_opinion` TEXT COMMENT '法官意见',
    `next_hearing_date` DATE COMMENT '下次开庭时间',
    `file_path` VARCHAR(500) COMMENT '庭审记录文件路径',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_hearing_date` (`hearing_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开庭记录表';

-- 证据材料表
CREATE TABLE IF NOT EXISTS `evidence` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '证据ID',
    `case_id` BIGINT NOT NULL COMMENT '案件ID',
    `evidence_name` VARCHAR(200) NOT NULL COMMENT '证据名称',
    `evidence_type` VARCHAR(50) COMMENT '证据类型',
    `evidence_no` VARCHAR(50) COMMENT '证据编号',
    `submit_date` DATE COMMENT '提交日期',
    `submit_court` VARCHAR(200) COMMENT '提交法院',
    `evidence_source` VARCHAR(200) COMMENT '证据来源',
    `description` TEXT COMMENT '证据说明',
    `file_path` VARCHAR(500) COMMENT '文件路径',
    `is_original` TINYINT DEFAULT 0 COMMENT '是否原件',
    `status` VARCHAR(20) DEFAULT 'STORED' COMMENT '状态：STORED-已存档，SUBMITTED-已提交，RETURNED-已退还',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_id` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证据材料表';

-- 法律文书表
CREATE TABLE IF NOT EXISTS `legal_document` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文书ID',
    `document_no` VARCHAR(50) COMMENT '文书编号',
    `document_name` VARCHAR(200) NOT NULL COMMENT '文书名称',
    `document_type` VARCHAR(50) COMMENT '文书类型',
    `case_id` BIGINT NOT NULL COMMENT '案件ID',
    `template_id` BIGINT COMMENT '使用的模板ID',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `document_content` LONGTEXT COMMENT '文书内容',
    `file_path` VARCHAR(500) COMMENT '文件路径',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，FINAL-定稿',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_id` (`case_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法律文书表';

-- 文书模板表
CREATE TABLE IF NOT EXISTS `document_template` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(200) NOT NULL COMMENT '模板名称',
    `template_type` VARCHAR(50) COMMENT '模板类型',
    `file_path` VARCHAR(500) NOT NULL COMMENT '模板文件路径',
    `description` TEXT COMMENT '模板说明',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板表';

-- 费用记录表
CREATE TABLE IF NOT EXISTS `fee_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `fee_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '费用编号',
    `case_id` BIGINT COMMENT '案件ID',
    `contract_id` BIGINT COMMENT '合同ID',
    `client_id` BIGINT NOT NULL COMMENT '客户ID',
    `fee_type` VARCHAR(20) NOT NULL COMMENT '费用类型：INCOME-收入，EXPENSE-支出',
    `fee_category` VARCHAR(50) COMMENT '费用分类',
    `fee_name` VARCHAR(200) NOT NULL COMMENT '费用名称',
    `amount` DECIMAL(18,2) NOT NULL COMMENT '金额',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '币种',
    `payment_date` DATE COMMENT '收付日期',
    `payer_payee` VARCHAR(200) COMMENT '付款方/收款方',
    `bank_account` VARCHAR(50) COMMENT '银行账号',
    `invoice_status` VARCHAR(20) DEFAULT 'NOT_ISSUED' COMMENT '发票状态：NOT_ISSUED-未开票，ISSUED-已开票',
    `invoice_no` VARCHAR(50) COMMENT '发票号码',
    `remark` TEXT COMMENT '备注',
    `file_path` VARCHAR(500) COMMENT '凭证文件路径',
    `status` VARCHAR(20) DEFAULT 'CONFIRMED' COMMENT '状态：PENDING-待确认，CONFIRMED-已确认',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_client_id` (`client_id`),
    INDEX `idx_fee_type` (`fee_type`),
    INDEX `idx_payment_date` (`payment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用记录表';

-- 欠款催收表
CREATE TABLE IF NOT EXISTS `debt_collection` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '催收ID',
    `case_id` BIGINT COMMENT '案件ID',
    `contract_id` BIGINT COMMENT '合同ID',
    `client_id` BIGINT NOT NULL COMMENT '客户ID',
    `total_debt` DECIMAL(18,2) NOT NULL COMMENT '欠款总额',
    `outstanding_debt` DECIMAL(18,2) NOT NULL COMMENT '未还金额',
    `due_date` DATE COMMENT '到期日期',
    `collection_count` INT DEFAULT 0 COMMENT '催收次数',
    `last_collection_date` DATE COMMENT '上次催收日期',
    `last_collection_result` TEXT COMMENT '上次催收结果',
    `next_collection_date` DATE COMMENT '下次催收日期',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待催收，COLLECTING-催收中，PAID-已结清，WRITE_OFF-坏账',
    `remark` TEXT COMMENT '备注',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_client_id` (`client_id`),
    INDEX `idx_due_date` (`due_date`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='欠款催收表';

-- 催收记录表
CREATE TABLE IF NOT EXISTS `collection_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `collection_id` BIGINT NOT NULL COMMENT '催收ID',
    `collection_date` DATE COMMENT '催收日期',
    `collection_method` VARCHAR(50) COMMENT '催收方式：电话、邮件、上门、律师函等',
    `collection_content` TEXT COMMENT '催收内容',
    `client_response` TEXT COMMENT '客户反馈',
    `next_action` VARCHAR(500) COMMENT '下一步行动',
    `file_path` VARCHAR(500) COMMENT '相关文件路径',
    `create_user_id` BIGINT COMMENT '创建人ID',
    `create_time` DATETIME COMMENT '创建时间',
    `update_time` DATETIME COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0,
    INDEX `idx_collection_id` (`collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='催收记录表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT COMMENT '操作用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `module` VARCHAR(50) COMMENT '操作模块',
    `operation` VARCHAR(50) COMMENT '操作类型',
    `method` VARCHAR(200) COMMENT '请求方法',
    `params` TEXT COMMENT '请求参数',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    `error_msg` TEXT COMMENT '错误信息',
    `execute_time` BIGINT COMMENT '执行时间（毫秒）',
    `create_time` DATETIME COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
