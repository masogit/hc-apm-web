-- Drop table

-- DROP TABLE public.asset_performance_record_delta;

CREATE TABLE public.asset_performance_record_delta (
	id bpchar(32) NOT NULL,
	amount float8 NULL,
	code varchar(255) NULL,
	cost_type varchar(255) NULL,
	create_time date NOT NULL,
	department varchar(255) NULL,
	emergency_count float8 NULL,
	emergency_revenue float8 NULL,
	group_code varchar(255) NULL,
	group_name varchar(255) NULL,
	happen_date date NULL,
	hospital_uid bpchar(32) NULL,
	inpatient_count float8 NULL,
	inpatient_revenue float8 NULL,
	institution_uid bpchar(32) NULL,
	outpatient_count float8 NULL,
	outpatient_revenue float8 NULL,
	pre_exam_department varchar(255) NULL,
	pre_exam_department_code varchar(255) NULL,
	projectname varchar(255) NULL,
	projectnumber varchar(255) NULL,
	quantity float8 NULL,
	site_uid bpchar(32) NULL,
	standard float8 NULL,
	tenant_uid bpchar(32) NULL,
	upload_status varchar(255) NULL,
	upload_time timestamp NULL,
	err_msg varchar(1024) NULL,
	hospital_name varchar(255) NULL,
	CONSTRAINT asset_performance_record_delta_pkey PRIMARY KEY (id)
);
