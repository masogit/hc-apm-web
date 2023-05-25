-- YHAAO-18604
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 3, '确认配置清单+验收培训页', '供应商小程序到货清单临床验收确认说明配置', 'arrivalListCheckCaption', '到货清单临床验收确认说明', 'f', 't', 1 where not exists (select * from config_item where key = 'arrivalListCheckCaption');
-- YHAAO-18549
update key_asset_report set report_type = 1;

--YHAAO-18594
update sys_role set home_page='/homeNurse1.xhtml' where   home_page='/homeNurse.xhtml'