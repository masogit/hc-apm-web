-- 设备型号数据初始化
update arrival_list_item set function_type=(select function_type from arrival_list where id=arrival_list_item.arrival_list_id);