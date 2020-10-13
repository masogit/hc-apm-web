--2020-10-14 start
-- FE/SALSE 全量导入支持批次识别
alter table asset_service_member add column import_file_id varchar(255);
-- 微信首次关注消息初始化
insert into wechat_subscribe_message (created_by, created_date, enabled, end_time, message, keyword, url, start_time) values ('system', now(), true, '2029-12-31 00:00:00', '感谢关注GEAPM医院资产管理数字化解决方案。点击微工作台，使用APM账号密码登录，享受GE数字化服务新体验。'||chr(10)||'如想获得试用体验，请点击我要试用，填写并提交信息申请试用体验机会。'||chr(10)||'您也可以回复一下数字获得更多信息：'||chr(10)||'1.自助注册APM账号'||chr(10)||'2.如何登录APM'||chr(10)||'3. 如何开通设备连接'||chr(10)||'4. APM操作指导'||chr(10)||'5. 如何找到密码'||chr(10)||'6. 设备报修'||chr(10)||'7. GE客服热线', '我要试用', 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbf1dfc5a5d3562d0&redirect_uri=https://geapm.gechs.cn/wx/user/register/553505&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect','2020-10-01');
--2020-10-14 end