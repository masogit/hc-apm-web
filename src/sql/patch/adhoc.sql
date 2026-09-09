--YHAAO-29247
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '生命时间轴是否只显示主工单', 'assetEventTimelineOnlyShowMainsub', '生命时间轴是否只显示主工单', 'f','t',3 where not exists(select * from config_item where key = 'assetEventTimelineOnlyShowMainsub');

-- YHAAO-28255  推理模型部署-邵逸夫32分类
UPDATE asset_classification_model_details SET category = '清洗消毒灭菌类' WHERE category = '消毒灭菌类';
UPDATE asset_classification_model_details SET category = '灯床塔设备类' WHERE category = '灯床塔等手术室设备类';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_his_data_source_create_date ON his_equipment_data_source (create_date);


-- 供应商新增字段：经营发证日期、生产发证日期、营业执照登记机关批准日期
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS business_issuing_date TIMESTAMP;
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS production_issuing_date TIMESTAMP;
ALTER TABLE vender_company ADD COLUMN IF NOT EXISTS license_approval_date TIMESTAMP;

ALTER TABLE supplier ADD COLUMN IF NOT EXISTS business_issuing_date TIMESTAMP;
ALTER TABLE supplier ADD COLUMN IF NOT EXISTS production_issuing_date TIMESTAMP;
ALTER TABLE supplier ADD COLUMN IF NOT EXISTS license_approval_date TIMESTAMP;

