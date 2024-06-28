-- 报修人电话同步历史数据
update v2_service_request sr set requestor_phone = ua.telephone from user_account ua where sr.requestor_id = ua.id and sr.requestor_phone is null and ua.telephone is not null;





insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '验证工单是否执行复检流程', 'verifyPmOrderReExaminationMode', '验证工单是否执行复检流程', 'f','t',3 where not exists(select * from config_item where key = 'verifyPmOrderReExaminationMode');
