-- YHAAO-20824 供应商管理--微信小程序解绑租户
-- 置空停用的用户
update user_account ua set is_active = false , is_deleted = true ,telephone = null ,wechat_id=null ,wx_union_id=null from  user_wx_app uwa  left join supplier_user  su on su.user_id = uwa.user_id where ua.id = uwa.user_id and su.id is null and uwa.app_name='hc-apm-supplier';
-- 删除停用供应商用户中间表
delete from user_wx_app uwa  where not exists(select 1 from supplier_user su where su.user_id = uwa.user_id and uwa.app_name='hc-apm-supplier');