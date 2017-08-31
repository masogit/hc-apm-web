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