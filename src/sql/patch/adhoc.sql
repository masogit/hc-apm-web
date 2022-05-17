-- 质控评审 省份&版本管理，租户需要可筛选
update audit_case t1 set tenant_uid = t2.tenant_uid from org_info t2 where t1.site_uid = t2.uid;