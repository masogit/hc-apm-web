-- APP推送 ↓
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'appTitle', 'Receive a ticket message', '您收到一条工单消息', NULL, '-1');
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'appAlert', 'You have a to-do list, click to view details', '您有一个待办事项, 单击查看详情', NULL, '-1');
-- APP推送 ↑
--YHAAO-15907
update v2_pm_order vpo set pm_rule_uid = rra.rule_uid1 ,last_modified_by = 'YHAAO-15907' from rel_rule_asset rra where vpo.asset_uid = rra.asset_uid and vpo.rule_level =1 and vpo.pm_rule_uid is null;
update v2_pm_order vpo set pm_rule_uid = rra.rule_uid2 , last_modified_by = 'YHAAO-15907' from rel_rule_asset rra where vpo.asset_uid = rra.asset_uid and vpo.rule_level =2 and vpo.pm_rule_uid is null;
update v2_pm_order vpo set pm_rule_uid = rra.rule_uid, last_modified_by = 'YHAAO-15907' from rel_rule_asset rra where vpo.asset_uid = rra.asset_uid and vpo.rule_level =3 and vpo.pm_rule_uid is null;
update v2_pm_order vpo set pm_rule_uid = rra.rule_uid4 , last_modified_by = 'YHAAO-15907' from rel_rule_asset rra where vpo.asset_uid = rra.asset_uid and vpo.rule_level =4 and vpo.pm_rule_uid is null;
--YHAAO-18146
ALTER TABLE public.asset_history ALTER COLUMN json_data TYPE text USING json_data::text;
-- YHAAO-18007
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 13, 'false', '临床科室人员切换自己到其它可见范围内科室', 'departmentSwitching', '切换可见范围内科室', 'f', 't', 3 where not exists (select * from config_item where key = 'departmentSwitching');
-- 修改提醒规则为平台级
UPDATE reminder_rule SET tenant_uid = null, institution_uid = null, hospital_uid = null, site_uid = null