update apm_config_info set value = value::jsonb - 'assets' where value like '%assets%' and value not like '%assets":[]%';
delete from user_favorites;