-- 2020-12-30 消息订阅默认配置维护 start
update user_subscription_push set default_send_status=true where sub_status=true;
update user_subscription_push set default_send_status=false where role_position='SalesManager' and menu_type in('1','5','7','8','11','12');
update user_subscription_push set default_send_status=false where role_position='FeManager' and menu_type in('5');

-- 2020-12-30 end

-- 2020-12-28 消息订阅菜单类型 start
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','1','保养报告上传','MaintenanceReportUpload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','2','保养微报上传','MaintenanceMicrosprintUplo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','3','设备月报','MonthlyReports',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','4','关爱报告','CareReport',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','5','监控警报','AssetAlert',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','6','简报消息','Briefings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','7','维修状态','MaintenanceWorks',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','8','维修报告上传','MaintenanceReportUploaded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','9','超声应用指导','USApplication',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','10','增值服务','ValueAddedServices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','11','应用培训状态','AppliedTrainingStatus',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','12','应用培训报告上传','ApplicationTrainingReportUpload',NULL,-1);
-- 2020-12-28 end