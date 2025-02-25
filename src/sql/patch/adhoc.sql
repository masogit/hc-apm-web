-- YHAAO-23998 默认修改供应商用户为管理员
update vender_info set is_admin = true where is_admin is false;
-- YHAAO-24115 迁移act_hi_taskinst表数据到保养新增步骤表
INSERT INTO v2_pm_order_step (id, pm_id, step_id, owner_uid, start_time, end_time) SELECT replace(uuid_generate_v4()::text, '-', ''), p.id, a.name_, a.assignee_, a.start_time_,a.end_time_ FROM act_hi_taskinst a LEFT JOIN v2_pm_order p ON a.proc_inst_id_ = p.inst_id WHERE a.PROC_DEF_ID_ LIKE 'pmOrderProcess:%' AND NOT EXISTS (SELECT 1 FROM v2_pm_order_step LIMIT 1);

--  YHAAO-24132 日常检查-有问题工单颜色显示优化  日常检查新增状态类型
update  nurse_site_maintain nsm  set status=9 where (status=3  or  status=8  ) and exists(select 1 from nurse_site_maintain_item nsmi where nsmi.parent_id=nsm.id  and  (nsmi.is_finish=false  or nsmi.is_finish is null ) );
-- YHAAO-24181  【综合绩效分析】新增投资收益率相关数据  添加投资收益配置
insert into public.config_item (uid, category, default_value, description, key, name, super_admin_edit, user_parent_config, value_type,validation) select replace(uuid_generate_v4()::text,'-',''), 10, '1', '采取什么计算方式来计算月投资收益率', 'investmentCalculationMethod', '投资收益率计算方式', 'f', 't', 7 , '[{"key":"计算方式1(ROI = (收入 - 成本)/设备原值*100%)","value":"1"},{"key":"计算方式2(ROI = (收入 - 成本)/成本*100%)","value":"2"},{"key":"计算方式3(①每月应计收入 = 设备原值/30；②ROI=  (收入 - 成本)/（每月应计收入*月份数）*100%，月份数=当前时间段的月份数，即（结束时间的月份-开始时间的月份+1）)","value":"3"}]' where not exists (select * from config_item where key = 'investmentCalculationMethod');