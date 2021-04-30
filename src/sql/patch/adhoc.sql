-- 设备型号数据初始化
update arrival_list_item set function_type=(select function_type from arrival_list where id=arrival_list_item.arrival_list_id);
--2020-05-19 game plan添加batch file type
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','ProbeRecommendExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);