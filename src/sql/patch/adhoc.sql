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

-- 重置sys_role表中序列为100
alter sequence "sys_role_id_seq" restart with 100 ;
--初始化zone_target_info表中数据 （start）
delete from zone_target_info where quarter_start_date = '2020-10-01' and quarter_end_date = '2020-12-31';

insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('安徽', 988.0, 482.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('北京', 2056.0, 388.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('福建', 1154.0, 666.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('广西', 735.0, 97.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('贵州', 608.0, 65.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('河北', 1592.0, 890.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('河南', 1948.0, 1112.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('黑龙江', 597.0, 247.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('湖北', 1599.0, 463.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('湖南', 1035.0, 215.0, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('吉林', 527.0, 205.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('江苏', 2393.0, 1392.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('江西', 701.0, 421.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('辽宁', 1099.0, 342.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('内蒙古', 704.0, 258.0 , '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('山东', 2869.0, 862.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('山西', 758.0, 496.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('陕西', 968.0, 480.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('上海', 1352.0, 724.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('天津', 513.0, 520.0, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('外蒙古', 13.0, 0.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('新疆', 462.0, 100.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('云南', 892.0, 164.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('浙江', 2407.0, 954.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('重庆', 747.0, 223.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('四川', 1337.0, 194.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('西藏', 58.0, 10.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('甘肃', 437.0, 157.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('宁夏', 220.0, 135.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('青海', 157.0, 57.0, '2020-10-01', '2020-12-31', now());

insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('广东', 3111.0, 822.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('海南', 296.0, 134.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('V2.0-E', 4664.0, 303.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('V2.0-N', 3170.0, 151.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('V2.0-S', 2655.0, 127.0, '2020-10-01', '2020-12-31', now());
insert into zone_target_info(province, sum_target, paste_code, quarter_start_date, quarter_end_date, created_time) values('V2.0-W', 2291.0, 119.0, '2020-10-01', '2020-12-31', now());


--初始化zone_target_info表中数据 （end）


-- 新增问答角色
ALTER TABLE "public"."sys_role" ADD CONSTRAINT "union_name" UNIQUE ("name");
INSERT INTO sys_role("name", "role_desc", "home_page") VALUES ('OperationalRole', 'QA运营管理员', '/homeHead.xhtml') ON conflict(name) do NOTHING;
INSERT INTO sys_role("name", "role_desc", "home_page") VALUES ('BigCoffeeRole', 'QA大咖团队', '/homeHead.xhtml') ON conflict(name) do NOTHING;


-- zkb
INSERT INTO msg_config ("id", "msg_name", "msg_bind_field", "msg_status", "msg_name_en") VALUES (10, '医院客户电话', 'requestorPhone', '0', 'requestor Phone');



