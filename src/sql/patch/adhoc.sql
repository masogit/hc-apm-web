-- 设备型号数据初始化
update arrival_list_item set function_type=(select function_type from arrival_list where id=arrival_list_item.arrival_list_id);

-- GE FE老数据 默认是非测试数据
UPDATE zkb_fe_part_order set test_status = 'f';
-- GE FE老数据 测试租户数据设置成测试状态
UPDATE zkb_fe_part_order set test_status = 't' where tenant_uid in ( SELECT uid from tenant_info where name in ('ZKB租户田瑛测试','ZKB田瑛111','ZKB管理租户','ZKBdemo','','ZKB体验租户'));