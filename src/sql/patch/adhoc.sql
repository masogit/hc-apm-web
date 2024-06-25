-- 报修人电话同步历史数据
update v2_service_request sr set requestor_phone = ua.telephone from user_account ua where sr.requestor_id = ua.id and sr.requestor_phone is null and ua.telephone is not null;