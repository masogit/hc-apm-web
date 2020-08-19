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
insert into asset_location_info values ('n_1','1号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,1);
insert into asset_location_info values ('n_2','2号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,2);
insert into asset_location_info values ('n_3','3号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,3);
insert into asset_location_info values ('n_5','5号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,4);
insert into asset_location_info values ('n_6','6号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,5);
insert into asset_location_info values ('n_7','7号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,6);
insert into asset_location_info values ('n_8','8号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,7);
insert into asset_location_info values ('n_9','9号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,8);
insert into asset_location_info values ('n_10','10号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,9);
insert into asset_location_info values ('n_11','11号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,10);
insert into asset_location_info values ('n_12','12号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,11);
insert into asset_location_info values ('n_xmz','新门诊楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,12);
insert into asset_location_info values ('n_frmz','发热门诊楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'main',null,13);

###1号楼
insert into asset_location_info values ('n_1_1','1层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_1_2','2层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_1_3','3层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_1_4','4层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_1_5','5层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_1_6','6层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_1_7','7层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info values ('n_1_8','8层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info values ('n_1_9','9层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info values ('n_1_10','10层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info values ('n_1_11','11层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);
insert into asset_location_info values ('n_1_12','12层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,12);
insert into asset_location_info values ('n_1_13','13层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,13);
insert into asset_location_info values ('n_1_14','14层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,14);
insert into asset_location_info values ('n_1_15','15层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,15);
insert into asset_location_info values ('n_1_16','16层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,16);
insert into asset_location_info values ('n_1_17','17层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,17);
insert into asset_location_info values ('n_1_18','18层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,18);
insert into asset_location_info values ('n_1_19','19层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,19);
insert into asset_location_info values ('n_1_20','20层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,20);
insert into asset_location_info values ('n_1_21','21层','n_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,21);


###2号楼
insert into asset_location_info values ('n_2_1','1层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_2_2','2层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_2_3','3层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_2_4','4层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_2_5','5层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_2_6','6层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_2_7','7层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info values ('n_2_8','8层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info values ('n_2_9','9层','n_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);


###3号楼
insert into asset_location_info values ('n_3_1','1层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_3_2','2层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_3_3','3层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_3_4','4层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_3_5','5层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_3_6','6层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_3_7','7层','n_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);


###5号楼
insert into asset_location_info values ('n_5_1','1层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_5_2','2层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_5_3','3层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_5_4','4层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_5_5','5层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_5_6','6层','n_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);


###6号楼
insert into asset_location_info values ('n_6_1','1层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_6_2','2层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_6_3','3层','n_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);


###7号楼
insert into asset_location_info values ('n_7_1','1层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_7_2','2层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_7_3','3层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_7_4','4层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_7_5','5层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_7_6','6层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_7_7','7层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info values ('n_7_8','8层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info values ('n_7_9','9层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info values ('n_7_10','10层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info values ('n_7_11','11层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);
insert into asset_location_info values ('n_7_12','12层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,12);
insert into asset_location_info values ('n_7_13','13层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,13);
insert into asset_location_info values ('n_7_14','14层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,14);
insert into asset_location_info values ('n_7_15','15层','n_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,15);


###8号楼
insert into asset_location_info values ('n_8_1','1层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_8_2','2层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_8_3','3层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_8_4','4层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_8_5','5层','n_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);


###9号楼
insert into asset_location_info values ('n_9_1','1层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_9_2','2层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_9_3','3层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_9_4','4层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_9_5','5层','n_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);


###10号楼
insert into asset_location_info values ('n_10_1','1层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_10_2','2层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_10_3','3层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_10_4','4层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_10_5','5层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_10_6','6层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_10_7','7层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info values ('n_10_8','8层','n_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);


###11号楼
insert into asset_location_info values ('n_11_1','1层','n_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_11_2','2层','n_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


###12号楼
insert into asset_location_info values ('n_12_1','1层','n_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_12_2','2层','n_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


###新门诊楼
insert into asset_location_info values ('n_xmz_1','1层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_xmz_2','2层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);
insert into asset_location_info values ('n_xmz_3','3层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,3);
insert into asset_location_info values ('n_xmz_4','4层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,4);
insert into asset_location_info values ('n_xmz_5','5层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,5);
insert into asset_location_info values ('n_xmz_6','6层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,6);
insert into asset_location_info values ('n_xmz_7','7层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,7);
insert into asset_location_info values ('n_xmz_8','8层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,8);
insert into asset_location_info values ('n_xmz_9','9层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,9);
insert into asset_location_info values ('n_xmz_10','10层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,10);
insert into asset_location_info values ('n_xmz_11','11层','n_xmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,11);


###发热门诊楼
insert into asset_location_info values ('n_frmz_1','1层','n_frmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,1);
insert into asset_location_info values ('n_frmz_2','2层','n_frmz', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='南部院区'),'sub',null,2);


--北部
insert into asset_location_info values ('b_1','1号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,1);
insert into asset_location_info values ('b_2','2号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,2);
insert into asset_location_info values ('b_3','3号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,3);
insert into asset_location_info values ('b_5','5号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,4);
insert into asset_location_info values ('b_6','6号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,5);
insert into asset_location_info values ('b_7','7号楼',null, (select oi.uid from tenant_info ti,org_infbo oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,6);
insert into asset_location_info values ('b_8','8号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,7);
insert into asset_location_info values ('b_9','9号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,8);
insert into asset_location_info values ('b_10','10号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,9);
insert into asset_location_info values ('b_11','11号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,10);
insert into asset_location_info values ('b_12','12号楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,11);
insert into asset_location_info values ('b_tjzx','体检中心',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,12);
insert into asset_location_info values ('b_yjls','药剂临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,13);
insert into asset_location_info values ('b_jgls','技工临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,14);
insert into asset_location_info values ('b_hqls','后勤临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,15);
insert into asset_location_info values ('b_ls','临时楼',null, (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'main',null,16);


###1号楼
insert into asset_location_info values ('b_1_-1','-1层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_1_1','1层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_1_2','2层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_1_3','3层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_1_4','4层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info values ('b_1_5','5层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info values ('b_1_6','6层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info values ('b_1_7','7层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info values ('b_1_8','8层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);
insert into asset_location_info values ('b_1_9','9层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,9);
insert into asset_location_info values ('b_1_10','10层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,10);
insert into asset_location_info values ('b_1_11','11层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,11);
insert into asset_location_info values ('b_1_12','12层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,12);
insert into asset_location_info values ('b_1_13','13层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,13);
insert into asset_location_info values ('b_1_14','14层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,14);
insert into asset_location_info values ('b_1_15','15层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,15);
insert into asset_location_info values ('b_1_16','16层','b_1', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,16);


###2号楼
insert into asset_location_info values ('b_2_-1','-1层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_2_1','1层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_2_2','2层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_2_3','3层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_2_4','4层','b_2', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);


###3号楼
insert into asset_location_info values ('b_3_-1','-1层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_3_1','1层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_3_2','2层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_3_3','3层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_3_4','4层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info values ('b_3_5','5层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info values ('b_3_6','6层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info values ('b_3_7','7层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info values ('b_3_8','8层','b_3', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);


###5号楼
insert into asset_location_info values ('b_5_-1','-1层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_5_1','1层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_5_2','2层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_5_3','3层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_5_4','4层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info values ('b_5_5','5层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info values ('b_5_6','6层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info values ('b_5_7','7层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);
insert into asset_location_info values ('b_5_8','8层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,8);
insert into asset_location_info values ('b_5_9','9层','b_5', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,9);


###6号楼
insert into asset_location_info values ('b_6_-1','-1层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_6_1','1层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_6_2','2层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_6_3','3层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_6_4','4层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info values ('b_6_5','5层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info values ('b_6_6','6层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);
insert into asset_location_info values ('b_6_7','7层','b_6', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,7);


###7号楼
insert into asset_location_info values ('b_7_1','1层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_7_2','2层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_7_3','3层','b_7', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


###8号楼
insert into asset_location_info values ('b_8_1','1层','b_8', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


###9号楼
insert into asset_location_info values ('b_9_-1','-1层','b_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,-1);
insert into asset_location_info values ('b_9_1','1层','b_9', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


###10号楼
insert into asset_location_info values ('b_10_1','1层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_10_2','2层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_10_3','3层','b_10', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


###11号楼
insert into asset_location_info values ('b_11_1','1层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_11_2','2层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_11_3','3层','b_11', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);


###12号楼
insert into asset_location_info values ('b_12_1','1层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_12_2','2层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);
insert into asset_location_info values ('b_12_3','3层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,3);
insert into asset_location_info values ('b_12_4','4层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,4);
insert into asset_location_info values ('b_12_5','5层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,5);
insert into asset_location_info values ('b_12_6','6层','b_12', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,6);


###体检中心
insert into asset_location_info values ('b_tjzx_1','1层','b_tjzx', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);


###药剂临时楼
insert into asset_location_info values ('b_yjls_1','1层','b_yjls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_yjls_2','2层','b_yjls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);


###技工临时楼
insert into asset_location_info values ('b_jgls_1','1层','b_jgls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_jgls_2','2层','b_jgls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);



###后勤临时楼
insert into asset_location_info values ('b_hqls_1','1层','b_hqls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);
insert into asset_location_info values ('b_hqls_2','2层','b_hqls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,2);


###临时楼
insert into asset_location_info values ('b_ls_1','1层','b_ls', (select oi.uid from tenant_info ti,org_info oi where ti.uid=oi.tenant_uid and ti.name = '上海交通大学医学院附属第九人民医院' and oi.org_type=3 and oi.name='北部院区'),'sub',null,1);

-- 2020.08.19 end 供应商系统 楼层数据


###初始化目录
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
INSERT INTO "zkb_file_directory"("id", "level", "name", "parent_id") VALUES ('66                              ', 2, 'LOGIQ P3 ', '8                               ');
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


