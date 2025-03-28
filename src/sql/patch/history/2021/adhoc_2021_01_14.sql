--2020-01-07 消息订阅 增加'设备维修人'配置 start
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '5', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '6', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '7', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '8', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,default_send_status,role_position)
select '8', FALSE, '9', true, true, true, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '10', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '11', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='deviceRepairer');
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '12', true, false, 'deviceRepairer'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='deviceRepairer');
--2020-01-07 end

--2020-01-05 消息推送配置表修改 start
update user_subscription_push set sub_status=true where menu_type='2' and role_position='FeManager';
--2020-01-05 end

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
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '北京', '陈伟', '305010998',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '河北', '王冰', '305007584',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '天津', '王冰', '305007584',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '山西', '郭敬森', '305019328',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '河南', '郭敬森', '305019328',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '吉林', '宋焕仁', '305007594',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '黑龙江', '宋焕仁', '305007594',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '内蒙', '沈旸', '305001728',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'North', '张章', '305001795', '辽宁', '沈旸', '305001728',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '上海', '徐晓燕', '305028253',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '浙江', '范文杰', '305010954',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '山东', '余斌', '305018562',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '江苏', '石泰峻', '305001682',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '安徽', '田海武', '305001886',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'East', '田海武', '305001886', '江西', '田海武', '305001886',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '广东', '林德伟', '305001856',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '海南', '林德伟', '305001856',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '福建', '谢良泽', '305010055',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '广西', '赵军', '305011252',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '湖南', '赵军', '305011252',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'South', 'David.yang', '212674832', '湖北', '李国洲', '305007829',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '四川', '孙杰', '305019746',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '西藏', '孙杰', '305019746',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '重庆', '孙杰', '305019746',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '贵州', '苏志峰', '305001897',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '云南', '苏志峰', '305001897',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '甘肃', '刘忠', '305001744',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '宁夏', '刘忠', '305001744',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '青海', '刘忠', '305001744',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '陕西', '刘忠', '305001744',now(),null);
INSERT INTO zone_mgr_info(region, igm, igm_sso, zone, zone_mgr, zone_mgr_sso,create_time, season_target) VALUES ( 'West', 'Qu, Hongyan', '212813522', '新疆', '王元江', '305001754',now(),null);
-- 2021-01-05 zone_manager数据 end

