-- YHAAO-25109
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 4, 'WORD', '维修工单报告导出形式', 'WorkOrderExportType', '维修工单报告导出形式', 'f', 't', 7,'[{"key":"WORD","value":"WORD"},{"key":"PDF","value":"PDF"}]' where not exists (select * from config_item where key = 'WorkOrderExportType');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 5, 'WORD', '保养工单报告导出形式', 'PMOrderExportType', '保养工单报告导出形式', 'f', 't', 7,'[{"key":"WORD","value":"WORD"},{"key":"PDF","value":"PDF"}]' where not exists (select * from config_item where key = 'PMOrderExportType');
-- YHAAO-25508
CREATE INDEX IF NOT EXISTS asset_lifecycle_asset_uid_idx ON PUBLIC.asset_lifecycle USING btree (asset_uid);
CREATE INDEX IF NOT EXISTS asset_lifecycle_tenant_uid_idx ON PUBLIC.asset_lifecycle USING btree (tenant_uid);


-- 多级审批给申请人uid赋值
UPDATE multi_level_approval mla SET application_uid = ( SELECT mla2.initiate_approver_uid FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL LIMIT 1 ) WHERE application_uid is null  and   EXISTS ( SELECT 1 FROM multi_level_approval mla2 WHERE mla.business_uid = mla2.business_uid AND mla2.initiate_approver_uid IS NOT NULL );

