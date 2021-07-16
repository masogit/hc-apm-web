--game plan����ANS����
INSERT INTO  role_msg_rel("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('ANSSpareExcel', 'gamePlanBatchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);


-- begin 2021-07-12 自助注册时， 根据所选“职务”为院长时， 自动将账号创建在医院级别 历史数据处理
update user_org uo set org_uid=t.hospital_uid,org_type=2 from
(
select uid,ua.org_uid,hospital_uid from user_account ua where ua.site_id=46 and exists(
select mail from user_self_register ufr where "position" in('dean') and (ua.login_name=ufr.phone or ua.login_name=ufr.mail))
) as t where uo.user_uid=t.uid and uo.org_uid=t.org_uid and org_type=4;

update user_account ua set site_uid=t.hospital_uid,org_uid=t.hospital_uid,org_id=t.hospital_id,org_level=t.org_leve2 from (
select ua.*,oi.org_level as org_leve2 from user_account ua,org_info oi where ua.hospital_uid=oi.uid and ua.site_id=46 and exists(
select mail from user_self_register ufr where "position" in('dean') and (ua.login_name=ufr.phone or ua.login_name=ufr.mail))
) as t where ua.id=t.id;
-- end