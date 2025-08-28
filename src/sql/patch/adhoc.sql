insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 5, '{"value": 1,"hours":0.1}', '保养工时设置', 'pmEntryTypeSet', '保养工时设置', 'f','t',9 where not exists(select * from config_item where key = 'pmEntryTypeSet');
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 4, 0, '维修工时类型设置', 'repairEntryTypeSet', '维修工时类型设置', 'f', 't', 7,'[{"key":"工时段","value":"1"},{"key":"总工时","value":"PDF"}]' where not exists (select * from config_item where key = 'repairEntryTypeSet');

update v2_pm_order set cancel_time = end_time where current_step_name = '已取消';

--YHAAO-25896 报废申请人取用户名且需要更新历史数据
update asset_scrap scrap  set created_by=ua.name  from user_account ua  where scrap.request_user_uid=ua.uid;
update asset_scrap scrap  set approval_by=ua.name  from user_account ua  where scrap.approval_uid=ua.uid;
-- 质控日历优化 v2_pm_order添加asset_uid索引
CREATE INDEX if not exists v2_pm_order_asset_uid_idx ON public.v2_pm_order (asset_uid);
CREATE INDEX if not exists v2_work_order_asset_uid_idx ON public.v2_work_order (asset_uid);

-- YHAAO-25895 无人值守调剂中心，数据异常问题处理
-- cs002
UPDATE shared_asset_req_detail SET charging_begin_time='2025-8-20 7:58:20',clinical_dept_for_return_name='心胸血管外科',clinical_dept_for_return_uid='d1772a767a704a16bd767dd0e2a8da7a',clinical_dept_name='心胸血管外科',clinical_dept_uid='d1772a767a704a16bd767dd0e2a8da7a',create_by='81980f81f880483da9aa38da53adf7fe',create_date='2025-8-20 7:58:20' WHERE ID='2c9b858298a36e380198cb7ddd8e7925';
UPDATE shared_asset_request SET clinical_dept_name='心胸血管外科',clinical_dept_uid='d1772a767a704a16bd767dd0e2a8da7a',applicant_phone='15647991266',applicant='那木吉拉',create_by='81980f81f880483da9aa38da53adf7fe',remark='',OWNER='那木吉拉' WHERE ID='2c9b858298a36e380198cb7ddd8e7924';
UPDATE shared_asset_request SET clinical_dept_name='心胸血管外科',clinical_dept_uid='d1772a767a704a16bd767dd0e2a8da7a',applicant_phone='15647991266',applicant='那木吉拉',create_by='81980f81f880483da9aa38da53adf7fe',remark='',OWNER='那木吉拉' WHERE ID='2c9b858298a36e380198cb7df10f7926';
--cs001
-- 借用请求
INSERT INTO shared_asset_request (ID,applicant,apply_time,behavior,clinical_dept_name,clinical_dept_uid,tenant_uid,institution_uid,hospital_uid,site_uid,STATE,data_type) VALUES ('da0b9dd97bed45dfa952115be6f81573','system','202-08-21 10:08:52',1,'肿瘤内科','7debb7fdf275402483e4c19261cb19cc','9e507a68922b479c86d0a410a88d78ec','8d940c6db65845c2a1f2549a5d159713','f4b3844d52e74294ba25f52b98c8d892','3c09d3da844b47218bd24a60c5ec63f1',6,2);
-- 归还请求
INSERT INTO shared_asset_request (ID,applicant,apply_time,behavior,clinical_dept_name,clinical_dept_uid,tenant_uid,institution_uid,hospital_uid,site_uid,STATE,data_type) VALUES ('f8e1353b7d43464caa0a1a999d863bc9','system','2025-08-21 11:16:05',2,'肿瘤内科','7debb7fdf275402483e4c19261cb19cc','9e507a68922b479c86d0a410a88d78ec','8d940c6db65845c2a1f2549a5d159713','f4b3844d52e74294ba25f52b98c8d892','3c09d3da844b47218bd24a60c5ec63f1',12,2);
-- 借用设备详情
INSERT INTO shared_asset_req_detail (ID,asset_info_id,asset_name,borrow_id,charging_begin_time,charging_end_time,charging_type,clinical_dept_for_return_name,clinical_dept_for_return_uid,clinical_dept_name,clinical_dept_uid,asset_number,tenant_uid,institution_uid,hospital_uid,site_uid,return_id,shared_asset_type,unit_price,asset_id,qr_code,maintain_time,is_cleaned) VALUES ('c191badd9dd7498fac9e305ae2be4cdf','cf582ce772bd4a079ce902f7bfd0045c','便携式彩色多普勒超声诊断仪	','da0b9dd97bed45dfa952115be6f81573','2025-08-21 10:08:52','2025-08-21 11:16:05',5,'肿瘤内科','7debb7fdf275402483e4c19261cb19cc','肿瘤内科','7debb7fdf275402483e4c19261cb19cc','0000000249686','9e507a68922b479c86d0a410a88d78ec','8d940c6db65845c2a1f2549a5d159713','f4b3844d52e74294ba25f52b98c8d892','3c09d3da844b47218bd24a60c5ec63f1','f8e1353b7d43464caa0a1a999d863bc9','03a4bb63d29642269dd835ed3622b4e9',5,922323,'2201042367111270','2025-08-21 11:16:05',TRUE);
-- 调剂类型
INSERT INTO shared_asset_req_type (id, actual_qty,apply_qty, except_qty, shared_asset_type, shared_id, swap_name) VALUES ('9812fb767bd84e70a9648e203ba91bb8', 1, 1,1, '03a4bb63d29642269dd835ed3622b4e9', 'da0b9dd97bed45dfa952115be6f81573','超声');
delete from shared_asset_req_type  where id ='8480321329ef45c5bba329cfe3b9537f';
INSERT INTO shared_asset_req_type (id, actual_qty,apply_qty, except_qty, shared_asset_type, shared_id, swap_name) VALUES ('8480321329ef45c5bba329cfe3b9537f', 1, 1,1, '03a4bb63d29642269dd835ed3622b4e9', 'f8e1353b7d43464caa0a1a999d863bc9','超声');
-- JHY023
INSERT INTO shared_asset_request (ID,applicant,apply_time,behavior,clinical_dept_name,clinical_dept_uid,tenant_uid,institution_uid,hospital_uid,site_uid,STATE,data_type) VALUES ('c9c5bd50f72042168a13bb75ac70a5e4','system','202-08-21 10:08:52',2,'创伤骨科（骨一科）','d6a87805331f44a296a2c065827841f0','9e507a68922b479c86d0a410a88d78ec','8d940c6db65845c2a1f2549a5d159713','f4b3844d52e74294ba25f52b98c8d892','3c09d3da844b47218bd24a60c5ec63f1',12,2);
update shared_asset_req_detail  set return_id = 'c9c5bd50f72042168a13bb75ac70a5e4',is_cleaned=true,maintain_time='2025-08-21 11:30:34',charging_end_time='2025-08-21 11:30:34' where id ='2c9b858298a36e380198a75b099b0e4f';
update asset_info set asset_dept_uid = clinical_dept_uid,asset_dept_id = clinical_dept_id,is_cleaned = true  where uid = '7864171be50744d9b1cd374244c05084';
-- HJX008
update shared_asset_req_detail  set  clinical_dept_for_return_name = '肾内科',clinical_dept_for_return_uid='7d4873666fe94dde965f1dbe25cc510b',clinical_dept_name='肾内科',clinical_dept_uid='7d4873666fe94dde965f1dbe25cc510b',charging_begin_time='2025-08-20 17:19:44',charging_end_time='2025-08-20 18:44:10'  where id='2c9b858298a36e380198cb7df77d7929';
update shared_asset_request  set clinical_dept_name = '肾内科',clinical_dept_uid='7d4873666fe94dde965f1dbe25cc510b',remark='' where id in('2c9b858298a36e380198cb7df77d7928','2c9b858298a36e380198cb7e2fd8792e');



ALTER TABLE adverse_events ALTER COLUMN harm_perform TYPE VARCHAR(1000);
ALTER TABLE adverse_events ALTER COLUMN device_fault_perform TYPE VARCHAR(1000);
ALTER TABLE adverse_events ALTER COLUMN event_cause_analyze TYPE VARCHAR(1000);
ALTER TABLE adverse_events ALTER COLUMN harm_degree_desc TYPE VARCHAR(1000);
ALTER TABLE adverse_events ALTER COLUMN return_reason TYPE VARCHAR(1000);