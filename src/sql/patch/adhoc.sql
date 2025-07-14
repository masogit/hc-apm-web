-- YHAAO-25109
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'WORD', '维修工单报告导出形式', 'WorkOrderExportType', '维修工单报告导出形式', 'f', 't', 7,'[{"key":"WORD","value":"WORD"},{"key":"PDF","value":"PDF"}]' where not exists (select * from config_item where key = 'WorkOrderExportType');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 5, 'WORD', '保养工单报告导出形式', 'PMOrderExportType', '保养工单报告导出形式', 'f', 't', 7,'[{"key":"WORD","value":"WORD"},{"key":"PDF","value":"PDF"}]' where not exists (select * from config_item where key = 'PMOrderExportType');
-- YHAAO-25508
CREATE INDEX IF NOT EXISTS asset_lifecycle_asset_uid_idx ON PUBLIC.asset_lifecycle USING btree (asset_uid);
CREATE INDEX IF NOT EXISTS asset_lifecycle_tenant_uid_idx ON PUBLIC.asset_lifecycle USING btree (tenant_uid);


-- 多级审批给申请人uid赋值
UPDATE multi_level_approval mla SET application_uid = ( SELECT mla2.initiate_approver_uid FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL LIMIT 1 ) WHERE application_uid is null  and   EXISTS ( SELECT 1 FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL );
-- 库存管理新增库存来源
update asset_stocked set stock_source = 3 where stock_source is null;
update asset_in_stock_record set stock_source = 3 where stock_source is null;
update asset_out_stock_record set stock_source = 3 where stock_source is null;
-- 效期库索引
CREATE INDEX idx_equipment_function_type_expr
    ON equipment_expiry_information (REGEXP_REPLACE(LOWER(TRIM(function_type)), '\s+', '', 'g'));
CREATE INDEX idx_equipment_keyword_pattern ON equipment_expiry_information
    (LOWER(keyword));
ALTER TABLE equipment_expiry_information ALTER COLUMN manufact_date TYPE varchar(10);

