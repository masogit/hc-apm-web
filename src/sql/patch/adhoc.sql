-- 12.02 release start
INSERT INTO sys_role ("home_page", "name", "role_desc", "created_by", "created_date", "last_modified_by", "last_modified_date", "authorities", "authorities_ch", "cards", "cards_ch", "menus", "role_desc_en") select NULL, 'GEInternalStaff', 'GE内部团队', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '' from sys_role where not exists(select 1 from sys_role where name = 'GEInternalStaff') limit 1;
CREATE INDEX asset_service_member_system_id ON asset_service_member(system_id);
-- 12.02 release end