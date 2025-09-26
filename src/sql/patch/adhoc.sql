ALTER TABLE v2_blob_object ADD column approve_status varchar(1);

ALTER TABLE asset_out_stock_record ADD column receive_status BOOLEAN;

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','receiveStatus','临床确认','Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','receiveStatus','临床确认','Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('receiveStatus','true','已确认','Confirmed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('receiveStatus','false','未确认','Unconfirmed',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','measureocr','计量OCR识别','Measurement Information',NULL,-1);

INSERT INTO public.config_item (uid, category, default_value, description, key, name, super_admin_edit,user_parent_config, value_type)select replace(uuid_generate_v4()::text,'-',''), 2, '{"rendered":false,"label":"借用原因","required":"false","requiredMessage":"借用原因必填！"}', '借用原因字段配置', 'rentReason', '借用原因', 'f','t',9 where not exists(select * from config_item where key = 'rentReason');
