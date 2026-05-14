-- YHAAO-27960 配置项-临床助手“查询天数”，改名为“有问题工单查询时间范围”
UPDATE config_item SET name = '有问题工单查询时间范围' WHERE key = 'setQueryNumberDays' AND name != '有问题工单查询时间范围';
