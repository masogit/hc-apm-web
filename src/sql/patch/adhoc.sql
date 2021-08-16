--智康保探头
update zkb_part_order_cat set is_vaild='N' where lower(cat_model) like '%3v%' or lower(cat_model) like '%4v%' or lower(cat_model) like '%10t-d%' or lower(cat_model) like '%6vt-d%' or lower(cat_model) like '%9t%' or lower(cat_model) like '%9t-rs%' or lower(cat_model) like '%6tc-rs%' or lower(cat_model) like '%6tc%' or lower(cat_model) like '%rm6c%' or lower(cat_model) like '%em6c%'  ;
--瑞金菜单添加
INSERT INTO sys_role (id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en) VALUES (nextval('sys_role_id_seq'), 'SupplierAdmin', '供应商-管理员', '/', null, null, null, null, null, null, null, null, 'supplierManagement,supplierManagement.supplierUser,supplierManagement.assetBindSupplierFE', null);
INSERT INTO sys_role (id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en) VALUES (nextval('sys_role_id_seq'), 'SupplierSales', '供应商-销售', '/', null, null, null, null, null, null, null, null, 'repair,repair.workbench', 'supplierManagement.supplierUser');
INSERT INTO sys_role (id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en) VALUES (nextval('sys_role_id_seq'), 'SupplierFE', '供应商-维修工程师', '/', null, null, null, null, null, null, null, null, 'repair,repair.workbench', 'supplierManagement.supplierUser');
INSERT INTO sys_role (id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en) VALUES (nextval('sys_role_id_seq'), 'SupplierOther', '供应商-其他', '/', null, null, null, null, null, null, null, null, 'repair,repair.workbench', null);
DELETE FROM  sys_role WHERE name in ('SupplierAdmin','SupplierSales','SupplierFE','SupplierOther') and id not in (select min(id) from sys_role where   name in ('SupplierAdmin','SupplierSales','SupplierFE','SupplierOther') group by name);

--ACCT维修消息配置添加两个选项
INSERT INTO msg_config (id,msg_bind_field,msg_name,msg_status,msg_name_en) VALUES ((select count(*)+1 from msg_config),'functionType', '设备型号', '0', 'Device model');
INSERT INTO msg_config (id,msg_bind_field,msg_name,msg_status,msg_name_en) VALUES ((select count(*)+1 from msg_config),'serialNum', '制造商序列号', '0', 'Manufacturer serial number');

--测试数据隔离
UPDATE zkb_part_order_user_config set test_status = 'f';
UPDATE zkb_part_order_user_config set test_status = 't' where login_name like 'test.%';
