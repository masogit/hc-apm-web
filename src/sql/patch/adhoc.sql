CREATE UNIQUE INDEX idx_udid_registry_num_info_registry_num ON udid_registry_num_info (registry_num);

INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0110'),
       '011006',
       '01-01-10-06',
       '乳腺旋切活检系统及附件'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '011006');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0313'),
       '031327',
       '01-03-13-27',
       '脑血栓取出装置'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '031327');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '0313'),
       '031328',
       '01-03-13-28',
       '房间隔穿刺鞘'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '031328');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1007'),
       '100702',
       '01-10-07-02',
       '离体脏器机械灌注转运设备'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '100702');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1307'),
       '130709',
       '01-13-07-09',
       '心耳夹'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '130709');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1309'),
       '130911',
       '01-13-09-11',
       '整形用植入线材'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '130911');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1311'),
       '131104',
       '01-13-11-04',
       '组织诱导性植入器械'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '131104');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1311'),
       '131105',
       '01-13-11-05',
       '含几丁糖关节腔内液体'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '131105');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1607'),
       '160717',
       '01-16-07-17',
       '人工角膜'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '160717');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '1706'),
       '170607',
       '01-17-06-07',
       '增材制造用金属、陶瓷义齿制作材料'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '170607');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2212'),
       '221204',
       '01-22-12-04',
       '微生物样本前处理仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221204');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2212'),
       '221205',
       '01-22-12-05',
       '粪便分析前处理仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221205');
INSERT INTO sys_auth_group (uid, parent_uid, classification_code, encoded, name)
SELECT REPLACE(uuid_generate_v4()::TEXT, '-', ''),
       (SELECT uid FROM sys_auth_group WHERE classification_code = '2213'),
       '221303',
       '01-22-13-03',
       '细胞过滤分选仪器'
WHERE NOT EXISTS (SELECT 1 FROM sys_auth_group WHERE classification_code = '221303');
