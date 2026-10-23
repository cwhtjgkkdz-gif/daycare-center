-- ============================================
-- 托育中心管理系统 - 数据库初始化脚本
-- 使用前请先创建数据库: CREATE DATABASE daycare_center DEFAULT CHARACTER SET utf8mb4;
-- ============================================

-- ----------------------------
-- 1. 角色表（先创建，因为用户表依赖它）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(50) NOT NULL,
    `role_code` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

-- ----------------------------
-- 2. 系统用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `real_name` VARCHAR(50) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `role_id` BIGINT DEFAULT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1=启用,0=禁用',
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0=正常,1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

-- ----------------------------
-- 3. 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID,0=顶级',
    `menu_name` VARCHAR(50) NOT NULL,
    `menu_type` CHAR(1) NOT NULL COMMENT 'M=目录,C=菜单,F=按钮',
    `path` VARCHAR(200) DEFAULT NULL,
    `component` VARCHAR(255) DEFAULT NULL,
    `perms` VARCHAR(100) DEFAULT NULL,
    `icon` VARCHAR(50) DEFAULT NULL,
    `sort` INT DEFAULT 0,
    `visible` TINYINT DEFAULT 1 COMMENT '1=可见,0=隐藏',
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单';

-- ----------------------------
-- 4. 角色-菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`),
    CONSTRAINT `fk_rm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
    CONSTRAINT `fk_rm_menu` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联';

-- ----------------------------
-- 5. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `module` VARCHAR(50) DEFAULT NULL,
    `operation` VARCHAR(50) DEFAULT NULL,
    `method` VARCHAR(200) DEFAULT NULL,
    `request_params` TEXT,
    `operator` VARCHAR(50) DEFAULT NULL,
    `ip` VARCHAR(50) DEFAULT NULL,
    `cost_time` BIGINT DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ----------------------------
-- 6. 班级表
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `class_name` VARCHAR(50) NOT NULL,
    `class_type` VARCHAR(20) NOT NULL COMMENT '托班/小班/中班/大班',
    `max_capacity` INT DEFAULT 30,
    `current_count` INT DEFAULT 0,
    `description` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级';

-- ----------------------------
-- 7. 儿童档案表
-- ----------------------------
DROP TABLE IF EXISTS `child`;
CREATE TABLE `child` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `gender` CHAR(1) NOT NULL COMMENT '男/女',
    `birth_date` DATE DEFAULT NULL,
    `class_id` BIGINT DEFAULT NULL,
    `parent_name` VARCHAR(50) DEFAULT NULL,
    `parent_phone` VARCHAR(20) DEFAULT NULL,
    `allergies` VARCHAR(500) DEFAULT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1=在园,0=离园',
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`),
    CONSTRAINT `fk_child_class` FOREIGN KEY (`class_id`) REFERENCES `class_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='儿童档案';

-- ----------------------------
-- 8. 教职工表
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `gender` CHAR(1) NOT NULL,
    `position` VARCHAR(50) NOT NULL COMMENT '教师/保育员/后勤/财务',
    `phone` VARCHAR(20) DEFAULT NULL,
    `hire_date` DATE DEFAULT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1=在职,0=离职',
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教职工';

-- ----------------------------
-- 9. 园区活动表
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT,
    `type` VARCHAR(20) DEFAULT NULL COMMENT '户外活动/室内活动/节日庆典/亲子活动/其他',
    `location` VARCHAR(200) DEFAULT NULL,
    `start_time` DATETIME DEFAULT NULL,
    `end_time` DATETIME DEFAULT NULL,
    `publisher` VARCHAR(50) DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区活动';

-- ----------------------------
-- 10. 通知公告表
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT,
    `type` VARCHAR(20) DEFAULT NULL COMMENT '通知/公告/温馨提示',
    `publisher` VARCHAR(50) DEFAULT NULL,
    `publish_time` DATETIME DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告';

-- ----------------------------
-- 11. 财务表
-- ----------------------------
DROP TABLE IF EXISTS `finance`;
CREATE TABLE `finance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `record_no` VARCHAR(50) NOT NULL,
    `child_name` VARCHAR(50) DEFAULT NULL,
    `type` VARCHAR(20) NOT NULL COMMENT '收费/退费',
    `amount` DECIMAL(10,2) NOT NULL,
    `handler` VARCHAR(50) DEFAULT NULL,
    `handle_time` DATETIME DEFAULT NULL,
    `remark` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务记录';

-- ----------------------------
-- 12. 考勤表
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `child_id` BIGINT NOT NULL,
    `class_id` BIGINT DEFAULT NULL,
    `attendance_date` DATE NOT NULL,
    `check_in_time` DATETIME DEFAULT NULL,
    `check_out_time` DATETIME DEFAULT NULL,
    `status` VARCHAR(20) DEFAULT NULL COMMENT '出勤/缺勤/迟到/请假',
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    `is_deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_child_date` (`child_id`, `attendance_date`),
    CONSTRAINT `fk_att_child` FOREIGN KEY (`child_id`) REFERENCES `child` (`id`),
    CONSTRAINT `fk_att_class` FOREIGN KEY (`class_id`) REFERENCES `class_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录';

-- ============================================
-- 默认数据通过 Java DataInitializer 类自动初始化
-- 启动项目后自动检查并插入：
--   1. 3个默认角色（超级管理员、班主任、财务人员）
--   2. 1个管理员账号（admin / admin123）
--   3. 默认菜单结构
--   4. 角色-菜单权限关联
-- ============================================