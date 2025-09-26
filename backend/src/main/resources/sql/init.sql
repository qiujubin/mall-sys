-- 创建数据库
CREATE DATABASE IF NOT EXISTS mall_sys DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall_sys;

-- 用户表
CREATE TABLE tbl_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account VARCHAR(50) NOT NULL COMMENT '账号',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '电话',
    gender VARCHAR(10) COMMENT '性别',
    employee_id VARCHAR(50) COMMENT '工号',
    dept_id BIGINT COMMENT '部门ID',
    role_id BIGINT COMMENT '角色ID',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 部门表（4级树形结构）
CREATE TABLE tbl_dept (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 角色表
CREATE TABLE tbl_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_desc VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 品牌表
CREATE TABLE tbl_brand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand_name VARCHAR(100) NOT NULL COMMENT '品牌名称',
    brand_logo VARCHAR(500) COMMENT '品牌LOGO',
    brand_desc TEXT COMMENT '品牌描述',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 商品类别表（3级树形结构）
CREATE TABLE tbl_goods_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL COMMENT '类别名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父类别ID',
    level INT DEFAULT 1 COMMENT '层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 品牌类别关联表
CREATE TABLE tbl_brand_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand_id BIGINT NOT NULL COMMENT '品牌ID',
    brand_name VARCHAR(100) NOT NULL COMMENT '品牌名称',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    category_name VARCHAR(100) NOT NULL COMMENT '类别名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 属性分组表
CREATE TABLE tbl_goods_attr_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_name VARCHAR(100) NOT NULL COMMENT '分组名称',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    icon VARCHAR(200) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    searchable TINYINT DEFAULT 1 COMMENT '是否可检索 1:是 0:否',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 规格参数表
CREATE TABLE tbl_goods_attr (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    attr_name VARCHAR(100) NOT NULL COMMENT '属性名称',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    attr_group_id BIGINT COMMENT '属性分组ID',
    attr_type TINYINT DEFAULT 1 COMMENT '属性类型 1:基本属性 2:销售属性',
    value_type TINYINT DEFAULT 1 COMMENT '值类型 1:单值 2:多值',
    attr_value TEXT COMMENT '可选值',
    searchable TINYINT DEFAULT 1 COMMENT '是否可检索 1:是 0:否',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 商品SPU表
CREATE TABLE tbl_goods_spu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    spu_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    spu_desc TEXT COMMENT '商品描述',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    brand_id BIGINT NOT NULL COMMENT '品牌ID',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志 0:未删除 1:已删除'
);

-- 插入初始数据
-- 插入角色数据
INSERT INTO tbl_role (role_name, role_desc) VALUES 
('总经理', '总经理角色'),
('部门主管', '部门主管角色'),
('普通员工', '普通员工角色');

-- 插入部门数据
INSERT INTO tbl_dept (dept_name, parent_id, level, sort_order) VALUES 
('总经办', 0, 1, 1),
('开发部', 0, 1, 2),
('市场部', 0, 1, 3),
('物流部', 0, 1, 4),
('网络部', 0, 1, 5),
('开心编程部88', 0, 1, 6),
('行政部', 1, 2, 1),
('财务部', 1, 2, 2),
('制造部', 1, 2, 3);

-- 插入用户数据
INSERT INTO tbl_user (account, password, nickname, phone, gender, employee_id, dept_id, role_id) VALUES 
('admin', 'admin123', '管理员', '13800138000', '男', 'admin001', 1, 1),
('andy', 'andy123', '安迪', '15604413552', '男', 'no02', 1, 1),
('test', '123456', '测试用户', '13800138001', '男', 'test001', 1, 1);

-- 插入商品类别数据
INSERT INTO tbl_goods_category (category_name, parent_id, level, sort_order) VALUES 
('手机/运营商/数码', 0, 1, 1),
('运营商', 0, 1, 2),
('手机通讯', 1, 2, 1),
('合同机', 2, 2, 1),
('手机卡', 2, 2, 2),
('宽带', 2, 2, 3),
('游戏手机', 3, 3, 1),
('5G手机', 3, 3, 2),
('拍照手机', 3, 3, 3),
('全面屏手机', 3, 3, 4),
('老人机', 3, 3, 5),
('对讲机', 3, 3, 6);

-- 插入品牌数据
INSERT INTO tbl_brand (brand_name, brand_logo, brand_desc, status, create_time) VALUES 
('苹果', '', '苹果品牌', 1, NOW()),
('华为', '', '华为品牌', 1, NOW()),
('小米', '', '小米品牌', 1, NOW()),
('OPPO', '', 'OPPO品牌', 1, NOW()),
('vivo', '', 'vivo品牌', 1, NOW());

-- 插入属性分组数据
INSERT INTO tbl_goods_attr_group (group_name, category_id, sort_order) VALUES 
('主体', 7, 1),
('屏幕', 7, 2),
('网络支持', 7, 3),
('操作系统', 7, 4);

-- 插入规格参数数据
INSERT INTO tbl_goods_attr (attr_name, category_id, attr_group_id, attr_type, value_type, attr_value) VALUES 
('入网型号', 7, 1, 1, 1, ''),
('存储空间', 7, 1, 1, 2, '64GB;128GB;256GB;512GB'),
('机身颜色', 7, 1, 1, 2, '黑色;白色;蓝色;红色'),
('屏幕尺寸', 7, 2, 1, 1, ''),
('屏幕分辨率', 7, 2, 1, 1, ''),
('上市年份', 7, 4, 1, 1, '');
