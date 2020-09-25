-- 2020.8.18 物资系统字典更新
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','21','24小时动态监测设备','24-hour Dynamic Monitoring Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','1','24小时动态脑电设备','24-hour Dynamic EEG Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','2','24小时动态睡眠设备','24-hour Dynamic Sleep Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','3','24小时动态心电设备','24-hour Dynamic ECG Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','4','24小时动态血糖设备','24-hour Dynamic Blood Glucose Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','5','24小时动态血压设备','24-hour Ambulatory Blood Pressure Equipment',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_18','9','其他（病理设备）','Other (Pathological Equipment)',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_18','10','染色机','Dyeing Machine',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_11','16','伽马刀','Gamma Knife',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_9','23','呼吸器','Apparatus Respiratorius',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_9','24','体外循环','Extracorporeal CircuIation',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','35','快速湿转仪','Rapid Wet Translator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','36','全自动密度梯度制备系统','Automatic Density Gradient Preparation System',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','37','全自动核酸提取仪','Automatic Nucleic Acid Extractor',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','34','杜瓦罐','Dewar Can',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','35','分液器','Liquid Separator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','36','数据系统','Data System',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','37','紫外分析仪','Ultraviolet Analyzer',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','38','紫外辐照机','Ultraviolet Irradiator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','39','紫外辐照射度计','Ultraviolet Radiation Radiometer',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','40','自动旋盖机','Automatic Capping Machine',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','41','质谱仪','Mass Spectrometer',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_13','9','原子力显微镜','Atomic Force Microscope',NULL,112);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','22','附件','Appendix',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_22','1','附件','Appendix',NULL,112);


-- 2020.08.19 start 供应商系统 楼层数据
--南部
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1','1号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2','2号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3','3号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5','5号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_6','6号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7','7号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8','8号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9','9号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10','10号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_11','11号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_12','12号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,11);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz','新门诊楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,12);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_frmz','发热门诊楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,13);

--1号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_1','1层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_2','2层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_3','3层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_4','4层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_5','5层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_6','6层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_7','7层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_8','8层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_9','9层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_10','10层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_11','11层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_12','12层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,12);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_13','13层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,13);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_14','14层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,14);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_15','15层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,15);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_16','16层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,16);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_17','17层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,17);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_18','18层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,18);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_19','19层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,19);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_20','20层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,20);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_1_21','21层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,21);


--2号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_1','1层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_2','2层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_3','3层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_4','4层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_5','5层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_6','6层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_7','7层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_8','8层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_2_9','9层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);


--3号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_1','1层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_2','2层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_3','3层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_4','4层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_5','5层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_6','6层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_3_7','7层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);


--5号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_1','1层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_2','2层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_3','3层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_4','4层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_5','5层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_5_6','6层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);


--6号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_6_1','1层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_6_2','2层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_6_3','3层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);


--7号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_1','1层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_2','2层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_3','3层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_4','4层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_5','5层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_6','6层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_7','7层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_8','8层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_9','9层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_10','10层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_11','11层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_12','12层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,12);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_13','13层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,13);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_14','14层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,14);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_7_15','15层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,15);


--8号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8_1','1层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8_2','2层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8_3','3层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8_4','4层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_8_5','5层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);


--9号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9_1','1层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9_2','2层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9_3','3层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9_4','4层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_9_5','5层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);


--10号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_1','1层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_2','2层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_3','3层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_4','4层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_5','5层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_6','6层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_7','7层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_10_8','8层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);


--11号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_11_1','1层','n_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_11_2','2层','n_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


--12号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_12_1','1层','n_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_12_2','2层','n_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


--新门诊楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_1','1层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_2','2层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_3','3层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_4','4层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_5','5层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_6','6层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_7','7层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_8','8层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_9','9层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_10','10层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_xmz_11','11层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);


--发热门诊楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_frmz_1','1层','n_frmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('n_frmz_2','2层','n_frmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


--北部
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1','1号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2','2号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3','3号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5','5号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6','6号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_7','7号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_8','8号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_9','9号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_10','10号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_11','11号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12','12号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,11);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_tjzx','体检中心',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,12);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_yjls','药剂临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,13);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_jgls','技工临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,14);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_hqls','后勤临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,15);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_ls','临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,16);


--1号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_-1','-1层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_1','1层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_2','2层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_3','3层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_4','4层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_5','5层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_6','6层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_7','7层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_8','8层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_9','9层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,9);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_10','10层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,10);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_11','11层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,11);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_12','12层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,12);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_13','13层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,13);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_14','14层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,14);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_15','15层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,15);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_1_16','16层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,16);


--2号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2_-1','-1层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2_1','1层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2_2','2层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2_3','3层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_2_4','4层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);


--3号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_-1','-1层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_1','1层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_2','2层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_3','3层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_4','4层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_5','5层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_6','6层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_7','7层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_3_8','8层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);


--5号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_-1','-1层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_1','1层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_2','2层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_3','3层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_4','4层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_5','5层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_6','6层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_7','7层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_8','8层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_5_9','9层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,9);


--6号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_-1','-1层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_1','1层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_2','2层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_3','3层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_4','4层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_5','5层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_6','6层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_6_7','7层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);


--7号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_7_1','1层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_7_2','2层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_7_3','3层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


--8号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_8_1','1层','b_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


--9号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_9_-1','-1层','b_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_9_1','1层','b_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


--10号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_10_1','1层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_10_2','2层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_10_3','3层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


--11号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_11_1','1层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_11_2','2层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_11_3','3层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


--12号楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_1','1层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_2','2层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_3','3层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_4','4层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_5','5层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_12_6','6层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);


--体检中心
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_tjzx_1','1层','b_tjzx', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


--药剂临时楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_yjls_1','1层','b_yjls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_yjls_2','2层','b_yjls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);


--技工临时楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_jgls_1','1层','b_jgls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_jgls_2','2层','b_jgls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);



--后勤临时楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_hqls_1','1层','b_hqls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_hqls_2','2层','b_hqls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);


