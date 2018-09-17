/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  212579464
 * Created: Aug 23, 2017
 */

ALTER TABLE public.v2_work_order ALTER COLUMN total_man_hour TYPE float8 USING total_man_hour::float8 ;
ALTER TABLE public.v2_work_order ALTER COLUMN man_hours TYPE float8 USING man_hours::float8 ;
ALTER TABLE public.v2_work_order_coworker ALTER COLUMN man_hours TYPE float8 USING man_hours::float8 ;
ALTER TABLE public.v2_work_order_step ALTER COLUMN owner_name TYPE varchar(64) USING owner_name::varchar ;

update user_account set uid = replace('' || uuid_generate_v4(),'-','') where uid is null;

-- set user default access scope
insert into user_org (id,user_uid,org_uid,org_type) (select replace('' || uuid_generate_v4(),'-','')as newuid, u.uid as user_uid,u.site_uid as org_uid,o.org_type from user_account u left join org_info o on u.site_uid=o.uid);

update asset_info set clinical_dept_uid=o.uid from org_info o where o.id=asset_info.clinical_dept_id;

-- set clinical_dept_uid for asset_info 
update asset_info a set clinical_dept_uid=(select uid from org_info o where o.id=a.clinical_dept_id) where clinical_dept_id is not null and a.clinical_dept_uid is null

-- update orgUID for userAccount
update user_account u set org_uid=(select uid from org_info o where u.org_id=o.id ) where org_uid is null;

-- update v2_blob_object set uid and bo_sub_type for asset
update v2_blob_object b set bo_uuid=(select uid from asset_info a where b.bo_id=a.id )) where b.bo_uuid is null and b.bo_type='AssetInfo';
update v2_blob_object b set bo_sub_type=1 where b.bo_sub_type is null and b.bo_type='AssetInfo' and b.object_type='image/jpeg';


--create new root org for each tenant
insert into org_info (site_id,uid,org_level,org_type,tenant_uid,institution_uid,hospital_uid,site_uid,"name") (select id,uid,0,0,uid,uid,uid,uid,"name" from tenant_info where uid not in(select uid from org_info where org_type=0)); 
update org_info set hospital_id=id where hospital_id is null and org_type=0;
update org_info set parent_uid=tenant_uid where org_type=1 and parent_uid is null;


-- update v2_blob_object set bo_uuid and bo_sub_type for QrCodeLib
update v2_blob_object o set bo_uuid=(select uid from qr_code_lib q where q.id=o.bo_id) where bo_id is not null and bo_uuid is null and bo_type='QrCodeLib'
update v2_blob_object set bo_sub_type='1' where bo_type='QrCodeLib' and object_type='image/jpeg'
update v2_blob_object set bo_sub_type='2' where bo_type='QrCodeLib' and object_type='audio/mp3'


-- added depatment uid for serviceRequest
update v2_service_request s set from_dept_uid=(select uid from org_info o where s.from_dept_id=o.id) where s.from_dept_uid is null

-- update asset_group for "其他"
update Asset_info set asset_group=99 where asset_group=22


--删除多院区角色
delete from  user_role where role_id=6
delete from sys_role where "name"='MultiHospital'


--for v1.9.1 change
--PM rule create table_definition
insert into table_definition (id,name,tenant_uid,institution_uid,hospital_uid,site_uid,schema,status) 
(select distinct replace('' || uuid_generate_v4(),'-','')as newuid,'performance_report', rul.tenant_uid ,rul.tenant_uid,rul.tenant_uid,rul.tenant_uid, rul.performance_report,'Released' from 
(select distinct pr.tenant_uid,pr.performance_report from pm_order_rule pr 
where pr.performance_report not in (Select Schema from table_definition where tenant_uid=pr.tenant_uid) and BIT_LENGTH(pr.performance_report)>0 )rul );

