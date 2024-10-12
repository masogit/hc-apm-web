-- YHAAO-22800 巡检工单是否自动添加性能检测报告
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 5, 'false', '是否开启巡检工单自动添加性能检测报告', 'enablePerformanceReportAutoAdd', '保养单是否自动添加报告', 'f', 't', 3 where not exists (select * from config_item where key = 'enablePerformanceReportAutoAdd');
-- YHAAO-22643 到货清单界面增加必填项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 3, 'false', '到货清单合同号和验收材料是否必填', 'enableArrivalListRequired', '到货清单必填项', 'f', 't', 3 where not exists (select * from config_item where key = 'enableArrivalListRequired');

--his分组添加创建时间
update his_asset_group set created_date= now(),last_modified_date=now()  where created_date is null;
update his_asset_group_asset_match set created_date= now(),last_modified_date=now() where created_date is null;
--his处理历史数据值
update  his_asset_group_asset_match set org_info_uid=site_uid  where org_info_uid is null;
update  his_asset_group_clinical_match set org_info_uid=site_uid  where org_info_uid is null;


-- YHAAO-22813 处理历史数据
update  asset_info  set institution_uid = tenant_uid,hospital_uid = tenant_uid,site_uid = tenant_uid,clinical_dept_uid = reverse(clinical_dept_uid),asset_dept_uid = reverse(asset_dept_uid),patrol_inspection_owner_uid=reverse(patrol_inspection_owner_uid),last_modified_date=now() where is_deleted = true and institution_uid != tenant_uid and hospital_uid != tenant_uid and site_uid != tenant_uid;

-- YHAAO-23051 云南阜外调剂中心科室名称刷数据
update shared_asset_request sar set  clinical_dept_name = oi.name from  org_info oi where  oi.id is not null and sar.clinical_dept_uid = oi.uid and oi.name != sar.clinical_dept_name and sar.state in (1,2,3,4,5,6,7,8,9,11) and sar.tenant_uid = (select  uid  from  tenant_info  where name ='云南省阜外心血管病医院');
update shared_asset_req_detail sard set clinical_dept_name = oi.name from org_info oi,shared_asset_request sar where oi.id is not null and sard.clinical_dept_uid = oi.uid and sard.clinical_dept_name != oi.name and sar.id in (sard.borrow_id,sard.return_id) and  sar.state in (1,2,3,4,5,6,7,8,9,11) and sar.tenant_uid = (select  uid  from  tenant_info  where name ='云南省阜外心血管病医院');