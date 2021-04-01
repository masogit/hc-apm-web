--2020-04-14 game plan添加batch file type
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','TubeInfoExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
--删除game plan msa_recommend表字段
ALTER TABLE msa_recommend DROP COLUMN bulb_chance;
ALTER TABLE msa_recommend DROP COLUMN tube_expected_time;
ALTER TABLE msa_recommend DROP COLUMN has_msa_recommend;
ALTER TABLE msa_recommend DROP COLUMN has_tube;