insert into table_definition (id,name,tenant_uid,institution_uid,hospital_uid,site_uid,schema,status) 
(select distinct replace('' || uuid_generate_v4(),'-','')as newuid,'safety_report', rul.tenant_uid ,rul.tenant_uid,rul.tenant_uid,rul.tenant_uid, rul.safety_report,'Released' from 
(select distinct pr.tenant_uid,pr.safety_report from pm_order_rule pr where pr.safety_report not in (Select Schema from table_definition where tenant_uid=pr.tenant_uid) and BIT_LENGTH(pr.safety_report)>0 )rul );

--PM rule refrence to table_definition
update pm_order_rule pr  set safety_report=(select id from table_definition td where td.Schema=pr.safety_report and td.tenant_uid=pr.tenant_uid) 
where pr.safety_report in (Select Schema from table_definition where tenant_uid=pr.tenant_uid);

update pm_order_rule pr  set performance_report=(select id from table_definition td where td.Schema=pr.performance_report and td.tenant_uid=pr.tenant_uid) 
where pr.performance_report in (Select Schema from table_definition where tenant_uid=pr.tenant_uid);

--data refrence change
update unstructured_data_collection uc set schema_id=(select safety_report from pm_order_rule where uid=uc.schema_id) where bo_type='safety-report';

update unstructured_data_collection uc set schema_id=(select performance_report from pm_order_rule where uid=uc.schema_id) where bo_type='performance-report';

--end for v1.9.1 change

--v2.1
SELECT setval('"sys_role_id_seq"', 11, false);
INSERT INTO sys_role ( name, role_desc) VALUES ( 'SharedAssetMgmt', '设备调剂管理人员');


--v4.2
update qr_code_lib set uid = replace('' || uuid_generate_v4(),'-','') where uid is null;
create table qr_code_lib_bak as (select* from qr_code_lib);
alter table qr_code_lib drop constraint uk_qr_code_lib_qrcode;
alter table qr_code_lib alter column site_id drop not null;
alter table qr_code_lib alter column hospital_id drop not null;
--alter table qr_code_lib drop constraint qr_code_lib_pkey;
alter table qr_code_lib alter column uid set not null;
--alter table qr_code_lib alter column id drop not null;
--alter table qr_code_lib add primary key(uid);
alter table qr_code_lib add constraint uk_qr_code_lib_qrcode_site_uid unique(qr_code,site_uid);

update supplier a set tenant_uid=(select uid from tenant_info o where o.id=a.site_id) where tenant_uid is null;

INSERT INTO sys_role (name, role_desc, home_page) VALUES ('LocalITAdmin','本地后台运营管理','/portal/uaa/userAccount/List.xhtml');

--alter table qr_code_lib drop constraint qr_code_lib_pkey;
--alter table qr_code_lib alter column id set not null;
--alter table qr_code_lib add primary key(id);

alter table asset_info drop constraint uk_asset_info_qr_code;
update workflow_config set pm_manhour_price = 1000 where pm_manhour_price is null;
update workflow_config set wo_manhour_price = 1000 where wo_manhour_price is null;


--v5.1
update table_definition set base_id=id where base_id is null;

update asset_contract set tenant_uid = o.tenant_uid,institution_uid=o.institution_uid,hospital_uid=o.hospital_uid,site_uid=o.site_uid  
    from  asset_contract ac,org_info o where ac.hospital_id=o.id and asset_contract.id=ac.id;

create table unstructured_data_bak as (select* from unstructured_data);

delete from unstructured_data where id in (select id from unstructured_data ud,(select * from (SELECT count(*) as count1,collection_id,key  FROM "public".unstructured_data  group by collection_id, key ) a where count1 >1) b
   where ud.collection_id=b.collection_id and ud."key"=b.key and ud.id not in (SELECT max(id) FROM "public".unstructured_data  group by collection_id, key ));

alter table unstructured_data add constraint uk_collection_id_key unique(collection_id,key);

update v2_work_order as wo set approver_id = sr.requestor_id, approver_name = sr.requestor_name 
	from v2_service_request as sr where sr.id = wo.sr_id and approval_status != '0' and approver_id is null;


