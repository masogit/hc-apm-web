-- 资产信息表新增的last_check_company(上次检测单位)字段，更新值
update asset_info set last_check_company = (select mc.check_company from measuring_certificate_detail mc where uid = mc.asset_uid order by mc.check_date desc limit 1) where (select count(1) from measuring_certificate_detail mc1 where uid = mc1.asset_uid) > 0

INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'ReviewSystemAdministrator', '评审系统管理员', '/');
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'PMAdministrator', 'PM管理员', '/');
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'ReviewAdministrator', '评审管理员', '/');
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'judge', '评审员', '/');


