-- YHAAO-20824 供应商管理--微信小程序解绑租户
-- 置空停用的用户
update user_account ua set is_active = false , is_deleted = true ,telephone = null ,wechat_id=null ,wx_union_id=null from  user_wx_app uwa  left join supplier_user  su on su.user_id = uwa.user_id where ua.id = uwa.user_id and su.id is null and uwa.app_name='hc-apm-supplier';
-- 删除停用供应商用户中间表
delete from user_wx_app uwa  where not exists(select 1 from supplier_user su where su.user_id = uwa.user_id and uwa.app_name='hc-apm-supplier');
-- YHAAO-20850 临床可否查看调剂计费信息
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 2, 'false', '临床可否查看调剂计费信息', 'enableViewingAdjustmentBillingInformation', '临床可否查看调剂计费信息', 'f', 't', 3 where not exists (select * from config_item where key = 'enableViewingAdjustmentBillingInformation');