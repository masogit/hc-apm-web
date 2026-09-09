--YHAAO-29247
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '生命时间轴是否只显示主工单', 'assetEventTimelineOnlyShowMainsub', '生命时间轴是否只显示主工单', 'f','t',3 where not exists(select * from config_item where key = 'assetEventTimelineOnlyShowMainsub');

-- YHAAO-28255  推理模型部署-邵逸夫32分类
UPDATE asset_classification_model_details SET category = '清洗消毒灭菌类' WHERE category = '消毒灭菌类';
UPDATE asset_classification_model_details SET category = '灯床塔设备类' WHERE category = '灯床塔等手术室设备类';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_his_data_source_create_date ON his_equipment_data_source (create_date);

-- YHAAO-29473
CREATE INDEX IF NOT EXISTS v2_pm_order_current_step_id_idx ON public.v2_pm_order USING btree (current_step_id);

-- ==================YHAAO-29413==========================
-- 1. 备份 sys_role 全表数据（已存在则先删除）
-- ============================================
DROP TABLE IF EXISTS sys_role_backup_20260907;
CREATE TABLE sys_role_backup_20260907 AS SELECT * FROM sys_role;
-- ============================================
-- 2. 更新全部角色的 home_page
-- ============================================
UPDATE sys_role SET home_page = '/portal/homeNurse1.xhtml';
-- ============================================
-- 3. 回滚语句（需要回滚时执行）
-- ============================================
-- UPDATE sys_role r SET home_page = b.home_page FROM sys_role_backup_20260907 b WHERE r.id = b.id;
-- DROP TABLE IF EXISTS sys_role_backup_20260907;


--YHAAO-29537
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type, validation)
select replace(uuid_generate_v4()::text,'-',''), 3, '1', '供应商合同默认层级', 'supplierContractDefaultOrg', '供应商合同默认层级', 'f','t',7,'[{"key":"集团","value":"1"},{"key":"医院","value":"2"},{"key":"院区","value":"3"}]' where not exists(select * from config_item where key = 'supplierContractDefaultOrg');





-- 供应商新增字段：经营发证日期、生产发证日期、营业执照登记机关批准日期
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS business_issuing_date TIMESTAMP;
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS production_issuing_date TIMESTAMP;
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS license_approval_date TIMESTAMP;

ALTER TABLE supplier ADD COLUMN IF NOT EXISTS business_issuing_date TIMESTAMP;
ALTER TABLE supplier ADD COLUMN IF NOT EXISTS production_issuing_date TIMESTAMP;
ALTER TABLE supplier ADD COLUMN IF NOT EXISTS license_approval_date TIMESTAMP;

