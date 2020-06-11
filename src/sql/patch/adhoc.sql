-- 2020.05.28 YHAAO-6501 pin date or phantom date < 90 days
insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet62f843e9b008629', 506, now(), null, 0, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源已经逾期，请您加速校正源购买和更换进程，逾期使用校正源或不进行Daily QA质控都将会严重影响PET图像质量。谢谢！', true, false, true);

insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet63f843e9b008629', 507, now(), 0, 60, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源马上到期，由于校正源购买周期很长，请您尽快准备校正源更换事宜，逾期使用校正源将会严重影响PET图像质量。谢谢！', true, false, true);

insert into asset_alert_config(id, config_type, create_date, lower_limit, upper_limit, check_type, device_type, is_default, is_notify_customer,
is_notify_fe, is_notify_ole, is_notify_sales, label, mute_period, pad_message, unit_of_measure, valid_values, wechat_message, is_enabled, ole_config, message_delay)
values ('21d87039ea3e45pet64f843e9b008629', 508, now(), 60, 90, 5, 5, true, true, true, false, true, '校正源寿命', 10080, null,
'D', null, 'PETCT设备所配置的校正源即将到期，由于校正源购买周期很长，请您尽早开始准备校正源更换事宜，谢谢！', true, false, true);



-- 2020.06.11 YHAAO-7278 质控读取新增默认配置数据
-- Drop table

DROP TABLE IF EXISTS public.audit_config;
DROP TABLE IF EXISTS public.audit_file;
DROP TABLE IF EXISTS public.audit_group;
DROP TABLE IF EXISTS public.audit_item;
DROP TABLE IF EXISTS public.audit_link;
DROP TABLE IF EXISTS public.audit_module;
DROP TABLE IF EXISTS public.audit_summary;


DROP TABLE IF EXISTS public.audit_default_file;

CREATE TABLE public.audit_default_file (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	expired bool NULL,
	file_id varchar(255) NULL,
	file_type varchar(255) NULL,
	id int4 NULL,
	item_id int4 NULL,
	"name" varchar(255) NULL,
	"template" bool NULL,
	upload_by varchar(255) NULL,
	upload_time timestamp NULL,
	url varchar(255) NULL,
	CONSTRAINT audit_default_file_pkey PRIMARY KEY (pid)
);

-- Drop table

DROP TABLE IF EXISTS public.audit_default_group;

CREATE TABLE public.audit_default_group (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	"comments" text NULL,
	completed bool NULL,
	expired bool NULL,
	id int4 NULL,
	module_id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	"version" varchar(255) NULL,
	CONSTRAINT audit_default_group_pkey PRIMARY KEY (pid)
);

-- Drop table

DROP TABLE IF EXISTS public.audit_default_item;

CREATE TABLE public.audit_default_item (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	completed bool NULL,
	group_id int4 NULL,
	id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	"owner" varchar(255) NULL,
	system_item bool NULL,
	CONSTRAINT audit_default_item_pkey PRIMARY KEY (pid)
);

-- Drop table

DROP TABLE IF EXISTS public.audit_default_link;

CREATE TABLE public.audit_default_link (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	group_id int4 NULL,
	id int4 NULL,
	link varchar(255) NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	CONSTRAINT audit_default_link_pkey PRIMARY KEY (pid)
);

-- Drop table

DROP TABLE IF EXISTS public.audit_default_module;

CREATE TABLE public.audit_default_module (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	completed bool NULL,
	id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	summary_id int4 NULL,
	CONSTRAINT audit_default_module_pkey PRIMARY KEY (pid)
);

-- Drop table

DROP TABLE IF EXISTS public.audit_default_summary;

CREATE TABLE public.audit_default_summary (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	config_id int4 NULL,
	except_progress numeric(19,2) NULL,
	id int4 NULL,
	"level" varchar(255) NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	progress numeric(19,2) NULL,
	CONSTRAINT audit_default_summary_pkey PRIMARY KEY (pid)
);



-- INSERT DATA
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 90.00, 'C', '制度管理', 1, 0.00);
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 60.00, 'B', '执行管理', 2, 0.00);
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 30.00, 'A', '改进管理', 3, 0.00);



INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备部门管理', 1, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医用设备管理', 2, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '设备安全管理', 3, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '培训和考核管理', 4, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备使用保障', 5, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备协作管理', 6, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备部门管理', 1, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医用设备管理', 2, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '设备安全管理', 3, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '培训和考核管理', 4, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备使用保障', 5, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备协作管理', 6, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备部门管理', 1, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医用设备管理', 2, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '设备安全管理', 3, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '培训和考核管理', 4, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备使用保障', 5, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '医学装备协作管理', 6, 3);



INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '医学装备管理总体系', 1, '6.10.1.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '管理人员配备', 2, '6.10.1.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '工作流程和岗位职责', 3, '6.10.1.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '常规与大型医学装备配置方案', 4, '6.10.1.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '医学装备生命周期管理制度', 5, '6.10.1.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '使用科室人员配备', 6, '6.10.1.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '购置论证相关制度与决策程序', 1, '6.10.2.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备清单', 2, '6.10.2.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备档案管理', 3, '6.10.2.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备使用评价制度', 4, '6.10.2.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '安全风险管控制度', 1, '6.10.3.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量管理制度', 2, '6.10.3.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '临床使用安全管控制度', 3, '6.10.3.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '相关人员培训制度', 4, '6.10.3.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射与放疗安全', 5, '6.10.3.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '机房警示标识', 6, '6.10.3.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '设备安全人员须知', 7, '6.10.3.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '特殊和放射装备制度', 8, '6.10.3.3.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '特殊和放射装备人员管理', 9, '6.10.3.3.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射诊断设备制度', 10, '6.10.3.4.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射使用管理制度', 11, '6.10.3.4.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(22, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射类安全指标', 12, '6.10.3.4.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(23, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备管理制度', 13, '6.10.3.5.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(24, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备日常管理', 14, '6.10.3.5.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(25, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量检测合格标志', 15, '6.10.3.5.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(26, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备相关人员须知', 16, '6.10.3.5.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(27, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '设备使用人员培训制度', 1, '6.10.4.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(28, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '技术支持制度', 2, '6.10.4.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(29, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '意外事件处理培训制度', 3, '6.10.4.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(30, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '操作手册管理制度', 4, '6.10.4.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(31, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '设备操作考核制度', 5, '6.10.4.1.C.5');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(32, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备使用保障制度', 1, '6.10.5.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(33, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备养计检制度', 2, '6.10.5.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(34, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '故障维修分析', 3, '6.10.5.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(35, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备应急预案', 4, '6.10.5.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(36, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '急救支持类装备待机要求', 5, '6.10.5.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(37, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '设备故障紧急预案程序', 6, '6.10.5.3.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(38, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '急救支持类装备急调配程序', 7, '6.10.5.3.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(39, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医务人员须知', 8, '6.10.5.3.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(40, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '管理部门学习制度', 1, '6.10.7.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(41, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '管理部门定期总结分析制度', 2, '6.10.7.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(42, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '使用部门专人管理制度', 3, '6.10.7.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(43, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '使用部门日常管理工作', 4, '6.10.7.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(44, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '使用人员培训制度', 5, '6.10.7.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(45, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '制度实行自查', 1, '6.10.1.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(46, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '主管部门监察', 2, '6.10.1.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(47, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 8, '制度实行自查', 1, '6.10.2.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(48, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 8, '主管部门监察', 2, '6.10.2.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(49, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '安全自查和记录', 1, '6.10.3.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(50, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门监察', 2, '6.10.3.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(51, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '机房安全自查', 3, '6.10.3.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(52, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门监察', 4, '6.10.3.2.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(53, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '特殊和放射装备自查', 5, '6.10.3.3.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(54, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门监察', 6, '6.10.3.3.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(55, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '放射使用科室自查', 7, '6.10.3.4.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(56, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门对放射部门监察', 8, '6.10.3.4.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(57, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '计量设备自查', 9, '6.10.3.5.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(58, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门对计量设备的监察', 10, '6.10.3.5.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(59, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '使用科室操作自查', 1, '6.10.4.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(60, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门对使用操作的监察', 2, '6.10.4.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(61, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '故障维修分析', 1, '6.10.5.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(62, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '故障分析改进措施', 2, '6.10.5.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(63, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '急救支持类装备实时监管', 3, '6.10.5.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(64, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '急救支持类装备紧急程序演练', 4, '6.10.5.3.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(65, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '管理部门定期自查', 1, '6.10.7.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(66, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '管理部门定期巡检使用部门', 2, '6.10.7.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(67, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '使用部门使用自查', 3, '6.10.7.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(68, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '使用部门定期分析反馈', 4, '6.10.7.2.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(69, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 13, '医学装备发展规划落实', 1, '6.10.1.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(70, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 13, '医学装备配置方案和管理落实', 2, '6.10.1.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(71, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 14, '设备管理工作持续改进', 1, '6.10.2.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(72, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '安全管控持续改进', 1, '6.10.3.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(73, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '机房安全持续改进', 2, '6.10.3.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(74, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '特殊医学装备安全措施改进', 3, '6.10.3.3.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(75, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '放射诊疗设备安全措施改进', 4, '6.10.3.4.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(76, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '计量设备使用持续改进', 5, '6.10.3.5.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(77, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '计量器具100%合格', 6, '6.10.3.5.A.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(78, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 16, '操作和维护规范持续改进', 1, '6.10.4.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(79, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '设备使用保障措施持续改进', 1, '6.10.5.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(80, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '急救支持类装备完好率100%', 2, '6.10.5.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(81, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '急救支持类装备使用保障持续改进', 3, '6.10.5.3.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(82, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 18, '医学装备管理持续改进', 1, '6.10.7.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(83, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 18, '全院医学装备使用管理持续改进', 2, '6.10.7.2.A.1');



INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 1, '管理体系文件', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 1, '医学装备委员会成立文件和章程', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 1, '年度医学装备发展规划', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 2, '管理和技术人员架构图', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 2, '技术人员资格证书', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 2, '大型设备维修资质证书', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 3, '工作制度和岗位职责文件', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 4, '医学装备配置标准文件', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 5, '生命周期管理文件(论证、决策、购置、验收、使用、保养、维修、应用分析和更新、处置环节)', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 6, '使用人员架构图', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 6, '相关人员资格证书文件', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 6, '临床工程师资质证书', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 7, '50万元及以上的医学装备可行性论证', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 8, '固定资产台账清单', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 9, '医疗设备档案管理制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 9, '单价5万元以上医学装备目录', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 10, '使用评价制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 10, '大型设备分析报告', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 11, '风险管控制度上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 11, '不良事件报告制度', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 12, '计量设备管理制度上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(22, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 12, '计量设备检查报告', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(23, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 13, '临床操作手册上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(24, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 13, '设备保养&维修报告上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(25, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 14, '培训记录上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(26, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 14, '考核记录上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(27, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 15, '放疗与放射设备清单', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(28, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 15, '辐射安全许可证', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(29, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 15, '性能检测报告', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(30, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 16, '机房警示标识规范与要求', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(31, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 16, '现场照片示例', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(32, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 17, '官方通知截图', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(33, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 17, '培训记录', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(34, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 45, '年度工作总结', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(35, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 45, '质控分数上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(36, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 46, '医疗质量管理委员会会议纪要', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(37, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 46, '人员考核绩效上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(38, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 47, '设计自查表上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(39, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 48, '财务报表审计', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(40, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 49, '临床安全记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(41, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 49, '不良事件记录', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(42, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 50, '主管部门自查表', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(43, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 69, '设备配置现状总结文件上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(44, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 70, '十三五规划上传', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(45, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 70, '实际案例上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(46, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 71, '委员会PPT材料', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(47, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 71, '实际案例上传', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(48, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 72, '不良事件减少比例', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(49, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 72, '维修率', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(50, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 72, '开机率', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(51, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 73, '医疗器械临床使用安全管理改进措施', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(52, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 73, '可疑医疗器械不良事件上报案例总结分析报告', 2, '', true);



INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf99a26c470ebbc7d7c990', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '1', '资产管理总制度及廉政制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99a26c470ebbc7d7c990');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf99d66c470ebbc7d7c992', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '9', '医疗装备维护与保养制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99d66c470ebbc7d7c992');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf99e56c470ebbc7d7c994', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '21', '计量装备管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99e56c470ebbc7d7c994');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf99f7827fb9f2fa202506', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99f7827fb9f2fa202506');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf9a07827fb9f2fa202508', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '医疗装备应急程序.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a07827fb9f2fa202508');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '5ecf9a136c470ebbc7d7c996', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '7', '岗位职责以及各种流程图.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a136c470ebbc7d7c996');
