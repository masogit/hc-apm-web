
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

 -- 2021-05-21 导入GEDictionaryExcel配置权限  start
 delete from role_msg_rel where msg_key = 'GEDictionaryExcel';
 INSERT INTO "public"."role_msg_rel"("msg_key", "msg_type", "role_id", "role_name", "role_type") VALUES ('GEDictionaryExcel', 'batchFileType', (select id from sys_role where name = 'DataMaintainStaff'), 'DataMaintainStaff', 0);
 -- end