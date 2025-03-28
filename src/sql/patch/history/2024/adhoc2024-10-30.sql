
-- 新增配置项”盘点有问题设备是否显示在已盘点中“， 默认值为‘是’，  云阜设置为”否
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 12, 'true', '盘点有问题设备是否显示在已盘点中', 'enableHasIssueShownInTheInventory', '盘点有问题设备是否显示在已盘点中', 'f', 't', 3 where not exists (select * from config_item where key = 'enableHasIssueShownInTheInventory');
INSERT INTO public.config_item_value("uid", "hospital_uid", "institution_uid", "key", "site_uid", "tenant_uid", "value") SELECT replace(uuid_generate_v4()::text, '-', ''), t.uid, t.uid, 'enableHasIssueShownInTheInventory', t.uid, t.uid, 'false' FROM tenant_info t WHERE t.name = '云南省阜外心血管病医院' AND NOT EXISTS ( SELECT 1 FROM config_item_value civ WHERE civ.key = 'enableHasIssueShownInTheInventory' AND civ.site_uid = t.uid );
-- YHAAO-23044  扫码盘点点开后资产属性增加字段：使用年限、下次计量日期、备注
update  config_item set validation='[{"key":"设备型号","value":"functionType"},{"key":"设备序列号","value":"serialNum"},{"key":"出厂日期","value":"manufactDate"},{"key":"注册证号","value":"registrationNo"},{"key":"安装位置","value":"locationName"},{"key":"二维码","value":"qrCode"},{"key":"制造商","value":"manufacture"},{"key":"使用年限","value":"lifecycle"},{"key":"下次计量日期","value":"nextMeteringDate"},{"key":"备注","value":"comments"}]' where key='scanInventoryField';


--不良事件退回回写多级审批意见
update multi_level_approval ap set approval_opinion = (SELECT e.return_reason from adverse_events e where e.uid = ap.business_uid) where ap.business_type = 13 and ap.approval_status = 3 and approval_opinion is null and ap.id in (select p.id from multi_level_approval p inner join (SELECT business_uid,max(actual_approver_date) actual_approver_date from multi_level_approval where business_type = 13 and approval_status = 3 group by business_uid ) t on p.business_uid = t.business_uid and t.actual_approver_date = p.actual_approver_date);
-- 删除重复数据
delete from shared_asset_req_detail where id in (select  sard.id  from   shared_asset_req_detail  sard left join asset_info  ai on sard.asset_info_id = ai.uid where sard.charging_end_time is null and ai.clinical_dept_uid = ai.asset_dept_uid);
delete from shared_asset_req_detail  where id in (select id  from (select  row_number() over( partition by asset_info_id order by charging_begin_time desc) as rn,id from  shared_asset_req_detail where  charging_end_time is null) as t where  t.rn > 1);