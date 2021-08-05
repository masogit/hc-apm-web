--YHAAO-12832 2021-08-05
update asset_alert_config set pad_message = '冷却水流量异常，流量过低会导致氦压缩机停机，流量过高会导致氦压缩机制冷效率下降，请立刻报修。' where config_type = 101;