--2021/05/21 start 修改自订阅消息中代理商FE、代理商Salses配置
update user_subscription_push set sub_status=true,send_status=true,default_send_status=true where role_position in('DealerFe','DealerSales') and menu_type in(5,6);
--2021/05/21 end

-- 删除开关机登记的外键约束
ALTER TABLE asset_opt_record DROP CONSTRAINT "fk3goow0twmmkvcf8rem5juby3j";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkhmd105dn8hl6gywkb5wffemxw";
ALTER TABLE asset_opt_record DROP CONSTRAINT "fkfxunn74gmcytw84gwilo20apk";