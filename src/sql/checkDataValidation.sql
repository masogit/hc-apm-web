/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  212579464
 * Created: Sep 18, 2017
 */

-- find all orgs which tenant has been deleted 
select * from org_info where tenant_uid not in(select uid from tenant_info);

-- 资产不存在,但是相关报修存在
SELECT * FROM "public".v2_service_request where asset_id not in(select id from asset_info)

-- 用户org_id 与 org_uid 所指向的组织不相同
select * from user_account u, org_info o where u.org_uid=o.uid and u.org_id!=o.id