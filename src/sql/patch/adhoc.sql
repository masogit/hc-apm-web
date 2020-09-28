
--2020-09-25 start
--消息推送控制初始化
insert into message_audience_config (id, site_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637058e', 18, 1, 1, now());
insert into message_audience_config (id, site_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637058f', 18, 1, 41, now());
insert into message_audience_config (id, site_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637058g', 18, 1, 3, now());
insert into message_audience_config (id, site_id, message_type, audience_type, create_time) values ('c09623a675b14a7489dc9f768637058h', 18, 1, 43, now());
--2020-09-25 end

-- zkb 2020-09-28
INSERT INTO "public"."zkb_part_order_user_config"("id", "config_type", "email", "name", "phone", "region", "remark", "sso", "tenant_uid", "login_name", "asset_type", "sub_config_type", "order_type", "is_vacation") VALUES ('2c978c847312a5f501731337abfb00  ', 'OrderCommon', 'GEHC.ServiceCenter@ge.com', '400公用邮箱', NULL, NULL, NULL, 'r01002996', NULL, NULL, NULL, NULL, NULL, NULL);



update zkb_part_order_cat set end_date = '3000-01-01' and start_date = '1900-01-01';

update zkb_part_order_cat set end_date = '2020-09-29';
INSERT INTO "public"."zkb_part_order_cat"("id", "cat_no", "cat_model", "cat_type", "value_type", "price", "is_vaild", "end_date", "start_date") VALUES ('1_2', 'H48681MJ', 'eM6C新型电子矩阵曲面容积探头', '高值EM', 'SpecialValue', '800,000', NULL, '3000-01-01', '2020-09-30');
-- zkb 2020-09-28