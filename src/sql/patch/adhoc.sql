-- 县域需求 工单主表去除无效字段
alter table v2_service_request drop column call_center_number;
alter table v2_service_request drop column sieble_number;
-- 县域需求 工单子表修改字段长度
alter table V2_Work_Order alter column call_center_number type varchar(64);
alter table V2_Work_Order alter column sieble_number type varchar(64);

-- 江宁医院租户标识
UPDATE tenant_info SET custom_tenant = '2' WHERE name = '南京市江宁医院';
