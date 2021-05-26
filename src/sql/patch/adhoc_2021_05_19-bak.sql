-- 删除开关机登记的外键约束
ALTER TABLE asset_opt_record DROP CONSTRAINT "fk3goow0twmmkvcf8rem5juby3j";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkhmd105dn8hl6gywkb5wffemxw";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkfxunn74gmcytw84gwilo20apk";

-- 删除收藏表唯一约束
ALTER TABLE user_favorites DROP CONSTRAINT "uk_c0m37nir6vp9w1hdoffxtcpr";

-- GE FE老数据 默认是非测试数据
UPDATE zkb_fe_part_order set test_status = 'f';
-- GE FE老数据 测试租户数据设置成测试状态
UPDATE zkb_fe_part_order set test_status = 't' where tenant_uid in ( SELECT uid from tenant_info where name in ('ZKB租户田瑛测试','ZKB田瑛111','ZKB管理租户','ZKBdemo','','ZKB体验租户'));

-- 初始化建飞角色
DELETE from zkb_course_train_user_config where id = '72f9a7f1dcc54c18841bd7bc4bd31356';
INSERT INTO zkb_course_train_user_config (id, login_name,email) VALUES ('72f9a7f1dcc54c18841bd7bc4bd31356', 'test.jianfei','ying.tian@ge.com');

-- 医疗器械分类编码迁移
with recursive cte_auth_group as (
    select * from sys_auth_group where parent_uid is null
    union
    select sag.* from sys_auth_group sag join cte_auth_group cte on sag.parent_uid = cte.uid
), cte_code as (
    select uid,
           cardinality(string_to_array(encoded, '-')) as length,
           (case
                when cardinality(string_to_array(encoded, '-')) = 1 then encoded
                when cardinality(string_to_array(encoded, '-')) = 2 then encoded
                when cardinality(string_to_array(encoded, '-')) = 3
                    then (string_to_array(encoded, '-'))[1]
                        || '-' || (string_to_array(encoded, '-'))[2]
                        || '-' || right((string_to_array(encoded, '-'))[3], 2)
           when cardinality(string_to_array(encoded, '-')) = 4
               then (string_to_array(encoded, '-'))[1]
                        || '-' || (string_to_array(encoded, '-'))[2]
                        || '-' || right((string_to_array(encoded, '-'))[3], 2)
                        || '-' || right((string_to_array(encoded, '-'))[4], 2)
            else ''
        end) as encoded_new,
           (case
                when cardinality(string_to_array(encoded, '-')) = 1 then null
                when cardinality(string_to_array(encoded, '-')) = 2
                    then (string_to_array(encoded, '-'))[2]
                when cardinality(string_to_array(encoded, '-')) = 3
                    then (string_to_array(encoded, '-'))[2]
                        || right((string_to_array(encoded, '-'))[3], 2)
           when cardinality(string_to_array(encoded, '-')) = 4
               then (string_to_array(encoded, '-'))[2]
                        || right((string_to_array(encoded, '-'))[3], 2)
                        || right((string_to_array(encoded, '-'))[4], 2)
            else null

        end) as classification_code_new
    from cte_auth_group
    order by encoded
)

update sys_auth_group  set encoded = code.encoded_new, classification_code = code.classification_code_new
    from cte_code code
where sys_auth_group.uid = code.uid;

--资产表的 医疗器械分类编码迁移
with cte_asset_info as (
    select uid,
           medical_apparatus_classification,
           cardinality(string_to_array(medical_apparatus_classification, '-')) as length,
           (case
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 1 then medical_apparatus_classification
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 2 then medical_apparatus_classification
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 3
                    then (string_to_array(medical_apparatus_classification, '-'))[1]
                        || '-' || (string_to_array(medical_apparatus_classification, '-'))[2]
                        || '-' || right((string_to_array(medical_apparatus_classification, '-'))[3], 2)
           when cardinality(string_to_array(medical_apparatus_classification, '-')) = 4
               then (string_to_array(medical_apparatus_classification, '-'))[1]
                        || '-' || (string_to_array(medical_apparatus_classification, '-'))[2]
                        || '-' || right((string_to_array(medical_apparatus_classification, '-'))[3], 2)
                        || '-' || right((string_to_array(medical_apparatus_classification, '-'))[4], 2)
            else ''
        end) as encoded_new,
           (case
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 1 then null
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 2
                    then (string_to_array(medical_apparatus_classification, '-'))[2]
                when cardinality(string_to_array(medical_apparatus_classification, '-')) = 3
                    then (string_to_array(medical_apparatus_classification, '-'))[2]
                        || right((string_to_array(medical_apparatus_classification, '-'))[3], 2)
           when cardinality(string_to_array(medical_apparatus_classification, '-')) = 4
               then (string_to_array(medical_apparatus_classification, '-'))[2]
                        || right((string_to_array(medical_apparatus_classification, '-'))[3], 2)
                        || right((string_to_array(medical_apparatus_classification, '-'))[4], 2)
            else null

        end) as classification_code_new
    from asset_info where medical_apparatus_classification is not null
)

update asset_info  set medical_apparatus_classification = cte.classification_code_new
    from cte_asset_info cte
where asset_info.uid = cte.uid and asset_info.medical_apparatus_classification is not null;

-- 设备型号数据初始化
update arrival_list_item set function_type=(select function_type from arrival_list where id=arrival_list_item.arrival_list_id);
--2020-05-19 game plan添加batch file type
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','ProbeRecommendExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);




