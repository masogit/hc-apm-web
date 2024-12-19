-- YHAAO-23521
create table if not exists public.i18n_type_manage as (select CAST(replace(uuid_generate_v4()::text,'-','') as varchar(32)) as id,CAST(t1.msg_type as varchar(255)),CAST(coalesce(t2.value_zh,t2.msg_key,t1.msg_type) as varchar(255)) as name, -1 as type,'' as description from  (select  msg_type from  i18n_message where  msg_type not in('label','message','customI18n','field_name')  group by msg_type having msg_type not like '%68%') t1 left join i18n_message t2 on t1.msg_type=t2.msg_key and t2.msg_type = 'label');


--YHAAO-23423 安装位置配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)
select replace(uuid_generate_v4()::text,'-',''), 4, 'false', '是否显示安装位置', 'showLocationNameFlag', '是否显示安装位置', 'f','t',3 where not exists(select * from config_item where key = 'showLocationNameFlag');
