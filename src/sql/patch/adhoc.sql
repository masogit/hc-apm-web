
insert into sys_role(id, home_page, name, role_desc, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en)
values ( (select max(id)+1 from sys_role) , '/homeHead.xhtml', 'DataMaintainStaff', '数据维护人员',  NULL, NULL, NULL, NULL, 'zkbBasicInfo,zkbBasicInfo.engineerMgt,config,config.accountdash,config.org,config.user,config.customRole', 'Data Maintain Staff');

--DataMaintainStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AconnectPerformanceExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AppTrainingExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ColdHeaderExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','EEMonitorCSV',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','OLERulesExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMResultExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMScheduleExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrResetReport',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UpTimeExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UsConnectStatusExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ZkbAssetTransferExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CarePackageExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CoilOpportunityExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','MSARecommendExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','PromotionActivityExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','TrainingQuotaExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

--GEInternalStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','DealerFEAndSalesExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAccountSendMailExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetCustomerExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetFeExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetOLEExcel',45,'GEInternalStaff',0);

-- geop
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','AssetInfoImportExcel','GeOp',0);
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','GEIBTableExcel','GeOp',0);

