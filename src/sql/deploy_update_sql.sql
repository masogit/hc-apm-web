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
insert into user_org (id,user_uid,org_uid,org_type) (select replace('' || uuid_generate_v4(),'-','')as newuid, u.uid as user_uid,u.site_uid as org_uid,o.org_type from user_account u left join org_info o on u.site_uid=o.uid)