update shared_asset_type set charging_type='1' where charging_type='次';
update shared_asset_req_detail set charging_type='1' where charging_type='次';
update shared_asset_type set charging_type='2' where charging_type='小时(半点/整点)';
update shared_asset_req_detail set charging_type='2' where charging_type='小时(半点/整点)';
update shared_asset_type set charging_type='3' where charging_type='天';
update shared_asset_req_detail set charging_type='3' where charging_type='天';
update shared_asset_type set charging_type='4' where charging_type='自然时间';
update shared_asset_req_detail set charging_type='4' where charging_type='自然时间';

ALTER TABLE public.shared_asset_req_detail ALTER COLUMN charging_begin_time TYPE timestamp USING charging_begin_time::timestamp;
ALTER TABLE public.shared_asset_req_detail ALTER COLUMN charging_end_time TYPE timestamp USING charging_end_time::timestamp;
ALTER TABLE public.shared_asset_request ALTER COLUMN apply_time TYPE timestamp USING apply_time::timestamp;
ALTER TABLE public.shared_asset_request ALTER COLUMN create_date TYPE timestamp USING create_date::timestamp;
ALTER TABLE public.shared_asset_request ALTER COLUMN msg_begin_time TYPE timestamp USING msg_begin_time::timestamp;
ALTER TABLE public.shared_asset_request ALTER COLUMN msg_end_time TYPE timestamp USING msg_end_time::timestamp;

alter table capture_data_messages alter start_date type timestamp(4) without time zone
alter table capture_data_messages alter end_date type timestamp(4) without time zone


--v6.1

update supplier set uid = replace('' || uuid_generate_v4(),'-','') where uid is null;



insert into contract_info ("name",start_date,end_date,amount,tenant_uid,institution_uid,hospital_uid,site_uid,id,contract_type) 
(select "name",start_date,end_date,contract_amount,tenant_uid,institution_uid,hospital_uid,site_uid,uid,
case when contract_type='1' then 'Full' when contract_type='2' then 'Artificial' when contract_type='3' then 'Other' 
when contract_type='11' then 'ZZB' when contract_type='12' then 'ZYB' when contract_type='13' then 'ZXB' when contract_type is null then 'Other'
end  from asset_contract); 

insert into contract_info_item (id,item_type,contract_id,asset_uid) (select replace('' || uuid_generate_v4(),'-',''),'ExistAsset',ac.uid,ai.uid  from asset_contract ac, asset_info ai where ac.asset_id=ai.id);
update asset_tag_msg_subscriber set level_0=true, level_1=true, level_2=true;

--v7.1

-- 国标码
DELETE FROM gb_code WHERE code = '2020000' AND "level" = 1 ;
DELETE FROM gb_code WHERE code = '2020100' AND "level" = 1 AND "name" = '电话机' AND parent_code = '2020000';
DELETE FROM gb_code WHERE code = '2019900' AND "level" = 1 AND "name" = '其他计算机设备及软件' AND parent_code IS NULL;


update supplier set supplier_t=true,maintainer_t=false,manufacturer_t=false where supplier_type='1' and supplier_t is NUll;
update supplier set maintainer_t=true,supplier_t=false,manufacturer_t=false where supplier_type='2' and maintainer_t is NULL;
update supplier set maintainer_t=true,supplier_t=true,manufacturer_t=false where supplier_type='3' and manufacturer_t is NUll;
update supplier set maintainer_t=false,supplier_t=true,manufacturer_t=false,supplier_type=1 where supplier_type is NUll and  supplier_t is NULL and maintainer_t is NULL and manufacturer_t is NUll;

INSERT INTO public.sys_role
(id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date)
VALUES(13, 'NursingStaff', '护理人员', '/homeAssetStaff.xhtml', NULL, NULL, NULL, NULL);

update pm_order_rule por
set site_uid = (select site_uid from user_account where id = por.approve_user_id),
hospital_uid = (select hospital_uid from user_account where id = por.approve_user_id),
institution_uid = (select institution_uid from user_account where id = por.approve_user_id)
where tenant_uid is not null and status = 1;

