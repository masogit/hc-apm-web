/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  212579464
 * Created: Aug 23, 2017
 */

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
insert into org_info (site_id,uid,org_level,org_type,tenant_uid,institution_uid,hospital_uid,site_uid,"name") (select id,uid,0,0,uid,uid,uid,uid,"name" from tenant_info where uid not in(select uid from org_info where org_type=0)) 
update org_info set hospital_id=id where hospital_id is null and org_type=0
update org_info set parent_uid=tenant_uid where org_type=1 and parent_uid is null


-- update v2_blob_object set bo_uuid and bo_sub_type for QrCodeLib
update v2_blob_object o set bo_uuid=(select uid from qr_code_lib q where q.id=o.bo_id) where bo_id is not null and bo_uuid is null and bo_type='QrCodeLib'
update v2_blob_object set bo_sub_type='1' where bo_type='QrCodeLib' and object_type='image/jpeg'
update v2_blob_object set bo_sub_type='2' where bo_type='QrCodeLib' and object_type='audio/mp3'


-- added depatment uid for serviceRequest
update v2_service_request s set from_dept_uid=(select uid from org_info o where s.from_dept_id=o.id) where s.from_dept_uid is null

-- update asset_group for "其他"
update Asset_info set asset_group=99 where asset_group=22