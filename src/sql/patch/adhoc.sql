
create index wechat_message_history_request_source on wechat_message_history (request_source);

--修改Asset_info.name字段长度，批量资产创建使用
ALTER TABLE asset_info alter COLUMN name type character varying(128);