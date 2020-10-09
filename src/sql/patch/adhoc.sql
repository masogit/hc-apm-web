--2020-09-25 start
--消息推送控制初始化
insert into message_audience_config (id, tenant_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637158e', 18, 1, 1, now());
insert into message_audience_config (id, tenant_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637158f', 18, 1, 41, now());
insert into message_audience_config (id, tenant_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637158g', 18, 1, 3, now());
insert into message_audience_config (id, tenant_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637158h', 18, 1, 43, now());
--2020-09-25 end
--2020-10-09 start
--修改字段类型
alter table unstructured_data alter column value type text;                                                                                                      
--2020-10-09 end                                                                                                      
                                                                                                      
