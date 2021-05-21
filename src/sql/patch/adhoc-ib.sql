--2021/05/21 start 修改自订阅消息中代理商FE、代理商Salses配置
update user_subscription_push set sub_status=true,send_status=true where role_position in('DealerFe','DealerSales') and menu_type in(5,6);
--2021/05/21 end