-- YHAAO-23998 默认修改供应商用户为管理员
update vender_info set is_admin = true where is_admin is false;
-- YHAAO-24115 迁移act_hi_taskinst表数据到保养新增步骤表
INSERT INTO v2_pm_order_step (id, pm_id, step_id, owner_uid, start_time, end_time) SELECT replace(uuid_generate_v4()::text, '-', ''), p.id, a.name_, a.assignee_, a.start_time_,a.end_time_ FROM act_hi_taskinst a LEFT JOIN v2_pm_order p ON a.proc_inst_id_ = p.inst_id WHERE a.PROC_DEF_ID_ LIKE 'pmOrderProcess:%' AND NOT EXISTS (SELECT 1 FROM v2_pm_order_step LIMIT 1);
