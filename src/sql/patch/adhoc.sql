--game plan新增ANS导入
INSERT INTO  role_msg_rel("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ANSSpareExcel', 'gamePlanBatchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
