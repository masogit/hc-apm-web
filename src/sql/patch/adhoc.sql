-- 批量更新设备使用年限截止日期字段
update asset_info set asset_lifecycle_deadline = date_trunc('month', manufact_date + INTERVAL '1 month' * lifecycle) where manufact_date is not null and lifecycle is not null
