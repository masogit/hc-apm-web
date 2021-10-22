
--不良事件字段非空约束修改
alter table adverse_events alter asset_name drop not null;
alter table adverse_events alter device_num drop not null;
alter table adverse_events alter product_name drop not null;
alter table adverse_events alter registration_number drop not null;