update pm_order_rule por
set site_uid = (select site_uid from user_account where id = por.owner_id),
hospital_uid = (select hospital_uid from user_account where id = por.owner_id),
institution_uid = (select institution_uid from user_account where id = por.owner_id)
where tenant_uid is not null and status = 0;

update rel_rule_asset rra
set site_uid = (select site_uid from asset_info where uid = rra.asset_uid),
hospital_uid = (select hospital_uid from asset_info where uid = rra.asset_uid),
institution_uid = (select institution_uid from asset_info where uid = rra.asset_uid)
where site_uid is null;

--v8.1
INSERT INTO public.sys_role
(id, name, role_desc, home_page, created_by, created_date, last_modified_by, last_modified_date)
VALUES(12, 'ExternalStaff', '维修商医工', '/homeAssetStaff.xhtml', NULL, NULL, NULL, NULL);


--v8.1 nurseSiteMaintain

--before deploy project
drop table public.nurse_site_maintain_data;
drop table public.nurse_site_maintain_item;
drop table public.nurse_site_maintain;
drop table public.nurse_site_maintain_config_item;
drop table public.nurse_site_maintain_config;

--after deploy project
CREATE INDEX nurse_site_maintain_item_index
    ON public.nurse_site_maintain_item USING btree
    (schedule_date ASC NULLS LAST)
    TABLESPACE pg_default;

ALTER TABLE public.nurse_site_maintain_item
    CLUSTER ON nurse_site_maintain_item_index;

CREATE INDEX nurse_site_maintain_index
    ON public.nurse_site_maintain USING btree
    (schedule_date ASC NULLS LAST)
    TABLESPACE pg_default;

ALTER TABLE public.nurse_site_maintain
    CLUSTER ON nurse_site_maintain_index;


--v8.2
ALTER TABLE public.v2_service_request ALTER COLUMN requestor_name TYPE varchar(32) USING requestor_name::varchar ;
ALTER TABLE public.v2_work_order ALTER COLUMN current_person_name TYPE varchar(32) USING current_person_name::varchar ;
-- 使用 使用科室时添加
--update asset_info set asset_dept_id=clinical_dept_id , asset_dept_uid=clinical_dept_uid where asset_dept_id is null and asset_dept_uid is null and swap_type_state is null and tenant_UID =?;
update workflow_config set hour_required = true where hour_required is null;

update sys_role set home_page = '/homeNurse.xhtml' where name like 'NursingStaff';
ALTER TABLE public.i18n_message DROP CONSTRAINT i18n_message_msg_key;


--v8.2 plus
update org_info set short_name = name, short_name_py_code = name_py_code;
update shared_asset_req_detail set unit_price = (select a.unit_price from shared_asset_type a left join asset_info b on a.id=b.shared_asset_type where shared_asset_req_detail.asset_info_id = b.uid) where unit_price is null;
update asset_info set clinical_dept_uid=asset_dept_uid, asset_dept_uid=clinical_dept_uid where shared_asset_type is not null and clinical_dept_uid!=asset_dept_uid;

--v9.1
update user_account set region = 1 where region is null;
ALTER TABLE weixin_article ALTER COLUMN content TYPE varchar(204800) USING content::varchar ;

update qr_code_lib set status=5 where status=2 and submit_wechat_id is null;

ALTER TABLE public.user_org DROP CONSTRAINT fk_gj8bl5ul9orysa05e0j4vve8n;
update tenant_info set tenant_config='{"menuList":["安装验收","设备档案","设备调剂","维修管理(不含排班管理)","维修排班管理","预防维护","绩效分析","系统配置","知识文档","护士站","计量管理"],"edition":"通用版"}' where tenant_config is null;

--v10.1
update tenant_info set tenant_config ='{"menuList":["安装验收","设备档案","设备调剂","维修管理","排班管理","预防维护","绩效分析","系统配置","知识文档","护士站","计量管理"],"edition":"通用版"}' where tenant_config='{"menuList":["安装验收","设备档案","设备调剂","维修管理(不含排班管理)","维修排班管理","预防维护","绩效分析","系统配置","知识文档","护士站","计量管理"],"edition":"通用版"}';
