-- begin 2020-11-16 自助注册时， 根据所选“职务”信息， 自动将账号创建在医院级别 历史数据处理
update user_org uo set org_uid=t.hospital_uid from
(
select uid,ua.org_uid,hospital_uid from user_account ua where ua.site_id=46 and exists(
select mail from user_self_register ufr where "position" in('deviceDirector','deviceRepairer','deviceOtherStaff','hospitalTechnician') and ua.login_name=ufr.mail)
) as t where uo.user_uid=t.uid and uo.org_uid=t.org_uid and org_type=4


update user_account ua set site_uid=t.hospital_uid,org_uid=t.hospital_uid,org_id=t.hospital_id,org_level=t.org_leve2 from (
select ua.*,oi.org_level as org_leve2 from user_account ua,org_info oi where ua.hospital_uid=oi.uid and ua.site_id=46 and exists(
select mail from user_self_register ufr where "position" in('deviceDirector','deviceRepairer','deviceOtherStaff','hospitalTechnician') and ua.login_name=ufr.mail) 
) as t where ua.id=t.id;
-- end

-- start add asset_service_member import_file_type column
DO
$do$
    BEGIN
        IF (SELECT COUNT(*) AS ct1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'asset_service_member' AND COLUMN_NAME = 'import_file_type' ) = 0   --判断字段数量是不是0
        THEN
            alter table asset_service_member add column import_file_type varchar(225);
        END IF;
    END;
$do$;
-- end
