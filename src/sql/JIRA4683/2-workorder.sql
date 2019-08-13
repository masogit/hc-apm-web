begin;
select * into nurse_site_maintain_data_bak from nurse_site_maintain_data where parent_id in (select id from nurse_site_maintain_item where parent_id in (select id from nurse_site_maintain where create_date<'20181101' and tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院')));
select * into nurse_site_maintain_item_bak from nurse_site_maintain_item where parent_id in (select id from nurse_site_maintain where create_date<'20181101' and tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院'));
select * into nurse_site_maintain_bak from nurse_site_maintain where id in (select id from nurse_site_maintain where create_date<'20181101' and tenant_uid in (select uid from org_info where name='江苏省沭阳县人民医院'));
select * into nurse_site_maintain_data_bak2 from nurse_site_maintain_data;
select * into nurse_site_maintain_item_bak2 from nurse_site_maintain_item;
select * into nurse_site_maintain_bak2 from nurse_site_maintain;
delete from nurse_site_maintain_data_bak2 where id in (select id from nurse_site_maintain_data_bak);
delete from nurse_site_maintain_item_bak2 where id in (select id from nurse_site_maintain_item_bak);
delete from nurse_site_maintain_bak2 where id in (select id from nurse_site_maintain_bak);
alter table nurse_site_maintain_data rename to nurse_site_maintain_data_bak3;
drop table nurse_site_maintain_data_bak3;
alter table nurse_site_maintain_item rename to nurse_site_maintain_item_bak3;
drop table nurse_site_maintain_item_bak3;
alter table nurse_site_maintain rename to nurse_site_maintain_bak3;
drop table nurse_site_maintain_bak3;
alter table nurse_site_maintain_data_bak2 rename to nurse_site_maintain_data;
alter table nurse_site_maintain_item_bak2 rename to nurse_site_maintain_item;
alter table nurse_site_maintain_bak2 rename to nurse_site_maintain;
ALTER TABLE public.nurse_site_maintain
ADD CONSTRAINT pk_nursesitemaintain PRIMARY KEY (id);
ALTER TABLE public.nurse_site_maintain_item
ADD CONSTRAINT pk_nursesitemaintainitem PRIMARY KEY (id);
ALTER TABLE public.nurse_site_maintain_data
ADD CONSTRAINT pk_nursesitemaintaindata PRIMARY KEY (id);
commit;