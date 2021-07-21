-- YHAAO-12614
update asset_alert_config set wechat_message = '检测到您的液位已低于推荐阈值，请密切关注液位变化，做到每天记录，确保远程监控联通；考虑到液氦供货周期长，需提前计划液氦预定和添加。',
 pad_message = '液氦液位已低于推荐阈值，请确保远程监控联通并提前计划液氦预定及添加。'
 where label = '液氦液面高度' and lower_limit is not null;