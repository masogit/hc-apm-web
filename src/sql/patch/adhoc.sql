-- 修改vender类型
update vender_info set bid_type = 'Vender' where bid_type = 'Supplier'
--远东集团添加system_client_info配置
INSERT INTO public.system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date) VALUES (lower(replace(gen_random_uuid()::text,'-','')), '2a773b22623311e7aeb25254001a1c14', '1dee1cec8d7f440dbf26a2d1f21a8580', '远东宏信', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', null, now());
-- 修改资产出入库配置文件显示
update config_item set description = '资产出入库科室设置，请输入科室uid', name = '资产出入库科室设置' where key = 'setAssetOutStockDept';

--智康宝 zone刷新ZKB FE数据
update zkb_part_order p set zone = (select a.zone from zkb_contract_asset a where a.contract_id = p.contract_id and a.asset_id = p.asset_id)  where zone is null and current_step in ('TechnicalPending','ProductPending','ManagePending');

-- YHAAO-23014  新增资产出入库管理员
INSERT INTO sys_role ( "name", "role_desc", "role_desc_en")  select 'StockedManager', '出入库管理员',  'Stocked Manager'
where not exists (select 1 from sys_role where name='StockedManager');
--删除维修和保养工单中 asset_id外键约束，因为云版mvs租户可能不存在相关设备
ALTER TABLE v2_pm_order DROP CONSTRAINT IF EXISTS fk_p6lxi1rkh49qtix91am8excms;
ALTER TABLE v2_work_order DROP CONSTRAINT IF EXISTS fk_fg05gp9eenc2sqth59kv2jk3t;

-- YHAAO-23300 添加网页版供应商登录地址
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'https://hcapm.gehealthcloud.cn/reports/vender/index', '网页版供应商链接', 'vendorLoginUrl', '网页版供应商链接', 'f', 't', 1 where not exists (select * from config_item where key = 'vendorLoginUrl');
