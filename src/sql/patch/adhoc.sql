-- 修改vender类型
update vender_info set bid_type = 'Vender' where bid_type = 'Supplier'
--远东集团添加system_client_info配置
INSERT INTO public.system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date) VALUES (lower(replace(gen_random_uuid()::text,'-','')), '2a773b22623311e7aeb25254001a1c14', '1dee1cec8d7f440dbf26a2d1f21a8580', '远东集团', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', null, now());
-- 修改资产出入库配置文件显示
update config_item set description = '资产出入库科室设置，请输入科室uid', name = '资产出入库科室设置' where key = 'setAssetOutStockDept';
INSERT INTO public.system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date) VALUES (lower(replace(gen_random_uuid()::text,'-','')), '2a773b22623311e7aeb25254001a1c14', '1dee1cec8d7f440dbf26a2d1f21a8580', '远东宏信', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', null, now());


--智康宝 zone刷新ZKB FE数据
update zkb_part_order p set zone = (select a.zone from zkb_contract_asset a where a.contract_id = p.contract_id and a.asset_id = p.asset_id)  where zone is null and current_step in ('TechnicalPending','ProductPending','ManagePending');
