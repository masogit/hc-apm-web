-- 2020-12-10 微信消息推送角色订阅配置 start
--menu_type:1,保养报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, true, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, true, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, true, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, true, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '1', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3', FALSE, '1', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43', FALSE, '1', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1', FALSE, '1', false, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41', FALSE, '1', false, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '1', true, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '1', true, false, 'DealerFe');--DealerFe

--menu_type:2 保养微报上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '2', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3', FALSE, '2', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43', FALSE, '2', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1', FALSE, '2', true, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41', FALSE, '2', true, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '2', true, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '2', true, false, 'DealerFe');--DealerFe

--menu_type:3 设备月报
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '3', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3', FALSE, '3', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43', FALSE, '3', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1', FALSE, '3', true, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41', FALSE, '3', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '3', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '3', false, false, 'DealerFe');--DealerFe

--menu_type:4 关爱报告
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8', FALSE, '4', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3', FALSE, '4', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43', FALSE, '4', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1', FALSE, '4', true, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41', FALSE, '4', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '4', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9', FALSE, '4', false, false, 'DealerFe');--DealerFe

--menu_type:5 监控报警
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, true, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', false, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', false, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', false, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', false, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', false, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, true, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, true, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, true, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '5', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '5', true, true, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '5', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '5', true, true, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '5', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '5', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '5', false, false, 'DealerFe');--DealerFe

--menu_type:6 简报消息
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '6', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '6', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '6', true, false, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '6', true, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '6', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '6', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '6', false, false, 'DealerFe');--DealerFe

--menu_type:7 维修状态
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, true, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '7', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '7', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '7', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '7', false, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '7', true, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '7', true, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '7', true, false, 'DealerFe');--DealerFe

--menu_type:8 维修报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, true, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, true, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, true, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, true, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '8', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '8', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '8', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '8', false, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '8', false, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '8', true, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '8', true, false, 'DealerFe');--DealerFe

--menu_type:9 超声应用指导
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', false, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', false, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', false, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', false, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '9', true, true, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '9', true, true, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '9', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '9', true, true, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '9', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '9', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '9', false, false, 'DealerFe');--DealerFe

--menu_type:10 增值服务
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '10', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '10', true, true, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '10', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '10', true, true, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '10', true, true, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '10', true, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '10', true, false, 'DealerFe');--DealerFe

--menu_type:11 应用培训工单
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, true, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '11', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '11', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '11', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '11', false, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '11', false, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '11', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '11', false, false, 'DealerFe');--DealerFe

--menu_type:12 应用培训报告上传
 --放射科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'radiologyDirector');--放射科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'radiologyTechnicianLead');--放射科技师长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'radiologyTechnician');--放射科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'radiologyOtherStaff');--放射科其他人员
 --超声科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'ultrasoundDirector');--超声科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'ultrasoundDoctor');--超声科医师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'ultrasoundTechnician');--超声科技师
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'ultrasoundNurse');--超声科护士
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'ultrasoundOtherStaff');--超声科其他人员
 --
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, true, 'dean');--院长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'headNurse');--护士长
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'otherDirector');--其他科室主任
 --设备科
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, true, 'deviceDirector');--设备科主任
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'hospitalTechnician');--设备科医工
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('8',FALSE, '12', true, false, 'deviceOtherStaff');--设备科其他人员
 --roles
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('3',FALSE, '12', true, false, 'Sales');--Sales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('43',FALSE, '12', true, true, 'SalesManager');--Sales Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('1',FALSE, '12', false, false, 'FE');--FE
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('41',FALSE, '12', false, false, 'FeManager');--FE Manager
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '12', false, false, 'DealerSales');--DealerSales
INSERT INTO user_subscription_push (ge_user_type,is_deleted,menu_type,send_status,sub_status,role_position)
VALUES ('9',FALSE, '12', false, false, 'DealerFe');--DealerFe
-- 2020-12-10 end

insert into sys_role(id, home_page, name, role_desc, authorities, authorities_ch, cards, cards_ch, menus, role_desc_en)
values ( (select max(id)+1 from sys_role) , '/homeHead.xhtml', 'DataMaintainStaff', '数据维护人员',  NULL, NULL, NULL, NULL, 'zkbBasicInfo,zkbBasicInfo.engineerMgt,config,config.accountdash,config.org,config.user,config.customRole', 'Data Maintain Staff');

--DataMaintainStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AconnectPerformanceExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','AppTrainingExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ColdHeaderExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','EEMonitorCSV',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','OLERulesExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMResultExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','PMScheduleExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','SrResetReport',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UpTimeExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UsConnectStatusExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','ZkbAssetTransferExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CarePackageExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','CoilOpportunityExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','MSARecommendExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','PromotionActivityExcel',(select max(id) from sys_role),'DataMaintainStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('gamePlanBatchFileType','TrainingQuotaExcel',(select max(id) from sys_role),'DataMaintainStaff',0);

--GEInternalStaff
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','DealerFEAndSalesExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAccountSendMailExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetCustomerExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetFeExcel',45,'GEInternalStaff',0);
insert into role_msg_rel(msg_type, msg_key, role_id, role_name,role_type) values ('batchFileType','UserAssetOLEExcel',45,'GEInternalStaff',0);

-- geop
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','AssetInfoImportExcel','GeOp',0);
insert into role_msg_rel(msg_type, msg_key, role_name,role_type) values ('batchFileType','GEIBTableExcel','GeOp',0);

--处理基础互通错误数据
update org_info set short_name = name,short_name_py_code=name_py_code,hospital_uid=uid,site_uid=uid,site_id=46,parent_id=hospital_id where institution_uid=(select uid from org_info where name = '基础互通' and org_type = 1) and org_type = 2 and site_id!=46;

-- YHAAO-9906
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '49', '无创自动测量血压计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '50', '无创非自动测量血压计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '51', '眼压计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '52', '纯音听力计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '53', '阻抗听力计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '54', '焦度计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '55', '角膜曲率计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '56', '放射治疗用电离室剂量计', '', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '57', '医用诊断X射线设备', '', NULL, '-1');

update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '医用CT';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '普通X光机';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '磁共振类';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = 'B超';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = 'X线胃肠';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '医用DSA';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '乳腺X光机';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '医用直线加速器';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '射线骨密度仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '超声多普勒胎儿监护仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '超声多普勒胎心仪';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '血压计（表）';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '电子血压计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '心电图机';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '脑电图仪';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '听力计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '验光仪';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '验光镜片组';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '氧气吸入器';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '酸度计';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '分光光度计';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '天平';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '电子体重秤';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '指针式机械体重秤';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '温湿度计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '体温计';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '电导率仪';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '剂量仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '活度计';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '标准器';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '测试仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '移液器';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '婴儿培养箱';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '普通压力表';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '精密压力表';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '安全阀';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '压力容器';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '高压氧舱';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '血压计（电子）';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '血压计（水银）';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '流量计';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '超声诊断设备';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '数字心电图';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '放射类';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '监护仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '呼吸机';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '除颤仪';
update i18n_message set value_zh =  value_zh || '(非强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '生物安全柜';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '无创自动测量血压计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '无创非自动测量血压计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '眼压计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '纯音听力计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '阻抗听力计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '焦度计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '角膜曲率计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '放射治疗用电离室剂量计';
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '医用诊断X射线设备';
