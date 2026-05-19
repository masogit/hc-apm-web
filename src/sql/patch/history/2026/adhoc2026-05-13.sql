insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 10, 'false', '维修成本是否纳入工单费用', 'maintenanceCostIncludedWorkOrderCost', '维修成本是否纳入工单费用', 'f','t',3 where not exists(select * from config_item where key = 'maintenanceCostIncludedWorkOrderCost');




--YHAAO-27143
INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '维修工单是否提醒使用备件', 'workOrderShowPartFlag', '维修工单是否提醒使用备件', 'f','t',3 where not exists(select * from config_item where key = 'workOrderShowPartFlag');


--YHAAO-27142
update config_item set value_type = 9, default_value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime';
update config_item_value set value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime' and value = 'false';
update config_item_value set value = '{"rendered":true,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'workOrderShowTravelTime' and value = 'true';

update config_item set value_type = 9, default_value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime';
update config_item_value set value = '{"rendered":false,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime' and value = 'false';
update config_item_value set value = '{"rendered":true,"required":false,"requiredMessage":"路途工时为必输项, 不能为空！"}' where key = 'pmOrderShowTravelTime' and value = 'true';


-- 27913
CREATE EXTENSION IF NOT EXISTS dblink;

DROP VIEW IF EXISTS public.ib_asset_info_dblink;

CREATE OR REPLACE VIEW public.ib_asset_info_dblink AS
SELECT
    t.name,
    t.depart_num,
    t.serial_num,
    t.system_id,
    t.qr_code
FROM dblink(
             'host=postgresql-ib port=5432 dbname=dfa4d250ca24740e52acbe4193f03d236 user=u25311cc705bad494ab334556a0c7670e password=pc8daf2a0436c33f9a1bd08c555cd647d'::text,
             'SELECT
			 t.name,
             t.depart_num ,
             t.serial_num,
             t.system_id,
             t.qr_code
                 FROM asset_info t
        WHERE t.is_deleted = false
          AND t.is_valid = true
		  and t.ib_status = 1
    '::text) AS t(
                               name varchar,
                               depart_num varchar,
                               serial_num varchar,
                               system_id varchar,
                               qr_code varchar
    );

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 8, 'false', '报修是否影响当天日常检查', 'repairAffectsDailyInspection', '报修是否影响当天日常检查', 'f','t',3 where not exists(select * from config_item where key = 'repairAffectsDailyInspection');


UPDATE data_entry de SET created_name = ua.name FROM user_account ua WHERE de.operation_user_uid = ua.uid;
