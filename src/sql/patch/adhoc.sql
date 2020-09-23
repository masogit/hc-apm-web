update asset_info set visible_status = 1 where site_id = 46 and (ib_status < 1 or ib_status is null);

-- 微信自动回复-关注公众号消息
ALTER TABLE "public"."wechat_subscribe_message"
  ALTER COLUMN "message" TYPE text COLLATE "pg_catalog"."default" USING "message"::text;

update wechat_subscribe_message set keyword=null,message='感谢关注GEAPM医院资产管理数字化解决方案。点击微工作台，使用APM账号密码登录，享受GE数字化服务新体验。您也可以回复一下数字获得更多信息：'||chr(10)||'1.自助注册APM账号'||chr(10)||'2.如何登录APM'||chr(10)||'3. 如何开通设备连接'||chr(10)||'4. APM操作指导'||chr(10)||'5. 如何找到密码'||chr(10)||'6. 设备报修'||chr(10)||'7. GE客服热线',url=null where enabled = true;


INSERT INTO public.sys_role (id,home_page,name,role_desc,menus,role_desc_en) VALUES (17,null,'SparePartsAdmin','备件管理员','simple-parts','Spare Parts Admin');


--400探头开单
INSERT INTO zkb_part_order_user_config("id", "config_type", "email", "name", "phone", "region", "remark", "sso", "tenant_uid", "login_name", "asset_type", "sub_config_type", "order_type") VALUES ('id20081908_2', 'OrderExtraPending', 'yu.xiao@ge.com', '肖雨', '18911860839', '', NULL, '', NULL, 'ZKB.XY', NULL, NULL, NULL);
INSERT INTO zkb_part_order_user_config("id", "config_type", "email", "name", "phone", "region", "remark", "sso", "tenant_uid", "login_name", "asset_type", "sub_config_type", "order_type") VALUES ('ty2020092355', 'OrderExtraPending', 'ying.tian@ge.com', 'zkb-4001', '18516585569', '', NULL, '', NULL, 'zkb-4001', NULL, NULL, NULL);

--400探头下单更改
update zkb_part_order_user_config set order_type = 'Probe' where login_name = 'ZKB.PHP';
update zkb_part_order_user_config set order_type = 'Probe' where login_name = 'zkb-4002';