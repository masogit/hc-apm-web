insert into user_favorites(created_by,created_date,last_modified_by,last_modified_date,system_id,user_uid)
 select 'system',now(),'system',now(),ai.system_id,u.uid from (
select value::jsonb->>'id' as userid,jsonb_array_elements(value::jsonb->'assets')->>'id' as aid from apm_config_info as aci
where value like '%assets%' and value not like '%assets":[]%') as t
LEFT JOIN user_account u on t.userid::int=u.id
LEFT JOIN asset_info ai on ai.id=t.aid::int;