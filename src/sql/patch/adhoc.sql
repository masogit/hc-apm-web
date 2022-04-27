INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'ReviewSystemAdministrator', '评审专家', '/');
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'PMAdministrator', '评审管理组', '/');
INSERT INTO sys_role("name", role_desc, home_page) VALUES( 'ReviewAdministrator', '评审管理员', '/');
delete from  	qr_code_lib  where id in(
select id  from  qr_code_lib  where	tenant_uid=(select  uid  from org_info  where name='山东阳光融和医院（待删除）')
        or  institution_uid=(select  uid  from org_info  where name='山东阳光融和医院（待删除）')
        or  hospital_uid=(select  uid  from org_info  where name='山东阳光融和医院（待删除）')
        or  site_uid=(select  uid  from org_info  where name='山东阳光融和医院（待删除）')
        or  org_id=(select  id  from org_info  where name='山东阳光融和医院（待删除）')

);
delete from  	qr_code_lib  where id in(
select  id
    from
        qr_code_lib
    where
						tenant_uid=(select  uid  from org_info  where name='超声部')
        or  institution_uid=(select  uid  from org_info  where name='超声部')
        or  hospital_uid=(select  uid  from org_info  where name='超声部')
        or  site_uid=(select  uid  from org_info  where name='超声部')
        or  org_id=(select  id  from org_info  where name='超声部')

);

delete from  	qr_code_lib  where id in(
select
        id
    from
        qr_code_lib
    where
						tenant_uid=(select  uid  from org_info  where name='恩泽妇产医院超声部')
        or  institution_uid=(select  uid  from org_info  where name='恩泽妇产医院超声部')
        or  hospital_uid=(select  uid  from org_info  where name='恩泽妇产医院超声部')
        or  site_uid=(select  uid  from org_info  where name='恩泽妇产医院超声部')
        or  org_id=(select  id  from org_info  where name='恩泽妇产医院超声部')

);
delete from  	qr_code_lib  where id in(
select
        id
    from
        qr_code_lib
    where
						tenant_uid=(select  uid  from org_info  where name='恩泽医院（路桥）超声部')
        or  institution_uid=(select  uid  from org_info  where name='恩泽医院（路桥）超声部')
        or  hospital_uid=(select  uid  from org_info  where name='恩泽医院（路桥）超声部')
        or  site_uid=(select  uid  from org_info  where name='恩泽医院（路桥）超声部')
        or  org_id=(select  id  from org_info  where name='恩泽医院（路桥）超声部')

);
delete from  	qr_code_lib  where id in(
select
        id
    from
        qr_code_lib
    where
						tenant_uid=(select  uid  from org_info  where name='台州医院路桥院区超声部')
        or  institution_uid=(select  uid  from org_info  where name='台州医院路桥院区超声部')
        or  hospital_uid=(select  uid  from org_info  where name='台州医院路桥院区超声部')
        or  site_uid=(select  uid  from org_info  where name='台州医院路桥院区超声部')
        or  org_id=(select  id  from org_info  where name='台州医院路桥院区超声部')

);