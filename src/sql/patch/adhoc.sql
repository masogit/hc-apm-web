-- 修改vender类型
update vender_info set bid_type = 'Vender' where bid_type = 'Supplier'
--远东集团添加system_client_info配置
INSERT INTO public.system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date) VALUES (lower(replace(gen_random_uuid()::text,'-','')), '2a773b22623311e7aeb25254001a1c14', '1dee1cec8d7f440dbf26a2d1f21a8580', '远东集团', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', null, now());