-- 2021-01-06 医疗器械分类数据 start
delete from "public"."sys_auth_group";
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0490821da8a34708b3a3872470479aee', '医疗设备', null, '01', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2208968c7cd947d2810f9a049472219e', '有源手术器械', '0490821da8a34708b3a3872470479aee', '01-01', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5fc5b6fa57a946d389a90697c19dde70', '超声手术设备及附件', '2208968c7cd947d2810f9a049472219e', '01-01-0101', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d4ca417c33b64095b1403632f3394480', '超声手术设备', '5fc5b6fa57a946d389a90697c19dde70', '01-01-0101-010101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d192154c68f34b429a707809d77c6bb2', '高强度超声治疗设备', '5fc5b6fa57a946d389a90697c19dde70', '01-01-0101-010102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3b7c86792db34ff081ebb3274d29accd', '超声手术设备附件', '5fc5b6fa57a946d389a90697c19dde70', '01-01-0101-010103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d4ec4aa18aa84df0b21fa74f66fd345d', '激光手术设备及附件', '2208968c7cd947d2810f9a049472219e', '01-01-0102', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dfec4823ae5c48819f897179eba9e86f', '激光手术设备', 'd4ec4aa18aa84df0b21fa74f66fd345d', '01-01-0102-010201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('35b4054911ea4d208d041b6114aa7798', '医用激光光纤', 'd4ec4aa18aa84df0b21fa74f66fd345d', '01-01-0102-010202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba24650fe8d14a7e8c9d70e2d04afc18', '高频/射频手术设备及附件', '2208968c7cd947d2810f9a049472219e', '01-01-0103', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1aa4e6c51abd46ad9529f08abcd9c59b', '高频手术设备', 'ba24650fe8d14a7e8c9d70e2d04afc18', '01-01-0103-010301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dc24dc40b34c493cbae897710453586b', '射频消融设备', 'ba24650fe8d14a7e8c9d70e2d04afc18', '01-01-0103-010302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('830703c9d1cb4f16bcaea9fc0aa2fc98', '氩保护气凝设备', 'ba24650fe8d14a7e8c9d70e2d04afc18', '01-01-0103-010303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('20a0ad91ad7e40fe8967d24c44379f66', '高频/射频用电极及导管', 'ba24650fe8d14a7e8c9d70e2d04afc18', '01-01-0103-010304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('60e46f1bde6b438e8a40de07b80f89fc', '射频消融设备用灌注泵', 'ba24650fe8d14a7e8c9d70e2d04afc18', '01-01-0103-010305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f4ed19c760e042c699577af141280850', '微波手术设备', '2208968c7cd947d2810f9a049472219e', '01-01-0104', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('98d6d6e6e3904be58a09121f191e9fb3', '微波手术设备', 'f4ed19c760e042c699577af141280850', '01-01-0104-010401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('73dcdd82d2294fa38b5b4b0ee3e2434b', '冷冻手术设备及附件', '2208968c7cd947d2810f9a049472219e', '01-01-0105', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8527a862196e482e887693538c0861ec', '冷冻手术设备', '73dcdd82d2294fa38b5b4b0ee3e2434b', '01-01-0105-010501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7bb47dbce7c24a1f9ffa042c05be9860', '冷冻消融针及导管', '73dcdd82d2294fa38b5b4b0ee3e2434b', '01-01-0105-010502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6c89bda9d6dc4a008ec3a28b37e41148', '冲击波手术设备', '2208968c7cd947d2810f9a049472219e', '01-01-0106', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('73974589de174f1abb69aa02a4ff7df0', '冲击波碎石机', '6c89bda9d6dc4a008ec3a28b37e41148', '01-01-0106-010601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aef0f8fa57e24ae3b41f7e6bab8b63bb', '手术导航、控制系统', '2208968c7cd947d2810f9a049472219e', '01-01-0107', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e3bcd69cf4543eca66fc276f6bfc617', '手术导航系统', 'aef0f8fa57e24ae3b41f7e6bab8b63bb', '01-01-0107-010701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4406c2a0982946159d796fb37851f3ad', '手术定位系统', 'aef0f8fa57e24ae3b41f7e6bab8b63bb', '01-01-0107-010702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df9aabbdd8ac47559602195f94bd4cfa', '手术控制系统', 'aef0f8fa57e24ae3b41f7e6bab8b63bb', '01-01-0107-010703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e43202aaef394650b5084b01d802b963', '手术照明设备', '2208968c7cd947d2810f9a049472219e', '01-01-0108', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('60c660fbc52c4bf2bc584e1e4e726c9e', '手术无影灯', 'e43202aaef394650b5084b01d802b963', '01-01-0108-010801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('59f287d3a06a4fe2b8556688828751c7', '手术辅助照明灯', 'e43202aaef394650b5084b01d802b963', '01-01-0108-010802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fd4a176cc8c14cd995b50d4e69898a35', '内窥镜手术用有源设备', '2208968c7cd947d2810f9a049472219e', '01-01-0109', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('43c798ae5adf43ffbdc835f72f42306e', '内窥镜手术用有源设备', 'fd4a176cc8c14cd995b50d4e69898a35', '01-01-0109-010901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('78a044e2fe8b4269b1f7673e0dd1395f', '其他手术设备', '2208968c7cd947d2810f9a049472219e', '01-01-0110', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0a51ab595a2e41329ec032bad4afe2cd', '水刀', '78a044e2fe8b4269b1f7673e0dd1395f', '01-01-0110-011001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b2edaef2d9814644aa17b72655ceff04', '分离控制盒', '78a044e2fe8b4269b1f7673e0dd1395f', '01-01-0110-011002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fadd3a839e554632b648ca2b800f1068', '电动吻合器', '78a044e2fe8b4269b1f7673e0dd1395f', '01-01-0110-011003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c42ed27e743a4dab91880ada990dd4ed', '手术动力系统', '78a044e2fe8b4269b1f7673e0dd1395f', '01-01-0110-011004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('18db1e0e93374323aadc7dc691402d00', '取、植皮设备', '78a044e2fe8b4269b1f7673e0dd1395f', '01-01-0110-011005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('030624d46c3c433a9160832d7d803bc0', '无源手术器械', '0490821da8a34708b3a3872470479aee', '01-02', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1a162b7f5c6543c0806af25bc9b6ec23', '手术器械-刀', '030624d46c3c433a9160832d7d803bc0', '01-02-0201', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e0cc15843fd44fbcbf2c75fc881f8040', '手术刀', '1a162b7f5c6543c0806af25bc9b6ec23', '01-02-0201-020101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5d9abf2d7a7c4bdcb11b5f6ac368c1fa', '血管刀', '1a162b7f5c6543c0806af25bc9b6ec23', '01-02-0201-020102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('257549d8e007408e8b4ace62f83b4160', '备皮刀', '1a162b7f5c6543c0806af25bc9b6ec23', '01-02-0201-020103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('66a7a371aa884c0ea17854f426b379ac', '环切器', '1a162b7f5c6543c0806af25bc9b6ec23', '01-02-0201-020104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8242b56ff0354bdc814c64421f7f74d9', '内窥镜用刀', '1a162b7f5c6543c0806af25bc9b6ec23', '01-02-0201-020105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('de7a95398b85426d8f384807d622f325', '手术器械-凿', '030624d46c3c433a9160832d7d803bc0', '01-02-0202', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('67f5cae9a9fb465fa08e1479b920e6c3', '手术凿', 'de7a95398b85426d8f384807d622f325', '01-02-0202-020201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4c453713dcde4c1f96df7447ef8d114c', '手术锤', 'de7a95398b85426d8f384807d622f325', '01-02-0202-020202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('03f2c7f5b3f144f287f499c63ee4acc2', '手术器械-剪', '030624d46c3c433a9160832d7d803bc0', '01-02-0203', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e29755f68a71477986efe66ba1e1c640', '组织剪', '03f2c7f5b3f144f287f499c63ee4acc2', '01-02-0203-020301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5bf1c58937be44e08c3c92e9b1b9eeae', '器械剪', '03f2c7f5b3f144f287f499c63ee4acc2', '01-02-0203-020302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1670099c1aa84782933c8d5673033146', '内窥镜用剪', '03f2c7f5b3f144f287f499c63ee4acc2', '01-02-0203-020303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7a842b31e96d41f9a959ff4850d53e44', '手术器械-钳', '030624d46c3c433a9160832d7d803bc0', '01-02-0204', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7af62474addc4a8e943b2f73615cc61d', '组织钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3990ac002c2b4d6babdc92e7190b33f0', '取样钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f415f62ce1ab4ad3a70d3f036e264975', '分离钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5b5b92744ea8492f8c25b8ef47342006', '牵引钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c5b5da5a93544a898c21a761d371ae25', '异物钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2f0442b61ec04d34b797d471eb09d99e', '止血钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('38e7070731524953b4ac78259e91f167', '扩张钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('055ecad673fa4a38b8ff19db19c518c9', '器械钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0113fe5d5b3946e59ee78dbd480a5395', '内窥镜用组织钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020409', '0409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4439921f443847a5b58be45bad237f2c', '内窥镜用取样钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020410', '0410');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('45f5c840fb224f2d8e83dd1ab3e87176', '内窥镜用分离钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020411', '0411');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('60a520be95c64cef800c5999741b3004', '内窥镜用异物钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020412', '0412');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f59b5573211644afb701667995db50ca', '内窥镜用器械钳', '7a842b31e96d41f9a959ff4850d53e44', '01-02-0204-020413', '0413');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('39c8fbb28b364a939ce69838556b73e0', '手术器械-镊', '030624d46c3c433a9160832d7d803bc0', '01-02-0205', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2acdc910526949c38f9b279bd6f00238', '组织镊', '39c8fbb28b364a939ce69838556b73e0', '01-02-0205-020501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('02adef9baad949209c8683381e109ba0', '器械镊', '39c8fbb28b364a939ce69838556b73e0', '01-02-0205-020502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('496c33ae77704ee2b14113c96cacbd94', '手术器械-夹', '030624d46c3c433a9160832d7d803bc0', '01-02-0206', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d3e20e13eaf44fef962d4f4c60d1d4c4', '闭合夹', '496c33ae77704ee2b14113c96cacbd94', '01-02-0206-020601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba59d625be5e4a05b14c79c095da3955', '止血夹', '496c33ae77704ee2b14113c96cacbd94', '01-02-0206-020602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7fb288dde6534a048e0b2953eb868d23', '器械夹', '496c33ae77704ee2b14113c96cacbd94', '01-02-0206-020603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8a0063e6f2664eef86d619021cb83e4c', '手术器械-针', '030624d46c3c433a9160832d7d803bc0', '01-02-0207', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9e7fa094ed35431086a822c1b5f8997b', '缝合针', '8a0063e6f2664eef86d619021cb83e4c', '01-02-0207-020701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('88e1e311c66c48fcbaf6efb375d83fbc', '手术针', '8a0063e6f2664eef86d619021cb83e4c', '01-02-0207-020702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a7ca629cf9f444689c34d7173568036e', '定位针', '8a0063e6f2664eef86d619021cb83e4c', '01-02-0207-020703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('39e736e3c5d14e178fa818b076bb10af', '内窥镜取样针', '8a0063e6f2664eef86d619021cb83e4c', '01-02-0207-020704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('58f83494fd894ac19501a3d9d17a6ba6', '手术器械-钩', '030624d46c3c433a9160832d7d803bc0', '01-02-0208', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('50c64a7cd3be49978305967ed82685d5', '手术钩', '58f83494fd894ac19501a3d9d17a6ba6', '01-02-0208-020801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6ee7de9a9b94f90a358a7a190ea02f7', '内窥镜用钩', '58f83494fd894ac19501a3d9d17a6ba6', '01-02-0208-020802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db56765f8bff416ba90b67c9b2c256fd', '手术器械-刮匙', '030624d46c3c433a9160832d7d803bc0', '01-02-0209', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('312de00999424180b4372b5d2c8b75ef', '手术刮匙', 'db56765f8bff416ba90b67c9b2c256fd', '01-02-0209-020901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('187306fda5d84537b713c041e8cbadb0', '内窥镜用组织刮匙', 'db56765f8bff416ba90b67c9b2c256fd', '01-02-0209-020902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8ed9d7566b864f5e91742ddf40b74cd5', '手术器械-剥离器', '030624d46c3c433a9160832d7d803bc0', '01-02-0210', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6e904a41095542068c59cea70b4b755c', '剥离器', '8ed9d7566b864f5e91742ddf40b74cd5', '01-02-0210-021001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f72c5fc916094d80ade45639ef43b2ba', '内窥镜用剥离器', '8ed9d7566b864f5e91742ddf40b74cd5', '01-02-0210-021002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7bebf3a853b844a0bcdc57f9a26055d0', '手术器械-牵开器', '030624d46c3c433a9160832d7d803bc0', '01-02-0211', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ac00238613034478927dcdf338421e67', '牵开器', '7bebf3a853b844a0bcdc57f9a26055d0', '01-02-0211-021101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0c4c64cb01a040309cbb6691650c76d4', '压迫器', '7bebf3a853b844a0bcdc57f9a26055d0', '01-02-0211-021102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd8c82ef9cc24ab7a6bfcd7e9cf1c24e', '扩张器', '7bebf3a853b844a0bcdc57f9a26055d0', '01-02-0211-021103', '1103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('29c442dd46174b21bdad416c0d003e6d', '内窥镜用牵开器', '7bebf3a853b844a0bcdc57f9a26055d0', '01-02-0211-021104', '1104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93e1dd1ca8414aa2970b4d7a37e56f69', '内窥镜用气囊扩张器', '7bebf3a853b844a0bcdc57f9a26055d0', '01-02-0211-021105', '1105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7edcf50c4b5041d9bb3dc2a4099a4657', '手术器械-穿刺导引器', '030624d46c3c433a9160832d7d803bc0', '01-02-0212', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c1cedffb601d4eb4a20784b416496302', '穿刺器', '7edcf50c4b5041d9bb3dc2a4099a4657', '01-02-0212-021201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5437b0c65d4549d98ca14dffdf7d7833', '打孔器', '7edcf50c4b5041d9bb3dc2a4099a4657', '01-02-0212-021202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a84b741593094168be80cd77d3791959', '输送导引器', '7edcf50c4b5041d9bb3dc2a4099a4657', '01-02-0212-021203', '1203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('301153806d7e481da02a8fee46d3a49c', '微创入路装置', '7edcf50c4b5041d9bb3dc2a4099a4657', '01-02-0212-021204', '1204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('138c59cd50894b8d89d0f30efffae348', '内窥镜用导引器', '7edcf50c4b5041d9bb3dc2a4099a4657', '01-02-0212-021205', '1205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('03930b4fdc754d178a349af2b0d7f54a', '手术器械-吻（缝）合器械及材料', '030624d46c3c433a9160832d7d803bc0', '01-02-0213', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9ed22ef60da94323bd42694e407a76a3', '吻合器（带钉）', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('063e56298b2640fca69acf551be11454', '吻合器（不带钉）', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a9b4c7fcbd74b8b9632d83355fa6bed', '内窥镜用吻（缝）合器械（不带钉）', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021303', '1303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c66361b3db54a319817b809e23b225a', '血管缝合装置', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021304', '1304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('129dce91fc714a6f8559f3c4a7972264', '施夹器', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021305', '1305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d03892b57c84471b81bfffd46f8af8a4', '可吸收缝合线', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021306', '1306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('438c5a167dea459e87d74afc9e471ccd', '不可吸收缝合线', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021307', '1307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('77364c387c3b4a4aa2e942d23946201c', '免缝闭合器械', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021308', '1308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('421118437f3a44e386d28a4771dea88b', '粘合剂', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021309', '1309');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e2ad68dbc0d4d09b812f35b7cbc65b4', '粘堵剂', '03930b4fdc754d178a349af2b0d7f54a', '01-02-0213-021310', '1310');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0ef66dd4f62c4932992f9f410bd5d2f5', '手术器械-冲吸器', '030624d46c3c433a9160832d7d803bc0', '01-02-0214', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4cd3482ba37c4b769a806df91a20387a', '冲吸器', '0ef66dd4f62c4932992f9f410bd5d2f5', '01-02-0214-021401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7b289f3ffaf34b25a5e731e6a709e9be', '吸引器', '0ef66dd4f62c4932992f9f410bd5d2f5', '01-02-0214-021402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4d1cb727f0714e1e9cca60d46fd306d8', '手术器械-其他器械', '030624d46c3c433a9160832d7d803bc0', '01-02-0215', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b783c091ddcc41279e276db5c42ec293', '套扎器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021501', '1501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fa21ef43bb8d4765bce9ef34c4e72411', '推结器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021502', '1502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1b0a27325fa4c38bc7596b212f79be3', '固位器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021503', '1503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e4842a832be040a68d91887ca2138b19', '清洁器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021504', '1504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b288d6d2d23f49e8857ab3b08311ce24', '测量器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021505', '1505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e3e19080e83045fc8fe86c07b2635011', '保护器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021506', '1506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3c86300f702e4cfbb79eb28f5698e4bd', '植皮器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021507', '1507');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ebf0bf442df84a1c94f08db8a933a19d', '标记器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021508', '1508');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('79a2c31ba4ad4957ade1385793134976', '手柄', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021509', '1509');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7f47445112d045ddb0456d085c5f9e09', '手术锉', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021510', '1510');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('756588c3809f4118b8c6ee73ca31ca99', '手术叉', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021511', '1511');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e69d784180f14245b7492649b25b90e2', '手术环', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021512', '1512');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dbed081324b14e3ca51a540bbff7e9e6', '试模', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021513', '1513');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1b939e4c8f4b4828842c729991091775', '夹子装置', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021514', '1514');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('69f5cc313e7a4c25b67b579a80fa4efa', '肛门镜', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021515', '1515');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7d11f69e7a9e4e32a794f68753e88df3', '内窥镜用推结器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021516', '1516');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('66d9dfb68288421f92b1e78ad7e376b0', '内窥镜用细胞刷', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021517', '1517');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('620fb82c1b2647cd9ae61ab639b360af', '内窥镜用取石器械', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021518', '1518');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a6b4a818b0d74f9eb2abc1acbecbdef9', '内窥镜切口牵开保护器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021519', '1519');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0ea2093ed4a478a98dfc9a06910068e', '内窥镜用取石球囊导管', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021520', '1520');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e95645dc5bb24daa863b421ca97ef622', '内窥镜用气囊导管', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021521', '1521');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7ea10714b1af4b5c8bc1f08ddda367ca', '内窥镜用给物器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021522', '1522');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('09251a829dd142bea7d9f7c058a03ece', '内窥镜用套扎器', '4d1cb727f0714e1e9cca60d46fd306d8', '01-02-0215-021523', '1523');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('844104d885d44d2c9757d2b8306dde50', '神经和心血管手术器械', '0490821da8a34708b3a3872470479aee', '01-03', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bdafb12618be4293a909b64343d53d22', '神经和心血管手术器械-刀', '844104d885d44d2c9757d2b8306dde50', '01-03-0301', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c4dc975a8ae64293930a761e196aa1e8', '手术刀', 'bdafb12618be4293a909b64343d53d22', '01-03-0301-030101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('897e463a3f374b42883b95635384de71', '神经和心血管手术器械-剪', '844104d885d44d2c9757d2b8306dde50', '01-03-0302', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('162cbacb65b145b295c3de7c02200881', '组织剪', '897e463a3f374b42883b95635384de71', '01-03-0302-030201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb4c9d967fff4da49e0596bdca552409', '神经和心血管手术器械-钳', '844104d885d44d2c9757d2b8306dde50', '01-03-0303', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('980d2dd4230242c0b1defadfeda4e7f6', '组织钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c5f1872a6ab94aa8a2ce78dbe8d77290', '取样钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5520eee6bbf44e16b5b7de4ae1b421e4', '分离钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e62879c9472a44ac82225fc9fa980ae6', '止血钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c9007c136fe9405e817ced179aa13e73', '异物钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df67071e8201472c940fe02116cf695d', '器械钳', 'eb4c9d967fff4da49e0596bdca552409', '01-03-0303-030306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3985a0eb147b4a1cba67c0c9817efed9', '神经和心血管手术器械-镊', '844104d885d44d2c9757d2b8306dde50', '01-03-0304', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c92923492b7b47d0b3a58eae10ff1c76', '组织镊', '3985a0eb147b4a1cba67c0c9817efed9', '01-03-0304-030401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e0d1daf26af14f53b94484ecbc372fc4', '摘除镊', '3985a0eb147b4a1cba67c0c9817efed9', '01-03-0304-030402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0920d534053f4e7ab052b9622391b899', '神经和心血管手术器械-夹', '844104d885d44d2c9757d2b8306dde50', '01-03-0305', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ff9d63d2eaee478788df8e225cf97595', '头皮夹', '0920d534053f4e7ab052b9622391b899', '01-03-0305-030501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fafc5a2d18c44cea80b150b67debe204', '止血夹', '0920d534053f4e7ab052b9622391b899', '01-03-0305-030502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d942ed2d18b44772b5b219d41fe61621', '神经和心血管手术器械-针', '844104d885d44d2c9757d2b8306dde50', '01-03-0306', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cdf0c0d83975481085fb89e937e10f31', '手术针', 'd942ed2d18b44772b5b219d41fe61621', '01-03-0306-030601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ecd6ca83131f4900928809751fc62c10', '排气针', 'd942ed2d18b44772b5b219d41fe61621', '01-03-0306-030602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dfa9a58beda244e6ac140aac174289c4', '神经和心血管手术器械-钩', '844104d885d44d2c9757d2b8306dde50', '01-03-0307', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5f626f00d6cd42d78aeb7aa09fa41fa0', '手术钩', 'dfa9a58beda244e6ac140aac174289c4', '01-03-0307-030701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b9b63aadea214e51973c20859b3010af', '神经和心血管手术器械-刮匙', '844104d885d44d2c9757d2b8306dde50', '01-03-0308', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3f95f89d797643a4bae914e8f11bd87f', '刮匙', 'b9b63aadea214e51973c20859b3010af', '01-03-0308-030801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('64562aae6a344cc4ab8c429e12d7161d', '神经和心血管手术器械-剥离器', '844104d885d44d2c9757d2b8306dde50', '01-03-0309', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('16571e638edb4d19a375bfb530f6a864', '剥离器', '64562aae6a344cc4ab8c429e12d7161d', '01-03-0309-030901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('19ada0751b89461fac3f70381388d1e6', '神经和心血管手术器械-牵开器', '844104d885d44d2c9757d2b8306dde50', '01-03-0310', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6cb577acd24f44459155d5832e3ea94e', '牵开器', '19ada0751b89461fac3f70381388d1e6', '01-03-0310-031001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aab6aa16ac544c839f629774afb155e1', '压器', '19ada0751b89461fac3f70381388d1e6', '01-03-0310-031002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3c3943edd3d046cb970ce18190a8c8c4', '扩张器', '19ada0751b89461fac3f70381388d1e6', '01-03-0310-031003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d69f22165554458cbafc6ef07375f82f', '神经和心血管手术器械-穿刺导引器', '844104d885d44d2c9757d2b8306dde50', '01-03-0311', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f88190281dd04de5b26fa0605cb029a1', '打孔器', 'd69f22165554458cbafc6ef07375f82f', '01-03-0311-031101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('998fa879449e42d1b67d00c77349c96b', '导引器', 'd69f22165554458cbafc6ef07375f82f', '01-03-0311-031102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c81908a17a404deaac8a15e49c3334a5', '神经和心血管手术器械-冲吸器', '844104d885d44d2c9757d2b8306dde50', '01-03-0312', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b799853551ed4cefa5a17f0a6cb09dc0', '冲吸器', 'c81908a17a404deaac8a15e49c3334a5', '01-03-0312-031201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9fd85bfb953e4a6bb1e6723a71c5bc67', '通条', 'c81908a17a404deaac8a15e49c3334a5', '01-03-0312-031202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('19f4d4ef26e94c008bd53fa9be126423', '吸引器', 'c81908a17a404deaac8a15e49c3334a5', '01-03-0312-031203', '1203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0ea0e38bf2154fee92a398c62ebefa91', '神经和心血管手术器械-心血管介入器械', '844104d885d44d2c9757d2b8306dde50', '01-03-0313', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2ddc77b59ee14b4da8e734b316641013', '造影导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db918a6360604d358156591530f7dc10', '导引导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cc4a35dd3b3c43dcb5183fdd97cdbc68', '中心静脉导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031303', '1303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('28de36da41804e0594238dabd856492f', '导管消毒连接器', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031304', '1304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4d6f9dbcaf99443a829c902f14bf3c8e', '灌注导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031305', '1305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9774536ed0694770828db08af8e040f9', '球囊扩张导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031306', '1306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('09dc923ffc86421ba771bcc716d2ae06', '切割球囊', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031307', '1307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('405aa38f664847faa7b818923ac0691e', '造影球囊', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031308', '1308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a22c5b1b1b8d4150937d0a9e625dc36a', '封堵球囊', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031309', '1309');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e073f5d93d7547709200031e4ba83d40', '血栓抽吸导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031310', '1310');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a8d6adf0c9dc4c26a5fd9eaf41d19a18', '套针外周导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031311', '1311');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('929b61792dfd4b80b35462673c166ed9', '穿刺针', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031312', '1312');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('43abaeefeee44a5ab786e2b7db06789f', '导引套管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031313', '1313');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('67cbe8bec89f47a9bcb12081ba36d980', '导管鞘', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031314', '1314');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e67c382d71354f0eb6fe968524d3711c', '扩张器', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031315', '1315');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0aac630cc6854cd999fbd92ae5ec1c66', '导丝', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031316', '1316');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('560a0f81a0254ffeb1cfe91e0fd81083', '球囊扩张导管用球囊充压装置', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031317', '1317');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e353ace83c2a4a8297d226d9c18ce736', '连接阀', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031318', '1318');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c8549852297945c1b772d77ceeaf46c2', '腔静脉滤器回收装置', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031319', '1319');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db21461c669d47a88da6c9be5823e861', '心脏封堵器装载器', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031320', '1320');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e8e4c0b158d54ecc81806c948773c42c', '心脏封堵器输送线缆', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031321', '1321');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a9930ec2bc6c436698c837991a879a74', '血管内回收装置', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031322', '1322');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('70e7773200004f24966b346957ff3007', '远端保护器', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031323', '1323');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f5ef5b80ba2e41d1a48e714424c8acfd', '环柄注射器', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031324', '1324');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('82fa946979f14c218be8b71f19aff0a6', '延长管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031325', '1325');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6b7658d875a440e98a7bac5291ab7be', '微导管', '0ea0e38bf2154fee92a398c62ebefa91', '01-03-0313-031326', '1326');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5194ddc07cd14a999efc2e3eac26fdb0', '神经和心血管手术器械-其他器械', '844104d885d44d2c9757d2b8306dde50', '01-03-0314', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1d9f0493fc8c4436bb7615e6c3608971', '分流栓', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dcc27458bea143fa91bccaf94917f920', '固位器', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ad2b2033e09844789b285464ffbd1e65', '推结器', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031403', '1403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('30b6608ae6a84165b61810547a97f35b', '排线器', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031404', '1404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fcb2106801c8459bb51ddcbdafbcfeb0', '手术叉', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031405', '1405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('78eca0f9051d4800aa1b18a8801447c6', '合拢器', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031406', '1406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a2675a8a57b8418b91e1c2688d192755', '测量器', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031407', '1407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('803066a6ebf54e579f79d3e74bd95053', '手柄', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031408', '1408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('11b81127403d4b74bc9277094bbef803', '手术锯', '5194ddc07cd14a999efc2e3eac26fdb0', '01-03-0314-031409', '1409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fee1a5de42e743cb91b90a8909828445', '骨科手术器械', '0490821da8a34708b3a3872470479aee', '01-04', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fb1256c8480f4e1b8739f76d6c9e929a', '骨科用刀', 'fee1a5de42e743cb91b90a8909828445', '01-04-0401', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cf01fa37506f4f1dae1788b4e6642ad3', '截骨用刀', 'fb1256c8480f4e1b8739f76d6c9e929a', '01-04-0401-040101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('42028b3dfa2b4ce6a047ad648da9671a', '骨科内窥镜用刀', 'fb1256c8480f4e1b8739f76d6c9e929a', '01-04-0401-040102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ca86e60041244c0eb578efd5cf13127f', '扩孔用刀', 'fb1256c8480f4e1b8739f76d6c9e929a', '01-04-0401-040103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ed4ae01794a9410a9b577ec6983066d6', '石膏切割用刀', 'fb1256c8480f4e1b8739f76d6c9e929a', '01-04-0401-040104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('002f3d565e9d4813987bd8f0f191fc1b', '骨科用剪', 'fee1a5de42e743cb91b90a8909828445', '01-04-0402', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ef1eaec6eff7431d8780e55f7ec5e7b4', '骨科内窥镜用剪', '002f3d565e9d4813987bd8f0f191fc1b', '01-04-0402-040201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6371131bd7a43329d849c82f9cca41d', '骨及组织用剪', '002f3d565e9d4813987bd8f0f191fc1b', '01-04-0402-040202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('57c910e3410d44db92522f5b3bf11a74', '植入物或石膏用剪', '002f3d565e9d4813987bd8f0f191fc1b', '01-04-0402-040203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('28c70e82901d4d268ddebca47f772c58', '骨科用钳', 'fee1a5de42e743cb91b90a8909828445', '01-04-0403', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a85a1495cdde4f948079759ad4bab709', '骨科内窥镜用钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0506918c86fd4a6482aa0e373f699078', '夹持/复位用钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2a260ccb51974cc593466ca22fb74f62', '咬骨钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cddd5a7dce054448b0042c59d0599a64', '组织用钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('17239fe508a340eab99da2b2c165e8c8', '撑开钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4c6f0b3db502412bb9596b6ed1c954cc', '压缩钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c741b42d0ba242748786384b21b1f7fe', '植入物塑形用钳', '28c70e82901d4d268ddebca47f772c58', '01-04-0403-040307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8d49bb54afb487aa599b422d6ee4212', '骨科用钩', 'fee1a5de42e743cb91b90a8909828445', '01-04-0404', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1e0a5bf89549463d91d68f552e1b94a8', '拉钩', 'b8d49bb54afb487aa599b422d6ee4212', '01-04-0404-040401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2dcfbfd5dc184d26a633338e4100826f', '牵开器', 'b8d49bb54afb487aa599b422d6ee4212', '01-04-0404-040402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1d5f3d58d84741b7b9a7a9e6995ee0dc', '骨钩', 'b8d49bb54afb487aa599b422d6ee4212', '01-04-0404-040403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('65264c7c587e4f55afa8937aaab5db06', '骨科用针', 'fee1a5de42e743cb91b90a8909828445', '01-04-0405', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4a8f08b489814773a3fc70e882c767c5', '探针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('600c6307d8c84d26b9e5b197c4219b36', '牵引针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e5506ce798104b5c967fafcfc4e128a2', '定位导引针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('947f896875b54696a49864721e217c2b', '固定针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ae93764cfd174cbbb3f710084eb478c2', '穿孔针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('38e0130a2b694e2cbb4806de6916ac57', '切割针', '65264c7c587e4f55afa8937aaab5db06', '01-04-0405-040506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0c060b33bdd94e0e9785f4e3120be3e0', '骨科用刮', 'fee1a5de42e743cb91b90a8909828445', '01-04-0406', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4bf3580fbb1d450a9d5bee831f2255a8', '骨科内窥镜用刮匙', '0c060b33bdd94e0e9785f4e3120be3e0', '01-04-0406-040601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('445d8f1c468b46fa809a3a7bd459f379', '刮匙', '0c060b33bdd94e0e9785f4e3120be3e0', '01-04-0406-040602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d354c8337eba4978b42303023448ec76', '骨科用锥', 'fee1a5de42e743cb91b90a8909828445', '01-04-0407', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0c9c22f05b1b4562ae52f8e50f75cd4e', '介入术用骨锥', 'd354c8337eba4978b42303023448ec76', '01-04-0407-040701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('adbd0f86cb344ca2b42e4400f148be23', '开口用锥', 'd354c8337eba4978b42303023448ec76', '01-04-0407-040702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ac875e3af8e244dda3d3a07f6a6cbdb4', '攻丝用锥', 'd354c8337eba4978b42303023448ec76', '01-04-0407-040703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dde25893eb5f4d5c8de4577bccc80b21', '骨科用钻', 'fee1a5de42e743cb91b90a8909828445', '01-04-0408', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86f9c6fabc894423b6eb1951b4f48909', '切/取骨钻', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('54adec9ebe52493195232472efee9227', '钻孔用钻', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b73f01268c324e7f88448c483647cc89', '导钻（套钻）', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('359840fa509c43beb522dfbb7c6499b4', '修整用钻', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040804', '0804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8009a8064ca545da9ae691c483e0710d', '扩髓用钻', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040805', '0805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5c1ccc0f35d04e918a2603e34171b401', '芯钻', 'dde25893eb5f4d5c8de4577bccc80b21', '01-04-0408-040806', '0806');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bcfe23961b8a406a8081d4cffa491e98', '骨科用锯', 'fee1a5de42e743cb91b90a8909828445', '01-04-0409', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bc23e68382c8408dbf9d451bf445135c', '骨锯', 'bcfe23961b8a406a8081d4cffa491e98', '01-04-0409-040901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('339b38878d8040dfb4668b38eb7cfee9', '石膏锯', 'bcfe23961b8a406a8081d4cffa491e98', '01-04-0409-040902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8be17757e55e475c915f6e233796177a', '配套工具', 'bcfe23961b8a406a8081d4cffa491e98', '01-04-0409-040903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('834fd7cf7879492ab94b5f2fc3eae3a4', '骨科用凿', 'fee1a5de42e743cb91b90a8909828445', '01-04-0410', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('27ec5bfd2f5745ae8e10255786426029', '骨凿', '834fd7cf7879492ab94b5f2fc3eae3a4', '01-04-0410-041001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('600fd32c60aa40fe86a50d689606ab52', '骨科用锉、铲', 'fee1a5de42e743cb91b90a8909828445', '01-04-0411', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fae2dad2fd4c4801837d07efc8fd0be2', '骨科用锉', '600fd32c60aa40fe86a50d689606ab52', '01-04-0411-041101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1a9382ff1de14bda9a725fa3241192cc', '骨科用铲', '600fd32c60aa40fe86a50d689606ab52', '01-04-0411-041102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f55695cf51c4496ea0b1de16dd24d205', '骨科用有源器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0412', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1bbe61a6430a4726a9fa0e9f5a0132ea', '骨科动力手术设备', 'f55695cf51c4496ea0b1de16dd24d205', '01-04-0412-041201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('afddc1fd3dc442e89abe409d2b9dfa7a', '配套工具', 'f55695cf51c4496ea0b1de16dd24d205', '01-04-0412-041202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c24790c25c904b58b912e6a4f52b6d24', '石膏切割器具', 'f55695cf51c4496ea0b1de16dd24d205', '01-04-0412-041203', '1203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('74c291bdde944ef8b7fc8af7628be875', '外固定及牵引器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0413', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('46fd76b1756e4dea9361fe13dfbc068c', '髌骨爪', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5592ae0dba6a4de1a12fbdf553f5fdad', '外固定支架', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7b8a462d8ddf4feb80126e8026480977', '夹板及固定带', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041303', '1303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('58b09279d05c496fb36ff1712e1a9307', '牵引器', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041304', '1304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e0e964073e3d47b4a2a36dfd0388f5fe', '术中牵引架及配件', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041305', '1305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c4432713fffa4b75a1419c77d6862565', '骨科牵引床及配件', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041306', '1306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c754a2ea8db64c9ba9483d0a947eb2b8', '紧固、支撑工具', '74c291bdde944ef8b7fc8af7628be875', '01-04-0413-041307', '1307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b7e5cf5a04b84c899333d490f2051c8c', '基础通用辅助器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0414', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7397d87621c64f63a5bd4e705e5128b1', '微创骨导引器', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1531bce17de6486ab8a4260709490028', '骨水泥器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0c876d654b7045d7ba564f66c1bf6cd9', '植骨器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041403', '1403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1ec255cf74f43f78d1d03ba1e8befce', '取样器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041404', '1404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('09134a68f2de4f0192dc168724430a1e', '测量器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041405', '1405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f178dc3b99f740fe96876ceec13cc1df', '定位导向器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041406', '1406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cc72794d586547748ea3dee2e43dc75a', '夹持、固定器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041407', '1407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a9c0c91505dd4b9fbd47de792c44d17d', '敲拔器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041408', '1408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('980ec81afbc74512a2f490763c8c21e3', '开口器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041409', '1409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c824acbd7cb14832b9687528bda5b46c', '石膏拆除器械', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041410', '1410');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('42eda8a78fc047d0a9a37d0642529018', '刨骨器', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041411', '1411');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b305c729142c4550ab5992512bb96b25', '植入取出工具', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041412', '1412');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4deafa11c7714cf793a87db848df5788', '配套工具', 'b7e5cf5a04b84c899333d490f2051c8c', '01-04-0414-041413', '1413');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2f49998b91fa4bed988dd68f6a2e10f0', '创伤外科辅助器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0415', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2b2593d9508343618bb359a0a6f43f52', '扩髓器', '2f49998b91fa4bed988dd68f6a2e10f0', '01-04-0415-041501', '1501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f9b0396da0c84fc59b1555e2f9f956ce', '骨把持器', '2f49998b91fa4bed988dd68f6a2e10f0', '01-04-0415-041502', '1502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('edf73f0f08e84a719ffc6528380e8669', '塑形工具', '2f49998b91fa4bed988dd68f6a2e10f0', '01-04-0415-041503', '1503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b39b932c2abe4dc7a5fdc21948c99579', '骨折复位器', '2f49998b91fa4bed988dd68f6a2e10f0', '01-04-0415-041504', '1504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3f81aa9c1bc94ac4b2d69fa3a7d9ce5c', '配套工具', '2f49998b91fa4bed988dd68f6a2e10f0', '01-04-0415-041505', '1505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('47eddccf7c1e4881b6dba5c0fc071c21', '关节外科辅助器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0416', '16');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8281e4e15f0a44288950e764e8c1d7a0', '骨水泥定型模具', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041601', '1601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fe90a5c210344af9be8615a260fa1954', '关节镜配套工具', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041602', '1602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('205ce0b391fc42789f21a3ea0d476eb6', '定位、导向、测量器械', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041603', '1603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1a012373fa3e45d3adfb2751eb9f1be0', '打拔器', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041604', '1604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('62a8e9e38736424584be2ed7a38c0551', '冲头', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041605', '1605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9f82be77b210414a86e5e065d3492b30', '配套工具', '47eddccf7c1e4881b6dba5c0fc071c21', '01-04-0416-041606', '1606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('efab8e587c6e4c2eba885e1de17e142d', '脊柱外科辅助器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0417', '17');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c303ec9fe3f047c68ce259ac5eb73b6f', '椎体成形器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041701', '1701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('931b085fc69c4d85b6267f162b29655b', '椎间盘旋切器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041702', '1702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0bb93788b5714a7f9d0aedbd86af8a0d', '注射推进装置', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041703', '1703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bdc56894c6b44563adde9ec712bacc9f', '椎体成形导引系统', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041704', '1704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a405a58c37404e7ea1ee0766cd7f3017', '纤维环缝合器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041705', '1705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('94dc531779814cb494eff0462e28ec0f', '椎体后缘处理器', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041706', '1706');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fdfd3333729a411bb085989fcabce0c4', '椎弓根定位测量器', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041707', '1707');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('07f67cf7cbfe41d7b9242aae9f13537e', '定位、导向、测量器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041708', '1708');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4dd12e83345e4cacab60d76ae211a77f', '开孔扩孔器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041709', '1709');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8740b0334c114bd8bdeade16f9d43f0a', '神经根探子', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041710', '1710');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4b0bd046d66840f8bdb3d61f4c08dbed', '植骨块嵌入器', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041711', '1711');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('85dc366f70844193869cb48d4b9df6dc', '椎弓根钉尾部切断器', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041712', '1712');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('72cb9ecde9554f479a43c2d1a08ab581', '脊柱手术通道器械', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041713', '1713');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bcdea9511f0d49b7b212496299167a47', '椎体复位器', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041714', '1714');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b9bf96a210cf4fbd85d8044b3a371648', '配套工具', 'efab8e587c6e4c2eba885e1de17e142d', '01-04-0417-041715', '1715');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1a8eabcb8fd6489ab172637315f294da', '骨科其他手术器械', 'fee1a5de42e743cb91b90a8909828445', '01-04-0418', '18');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c7c791fdb1694109836ff6c942b8056f', '剥离器', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041801', '1801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ed07d4e87557429ba87d25b7bb197d7c', '颅骨矫形器械', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041802', '1802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ee25ed0dc8054121a8d89425b74928e5', '剥离保护器', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041803', '1803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d57e58f9f04441f99a78a1b5318589af', '韧带手术器械', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041804', '1804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61bbd75e115a421d86f89bf151385e1e', '骨科组织保护器具', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041805', '1805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f97047dc27514754969e7b4da4fb82b6', '骨科手术体位固定架', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041806', '1806');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('78286b6c72154255a056bf9ce92ee41e', '软骨整形器械', '1a8eabcb8fd6489ab172637315f294da', '01-04-0418-041807', '1807');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('866a8e1ecc54442a84f0c4c960a39136', '放射治疗器械', '0490821da8a34708b3a3872470479aee', '01-05', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba69f6ae343a4d33ac8536d4edc011ec', '放射治疗设备', '866a8e1ecc54442a84f0c4c960a39136', '01-05-0501', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4afc9a560e934f53aee736d717224eaa', '医用电子加速器', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f9c4a8c3270c487483c96ad6b989a323', '医用轻离子治疗系统', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ec0936c0553f4faaa65bfa18f2c69176', '医用X射线治疗设备', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9dd1d721903f4f2f92ff5c282a8b7ba3', '伽玛射束远距离治疗机', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('606ef5688bf04c06971ef7671c293dd1', '近距离后装治疗设备', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bcf057babb2943b3884affdcff71abd9', '放射性粒籽植入治疗系统', 'ba69f6ae343a4d33ac8536d4edc011ec', '01-05-0501-050106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3d9404d61a744a2b90542a649e94907a', '放射治疗模拟及图像引导系统', '866a8e1ecc54442a84f0c4c960a39136', '01-05-0502', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d50598a986134659966c3e6f1ecfd015', '放射治疗模拟系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b836088117974402923bb29c53e0919d', '放射治疗用X射线图像引导系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('108b02ca865e495ab25562f6450dc7d8', '电子射野成像系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('456323f90dca4e9fafcba4112913c5e8', '超声影像引导系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b31ca54370dd413d923837695e3d4c26', '电磁定位系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8f08c3b78777466ba06326ca78a428d9', '光学定位引导系统', '3d9404d61a744a2b90542a649e94907a', '01-05-0502-050206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a00c20b15a304bcaa1b8351856251217', '放射治疗准直限束装置', '866a8e1ecc54442a84f0c4c960a39136', '01-05-0503', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a057d18f1188487d95da34dfaf37635f', 'X辐射放射治疗立体定向系统', 'a00c20b15a304bcaa1b8351856251217', '01-05-0503-050301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('30a50479bf9d4d53b4fa32af487efbe8', '准直限束装置', 'a00c20b15a304bcaa1b8351856251217', '01-05-0503-050302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da43c9f828014dad903fba2e65fa1811', '放射治疗配套器械', '866a8e1ecc54442a84f0c4c960a39136', '01-05-0504', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e63a36cbb4a849f09a8215df378ca096', '射线束扫描测量系统', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('058bbfcffbff45658d2ed83175379e4b', '呼吸门控系统', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('04db98f8ddb14e32a05d14b6cfb55f2e', '放射治疗患者摆位系统', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('56c164a550b7423ab0d827dab00e1f83', '施源器', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f07f982cb127405289c11d7bbb158b3f', '治疗机用X射线管', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('936795f12ba64a54868dd7eb74a761ca', '放射治疗激光定位系统', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a837e3ec45724b52a3cef83b705cd9c3', '放射性粒籽防护植入器', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bed022e377a94251afa6eb47d915a9ec', '放射治疗患者用固定装置', 'da43c9f828014dad903fba2e65fa1811', '01-05-0504-050408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('efd6c0a469be4505ac164d5caec92a6f', '医用成像器械', '0490821da8a34708b3a3872470479aee', '01-06', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('88538234e097415bb066bf15a7a864d1', '诊断X射线机', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0601', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8b1dd3c55450414d8d89f9ef7fbc718e', '血管造影X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1b42e370b0e14e248bfab3340adab24b', '泌尿X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e32b2cae7f8435bb981de5a875f86d5', '乳腺X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9050572a720e4e758e332b39e0a63ce8', '口腔X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('855ecd264f7e4a47a4c64c0f8898c8b5', '透视摄影X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('509eb11fb91f459597c826be180d69ed', '移动式C形臂X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('118f33dc70324b8fab21ee8ffe3dea99', '摄影X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('88b0e4731c7744d99735cdd7ecbb59b1', '透视X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0e5ab41a8929415782e8b35329d7735b', 'X射线骨密度仪', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060109', '0109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('557666add36143e1bb40ae5a237bf334', '车载X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060110', '0110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3fba2f99ff8649948464ec57d359e0a8', '携带式X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060111', '0111');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('15ceda3f07004b3cb1251ff849bb8432', '肢体数字化体层摄影X射线机', '88538234e097415bb066bf15a7a864d1', '01-06-0601-060112', '0112');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('24bdccee5acb4cc78708cc6254b91556', 'X射线计算机体层摄影设备（CT）', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0602', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e530aa321c6548c5a224c99e14e8fc09', 'X射线计算机体层摄影设备（CT）', '24bdccee5acb4cc78708cc6254b91556', '01-06-0602-060201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5ad28e5b66ee41b9b41c89839235a3e6', 'X射线发生、限束装置', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0603', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('850308a58c4b414e8fdfd4167e595941', 'X 射线高压发生器', '5ad28e5b66ee41b9b41c89839235a3e6', '01-06-0603-060301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('95a54c1deaff462a95a8fc5c8f66d791', 'X射线管', '5ad28e5b66ee41b9b41c89839235a3e6', '01-06-0603-060302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0455c0e467bd442991fb59650e7ed956', 'X射线管组件', '5ad28e5b66ee41b9b41c89839235a3e6', '01-06-0603-060303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('92fb8bb800764ee684c17e9082ddc6e0', '限束装置', '5ad28e5b66ee41b9b41c89839235a3e6', '01-06-0603-060304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e2c7050be6ee44eea93aba4c2173ca7d', 'X射线影像接收处理装置', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0604', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd18a4d8ab9b4dc2b14ec0bc2de44296', 'X射线影像增强器、X射线影像增强器电视系统', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('649ca54dd1684770aa67f1b4c9b86860', 'X射线探测器、X射线探测器及其影像系统', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3dd165d5e7c5487c95a89ac3f2e2ae1f', 'X射线摄影用影像板成像装置（CR）', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('76463aa9dcab4fa8859bd0083f403a77', 'X射线感光胶片', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e61583078c254ec380b85fe6653b7293', '医用增感屏', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8b0add3bb194e75bc1ede73ac44ab65', '透视荧光屏', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8eab4cb3c54450bae61e94619c8e496', '影像板', 'e2c7050be6ee44eea93aba4c2173ca7d', '01-06-0604-060407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('682fc8f5bb854c8cb9898fb060c07e84', 'X射线附属及辅助设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0605', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c60fe742455242da927d09e4636cea33', '透视摄影床', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e5736db968aa4b9cb57c7f61c04df33a', '导管床', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4d8e04972c7843d3bd42f656e467bc1a', 'X射线摄影患者支撑装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86b64367159e448db2d32b3e6138d9d1', '悬吊、支撑装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dde42ce984a84893850e75411087629f', '造影剂注射装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8842981abff84876829aec0e1a11443c', '防散射滤线栅', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('89980c64bab54a6bb43ca6711d041ccc', 'X射线摄影暗盒', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060507', '0507');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f090c5a037824bcd9c9eaf06838177a9', 'X射线胶片显影剂、定影剂', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060508', '0508');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bd9cb28558af427798334846801d3300', '胶片观察装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060509', '0509');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('511f5ac7d6824746aa0b7de5c6ef34cd', 'X射线胶片自动洗片机', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060510', '0510');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('793c7ef481e940bf9ddf21c63ee74a8f', '患者体位固定装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060511', '0511');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('70f5ffc3056147488e6d5c55189ea6f9', '穿刺定位引导装置', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060512', '0512');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8cc768802a104bcc975da87f18f4d5df', '静脉尿路造影腹压器', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060513', '0513');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('51af97517a3a4d9e91bd610c0edd73ef', '胃肠X射线检查用品', '682fc8f5bb854c8cb9898fb060c07e84', '01-06-0605-060514', '0514');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bfccc676398d4857bf5e68efad2ca2e7', '医用射线防护设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0606', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6e04bf9797674f0b962c5e3f361ade29', '医用射线防护用具', 'bfccc676398d4857bf5e68efad2ca2e7', '01-06-0606-060601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3a948e567c4d41d8a8473a36cec8dabc', '医用射线防护装置', 'bfccc676398d4857bf5e68efad2ca2e7', '01-06-0606-060602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b4c440cb596d4561bbdabd3179e3e865', '超声影像诊断设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0607', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('416f2e6d6c6345bab247a78427fb7c4c', '超声脉冲回波成像设备', 'b4c440cb596d4561bbdabd3179e3e865', '01-06-0607-060701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('461ba14852554db3a6dfe8eb28658c35', '超声回波多普勒成像设备', 'b4c440cb596d4561bbdabd3179e3e865', '01-06-0607-060702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('922a94e26f78413ca940259589756f39', '超声影像诊断附属设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0608', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('63fa80bbdf5b470da886e262d3bfb203', '超声耦合剂', '922a94e26f78413ca940259589756f39', '01-06-0608-060801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9fc7ef0a742a4afa86acfb5d54c9f182', '超声耦合垫', '922a94e26f78413ca940259589756f39', '01-06-0608-060802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('186d8f6667a44fbab8014f1474c78b1c', '超声水囊', '922a94e26f78413ca940259589756f39', '01-06-0608-060803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5f0740d33211458b91af778f372a599a', '超声探头', '922a94e26f78413ca940259589756f39', '01-06-0608-060804', '0804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('286a633a99fc429f9dd2d85ead473a92', '超声探头穿刺架', '922a94e26f78413ca940259589756f39', '01-06-0608-060805', '0805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3bdfa40cbd7043db828baccc13a71a42', '胃肠超声显像粉', '922a94e26f78413ca940259589756f39', '01-06-0608-060806', '0806');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8e78c574e4e64dd7ae0f280765c13929', '磁共振成像设备（MRI）', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0609', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('41b066a5e6b940969ea50f71cb80594f', '永磁型磁共振成像系统', '8e78c574e4e64dd7ae0f280765c13929', '01-06-0609-060901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e9a54e561dc44788b8303f772e2c2a2e', '常导型磁共振成像系统', '8e78c574e4e64dd7ae0f280765c13929', '01-06-0609-060902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('59a90e70b5964aeb95383ea76a29a987', '超导型磁共振成像系统', '8e78c574e4e64dd7ae0f280765c13929', '01-06-0609-060903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fde004dbead8449aa960cae39dfc7155', '磁共振辅助设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0610', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4fadd114630643b89e8e6c9da52818f7', '磁共振造影注射装置', 'fde004dbead8449aa960cae39dfc7155', '01-06-0610-061001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f2d085c40cbe48a7bc808a5bcf29d479', '磁共振辅助刺激系统', 'fde004dbead8449aa960cae39dfc7155', '01-06-0610-061002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('045f204abaee46e9b3fd975c4c1c61ac', '磁共振定位装置', 'fde004dbead8449aa960cae39dfc7155', '01-06-0610-061003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b247702fa12846ea8ac75d5957316cd2', '放射性核素成像设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0611', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f4e7943e984742a896799fcbcc14ca5b', '伽玛照相机', 'b247702fa12846ea8ac75d5957316cd2', '01-06-0611-061101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c7a5c54dfd77438787ce6da87c24bfb5', '单光子发射计算机断层成像设备', 'b247702fa12846ea8ac75d5957316cd2', '01-06-0611-061102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4d2b7f9a7871427f962662f95ba77031', '正电子发射断层成像设备', 'b247702fa12846ea8ac75d5957316cd2', '01-06-0611-061103', '1103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1c468be26f164f3ab78395bf44f37954', '放射性核素扫描装置', 'b247702fa12846ea8ac75d5957316cd2', '01-06-0611-061104', '1104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('74cd8d5a4a4b40428218175274de3d5b', '放射性核素成像辅助设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0612', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2d3a9b6e3755471e9774b20698ccdec2', '自动给药系统', '74cd8d5a4a4b40428218175274de3d5b', '01-06-0612-061201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('77629b9c1dab4595a48dcbf28f1a8fe2', '锝气体发生器', '74cd8d5a4a4b40428218175274de3d5b', '01-06-0612-061202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93dc7d9fe4304f3c9b09011c207eb583', '光学成像诊断设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0613', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bf7f706172b647c094554eb0057df490', '红外热像仪', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e527fea14f94ce3b609bbdb0d2f62f8', '红外线乳腺诊断仪', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b27b2ab2d5cd4b13964eb8f7f6fc281c', '光相干断层成像系统（非眼科）', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061303', '1303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ae49f7b5c5df44b39eb55e5c010137cc', '手术显微镜（非眼科）', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061304', '1304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e05aa896b2144c6bf001c426852df47', '微循环显微镜', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061305', '1305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('36f5a065c122415baf3d77939910bff1', '医用光学放大器具', '93dc7d9fe4304f3c9b09011c207eb583', '01-06-0613-061306', '1306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('117a16c4ecd54dd1a6bc5113fa01cca1', '医用内窥镜', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0614', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ff0c97d89887411790b73938b2d5c0ff', '光学内窥镜', '117a16c4ecd54dd1a6bc5113fa01cca1', '01-06-0614-061401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fd23c8aacf774da6b4f0672906cbb7f8', '电凝切割内窥镜', '117a16c4ecd54dd1a6bc5113fa01cca1', '01-06-0614-061402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0a18454e018148feb93d0b08974cf44a', '电子内窥镜', '117a16c4ecd54dd1a6bc5113fa01cca1', '01-06-0614-061403', '1403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4f55f811b6f24906a36b2fd42306d058', '胶囊式内窥镜系统', '117a16c4ecd54dd1a6bc5113fa01cca1', '01-06-0614-061404', '1404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('399cbf375a434c43886ba76ea9fa4eb1', '内窥镜功能供给装置', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0615', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dc929d7862a141d1b46e714aa1741a2d', '内窥镜用冷光源', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061501', '1501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d102a3572a6e4013a7dea27c41057cc4', '内窥镜摄像系统', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061502', '1502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6cba510fb9714b7d9ecbe13a53d6c66d', '电子内窥镜图像处理器', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061503', '1503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c93202404e7d471d8eeee831ee706022', '内窥镜送气装置', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061504', '1504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1b2c72261d3741db852ba94ffa83a0bc', '内窥镜冲洗吸引器', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061505', '1505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('07fe6dbb57e14923873d57f9f67f296b', '内窥镜膨腔泵', '399cbf375a434c43886ba76ea9fa4eb1', '01-06-0615-061506', '1506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cf15c05d81a349bebc414f5816333563', '内窥镜辅助用品', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0616', '16');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('761bbbfc5230479fbda2d85e4f979290', '内窥镜插入形状观测系统', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061601', '1601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c1440d80247143f5913b484f90849128', '胶囊内窥镜姿态控制器', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061602', '1602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e4319d670694898a89ba3a4149885b0', '内窥镜气囊控制器', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061603', '1603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aca1e0e284e147c2bae04fa5d0433508', '内窥镜润滑剂', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061604', '1604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1f3d83a6c393477594dd0f6612770afc', '内窥镜先端帽', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061605', '1605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b2faefbe67c24054b0449436baf53e3c', '内窥镜用活检袋', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061606', '1606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('465006e788c946bf840775b01ffd32d1', '内窥镜咬口、套管', 'cf15c05d81a349bebc414f5816333563', '01-06-0616-061607', '1607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3366084a4f2d43f283a487655af42ae4', '组合功能融合成像器械', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0617', '17');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e438e9560f544590b3890280e9ee0f83', '单光子发射及X射线计算机断层成像系统', '3366084a4f2d43f283a487655af42ae4', '01-06-0617-061701', '1701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cfee3f284702416381f8dde01fa6e392', '正电子发射及X射线计算机断层成像系统', '3366084a4f2d43f283a487655af42ae4', '01-06-0617-061702', '1702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1fa1372fc91c4617b6aba76bff62f1b0', '正电子发射及磁共振成像系统', '3366084a4f2d43f283a487655af42ae4', '01-06-0617-061703', '1703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c627c7f37b9241649b8bec9aeebfb895', '超声电子内窥镜', '3366084a4f2d43f283a487655af42ae4', '01-06-0617-061704', '1704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b22172519e7442d5823e163cddaf451c', '复合内窥镜', '3366084a4f2d43f283a487655af42ae4', '01-06-0617-061705', '1705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1b0f107748c34abd8cc273c70d8a791e', '图像显示、处理、传输及打印设备', 'efd6c0a469be4505ac164d5caec92a6f', '01-06-0618', '18');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0400de79824843b7a601ec82148726b6', '图像显示处理工作站', '1b0f107748c34abd8cc273c70d8a791e', '01-06-0618-061801', '1801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2b76d5e150d740f1ba2da5d4392baffa', '胶片扫描仪', '1b0f107748c34abd8cc273c70d8a791e', '01-06-0618-061802', '1802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fdbf050ed62146edbf01af733bd1e01d', '医用图像打印机', '1b0f107748c34abd8cc273c70d8a791e', '01-06-0618-061803', '1803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('654e59375d1b41f0b3df1eb85a0c65d3', '影像记录介质', '1b0f107748c34abd8cc273c70d8a791e', '01-06-0618-061804', '1804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('959f49e219c44e3d94f6e4914c04126c', '取片机', '1b0f107748c34abd8cc273c70d8a791e', '01-06-0618-061805', '1805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a9936d4c3d8f43c9bcad75ec52bd888c', '医用诊察和监护器械', '0490821da8a34708b3a3872470479aee', '01-07', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4bd2fb3e7cfe4dc98a664d4b2f2be586', '诊察辅助器械', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0701', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('84e52ef63050494eb163fc24df7f7670', '压舌板', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('029fd8fe38374098b5e5d534bcb399fc', '听诊器', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3339dcc97be9423089240b9eba2fd5cf', '五官科检查镜', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f9f5bb1608214bcfac4a80d72472acb4', '叩诊锤', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c106b41722494e3e8b32b536798c27d9', '表面检查灯', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ff630817ee8c4eb7a3de465fe079903e', '反光器具', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0e322df9a0fd45d9b11e8bcbbc8526f2', '听觉检查音叉', '4bd2fb3e7cfe4dc98a664d4b2f2be586', '01-07-0701-070107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('87df9e4e42be4907996934330e2529c2', '呼吸功能及气体分析测定装置', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0702', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3306e136819747768d86103490f1fb03', '气体测定设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c569d05447b04a17824eee662f7207c0', '呼吸热量监测设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dc155379bcd74ab1abeccb26eb057207', '肺功能测试设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4fb29df6d31844dbaf5091a17324c75c', '呼气流量测量设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('79a67482b9974f54aa3f550c48e43bdd', '呼吸压力测量设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6b2bffcd4e2a4e938a4ed003bcffadd1', '气道过敏反应测试设备', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8493f26e42434f4ca8199de1e0f07460', '单一气体检测器', '87df9e4e42be4907996934330e2529c2', '01-07-0702-070207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('af0d0e39173e46fd94f2a4dc6330472d', '生理参数分析测量设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0703', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('11e18d98212c49a6997276734deecd84', '心电测量、分析设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3c0cb897dbbb47018fcd2501a05b6e65', '心脏电生理标测设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('15cfa0b1bb524c1784657deaad883835', '无创血压测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5ced0a1a489e404faabc5b1cae067fae', '体温测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a32382827f1d44e1b58902e2cc9c02e7', '脉搏血氧测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1f5ee13b1b0f4f54ab369cf85608b249', '生理参数诱发诊断设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b819230ba69b44108e86031df1acaf63', '血管硬度测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4db65095827d4e1bb79275ab21b04962', '无创血流分析设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070308', '0308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2edd41dd7c134d29aa337fc7b033919f', '体表色素测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070309', '0309');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a340891caa274c78bb2a34e5be0a389e', '电导分析仪', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070310', '0310');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('488a6c9414e246b69eecb99ed5793c26', '鼻阻力测量设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070311', '0311');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cbf16bc4c7bd4b1f9962fb40833917d1', '血管内皮功能测试设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070312', '0312');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0282067d0cc24138933890a52d5473d1', '脑磁图设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070313', '0313');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('69c5d6ca020e486c85ac02b06cf30cae', '有创颅内压设备', 'af0d0e39173e46fd94f2a4dc6330472d', '01-07-0703-070314', '0314');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c4e7a2c519144fc2a8168d0c853bd783', '监护设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0704', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d47f1505e1a54e41901566fb7617496b', '病人监护设备', 'c4e7a2c519144fc2a8168d0c853bd783', '01-07-0704-070401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4bac8d00158f4cc786b0a5db590fe689', '神经监护设备', 'c4e7a2c519144fc2a8168d0c853bd783', '01-07-0704-070402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd1b5d8298a043419ad7d5fb3711f684', '动态血糖/葡萄糖监测设备', 'c4e7a2c519144fc2a8168d0c853bd783', '01-07-0704-070403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a014914dd0a4225b260c75578aa7fa3', '电声学测量、分析设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0705', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6cc795d0db11417997affc2cadc2f9ab', '听力计', '9a014914dd0a4225b260c75578aa7fa3', '01-07-0705-070501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4de241e480224d14a8c237c84714f0bf', '电声门图仪', '9a014914dd0a4225b260c75578aa7fa3', '01-07-0705-070502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('688d36fe65d34e67a90fb3b0f49fee1b', '耳声发射仪', '9a014914dd0a4225b260c75578aa7fa3', '01-07-0705-070503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2b8107c79a9c4b998bad5cacbbf54566', '耳声阻抗测量仪', '9a014914dd0a4225b260c75578aa7fa3', '01-07-0705-070504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93f0ee6b669d438a972f7ed6fe368cdd', '放射性核素诊断设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0706', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('81e2d0de964b4d81b590b92c1c9b7841', '放射性核素骨密度测量设备', '93f0ee6b669d438a972f7ed6fe368cdd', '01-07-0706-070601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('774114214fa44615b4b1929a03ce929a', '肾及甲状腺功能测量设备', '93f0ee6b669d438a972f7ed6fe368cdd', '01-07-0706-070602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2df2a41c1c8d431dbcc11ac2add0e5ce', '伽玛射线探测装置', '93f0ee6b669d438a972f7ed6fe368cdd', '01-07-0706-070603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3973453f3f674ce7bd3c78f9a3150583', '超声生理参数测量、分析设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0707', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dd5c147044d74d969319bc32959641b4', '超声多普勒血流分析设备', '3973453f3f674ce7bd3c78f9a3150583', '01-07-0707-070701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93a8a85124884261a9f85adc280e5b40', '超声人体组织测量设备', '3973453f3f674ce7bd3c78f9a3150583', '01-07-0707-070702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('99f4870086894ff1b6e4f29213a6c332', '遥测和中央监护设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0708', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d0c80108b0e841269f61e9f9bc8070c7', '遥测监护设备', '99f4870086894ff1b6e4f29213a6c332', '01-07-0708-070801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('626f344043ee486993692b2d77037464', '远程监护设备', '99f4870086894ff1b6e4f29213a6c332', '01-07-0708-070802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8e6e38a77f894af997f6f301e93e5ba7', '中央监护系统', '99f4870086894ff1b6e4f29213a6c332', '01-07-0708-070803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('de48204499ae4374a17d2aad5e855bf0', '其他测量、分析设备', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0709', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('758a83a1f59140cda0c0d6688182271f', '泌尿、消化动力学测量、分析设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('43a86e571e54431489bc3afc3bb2a357', '眼震电图设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bf498388893148f7873ce1517fe998d8', '睡眠呼吸监测设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9489313ed3034f1eb28b938ced6bd6b7', '平衡测试设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070904', '0904');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a8f446addbd649e19103e808a8565f1d', '言语障碍测量设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070905', '0905');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fa44173be71f48289506defd2246dd28', '心血管功能检测设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070906', '0906');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aede064eff9c40e295c3faf0aafac24e', '人体阻抗测量、分析设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070907', '0907');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3273c36dea694dcc8df706ae5f5b4974', '酸碱度检测设备', 'de48204499ae4374a17d2aad5e855bf0', '01-07-0709-070908', '0908');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6e7a4df49fa44679ab4895095a86c8f1', '附件、耗材', 'a9936d4c3d8f43c9bcad75ec52bd888c', '01-07-0710', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f08750e7a9ed4a169c6eeffcb2d39552', '有创血压传感器', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('201da521c59640948043aeda0db08abc', '电生理标测导管', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5c3a2625a7454712b0008b829e661355', '体表电极', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('621c370d0f0542a5a2a2437e49877f5b', '脉搏血氧传感器', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('64ef034bd80f47a78ac0b7919414578b', '导电膏', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d29a9d285b4742e38cc07cb3515c7a7c', '无创血压袖带', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071006', '1006');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a9278022410142bf89d2d35f1b9d2105', '心电导联线', '6e7a4df49fa44679ab4895095a86c8f1', '01-07-0710-071007', '1007');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9827a419e3054648a77478286ab3c6bb', '呼吸、麻醉和急救器械', '0490821da8a34708b3a3872470479aee', '01-08', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('56b881f3708343fbb092eb362b9feb83', '呼吸设备', '9827a419e3054648a77478286ab3c6bb', '01-08-0801', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3308e3e02f08471593de10e8973b6526', '治疗呼吸机（生命支持）', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61c6d31faee040ae9cc6829b5294b6b9', '急救和转运用呼吸机', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ab5741bd169245489edf08599d6c07f6', '高频呼吸机', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93073f641ea943d681fc3a381eba50f1', '家用呼吸机（生命支持）', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('690d42d88dca43c1b02bb5afff3ad61a', '家用呼吸支持设备（非生命支持）', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('11190f2a40374056aad70999a3df36ff', '睡眠呼吸暂停治疗设备', '56b881f3708343fbb092eb362b9feb83', '01-08-0801-080106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0b834c0277454d519bd12b972971907a', '麻醉器械', '9827a419e3054648a77478286ab3c6bb', '01-08-0802', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('328822a3e4bf42e6beb00b425e7bb841', '麻醉机', '0b834c0277454d519bd12b972971907a', '01-08-0802-080201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('365fe66c652e46ab950466076dbb4d4c', '麻醉穿刺针', '0b834c0277454d519bd12b972971907a', '01-08-0802-080202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b79612eaf1524c528029142821901503', '吸入镇痛装置', '0b834c0277454d519bd12b972971907a', '01-08-0802-080203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2d79068cc7434f2393e1622035ea11d3', '急救设备', '9827a419e3054648a77478286ab3c6bb', '01-08-0803', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5ade604b862a4b75806996e4e7ef693e', '体外除颤设备', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a0be8fb9232e4d38bddd79f97cc8ed3e', '婴儿培养箱', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('945c299bcb194bcfa7d0ee45e7fceae0', '婴儿辐射保暖台', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('de3ac1f1373945199a53fd8e74b2697f', '心肺复苏设备', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a8cb8373a9f448ee83aa7e14a21cbe9a', '人工复苏器（简易呼吸器）', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5091a4a2267b4e2cb125e80377299c7e', '气动急救复苏器', '2d79068cc7434f2393e1622035ea11d3', '01-08-0803-080306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a935f16cf56b4e2aaf93ec1f1fcdb0fe', '医用制氧设备', '9827a419e3054648a77478286ab3c6bb', '01-08-0804', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('153075bf168a464fafa60e9de471085c', '医用分子筛制氧系统', 'a935f16cf56b4e2aaf93ec1f1fcdb0fe', '01-08-0804-080401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9ec45b5db9b646a0955b55951a59399e', '医用分子筛制氧机', 'a935f16cf56b4e2aaf93ec1f1fcdb0fe', '01-08-0804-080402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bbf993f540db4ed78df066b091deb701', '医用膜分离制氧系统', 'a935f16cf56b4e2aaf93ec1f1fcdb0fe', '01-08-0804-080403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e202a351d81342c29c287e2439a8b94a', '医用膜分离制氧机', 'a935f16cf56b4e2aaf93ec1f1fcdb0fe', '01-08-0804-080404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('47bb97e2ecca4a79b7e37a56c40107fc', '氧气发生器', 'a935f16cf56b4e2aaf93ec1f1fcdb0fe', '01-08-0804-080405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('99b7702a31e9458e96ee6be600f9d877', '呼吸、麻醉、急救设备辅助装置', '9827a419e3054648a77478286ab3c6bb', '01-08-0805', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f694b611d1f94d99a5b2db903e40ecc7', '麻醉蒸发器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a256ed1995a49cc9d036556f1ce1ec9', '医用呼吸道湿化器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c7c64334afb4ea8abd6173ef07965f8', '呼吸系统过滤器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2ddf3453ed574976afa551422f0e1c96', '热湿交换器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d91bb4a47699454f98648553dc06a320', '呼吸管路辅助器械', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9ae78d1976df4852b78ff07ea3fb48b7', '气管插管用喉镜', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('684c705a66bc47a9af0661a7f0764333', '雾化设备/雾化装置', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080507', '0507');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86a9683aedee4384ade04a80fe05632f', '麻醉储气囊', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080508', '0508');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ed191aed71a74d7cb980580a14e7ddb2', '麻醉废气吸附器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080509', '0509');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eefc2aff5bbb484f810c69fefe1cd5f4', '麻醉气体净化传递和收集系统', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080510', '0510');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e3c066bc9ca14ed490cd8bf8715ad251', '吸氧头罩', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080511', '0511');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('80dfd500435d4bf98b185dd58530f466', '除颤电极', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080512', '0512');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cdfd92f97e774ece9bed6ac236fa2585', '呼吸训练器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080513', '0513');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e3ac568da7c4890b15738bf6d0a5ed2', '二氧化碳吸收器（含二氧化碳吸收剂）', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080514', '0514');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4c95adc716ee4a208227588c80899bb5', '氧气吸入器', '99b7702a31e9458e96ee6be600f9d877', '01-08-0805-080515', '0515');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da9c8a89ad634aa18c34fc28646df069', '呼吸、麻醉用管路、面罩', '9827a419e3054648a77478286ab3c6bb', '01-08-0806', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c40fb13dcd54fc6aa28156f4af3d56b', '硬膜外麻醉导管', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('96a32bf594b2412f8044191f904d738e', '呼吸管路', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e79b9fa18a3540d890eef72aeab1555a', '气管内插管/气管套管', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('776d7e3a3f0c4a058f1550368321bee3', '食道气管插管', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f3ed98bb59f647bfba2b327bebf6a017', '喉罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f1b9c4f15ea0442680c02fc7fdd65082', '口咽/鼻咽通气道', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aca2d65a37fb48119741c6811902065e', '支气管堵塞器', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080607', '0607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d52a4f9ef86841539fcfcee9506c5d49', '鼻氧管', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080608', '0608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('738fd2fe9ee84a18b6fe460af611ee5e', '呼吸道用吸引导管（吸痰管）', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080609', '0609');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ae526c0753a8495c93217acb83e2be72', '呼吸面罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080610', '0610');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1ac3df2e908f403d8bb539b6ef6da28a', '持续正压通气用面罩、口罩、鼻罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080611', '0611');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ff9d864e8cce4d85bf268151999bf07a', '雾化面罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080612', '0612');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0dab6a6577ea4855aaae4451738b4d2c', '麻醉面罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080613', '0613');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2d97f38c9d9c4c9cb4ee8550cb9e188f', '输氧面罩', 'da9c8a89ad634aa18c34fc28646df069', '01-08-0806-080614', '0614');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('adc22ba6746748b58899f78f3deaad20', '医用供气排气相关设备', '9827a419e3054648a77478286ab3c6bb', '01-08-0807', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4936a8cbc6534145b6d46d8a6fbb78cd', '医用空气压缩机', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c7497a5d5325466fb8f6ef8d31a58b03', '医用气体混合器', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a0957556ef484965ad586c4c873fbef1', '供氧、排氧器', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('152ef43d440a46169fd50934cfd211e5', '医用压缩气体供应系统', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('abed200e7a6842029037c91310606fa5', '医用气体汇流排', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('817cbf6ba9ac4387b8d44d1a7a11cf51', '医用气体报警系统', 'adc22ba6746748b58899f78f3deaad20', '01-08-0807-080706', '0706');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86d8517722524ba3bde3d4bcc736c55a', '物理治疗器械', '0490821da8a34708b3a3872470479aee', '01-09', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('75e0627bffca46d9bc80dbeb76f11112', '电疗设备/器具', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0901', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fde3f2df297f4632a5041200f547881b', '电位治疗设备', '75e0627bffca46d9bc80dbeb76f11112', '01-09-0901-090101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('64bb9ab44ff84426baca4b863bf36969', '直流电治疗设备', '75e0627bffca46d9bc80dbeb76f11112', '01-09-0901-090102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('422cbfdbc86142278304e882900c4d64', '低中频治疗设备', '75e0627bffca46d9bc80dbeb76f11112', '01-09-0901-090103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('428fd06c095b46e7a357419735278838', '静电贴敷器具', '75e0627bffca46d9bc80dbeb76f11112', '01-09-0901-090104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9afe595ca1de446bbfef721c735c4acb', '神经和肌肉刺激器用电极', '75e0627bffca46d9bc80dbeb76f11112', '01-09-0901-090105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c5bc480bf92040eb947f7f5dd6e48c0e', '温热（冷）治疗设备/器具', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0902', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6edd343aa8134b6d95e1c666c4c79189', '热传导治疗设备', 'c5bc480bf92040eb947f7f5dd6e48c0e', '01-09-0902-090201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4eb5eb0efdb746dd8becb85dbe36f98f', '热辐射治疗设备', 'c5bc480bf92040eb947f7f5dd6e48c0e', '01-09-0902-090202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7f7a0d2654c24f899d0e7b47b9c34280', '物理降温设备', 'c5bc480bf92040eb947f7f5dd6e48c0e', '01-09-0902-090203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('03f6d2f468424886b7434749915e9afb', '光治疗设备', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0903', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9f2b0416106a4974a48ec6b623620534', '激光治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('26f2e41bc341482c922b5220ef31db15', '光动力激光治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5ca4e03534ce46c8970cff191d259107', '光动力治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('54fe07c212984d6cbffd80594ef35d7b', '强脉冲光治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1230e393300d4a2f873e0fc0301ef7d7', '红光治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d424d90097bd462985cbff642f71e515', '蓝光治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba1c466e50be4f5fa755aff9db1df8d7', '紫外治疗设备', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2929ec99653a4843a2eab6a04e97af08', '光治疗设备附件', '03f6d2f468424886b7434749915e9afb', '01-09-0903-090308', '0308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4adf3b5d4e66418896fc19f11f5d4c98', '力疗设备/器具', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0904', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb68f4f0e16d4034aa883ffb8c719e45', '负压（振动）治疗设备', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8f4748db235d4b2ca3c58e1c0053719c', '加压治疗设备', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9fc64452069344e78d7e7031d6c8efdf', '牵引治疗设备', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bb7fc0ffb3374a64bbb29fb335d01502', '牵引器具', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('98d26a87fbd44627bae4f115189b7807', '冲击波治疗设备', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6b61e83d5ae04c0e9d1446f4ea46eef9', '气囊式体外反搏装置', '4adf3b5d4e66418896fc19f11f5d4c98', '01-09-0904-090406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd156559dd654fc9be61539f5f55abe4', '磁疗设备/器具', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0905', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0bc6a2ad51e74ae396a4f00aaed8e739', '动磁场治疗设备', 'cd156559dd654fc9be61539f5f55abe4', '01-09-0905-090501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('22e92edbbff9418fb0908910753539e8', '静磁场治疗器具', 'cd156559dd654fc9be61539f5f55abe4', '01-09-0905-090502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('922bc664e13f40dab31ca21b44b2767a', '超声治疗设备及附件', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0906', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ed023d444f024d5ea793dd63cf531e73', '超声治疗设备', '922bc664e13f40dab31ca21b44b2767a', '01-09-0906-090601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('533c60b3fcc842689020a714be813226', '超声治疗设备附件', '922bc664e13f40dab31ca21b44b2767a', '01-09-0906-090602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eef2486b02af4c4aa9d616aa452adf1a', '高频治疗设备', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0907', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('77b71410d17c4d159ba7e22652d06ef7', '射频热疗设备', 'eef2486b02af4c4aa9d616aa452adf1a', '01-09-0907-090701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('534fe94aeab542e8aa143eaf98f7402b', '射频浅表治疗设备', 'eef2486b02af4c4aa9d616aa452adf1a', '01-09-0907-090702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('57db7ddae56b40d8be5110cff49c0f1e', '微波治疗设备', 'eef2486b02af4c4aa9d616aa452adf1a', '01-09-0907-090703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('155dd5ff519044a09ea81653bcdff554', '短波治疗仪', 'eef2486b02af4c4aa9d616aa452adf1a', '01-09-0907-090704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('939c6762cd004ab3a4b24fc43b0d1167', '毫米波治疗设备', 'eef2486b02af4c4aa9d616aa452adf1a', '01-09-0907-090705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('510f4a560ab94d1aa0971ae486e4a47d', '其他物理治疗设备', '86d8517722524ba3bde3d4bcc736c55a', '01-09-0908', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e8d79a8e0b4d4ff2ac74e20de6f3d0db', '医用氧舱', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6de5805d1090474a950188393355db8e', '臭氧治疗设备', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bf28f603b9ea4e7d9c50344fbd6f84b1', '生物反馈治疗设备', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f6a313dc0fa14428a9aece3cc2c3ba17', '烧烫伤浸浴装置', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090804', '0804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('71ed03c86d4549fa8695eb7ccc78278d', '肠道水疗机', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090805', '0805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bbaf8db523e44602938825fee951c490', '药物导入设备', '510f4a560ab94d1aa0971ae486e4a47d', '01-09-0908-090806', '0806');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('502c755d03c44eccbb190fcc5c14749a', '输血、透析和体外循环器械', '0490821da8a34708b3a3872470479aee', '01-10', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1c9d6936c4a74c9199a53a00da4b9fd6', '血液分离、处理、贮存设备', '502c755d03c44eccbb190fcc5c14749a', '01-10-1001', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4a37cfa8969a456da7ccf8827755418c', '血液成分分离设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8aa58abc47240c8a69e7b60471cc439', '自体血液回收设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('45c1f776e9174947ad65237a7b548ad1', '血细胞处理设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('508177b13d17429ebd23f22737aa3643', '血液辐照设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3c812e404bb248e296c765519bed88ca', '血浆病毒灭活设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1dd45a09571d425782383057e5c9d5b0', '血液融化设备', '1c9d6936c4a74c9199a53a00da4b9fd6', '01-10-1001-100106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0f294e52d731440ba3f1060e4826369f', '血液分离、处理、贮存器具', '502c755d03c44eccbb190fcc5c14749a', '01-10-1002', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('270acd8f50fc4f72a3ebb80afaf5048a', '血袋', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('66171c3524aa4f8086a8281a7498f6fb', '离心式血液成分分离器', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f2152f53d57d4d538155611de502a230', '动静脉穿刺器', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c903439f0fd048c6934311621561b910', '输血器', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7a2304a252eb4b56a0f922036a650b0c', '自体血液处理器具', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7909d2ca6e0e49a39003357d50fac93b', '血浆管路', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('36d30b1f99e947229794db20638a3557', '冰冻红细胞洗涤机用管路', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('91a3fa7c3d454a08b38157ce4f5ff5d0', '富血小板血浆制备器', '0f294e52d731440ba3f1060e4826369f', '01-10-1002-100208', '0208');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e901517ed5074e6a80333396caf57ce6', '血液净化及腹膜透析设备', '502c755d03c44eccbb190fcc5c14749a', '01-10-1003', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a89df507b4074997a7ade8eb1f83200d', '血液透析设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a42d889892f943b290691f4a236a3f5b', '连续性血液净化设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2d3fc451be75458684669caee5685a0e', '血液灌流设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0c395de9dfa945759b32473b501dac4f', '人工肝设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f6b367604e6b453db6d57269b3d6c884', '血液透析辅助设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ad798cde2e79453db14d0ae251c7cac1', '腹膜透析设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b217e6e20efc4b86962bb5bde1e24a7a', '腹膜透析辅助设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('85ff732dcb3941c8a4e935846709ace3', '血脂分离设备', 'e901517ed5074e6a80333396caf57ce6', '01-10-1003-100308', '0308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('20469e86f55e4174a048cbf58105c4be', '血液净化及腹膜透析器具', '502c755d03c44eccbb190fcc5c14749a', '01-10-1004', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2819f5925f6a4e9d992e116dc9b3a9fe', '血液透析器具', '20469e86f55e4174a048cbf58105c4be', '01-10-1004-100401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9536e87b431f4b079f68dfe7dcd1fed1', '血液灌流器具', '20469e86f55e4174a048cbf58105c4be', '01-10-1004-100402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fb400b308d09437391da38dd8dd952c5', '血液净化辅助器具', '20469e86f55e4174a048cbf58105c4be', '01-10-1004-100403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3a83a982b99e4d12978e80b5e2b2c7a8', '腹膜透析器具', '20469e86f55e4174a048cbf58105c4be', '01-10-1004-100404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e558681cf10a4b61befeb83b564a33fe', '血脂分离器具', '20469e86f55e4174a048cbf58105c4be', '01-10-1004-100405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba805b69b54a4c748a76b17809fcf198', '心肺转流设备', '502c755d03c44eccbb190fcc5c14749a', '01-10-1005', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('666f0ab429e4408b915c9680506efa55', '心肺转流用泵', 'ba805b69b54a4c748a76b17809fcf198', '01-10-1005-100501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6a8b89ce95064b7c86632d60f9aadcc9', '心肺转流监测设备', 'ba805b69b54a4c748a76b17809fcf198', '01-10-1005-100502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1989ac7fdfc446539f3614a4c55ae091', '热交换设备', 'ba805b69b54a4c748a76b17809fcf198', '01-10-1005-100503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2c5773c7be1b4b42bf5397638d225844', '体外心肺支持辅助系统', 'ba805b69b54a4c748a76b17809fcf198', '01-10-1005-100504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('859190a5cdb447aebb307ed9154697ec', '体外心肺支持用升温仪', 'ba805b69b54a4c748a76b17809fcf198', '01-10-1005-100505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dbed2b2b58304a3fa86d297d915c32d5', '心肺转流器具', '502c755d03c44eccbb190fcc5c14749a', '01-10-1006', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0b1fd8b320d74fcbaa6e9c3eaeb0e6da', '氧合器', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4c131552fe524782a70beb8ad82986e7', '贮血滤血器', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('85f07f34ec554d8b93d5ecede1fd0147', '微栓过滤器', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('13f4da613b7744b2a2c1a646112951e2', '血液浓缩器', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5e264a85d1944e119cc04e38af450ae8', '心脏停跳液灌注器', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61a602a7210640bba693e9589004fd82', '心肺转流用管路及接头', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('079a859b9bea466c87af114cd393f15d', '离心泵泵头', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100607', '0607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('14dc16a26db047cc9c2b9e167501409a', '心脏停跳液', 'dbed2b2b58304a3fa86d297d915c32d5', '01-10-1006-100608', '0608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('23ea5c2b2f7a41cabfea28dbe15d0e00', '其他', '502c755d03c44eccbb190fcc5c14749a', '01-10-1007', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df6c8171ad094e0ea78e5167df4608ed', '腹水超滤浓缩回输设备', '23ea5c2b2f7a41cabfea28dbe15d0e00', '01-10-1007-100701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('422d326e180d492d854afdf2e63a4582', '医疗器械消毒灭菌器械', '0490821da8a34708b3a3872470479aee', '01-11', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6b410edda9ff4c2dba5e18998ee84c86', '湿热消毒灭菌设备', '422d326e180d492d854afdf2e63a4582', '01-11-01', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c1294a228dd34b8086ad1c9584ef64fa', '蒸汽消毒器', '6b410edda9ff4c2dba5e18998ee84c86', '01-11-01-01', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('535d29e84ae642158ba709f38f028c58', '煮沸消毒器', '6b410edda9ff4c2dba5e18998ee84c86', '01-11-01-02', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3368416338be45be830e3b40a1b4878e', '压力蒸汽灭菌器', '6b410edda9ff4c2dba5e18998ee84c86', '01-11-01-03', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86e0e0b6d629436682479ba56c801069', '干热消毒灭菌设备', '422d326e180d492d854afdf2e63a4582', '01-11-02', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a7e3970e0f92473e95a0f19b44d92862', '热空气消毒器', '86e0e0b6d629436682479ba56c801069', '01-11-02-01', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ab4510d4a8be493e88d748af656a7ec8', '热空气灭菌器', '86e0e0b6d629436682479ba56c801069', '01-11-02-02', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('22ad3b69e8974f7594167556abc68b02', '热辐射灭菌器', '86e0e0b6d629436682479ba56c801069', '01-11-02-03', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd960004e920431698547666f30541d0', '化学消毒灭菌设备', '422d326e180d492d854afdf2e63a4582', '01-11-03', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('666bebb6cc2e44a2b3bc46e749902103', '酸性氧化电位水生成器', 'cd960004e920431698547666f30541d0', '01-11-03-01', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('788bd692267f4b1dbf2c960e9d0a500a', '臭氧消毒器', 'cd960004e920431698547666f30541d0', '01-11-03-02', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('28283bae715746ff84972ce53efd685e', '环氧乙烷灭菌器', 'cd960004e920431698547666f30541d0', '01-11-03-03', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1c9bb30cc77a43f397f9459f019ae36c', '甲醛灭菌器', 'cd960004e920431698547666f30541d0', '01-11-03-04', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8bd2800e4a174a89aa02f8066b6233c2', '过氧化氢灭菌器', 'cd960004e920431698547666f30541d0', '01-11-03-05', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1f177535b3cd4356b5b1f76859928a2d', '其他化学消毒灭菌器', 'cd960004e920431698547666f30541d0', '01-11-03-06', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cf053704d07b4b6889f71d372e763a08', '紫外线消毒设备', '422d326e180d492d854afdf2e63a4582', '01-11-04', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('991ebbe34e2c40d7ab0d22af10afa136', '紫外线消毒器', 'cf053704d07b4b6889f71d372e763a08', '01-11-04-01', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5dc2d56d3a1a44e6971af992a9eeba76', '清洗消毒设备', '422d326e180d492d854afdf2e63a4582', '01-11-05', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a35503d5cd04e02a9bc74639c594eeb', '清洗消毒器', '5dc2d56d3a1a44e6971af992a9eeba76', '01-11-05-01', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('202434d6a8ce45d78fbc0984b57bc3a2', '医用清洗器', '5dc2d56d3a1a44e6971af992a9eeba76', '01-11-05-02', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1ab1d2d782a541e88fa61bc9223c9a39', '有源植入器械', '0490821da8a34708b3a3872470479aee', '01-12', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('02ff97b594a94980937b5804727c8f1b', '心脏节律管理设备', '1ab1d2d782a541e88fa61bc9223c9a39', '01-12-1201', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9b36f7ea23f5444aaa63c9fe1c84dc21', '植入式心脏起搏器', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b2c2c80d29fc4ffdace30882f35549d6', '植入式心律转复除颤器', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b783f253242f4813be63688b3ea31b61', '临时起搏器', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cc5154f2ea1d43bda62c8f1b47da2ab5', '植入式心脏起搏电极导线', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aad47b70c18d47bf9b57f4e5efdaf48c', '植入式心脏除颤电极导线', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da08fb88d7a741ed97f80127665ca077', '临时起搏电极导线', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('950300c642f9492983657cc396eef68e', '植入式心脏事件监测设备', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c43c19b9ee0243fc98cdcf2da8540ade', '植入式封堵工具', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b819d1b12ae7411a8a530d56eacee985', '植入式电极导线移除工具', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120109', '0109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8f11d54ffc024760839ce50c5354988f', '起搏系统分析设备', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120110', '0110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('190af296bc3a4fdeae9f540a1274da4a', '心脏节律管理程控设备', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120111', '0111');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('86772477f2e44c91afa3ad3c77e974c1', '连接器套筒', '02ff97b594a94980937b5804727c8f1b', '01-12-1201-120112', '0112');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('608b86ec7d4f40e6a7fb328ba9ebfa26', '神经调控设备', '1ab1d2d782a541e88fa61bc9223c9a39', '01-12-1202', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a1f9f9278bd7449bbcc5ece75e0832e9', '植入式神经刺激器', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7ae4985910a24eed9e83c6c35b2b03c7', '植入式神经刺激电极', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f4982108898e48a192bb7648d99c82f0', '测试刺激电极', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('56d459aabc334dd48723771c2bc4543e', '神经调控充电设备', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0f97dea76d2c46d2851c315b0a6fb1e4', '植入式电极导线适配工具', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('06e92ba47f124561969381907b4ba110', '植入式电极导线补件', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b3cd3cd77d904ab68a02eba90a4d7190', '测试刺激器', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8392111f8e14d0ba2f0be9804a7fedb', '测试延伸导线', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120208', '0208');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fdaecea487b948be87675bbb6f743552', '神经调控程控设备', '608b86ec7d4f40e6a7fb328ba9ebfa26', '01-12-1202-120209', '0209');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da565e2bacf8435e8946f69d5050c71f', '辅助位听觉设备', '1ab1d2d782a541e88fa61bc9223c9a39', '01-12-1203', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e9e85f641c57416dbeb4f043d083a8ef', '植入式位听觉设备', 'da565e2bacf8435e8946f69d5050c71f', '01-12-1203-120301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1680e2a10c274f44982b82c5e9fef096', '体外声音处理器', 'da565e2bacf8435e8946f69d5050c71f', '01-12-1203-120302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d470a7b853d04fdcb43f2b7bbb22ba59', '辅助位听觉调控设备', 'da565e2bacf8435e8946f69d5050c71f', '01-12-1203-120303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('142dd09469f5450aacabbee41d7db74a', '其他', '1ab1d2d782a541e88fa61bc9223c9a39', '01-12-1204', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('35e1c3166ae748c590879f13662d8db6', '植入式心脏收缩力调节设备', '142dd09469f5450aacabbee41d7db74a', '01-12-1204-120401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8951aff043ed4c8fb909922f72a36944', '植入式循环辅助设备', '142dd09469f5450aacabbee41d7db74a', '01-12-1204-120402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e17ecfcc73994f60acddff751022e85a', '植入式药物输注设备', '142dd09469f5450aacabbee41d7db74a', '01-12-1204-120403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8c63a3324042473ca206c64e911547e3', '无源植入器械', '0490821da8a34708b3a3872470479aee', '01-13', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1c419f5cb3684da493e4e226847f3adc', '骨接合植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1301', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c7e71c5cc947447b919f83f5a1315236', '单/多部件金属骨固定器械及附件', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3e5a0ba040ce4ed08522ec8e242de52c', '单/多部件可吸收骨固定器械', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('201e88c3389c4588be3111a7cccef6c6', '单/多部件记忆合金骨固定器械', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7bbeac7ba9784b439327b96e47834c54', '金属髓内装置', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cfae5cbebe1f4272801bca658ef3295b', '金属固定环扎装置', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c6be83e814854a31a40563ad9ae87444', '光面或带螺纹的金属骨固定紧固件', '1c419f5cb3684da493e4e226847f3adc', '01-13-1301-130106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('086633a2802448ae810a77e9f3c291f1', '运动损伤软组织修复重建及置换植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1302', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('787b8f0c390344cb8331a1b17e6c35d6', '运动损伤软组织修复重建植入物', '086633a2802448ae810a77e9f3c291f1', '01-13-1302-130201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c33db4f05de0475e94ce590c72cb3f4f', '运动损伤软组织置换植入物', '086633a2802448ae810a77e9f3c291f1', '01-13-1302-130202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b162dc8029774a218f60551d258d147f', '脊柱植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1303', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3b0d54ed73094a80a02e23cb636b73a4', '脊柱椎板间固定系统', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a0abd82b4d3f49739f14cffc23c76ccb', '脊柱椎体间固定/置换系统', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('62f4ebd07a1c4b009aa9a75697b25f8c', '脊柱椎弓根系统', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('606edab0938e427bae1f9d4cb43a9cda', '椎间融合器', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c435cef29ab5475aa01ded0afcfa8402', '椎间盘假体', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f52e864a0b7b4e9697ef0266fcda2f53', '棘突植入物', 'b162dc8029774a218f60551d258d147f', '01-13-1303-130306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c7cbc631a8ca4812b9ffb63911f859ae', '关节置换植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1304', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4cecc98615d14dde94d0daee34ed874b', '髋关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('05255a08f2704758a85d2bb4e609ba68', '膝关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('832a486a4dff40d69a8f4f64504c66b2', '肩关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('56d3c34fa7ee4ac7801b1c4c1f78ba7e', '肘关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8aada18753e84631b88e4f03c43dcd43', '指关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1294136178db4f93a9b830461b374992', '腕关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b72ff27db34e4400a73c7283d332a9fa', '踝关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('62e4e295e76245fe8a6651bdff2491d4', '颞下颌关节假体', 'c7cbc631a8ca4812b9ffb63911f859ae', '01-13-1304-130408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('388d2a89275141f3a7f333f252324ea1', '骨科填充和修复材料', '8c63a3324042473ca206c64e911547e3', '01-13-1305', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1e0942846b24743881d49948198e84a', '丙烯酸树脂骨水泥', '388d2a89275141f3a7f333f252324ea1', '01-13-1305-130501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5883e033f2c74a5d8068908973274cc1', '钙盐类骨填充植入物', '388d2a89275141f3a7f333f252324ea1', '01-13-1305-130502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('91a1c081388a462782baf1a11187947f', '同种异体骨修复材料', '388d2a89275141f3a7f333f252324ea1', '01-13-1305-130503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd7cab58ddae45ec80ff3a2050d80f95', '金属填充物', '388d2a89275141f3a7f333f252324ea1', '01-13-1305-130504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8664219960224c91aa9164a075a9ca22', '神经内/外科植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1306', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fa4ae354663246758fa720211af92ddd', '单/多部件预制颅骨成形术板及紧固件', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0dfb0da5806441e2a78472073f92b735', '颅骨夹/锁', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('98b2eef6e99141ba82d5861c56cfe1a3', '单/多部件颅颌面固定器械及附件', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e57fb53cfeb84f52ba545f80457fc3d4', '硬脑（脊）膜补片', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b0237eaaff294127bdaed3e1a3759138', '动脉瘤夹', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('20d922e2363d4b0e9816c4740d49ccfd', '颅内支架系统', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e825f397e86c4dcfa8b17ea6ec3a2b77', '颅内栓塞器械', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130607', '0607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2f997d43f7dc4cb698dafbe301a32e18', '颅内弹簧圈系统', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130608', '0608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0b7e264f2fa643de8255fc82ce891847', '人工颅骨', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130609', '0609');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1aa68497a2ce41cd80e99fe4da448f9f', '脑积水分流器及组件', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130610', '0610');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('87287d53eb5d44f9b71ac8aaab09bb1e', '颅内动脉瘤血流导向装置', '8664219960224c91aa9164a075a9ca22', '01-13-1306-130611', '0611');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6c96719443fe402db467b29a3f1d38ea', '心血管植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1307', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1ff841c385b54bfa8d13814c2849c8ef', '血管内假体', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5efd9834c71d4f648973f1d312abefcc', '血管支架', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4ad9f9a85e35414dadc605f2a17bf2e6', '腔静脉滤器', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3a78ec4da4c1478b8c4e9bea840acac3', '人工血管', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('43473a24a9994c1590b2cfb151eef2ad', '心血管补片', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8c1d13aa6964a27addcebd09d3e45b2', '人工心脏瓣膜及瓣膜修复器械', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130706', '0706');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8ff86e572ed45e78fa796a9e8afd8c6', '心脏封堵器', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130707', '0707');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8510d06cfc648168a8c8357e9b602e4', '心血管栓塞器械', '6c96719443fe402db467b29a3f1d38ea', '01-13-1307-130708', '0708');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('159ef0f4f2e9452396ed184831e8be77', '耳鼻喉植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1308', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6671505700e1407c9120efd715089cb7', '听小骨假体', '159ef0f4f2e9452396ed184831e8be77', '01-13-1308-130801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd67eafd66c84ec2a2a608959c9d7a3d', '耳内假体', '159ef0f4f2e9452396ed184831e8be77', '01-13-1308-130802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4668afcc63284b22abef402ecbc8b802', '植入性止鼾装置', '159ef0f4f2e9452396ed184831e8be77', '01-13-1308-130803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3917e3013a7447fb822f424938829dc7', '整形及普通外科植入物', '8c63a3324042473ca206c64e911547e3', '01-13-1309', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('52cd85660ca147c3bc07dfd423d9699e', '整形填充材料', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3057ed4a2ed1482ca7c793305e47e2c1', '整形用注射填充物', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f67494e9a5274c73bf5e6c0ae5ffb597', '乳房植入物', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('45e973c5e02b4ea7b9adc0d6ab468827', '外科补片/外科修补网', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130904', '0904');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5a14fbc98b7b48789d32eb62ca616f48', '修补固定器', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130905', '0905');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8d32c587d164c949043d2f7c564ca4f', '非血管支架', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130906', '0906');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('32dbb2d0a4af45cbbf3d78a38ac2a1b2', '支气管内活瓣', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130907', '0907');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ece5c695ff2d47c2ac4365d60fd2b442', '肛瘘塞', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130908', '0908');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4a241f7bccc44d0f97913e2c18ca41d2', '阴茎假体', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130909', '0909');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('768ac6a54281422194397bafa9bfeb6d', '软组织扩张器', '3917e3013a7447fb822f424938829dc7', '01-13-1309-130910', '0910');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('165921fb39ff48f9adb0fa26f58ae1ee', '组织工程支架材料', '8c63a3324042473ca206c64e911547e3', '01-13-1310', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c2cd4ba7f2af4553bb51aa5263692454', '脱细胞皮肤', '165921fb39ff48f9adb0fa26f58ae1ee', '01-13-1310-131001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('59b809b46cf54d889e99ca7ea8f9f549', '脱细胞真皮基质', '165921fb39ff48f9adb0fa26f58ae1ee', '01-13-1310-131002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b34af17c94294ef3a157cb0f3c2991ec', '胶原蛋白支架材料', '165921fb39ff48f9adb0fa26f58ae1ee', '01-13-1310-131003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('75d8fddb6a504500a7373250c4ae13d1', '神经修复材料', '165921fb39ff48f9adb0fa26f58ae1ee', '01-13-1310-131004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1bdb49da28f1443cbe67166ee4c169c7', '含重组人骨形态发生蛋白质的骨修复材料', '165921fb39ff48f9adb0fa26f58ae1ee', '01-13-1310-131005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e43b4187673842149946e2c8ad1ad0c2', '其他', '8c63a3324042473ca206c64e911547e3', '01-13-1311', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6e52332030fe44bc9d5889c09a6db12f', '骨蜡', 'e43b4187673842149946e2c8ad1ad0c2', '01-13-1311-131101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('adaf8f8529b24465a16104187100ff97', '漏斗胸成形系统', 'e43b4187673842149946e2c8ad1ad0c2', '01-13-1311-131102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0cadfa12e9cb47e8871c21e6bb3d05db', '胸骨捆扎/抓扣固定系统', 'e43b4187673842149946e2c8ad1ad0c2', '01-13-1311-131103', '1103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('43e8075d02d14d159769d9f8419ad359', '注输、护理和防护器械', '0490821da8a34708b3a3872470479aee', '01-14', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('095292c9d46b4e1baf4f6d2f6f3ada8d', '注射、穿刺器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1401', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('124be53ae3dc458198a9762ef989e995', '注射泵', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f5f2b043d1cc47e095c3b1f36224f2d2', '无菌注射器', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ceb7b86858a340bcad67c262afaf450f', '无针注射器', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1fe8b85783749479202f6014b671884', '笔式注射器', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('723d8ce7a7014333b860f67e2904e7e0', '玻璃注射器', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c68d2f62816e4ef6b9ffea2409d0851d', '注射针', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4864c1c907d24e9980cdbe30ff253780', '注射器辅助推动装置', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7097af58d802403cbebe3775cb43f04c', '穿刺器械', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e74379e36ee34fae9e7f98fc6bb54e5c', '活检针', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140109', '0109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0840e4a809ed49d5ad26395988c5c01b', '活检枪', '095292c9d46b4e1baf4f6d2f6f3ada8d', '01-14-1401-140110', '0110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('77e97702f29245e0aeeb024d8fa3a0cb', '血管内输液器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1402', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('31e84d93ae914445908f705669e7c6ce', '输液泵', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('51708bd305de412da7b81d4bb8b640b0', '输液信息采集系统', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6190a5aff5af4a18ad78cc2b21b3c83a', '输液辅助电子设备', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e92cec0d78d41a884c792744b65d009', '无源输注泵', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('76a16eda9c1e4c9d8318b3ad45ae0bc4', '输液器', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6280b4456bcf43f7a4ff3b0a4a409c30', '静脉输液针', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6cc2fbe2a8244d829ea2e95e7608ee49', '血管内留置针', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('94e8eb86eb7e458cabb6c70153f93f94', '输液连接管路', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140208', '0208');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('90c3811195494fc39e21d62e4b7b9f10', '输液、输血用连接件及附件', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140209', '0209');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('16d3f993ef484cfaa2927a68dd59f7be', '植入式给药器械', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140210', '0210');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b41ad5a5d3de4036b5076daa6da3acd0', '输液袋', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140211', '0211');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('105581ed4caa4c009fce171bfc230c71', '药液用转移、配药器具', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140212', '0212');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db0ec3bff7ef46ce871d2ebdcf2aadb3', '输液用放气针', '77e97702f29245e0aeeb024d8fa3a0cb', '01-14-1402-140213', '0213');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4f1a6a2c2d8b46cdbf10088c805bda1e', '非血管内输液器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1403', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e24da813f3b9427cbcda15a4c57fe907', '肠营养泵', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dece8bd4fc59401087ccb52ad671e963', '胰岛素泵', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('35b7c14844044ce986894771cbc70c2c', '胰岛素泵用皮下输液器', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('97ea903667384b908da27dce97703343', '胰岛素泵用储液器', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('84debb0fef774280b9bb74eebd7ce26b', '肠营养器', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1d20f99ec96a4aba9225578b79ad73e6', '肠营养袋', '4f1a6a2c2d8b46cdbf10088c805bda1e', '01-14-1403-140306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0522fe1c59d245ad9d928d3cb7f89c9b', '止血器具', '43e8075d02d14d159769d9f8419ad359', '01-14-1404', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e807ce285d5d454c8c088c0cdee672ea', '有源止血器', '0522fe1c59d245ad9d928d3cb7f89c9b', '01-14-1404-140401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('efc6cfe03fd545fc95e9bf2e434bae78', '无源止血器', '0522fe1c59d245ad9d928d3cb7f89c9b', '01-14-1404-140402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('16ab6ce2ddc94d978d9009d5d2b8ca82', '无源止血带', '0522fe1c59d245ad9d928d3cb7f89c9b', '01-14-1404-140403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b02e8518184f499c940d26c78aa33afa', '非血管内导（插）管', '43e8075d02d14d159769d9f8419ad359', '01-14-1405', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cec77f2c0f804421ac6b84dacec85eed', '经皮肠营养导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e75687e38124feaa53bd03736318f82', '经鼻肠营养导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('41ba06e07c9a4c6981e3e36c2cfa4168', '导尿管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('74cf083b7dd94ffd845c08dbd723632c', '直肠管（肛门管）', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('028898dd0c234f0c9f893d9ad3271d69', '输尿管支架', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('48b8689b0e5a40d19fa99e98af1e5b88', '引流导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('be159c32b3b64656862f7e94853bea8c', '扩张导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140507', '0507');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4db08b9a18904205922e43d9161bc2ee', '造影导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140508', '0508');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bc16af5138d94160baa059260b6176f2', '测压导管', 'b02e8518184f499c940d26c78aa33afa', '01-14-1405-140509', '0509');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('790c4eced966405ba917e11cb5feda3f', '与非血管内导管配套用体外器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1406', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('540645cfbfce4ba1a954fe2b949c5f53', '颅脑外引流收集装置', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1e401cc7dbc04546877ad816ef0c6855', '胸腔引流装置', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9fd814a3383645b1ae56305f56a996d4', '负压引流器及组件', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bff375d30b234c7a85e430b303e5d08d', '真空负压机', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e16cea45f011418aa9c4763bde2a335b', '负压引流海绵', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2b433f3d36b34e138bec233bf4094060', '负压引流封闭膜', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a898c6f688194ab38e6394fedbdbf853', '医用电动吸引器械', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140607', '0607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1f68a9c97ccb46faaaa0a46e8266a418', '以负压源或压力源为动力吸引器械', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140608', '0608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('24e67b3d6d8e4a98b657febd5d43888b', '医用人工驱动吸引器械', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140609', '0609');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('302ee0d7270f4123bd832c612b4c4439', '医用中心吸引系统', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140610', '0610');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('88db147146484dbe85246499c1eea740', '体外引流、吸引管', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140611', '0611');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('76ad9bb101a4467fbd658f8a48b1a194', '引流袋（容器）/收集袋（容器）、粪便管理器械', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140612', '0612');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('617cb839b66c4e0e88598d6477d45776', '非血管内导管充盈装置', '790c4eced966405ba917e11cb5feda3f', '01-14-1406-140613', '0613');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd7f3c6f5cae41398dabdbd60cbf0030', '清洗、灌洗、吸引、给药器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1407', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2ae2c21295424d7495439d464520cb99', '冲洗器械', 'cd7f3c6f5cae41398dabdbd60cbf0030', '01-14-1407-140701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d465d4e6da1e4322ad06e593fb97209a', '灌肠器', 'cd7f3c6f5cae41398dabdbd60cbf0030', '01-14-1407-140702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7b188337f0e644c3a0786d9820d6b764', '给药器', 'cd7f3c6f5cae41398dabdbd60cbf0030', '01-14-1407-140703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d4b4467ca7574f45a4528ab9d072aeaa', '可吸收外科敷料（材料）', '43e8075d02d14d159769d9f8419ad359', '01-14-1408', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('26dbc2c0ab54456aadcef7f07b20718a', '可吸收外科止血材料', 'd4b4467ca7574f45a4528ab9d072aeaa', '01-14-1408-140801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5597e02609cf45d69e36e774f7321854', '可吸收外科防粘连敷料', 'd4b4467ca7574f45a4528ab9d072aeaa', '01-14-1408-140802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('353b2901247b42bc89ab658e153c7355', '不可吸收外科敷料', '43e8075d02d14d159769d9f8419ad359', '01-14-1409', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0bc1d766a8204032961437973aff91f5', '外科织造布类敷料', '353b2901247b42bc89ab658e153c7355', '01-14-1409-140901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0bc6541086774ca8abcc3d619a02fb97', '外科非织造布敷料', '353b2901247b42bc89ab658e153c7355', '01-14-1409-140902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('349c5f899a574f828ebf5b4ae7094f94', '外科海绵敷料', '353b2901247b42bc89ab658e153c7355', '01-14-1409-140903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b129229aa4574d8f97d5112d4e1357f6', '创面敷料', '43e8075d02d14d159769d9f8419ad359', '01-14-1410', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cde5db03fa76419bbbf4d469ed0a6c12', '创面敷贴', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6ff35d43bb8f4691968ef34e3efde140', '创口贴', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5c524155d1304b83af4122ef3a510d3b', '粉末敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('04bf6373665048948e7aaab667b92f31', '凝胶敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a6fe0c10065c4cf5a37114ea96d16273', '水胶体敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4fa36d4fb9ce43a28d12a296b6076513', '纤维敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141006', '1006');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7bf2c7e7e43246339e11588af6fa2681', '泡沫敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141007', '1007');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0f732306c2914dfc840aa4d7516886ad', '液体、膏状敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141008', '1008');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e916c536868548afa0f67c00736992ec', '隔离敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141009', '1009');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1687411ee4341d3b252bc17f41dd318', '生物敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141010', '1010');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fd935467e4694be0ada30653f9bac4b4', '碳纤维和活性炭敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141011', '1011');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e4e95ad0779496f9bcddec304642523', '含壳聚糖敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141012', '1012');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dd4e0813bf9c494a8d313ffd6cc65fb7', '含银敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141013', '1013');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b38714b7fcbb474699753275763aa532', '胶原贴敷料', 'b129229aa4574d8f97d5112d4e1357f6', '01-14-1410-141014', '1014');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('165637c433ee465290b81f763b11b11d', '包扎敷料', '43e8075d02d14d159769d9f8419ad359', '01-14-1411', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3c09ac84dfd64cc8b747371e5bbac774', '绷带', '165637c433ee465290b81f763b11b11d', '01-14-1411-141101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('87f0ed422a9042df8e6fbcef690a5531', '胶带', '165637c433ee465290b81f763b11b11d', '01-14-1411-141102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f51967bfa0814f89ac5c3220ec57e654', '造口、疤痕护理用品', '43e8075d02d14d159769d9f8419ad359', '01-14-1412', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9e5109b4bebc42af9ec33e2df387349a', '造口护理及辅助器械', 'f51967bfa0814f89ac5c3220ec57e654', '01-14-1412-141201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('762b8e4469a84e8da33fc8ed5cb3790d', '疤痕修复材料', 'f51967bfa0814f89ac5c3220ec57e654', '01-14-1412-141202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ec6e24ed57e74c24a707b6739dec8be9', '手术室感染控制用品', '43e8075d02d14d159769d9f8419ad359', '01-14-1413', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c3db82c713004e13ab2d1d8881fe3463', '手术单', 'ec6e24ed57e74c24a707b6739dec8be9', '01-14-1413-141301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('865a0790e7764e6ca44a10b5dc7d2d91', '手术膜', 'ec6e24ed57e74c24a707b6739dec8be9', '01-14-1413-141302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6db357a838b34ad38e6de94ad871adf4', '外科手套', 'ec6e24ed57e74c24a707b6739dec8be9', '01-14-1413-141303', '1303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b53bd8c2f3b64eceb07e0fe098adcaef', '外科口罩', 'ec6e24ed57e74c24a707b6739dec8be9', '01-14-1413-141304', '1304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('99ab3a0edf1b4905bc3749ea20516f33', '手术室用衣帽', 'ec6e24ed57e74c24a707b6739dec8be9', '01-14-1413-141305', '1305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b1a08accc0654ec4bf4f743dc60dd462', '医护人员防护用品', '43e8075d02d14d159769d9f8419ad359', '01-14-1414', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fff505774bbb4ab1838383c6c112c1ca', '防护口罩', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cc2748dd29ef4abc9097e0950becaa85', '防护服', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('40fd0496a5ff467bab1028705b1aed85', '隔离衣帽', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141403', '1403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5de43bc99ea8468a9c4853c19cd7646f', '手部防护用品', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141404', '1404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('60c0e159a8334649a1246e9936b8eb03', '足部隔离用品', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141405', '1405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8e8ec09f46b74a479297896999e2405e', '隔离护罩', 'b1a08accc0654ec4bf4f743dc60dd462', '01-14-1414-141406', '1406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b06292254d1045429d6286384a5394ba', '病人护理防护用品', '43e8075d02d14d159769d9f8419ad359', '01-14-1415', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4908af3ea85f4a31a0899b5bc8f46b78', '婴儿光疗防护眼罩', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141501', '1501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e495353a49b041c9b28e92be03059464', '眼贴', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141502', '1502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5b2e8afece5445baa00cb20512865648', '鼻部护理器械', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141503', '1503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0228049215e5495189b3f0bd06b48ac5', '海水鼻腔清洗液', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141504', '1504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6284909f96ce4e60bc3b13905bf05772', '垫单', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141505', '1505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ce52aeff3ccc4c3881c8d88a8d73f272', '医用防护衬垫', 'b06292254d1045429d6286384a5394ba', '01-14-1415-141506', '1506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('64221415f3d94638849f182c27bca02e', '其它器械', '43e8075d02d14d159769d9f8419ad359', '01-14-1416', '16');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b0cc7c3d06134fa89c7793f892d88b05', '洁净屏', '64221415f3d94638849f182c27bca02e', '01-14-1416-141601', '1601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c118217e8652498e8216953b0e0bca46', '血管显像设备', '64221415f3d94638849f182c27bca02e', '01-14-1416-141602', '1602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9aec94b7a59a4dbeb889669dce42f5cb', '预充式导管冲洗器', '64221415f3d94638849f182c27bca02e', '01-14-1416-141603', '1603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7a83d82ba9fa4a3a8cd0c46cca733df3', '抗鼻腔过敏凝胶（不含药）', '64221415f3d94638849f182c27bca02e', '01-14-1416-141604', '1604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d7e2d07b72b54227b0a7e454fe049c3e', '通气辅助器械', '64221415f3d94638849f182c27bca02e', '01-14-1416-141605', '1605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b0cc76f1fbdd4ee9a00e4b4125b5e789', '咬口', '64221415f3d94638849f182c27bca02e', '01-14-1416-141606', '1606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('62c5bc0122c044fcaaa1fefc809459a8', '急救毯', '64221415f3d94638849f182c27bca02e', '01-14-1416-141607', '1607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7fdf74db42fb44e7ba70d6055dbfa072', '体表器械固定装置', '64221415f3d94638849f182c27bca02e', '01-14-1416-141608', '1608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b9b59bac55d24401b8653eac1018eddf', '润滑剂及载体', '64221415f3d94638849f182c27bca02e', '01-14-1416-141609', '1609');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('357d892309d24812bfa6811dcf0ece43', '涂抹及吸液材料', '64221415f3d94638849f182c27bca02e', '01-14-1416-141610', '1610');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('90f1bbb8bd584cdf8609e86b9d81a365', '压力绷带', '64221415f3d94638849f182c27bca02e', '01-14-1416-141611', '1611');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cee8aa7a88894acdb6580364acbe8f3f', '医用导管夹', '64221415f3d94638849f182c27bca02e', '01-14-1416-141612', '1612');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('00bbc958976e42abb7e3ec5266297e59', '无菌接管机', '64221415f3d94638849f182c27bca02e', '01-14-1416-141613', '1613');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a5419e8bf7f04b6f86ec8eb5c4476494', '患者承载器械', '0490821da8a34708b3a3872470479aee', '01-15', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3b5120c8334c4c37be8ae55d41a809aa', '手术台', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1501', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2e38c335174f46b69b93aa4053a59db9', '电动手术台（液压、机械、气动等）', '3b5120c8334c4c37be8ae55d41a809aa', '01-15-1501-150101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('81dd792f60bf45ac80de9455cf30378b', '手动手术台（液压）', '3b5120c8334c4c37be8ae55d41a809aa', '01-15-1501-150102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('af3fa3f220e3407ab5ebfcde3d123c93', '手动手术台（机械）', '3b5120c8334c4c37be8ae55d41a809aa', '01-15-1501-150103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9069c8df701d4d3c92ccf96bb36b84a2', '诊疗台', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1502', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da1b8bf1a9e3498a84fe631cc5745ded', '电动诊疗台及诊疗椅', '9069c8df701d4d3c92ccf96bb36b84a2', '01-15-1502-150201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ee6808cff74d45c7ac549e0237255bfd', '手动诊疗台及诊疗椅', '9069c8df701d4d3c92ccf96bb36b84a2', '01-15-1502-150202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd8b7a6ad7e44614807c6bf3d4a25aac', '医用病床', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1503', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b13cf94be9d24ce09ade3d410f56beb8', '电动病床', 'cd8b7a6ad7e44614807c6bf3d4a25aac', '01-15-1503-150301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c9aa5395952e48fcbacd2dc93203e538', '手动病床', 'cd8b7a6ad7e44614807c6bf3d4a25aac', '01-15-1503-150302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('47a3e5e74d394ffea051864a19ece6ee', '医用婴儿床', 'cd8b7a6ad7e44614807c6bf3d4a25aac', '01-15-1503-150303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('82405d76e28143168ad52d44ac551afd', '患者位置固定辅助器械', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1504', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d27427da283e45329e2b7d72916910f4', '电动患者手术位置固定辅助器械', '82405d76e28143168ad52d44ac551afd', '01-15-1504-150401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ebe8cc6ff771487196241fdb0b7d7e3a', '无源患者手术位置固定辅助器械', '82405d76e28143168ad52d44ac551afd', '01-15-1504-150402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0bdfd4024eb45848e25467abe4ce252', '患者转运器械', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1505', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('75b19a729f6d46e9bcef9ef28a6eab2b', '患者运送隔离器械', 'c0bdfd4024eb45848e25467abe4ce252', '01-15-1505-150501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6323afb405a4c39a379859202b246ce', '电动推车、担架等器械', 'c0bdfd4024eb45848e25467abe4ce252', '01-15-1505-150502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a4137448f1b447b8bbab9f2cbc1222e0', '手动推车、担架等器械', 'c0bdfd4024eb45848e25467abe4ce252', '01-15-1505-150503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ec740dc8058147f0b094ba9efc5f17dc', '简易转移器械', 'c0bdfd4024eb45848e25467abe4ce252', '01-15-1505-150504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bb2ed741cb89493b9d4d1295f1fa71b7', '其他转移器械', 'c0bdfd4024eb45848e25467abe4ce252', '01-15-1505-150505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e5eb50fcb654643ba911fe23af2ad80', '防压疮（褥疮）垫', 'a5419e8bf7f04b6f86ec8eb5c4476494', '01-15-1506', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb07aac630c841d9ae49382836083b66', '电动防压疮（褥疮）垫', '4e5eb50fcb654643ba911fe23af2ad80', '01-15-1506-150601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('31b1174cff3f4ac19dfc3dd2689c5ce4', '手动防压疮（褥疮）垫', '4e5eb50fcb654643ba911fe23af2ad80', '01-15-1506-150602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('38cc388e5f624ee69863901392083ded', '眼科器械', '0490821da8a34708b3a3872470479aee', '01-16', '16');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61b52248099a4b8c9d3c0179219411a7', '眼科无源手术器械', '38cc388e5f624ee69863901392083ded', '01-16-1601', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('807693f06d604a6b8c07a6928e8da6c7', '眼用刀', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('468e8ea61eec449582d964057714d240', '眼用凿', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('710e170ffd8a4f608d78b5554261917d', '眼用剪', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bbfa24c3cead4981b33ebef51abdf474', '眼用钳', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ce923fc8f10840aeadce51c58282ba28', '眼用镊', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dcad9c0634974162a101a3ff107031d3', '眼用夹', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('404d1a8e57a04286be96e8d791af75fe', '眼用针', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7d58c002218e4c01ae686f9fd709c681', '眼用钩', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0a4a2f2f23849ee8e4b35f0f5512ff9', '眼用刮匙', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160109', '0109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61bb62d9b00149a8ac3f6a4418006c02', '眼用剥离器', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160110', '0110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ef6bff15f19943308df0e16d7a570c95', '眼用牵开器', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160111', '0111');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e0245e8bfa94266b18e1ed914757afb', '眼用扩张器', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160112', '0112');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b29dd4886d0d4f69b7c78a7f000beedd', '眼用冲吸器', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160113', '0113');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93976fc749c54a5488cf089e6204d695', '眼用钻', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160114', '0114');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da1ac89ad7fe4639a8d41a651f6ad274', '眼用锯', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160115', '0115');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db55b47317a747a7aec23d1aee671f3b', '眼用铲', '61b52248099a4b8c9d3c0179219411a7', '01-16-1601-160116', '0116');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('84434f0ded82405181da5eab9b7386c7', '眼科无源辅助手术器械', '38cc388e5f624ee69863901392083ded', '01-16-1602', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('abf5295fd73041babb46ea95847810f4', '眼用穿刺器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('53ae5bef693d480ba0deefdeb9a2186e', '眼用注入器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b77aec6640274abf9ce1bcc2f6c5619f', '点眼棒', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('90372556de624fe0903f1a01f4f5fef9', '眼用压迫器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('790704e7c50a48549ad574ad322052cc', '眼用保护、支持器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7eab68ece8444324b99d02ed1d4e7300', '眼用器械手柄', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a9a0c492bcf2461a93b5f8daf9feba4f', '眼用固位器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6b65736bdeff46b3a7a9a4379361102d', '眼用测量器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160208', '0208');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('407e0e9789454f889e1aad6815b84999', '眼用取出器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160209', '0209');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ffe62a12cdc44ff38cc7fe6d0fd9a021', '眼用抛光器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160210', '0210');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c94d818ae8424066b8cf385bf8ba7474', '眼用置物台', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160211', '0211');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b59f942906114dc58f1d72f4b6c687a9', '眼用碎核器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160212', '0212');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cfc6e4f04ac045cab44d4dd3444160ca', '眼用咬除器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160213', '0213');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d713d6b5eaa740628231fafc9851aecb', '眼用止血器', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160214', '0214');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fa83dfce82e04e1caef9965a85357c99', '眼用浸泡环', '84434f0ded82405181da5eab9b7386c7', '01-16-1602-160215', '0215');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('69d64f0570c8431da4dbe71384dcaba8', '视光设备和器具', '38cc388e5f624ee69863901392083ded', '01-16-1603', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6248344aa79f4c6e91f33256d05ff326', '验光设备和器具', '69d64f0570c8431da4dbe71384dcaba8', '01-16-1603-160301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('50d82ae5d88f4ed188d45f0cba815e05', '视功能检查设备和器具', '69d64f0570c8431da4dbe71384dcaba8', '01-16-1603-160302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6e787d835e53480b82c27739b421ed2c', '视觉治疗设备', '69d64f0570c8431da4dbe71384dcaba8', '01-16-1603-160303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8d5f585663e48a5af0062bac149f265', '眼科测量诊断设备和器具', '38cc388e5f624ee69863901392083ded', '01-16-1604', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('51472a8c7fd540bea4f4366c44476f6d', '眼科激光诊断设备', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3ce9a7f2f51a48c482a2d0de0a00a1d2', '眼压持续监测仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('558545091d174164b83d12a473cfdd3d', '眼科超声诊断设备', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b8fd95e355ae41269d5fb604b75041bd', '光学相干断层扫描仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5101be1614ff4275aa625b60cdac6b03', '眼用照相机', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c6750e506134bcd8a6074ee9938037c', '眼底造影机', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ac6aab6b1f5e49a082e183d1cad27f39', '裂隙灯显微镜', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d16f853bebf444aca373a8bcc01b35e7', '直接检眼镜', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb3a8215090d49199d9ca088956d4a91', '间接检眼镜', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160409', '0409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eeb526b8a4f84df99c1a004fa5196f30', '角膜内皮细胞显微镜', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160410', '0410');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('300b6bf18bec4f899bbd1742bedc2067', '角膜共焦显微镜', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160411', '0411');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b5b95ed19aed460a90f74b1cfcf7c896', '角膜地形图仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160412', '0412');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6850327d92824706b23a43a3deea6dfa', '角膜测厚仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160413', '0413');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ac68f29433b746bc8fab45207f4439b9', '眼前节测量诊断系统', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160414', '0414');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4515782705e9406696a720bb316c2e0d', '眼组织深度测量仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160415', '0415');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c73956b8846d4e0ca0144e300d59c990', '黄斑完整性评估仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160416', '0416');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0f80613cd88f4c458e828f0e81d69677', '眼压计', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160417', '0417');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0548528eeb3a49aea9d37c261accbd83', '眼球突出计', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160418', '0418');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3cc5713b48764d6da1b814ec342d2f3f', '干眼检测仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160419', '0419');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e0c54ee316f8489eacde48fca01918e4', '视网膜自适应光学成像仪', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160420', '0420');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b52a6dfa35ad45988eec95890c90f883', '眼科诊断辅助器具', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160421', '0421');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('732db02a7a2b444ca80a59bba74bba81', '眼力器', 'b8d5f585663e48a5af0062bac149f265', '01-16-1604-160422', '0422');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('00ab8fcbf6d64166845718536e867b6e', '眼科治疗和手术设备、辅助器具', '38cc388e5f624ee69863901392083ded', '01-16-1605', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('48feb3ae45a44667b2c91ba8672ac900', '眼科超声手术设备', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('41ab576a39024e99a88669a7da72ab7c', '眼科激光治疗设备', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('61e034837af144988881563b0b8cf9e0', '眼科内窥镜及附件', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('97ba65df2d504826b62f72143aa3a99d', '眼科冷冻治疗设备', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('483eb69987984c4184dec03711d5967c', '其他眼科治疗和手术设备', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('108faad47568406781910e47962b3b2b', '眼科治疗和手术辅助器具', '00ab8fcbf6d64166845718536e867b6e', '01-16-1605-160506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8c3c56669a8842b38560e5d83e7d1d08', '眼科矫治和防护器具', '38cc388e5f624ee69863901392083ded', '01-16-1606', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f68252a59111465a93331ac6181732e5', '接触镜', '8c3c56669a8842b38560e5d83e7d1d08', '01-16-1606-160601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3e77d54b45fb4456b284070cffbb70ac', '接触镜护理产品', '8c3c56669a8842b38560e5d83e7d1d08', '01-16-1606-160602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ea7c56ca76a84b80adf59c48acbe75c2', '防护器具', '8c3c56669a8842b38560e5d83e7d1d08', '01-16-1606-160603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3bb059ea94924f49a75b5c2a21ae0165', '助视器', '8c3c56669a8842b38560e5d83e7d1d08', '01-16-1606-160604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0d9507e616e444069c15d31270a8899b', '眼科植入物及辅助器械', '38cc388e5f624ee69863901392083ded', '01-16-1607', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5048a98a437b461cb65d67f3aee0dc5c', '人工晶状体', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a1a4224530464262ac376dbfabbcf50f', '眼内填充物', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('abc1101788e6479d822c3433ab0a8d0f', '青光眼引流装置', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('97ad7550d9e24631987970dd270d44be', '眼用粘弹剂', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('be704577197e483284aa1d1d323c9ed2', '泪点塞', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3b5e6f92a4a440aa8b9b3e30c007ae9d', '义眼台', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160706', '0706');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ae4552cf9a5743319477beb22e69e85b', '囊袋张力环', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160707', '0707');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0e9d850f86444978b800bfdea5ae3c31', '人工玻璃体球囊', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160708', '0708');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('18f24370119244d0b94745edb2c37382', '组织工程生物羊膜', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160709', '0709');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('58131f75a3d04383a82d6fbbcfcb7a28', '角膜基质片', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160710', '0710');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('954784aed1df47ff9b33f4037dbe6163', '角膜基质环', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160711', '0711');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b3baf806146e42e68f645f9beb2815df', '泪道管', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160712', '0712');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4f0d4627d8e24595b5e2822030030d90', '硅胶环扎带', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160713', '0713');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('931e91ba751546a9a156abfde60be9ff', '义眼片', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160714', '0714');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('72e04e672a8c4b45b00bec0e5e74bc8c', '人工晶状体、人工玻璃体植入器械', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160715', '0715');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a331d5f792d1402db6c823eaf7ea9b8c', '囊袋张力环植入器械', '0d9507e616e444069c15d31270a8899b', '01-16-1607-160716', '0716');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e18943ae24714a559e2fe8f875b710fb', '口腔科器械', '0490821da8a34708b3a3872470479aee', '01-17', '17');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8d9a395c9d5746b9a67199ebe0087689', '口腔诊察设备', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1701', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('681c229269b240f9b0f6399b8008277a', '牙周袋探测设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f97a2a07670e49c9a459eebfc61e023a', '牙髓活力测试设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bfb394ad8b3b4d6eae643cdf2c04d001', '牙本质测量设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e3b9e2d1c5348efae39e5cb3d6a01d3', '龋齿探测设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f9903c885b7e45e4962a503db84102ef', '口腔成像设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('70a61a7613054a40afc2149e36d3dded', '口腔照明设备', '8d9a395c9d5746b9a67199ebe0087689', '01-17-1701-170106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ba7f35aee244428b8623ca056640f6d8', '口腔诊察器具', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1702', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('70699b1d0baf45499cb7ff110616714a', '手动测量用器械', 'ba7f35aee244428b8623ca056640f6d8', '01-17-1702-170201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('714ce49b0b6343bca42708c97e342e7a', '口腔用镜', 'ba7f35aee244428b8623ca056640f6d8', '01-17-1702-170202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b39e9fa196ae45d79e487ae3214e0f84', '口腔成像辅助器具', 'ba7f35aee244428b8623ca056640f6d8', '01-17-1702-170203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1732b820e5bd436081d3b4a8b89dfc5e', '口腔治疗设备', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1703', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('26488a307e81426faf508a44d14bc66d', '牙科治疗机', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7d6a17a7a9084f41a507827b6acf8536', '牙科用椅', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4dd8f747b6c04c56b0ce05b0d60f1ba4', '口腔洁治清洗设备及附件', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('25b745b0697e416c87bffddabed24400', '牙科手机及附件', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ad63b97766034ba692bcb919824ece43', '口腔正负压设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fa2ba249f2b44ad3bf37030dd6b0c5f2', '固化设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('84e5770be7f142e39a84245c3e844d5f', '牙科种植用设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a1e1f4c88bdb4c3795080884797b655f', '牙齿漂白设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170308', '0308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('06234807fdd14788bff9f3c511df3327', '根管治疗设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170309', '0309');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('684b9f8760824bd7af851cc405c7702c', '口腔麻醉推注设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170310', '0310');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2a677ba2b6a44a77a1400ec8c16c0c49', '银汞合金调合器', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170311', '0311');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d229b9cab23944b88589194822f006fd', '口腔用骨粉制备设备', '1732b820e5bd436081d3b4a8b89dfc5e', '01-17-1703-170312', '0312');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7da716bcb4fc4a689fe651defd9098a3', '口腔治疗器具', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1704', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('115b6b328c6647e1b44376da19dae683', '口腔手术刀、凿', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a057bd28ed9f4e8eaffa2ef1224c8206', '口腔用钳', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('68272c709a674ee390dba7ba85ee513b', '口腔手术剪', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('87b324b705324b8791187c3617d42069', '牙挺', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('05828f912bcb42c1aa9d8dbb8a8bfd40', '口腔针', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0e048aba2d1d4ce08decdc895dbbfc9c', '牙科锉', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('366571cd6dda4d63bf4cdb0355bab1ac', '口腔车针、钻', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('76ac3fd10dbb47b7a7a9c5639e911130', '洁治器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1e663039dd34488a9eb7ff38d1002af5', '口腔隔离器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170409', '0409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('166dedf0f67a4cf8af49bb18a97e0e88', '打磨抛光清洁器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170410', '0410');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b44f75d804b14e83bf819a0e4bfa9deb', '种植体安装辅助器械', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170411', '0411');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b5d12c4f66ff47ca8b1766453bcd83b8', '材料输送器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170412', '0412');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b3ac45e23cbd4486b7646145c6ccd5d4', '正畸材料处理器械', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170413', '0413');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('15fa3ce2389740e2931b584e6da01a88', '口腔清洗器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170414', '0414');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8492b57a6def438390b54cdae40a0463', '口腔综合治疗设备配件', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170415', '0415');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ae433b2c740e45d98f8af7f8bf22a7a5', '口腔用镊、夹', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170416', '0416');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd4ecdcf23b94a97851edcf1477dbdb7', '口腔注射用具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170417', '0417');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0eeb0d03441f40a38265b4b83a152872', '口腔分离牵开用具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170418', '0418');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cd46a00f63cf45368d0fe20c6f0e242d', '去冠器', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170419', '0419');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('53ea59c38f5542f7b8d94cb96e2aa23d', '治疗辅助器具', '7da716bcb4fc4a689fe651defd9098a3', '01-17-1704-170420', '0420');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1efe05da8801488db72171a0449bf371', '口腔充填修复材料', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1705', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c470ae9e159a46deb6bf42a25883b7a4', '水门汀', '1efe05da8801488db72171a0449bf371', '01-17-1705-170501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('37dec214d71a42c4ae4333724fd83a00', '粘接剂', '1efe05da8801488db72171a0449bf371', '01-17-1705-170502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0e342772a9a74a839e0e2497c92e1a05', '根管充填封闭材料', '1efe05da8801488db72171a0449bf371', '01-17-1705-170503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e1704bfe2ff34395bbe6f8a6c8c324a0', '复合树脂', '1efe05da8801488db72171a0449bf371', '01-17-1705-170504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df551358c9a84c46b2fe0dd46a1722d6', '复合体', '1efe05da8801488db72171a0449bf371', '01-17-1705-170505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('207a887a331e4542a089121a5d6465c5', '银汞合金', '1efe05da8801488db72171a0449bf371', '01-17-1705-170506', '0506');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aef8894f516c4fd7b1e8798ab465d3db', '临时充填材料', '1efe05da8801488db72171a0449bf371', '01-17-1705-170507', '0507');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('07df96a3b92f44708eba7ba3adea74d9', '盖髓材料', '1efe05da8801488db72171a0449bf371', '01-17-1705-170508', '0508');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b3303ce882de4d14b7338ffcd5f6eacd', '口腔义齿制作材料', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1706', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2070fff579954f6eb2398251e752a465', '义齿用金属材料及制品', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ffad0fcacefe46188eeea41542bc2291', '义齿用陶瓷材料及制品', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3fcb31fce32b4dfe8d074e7100d284af', '义齿用高分子材料及制品', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dfa5e45052ca49ecba8c1960bd84bcb4', '定制式义齿', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a430e4e2b164be8b013ab920ab5c41e', '固位桩', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0733eceee12240d9b05e791d0b5b62fe', '牙托梗', 'b3303ce882de4d14b7338ffcd5f6eacd', '01-17-1706-170606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6bcef6fe528475b8a7a09ad18ee554a', '口腔正畸材料及制品', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1707', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5ab2103b81434d13b68e2cd358c3a073', '托槽', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aba5283d5d1044cb9533d038c16ead8b', '正畸丝', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0955b46048944a4fa37791a2f694c446', '带环及颊面管', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('94efdcc643c34b8eb47359985b77aa8f', '正畸基托聚合物', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b6038e1ce2424dffa49d4172d0ca519f', '正畸弹簧', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6fff1a71173e44b49096ba95bfed8954', '正畸弹性体附件', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170706', '0706');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a2e49f03d27446688a9fc6a4f41d8c08', '矫治器具及附件', 'b6bcef6fe528475b8a7a09ad18ee554a', '01-17-1707-170707', '0707');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ff3c9535b45646efa1e81f665af86f59', '口腔植入及组织重建材料', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1708', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('36b04117ee8345ad80424b8a27b32632', '牙种植体', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8e641685811542dbaa8ec66d7ac3ba68', '基台及附件', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b60d2b72032d482187fc3bccef6c6117', '种植支抗', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3b1e092003114ebd9a7adf028fab8b7d', '种植体密封材料', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170804', '0804');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('18cdc7a8c24446f7bc417a3b1657c45e', '种植辅助材料', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170805', '0805');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a722d53679084d289fe58825ca77216a', '骨填充及修复材料', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170806', '0806');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('06437f13b95040278d4ec5b38a4257f8', '颌面固定植入物', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170807', '0807');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('db1d4ba444134ca099cb04f82cacce0e', '颌面部赝复及修复重建材料及制品', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170808', '0808');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('59ccb942e0194a0782c680dc987dad0e', '基台定制材料', 'ff3c9535b45646efa1e81f665af86f59', '01-17-1708-170809', '0809');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1b69e7a9702c4e9d86352560bf074d7f', '口腔治疗辅助材料', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1709', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2409eab24b7b4dc1b928948842341a7c', '根管预备辅助材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('effb7aac6e0649c4941f2b813ed4acb5', '吸潮纸尖', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0ad2c05846a5445897bf97c4b20c2eaa', '酸蚀剂', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('732a417494f0441ca59aee1342f492c8', '预处理剂', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170904', '0904');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aa00d22a27d245d6a13384eda9723782', '排龈材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170905', '0905');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('17d39352b8c640f7a4607d2715615e59', '研磨抛光材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170906', '0906');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d1076c6d172343dd83067f35d9a1748c', '印模材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170907', '0907');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0357686ef8e74296b78a5ab120c3bbfd', '模型材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170908', '0908');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fd3d5e01839c4c3185279d07eb025c87', '铸造包埋材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170909', '0909');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e56efc1786884b2ab32bfdf5f4c81899', '蜡', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170910', '0910');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('334451571e914e11b4e4ec5381f88f22', '牙科分离剂', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170911', '0911');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('65812e0bfd54449e8dc7f45a07325e56', '咬合关系记录/检查材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170912', '0912');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df8ebdf08277491684fcde0728a37399', '隔离及赋形材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170913', '0913');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('52d3d0520cba4a478e9056f6f1f83f85', '义齿试用材料', '1b69e7a9702c4e9d86352560bf074d7f', '01-17-1709-170914', '0914');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d8bbb73685d049dd8f8cb925b65f8c7f', '其他口腔材料', 'e18943ae24714a559e2fe8f875b710fb', '01-17-1710', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1e21071e62214cab8f4b93660aeb6ab0', '牙周塞治剂', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3fe2c3b80516425fa0dd734a3275a036', '口腔溃疡、组织创面愈合治疗辅助材料', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bdd8d887c2f14023af1e63f79d2a30bd', '脱敏剂', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c84308f1fe24ef3b31c2be47f1832ff', '防龋材料', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9e3d90d546e14e30aa5379c03529bf39', '牙科膜片', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7b28a68900914fa5818425d710e1236b', '牙齿漂白材料', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171006', '1006');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('732cdafbd4e343698fab047749dc8884', '菌斑/龋齿指示剂', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171007', '1007');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a2e259cc2f604afeabce9654a6ad4b1b', '牙髓活力测试剂', 'd8bbb73685d049dd8f8cb925b65f8c7f', '01-17-1710-171008', '1008');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('070c85cbc6f4425685b9802036516bef', '妇产科、辅助生殖和避孕器械', '0490821da8a34708b3a3872470479aee', '01-18', '18');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a883a5ac611741f68fcf3de92d823215', '妇产科手术器械', '070c85cbc6f4425685b9802036516bef', '01-18-1801', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b956deeda3024da89c494403c12df706', '妇产科用刀', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c68540eded984d08a78c3fd7d843dbe5', '妇产科用剪', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('99056fc31b6643ae9fefed9a7161c4ce', '妇产科用钳', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ec31c0284b30481eb64e7135cb73d681', '妇产科用镊、夹、钩、针', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8673b4991c484a88a22c7ef85e71131d', '妇产科用扩张器、牵开器', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d7e957c9882d47b7a312517fc7754ea5', '助产器械', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2bfe9b4e80724b1d94a4f6a4a1123b45', '阴道洗涤器/给药器', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('399610b09512447ebd991387eb79978a', '妇科剥离器械', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bb6c3341665a4917a3df756be525aaa4', '子宫操纵器', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180109', '0109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('214310387825496ea79226a040c50b33', '子宫输卵管造影、输卵管通液器械', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180110', '0110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e35d24a61a8e45bb9df6c21bf75ac652', '妇科压板', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180111', '0111');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9339594cfda94796a1af0118a12470c0', '医用妇科护垫', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180112', '0112');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('92dfec1eb47d4aaeb914b4c268b003c6', '凝胶', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180113', '0113');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c09d708bd69c45a5a7b1ba4a2474c043', '阴道填塞材料', 'a883a5ac611741f68fcf3de92d823215', '01-18-1801-180114', '0114');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7075e8af7d4f4da49bb3eab901ced6d9', '妇产科测量、监护设备', '070c85cbc6f4425685b9802036516bef', '01-18-1802', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9def16990d3846308068b9119333c0d8', '超声多普勒胎儿监护设备', '7075e8af7d4f4da49bb3eab901ced6d9', '01-18-1802-180201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b060780e39dc4754b45071ecb0f1a876', '超声多普勒胎儿心率设备', '7075e8af7d4f4da49bb3eab901ced6d9', '01-18-1802-180202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0a11c8202fb84766881dfedc86854e14', '手动测量器械', '7075e8af7d4f4da49bb3eab901ced6d9', '01-18-1802-180203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b186f707fd0f4759845a5df244d0bf5e', '妇产科诊断器械', '070c85cbc6f4425685b9802036516bef', '01-18-1803', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('284d0b81c93c4871ac2b3b757682dac6', '妇科超声诊断设备', 'b186f707fd0f4759845a5df244d0bf5e', '01-18-1803-180301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0847b0b32ee24d4a9757f1c3e46a479e', '阴道镜', 'b186f707fd0f4759845a5df244d0bf5e', '01-18-1803-180302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93e3b5a1da7f4924970d9594d5aa16da', '妇科内窥镜', 'b186f707fd0f4759845a5df244d0bf5e', '01-18-1803-180303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8ab7f844eb174f5baf35572d27cf4998', '妇科采样器械', 'b186f707fd0f4759845a5df244d0bf5e', '01-18-1803-180304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d7f9fc36e22f40339a2a36b61cc4b1c3', '妇科检查器械', 'b186f707fd0f4759845a5df244d0bf5e', '01-18-1803-180305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9e6a68e9f89a46948b1559d26fd000ae', '妇产科治疗器械', '070c85cbc6f4425685b9802036516bef', '01-18-1804', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('252836b396024ad1b57f2030b5d577ad', '妇科物理治疗器械', '9e6a68e9f89a46948b1559d26fd000ae', '01-18-1804-180401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a6a4f18e200a4c299f555d5007dcde1c', '妇科假体器械', '9e6a68e9f89a46948b1559d26fd000ae', '01-18-1804-180402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7b4d2e7660f84e48aaa22f18b4014794', '妇产科承载器械', '070c85cbc6f4425685b9802036516bef', '01-18-1805', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a36576dd8b4b4b6b950eb4b01e9e8409', '产床', '7b4d2e7660f84e48aaa22f18b4014794', '01-18-1805-180501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0d90472902146ffabdca846df51a600', '妇科手术/检查床', '7b4d2e7660f84e48aaa22f18b4014794', '01-18-1805-180502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6f79a42c17494e3d94df495f70d4dc72', '妊娠控制器械', '070c85cbc6f4425685b9802036516bef', '01-18-1806', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('49ed718fff1b40f591fecb23081fd984', '宫内节育器及取放器械', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bd7bf532421c40049c643d8f0f9001f8', '输卵（精）管封闭器械', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e121bfc4febe4d119e2275ea21594cac', '屏障式避孕器械', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('52836810d718499f9ffe90fdfed5309a', '避孕凝胶', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('84fb0cb0c4e84b9db88ecc683291bb10', '结扎手术器械', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1bd98394878443738a7250f303c72541', '宫腔负压吸引设备及附件', '6f79a42c17494e3d94df495f70d4dc72', '01-18-1806-180606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5caa7dec0ce1406da747f5dab789c0d1', '辅助生殖器械', '070c85cbc6f4425685b9802036516bef', '01-18-1807', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2f2ee9acd3864c4aa378ac0664328d90', '辅助生殖导管', '5caa7dec0ce1406da747f5dab789c0d1', '01-18-1807-180701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cf5a319792474bfd8a1f078609c9ccb7', '辅助生殖穿刺取卵/取精针', '5caa7dec0ce1406da747f5dab789c0d1', '01-18-1807-180702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3a6cbabc40124c16b81a71ae23c5b6bb', '辅助生殖微型工具', '5caa7dec0ce1406da747f5dab789c0d1', '01-18-1807-180703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f3482aa5682f4693856dc612f44b71b5', '体外辅助生殖用液', '5caa7dec0ce1406da747f5dab789c0d1', '01-18-1807-180704', '0704');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('352f5cfab3194754933530b28729f480', '辅助生殖专用仪器', '5caa7dec0ce1406da747f5dab789c0d1', '01-18-1807-180705', '0705');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4452fb8cb53f40b0a36a030e7b5f7387', '医用康复器械', '0490821da8a34708b3a3872470479aee', '01-19', '19');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0ace9c8008e04e53afec78a228a2f3f4', '认知言语视听障碍康复设备', '4452fb8cb53f40b0a36a030e7b5f7387', '01-19-1901', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('34ef0f96c9ca45c89dac105fa9398933', '认知障碍康复设备', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2af7d52dad0c41f08e8d6950b4e0811f', '视觉康复设备', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f183912d29cb4884a50f261de7796c25', '听觉康复设备', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('de7d25ec67f64671b65298344969cc8d', '言语障碍康复设备', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('797e3db43ab046bfbee8da8321c20d4c', '真耳测试仪', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('21e386e1371f4ae5a48285bea70b3c33', '助讲器', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c374b096452d4539b113b4184276ade0', '助听器', '0ace9c8008e04e53afec78a228a2f3f4', '01-19-1901-190107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('06d2ce21d988450385ceb7ce4cf66ad8', '运动康复训练器械', '4452fb8cb53f40b0a36a030e7b5f7387', '01-19-1902', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4d5e588c358c4351b599aceb3103676f', '步态训练设备', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2d0cbc870f8e49ea9f739d8f2c2cb7a4', '康复训练床', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d2a3a051f47442e29508adc10761bdb3', '平衡训练设备', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ca343f816df54e6abb92a7e5f01fbe8d', '振动训练设备', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6a2eb967099c44279894021060db3f52', '关节训练设备', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c86d1f0fbc1a40539445f06d62329a34', '盆底肌肉训练设备', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb5c9e832fc04d0eab5101c88c820fa2', '舌肌康复训练器', '06d2ce21d988450385ceb7ce4cf66ad8', '01-19-1902-190207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('18d31e98b73e4b369913c633e54c47b9', '助行器械', '4452fb8cb53f40b0a36a030e7b5f7387', '01-19-1903', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7d71a27f88fc49c4aa3af3541472d302', '医用轮椅车', '18d31e98b73e4b369913c633e54c47b9', '01-19-1903-190301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('57dfbd4924d842709343066f92fbc21d', '辅助行走站立器械', '18d31e98b73e4b369913c633e54c47b9', '01-19-1903-190302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d655b7bc5fb94692beb3fd1ce17aba4d', '矫形固定器械', '4452fb8cb53f40b0a36a030e7b5f7387', '01-19-1904', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('34c88210968747dab45cf6444ca7659e', '矫形器', 'd655b7bc5fb94692beb3fd1ce17aba4d', '01-19-1904-190401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('08f0be9006474d8e93227d69c74be7c1', '固定器', 'd655b7bc5fb94692beb3fd1ce17aba4d', '01-19-1904-190402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1c20993d767c4f89b2a8fe554a29cc99', '中医器械', '0490821da8a34708b3a3872470479aee', '01-20', '20');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b24750f580644eddb10ad546b5b2daab', '中医诊断设备', '1c20993d767c4f89b2a8fe554a29cc99', '01-20-2001', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7cdb9a2662734166a37d0f5680f5bb33', '脉诊设备', 'b24750f580644eddb10ad546b5b2daab', '01-20-2001-200101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1d0a8a32c904472299fed407be22af4c', '望诊设备', 'b24750f580644eddb10ad546b5b2daab', '01-20-2001-200102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0570f01ea8364078af41132e0a909b36', '穴位阻抗检测设备', 'b24750f580644eddb10ad546b5b2daab', '01-20-2001-200103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dcb30d4546764069991fa49db17ebef2', '中医治疗设备', '1c20993d767c4f89b2a8fe554a29cc99', '01-20-2002', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('586583cfa60f454085715457cfab661a', '穴位电刺激设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('159d4d228167405ca76486453390be8b', '温针治疗设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('33744ad081364f0a9815040ebdf6b539', '灸疗设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200203', '0203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1098e9c6fad4465ead484f9832486ec5', '拔罐设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200204', '0204');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2c55ae9cd2ee43ce9874f33e49e4c560', '熏蒸治疗设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200205', '0205');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e5d65900301d4cc38526b878a8385661', '穴位微波刺激设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200206', '0206');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cf4584f8a65b40cf89c6b4bb06c03221', '穴位激光刺激设备', 'dcb30d4546764069991fa49db17ebef2', '01-20-2002-200207', '0207');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c866a0087935487081303268974cc869', '中医器具', '1c20993d767c4f89b2a8fe554a29cc99', '01-20-2003', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('eb8539f99fae43f389f95a2280d8f089', '针灸针', 'c866a0087935487081303268974cc869', '01-20-2003-200301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e799e7267bcd47ed98d8b35fb4959f05', '三棱针', 'c866a0087935487081303268974cc869', '01-20-2003-200302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5b97a809983a44fab247e81b7962c469', '小针刀', 'c866a0087935487081303268974cc869', '01-20-2003-200303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('01cd2b70626b457ebf51b9bd5e348b89', '皮肤针', 'c866a0087935487081303268974cc869', '01-20-2003-200304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('37dc8897d81546c295eceacbe1978285', '滚针', 'c866a0087935487081303268974cc869', '01-20-2003-200305', '0305');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3ca08f508c404563a132654ce5b3aea3', '皮内针', 'c866a0087935487081303268974cc869', '01-20-2003-200306', '0306');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('740a2be13afc4d3c9127c7c023a40811', '埋线针', 'c866a0087935487081303268974cc869', '01-20-2003-200307', '0307');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cdb6716102164a4b87510ab987535552', '灸疗器具', 'c866a0087935487081303268974cc869', '01-20-2003-200308', '0308');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0a73c9cd4f954fef820d5642e9c6c87f', '穴位磁疗器具', 'c866a0087935487081303268974cc869', '01-20-2003-200309', '0309');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4e10c9efbe6b4c9db91a150453f3276e', '浮针', 'c866a0087935487081303268974cc869', '01-20-2003-200310', '0310');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b5320a2f43354c018c4f1738dcdeeab8', '穴位压力刺激器具', 'c866a0087935487081303268974cc869', '01-20-2003-200311', '0311');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('da0845fab3e64938a40e3774afd1f1d2', '刮痧器具', 'c866a0087935487081303268974cc869', '01-20-2003-200312', '0312');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('943d4173fd3a4fcdbffe4d6876070029', '拔罐器具', 'c866a0087935487081303268974cc869', '01-20-2003-200313', '0313');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2ce42144f3c3484ea87c21d80e04e4cd', '医用软件', '0490821da8a34708b3a3872470479aee', '01-21', '21');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c2c5b38e6a744be48838d2553169d9b5', '治疗计划软件', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2101', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('bd93375160fb45e098b5c123169224c3', '放射治疗计划系统软件', 'c2c5b38e6a744be48838d2553169d9b5', '01-21-2101-210101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c9f8ec6e9565497da83248b1b1804fe2', '放射治疗辅助软件', 'c2c5b38e6a744be48838d2553169d9b5', '01-21-2101-210102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4b2fd6e6e68a4830913914ca03d1e526', '手术计划软件', 'c2c5b38e6a744be48838d2553169d9b5', '01-21-2101-210103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('920a8c2bcb9b4ee885029536d93cb22a', '影像处理软件', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2102', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e04143fead254fdfacd9b0a46c810477', '医学影像存储与传输系统软件', '920a8c2bcb9b4ee885029536d93cb22a', '01-21-2102-210201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4c73c03ca93042dca973d2f5fdfe886e', '医学影像处理软件', '920a8c2bcb9b4ee885029536d93cb22a', '01-21-2102-210202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a005a9fc660645f790663fa6ea4832c9', '数据处理软件', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2103', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('dead010acce74bf0a15c658c3e7f575c', '监护软件', 'a005a9fc660645f790663fa6ea4832c9', '01-21-2103-210301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cecf431267b5404783a818930210fa1f', '生理信号处理软件', 'a005a9fc660645f790663fa6ea4832c9', '01-21-2103-210302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5bd6e631810d439b9f91ea8d481cd53b', '决策支持软件', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2104', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('594f31b05535496fa03047b27dd2ed20', '药物计算软件', '5bd6e631810d439b9f91ea8d481cd53b', '01-21-2104-210401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d02af463e4cd4a1d8d7990a326e0acac', '计算机辅助诊断/分析软件', '5bd6e631810d439b9f91ea8d481cd53b', '01-21-2104-210402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('98ac6fb376b945e4814392c5f7afac6b', '中医诊疗软件', '5bd6e631810d439b9f91ea8d481cd53b', '01-21-2104-210403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ce3f2f4b8c2e4e88bbc934ec3b0d45b3', '体外诊断类软件', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2105', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('58c44209517e4c18838bcb1c522e2580', '医学显微影像分析软件', 'ce3f2f4b8c2e4e88bbc934ec3b0d45b3', '01-21-2105-210501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3eda640b41184888b0690495f208be4c', '筛查、分析软件', 'ce3f2f4b8c2e4e88bbc934ec3b0d45b3', '01-21-2105-210502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('cb01653e15e742e4a4cc3acfb69d2860', '其他', '2ce42144f3c3484ea87c21d80e04e4cd', '01-21-2106', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('d06184444c47432fafae0c6f30c26371', '康复训练软件', 'cb01653e15e742e4a4cc3acfb69d2860', '01-21-2106-210601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('249eef06243848ddbe8413aad9a09ede', '临床检验器械', '0490821da8a34708b3a3872470479aee', '01-22', '22');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1d764ae977f740df9650148973d6d397', '血液学分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2201', '01');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4030ed43d4af4fc0af7ea77bbec20fc0', '血型分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220101', '0101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9c4e653de9134b648ef45fd246738985', '血细胞分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220102', '0102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('06a01d6f9cf04ad7933955740e87abbc', '血细胞形态分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220103', '0103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f360bace3a464eeebf7e48f22813edf2', '凝血分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220104', '0104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('05f8994999e14f34b903954ebc015f8d', '血小板分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220105', '0105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3ba261cf81de48578a1f874763435d2c', '血流变分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220106', '0106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('aa5dadabb2414e7b8cd01bddf27ddabe', '红细胞沉降仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220107', '0107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('35cf7fd15bee4cf9bd5f7a5efe4bf38e', '流式细胞分析仪器', '1d764ae977f740df9650148973d6d397', '01-22-2201-220108', '0108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2f603952a56e40c48dfc686ad7b13f25', '生化分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2202', '02');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8b96cdc1e1ff40609c6c2e1cd5dd4103', '生化分析仪器', '2f603952a56e40c48dfc686ad7b13f25', '01-22-2202-220201', '0201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('16481e2f82d34a54942f0ec57db16f53', '血糖及血糖相关参数分析仪器', '2f603952a56e40c48dfc686ad7b13f25', '01-22-2202-220202', '0202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0bd7df8da4c140a3ae325674aa41a118', '电解质及血气分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2203', '03');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b49d31333aa9463bbfe9ffc042ca5371', '电解质分析仪器', '0bd7df8da4c140a3ae325674aa41a118', '01-22-2203-220301', '0301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('99d1c47ea6e648618031006942b870ed', '血气分析仪器', '0bd7df8da4c140a3ae325674aa41a118', '01-22-2203-220302', '0302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9d248f2440c94dd7acb3eeaa708c20d4', '电解质血气分析仪器', '0bd7df8da4c140a3ae325674aa41a118', '01-22-2203-220303', '0303');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('109e42e240994c23bdae51179a0043cc', '电解质血气检测电极', '0bd7df8da4c140a3ae325674aa41a118', '01-22-2203-220304', '0304');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8e81032029fc4baa99d5e66728986421', '免疫分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2204', '04');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('375375699f5c45008d6de8984b1e00a9', '酶联免疫分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220401', '0401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('92756ebf19734a71b40c123f5f201cf9', '化学发光免疫分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220402', '0402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ce588fffbe4549b88634bf85bce8b352', '荧光免疫分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220403', '0403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ebefa25e9d584a89a7b735c8944db8bc', '免疫层析分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220404', '0404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4a2913a41227439ba096a0e24f26946f', '免疫印迹仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220405', '0405');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3fb9680b38a543a7bb8a072dbf01bef9', '免疫散射浊度分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220406', '0406');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('04cd99fc755147689a123c89f27ff7c3', '免疫分析一体机', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220407', '0407');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1dd9a20a298943acba7eb7f9495d5abd', '间接免疫荧光分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220408', '0408');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0ec061a28e6481ca9e6dcc594c25cc6', '生化免疫分析仪器', '8e81032029fc4baa99d5e66728986421', '01-22-2204-220409', '0409');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('200cffc1722c4ef382528f9e69f6cdfc', '分子生物学分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2205', '05');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c31bacd69102483ca8ff4e47c09229cd', '基因测序仪器', '200cffc1722c4ef382528f9e69f6cdfc', '01-22-2205-220501', '0501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('098c73d50c3947cc87026129d1ee21e7', 'sanger测序仪器', '200cffc1722c4ef382528f9e69f6cdfc', '01-22-2205-220502', '0502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('df8bf7ef9c684b1bbb59670f3d73f109', '核酸扩增分析仪器', '200cffc1722c4ef382528f9e69f6cdfc', '01-22-2205-220503', '0503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ce22ad20fd6c45619387e9687326e76b', '核酸扩增仪器', '200cffc1722c4ef382528f9e69f6cdfc', '01-22-2205-220504', '0504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ef80bdadada14dd995fae3f2f3e4c693', '核酸分子杂交仪器', '200cffc1722c4ef382528f9e69f6cdfc', '01-22-2205-220505', '0505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('48937a96849948cd84be09a9ffb2c123', '微生物分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2206', '06');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e4477aa386e943eb85e5bcdcd1b3a487', '微生物比浊仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220601', '0601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5d88b1f078274194914372dd60a62018', '微生物培养监测仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220602', '0602');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fec267d9582b4c22b31a2d5cbe986d58', '微生物药敏培养监测仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220603', '0603');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('481ab0cc563d403e91cf02e66715b09c', '微生物鉴定仪器（非质谱）', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220604', '0604');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c1aebb6b54dc4d84884d9cb9630dad96', '微生物质谱鉴定仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220605', '0605');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('490c85fb151d45f7b1e67b978471c3a6', '微生物鉴定药敏分析仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220606', '0606');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f824da1307794c5ba3fefacff5a06339', '细菌内毒素/真菌葡聚糖检测仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220607', '0607');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('57bba8f5b547429a9ef1a840dff0ecbc', '幽门螺旋杆菌分析仪器', '48937a96849948cd84be09a9ffb2c123', '01-22-2206-220608', '0608');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9360250f05f54b668e25d41aec43b195', '扫描图像分析系统', '249eef06243848ddbe8413aad9a09ede', '01-22-2207', '07');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('613f90edd9c64063bb658712142c1bed', '医用显微镜', '9360250f05f54b668e25d41aec43b195', '01-22-2207-220701', '0701');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8f70f58104ff41a9a1b3ed10cd22410a', '图像扫描仪器', '9360250f05f54b668e25d41aec43b195', '01-22-2207-220702', '0702');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0ef83f4bbb4f4ad8b6686fd899a1eada', '图像分析仪器', '9360250f05f54b668e25d41aec43b195', '01-22-2207-220703', '0703');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('b78edd6bdfab4da88e294f4597a06ae3', '放射性核素标本测定装置', '249eef06243848ddbe8413aad9a09ede', '01-22-2208', '08');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4ed9150e67b04329b54d0c99b552eba3', '放射免疫γ计数器', 'b78edd6bdfab4da88e294f4597a06ae3', '01-22-2208-220801', '0801');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6993dd8b63ed41eca2eef2cc69f05467', '液体闪烁计数器', 'b78edd6bdfab4da88e294f4597a06ae3', '01-22-2208-220802', '0802');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('3d9b47cce16d4beba967bc5f1277e1ef', '放射性层析扫描装置', 'b78edd6bdfab4da88e294f4597a06ae3', '01-22-2208-220803', '0803');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9f909fe115a24b57b5e70ec9c78904f5', '尿液及其他样本分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2209', '09');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('7c80136e958649069b41e3a8cf13b400', '干化学尿液分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220901', '0901');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('397bad3a695545e58cb57beabe0039d9', '尿液有形成分分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220902', '0902');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('499c704546ec48c89eab248f45a98d29', '尿液分析系统', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220903', '0903');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4a572cba797642328f031e4e170e5485', '粪便分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220904', '0904');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f52d27a4a51b4b4ab8037c3835ce2f94', '精子分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220905', '0905');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('899f01f88f0b4485bec4d8056f26d30d', '生殖道分泌物分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220906', '0906');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6bec39ca88e642c699840800b96a5a52', '其他体液分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220907', '0907');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('90f5e7416e0748a6b94fd550e16151df', '其他体液形态学分析仪器', '9f909fe115a24b57b5e70ec9c78904f5', '01-22-2209-220908', '0908');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('6462bffdad934c57b1eb4ac910f0e6f6', '其他医用分析设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2210', '10');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e9871371f1cc43d5883ce56d50e96c74', '流式点阵仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221001', '1001');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9596660dbead44ea9e8f2af1e5a2484b', '微量元素分析仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221002', '1002');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c8f91670e4114fd29d0b73887a3f88b4', '质谱检测系统', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221003', '1003');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('14175d8ee7ed4c599a4e0b6f38ffac83', '液相色谱分析仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221004', '1004');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4f96f7e1670a47c5bf3bfe1839a7471d', '色谱柱', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221005', '1005');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f0a3fbc12e0645699f3dffcb25cca58f', '渗透压测定仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221006', '1006');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c2cd5b34f3b0481fbc328c926ff136b7', '循环肿瘤细胞分析仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221007', '1007');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9ce50578603148448dc44ea199789750', '生物芯片分析仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221008', '1008');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9e60b642b3d94024b5dca7861e636e50', '电泳仪器', '6462bffdad934c57b1eb4ac910f0e6f6', '01-22-2210-221009', '1009');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e58d2541e051470eb64ba84eed209c1a', '采样设备和器具', '249eef06243848ddbe8413aad9a09ede', '01-22-2211', '11');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8cf5826137684903a19c92e0bd5a0ff0', '动静脉采血针及连接件', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221101', '1101');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ed88b76490f74f669f2da34d59ab1418', '末梢采血针', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221102', '1102');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('0f11a4b91ba54344a1934f5e088d749a', '采血笔', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221103', '1103');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c96ec16621964a9ab8694facee494264', '静脉血样采血管', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221104', '1104');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('8b04f3f4c48c4d798497f4b209d10325', '末梢采血管', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221105', '1105');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('fe6abe3cc9354a16a7d272e9577f6ab7', '末梢血采集容器', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221106', '1106');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a49f79c29ba4f03831a98c4eb5e78b4', '血液采集卡', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221107', '1107');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2ee7288184994def887e2d59124d9b93', '胃隐血采集器具', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221108', '1108');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('044ff86afb144f31a49bf9dc8b66cb89', '其他样本采集器具', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221109', '1109');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f16270de29ef430e9ffd5c9ba6707824', '激光采血仪', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221110', '1110');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9297a486fbe84d97ad6041376471e8b1', '足跟采血器', 'e58d2541e051470eb64ba84eed209c1a', '01-22-2211-221111', '1111');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('edcb16354e11473b8c701ad5ea67ca35', '形态学分析前样本处理设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2212', '12');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('40ef40e44b95427e9a7bfeb913debef1', '血细胞分析前样本处理仪器', 'edcb16354e11473b8c701ad5ea67ca35', '01-22-2212-221201', '1201');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a0bf61273aa843da9aaf0a92af4a332f', '病理分析前样本处理仪器', 'edcb16354e11473b8c701ad5ea67ca35', '01-22-2212-221202', '1202');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('4abea079f42c481cb4aba88be9b0c25d', '流式细胞术样本裂解仪', 'edcb16354e11473b8c701ad5ea67ca35', '01-22-2212-221203', '1203');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f8f84ebcf4a54082a510331514a630a7', '样本分离设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2213', '13');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('c0871801cbf8450aabe7d946f4015dbd', '医用离心机', 'f8f84ebcf4a54082a510331514a630a7', '01-22-2213-221301', '1301');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('59e116db4c674ed499e4380134a3d161', '核酸提取纯化仪', 'f8f84ebcf4a54082a510331514a630a7', '01-22-2213-221302', '1302');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('407285033229425db3259674fefa9871', '培养与孵育设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2214', '14');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('79ef7f4c0c164afc80879a1211950894', '医用培养/恒温箱', '407285033229425db3259674fefa9871', '01-22-2214-221401', '1401');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('e9a7561883cc4c50b40a7f4d6cf8e079', '厌氧培养系统', '407285033229425db3259674fefa9871', '01-22-2214-221402', '1402');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('1ad1a7ed75b14d648fd112e291fa2802', '孵育器', '407285033229425db3259674fefa9871', '01-22-2214-221403', '1403');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('03dd731913ea4e3b9150b348fe37a877', '血小板振荡器', '407285033229425db3259674fefa9871', '01-22-2214-221404', '1404');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('ca28394d6def46329c65fa3f5d31c029', '检验及其他辅助设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2215', '15');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('93f6a197e35e4939936e95eaf782158c', '洗板机', 'ca28394d6def46329c65fa3f5d31c029', '01-22-2215-221501', '1501');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('32720ee18b1c4dd9ba4c52e7a4d1bf34', '计数板', 'ca28394d6def46329c65fa3f5d31c029', '01-22-2215-221502', '1502');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('2952cecafcb04bb59d970f5d7d956a50', '自动加样系统', 'ca28394d6def46329c65fa3f5d31c029', '01-22-2215-221503', '1503');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f1486f2ba91a43feb115ed7f804a6efd', '低温储存设备', 'ca28394d6def46329c65fa3f5d31c029', '01-22-2215-221504', '1504');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('5e82bd0cbad04fa9a5324f206a048c8a', '样本处理系统', 'ca28394d6def46329c65fa3f5d31c029', '01-22-2215-221505', '1505');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('a6d24ce1cda144ae9cd07e220c8547e0', '医用生物防护设备', '249eef06243848ddbe8413aad9a09ede', '01-22-2216', '16');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('9a4bb9dc01984fb3b7ee199ad82cb644', '生物安全柜', 'a6d24ce1cda144ae9cd07e220c8547e0', '01-22-2216-221601', '1601');
INSERT INTO "public"."sys_auth_group" ("uid", "name", "parent_uid", "encoded", "classification_code") VALUES ('f2c6080c832d47c49f645b35c171fee7', '洁净工作台', 'a6d24ce1cda144ae9cd07e220c8547e0', '01-22-2216-221602', '1602');
-- 2021-01-06 医疗器械分类数据 end

--2020-01-07 update sr file name history
delete from sr_name_transfer_mapping;

INSERT INTO sr_name_transfer_mapping (file_name,mapped_name,sr_type) VALUES
	 ('SR Report.msg','服务记录单.msg',3),
	 ('SR Report.msg','服务记录单.msg',6),
	 ('PM Checklist.docx','保养报告.docx',4),
	 ('PM Checklist.docx','保养报告.docx',5),
	 ('Other.pdf','服务记录单.pdf',4),
	 ('Other.pdf','服务记录单.pdf',5),
	 ('Logs.docx','保养报告.docx',4),
	 ('Logs.docx','保养报告.docx',5),
	 ('PM Report.jpg','服务记录单.jpg',4),
	 ('PM Report.jpg','服务记录单.jpg',5),
	 ('Tube Failure Report.bmp','球管失效报告.bmp',3),
	 ('Tube Failure Report.bmp','球管失效报告.bmp',6),
	 ('SHA PM List.docx','保养照片.docx',4),
	 ('SHA PM List.docx','保养照片.docx',5),
	 ('Other.JPG','服务记录单.JPG',3),
	 ('Other.JPG','服务记录单.JPG',6),
	 ('SR Report.doc','保养报告.doc',4),
	 ('SR Report.doc','保养报告.doc',5),
	 ('Other.PNG','确认函.PNG',3),
	 ('Other.PNG','确认函.PNG',6),
	 ('PM Report.jpeg','服务记录单.jpeg',3),
	 ('PM Report.jpeg','服务记录单.jpeg',6),
	 ('Other update new.pdf','临床应用报告.pdf',9),
	 ('SR Report.jpeg','服务记录单.jpeg',3),
	 ('SR Report.jpeg','服务记录单.jpeg',6),
	 ('SR Report.jpg','服务记录单.jpg',4),
	 ('SR Report.jpg','服务记录单.jpg',5),
	 ('SR Report.xlsx','服务记录单.xlsx',3),
	 ('SR Report.xlsx','服务记录单.xlsx',6),
	 ('SR Report.pdf','培训完成确认函.pdf',9),
	 ('Tube Failure Report.docx','球管失效报告.docx',3),
	 ('Tube Failure Report.docx','球管失效报告.docx',6),
	 ('PM Report.docx','保养报告.docx',4),
	 ('PM Report.docx','保养报告.docx',5),
	 ('update.pdf','保养报告.pdf',3),
	 ('update.pdf','保养报告.pdf',6),
	 ('FMI CCR.docx','现场更新服务记录单.docx',3),
	 ('FMI CCR.docx','现场更新服务记录单.docx',6),
	 ('SR Report.png','服务记录单.png',4),
	 ('SR Report.png','服务记录单.png',5),
	 ('PM Report.doc','保养报告.doc',4),
	 ('PM Report.doc','保养报告.doc',5),
	 ('PM Report.JPG','服务记录单.JPG',3),
	 ('PM Report.JPG','服务记录单.JPG',6),
	 ('Tube Failure Report.pdf','球管失效报告.pdf',3),
	 ('Tube Failure Report.pdf','球管失效报告.pdf',6),
	 ('FMI CCR.jpg','现场更新服务记录单.jpg',4),
	 ('FMI CCR.jpg','现场更新服务记录单.jpg',5),
	 ('AWB1.pdf','确认函.pdf',3),
	 ('AWB1.pdf','确认函.pdf',6),
	 ('Other update.pdf','临床应用报告.pdf',9),
	 ('Other.txt','日志文件.txt',3),
	 ('Other.txt','日志文件.txt',6),
	 ('PM Checklist update-pdf new.pdf','服务记录单.pdf',4),
	 ('PM Checklist update-pdf new.pdf','服务记录单.pdf',5),
	 ('SR Report.JPG','服务记录单.JPG',3),
	 ('SR Report.JPG','服务记录单.JPG',6),
	 ('PLC.jpg','定位卡.jpg',3),
	 ('PLC.jpg','定位卡.jpg',6),
	 ('Tube Failure Report.xlsx','球管失效报告.xlsx',3),
	 ('Tube Failure Report.xlsx','球管失效报告.xlsx',6),
	 ('PM Checklist update.pdf','服务记录单.pdf',4),
	 ('PM Checklist update.pdf','服务记录单.pdf',5),
	 ('PM Checklist.htmL','保养报告.htmL',4),
	 ('PM Checklist.htmL','保养报告.htmL',5),
	 ('SR Report update-pdf new.pdf','保养报告.pdf',3),
	 ('SR Report update-pdf new.pdf','保养报告.pdf',6),
	 ('SR Report.jpeg','服务记录单.jpeg',4),
	 ('SR Report.jpeg','服务记录单.jpeg',5),
	 ('Tube Failure Report.7z','球管失效报告.7z',3),
	 ('Tube Failure Report.7z','球管失效报告.7z',6),
	 ('PM Report.png','保养报告.png',4),
	 ('PM Report.png','保养报告.png',5),
	 ('PM Report.log','日志文件.log',4),
	 ('PM Report.log','日志文件.log',5),
	 ('update.pdf','临床应用报告.pdf',9),
	 ('PM Checklist.jpg','保养报告.jpg',4),
	 ('PM Checklist.jpg','保养报告.jpg',5),
	 ('Electrical Safety Test.pdf','确认函.pdf',4),
	 ('Electrical Safety Test.pdf','确认函.pdf',5),
	 ('Tube Failure Report.png','球管失效报告.png',3),
	 ('Tube Failure Report.png','球管失效报告.png',6),
	 ('Logs.xlsx','日志文件.xlsx',3),
	 ('Logs.xlsx','日志文件.xlsx',6),
	 ('Other.pdf','临床应用报告.pdf',9),
	 ('SR Report.pdf','服务记录单.pdf',4),
	 ('SR Report.pdf','服务记录单.pdf',5),
	 ('Logs.doc','保养报告.doc',4),
	 ('Logs.doc','保养报告.doc',5),
	 ('Tube Failure Report.jpg','球管失效报告.jpg',3),
	 ('Tube Failure Report.jpg','球管失效报告.jpg',6),
	 ('AWB1.jpg','确认函.jpg',3),
	 ('AWB1.jpg','确认函.jpg',6),
	 ('Tube Failure Report.jpeg','球管失效报告.jpeg',3),
	 ('Tube Failure Report.jpeg','球管失效报告.jpeg',6),
	 ('SHA.pdf','保养照片.pdf',4),
	 ('SHA.pdf','保养照片.pdf',5),
	 ('SR Report.PNG','服务记录单.PNG',3),
	 ('SR Report.PNG','服务记录单.PNG',6),
	 ('Tube Failure Report.PNG','球管失效报告.PNG',4),
	 ('Tube Failure Report.PNG','球管失效报告.PNG',5),
	 ('FMI CCR.pdf','现场更新服务记录单.pdf',4),
	 ('FMI CCR.pdf','现场更新服务记录单.pdf',5),
	 ('Tube Failure Report.pdf','保养报告.pdf',4),
	 ('Tube Failure Report.pdf','保养报告.pdf',5),
	 ('SR Report.jpeg','培训完成确认函.jpeg',9),
	 ('Logs.pdf','保养报告.pdf',4),
	 ('Logs.pdf','保养报告.pdf',5),
	 ('SR Report.docx','保养报告.docx',4),
	 ('SR Report.docx','保养报告.docx',5),
	 ('SHA.SPT','日志文件.SPT',4),
	 ('SHA.SPT','日志文件.SPT',5),
	 ('PM Checklist.doc','保养报告.doc',4),
	 ('PM Checklist.doc','保养报告.doc',5),
	 ('PM Report.PDF','保养报告.PDF',4),
	 ('PM Report.PDF','保养报告.PDF',5),
	 ('PM Checklist.PNG','确认函.PNG',4),
	 ('PM Checklist.PNG','确认函.PNG',5),
	 ('SHA PM List.pdf','保养报告.pdf',4),
	 ('SHA PM List.pdf','保养报告.pdf',5),
	 ('PM Checklist.xlsx','保养报告.xlsx',4),
	 ('PM Checklist.xlsx','保养报告.xlsx',5),
	 ('PM Checklist.pdf','保养报告.pdf',4),
	 ('PM Checklist.pdf','保养报告.pdf',5),
	 ('PM Report.xlsx','保养报告.xlsx',4),
	 ('PM Report.xlsx','保养报告.xlsx',5),
	 ('INS Checklist.pdf','保养报告.pdf',4),
	 ('INS Checklist.pdf','保养报告.pdf',5),
	 ('PM Checklist.pdf','保养报告.pdf',3),
	 ('PM Checklist.pdf','保养报告.pdf',6),
	 ('SHA PM List.xlsx','保养报告.xlsx',4),
	 ('SHA PM List.xlsx','保养报告.xlsx',5),
	 ('PM Report.txt','保养报告.txt',4),
	 ('PM Report.txt','保养报告.txt',5),
	 ('App.jpg','培训完成确认函.jpg',9),
	 ('SR Report.docx','服务记录单.docx',3),
	 ('SR Report.docx','服务记录单.docx',6),
	 ('FMI CCR.pdf','现场更新服务记录单.pdf',3),
	 ('FMI CCR.pdf','现场更新服务记录单.pdf',6),
	 ('SR Report.pptx','服务记录单.pptx',3),
	 ('SR Report.pptx','服务记录单.pptx',6),
	 ('Other.PNG','确认函.PNG',4),
	 ('Other.PNG','确认函.PNG',5),
	 ('Other.doc','保养报告.doc',4),
	 ('Other.doc','保养报告.doc',5),
	 ('Demo Checklist.pdf','球管失效报告.pdf',4),
	 ('Demo Checklist.pdf','球管失效报告.pdf',5),
	 ('SR Report.pdf','服务记录单.pdf',3),
	 ('SR Report.pdf','服务记录单.pdf',6),
	 ('Other.jpg','确认函.jpg',4),
	 ('Other.jpg','确认函.jpg',5),
	 ('App.docx','临床应用报告.docx',9),
	 ('App.pdf','临床应用报告.pdf',9),
	 ('SR Report.zip','服务记录单.zip',3),
	 ('SR Report.zip','服务记录单.zip',6),
	 ('SR Report.jpg','培训完成确认函.jpg',9),
	 ('SHA.docx','保养照片.docx',4),
	 ('SHA.docx','保养照片.docx',5),
	 ('Other.docx','确认函.docx',4),
	 ('Other.docx','确认函.docx',5),
	 ('Other.msg','服务记录单.msg',3),
	 ('Other.msg','服务记录单.msg',6),
	 ('SR Report.jfif','服务记录单.jfif',3),
	 ('SR Report.jfif','服务记录单.jfif',6);

update service_request_report service set file_name = (case when trans.transfer_name is not null then trans.transfer_name else service.file_name end) from (select file.id, replace(file.file_name, file.sub_file, transfer.mapped_name) transfer_name  from (select sr.id, event.event_type, sr.file_name, substring(sr.file_name, position('_' in sr.file_name)+1) sub_file from service_request_report sr, asset_lifecycle_event event where sr.alce_id = event.id and position('_' in sr.file_name) > 0) file, sr_name_transfer_mapping transfer where file.event_type = transfer.sr_type and upper(file.sub_file) = upper(transfer.file_name)) trans where service.id = trans.id;
--2020-01-07 update sr file name history

-- 2021-01-11 质控评审链接初始化 start
delete from "public"."audit_default_link";
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('33', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/asset/info/AssetList.xhtml', '设备档案基本信息', '1');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('34', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/asset/contract/ContractList.xhtml', '设备合同管理', '2');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('35', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/asset/info/AssetTerminateEntryList.xhtml', '报废资产管理', '3');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('36', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/woMgr/workOrderList.xhtml', '设备维修工作台', '4');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('37', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '5');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('38', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '6');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('39', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '7');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('40', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '8');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('41', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/nurseSite/assetOptRecordList.xhtml', '开关机登记', '9');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('42', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '51', '/geapm/portal/asset/arrived/arrivalList.xhtml', '到货清单', '10');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('43', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '53', '/geapm/portalwoMgr/workOrderList.xhtml', '设备维修工作台', '11');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('44', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '53', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '12');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('45', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '53', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '13');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('46', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '53', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '14');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('47', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '53', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '15');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('48', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '55', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '16');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('49', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '55', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '17');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('50', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '57', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '18');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('51', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '57', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '19');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('52', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '58', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '20');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('53', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '58', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '21');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('54', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '58', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '22');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('55', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '59', '/geapm/portal/nurseSite/maintainMain.xhtml', ' 日常检查', '23');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('56', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '59', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '24');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('57', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '59', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '25');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('58', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/wo/woExport/woReportMain.xhtml', '设备维修工作台', '26');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('59', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '27');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('60', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '28');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('61', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '29');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('62', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '30');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('63', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '31');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('64', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '60', '/geapm/portal/measuring/measuringTypeConfigList.xhtml', '计量类别设置', '32');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('65', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '61', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '33');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('66', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '61', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '34');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('67', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '61', '/geapm/portal/measuring/measuringResultMain.xhtml', '计量信息', '35');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('68', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '62', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '36');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('69', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '62', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '37');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('70', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '62', '/geapm/portal/measuring/measuringResultMain.xhtml', '计量信息', '38');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('71', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '65', '/geapm/portal/woMgr/workOrderList.xhtml', '设备维修工作台', '39');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('72', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '65', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '40');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('73', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '65', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '41');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('74', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/woMgr/workOrderList.xhtml', '设备维修工作台', '42');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('75', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '43');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('76', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '44');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('77', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/BorrowAcceptanceV2/List.xhtml', '借用受理', '45');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('78', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/ReturnAcceptanceV2/List.xhtml', '归还受理', '46');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('79', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '67', '/geapm/portal/allocationCenter/StockOfAssets.xhtml', '设备库存情况', '47');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('80', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '68', '/geapm/portal/BorrowAcceptanceV2/List.xhtml', '借用受理', '48');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('81', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '68', '/geapm/portal/ReturnAcceptanceV2/List.xhtml', '归还受理', '49');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('82', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '68', '/geapm/portal/allocationCenter/StockOfAssets.xhtml', '设备库存情况', '50');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('83', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '68', '/geapm/portal/ReturnAcceptanceV2/ListForQuery.xhtml', '设备计费情况', '51');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('84', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '72', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '52');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('85', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '72', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '53');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('86', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/asset/info/AssetList.xhtml', '设备档案基本信息', '54');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('87', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/asset/contract/ContractList.xhtml', '设备合同管理', '55');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('88', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/woMgr/workOrderList.xhtml', '设备维修工作台', '56');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('89', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/wo/assetWorkOrder.xhtml', '设备维修记录', '57');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('90', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/wo/woExport/woReportMain.xhtml', '维修工单报表', '58');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('91', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/pm/pmOrderListAll.xhtml', '保养工作台', '59');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('92', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/nurseSite/maintainMain.xhtml', '日常检查', '60');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('93', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/nurseSite/maintainExportMain.xhtml', '日常检查报表', '61');
INSERT INTO "public"."audit_default_link" ("id", "created_by", "created_date", "hospital_uid", "institution_uid", "last_modified_by", "last_modified_date", "site_uid", "tenant_uid", "group_id", "link", "name", "order_num") VALUES ('94', 'superadmin', '0001-01-01 00:00:00', '', '', '', NULL, '', '', '73', '/geapm/portal/nurseSite/assetOptRecordList.xhtml', '开关机登记', '62');
-- 2021-01-11 质控评审链接初始化 end

-- 2021-01-13 更新权重
UPDATE zkb_key_behavior_index_config set paste_quick_response_rate_full_score = 20 where id = '1                               ';



-- svc_engine_logistics_status

INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('1', 'Order not booked', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('2', 'Pending CS', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('3', 'Warehouse denial', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('4', 'Manual Hold', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('5', 'OE Error', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('6', 'On Hold', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('7', 'ITC screening in progress', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('8', 'Pending IR completion', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('9', 'Pending Triage', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('10', 'Pick Release Pending', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('11', 'Release to Supplier', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('12', 'Awaiting Pick', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('16', 'Dropship complete', 'shipping', '发货中');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('17', 'Shipped', 'shipping', '发货中');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('18', 'Delivered', 'arrivaled', '送达');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('19', 'Closed', 'arrivaled', '送达');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('15', 'Shipped not consigned', 'shipping', '发货中');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('14', 'Ship from Oversea', 'shipping', '发货中');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('13', 'Part on shipping line', 'deliveredPending', '待发货');
INSERT INTO svc_engine_logistics_status(id, decoded_status, local_status_code, local_status_name) VALUES ('21', 'Rsvd no pick', 'deliveredPending', '待发货');

-- org_info  是否显示物流信息
update  org_info o set display_svc = '0';


-- 197家医院显示物流信息
update org_info set display_svc = '1' where hospital_ucmid = 'AH00265';
update org_info set display_svc = '1' where hospital_ucmid = 'AH00270';
update org_info set display_svc = '1' where hospital_ucmid = 'AH00278';
update org_info set display_svc = '1' where hospital_ucmid = 'AH00290';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00070';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00764';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00638';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00643';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00644';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00652';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00862';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00742';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00156';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00765';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00778';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00802';
update org_info set display_svc = '1' where hospital_ucmid = 'JS00326';
update org_info set display_svc = '1' where hospital_ucmid = 'JX00147';
update org_info set display_svc = '1' where hospital_ucmid = 'JX00343';
update org_info set display_svc = '1' where hospital_ucmid = 'JX00345';
update org_info set display_svc = '1' where hospital_ucmid = 'JX00346';
update org_info set display_svc = '1' where hospital_ucmid = 'U-2BSANS';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00763';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00997';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00650';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00659';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00660';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00674';
update org_info set display_svc = '1' where hospital_ucmid = 'SD01953';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00908';
update org_info set display_svc = '1' where hospital_ucmid = 'SD12086';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00015';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00335';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00992';
update org_info set display_svc = '1' where hospital_ucmid = 'SD03501';
update org_info set display_svc = '1' where hospital_ucmid = 'SD15280';
update org_info set display_svc = '1' where hospital_ucmid = 'SD09277';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00257';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00789';
update org_info set display_svc = '1' where hospital_ucmid = 'SD00732';
update org_info set display_svc = '1' where hospital_ucmid = 'SH09214';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00058';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00166';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00292';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00395';
update org_info set display_svc = '1' where hospital_ucmid = 'SH09177';
update org_info set display_svc = '1' where hospital_ucmid = 'U-2BPBCF';
update org_info set display_svc = '1' where hospital_ucmid = 'SH08371';
update org_info set display_svc = '1' where hospital_ucmid = 'SH09179';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00293';
update org_info set display_svc = '1' where hospital_ucmid = 'SH09184';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00296';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00129';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00233';
update org_info set display_svc = '1' where hospital_ucmid = 'SH00220';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00319';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00325';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00340';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00615';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00617';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00801';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ11942';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ13174';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00577';
update org_info set display_svc = '1' where hospital_ucmid = 'ZJ00589';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00022';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00070';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00071';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00099';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00272';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00279';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00037';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00072';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00073';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00381';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ08527';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00176';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00341';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00156';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00172';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00175';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00294';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00299';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00306';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00364';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ08853';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00161';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00386';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00026';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00038';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00283';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00286';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00290';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00314';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00316';
update org_info set display_svc = '1' where hospital_ucmid = 'BJ00323';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00687';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00646';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00605';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00612';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00001';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00513';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00532';
update org_info set display_svc = '1' where hospital_ucmid = 'HB00881';
update org_info set display_svc = '1' where hospital_ucmid = 'HN00689';
update org_info set display_svc = '1' where hospital_ucmid = 'HN00007';
update org_info set display_svc = '1' where hospital_ucmid = 'HN00536';
update org_info set display_svc = '1' where hospital_ucmid = 'HN01008';
update org_info set display_svc = '1' where hospital_ucmid = 'HN00763';
update org_info set display_svc = '1' where hospital_ucmid = 'HL00416';
update org_info set display_svc = '1' where hospital_ucmid = 'HL00454';
update org_info set display_svc = '1' where hospital_ucmid = 'HL00487';
update org_info set display_svc = '1' where hospital_ucmid = 'HL00506';
update org_info set display_svc = '1' where hospital_ucmid = 'HL00652';
update org_info set display_svc = '1' where hospital_ucmid = 'JL00216';
update org_info set display_svc = '1' where hospital_ucmid = 'JL00217';
update org_info set display_svc = '1' where hospital_ucmid = 'JL00222';
update org_info set display_svc = '1' where hospital_ucmid = 'JL00225';
update org_info set display_svc = '1' where hospital_ucmid = 'JL00238';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00182';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00004';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00007';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00432';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00523';
update org_info set display_svc = '1' where hospital_ucmid = 'LN00018';
update org_info set display_svc = '1' where hospital_ucmid = 'NM00068';
update org_info set display_svc = '1' where hospital_ucmid = 'NM11419';
update org_info set display_svc = '1' where hospital_ucmid = 'NM00193';
update org_info set display_svc = '1' where hospital_ucmid = 'NM00206';
update org_info set display_svc = '1' where hospital_ucmid = 'SJ00618';
update org_info set display_svc = '1' where hospital_ucmid = 'SJ00511';
update org_info set display_svc = '1' where hospital_ucmid = 'SJ00536';
update org_info set display_svc = '1' where hospital_ucmid = 'SJ01005';
update org_info set display_svc = '1' where hospital_ucmid = 'TJ00067';
update org_info set display_svc = '1' where hospital_ucmid = 'TJ00114';
update org_info set display_svc = '1' where hospital_ucmid = 'TJ00117';
update org_info set display_svc = '1' where hospital_ucmid = 'TJ00161';
update org_info set display_svc = '1' where hospital_ucmid = 'TJ00162';
update org_info set display_svc = '1' where hospital_ucmid = 'FJ00005';
update org_info set display_svc = '1' where hospital_ucmid = 'FJ00370';
update org_info set display_svc = '1' where hospital_ucmid = 'FJ00403';
update org_info set display_svc = '1' where hospital_ucmid = 'FJ00401';
update org_info set display_svc = '1' where hospital_ucmid = 'FJ00410';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00963';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00025';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00046';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00098';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00595';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00632';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00817';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00820';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00833';
update org_info set display_svc = '1' where hospital_ucmid = 'GD07832';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00871';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00181';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00885';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00857';
update org_info set display_svc = '1' where hospital_ucmid = 'GD00292';
update org_info set display_svc = '1' where hospital_ucmid = 'GX00384';
update org_info set display_svc = '1' where hospital_ucmid = 'GX00025';
update org_info set display_svc = '1' where hospital_ucmid = 'GX00034';
update org_info set display_svc = '1' where hospital_ucmid = 'HA08897';
update org_info set display_svc = '1' where hospital_ucmid = 'HU10686';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00618';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00313';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00380';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00387';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00397';
update org_info set display_svc = '1' where hospital_ucmid = 'HU00652';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00241';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00644';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00440';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00509';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00024';
update org_info set display_svc = '1' where hospital_ucmid = 'HX00407';
update org_info set display_svc = '1' where hospital_ucmid = 'GS00012';
update org_info set display_svc = '1' where hospital_ucmid = 'GS00022';
update org_info set display_svc = '1' where hospital_ucmid = 'GS00199';
update org_info set display_svc = '1' where hospital_ucmid = 'GZ00228';
update org_info set display_svc = '1' where hospital_ucmid = 'NX00059';
update org_info set display_svc = '1' where hospital_ucmid = 'NX00066';
update org_info set display_svc = '1' where hospital_ucmid = 'QH00067';
update org_info set display_svc = '1' where hospital_ucmid = 'SX00258';
update org_info set display_svc = '1' where hospital_ucmid = 'SX00538';
update org_info set display_svc = '1' where hospital_ucmid = 'SX00055';
update org_info set display_svc = '1' where hospital_ucmid = 'SX00762';
update org_info set display_svc = '1' where hospital_ucmid = 'SX00796';
update org_info set display_svc = '1' where hospital_ucmid = 'SC00640';
update org_info set display_svc = '1' where hospital_ucmid = 'SC00264';
update org_info set display_svc = '1' where hospital_ucmid = 'SC00907';
update org_info set display_svc = '1' where hospital_ucmid = 'XJ00313';
update org_info set display_svc = '1' where hospital_ucmid = 'XJ00314';
update org_info set display_svc = '1' where hospital_ucmid = 'U-1K7248';
update org_info set display_svc = '1' where hospital_ucmid = 'CQ00131';
update org_info set display_svc = '1' where hospital_ucmid = 'CQ00108';
update org_info set display_svc = '1' where hospital_ucmid = 'CQ00140';

-- 初始化 audit_file表新加字段by_admin
update audit_file set by_admin= true where id in (select id from audit_default_file);
update audit_file set by_admin= false where id not in (select id from audit_default_file);

alter table audit_default_file add by_admin boolean;
