--YHAAO-29247
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, 'false', '生命时间轴是否只显示主工单', 'assetEventTimelineOnlyShowMainsub', '生命时间轴是否只显示主工单', 'f','t',3 where not exists(select * from config_item where key = 'assetEventTimelineOnlyShowMainsub');
