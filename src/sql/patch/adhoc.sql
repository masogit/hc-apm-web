INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time, last_access_date)  select replace(uuid_generate_v4()::text,'-',''), 'ac07d2c239df4ee79f645f05d153debc', 'ac07d2c239df4ee79f645f05d153debc', '常州市中医医院', '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, null where not exists(select 1 from system_client_info where bind_site_uid='ac07d2c239df4ee79f645f05d153debc');

ALTER TABLE his_equipment_data_source_backup ADD PRIMARY KEY (id);

---智康保归还数据修复
update zkb_part_order_return set data_satus = 'Pending' where id= '2c92889590ca5ac40190d88d12022490';

--预算批量修改年份数据
update budget_application set application_year = date_part('year', application_date)   where application_year is  null;
