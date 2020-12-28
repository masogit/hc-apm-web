-- 2020-12-21 九院物资系统'设备是否进口'字段值修复，国产设备为2 start
update arrival_list_item set import_equipment=2 where import_equipment=0;
update asset_info set import_equipment=2 where import_equipment=0;
-- 2020-12-21 end

-- 2020-12-10 微信消息推送角色订阅配置 start
--menu_type:1,保养报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, true, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, true, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, true, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, true, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '1', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3', FALSE, '1', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43', FALSE, '1', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1', FALSE, '1', false, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41', FALSE, '1', false, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '1', true, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '1', true, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=1 and role_position='DealerFe');--DealerFe

--menu_type:2 保养微报上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '2', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3', FALSE, '2', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43', FALSE, '2', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1', FALSE, '2', true, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41', FALSE, '2', true, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '2', true, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '2', true, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=2 and role_position='DealerFe');--DealerFe

--menu_type:3 设备月报
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '3', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3', FALSE, '3', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43', FALSE, '3', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1', FALSE, '3', true, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41', FALSE, '3', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '3', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '3', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=3 and role_position='DealerFe');--DealerFe

--menu_type:4 关爱报告
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '4', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3', FALSE, '4', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43', FALSE, '4', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1', FALSE, '4', true, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41', FALSE, '4', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '4', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9', FALSE, '4', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=4 and role_position='DealerFe');--DealerFe

--menu_type:5 监控报警
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, true, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', false, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', false, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', false, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', false, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', false, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, true, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, true, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '5', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, true, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '5', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '5', true, true, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '5', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '5', true, true, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '5', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '5', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '5', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=5 and role_position='DealerFe');--DealerFe

--menu_type:6 简报消息
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '6', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '6', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '6', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '6', true, false, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '6', true, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '6', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '6', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '6', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=6 and role_position='DealerFe');--DealerFe

--menu_type:7 维修状态
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '7', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, true, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '7', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '7', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '7', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '7', false, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '7', true, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '7', true, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '7', true, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=7 and role_position='DealerFe');--DealerFe

--menu_type:8 维修报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, true, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, true, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, true, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, true, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '8', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '8', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '8', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '8', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '8', false, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '8', false, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '8', true, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '8', true, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=8 and role_position='DealerFe');--DealerFe

--menu_type:9 超声应用指导
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', false, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', false, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', false, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', false, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '9', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '9', true, true, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '9', true, true, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '9', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '9', true, true, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '9', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '9', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '9', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=9 and role_position='DealerFe');--DealerFe

--menu_type:10 增值服务
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '10', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '10', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '10', true, true, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '10', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '10', true, true, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '10', true, true, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '10', true, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '10', true, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=10 and role_position='DealerFe');--DealerFe

--menu_type:11 应用培训工单
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '11', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, true, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '11', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '11', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '11', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '11', false, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '11', false, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '11', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '11', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=11 and role_position='DealerFe');--DealerFe

--menu_type:12 应用培训报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'radiologyDirector'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'radiologyTechnicianLead'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'radiologyTechnician'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'radiologyOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'ultrasoundDirector'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'ultrasoundDoctor'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'ultrasoundTechnician'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'ultrasoundNurse'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'ultrasoundOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, true, 'dean'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'headNurse'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'otherDirector'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='otherDirector');--其他科室主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8', FALSE, '12', true, false, 'other'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='other');--其他
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, true, 'deviceDirector'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'hospitalTechnician'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '8',FALSE, '12', true, false, 'deviceOtherStaff'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '3',FALSE, '12', true, false, 'Sales'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '43',FALSE, '12', true, true, 'SalesManager'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '1',FALSE, '12', false, false, 'FE'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '41',FALSE, '12', false, false, 'FeManager'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '12', false, false, 'DealerSales'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
select '9',FALSE, '12', false, false, 'DealerFe'
where not exists (select 1 from user_subscription_push where menu_type=12 and role_position='DealerFe');--DealerFe
-- 2020-12-10 end


-- 先删除，再插入
delete from role_msg_rel ;
delete from sys_role where name = 'DataMaintainStaff';
-- 新增一个 数据维护人员 角色
insert into sys_role(id, home_page, name, role_desc, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en)
values ( (select max(id)+1 from sys_role) , '/homeHead.xhtml', 'DataMaintainStaff', '数据维护人员',  NULL, NULL, NULL, NULL, 'zkbBasicInfo,zkbBasicInfo.engineerMgt,config,config.accountdash,config.org,config.user,config.customRole', 'Data Maintain Staff');

--DataMaintainStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AconnectPerformanceExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AppTrainingExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ColdHeaderExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','EEMonitorCSV',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','OLERulesExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMResultExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMScheduleExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrResetReport',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UpTimeExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UsConnectStatusExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ZkbAssetTransferExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrReportPdf',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetSalesExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CarePackageExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CoilOpportunityExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','MSARecommendExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','PromotionActivityExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','TrainingQuotaExcel',(select id from sys_role where name = 'DataMaintainStaff'),'DataMaintainStaff',0);

--GEInternalStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','DealerFEAndSalesExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAccountSendMailExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetCustomerExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetFeExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetOLEExcel',(select id from sys_role where name = 'GEInternalStaff'),'GEInternalStaff',0);

-- geop
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','AssetInfoImportExcel','GeOp',0);
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','GEIBTableExcel','GeOp',0);


--处理基础互通错误数据
update org_info set short_name = name,short_name_py_code=name_py_code,hospital_uid=uid,site_uid=uid,site_id=46,parent_id=hospital_id where institution_uid=(select uid from org_info where name = '基础互通' and org_type = 1) and org_type = 2 and site_id!=46;
