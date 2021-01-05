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

-- 2021-01-05 zone_manager数据 start
INSERT INTO "public"."zone_mgr_info" VALUES (4, '2020-12-24 16:15:32.992859', '张章', '305001795', 'North', '河北', '王冰', '305007584', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (5, '2020-12-24 16:15:33.0101', '张章', '305001795', 'North', '天津', '王冰', '305007584', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (6, '2020-12-24 16:15:33.027353', '张章', '305001795', 'North', '山西', '郭敬森', '305019328', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (7, '2020-12-24 16:15:33.043827', '张章', '305001795', 'North', '河南', '郭敬森', '305019328', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (8, '2020-12-24 16:15:33.060393', '张章', '305001795', 'North', '吉林', '宋焕仁', '305007594', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (9, '2020-12-24 16:15:33.07692', '张章', '305001795', 'North', '黑龙江', '宋焕仁', '305007594', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (10, '2020-12-24 16:15:33.097081', '张章', '305001795', 'North', '内蒙', '沈旸', '305001728', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (11, '2020-12-24 16:15:33.113613', '张章', '305001795', 'North', '辽宁', '沈旸', '305001728', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (12, '2020-12-24 16:15:33.1301', '田海武', '305001886', 'East', '上海', '徐晓燕', '305028253', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (13, '2020-12-24 16:15:33.147329', '田海武', '305001886', 'East', '浙江', '范文杰', '305010954', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (14, '2020-12-24 16:15:33.164598', '田海武', '305001886', 'East', '山东', '余斌', '305018562', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (15, '2020-12-24 16:15:33.181077', '田海武', '305001886', 'East', '江苏', '石泰峻', '305001682', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (16, '2020-12-24 16:15:33.197584', '田海武', '305001886', 'East', '安徽', '田海武', '305001886', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (17, '2020-12-24 16:15:33.214224', '田海武', '305001886', 'East', '江西', '田海武', '305001886', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (18, '2020-12-24 16:15:33.230678', 'David.yang', '212674832', 'South', '广东', '林德伟', '305001856', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (19, '2020-12-24 16:15:33.247172', 'David.yang', '212674832', 'South', '海南', '林德伟', '305001856', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (20, '2020-12-24 16:15:33.263624', 'David.yang', '212674832', 'South', '福建', '谢良泽', '305010055', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (21, '2020-12-24 16:15:33.280163', 'David.yang', '212674832', 'South', '广西', '赵军', '305011252', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (22, '2020-12-24 16:15:33.2967', 'David.yang', '212674832', 'South', '湖南', '赵军', '305011252', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (23, '2020-12-24 16:15:33.313833', 'David.yang', '212674832', 'South', '湖北', '李国洲', '305007829', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (24, '2020-12-24 16:15:33.330334', 'Qu, Hongyan', '212813522', 'West', '四川', '孙杰', '305019746', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (25, '2020-12-24 16:15:33.347651', 'Qu, Hongyan', '212813522', 'West', '西藏', '孙杰', '305019746', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (26, '2020-12-24 16:15:33.365573', 'Qu, Hongyan', '212813522', 'West', '重庆', '孙杰', '305019746', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (27, '2020-12-24 16:15:33.382163', 'Qu, Hongyan', '212813522', 'West', '贵州', '苏志峰', '305001897', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (28, '2020-12-24 16:15:33.398631', 'Qu, Hongyan', '212813522', 'West', '云南', '苏志峰', '305001897', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (29, '2020-12-24 16:15:33.415835', 'Qu, Hongyan', '212813522', 'West', '甘肃', '刘忠', '305001744', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (30, '2020-12-24 16:15:33.432313', 'Qu, Hongyan', '212813522', 'West', '宁夏', '刘忠', '305001744', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (31, '2020-12-24 16:15:33.450424', 'Qu, Hongyan', '212813522', 'West', '青海', '刘忠', '305001744', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (32, '2020-12-24 16:15:33.46916', 'Qu, Hongyan', '212813522', 'West', '陕西', '刘忠', '305001744', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (33, '2020-12-24 16:15:33.486481', 'Qu, Hongyan', '212813522', 'West', '新疆', '王元江', '305001754', NULL);
INSERT INTO "public"."zone_mgr_info" VALUES (3, '2020-12-24 15:58:30.137758', '张章', '305001795', 'North', '北京', '陈伟', '305010998', 50);
-- 2021-01-05 zone_manager数据 end