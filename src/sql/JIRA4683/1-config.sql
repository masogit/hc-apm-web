begin;
select * into nurse_site_maintain_config_item_bak from nurse_site_maintain_config_item where parent_id in (select id from nurse_site_maintain_config where tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院') and create_date<'20181101');
select * into nurse_site_maintain_config_bak from nurse_site_maintain_config where tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院') and create_date<'20181101';
delete from nurse_site_maintain_config_item where parent_id in (select id from nurse_site_maintain_config where tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院') and create_date<'20181101');
delete from nurse_site_maintain_config where tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院') and create_date<'20181101';
commit;