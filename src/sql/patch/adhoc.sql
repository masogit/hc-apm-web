create index wechat_message_history_request_source on wechat_message_history (request_source);

--修改Asset_info.name字段长度，批量资产创建使用
ALTER TABLE asset_info alter COLUMN name type character varying(128);

-- game plan添加文件上传类型
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','SparePartExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','MVSControlExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','MVSMatrixExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','IBReportExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);

create index asset_info_connect_to_insite on asset_info (connect_to_insite);
create index asset_info_ib_device_status on asset_info (ib_device_status);
create index asset_info_one_qr_code on asset_info (one_qr_code);




--2021-03-17
--初始化zone_target_info表中数据 （start）
delete from zone_target_info where quarter_start_date = '2020-10-01' and quarter_end_date = '2020-12-31';

insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('安徽', 1586.0, 518.0, 0.3266, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('北京', 2546.0, 395.0, 0.1551, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('福建', 1615.0, 724.0, 0.4483, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('广西', 924.0, 106.0, 0.1147, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('贵州', 831.0, 72.0, 0.0866, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('河北', 2004.0, 918.0, 0.4581, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('河南', 2757.0, 1165.0, 0.4226, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('黑龙江', 855.0, 255.0, 0.2982, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('湖北', 2076.0, 474.0, 0.2283, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('湖南', 1443.0, 227.0, 0.1573, '2020-10-01', '2020-12-31', now());


insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('吉林', 700.0, 206.0, 0.2943, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('江苏', 3691.0, 1466.0, 0.3972, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('江西', 1069.0, 460.0, 0.4303, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('辽宁', 1413.0, 345.0, 0.2442, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('内蒙古', 894.0, 262.0, 0.2931, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('山东', 3799.0, 935.0, 0.2461, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('山西', 1140.0, 501.0, 0.4395, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('陕西', 1353.0, 518.0, 0.3829, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('上海', 1900.0, 743.0, 0.3911, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('天津', 649.0, 562.0, 0.8659, '2020-10-01', '2020-12-31', now());


insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('外蒙古', 19.0, 0.0, 0.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('新疆', 585.0, 109.0, 0.1863, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('云南', 1144.0, 173.0, 0.1512, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('浙江', 3329.0, 1016.0, 0.3052, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('重庆', 1000.0, 233.0, 0.2330, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('四川', 2010.0, 203.0, 0.1010, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('西藏', 79.0, 10.0, 0.1266, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('甘肃', 632.0, 181.0, 0.2864, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('宁夏', 289.0, 144.0, 0.4983, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('青海', 254.0, 61.0, 0.2402, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('广东', 4097.0, 855.0, 0.2087, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, sum_paste_code_rate, quarter_start_date, quarter_end_date, created_time) values('海南', 430.0, 138.0, 0.3209, '2020-10-01', '2020-12-31', now());

--初始化zone_target_info表中数据 （end）
