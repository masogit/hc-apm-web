-- YHAAO-21750 盘点查询优化
update inventory_order_detail set is_last = true from  (
select id, ROW_NUMBER() OVER (PARTITION BY asset_id ORDER BY last_modified_date DESC) AS rn from inventory_order_detail  ) t
where inventory_order_detail.id = t.id and t.rn = 1;

update inventory_issue set is_last = true from  (
select id, ROW_NUMBER() OVER (PARTITION BY asset_id ORDER BY last_modified_date DESC) AS rn from inventory_issue  ) t
where inventory_issue.id = t.id and t.rn = 1;