-- 2020.06.11 YHAAO-7278 质控读取新增默认配置数据
-- Drop table

DROP TABLE IF EXISTS public.audit_config;
--
-- CREATE TABLE public.audit_config (
-- 	id serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	active bool NULL,
-- 	CONSTRAINT audit_config_pkey PRIMARY KEY (id)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_file;
--
-- CREATE TABLE public.audit_file (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	deleted bool NULL,
-- 	expired bool NULL,
-- 	file_id varchar(255) NULL,
-- 	file_type varchar(255) NULL,
-- 	id int4 NULL,
-- 	item_id int4 NULL,
-- 	"name" varchar(255) NULL,
-- 	system_file bool NULL,
-- 	"template" bool NULL,
-- 	upload_by varchar(255) NULL,
-- 	upload_time timestamp NULL,
-- 	url varchar(255) NULL,
-- 	CONSTRAINT audit_file_pkey PRIMARY KEY (pid)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_group;
--
-- CREATE TABLE public.audit_group (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	"comments" text NULL,
-- 	completed bool NULL,
-- 	expired bool NULL,
-- 	id int4 NULL,
-- 	module_id int4 NULL,
-- 	"name" varchar(255) NULL,
-- 	order_num int4 NULL,
-- 	"version" varchar(255) NULL,
-- 	CONSTRAINT audit_group_pkey PRIMARY KEY (pid)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_item;
--
-- CREATE TABLE public.audit_item (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	completed bool NULL,
-- 	group_id int4 NULL,
-- 	id int4 NULL,
-- 	"name" varchar(255) NULL,
-- 	order_num int4 NULL,
-- 	"owner" varchar(255) NULL,
-- 	system_item bool NULL,
-- 	CONSTRAINT audit_item_pkey PRIMARY KEY (pid)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_link;
--
-- CREATE TABLE public.audit_link (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	group_id int4 NULL,
-- 	id int4 NULL,
-- 	link varchar(255) NULL,
-- 	"name" varchar(255) NULL,
-- 	order_num int4 NULL,
-- 	CONSTRAINT audit_link_pkey PRIMARY KEY (pid)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_module;
--
-- CREATE TABLE public.audit_module (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	completed bool NULL,
-- 	id int4 NULL,
-- 	"name" varchar(255) NULL,
-- 	order_num int4 NULL,
-- 	summary_id int4 NULL,
-- 	CONSTRAINT audit_module_pkey PRIMARY KEY (pid)
-- );
--
-- -- Drop table
--
DROP TABLE IF EXISTS public.audit_summary;
--
-- CREATE TABLE public.audit_summary (
-- 	pid serial NOT NULL,
-- 	created_by varchar(50) NULL,
-- 	created_date timestamp NULL,
-- 	hospital_uid bpchar(32) NULL,
-- 	institution_uid bpchar(32) NULL,
-- 	last_modified_by varchar(50) NULL,
-- 	last_modified_date timestamp NULL,
-- 	site_uid bpchar(32) NULL,
-- 	tenant_uid bpchar(32) NULL,
-- 	config_id int4 NULL,
-- 	except_progress numeric(19,2) NULL,
-- 	id int4 NULL,
-- 	"level" varchar(255) NULL,
-- 	"name" varchar(255) NULL,
-- 	order_num int4 NULL,
-- 	progress numeric(19,2) NULL,
-- 	CONSTRAINT audit_summary_pkey PRIMARY KEY (pid)
-- );


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
	deleted bool NULL,
	expired bool NULL,
	file_id varchar(255) NULL,
	file_type varchar(255) NULL,
	id int4 NULL,
	item_id int4 NULL,
	"name" varchar(255) NULL,
	system_file bool NULL,
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

INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(53, 'system', '2020-06-16 16:40:24.771', '', '', 'system', '2020-06-16 16:40:24.771', '', '', true, 23, '计量设备管理制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(54, 'system', '2020-06-16 16:41:48.786', '', '', 'system', '2020-06-16 16:41:48.786', '', '', false, 26, '计量器具强制检定制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(55, 'system', '2020-06-16 16:42:28.053', '', '', 'system', '2020-06-16 16:42:28.053', '', '', false, 24, '计量器具强制检定目录及检定周期表', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(56, 'system', '2020-06-16 16:42:49.531', '', '', 'system', '2020-06-16 16:42:49.531', '', '', false, 24, '计量设备检测流程', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(57, 'system', '2020-06-16 16:45:56.591', '', '', 'system', '2020-06-16 16:45:56.591', '', '', false, 31, '常用考试考核模板', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(58, 'system', '2020-06-16 16:48:35.574', '', '', 'system', '2020-06-16 16:48:35.574', '', '', false, 29, '医疗器械临床使用安全控制与风险管理应急预案', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(59, 'system', '2020-06-16 16:49:32.68', '', '', 'system', '2020-06-16 16:49:32.68', '', '', false, 33, '医疗设备三级保养体系', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(60, 'system', '2020-06-16 16:51:56.176', '', '', 'system', '2020-06-16 16:51:56.176', '', '', true, 35, '医疗设备应急预案', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(61, 'system', '2020-06-16 16:52:28.545', '', '', 'system', '2020-06-16 16:52:28.545', '', '', true, 35, '医疗设备应急管理程序', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(62, 'system', '2020-06-16 16:53:51.104', '', '', 'system', '2020-06-16 16:53:51.104', '', '', false, 38, '医疗设备应急替代工作流程', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(63, 'system', '2020-06-17 16:26:23.534', '', '', 'system', '2020-06-17 16:26:23.534', '', '', false, 27, '医疗器械操作培训和考核制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(64, 'system', '2020-06-17 16:26:34.427', '', '', 'system', '2020-06-17 16:26:34.427', '', '', false, 27, '设备年度培训计划表（含使用科室计划）', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(65, 'system', '2020-06-17 16:26:46.606', '', '', 'system', '2020-06-17 16:26:46.606', '', '', false, 27, '抢救物品培训考核及计划（结合护理部）', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(66, 'system', '2020-06-17 16:27:00.101', '', '', 'system', '2020-06-17 16:27:00.101', '', '', false, 27, '抢救设备及物品清单', 4, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(67, 'system', '2020-06-17 16:27:24.558', '', '', 'system', '2020-06-17 16:27:24.558', '', '', false, 29, '培训课件含意外情况处理预案及措施', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(68, 'system', '2020-06-17 16:27:40.056', '', '', 'system', '2020-06-17 16:27:40.056', '', '', false, 30, '操作手册管理制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(69, 'system', '2020-06-17 16:27:51.169', '', '', 'system', '2020-06-17 16:27:51.169', '', '', false, 30, '操作手册目录清单（5万元及急救设备）', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(70, 'system', '2020-06-17 16:28:06.244', '', '', 'system', '2020-06-17 16:28:06.244', '', '', false, 31, '医学装备培训考核表', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(71, 'system', '2020-06-17 16:28:38.37', '', '', 'system', '2020-06-17 16:28:38.37', '', '', false, 59, '使用科室自查表', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(72, 'system', '2020-06-17 16:28:50.846', '', '', 'system', '2020-06-17 16:28:50.846', '', '', false, 60, '装备管理部门督查表', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(73, 'system', '2020-06-17 16:29:00.702', '', '', 'system', '2020-06-17 16:29:00.702', '', '', false, 60, '装备管理部门对全院培训情况分析、反馈', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(74, 'system', '2020-06-17 16:29:28.324', '', '', 'system', '2020-06-17 16:29:28.324', '', '', false, 78, '数据或实例显示,持续改进有成效', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(75, 'system', '2020-06-17 16:45:18.008', '', '', 'system', '2020-06-17 16:45:18.008', '', '', false, 33, '医学装备管理部门有对医学装备保养、维修制度', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(76, 'system', '2020-06-17 16:59:30.608', '', '', 'system', '2020-06-17 16:59:30.608', '', '', false, 33, '使用登记本', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(77, 'system', '2020-06-17 17:08:08.76', '', '', 'system', '2020-06-17 17:08:08.76', '', '', false, 33, '使用培训包含日常维护保养内容', 4, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(78, 'system', '2020-06-17 17:08:22.447', '', '', 'system', '2020-06-17 17:08:22.447', '', '', false, 34, '维护保养记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(79, 'system', '2020-06-17 17:08:47.327', '', '', 'system', '2020-06-17 17:08:47.327', '', '', false, 61, '维修情况分析报告', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(80, 'system', '2020-06-17 17:08:57.061', '', '', 'system', '2020-06-17 17:08:57.061', '', '', false, 62, '委员会PPT材料及会议纪要含上述内容', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(81, 'system', '2020-06-17 17:09:15.278', '', '', 'system', '2020-06-17 17:09:15.278', '', '', false, 79, '数据或实例显示,持续改进有成效', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(82, 'system', '2020-06-17 17:17:53.849', '', '', 'system', '2020-06-17 17:17:53.849', '', '', false, 35, '急救类,生命支持类医学装备应急预案', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(83, 'system', '2020-06-17 17:18:06.414', '', '', 'system', '2020-06-17 17:18:06.414', '', '', false, 35, '急救类,生命支持类医学装备清单', 4, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(84, 'system', '2020-06-17 17:18:20.953', '', '', 'system', '2020-06-17 17:18:20.953', '', '', false, 36, '急救生命支持类设备记录(台账,日检记录,交接班记录,保障方案)(各科室保存)', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(85, 'system', '2020-06-17 17:42:19.552', '', '', 'system', '2020-06-17 17:42:19.552', '', '', false, 80, '用实例或数据说明,对急救类,生命支持类装备管理的改进措施落实,效果分析有效', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(86, 'system', '2020-06-17 17:43:00.217', '', '', 'system', '2020-06-17 17:43:00.217', '', '', false, 37, '装备故障时有紧急替代流程', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(87, 'system', '2020-06-17 17:43:12.171', '', '', 'system', '2020-06-17 17:43:12.171', '', '', false, 38, '医学装备应急调配制度', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(88, 'system', '2020-06-17 17:43:23.765', '', '', 'system', '2020-06-17 17:43:23.765', '', '', false, 39, '应知应会内容,有培训内容和考试记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(89, 'system', '2020-06-17 17:43:46.879', '', '', 'system', '2020-06-17 17:43:46.879', '', '', false, 64, '医学装备部门装备应急调配演练和总结记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(90, 'system', '2020-06-17 17:44:29.455', '', '', 'system', '2020-06-17 17:44:29.455', '', '', false, 81, '医学装备部门装备针对应急调配演练和监管中存在的问题制定整改措施和落实记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(91, 'system', '2020-06-17 17:44:39.714', '', '', 'system', '2020-06-17 17:44:39.714', '', '', false, 81, '应急演练及监管问题整改报告(PDCA整改报告)；全院保障装备应急调配机制实施总结报告', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(92, 'system', '2020-06-17 17:44:58.535', '', '', 'system', '2020-06-17 17:44:58.535', '', '', false, 40, '医工培训管理制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(93, 'system', '2020-06-17 17:45:05.83', '', '', 'system', '2020-06-17 17:45:05.83', '', '', false, 40, '医工档案（通知、资料、学分证等）', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(94, 'system', '2020-06-17 17:45:11.499', '', '', 'system', '2020-06-17 17:45:11.499', '', '', false, 40, '医工内部培训计划表', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(95, 'system', '2020-06-17 17:45:19.27', '', '', 'system', '2020-06-17 17:45:19.27', '', '', false, 40, '培训课件、记录、考核资料', 4, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(96, 'system', '2020-06-17 17:45:34.483', '', '', 'system', '2020-06-17 17:45:34.483', '', '', false, 41, '全院医学装备发展规划、采购计划的完成、管理和使用情况分析', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(97, 'system', '2020-06-17 17:45:42.379', '', '', 'system', '2020-06-17 17:45:42.379', '', '', false, 41, '医学装备维修、报废、巡检等方面的问题、分析、总结报告', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(98, 'system', '2020-06-17 17:46:00.059', '', '', 'system', '2020-06-17 17:46:00.059', '', '', false, 65, '1.医学装备管理部门对培训、采购、维修等业务的自查表（一张表）', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(99, 'system', '2020-06-17 17:46:13.632', '', '', 'system', '2020-06-17 17:46:13.632', '', '', false, 66, '巡检记录', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(100, 'system', '2020-06-17 17:46:24.73', '', '', 'system', '2020-06-17 17:46:24.73', '', '', false, 66, '巡检总结分析报告', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(101, 'system', '2020-06-17 17:46:37.874', '', '', 'system', '2020-06-17 17:46:37.874', '', '', false, 82, '设备相关满意度汇总（数据或实例显示,持续改进有成效）', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(102, 'system', '2020-06-17 17:47:00.212', '', '', 'system', '2020-06-17 17:47:00.212', '', '', false, 42, '建立科室自查表，医学装备管理部门定期督查执行情况（督查表）', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(103, 'system', '2020-06-17 17:47:06.74', '', '', 'system', '2020-06-17 17:47:06.74', '', '', false, 42, '兼职管理人员名单', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(104, 'system', '2020-06-17 17:47:15.118', '', '', 'system', '2020-06-17 17:47:15.118', '', '', false, 43, '日常使用管理制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(105, 'system', '2020-06-17 17:47:23.507', '', '', 'system', '2020-06-17 17:47:23.507', '', '', false, 43, '应急管理制度', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(106, 'system', '2020-06-17 17:48:36.536', '', '', 'system', '2020-06-17 17:48:36.536', '', '', false, 44, '医疗装备的使用培训考核制度', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(107, 'system', '2020-06-17 17:48:42.532', '', '', 'system', '2020-06-17 17:48:42.532', '', '', false, 44, '培训考核记录', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(108, 'system', '2020-06-17 17:49:23.728', '', '', 'system', '2020-06-17 17:49:23.728', '', '', false, 67, '科室自查表包含维护保养内容', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(109, 'system', '2020-06-17 17:49:32.291', '', '', 'system', '2020-06-17 17:49:32.291', '', '', false, 68, '医学装备管理部门督查表（一张表）', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(110, 'system', '2020-06-17 17:49:42.608', '', '', 'system', '2020-06-17 17:49:42.608', '', '', false, 83, '数据或实例显示,持续改进有成效', 1, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(111, 'system', '2020-06-17 17:55:35.21', '', '', 'system', '2020-06-17 17:55:35.21', '', '', false, 80, '数据或实例显示,持续改进有成效', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(112, 'system', '2020-06-18 17:11:59.897', '', '', 'system', '2020-06-18 17:11:59.897', '', '', false, 71, '全院医疗设备管理持续改进分析', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(113, 'system', '2020-06-18 17:48:25.847', '', '', 'system', '2020-06-18 17:48:25.847', '', '', false, 9, '医学装备分类管理目录', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(114, 'system', '2020-06-18 17:53:16.962', '', '', 'system', '2020-06-18 17:53:16.962', '', '', false, 24, '计量管理模板', 3, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(115, 'system', '2020-06-18 18:04:21.277', '', '', 'system', '2020-06-18 18:04:21.277', '', '', false, 32, '医疗器械使用质量监督管理办法', 2, '', true);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
VALUES(116, 'system', '2020-06-18 18:07:14.093', '', '', 'system', '2020-06-18 18:07:14.093', '', '', false, 32, '医学装备部门工作制度中关于保障装备使用的管理制度与规范', 3, '', true);


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ee88275e3a5402f9038046a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '1', '资产管理处制度总则.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88275e3a5402f9038046a');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ecf99d66c470ebbc7d7c992', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '9', '医疗装备维护与保养制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99d66c470ebbc7d7c992');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ecf99e56c470ebbc7d7c994', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '21', '计量装备管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99e56c470ebbc7d7c994');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ecf99f7827fb9f2fa202506', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99f7827fb9f2fa202506');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ecf9a07827fb9f2fa202508', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '医疗装备应急程序.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a07827fb9f2fa202508');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', true, false, false, '5ecf9a136c470ebbc7d7c996', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '7', '岗位职责以及各种流程图.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a136c470ebbc7d7c996');

INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(7, 'system', '2020-06-16 16:31:48.553', '', '', 'system', '2020-06-16 16:31:48.553', '', '', true, false, false, '5ee88374e3a5402f9038046e', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '4', '岗位职责.docx', 'system', '2020-06-16 16:31:48.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88374e3a5402f9038046e');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(8, 'system', '2020-06-16 16:34:08.544', '', '', 'system', '2020-06-16 16:34:08.544', '', '', true, false, false, '5ee88400e3a5402f90380472', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '13', '医疗设备论证流程.docx', 'system', '2020-06-16 16:34:08.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88400e3a5402f90380472');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(9, 'system', '2020-06-16 16:35:28.81', '', '', 'system', '2020-06-16 16:35:28.81', '', '', true, false, false, '5ee88450e3a5402f90380474', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '14', '固定资产台账清单.xlsx', 'system', '2020-06-16 16:35:28.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88450e3a5402f90380474');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(10, 'system', '2020-06-16 16:36:18.749', '', '', 'system', '2020-06-16 16:36:18.749', '', '', true, false, false, '5ee88482e3a5402f90380478', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '15', '医疗器械临床使用安全控制与风险管理流程.docx', 'system', '2020-06-16 16:36:18.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88482e3a5402f90380478');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(11, 'system', '2020-06-16 16:38:28.963', '', '', 'system', '2020-06-16 16:38:28.963', '', '', true, false, false, '5ee88504e3a5402f90380480', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '24', '医疗设备保养、校验制度.docx', 'system', '2020-06-16 16:38:28.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88504e3a5402f90380480');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(12, 'system', '2020-06-16 16:39:16.881', '', '', 'system', '2020-06-16 16:39:16.881', '', '', true, false, false, '5ee88533e3a5402f90380482', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '25', '医学装备继续教育考核制度.docx', 'system', '2020-06-16 16:39:15.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88533e3a5402f90380482');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(13, 'system', '2020-06-16 16:40:42.159', '', '', 'system', '2020-06-16 16:40:42.159', '', '', true, false, false, '5ee8858ae3a5402f90380484', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '53', '计量器具管理制度.docx', 'system', '2020-06-16 16:40:42.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee8858ae3a5402f90380484');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(14, 'system', '2020-06-16 16:42:01.917', '', '', 'system', '2020-06-16 16:42:01.917', '', '', true, false, false, '5ee885d9e3a5402f90380486', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '54', '计量器具强制检定制度.docx', 'system', '2020-06-16 16:42:01.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee885d9e3a5402f90380486');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(15, 'system', '2020-06-16 16:42:34.904', '', '', 'system', '2020-06-16 16:42:34.904', '', '', true, false, false, '5ee885fae3a5402f90380488', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '55', '计量器具强制检定目录及检定周期表.docx', 'system', '2020-06-16 16:42:34.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee885fae3a5402f90380488');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(16, 'system', '2020-06-16 16:42:57.997', '', '', 'system', '2020-06-16 16:42:57.997', '', '', true, false, false, '5ee88611e3a5402f90380490', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '56', '计量设备检测流程.docx', 'system', '2020-06-16 16:42:57.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88611e3a5402f90380490');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(17, 'system', '2020-06-16 16:46:38.835', '', '', 'system', '2020-06-16 16:46:38.835', '', '', true, false, false, '5ee886eee3a5402f9038049b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '57', '考试试卷.docx', 'system', '2020-06-16 16:46:38.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee886eee3a5402f9038049b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(18, 'system', '2020-06-16 16:48:55.016', '', '', 'system', '2020-06-16 16:48:55.016', '', '', true, false, false, '5ee88776e3a5402f9038049d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '58', '医疗器械临床使用安全控制与风险管理应急预案.docx', 'system', '2020-06-16 16:48:54.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88776e3a5402f9038049d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(19, 'system', '2020-06-16 16:49:53.67', '', '', 'system', '2020-06-16 16:49:53.67', '', '', true, false, false, '5ee887b1e3a5402f9038049f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '59', '医疗设备三级保养体系.docx', 'system', '2020-06-16 16:49:53.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee887b1e3a5402f9038049f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(20, 'system', '2020-06-16 16:52:06.332', '', '', 'system', '2020-06-16 16:52:06.332', '', '', true, false, false, '5ee88836e3a5402f903804a5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '60', '医疗设备应急预案.docx', 'system', '2020-06-16 16:52:06.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88836e3a5402f903804a5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(21, 'system', '2020-06-16 16:52:40.274', '', '', 'system', '2020-06-16 16:52:40.274', '', '', true, false, false, '5ee88858e3a5402f903804a7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '61', '医疗设备应急管理程序.docx', 'system', '2020-06-16 16:52:40.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88858e3a5402f903804a7');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(22, 'system', '2020-06-16 16:54:00.546', '', '', 'system', '2020-06-16 16:54:00.546', '', '', true, false, false, '5ee888a8e3a5402f903804a9', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '62', '医疗设备应急替代工作流程.docx', 'system', '2020-06-16 16:54:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee888a8e3a5402f903804a9');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(23, 'system', '2020-06-18 17:09:24.278', '', '', 'system', '2020-06-18 17:09:24.278', '', '', true, false, false, '5eeb2f43e3a5402f90381911', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '75', 'APM_通用保养模板（示例).xlsx', 'system', '2020-06-18 17:09:23.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb2f43e3a5402f90381911');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(24, 'system', '2020-06-18 17:12:21.971', '', '', 'system', '2020-06-18 17:12:21.971', '', '', true, false, false, '5eeb2ff5e3a5402f90381916', 'application/pdf', true, '112', 'APM使用分析报告-以华东某医院为例.pdf', 'system', '2020-06-18 17:12:21.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb2ff5e3a5402f90381916');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(25, 'system', '2020-06-18 17:48:38.002', '', '', 'system', '2020-06-18 17:48:38.002', '', '', true, false, false, '5eeb3875e3a5402f90381933', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '113', 'APM_设备分类标准汇总.xlsx', 'system', '2020-06-18 17:48:37.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb3875e3a5402f90381933');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(26, 'system', '2020-06-18 17:53:34.953', '', '', 'system', '2020-06-18 17:53:34.953', '', '', true, false, false, '5eeb399ee3a5402f9038193b', 'application/vnd.ms-excel', true, '114', 'APM_计量管理模板.xls', 'system', '2020-06-18 17:53:34.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb399ee3a5402f9038193b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(27, 'system', '2020-06-18 18:06:30.61', '', '', 'system', '2020-06-18 18:06:30.61', '', '', true, false, false, '5eeb3ca6e3a5402f90381945', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '115', '医疗器械使用质量监督管理办法.docx', 'system', '2020-06-18 18:06:30.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb3ca6e3a5402f90381945');


-- INSERT INTO public.audit_config
-- (created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, active)
-- VALUES('test.1111psls1', '2020-06-04 21:38:59.081', '', '', 'test.1111psls1', '2020-06-04 21:38:59.081', '', '057fdd13412e46df891762c01f526846', true);
-- INSERT INTO public.audit_config
-- (created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, active)
-- VALUES('huangjianzhen', '2020-06-04 21:47:48.743', '', '', 'huangjianzhen', '2020-06-04 21:47:48.743', '', '2a773154623311e7aeb25254001a1c14', true);
-- INSERT INTO public.audit_config
-- (created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, active)
-- VALUES('Demonstration', '2020-06-24 19:09:27.836', '', '', 'Demonstration', '2020-06-24 19:09:27.836', '', '2a773b22623311e7aeb25254001a1c14', true);
--
--
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(1, 'test.1111psls1', '2020-06-04 21:38:59.284', '', '', 'test.1111psls1', '2020-06-04 21:38:59.284', '', '057fdd13412e46df891762c01f526846', 1, 90.00, 'C', '制度管理', 1, 4.55);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(2, 'test.1111psls1', '2020-06-04 21:38:59.388', '', '', 'test.1111psls1', '2020-06-04 21:38:59.388', '', '057fdd13412e46df891762c01f526846', 1, 60.00, 'B', '执行管理', 2, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(3, 'test.1111psls1', '2020-06-04 21:38:59.42', '', '', 'test.1111psls1', '2020-06-04 21:38:59.42', '', '057fdd13412e46df891762c01f526846', 1, 30.00, 'A', '改进管理', 3, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(1, 'huangjianzhen', '2020-06-04 21:47:48.885', '', '', 'huangjianzhen', '2020-06-04 21:47:48.885', '', '2a773154623311e7aeb25254001a1c14', 2, 90.00, 'C', '制度管理', 1, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(2, 'huangjianzhen', '2020-06-04 21:47:48.948', '', '', 'huangjianzhen', '2020-06-04 21:47:48.948', '', '2a773154623311e7aeb25254001a1c14', 2, 60.00, 'B', '执行管理', 2, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(3, 'huangjianzhen', '2020-06-04 21:47:48.975', '', '', 'huangjianzhen', '2020-06-04 21:47:48.975', '', '2a773154623311e7aeb25254001a1c14', 2, 30.00, 'A', '改进管理', 3, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(1, 'Demonstration', '2020-06-24 19:09:28.102', '', '', 'Demonstration', '2020-06-24 19:09:28.102', '', '2a773b22623311e7aeb25254001a1c14', 3, 90.00, 'C', '制度管理', 1, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(2, 'Demonstration', '2020-06-24 19:09:28.206', '', '', 'Demonstration', '2020-06-24 19:09:28.206', '', '2a773b22623311e7aeb25254001a1c14', 3, 60.00, 'B', '执行管理', 2, 0.00);
-- INSERT INTO public.audit_summary
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
-- VALUES(3, 'Demonstration', '2020-06-24 19:09:28.234', '', '', 'Demonstration', '2020-06-24 19:09:28.234', '', '2a773b22623311e7aeb25254001a1c14', 3, 30.00, 'A', '改进管理', 3, 0.00);
--
--
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(1, 'test.1111psls1', '2020-06-04 21:38:59.291', '', '', 'test.1111psls1', '2020-06-04 21:38:59.291', '', '057fdd13412e46df891762c01f526846', false, '医学装备部门管理', 1, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(2, 'test.1111psls1', '2020-06-04 21:38:59.33', '', '', 'test.1111psls1', '2020-06-04 21:38:59.33', '', '057fdd13412e46df891762c01f526846', false, '医用设备管理', 2, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(3, 'test.1111psls1', '2020-06-04 21:38:59.34', '', '', 'test.1111psls1', '2020-06-04 21:38:59.34', '', '057fdd13412e46df891762c01f526846', false, '设备安全管理', 3, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(4, 'test.1111psls1', '2020-06-04 21:38:59.37', '', '', 'test.1111psls1', '2020-06-04 21:38:59.37', '', '057fdd13412e46df891762c01f526846', false, '培训和考核管理', 4, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(5, 'test.1111psls1', '2020-06-04 21:38:59.375', '', '', 'test.1111psls1', '2020-06-04 21:38:59.375', '', '057fdd13412e46df891762c01f526846', false, '医学装备使用保障', 5, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(6, 'test.1111psls1', '2020-06-04 21:38:59.383', '', '', 'test.1111psls1', '2020-06-04 21:38:59.383', '', '057fdd13412e46df891762c01f526846', false, '医学装备协作管理', 6, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(7, 'test.1111psls1', '2020-06-04 21:38:59.389', '', '', 'test.1111psls1', '2020-06-04 21:38:59.389', '', '057fdd13412e46df891762c01f526846', false, '医学装备部门管理', 1, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(8, 'test.1111psls1', '2020-06-04 21:38:59.395', '', '', 'test.1111psls1', '2020-06-04 21:38:59.395', '', '057fdd13412e46df891762c01f526846', false, '医用设备管理', 2, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(9, 'test.1111psls1', '2020-06-04 21:38:59.398', '', '', 'test.1111psls1', '2020-06-04 21:38:59.398', '', '057fdd13412e46df891762c01f526846', false, '设备安全管理', 3, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(10, 'test.1111psls1', '2020-06-04 21:38:59.409', '', '', 'test.1111psls1', '2020-06-04 21:38:59.409', '', '057fdd13412e46df891762c01f526846', false, '培训和考核管理', 4, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(11, 'test.1111psls1', '2020-06-04 21:38:59.411', '', '', 'test.1111psls1', '2020-06-04 21:38:59.411', '', '057fdd13412e46df891762c01f526846', false, '医学装备使用保障', 5, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(12, 'test.1111psls1', '2020-06-04 21:38:59.415', '', '', 'test.1111psls1', '2020-06-04 21:38:59.415', '', '057fdd13412e46df891762c01f526846', false, '医学装备协作管理', 6, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(13, 'test.1111psls1', '2020-06-04 21:38:59.42', '', '', 'test.1111psls1', '2020-06-04 21:38:59.42', '', '057fdd13412e46df891762c01f526846', false, '医学装备部门管理', 1, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(14, 'test.1111psls1', '2020-06-04 21:38:59.425', '', '', 'test.1111psls1', '2020-06-04 21:38:59.425', '', '057fdd13412e46df891762c01f526846', false, '医用设备管理', 2, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(15, 'test.1111psls1', '2020-06-04 21:38:59.428', '', '', 'test.1111psls1', '2020-06-04 21:38:59.428', '', '057fdd13412e46df891762c01f526846', false, '设备安全管理', 3, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(16, 'test.1111psls1', '2020-06-04 21:38:59.437', '', '', 'test.1111psls1', '2020-06-04 21:38:59.437', '', '057fdd13412e46df891762c01f526846', false, '培训和考核管理', 4, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(17, 'test.1111psls1', '2020-06-04 21:38:59.438', '', '', 'test.1111psls1', '2020-06-04 21:38:59.438', '', '057fdd13412e46df891762c01f526846', false, '医学装备使用保障', 5, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(18, 'test.1111psls1', '2020-06-04 21:38:59.441', '', '', 'test.1111psls1', '2020-06-04 21:38:59.441', '', '057fdd13412e46df891762c01f526846', false, '医学装备协作管理', 6, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(1, 'huangjianzhen', '2020-06-04 21:47:48.886', '', '', 'huangjianzhen', '2020-06-04 21:47:48.886', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(2, 'huangjianzhen', '2020-06-04 21:47:48.902', '', '', 'huangjianzhen', '2020-06-04 21:47:48.902', '', '2a773154623311e7aeb25254001a1c14', false, '医用设备管理', 2, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(3, 'huangjianzhen', '2020-06-04 21:47:48.91', '', '', 'huangjianzhen', '2020-06-04 21:47:48.91', '', '2a773154623311e7aeb25254001a1c14', false, '设备安全管理', 3, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(4, 'huangjianzhen', '2020-06-04 21:47:48.933', '', '', 'huangjianzhen', '2020-06-04 21:47:48.933', '', '2a773154623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(5, 'huangjianzhen', '2020-06-04 21:47:48.937', '', '', 'huangjianzhen', '2020-06-04 21:47:48.937', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(6, 'huangjianzhen', '2020-06-04 21:47:48.943', '', '', 'huangjianzhen', '2020-06-04 21:47:48.943', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(7, 'huangjianzhen', '2020-06-04 21:47:48.949', '', '', 'huangjianzhen', '2020-06-04 21:47:48.949', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(8, 'huangjianzhen', '2020-06-04 21:47:48.955', '', '', 'huangjianzhen', '2020-06-04 21:47:48.955', '', '2a773154623311e7aeb25254001a1c14', false, '医用设备管理', 2, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(9, 'huangjianzhen', '2020-06-04 21:47:48.958', '', '', 'huangjianzhen', '2020-06-04 21:47:48.958', '', '2a773154623311e7aeb25254001a1c14', false, '设备安全管理', 3, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(10, 'huangjianzhen', '2020-06-04 21:47:48.967', '', '', 'huangjianzhen', '2020-06-04 21:47:48.967', '', '2a773154623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(11, 'huangjianzhen', '2020-06-04 21:47:48.969', '', '', 'huangjianzhen', '2020-06-04 21:47:48.969', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(12, 'huangjianzhen', '2020-06-04 21:47:48.972', '', '', 'huangjianzhen', '2020-06-04 21:47:48.972', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(13, 'huangjianzhen', '2020-06-04 21:47:48.976', '', '', 'huangjianzhen', '2020-06-04 21:47:48.976', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(14, 'huangjianzhen', '2020-06-04 21:47:48.979', '', '', 'huangjianzhen', '2020-06-04 21:47:48.979', '', '2a773154623311e7aeb25254001a1c14', false, '医用设备管理', 2, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(15, 'huangjianzhen', '2020-06-04 21:47:48.982', '', '', 'huangjianzhen', '2020-06-04 21:47:48.982', '', '2a773154623311e7aeb25254001a1c14', false, '设备安全管理', 3, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(16, 'huangjianzhen', '2020-06-04 21:47:48.989', '', '', 'huangjianzhen', '2020-06-04 21:47:48.989', '', '2a773154623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(17, 'huangjianzhen', '2020-06-04 21:47:48.99', '', '', 'huangjianzhen', '2020-06-04 21:47:48.99', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(18, 'huangjianzhen', '2020-06-04 21:47:48.993', '', '', 'huangjianzhen', '2020-06-04 21:47:48.993', '', '2a773154623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(1, 'Demonstration', '2020-06-24 19:09:28.112', '', '', 'Demonstration', '2020-06-24 19:09:28.112', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(2, 'Demonstration', '2020-06-24 19:09:28.154', '', '', 'Demonstration', '2020-06-24 19:09:28.154', '', '2a773b22623311e7aeb25254001a1c14', false, '医用设备管理', 2, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(3, 'Demonstration', '2020-06-24 19:09:28.163', '', '', 'Demonstration', '2020-06-24 19:09:28.163', '', '2a773b22623311e7aeb25254001a1c14', false, '设备安全管理', 3, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(4, 'Demonstration', '2020-06-24 19:09:28.19', '', '', 'Demonstration', '2020-06-24 19:09:28.19', '', '2a773b22623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(5, 'Demonstration', '2020-06-24 19:09:28.195', '', '', 'Demonstration', '2020-06-24 19:09:28.195', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(6, 'Demonstration', '2020-06-24 19:09:28.201', '', '', 'Demonstration', '2020-06-24 19:09:28.201', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 1);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(7, 'Demonstration', '2020-06-24 19:09:28.206', '', '', 'Demonstration', '2020-06-24 19:09:28.206', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(8, 'Demonstration', '2020-06-24 19:09:28.211', '', '', 'Demonstration', '2020-06-24 19:09:28.211', '', '2a773b22623311e7aeb25254001a1c14', false, '医用设备管理', 2, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(9, 'Demonstration', '2020-06-24 19:09:28.215', '', '', 'Demonstration', '2020-06-24 19:09:28.215', '', '2a773b22623311e7aeb25254001a1c14', false, '设备安全管理', 3, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(10, 'Demonstration', '2020-06-24 19:09:28.225', '', '', 'Demonstration', '2020-06-24 19:09:28.225', '', '2a773b22623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(11, 'Demonstration', '2020-06-24 19:09:28.227', '', '', 'Demonstration', '2020-06-24 19:09:28.227', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(12, 'Demonstration', '2020-06-24 19:09:28.231', '', '', 'Demonstration', '2020-06-24 19:09:28.231', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 2);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(13, 'Demonstration', '2020-06-24 19:09:28.235', '', '', 'Demonstration', '2020-06-24 19:09:28.235', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备部门管理', 1, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(14, 'Demonstration', '2020-06-24 19:09:28.239', '', '', 'Demonstration', '2020-06-24 19:09:28.239', '', '2a773b22623311e7aeb25254001a1c14', false, '医用设备管理', 2, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(15, 'Demonstration', '2020-06-24 19:09:28.242', '', '', 'Demonstration', '2020-06-24 19:09:28.242', '', '2a773b22623311e7aeb25254001a1c14', false, '设备安全管理', 3, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(16, 'Demonstration', '2020-06-24 19:09:28.25', '', '', 'Demonstration', '2020-06-24 19:09:28.25', '', '2a773b22623311e7aeb25254001a1c14', false, '培训和考核管理', 4, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(17, 'Demonstration', '2020-06-24 19:09:28.252', '', '', 'Demonstration', '2020-06-24 19:09:28.252', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备使用保障', 5, 3);
-- INSERT INTO public.audit_module
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
-- VALUES(18, 'Demonstration', '2020-06-24 19:09:28.255', '', '', 'Demonstration', '2020-06-24 19:09:28.255', '', '2a773b22623311e7aeb25254001a1c14', false, '医学装备协作管理', 6, 3);
--
--
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(1, 'test.1111psls1', '2020-06-04 21:38:59.297', '', '', 'test.1111psls1', '2020-06-04 21:38:59.297', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '医学装备管理总体系', 1, '6.10.1.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(2, 'test.1111psls1', '2020-06-04 21:38:59.313', '', '', 'test.1111psls1', '2020-06-04 21:38:59.313', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '管理人员配备', 2, '6.10.1.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(3, 'test.1111psls1', '2020-06-04 21:38:59.318', '', '', 'test.1111psls1', '2020-06-04 21:38:59.318', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '工作流程和岗位职责', 3, '6.10.1.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(4, 'test.1111psls1', '2020-06-04 21:38:59.321', '', '', 'test.1111psls1', '2020-06-04 21:38:59.321', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '常规与大型医学装备配置方案', 4, '6.10.1.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(5, 'test.1111psls1', '2020-06-04 21:38:59.324', '', '', 'test.1111psls1', '2020-06-04 21:38:59.324', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '医学装备生命周期管理制度', 5, '6.10.1.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(6, 'test.1111psls1', '2020-06-04 21:38:59.326', '', '', 'test.1111psls1', '2020-06-04 21:38:59.326', '', '057fdd13412e46df891762c01f526846', '', false, false, 1, '使用科室人员配备', 6, '6.10.1.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(7, 'test.1111psls1', '2020-06-04 21:38:59.331', '', '', 'test.1111psls1', '2020-06-04 21:38:59.331', '', '057fdd13412e46df891762c01f526846', '', false, false, 2, '购置论证相关制度与决策程序', 1, '6.10.2.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(8, 'test.1111psls1', '2020-06-04 21:38:59.333', '', '', 'test.1111psls1', '2020-06-04 21:38:59.333', '', '057fdd13412e46df891762c01f526846', '', false, false, 2, '医学装备清单', 2, '6.10.2.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(9, 'test.1111psls1', '2020-06-04 21:38:59.334', '', '', 'test.1111psls1', '2020-06-04 21:38:59.334', '', '057fdd13412e46df891762c01f526846', '', false, false, 2, '医学装备档案管理', 3, '6.10.2.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(10, 'test.1111psls1', '2020-06-04 21:38:59.338', '', '', 'test.1111psls1', '2020-06-04 21:38:59.338', '', '057fdd13412e46df891762c01f526846', '', false, false, 2, '医学装备使用评价制度', 4, '6.10.2.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(11, 'test.1111psls1', '2020-06-04 21:38:59.341', '', '', 'test.1111psls1', '2020-06-04 21:38:59.341', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '安全风险管控制度', 1, '6.10.3.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(12, 'test.1111psls1', '2020-06-04 21:38:59.345', '', '', 'test.1111psls1', '2020-06-04 21:38:59.345', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '计量管理制度', 2, '6.10.3.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(13, 'test.1111psls1', '2020-06-04 21:38:59.349', '', '', 'test.1111psls1', '2020-06-04 21:38:59.349', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '临床使用安全管控制度', 3, '6.10.3.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(14, 'test.1111psls1', '2020-06-04 21:38:59.352', '', '', 'test.1111psls1', '2020-06-04 21:38:59.352', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '相关人员培训制度', 4, '6.10.3.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(15, 'test.1111psls1', '2020-06-04 21:38:59.354', '', '', 'test.1111psls1', '2020-06-04 21:38:59.354', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '放射与放疗安全', 5, '6.10.3.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(16, 'test.1111psls1', '2020-06-04 21:38:59.358', '', '', 'test.1111psls1', '2020-06-04 21:38:59.358', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '机房警示标识', 6, '6.10.3.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(17, 'test.1111psls1', '2020-06-04 21:38:59.36', '', '', 'test.1111psls1', '2020-06-04 21:38:59.36', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '设备安全人员须知', 7, '6.10.3.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(18, 'test.1111psls1', '2020-06-04 21:38:59.363', '', '', 'test.1111psls1', '2020-06-04 21:38:59.363', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '特殊和放射装备制度', 8, '6.10.3.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(19, 'test.1111psls1', '2020-06-04 21:38:59.364', '', '', 'test.1111psls1', '2020-06-04 21:38:59.364', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '特殊和放射装备人员管理', 9, '6.10.3.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(20, 'test.1111psls1', '2020-06-04 21:38:59.365', '', '', 'test.1111psls1', '2020-06-04 21:38:59.365', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '放射诊断设备制度', 10, '6.10.3.4.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(21, 'test.1111psls1', '2020-06-04 21:38:59.365', '', '', 'test.1111psls1', '2020-06-04 21:38:59.365', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '放射使用管理制度', 11, '6.10.3.4.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(22, 'test.1111psls1', '2020-06-04 21:38:59.366', '', '', 'test.1111psls1', '2020-06-04 21:38:59.366', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '放射类安全指标', 12, '6.10.3.4.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(23, 'test.1111psls1', '2020-06-04 21:38:59.367', '', '', 'test.1111psls1', '2020-06-04 21:38:59.367', '', '057fdd13412e46df891762c01f526846', '', true, false, 3, '计量设备管理制度', 13, '6.10.3.5.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(24, 'test.1111psls1', '2020-06-04 21:38:59.368', '', '', 'test.1111psls1', '2020-06-04 21:38:59.368', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '计量设备日常管理', 14, '6.10.3.5.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(25, 'test.1111psls1', '2020-06-04 21:38:59.369', '', '', 'test.1111psls1', '2020-06-04 21:38:59.369', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '计量检测合格标志', 15, '6.10.3.5.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(26, 'test.1111psls1', '2020-06-04 21:38:59.37', '', '', 'test.1111psls1', '2020-06-04 21:38:59.37', '', '057fdd13412e46df891762c01f526846', '', false, false, 3, '计量设备相关人员须知', 16, '6.10.3.5.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(27, 'test.1111psls1', '2020-06-04 21:38:59.371', '', '', 'test.1111psls1', '2020-06-04 21:38:59.371', '', '057fdd13412e46df891762c01f526846', '', false, false, 4, '设备使用人员培训制度', 1, '6.10.4.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(28, 'test.1111psls1', '2020-06-04 21:38:59.372', '', '', 'test.1111psls1', '2020-06-04 21:38:59.372', '', '057fdd13412e46df891762c01f526846', '', false, false, 4, '技术支持制度', 2, '6.10.4.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(29, 'test.1111psls1', '2020-06-04 21:38:59.373', '', '', 'test.1111psls1', '2020-06-04 21:38:59.373', '', '057fdd13412e46df891762c01f526846', '', false, false, 4, '意外事件处理培训制度', 3, '6.10.4.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(30, 'test.1111psls1', '2020-06-04 21:38:59.374', '', '', 'test.1111psls1', '2020-06-04 21:38:59.374', '', '057fdd13412e46df891762c01f526846', '', false, false, 4, '操作手册管理制度', 4, '6.10.4.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(31, 'test.1111psls1', '2020-06-04 21:38:59.375', '', '', 'test.1111psls1', '2020-06-04 21:38:59.375', '', '057fdd13412e46df891762c01f526846', '', false, false, 4, '设备操作考核制度', 5, '6.10.4.1.C.5');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(32, 'test.1111psls1', '2020-06-04 21:38:59.376', '', '', 'test.1111psls1', '2020-06-04 21:38:59.376', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '医学装备使用保障制度', 1, '6.10.5.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(33, 'test.1111psls1', '2020-06-04 21:38:59.377', '', '', 'test.1111psls1', '2020-06-04 21:38:59.377', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '医学装备养计检制度', 2, '6.10.5.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(34, 'test.1111psls1', '2020-06-04 21:38:59.378', '', '', 'test.1111psls1', '2020-06-04 21:38:59.378', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '故障维修分析', 3, '6.10.5.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(35, 'test.1111psls1', '2020-06-04 21:38:59.379', '', '', 'test.1111psls1', '2020-06-04 21:38:59.379', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '医学装备应急预案', 4, '6.10.5.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(36, 'test.1111psls1', '2020-06-04 21:38:59.38', '', '', 'test.1111psls1', '2020-06-04 21:38:59.38', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '急救支持类装备待机要求', 5, '6.10.5.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(37, 'test.1111psls1', '2020-06-04 21:38:59.38', '', '', 'test.1111psls1', '2020-06-04 21:38:59.38', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '设备故障紧急预案程序', 6, '6.10.5.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(38, 'test.1111psls1', '2020-06-04 21:38:59.381', '', '', 'test.1111psls1', '2020-06-04 21:38:59.381', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '急救支持类装备急调配程序', 7, '6.10.5.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(39, 'test.1111psls1', '2020-06-04 21:38:59.382', '', '', 'test.1111psls1', '2020-06-04 21:38:59.382', '', '057fdd13412e46df891762c01f526846', '', false, false, 5, '医务人员须知', 8, '6.10.5.3.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(40, 'test.1111psls1', '2020-06-04 21:38:59.384', '', '', 'test.1111psls1', '2020-06-04 21:38:59.384', '', '057fdd13412e46df891762c01f526846', '', false, false, 6, '管理部门学习制度', 1, '6.10.7.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(41, 'test.1111psls1', '2020-06-04 21:38:59.385', '', '', 'test.1111psls1', '2020-06-04 21:38:59.385', '', '057fdd13412e46df891762c01f526846', '', false, false, 6, '管理部门定期总结分析制度', 2, '6.10.7.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(42, 'test.1111psls1', '2020-06-04 21:38:59.385', '', '', 'test.1111psls1', '2020-06-04 21:38:59.385', '', '057fdd13412e46df891762c01f526846', '', false, false, 6, '使用部门专人管理制度', 3, '6.10.7.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(43, 'test.1111psls1', '2020-06-04 21:38:59.386', '', '', 'test.1111psls1', '2020-06-04 21:38:59.386', '', '057fdd13412e46df891762c01f526846', '', false, false, 6, '使用部门日常管理工作', 4, '6.10.7.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(44, 'test.1111psls1', '2020-06-04 21:38:59.387', '', '', 'test.1111psls1', '2020-06-04 21:38:59.387', '', '057fdd13412e46df891762c01f526846', '', false, false, 6, '使用人员培训制度', 5, '6.10.7.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(45, 'test.1111psls1', '2020-06-04 21:38:59.39', '', '', 'test.1111psls1', '2020-06-04 21:38:59.39', '', '057fdd13412e46df891762c01f526846', '', false, false, 7, '制度实行自查', 1, '6.10.1.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(46, 'test.1111psls1', '2020-06-04 21:38:59.392', '', '', 'test.1111psls1', '2020-06-04 21:38:59.392', '', '057fdd13412e46df891762c01f526846', '', false, false, 7, '主管部门监察', 2, '6.10.1.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(47, 'test.1111psls1', '2020-06-04 21:38:59.395', '', '', 'test.1111psls1', '2020-06-04 21:38:59.395', '', '057fdd13412e46df891762c01f526846', '', false, false, 8, '制度实行自查', 1, '6.10.2.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(48, 'test.1111psls1', '2020-06-04 21:38:59.397', '', '', 'test.1111psls1', '2020-06-04 21:38:59.397', '', '057fdd13412e46df891762c01f526846', '', false, false, 8, '主管部门监察', 2, '6.10.2.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(49, 'test.1111psls1', '2020-06-04 21:38:59.399', '', '', 'test.1111psls1', '2020-06-04 21:38:59.399', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '安全自查和记录', 1, '6.10.3.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(50, 'test.1111psls1', '2020-06-04 21:38:59.402', '', '', 'test.1111psls1', '2020-06-04 21:38:59.402', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '主管部门监察', 2, '6.10.3.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(51, 'test.1111psls1', '2020-06-04 21:38:59.403', '', '', 'test.1111psls1', '2020-06-04 21:38:59.403', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '机房安全自查', 3, '6.10.3.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(52, 'test.1111psls1', '2020-06-04 21:38:59.404', '', '', 'test.1111psls1', '2020-06-04 21:38:59.404', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '主管部门监察', 4, '6.10.3.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(53, 'test.1111psls1', '2020-06-04 21:38:59.405', '', '', 'test.1111psls1', '2020-06-04 21:38:59.405', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '特殊和放射装备自查', 5, '6.10.3.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(54, 'test.1111psls1', '2020-06-04 21:38:59.405', '', '', 'test.1111psls1', '2020-06-04 21:38:59.405', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '主管部门监察', 6, '6.10.3.3.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(55, 'test.1111psls1', '2020-06-04 21:38:59.406', '', '', 'test.1111psls1', '2020-06-04 21:38:59.406', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '放射使用科室自查', 7, '6.10.3.4.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(56, 'test.1111psls1', '2020-06-04 21:38:59.407', '', '', 'test.1111psls1', '2020-06-04 21:38:59.407', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '主管部门对放射部门监察', 8, '6.10.3.4.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(57, 'test.1111psls1', '2020-06-04 21:38:59.408', '', '', 'test.1111psls1', '2020-06-04 21:38:59.408', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '计量设备自查', 9, '6.10.3.5.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(58, 'test.1111psls1', '2020-06-04 21:38:59.408', '', '', 'test.1111psls1', '2020-06-04 21:38:59.408', '', '057fdd13412e46df891762c01f526846', '', false, false, 9, '主管部门对计量设备的监察', 10, '6.10.3.5.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(59, 'test.1111psls1', '2020-06-04 21:38:59.41', '', '', 'test.1111psls1', '2020-06-04 21:38:59.41', '', '057fdd13412e46df891762c01f526846', '', false, false, 10, '使用科室操作自查', 1, '6.10.4.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(60, 'test.1111psls1', '2020-06-04 21:38:59.411', '', '', 'test.1111psls1', '2020-06-04 21:38:59.411', '', '057fdd13412e46df891762c01f526846', '', false, false, 10, '主管部门对使用操作的监察', 2, '6.10.4.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(61, 'test.1111psls1', '2020-06-04 21:38:59.412', '', '', 'test.1111psls1', '2020-06-04 21:38:59.412', '', '057fdd13412e46df891762c01f526846', '', false, false, 11, '故障维修分析', 1, '6.10.5.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(62, 'test.1111psls1', '2020-06-04 21:38:59.413', '', '', 'test.1111psls1', '2020-06-04 21:38:59.413', '', '057fdd13412e46df891762c01f526846', '', false, false, 11, '故障分析改进措施', 2, '6.10.5.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(63, 'test.1111psls1', '2020-06-04 21:38:59.414', '', '', 'test.1111psls1', '2020-06-04 21:38:59.414', '', '057fdd13412e46df891762c01f526846', '', false, false, 11, '急救支持类装备实时监管', 3, '6.10.5.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(64, 'test.1111psls1', '2020-06-04 21:38:59.415', '', '', 'test.1111psls1', '2020-06-04 21:38:59.415', '', '057fdd13412e46df891762c01f526846', '', false, false, 11, '急救支持类装备紧急程序演练', 4, '6.10.5.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(65, 'test.1111psls1', '2020-06-04 21:38:59.416', '', '', 'test.1111psls1', '2020-06-04 21:38:59.416', '', '057fdd13412e46df891762c01f526846', '', false, false, 12, '管理部门定期自查', 1, '6.10.7.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(66, 'test.1111psls1', '2020-06-04 21:38:59.417', '', '', 'test.1111psls1', '2020-06-04 21:38:59.417', '', '057fdd13412e46df891762c01f526846', '', false, false, 12, '管理部门定期巡检使用部门', 2, '6.10.7.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(67, 'test.1111psls1', '2020-06-04 21:38:59.418', '', '', 'test.1111psls1', '2020-06-04 21:38:59.418', '', '057fdd13412e46df891762c01f526846', '', false, false, 12, '使用部门使用自查', 3, '6.10.7.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(68, 'test.1111psls1', '2020-06-04 21:38:59.419', '', '', 'test.1111psls1', '2020-06-04 21:38:59.419', '', '057fdd13412e46df891762c01f526846', '', false, false, 12, '使用部门定期分析反馈', 4, '6.10.7.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(69, 'test.1111psls1', '2020-06-04 21:38:59.421', '', '', 'test.1111psls1', '2020-06-04 21:38:59.421', '', '057fdd13412e46df891762c01f526846', '', false, false, 13, '医学装备发展规划落实', 1, '6.10.1.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(70, 'test.1111psls1', '2020-06-04 21:38:59.423', '', '', 'test.1111psls1', '2020-06-04 21:38:59.423', '', '057fdd13412e46df891762c01f526846', '', false, false, 13, '医学装备配置方案和管理落实', 2, '6.10.1.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(71, 'test.1111psls1', '2020-06-04 21:38:59.426', '', '', 'test.1111psls1', '2020-06-04 21:38:59.426', '', '057fdd13412e46df891762c01f526846', '', false, false, 14, '设备管理工作持续改进', 1, '6.10.2.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(72, 'test.1111psls1', '2020-06-04 21:38:59.428', '', '', 'test.1111psls1', '2020-06-04 21:38:59.428', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '安全管控持续改进', 1, '6.10.3.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(73, 'test.1111psls1', '2020-06-04 21:38:59.431', '', '', 'test.1111psls1', '2020-06-04 21:38:59.431', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '机房安全持续改进', 2, '6.10.3.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(74, 'test.1111psls1', '2020-06-04 21:38:59.434', '', '', 'test.1111psls1', '2020-06-04 21:38:59.434', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '特殊医学装备安全措施改进', 3, '6.10.3.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(75, 'test.1111psls1', '2020-06-04 21:38:59.434', '', '', 'test.1111psls1', '2020-06-04 21:38:59.434', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '放射诊疗设备安全措施改进', 4, '6.10.3.4.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(76, 'test.1111psls1', '2020-06-04 21:38:59.435', '', '', 'test.1111psls1', '2020-06-04 21:38:59.435', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '计量设备使用持续改进', 5, '6.10.3.5.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(77, 'test.1111psls1', '2020-06-04 21:38:59.436', '', '', 'test.1111psls1', '2020-06-04 21:38:59.436', '', '057fdd13412e46df891762c01f526846', '', false, false, 15, '计量器具100%合格', 6, '6.10.3.5.A.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(78, 'test.1111psls1', '2020-06-04 21:38:59.438', '', '', 'test.1111psls1', '2020-06-04 21:38:59.438', '', '057fdd13412e46df891762c01f526846', '', false, false, 16, '操作和维护规范持续改进', 1, '6.10.4.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(79, 'test.1111psls1', '2020-06-04 21:38:59.439', '', '', 'test.1111psls1', '2020-06-04 21:38:59.439', '', '057fdd13412e46df891762c01f526846', '', false, false, 17, '设备使用保障措施持续改进', 1, '6.10.5.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(80, 'test.1111psls1', '2020-06-04 21:38:59.44', '', '', 'test.1111psls1', '2020-06-04 21:38:59.44', '', '057fdd13412e46df891762c01f526846', '', false, false, 17, '急救支持类装备完好率100%', 2, '6.10.5.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(81, 'test.1111psls1', '2020-06-04 21:38:59.44', '', '', 'test.1111psls1', '2020-06-04 21:38:59.44', '', '057fdd13412e46df891762c01f526846', '', false, false, 17, '急救支持类装备使用保障持续改进', 3, '6.10.5.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(82, 'test.1111psls1', '2020-06-04 21:38:59.442', '', '', 'test.1111psls1', '2020-06-04 21:38:59.442', '', '057fdd13412e46df891762c01f526846', '', false, false, 18, '医学装备管理持续改进', 1, '6.10.7.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(83, 'test.1111psls1', '2020-06-04 21:38:59.443', '', '', 'test.1111psls1', '2020-06-04 21:38:59.443', '', '057fdd13412e46df891762c01f526846', '', false, false, 18, '全院医学装备使用管理持续改进', 2, '6.10.7.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(1, 'huangjianzhen', '2020-06-04 21:47:48.887', '', '', 'huangjianzhen', '2020-06-04 21:47:48.887', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '医学装备管理总体系', 1, '6.10.1.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(2, 'huangjianzhen', '2020-06-04 21:47:48.891', '', '', 'huangjianzhen', '2020-06-04 21:47:48.891', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '管理人员配备', 2, '6.10.1.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(3, 'huangjianzhen', '2020-06-04 21:47:48.894', '', '', 'huangjianzhen', '2020-06-04 21:47:48.894', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '工作流程和岗位职责', 3, '6.10.1.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(4, 'huangjianzhen', '2020-06-04 21:47:48.896', '', '', 'huangjianzhen', '2020-06-04 21:47:48.896', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '常规与大型医学装备配置方案', 4, '6.10.1.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(5, 'huangjianzhen', '2020-06-04 21:47:48.897', '', '', 'huangjianzhen', '2020-06-04 21:47:48.897', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '医学装备生命周期管理制度', 5, '6.10.1.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(6, 'huangjianzhen', '2020-06-04 21:47:48.899', '', '', 'huangjianzhen', '2020-06-04 21:47:48.899', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 1, '使用科室人员配备', 6, '6.10.1.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(7, 'huangjianzhen', '2020-06-04 21:47:48.903', '', '', 'huangjianzhen', '2020-06-04 21:47:48.903', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 2, '购置论证相关制度与决策程序', 1, '6.10.2.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(8, 'huangjianzhen', '2020-06-04 21:47:48.905', '', '', 'huangjianzhen', '2020-06-04 21:47:48.905', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 2, '医学装备清单', 2, '6.10.2.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(9, 'huangjianzhen', '2020-06-04 21:47:48.906', '', '', 'huangjianzhen', '2020-06-04 21:47:48.906', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 2, '医学装备档案管理', 3, '6.10.2.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(10, 'huangjianzhen', '2020-06-04 21:47:48.908', '', '', 'huangjianzhen', '2020-06-04 21:47:48.908', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 2, '医学装备使用评价制度', 4, '6.10.2.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(11, 'huangjianzhen', '2020-06-04 21:47:48.911', '', '', 'huangjianzhen', '2020-06-04 21:47:48.911', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '安全风险管控制度', 1, '6.10.3.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(12, 'huangjianzhen', '2020-06-04 21:47:48.914', '', '', 'huangjianzhen', '2020-06-04 21:47:48.914', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '计量管理制度', 2, '6.10.3.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(13, 'huangjianzhen', '2020-06-04 21:47:48.917', '', '', 'huangjianzhen', '2020-06-04 21:47:48.917', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '临床使用安全管控制度', 3, '6.10.3.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(14, 'huangjianzhen', '2020-06-04 21:47:48.919', '', '', 'huangjianzhen', '2020-06-04 21:47:48.919', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '相关人员培训制度', 4, '6.10.3.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(15, 'huangjianzhen', '2020-06-04 21:47:48.921', '', '', 'huangjianzhen', '2020-06-04 21:47:48.921', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '放射与放疗安全', 5, '6.10.3.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(16, 'huangjianzhen', '2020-06-04 21:47:48.924', '', '', 'huangjianzhen', '2020-06-04 21:47:48.924', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '机房警示标识', 6, '6.10.3.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(17, 'huangjianzhen', '2020-06-04 21:47:48.926', '', '', 'huangjianzhen', '2020-06-04 21:47:48.926', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '设备安全人员须知', 7, '6.10.3.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(18, 'huangjianzhen', '2020-06-04 21:47:48.928', '', '', 'huangjianzhen', '2020-06-04 21:47:48.928', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '特殊和放射装备制度', 8, '6.10.3.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(19, 'huangjianzhen', '2020-06-04 21:47:48.928', '', '', 'huangjianzhen', '2020-06-04 21:47:48.928', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '特殊和放射装备人员管理', 9, '6.10.3.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(20, 'huangjianzhen', '2020-06-04 21:47:48.929', '', '', 'huangjianzhen', '2020-06-04 21:47:48.929', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '放射诊断设备制度', 10, '6.10.3.4.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(21, 'huangjianzhen', '2020-06-04 21:47:48.93', '', '', 'huangjianzhen', '2020-06-04 21:47:48.93', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '放射使用管理制度', 11, '6.10.3.4.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(22, 'huangjianzhen', '2020-06-04 21:47:48.93', '', '', 'huangjianzhen', '2020-06-04 21:47:48.93', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '放射类安全指标', 12, '6.10.3.4.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(23, 'huangjianzhen', '2020-06-04 21:47:48.931', '', '', 'huangjianzhen', '2020-06-04 21:47:48.931', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '计量设备管理制度', 13, '6.10.3.5.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(24, 'huangjianzhen', '2020-06-04 21:47:48.932', '', '', 'huangjianzhen', '2020-06-04 21:47:48.932', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '计量设备日常管理', 14, '6.10.3.5.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(25, 'huangjianzhen', '2020-06-04 21:47:48.932', '', '', 'huangjianzhen', '2020-06-04 21:47:48.932', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '计量检测合格标志', 15, '6.10.3.5.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(26, 'huangjianzhen', '2020-06-04 21:47:48.933', '', '', 'huangjianzhen', '2020-06-04 21:47:48.933', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 3, '计量设备相关人员须知', 16, '6.10.3.5.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(27, 'huangjianzhen', '2020-06-04 21:47:48.934', '', '', 'huangjianzhen', '2020-06-04 21:47:48.934', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 4, '设备使用人员培训制度', 1, '6.10.4.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(28, 'huangjianzhen', '2020-06-04 21:47:48.935', '', '', 'huangjianzhen', '2020-06-04 21:47:48.935', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 4, '技术支持制度', 2, '6.10.4.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(29, 'huangjianzhen', '2020-06-04 21:47:48.935', '', '', 'huangjianzhen', '2020-06-04 21:47:48.935', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 4, '意外事件处理培训制度', 3, '6.10.4.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(30, 'huangjianzhen', '2020-06-04 21:47:48.936', '', '', 'huangjianzhen', '2020-06-04 21:47:48.936', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 4, '操作手册管理制度', 4, '6.10.4.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(31, 'huangjianzhen', '2020-06-04 21:47:48.937', '', '', 'huangjianzhen', '2020-06-04 21:47:48.937', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 4, '设备操作考核制度', 5, '6.10.4.1.C.5');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(32, 'huangjianzhen', '2020-06-04 21:47:48.938', '', '', 'huangjianzhen', '2020-06-04 21:47:48.938', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '医学装备使用保障制度', 1, '6.10.5.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(33, 'huangjianzhen', '2020-06-04 21:47:48.939', '', '', 'huangjianzhen', '2020-06-04 21:47:48.939', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '医学装备养计检制度', 2, '6.10.5.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(34, 'huangjianzhen', '2020-06-04 21:47:48.939', '', '', 'huangjianzhen', '2020-06-04 21:47:48.939', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '故障维修分析', 3, '6.10.5.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(35, 'huangjianzhen', '2020-06-04 21:47:48.94', '', '', 'huangjianzhen', '2020-06-04 21:47:48.94', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '医学装备应急预案', 4, '6.10.5.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(36, 'huangjianzhen', '2020-06-04 21:47:48.94', '', '', 'huangjianzhen', '2020-06-04 21:47:48.94', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '急救支持类装备待机要求', 5, '6.10.5.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(37, 'huangjianzhen', '2020-06-04 21:47:48.941', '', '', 'huangjianzhen', '2020-06-04 21:47:48.941', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '设备故障紧急预案程序', 6, '6.10.5.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(38, 'huangjianzhen', '2020-06-04 21:47:48.942', '', '', 'huangjianzhen', '2020-06-04 21:47:48.942', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '急救支持类装备急调配程序', 7, '6.10.5.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(39, 'huangjianzhen', '2020-06-04 21:47:48.943', '', '', 'huangjianzhen', '2020-06-04 21:47:48.943', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 5, '医务人员须知', 8, '6.10.5.3.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(40, 'huangjianzhen', '2020-06-04 21:47:48.944', '', '', 'huangjianzhen', '2020-06-04 21:47:48.944', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 6, '管理部门学习制度', 1, '6.10.7.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(41, 'huangjianzhen', '2020-06-04 21:47:48.945', '', '', 'huangjianzhen', '2020-06-04 21:47:48.945', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 6, '管理部门定期总结分析制度', 2, '6.10.7.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(42, 'huangjianzhen', '2020-06-04 21:47:48.946', '', '', 'huangjianzhen', '2020-06-04 21:47:48.946', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 6, '使用部门专人管理制度', 3, '6.10.7.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(43, 'huangjianzhen', '2020-06-04 21:47:48.946', '', '', 'huangjianzhen', '2020-06-04 21:47:48.946', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 6, '使用部门日常管理工作', 4, '6.10.7.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(44, 'huangjianzhen', '2020-06-04 21:47:48.947', '', '', 'huangjianzhen', '2020-06-04 21:47:48.947', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 6, '使用人员培训制度', 5, '6.10.7.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(45, 'huangjianzhen', '2020-06-04 21:47:48.95', '', '', 'huangjianzhen', '2020-06-04 21:47:48.95', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 7, '制度实行自查', 1, '6.10.1.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(46, 'huangjianzhen', '2020-06-04 21:47:48.953', '', '', 'huangjianzhen', '2020-06-04 21:47:48.953', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 7, '主管部门监察', 2, '6.10.1.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(47, 'huangjianzhen', '2020-06-04 21:47:48.955', '', '', 'huangjianzhen', '2020-06-04 21:47:48.955', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 8, '制度实行自查', 1, '6.10.2.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(48, 'huangjianzhen', '2020-06-04 21:47:48.956', '', '', 'huangjianzhen', '2020-06-04 21:47:48.956', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 8, '主管部门监察', 2, '6.10.2.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(49, 'huangjianzhen', '2020-06-04 21:47:48.958', '', '', 'huangjianzhen', '2020-06-04 21:47:48.958', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '安全自查和记录', 1, '6.10.3.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(50, 'huangjianzhen', '2020-06-04 21:47:48.96', '', '', 'huangjianzhen', '2020-06-04 21:47:48.96', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 2, '6.10.3.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(51, 'huangjianzhen', '2020-06-04 21:47:48.962', '', '', 'huangjianzhen', '2020-06-04 21:47:48.962', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '机房安全自查', 3, '6.10.3.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(52, 'huangjianzhen', '2020-06-04 21:47:48.962', '', '', 'huangjianzhen', '2020-06-04 21:47:48.962', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 4, '6.10.3.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(53, 'huangjianzhen', '2020-06-04 21:47:48.963', '', '', 'huangjianzhen', '2020-06-04 21:47:48.963', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '特殊和放射装备自查', 5, '6.10.3.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(54, 'huangjianzhen', '2020-06-04 21:47:48.964', '', '', 'huangjianzhen', '2020-06-04 21:47:48.964', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 6, '6.10.3.3.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(55, 'huangjianzhen', '2020-06-04 21:47:48.964', '', '', 'huangjianzhen', '2020-06-04 21:47:48.964', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '放射使用科室自查', 7, '6.10.3.4.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(56, 'huangjianzhen', '2020-06-04 21:47:48.965', '', '', 'huangjianzhen', '2020-06-04 21:47:48.965', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '主管部门对放射部门监察', 8, '6.10.3.4.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(57, 'huangjianzhen', '2020-06-04 21:47:48.965', '', '', 'huangjianzhen', '2020-06-04 21:47:48.965', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '计量设备自查', 9, '6.10.3.5.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(58, 'huangjianzhen', '2020-06-04 21:47:48.966', '', '', 'huangjianzhen', '2020-06-04 21:47:48.966', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 9, '主管部门对计量设备的监察', 10, '6.10.3.5.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(59, 'huangjianzhen', '2020-06-04 21:47:48.967', '', '', 'huangjianzhen', '2020-06-04 21:47:48.967', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 10, '使用科室操作自查', 1, '6.10.4.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(60, 'huangjianzhen', '2020-06-04 21:47:48.968', '', '', 'huangjianzhen', '2020-06-04 21:47:48.968', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 10, '主管部门对使用操作的监察', 2, '6.10.4.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(61, 'huangjianzhen', '2020-06-04 21:47:48.969', '', '', 'huangjianzhen', '2020-06-04 21:47:48.969', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 11, '故障维修分析', 1, '6.10.5.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(62, 'huangjianzhen', '2020-06-04 21:47:48.97', '', '', 'huangjianzhen', '2020-06-04 21:47:48.97', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 11, '故障分析改进措施', 2, '6.10.5.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(63, 'huangjianzhen', '2020-06-04 21:47:48.97', '', '', 'huangjianzhen', '2020-06-04 21:47:48.97', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 11, '急救支持类装备实时监管', 3, '6.10.5.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(64, 'huangjianzhen', '2020-06-04 21:47:48.971', '', '', 'huangjianzhen', '2020-06-04 21:47:48.971', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 11, '急救支持类装备紧急程序演练', 4, '6.10.5.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(65, 'huangjianzhen', '2020-06-04 21:47:48.972', '', '', 'huangjianzhen', '2020-06-04 21:47:48.972', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 12, '管理部门定期自查', 1, '6.10.7.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(66, 'huangjianzhen', '2020-06-04 21:47:48.973', '', '', 'huangjianzhen', '2020-06-04 21:47:48.973', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 12, '管理部门定期巡检使用部门', 2, '6.10.7.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(67, 'huangjianzhen', '2020-06-04 21:47:48.973', '', '', 'huangjianzhen', '2020-06-04 21:47:48.973', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 12, '使用部门使用自查', 3, '6.10.7.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(68, 'huangjianzhen', '2020-06-04 21:47:48.974', '', '', 'huangjianzhen', '2020-06-04 21:47:48.974', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 12, '使用部门定期分析反馈', 4, '6.10.7.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(69, 'huangjianzhen', '2020-06-04 21:47:48.976', '', '', 'huangjianzhen', '2020-06-04 21:47:48.976', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 13, '医学装备发展规划落实', 1, '6.10.1.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(70, 'huangjianzhen', '2020-06-04 21:47:48.977', '', '', 'huangjianzhen', '2020-06-04 21:47:48.977', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 13, '医学装备配置方案和管理落实', 2, '6.10.1.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(71, 'huangjianzhen', '2020-06-04 21:47:48.98', '', '', 'huangjianzhen', '2020-06-04 21:47:48.98', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 14, '设备管理工作持续改进', 1, '6.10.2.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(72, 'huangjianzhen', '2020-06-04 21:47:48.982', '', '', 'huangjianzhen', '2020-06-04 21:47:48.982', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '安全管控持续改进', 1, '6.10.3.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(73, 'huangjianzhen', '2020-06-04 21:47:48.985', '', '', 'huangjianzhen', '2020-06-04 21:47:48.985', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '机房安全持续改进', 2, '6.10.3.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(74, 'huangjianzhen', '2020-06-04 21:47:48.986', '', '', 'huangjianzhen', '2020-06-04 21:47:48.986', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '特殊医学装备安全措施改进', 3, '6.10.3.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(75, 'huangjianzhen', '2020-06-04 21:47:48.987', '', '', 'huangjianzhen', '2020-06-04 21:47:48.987', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '放射诊疗设备安全措施改进', 4, '6.10.3.4.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(76, 'huangjianzhen', '2020-06-04 21:47:48.988', '', '', 'huangjianzhen', '2020-06-04 21:47:48.988', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '计量设备使用持续改进', 5, '6.10.3.5.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(77, 'huangjianzhen', '2020-06-04 21:47:48.988', '', '', 'huangjianzhen', '2020-06-04 21:47:48.988', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 15, '计量器具100%合格', 6, '6.10.3.5.A.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(78, 'huangjianzhen', '2020-06-04 21:47:48.989', '', '', 'huangjianzhen', '2020-06-04 21:47:48.989', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 16, '操作和维护规范持续改进', 1, '6.10.4.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(79, 'huangjianzhen', '2020-06-04 21:47:48.991', '', '', 'huangjianzhen', '2020-06-04 21:47:48.991', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 17, '设备使用保障措施持续改进', 1, '6.10.5.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(80, 'huangjianzhen', '2020-06-04 21:47:48.991', '', '', 'huangjianzhen', '2020-06-04 21:47:48.991', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 17, '急救支持类装备完好率100%', 2, '6.10.5.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(81, 'huangjianzhen', '2020-06-04 21:47:48.992', '', '', 'huangjianzhen', '2020-06-04 21:47:48.992', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 17, '急救支持类装备使用保障持续改进', 3, '6.10.5.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(82, 'huangjianzhen', '2020-06-04 21:47:48.993', '', '', 'huangjianzhen', '2020-06-04 21:47:48.993', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 18, '医学装备管理持续改进', 1, '6.10.7.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(83, 'huangjianzhen', '2020-06-04 21:47:48.994', '', '', 'huangjianzhen', '2020-06-04 21:47:48.994', '', '2a773154623311e7aeb25254001a1c14', '', false, false, 18, '全院医学装备使用管理持续改进', 2, '6.10.7.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(1, 'Demonstration', '2020-06-24 19:09:28.121', '', '', 'Demonstration', '2020-06-24 19:09:28.121', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '医学装备管理总体系', 1, '6.10.1.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(2, 'Demonstration', '2020-06-24 19:09:28.139', '', '', 'Demonstration', '2020-06-24 19:09:28.139', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '管理人员配备', 2, '6.10.1.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(3, 'Demonstration', '2020-06-24 19:09:28.143', '', '', 'Demonstration', '2020-06-24 19:09:28.143', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '工作流程和岗位职责', 3, '6.10.1.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(4, 'Demonstration', '2020-06-24 19:09:28.146', '', '', 'Demonstration', '2020-06-24 19:09:28.146', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '常规与大型医学装备配置方案', 4, '6.10.1.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(5, 'Demonstration', '2020-06-24 19:09:28.148', '', '', 'Demonstration', '2020-06-24 19:09:28.148', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '医学装备生命周期管理制度', 5, '6.10.1.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(6, 'Demonstration', '2020-06-24 19:09:28.151', '', '', 'Demonstration', '2020-06-24 19:09:28.151', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 1, '使用科室人员配备', 6, '6.10.1.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(7, 'Demonstration', '2020-06-24 19:09:28.155', '', '', 'Demonstration', '2020-06-24 19:09:28.155', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 2, '购置论证相关制度与决策程序', 1, '6.10.2.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(8, 'Demonstration', '2020-06-24 19:09:28.156', '', '', 'Demonstration', '2020-06-24 19:09:28.156', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 2, '医学装备清单', 2, '6.10.2.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(9, 'Demonstration', '2020-06-24 19:09:28.158', '', '', 'Demonstration', '2020-06-24 19:09:28.158', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 2, '医学装备档案管理', 3, '6.10.2.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(10, 'Demonstration', '2020-06-24 19:09:28.161', '', '', 'Demonstration', '2020-06-24 19:09:28.161', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 2, '医学装备使用评价制度', 4, '6.10.2.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(11, 'Demonstration', '2020-06-24 19:09:28.164', '', '', 'Demonstration', '2020-06-24 19:09:28.164', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '安全风险管控制度', 1, '6.10.3.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(12, 'Demonstration', '2020-06-24 19:09:28.168', '', '', 'Demonstration', '2020-06-24 19:09:28.168', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '计量管理制度', 2, '6.10.3.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(13, 'Demonstration', '2020-06-24 19:09:28.171', '', '', 'Demonstration', '2020-06-24 19:09:28.171', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '临床使用安全管控制度', 3, '6.10.3.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(14, 'Demonstration', '2020-06-24 19:09:28.173', '', '', 'Demonstration', '2020-06-24 19:09:28.173', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '相关人员培训制度', 4, '6.10.3.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(15, 'Demonstration', '2020-06-24 19:09:28.176', '', '', 'Demonstration', '2020-06-24 19:09:28.176', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '放射与放疗安全', 5, '6.10.3.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(16, 'Demonstration', '2020-06-24 19:09:28.179', '', '', 'Demonstration', '2020-06-24 19:09:28.179', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '机房警示标识', 6, '6.10.3.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(17, 'Demonstration', '2020-06-24 19:09:28.182', '', '', 'Demonstration', '2020-06-24 19:09:28.182', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '设备安全人员须知', 7, '6.10.3.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(18, 'Demonstration', '2020-06-24 19:09:28.184', '', '', 'Demonstration', '2020-06-24 19:09:28.184', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '特殊和放射装备制度', 8, '6.10.3.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(19, 'Demonstration', '2020-06-24 19:09:28.184', '', '', 'Demonstration', '2020-06-24 19:09:28.184', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '特殊和放射装备人员管理', 9, '6.10.3.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(20, 'Demonstration', '2020-06-24 19:09:28.185', '', '', 'Demonstration', '2020-06-24 19:09:28.185', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '放射诊断设备制度', 10, '6.10.3.4.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(21, 'Demonstration', '2020-06-24 19:09:28.186', '', '', 'Demonstration', '2020-06-24 19:09:28.186', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '放射使用管理制度', 11, '6.10.3.4.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(22, 'Demonstration', '2020-06-24 19:09:28.187', '', '', 'Demonstration', '2020-06-24 19:09:28.187', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '放射类安全指标', 12, '6.10.3.4.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(23, 'Demonstration', '2020-06-24 19:09:28.188', '', '', 'Demonstration', '2020-06-24 19:09:28.188', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '计量设备管理制度', 13, '6.10.3.5.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(24, 'Demonstration', '2020-06-24 19:09:28.188', '', '', 'Demonstration', '2020-06-24 19:09:28.188', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '计量设备日常管理', 14, '6.10.3.5.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(25, 'Demonstration', '2020-06-24 19:09:28.189', '', '', 'Demonstration', '2020-06-24 19:09:28.189', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '计量检测合格标志', 15, '6.10.3.5.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(26, 'Demonstration', '2020-06-24 19:09:28.19', '', '', 'Demonstration', '2020-06-24 19:09:28.19', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 3, '计量设备相关人员须知', 16, '6.10.3.5.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(27, 'Demonstration', '2020-06-24 19:09:28.191', '', '', 'Demonstration', '2020-06-24 19:09:28.191', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 4, '设备使用人员培训制度', 1, '6.10.4.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(28, 'Demonstration', '2020-06-24 19:09:28.192', '', '', 'Demonstration', '2020-06-24 19:09:28.192', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 4, '技术支持制度', 2, '6.10.4.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(29, 'Demonstration', '2020-06-24 19:09:28.193', '', '', 'Demonstration', '2020-06-24 19:09:28.193', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 4, '意外事件处理培训制度', 3, '6.10.4.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(30, 'Demonstration', '2020-06-24 19:09:28.193', '', '', 'Demonstration', '2020-06-24 19:09:28.193', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 4, '操作手册管理制度', 4, '6.10.4.1.C.4');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(31, 'Demonstration', '2020-06-24 19:09:28.194', '', '', 'Demonstration', '2020-06-24 19:09:28.194', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 4, '设备操作考核制度', 5, '6.10.4.1.C.5');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(32, 'Demonstration', '2020-06-24 19:09:28.196', '', '', 'Demonstration', '2020-06-24 19:09:28.196', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '医学装备使用保障制度', 1, '6.10.5.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(33, 'Demonstration', '2020-06-24 19:09:28.196', '', '', 'Demonstration', '2020-06-24 19:09:28.196', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '医学装备养计检制度', 2, '6.10.5.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(34, 'Demonstration', '2020-06-24 19:09:28.197', '', '', 'Demonstration', '2020-06-24 19:09:28.197', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '故障维修分析', 3, '6.10.5.1.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(35, 'Demonstration', '2020-06-24 19:09:28.198', '', '', 'Demonstration', '2020-06-24 19:09:28.198', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '医学装备应急预案', 4, '6.10.5.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(36, 'Demonstration', '2020-06-24 19:09:28.198', '', '', 'Demonstration', '2020-06-24 19:09:28.198', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '急救支持类装备待机要求', 5, '6.10.5.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(37, 'Demonstration', '2020-06-24 19:09:28.199', '', '', 'Demonstration', '2020-06-24 19:09:28.199', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '设备故障紧急预案程序', 6, '6.10.5.3.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(38, 'Demonstration', '2020-06-24 19:09:28.2', '', '', 'Demonstration', '2020-06-24 19:09:28.2', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '急救支持类装备急调配程序', 7, '6.10.5.3.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(39, 'Demonstration', '2020-06-24 19:09:28.201', '', '', 'Demonstration', '2020-06-24 19:09:28.201', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 5, '医务人员须知', 8, '6.10.5.3.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(40, 'Demonstration', '2020-06-24 19:09:28.202', '', '', 'Demonstration', '2020-06-24 19:09:28.202', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 6, '管理部门学习制度', 1, '6.10.7.1.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(41, 'Demonstration', '2020-06-24 19:09:28.203', '', '', 'Demonstration', '2020-06-24 19:09:28.203', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 6, '管理部门定期总结分析制度', 2, '6.10.7.1.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(42, 'Demonstration', '2020-06-24 19:09:28.203', '', '', 'Demonstration', '2020-06-24 19:09:28.203', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 6, '使用部门专人管理制度', 3, '6.10.7.2.C.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(43, 'Demonstration', '2020-06-24 19:09:28.204', '', '', 'Demonstration', '2020-06-24 19:09:28.204', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 6, '使用部门日常管理工作', 4, '6.10.7.2.C.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(44, 'Demonstration', '2020-06-24 19:09:28.205', '', '', 'Demonstration', '2020-06-24 19:09:28.205', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 6, '使用人员培训制度', 5, '6.10.7.2.C.3');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(45, 'Demonstration', '2020-06-24 19:09:28.207', '', '', 'Demonstration', '2020-06-24 19:09:28.207', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 7, '制度实行自查', 1, '6.10.1.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(46, 'Demonstration', '2020-06-24 19:09:28.209', '', '', 'Demonstration', '2020-06-24 19:09:28.209', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 7, '主管部门监察', 2, '6.10.1.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(47, 'Demonstration', '2020-06-24 19:09:28.212', '', '', 'Demonstration', '2020-06-24 19:09:28.212', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 8, '制度实行自查', 1, '6.10.2.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(48, 'Demonstration', '2020-06-24 19:09:28.214', '', '', 'Demonstration', '2020-06-24 19:09:28.214', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 8, '主管部门监察', 2, '6.10.2.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(49, 'Demonstration', '2020-06-24 19:09:28.216', '', '', 'Demonstration', '2020-06-24 19:09:28.216', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '安全自查和记录', 1, '6.10.3.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(50, 'Demonstration', '2020-06-24 19:09:28.218', '', '', 'Demonstration', '2020-06-24 19:09:28.218', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 2, '6.10.3.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(51, 'Demonstration', '2020-06-24 19:09:28.219', '', '', 'Demonstration', '2020-06-24 19:09:28.219', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '机房安全自查', 3, '6.10.3.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(52, 'Demonstration', '2020-06-24 19:09:28.22', '', '', 'Demonstration', '2020-06-24 19:09:28.22', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 4, '6.10.3.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(53, 'Demonstration', '2020-06-24 19:09:28.22', '', '', 'Demonstration', '2020-06-24 19:09:28.22', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '特殊和放射装备自查', 5, '6.10.3.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(54, 'Demonstration', '2020-06-24 19:09:28.221', '', '', 'Demonstration', '2020-06-24 19:09:28.221', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '主管部门监察', 6, '6.10.3.3.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(55, 'Demonstration', '2020-06-24 19:09:28.222', '', '', 'Demonstration', '2020-06-24 19:09:28.222', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '放射使用科室自查', 7, '6.10.3.4.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(56, 'Demonstration', '2020-06-24 19:09:28.223', '', '', 'Demonstration', '2020-06-24 19:09:28.223', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '主管部门对放射部门监察', 8, '6.10.3.4.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(57, 'Demonstration', '2020-06-24 19:09:28.223', '', '', 'Demonstration', '2020-06-24 19:09:28.223', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '计量设备自查', 9, '6.10.3.5.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(58, 'Demonstration', '2020-06-24 19:09:28.224', '', '', 'Demonstration', '2020-06-24 19:09:28.224', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 9, '主管部门对计量设备的监察', 10, '6.10.3.5.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(59, 'Demonstration', '2020-06-24 19:09:28.226', '', '', 'Demonstration', '2020-06-24 19:09:28.226', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 10, '使用科室操作自查', 1, '6.10.4.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(60, 'Demonstration', '2020-06-24 19:09:28.226', '', '', 'Demonstration', '2020-06-24 19:09:28.226', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 10, '主管部门对使用操作的监察', 2, '6.10.4.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(61, 'Demonstration', '2020-06-24 19:09:28.228', '', '', 'Demonstration', '2020-06-24 19:09:28.228', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 11, '故障维修分析', 1, '6.10.5.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(62, 'Demonstration', '2020-06-24 19:09:28.229', '', '', 'Demonstration', '2020-06-24 19:09:28.229', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 11, '故障分析改进措施', 2, '6.10.5.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(63, 'Demonstration', '2020-06-24 19:09:28.229', '', '', 'Demonstration', '2020-06-24 19:09:28.229', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 11, '急救支持类装备实时监管', 3, '6.10.5.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(64, 'Demonstration', '2020-06-24 19:09:28.23', '', '', 'Demonstration', '2020-06-24 19:09:28.23', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 11, '急救支持类装备紧急程序演练', 4, '6.10.5.3.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(65, 'Demonstration', '2020-06-24 19:09:28.231', '', '', 'Demonstration', '2020-06-24 19:09:28.231', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 12, '管理部门定期自查', 1, '6.10.7.1.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(66, 'Demonstration', '2020-06-24 19:09:28.232', '', '', 'Demonstration', '2020-06-24 19:09:28.232', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 12, '管理部门定期巡检使用部门', 2, '6.10.7.1.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(67, 'Demonstration', '2020-06-24 19:09:28.233', '', '', 'Demonstration', '2020-06-24 19:09:28.233', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 12, '使用部门使用自查', 3, '6.10.7.2.B.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(68, 'Demonstration', '2020-06-24 19:09:28.234', '', '', 'Demonstration', '2020-06-24 19:09:28.234', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 12, '使用部门定期分析反馈', 4, '6.10.7.2.B.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(69, 'Demonstration', '2020-06-24 19:09:28.236', '', '', 'Demonstration', '2020-06-24 19:09:28.236', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 13, '医学装备发展规划落实', 1, '6.10.1.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(70, 'Demonstration', '2020-06-24 19:09:28.237', '', '', 'Demonstration', '2020-06-24 19:09:28.237', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 13, '医学装备配置方案和管理落实', 2, '6.10.1.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(71, 'Demonstration', '2020-06-24 19:09:28.24', '', '', 'Demonstration', '2020-06-24 19:09:28.24', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 14, '设备管理工作持续改进', 1, '6.10.2.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(72, 'Demonstration', '2020-06-24 19:09:28.243', '', '', 'Demonstration', '2020-06-24 19:09:28.243', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '安全管控持续改进', 1, '6.10.3.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(73, 'Demonstration', '2020-06-24 19:09:28.245', '', '', 'Demonstration', '2020-06-24 19:09:28.245', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '机房安全持续改进', 2, '6.10.3.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(74, 'Demonstration', '2020-06-24 19:09:28.247', '', '', 'Demonstration', '2020-06-24 19:09:28.247', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '特殊医学装备安全措施改进', 3, '6.10.3.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(75, 'Demonstration', '2020-06-24 19:09:28.248', '', '', 'Demonstration', '2020-06-24 19:09:28.248', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '放射诊疗设备安全措施改进', 4, '6.10.3.4.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(76, 'Demonstration', '2020-06-24 19:09:28.249', '', '', 'Demonstration', '2020-06-24 19:09:28.249', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '计量设备使用持续改进', 5, '6.10.3.5.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(77, 'Demonstration', '2020-06-24 19:09:28.249', '', '', 'Demonstration', '2020-06-24 19:09:28.249', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 15, '计量器具100%合格', 6, '6.10.3.5.A.2');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(78, 'Demonstration', '2020-06-24 19:09:28.251', '', '', 'Demonstration', '2020-06-24 19:09:28.251', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 16, '操作和维护规范持续改进', 1, '6.10.4.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(79, 'Demonstration', '2020-06-24 19:09:28.253', '', '', 'Demonstration', '2020-06-24 19:09:28.253', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 17, '设备使用保障措施持续改进', 1, '6.10.5.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(80, 'Demonstration', '2020-06-24 19:09:28.254', '', '', 'Demonstration', '2020-06-24 19:09:28.254', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 17, '急救支持类装备完好率100%', 2, '6.10.5.2.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(81, 'Demonstration', '2020-06-24 19:09:28.254', '', '', 'Demonstration', '2020-06-24 19:09:28.254', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 17, '急救支持类装备使用保障持续改进', 3, '6.10.5.3.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(82, 'Demonstration', '2020-06-24 19:09:28.256', '', '', 'Demonstration', '2020-06-24 19:09:28.256', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 18, '医学装备管理持续改进', 1, '6.10.7.1.A.1');
-- INSERT INTO public.audit_group
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
-- VALUES(83, 'Demonstration', '2020-06-24 19:09:28.256', '', '', 'Demonstration', '2020-06-24 19:09:28.256', '', '2a773b22623311e7aeb25254001a1c14', '', false, false, 18, '全院医学装备使用管理持续改进', 2, '6.10.7.2.A.1');
--
--
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(1, 'test.1111psls1', '2020-06-04 21:38:59.303', '', '', 'test.1111psls1', '2020-06-04 21:38:59.303', '', '057fdd13412e46df891762c01f526846', true, 1, '管理体系文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(2, 'test.1111psls1', '2020-06-04 21:38:59.311', '', '', 'test.1111psls1', '2020-06-04 21:38:59.311', '', '057fdd13412e46df891762c01f526846', true, 1, '医学装备委员会成立文件和章程', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(3, 'test.1111psls1', '2020-06-04 21:38:59.312', '', '', 'test.1111psls1', '2020-06-04 21:38:59.312', '', '057fdd13412e46df891762c01f526846', false, 1, '年度医学装备发展规划', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(4, 'test.1111psls1', '2020-06-04 21:38:59.314', '', '', 'test.1111psls1', '2020-06-04 21:38:59.314', '', '057fdd13412e46df891762c01f526846', false, 2, '管理和技术人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(5, 'test.1111psls1', '2020-06-04 21:38:59.315', '', '', 'test.1111psls1', '2020-06-04 21:38:59.315', '', '057fdd13412e46df891762c01f526846', false, 2, '技术人员资格证书', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(6, 'test.1111psls1', '2020-06-04 21:38:59.317', '', '', 'test.1111psls1', '2020-06-04 21:38:59.317', '', '057fdd13412e46df891762c01f526846', false, 2, '大型设备维修资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(7, 'test.1111psls1', '2020-06-04 21:38:59.319', '', '', 'test.1111psls1', '2020-06-04 21:38:59.319', '', '057fdd13412e46df891762c01f526846', false, 3, '工作制度和岗位职责文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(8, 'test.1111psls1', '2020-06-04 21:38:59.323', '', '', 'test.1111psls1', '2020-06-04 21:38:59.323', '', '057fdd13412e46df891762c01f526846', false, 4, '医学装备配置标准文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(9, 'test.1111psls1', '2020-06-04 21:38:59.324', '', '', 'test.1111psls1', '2020-06-04 21:38:59.324', '', '057fdd13412e46df891762c01f526846', false, 5, '生命周期管理文件(论证、决策、购置、验收、使用、保养、维修、应用分析和更新、处置环节)', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(10, 'test.1111psls1', '2020-06-04 21:38:59.327', '', '', 'test.1111psls1', '2020-06-04 21:38:59.327', '', '057fdd13412e46df891762c01f526846', false, 6, '使用人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(11, 'test.1111psls1', '2020-06-04 21:38:59.328', '', '', 'test.1111psls1', '2020-06-04 21:38:59.328', '', '057fdd13412e46df891762c01f526846', false, 6, '相关人员资格证书文件', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(12, 'test.1111psls1', '2020-06-04 21:38:59.329', '', '', 'test.1111psls1', '2020-06-04 21:38:59.329', '', '057fdd13412e46df891762c01f526846', false, 6, '临床工程师资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(13, 'test.1111psls1', '2020-06-04 21:38:59.332', '', '', 'test.1111psls1', '2020-06-04 21:38:59.332', '', '057fdd13412e46df891762c01f526846', false, 7, '50万元及以上的医学装备可行性论证', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(14, 'test.1111psls1', '2020-06-04 21:38:59.334', '', '', 'test.1111psls1', '2020-06-04 21:38:59.334', '', '057fdd13412e46df891762c01f526846', false, 8, '固定资产台账清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(15, 'test.1111psls1', '2020-06-04 21:38:59.335', '', '', 'test.1111psls1', '2020-06-04 21:38:59.335', '', '057fdd13412e46df891762c01f526846', false, 9, '医疗设备档案管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(16, 'test.1111psls1', '2020-06-04 21:38:59.337', '', '', 'test.1111psls1', '2020-06-04 21:38:59.337', '', '057fdd13412e46df891762c01f526846', false, 9, '单价5万元以上医学装备目录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(17, 'test.1111psls1', '2020-06-04 21:38:59.338', '', '', 'test.1111psls1', '2020-06-04 21:38:59.338', '', '057fdd13412e46df891762c01f526846', false, 10, '使用评价制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(18, 'test.1111psls1', '2020-06-04 21:38:59.339', '', '', 'test.1111psls1', '2020-06-04 21:38:59.339', '', '057fdd13412e46df891762c01f526846', false, 10, '大型设备分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(19, 'test.1111psls1', '2020-06-04 21:38:59.342', '', '', 'test.1111psls1', '2020-06-04 21:38:59.342', '', '057fdd13412e46df891762c01f526846', false, 11, '风险管控制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(20, 'test.1111psls1', '2020-06-04 21:38:59.345', '', '', 'test.1111psls1', '2020-06-04 21:38:59.345', '', '057fdd13412e46df891762c01f526846', false, 11, '不良事件报告制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(21, 'test.1111psls1', '2020-06-04 21:38:59.346', '', '', 'test.1111psls1', '2020-06-04 21:38:59.346', '', '057fdd13412e46df891762c01f526846', false, 12, '计量设备管理制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(22, 'test.1111psls1', '2020-06-04 21:38:59.348', '', '', 'test.1111psls1', '2020-06-04 21:38:59.348', '', '057fdd13412e46df891762c01f526846', false, 12, '计量设备检查报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(23, 'test.1111psls1', '2020-06-04 21:38:59.35', '', '', 'test.1111psls1', '2020-06-04 21:38:59.35', '', '057fdd13412e46df891762c01f526846', false, 13, '临床操作手册上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(24, 'test.1111psls1', '2020-06-04 21:38:59.351', '', '', 'test.1111psls1', '2020-06-04 21:38:59.351', '', '057fdd13412e46df891762c01f526846', false, 13, '设备保养&维修报告上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(25, 'test.1111psls1', '2020-06-04 21:38:59.352', '', '', 'test.1111psls1', '2020-06-04 21:38:59.352', '', '057fdd13412e46df891762c01f526846', false, 14, '培训记录上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(26, 'test.1111psls1', '2020-06-04 21:38:59.353', '', '', 'test.1111psls1', '2020-06-04 21:38:59.353', '', '057fdd13412e46df891762c01f526846', false, 14, '考核记录上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(27, 'test.1111psls1', '2020-06-04 21:38:59.355', '', '', 'test.1111psls1', '2020-06-04 21:38:59.355', '', '057fdd13412e46df891762c01f526846', false, 15, '放疗与放射设备清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(28, 'test.1111psls1', '2020-06-04 21:38:59.356', '', '', 'test.1111psls1', '2020-06-04 21:38:59.356', '', '057fdd13412e46df891762c01f526846', false, 15, '辐射安全许可证', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(29, 'test.1111psls1', '2020-06-04 21:38:59.357', '', '', 'test.1111psls1', '2020-06-04 21:38:59.357', '', '057fdd13412e46df891762c01f526846', false, 15, '性能检测报告', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(30, 'test.1111psls1', '2020-06-04 21:38:59.359', '', '', 'test.1111psls1', '2020-06-04 21:38:59.359', '', '057fdd13412e46df891762c01f526846', false, 16, '机房警示标识规范与要求', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(31, 'test.1111psls1', '2020-06-04 21:38:59.359', '', '', 'test.1111psls1', '2020-06-04 21:38:59.359', '', '057fdd13412e46df891762c01f526846', false, 16, '现场照片示例', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(32, 'test.1111psls1', '2020-06-04 21:38:59.361', '', '', 'test.1111psls1', '2020-06-04 21:38:59.361', '', '057fdd13412e46df891762c01f526846', false, 17, '官方通知截图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(33, 'test.1111psls1', '2020-06-04 21:38:59.362', '', '', 'test.1111psls1', '2020-06-04 21:38:59.362', '', '057fdd13412e46df891762c01f526846', false, 17, '培训记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(34, 'test.1111psls1', '2020-06-04 21:38:59.39', '', '', 'test.1111psls1', '2020-06-04 21:38:59.39', '', '057fdd13412e46df891762c01f526846', false, 45, '年度工作总结', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(35, 'test.1111psls1', '2020-06-04 21:38:59.391', '', '', 'test.1111psls1', '2020-06-04 21:38:59.391', '', '057fdd13412e46df891762c01f526846', false, 45, '质控分数上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(36, 'test.1111psls1', '2020-06-04 21:38:59.393', '', '', 'test.1111psls1', '2020-06-04 21:38:59.393', '', '057fdd13412e46df891762c01f526846', false, 46, '医疗质量管理委员会会议纪要', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(37, 'test.1111psls1', '2020-06-04 21:38:59.394', '', '', 'test.1111psls1', '2020-06-04 21:38:59.394', '', '057fdd13412e46df891762c01f526846', false, 46, '人员考核绩效上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(38, 'test.1111psls1', '2020-06-04 21:38:59.396', '', '', 'test.1111psls1', '2020-06-04 21:38:59.396', '', '057fdd13412e46df891762c01f526846', false, 47, '设计自查表上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(39, 'test.1111psls1', '2020-06-04 21:38:59.398', '', '', 'test.1111psls1', '2020-06-04 21:38:59.398', '', '057fdd13412e46df891762c01f526846', false, 48, '财务报表审计', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(40, 'test.1111psls1', '2020-06-04 21:38:59.4', '', '', 'test.1111psls1', '2020-06-04 21:38:59.4', '', '057fdd13412e46df891762c01f526846', false, 49, '临床安全记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(41, 'test.1111psls1', '2020-06-04 21:38:59.401', '', '', 'test.1111psls1', '2020-06-04 21:38:59.401', '', '057fdd13412e46df891762c01f526846', false, 49, '不良事件记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(42, 'test.1111psls1', '2020-06-04 21:38:59.402', '', '', 'test.1111psls1', '2020-06-04 21:38:59.402', '', '057fdd13412e46df891762c01f526846', false, 50, '主管部门自查表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(43, 'test.1111psls1', '2020-06-04 21:38:59.422', '', '', 'test.1111psls1', '2020-06-04 21:38:59.422', '', '057fdd13412e46df891762c01f526846', false, 69, '设备配置现状总结文件上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(44, 'test.1111psls1', '2020-06-04 21:38:59.423', '', '', 'test.1111psls1', '2020-06-04 21:38:59.423', '', '057fdd13412e46df891762c01f526846', false, 70, '十三五规划上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(45, 'test.1111psls1', '2020-06-04 21:38:59.424', '', '', 'test.1111psls1', '2020-06-04 21:38:59.424', '', '057fdd13412e46df891762c01f526846', false, 70, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(46, 'test.1111psls1', '2020-06-04 21:38:59.426', '', '', 'test.1111psls1', '2020-06-04 21:38:59.426', '', '057fdd13412e46df891762c01f526846', false, 71, '委员会PPT材料', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(47, 'test.1111psls1', '2020-06-04 21:38:59.427', '', '', 'test.1111psls1', '2020-06-04 21:38:59.427', '', '057fdd13412e46df891762c01f526846', false, 71, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(48, 'test.1111psls1', '2020-06-04 21:38:59.429', '', '', 'test.1111psls1', '2020-06-04 21:38:59.429', '', '057fdd13412e46df891762c01f526846', false, 72, '不良事件减少比例', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(49, 'test.1111psls1', '2020-06-04 21:38:59.43', '', '', 'test.1111psls1', '2020-06-04 21:38:59.43', '', '057fdd13412e46df891762c01f526846', false, 72, '维修率', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(50, 'test.1111psls1', '2020-06-04 21:38:59.431', '', '', 'test.1111psls1', '2020-06-04 21:38:59.431', '', '057fdd13412e46df891762c01f526846', false, 72, '开机率', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(51, 'test.1111psls1', '2020-06-04 21:38:59.432', '', '', 'test.1111psls1', '2020-06-04 21:38:59.432', '', '057fdd13412e46df891762c01f526846', false, 73, '医疗器械临床使用安全管理改进措施', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(52, 'test.1111psls1', '2020-06-04 21:38:59.433', '', '', 'test.1111psls1', '2020-06-04 21:38:59.433', '', '057fdd13412e46df891762c01f526846', false, 73, '可疑医疗器械不良事件上报案例总结分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(1, 'huangjianzhen', '2020-06-04 21:47:48.888', '', '', 'huangjianzhen', '2020-06-04 21:47:48.888', '', '2a773154623311e7aeb25254001a1c14', false, 1, '管理体系文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(2, 'huangjianzhen', '2020-06-04 21:47:48.89', '', '', 'huangjianzhen', '2020-06-04 21:47:48.89', '', '2a773154623311e7aeb25254001a1c14', false, 1, '医学装备委员会成立文件和章程', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(3, 'huangjianzhen', '2020-06-04 21:47:48.89', '', '', 'huangjianzhen', '2020-06-04 21:47:48.89', '', '2a773154623311e7aeb25254001a1c14', false, 1, '年度医学装备发展规划', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(4, 'huangjianzhen', '2020-06-04 21:47:48.892', '', '', 'huangjianzhen', '2020-06-04 21:47:48.892', '', '2a773154623311e7aeb25254001a1c14', false, 2, '管理和技术人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(5, 'huangjianzhen', '2020-06-04 21:47:48.892', '', '', 'huangjianzhen', '2020-06-04 21:47:48.892', '', '2a773154623311e7aeb25254001a1c14', false, 2, '技术人员资格证书', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(6, 'huangjianzhen', '2020-06-04 21:47:48.893', '', '', 'huangjianzhen', '2020-06-04 21:47:48.893', '', '2a773154623311e7aeb25254001a1c14', false, 2, '大型设备维修资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(7, 'huangjianzhen', '2020-06-04 21:47:48.895', '', '', 'huangjianzhen', '2020-06-04 21:47:48.895', '', '2a773154623311e7aeb25254001a1c14', false, 3, '工作制度和岗位职责文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(8, 'huangjianzhen', '2020-06-04 21:47:48.897', '', '', 'huangjianzhen', '2020-06-04 21:47:48.897', '', '2a773154623311e7aeb25254001a1c14', false, 4, '医学装备配置标准文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(9, 'huangjianzhen', '2020-06-04 21:47:48.898', '', '', 'huangjianzhen', '2020-06-04 21:47:48.898', '', '2a773154623311e7aeb25254001a1c14', false, 5, '生命周期管理文件(论证、决策、购置、验收、使用、保养、维修、应用分析和更新、处置环节)', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(10, 'huangjianzhen', '2020-06-04 21:47:48.9', '', '', 'huangjianzhen', '2020-06-04 21:47:48.9', '', '2a773154623311e7aeb25254001a1c14', false, 6, '使用人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(11, 'huangjianzhen', '2020-06-04 21:47:48.901', '', '', 'huangjianzhen', '2020-06-04 21:47:48.901', '', '2a773154623311e7aeb25254001a1c14', false, 6, '相关人员资格证书文件', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(12, 'huangjianzhen', '2020-06-04 21:47:48.902', '', '', 'huangjianzhen', '2020-06-04 21:47:48.902', '', '2a773154623311e7aeb25254001a1c14', false, 6, '临床工程师资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(13, 'huangjianzhen', '2020-06-04 21:47:48.904', '', '', 'huangjianzhen', '2020-06-04 21:47:48.904', '', '2a773154623311e7aeb25254001a1c14', false, 7, '50万元及以上的医学装备可行性论证', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(14, 'huangjianzhen', '2020-06-04 21:47:48.905', '', '', 'huangjianzhen', '2020-06-04 21:47:48.905', '', '2a773154623311e7aeb25254001a1c14', false, 8, '固定资产台账清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(15, 'huangjianzhen', '2020-06-04 21:47:48.907', '', '', 'huangjianzhen', '2020-06-04 21:47:48.907', '', '2a773154623311e7aeb25254001a1c14', false, 9, '医疗设备档案管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(16, 'huangjianzhen', '2020-06-04 21:47:48.907', '', '', 'huangjianzhen', '2020-06-04 21:47:48.907', '', '2a773154623311e7aeb25254001a1c14', false, 9, '单价5万元以上医学装备目录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(17, 'huangjianzhen', '2020-06-04 21:47:48.909', '', '', 'huangjianzhen', '2020-06-04 21:47:48.909', '', '2a773154623311e7aeb25254001a1c14', false, 10, '使用评价制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(18, 'huangjianzhen', '2020-06-04 21:47:48.909', '', '', 'huangjianzhen', '2020-06-04 21:47:48.909', '', '2a773154623311e7aeb25254001a1c14', false, 10, '大型设备分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(19, 'huangjianzhen', '2020-06-04 21:47:48.911', '', '', 'huangjianzhen', '2020-06-04 21:47:48.911', '', '2a773154623311e7aeb25254001a1c14', false, 11, '风险管控制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(20, 'huangjianzhen', '2020-06-04 21:47:48.914', '', '', 'huangjianzhen', '2020-06-04 21:47:48.914', '', '2a773154623311e7aeb25254001a1c14', false, 11, '不良事件报告制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(21, 'huangjianzhen', '2020-06-04 21:47:48.915', '', '', 'huangjianzhen', '2020-06-04 21:47:48.915', '', '2a773154623311e7aeb25254001a1c14', false, 12, '计量设备管理制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(22, 'huangjianzhen', '2020-06-04 21:47:48.916', '', '', 'huangjianzhen', '2020-06-04 21:47:48.916', '', '2a773154623311e7aeb25254001a1c14', false, 12, '计量设备检查报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(23, 'huangjianzhen', '2020-06-04 21:47:48.918', '', '', 'huangjianzhen', '2020-06-04 21:47:48.918', '', '2a773154623311e7aeb25254001a1c14', false, 13, '临床操作手册上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(24, 'huangjianzhen', '2020-06-04 21:47:48.918', '', '', 'huangjianzhen', '2020-06-04 21:47:48.918', '', '2a773154623311e7aeb25254001a1c14', false, 13, '设备保养&维修报告上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(25, 'huangjianzhen', '2020-06-04 21:47:48.919', '', '', 'huangjianzhen', '2020-06-04 21:47:48.919', '', '2a773154623311e7aeb25254001a1c14', false, 14, '培训记录上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(26, 'huangjianzhen', '2020-06-04 21:47:48.92', '', '', 'huangjianzhen', '2020-06-04 21:47:48.92', '', '2a773154623311e7aeb25254001a1c14', false, 14, '考核记录上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(27, 'huangjianzhen', '2020-06-04 21:47:48.921', '', '', 'huangjianzhen', '2020-06-04 21:47:48.921', '', '2a773154623311e7aeb25254001a1c14', false, 15, '放疗与放射设备清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(28, 'huangjianzhen', '2020-06-04 21:47:48.922', '', '', 'huangjianzhen', '2020-06-04 21:47:48.922', '', '2a773154623311e7aeb25254001a1c14', false, 15, '辐射安全许可证', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(29, 'huangjianzhen', '2020-06-04 21:47:48.923', '', '', 'huangjianzhen', '2020-06-04 21:47:48.923', '', '2a773154623311e7aeb25254001a1c14', false, 15, '性能检测报告', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(30, 'huangjianzhen', '2020-06-04 21:47:48.924', '', '', 'huangjianzhen', '2020-06-04 21:47:48.924', '', '2a773154623311e7aeb25254001a1c14', false, 16, '机房警示标识规范与要求', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(31, 'huangjianzhen', '2020-06-04 21:47:48.925', '', '', 'huangjianzhen', '2020-06-04 21:47:48.925', '', '2a773154623311e7aeb25254001a1c14', false, 16, '现场照片示例', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(32, 'huangjianzhen', '2020-06-04 21:47:48.926', '', '', 'huangjianzhen', '2020-06-04 21:47:48.926', '', '2a773154623311e7aeb25254001a1c14', false, 17, '官方通知截图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(33, 'huangjianzhen', '2020-06-04 21:47:48.927', '', '', 'huangjianzhen', '2020-06-04 21:47:48.927', '', '2a773154623311e7aeb25254001a1c14', false, 17, '培训记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(34, 'huangjianzhen', '2020-06-04 21:47:48.951', '', '', 'huangjianzhen', '2020-06-04 21:47:48.951', '', '2a773154623311e7aeb25254001a1c14', false, 45, '年度工作总结', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(35, 'huangjianzhen', '2020-06-04 21:47:48.952', '', '', 'huangjianzhen', '2020-06-04 21:47:48.952', '', '2a773154623311e7aeb25254001a1c14', false, 45, '质控分数上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(36, 'huangjianzhen', '2020-06-04 21:47:48.953', '', '', 'huangjianzhen', '2020-06-04 21:47:48.953', '', '2a773154623311e7aeb25254001a1c14', false, 46, '医疗质量管理委员会会议纪要', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(37, 'huangjianzhen', '2020-06-04 21:47:48.954', '', '', 'huangjianzhen', '2020-06-04 21:47:48.954', '', '2a773154623311e7aeb25254001a1c14', false, 46, '人员考核绩效上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(38, 'huangjianzhen', '2020-06-04 21:47:48.956', '', '', 'huangjianzhen', '2020-06-04 21:47:48.956', '', '2a773154623311e7aeb25254001a1c14', false, 47, '设计自查表上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(39, 'huangjianzhen', '2020-06-04 21:47:48.957', '', '', 'huangjianzhen', '2020-06-04 21:47:48.957', '', '2a773154623311e7aeb25254001a1c14', false, 48, '财务报表审计', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(40, 'huangjianzhen', '2020-06-04 21:47:48.959', '', '', 'huangjianzhen', '2020-06-04 21:47:48.959', '', '2a773154623311e7aeb25254001a1c14', false, 49, '临床安全记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(41, 'huangjianzhen', '2020-06-04 21:47:48.96', '', '', 'huangjianzhen', '2020-06-04 21:47:48.96', '', '2a773154623311e7aeb25254001a1c14', false, 49, '不良事件记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(42, 'huangjianzhen', '2020-06-04 21:47:48.961', '', '', 'huangjianzhen', '2020-06-04 21:47:48.961', '', '2a773154623311e7aeb25254001a1c14', false, 50, '主管部门自查表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(43, 'huangjianzhen', '2020-06-04 21:47:48.977', '', '', 'huangjianzhen', '2020-06-04 21:47:48.977', '', '2a773154623311e7aeb25254001a1c14', false, 69, '设备配置现状总结文件上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(44, 'huangjianzhen', '2020-06-04 21:47:48.978', '', '', 'huangjianzhen', '2020-06-04 21:47:48.978', '', '2a773154623311e7aeb25254001a1c14', false, 70, '十三五规划上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(45, 'huangjianzhen', '2020-06-04 21:47:48.979', '', '', 'huangjianzhen', '2020-06-04 21:47:48.979', '', '2a773154623311e7aeb25254001a1c14', false, 70, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(46, 'huangjianzhen', '2020-06-04 21:47:48.98', '', '', 'huangjianzhen', '2020-06-04 21:47:48.98', '', '2a773154623311e7aeb25254001a1c14', false, 71, '委员会PPT材料', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(47, 'huangjianzhen', '2020-06-04 21:47:48.981', '', '', 'huangjianzhen', '2020-06-04 21:47:48.981', '', '2a773154623311e7aeb25254001a1c14', false, 71, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(48, 'huangjianzhen', '2020-06-04 21:47:48.983', '', '', 'huangjianzhen', '2020-06-04 21:47:48.983', '', '2a773154623311e7aeb25254001a1c14', false, 72, '不良事件减少比例', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(49, 'huangjianzhen', '2020-06-04 21:47:48.983', '', '', 'huangjianzhen', '2020-06-04 21:47:48.983', '', '2a773154623311e7aeb25254001a1c14', false, 72, '维修率', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(50, 'huangjianzhen', '2020-06-04 21:47:48.984', '', '', 'huangjianzhen', '2020-06-04 21:47:48.984', '', '2a773154623311e7aeb25254001a1c14', false, 72, '开机率', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(51, 'huangjianzhen', '2020-06-04 21:47:48.985', '', '', 'huangjianzhen', '2020-06-04 21:47:48.985', '', '2a773154623311e7aeb25254001a1c14', false, 73, '医疗器械临床使用安全管理改进措施', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(52, 'huangjianzhen', '2020-06-04 21:47:48.986', '', '', 'huangjianzhen', '2020-06-04 21:47:48.986', '', '2a773154623311e7aeb25254001a1c14', false, 73, '可疑医疗器械不良事件上报案例总结分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(117, 'test.1111psls1', '2020-06-15 16:18:35.113', '', '', 'test.1111psls1', '2020-06-15 16:18:35.113', '', '057fdd13412e46df891762c01f526846', false, 1, '测试项目', 4, '', false);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(53, 'pszipeng', '2020-06-16 16:40:24.771', '', '', 'pszipeng', '2020-06-16 16:40:24.771', '', '057fdd13412e46df891762c01f526846', true, 23, '计量设备管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(54, 'pszipeng', '2020-06-16 16:41:48.786', '', '', 'pszipeng', '2020-06-16 16:41:48.786', '', '057fdd13412e46df891762c01f526846', false, 26, '计量器具强制检定制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(55, 'pszipeng', '2020-06-16 16:42:28.053', '', '', 'pszipeng', '2020-06-16 16:42:28.053', '', '057fdd13412e46df891762c01f526846', false, 24, '计量器具强制检定目录及检定周期表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(56, 'pszipeng', '2020-06-16 16:42:49.531', '', '', 'pszipeng', '2020-06-16 16:42:49.531', '', '057fdd13412e46df891762c01f526846', false, 24, '计量设备检测流程', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(57, 'pszipeng', '2020-06-16 16:45:56.591', '', '', 'pszipeng', '2020-06-16 16:45:56.591', '', '057fdd13412e46df891762c01f526846', false, 31, '常用考试考核模板', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(58, 'pszipeng', '2020-06-16 16:48:35.574', '', '', 'pszipeng', '2020-06-16 16:48:35.574', '', '057fdd13412e46df891762c01f526846', false, 29, '医疗器械临床使用安全控制与风险管理应急预案', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(59, 'pszipeng', '2020-06-16 16:49:32.68', '', '', 'pszipeng', '2020-06-16 16:49:32.68', '', '057fdd13412e46df891762c01f526846', false, 33, '医疗设备三级保养体系', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(60, 'pszipeng', '2020-06-16 16:51:56.176', '', '', 'pszipeng', '2020-06-16 16:51:56.176', '', '057fdd13412e46df891762c01f526846', true, 35, '医疗设备应急预案', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(61, 'pszipeng', '2020-06-16 16:52:28.545', '', '', 'pszipeng', '2020-06-16 16:52:28.545', '', '057fdd13412e46df891762c01f526846', true, 35, '医疗设备应急管理程序', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(62, 'pszipeng', '2020-06-16 16:53:51.104', '', '', 'pszipeng', '2020-06-16 16:53:51.104', '', '057fdd13412e46df891762c01f526846', false, 38, '医疗设备应急替代工作流程', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(63, 'psliwei', '2020-06-17 16:26:23.534', '', '', 'psliwei', '2020-06-17 16:26:23.534', '', '057fdd13412e46df891762c01f526846', false, 27, '医疗器械操作培训和考核制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(64, 'psliwei', '2020-06-17 16:26:34.427', '', '', 'psliwei', '2020-06-17 16:26:34.427', '', '057fdd13412e46df891762c01f526846', false, 27, '设备年度培训计划表（含使用科室计划）', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(65, 'psliwei', '2020-06-17 16:26:46.606', '', '', 'psliwei', '2020-06-17 16:26:46.606', '', '057fdd13412e46df891762c01f526846', false, 27, '抢救物品培训考核及计划（结合护理部）', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(66, 'psliwei', '2020-06-17 16:27:00.101', '', '', 'psliwei', '2020-06-17 16:27:00.101', '', '057fdd13412e46df891762c01f526846', false, 27, '抢救设备及物品清单', 4, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(67, 'psliwei', '2020-06-17 16:27:24.558', '', '', 'psliwei', '2020-06-17 16:27:24.558', '', '057fdd13412e46df891762c01f526846', false, 29, '培训课件含意外情况处理预案及措施', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(68, 'psliwei', '2020-06-17 16:27:40.056', '', '', 'psliwei', '2020-06-17 16:27:40.056', '', '057fdd13412e46df891762c01f526846', false, 30, '操作手册管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(69, 'psliwei', '2020-06-17 16:27:51.169', '', '', 'psliwei', '2020-06-17 16:27:51.169', '', '057fdd13412e46df891762c01f526846', false, 30, '操作手册目录清单（5万元及急救设备）', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(70, 'psliwei', '2020-06-17 16:28:06.244', '', '', 'psliwei', '2020-06-17 16:28:06.244', '', '057fdd13412e46df891762c01f526846', false, 31, '医学装备培训考核表', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(71, 'psliwei', '2020-06-17 16:28:38.37', '', '', 'psliwei', '2020-06-17 16:28:38.37', '', '057fdd13412e46df891762c01f526846', false, 59, '使用科室自查表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(72, 'psliwei', '2020-06-17 16:28:50.846', '', '', 'psliwei', '2020-06-17 16:28:50.846', '', '057fdd13412e46df891762c01f526846', false, 60, '装备管理部门督查表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(73, 'psliwei', '2020-06-17 16:29:00.702', '', '', 'psliwei', '2020-06-17 16:29:00.702', '', '057fdd13412e46df891762c01f526846', false, 60, '装备管理部门对全院培训情况分析、反馈', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(74, 'psliwei', '2020-06-17 16:29:28.324', '', '', 'psliwei', '2020-06-17 16:29:28.324', '', '057fdd13412e46df891762c01f526846', false, 78, '数据或实例显示,持续改进有成效', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(75, 'psliwei', '2020-06-17 16:45:18.008', '', '', 'psliwei', '2020-06-17 16:45:18.008', '', '057fdd13412e46df891762c01f526846', false, 33, '医学装备管理部门有对医学装备保养、维修制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(76, 'psliwei', '2020-06-17 16:59:30.608', '', '', 'psliwei', '2020-06-17 16:59:30.608', '', '057fdd13412e46df891762c01f526846', false, 33, '使用登记本', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(77, 'psliwei', '2020-06-17 17:08:08.76', '', '', 'psliwei', '2020-06-17 17:08:08.76', '', '057fdd13412e46df891762c01f526846', false, 33, '使用培训包含日常维护保养内容', 4, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(78, 'psliwei', '2020-06-17 17:08:22.447', '', '', 'psliwei', '2020-06-17 17:08:22.447', '', '057fdd13412e46df891762c01f526846', false, 34, '维护保养记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(79, 'psliwei', '2020-06-17 17:08:47.327', '', '', 'psliwei', '2020-06-17 17:08:47.327', '', '057fdd13412e46df891762c01f526846', false, 61, '维修情况分析报告', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(80, 'psliwei', '2020-06-17 17:08:57.061', '', '', 'psliwei', '2020-06-17 17:08:57.061', '', '057fdd13412e46df891762c01f526846', false, 62, '委员会PPT材料及会议纪要含上述内容', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(81, 'psliwei', '2020-06-17 17:09:15.278', '', '', 'psliwei', '2020-06-17 17:09:15.278', '', '057fdd13412e46df891762c01f526846', false, 79, '数据或实例显示,持续改进有成效', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(82, 'psliwei', '2020-06-17 17:17:53.849', '', '', 'psliwei', '2020-06-17 17:17:53.849', '', '057fdd13412e46df891762c01f526846', false, 35, '急救类,生命支持类医学装备应急预案', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(83, 'psliwei', '2020-06-17 17:18:06.414', '', '', 'psliwei', '2020-06-17 17:18:06.414', '', '057fdd13412e46df891762c01f526846', false, 35, '急救类,生命支持类医学装备清单', 4, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(84, 'psliwei', '2020-06-17 17:18:20.953', '', '', 'psliwei', '2020-06-17 17:18:20.953', '', '057fdd13412e46df891762c01f526846', false, 36, '急救生命支持类设备记录(台账,日检记录,交接班记录,保障方案)(各科室保存)', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(85, 'psliwei', '2020-06-17 17:42:19.552', '', '', 'psliwei', '2020-06-17 17:42:19.552', '', '057fdd13412e46df891762c01f526846', false, 80, '用实例或数据说明,对急救类,生命支持类装备管理的改进措施落实,效果分析有效', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(86, 'psliwei', '2020-06-17 17:43:00.217', '', '', 'psliwei', '2020-06-17 17:43:00.217', '', '057fdd13412e46df891762c01f526846', false, 37, '装备故障时有紧急替代流程', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(87, 'psliwei', '2020-06-17 17:43:12.171', '', '', 'psliwei', '2020-06-17 17:43:12.171', '', '057fdd13412e46df891762c01f526846', false, 38, '医学装备应急调配制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(88, 'psliwei', '2020-06-17 17:43:23.765', '', '', 'psliwei', '2020-06-17 17:43:23.765', '', '057fdd13412e46df891762c01f526846', false, 39, '应知应会内容,有培训内容和考试记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(89, 'psliwei', '2020-06-17 17:43:46.879', '', '', 'psliwei', '2020-06-17 17:43:46.879', '', '057fdd13412e46df891762c01f526846', false, 64, '医学装备部门装备应急调配演练和总结记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(90, 'psliwei', '2020-06-17 17:44:29.455', '', '', 'psliwei', '2020-06-17 17:44:29.455', '', '057fdd13412e46df891762c01f526846', false, 81, '医学装备部门装备针对应急调配演练和监管中存在的问题制定整改措施和落实记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(91, 'psliwei', '2020-06-17 17:44:39.714', '', '', 'psliwei', '2020-06-17 17:44:39.714', '', '057fdd13412e46df891762c01f526846', false, 81, '应急演练及监管问题整改报告(PDCA整改报告)；全院保障装备应急调配机制实施总结报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(92, 'psliwei', '2020-06-17 17:44:58.535', '', '', 'psliwei', '2020-06-17 17:44:58.535', '', '057fdd13412e46df891762c01f526846', false, 40, '医工培训管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(93, 'psliwei', '2020-06-17 17:45:05.83', '', '', 'psliwei', '2020-06-17 17:45:05.83', '', '057fdd13412e46df891762c01f526846', false, 40, '医工档案（通知、资料、学分证等）', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(94, 'psliwei', '2020-06-17 17:45:11.499', '', '', 'psliwei', '2020-06-17 17:45:11.499', '', '057fdd13412e46df891762c01f526846', false, 40, '医工内部培训计划表', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(95, 'psliwei', '2020-06-17 17:45:19.27', '', '', 'psliwei', '2020-06-17 17:45:19.27', '', '057fdd13412e46df891762c01f526846', false, 40, '培训课件、记录、考核资料', 4, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(96, 'psliwei', '2020-06-17 17:45:34.483', '', '', 'psliwei', '2020-06-17 17:45:34.483', '', '057fdd13412e46df891762c01f526846', false, 41, '全院医学装备发展规划、采购计划的完成、管理和使用情况分析', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(97, 'psliwei', '2020-06-17 17:45:42.379', '', '', 'psliwei', '2020-06-17 17:45:42.379', '', '057fdd13412e46df891762c01f526846', false, 41, '医学装备维修、报废、巡检等方面的问题、分析、总结报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(98, 'psliwei', '2020-06-17 17:46:00.059', '', '', 'psliwei', '2020-06-17 17:46:00.059', '', '057fdd13412e46df891762c01f526846', false, 65, '1.医学装备管理部门对培训、采购、维修等业务的自查表（一张表）', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(99, 'psliwei', '2020-06-17 17:46:13.632', '', '', 'psliwei', '2020-06-17 17:46:13.632', '', '057fdd13412e46df891762c01f526846', false, 66, '巡检记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(100, 'psliwei', '2020-06-17 17:46:24.73', '', '', 'psliwei', '2020-06-17 17:46:24.73', '', '057fdd13412e46df891762c01f526846', false, 66, '巡检总结分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(101, 'psliwei', '2020-06-17 17:46:37.874', '', '', 'psliwei', '2020-06-17 17:46:37.874', '', '057fdd13412e46df891762c01f526846', false, 82, '设备相关满意度汇总（数据或实例显示,持续改进有成效）', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(102, 'psliwei', '2020-06-17 17:47:00.212', '', '', 'psliwei', '2020-06-17 17:47:00.212', '', '057fdd13412e46df891762c01f526846', false, 42, '建立科室自查表，医学装备管理部门定期督查执行情况（督查表）', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(103, 'psliwei', '2020-06-17 17:47:06.74', '', '', 'psliwei', '2020-06-17 17:47:06.74', '', '057fdd13412e46df891762c01f526846', false, 42, '兼职管理人员名单', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(104, 'psliwei', '2020-06-17 17:47:15.118', '', '', 'psliwei', '2020-06-17 17:47:15.118', '', '057fdd13412e46df891762c01f526846', false, 43, '日常使用管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(105, 'psliwei', '2020-06-17 17:47:23.507', '', '', 'psliwei', '2020-06-17 17:47:23.507', '', '057fdd13412e46df891762c01f526846', false, 43, '应急管理制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(106, 'psliwei', '2020-06-17 17:48:36.536', '', '', 'psliwei', '2020-06-17 17:48:36.536', '', '057fdd13412e46df891762c01f526846', false, 44, '医疗装备的使用培训考核制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(107, 'psliwei', '2020-06-17 17:48:42.532', '', '', 'psliwei', '2020-06-17 17:48:42.532', '', '057fdd13412e46df891762c01f526846', false, 44, '培训考核记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(108, 'psliwei', '2020-06-17 17:49:23.728', '', '', 'psliwei', '2020-06-17 17:49:23.728', '', '057fdd13412e46df891762c01f526846', false, 67, '科室自查表包含维护保养内容', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(109, 'psliwei', '2020-06-17 17:49:32.291', '', '', 'psliwei', '2020-06-17 17:49:32.291', '', '057fdd13412e46df891762c01f526846', false, 68, '医学装备管理部门督查表（一张表）', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(110, 'psliwei', '2020-06-17 17:49:42.608', '', '', 'psliwei', '2020-06-17 17:49:42.608', '', '057fdd13412e46df891762c01f526846', false, 83, '数据或实例显示,持续改进有成效', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(111, 'psliwei', '2020-06-17 17:55:35.21', '', '', 'psliwei', '2020-06-17 17:55:35.21', '', '057fdd13412e46df891762c01f526846', false, 80, '数据或实例显示,持续改进有成效', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(112, 'pszipeng', '2020-06-18 17:11:59.897', '', '', 'pszipeng', '2020-06-18 17:11:59.897', '', '057fdd13412e46df891762c01f526846', false, 71, '全院医疗设备管理持续改进分析', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(113, 'pszipeng', '2020-06-18 17:48:25.847', '', '', 'pszipeng', '2020-06-18 17:48:25.847', '', '057fdd13412e46df891762c01f526846', false, 9, '医学装备分类管理目录', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(114, 'pszipeng', '2020-06-18 17:53:16.962', '', '', 'pszipeng', '2020-06-18 17:53:16.962', '', '057fdd13412e46df891762c01f526846', false, 24, '计量管理模板', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(115, 'pszipeng', '2020-06-18 18:04:21.277', '', '', 'pszipeng', '2020-06-18 18:04:21.277', '', '057fdd13412e46df891762c01f526846', false, 32, '医疗器械使用质量监督管理办法', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(116, 'pszipeng', '2020-06-18 18:07:14.093', '', '', 'pszipeng', '2020-06-18 18:07:14.093', '', '057fdd13412e46df891762c01f526846', false, 32, '医学装备部门工作制度中关于保障装备使用的管理制度与规范', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(1, 'Demonstration', '2020-06-24 19:09:28.127', '', '', 'Demonstration', '2020-06-24 19:09:28.127', '', '2a773b22623311e7aeb25254001a1c14', false, 1, '管理体系文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(2, 'Demonstration', '2020-06-24 19:09:28.137', '', '', 'Demonstration', '2020-06-24 19:09:28.137', '', '2a773b22623311e7aeb25254001a1c14', false, 1, '医学装备委员会成立文件和章程', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(3, 'Demonstration', '2020-06-24 19:09:28.138', '', '', 'Demonstration', '2020-06-24 19:09:28.138', '', '2a773b22623311e7aeb25254001a1c14', false, 1, '年度医学装备发展规划', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(4, 'Demonstration', '2020-06-24 19:09:28.14', '', '', 'Demonstration', '2020-06-24 19:09:28.14', '', '2a773b22623311e7aeb25254001a1c14', false, 2, '管理和技术人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(5, 'Demonstration', '2020-06-24 19:09:28.141', '', '', 'Demonstration', '2020-06-24 19:09:28.141', '', '2a773b22623311e7aeb25254001a1c14', false, 2, '技术人员资格证书', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(6, 'Demonstration', '2020-06-24 19:09:28.142', '', '', 'Demonstration', '2020-06-24 19:09:28.142', '', '2a773b22623311e7aeb25254001a1c14', false, 2, '大型设备维修资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(7, 'Demonstration', '2020-06-24 19:09:28.144', '', '', 'Demonstration', '2020-06-24 19:09:28.144', '', '2a773b22623311e7aeb25254001a1c14', false, 3, '工作制度和岗位职责文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(8, 'Demonstration', '2020-06-24 19:09:28.147', '', '', 'Demonstration', '2020-06-24 19:09:28.147', '', '2a773b22623311e7aeb25254001a1c14', false, 4, '医学装备配置标准文件', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(9, 'Demonstration', '2020-06-24 19:09:28.149', '', '', 'Demonstration', '2020-06-24 19:09:28.149', '', '2a773b22623311e7aeb25254001a1c14', false, 5, '生命周期管理文件(论证、决策、购置、验收、使用、保养、维修、应用分析和更新、处置环节)', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(10, 'Demonstration', '2020-06-24 19:09:28.152', '', '', 'Demonstration', '2020-06-24 19:09:28.152', '', '2a773b22623311e7aeb25254001a1c14', false, 6, '使用人员架构图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(11, 'Demonstration', '2020-06-24 19:09:28.152', '', '', 'Demonstration', '2020-06-24 19:09:28.152', '', '2a773b22623311e7aeb25254001a1c14', false, 6, '相关人员资格证书文件', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(12, 'Demonstration', '2020-06-24 19:09:28.153', '', '', 'Demonstration', '2020-06-24 19:09:28.153', '', '2a773b22623311e7aeb25254001a1c14', false, 6, '临床工程师资质证书', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(13, 'Demonstration', '2020-06-24 19:09:28.155', '', '', 'Demonstration', '2020-06-24 19:09:28.155', '', '2a773b22623311e7aeb25254001a1c14', false, 7, '50万元及以上的医学装备可行性论证', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(14, 'Demonstration', '2020-06-24 19:09:28.157', '', '', 'Demonstration', '2020-06-24 19:09:28.157', '', '2a773b22623311e7aeb25254001a1c14', false, 8, '固定资产台账清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(15, 'Demonstration', '2020-06-24 19:09:28.159', '', '', 'Demonstration', '2020-06-24 19:09:28.159', '', '2a773b22623311e7aeb25254001a1c14', false, 9, '医疗设备档案管理制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(16, 'Demonstration', '2020-06-24 19:09:28.16', '', '', 'Demonstration', '2020-06-24 19:09:28.16', '', '2a773b22623311e7aeb25254001a1c14', false, 9, '单价5万元以上医学装备目录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(17, 'Demonstration', '2020-06-24 19:09:28.161', '', '', 'Demonstration', '2020-06-24 19:09:28.161', '', '2a773b22623311e7aeb25254001a1c14', false, 10, '使用评价制度', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(18, 'Demonstration', '2020-06-24 19:09:28.162', '', '', 'Demonstration', '2020-06-24 19:09:28.162', '', '2a773b22623311e7aeb25254001a1c14', false, 10, '大型设备分析报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(19, 'Demonstration', '2020-06-24 19:09:28.165', '', '', 'Demonstration', '2020-06-24 19:09:28.165', '', '2a773b22623311e7aeb25254001a1c14', false, 11, '风险管控制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(20, 'Demonstration', '2020-06-24 19:09:28.167', '', '', 'Demonstration', '2020-06-24 19:09:28.167', '', '2a773b22623311e7aeb25254001a1c14', false, 11, '不良事件报告制度', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(21, 'Demonstration', '2020-06-24 19:09:28.168', '', '', 'Demonstration', '2020-06-24 19:09:28.168', '', '2a773b22623311e7aeb25254001a1c14', false, 12, '计量设备管理制度上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(22, 'Demonstration', '2020-06-24 19:09:28.17', '', '', 'Demonstration', '2020-06-24 19:09:28.17', '', '2a773b22623311e7aeb25254001a1c14', false, 12, '计量设备检查报告', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(23, 'Demonstration', '2020-06-24 19:09:28.172', '', '', 'Demonstration', '2020-06-24 19:09:28.172', '', '2a773b22623311e7aeb25254001a1c14', false, 13, '临床操作手册上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(24, 'Demonstration', '2020-06-24 19:09:28.172', '', '', 'Demonstration', '2020-06-24 19:09:28.172', '', '2a773b22623311e7aeb25254001a1c14', false, 13, '设备保养&维修报告上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(25, 'Demonstration', '2020-06-24 19:09:28.174', '', '', 'Demonstration', '2020-06-24 19:09:28.174', '', '2a773b22623311e7aeb25254001a1c14', false, 14, '培训记录上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(26, 'Demonstration', '2020-06-24 19:09:28.175', '', '', 'Demonstration', '2020-06-24 19:09:28.175', '', '2a773b22623311e7aeb25254001a1c14', false, 14, '考核记录上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(27, 'Demonstration', '2020-06-24 19:09:28.177', '', '', 'Demonstration', '2020-06-24 19:09:28.177', '', '2a773b22623311e7aeb25254001a1c14', false, 15, '放疗与放射设备清单', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(28, 'Demonstration', '2020-06-24 19:09:28.177', '', '', 'Demonstration', '2020-06-24 19:09:28.177', '', '2a773b22623311e7aeb25254001a1c14', false, 15, '辐射安全许可证', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(29, 'Demonstration', '2020-06-24 19:09:28.178', '', '', 'Demonstration', '2020-06-24 19:09:28.178', '', '2a773b22623311e7aeb25254001a1c14', false, 15, '性能检测报告', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(30, 'Demonstration', '2020-06-24 19:09:28.18', '', '', 'Demonstration', '2020-06-24 19:09:28.18', '', '2a773b22623311e7aeb25254001a1c14', false, 16, '机房警示标识规范与要求', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(31, 'Demonstration', '2020-06-24 19:09:28.181', '', '', 'Demonstration', '2020-06-24 19:09:28.181', '', '2a773b22623311e7aeb25254001a1c14', false, 16, '现场照片示例', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(32, 'Demonstration', '2020-06-24 19:09:28.182', '', '', 'Demonstration', '2020-06-24 19:09:28.182', '', '2a773b22623311e7aeb25254001a1c14', false, 17, '官方通知截图', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(33, 'Demonstration', '2020-06-24 19:09:28.183', '', '', 'Demonstration', '2020-06-24 19:09:28.183', '', '2a773b22623311e7aeb25254001a1c14', false, 17, '培训记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(34, 'Demonstration', '2020-06-24 19:09:28.208', '', '', 'Demonstration', '2020-06-24 19:09:28.208', '', '2a773b22623311e7aeb25254001a1c14', false, 45, '年度工作总结', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(35, 'Demonstration', '2020-06-24 19:09:28.209', '', '', 'Demonstration', '2020-06-24 19:09:28.209', '', '2a773b22623311e7aeb25254001a1c14', false, 45, '质控分数上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(36, 'Demonstration', '2020-06-24 19:09:28.21', '', '', 'Demonstration', '2020-06-24 19:09:28.21', '', '2a773b22623311e7aeb25254001a1c14', false, 46, '医疗质量管理委员会会议纪要', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(37, 'Demonstration', '2020-06-24 19:09:28.211', '', '', 'Demonstration', '2020-06-24 19:09:28.211', '', '2a773b22623311e7aeb25254001a1c14', false, 46, '人员考核绩效上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(38, 'Demonstration', '2020-06-24 19:09:28.213', '', '', 'Demonstration', '2020-06-24 19:09:28.213', '', '2a773b22623311e7aeb25254001a1c14', false, 47, '设计自查表上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(39, 'Demonstration', '2020-06-24 19:09:28.214', '', '', 'Demonstration', '2020-06-24 19:09:28.214', '', '2a773b22623311e7aeb25254001a1c14', false, 48, '财务报表审计', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(40, 'Demonstration', '2020-06-24 19:09:28.216', '', '', 'Demonstration', '2020-06-24 19:09:28.216', '', '2a773b22623311e7aeb25254001a1c14', false, 49, '临床安全记录', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(41, 'Demonstration', '2020-06-24 19:09:28.217', '', '', 'Demonstration', '2020-06-24 19:09:28.217', '', '2a773b22623311e7aeb25254001a1c14', false, 49, '不良事件记录', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(42, 'Demonstration', '2020-06-24 19:09:28.218', '', '', 'Demonstration', '2020-06-24 19:09:28.218', '', '2a773b22623311e7aeb25254001a1c14', false, 50, '主管部门自查表', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(43, 'Demonstration', '2020-06-24 19:09:28.237', '', '', 'Demonstration', '2020-06-24 19:09:28.237', '', '2a773b22623311e7aeb25254001a1c14', false, 69, '设备配置现状总结文件上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(44, 'Demonstration', '2020-06-24 19:09:28.238', '', '', 'Demonstration', '2020-06-24 19:09:28.238', '', '2a773b22623311e7aeb25254001a1c14', false, 70, '十三五规划上传', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(45, 'Demonstration', '2020-06-24 19:09:28.238', '', '', 'Demonstration', '2020-06-24 19:09:28.238', '', '2a773b22623311e7aeb25254001a1c14', false, 70, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(46, 'Demonstration', '2020-06-24 19:09:28.241', '', '', 'Demonstration', '2020-06-24 19:09:28.241', '', '2a773b22623311e7aeb25254001a1c14', false, 71, '委员会PPT材料', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(47, 'Demonstration', '2020-06-24 19:09:28.241', '', '', 'Demonstration', '2020-06-24 19:09:28.241', '', '2a773b22623311e7aeb25254001a1c14', false, 71, '实际案例上传', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(48, 'Demonstration', '2020-06-24 19:09:28.243', '', '', 'Demonstration', '2020-06-24 19:09:28.243', '', '2a773b22623311e7aeb25254001a1c14', false, 72, '不良事件减少比例', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(49, 'Demonstration', '2020-06-24 19:09:28.244', '', '', 'Demonstration', '2020-06-24 19:09:28.244', '', '2a773b22623311e7aeb25254001a1c14', false, 72, '维修率', 2, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(50, 'Demonstration', '2020-06-24 19:09:28.245', '', '', 'Demonstration', '2020-06-24 19:09:28.245', '', '2a773b22623311e7aeb25254001a1c14', false, 72, '开机率', 3, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(51, 'Demonstration', '2020-06-24 19:09:28.246', '', '', 'Demonstration', '2020-06-24 19:09:28.246', '', '2a773b22623311e7aeb25254001a1c14', false, 73, '医疗器械临床使用安全管理改进措施', 1, '', true);
-- INSERT INTO public.audit_item
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item)
-- VALUES(52, 'Demonstration', '2020-06-24 19:09:28.247', '', '', 'Demonstration', '2020-06-24 19:09:28.247', '', '2a773b22623311e7aeb25254001a1c14', false, 73, '可疑医疗器械不良事件上报案例总结分析报告', 2, '', true);
--
--
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(1, 'test.1111psls1', '2020-06-04 21:38:59.32', '', '', 'test.1111psls1', '2020-06-04 21:38:59.32', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88275e3a5402f9038046a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '1', '资产管理处制度总则.docx', 'system', '2020-06-04 21:38:59.149', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88275e3a5402f9038046a');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(2, 'test.1111psls1', '2020-06-04 21:38:59.32', '', '', 'test.1111psls1', '2020-06-04 21:38:59.32', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ecf9a136c470ebbc7d7c996', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '7', '岗位职责以及各种流程图.docx', 'system', '2020-06-04 21:38:59.149', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a136c470ebbc7d7c996');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(3, 'test.1111psls1', '2020-06-04 21:38:59.325', '', '', 'test.1111psls1', '2020-06-04 21:38:59.325', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ecf99d66c470ebbc7d7c992', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '9', '医疗装备维护与保养制度.docx', 'system', '2020-06-04 21:38:59.153', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99d66c470ebbc7d7c992');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(4, 'test.1111psls1', '2020-06-04 21:38:59.343', '', '', 'test.1111psls1', '2020-06-04 21:38:59.343', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ecf99f7827fb9f2fa202506', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-06-04 21:38:59.185', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99f7827fb9f2fa202506');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(5, 'test.1111psls1', '2020-06-04 21:38:59.344', '', '', 'test.1111psls1', '2020-06-04 21:38:59.344', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ecf9a07827fb9f2fa202508', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '医疗装备应急程序.docx', 'system', '2020-06-04 21:38:59.185', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a07827fb9f2fa202508');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(6, 'test.1111psls1', '2020-06-04 21:38:59.347', '', '', 'test.1111psls1', '2020-06-04 21:38:59.347', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ecf99e56c470ebbc7d7c994', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '21', '计量装备管理制度.docx', 'system', '2020-06-04 21:38:59.187', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99e56c470ebbc7d7c994');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(1, 'huangjianzhen', '2020-06-04 21:47:48.889', '', '', 'huangjianzhen', '2020-06-04 21:47:48.889', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ee88275e3a5402f9038046a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '1', '资产管理处制度总则.docx', 'system', '2020-06-04 21:47:48.756', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88275e3a5402f9038046a');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(2, 'huangjianzhen', '2020-06-04 21:47:48.895', '', '', 'huangjianzhen', '2020-06-04 21:47:48.895', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ecf9a136c470ebbc7d7c996', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '7', '岗位职责以及各种流程图.docx', 'system', '2020-06-04 21:47:48.76', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a136c470ebbc7d7c996');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(3, 'huangjianzhen', '2020-06-04 21:47:48.899', '', '', 'huangjianzhen', '2020-06-04 21:47:48.899', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ecf99d66c470ebbc7d7c992', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '9', '医疗装备维护与保养制度.docx', 'system', '2020-06-04 21:47:48.763', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99d66c470ebbc7d7c992');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(4, 'huangjianzhen', '2020-06-04 21:47:48.912', '', '', 'huangjianzhen', '2020-06-04 21:47:48.912', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ecf99f7827fb9f2fa202506', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-06-04 21:47:48.783', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99f7827fb9f2fa202506');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(5, 'huangjianzhen', '2020-06-04 21:47:48.913', '', '', 'huangjianzhen', '2020-06-04 21:47:48.913', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ecf9a07827fb9f2fa202508', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '医疗装备应急程序.docx', 'system', '2020-06-04 21:47:48.783', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a07827fb9f2fa202508');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(6, 'huangjianzhen', '2020-06-04 21:47:48.916', '', '', 'huangjianzhen', '2020-06-04 21:47:48.916', '', '2a773154623311e7aeb25254001a1c14', true, false, false, '5ecf99e56c470ebbc7d7c994', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '21', '计量装备管理制度.docx', 'system', '2020-06-04 21:47:48.785', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99e56c470ebbc7d7c994');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(28, 'pszipeng', '2020-06-09 16:49:02.038', '', '', 'pszipeng', '2020-06-09 16:49:02.038', '', '057fdd13412e46df891762c01f526846', true, false, false, '5edf4cfde3a54072d1c74364', 'application/pdf', false, '34', '2020报销新规.pdf', 'pszipeng', '2020-06-09 16:49:01.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5edf4cfde3a54072d1c74364');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(7, 'pszipeng', '2020-06-16 16:31:48.553', '', '', 'pszipeng', '2020-06-16 16:31:48.553', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88374e3a5402f9038046e', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '4', '岗位职责.docx', 'pszipeng', '2020-06-16 16:31:48.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88374e3a5402f9038046e');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(8, 'pszipeng', '2020-06-16 16:34:08.544', '', '', 'pszipeng', '2020-06-16 16:34:08.544', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88400e3a5402f90380472', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '13', '医疗设备论证流程.docx', 'pszipeng', '2020-06-16 16:34:08.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88400e3a5402f90380472');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(9, 'pszipeng', '2020-06-16 16:35:28.81', '', '', 'pszipeng', '2020-06-16 16:35:28.81', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88450e3a5402f90380474', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '14', '固定资产台账清单.xlsx', 'pszipeng', '2020-06-16 16:35:28.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88450e3a5402f90380474');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(10, 'pszipeng', '2020-06-16 16:36:18.749', '', '', 'pszipeng', '2020-06-16 16:36:18.749', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88482e3a5402f90380478', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '15', '医疗器械临床使用安全控制与风险管理流程.docx', 'pszipeng', '2020-06-16 16:36:18.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88482e3a5402f90380478');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(11, 'pszipeng', '2020-06-16 16:38:28.963', '', '', 'pszipeng', '2020-06-16 16:38:28.963', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88504e3a5402f90380480', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '24', '医疗设备保养、校验制度.docx', 'pszipeng', '2020-06-16 16:38:28.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88504e3a5402f90380480');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(12, 'pszipeng', '2020-06-16 16:39:16.881', '', '', 'pszipeng', '2020-06-16 16:39:16.881', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88533e3a5402f90380482', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '25', '医学装备继续教育考核制度.docx', 'pszipeng', '2020-06-16 16:39:15.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88533e3a5402f90380482');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(13, 'pszipeng', '2020-06-16 16:40:42.159', '', '', 'pszipeng', '2020-06-16 16:40:42.159', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee8858ae3a5402f90380484', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '53', '计量器具管理制度.docx', 'pszipeng', '2020-06-16 16:40:42.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee8858ae3a5402f90380484');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(14, 'pszipeng', '2020-06-16 16:42:01.917', '', '', 'pszipeng', '2020-06-16 16:42:01.917', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee885d9e3a5402f90380486', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '54', '计量器具强制检定制度.docx', 'pszipeng', '2020-06-16 16:42:01.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee885d9e3a5402f90380486');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(15, 'pszipeng', '2020-06-16 16:42:34.904', '', '', 'pszipeng', '2020-06-16 16:42:34.904', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee885fae3a5402f90380488', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '55', '计量器具强制检定目录及检定周期表.docx', 'pszipeng', '2020-06-16 16:42:34.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee885fae3a5402f90380488');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(16, 'pszipeng', '2020-06-16 16:42:57.997', '', '', 'pszipeng', '2020-06-16 16:42:57.997', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88611e3a5402f90380490', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '56', '计量设备检测流程.docx', 'pszipeng', '2020-06-16 16:42:57.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88611e3a5402f90380490');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(17, 'pszipeng', '2020-06-16 16:46:38.835', '', '', 'pszipeng', '2020-06-16 16:46:38.835', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee886eee3a5402f9038049b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '57', '考试试卷.docx', 'pszipeng', '2020-06-16 16:46:38.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee886eee3a5402f9038049b');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(18, 'pszipeng', '2020-06-16 16:48:55.016', '', '', 'pszipeng', '2020-06-16 16:48:55.016', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88776e3a5402f9038049d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '58', '医疗器械临床使用安全控制与风险管理应急预案.docx', 'pszipeng', '2020-06-16 16:48:54.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88776e3a5402f9038049d');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(19, 'pszipeng', '2020-06-16 16:49:53.67', '', '', 'pszipeng', '2020-06-16 16:49:53.67', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee887b1e3a5402f9038049f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '59', '医疗设备三级保养体系.docx', 'pszipeng', '2020-06-16 16:49:53.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee887b1e3a5402f9038049f');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(20, 'pszipeng', '2020-06-16 16:52:06.332', '', '', 'pszipeng', '2020-06-16 16:52:06.332', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88836e3a5402f903804a5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '60', '医疗设备应急预案.docx', 'pszipeng', '2020-06-16 16:52:06.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88836e3a5402f903804a5');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(21, 'pszipeng', '2020-06-16 16:52:40.274', '', '', 'pszipeng', '2020-06-16 16:52:40.274', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee88858e3a5402f903804a7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '61', '医疗设备应急管理程序.docx', 'pszipeng', '2020-06-16 16:52:40.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88858e3a5402f903804a7');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(22, 'pszipeng', '2020-06-16 16:54:00.546', '', '', 'pszipeng', '2020-06-16 16:54:00.546', '', '057fdd13412e46df891762c01f526846', true, false, false, '5ee888a8e3a5402f903804a9', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '62', '医疗设备应急替代工作流程.docx', 'pszipeng', '2020-06-16 16:54:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee888a8e3a5402f903804a9');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(23, 'pszipeng', '2020-06-18 17:09:24.278', '', '', 'pszipeng', '2020-06-18 17:09:24.278', '', '057fdd13412e46df891762c01f526846', true, false, false, '5eeb2f43e3a5402f90381911', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '75', 'APM_通用保养模板（示例).xlsx', 'pszipeng', '2020-06-18 17:09:23.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb2f43e3a5402f90381911');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(24, 'pszipeng', '2020-06-18 17:12:21.971', '', '', 'pszipeng', '2020-06-18 17:12:21.971', '', '057fdd13412e46df891762c01f526846', true, false, false, '5eeb2ff5e3a5402f90381916', 'application/pdf', true, '112', 'APM使用分析报告-以华东某医院为例.pdf', 'pszipeng', '2020-06-18 17:12:21.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb2ff5e3a5402f90381916');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(25, 'pszipeng', '2020-06-18 17:48:38.002', '', '', 'pszipeng', '2020-06-18 17:48:38.002', '', '057fdd13412e46df891762c01f526846', true, false, false, '5eeb3875e3a5402f90381933', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true, '113', 'APM_设备分类标准汇总.xlsx', 'pszipeng', '2020-06-18 17:48:37.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb3875e3a5402f90381933');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(26, 'pszipeng', '2020-06-18 17:53:34.953', '', '', 'pszipeng', '2020-06-18 17:53:34.953', '', '057fdd13412e46df891762c01f526846', true, false, false, '5eeb399ee3a5402f9038193b', 'application/vnd.ms-excel', true, '114', 'APM_计量管理模板.xls', 'pszipeng', '2020-06-18 17:53:34.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb399ee3a5402f9038193b');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(27, 'pszipeng', '2020-06-18 18:06:30.61', '', '', 'pszipeng', '2020-06-18 18:06:30.61', '', '057fdd13412e46df891762c01f526846', true, false, false, '5eeb3ca6e3a5402f90381945', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '115', '医疗器械使用质量监督管理办法.docx', 'pszipeng', '2020-06-18 18:06:30.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5eeb3ca6e3a5402f90381945');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(1, 'Demonstration', '2020-06-24 19:09:28.134', '', '', 'Demonstration', '2020-06-24 19:09:28.134', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ee88275e3a5402f9038046a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '1', '资产管理处制度总则.docx', 'system', '2020-06-24 19:09:27.93', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ee88275e3a5402f9038046a');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(2, 'Demonstration', '2020-06-24 19:09:28.145', '', '', 'Demonstration', '2020-06-24 19:09:28.145', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ecf9a136c470ebbc7d7c996', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '7', '岗位职责以及各种流程图.docx', 'system', '2020-06-24 19:09:27.938', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a136c470ebbc7d7c996');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(3, 'Demonstration', '2020-06-24 19:09:28.15', '', '', 'Demonstration', '2020-06-24 19:09:28.15', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ecf99d66c470ebbc7d7c992', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '9', '医疗装备维护与保养制度.docx', 'system', '2020-06-24 19:09:27.942', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99d66c470ebbc7d7c992');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(4, 'Demonstration', '2020-06-24 19:09:28.165', '', '', 'Demonstration', '2020-06-24 19:09:28.165', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ecf99f7827fb9f2fa202506', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-06-24 19:09:27.975', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99f7827fb9f2fa202506');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(5, 'Demonstration', '2020-06-24 19:09:28.166', '', '', 'Demonstration', '2020-06-24 19:09:28.166', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ecf9a07827fb9f2fa202508', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '19', '医疗装备应急程序.docx', 'system', '2020-06-24 19:09:27.975', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf9a07827fb9f2fa202508');
-- INSERT INTO public.audit_file
-- (id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
-- VALUES(6, 'Demonstration', '2020-06-24 19:09:28.169', '', '', 'Demonstration', '2020-06-24 19:09:28.169', '', '2a773b22623311e7aeb25254001a1c14', true, false, false, '5ecf99e56c470ebbc7d7c994', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', true, '21', '计量装备管理制度.docx', 'system', '2020-06-24 19:09:27.977', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5ecf99e56c470ebbc7d7c994');


-- 2020.06.24 新增角色：远程培训管理员
-- INSERT INTO sys_role (home_page, name, role_desc, created_by, created_date, last_modified_by, last_modified_date, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en) VALUES (null, 'RemoteTrainingAdmin', '远程培训管理员', null, null, null, null, null, null, null, null, 'homePage', 'Remote Training Administrator');

-- 2020.06.29 新增合同字段
-- ALTER TABLE contract_info ADD bid_date date;
-- ALTER TABLE contract_info ADD receive_digital_contract_date date;
-- ALTER TABLE contract_info ADD send_contract_date date;
-- ALTER TABLE contract_info ADD send_lawer_date date;
-- ALTER TABLE contract_info ADD receive_contract_date date;
-- ALTER TABLE contract_info ADD archive_date date;
-- ALTER TABLE contract_info ADD expect_arrival_date date;

--ACCOUNT 资产 联通、设备状态 置空（7.1发布后 删除）
-- update asset_info set connect_to_insite = null,ib_device_status = null;

--IB 添加评审老师角色
delete from sys_role  where name= 'AuditStaff';

INSERT INTO public.sys_role (id,home_page,name,role_desc,menus,role_desc_en) VALUES (15,'/','AuditStaff','评审老师','audit','Judge Teacher ');

--2020.07.21 apm_config_info 中 value值为text类型
ALTER TABLE apm_config_info ALTER COLUMN  "value" TYPE TEXT;