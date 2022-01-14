-- 县域需求 工单主表去除无效字段
alter table v2_service_request drop column call_center_number;
alter table v2_service_request drop column sieble_number;
-- 县域需求 工单子表修改字段长度
alter table V2_Work_Order alter column call_center_number type varchar(64);
alter table V2_Work_Order alter column sieble_number type varchar(64);

-- 江宁医院租户标识
UPDATE tenant_info SET custom_tenant = '2' WHERE name = '南京市江宁医院';
-- 运营人员角色 OpWorker
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'OpWorker', '运营人员', '/homeOpWorker.xhtml');
-- 浙江大学医学院附属邵逸夫医院
UPDATE tenant_info SET custom_tenant = '3' WHERE name = '浙江大学医学院附属邵逸夫医院';

create table equip_status_record(id serial NOT NULL, asset_uid varchar(32), status_type int, begin_time timestamp, end_time timestamp, equip_addr varchar(64));
alter table equip_status_record add primary key(id);
create index ix_ equip_status_record_assetuid on equip_status_record(asset_uid);

Alter table asset_info drop column running_status;
Alter table asset_info add column running_status int default 0;

Alter table asset_info drop column shared_status;
Alter table asset_info add column shared_status int default 1;

