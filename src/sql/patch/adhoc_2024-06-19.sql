update asset_termination_report_template  set template_type='ToBeTerminated';
-- YHAAO-21868 合同增加到期提醒天数配置项
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type) select replace(uuid_generate_v4()::text,'-',''), 1, '0', '合同列表到期标红（0默认不开启）', 'contractExpirationReminderDays', '合同到期提醒天数', 'f', 't', 2 where not exists (select * from config_item where key = 'contractExpirationReminderDays');
-- YHAAO-21974 MVS工单跨租户同步 手机端事件记录
DROP TABLE IF EXISTS "public"."mvs_ib_mapping";

CREATE SEQUENCE IF NOT EXISTS mvs_ib_mapping_id_seq START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE;

CREATE TABLE "public"."mvs_ib_mapping" (
    "id" int4 NOT NULL DEFAULT nextval('mvs_ib_mapping_id_seq':: regclass),
    "create_time" TIMESTAMP(6),
    "created_by" VARCHAR(255) COLLATE "pg_catalog"."default",
    "ib_id" int4,
    "ib_system_id" VARCHAR(255) COLLATE "pg_catalog"."default",
    "ib_system_num" VARCHAR(255) COLLATE "pg_catalog"."default",
    "ib_tenant_name" VARCHAR(255) COLLATE "pg_catalog"."default",
    "ib_tenant_uid" VARCHAR(255) COLLATE "pg_catalog"."default",
    "modify_time" TIMESTAMP(6),
    "mvs_ib_id" int4,
    "mvs_qrcode" VARCHAR(255) COLLATE "pg_catalog"."default",
    "ib_uid" VARCHAR(255) COLLATE "pg_catalog"."default",
    "mvs_ib_uid" VARCHAR(255) COLLATE "pg_catalog"."default"
);



INSERT INTO
    mvs_ib_mapping(
        create_time,
        created_by,
        ib_id,
        ib_system_id,
        ib_system_num,
        ib_tenant_name,
        ib_tenant_uid,
        modify_time,
        mvs_ib_id,
        mvs_qrcode
    )
SELECT
    CURRENT_TIMESTAMP,
    'system',
    ai.id AS ib_id,
    ai.system_id AS ib_system_id,
    ai.system_num1 AS ib_system_num,
    ti.name AS ib_tenant_name,
    ti.uid AS ib_tenant_uid,
    CURRENT_TIMESTAMP,
    mvs.id AS mvs_ib_id,
    mvs.qr_code AS mvs_qrcode
FROM
    asset_info ai,
    asset_info mvs,
    tenant_info ti
WHERE
    mvs.tenant_uid = '68fc4f19938546d7bdfaf2d405cd6e6d'
    AND ai.tenant_uid != '68fc4f19938546d7bdfaf2d405cd6e6d'
    AND ai.tenant_uid = ti.uid
    AND (
        (
            mvs.qr_code = SUBSTRING(ai.system_id, 5)
            AND ai.system_id LIKE 'MVS-%'
        )
        OR (
            mvs.qr_code = SUBSTRING(ai.system_num1, 5)
            AND ai.system_num1 LIKE 'MVS-%'
        )
    );
-- 报修人电话同步历史数据
update v2_service_request sr set requestor_phone = ua.telephone from user_account ua where sr.requestor_id = ua.id and sr.requestor_phone is null and ua.telephone is not null;