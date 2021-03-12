
create index wechat_message_history_request_source on wechat_message_history (request_source);

--修改Asset_info.name字段长度，批量资产创建使用
ALTER TABLE asset_info alter COLUMN name type character varying(128);

-- game plan添加文件上传类型
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','SparePartExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);