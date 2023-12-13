/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.1.100mysql
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : 192.168.1.100:3306
 Source Schema         : metax_cloud

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 29/11/2023 10:24:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for channel_account
-- ----------------------------
DROP TABLE IF EXISTS `channel_account`;
CREATE TABLE `channel_account`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '账号名称',
  `send_channel` tinyint(4) NOT NULL DEFAULT 0 COMMENT '消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人',
  `account_config` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '账号配置',
  `creator` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Hanabi' COMMENT '拥有者',
  `created` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除：0.不删除 1.删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_send_channel`(`send_channel`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '渠道账号信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 72 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_template
-- ----------------------------
DROP TABLE IF EXISTS `message_template`;
CREATE TABLE `message_template`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '标题',
  `msg_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败',
  `push_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '推送类型：10.实时 20.定时',
  `cron_task_id` bigint(20) NULL DEFAULT NULL COMMENT '定时任务Id (xxl-job-admin返回)',
  `cron_crowd_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '定时发送人群的文件路径',
  `expect_push_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式',
  `send_channel` int(10) NOT NULL DEFAULT 0 COMMENT '消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人',
  `msg_content` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息内容 占位符用${var}表示',
  `send_account` tinyint(20) NOT NULL DEFAULT 0 COMMENT '发送账号 一个渠道下可存在多个账号',
  `creator` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `updator` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `msg_type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '10.通知类消息 20.营销类消息 30.验证码类消息',
  `audit_status` tinyint(4) NOT NULL DEFAULT 10 COMMENT '当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝',
  `current_id` bigint(20) NULL DEFAULT -1 COMMENT '当前定时模板使用用户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_channel`(`send_channel`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息模板信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-purple', 'Y', 'admin', '2023-09-02 11:45:56', 'admin', '2023-09-13 00:38:02', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2023-09-02 11:45:56', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2023-09-02 11:45:56', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'true', 'Y', 'admin', '2023-09-02 11:45:56', 'admin', '2023-09-13 00:37:08', '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2023-09-02 11:45:56', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', 'xx科技', 0, 'hanabi', '15888888888', '2b@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-10-19 11:13:27');
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '广州总公司', 1, 'hanabi', '15888888888', 'metax@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:37:39');
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '东莞分公司', 2, 'hanabi', '15888888888', 'metax@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-09-29 15:59:37');
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:49');
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:43');
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:29');
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:37');
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:24');
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:10');
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, 'hanabi', '15888888888', 'hanabi@qq.com', '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:38:17');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (30, 10, '发送', '10', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (31, 11, '启动', '11', 'sys_oper_type', '', 'success', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (32, 12, '停止', '12', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (33, 0, '实时', '实时', 'sys_push_type', NULL, 'warning', 'N', '0', 'admin', '2023-10-12 23:05:48', 'admin', '2023-10-12 23:12:08', NULL);
INSERT INTO `sys_dict_data` VALUES (34, 1, '定时', '定时', 'sys_push_type', NULL, 'success', 'N', '0', 'admin', '2023-10-12 23:06:04', 'admin', '2023-10-12 23:06:11', '定时');
INSERT INTO `sys_dict_data` VALUES (35, 0, '待审核', '10', 'sys_audit_status', NULL, 'warning', 'N', '0', 'admin', '2023-10-17 14:56:43', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (36, 1, '审核通过', '20', 'sys_audit_status', NULL, 'success', 'N', '0', 'admin', '2023-10-17 14:57:32', 'admin', '2023-10-18 01:49:12', NULL);
INSERT INTO `sys_dict_data` VALUES (37, 2, '不通过', '30', 'sys_audit_status', NULL, 'danger', 'N', '0', 'admin', '2023-10-17 14:57:49', 'admin', '2023-10-18 01:48:15', NULL);
INSERT INTO `sys_dict_data` VALUES (38, 0, '正常', '正常', 'sys_msg_status', NULL, 'primary', 'N', '0', 'admin', '2023-10-17 15:13:16', 'admin', '2023-10-17 15:13:35', NULL);
INSERT INTO `sys_dict_data` VALUES (39, 1, '已启用', '已启用', 'sys_msg_status', NULL, 'success', 'N', '0', 'admin', '2023-10-17 15:13:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (40, 2, '已停用', '已停用', 'sys_msg_status', NULL, 'info', 'N', '0', 'admin', '2023-10-17 15:13:49', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (41, 0, '任务开始', '任务开始', 'sys_cron_task_status', NULL, 'primary', 'N', '0', 'admin', '2023-10-18 12:04:27', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (42, 1, '启动中', '启动中', 'sys_cron_task_status', NULL, 'primary', 'N', '0', 'admin', '2023-10-18 12:04:52', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (43, 2, '发送中', '发送中', 'sys_cron_task_status', NULL, 'warning', 'N', '0', 'admin', '2023-10-18 12:05:18', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (44, 3, '发送成功', '发送成功', 'sys_cron_task_status', NULL, 'success', 'N', '0', 'admin', '2023-10-18 12:05:32', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (45, 4, '已暂停', '已暂停', 'sys_cron_task_status', NULL, 'info', 'N', '0', 'admin', '2023-10-18 12:05:51', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (46, 5, '发送失败', '发送失败', 'sys_cron_task_status', NULL, 'danger', 'N', '0', 'admin', '2023-10-18 12:06:09', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (47, 0, '发送中', '发送中', 'sys_send_task_status', NULL, 'warning', 'N', '0', 'admin', '2023-10-18 12:10:47', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (48, 1, '发送成功', '发送成功', 'sys_send_task_status', NULL, 'success', 'N', '0', 'admin', '2023-10-18 12:10:59', '', NULL, NULL);
INSERT INTO `sys_dict_data` VALUES (49, 2, '发送失败', '发送失败', 'sys_send_task_status', NULL, 'danger', 'N', '0', 'admin', '2023-10-18 12:11:14', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '1', 'admin', '2023-09-02 11:45:56', 'admin', '2023-10-12 21:00:36', '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '1', 'admin', '2023-09-02 11:45:56', 'admin', '2023-10-12 21:00:13', '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-10-12 20:38:10', '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (11, '推送类型', 'sys_push_type', '0', 'admin', '2023-10-12 23:04:50', '', NULL, '消息推送类型');
INSERT INTO `sys_dict_type` VALUES (12, '审核状态', 'sys_audit_status', '0', 'admin', '2023-10-17 14:55:53', '', NULL, '审核状态');
INSERT INTO `sys_dict_type` VALUES (13, '消息状态', 'sys_msg_status', '0', 'admin', '2023-10-17 15:12:49', 'admin', '2023-10-18 12:02:47', '消息状态字典');
INSERT INTO `sys_dict_type` VALUES (14, '定时模板字典', 'sys_cron_task_status', '0', 'admin', '2023-10-18 12:03:11', 'admin', '2023-10-18 12:03:50', '用于定时模板链路追踪\n');
INSERT INTO `sys_dict_type` VALUES (15, '消息发送状态', 'sys_send_task_status', '0', 'admin', '2023-10-18 12:10:12', 'admin', '2023-10-18 12:10:25', '用于消息链路追踪');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2023-09-02 11:45:56', '', NULL, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示信息',
  `access_time` datetime(0) NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status`) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`access_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 115 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (1, 'admin', '127.0.0.1', '0', '退出成功', '2023-10-31 11:08:58');
INSERT INTO `sys_logininfor` VALUES (2, 'admin', '127.0.0.1', '0', '登录成功', '2023-10-31 11:09:10');
INSERT INTO `sys_logininfor` VALUES (3, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-10-31 13:12:58');
INSERT INTO `sys_logininfor` VALUES (4, 'admin', '127.0.0.1', '0', '登录成功', '2023-10-31 21:41:15');
INSERT INTO `sys_logininfor` VALUES (5, 'admin', '127.0.0.1', '0', '登录成功', '2023-10-31 21:41:19');
INSERT INTO `sys_logininfor` VALUES (6, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-01 13:46:05');
INSERT INTO `sys_logininfor` VALUES (7, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-01 13:46:07');
INSERT INTO `sys_logininfor` VALUES (8, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-01 17:20:56');
INSERT INTO `sys_logininfor` VALUES (9, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-01 18:50:34');
INSERT INTO `sys_logininfor` VALUES (10, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-01 18:50:42');
INSERT INTO `sys_logininfor` VALUES (11, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-01 21:35:24');
INSERT INTO `sys_logininfor` VALUES (12, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-02 08:33:06');
INSERT INTO `sys_logininfor` VALUES (13, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-02 12:38:35');
INSERT INTO `sys_logininfor` VALUES (14, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-02 12:38:59');
INSERT INTO `sys_logininfor` VALUES (15, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-02 21:29:31');
INSERT INTO `sys_logininfor` VALUES (16, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-03 12:31:19');
INSERT INTO `sys_logininfor` VALUES (17, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-04 01:43:58');
INSERT INTO `sys_logininfor` VALUES (18, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-04 02:33:36');
INSERT INTO `sys_logininfor` VALUES (19, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-07 19:33:30');
INSERT INTO `sys_logininfor` VALUES (20, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-07 21:37:41');
INSERT INTO `sys_logininfor` VALUES (21, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-07 21:56:04');
INSERT INTO `sys_logininfor` VALUES (22, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-07 21:56:11');
INSERT INTO `sys_logininfor` VALUES (23, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-08 08:43:20');
INSERT INTO `sys_logininfor` VALUES (24, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-08 08:44:02');
INSERT INTO `sys_logininfor` VALUES (25, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-09 13:32:25');
INSERT INTO `sys_logininfor` VALUES (26, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-09 15:26:52');
INSERT INTO `sys_logininfor` VALUES (27, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-09 21:33:08');
INSERT INTO `sys_logininfor` VALUES (28, '若依', '127.0.0.1', '1', '登录用户不存在', '2023-11-12 19:18:15');
INSERT INTO `sys_logininfor` VALUES (29, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-12 19:18:28');
INSERT INTO `sys_logininfor` VALUES (30, '', '127.0.0.1', '0', '退出成功', '2023-11-12 20:24:19');
INSERT INTO `sys_logininfor` VALUES (31, '', '127.0.0.1', '0', '退出成功', '2023-11-12 20:26:18');
INSERT INTO `sys_logininfor` VALUES (32, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-12 20:26:31');
INSERT INTO `sys_logininfor` VALUES (33, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-12 20:28:27');
INSERT INTO `sys_logininfor` VALUES (34, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-12 20:29:01');
INSERT INTO `sys_logininfor` VALUES (35, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 11:19:30');
INSERT INTO `sys_logininfor` VALUES (36, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 21:00:28');
INSERT INTO `sys_logininfor` VALUES (37, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 22:11:12');
INSERT INTO `sys_logininfor` VALUES (38, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 22:15:04');
INSERT INTO `sys_logininfor` VALUES (39, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 22:15:12');
INSERT INTO `sys_logininfor` VALUES (40, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 23:10:21');
INSERT INTO `sys_logininfor` VALUES (41, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 23:10:27');
INSERT INTO `sys_logininfor` VALUES (42, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 23:11:12');
INSERT INTO `sys_logininfor` VALUES (43, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 23:22:31');
INSERT INTO `sys_logininfor` VALUES (44, 'admin', '127.0.0.1', '1', '登录用户不存在', '2023-11-15 23:25:58');
INSERT INTO `sys_logininfor` VALUES (45, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 23:26:16');
INSERT INTO `sys_logininfor` VALUES (46, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 23:31:01');
INSERT INTO `sys_logininfor` VALUES (47, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 23:35:31');
INSERT INTO `sys_logininfor` VALUES (48, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 23:35:37');
INSERT INTO `sys_logininfor` VALUES (49, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-15 23:50:18');
INSERT INTO `sys_logininfor` VALUES (50, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-15 23:50:22');
INSERT INTO `sys_logininfor` VALUES (51, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-16 00:35:04');
INSERT INTO `sys_logininfor` VALUES (52, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-16 01:34:41');
INSERT INTO `sys_logininfor` VALUES (53, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-16 01:34:52');
INSERT INTO `sys_logininfor` VALUES (54, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-16 01:34:57');
INSERT INTO `sys_logininfor` VALUES (55, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-16 01:35:00');
INSERT INTO `sys_logininfor` VALUES (56, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-16 01:37:58');
INSERT INTO `sys_logininfor` VALUES (57, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-16 01:38:03');
INSERT INTO `sys_logininfor` VALUES (58, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-16 01:38:12');
INSERT INTO `sys_logininfor` VALUES (59, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-16 01:38:25');
INSERT INTO `sys_logininfor` VALUES (60, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-16 01:40:35');
INSERT INTO `sys_logininfor` VALUES (61, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-16 01:41:07');
INSERT INTO `sys_logininfor` VALUES (62, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-16 16:41:17');
INSERT INTO `sys_logininfor` VALUES (63, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-16 16:41:37');
INSERT INTO `sys_logininfor` VALUES (64, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-16 16:41:38');
INSERT INTO `sys_logininfor` VALUES (65, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-16 17:53:37');
INSERT INTO `sys_logininfor` VALUES (66, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-16 20:43:10');
INSERT INTO `sys_logininfor` VALUES (67, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-17 11:24:24');
INSERT INTO `sys_logininfor` VALUES (68, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 11:26:17');
INSERT INTO `sys_logininfor` VALUES (69, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 17:03:26');
INSERT INTO `sys_logininfor` VALUES (70, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-17 19:35:58');
INSERT INTO `sys_logininfor` VALUES (71, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-17 19:36:46');
INSERT INTO `sys_logininfor` VALUES (72, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-17 19:59:15');
INSERT INTO `sys_logininfor` VALUES (73, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 19:59:20');
INSERT INTO `sys_logininfor` VALUES (74, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-17 20:02:40');
INSERT INTO `sys_logininfor` VALUES (75, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 20:02:43');
INSERT INTO `sys_logininfor` VALUES (76, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-17 20:04:14');
INSERT INTO `sys_logininfor` VALUES (77, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 20:04:19');
INSERT INTO `sys_logininfor` VALUES (78, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-17 20:41:29');
INSERT INTO `sys_logininfor` VALUES (79, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-18 13:23:56');
INSERT INTO `sys_logininfor` VALUES (80, 'hanabi', '127.0.0.1', '0', '退出成功', '2023-11-18 22:57:24');
INSERT INTO `sys_logininfor` VALUES (81, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-18 22:57:55');
INSERT INTO `sys_logininfor` VALUES (82, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-20 10:50:23');
INSERT INTO `sys_logininfor` VALUES (83, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-20 12:41:25');
INSERT INTO `sys_logininfor` VALUES (84, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-20 13:28:07');
INSERT INTO `sys_logininfor` VALUES (85, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-20 13:51:34');
INSERT INTO `sys_logininfor` VALUES (86, 'zzx', '127.0.0.1', '0', '注册成功', '2023-11-21 16:04:19');
INSERT INTO `sys_logininfor` VALUES (87, 'zzx', '127.0.0.1', '0', '登录成功', '2023-11-21 16:04:37');
INSERT INTO `sys_logininfor` VALUES (88, 'zzx', '127.0.0.1', '0', '退出成功', '2023-11-21 16:05:04');
INSERT INTO `sys_logininfor` VALUES (89, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-21 16:05:20');
INSERT INTO `sys_logininfor` VALUES (90, 'zzx', '127.0.0.1', '0', '注册成功', '2023-11-21 16:08:10');
INSERT INTO `sys_logininfor` VALUES (91, 'zzx', '127.0.0.1', '0', '注册成功', '2023-11-21 16:08:39');
INSERT INTO `sys_logininfor` VALUES (92, 'zzx', '127.0.0.1', '0', '登录成功', '2023-11-21 16:13:25');
INSERT INTO `sys_logininfor` VALUES (93, 'zzx', '127.0.0.1', '0', '退出成功', '2023-11-21 16:56:16');
INSERT INTO `sys_logininfor` VALUES (94, 'zzx', '127.0.0.1', '1', '登录用户不存在', '2023-11-21 16:56:24');
INSERT INTO `sys_logininfor` VALUES (95, 'zzx', '127.0.0.1', '0', '注册成功', '2023-11-21 16:56:37');
INSERT INTO `sys_logininfor` VALUES (96, 'zzx', '127.0.0.1', '0', '登录成功', '2023-11-21 16:56:46');
INSERT INTO `sys_logininfor` VALUES (97, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-21 17:02:52');
INSERT INTO `sys_logininfor` VALUES (98, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-21 17:20:38');
INSERT INTO `sys_logininfor` VALUES (99, 'zzx', '127.0.0.1', '0', '退出成功', '2023-11-21 17:26:32');
INSERT INTO `sys_logininfor` VALUES (100, 'zzx', '127.0.0.1', '0', '登录成功', '2023-11-21 17:58:32');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-21 18:44:35');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-21 18:45:18');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-21 18:48:14');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-21 19:23:22');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-22 10:03:12');
INSERT INTO `sys_logininfor` VALUES (106, 'hanabi', '127.0.0.1', '0', '登录成功', '2023-11-22 10:03:40');
INSERT INTO `sys_logininfor` VALUES (107, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-22 11:33:08');
INSERT INTO `sys_logininfor` VALUES (108, '小黄', '127.0.0.1', '0', '退出成功', '2023-11-22 11:33:55');
INSERT INTO `sys_logininfor` VALUES (109, '小黄', '127.0.0.1', '0', '登录成功', '2023-11-22 11:34:08');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-23 16:32:03');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-24 14:36:30');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '127.0.0.1', '0', '退出成功', '2023-11-24 14:46:04');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-29 09:02:37');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '127.0.0.1', '0', '登录成功', '2023-11-29 09:38:38');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2079 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 8, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:44:40', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 7, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:44:35', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 10, 'tool', NULL, '', 1, 0, 'M', '1', '0', '', 'tool', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:42:48', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '测试', 0, 12, 'Index_v3', 'index_v3', '', 1, 0, 'C', '1', '0', '', 'guide', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:42:53', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 2078, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:43:23', '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 2078, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:43:31', '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 10, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:34:03', '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 2078, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:43:36', '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 2078, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:43:48', '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2023-09-02 11:45:56', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2023-09-02 11:45:56', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2023-09-02 11:45:56', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 0, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 20:34:54', '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 1, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-20 21:36:36', '在线用户菜单');
INSERT INTO `sys_menu` VALUES (111, '系统流量控制台', 2, 3, 'http://localhost:8090', '', '', 0, 0, 'C', '0', '0', 'monitor:sentinel:list', 'sentinel', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-18 13:24:41', '流量控制菜单');
INSERT INTO `sys_menu` VALUES (112, '系统服务控制台', 2, 4, 'http://192.168.1.100:8848/nacos', '', '', 0, 0, 'C', '0', '0', 'monitor:nacos:list', 'nacos', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-09 21:57:22', '服务治理菜单');
INSERT INTO `sys_menu` VALUES (113, '系统状态控制台', 2, 5, 'http://localhost:9100/login', '', '', 0, 0, 'C', '0', '0', 'monitor:server:list', 'springBootAdmin', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-09 22:00:01', '服务监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2023-09-02 11:45:56', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2023-09-02 11:45:56', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'http://localhost:8080/swagger-ui/index.html', '', '', 0, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2023-09-02 11:45:56', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'system/operlog/index', '', 1, 0, 'C', '0', '0', 'system:operlog:list', 'form', 'admin', '2023-09-02 11:45:56', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'system/logininfor/index', '', 1, 0, 'C', '0', '0', 'system:logininfor:list', 'logininfor', 'admin', '2023-09-02 11:45:56', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:operlog:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:operlog:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:operlog:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:export', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:logininfor:unlock', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2024, '消息模板', 2036, 1, 'message_template', 'web/message_template/index', NULL, 1, 0, 'C', '0', '0', 'web:message_template:list', 'list', 'admin', '2023-09-02 15:35:42', 'admin', '2023-09-13 00:34:43', '消息模板菜单');
INSERT INTO `sys_menu` VALUES (2025, '消息模板查询', 2024, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template:query', '#', 'admin', '2023-09-02 15:35:42', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2026, '消息模板新增', 2024, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template:add', '#', 'admin', '2023-09-02 15:35:42', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2027, '消息模板修改', 2024, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template:edit', '#', 'admin', '2023-09-02 15:35:42', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2028, '消息模板删除', 2024, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template:remove', '#', 'admin', '2023-09-02 15:35:42', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2029, '消息模板导出', 2024, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template:export', '#', 'admin', '2023-09-02 15:35:42', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2030, '渠道账号', 2066, 1, 'channel_account', 'web/channel_account/index', NULL, 1, 0, 'C', '0', '0', 'web:channel_account:list', 'client', 'admin', '2023-09-02 17:08:38', 'admin', '2023-10-19 10:54:08', '渠道账号菜单');
INSERT INTO `sys_menu` VALUES (2031, '渠道账号查询', 2030, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:channel_account:query', '#', 'admin', '2023-09-02 17:08:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2032, '渠道账号新增', 2030, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:channel_account:add', '#', 'admin', '2023-09-02 17:08:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2033, '渠道账号修改', 2030, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:channel_account:edit', '#', 'admin', '2023-09-02 17:08:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2034, '渠道账号删除', 2030, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:channel_account:remove', '#', 'admin', '2023-09-02 17:08:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2035, '渠道账号导出', 2030, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:channel_account:export', '#', 'admin', '2023-09-02 17:08:38', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2036, '模板管理', 0, 0, 'web', NULL, NULL, 1, 0, 'M', '0', '0', '', 'guide', 'admin', '2023-09-13 00:34:17', 'admin', '2023-11-01 00:59:39', '');
INSERT INTO `sys_menu` VALUES (2037, '定时任务启动', 2024, 5, 'message_template', 'web/message_template/index', NULL, 1, 0, 'C', '0', '0', 'web:message_template:start', 'job', 'admin', '2023-09-24 00:57:54', 'admin', '2023-09-24 11:18:56', '');
INSERT INTO `sys_menu` VALUES (2038, '定时任务停止', 2024, 6, 'message_template', 'web/message_template/index', NULL, 1, 0, 'C', '0', '0', 'web:message_template:stop', 'button', 'admin', '2023-09-24 00:59:14', 'admin', '2023-09-24 11:17:33', '');
INSERT INTO `sys_menu` VALUES (2039, '消息模板发送', 2024, 5, 'message_template', 'web/message_template/index', NULL, 1, 0, 'C', '0', '0', 'web:message_template:send', 'guide', 'admin', '2023-09-24 11:18:40', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2046, '消息链路追踪', 2058, 0, 'send_task_info', 'web/send_task_info/index', NULL, 1, 0, 'C', '0', '0', 'web:send_task_info:list', 'druid', 'admin', '2023-10-03 21:21:59', 'admin', '2023-10-18 12:22:18', '消息查询菜单');
INSERT INTO `sys_menu` VALUES (2047, '消息查询查询', 2046, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:send_task_info:query', '#', 'admin', '2023-10-03 21:21:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2048, '消息查询新增', 2046, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:send_task_info:add', '#', 'admin', '2023-10-03 21:21:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2049, '消息查询修改', 2046, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:send_task_info:edit', '#', 'admin', '2023-10-03 21:21:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2050, '消息查询删除', 2046, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:send_task_info:remove', '#', 'admin', '2023-10-03 21:21:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2051, '消息查询导出', 2046, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:send_task_info:export', '#', 'admin', '2023-10-03 21:21:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2052, '定时模板链路追踪', 2058, 1, 'cron_task_cords', 'web/cron_task_cords/index', NULL, 1, 0, 'C', '0', '0', 'web:cron_task_cords:list', 'job', 'admin', '2023-10-07 23:15:20', 'admin', '2023-10-08 17:29:17', '定时模板链路追踪菜单');
INSERT INTO `sys_menu` VALUES (2053, '定时模板链路追踪查询', 2052, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:cron_task_cords:query', '#', 'admin', '2023-10-07 23:15:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2054, '定时模板链路追踪新增', 2052, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:cron_task_cords:add', '#', 'admin', '2023-10-07 23:15:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2055, '定时模板链路追踪修改', 2052, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:cron_task_cords:edit', '#', 'admin', '2023-10-07 23:15:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2056, '定时模板链路追踪删除', 2052, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:cron_task_cords:remove', '#', 'admin', '2023-10-07 23:15:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2057, '定时模板链路追踪导出', 2052, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:cron_task_cords:export', '#', 'admin', '2023-10-07 23:15:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2058, '链路追踪', 0, 1, 'track', NULL, NULL, 1, 0, 'M', '0', '0', '', '链路追踪', 'admin', '2023-10-08 12:59:06', 'admin', '2023-11-10 00:00:53', '');
INSERT INTO `sys_menu` VALUES (2059, '模板审核', 2036, 2, 'message_template_audit', 'web/message_template_audit/index', NULL, 1, 0, 'C', '0', '0', 'web:message_template_audit:list', 'checkbox', 'admin', '2023-10-17 15:29:24', 'admin', '2023-10-19 10:49:40', '模板审核菜单');
INSERT INTO `sys_menu` VALUES (2060, '模板审核查询', 2059, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template_audit:query', '#', 'admin', '2023-10-17 15:29:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2061, '模板审核新增', 2059, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template_audit:add', '#', 'admin', '2023-10-17 15:29:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2062, '模板审核修改', 2059, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template_audit:edit', '#', 'admin', '2023-10-17 15:29:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2063, '模板审核删除', 2059, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template_audit:remove', '#', 'admin', '2023-10-17 15:29:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2064, '模板审核导出', 2059, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'web:message_template_audit:export', '#', 'admin', '2023-10-17 15:29:24', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2065, '接受者链路追踪', 2058, 2, 'receiver_records', 'web/receiver_records/index', NULL, 1, 0, 'C', '0', '0', 'web:receiver_records:list', 'peoples', 'admin', '2023-10-19 09:14:56', 'admin', '2023-10-19 10:47:58', '');
INSERT INTO `sys_menu` VALUES (2066, '渠道管理', 0, 2, 'sendChannel', NULL, NULL, 1, 0, 'M', '0', '0', '', '渠道', 'admin', '2023-10-19 10:53:51', 'admin', '2023-11-01 00:56:54', '');
INSERT INTO `sys_menu` VALUES (2067, 'RabbitMQ控制台', 2, 3, 'http://192.168.1.100:15672', NULL, NULL, 0, 0, 'C', '0', '0', 'monitor:rabbitmq:list', 'rabbitmq', 'admin', '2023-10-19 13:19:56', 'admin', '2023-10-19 13:21:43', '');
INSERT INTO `sys_menu` VALUES (2068, '定时任务调度中心', 2, 4, 'http://localhost:8880/xxl-job-admin', NULL, NULL, 0, 0, 'C', '0', '0', 'monitor:xxl-job:list', 'xxl-job', 'admin', '2023-10-19 13:23:04', 'admin', '2023-11-09 21:56:40', '');
INSERT INTO `sys_menu` VALUES (2069, '短信回执', 0, 4, 'sms_records', NULL, NULL, 1, 0, 'M', '0', '0', '', '短信营销-群发短信', 'admin', '2023-10-30 16:58:13', 'admin', '2023-11-09 16:01:57', '');
INSERT INTO `sys_menu` VALUES (2070, '回执拉取(保留30天)', 2069, 0, 'sms_records', 'web/sms_records/index', NULL, 1, 0, 'C', '0', '0', 'web:sms_records:list', '重新拉取', 'admin', '2023-10-30 17:08:59', 'admin', '2023-11-01 14:54:25', '');
INSERT INTO `sys_menu` VALUES (2071, '数据分析', 0, 3, 'analyse', NULL, NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', '2023-11-09 15:21:09', 'admin', '2023-11-09 16:02:50', '');
INSERT INTO `sys_menu` VALUES (2072, '模板分析', 2071, 0, 'template_analyse', 'web/template_analyse/index', NULL, 1, 0, 'C', '0', '0', 'web:template_analyse:list', 'chart-bar', 'admin', '2023-11-09 15:25:20', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2073, '即时通信', 0, 4, 'talk', NULL, NULL, 1, 0, 'M', '0', '0', '', 'message', 'admin', '2023-11-15 11:24:51', 'admin', '2023-11-16 17:49:14', '');
INSERT INTO `sys_menu` VALUES (2074, '即时聊天', 2073, 0, 'talk', 'web/talk/index', NULL, 1, 0, 'C', '0', '0', 'web:talk:list', 'faxian', 'admin', '2023-11-15 11:26:35', 'admin', '2023-11-16 17:50:56', '');
INSERT INTO `sys_menu` VALUES (2075, '消息管理', 0, 4, 'message', NULL, NULL, 1, 0, 'M', '0', '0', '', '电子邮件_email102', 'admin', '2023-11-20 11:34:45', 'admin', '2023-11-20 18:27:35', '');
INSERT INTO `sys_menu` VALUES (2076, '用户数据分析', 2075, 0, 'message_analyse', 'web/message_analyse/index', NULL, 1, 0, 'C', '0', '0', 'web:message_analyse:list', '统计分析', 'admin', '2023-11-20 11:50:55', 'admin', '2023-11-20 14:14:43', '');
INSERT INTO `sys_menu` VALUES (2077, '用户消息列表', 2075, 1, 'message_view', 'web/message_view/index', NULL, 1, 0, 'C', '0', '0', 'web:message_view:list', '服务商', 'admin', '2023-11-20 11:52:42', 'admin', '2023-11-20 19:56:11', '');
INSERT INTO `sys_menu` VALUES (2078, '权限管理', 0, 6, 'security', NULL, NULL, 1, 0, 'M', '0', '0', '', '权限', 'admin', '2023-11-20 20:42:06', 'admin', '2023-11-20 21:36:06', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2023-09-02 11:45:56', 'hanabi', '2023-11-12 20:30:49', '管理员');
INSERT INTO `sys_notice` VALUES (3, '测试', '1', 0x3C703EE59388E59388E59388E593883C2F703E, '0', 'admin', '2023-11-09 21:33:27', '', NULL, NULL);

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int(1) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type`) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status`) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 78 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2023-09-02 11:45:56', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2023-09-02 11:45:56', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2023-09-02 11:45:56', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '1', 1, 1, '0', '0', 'admin', '2023-09-02 11:45:56', 'admin', '2023-11-21 16:58:26', '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 2024);
INSERT INTO `sys_role_menu` VALUES (2, 2025);
INSERT INTO `sys_role_menu` VALUES (2, 2026);
INSERT INTO `sys_role_menu` VALUES (2, 2027);
INSERT INTO `sys_role_menu` VALUES (2, 2028);
INSERT INTO `sys_role_menu` VALUES (2, 2029);
INSERT INTO `sys_role_menu` VALUES (2, 2030);
INSERT INTO `sys_role_menu` VALUES (2, 2031);
INSERT INTO `sys_role_menu` VALUES (2, 2032);
INSERT INTO `sys_role_menu` VALUES (2, 2033);
INSERT INTO `sys_role_menu` VALUES (2, 2034);
INSERT INTO `sys_role_menu` VALUES (2, 2035);
INSERT INTO `sys_role_menu` VALUES (2, 2036);
INSERT INTO `sys_role_menu` VALUES (2, 2037);
INSERT INTO `sys_role_menu` VALUES (2, 2038);
INSERT INTO `sys_role_menu` VALUES (2, 2039);
INSERT INTO `sys_role_menu` VALUES (2, 2046);
INSERT INTO `sys_role_menu` VALUES (2, 2047);
INSERT INTO `sys_role_menu` VALUES (2, 2048);
INSERT INTO `sys_role_menu` VALUES (2, 2049);
INSERT INTO `sys_role_menu` VALUES (2, 2050);
INSERT INTO `sys_role_menu` VALUES (2, 2051);
INSERT INTO `sys_role_menu` VALUES (2, 2052);
INSERT INTO `sys_role_menu` VALUES (2, 2053);
INSERT INTO `sys_role_menu` VALUES (2, 2054);
INSERT INTO `sys_role_menu` VALUES (2, 2055);
INSERT INTO `sys_role_menu` VALUES (2, 2056);
INSERT INTO `sys_role_menu` VALUES (2, 2057);
INSERT INTO `sys_role_menu` VALUES (2, 2058);
INSERT INTO `sys_role_menu` VALUES (2, 2065);
INSERT INTO `sys_role_menu` VALUES (2, 2066);
INSERT INTO `sys_role_menu` VALUES (2, 2069);
INSERT INTO `sys_role_menu` VALUES (2, 2070);
INSERT INTO `sys_role_menu` VALUES (2, 2071);
INSERT INTO `sys_role_menu` VALUES (2, 2072);
INSERT INTO `sys_role_menu` VALUES (2, 2073);
INSERT INTO `sys_role_menu` VALUES (2, 2074);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`, `user_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', 'Hanabi', '00', 'metax@163.com', '15888888888', '1', 'http://127.0.0.1:9300/statics/2023/09/27/blob_20230927185533A001.png', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2023-09-02 11:45:56', 'admin', '2023-09-02 11:45:56', '', '2023-09-10 00:58:39', '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'hanabi', '2b', '00', '2b@qq.com', '15666666666', '1', '', '$2a$10$ncoCoITsOvq7fbFEgRxL6uqIIEj5N9vQyoFOvUo5xJIStE5M2/TWa', '0', '0', '127.0.0.1', '2023-09-02 11:45:56', 'admin', '2023-09-02 11:45:56', 'admin', '2023-10-18 14:27:52', '测试员');
INSERT INTO `sys_user` VALUES (3, 108, '小黄', '小黄', '00', '', '16666668956', '2', '', '$2a$10$pEgdvUHW3rNVZ8teEaikhe2DN88pZKP8YE..G1NrWgCi/ta0CIkE.', '0', '0', '', NULL, 'admin', '2023-11-16 17:52:57', 'admin', '2023-11-16 20:42:22', NULL);
INSERT INTO `sys_user` VALUES (4, NULL, 'zzx', 'zzx', '00', '', '', '0', '', '$2a$10$fBDOJpdfDkA6gZRPi6xJuuiesgkRqun9TGZf0Nz0ADoBgD3F2/tpa', '0', '2', '', NULL, '', '2023-11-21 16:04:19', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (5, NULL, 'zzx', 'zzx', '00', '', '', '0', '', '$2a$10$SwuhSo/5QJQuuQJ/lfPNGeGPP1j6F4yniRsIlrRfiy84VccXOguPW', '0', '2', '', NULL, '', '2023-11-21 16:08:10', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (6, NULL, 'zzx', 'zzx', '00', '', '', '0', '', '$2a$10$71wfFt5AZagWR7SHOVqkcOMbtrxkRI6IH7eLHP9v2aq5Lyl9P9NdK', '0', '2', '', NULL, '', '2023-11-21 16:08:39', '', NULL, NULL);
INSERT INTO `sys_user` VALUES (7, NULL, 'zzx', 'hanabizzx', '00', 'hanabi@163.com', '15813295667', '0', '', '$2a$10$xOSBeP/TYCKOABIYcVXDgezM6Q0irZZP.CzBvzomIzpEYFOU2LEoW', '0', '0', '', NULL, '', '2023-11-21 16:56:37', 'admin', '2023-11-21 16:57:40', NULL);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (2, 2);
INSERT INTO `sys_user_post` VALUES (3, 4);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 2);
INSERT INTO `sys_user_role` VALUES (7, 2);

SET FOREIGN_KEY_CHECKS = 1;
