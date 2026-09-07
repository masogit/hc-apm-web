insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 23, '8', '手术室设备工作时长', 'OperatingRoomEquipmentWorkingHours', '手术室设备工作时长', 'f', 't', 2 where not exists (select * from config_item where key = 'OperatingRoomEquipmentWorkingHours');

-- YHAAO-28825
-- Step 1: 清空所有校准证书(certificate_type=2)的有效期至(valid_date_to)
UPDATE measuring_certificate_detail SET valid_date_to = NULL,last_modified_date = NOW() WHERE certificate_type = 2 AND valid_date_to IS NOT NULL;
-- Step 2: 清空最新计量记录为校准证书的设备的"建议下次计量日期"(next_metering_date)
-- 仅处理每个设备计量日期(check_date)最大的记录且该记录为校准证书的情况

UPDATE asset_info ai SET next_metering_date = NULL,last_modified_date = NOW() WHERE ai.uid IN (SELECT latest_mcd.asset_uid FROM (SELECT DISTINCT ON (mcd.asset_uid) mcd.asset_uid,
    mcd.certificate_type
    FROM measuring_certificate_detail mcd
    WHERE mcd.asset_uid IS NOT NULL
    ORDER BY mcd.asset_uid, mcd.check_date DESC, mcd.id DESC
    ) latest_mcd
    WHERE latest_mcd.certificate_type = 2
);

ALTER TABLE user_auth_mapping ADD column tenant_uid varchar(32);


-- YHAAO-28879
delete from  public.config_item where key = 'NursingStaffScanCodeToAssetRegister';
insert into public.config_item (uid, category,default_value,  description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 1, 'repair', '护理人员扫码默认打开页面设置', 'NursingStaffScanCodeToAssetRegister', '护理人员扫码默认打开页面设置', 'f', 't', 7,'[{"key":"事件记录","value":"record"},{"key":"设备台账","value":"asset"},{"key":"报修页面","value":"repair"}]' where not exists (select * from config_item where key = 'NursingStaffScanCodeToAssetRegister');


--YHAAO-29195
ALTER TABLE public.pm_order_rule ADD CONSTRAINT pm_order_rule_unique UNIQUE (uid);
