
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MedicalConsortiumType', '医共体类别', 'Medical Consortium Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('MedicalConsortiumType','1','牵头医院','1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('MedicalConsortiumType','2','乡镇卫生院','1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('MedicalConsortiumType','3','社区卫生服务中心','1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('MedicalConsortiumType','4','社区卫生服务站','1',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentAttachmentType','ContractInvoice','购置合同/发票复印件','ContractInvoice',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentAttachmentType','MaintenanceRecord','维修记录原件及复印件','MaintenanceRecord',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentAttachmentType','InspectionReport','设备检测报告','InspectionReport',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentAttachmentType','PhysicalPhoto','实物图片','PhysicalPhoto',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentAttachmentType','other','其他','other',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentBorrowAttachmentType','DemandDocument','需求论证报告','DemandDocument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentBorrowAttachmentType','SitePowerProof','场地/电力证明材料','SitePowerProof',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentBorrowAttachmentType','OperatorQualification','操作人员资质证明','OperatorQualification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HospitalAdjustmentBorrowAttachmentType','other','其他','other',NULL,-1);

INSERT INTO sys_role( "name", "role_desc", "home_page", "created_by", "created_date", "last_modified_by", "last_modified_date", "authorities", "authorities_ch", "cards", "cards_ch", "menus", "role_desc_en") select  'HospitalAdjustmentAdmin', '医院院间调剂', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL from sys_role where not exists(select 1 from sys_role where name = 'HospitalAdjustmentAdmin') limit 1;

INSERT INTO sys_role ( "name", "role_desc", "home_page", "created_by", "created_date", "last_modified_by", "last_modified_date", "authorities", "authorities_ch", "cards", "cards_ch", "menus", "role_desc_en") select   'InstitutionAdjustmentAdmin', '集团院间调剂', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL from sys_role where not exists(select 1 from sys_role where name = 'InstitutionAdjustmentAdmin') limit 1;
-- 添加GEIB调用acct system client
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time,
                                last_access_date)
VALUES (REPLACE(uuid_generate_v4()::text, '-', ''), '2a773154623311e7aeb25254001a1c14',
        '1dee1cec8d7f440dbf26a2d1f21a8580', 'GEIB',
        '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, clock_timestamp());