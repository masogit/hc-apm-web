
--不良事件字段非空约束修改
alter table adverse_events alter asset_name drop not null;
alter table adverse_events alter device_num drop not null;
alter table adverse_events alter product_name drop not null;
alter table adverse_events alter registration_number drop not null;
--不良事件字段长度修改
alter table adverse_events alter column medical_history type varchar(1000);
alter table adverse_events alter column except_affect type varchar(1000);
alter table adverse_events alter column use_process type varchar(1000);
alter table adverse_events alter column device_desc type varchar(1000);
alter table adverse_events alter column event_cause_desc type varchar(1000);
alter table adverse_events alter column init_dispose_situ type varchar(1000);
alter table adverse_events alter column survey_situ type varchar(1000);
alter table adverse_events alter column relevance_eval type varchar(1000);
alter table adverse_events alter column eval_cause_analyze type varchar(1000);
alter table adverse_events alter column control_measure_desc type varchar(1000);
alter table adverse_events alter column not_control_cause type varchar(1000);
alter table adverse_events alter column false_report_cause type varchar(1000);

-- 新增调剂名称设置中间表
CREATE TABLE shared_asset_swap_index(
        id char(32),
        shared_asset_type char(32) ,
        asset_uid char(32) ,
        swap_index varchar(32)
)
--对shared_asset_swap_index表新增字段注释
COMMENT ON COLUMN "public"."shared_asset_swap_index"."shared_asset_type" IS '调剂设备类型uid ,  对应shared_asset_type表主键';
COMMENT ON COLUMN "public"."shared_asset_swap_index"."asset_uid" IS '对应asset_info表uid';
COMMENT ON COLUMN "public"."shared_asset_swap_index"."swap_index" IS '同一调剂设备类型shared_asset_type下应唯一';