--临时楼
insert into asset_location_info("id", "name", "parent", "site_uid", "type", "remark", "index") values ('b_ls_1','1层','b_ls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);

-- 2020.08.19 end 供应商系统 楼层数据


--初始化目录
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('1                               ', 1, 'ULS VENUE', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('2                               ', 1, 'CT', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('3                               ', 1, 'WIX MAMMO', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('4                               ', 1, 'ULS Vivid', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('5                               ', 1, 'WIX SURGEY', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('6                               ', 1, 'WIX RF', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('7                               ', 1, 'ULS BMD', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('8                               ', 1, 'ULS Logiq', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('9                               ', 1, 'ULS Voluson', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('10                              ', 1, 'WIX DXR', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('11                              ', 1, 'WIX BMD', NULL);
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('12                              ', 2, 'VENUE 40', '1                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('13                              ', 2, 'HISPEED', '2                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('14                              ', 2, 'CT385', '2                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('15                              ', 2, 'ACT ACTS', '2                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('16                              ', 2, 'BRIGHTSPEED', '2                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('17                              ', 2, 'CT315 325', '2                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('18                              ', 2, 'SENO DS', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('19                              ', 2, 'SENO 2000D', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('20                              ', 2, 'CRYSTAL', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('21                              ', 2, 'SENO PRISTINA', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('22                              ', 2, 'ALPHA RT', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('23                              ', 2, 'SENO ESSENTIAL', '3                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('24                              ', 2, 'VIVID S5/S6', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('25                              ', 2, 'Vivid E80 E90 E95', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('26                              ', 2, 'Vivid E7 E9', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('27                              ', 2, 'Vivid 7', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('28                              ', 2, 'VIVID E', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('29                              ', 2, 'Vivid I/Q', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('30                              ', 2, 'Vivid S60 S70', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('31                              ', 2, 'VIVIDT 8', '4                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('32                              ', 2, 'OEC ONE', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('33                              ', 2, 'OEC9800', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('34                              ', 2, 'OEC7900', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('35                              ', 2, 'OEC7500', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('36                              ', 2, 'OEC ONE CFD', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('37                              ', 2, 'OEC850', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('38                              ', 2, 'OEC715 785 865', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('39                              ', 2, 'OEC ONE II', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('40                              ', 2, 'OEC7700', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('41                              ', 2, 'OEC MINIVIEW', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('42                              ', 2, 'OEC9900', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('43                              ', 2, 'OEC ELITE', '5                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('44                              ', 2, 'RXI', '6                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('45                              ', 2, 'PS800', '6                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('46                              ', 2, 'R500', '6                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('47                              ', 2, 'PT800', '6                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('48                              ', 2, 'ACHILLES EXPII', '7                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('49                              ', 2, 'ACHILLES EXP', '7                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('50                              ', 2, 'Logiq F', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('51                              ', 2, 'Logiq C', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('52                              ', 2, 'LOGIQ S6', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('53                              ', 2, 'Logiq P6', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('54                              ', 2, 'Logiq V1 V2', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('55                              ', 2, 'Logiq E', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('56                              ', 2, 'Logiq P5', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('57                              ', 2, 'Logiq P7 P9', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('58                              ', 2, 'Venue50', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('59                              ', 2, 'Logiq 7', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('60                              ', 2, 'Logiq S8', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('61                              ', 2, 'Logiq 5', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('62                              ', 2, 'Logiq E8', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('63                              ', 2, 'LOGIQ 3', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('64                              ', 2, 'Logiq 9', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('65                              ', 2, 'LOGIQ BOOK XP', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('66                              ', 2, 'LOGIQ P3', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('67                              ', 2, 'Logiq S7', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('68                              ', 2, 'LOGIQ V3/V5', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('69                              ', 2, 'Logiq E9', '8                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('70                              ', 2, 'Voluson 730', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('71                              ', 2, 'VOLUSON I/E', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('72                              ', 2, 'Voluson S6/S8', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('73                              ', 2, 'Voluson E6 E8 E10', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('74                              ', 2, 'VOLUSON S10', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('75                              ', 2, 'Voluson P6 P8', '9                               ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('76                              ', 2, 'XR575', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('77                              ', 2, 'XR6000', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('78                              ', 2, 'DR-F', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('79                              ', 2, 'D6000-3', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('80                              ', 2, 'XRD-2X', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('81                              ', 2, 'D6000-2', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('82                              ', 2, 'XR656G3', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('83                              ', 2, 'XR650', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('84                              ', 2, 'D8000', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('85                              ', 2, 'XR316', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('86                              ', 2, 'XR118', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('87                              ', 2, 'XRD', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('88                              ', 2, 'AMX4PLUS', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('89                              ', 2, 'XR648', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('90                              ', 2, 'XR642', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('91                              ', 2, 'XR646HD', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('92                              ', 2, 'XR646', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('93                              ', 2, 'XR220', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('94                              ', 2, 'XR656', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('95                              ', 2, 'XR515', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('96                              ', 2, 'XR240', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('97                              ', 2, 'XR656G2', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('98                              ', 2, 'D6000-1', '10                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('99                              ', 2, 'PRODIGY', '11                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('100                             ', 2, 'IDXA', '11                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('101                             ', 2, 'DPX DUO BRAVO', '11                              ');
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('102                             ', 2, 'DPX NT', '11                              ');
-- 2020.09.02 start 智康宝角色
INSERT INTO sys_role ("home_page", "name", "role_desc", "created_by", "created_date", "last_modified_by", "last_modified_date", "authorities", "authorities_ch", "cards", "cards_ch", "menus", "role_desc_en") VALUES ('/homeHead.xhtml', 'zkbDealerSales', '代理商Sales', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'asset,assetAdjust.batch,assetAdjust.borrowAccept,assetAdjust.config,assetAdjust.cost,assetAdjust.nameConfig,assetAdjust.returnAccept,assetAdjust.stock,asset.batchImportData,asset.create,asset.info,asset.inventory,asset.mgt,asset.scrap,asset.scrapTempMgt,asset.supplierMgt,examineMgt,examineMgt.pcc,examineMgt.pccConfig,examineMgt.ssoImp,install.list,install.store,knowledge.mgt,knowledge.zkbMgt,knowledge.zkbView,link,link,link.CPM,maintain,maintain.assetMgt,maintain.electricDataImport,maintain.reportFormMgt,maintain.reportMgt,maintain.rule,maintain.workbench,measure.categoryConfig,measure.console,measure.info,measure.rule,measure.ruleMgt,measure.templateMgt,nurseSite,nurseSite.config,nurseSite.maintain,nurseSite.optRecord,nurseSite.report,performance.BI,performance.bigScreen,performance.config,performance.deptMgt,performance.import,performance.scan,repair,repair,repair.count,repair.report,repair.reportMgt,repair.vacationMgt,repair.workbench,repair.workSchedule,zkbBasicInfo,zkbBasicInfo.agentContactPersonMgt,zkbBasicInfo.engineerMgt,zkbContract,zkbContract,zkbContract.contractMgt,zkbContract.courseMgt', 'Local Background Operation Management');
INSERT INTO sys_role ("home_page", "name", "role_desc", "created_by", "created_date", "last_modified_by", "last_modified_date", "authorities", "authorities_ch", "cards", "cards_ch", "menus", "role_desc_en") VALUES ('/homeHead.xhtml', 'zkbDealerFE', '代理商FE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'asset,assetAdjust.batch,assetAdjust.borrowAccept,assetAdjust.config,assetAdjust.cost,assetAdjust.nameConfig,assetAdjust.returnAccept,assetAdjust.stock,asset.batchImportData,asset.create,asset.info,asset.inventory,asset.mgt,asset.scrap,asset.scrapTempMgt,asset.supplierMgt,examineMgt,examineMgt.pcc,examineMgt.pccConfig,examineMgt.ssoImp,install.list,install.store,knowledge.mgt,knowledge.zkbMgt,knowledge.zkbView,link,link,link.CPM,maintain,maintain.assetMgt,maintain.electricDataImport,maintain.reportFormMgt,maintain.reportMgt,maintain.rule,maintain.workbench,measure.categoryConfig,measure.console,measure.info,measure.rule,measure.ruleMgt,measure.templateMgt,nurseSite,nurseSite.config,nurseSite.maintain,nurseSite.optRecord,nurseSite.report,performance.BI,performance.bigScreen,performance.config,performance.deptMgt,performance.import,performance.scan,repair,repair,repair.count,repair.report,repair.reportMgt,repair.vacationMgt,repair.workbench,repair.workSchedule,zkbBasicInfo,zkbBasicInfo.agentContactPersonMgt,zkbBasicInfo.engineerMgt,zkbContract,zkbContract,zkbContract.contractMgt,zkbContract.courseMgt', 'Local Background Operation Management');

-- 2020.9.7 物资系统字典更新[资金来源]
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1001','教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1002','科研','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1003','科研+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1004','财政','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1005','财政+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1006','财政+科研','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1007','财政+科研+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1008','自筹','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1009','自筹+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1010','自筹+科研','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1011','自筹+科研+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1012','自筹+财政','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1013','自筹+财政+教育','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1014','自筹+财政+科研','',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1015','自筹+财政+科研+教育','',NULL,112);
--  之前8.19的[资产分类] 未运行
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','21','24小时动态监测设备','24-hour Dynamic Monitoring Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','1','24小时动态脑电设备','24-hour Dynamic EEG Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','2','24小时动态睡眠设备','24-hour Dynamic Sleep Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','3','24小时动态心电设备','24-hour Dynamic ECG Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','4','24小时动态血糖设备','24-hour Dynamic Blood Glucose Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_21','5','24小时动态血压设备','24-hour Ambulatory Blood Pressure Equipment',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_18','9','其他（病理设备）','Other (Pathological Equipment)',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_18','10','染色机','Dyeing Machine',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_11','16','伽马刀','Gamma Knife',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_9','23','呼吸器','Apparatus Respiratorius',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_9','24','体外循环','Extracorporeal CircuIation',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','35','快速湿转仪','Rapid Wet Translator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','36','全自动密度梯度制备系统','Automatic Density Gradient Preparation System',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_14','37','全自动核酸提取仪','Automatic Nucleic Acid Extractor',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','34','杜瓦罐','Dewar Can',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','35','分液器','Liquid Separator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','36','数据系统','Data System',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','37','紫外分析仪','Ultraviolet Analyzer',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','38','紫外辐照机','Ultraviolet Irradiator',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','39','紫外辐照射度计','Ultraviolet Radiation Radiometer',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','40','自动旋盖机','Automatic Capping Machine',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_8','41','质谱仪','Mass Spectrometer',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_13','9','原子力显微镜','Atomic Force Microscope',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','22','附件','Appendix',NULL,112);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroupSecondary_22','1','附件','Appendix',NULL,112);


-- 2020.09.08 YHAAO-8336 质控评审

-- Drop table
DROP TABLE IF EXISTS public.audit_config;
DROP TABLE IF EXISTS public.audit_file;
DROP TABLE IF EXISTS public.audit_group;
DROP TABLE IF EXISTS public.audit_item;
DROP TABLE IF EXISTS public.audit_link;
DROP TABLE IF EXISTS public.audit_module;
DROP TABLE IF EXISTS public.audit_summary;



DROP TABLE public.audit_default_file;

CREATE TABLE public.audit_default_file (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	deleted bool NULL,
	expired bool NULL,
	file_id varchar(255) NULL,
	file_type varchar(255) NULL,
	id int4 NULL,
	item_id int4 NULL,
	"name" varchar(255) NULL,
	system_file bool NULL,
	"template" bool NULL,
	upload_by varchar(255) NULL,
	upload_time timestamp NULL,
	url varchar(255) NULL,
	CONSTRAINT audit_default_file_pkey PRIMARY KEY (pid)
);

DROP TABLE public.audit_default_group;

CREATE TABLE public.audit_default_group (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	"comments" text NULL,
	completed bool NULL,
	expired bool NULL,
	highlights text NULL,
	id int4 NULL,
	interpretation text NULL,
	module_id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	original text NULL,
	"version" varchar(255) NULL,
	CONSTRAINT audit_default_group_pkey PRIMARY KEY (pid)
);

DROP TABLE public.audit_default_item;

CREATE TABLE public.audit_default_item (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	completed bool NULL,
	deleted bool NULL,
	group_id int4 NULL,
	id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	"owner" varchar(255) NULL,
	system_item bool NULL,
	CONSTRAINT audit_default_item_pkey PRIMARY KEY (pid)
);

DROP TABLE public.audit_default_link;

CREATE TABLE public.audit_default_link (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	group_id int4 NULL,
	id int4 NULL,
	link varchar(255) NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	CONSTRAINT audit_default_link_pkey PRIMARY KEY (pid)
);

DROP TABLE public.audit_default_module;

CREATE TABLE public.audit_default_module (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	completed bool NULL,
	id int4 NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	summary_id int4 NULL,
	CONSTRAINT audit_default_module_pkey PRIMARY KEY (pid)
);

DROP TABLE public.audit_default_summary;

CREATE TABLE public.audit_default_summary (
	pid serial NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	hospital_uid bpchar(32) NULL,
	institution_uid bpchar(32) NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	site_uid bpchar(32) NULL,
	tenant_uid bpchar(32) NULL,
	config_id int4 NULL,
	except_progress numeric(19,2) NULL,
	id int4 NULL,
	"level" varchar(255) NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	progress numeric(19,2) NULL,
	CONSTRAINT audit_default_summary_pkey PRIMARY KEY (pid)
);



-- INSERT DATA
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 90.00, 'C', '制度管理', 1, 0.00);
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 60.00, 'B', '执行管理', 2, 0.00);
INSERT INTO public.audit_default_summary
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, config_id, except_progress, level, name, order_num, progress)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', 0, 30.00, 'A', '改进管理', 3, 0.00);



INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.1医学装备部门管理', 1, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.2医用设备管理', 2, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.3设备安全管理', 3, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.4培训和考核管理', 4, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.5医学装备使用保障', 5, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.6医用耗材管理', 6, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.7医学装备协作管理', 7, 1);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.1医学装备部门管理', 1, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.2医用设备管理', 2, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.3设备安全管理', 3, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.4培训和考核管理', 4, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.5医学装备使用保障', 5, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.6医用耗材管理', 6, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.7医学装备协作管理', 7, 2);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.1医学装备部门管理', 1, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.2医用设备管理', 2, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.3设备安全管理', 3, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.4培训和考核管理', 4, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.5医学装备使用保障', 5, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.6医用耗材管理', 6, 3);
INSERT INTO public.audit_default_module
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, name, order_num, summary_id)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, '6.10.7医学装备协作管理', 7, 3);



INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '医学装备管理总体系', 1, '6.10.1.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '管理人员配备', 2, '6.10.1.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '工作流程和岗位职责', 3, '6.10.1.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '常规与大型医学装备配置方案', 4, '6.10.1.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '医学装备生命周期管理制度', 5, '6.10.1.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 1, '使用科室人员配备', 6, '6.10.1.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '购置论证相关制度与决策程序', 1, '6.10.2.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备清单', 2, '6.10.2.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备档案管理', 3, '6.10.2.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 2, '医学装备使用评价制度', 4, '6.10.2.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '安全风险管控制度', 1, '6.10.3.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量管理制度', 2, '6.10.3.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '临床使用安全管控制度', 3, '6.10.3.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '相关人员培训制度', 4, '6.10.3.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射与放疗安全', 5, '6.10.3.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '机房警示标识', 6, '6.10.3.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '设备安全人员须知', 7, '6.10.3.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '特殊和放射装备制度', 8, '6.10.3.3.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '特殊和放射装备人员管理', 9, '6.10.3.3.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射诊断设备制度', 10, '6.10.3.4.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射使用管理制度', 11, '6.10.3.4.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(22, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '放射类安全指标', 12, '6.10.3.4.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(23, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备管理制度', 13, '6.10.3.5.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(24, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备日常管理', 14, '6.10.3.5.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(25, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量检测合格标志', 15, '6.10.3.5.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(26, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 3, '计量设备相关人员须知', 16, '6.10.3.5.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(27, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '设备使用人员培训制度', 1, '6.10.4.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(28, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '技术支持制度', 2, '6.10.4.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(29, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '意外事件处理培训制度', 3, '6.10.4.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(30, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '操作手册管理制度', 4, '6.10.4.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(31, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 4, '设备操作考核制度', 5, '6.10.4.1.C.5');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(32, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备使用保障制度', 1, '6.10.5.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(33, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备养计检制度', 2, '6.10.5.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(34, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '故障维修分析', 3, '6.10.5.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(35, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医学装备应急预案', 4, '6.10.5.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(36, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '急救支持类装备待机要求', 5, '6.10.5.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(37, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '设备故障紧急预案程序', 6, '6.10.5.3.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(38, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '急救支持类装备急调配程序', 7, '6.10.5.3.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(39, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 5, '医务人员须知', 8, '6.10.5.3.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(40, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '医用耗材管理制度', 1, '6.10.6.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(41, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '医用耗材采购管理', 2, '6.10.6.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(42, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '高值耗材使用管理', 3, '6.10.6.1.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(43, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 6, '医用耗材不良事件管理', 4, '6.10.6.1.C.4');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(44, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '管理部门学习制度', 1, '6.10.7.1.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(45, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '管理部门定期总结分析制度', 2, '6.10.7.1.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(46, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '使用部门专人管理制度', 3, '6.10.7.2.C.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(47, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '使用部门日常管理工作', 4, '6.10.7.2.C.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(48, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 7, '使用人员培训制度', 5, '6.10.7.2.C.3');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(49, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 8, '制度实行自查', 1, '6.10.1.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(50, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 8, '主管部门监察', 2, '6.10.1.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(51, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '制度实行自查', 1, '6.10.2.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(52, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 9, '主管部门监察', 2, '6.10.2.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(53, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '安全自查和记录', 1, '6.10.3.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(54, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门监察', 2, '6.10.3.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(55, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '机房安全自查', 3, '6.10.3.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(56, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门监察', 4, '6.10.3.2.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(57, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '特殊和放射装备自查', 5, '6.10.3.3.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(58, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门监察', 6, '6.10.3.3.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(59, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '放射使用科室自查', 7, '6.10.3.4.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(60, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门对放射部门监察', 8, '6.10.3.4.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(61, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '计量设备自查', 9, '6.10.3.5.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(62, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 10, '主管部门对计量设备的监察', 10, '6.10.3.5.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(63, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '使用科室操作自查', 1, '6.10.4.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(64, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 11, '主管部门对使用操作的监察', 2, '6.10.4.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(65, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '故障维修分析', 1, '6.10.5.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(66, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '故障分析改进措施', 2, '6.10.5.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(67, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '急救支持类装备实时监管', 3, '6.10.5.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(68, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 12, '急救支持类装备紧急程序演练', 4, '6.10.5.3.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(69, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 13, '高值耗材的使用自查', 1, '6.10.6.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(70, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 13, '主管部门对高值耗材的监察主管部门对高值耗材的监察', 2, '6.10.6.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(71, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 14, '管理部门定期自查', 1, '6.10.7.1.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(72, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 14, '管理部门定期巡检使用部门', 2, '6.10.7.1.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(73, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 14, '使用部门使用自查', 3, '6.10.7.2.B.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(74, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 14, '使用部门定期分析反馈', 4, '6.10.7.2.B.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(75, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '医学装备发展规划落实', 1, '6.10.1.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(76, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 15, '医学装备配置方案和管理落实', 2, '6.10.1.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(77, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 16, '设备管理工作持续改进', 1, '6.10.2.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(78, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '安全管控持续改进', 1, '6.10.3.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(79, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '机房安全持续改进', 2, '6.10.3.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(80, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '特殊医学装备安全措施改进', 3, '6.10.3.3.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(81, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '放射诊疗设备安全措施改进', 4, '6.10.3.4.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(82, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '计量设备使用持续改进', 5, '6.10.3.5.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(83, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 17, '计量器具100%合格', 6, '6.10.3.5.A.2');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(84, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 18, '操作和维护规范持续改进', 1, '6.10.4.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(85, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 19, '设备使用保障措施持续改进', 1, '6.10.5.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(86, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 19, '急救支持类装备完好率100%', 2, '6.10.5.2.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(87, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 19, '急救支持类装备使用保障持续改进', 3, '6.10.5.3.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(88, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 20, '医用耗材管理的持续改进', 1, '6.10.6.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(89, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 21, '医学装备管理持续改进', 1, '6.10.7.1.A.1');
INSERT INTO public.audit_default_group
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, comments, completed, expired, module_id, name, order_num, version)
VALUES(90, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', '', false, false, 21, '全院医学装备使用管理持续改进', 2, '6.10.7.2.A.1');



UPDATE public.audit_default_group SET original = '6.10.1.1.C.1建立医学装备三级管理体系，成立院级领导、医学装备管理部门、相关部门人员和专家组成的医学装备委员会，负责对医院医学装备发展规划、年度装备计划、采购活动等重大事项进行评估、论证和咨询，有记录。',
highlights = '根据医院整体规划全面落实医疗设备设备的全生命周期科学管理，并对制度流程进行多次优化与修改。'
WHERE id = 1;
UPDATE public.audit_default_group SET original = '6.10.1.1.C.2医学装备管理部门根据医院功能和任务需求，配备专人管理和专业技术人员。',
highlights = '根据医院整体规划全面落实医疗设备设备的全生命周期科学管理，并对制度流程进行多次优化与修改。'
WHERE id = 2;
UPDATE public.audit_default_group SET original = '6.10.1.1.C.3遵循“统一领导、归口管理、分级负责、责权一致”原则，制定医院医学装备管理制度、工作流程和岗位职责，并执行。',
highlights = '根据医院整体规划全面落实医疗设备设备的全生命周期科学管理，并对制度流程进行多次优化与修改。'
WHERE id = 3;
UPDATE public.audit_default_group SET original = '6.10.1.2.C.1根据国家法律、法规及卫生行政部门规章、管理办法、标准要求，制定常规与大型医学装备配置方案。',
highlights = null
WHERE id = 4;
UPDATE public.audit_default_group SET original = '6.10.1.2.C.2有医学装备论证、决策、购置、验收、使用、保养、维修、应用分析和更新、处置等相关制度与工作流程，并执行。',
highlights = null
WHERE id = 5;
UPDATE public.audit_default_group SET original = '6.10.1.2.C.3医学装备使用部门设专职或兼职管理人员。医学装备使用人员有培训和考核，合格后方可上岗操作。',
highlights = null
WHERE id = 6;
UPDATE public.audit_default_group SET original = '6.10.2.1.C.1有医学装备购置论证相关制度与决策程序，单价在50万元及以上的医学装备有可行性论证。',
highlights = null
WHERE id = 7;
UPDATE public.audit_default_group SET original = '6.10.2.1.C.2有根据全国卫生系统医疗器械仪器设备分类与代码，建立医学装备分类、分户电子账目，实行信息化管理。',
highlights = null
WHERE id = 8;
UPDATE public.audit_default_group SET original = '6.10.2.1.C.3医学装备档案管理有制度与档案资料，落实档案、账目、账物相符、准确等管理规定。单价在5万元及以上的医学装备按照集中统一管理的原则。',
highlights = null
WHERE id = 9;
UPDATE public.audit_default_group SET original = '6.10.2.1.C.4有医学装备使用评价相关制度。有大型医用设备使用、功能开发、社会效益、成本效益等分析评价。',
highlights = null
WHERE id = 10;
UPDATE public.audit_default_group SET original = '6.10.3.1.C.1有医学装备临床使用安全控制与风险管理、医疗器械临床使用安全监测和安全事件报告相关制度与流程，并落实。',
highlights = null
WHERE id = 11;
UPDATE public.audit_default_group SET original = '6.10.3.1.C.2有医学装备质量保障要求，如：操作者自我检查，医学装备计（剂）量准确、安全防护、性能指标符合要求。对暂停或终止使用的高风险器械有记录。',
highlights = null
WHERE id = 12;
UPDATE public.audit_default_group SET original = '6.10.3.1.C.3有生命支持类、急救类、植入类、辐射类、灭菌类和大型医用设备等医学装备临床使用安全监测与报告制度。',
highlights = null
WHERE id = 13;
UPDATE public.audit_default_group SET original = '6.10.3.1.C.4临床及医技科室使用部门与医学装备管理部门人员知晓上述要求。',
highlights = null
WHERE id = 14;
UPDATE public.audit_default_group SET original = '6.10.3.2.C.1放射与放疗等医学装备的机房设计、建设、防护装修和设施符合安全、环保等有关要求。',
highlights = null
WHERE id = 15;
UPDATE public.audit_default_group SET original = '6.10.3.2.C.2机房显著位置有规范的警示标识。',
highlights = null
WHERE id = 16;
UPDATE public.audit_default_group SET original = '6.10.3.2.C.3医学装备管理部门与机房的工作人员知晓防护有关要求和措施。',
highlights = null
WHERE id = 17;
UPDATE public.audit_default_group SET original = '6.10.3.3.C.1高压容器等特种设备和放射装置等特殊医学装备具有生产、安装合格证明以及根据规定必备的许可证明。',
highlights = null
WHERE id = 18;
UPDATE public.audit_default_group SET original = '6.10.3.3.C.2特种设备和特殊医学装备操作人员经过培训，具有上岗资格。',
highlights = null
WHERE id = 19;
UPDATE public.audit_default_group SET original = '6.10.3.4.C.1有放射和诊断影像设备的管理制度，并落实。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 20;
UPDATE public.audit_default_group SET original = '6.10.3.4.C.2定期检查、维护、校准和监测，有记录。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 21;
UPDATE public.audit_default_group SET original = '6.10.3.4.C.3技术指标和安全、防护性能符合有关标准与要求，有行政监管部门检测报告。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 22;
UPDATE public.audit_default_group SET original = '6.10.3.5.C.1有计量设备监测管理的相关制度，并落实。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 23;
UPDATE public.audit_default_group SET original = '6.10.3.5.C.2有计量设备清单、定期检测记录和维修记录等相关资料。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 24;
UPDATE public.audit_default_group SET original = '6.10.3.5.C.3经检测的计量器具有计量检测合格标志，标志显示检测时间与登记记录一致。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 25;
UPDATE public.audit_default_group SET original = '6.10.3.5.C.4相关人员知晓上述内容。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 26;
UPDATE public.audit_default_group SET original = '6.10.4.1.C.1有保障常用仪器、设备和抢救物品使用者培训的制度、培训计划及具体课程安排。',
highlights = null
WHERE id = 27;
UPDATE public.audit_default_group SET original = '6.10.4.1.C.2医疗装备部门为临床使用的医疗器械提供技术支持、业务指导、安全保障与咨询服务。',
highlights = null
WHERE id = 28;
UPDATE public.audit_default_group SET original = '6.10.4.1.C.3培训内容涵盖使用中可能出现的意外情况处理预案及措施。',
highlights = null
WHERE id = 29;
UPDATE public.audit_default_group SET original = '6.10.4.1.C.4有医疗设备操作手册并随设备存放，供方便查阅。',
highlights = null
WHERE id = 30;
UPDATE public.audit_default_group SET original = '6.10.4.1.C.5对设备操作人员的培训情况有考核，并有记录。',
highlights = null
WHERE id = 31;
UPDATE public.audit_default_group SET original = '6.10.5.1.C.1有保障医学装备管理相关制度和规范。有全院装备配置和使用情况清单。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 32;
UPDATE public.audit_default_group SET original = '6.10.5.1.C.2医学装备管理部门对医学装备保养、维修、校验等实施统一管理，并指导操作人员履行日常保养和维护。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 33;
UPDATE public.audit_default_group SET original = '6.10.5.1.C.3有使用部门和医学装备管理部门对医学装备保障情况的登记资料。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 34;
UPDATE public.audit_default_group SET original = '6.10.5.2.C.1有急救类、生命支持类医学装备应急预案，保障紧急救援工作需要。',
highlights = null
WHERE id = 35;
UPDATE public.audit_default_group SET original = '6.10.5.2.C.2各科室急救类、生命支持类装备时刻保持待用状态。',
highlights = null
WHERE id = 36;
UPDATE public.audit_default_group SET original = '6.10.5.3.C.1建立的应急管理程序，装备故障时有紧急替代流程。',
highlights = '调配中心应急设备配套齐全，工程师24小时电话待命快速响应。'
WHERE id = 37;
UPDATE public.audit_default_group SET original = '6.10.5.3.C.2优先保障急救类、生命支持类装备的应急调配。',
highlights = '调配中心应急设备配套齐全，工程师24小时电话待命快速响应。'
WHERE id = 38;
UPDATE public.audit_default_group SET original = '6.10.5.3.C.3医务人员知晓医疗装备应急管理与替代程序。',
highlights = '调配中心应急设备配套齐全，工程师24小时电话待命快速响应。'
WHERE id = 39;
UPDATE public.audit_default_group SET original = '6.10.6.1.C.1依据国家相关规定制订高值医用耗材、一次性使用的无菌器械及其他医用耗材管理制度及实施细则。',
highlights = null
WHERE id = 40;
UPDATE public.audit_default_group SET original = '6.10.6.1.C.2对高值医用耗材、一次性使用的无菌器械和其他医用耗材的采购记录有管理，可追溯到每批产品的进货来源。',
highlights = null
WHERE id = 41;
UPDATE public.audit_default_group SET original = '6.10.6.1.C.3有高值医用耗材使用程序与记录。',
highlights = null
WHERE id = 42;
UPDATE public.audit_default_group SET original = '6.10.6.1.C.4有医用耗材使用不良事件报告与监测管理流程，并执行。',
highlights = null
WHERE id = 43;
UPDATE public.audit_default_group SET original = '6.10.7.1.C.1部门管理人员接受医学装备管理基本技能培训，有质量管理基本知识。',
highlights = '根据临床需求，不断调整服务方式，通过滚动培训，上机考核等手段提高设备规范使用提高设备使用率，做好预防、应急维修提高设备完好率。'
WHERE id = 44;
UPDATE public.audit_default_group SET original = '6.10.7.1.C.2定期分析全院医学装备发展规划、采购计划的完成、管理和使用情况；分析医学装备维修和报废等管理过程中存在问题，并有记录或总结报告。',
highlights = '根据临床需求，不断调整服务方式，通过滚动培训，上机考核等手段提高设备规范使用提高设备使用率，做好预防、应急维修提高设备完好率。'
WHERE id = 45;
UPDATE public.audit_default_group SET original = '6.10.7.2.C.1医学装备使用科室（或部门）执行安装、验收、使用、存放、维修，保养、报废等相关管理制度，并设专职或兼职人员管理科室医学装备。',
highlights = null
WHERE id = 46;
UPDATE public.audit_default_group SET original = '6.10.7.2.C.2有医学装备日常管理工作和意外应急管理等相关制度，并执行。',
highlights = null
WHERE id = 47;
UPDATE public.audit_default_group SET original = '6.10.7.2.C.3医学装备相关使用人员接受基本技能培训，合格后方可上岗。',
highlights = null
WHERE id = 48;
UPDATE public.audit_default_group SET original = '6.10.1.1.B.1主管部门对医学装备委员会工作计划进展和人员管理有检查、分析、反馈。',
highlights = '根据医院整体规划全面落实医疗设备设备的全生命周期科学管理，并对制度流程进行多次优化与修改。'
WHERE id = 49;
UPDATE public.audit_default_group SET original = '6.10.1.2.B.1主管部门有检查、分析、反馈。',
highlights = null
WHERE id = 50;
UPDATE public.audit_default_group SET original = '6.10.2.1.B.1医学装备管理部门对制度建设、档案资料、账目、设备管理和使用情况等有自查。',
highlights = null
WHERE id = 51;
UPDATE public.audit_default_group SET original = '6.10.2.1.B.2主管部门对医学装备配置、审计结果、分析报告有监管，提出改进建议，及时报送医学装备委员会。',
highlights = null
WHERE id = 52;
UPDATE public.audit_default_group SET original = '6.10.3.1.B.1使用部门和医学装备管理部门对医学装备、医疗器械安全管理有自查，有风险程度分析和使用情况的分析，有记录。',
highlights = null
WHERE id = 53;
UPDATE public.audit_default_group SET original = '6.10.3.1.B.2主管部门有监管、分析、反馈。',
highlights = null
WHERE id = 54;
UPDATE public.audit_default_group SET original = '6.10.3.2.B.1医学装备管理部门对机房环境有自查和监测，并有记录。',
highlights = null
WHERE id = 55;
UPDATE public.audit_default_group SET original = '6.10.3.2.B.2主管部门有检查与监管。',
highlights = null
WHERE id = 56;
UPDATE public.audit_default_group SET original = '6.10.3.3.B.1医学装备管理部门对特殊装备定期自查和监测，有记录。',
highlights = null
WHERE id = 57;
UPDATE public.audit_default_group SET original = '6.10.3.3.B.2主管部门有检查、分析、反馈。',
highlights = null
WHERE id = 58;
UPDATE public.audit_default_group SET original = '6.10.3.4.B.1使用科室对放射诊疗设备使用情况有自查。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 59;
UPDATE public.audit_default_group SET original = '6.10.3.4.B.2主管部门对放射和诊断性影像等设备定期检查、维护和校准有监管。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 60;
UPDATE public.audit_default_group SET original = '6.10.3.5.B.1使用科室对计量设备使用情况有自查，有记录。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 61;
UPDATE public.audit_default_group SET original = '6.10.3.5.B.2医学装备管理部门对计量设备定期自查和监测，有记录。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 62;
UPDATE public.audit_default_group SET original = '6.10.4.1.B.1科室对培训情况和效果有自查。',
highlights = null
WHERE id = 63;
UPDATE public.audit_default_group SET original = '6.10.4.1.B.2主管部门有检查、分析、反馈。',
highlights = null
WHERE id = 64;
UPDATE public.audit_default_group SET original = '6.10.5.1.B.1医学装备部门对医学装备故障维修情况有分析报告。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 65;
UPDATE public.audit_default_group SET original = '6.10.5.1.B.2主管部门根据医学装备使用监管分析结果提出整改措施。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 66;
UPDATE public.audit_default_group SET original = '6.10.5.2.B.1主管部门对急救类、生命支持类装备完好情况和使用情况进行实时监管。',
highlights = null
WHERE id = 67;
UPDATE public.audit_default_group SET original = '6.10.5.3.B.1主管部门对医学装备应急调配有演练和监管。',
highlights = '调配中心应急设备配套齐全，工程师24小时电话待命快速响应。'
WHERE id = 68;
UPDATE public.audit_default_group SET original = '6.10.6.1.B.1科室对高值医用耗材使用情况有自查。',
highlights = null
WHERE id = 69;
UPDATE public.audit_default_group SET original = '6.10.6.1.B.2主管和监管部门对高值医用耗材管理的全过程有检查、评估、分析、反馈。',
highlights = null
WHERE id = 70;
UPDATE public.audit_default_group SET original = '6.10.7.1.B.1医学装备部门有检查、分析、反馈。',
highlights = '根据临床需求，不断调整服务方式，通过滚动培训，上机考核等手段提高设备规范使用提高设备使用率，做好预防、应急维修提高设备完好率。'
WHERE id = 71;
UPDATE public.audit_default_group SET original = '6.10.7.1.B.2医学装备部门定期到科室巡视检查医学装备，并有分析、评估和反馈记录。',
highlights = '根据临床需求，不断调整服务方式，通过滚动培训，上机考核等手段提高设备规范使用提高设备使用率，做好预防、应急维修提高设备完好率。'
WHERE id = 72;
UPDATE public.audit_default_group SET original = '6.10.7.2.B.1科室有对维护保养和管理情况有自查、有记录。',
highlights = null
WHERE id = 73;
UPDATE public.audit_default_group SET original = '6.10.7.2.B.2主管部门对质量与安全管理情况有检查、分析、反馈。',
highlights = null
WHERE id = 74;
UPDATE public.audit_default_group SET original = '6.10.1.1.A.1持续改进有成效，医学装备发展规划落实到位。',
highlights = '根据医院整体规划全面落实医疗设备设备的全生命周期科学管理，并对制度流程进行多次优化与修改。'
WHERE id = 75;
UPDATE public.audit_default_group SET original = '6.10.1.2.A.1持续改进有成效，医学装备配置方案和管理要求得到有效落实。',
highlights = null
WHERE id = 76;
UPDATE public.audit_default_group SET original = '6.10.2.1.A.1持续改进有成效，医学装备管理工作得到落实。',
highlights = null
WHERE id = 77;
UPDATE public.audit_default_group SET original = '6.10.3.1.A.1持续改进有成效，医学装备安全管理措施到位，保障临床工作安全使用。',
highlights = null
WHERE id = 78;
UPDATE public.audit_default_group SET original = '6.10.3.2.A.1持续改进有成效，机房安全管理措施落实到位。',
highlights = null
WHERE id = 79;
UPDATE public.audit_default_group SET original = '6.10.3.3.A.1持续改进有成效，特种设备和特殊医学装备安全措施得到有效落实。',
highlights = null
WHERE id = 80;
UPDATE public.audit_default_group SET original = '6.10.3.4.A.1持续改进有成效，所有放射诊疗设备监测均符合要求。',
highlights = '通过日常巡检发现并解决问题，保障放射设备使用符合规范。'
WHERE id = 81;
UPDATE public.audit_default_group SET original = '6.10.3.5.A.1持续改进有成效，计量设备使用均符合规范。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 82;
UPDATE public.audit_default_group SET original = '6.10.3.5.A.2医院使用的计量器具100%有计量检测合格标志，100%在有效期内。',
highlights = '对需计量的设备器具做到台账相符、无差错、无遗漏、无过期。'
WHERE id = 83;
UPDATE public.audit_default_group SET original = '6.10.4.1.A.1持续改进有成效，全院仪器设备使用人员操作和维护规范。',
highlights = null
WHERE id = 84;
UPDATE public.audit_default_group SET original = '6.10.5.1.A.1持续改进有成效，全院医学装备全程管理和使用规范，满足临床使用需求。',
highlights = '通过运用统计学的方法对大型设备故障情况进行科学分析，有效提高了设备完好率和使用率。'
WHERE id = 85;
UPDATE public.audit_default_group SET original = '6.10.5.2.A.1持续改进有成效，急救类、生命支持类装备完好率100%。',
highlights = null
WHERE id = 86;
UPDATE public.audit_default_group SET original = '6.10.5.3.A.1持续改进有成效，全院医学装备应急调配有保障。',
highlights = '调配中心应急设备配套齐全，工程师24小时电话待命快速响应。'
WHERE id = 87;
UPDATE public.audit_default_group SET original = '6.10.6.1.A.1持续改进有成效，医用耗材实施全程管理，并符合相关要求。百元医疗收入(不含药品收入)中消耗的卫生材料降到20元以下。',
highlights = null
WHERE id = 88;
UPDATE public.audit_default_group SET original = '6.10.7.1.A.1持续改进有成效，医学装备管理规范、使用科室满意度不断提高。',
highlights = '根据临床需求，不断调整服务方式，通过滚动培训，上机考核等手段提高设备规范使用提高设备使用率，做好预防、应急维修提高设备完好率。'
WHERE id = 89;
UPDATE public.audit_default_group SET original = '6.10.7.2.A.1持续改进有成效，全院医学装备使用管理规范。',
highlights = null
WHERE id = 90;



INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 1, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 2, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 3, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 4, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 5, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 6, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 7, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 8, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 9, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 10, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 11, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 12, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 13, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 14, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 15, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 16, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 17, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 18, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 19, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 20, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 21, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(22, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 22, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(23, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 23, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(24, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 24, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(25, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 25, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(26, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 26, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(27, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 27, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(28, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 28, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(29, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 29, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(30, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 30, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(31, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 31, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(32, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 32, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(33, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 33, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(34, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 34, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(35, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 35, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(36, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 36, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(37, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 37, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(38, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 38, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(39, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 39, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(40, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 40, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(41, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 41, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(42, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 42, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(43, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 43, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(44, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 44, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(45, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 45, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(46, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 46, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(47, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 47, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(48, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 48, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(49, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 49, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(50, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 50, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(51, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 51, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(52, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 52, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(53, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 53, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(54, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 54, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(55, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 55, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(56, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 56, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(57, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 57, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(58, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 58, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(59, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 59, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(60, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 60, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(61, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 61, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(62, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 62, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(63, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 63, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(64, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 64, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(65, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 65, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(66, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 66, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(67, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 67, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(68, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 68, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(69, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 69, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(70, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 70, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(71, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 71, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(72, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 72, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(73, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 73, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(74, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 74, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(75, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 75, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(76, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 76, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(77, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 77, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(78, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 78, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(79, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 79, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(80, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 80, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(81, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 81, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(82, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 82, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(83, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 83, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(84, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 84, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(85, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 85, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(86, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 86, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(87, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 87, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(88, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 88, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(89, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 89, '参考模板', 1, '', false, false);
INSERT INTO public.audit_default_item
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, completed, group_id, name, order_num, owner, system_item, deleted)
VALUES(90, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '', false, 90, '参考模板', 1, '', false, false);



INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(1, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f905e3a540083cef0fbd', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '1', 'XX年全院设备清单.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f905e3a540083cef0fbd');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(2, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f94de3a540083cef0ff3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '1', '医疗设备仪器与医用耗材管理委员会工作职责.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f94de3a540083cef0ff3');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(3, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f979e3a540083cef0ff7', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '1', '医疗设备大购置预算表签字表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f979e3a540083cef0ff7');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(4, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f9a3e3a540083cef0ffb', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '1', '年度医疗设备大购置数据统计简表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f9a3e3a540083cef0ffb');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(5, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f9c0e3a540083cef0ffd', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '1', '年度医疗设备大购置论证会专家签到表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f9c0e3a540083cef0ffd');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(6, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f9dee3a540083cef0fff', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '1', '年度设备购置预算汇总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f9dee3a540083cef0fff');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(7, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f9f4e3a540083cef1001', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '1', '购置医疗设备汇总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f9f4e3a540083cef1001');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(8, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe02e3a540083cef102f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '2', '资产管理处工作岗位人员情况说明.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe02e3a540083cef102f');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(9, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe26e3a540083cef1031', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '3', '设备管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe26e3a540083cef1031');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(10, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe5ee3a540083cef1035', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '3', '资产管理处岗位职责.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe5ee3a540083cef1035');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(11, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe70e3a540083cef1039', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '3', '资产管理处工作岗位人员情况说明.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe70e3a540083cef1039');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(12, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56feafe3a540083cef103f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '4', '万元以上设备年度大购置申请论证流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56feafe3a540083cef103f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(13, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fec0e3a540083cef1041', 'application/msword',
true, '4', '乙类大型医用设备配置申请表.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fec0e3a540083cef1041');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(14, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56feeae3a540083cef1043', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '4', '党政联席会议、院务会记录.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56feeae3a540083cef1043');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(15, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56ff03e3a540083cef1049', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '4', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56ff03e3a540083cef1049');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(16, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56ff21e3a540083cef104f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '4', '大型设备购置论证报告.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56ff21e3a540083cef104f');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(17, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56ff3ee3a540083cef1051', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '2 医疗装备计划、采购、招标、合同、签收、安装、使用、折旧、安全管理等制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56ff3ee3a540083cef1051');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(18, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571809e3a540083cef10ab', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', 'PM保养计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571809e3a540083cef10ab');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(19, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57184ce3a540083cef10ad', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57184ce3a540083cef10ad');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(20, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571872e3a540083cef10af', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备仪器与医用耗材管理委员会工作职责.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571872e3a540083cef10af');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(21, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57189ce3a540083cef10b1', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备保养、校验制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57189ce3a540083cef10b1');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(22, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5718fae3a540083cef10b3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备年度购置计划申请制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5718fae3a540083cef10b3');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(23, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571abee3a540083cef10b5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备更新制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571abee3a540083cef10b5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(24, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571b11e3a540083cef10b7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备维修流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571b11e3a540083cef10b7');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(25, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571b6de3a540083cef10b9', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备维修管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571b6de3a540083cef10b9');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(26, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571bd0e3a540083cef10bb', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备论证流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571bd0e3a540083cef10bb');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(27, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571bf3e3a540083cef10bd', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备采购制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571bf3e3a540083cef10bd');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(28, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571c51e3a540083cef10bf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备验收制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571c51e3a540083cef10bf');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(29, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571d38e3a540083cef10d0', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医疗设备验收流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571d38e3a540083cef10d0');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(30, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571dd7e3a540083cef10d2', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '医院固定资产折旧年限表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571dd7e3a540083cef10d2');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(31, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571e59e3a540083cef10d7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '固定资产管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571e59e3a540083cef10d7');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(32, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571ed0e3a540083cef10d9', 'application/msword',
true, '5', '固定资产验收单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571ed0e3a540083cef10d9');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(33, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571ef4e3a540083cef10db', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '国有资产处置管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571ef4e3a540083cef10db');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(34, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f571f21e3a540083cef10dd', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '大型医用设备使用评价分析制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f571f21e3a540083cef10dd');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(35, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57224fe3a540083cef10f6', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '大型医用设备采购制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57224fe3a540083cef10f6');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(36, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57230de3a540083cef10fe', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '5', '维修工单记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57230de3a540083cef10fe');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(37, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57233be3a540083cef1100', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '计划外医疗设备购置申请制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57233be3a540083cef1100');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(38, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5724b9e3a540083cef1104', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '5', '设备报废处置流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5724b9e3a540083cef1104');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(39, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5724dbe3a540083cef1106', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '5', '资产账务，档案自查情况.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5724dbe3a540083cef1106');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(40, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57297de3a540083cef1166', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '6', '贵重医疗设备培训记录表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57297de3a540083cef1166');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(41, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572d6de3a540083cef11c4', 'application/msword',
true, '7', 'xx市乙类大型医用设备配置申请表.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572d6de3a540083cef11c4');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(42, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572dd5e3a540083cef11d4', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '7', '党政联席会议、院务会记录.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/testId');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(43, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572df2e3a540083cef11da', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '7', '医疗设备论证流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572df2e3a540083cef11da');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(44, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572e18e3a540083cef11dc', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '7', '年度医疗设备大购置论证会专家签到表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572e18e3a540083cef11dc');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(45, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572e8de3a540083cef11ec', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '8', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572e8de3a540083cef11ec');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(46, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572eb4e3a540083cef11fa', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '8', '固定资产台账清单.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572eb4e3a540083cef11fa');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(47, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572f97e3a540083cef120c', 'application/msword',
true, '9', '1.5T磁共振招标文件.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572f97e3a540083cef120c');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(48, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57304ae3a540083cef1223', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '9', 'APM_设备分类标准汇总.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57304ae3a540083cef1223');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(49, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57306ae3a540083cef1227', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '9', '医疗器械临床使用安全控制与风险管理流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57306ae3a540083cef1227');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(50, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57308be3a540083cef1233', 'application/msword',
true, '9', '回旋加速器 招标文件.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57308be3a540083cef1233');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(51, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5730a9e3a540083cef1235', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '9', '投标文件IBA.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5730a9e3a540083cef1235');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(52, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57324ee3a540083cef1261', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '10', '大型医用设备分析评价报告.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57324ee3a540083cef1261');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(53, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573276e3a540083cef1267', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '10', '大型医用设备成本效益分析模板.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573276e3a540083cef1267');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(54, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5732b6e3a540083cef128f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '10', '大型设备收益表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5732b6e3a540083cef128f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(55, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5732e5e3a540083cef129f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '10', '大型设备购置论证报告.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5732e5e3a540083cef129f');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(56, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5734a6e3a540083cef12bd', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '11', '不良事件汇总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5734a6e3a540083cef12bd');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(57, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5734c3e3a540083cef12bf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '11', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5734c3e3a540083cef12bf');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(58, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5734fbe3a540083cef12c1', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '11', '医疗装备应急程序.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5734fbe3a540083cef12c1');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(59, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573522e3a540083cef12c3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '11', '质量安全、紧急预案、保养模板、技术支持等.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573522e3a540083cef12c3');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(60, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573563e3a540083cef12c5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '12', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573563e3a540083cef12c5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(61, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57357de3a540083cef12c7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '12', '计量装备管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57357de3a540083cef12c7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(62, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5737c9e3a540083cef12f4', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '13', '医疗设备保养、校验制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5737c9e3a540083cef12f4');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(63, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5737ede3a540083cef12f8', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '14', 'xx年资产管理处相关制度培训考核表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5737ede3a540083cef12f8');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(64, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57381fe3a540083cef12fc', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '14', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57381fe3a540083cef12fc');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(65, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57384fe3a540083cef1304', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '14', '医学装备继续教育考核制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57384fe3a540083cef1304');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(66, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574036e3a540083cef13eb', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '15', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574036e3a540083cef13eb');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(67, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574066e3a540083cef13f7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '16', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574066e3a540083cef13f7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(68, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574122e3a540083cef1425', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '18', '介入放射学年度实施总人次数表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574122e3a540083cef1425');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(69, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574173e3a540083cef142f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '18', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574173e3a540083cef142f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(70, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57418be3a540083cef1437', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '18', '减压器接点表分布记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57418be3a540083cef1437');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(71, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5741a4e3a540083cef1439', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '18', '射线装置、放射工作场所、环境监测情况.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5741a4e3a540083cef1439');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(72, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5741bbe3a540083cef143b', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '18', '射线装置与同位素基本情况表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5741bbe3a540083cef143b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(73, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5741d2e3a540083cef143d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '18', '改进整改措施.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5741d2e3a540083cef143d');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(74, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574220e3a540083cef143f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '19', '员工安全教育培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574220e3a540083cef143f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(75, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574232e3a540083cef1441', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '19', '生产培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574232e3a540083cef1441');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(76, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5742e8e3a540083cef1464', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '21', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5742e8e3a540083cef1464');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(77, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574319e3a540083cef1466', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '22', '介入放射学年度实施总人次数表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574319e3a540083cef1466');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(78, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57432ee3a540083cef1468', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '22', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57432ee3a540083cef1468');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(79, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57434ee3a540083cef146c', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '22', '减压器接点表分布记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57434ee3a540083cef146c');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(80, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574363e3a540083cef1470', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '22', '射线装置、放射工作场所、环境监测情况.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574363e3a540083cef1470');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(81, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574379e3a540083cef1472', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '22', '射线装置与同位素基本情况表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574379e3a540083cef1472');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(82, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57438ee3a540083cef1474', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '22', '改进整改措施.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57438ee3a540083cef1474');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(83, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5744b6e3a540083cef14a0', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '27', '三基考核题目.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5744b6e3a540083cef14a0');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(84, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5744d8e3a540083cef14a6', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '27', '医疗仪器设备使用人员考核表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5744d8e3a540083cef14a6');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(85, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5744ece3a540083cef14a8', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '27', '设备使用人员考核机制.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5744ece3a540083cef14a8');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(86, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574500e3a540083cef14aa', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '27', '设备使用人员考核记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574500e3a540083cef14aa');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(87, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57452be3a540083cef14b4', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '28', '医疗设备培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57452be3a540083cef14b4');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(88, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574540e3a540083cef14b6', 'application/vnd.ms-excel',
true, '28', '医疗设备维修保养记录表.xls', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574540e3a540083cef14b6');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(89, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574553e3a540083cef14b8', 'application/msword',
true, '28', '医疗设备维修记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574553e3a540083cef14b8');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(90, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574566e3a540083cef14ba', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '28', '维修工单记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574566e3a540083cef14ba');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(91, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5745a0e3a540083cef14bc', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '29', '呼吸机意外情况处理表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5745a0e3a540083cef14bc');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(92, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5745b5e3a540083cef14c4', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '29', '监护仪意外情况处理方案.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5745b5e3a540083cef14c4');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(93, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5745cae3a540083cef14c6', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '29', '除颤仪意外情况处理表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5745cae3a540083cef14c6');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(94, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5745f6e3a540083cef14d2', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '30', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5745f6e3a540083cef14d2');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(95, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57460be3a540083cef14d6', 'application/msword',
true, '30', '放射设备操作说明.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57460be3a540083cef14d6');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(96, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574623e3a540083cef14d8', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '31', '医疗设备培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574623e3a540083cef14d8');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(97, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57465ee3a540083cef14ee', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '31', '科室培训考核评分表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57465ee3a540083cef14ee');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(98, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748ace3a540083cef151e', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '32', 'XXXX年全院设备清单.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748ace3a540083cef151e');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(99, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748b6e3a540083cef1550', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '32', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748b6e3a540083cef1550');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(100, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748bce3a540083cef1552', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '32', '设备管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748bce3a540083cef1552');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(101, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748c6e3a540083cef1554', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '33', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748c6e3a540083cef1554');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(102, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748d1e3a540083cef1558', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '33', '医疗设备维修管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748d1e3a540083cef1558');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(103, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5748dce3a540083cef155a', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '34', 'XX年--XX年所有报修记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5748dce3a540083cef155a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(104, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57496ae3a540083cef1574', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '35', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57496ae3a540083cef1574');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(105, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574973e3a540083cef1576', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '35', '医疗设备应急管理程序.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574973e3a540083cef1576');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(106, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574979e3a540083cef1578', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '35', '急救设备应急预案.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574979e3a540083cef1578');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(107, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574981e3a540083cef157a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '35', '急救设备故障处理流程表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574981e3a540083cef157a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(108, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749a1e3a540083cef1582', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '37', '医疗设备应急替代制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749a1e3a540083cef1582');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(109, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749a7e3a540083cef1584', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '37', '医疗设备应急替代工作流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749a7e3a540083cef1584');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(110, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749aee3a540083cef1588', 'application/msword',
true, '37', '呼吸机保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749aee3a540083cef1588');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(111, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749b8e3a540083cef158a', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '37', '急救医疗设备安全风险分析与预防性维护自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749b8e3a540083cef158a');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(112, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749c1e3a540083cef158c', 'application/msword',
true, '37', '监护仪保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749c1e3a540083cef158c');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(113, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749cae3a540083cef158e', 'application/msword',
true, '37', '除颤仪保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749cae3a540083cef158e');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(114, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749d3e3a540083cef1590', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '38', '医疗设备应急替代工作流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749d3e3a540083cef1590');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(115, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749dae3a540083cef1592', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '38', '应急中心设备借用情况总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749dae3a540083cef1592');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(116, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749e0e3a540083cef1594', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '38', '应急物资和设备储备计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749e0e3a540083cef1594');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(117, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749ebe3a540083cef1596', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '39', '医疗设备应急替代工作流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749ebe3a540083cef1596');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(118, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5749fae3a540083cef1598', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '39', '应急中心设备借用情况总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5749fae3a540083cef1598');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(119, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a00e3a540083cef159a', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '39', '应急物资和设备储备计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a00e3a540083cef159a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(120, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a2ee3a540083cef15a4', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '40', '一次性（含植入性）医用材料采购管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a2ee3a540083cef15a4');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(121, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a36e3a540083cef15a6', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '41', '医疗植入物知情同意书.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a36e3a540083cef15a6');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(122, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a3ee3a540083cef15a8', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '41', '耗材入库单.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a3ee3a540083cef15a8');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(123, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a47e3a540083cef15aa', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '41', '耗材出库单.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a47e3a540083cef15aa');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(124, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a61e3a540083cef15ac', 'application/pdf',
true, '43', '关于开展医疗器械不良事件上报工作的说明.PPT.pdf', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a61e3a540083cef15ac');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(125, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a69e3a540083cef15d7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '43', '医疗器械不良事件监测报告管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a69e3a540083cef15d7');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(126, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a71e3a540083cef15d9', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '43', '可疑医疗器械不良事件报告表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a71e3a540083cef15d9');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(127, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a77e3a540083cef15db', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '43', '高值耗材不良事件汇总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a77e3a540083cef15db');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(128, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574aa0e3a540083cef15dd', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '43', '医疗安全不良事件上报流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574aa0e3a540083cef15dd');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(129, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574adae3a540083cef15e9', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '44', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574adae3a540083cef15e9');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(130, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ae5e3a540083cef15eb', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '45', 'XXXX年度维修年终总结.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ae5e3a540083cef15eb');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(131, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574aebe3a540083cef15ed', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '45', '固定资产报废小结表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574aebe3a540083cef15ed');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(132, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574af3e3a540083cef15ef', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '45', '大购置执行情况月度汇总.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574af3e3a540083cef15ef');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(133, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b2de3a540083cef15ff', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一次性（含植入性）医用材料停用制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b2de3a540083cef15ff');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(134, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b35e3a540083cef1603', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一次性（含植入性）医用材料准入制度(流程).docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b35e3a540083cef1603');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(135, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b3de3a540083cef1605', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一次性（含植入性）医用材料经销商变更制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b3de3a540083cef1605');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(136, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b4fe3a540083cef1607', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一次性（含植入性）医用材料采购管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b4fe3a540083cef1607');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(137, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b5de3a540083cef1609', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一次性（含植入性）医用材料院内公开招标制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b5de3a540083cef1609');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(138, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b68e3a540083cef160b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '一级库房管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b68e3a540083cef160b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(139, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b71e3a540083cef160d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '不良事件应急预案的设定与启动.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b71e3a540083cef160d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(140, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b78e3a540083cef160f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '二级库房管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b78e3a540083cef160f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(141, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b82e3a540083cef1611', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '体外诊断试剂管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b82e3a540083cef1611');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(142, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b90e3a540083cef1613', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '供方资质审核评价制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b90e3a540083cef1613');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(143, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b9ce3a540083cef1615', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医学装备继续教育考核制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b9ce3a540083cef1615');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(144, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ba4e3a540083cef1617', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医用材料及设备退库、调拨、移交制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ba4e3a540083cef1617');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(145, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574bc0e3a540083cef1619', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医用材料采购供货协议签订制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574bc0e3a540083cef1619');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(146, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574befe3a540083cef161b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械不良事件监测报告管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574befe3a540083cef161b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(147, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c03e3a540083cef161d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械临床使用安全事件监测与报告制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c03e3a540083cef161d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(148, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c19e3a540083cef161f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械临床使用安全控制与风险管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c19e3a540083cef161f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(149, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c2ae3a540083cef1621', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械临床使用安全控制与风险管理应急预案.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c2ae3a540083cef1621');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(150, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c39e3a540083cef1623', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械技术支持与咨询服务制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c39e3a540083cef1623');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(151, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c45e3a540083cef1625', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗器械质量管理员职责.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c45e3a540083cef1625');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(152, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c5ae3a540083cef1627', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备保养、校验制度 .docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c5ae3a540083cef1627');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(153, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c63e3a540083cef1629', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备应急替代制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c63e3a540083cef1629');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(154, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574c9be3a540083cef162b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备应急管理程序 .docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574c9be3a540083cef162b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(155, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574cb9e3a540083cef162d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备应急预案.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574cb9e3a540083cef162d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(156, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574cc9e3a540083cef162f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备折旧及器械、耗材成本核算制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574cc9e3a540083cef162f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(157, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574cdae3a540083cef1631', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '医疗设备维修管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574cdae3a540083cef1631');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(158, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574d11e3a540083cef1633', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '安全控制与风险管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574d11e3a540083cef1633');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(159, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574df8e3a540083cef1639', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '应急物资和设备储备计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574df8e3a540083cef1639');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(160, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e0ae3a540083cef163b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '应急物资和设备管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e0ae3a540083cef163b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(161, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e17e3a540083cef163d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '廉政建设制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e17e3a540083cef163d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(162, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e25e3a540083cef163f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '征求临床意见制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e25e3a540083cef163f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(163, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e47e3a540083cef1641', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '捐赠物品管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e47e3a540083cef1641');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(164, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e62e3a540083cef1643', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '计量器具强制检定制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e62e3a540083cef1643');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(165, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e6ae3a540083cef1645', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '计量器具强制检定目录及检定周期表.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e6ae3a540083cef1645');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(166, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e73e3a540083cef1647', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '计量器具管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e73e3a540083cef1647');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(167, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e7de3a540083cef1649', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '质量与安全管理团队.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e7de3a540083cef1649');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(168, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e88e3a540083cef164b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '资产管理处制度总则.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e88e3a540083cef164b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(169, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574e93e3a540083cef164d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '资产管理处接待医疗器械生产经营企业的管理办法.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574e93e3a540083cef164d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(170, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ea0e3a540083cef164f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '46', '资产管理处质量与安全指标.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ea0e3a540083cef164f');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(171, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574eb6e3a540083cef1651', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '47', '万元以上设备年度大购置申请论证流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574eb6e3a540083cef1651');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(172, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ebfe3a540083cef1653', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗器械临床使用安全控制与风险管理应急预案.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ebfe3a540083cef1653');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(173, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ec6e3a540083cef1655', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗装备计划、采购、招标、合同、签收、安装、使用、折旧、安全管理等制度.docx.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ec6e3a540083cef1655');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(174, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ecee3a540083cef1657', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '47', '医疗设备大购置预算表签字表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ecee3a540083cef1657');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(175, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ed9e3a540083cef165b', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗设备应急替代制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ed9e3a540083cef165b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(176, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ee3e3a540083cef165d', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗设备应急管理程序.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ee3e3a540083cef165d');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(177, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574eede3a540083cef165f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗设备应急预案.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574eede3a540083cef165f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(178, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574efae3a540083cef1661', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '医疗设备论证流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574efae3a540083cef1661');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(179, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f05e3a540083cef1663', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '47', '年度医疗设备大购置数据统计简表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f05e3a540083cef1663');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(180, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f21e3a540083cef1667', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '应急物资和设备储备计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f21e3a540083cef1667');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(181, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f2de3a540083cef1669', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '应急物资和设备管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f2de3a540083cef1669');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(182, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f3ce3a540083cef166b', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '47', '计量设备检测流程.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f3ce3a540083cef166b');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(183, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f45e3a540083cef166d', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '47', '购置医疗设备汇总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f45e3a540083cef166d');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(184, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f54e3a540083cef166f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '48', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f54e3a540083cef166f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(185, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f5ee3a540083cef1671', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '48', '医疗设备使用人员考核表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f5ee3a540083cef1671');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(186, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574f65e3a540083cef1673', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '48', '贵重医疗设备培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574f65e3a540083cef1673');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(187, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f86ee3a540083cef0fab', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '49', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f86ee3a540083cef0fab');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(188, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f8b3e3a540083cef0fb3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '49', '高值耗材采购管理专项巡查工作安排.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f8b3e3a540083cef0fb3');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(189, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe96e3a540083cef103d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '50', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe96e3a540083cef103d');

INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(190, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572c12e3a540083cef1190', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '51', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572c12e3a540083cef1190');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(191, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572cfde3a540083cef11ae', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '52', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572cfde3a540083cef11ae');

INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(192, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5733dbe3a540083cef12b5', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '53', '大型医疗设备安全风险分析与预防性维护自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5733dbe3a540083cef12b5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(193, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57340fe3a540083cef12b7', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '53', '安全事件监测考核表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57340fe3a540083cef12b7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(194, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573468e3a540083cef12bb', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '54', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573468e3a540083cef12bb');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(195, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5738d6e3a540083cef1306', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '55', '医学工程部资产总档案管理自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5738d6e3a540083cef1306');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(196, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573fe8e3a540083cef13e5', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '55', '设备科机房环境定期自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573fe8e3a540083cef13e5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(197, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573fffe3a540083cef13e7', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '55', '超声科机房环境定期自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573fffe3a540083cef13e7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(198, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5740ade3a540083cef1405', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '57', '锅炉自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5740ade3a540083cef1405');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(199, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5740d7e3a540083cef1411', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '58', '减压器接点表分布记录.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5740d7e3a540083cef1411');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(200, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57410ae3a540083cef141d', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '58', '特种持续改进工作总结.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57410ae3a540083cef141d');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(201, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574281e3a540083cef144f', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '59', '医学工程部资产总档案管理自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574281e3a540083cef144f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(202, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574296e3a540083cef1451', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '59', '设备科机房环境定期自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574296e3a540083cef1451');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(203, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5742abe3a540083cef145b', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '59', '超声科机房环境定期自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5742abe3a540083cef145b');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(204, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5743cce3a540083cef147a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '61', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5743cce3a540083cef147a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(205, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5743f9e3a540083cef1480', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '62', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5743f9e3a540083cef1480');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(206, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574462e3a540083cef1490', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '63', '科室培训情况与效果反馈表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574462e3a540083cef1490');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(207, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57448ae3a540083cef1498', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '64', '设备培训情况总结.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57448ae3a540083cef1498');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(208, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574871e3a540083cef1514', 'application/vnd.openxmlformats-officedocument.wordprocessingml.sheet',
true, '65', '大型医用设备分析评价报告XXXX年.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574871e3a540083cef1514');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(209, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574881e3a540083cef1516', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '65', '心电监护仪故障分析报告.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574881e3a540083cef1516');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(210, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574888e3a540083cef1518', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '65', '设备分析报告 MRI_CT_DSA_MI.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574888e3a540083cef1518');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(211, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574895e3a540083cef151a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '66', '安全和风险防控排查情况小节.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574895e3a540083cef151a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(212, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57494ae3a540083cef156c', 'application/vnd.openxmlformats-officedocument.spreadsheetml.document',
true, '67', '大放绩效分析APM截图.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57494ae3a540083cef156c');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(213, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574950e3a540083cef1570', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '67', '应急中心设备借用情况总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574950e3a540083cef1570');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(214, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574997e3a540083cef1580', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '68', '应急中心设备借用情况总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574997e3a540083cef1580');



INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(215, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a09e3a540083cef159c', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '69', '高值耗材自查情况（表）.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a09e3a540083cef159c');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(216, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a11e3a540083cef159e', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '70', '内部巡查整改建议书.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a11e3a540083cef159e');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(217, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a1ee3a540083cef15a0', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '70', '高值耗材采购管理内部专项巡查工作安排.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a1ee3a540083cef15a0');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(218, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574a24e3a540083cef15a2', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '70', '高值耗材采购管理内部专项巡查整改报告.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574a24e3a540083cef15a2');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(219, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ac0e3a540083cef15e3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '71', '改进整改措施.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ac0e3a540083cef15e3');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(220, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ac7e3a540083cef15e5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '71', '质量安全情况汇总.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ac7e3a540083cef15e5');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(221, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ad1e3a540083cef15e7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '72', '安全和风险防控排查情况小节.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ad1e3a540083cef15e7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(222, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b1de3a540083cef15f9', 'application/msword',
true, '73', 'XXXX年度在院设备.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b1de3a540083cef15f9');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(223, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f7ffe3a540083cef0f9f', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '75', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f7ffe3a540083cef0f9f');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(224, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f82fe3a540083cef0fa5', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '75', '医院发展规划预算项目库分年度筹资安排-大型医疗设备购置项目.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f82fe3a540083cef0fa5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(225, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56f84fe3a540083cef0fa7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '75', '资产管理关于十三五规划大型医用设备的调整计划.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56f84fe3a540083cef0fa7');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(226, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f56fe82e3a540083cef103b', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '76', '工作亮点总结.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f56fe82e3a540083cef103b');

INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(227, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f572bdee3a540083cef1188', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '77', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f572bdee3a540083cef1188');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(228, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573345e3a540083cef12af', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '78', '安全和风险防控排查情况小节.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573345e3a540083cef12af');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(229, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f573381e3a540083cef12b1', 'application/vnd.ms-excel',
true, '78', '设备及人员 安全管理各类统计表.xls', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f573381e3a540083cef12b1');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(230, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574086e3a540083cef13fd', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '80', '特种设备持续改进报告.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574086e3a540083cef13fd');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(231, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574421e3a540083cef148a', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '84', '设备培训情况分析.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574421e3a540083cef148a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(232, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5746e3e3a540083cef14fc', 'application/vnd.ms-excel',
true, '85', '医疗设备维修保养记录表.xls', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5746e3e3a540083cef14fc');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(233, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f5746f5e3a540083cef14fe', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '85', '维修保养记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f5746f5e3a540083cef14fe');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(234, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57491ce3a540083cef1562', 'application/msword',
true, '86', '呼吸机保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57491ce3a540083cef1562');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(235, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574924e3a540083cef1564', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '86', '急救医疗设备安全风险分析与预防性维护自查表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574924e3a540083cef1564');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(236, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57492ce3a540083cef1566', 'application/msword',
true, '86', '监护仪保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57492ce3a540083cef1566');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(237, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574933e3a540083cef1568', 'application/msword',
true, '86', '维修保养记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574933e3a540083cef1568');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(238, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574940e3a540083cef156a', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '86', '除颤仪保养记录单.doc', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574940e3a540083cef156a');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(239, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f57498ce3a540083cef157c', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '87', '应急中心设备借用情况总表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f57498ce3a540083cef157c');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(240, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ab1e3a540083cef15df', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '89', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ab1e3a540083cef15df');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(241, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574ab6e3a540083cef15e1', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
true, '89', '医疗设备培训记录表.xlsx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574ab6e3a540083cef15e1');


INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(242, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574afde3a540083cef15f1', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '90', '其他文档说明.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574afde3a540083cef15f1');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(243, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b03e3a540083cef15f3', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '90', '医疗设备保养、校验制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b03e3a540083cef15f3');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(244, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b0ae3a540083cef15f5', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '90', '医疗设备维修管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b0ae3a540083cef15f5');
INSERT INTO public.audit_default_file
(id, created_by, created_date, hospital_uid, institution_uid, last_modified_by, last_modified_date, site_uid, tenant_uid, system_file, deleted, expired, file_id, file_type, template, item_id, name, upload_by, upload_time, url)
VALUES(245, 'system', '2020-05-20 00:00:00.0', '', '', 'system', '2020-05-20 00:00:00.0', '', '',true, false, false, '5f574b11e3a540083cef15f7', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
true, '90', '计量器具管理制度.docx', 'system', '2020-05-20 00:00:00.0', '/hcapmobjecthubservice/api/apm/objectHub/objects/download/5f574b11e3a540083cef15f7');

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