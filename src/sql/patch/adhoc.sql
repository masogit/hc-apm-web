--菜单添加
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierMaintain','预防维护','supplier maintain',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierMaintain.workbench','保养工作台','supplier maintain workbench ',NULL,-1);


--智康保合同日期
update zkb_contract set contract_start_date = start_date, contract_end_date = end_date;