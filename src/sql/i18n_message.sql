delete from i18n_message where site_id=-1 and msg_type!='modalityType';

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','cardsIsBlank','盘点卡片不能为空!','Cards cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RelMeasureRule','配置计量规则','Configure Measurement Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','parts','备件管理','Parts Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','sparePartsManagement','简易备件管理','SpareParts Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.scrapTempMgt','报损模板管理','Scrap Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.create','新增资产建档','Build New Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.audit','资产审核','Asset Approval',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust','设备调剂','Equipment Swap',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.borrowApply','借用申请','Request for Borrow',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.borrowAccept','借用受理','Borrow Acceptance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.returnApply','归还申请','Request for Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.returnAccept','归还受理','Return Acceptance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.batch','设备批量调剂','Batch Equipment Swap',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','audit','质控评审','audit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','review','质控评审','audit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MeasureOrderRule','计量规则','Measurement Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MeasureRuleManager','计量规则管理','Measurement Rule Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ValidationRequire','输入不可为空！','Input cannot be empty!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','source_not_supported','输入链接不支持，请重新输入！','Input link is not supported, please re-enter!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','session_expired','您的会话已经超时，请重新登录.','Your session has timed out, please log in again.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PersistenceErrorOccured','保存数据出错.','Data Save Error',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PersistenceErrorDuplicateKey','输入的数据与已有的记录重复.','The entered data is duplicated with the existing record.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AdvancedSearch','高级查询','Advanced Search',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Add','添加','Add',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','View','查看','View',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Edit','修改','Edit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Delete','删除','Delete',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Close','关闭','Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Return','返回','Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Save','保存','Save',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Submit','提交','Submit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Confirm','确认','Confirm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Refresh','刷新','Refresh',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Locked','锁定','Lock',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Successfully','成功','Successfully',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Failed','失败','Failed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssignRoles','设置角色权限','Set Role Permissions',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SelectOneMessage','请选择...','Please select...',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NewPassword','请输入新密码','Please enter a new password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfirmNewPassword','请再次输入新密码','Please enter your new password again',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deleteConfirmation','您确信要删除此条数据么？','Are you sure you want to delete this data?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Created','已经成功地被创建。','Successfully Created',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Updated','已经成功地被修改。','Successfully Updated',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Deleted','已经成功地被删除。','Successfully Deleted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','noRecordFound','(没有查询到数据)','(No Data Found)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','recordCount','总行数','Row Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','True2Yes','是','Yes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','true','是','Yes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','False2No','否','No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','false','否','No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','passwordIncorrect','对不起，您输入的密码不正确','Sorry, the password you entered is incorrect.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','login_error','用户名或密码错误','User Name or Password Error',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','security_error','用户名或密码错误','User Name or Password Error',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','security_bad_credentials','用户名或密码错误','User Name or Password Error',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RowsPerPage','每页行数','Rows/Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssignRole','角色设置','Assign Role',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChangePassword','修改密码','Change Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','exportToXLS','导出到Excel','Export to Excel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'exportToDoc', '导出到Word', 'Export To Word', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','List','列表','List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Download','下载','Download',NULL,-1);
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Search','查询','Search',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Reset','重置','Reset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Actions','操作','Actions',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DataSavedOK','数据已经保存成功.','Data saved successfully',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Welcome','欢迎您','Welcome',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DuplicateLoginName','登录用户名 %s 已有人使用，请更换登录用户名.','Login user name %s has been used, please change the login user name.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchImport','批量导入系统配置','Batch Import System Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ClearAll','清空自定义配置','Clear All Custom Configurations',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http500ErrorTitle','系统发生异常','Exception Occurred',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http500ErrorMsg','请联系系统管理员.','Please contact system administrator.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http404NotFoundTitle','网页不存在','Page Not Found',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ResetPassword','重置密码','Reset Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.scrap','报废资产','Terminated Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','GenerateQRCodes','生成二维码','Generate QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QRCodeRefresh','换一批','Refresh QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','GetQRCode','扫码成功','Got QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','FoundAsset','找到设备','Found Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NotFoundAsset','未找到设备','Not Found Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BindAsset','绑定已有资产','Bind Existing Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PhotoList','照片列表','Photo List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ScanCode','扫码','Scan Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PictureUpload','图片上传','Upload Pictures',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ContinueAdd','继续添加','Add Next One',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Bind','绑定','Binding',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','OldPassword','旧密码','Old Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PleaseInput','请输入','Please Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NewPass','新密码','New Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfirmPass','确认密码','Confirm Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QrCodeLib','二维码创建资产','QR Code Create Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','issueDate','发行日期','Issue Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','submitDate','提交日期','Submit Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','submitWechatId','提交人','Submitted by',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AccountLocked','账号被锁定，请24小时后再试!','The account is locked. Please try it again after 24 hours!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SamePwd','新老密码不能相同!','The new password cannot be the same as the old one!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','lockAccount','锁定账号','Lock Account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','lockAccountConfirm','您确定要锁定此账号么?','Are you sure you want to lock this account?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','unlockAccount','解锁账号','Unlock Account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','unlockAccountConfirm','您确定要解锁此账号么?','Are you sure you want to unlock this account?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AccountNotActivated','对不起，此账号已经被冻结.','Sorry, this account is not activated.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HomePageHospitalHead','我的主页(院长)','Home Page (Hospital Head)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HomePageDeptHead','我的主页(主任)','Home Page (Department Head)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HomePageAssetHead','我的主页(设备科)','Home Page (Equipment Department)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset','设备档案','Equipment Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.achievements','设备绩效','asset achievements',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetMDM','设备档案','Equipment Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetDocumentMgmt','电子档案','Electronic Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetContractMgmt','合同管理','Contract Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.mgt','合同管理','Contract Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SupplierMgmt','供应商管理','Supplier Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.supplierMgt','供应商管理','Supplier Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.supplierEvaluation','供应商评价','Supplier evaluation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SupplierEvaluation','供应商评价','Supplier evaluation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SupplierName','供应商名称','Supplier name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SupplierRealName','供应商名称','Supplier real name',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetValueAnalysis','设备价值统计','Equipment Value Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ServiceRequestMgmt','报修管理','Request Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MyWorkOrder','报修处理','Request Acceptance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MaintainanceRecord','维修记录','Repair Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ServiceQualityReview','维修质量评估','Repair Quality Evaluation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Inspection','巡检管理','Inspection Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMonitor','设备监控','Equipment Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceDowntimeAnalysis','停机率分析','Downtime Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMaintainanceEventAnalysis','维修事件统计','Repair Event Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMaintainanceCostAnalysis','维护成本统计','Repair Cost Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceUtilizationAnalysis','使用情况统计','Utilization Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SparePartsConsumptionAnalysis','备件消耗统计','Spare Part Consumption Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceFailureAnalysis','故障分类统计','Failure Classification Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceOperationMonitor','运营监控','Operation Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PreventiveMaintainceMgmt','预防维护','Preventive Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain','预防维护','Preventive Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PreventiveMaintaincePlanning','保养统计','Preventive Maintenance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMetrologyMgmt','计量管理','Metering Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','measure','计量管理','Metering Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceQualityControl','质控管理','Quality Control Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceAdverseEventMgmt','不良事件上报','Adverse Event Reporting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMedicalAccidentMgmt','医疗事故风险监控','Medical Accident Risk Monitoring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetKMMgmt','知识库管理','Knowledge Base Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceMgmtForum','管理论坛','Management Forum',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceApplication','应用/维修培训管理','Application/Maintenance Training Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceUpgradMgmt','升级管理','Upgrade Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AdvancedDeviceInfoTracking','扫描详情统计','Detailed Utilization Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceROIAnalysis','平均投资回报分析','Average ROI Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceKPIForecast','绩效预测','Performance Forecast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceCostAnalysis','成本统计分析','Statistical Cost Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DevicePurchaseDecisionSupport','辅助决策分析','Decision Support Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SiteAdmin','租户管理','Tenant Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InspectionChecklist','巡检配置','Inspection Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InspectionOrder','巡检计划','Inspection Plan',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InspectionRecord','巡检记录','Inspection Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChecklistType','检查类型','Check Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Setting','系统设置','Setting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.info','基本信息','Basic Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetList','基本信息','Basic Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BasicInfo','基本信息','Basic Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UsageAmount','使用量','Usage Amount',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChecklistAdmin','巡检/计量条目维护','Inspection/Measurement Entry Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkListSetting','检查项配置','Check Item Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkListItem','检查项','Check Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetStatusTimeChange','资产状态与时间变更','Asset Status & Time Change',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetContractAmount','合同总额(元)','Contract Amount (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetContractAttachment','合同附件','Contract Attachment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetContractName','合同名称','Contract Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SysCodeConfig','系统编码配置','System Code Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ItemConfig','配置','Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SettingOwner','设置','Setting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AvailableAssets','备选设备','Available Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChoseAssets','选中设备','Selected Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChoosingAssets','选择设备列表','Equipment Select List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','FilterCondition','过滤条件','Filter Condition',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetBrowser','设备分布分析','Equipment Distribution Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.analysis','设备分布分析','Equipment Distribution Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UsageAndClinicalPerformance','运用与临床绩效','Application and Clinical Performance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','FinancialPerformanceAndForecast','财务绩效与预测','Financial Performance and Forecast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Size','尺寸','Size',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','setToUsing','设置为使用','Set to Use',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','modify','编辑','Edit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preview','预览','Preview',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','template','模版','Template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','width','宽','Width',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','height','高','Height',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','positionX','横向位置','Horizontal Position',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','positionY','纵向位置','Vertical Position',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','font','字体','Font',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Text','文本','Text',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','field','字段','Field',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Color','颜色','Color',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','type','类型','Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceStatus','资产状态','Asset Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','exportQrCode','导出二维码图片','Export QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','toBeTerminatedAppraisal','报损鉴定表导出','Scrap Appraisal Export',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','terminationCause','报废原因','Termination Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SiteInfo','医联体/租户信息','Site/Tenant Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TenantInfo','租户','Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','OrgInfo','组织机构信息','Organization Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UserAccount','用户账号信息','User Account Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetInfo','资产信息','Asset Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrder','维修工单','Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderStep','工单步骤','Workorder Step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderStepDetail','工单详情','Workorder Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetClinicalRecord','检查记录','Inspection Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InspectionOrderDetail','巡检工单详情','Inspection Workorder Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Supplier','供应商信息','Supplier Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InspectionWorkOrder','巡检工单','Inspection Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MetrologWorkOrder','计量工单','Measurement Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QualityCtrlWorkOrder','质控工单','Quality Control Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetInventory','设备盘点','Equipment Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceCheck','设备盘点','Equipment Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MetrologyOrderDetail','计量工单详情','Measurement Workorder Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QualityCtrlOrderDetail','质控工单详情','Quality Control Workorder Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QualityCtrlMonitor','质量监控','Quality Control Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MetrologyMonitor','计量监控','Mearsurement Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','saveOrder','保存顺序','Save Order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','I18nMessage','编码类型配置','Encoding Type Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MM3Import','冷头报警数据导入','Cold Head Alarm Data Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','equipmentTaker','取件人','Equipment Taker',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','feedback','评价','Comment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Renew','更新','Update',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','autoAck','工单无需临床验收?','autoAck?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','confirmedDownTime','停机时间','Down Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','dispatchMgmt','派工管理','Dispatch Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','dispatchWorkOrder','派工','Dispatch Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderAssignee','维修人员','Assignee',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AckNoPass','验收不通过','Acknowledge Not Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Acknowledge','验收','Acknowledge',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','FaultyDesc','故障文字描述','Faulty Text Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','newWorkOrder','新建工单','New Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','takeOrtrans','接单/转单','Take/Transfer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','takeorder','接单','Take',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','workcomplete','维修完成','Repair Complete',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','closeWo','关单','Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','expensedetail','费用明细','Expense Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','recoveryTime','恢复时间','Recovery Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','estimatetime','预估时间','Estimate Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairType','维修类别','Repair Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','coworker','协作者','Coworker',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','suscriber','关注者','Subscriber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListMgmt','到货清单','Arrival List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ArrivalList','到货清单','Arrival List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','install.list','到货清单','Arrival List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListItemMgmt','安装入库','Install and Store',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','install.store','安装入库','Install and Store',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetFinishInstallMgmt','完成安装','Finish Install',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListInfo','到货单信息','Arrival List Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListAssetInfo','设备信息','Equipment Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListAssetInfoInput','设备档案预录','Equipment Pre-record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListAckInfo','验收信息','Acknowledgment Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListBusinessAck','商务验收','Business Acknowledgment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListClinicalAck','临床验收','Clinical Acknowledge',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListTechnicalAck','技术验收','Technical Acknowledgment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListBusinessAckDate','商务验收日期','Business Acknowledgment Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListClinicalAckDate','临床验收日期','Clinical Acknowledge Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListTechnicalAckDate','技术验收日期','Technical Acknowledgment Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListPic','相关图片','Pictures',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListInstallInfo','安装信息','Install Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListInstallAckReject','验收不合格','Acknowledge Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MeasuringTypeConfig','计量类型','Measurement Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NextMeteringDateBeforeLastMeteringDateError','下次计量日期不能早于最后计量日期','The Next Measurement Date can not earlier than the Last Measurement Date.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ManualAndGrapMsg','允许抢单和允许人工至少选一个!','There should be at least one configuration for manual or grap mode!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','accessoryPrice','单价(元)','Unit Price (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','address','地址','Address',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','aliasName','设备别名','Equipment Alias',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','functionType','设备型号','Equipment Model',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetId','设备ID','Equipment ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerId','维修负责人','Repair Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerName','维修负责人','Repair Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerTel','维修负责人电话','Repair Owner Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','attachmentUrl','上传附件','Attachment Upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','barcode','条码','Bar Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','caseOwnerId','负责人','Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','caseOwnerName','负责人','Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','casePriority','紧急度','Priority',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','caseSubType','故障子类别','Fault Subtype',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','caseType','故障类别','Fault Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','city','城市','City',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','transfer','转单','Transfer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','clinicalDeptId','所属科室','Clinical Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','clinicalDeptName','所属科室','Clinical Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','closeReason','关单原因','Close Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','confirmedEndTime','确认的恢复可用时间','Confirmed Availability Recovery Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','confirmedStartTime','确认的停机时间','Confirmed Down Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contactor','供应商联系人姓名','Supplier Contact Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','createTime','创建时间','Create Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','nextStep','下一步','Next Step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','creatorName','创建者','Creator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','taskOwner','处理人','Task Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','currentStep','当前处理步骤','Current Step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','departNum','设备编号（院方）','Equipment ID (Hospital)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','depreciationMethod','折旧算法','Depreciation Method',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','deptId','巡检科室','Inspection Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','deptName','巡检科室','Inspection Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','description','处理描述','Processing Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','examDate','检查日期','Check Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','endTime','结束时间','End Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','examEndTime','结束时间','End Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','fileUrl','文件路径','File Path',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','filmCount','胶片量','Film Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','financingNum','资产编号','Asset ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','functionGrade','功能等级','Function Grade',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetGroup','设备分类','Equipment Classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','riskAssessment','风险评分','Risk Assessment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','detectionCycle','检测周期','Detection Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','maintenancePeriod','保养周期','Maintenance Period',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetGroupSecondary','设备分类2级','Equipment Classification=Class II',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','hospitalId','所属院区','Site',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','injectCount','注射量','Inject Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','startUsingDate','启用日期','Start Using Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','installDate','启用日期','Start Using Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isClosed','已关单？','Is Closed?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isFinished','完成？','Is Finished?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','intExtType','工单类型','Workorder Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isPassed','通过?','Is Passed?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isValid','是否在用','Is Valid?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','item','巡检项目','Inspection Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','itemId','巡检项目','Inspection Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','itemName','巡检项目','Inspection Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lifecycle','使用年限','Life Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','locationCode','安装位置编码','Installation Location Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetValueCategory','资产价值分类','Asset Value Category',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','locationName','安装位置','Installation Location',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','warrantyStatus','维保状态','Warranty Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','locationNotes','位置备注1(科室)','Installation Location Notes',NULL,-1);
--     INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','maitananceTel','维修商电话','Maintainer Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','maitananceTel','维修商联系方式','Maintainer Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','manufactDate','出厂日期','Manufacture Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','modalityId','院内IT系统编号','Hospital-based IT Modality ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','nextTime','下次计划时间','Next Plan Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','ownerId','执行人','Executor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','ownerName','责任人','Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','ownerOrgId','执行人所属部门','Executor Organization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','ownerOrgName','执行人所属部门','Executor Organization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Attachment','附件','Attachment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','paperUrl','附件','Attachment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','patientAge','年龄','Age',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','patientGender','性别','Sex',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','patientId','患者信息','Patient ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','planDate','计划内最近一次维护日期','Plan’s Last PM Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','priceAmount','价格','Price',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','priceUnit','单位','Unit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','procedureId','检查部位','Exam Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','procedureName','检查部位','Exam Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','procedureStepId','检查步骤','Exam Step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','procedureStepName','检查步骤','Exam Step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','purchaseDate','采购日期','Purchase Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','purchasePrice','采购价格(元)','Purchase Price (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','reportUrl','维护报告','PM Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','requestReason','故障现象','Fault Phenomenon',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','requestTime','报修时间','Request Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','requestorId','报修人','Requestor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','requestorName','报修人','Requestor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','requireName','维修人','Require',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','salvageValue','最终残值(元)','Final Salvage Value (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','serialNum','制造商序列号','Manufacturer Serial Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','examStartTime','开始时间','Start Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','startTime','开始时间','Start Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','status','当前状态','Current Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','stepName','步骤名称','Step Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','terminateDate','报废日期','Terminate Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','totalManHour','总计人工(小时)','Total Manhours (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','totalPrice','总计费用(元)','Total Price (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','updateDetail','变更内容描述','Update Detail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','updatePersonId','变更人','Update Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','updatePersonName','变更人','Update Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','updateTime','处理时间','Update Time',NULL,-1);
--     INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','vendorTel','供应商电话','Vendor Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','vendorTel','供应商联系方式','Vendor Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','warrantyDate','维保截止日期','Warranty Expiration Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','zipcode','邮编','Zip Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contactPerson','联系人','Contact Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contactPhone','联系电话','Contact Phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','defaultLang','默认语言','Default Language',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contactEmail','Email','Email',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','email','Email','Email',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','homePage','我的主页','Home Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','homePage','我的主页','Home Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','home','我的主页','Home Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HomePage','我的主页','Home Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isActive','激活?','Is Active?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isEnabled','启用?','Is Enabled?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isLocalAdmin','本院管理员','Is Local Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isOnline','在线?','Is Online?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isSiteAdmin','Site管理员?','Is Site Administrator?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isSuperAdmin','超级管理员?','Is Super Administrator?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lastLoginTime','上次登录时间','Last Login Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','location','地点','Location En',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','locationEn','地点','Location En',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','nameEn','名称(En)','Name En',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','hospitalUcmid','医院UCMID','Hospital Ucmid',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','orgId','所属部门','Organization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','parentId','上级部门','Parent Organization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','roleDesc','角色描述','Role Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','roleId','角色','Role',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','tel','电话','Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','telephone','电话','Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','timeZone','时区','Time Zone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','siteDescription','详细描述','Detailed Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','patientNameEn','姓名','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','patientNameZh','姓名','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','userName','姓名','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','workOrderName','工单名称','Workorder Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lastPmDate','上次保养日期','Last PM Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lastQaDate','上次巡检日期','Last Inspection Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lastMeteringDate','上次计量日期','Last Measurement Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','period','循环周期','Period',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','wfAutoStep2','自动审核','Auto Approve',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','wfAutoStep4','自动接单','Auto Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','wfAutoStep5','自动维修','Auto Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','wfAutoStep6','自动关单','Auto Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PmRecord','保养记录','PM Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','manHours','内部工时(小时)','Internal Manhours (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ActiveAsset','重新启用','Re-activate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InactiveAsset','转入报废','Transfer to “inactive”',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PreInactiveAsset','转入待报废','Transfer to “to be terminated”',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BuildAsset','资产建档','Build Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PwdTips','密码必须6位以上，首字母大写,尾字母小写,必须包含数字,不可使用3个以上连续的数字或字母,不可使用3个以上相同的数字或字母,必须包含一个特殊字符(!@#$%&)','Password must be more than 6 digits with the uppercase first letter and the lowercase last letter, and contain numbers with no more than 3 consecutive numbers or letters or more than 3 identical numbers or letters. It must contain a special character (!@#$%&)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceType','资产类型','Asset Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','V2_WorkFlowConfig','资产类型工作流配置','Asset Type Work Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UrgentTimeout','紧急工单超时时间','Urgent Workorder Timeout Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UrgentMaxMessageCount','紧急工单超时提醒次数','Urgent Workorder Maximum Message Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceTypeGroup','设备类型分组配置','Equipment Type Group Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','V3_WorkFlowConfig','院区工作流配置','Site Workorder Work Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderWorkflowConfig','紧急工单工作流配置','Urgent Workorder Work Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','V2_WorkOrderWorkflowConfig','紧急工单工作流配置','Urgent Workorder Work Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','brand','品牌','Brand',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','nationalStandardCode','设备国标码','National Standard Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','srNum','报修单号','Request Workorder Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetUsage','用途分类','Usage Classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','capitalSource','资金来源','Capital Source',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','riskGrade','风险等级','Risk Grade',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','riskGradeNew','风险等级','Risk Grade',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measureGrade','计量等级','Measurement Grade',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','emergencyEquipment','是否急救设备','Emergency Equipment?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','importEquipment','是否进口设备','Import Equipment?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measuringItemCategory','计量项目类别','Measurement Item Category',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measuringStatus','计量状态','Measurement Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','accountingCategory','核算类别','Accounting Category',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','maintenanceValue','维修价值','Maintenance Value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','proprietaryClass','专有分类','Proprietary Class',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','creatorUID','创建人','Created by',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arriveDate','到货日期','Arrival Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalDate','到货日期','Arrival Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','accessoryQuantity','数量','Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','quantity','数量','Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListSiteUID','到货院区','Arrival Site',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListName','到货单名称/编号','Arrival List Name/Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListOrderType','到货单类型','Arrival List Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListManufacture','生产厂家','Manufacturer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','brandManufacturerShortName','品牌/制造商简称','Brand Manufacturer Short Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListOrigin','产地','Origin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contractNo','合同号','Contract Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListContractNum','合同号','Contract Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListContractPrice','合同价格','Contract Price',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListAgent','经办人','Agent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListClassCode','设备分类编码','Equipment Classification Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListItemNum','编号','ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','currentPersonName','当前处理人','Current Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','currentPersonUID','当前处理人','Current Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalInstallation','到货安装','Arrival Installation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','install','到货安装','Arrival Installation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','ArrivalProcessType','安装流程类型','Installation Process Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','7','反馈','Feedback',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetStatus','3','限制功能使用','Partial Function',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetStatus','4','待报废','To be terminated',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetStatus','5','停用','Disuse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('casePriority','1','紧急','Urgent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('casePriority','2','重要','Important',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('casePriority','3','一般','General',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('region','1','大陆','Mainland',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('region','2','香港','Hong Kong',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('region','3','台湾','Taiwan',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','1','照片','Photo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','2','用户手册','User Manual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','3','培训资料','Training Material',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','Measuring','计量文档','Measurement Document',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','vlink','视频链接','Video Link',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('deviceStatus','1','备用','Standby',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('caseSubType','1','故障子类型1','Fault Subtype 1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('caseSubType','2','故障子类型2','Fault Subtype 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('caseSubType','3','故障子类型3','Fault Subtype 3',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('caseSubType','4','故障子类型4','Fault Subtype 4',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('caseSubType','5','故障子类型5','Fault Subtype 5',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionGrade','1','等级1','Grade 1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionGrade','2','等级2','Grade 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionGrade','3','等级3','Grade 3',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ownAssetGroup','1','所属设备类型1','Equipment Type 1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ownAssetGroup','2','所属设备类型2','Equipment Type 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ownAssetGroup','3','所属设备类型3','Equipment Type 3',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ownAssetGroup','4','所属设备类型4','Equipment Type 4',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ownAssetGroup','5','所属设备类型5','Equipment Type 5',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','1','每日','Daily',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','2','每周','Weekly',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','3','每两周','Biweekly',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','4','每月','Monthly',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','5','每季度','Quarterly',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','6','每半年','Semiannual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inspectionPeriod','7','每年','Annual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('depreciationMethodList','1','平均年限','Straight-Line',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('depreciationMethodList','2','加速双倍余额','Acceleration Double Balance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('depreciationMethodList','3','加速年限','Acceleration Line',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetInfoDetail','设备详细信息','Equipment Details',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetInventoryUnmatchedList','与实物不符的设备列表','Equipment Inventory Unmatched List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetInventoryDate','设备盘点日期','Equipment Inventory Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DefaultHospitalName','总部/院区','Head Office/Site',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ForeighKeyErrorWithSiteId','该租户不可删除: 请先删除其下面的组织机构信息','This tenant cannot be deleted: Please delete the organization information below it first.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ForeighKeyErrorWithOrgId','该组织机构不可删除: 请先删除其下面的组织机构或用户信息','This organization cannot be deleted: Please delete the organization/user information below it first.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TerminateAssetInfo','报废资产信息','Terminated Asset Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TerminateAssetInfoDetail','报废资产信息','Terminated Asset Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qrCodeLibStatus','4','已作废','Discarded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qrCodeLibStatus','5','已使用','Used',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qrCodeLibStatus','1','已发行','Issued',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qrCodeLibStatus','2','已上传','Uploaded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('status','1','在修','Fixing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Finish','完成','Finish',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('status','2','完成','Finish',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Cancel','取消','Cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('status','3','取消','Cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('intExtType','1','内部','Internal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('intExtType','2','外部','External',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('intExtType','3','混合','Mixed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairType','2','送到维修中心','To The Service Center',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairType','3','返厂维修','Return Factory For Repair',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','exceptionReason','异常原因','Exception Reason',NULL,-1);


--县域新增维修类型“远程修复”
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairTypeCountyArea','1','上门维修','On-site Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairTypeCountyArea','2','送到维修中心','To The Service Center',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairTypeCountyArea','3','返厂维修','Return Factory For Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairTypeCountyArea','4','远程修复','Remote Repair',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','1','压缩机状态报警(常开报警)','Compressor Status Alarm (Normally Open)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','2','压缩机报警状态报警(常开报警)','Compressor Alarm Status Alarm (Normally Open)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','3','水浸状态报警(常闭报警)','Flooding Status Alarm (Normally Closed)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','101','水冷温度报警','Water Cooling Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','102','机房湿度报警','Room Humidity Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','103','机房温度报警','Room Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','4','压缩机状态报警(常闭报警)','Compressor Status Alarm (Normally Closed)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','105','流量报警','Flow Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','5','压缩机报警状态报警(常闭报警)','Compressor Alarm Status Alarm (Normally Closed)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','104','水冷机冷却液温度报警','Water Cooler Coolant Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','6','压缩机状态 alarm(Normally open alarm)','Compressor Status Alarm (Normally Open)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','7','水冷机电流监测报警(常开报警)','Water Cooler Current Monitoring Alarm (Normally Open)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','8','压缩机状态 alarm(Normally close alarm)','Compressor Status Alarm (Normally Closed)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','9','水浸状态报警(常开报警)','Flooding Status Alarm (Normally Open)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','10','水冷机电流监测报警(常闭报警)','Water Cooler Current Monitoring Alarm (Normally Closed)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','106','机房湿度 alarm','Room Humidity Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','107','机房温度 alarm','Room Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','108','水冷温度 alarm','Water Cooling Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','109','水冷机冷却液温度 alarm','Water Cooler Coolant Temperature Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertType','110','流量 alarm','Flow Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','2','超声类','Ultrasonic',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','2','超声类','Ultrasonic',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','3','呼吸麻醉类','Breathing Anesthesia',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','3','呼吸麻醉类','Breathing Anesthesia',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','4','手术室类','Operating Room',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','4','手术室类','Operating Room',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','5','消毒杀菌类','Sterilization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','5','消毒杀菌类','Sterilization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','6','输液注射类','Infusion & Injection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','6','输液注射类','Infusion & Injection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','7','除颤起搏类','Difibrillation & Peacemaking',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','7','除颤起搏类','Difibrillation & Peacemaking',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','8','康复类','Rehabilitation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','8','康复类','Rehabilitation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','9','血液透析类','Hemodialysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','9','血液透析类','Hemodialysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','10','实验类（检验/病理）','Experimental (Test/Pathology)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','10','实验类（检验/病理）','Experimental (Test/Pathology)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','11','内窥镜类','Endoscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','11','内窥镜类','Endoscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','12','生命体征监护类','Vital Signs Monitoring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','12','生命体征监护类','Vital Signs Monitoring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetUsage','1','诊断类','Diagnosis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetUsage','2','治疗类','Therapy',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetUsage','3','辅助类','Auxiliary',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','1','科研','Research',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','2','教学','Teaching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','3','医疗','Medical',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureGrade','1','A类计量设备','Measurement Equipment of Class A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureGrade','2','B类计量设备','Measurement Equipment of Class B',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureGrade','3','C类计量设备','Measurement Equipment of Class C',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureGrade','4','非计量设备','Non-measurement Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGrade','30','一级A','Grade 1A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGrade','40','一级B','Grade 1B',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGrade','20','二级','Secondary',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGrade','10','三级','Third',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGradeNew','30','低风险','Low Risk',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGradeNew','20','中风险','Medium Risk',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('riskGradeNew','10','高风险','High Risk',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('emergencyEquipment','1','急救设备','Emergency Equipment?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('emergencyEquipment','2','非急救设备','Non-emergency Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importEquipment','1','进口','Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importEquipment','2','国产','Domestic',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','1','医用CT','Medical CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','2','普通X光机','Ordinary X-ray Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','3','磁共振类','Nuclear Magnetic Resonance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','4','B超','B-mode Ultrasound',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','5','X线胃肠','Gastrointestine X-ray',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','6','医用DSA','Medical DSA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','7','乳腺X光机','Medical Breast X-ray Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','8','医用直线加速器','Medical Linear Accelerator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','9','射线骨密度仪','Ray Bone Density Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','10','超声多普勒胎儿监护仪','Ultrasonic Doppler Fetal Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','11','超声多普勒胎心仪','Ultrasonic Doppler Fetal Heart Rate Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','12','血压计（表）','Sphygmomanometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','13','电子血压计','Electronic Sphygmomanometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','14','心电图机','ECG Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','15','脑电图仪','EEG Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','16','听力计','Audiometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','17','验光仪','Refractometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','18','验光镜片组','Optometry Lens Group',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','19','氧气吸入器','Oxygen Inhaler',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','20','酸度计','PH Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','21','分光光度计','Spectrophotometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','22','天平','Balance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','23','电子体重秤','Electronic Scale',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','24','指针式机械体重秤','Pointer Type Mechanical Weight Scale',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','25','温湿度计','Hygrothermograph',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','26','体温计','Thermometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','27','电导率仪','Conductivity Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','28','剂量仪','Dosimeter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','29','活度计','Activity Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','30','标准器','Standard',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','31','测试仪','Tester',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','32','移液器','Pipette',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','33','婴儿培养箱','Infant Incubator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','34','普通压力表','General Pressure Gauge',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','35','精密压力表','Precision Pressure Gauge',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','36','安全阀','Safety Valve',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','37','压力容器','Pressure Vessel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','38','高压氧舱','Hyperbaric Oxygen Chamber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','39','血压计（电子）','Sphygmomanometer (Electronic)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','40','血压计（水银）','Sphygmomanometer (Mercury)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','41','流量计','Flow Meter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','42','超声诊断设备','Ultrasound Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','43','数字心电图','Digital ECG',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','44','放射类','Radioactive',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','45','监护仪','Medical Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','46','呼吸机','Respirator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','47','除颤仪','Defibrillator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringItemCategory','48','生物安全柜','Biosafety Cabinet',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '49', '无创自动测量血压计', 'Non-invasive automatic measurement sphygmomanometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '50', '无创非自动测量血压计', 'Non-invasive non-automatic measuring sphygmomanometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '51', '眼压计', 'Tonometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '52', '纯音听力计', 'Pure tone audiometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '53', '阻抗听力计', 'Impedance audiometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '54', '焦度计', 'Focal meter', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '55', '角膜曲率计', 'Keratometer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '56', '放射治疗用电离室剂量计', 'Ionization Chamber Dosimeter for Radiotherapy', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('measuringItemCategory', '57', '医用诊断X射线设备', 'Medical diagnostic X-ray equipment', NULL, '-1');

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
update i18n_message set value_zh =  value_zh || '(强检)' where msg_type = 'measuringItemCategory' and site_id  = -1 and value_zh = '活度计';
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


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','1','放射类','Radioactive',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','1','放射类','Radioactive',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringStatus','3','无结果','No Result',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','1','医用电子仪器','Medical Electronic Equipment',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','2','光学仪器及内窥镜','Optical Instruments and Endoscopes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','3','医用机械设备','Medical Machinery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','4','电子产品及通讯设备','Electronic Products and Communication Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','5','仪器仪表计量标准器具','Instrumentation and Standard Measurement Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','login','登录系统','Login System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','login_name','登录用户名','Login Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','password','密码','Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','telephone','手机号','Login Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','msmcode','验证码','Login Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http404NotFoundMsg','您想访问的网页不存在.','The resource you are looking for does not exist.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http401AccessDeniedTitle','权限受限','Access Denied',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Http401AccessDeniedMsg','对不起，您未获得访问此网页的授权.','Sorry, You are not authorized to access this resource.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','UserRole','用户权限','User Privilege',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Upload','上传','Upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','From','从','From',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Execute','执行','Execute',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DuplicateSiteName','SiteAdmin名称 %s 已有人使用，请更换租户名称.','SiteAdmin name %s has been used, please change the tenant name.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','invalidEmail','Email: 不是有效的Email地址.','Email: Invalid Email Address',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DuplicateMsgKey','编码 %s 已存在,请更换其他名称','The code %s already exists, please change another name.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','PasswordNotMatch','输入的密码不匹配','The entered password does not match',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','resetPassword','重置密码','Reset Password',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','continue','继续','Continue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TerminatedAsset','报废资产','Terminated Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','To','到','To',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Profile','我的账号','My Account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','4','四月','April',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','name','名称','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','PmOrder','保养记录','PM Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','msgType','编码类型','Encoding Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','msgKey','编码值','Code Value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','msgValue','编码名称','Code Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','displayOrder','执行顺序','Display Order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','cyclicStartDate','循环开始时间','Cyclic Start Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','cyclicEndDate','循环结束时间','Cyclic End Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','risProcedureName','ris部位名称','ris Procedure Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','apmProcedureName','apm部位名称','apm Procedure Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','AttachedFileInfo','附件信息','Attachment Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','V2_BlobObject','文档','Document',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','otherExpense','其他费用(元)','Other Expenses (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','manhourPrice','内部工时(小时)','Internal Manhours (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','clinicalOwnerId','使用责任人','Clinical Department Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','clinicalOwnerName','使用责任人','Clinical Department Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','clinicalOwnerTel','使用责任人电话','Clinical Department Owner Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','registrationNo','注册证号','Registration Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','factoryWarrantyDate','质保截止日期','Warranty Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contractType','合同类型','Contract Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerId2','维修负责人2','Repair Owner 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerName2','维修负责人2','Repair Owner 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerTel2','维修负责人2电话','Repair Owner 2 Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','eamId','EAM系统编号','EAM System ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemId','SystemID/厂商报修ID','System ID/Manufacturer Request ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isTakeOrderEnabled','允许抢单?','Is CompeteOrder Enabled?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','link','链接','Link',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('accountingCategory','6','其他设备','Other Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('usageStatus','1','审核未通过','Not Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qrCodeLibStatus','3','已建档','Documented',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('usageStatus','3','已建档','Documented',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerName3','维修负责人3','Repair Owner 3',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetOwnerTel3','维修负责人3电话','Repair Owner 3 Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListStatus','1','未完成','Not Finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListStatus','2','部分完成','Partially Finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AckPass','验收通过','Acknowledge Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListInstallAckPass','验收通过','Acknowledge Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','5','五月','May',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListAckStatus','pass','验收通过','Acknowledge Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListAckStatus','reject','验收未通过','Acknowledge Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListItemStatus','new','安装中','Installing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListItemStatus','instorage','已入库','In Storage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListItemStatus','rejected','退货','Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListOrderType','1','医院购买','Hospital Purchase',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListOrderType','2','赠送','Gift',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListOrderType','4','试用','Trial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListOrderType','5','借用','Rent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListProcessType','Easy','简易','Easy',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','2','临时租户','2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','6','六月','June',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('proprietaryClass','1','一般设备','General Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('proprietaryClass','2','仪器仪表及量具','Instruments and Gauges',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('proprietaryClass','3','专用设备','“Special Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inventoryStatus','0','未盘点','Not Checked',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inventoryStatus','1','已盘点','Checked',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inventoryFinished','true','已关闭','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6801','6801基础外科手术器械','6801 Basic Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6802','6802显微外科手术器械','6802 Microsurgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6803','6803神经外科手术器械','6803 Neurosurgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6804','6804眼科手术器械','6804 Ophthalmic Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6805','6805耳鼻喉科手术器械','6805 ENT Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6806','6806口腔科手术器械','6806 Stomatological Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6807','6807胸腔心血管外科手术器械','6807 Thoracic Cardiovascular Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6808','6808腹部外科手术器械','6808 Abdominal Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6809','6809泌尿肛肠外科手术器械','6809 Urinary Anorectal Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6810','6810矫形外科(骨科)手术器械','6810 Orthopaedic (Orthopedic) Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6812','6812妇产科用手术器械','6812 Surgical Instruments for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6813','6813计划生育手术器械','6813 Family Planning Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6815','6815注射穿刺器械','6815 Injection and Puncture Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6816','6816烧伤(整形)科手术器械','6816 Burn (Plastic) Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6820','6820普通诊察器械','6820 General Diagnostic Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6821','6821医用电子仪器设备','6821 Medical Electronic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6822','6822医用光学器具、仪器及内窥镜设备','6822 Medical Optical Instruments and Endoscope Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6823','6823医用超声仪器及有关设备','6823 Medical Ultrasound Equipment and Related Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6824','6824医用激光仪器设备','6824 Medical Laser Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6825','6825医用高频仪器设备','6825 Medical High Frequency Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6826','6826物理治疗及康复设备','6826 Physical Therapy and Rehabilitation Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6827','6827中医器械','6827 Equipment Used in TCM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6828','6828医用磁共振设备','6828 Medical Magnetic Resonance Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6830','6830医用X射线设备','6830 Medical Magnetic Resonance Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6831','6831医用X射线附属设备及部件','6831 Medical X-ray Accessory Equipment and Components',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6832','6832医用高能射线设备','6832 Medical High Energy Ray Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6833','6833医用核素设备','6833 Medical Nuclide Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6834','6834医用射线防护用品、装置','6834 Medical Radiation Protection Products and Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6840','6840临床检验分析仪器','6840 Clinical Test Analyzer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6841','6841医用化验和基础设备器具','6841 Medical Laboratory and Basic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6845','6845体外循环及血液处理设备','6845 Extracorporeal Circulation and Blood Processing Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6846','6846植入材料和人工器官','6846 Implant Materials and Artificial Organs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6854','6854手术室、急救室、诊疗室设备及器具','6854 Equipment and Instruments for Operating, Emergency and Treatment Rooms',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6855','6855口腔科设备及器具','6855 Stomatological Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6856','6856病房护理设备及器具','6856 Ward Care Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','innerPm','内部保养','inner pm orders',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'outerPm', '外部保养', 'outer pm orders', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'outerRemotePm', '外部远程保养', 'outer remote pm orders', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6857','6857消毒和灭菌设备及器具','6857 Disinfection and Sterilization Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6858','6858医用冷疗、低温、冷藏设备及器具','6858 Medical Cold Therapy, Low Temperature and Refrigeration Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6863','6863口腔科材料','6863 Dental Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6864','6864医用卫生材料及敷料','6864 Medical Hygiene Materials and Dressings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6865','6865医用缝合材料及粘合剂','6865 Medical Suture Materials and Adhesives',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6866','6866医用高分子材料及制品','6866 Medical Polymer Materials and Products',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6870','6870软件','6870 Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6877','6877介入器材','6877 Interventional Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','DeleteConformation','删除确认','Delete Conformation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','DeleteConformationMsg','您确定要删除选定的记录么?','Are you sure to delete the selected record?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','devicePhoto','设备照片文件','Equipment Photo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','deviceContract','设备合同文件','Equipment Contract',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','noAssetSelected','请选择一个设备','Please select a piece of equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','searchAssetByDept','按所属科室选设备','Select Equipment by Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','searchAsset','高级查询','Advanced Search',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','selectAssetOwner','请选择责任人...','Please select owner...',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','TransferOrder','转单','Transfer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','noWorkOrderSelected','请选择一个工单','Please select a workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ProcessWorkOrder','处理工单','Process Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','shouldEarly','{0}应该早于{1}!','{0} should be earlier than {1}!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','shouldLate','{0}应该晚于{1}!','{0} should be later than {1}!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','todayDate','当前日期','Current Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Hospital','医院','Hospital',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Site','院区','Site',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','invalidSizeMessage','文件内容不能大于','File size cannot exceed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','remainingCharacters','还有{0}字','{0} characters remaining',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','fileTransFail','文件传输失败','File transfer failed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','DeviceInventorySuccess','设备盘点成功！','Equipment Inventory Success!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','I18nCode','编码','Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','noAvailableCheckListItem','没有可用的检查项，点此进行配置','There’s no check item available, please click here to configure it.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','fileLimitMessage','文件个数不能大于','The number of files cannot be greater than',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','is uploaded.','%s已上传.','%s is uploaded.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','DeleteFileConformationMsg','确认删除文件吗？','Confirm to delete the file?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','SelectUploadFile','请选择上传文件','Please select file to upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','SuccessUploaded','上传成功','Successfully Uploaded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','InvalidPictureFileType','文件类型应该属于(xls,xlsx,doc,docx,pdf,jpeg,jpg,png,txt,ps)','File type should be the form of (xls,xlsx,doc,docx,pdf,jpeg,jpg,png,txt and ps)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','WhenIsFinished','任务完成时，%s不能为空.','%s cannot be empty when the task is finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ResetConformation','重置确认','Reset Conformation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ResetConformationMsg','您确定要重置选定用户的密码么?','Are you sure you want to reset the password of the selected user?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','passwordResetTips','您使用的是系统初始密码。为保证您的账号安全，建议您立即修改密码.','You are using the initial system password. To keep your account secure, we recommend that you change your password immediately.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AllHospitals','所有院区','All Sites',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ViewHistoryWorkOrder','查看历史报修','View History Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','WarningForUpdateQRCode','此设备已绑定二维码，继续此操作会更新编码','This equipment is already bound to the QR code. If you continue this operation, the code will be updated.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','SuccessForBinding','绑定设备成功!','Success for Binding!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ChooseAssets','请选择设备!','Please select equipment!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ChooseAssetsOwner','请选择设备负责人!','Please select equipment owner!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','InvalidQRCode','无效的二维码!','Invalid QR Code!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','WrongHospitalQRCode','非本院二维码!','Wrong Hospital QR Code!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AlreadyUsingQrCode','已经使用中二维码!','Already Using QR Code!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','DeleteAssetConformationMsg','确认删除设备?','Confirm to delete the equipment?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Institution','集团','Institution',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ExistsUnfinishedWorkOrders','当前设备存在未完成工单，不能报废！','The current equipment has an unfinished workorder and cannot be terminated!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','CancelWorkOrderConformationMsg','确定取消此报修?','To cancel the request?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','cancelWorkOrderReason','取消原因','Cancellation Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','OperationFail','{0}请求失败','{0} Request Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','comments','备注','Comments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','feedbackComment','备注','Comments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','feedbackRating','评分','Rating',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','feedbackRating','评分','Rating',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','patProblems','原因描述','Reason Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','firstCheckDate','接电子版合同#初审返修','接电子版合同#初审返修',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','patActions','解决方案','Resolution',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','patTests','交付前检测','Test before Delivery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','equipmentTaker','取物人','Equipment Taker',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','cancelWorkOrder','取消报修','Cancel Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','工单完工不需要验收?','','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','NeedDispather','允许派工时派工人不能为空!','Dispatcher cannot be null when dispatch is allowed!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','NeedInstitution','集团不能为空!','Institution cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','NeedHospital','医院不能为空!','Hospital cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','NeedSite','院区不能为空!','Site cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','GuestAccountCheckedWarning','临时报修账号角色不能与其他角色同时存在!','Temporary repair account role cannot co-exist with other roles!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','GuestAccountNeedToUnCheckWarning','临时报修账号角色不能与其他角色同时存在!','Temporary repair account role cannot co-exist with other roles!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','InvalidDateInputFormat','请输入正确的日期!','Please enter the correct date!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceScanhd','设备扫描量（次）','Equipment Scan Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceScanlg','扫描次数','Scan Times',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceExpohd','设备曝光量（次）','Equipment Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','exposeCount','曝光量','Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceExpolg_1','曝光量','Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceExpolg_2','基准曝光量','Baseline Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceROIhd','设备投资回报（元）','Equipment ROI (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceROIlg_1','收入','Revenue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceROIlg_2','利润','Profit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','hourslb','小时','Hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CNYlb','元','RMB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsagehd','设备使用（小时）','Equipment Usage (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsagelg_1','使用','In-use',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsagelg_2','等待','Idle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceDThd','设备停机率（％）','Equipment Downtime (Percent)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceDTlg_1','停机率','Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceDTlg_2','基准停机率','Baseline Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','pctlb','百分比','Percent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsageStat_1','总等待时间','Idle (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsageStat_2','总使用时间','In-use (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsageStat_3','总停机时间','Downtime (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','devicePerfhd','设备绩效分析','Equipment Performance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetTopPerfhd','利润最高设备类型','Type of Equipment with the Highest Profits',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deptTopPerfhd','利润最高科室','Department with the Highest Profits',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardhd','设备绩效分析汇总','Equipment Performance Analysis Summary',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm4','时间','Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm2','序列号','Serial Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm3','所在科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm5','收入(元)','Revenue (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm6','运营成本(元)','Operating Cost (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm7','利润(元)','Profit (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm8','扫描量(次)','Scan Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm9','曝光量(次)','Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm10','维修(次)','Maintenance Frequency',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm11','累计停机时长(小时)','Accumulated Downtime (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceScanStat','总扫描量','Scan Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceValueScanhd','总扫描量','Scan Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceExpoStat','总曝光量','Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceValueExpohd','总曝光量','Expose Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selectAssetSingle','选择单台设备','Select Single Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selectAssetChoose','选择设备','Select Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderConfig','维修工单流程配置','Workorder Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceValueRevenuehd','设备总成本','Total Equipment Cost',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceValueCosthd','设备总成本','Total Equipment Cost',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','groupByDay','日','Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','groupByYear','年','Year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkIntervalNotice_1','提示','Notice',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkIntervalNotice_2','选择范围控制最小1个月，最大3年','Search interval between 1 month and 3 years',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selectSearchInterval_1','到','To',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selectSearchInterval_2','确定','OK',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetStatus','2','停机','Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceDTlg_3','停机','Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceUsagelg_3','停机','Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','friendlyTips','友情提示','Friendly Tips',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','excellentEnjoymentA','享A','excellent enjoyment A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','excellentEnjoymentB','优/享B','excellent enjoyment B',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','opsVersion','OPS版','ops version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInventoryAuthority','assetInventory.close','关闭盘点工单','Close Inventory Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInventoryAuthority','assetInventory.view','查看盘点工单','View Inventory Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInventoryAuthority','assetInventory.scan','扫码盘点','Scan Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','1','带教','Teaching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','2','接待','Reception',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','3','会议','Meeting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','4','培训','Training',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','5','巡查回访','Inspection and Visit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','7','论证','Argument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','9','手术转播','Surgical Broadcast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','10','安装验收','Installation acceptance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','11','行政审批','Administrative approval',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','12','不良事件监测','Adverse event monitoring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','authoritiesIsBlank','盘点权限不能为空!','Authorities cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.cost','设备计费情况','Equipment Charging',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.stock','设备库存情况','Equipment Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.config','调剂设备配置','Swapped Equipment Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','assetAdjust.nameConfig','调剂名称设置','Swapped Equipment Name Settings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','install.check','核对入库','Check and Store',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.workbench','维修工作台','Repair Workbench',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.count','设备维修统计','Equipment Repair Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.report','维修工单报表','Repair Workorder Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.workSchedule','排班管理','Work Schedule Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.adverseEvents','不良事件','Adverse Events',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.vacationMgt','休假管理','Vacation Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.workbench','保养工作台','Maintenance Workbench',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.electricDataImport','电气安全数据导入','Electric Safety Data Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.reportMgt','报告模版管理','Report Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.reportMgt','报表模版管理','Report Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.reportFormMgt','报表模版管理','Report Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','measure.categoryConfig','计量类别设置','Measurement Category Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','measure.info','计量信息','Measurement Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('dispatchMode','1','专人派工','Specially-assigned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('dispatchMode','2','抢单','Grap',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','wfAutoStep3','自动派工','Auto Assign',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('dispatchMode','3','自动派工','Auto Assign',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkFlowConfig','工作流配置','Work Flow Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','dispatchMode','派工模式','Dispatch Mode',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','dispatchUserName','派工人','Dispatcher',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','timeoutDispatch','派工超时时间(分钟)','Dispatch Timeout (Minutes)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','timeoutAccept','接单超时时间(分钟)','Accept Timeout (Minutes)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','timeoutAcceptance','验收超时时间(分钟)','Acceptance Timeout (Minutes)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','timeoutRepair','维修超时时间(分钟)','Repair Timeout (Minutes)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','timeoutClose','关单超时时间(分钟)','Close Timeout (Minutes)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','orderReopenTimeframe','二次开单间隔(天)','Order Reopen Time Frame (Days)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','unbundWX','是否解除微信绑定？','Release WeChat Binding?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maxMessageCount','最大消息提醒次数','Maximum Message Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Import','导入','Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','choseFileToUpload','选择文件上传','Select File to Upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','downloadTemplateFile','下载模版文件','Download Template File',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ImportAssetDataFile','批量导入资产数据','Batch Import Asset Data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.batchImportData','批量导入资产数据','Batch Import Asset Data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ImportSsoUserDataFile','批量导入SSO用户数据','Batch Import SSO User Data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','importStatus','导入结果','Import Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','New','待添加','To be added',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','Exist','已经存在','Already Exist',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','Created','成功添加','Successfully Added',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','Failure','添加失败','Fail to add',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','InvalidParameter','错误的参数','Invalid Parameter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ParsingImportFileError','文件不能解析,请检查模版/数据后重新上传','Fail to parse file. Please check the template/data and upload it again.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','CheckFail','数据错误','Data Error',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importStatus','NoAsset','无匹配设备','No Matched Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('generateMethod','2','前缀+年份+流水号+设备数','Prefix+Year+Serial Number+Equipment Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('generateMethod','1','前缀+国标码+设备数','Prefix+National Standard Code+Equipment Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('generateMethod','3','前缀+四位年份+流水号','Prefix+Four-digit Year+Serial Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','1','全保','Full Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','2','人工保','Manual Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','usageStatus','设备状态','Equipment Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_status','设备状态','Equipment Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_off','停机中','Down (Out of Service)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_out','保修期到期（2个月内）','MSA to be expired in 2 months',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_preventive','保养（1周内）','Maintained in 1 week',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_metrology','设备计量（2个月内）','Equipment to be measured in 2 months',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_quality','设备质控（2个月内）','Equipment subject to quality control in 2 months',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderMgmt','维修流程','Repair Process',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_maintenance_progressSeries','维修流程','Repair Process',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair','维修流程','Repair Process',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_maintenance_progress','维修流程：','Repair Process:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_maintenance_owner','维修人：','Assignee:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_off_time','停机时间：','Down Time:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_off_reason','原因：','Reason:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_title','我的工单管理','My Workorder Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checklistType','1','巡检','Inspection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_tile_inspection','巡检','Inspection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checklistType','2','计量','Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_tile_meter','计量','Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','6','计量','Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reportType','2','计量','Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checklistType','3','质控','Quality Control',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_tile_quality','质控','Quality Control',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_tile_preventive','保养','Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reportType','1','保养','Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','templateType','模板类型','templateType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('templateType','Maintain','保养','Maintain',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('templateType','AssetAchievements','设备绩效','AssetAchievements',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetAchievementsTemplate','设备绩效模板','Asset Achievements Template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetAchievementsRecord','设备绩效记录','Asset Achievements Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RecordDate','记录年月','Record Date',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_title','设备维修事件统计分析','Equipment Repair Event Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_empty','未填','Unspecified',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_otherReason','其他原因','Other Reasons',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_otherCategory','其他设备类型','Other Equipment Types',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_otherRoom','其他科室','Other Departments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_reasonTile','故障主要原因','Main Cause of Fault',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_stepTile','故障处理流程最耗时步骤','Most Time-consuming Steps in the Troubleshooting Process',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_roomTile','故障主要发生的科室','Department where the main fault occurs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_categoryTile','故障主要发生的设备类型','Type of equipment where the fault mainly occurs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_occurrenceTile','设备故障数','Equipment Fault Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_rankTile_room','在科室中排名','Rank among the Departments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_rankTile_category','在同类设备中排名','Rank among Equipment of the Same Kind',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_rankTile_device','在所有设备中排名','Rank among the Various Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_reasonChart','设备故障原因分析（次）','Equipment Fault Cause Analysis (Times)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_reasonChart_xAxis','故障原因','Fault Cause',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart','设备故障处理流程响应时间分布','Distributed Response Time of the Equipment Fault Handling Process',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_section','耗时最长的三个步骤的具体响应时间分布','Distributed Response Time of the Most Time-consuming Three Steps',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_minute','%s%d分钟','%s%d Minutes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_hour','%s%.1f小时','%s%.1f Hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_legend_0','未响应','No Response',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_legend_1','小于30分钟','Less than 30 Minutes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_legend_2','30分钟到1小时以内','Within the Range of 30 Minutes-1 Hour',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_legend_3','1小时到1天以内','Within the Range of 1 hour-1 Day',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_timeChart_legend_4','1天以上','Over 1 Day',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_reasonChart_yAxis','故障次数','Fault Times',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart','设备故障分布（次）','Equipment Fault Distribution (Times)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_yAxis','故障次数','Fault Times',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_room','按科室','by Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Department','科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_room_xAxis','科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_category','按设备类型','by Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_device','按单台设备（前40台）','by Single Equipment (First 40 Pieces)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart','设备故障所占比例','Equipment Fault Percentage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_counting','台','Pieces',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_this','本台设备','This Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_that','其它设备','Other Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_percentage','所占比例：','Percentage:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_occurrence','故障数：','Fault Count:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_room','占所在科室总故障比例','Percentage of the Department’s Total Faults',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_room_occurrence','所在科室故障数：','Number of the Department’s Faults:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_room_rank','在所在科室内排名：','Rank among the Department:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_category','占所在设备类型故障比例','Percentage of the Equipment Type Faults',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_category_occurrence','所在类型故障数：','Number of the Type Faults:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_category_rank','在此类设备中排名：','Rank among Equipment of This Kind:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_device','占所有设备故障比例','Percentage of the Equipment Faults',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_device_occurrence','所有设备故障数：','Number of the Equipment Faults:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_percentageChart_device_rank','在所有设备中排名：','Rank among the Various Equipment:',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_title','设备保养排期／跟踪／统计分析','Equipment Maintenance Scheduling/Tracking/Statistical Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_planChart','PM统计表','PM Statistics',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_predicationChart','设备维护预测','Equipment Maintenance Forecast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_year','%d年','%dY',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_month','%d月','%dM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_allDevices','全部设备','All Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListAssetName','设备名称','Equipment Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','preventiveMaintenanceAnalysis_deviceName','设备名称','Equipment Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_count','检查部位总数','Exam Part Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_summary','设备检查部位统计（个）','Equipment Exam Part Summary (pcs)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_device','个','Pieces',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_week','周','Week',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','groupByMonth','月','Month',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_month','月','Month',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceValueProfithd','设备总利润','Total Equipment Profit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetTotalProfit','设备总利润','Total Equipment Profit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetTotalProfitForecast','设备总利润预测','Total Equipment Profit Forecast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetAveROI','设备平均投资回报（元）','Equipment Average ROI (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetROI','设备投资回报','Equipment ROI',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','profitDistro','利润科室分布','Profit Department Distribution',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','modalityType','设备类型','Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','modalityTypeId','设备类型','Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetCategory','设备类型','Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_category_xAxis','设备类型','Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetType','设备类型','Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetAnnual','设备绩效年报（元）','Equipment Performance Annual Report (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetAnnualForecast','设备绩效年报预测（元）','Equipment Performance Annual Report Forecast (RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetAnnualForecast_title','设备绩效预测','Equipment Performance Forecast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','StaffPerformance','人员绩效分析','Staff Performance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','FailureAnalysis','设备故障分析','Equipment Failure Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ProcessAnalysis','维修流程分析','Repair Process Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MaintenanceCost','维修成本分析','Repair Cost Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetSeriaNumConfig','资产编号规则配置','Rules for Generating Asset IDs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','generateMethod','资产编码生成规则','Rules for Generating Asset IDs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','1','一月','January',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','2','二月','February',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','3','三月','March',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','7','七月','July',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','8','八月','August',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','9','九月','September',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','10','十月','October',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','11','十一月','November',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('month','12','十二月','December',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('focusOption','0','所有工单流程消息','All Workorder Process Messages',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('focusOption','1','仅工单创建和关单消息','Only Workorder Create and Close Messages',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('focusOption','2','不推送工单消息','No Workorder Message Push',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('focusOption','3','EE报警','EE Alarm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','1','医用缝合针(不带线)','Medical Suture Needle (without Thread)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','2','基础外科用刀','Knives for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','3','基础外科用剪','Scissors for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','4','基础外科用钳','Forceps for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','5','基础外科用镊夹','Tweezers and Clips for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','6','基础外科用针、钩','Needles and Hooks for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6801','7','基础外科其它器械','Other Devices for Basic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','1','显微外科用刀、凿','Knives and Chisels for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','2','显微外科用剪','Scissors for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','3','显微外科用钳','Forceps for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','4','显微外科用镊、夹','Tweezers and Clips for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','5','显微外科用针、钩','Needles and Hooks for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6802','6','显微外科用其他器械','Other Devices for Microsurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6803','1','神经外科脑内用刀','Intracerebral Knives for Neurosurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6803','2','神经外科脑内用钳','Intracerebral Forceps for Neurosurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6803','3','神经外科脑内用镊','Intracerebral Tweezers for Neurosurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6803','4','神经外科脑内用钩、刮','Intracerebral Hooks and Scrapers for Neurosurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6803','5','神经外科脑内用其他器械','Other Intracerebral Instruments for Neurosurgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6804','2','眼科手术用剪','Scissors for Ophthalmic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6804','3','眼科手术用钳','Forceps for Ophthalmic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6804','4','眼科手术用镊、夹','Tweezers and Clips for Ophthalmic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6804','5','眼科手术用钩、针','Needles and Hooks for Ophthalmic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6804','6','眼科手术用其他器械','Other Devices for Ophthalmic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','1','耳鼻喉科用刀、凿','Knives and Chisels for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','2','耳鼻喉科用剪','Scissors for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','3','耳鼻喉科用钳','Forceps for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','4','耳鼻喉科用镊、夹','Tweezers and Clips for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','5','耳鼻喉科用钩、针','Needles and Hooks for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6805','6','耳鼻喉科用其他器械','Other Devices for ENT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','1','口腔用刀、凿','Knives and Chisels for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','2','口腔用剪','Scissors for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','3','口腔用钳','Forceps for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','4','口腔用镊、夹','Tweezers and Clips for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','5','口腔用钩、针','Needles and Hooks for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6806','6','口腔用其它器械','Other Devices for Stomatology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','1','胸腔心血管外科用刀','Knives for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','2','胸腔心血管外科用剪','Scissors for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','3','胸腔心血管外科用钳','Forceps for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','4','胸腔心血管外科用镊、夹','Tweezers and Clips for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','5','胸腔心血管外科用钩、针','Needles and Hooks for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','6','胸腔心血管外科用其他器械','Other Devices for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6807','7','胸腔心血管外科用吸引器','Aspirators for Thoracic Cardiovascular Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6808','2','腹部外科用剪','Scissors for Abdominal Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6808','3','腹部外科用钳','Forceps for Abdominal Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6808','4','腹部外科用钩、针','Needles and Hooks for Abdominal Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6808','6','腹部外科用其他器械','Other Devices for Abdominal Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6809','2','泌尿肛肠科用剪','Urinary Anorectal Scissors',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6809','3','泌尿肛肠科用钳','Urinary Anorectal Forceps',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6809','5','泌尿肛肠科用钩、针','Urinary Anorectal Needles and Hooks',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6809','6','泌尿肛肠科用其他器械','Other Urinary Anorectal Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','1','矫形（骨科）外科用刀、锥','Knives and Cones for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','2','矫形（骨科）外科用剪','Scissors for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','3','矫形（骨科）外科用钳','Forceps for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','4','矫形（骨科）外科用锯、凿、锉','Saws, Chisels and Files for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','5','矫形（骨科）外科用钩、针','Needles and Hooks for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','6','矫形（骨科）外科用刮','Scrapers for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','7','矫形（骨科）外科用有源器械','Active Devices for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6810','8','矫形（骨科）外科用其它器械','Other Devices for Orthopaedic Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','1','妇产科用刀','Knives for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','2','妇产科用剪','Scissors for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','3','妇产科用钳','Forceps for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','4','妇产科用镊、夹','Tweezers and Clips for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','5','妇产科用钩、针','Needles and Hooks for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6812','6','妇产科用其他器械','Other Devices for Obstetrics and Gynecology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6813','3','计划生育用钳','Forceps for Family Planning',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6813','6','计划生育用其他器械','Other Devices for Family Planning',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6816','1','烧伤（整形）用刀、凿','Knives and Chisels for Burn (Plastic) Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6816','3','烧伤（整形）用钳','Forceps for Burn (Plastic) Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6816','4','烧伤（整形）用镊、夹','Tweezers and Clips for Burn (Plastic) Surgery',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6816','6','烧伤（整形）用其他器械','Other Devices for Burn (Plastic) Surgery',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','1','体温计','Thermometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','3','肺量计','Spirometer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','4','听诊器（无电能）','Stethoscope (without Electric Energy)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','5','叩诊锤（无电能）','Percussion Hammer (without Electric Energy)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','6','反光器具','Reflective Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','7','视力诊察器具','Visual Examination Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','1','用于心脏的治疗、急救装置','Cardiac Therapy Emergency Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','2','有创式电生理仪器及创新电生理仪器','Invasive Electrophysiological Instruments and Innovative Electrophysiological Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','3','有创医用传感器&无创医用传感器','Invasive Medical Sensors & Non-invasive Medical Sensors',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','4','心电诊断仪器','ECG Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','5','脑电诊断仪器','EEG Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','6','肌电诊断仪器','EMG Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','7','其他生物电诊断仪器','Other Bioelectric Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','8','电声诊断仪器','Electroacoustic Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','9','无创监护仪器','Non-invasive Monitoring Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','10','呼吸功能及气体分析测定装置','Respiratory Function and Gas Analysis Measuring Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','11','医用刺激器','Medical Stimulator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','12','血流量、容量测定装置','Blood Flow and Capacity Measuring Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','13','电子压力测定装置','Electronic Pressure Measuring Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','14','生理研究实验仪器','Experimental Instruments for Physiological Research',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','15','光谱诊断设备','Spectral Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','16','体外反搏及其辅助循环装置','External Counterpulsation and Its Auxiliary Circulation Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','17','睡眠呼吸治疗系统','Sleep Breathing Therapy System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','18','心电电极','ECG Electrode',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6821','20','心电导联线','ECG Cable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','1','植入体内或长期接触体内的眼科光学器具','Ophthalmic Optical Instruments Implanted in the Body or in Prolonged Contact with the Body',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','2','心及血管、有创、腔内手术用内窥镜','Endoscopes for Cardiac Vascular, Invasive and Endoscopic Surgeries',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','3','电子内窥镜','Electronic Endoscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','4','眼科光学仪器','Ophthalmic Optical Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','5','光学内窥镜及冷光源','Optical Endoscope and Cold Light Source',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','6','医用手术及诊断用显微设备','Microscopic Devices for Medical Surgery and Diagnosis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','7','医用放大镜','Medical Magnifier',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6822','8','医用光学仪器配件及附件','Medical Optical Instrument Accessories',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','1','超声手术及聚焦治疗设备','Ultrasonic Surgery and Focused Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','2','彩色超声成像设备及超声介入/腔内诊断设备','Color Ultrasound Imaging Equipment and Ultrasound Intervention/Intraluminal Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','3','超声母婴监护设备','Ultrasound Maternal Monitoring Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','4','超声换能器','Ultrasound Transducer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','5','便携式超声诊断设备','Portable Ultrasound Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','6','超声理疗设备','Ultrasonic Physiotherapy Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6823','7','超声辅助材料','Ultrasonic Auxiliary Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','1','激光手术和治疗设备','Laser Surgery and Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','2','激光诊断仪器','Laser Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','3','介入式激光诊治仪器','Interventional Laser Diagnosis and Treatment Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','4','激光手术器械','Laser Surgical Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','5','弱激光体外治疗仪器','Weak Laser In-vitro Treatment Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6824','6','干色激光打印机','Dry-color Laser Printer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6825','1','高频手术和电凝设备','High-frequency Surgery and Electrocoagulation Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6825','2','高频电熨设备','High-frequency Electric Ironing Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6825','3','微波治疗设备','Microwave Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6825','4','射频治疗设备','Radiofrequency Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6825','5','高频电极','High-frequency Electrode',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','1','高压氧治疗设备','Hyperbaric Oxygen Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','4','干保','Cadre Heath Care',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','2','电疗仪器','Electrotherapy Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','5','临床试验','Clinical Trial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','3','光谱辐射治疗仪器','Spectral Radiation Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','4','高压电位治疗设备','High-voltage Potential Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','5','理疗康复仪器','Physiotherapy Rehabilitation Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','6','生物反馈仪','Biofeedback Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','7','磁疗仪器','Magnetic Therapy Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','8','眼科康复治疗仪器','Ophthalmic Rehabilitation Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6826','9','理疗用电极','Physiotherapy Electrode',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6827','1','诊断仪器','Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6827','2','治疗仪器','Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6827','3','中医器具','Instruments Used in TCM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6828','1','医用磁共振成像设备(MRI)','Medical Magnetic Resonance Imaging (MRI) Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6830','1','X射线治疗设备','X-ray Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6830','2','X射线诊断设备及高压发生装置','X-ray Diagnostic Equipment and High-voltage Generating Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6830','3','X射线手术影像设备','X-ray Surgery Imaging Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6830','4','X射线计算机断层摄影设备（CT）','X-ray Computed Tomography (CT) Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','1','医用X射线管、管组件或源组件','Medical X-ray Tube, Tube Assembly or Source Assembly',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','2','医用X线影像系统及成像器件','Medical X-ray Imaging System and Imaging Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','6','自购','Self-purchase',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','7','租赁','Lease',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','8','投放','Launch',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','9','财政','Financial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource','10','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListOrderType','3','投放','Launch',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6820','2','血压计','Sphygmomanometer',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','3','X线机配套用患者或部件支撑装置（电动）','X-ray Machine Supporting Patient or Component Support Device (Electric)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','4','X射线透视、摄影附加装置','X-ray Fluoroscopy and Photography Attachments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','5','X射线机用限速器','Speed Limiter for X-ray Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','6','医用X线胶片处理装置','Medical X-ray Film Processing Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6831','7','医用X线机配套用非电动床、椅等用具','Non-electric Bed, Chair and other Equipment for Medical X-ray Machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6832','1','医用高能射线治疗设备','Medical High-energy Radiation Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6832','2','高能射线治疗定位设备','High-energy Radiation Treatment Positioning Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6833','1','放射性核素治疗设备','Radionuclide Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6833','2','放射性核素诊断设备','Radionuclide Diagnostic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6833','3','核素标本测定装置','Nuclide Specimen Measuring Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6833','4','核素设备用准直装置','Collimation Device for Radionuclide Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6834','1','医用射线防护用品','Medical Radiation Protection Products',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6834','2','医用射线防护装置','Medical Radiation Protection Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','1','血液分析系统','Blood Analysis System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','2','生化分析系统','Biochemical Analysis System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','3','免疫分析系统','Immunoassay System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','4','细菌分析系统','Bacterial Analysis System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','5','尿液分析系统','Urine Analysis System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','6','生物分离系统','Biological Separation System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','7','血气分析系统','Blood-gas Analysis System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','8','基因和生命科学仪器','Gene and Life Science Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6840','10','临床医学检验辅助设备','Auxiliary Equipment for Clinical Medical Examination',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6841','1','医用培养箱','Medical Incubator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Create','新增','Create',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6841','2','医用离心机','Medical  Centrifuge',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6841','3','病理分析前处理设备','Pathological Analysis Pretreatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6841','4','血液化验设备和器具','Blood Testing Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','1','人工心肺设备','Artificial Cardiopulmonary Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','2','氧合器','Oxygenator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','3','人工心肺设备辅助装置','Assist Devices for Artificial Heart-lung Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','4','血液净化设备和血液净化器具','Blood Purification Equipment and Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','5','血液净化设备辅助装置','Assist Devices for Blood Purification Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','6','体液处理设备','Body Fluid Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6845','7','透析粉、透析液','Dialysis Powder and Dialysate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6846','1','植入器材','Implanted Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6846','2','植入性人工器官','Implantable Artificial Organs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6846','3','接触式人工器官','Contact Artificial Organs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6846','4','支架','Support',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6846','5','器官辅助装置','Assist Devices for Organs',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','1','手术及急救装置','Surgery and Emergency Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','2','呼吸设备','Respiratory Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','3','呼吸麻醉设备及附件','Respiratory Anesthesia Equipment and Accessories',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','4','婴儿保育设备','Baby Care Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','5','输液辅助装置','Auxiliary Equipment for Infusion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','6','负压吸引装置','Auxiliary Equipment for Infusion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','7','呼吸设备配件','Respiratory Equipment Accessories',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','8','医用制气设备','Medical Gas Production Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','9','电动、液压手术台','Electric and Hydraulic Operating Table',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','10','冲洗、通气、减压器具','Flushing, Ventilation and Decompression Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','11','诊察治疗设备','Diagnostic Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','13','手术灯','Operating Lamp',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6854','14','手动手术台床','Manual Operating Table',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','1','口腔综合治疗设备','Comprehensive Oral Treatment Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','2','牙钻机及配件','Dental Chair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','3','牙科椅','Dental Chair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','4','牙科手机','Dental Handpiece',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','5','洁牙、补牙设备','Dental Cleaning and Filling Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','6','车针','Dental Drill',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','7','口腔综合治疗设备配件','Comprehensive Oral Treatment Equipment Accessories',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6855','8','口腔灯','Mouth Lamp',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6856','1','供氧系统','Oxygen System',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6856','2','病床','Bed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6856','4','医用供气、输气装置','Medical Gas Supply and Delivery Devices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','1','辐射灭菌设备','Radiation Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','2','压力蒸汽灭菌设备','Pressure Steam Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','3','气体灭菌设备','Gas Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','4','干热灭菌设备','Dry-heat Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','5','高压电离灭菌设备','High-voltage Ionization Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','6','专用消毒设备','Special Disinfection Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','8','煮沸灭菌器具','Boiling Sterilization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6857','9','煮沸消毒设备','Boiling Disinfection Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6858','1','低温治疗仪器','Hypothermia Instrument',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6858','2','医用低温设备','Medical Cryogenic Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6858','3','医用冷藏设备','Medical Refrigeration Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6858','4','医用冷冻设备','Medical Freezing Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6858','5','冷敷器具','Cold Compress Instruments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','1','高分子义齿材料','Polymer Denture Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','2','齿科植入材料','Polymer Implant Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','3','根管充填材料','Root Canal Filling Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','4','牙周塞治剂','Periodontal Sputum',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','5','颌面部修复材料','Maxillofacial Repair Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','6','永久性充填材料及有关材料','Permanent Filling Materials and Related Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','7','暂封性充填材料及有关材料','Temporary Filling Materials and Related Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','8','金属、陶瓷类义齿材料','Metal and Ceramic Denture Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','9','齿科预防保健材料','Preventive Dental Healthcare Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','10','充填辅助材料','Auxiliary Filling Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','11','正畸材料','Orthodontic Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','12','印模材料','Impression Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','13','铸造包埋材料','Casting Embedding Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','14','模型材料','Modeling Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','15','齿科辅助材料','Auxiliary Dental Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6863','16','研磨材料','Abrasive Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6864','1','可吸收性止血、防粘连材料','Absorbable Hemostasis and Anti-adhesion Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6864','2','敷料、护创材料','Dressing and Protective Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6864','3','手术用品','Surgical Supplies',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6864','4','粘贴材料','Adhesive Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6865','1','医用可吸收缝合线（带针/不带针）','Medical Absorbable Suture (with/without the Needle)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6865','2','不可吸收缝合线（带针/不带针）','Non-absorbable Suture (with/without the Needle)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6865','3','医用粘合剂','Medical Adhesive',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6865','4','表面缝合材料','Surface Suture Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','1','输液、输血器具及管路','Infusion and Blood Transfusion Equipment and Piping',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','2','妇科检查器械','Gynecological Examination Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','3','避孕器械','Contraceptive Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','4','导管、引流管','Catheter and Drainage Tube',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','5','呼吸麻醉或通气用气管插管','Endotracheal Intubation for Respiratory Anesthesia or Ventilation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','6','肠道插管','Intestinal Cannula',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','7','手术手套','Surgical Gloves',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','8','引流容器','Drainage Container',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6866','9','一般医疗用品','General Medical Supplies',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6870','1','功能程序化软件','Functional Programming Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6870','2','诊断图象处理软件','Functional Programming Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6870','3','诊断数据处理软件','Diagnostic Data Processing Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6870','4','影象档案传输、处理系统软件','Image File Transfer and Processing System Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6870','5','人体解剖学测量软件','Human Anatomy Measurement Software',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6877','1','血管内导管','Intravascular Catheter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6877','2','导丝和管鞘','Guide Wire and Tube Sheath',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6877','3','栓塞器材','Embolization Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','all','全部','All',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','All','全部','All',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','addworker','增加协作人','Add Coworker',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manhours','工时(小时)','Manhours (Hours)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','otherExpense','其它费用','Other Expenses',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','addAcce','增加备件','Add Accessory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','addPic','增加图片','Add Picture',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','confirmcancel','确认取消','Confirm Cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repaired','已报修','Repaired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','4','维修中','in Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHead_tile_maintenance','维修中','in Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairing','维修中','in Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetArrivalListInstallAckNone','待验收','To be acknowledged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','5','待验收','To be acknowledged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListAckStatus','none','待验收','To be acknowledged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitAck','待验收','To be acknowledged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitFeedback','待评价','To be evaluated',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Closed','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','9','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','10','待送修','wait Sending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','11','待返还','wait return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitSending','待送修','wait Sending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitReturn','待返还','wait Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','confirmSend','确认送达','Confirm Send',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','confirmReturn','确认领取','Confirm Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','closed','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Canceled','已取消','Canceled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('region','100','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','canceled','已取消','Canceled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tobeReview','待审批','To be approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetKnowledgeBaseMgmt','知识文档','Knowledge Document',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','knowledge','知识文档','Knowledge Document',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','messagepushoption','工单消息推送选项','Workorder Message Push Option',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','overtimenotification','工单超时推送选项','Workorder Overtime Message Push Notification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','subscriber','关注人','Subscriber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetTagMsgSubscriber','维修事件消息订阅','Repair Event Message Subscription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','msgsubscribe','维修事件消息订阅','Repair Event Message Subscription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','signon','签到','Sign-in',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','new','新建','New',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','staff_tile_maintenance','维修','Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repair','维修','Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','audit','审批','Approval',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetDeptId','使用科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetDeptName','使用科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','clinicaldepart','使用科室','Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','equiptaketime','取物时间','Equipment Take Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','equiptake','取物','Equipment Take',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','qualityexpirdate','质保期','Warranty Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','expectarrivaldate','预期到达时间','Expected Arrival Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','arrival','到货','Arrival',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','newrepair','新增报修','New Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','newRepair','新增报修','New Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','createrepair','新增报修','New Repair',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','functionGroup','医疗器械分类(旧)','Medical Device Classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','medicalInstrumentClassification','医疗器械分类(旧)','Medical Device Classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetCode68','医疗器械子分类(旧)','Medical Device Subclassification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','medicalInstrumentSubclassification','医疗器械子分类(旧)','Medical Device Subclassification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','parentAsset','主设备','Parent Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PmOrderRule','保养规则','PM Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.rule','保养规则','PM Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approveUserId','审核人','Approver',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','8','待审核','To be approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('usageStatus','2','待审核','To be approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmRuleStatus','0','待审核','To be approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PmOrderRuleItem','保养检查项配置','PM Check Item Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PmOrderRuleCheckList','保养检查项','PM Check Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approveSuccess','审核成功！','Approve Success!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','createdDate','创建日期','Create Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srRepairType','1','主动维修','Active Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srRepairType','2','被动维修','Passive Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RelRuleAsset','配置资产保养规则','Assigned Asset PM Rules',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','safetyReport','电气安全检测报告','Electric Safety Test Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','performanceReport','性能检测报告','Performance Test Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceName','设备标准名称','Equipment Standard Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','makerShortName','品牌/制造商简称','Brand/Manufacturer Short Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','pmReminderTime','保养提醒时间(天)','PM Reminder Time (Days)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','needFilterFurthur','返回记录过多，请增加过滤条件','There are too many return records, please increase the filter condition.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetName','资产名称','Asset Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetsDashboardclm1','资产名称','Asset Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetName','资产名称','Asset Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partsType','1','库房领用','Get from Warehouse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partsType','2','医工采购','Medical Worker Purchase',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partsType','3','外修采购','External Purchase',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partsType','4','报废设备拆卸','Terminated Equipment Removal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partsType','5','保修合同配件','MSA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetRuleManager','资产规则管理','Asset Rules Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','maintain.assetMgt','资产规则管理','Asset Rules Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','fieldLabel','字段标签显示','Field Label Display',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','fieldKey','字段标识','Field Identifier',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','fieldType','字段类型','Field Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeInputText','文本输入','Text Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeNumber','数字输入','Number Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeSwitch','布尔输入','Boolean Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeSelect','下拉选择','Dropdown Select',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeTextArea','多行文本','Multi-line Text',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ComponentTypeDate','日期输入','Date Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','repairForm','维修类型','Repair Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairForm','1','内修','Internal Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairForm','2','外修','External Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','0','借用已失效','Borrow expired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairForm','3','内外修可选','Internal and External Optional',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','1','完全修复','Fixed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','2','部分修复','Partially Fixed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','3','无法修复','Unfixed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','4','需外送修复','External Repair Required',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','5','误报','False Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairResult','6','重复报修','Repeat Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleStatus','1','已配置','Configured',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleStatus','2','未配置','Not Configured',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevel','2','2级规则','Level 2 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevel','3','3级规则','Level 3 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ruleLevel','规则级别','Rule Level',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevelFour','1','巡检','Level 1 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevelFour','2','2级规则','Level 2 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevelFour','3','3级规则','Level 3 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ruleLevelFour','4','质量控制','Level 4 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ruleLevelFour','规则级别','Rule Level',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchFileImport','批量数据导入','Batch Data Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','fileType','文件类型','File Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileType','文件类型','File Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchFileName','文件名称','File Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileUploadedTime','上传时间','Upload Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileCreatorUID','上传人','Uploader',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileStatus','处理状态','Processing Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileProcessStartTime','开始处理时间','Process Start Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchfileProcessEndTime','结束处理时间','Process End Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','EEMonitorCSV','环境状态监控','Environmental Status Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','ColdHeaderExcel','MR冷头数据监控','MR Cold Head Data Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','PMScheduleExcel','保养计划','PM Schedule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','PMResultExcel','保养结果','PM Result',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UpTimeExcel','开机时间','Up Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','SrReportPdf','服务报告','Service Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','SrResetReport','服务报告重置','Service Report Reset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','SrExcel','服务请求报表','Service Request Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserAssetCustomerExcel','院方人员列表','Hospital Personnel List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserAssetFeExcel','FE列表','FE List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserAssetOLEExcel','OLE列表','OLE List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserAssetSalesExcel','Sales列表','Sales List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'AppTrainingExcel', '应用培训列表', 'App Training List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'OLERulesExcel', 'OLE规则列表', 'OLE Rule List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'AssetInfoImportExcel', '资产台账创建列表', 'Asset Info Import List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'ZkbAssetTransferExcel', '智康宝资产转移', 'ZKB Asset Transfer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'UsConnectStatusExcel', '设备inisite状态导入列表', 'US Inisite Status Import List', NULL,
                                                                                            '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserAccountSendMailExcel','用户账号发送邮件列表','User Account Send Mail List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'AconnectPerformanceExcel', '基础互通-用', 'Aconnect Performance', NULL,'-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType', 'ZoneManagerInfoExcel', 'ZoneManager列表', 'Zone Manager List', NULL,'-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','FAILEDUPLOAD','上传失败','Failed Upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','STARTING','待处理','Starting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','STARTED','已开始','Started',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','STOPPED','已暂停','Stopped',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListStatus','3','已完成','Finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListItemStatus','finished','已完成','Finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','COMPLETED','已完成','Finished',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','FAILED','失败','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileProcessStatus','UNKNOWN','异常','Exception',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','geDeviceType','GE设备分类','GE Equipment Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','3','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetFunctionType','6899','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('6899','1','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','0','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('generateMethod','0','其他','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','2','CT Performance','CT Performance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','21','CT Premium','CT Premium',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','22','CT Distribution','CT Distribution',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','23','CT Value','CT Value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','24','NM CT','NM CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','31','U/S LOGIQ E9','U/S LOGIQ E9',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','32','U/S VIVID E9','U/S VIVID E9',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','33','U/S VOLUSON E8','U/S VOLUSON E8',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('geDeviceType', '101', 'WS', 'WS', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('formatNum','1','0000','0000',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('formatNum','2','00000','00000',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('formatNum','3','000000','000000',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('formatNum','4','0000000','0000000',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('formatNum','5','00000000','00000000',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','0','普通租户','Ordinary Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','1','代理商租户','Agent Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','5','供应商租户','Supplier Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','6','县域租户','County Area Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tenantType','租户类型','Tenant Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetStatus','1','正常','Normal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('deviceStatus','0','正常','Normal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('arrivalListProcessType','Normal','正常','Normal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','Normal','正常','Normal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','Abnormal','不正常','Abnormal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','High','过高','High',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','Low','过低','Low',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','DeltaException','过低','Low',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('TableReportResult','MuException','过低','Low',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','1','MR','MR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','7','X-Ray','X-Ray',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','8','Mammo','Mammo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','9','OEC','OEC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','1','借用待受理','Borrow to be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','2','借用待交付','Borrow to be delivered',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','3','借用待确认','Borrow to be confirmed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','4','借用已取消','Borrow canceled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','5','借用已搁置','Borrow suspended',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetBorrowStatus','6','借用已完成','Borrow completed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','7','归还待受理','Return to be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','8','归还待交付','Return to be delivered',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','9','归还待确认','Return to be confirmed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','10','归还已取消','Return canceled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','11','归还已搁置','Return suspended',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','12','归还已完成','Return completed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetReturnStatus','13','归还已失效','Return expired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('maintenanceType','1','日常维护','Daily Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('maintenanceType','2','周期维护','Periodic Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('executable','1','可执行','Executable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('executable','2','不可执行','Non-executable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','WoNeedDispather','维修配置中允许派工时派工人不能为空!','Dispatcher cannot be null when dispatch is allowed in the repair configuration!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','PmNeedDispather','保养配置中允许派工时派工人不能为空!','Dispatcher cannot be null when dispatch is allowed in the PM configuration!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','MeasureNeedDispather','计量配置中允许派工时派工人不能为空!','Dispatcher cannot be null when dispatch is allowed in the measurement configuration!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message', 'CurrentEquipmentIsLoanedOut', '当前已有设备借出，如需更换，请完成当前调剂借用归还流程!', 'The current equipment is loaned out. If you need to replace it, please complete the current transfer loan return process', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','WoManualAndGrapMsg','维修配置中允许抢单和允许人工至少选一个!','There should be at least one repair configuration for manual or grap mode!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','PmManualAndGrapMsg','保养配置中允许抢单和允许人工至少选一个!','There should be at least one PM configuration for manual or grap mode!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','MeasureManualAndGrapMsg','计量配置中允许抢单和允许人工至少选一个!','There should be at least one measurement configuration for manual or grap mode!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource2','1','自有资金','Owned Capital',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('capitalSource2','2','合资公司资金','JV Capital',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','capitalSource2','资金来源2','Capital Source 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmType','1','周期','Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCodeConfig','DFQ','资产编号,设备编号,二维码合一','Integrated Asset ID, Equipment ID and QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCodeConfig','DF','资产编号,设备编号合一','Integrated Asset ID and Equipment ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringStatus','1','合格','Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qualityChecking','1','合格','Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qaStatus','0','合格','Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measuringStatus','2','不合格','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qualityChecking','2','不合格','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('qaStatus','1','不合格','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','selectedOrg','所属部门不能为空！','Organization cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CaptureDataMessages','消息配置','Message Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ElectricSafetyData','电气安全数据','Electric Safety Data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PccExamineReport','PCC探头检测报告','PCC Probe Test Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','FormatIllegal','初始流水号长度只能递增!','Initial serial number can only be incremented!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','prefixIsNull','资产编号前缀不能为空!','Asset ID Prefix cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','prefixExistsAlready','前缀已存在！','Prefix already exists!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','srNumTooLong','资产编号不能超过16位！','The asset ID cannot exceed 16 digits!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','ZYB','智优保','Excellent Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','moreThanThree','使用国标码生成资产编号时前缀不能超过3位！','When using the national standard code to generate the asset number, the prefix cannot exceed 3 digits',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','1','通过','Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','pass','通过','Pass',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','0','不通过','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','fail','不通过','Fail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','2','不适用','N/A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('checkItem','n/a','不适用','N/A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','countlb','次','Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('chargingType','1','次','Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('chargingType','2','小时(半点)','Hour (Half Hour)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('chargingType','3','天','Day',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationMonitor_day','天','Day',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('chargingType','4','自然时间','Natural Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('chargingType','5','小时(整点)','Hour (O''clock)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','vendor','供应商','Vendor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListVendor','供应商','Vendor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('supplierType','1','供应商','Vendor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','maitanance','维修商','Maintainer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('supplierType','2','维修商','Maintainer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','manufacture','制造商','Manufacturer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','manufactureTel','制造商联系方式','Manufacture Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('supplierType','3','制造商','Manufacturer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','isAssetPMed','是否PM','Need PM?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','isAssetChecked','是否盘点','Is Checked?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','isWarrantyNotice','是否对维保到期提醒','Is Warranty Expiration Notice?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetContract','合同','Contract',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ContractInfo','合同','Contract',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','contractNum','合同编号','Contract Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','foreignTradeNum','外贸合同号','Foreign Trade Contract Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','subjectProject','所属课题项目','Subject Project',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','reportNum','报告号','Report Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','applyDept','申请科室','Apply Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','applyProjectName','项目申请名称','Applied Project Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','estimatedPrice','预估单价(万元)','Estimated Unit Price ( Ten Thousand RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','priceType','价值类别','Value Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','argumentEndDate','论证会结果/时间','Argument End Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','parameterEndDate','技术参数送出时间','Technical Parameter Sending Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','parameterPerson','参数撰写人','Parameter Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tenderOpenDate','开标时间','Tender Open Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','bidWinner','中标单位','Bid Winner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','bidWinnerPerson','中标单位联系人','Bid Winner Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','bidWinnerTelphone','中标单位联系电话','Bid Winner Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tenderNoticeDate','中标通知书时间','Tender Notice Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tenderType','购入方式','Tender Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','contractAssetName','合同内设备名称','Contract Equipment Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','contractAssetPrice','合同标价(万元)','Contract Price (Ten Thousand RMB)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','contractAssetQuantity','合同内设备数量','Contract Equipment Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deliveryDays','交货期(天)','Delivery Days',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListFunctionType','规格型号','Pattern',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','pattern','规格型号','Pattern',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','receiveFileDate','接招标文件材料','Receive File',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','copyFileDate','拷样本、发《供货通知书》','Copy File and Send Delivery Notice',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','changeCommentDate','送审#接审计修改意见','Submit for Approval # Receive Audit Change Comments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','lawerDignDate','律师#律师意见及签字','Lawyer # Lawyer’s Comments and Signature',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','returnSupplierDate','返回供货商修改意见','Return Supplier’s Modification Comments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','fileContractDate','收合同定稿【4份】','Receive【4】Finalized Contracts',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','signContractDate','签字并盖合同专用章','Sign and Stamp the Contract with the Special Seal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tailPaymentDate','报送尾款日期','Submit Final Payment Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','applyPaymentDate','供应商申请尾款日期','Supplier’s Applied Final Payment Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','downPaymentDate','报送首付日期','Submit Down Payment Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','noticeDate','通知供货时间','Delivery Notice Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','plannedArriveDate','应到货时间','Planned Arrive Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','inTimeArrived','是否在规定时间内到货','Arrive within the Specified Time?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','Full','维修-全保','Repair-Full Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','Artificial','维修-人工保','Repair-Manual Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','devicelb','设备','Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceAnalysis_distributionChart_device_xAxis','设备','Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','Equipment','设备','Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','accessory','备件','Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','Parts','备件','Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','11','智尊保','Extreme Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','ZZB','智尊保','Extreme Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','12','智优保','Excellent Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','Other','其它','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractType','13','智享保','Intelligent Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractInfoType','ZXB','智享保','Intelligent Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','WinningBidNotice','01.中标通知','Notification of winning bid',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','TenderingBidding','02.招标文件、投标文件（若有）','Bidding documents, tender documents',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','PurchaseContractAgreement','03.采购合同/协议书','Purchase Contract/Agreement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','SupplierBusinessLicense','04.供应商营业执照、医疗器械生产/经营许可证、税务证（电子版）','Supplier Business License',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','SupplierProductAuthorizationLetter','05.供应商产品授权书','Supplier Product Authorization Letter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','MedicalDeviceRegistrationCertificate','06.产品医疗器械注册证','Product Medical Device Registration Certificate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','ProductConfigurationList','07.产品配置清单','Product Configuration List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','CustomsDeclaration','08.报关单（进口商品需提供）','Import Commodity Customs Declaration Form',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','InspectionForm','09.商检单（进口商品若有需提供）','Import Commodity Inspection Form',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','CopyOfInvoice','10.发票复印件','Copy of invoice',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','ProductArrivalReceiptForm','11.产品到货接收表（物流单或到货单或器械科工程师/使用科室人员收货确认签字单）','Product Arrival Receipt Form',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','ProductQualificationCertificateInformation','12.产品合格证明资料','Product Qualification Certificate Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','SpecificationsParameters','13.产品技术指标参数','Product technical specifications and parameters',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','InstallationAcceptanceReport','14.使用前临床安装验收报告（若有）','Clinical installation acceptance report before use',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','InspectionTestingReport','15.使用前产品检验检测报告或证书（若有）','Product inspection and testing report or certificate before use',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','UseTrainingMaterials','16.使用前临床培训资料、维修培训资料、培训表纸质、课件图片','Use Training Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','AcceptanceForm','17.验收单','Acceptance Form',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','AcceptanceFormList','18.验收清单','Acceptance Form List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','UserManual','19.使用说明书（操作规程）','User Manual (Operating Procedures)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','MaintenanceManual','20.维修作业指导材料（维修手册）','Maintenance Operation Guidance Material (Maintenance Manual)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','AfterServiceCommitment','21.售后服务承诺（电子版）','After sales Service Commitment (Electronic Version)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','LossesList','22.易损易耗件清单（电子版）','List of vulnerable and consumable parts (electronic version)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','AfterServiceNetwork','23.售后服务网点资料（电子版）','After sales service network information (electronic version)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','Other','24.其它','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','ContractDocuments','25.合同文件','Contract documents',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','Tenders','26.招投标文件','Tenders',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractAttachmentType','SupplementaryAgreement','27.补充协议书','Supplementary Agreement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','8','其它','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractPriceType','LT1','1万元以下','Less than 10,000 RMB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractPriceType','BT1_5','1-5万元','10,000-50,000 RMB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractPriceType','BT5_50','5-50万元','50,000-500,000 RMB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('contractPriceType','GT50','50万元以上','Greater than 500,000 RMB ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenderType','Tenders','招标','Tender ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenderType','Negotiated','院内议标','Site Negotiated ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenderType','Continue','续标','Continue ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetValueCategory','S','小型','Small',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetValueCategory','M','中型','Medium',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetValueCategory','L','大型','Large',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ExportTemplateConfig','报表模版配置','Report Template Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderExportTemplate','报表模版','Report Template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetTerminationReportTemplate','报损鉴定模版','Scrap Appraisal Template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PmOrderReportTemplate','预防维护报表模版','PM Report Template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('attachmentType','4','验收及其他材料','Verification and Other Materials',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','nurseSiteMaintainStatus','检查状态','Check Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','0','未检查','Not Checked',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','1','废弃','Discarded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmRuleStatus','1','已通过','Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','2','已通过','Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','5','已通过','Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','6','已通过','Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','7','已通过','Approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('inventoryFinished','false','进行中','in Progress',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','4','进行中','in Progress',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Attribute','属性','Attribute',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AttrDesc','子属性','Subattribute',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','hasAssets','租户下有关联资产，不能删除！','Tenant has associated assets and cannot be deleted!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','hasUsers','租户下有关联用户，不能删除！','Tenant has associated users and cannot be deleted!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','pmOwnerName','保养负责人','PM Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','pmOwnerTel','保养负责人电话','PM Owner Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measuringOwnerTel','计量负责人电话','Measuring Owner Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','acceptDate','验收日期','Accept Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','warrantyMonth','质保时间（月）','Warranty Period (Months)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','arrivalListInvoiceNum','发票号','Invoice Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','invoiceNum','发票号','Invoice Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','1','支持销售','Sales Support',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','2','安装/移机','Install/Transfer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairType','1','上门维修','On-site Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','3','上门维修','On-site Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','4','电话解决','Telephone Solve',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','5','寄修业务','Delivery Business',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','6','外修业务','External Repair Business',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','7','检测观察','Test and Observe',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsType','8','拜访客户','Customer Visit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsPriority','1','普通','Regular',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsPriority','2','加急','Urgent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mvsDeviceType','MVS 设备大类','MVS: Equipment Types',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mvsAssetGroup','MVS专有设备分类','MVS Proprietary Equipment Classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsDeviceType','1','DI','DI',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsDeviceType','2','ULS','ULS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsDeviceType','3','Others','Others',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsDeviceType','4','Component','Component',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','1','CT类','CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','2','磁共振MRI','Magnetic Resonance Imaging (MRI)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','3','血管造影机DSA','Digital Subtraction Angiography (DSA)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','4','普放类','General Radiotherapy',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','5','超声影像类','Uhrosonography',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','6','监护类','Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','7','呼吸类','Breathing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','8','麻醉类','Anesthesia',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','9','软式内窥镜类','Soft Endoscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','10','硬式内窥镜类','Hard Endoscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','11','血液净化类','Blood Purification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','12','电刀、超声刀等医用刀类','Electric knife, Ultrasonic Knife and Other Medical Knives',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','13','灯床塔等手术室设备类','Operating Room Equipment (e.g. Lamp Bed Tower)',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','14','输注泵类','Infusion Pump',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','15','检验室设备类','Test Room Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','16','病理类','Pathology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','17','消毒设备类','Disinfection Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','18','手术显微镜','Operating Microscope',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','19','医用激光类','Medical Laser',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','20','核医学类','Nuclear Medicine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','21','放疗类','Radiotherapy',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetGroup','13','其他类','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetCategory','13','其他类','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mvsAssetGroup','22','其他类','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AllTenants','所有租户','All Tenants',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','common','通用版','Common Version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','business','业务版','Business Version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','ultrasound','超声科室版','Ultrasound Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','audit','质控评审版','audit version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','supplierVersion','供应商版','supplier version',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset','特种设备管理','Special Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.asset','台账管理','Special Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.workOrder','维修管理','Special Work Order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.pmOrder','保养管理','Special PmOrder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.measuring','年检管理','Special Measuring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.fileAttachment','档案管理','Special File Attachment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','specialAsset.riskAssessment','风险管理','Special Risk Assessment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','jmReport','报表管理','Jm Report',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','swapCenter','调剂中心版','audit version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetLifecycleEventStatus','Open','Open','Open',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetLifecycleEventStatus','Assigned','Assigned','Assigned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetLifecycleEventStatus','Equipment Up','Equipment Up','Equipment Up',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetLifecycleEventStatus','Closed','Closed','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sysAuthority','1','盘点','Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInventoryCard','mgr_inv','盘点','Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','containsKeyRole','自定义角色名字不能是系统角色!','Custom role name cannot contain system role!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','1','MR','MR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','2','CT','CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','3','US','US',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','301','US探头','US探头',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','3','US','US',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','4','IGS','IGS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','4','IGS','IGS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','5','Pet CT 非DIQ/DIM/DIM DR','Pet CT 非DIQ/DIM/DIM DR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','51','Pet CT DIQ/DIM/DIM DR','Pet CT DIQ/DIM/DIM DR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','5','Pet CT','Pet CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','6','NM','NM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('alertConfigDeviceType','6','NM CT','NM CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AssetsIsBlank','设备列表不能为空！','Equipment List cannot be null!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmOrderRuleLevel','2','二级规则','Level 2 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmOrderRuleLevel','3','三级规则','Level 3 Rule',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInventoryAuthority','assetInventory.create','新建盘点工单','Create Inventory Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetDocMgmt','文档管理','Document Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','knowledge.mgt','文档管理','Document Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceOperationKPIAnalysis','绩效配置','Performance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceKPIAnalysis','绩效分析','Performance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance','绩效分析','Performance Analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.bigScreen','设备管理大屏','Big Screen for Equipment Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.deptMgt','科务管理','Department Transaction Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DeviceStatusMonitor','工单状态监控','Status Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.scan','状态监控','Status Monitor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.CT','CT球管预测','CT Bulb Tube Prediction',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SmallAssetPerformanceConfig','设备绩效配置','Equipment Performance Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.config','设备绩效配置','Equipment Performance Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.import','设备绩效导入','Equipment Performance Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','performance.BI','BI绩效分析平台','BI Performance Analysis Platform',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','otherLink','相关链接','Related Link',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','link','相关链接','Related Link',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetMgt','设备运营管理','Equipment Operation Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','assetMgt','设备运营管理','Equipment Operation Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','link.CPM','设备运营管理','Equipment Operation Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SysAdmin','系统配置','System Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config','系统配置','System Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','OrgAdmin','组织机构管理','Organization Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','CustomRole','自定义角色','Custom Role',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','Accountdash','资产管理一览','Medical Asset Executive View',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.org','组织机构管理','Organization Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UserAdmin','用户管理','User Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.user','用户管理','User Administrator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.customRole','自定义角色','Custom Role',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MessageSubscriber','设备消息订阅','Equipment Message Subscription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.message','设备消息订阅','Equipment Message Subscription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.assetMessage','设备消息订阅','Equipment Message Subscription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QRCodeMgmt','二维码管理','QR Code Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.qrCode','二维码管理','QR Code Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QRCodeTemplateMgmt','二维码模版管理','QR Code Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.qrCodeMgt','二维码模版管理','QR Code Template Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetOwnerSetting','维修责任人设置','Repair Owner Setting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.repairOwner','维修责任人设置','Repair Owner Setting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetTag','设备标签','Equipment Tag',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.assetMark','设备标签','Equipment Tag',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BiomedGroup','维修工程师群组','Maintenance Engineer Group',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.engineerGroup','维修工程师群组','Maintenance Engineer Group',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SystemConfig','流程配置项','Flow Configuration Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.workFlow','流程配置项','Flow Configuration Item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.custom','自定义配置','Custom Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.attribute','属性配置','Attribute Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.repairMessage','维修消息配置','Repair Message Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.accountdash','资产管理一览','Medical Asset Executive View',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','examineMgt','检测管理','Examination Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','examineMgt.pcc','探头检测','Probe Examination',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','examineMgt.pccConfig','探头检测配置','Probe Examination Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','examineMgt.ssoImp','SSO用户导入','SSO User Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.inventory','盘点工单','Inventory Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.history','资产修改记录','Asset History',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','deviceDataPreview','设备数据概览','Device data preview',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierManagement','供应商管理','supplier management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierManagement.supplierUser','供应商用户管理','supplier user management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierManagement.tenantSupplierMapping','租户供应商绑定','tenant supplier mapping',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierManagement.assetBindSupplier','供应商设备绑定','asset Bind Supplier',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierManagement.assetBindSupplierFE','维修工程师绑定','asset Bind Supplier FE',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierMaintain','预防维护(供应商)','supplier maintain',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierMaintain.workbench','保养工作台','supplier maintain workbench ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierRepair','维修流程(供应商)','supplier maintain',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','supplierRepair.workbench','维修工作台','supplier maintain workbench ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.buildManagement','楼宇管理','build management  ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BuildManagement','楼宇管理','Build Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BuildUp','上一级','Build Up',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCard','mgr_inv','盘点工单','Inventory Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite','临床助手','Clinical Assistant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCard','nurse','临床助手','Clinical Assistant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCardNurse','nurse.today','当日检查','Day’s Check',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCardNurse','nurse.delay','逾期检查','Overdue Check',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','3','有问题','Has Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','8','有问题','Has Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCardNurse','nurse.error','有问题','Has Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCardNurse','nurse.pass','30天内通过','Pass within 30 Days',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCardNurse','nurse.open','未关机列表','Shutdown Failure List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite.maintain','日常检查','Daily Check',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','nurseSiteMaintainAuth','日常检查','Daily Check',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite.config','日常检查设置','Daily Check Settings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','nurseSiteMaintainConfigAuth','日常检查设置','Daily Check Settings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite.report','日常检查报表','Daily Check Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','nurseSiteMaintainExportAuth','日常检查报表','Daily Check Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite.optRecord','开关机登记','Startup/Shutdown Registration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','nurseSite.useRegistration','使用登记','Use Registration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','nurseSiteOptRecordAuth','开关机登记','Startup/Shutdown Registration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','measureInfoAuth','计量信息','Measurement Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureInfoAuth','s','计量信息','Measurement Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainAuth','a','创建日常检查工单','Create Daily Check Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainAuth','d','删除日常检查工单','Delete Daily Check Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainAuth','m','处理日常检查工单','Process Daily Check Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainAuth','s','查看日常检查工单','View Daily Check Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainConfigAuth','a','创建日常检查配置','Create Daily Check Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainConfigAuth','d','删除日常检查配置','Delete Daily Check Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainConfigAuth','m','修改日常检查配置','Update Daily Check Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainConfigAuth','s','查看日常检查配置','View Daily Check Configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainExportAuth','o','导出日常检查报表','Export Daily Check Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainExportAuth','s','查看日常检查报表','View Daily Check Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteOptRecordAuth','o','导出开关机记录','Export  Startup & Shutdown Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteOptRecordAuth','s','查看开关机记录','View Startup & Shutdown Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','redirect2Wechat','跳转至微信公众号','Jump to WeChat Public Account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','redirect2FuncPage','跳转至功能页面','Jump to the Feature Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MeasureOrderRuleItem','计量检查项','Measure Check Items',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','ExistsRelMeasureRule','该规则已绑定资产，无法删除！','The rule has been bound to asset and cannot be deleted!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureType','1','计量周期','Measurement Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureType','2','计量一次性','One-time Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureType','3','计量重测','Measurement Retest',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','2','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitDispatch','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmSteps','1','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureSteps','1','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','3','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitTake','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmSteps','2','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureSteps','2','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmSteps','3','已计划','Planned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureSteps','3','已计划','Planned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','toClose','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('woSteps','6','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitClose','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmSteps','5','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureSteps','5','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmSteps','7','待抢单','To grap',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureSteps','7','待抢单','To grap',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmType','2','一次性','Disposable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmTypeWeb','2','一次性','Disposable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureTypeWeb','2','一次性','Disposable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmType','3','重测','Retest',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmType','4','复检','re-examination',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmTypeWeb','3','重测','Retest',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmTypeWeb','4','复检','re-examination',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureTypeWeb','3','重测','Retest',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmTypeWeb','1','周期性','Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('measureTypeWeb','1','周期性','Cycle',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measuringOwnerName','计量负责人','Measuring Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','measuringOwner','计量负责人','Measuring Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('subStatus','1','等待备件','Wait for Parts',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('subStatus','2','返厂维修-有备机','Return-Standby Available',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('subStatus','3','返厂维修-无备机','Return-Standby Unavailable',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','PmIsSupportBatchFinish','保养是否支持批量完成','Does PM support batch finish?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','parentAssetId','父设备资产id','Parent Equipment ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','QRCode','二维码','QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','qrCode','二维码','QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isPm','是否是保养','Need PM?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isFixed','是否固定资产','Is Fixed?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isChecked','是否选勾','Is Checked?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','warranty','担保','Warranty',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','logout','注销','Logout',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','logout','注销','Logout',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','clinicalDeptHeadWeight','备件审核-所属科室主任权重','Part Approval-Clinical Department Head Weight',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetDeptHeadWeight','备件审核-使用科室主任权重','Part Approval-Department Head Weight',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetHeadWeight','备件审核-医工主任权重','Part Approval-Medical Worker Weight',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','clinicalStaffWeight','备件审核-临床科室人员权重','Part Approval-Clinical Department Staff Weight',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','otherClinicalDeptHeadWeight','备件审核-其他临床科室主任权重','Part Approval-Other Clinical Medical Head Weight',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','orgPath','所属单位','Organization',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetLocation','当前位置','Current Location',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('habit', '1', '国内版', 'InternalEdition', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('habit', '2', '国际版', 'InternationalEdition ', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('currency', '1', '元', 'CNY', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('currency', '2', '美元', 'Dollar', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'habit', '使用范围', 'TenantRange', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'currency', '货币', 'Currency', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Legend', '图例', 'Legend', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MorWExpireSoon', '维保或质保即将到期', 'Maintenance or Warranty Expire Soon', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MorWExpired', '维保或质保已经到期', 'Maintenance or Warranty Expired', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Search', '检索', 'Search', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'JumpTo', '转到', 'Jump to', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Page', '页', 'Page', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportQRCode', '导出二维码', 'Export QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectExportTemplate', '选择导出模版', 'Select Export Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TemplateUsedbyDefault', '默认使用中模版', 'Template Used by Default', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SubmitForApproval', '提交审核', 'Submit for Approval', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CancelWarrantyReminder', '取消保修提醒', 'Cancel Warranty Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmToCancelTheEquipmentWarrantyReminder', '确定取消该设备保修提醒?', 'Confirm to cancel the equipment warranty reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchToBeTerminated', '批量待报废', 'Batch To be Terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmationOfBatchToBeTerminated', '批量待报废确认', 'Confirmation of Batch To be Terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AreYouSureToConfirmTheRecordOfSelectedItemsToBeTerminated', '您确定要待报废选定的记录么?', 'Are you sure to confirm the record of selected items to be terminated?', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ContractStartDate', '合同开始日期', 'Contract Start Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ContractEndtDate', '合同结束日期', 'Contract End Date', NULL, '-1');




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentID', '设备编号（院方）', 'Equipment ID ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentName', '设备名称', 'Equipment Name ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentAlias', '设备别名', 'Equipment Alias ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairOwner', '维修负责人', 'Repair Owner ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairOwner2', '维修负责人2', 'Repair Owner 2 ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairOwner3', '维修负责人3', 'Repair Owner 3 ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Site', '院区', 'Site ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetDepartment', '使用科室', 'Asset Department ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Manufacturer', '制造商', 'Manufacturer ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BrandManufacturerShortName', '品牌/制造商简称', 'Brand/Manufacturer Short Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentStandardName', '设备标准名称', 'Equipment Standard Name ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RegistrationNumber', '注册证号', 'Registration Number ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Vendor', '供应商', 'Vendor ', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MedicalDeviceClassification', '医疗器械分类(旧)', 'Medical Device Classification ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MedicalDeviceSubclassification', '医疗器械子分类(旧)', 'Medical Device Subclassification ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'medicalApparatusClassification', '医疗器械分类', 'Medical Apparatus Classification ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentClassification', '设备分类', 'Equipment Classification ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FunctionGrade', '功能等级', 'Function Grade ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairType', '维修类型', 'Repair Type ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentModel', '设备型号', 'Equipment Model ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ManufacturerSerialNumber', '制造商序列号', 'Manufacturer Serial Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SystemIDManufacturerRequestID', 'SystemID/厂商报修ID', 'System ID/Manufacturer Request ID ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', '2010NationalCode', '2010国家编码', '2010 National Code ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetID', '资产编号', 'Asset ID ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BarCode', '条码', 'Bar Code ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ITSystemID', 'IT系统编号', 'IT System ID ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PurchasePrice', '采购价格', 'Purchase Price ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FinalSalvageValue', '最终残值', 'Final Salvage Value ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DepreciationMethod', '折旧算法', 'Depreciation Method ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LifeCycle', '使用年限', 'Life Cycle', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ManufactureDate', '出厂日期', 'Manufacture Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PurchaseDate', '采购日期', 'Purchase Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ArrivalDate', '到货日期', 'Arrival Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StartUsingDate', '启用日期', 'Start Using Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WarrantyDate', '维保截止日期', 'Warranty Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WarrantyExpirationDate', '维保截止日期', 'Warranty Expiration Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TerminateDate', '报废日期', 'Terminate Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LastMaintenanceDate', '上次保养日期', 'Last Maintenance Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LastMeasurementDate', '上次计量日期', 'Last Measurement Date ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LastInspectionDate', '上次巡检日期', 'Last Inspection Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Maintainer', '维修商 ', 'Maintainer ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintainerTelephone', '维修商电话', 'Maintainer Telephone ', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCode', '二维码 ', 'QR Code ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InstallationLocationCode', '安装位置编码', 'Installation Location Code ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InstallationLocation', '安装位置', 'Installation Location ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UseOwner', '使用责任人', 'Use Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Remark', '备注', 'Remark', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InstallationLocationNotes', '位置备注1(科室)', 'Installation LocationNotes', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UsageClassification', '用途分类', 'Usage Classification ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CapitalSource', '资金来源', 'Capital Source ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RiskGrade', '风险等级', 'Risk Grade ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementGrade', '计量等级', 'Measurement Grade ', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementItemCategory', '计量项目类别 ', 'Measurement Item Category ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsEmergencyEquipment', '是否急救设备', 'Emergency Equipment? ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsImportEquipment', '是否进口设备', 'Import Equipment? ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentStatus', '当前状态', 'Current Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsPM', '是否PM', 'PM? ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsChecked', '是否盘点', 'Is Checked? ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UUID', 'UUID', 'UUID', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DamageAppraisalTemplateManagement', '报损鉴定模版管理', 'Damage Appraisal Template Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TemplateFile', '模版文件', 'Template File', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DownloadTheCurrentActiveTemplate', '下载当前生效模版', 'Download the current active template', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InternalWorkorder', '内部工单', 'Internal Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalWorkorder', '外部工单', 'External Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalRemoteWorkorder', '外部远程工单', 'External Remote Workorder', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ToBeDone', '待办', 'To be done', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestForRepair', '报修', 'Request for Repair', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UpdateRepairResult', '修改维修结果', 'Update Repair Result', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairResult', '维修结果', 'Repair Result', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Approved', '已经通过审批', 'Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EstimatedUnitPriceRMB', '预估单价(元)', 'Estimated Unit Price (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ContractPriceRMB', '合同价格(元)', 'Contract Price (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartType', '备件类别', 'Part Type', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EstimatedArrivalTime', '预计到达时间', 'Estimated Arrival Time', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartType', '维修总金额', 'Part Type', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NewSuuplierInformation', '新增供应商信息', 'New Suuplier Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartName', '备件名称', 'Part Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartQuantity', '备件(数量)', 'Part (Quantity)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InvoicingTime', '开票时间', 'Invoicing Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InvoiceProvider', '发票提供方', 'Invoice Provider', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApplyTime', '申请时间', 'Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SaveAndAdd', '保存新增', 'Save and Add', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ActualPriceRMB', '实际价格(元)', 'Actual Price (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestImage', '报修图片', 'Request Image', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentFault', '设备故障', 'Equipment Fault', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairTelephoneNumberTips', '该设备可以外修,报修电话', 'The equipment can be repaired externally. Repair Telephone Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AddDocument', '添加文档', 'Add Document', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToMeasure', '计量不通过', 'Failed to measure', NULL, '-1');




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ArrivalConfirmationTime', '到货确认时间', 'Arrival Confirmation Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Approved2', '已审核', 'Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AcknowledgedBy', '验收人', 'Acknowledged by', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReasonForApprovalRequest', '申请审核原因', 'Reason for Approval Request', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LatestDevelopments', '最新进展', 'Latest Developments', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OuterRepairOrgName', '外修单位名称', 'Outer Repair Org Name', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QuotationApproval', '报价审核', 'Quotation Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkRequirements', '工作需要', 'Work Requirements', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ArrivalState', '到货状态', 'Arrival State', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Arrived', '已到货', 'Arrived', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NonArrived', '未到货', 'Non-arrived', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalWorkorderID', '外部工单号', 'External Workorder ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UpdateTime', '更新时间', 'Update Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReportDownload', '报告下载', 'Report Download', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AttachmentUpload', '附件上传', 'Attachment Upload', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Configuration', '配置', 'Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestWorkorder', '报修单', 'Request Workorder', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MVSContractStartDate', 'MVS合同起始日', 'MVS Contract Start Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MVSContractEndDate', 'MVS合同截止日', 'MVS Contract End Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PickUpTime', '取件时间', 'Pick-up Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UpdateShutdownTime', '更新停机时间', 'Update Shutdown Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Cancelor', '取消人', 'Cancelor', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairImage', '维修图片', 'Repair Image', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairAttachment', '维修附件', 'Repair Attachment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestVoice', '报修语音', 'Request Voice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DownloadFile', '下载文件', 'Download File', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentWorkorder', '当前工单', 'Current Workorder', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CheckInTime', '签到时间', 'Check-in Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalExpenses', '外部费用', 'External Expenses', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UsedPart', '使用的备件', 'Used Part', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Unapproved', '未审核', 'Unapproved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CorworkerManhours', '协作者工时', 'Corworker Manhours', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestStartTime', '报修开始时间', 'Request Start Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestEndTime', '报修结束时间', 'Request End Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SearchResult', '查询结果', 'Search Result', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderQuantity', '工单数量', 'Workorder Quantity', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentRepairStatistics', '设备维修统计', 'Equipment Repair Statistics', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Push', '推送', 'Push', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NotPush', '不推送', 'Not Push', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentFocus', '设备关注', 'Equipment Focus', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SubscriberCannotBeEmpty', '关注人不能为空', 'Subscriber cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectedEquipment', '已选设备', 'Selected Equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentName2', '设备名', 'Equipment Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSelection', '设备选择', 'Equipment Selection', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentToBeSelected', '待选设备', 'Equipment to Be Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MessageSettings', '消息设置', 'Message Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairEvent', '维修事件', 'Repair Event', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementEvent', '计量事件', 'Measurement Event', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TagSubscription', '标签关注', 'Tag Subscription', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentLabelCannotBeEmpty', '设备标签不能为空', 'Equipment Label cannot be empty', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectType', '选择类型', 'Select Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EEAlert', 'EE报警', 'EE Alert', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderMessagePushOptionCannotBeEmpty', '工单消息推送选项不能为空', 'Workorder Message Push Option cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderTimeoutMessagePush', '工单超时消息推送', 'Workorder Timeout Message Push', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportRepairWorkorderReports', '维修工单报表导出', 'Export Repair Workorder Reports', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportWorkorderReports', '工单报表导出', 'Export Workorder Reports', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportWorkorderPartReports', '工单备件报表导出', 'Export Workorder Part Reports', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderReportStatistics', '工单报表统计', 'Workorder Report Statistics', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentOwner', '当前责任人', 'Current Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UnderRepair', '在修', 'Under Repair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsPartExist', '是否有备件', 'Part Exist?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExistPart', '有备件', 'Exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UnexistPart', '无备件', 'Unexist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsMaintenanceAmountExist', '是否有维修金额', 'Maintenance Amount Exist?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExistMaintenanceAmount', '有维修金额', 'Exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UnExistMaintenanceAmount', '无维修金额', 'Unexist', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('casePriority', '4', '超时消息', 'Timeout Message', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EarliestApplyTime', '最早报修时间', 'Earliest Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LatestApplyTime', '最晚报修时间', 'Latest Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EarliestCloseTime', '最早关单时间', 'Earliest Close Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LatestCloseTime', '最晚关单时间', 'Latest Close Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FaultDescription', '故障描述', 'Fault Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentStep', '当前步骤', 'Current Step', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderStatus', '工单状态', 'Workorder Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportAllReports', '报表全部导出', 'Export All Reports', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectAndExportReports', '报表选择导出', 'Select and Export Reports', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportAllDetails', '详情全部导出', 'Export All Details', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectAndExportDetails', '详情选择导出', 'Select and Export Details', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReportFormTemplateManagement', '报表模版管理', 'Report Template Management', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationManagement', '休假管理', 'Vacation Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Approved3', '审批通过', 'Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NotApproved', '审批未通过', 'Not Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationDate', '休假日期', 'Vacation Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DateType', '日期类型', 'Date Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationApplicant', '休假人', 'Vacation Applicant', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApprovalStatus', '总审批进度', 'Approval Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationNotes', '休假注意', 'Vacation Notes', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationConflictingTip', '和排班记录冲突的休假日期(是否继续休假)', 'Vacation date conflicting with the work schedule record (whether or not to continue vacation)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CloseStatus', '结单状态', 'Close Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Closed2', '已结单', 'Closed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Opened', '未结单', 'Opened', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartAmountInterval', '备件金额区间', 'Part Amount Interval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderID', '工单编号', 'Workorder ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ChangeintoClosed', '变更为结单', 'Change into ‘Closed‘', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StatusChangeConfirmation', '状态变更确认', 'Status Change Confirmation', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ChangeIntoOpened', '变更为未结单', 'Change into ‘Opened’', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CloseSelectedTip', '将所选记录变更成结单状态!', 'Change the selected record into Close Status!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OpenSelectedTip', '将所选记录变更成未结单状态！', 'Change the selected record into Opened status!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ClosedPrice', '发生费用', 'Closed Price', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InvoiceTime', '开票日期', 'Invoice Time', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StatisticalMethod', '统计方式', 'Statistical Method', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ByDepartmentAndEquipmentType', '按科室和设备类型', 'by Department and Equipment Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'byEquipmentName', '按设备名称', 'by Equipment Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'byFaultType', '按故障类别', 'by Fault Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'byMaintenanceEngineer', '按维修工程师', 'by Maintenance Engineer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'byDepartmentalStatisticalWorkload', '按科室统计工作量', 'by Departmental Statistical Workload', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ViewStatistics', '查看统计结果', 'View Statistics', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FaultType', '故障类型', 'Fault Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineer', '维修工程师', 'Maintenance Engineer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AverageElapsedTime', '平均耗时', 'Average Elapsed Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Workload', '工作量', 'Workload', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FrequencyOfFinishedRequest', '已完成报修次数', 'Frequency of Finished Request', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Proportion', '比例', 'Proportion', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InternalMaintenanceRecord', '内部保养记录', 'Internal Maintenance Record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceID', '保养编号', 'Maintenance ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalMaintenanceRecord', '远程保养记录', 'External Maintenance Record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UpdateDate', '更新日期', 'Update Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalMaintenanceRecord2', '外修记录', 'External Maintenance Record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OnSiteMaintenanceRecord', '现场保养记录', 'On-site Maintenance Record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentRelatedContract', '设备相关合同', 'Equipment-related Contract', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApprovalFallureReasonRequired', '审核不通过原因必须填写', 'Approval Fallure Reason Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CompeteOrder', '抢单', 'Compete Order', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TheCompletedManhoursTips', '所填工时不能小于0.01', 'The completed manhours cannot be less than 0.01', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartUnitPriceRMB', '备件单价(元)', 'Part Unit Price (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Close2', '结单', 'Close', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TypeErrTip', '必须选择一个类型', 'Must select a type', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceSubStatus', '维修子状态', 'Maintenance sub status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TotalMaintenanceAmount', '维修总金额', 'Total maintenance amount', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceLaborCost', '维修人工费用', 'Maintenance labor cost', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '1', '自然故障(设备老化/过度使用等)', 'Natural failure (equipment aging / overuse, etc.)', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '2', '人为故障(操作不当/校正失调等)', 'Human failure (improper operation / correction of maladjustment, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '3', '环境故障(温湿度/灰尘/磁场干扰等)', 'Environmental failure (temperature and humidity / dust / magnetic field interference, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '4', '设计故障(设备软硬设计故障)', 'Design failure (equipment hardware and software design failure)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '5', '其它', 'other', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultType', '6', '可规避故障', 'Avoidable fault', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Assignee', '维修人', 'Assignee', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LabelDeviceMessageSubscription', '标签设备消息订阅', 'Label device message subscription', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TagMessageSubscription', '标签消息订阅', 'Tag message subscription', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairWorkbench', '维修工作台', 'Repair Workbench', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MyRepairWorkorder', '我的维修工单', 'My Repair Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairWorkorderReport', '维修工单报表', 'Repair Workorder Report', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkScheduleManagement', '排班管理', 'Work Schedule Management', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NotSelectTheRecordsThatNeedToBeExported', '未选中的需要导出的记录', 'Not select the records that need to be exported', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExportFailed', '导出失败', 'Export Failed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApplyTimeErrTip', '报修时间范围和关单时间范围需任填一组（最早时间/最晚时间）.', 'Either Apply Time range or close time range needs to be completed (earliest/latest time).', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LastestApplyTimeErrTip', '请选择最晚报修时间', 'Please select the latest Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EarliestApplyTimeErrTip', '请选择最早报修时间', 'Please select the earliest Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LastestCloseTimeErrTip', '请选择最晚关单时间', 'Please select the latest close time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EarliestCloseTimeErrTip', '请选择最早关单时间', 'Please select the earliest close time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApplyTimeTooLongErrTip', '报修时间段最大不能超过一年!', 'Apply Time cannot exceed a maximum of one year!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CloseTimeTooLongErrTip', '关单时间段最大不能超过一年!', 'Close time cannot exceed a maximum of one year!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AllDay', '全天', 'All Day', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Morning', '上午', 'Morning', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Afternoon', '下午', 'Afternoon', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StartTimeCannotBeEarlierThanCurrentTime', '开始日期不能早于当前日期', 'Start Time cannot be earlier than Current Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EndTimeCannotBeEarlierThanStartTime', '结束日期不能早于开始日期', 'End Time cannot be earlier than Start Time', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationErrorMsgTip', '上只有你一个责任人或其他责任人都请假:', ' Shows that only you or other owners ask for a leave!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ThisDateHasALeaveRecord', '该日期已存在请假记录', 'This date has a leave record: ', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ScrapTemplateManagement', '报损模板管理', 'Scrap Template Management', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SystemRequiredFieldsCannotBeDeleted', '系统必填字段,不能删除', 'System’s required fields cannot be deleted', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DynamicList', '动态列表', 'Dynamic List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StaticList', '静态列表', 'Static List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ListName', '列表名称', 'List Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NumberOfListItems', '列表项个数', 'Number of List Items', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ListItem', '列表项', 'List Item', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentMustBeSelectedErrMsg', '设备没有选取,请务必选择设备', 'The equipment is not selected, please be sure to select it', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MessageSettingsCannotBeEmpty', '消息设置不可为空', 'Message Settings cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BeSureToSelectPriority', '务必选择紧急程度', 'Be sure to select priority', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DuplicateCreateSubscriptionsErrMsg', '不能重复创建相同设备的消息订阅', 'Cannot repeatedly create the message subscriptions for the same equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSubscriptionSuccessTip', '成功创建设备消息订阅', 'Equipment message subscription successfully created', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TimeoutMessageErrTip', '务必选择工单超时消息推送', 'Be sure to select the message push for workorder timeout', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BeSureToSelectWorkorderPriority', '务必选择工单紧急程度', 'Be sure to select workorder priority', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DuplicateSubscriptionsErrMsg', '不能重复相同的设备消息订阅', 'Cannot repeat the message subscriptions for the same equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Tag', '标签', 'Tag', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedGenerateWordErrMsg', 'Word模版生成失败', 'Failed to generate Word template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TakingTimeIsGreaterThanCurrentTimeTip', '取物时间大于当前时间', 'Taking time is greater than the current time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoEquipmentSelectedNoRepairTip', '没有选择设备,不能报修', 'No equipment selected, no repair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ToBeTerminatedCannotBeRequestedForRepairTip', '所选设备待报废,不能报修', 'The selected equipment is to be terminated and cannot be requested for repair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoAssetDepartmentNoRepaired', '所选设备没有所属科室,不能报修', 'The selected equipment cannot be requested for repair for no asset department', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MVSEquipmentRepairRecord', 'MVS设备维修记录', 'MVS Equipment Repair Record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DownloadTheReportTip', '请选择需要下载报告的工单！', 'Please select the workorder for which you want to download the report！', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderIsEmpty', '工单报告为空!', 'Workorder is empty!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StartDate', '开始日期', 'Start Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Vacation', '休假', 'Vacation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EndDate', '结束日期！', 'End Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DateTypeCannotBeEmpty', '日期类型不能为空', 'Date Type cannot be empty', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CreateWorkScheduleInBatch', '批量创建排班', 'Create work schedule in batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SequenceTimeSettings', '班次时间设置', 'Sequence Time Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SwapCenterDutySettings', '调剂中心值班设置', 'Swap Center Duty Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkScheduleDetails', '排班明细', 'Work Schedule Details', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Date', '日期', 'Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Personnel', '人员', 'Personnel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PersonnelCannotBeEmpty', '人员不能为空', 'Personnel cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SequenceCannotBeEmpty', '班次不能为空', 'Sequence cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Sequence', '班次', 'Sequence', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MorningShift', '早班', 'Morning Shift', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NightShift', '晚班', 'Night Shift', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UncreatedWorkScheduleRecord', '未创建的排班记录（和休假冲突）', 'Uncreated Work Schedule Record (conflicting with vcation)', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ConfictingWithTheExistingRecords', '和已存在记录冲突。', 'Conficting with the existing records.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchWorkSchedule', '批量排班', 'Batch Work Schedule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchWorkScheduleType', '批量排班类型', 'Batch Work Schedule Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchWorkScheduleTypecannotbeEmpty', '批量排班类型不能为空', 'Batch Work Schedule Type cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeekDay', '工作日', 'WeekDay', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeekEnd', '周末', 'WeekEnd', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SwapDutyOwner', '调剂值班负责人', 'Swap Duty Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NormalDutyTime', '正常值班时间', 'Normal Duty Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NightDutyTelephoneNumber', '夜班值班电话', 'Night Duty Telephone Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NightDutyTelephoneNumberCannotBeEmpty', '夜班值班电话不能为空', 'Night Duty Telephone Number cannot be empty', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkScheduleCalendar', '排班日历', 'Work Schedule Calendar', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UpTimeExcel', '开机时间', 'Up Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'HasAScheduleRecord', '有排班记录！', 'has a schedule record!', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationApproval', '请假审批', 'Vacation Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ApprovalNotes', '审批注意信息', 'Approval Notes', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VacationApplicant2', '请假人', 'Vacation Applicant', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Reject', '拒绝', 'Reject', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EndDate2', '结束日期', 'End Date', NULL, '-1');

INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractImport','合同导入','Contract Import',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractRelatedEquipmentImport','合同相关设备导入','Contract-related Equipment Import',NULL	,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractComplete','验收完成','Complete',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FrequencyOfMaintenance', '保养次数', 'Frequency of Maintenance', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ImportContractInformation','导入合同信息','Import Contract Information',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','SiteCannotBeEmpty','所属院区不能为空','Site cannot be empty',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','UpFileImport','上传文件进行导入','Upload the file for import',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileNeedData','文件中填入要导入的合同数据','Fill in the file with the contract data to be imported
    ',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReportNumber', '报告号', 'Report Number', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ImportFailureData','导入失败数据','Import Failure Data',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','EXCELRowNumber','EXCEL行数','EXCEL Row Number',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FailureInformation','失败信息','Failure Information',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractUID','合同UID','Contract UID',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','RelatedEquipmentUID','相关设备UID','Related Equipment UID',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractPeriodMonths','合同时间(月)','Contract Period (Months)',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AttachmentType','附件类型','Attachment Type',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileName','文件名称','File Name',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','UploadTime','上传时间','Upload Time',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','Uploader','上传人','Uploader',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PaymentInformation','付款信息','Payment Information',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PaymentAmount','付款金额','Payment Amount',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PaymentTime','付款时间','Payment Time',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','Operation','操作','Operation',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AddPaymentInformation','添加付款信息','Add Payment Information',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','DetailOne','详细信息一','Detail One',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AddOrderedEquipment','添加订购设备','Add Ordered Equipment',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractRelatedEquipment','合同相关设备','Contract-related Equipment',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','InstallationStatus','安装状态','Installation Status',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AddExistingEquipment','添加已有设备','Add Existing Equipment',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','Quantity','数量','Quantity',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AddUpdatePurchasedEquipmment','添加/修改采购设备','Add/Update Purchased Equipment',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','UploadFile','上传文件','Upload File',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ExportContractInformation','导出合同信息','Export Contract Information',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractStartTime','合同开始时间','Contract Start Time',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','StartTime','开始时间','Start Time',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractEndTime','合同结束时间','Contract End Time',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PreviewResult','预览结果','Preview Result',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractName','合同名称','Contract Name',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ContractValue','合同总额','Contract Amount',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ExportAllContractLists','合同列表全部导出','Export All Contract Lists',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','SelectAndExportContractList','合同列表选择导出','Select and Export Contract List',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaximumFiveThDataLinesInTheExcel', '导入Excel中数据行数最多5000条，请分批导入！', 'Import into a maximum of 5000 data lines in the Excel, please import in batches', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaximumFiveThDataLinesInTheExcel10000', '导入Excel中数据行数最多10000条，请分批导入！', 'Import into a maximum of 10000 data lines in the Excel, please import in batches', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ImportCompletedNoteFailureRecord', '导入完成，注意有导入失败的记录！', 'Import completed, Please note a record of import failure!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ThereAreATotalOf', '总共有', 'There are a total of', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RecordsOfFailure', '条记录处理失败', 'records of processing failure!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ImportComplete', '导入完成', 'Import Complete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Deleted', '已删除', 'Deleted', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CannotBeDeleted', '不能删除', 'Cannot be deleted', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ContractNumberCannotBeReused', '合同编号不能重复使用', 'Contract number cannot be reused', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CanOnlyAddEquipmentInTheSiteWhereTheContractIsLocated', '只能添加合同所在院区的设备', 'Can only add equipment in the site where the contract is located', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ThisEquipmentExists!', '该设备已经存在', 'This equipment exists!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'StartTimeEndTimeCantBeEmptyWhile', '合同开始时间和合同结束时间不能同时为空', 'Contract Start Time and Contract End Time cannot be empty simultaneously', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractNumberIsEmpty', '错误：合同编号为空', 'Error: Contract Number is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractNameIsEmpty', '错误：合同名称为空', 'Error: Contract Name is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorStartTimeIsEmpty', '错误：开始时间为空', 'Error: Start Time is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorEndTimeContractPeriodCantEmptyWhile', '错误：结束时间为空和合同时间月不能同时为空', 'Error: End Time is empty, but it cannot be empty along with Contract Period (Months).', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractNumberCantDuplicated', '错误：合同编号不能重复', 'Error: Contract Number cannot be duplicated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorInvalidContractUID', '错误：无效的合同UID', 'Error: Invalid Contract UID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorChangeContractTypeNotAllowed', '错误：不允许更改合同类型', 'Error: Change of Contract Type Not Allowed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorInvalidSupplier', '错误：不存在的供应商', 'Error: Invalid Supplier', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDuplicatedData', '错误：重复的数据', 'Error: Duplicated Data', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractUIDCantEmpty', '错误: 合同UID不能为空', 'Error: Contract UID cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractTypeInconsistent', '错误：合同类型和所选合同类型不一致', 'Error: Contract type and selected contract type are inconsistent', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorRelatedEquipmentUIDCantEmpty', '错误：相关设备UID不能为空', 'Error: Related Equipment UID cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorInvalidRelatedEquipmentUID', '错误：无效的相关设备UID', 'Error: Invalid Related Equipment UID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorTheEquipmentIsContained', '错误：合同中已经存在该设备', 'Error: The equipment is contained in the contract', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AssetUID', '资产UID', 'Asset UID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DepartmentName', '部门名称', 'Department Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InvalidUUID', '无效UUID', 'Invalid UUID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentAlias', '设备别名', 'Equipment Alias', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner', '维修负责人', 'Repair Owner', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner2', '维修负责人2', 'Repair Owner 2', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner3', '维修负责人3', 'Repair Owner 3', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PMOwner', '保养负责人', 'PM Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BrandManufacturerShortName', '品牌/制造商简称', 'Brand/Manufacturer Short Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentStandardName', '设备标准名称', 'Equipment Standard Name', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RegistrationNumber', '注册证号', 'Registration Number', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MedicalDeviceClassification', '医疗器械分类', 'Medical Device Classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MedicalDeviceSubclassification', '医疗器械子分类(旧)', 'Medical Device Subclassification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentClassificationClassII', '设备分类2级', 'Equipment Classification-Class II', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FunctionGrade', '功能等级', 'Function Grade', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManufacturerSerialNumber', '制造商序列号', 'Manufacturer Serial Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManufacturerRequestID', '厂商报修ID', 'Manufacturer Request ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NationalCode', '2010国家编码', '2010 National Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SsystemID', '系统编号', ' Ssystem ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PurchasePrice', '采购价格', 'Purchase Price', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FinalSalvageValue', '最终残值', 'Final Salvage Value', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LifeCycle', '使用年限', 'Life Cycle', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DepreciationMethod', '折旧算法', 'Depreciation Method', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManufactureDate', '出厂日期', 'Manufacture Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PurchaseDate', '采购日期', 'Purchase Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ArrivalDate', '到货日期', 'Arrival Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'StartUsingDate,', '启用日期', 'Start Using Date, ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyDate', '维保截止日期', 'Warranty Expiration Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyExpirationDate', '维保截止日期', 'Warranty Expiration Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TerminateDate', '报废日期', 'Terminate Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LastMaintenanceDate', '上次保养日期', 'Last Maintenance Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LastMeasurementDate', '上次计量日期', 'Last Measurement Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LastInspectionDate', '上次巡检日期', 'Last Inspection Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintainerTelephone', '维修商电话', 'Maintainer Telephone', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstallationLocationCode', '安装位置编码', 'Installation Location Code', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstallationLocation', '安装位置', 'Installation Location', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UseOwner', '使用责任人', 'Use Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'IsPM', '是否PM', 'Is PM?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'IsChecked2', '是否盘点', 'Is Checked?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AssetValueCategory', '资产价值分类', 'Asset Value Category', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CapitalSourceTwo', '资金来源2', 'Capital Source 2', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyExpirationReminder', '是否对维保到期提醒', 'Warranty Expiration Reminder?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SubjectProject', '所属课题项目', 'Subject Project', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AcceptDate', '验收日期', 'Accept Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyPeriodMonths', '质保时间（月）', 'Warranty Period (Months)', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'QRCode', '二维码', 'QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ProprietaryClass', '专有分类', 'Proprietary Class', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RowStart', '第', 'Row', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DataError', '行数据错误', 'Data Error:', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Description', '说明', 'Description', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileNeedAssetData','文件中填入要导入的资产数据','Fill in the file with the asset data to be imported',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AllRecord','全部记录','All Record',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ErrorRecord','错误记录','Error Record',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AssetImport','资产导入','Asset Import',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','CheckResults','检查结果','Check Results',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','IfIsNullThenGenerate','当二维码为空时自动生成','Automatically generated when the QR code is empty',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','DepartmentImport','部门导入','Department Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PleaseSelectASiteFirst', '请先选择所属院区！', 'Please select a site first!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemplateVersionIncorrect', '模版版本不正确', 'Template Version Incorrect', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','InventoryWorkorder','盘点工单','Inventory Workorder',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','InventoryDepartment','盘点科室','Inventory Department',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PlannedStartTime','计划开始时间','Planned Start Time',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','plannedEndTime','计划结束时间','Planned End Time',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ActualStartTime','实际开始时间','Actual  Start Time',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','ActuaEndTime','实际结束时间','Actual End Time',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','Status','当前状态','Status',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','InProgress','进行中','in Progress',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InventoryDetails', '盘点详情', 'Inventory Details', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NonInventory', '未盘点', 'Non-inventory', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Inventorized', '已盘点', 'Inventorized', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NonLedger', '无台账', 'Non-ledger', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'HasIssue', '有问题', 'Has Issue', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WholeHospitalView', '全部', 'Whole hospital view', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NoWorkOrder', '无工单', 'No Work Order', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentQRCode', '设备二维码', 'Equipment QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentPropertyNumber', '设备财产号', 'Equipment Property Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AssetStatus', '资产状态', 'Asset Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Normal', '正常', 'Normal', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Stopped', '停机', 'Stopped', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartialFunction', '限制功能使用', 'Partial Function', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ToBeTerminated', '待报废', 'To be terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'terminated', '停用', 'terminated', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstallationLocation', '安装位置', 'Installation Location', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentType', '设备类型', 'Equipment Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InventoryChecker', '盘点人', 'Inventory Checker', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InvnetoryTime', '盘点时间', 'Invnetory Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ProblemDescription', '问题描述', 'Problem Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FailedItems', '未通过项', 'Failed Items', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Solved', '已解决', 'Solved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentDetails', '设备详情', 'Equipment Details', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentClassification', '设备分类', 'Equipment Classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CurrentStatus', '当前状态', 'Current Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UseOwner', '使用责任人', 'Use Owner', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'QRCode', '二维码', 'QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceTelephone', '维修电话', 'Maintenance Telephone', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner', '维修负责人', 'Repair Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner2', '维修负责人2', 'Repair Owner2', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner3', '维修负责人3', 'Repair Owner3', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwnerTelephone', '维修负责人电话', 'Repair Owner Telephone', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner2Telephone', '维修负责人2电话', 'Repair Owner2 Telephone', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairOwner3Telephone', '维修负责人3电话', 'Repair Owner3 Telephone', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RepairType', '维修类型', 'Repair Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UsageClassification', '用途分类', 'Usage Classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FunctionGrade', '功能等级', 'Function Grade', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MedicalDeviceClassification', '医疗器械分类', 'Medical Device Classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CapitalSource', '资金来源', 'Capital Source', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RiskGrade', '风险等级', 'Risk Grade', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MeasurementGrade', '计量等级', 'Measurement Grade', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EmergencyEquipment', '是否急救设备', 'Emergency Equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ImportEquipment', '是否进口设备', 'Import Equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AccountingCategory', '核算类别', 'Accounting Category', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MeasurementItemCategory', '计量项目类别', 'Measurement Item Category', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BarCode', '条码', 'Bar Code', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RegistrationNumber', '注册证号', 'Registration Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'HospitalBasedITSystemID', '院内IT系统编号', 'Hospital-based IT System ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ITSystemID', '院内IT系统编号', 'IT System ID', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstallationLocationCode', '安装位置编码', 'Installation Location Code', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManufactureDate', '出厂日期', 'Manufacture Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PurchaseDate', '采购日期', 'Purchase Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceDeadline', '维保截止日期', 'Maintenance Deadline', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ArrivalDate', '到货日期', 'Arrival Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'StartUsingDate', '启用日期', 'Start Using Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyDate', '质保日期', 'Warranty Date', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TerminateDate', '报废日期', 'Terminate Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FinalSalvageValueRMB', '最终残值（元）', 'Final Salvage Value (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PurchasePriceRMB', '采购价格（元）', 'Purchase Price (RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceWorkbench', '保养工作台', 'Maintenance Workbench', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenancePeriod', '保养周期(月)', 'Maintenance period (months)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceWorkorder', '保养工单', 'Maintenance Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InternalMaintenance', '内部保养', 'Internal Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OnSiteMaintenance', '现场保养', 'On-site Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RemoteMaintenance', '远程保养', 'Remote Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ToBeDone', '待办', 'To be done', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CreateMaintenance', '创建保养', 'Create Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCreateMaintenance', '批量创建保养', 'Batch Create Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SecondaryMaintenanceTip', '由设备保养人员按计划进行，主要是内部清洁，紧固易松动的螺丝和零件，检查有无异常情况（如明细噪声等），局部检查和调整。', 'The equipment maintenance personnel shall carry out as planned, mainly the internal cleaning, tightening of loose screws and parts, checking for abnormal conditions (such as detailed noise, etc.), partial inspection and adjustment.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ThreelevelMaintenanceTip', '由设备保养人员、或和设备原厂售后人员共同进行，检查设备的主体部分，必要时更换易损部件，调整、校验参数，使设备满足操作手则和实际使用需求。', 'The equipment maintenance personnel or the equipment''s original after-sales personnel shall check the main part of the equipment, replace the vulnerable parts if necessary, adjust and verify the parameters, so that the equipment meets the requirements of the operator and the actual use.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkorderToBeAssigned', '待派工', 'Workorder to be assigned', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkorderToPickUp', '待接单', 'Workorder to Pick Up', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OrderToCompete', '待抢单', 'Order to compete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Planned', '已计划', 'Planned', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'maintenance1', '保养中', 'Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ToBeApproved', '待审核', 'To be approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Finished', '已关单', 'Finished', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Completed1', '已完成', 'Completed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentIDHospital', '设备编号（院方）', 'Equipment ID (Hospital)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MeasurementGrade2', '保养等级', 'Measurement Grade', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceType', '保养类型', 'Maintenance Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Creator', '创建人', 'Creator', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Owner', '负责人', 'Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceNumber', '保养编号', 'Maintenance Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CurrentProcessingStep', '当前处理步骤', 'Current Processing Step', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PlannedCompletionTime', '计划完成时间', 'Planned Completion Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ActualCompletionTime', '实际完成时间', 'Actual Completion Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Dispatch', '派工', 'Dispatch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Complete', '完成', 'Complete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OrderCompetitionDialog', '抢单对话框', 'Order Competition Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Maintenance', '保养', 'Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Approval', '审核', 'Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchDispatch', '批量派工', 'Batch Dispatch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchTake', '批量接单', 'Batch Take', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchReassign', '批量转单', 'Batch Reassign', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchComplete', '批量完成', 'Batch Complete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchGrap', '批量抢单', 'Batch Grap', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchClose', '批量关单', 'Batch Close', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCancel', '批量取消', 'Batch Cancel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ExportMaintenanceWorkorder', '导出保养单', 'Export Maintenance Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceEndTime', '结束保养时间', 'Maintenance End Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EndTime', '结束时间', 'End Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TotalManhours', '总工时', 'Total Manhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TotalManhoursHours', '总工时（小时）', 'Total Manhours (Hours)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CancelDialog', '取消对话框', 'Cancel Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CancelCurrentWorkorder', '取消当前工单', 'Cancel Current Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CancelAllOfPeriodicWorkorders', '取消所有周期性工单', 'Cancel all of periodic workorders', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CancellationReason', '取消原因', 'Cancellation Reason', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NewMaintenance', '新增保养', 'New Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level2Rule', '2级规则', 'Level 2 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level3Rule', '3级规则', 'Level 3 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AttachmentUpload', '附件上传', 'Attachment Upload', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchMaintenanceProvider', '批量保养人', 'Batch Maintenance Provider', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PlannedBatchCompletionTime', '批量计划完成时间', 'Planned Batch Completion Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceLevel', '保养级别', 'Maintenance Level', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CloseDialog', '关单框', 'Close Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmToClose', '确认关单', 'Confirm to Close', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ToRepair', '去报修', 'To Repair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmToCloseInBatch', '确认批量关单', 'Confirm to close in batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DispatchDialog', '派工对话框', 'Dispatch Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceProvider', '保养人', 'Maintenance Provider', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MustEnterThePlannedCompletionTime', '计划完成时间必须输入', 'Must enter the planned completion time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OrderDialog', '接单对话框', 'Order Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReassignDialog', '转单框', 'Reassign Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkRequirements', '工作需要', 'Work Requirements', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchOrderCompetitionDialog', '批量抢单对话框', 'Batch Order Competition Dialog', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentID', '设备编号（院方）', 'Equipment ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManhoursHours', '工时（小时）', 'Manhours(Hours)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceResult', '保养结果', 'Maintenance Result', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Cancelor', '取消人', 'Cancelor', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UsedPart', '使用的备件', 'Used Part', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EstimatedUnitPriceRMB', '预估单价(元)', 'Estimated Unit Price(RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ContractPriceRMB', '合同价格(元)', 'Contract Price(RMB)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ApprovalStatus', '审核状态', 'Approval Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Unapproved', '未审核', 'Unapproved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Pass', '通过', 'Pass', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Fail', '不通过', 'Fail', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Part', '备件', 'Part', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InvoiceNumber', '发票号', 'Invoice Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InvoicingTime', '开票时间', 'Invoicing Time', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarrantyDate', '质保期', 'Warranty Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ApplyTime', '申请时间', 'Apply Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EstimatedArrivalTime', '预计到达时间', 'Estimated Arrival Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ArrivalConfirmationTime', '到达确认时间', 'Arrival Confirmation Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Arrival', '到货', 'Arrival', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Arrived', '已到货', 'Arrived', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NonArrived', '未到货', 'Non-arrived', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CoworkerInformation', '协作人信息', 'Coworker Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DownloadFile', '下载文件', 'Download File', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceList', '保养清单', 'Maintenance List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceReport', '保养报告', 'Maintenance Report', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CheckItem', '检查项', 'Check Item', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceWorkorderMessage', '保养工单消息', 'Maintenance Workorder Message', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceComplete', '保养完成', 'Maintenance Complete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManhoursCannotL1', '工时不能小于0.01', 'Manhours (Hours) cannot be less than 0.01', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartName', '备件名称', 'Part Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Approved', '已审核', 'Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AddCoworker', '增加协作人', 'Add Coworker', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AddReport', '添加报告', 'Add Report', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SubmitForApproval', '提交审核', 'Submit for Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SelectApprover', '选择审核人', 'Select Approver', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReasonForApprovalRequest', '申请审核原因', 'Reason for Approval Request', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'QuotationApproval', '报价审核', 'Quotation Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartQuantity', '备件(数量)', 'Part (Quantity)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InvoiceProvider', '发票提供方', 'Invoice Provider', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ContractNumber', '合同号', 'Contract Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentTakingTime', '取物时间', 'Equipment Taking Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Opened', '未结单', 'Opened', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SaveAdd', '保存新增', 'Save and Add', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NewSuuplierInformation', '新增供应商信息', 'New Suuplier Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Maintainer', '维修商', 'Maintainer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Manufacturer', '制造商', 'Manufacturer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'pmComment', '工单备注', 'pm comment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'originalPmNum', '原始保养工单号', 'pm originalPmNum', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InternalManhoursHours', '内部工时(小时)', 'Internal Manhours (Hours)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartApproval', '备件审批', 'Part Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ApprovedPass', '已经通过审批', 'Approved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartType', '备件类别', 'Part Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MustProvideTheReasonForApprovalFailure', '审核不通过原因必须填写', 'Must provide the reason for approval failure', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ApprovalP', '审批', 'Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartUpdate', '备件更新', 'Part Update', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ActualPriceRMB ', '实际价格(元)', 'Actual Price (RMB) ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ClosedFinish', '已结单', 'Closed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'IsOpened', '未结单', 'IsOpened', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PartView', '备件查看', 'Part View', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Vendor', '供应商', 'Vendor', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SerialNumber', '序列号', 'Serial Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentFault', '设备故障', 'Equipment Fault', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CurrentAssetLocation', '资产当前位置', 'Current Asset Location', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'externalTeleNum', '该设备可以外修，报修电话', 'The equipment can be repaired externally. Repair Telephone Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Month', '月', 'Month)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RuleLevel', '规则级别', 'Rule Level', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SaveAs', '另存为', 'Save as', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmDeleteRule', '若删除该规则，则已配置的资产规则也会被删除，确定要删除该规则吗？', 'If you delete the rule, the configured asset rules will also be deleted. Are you sure you want to delete the rule?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OutOfRange', '超出范围（只能是1到120）！', 'Out of Range (1-120 only) !', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceTemplate', '保养模版', 'Maintenance Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DownloadSampleTemplate', '下载模版示例', 'Download Sample Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ImportTemplate', '导入模版', 'Import Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AddCheckItem', '增加检查项', 'Add Check Item', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Items', '项目内容', 'Items', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UploadTemplateFile', '上传模版文件', 'Upload Template File', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'GenerateMaintenanceWorkordersInBatch', '批量生成保养工单', 'Generate maintenance workorders in batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SearchAssets', '查询资产', 'Search Assets', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'totalAsset', '共有', 'There are a total of ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SeeMoreAsset', '个资产，下拉查看更多', 'assets, Pull down to see more', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Department', '所属科室', 'Department', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentModel', '设备型号', 'Equipment Model', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfigureWorkorder', '配置工单', 'Configure Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EstimatedCompletionTime', '预计完成时间', 'Estimated Completion Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkorderType', '工单类型', 'Workorder Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Generate', '生成', 'Generate', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintenanceRuleCombine', '保养规则(该过滤条件不会单独生效，需要配合以上条件)', 'Maintenance Rules (This filter condition does not take effect separately and needs to meet the above conditions)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RuleGrade', '规则等级', 'Rule Grade', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SelectedEquipment', '已选设备', 'Selected Equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentToBeSelected', '待选设备', 'Equipment to Be Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MustSelectCurrentStatus', '必须选择当前状态', 'Must select current status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NoDepartmentCantRepair', '所选设备没有使用科室，不能报修', 'The selected equipment cannot be requested for repair for no asset department', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CantContainWorkordersToBeAcceptedOrCompeted', '您的批量中不能包含待接单或待抢单的保养工单', 'Your batch cannot contain the maintenance workorders that are to be accepted or competed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SchemaIdUnknowReport', '未知报告: unstrurct data collection的 记录并未能在table defintion中找到 schema_Id is %s', 'Unkown Report: schema_Id is %s cannot be found through the unstrurct data collection record in the table definition', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MustProvideReason', '审核不通过原因必须填写', 'Must provide the reason for approval failure', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CantOperateWorkorderUnderApproval', '审核中的工单,无法操作', 'Unable to operate the workorder under approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CanTLevel3WorkOrdersInBatches', '3级保养工单不能批量完成', 'Cannot complete the level 3 maintenance work orders in batches', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CanTLevel4WorkOrdersInBatches', '质量控制保养工单不能批量完成', 'Cannot complete the level 3 maintenance work orders in batches', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CantRessignApprovalBatch', '审核中的工单，无法批量转单', 'Unable to reassign the workorders under approval in batches', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FailedToGenerateTheMaintenanceReport', '保养报告生成失败', 'Failed to generate the maintenance report', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DeactivatedCantRepair', '所选设备停用，不能保养', 'The selected equipment is deactivated and cannot be maintained', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CantRepairToBeTerminated', '所选设备待报废，不能保养', 'The selected equipment is to be terminated and cannot be requested for repair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CantMaintenNoAssetDepartment', '没有使用科室的资产不能创建保养工单', 'Cannot create a maintenance workorder for any asset with no asset department', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ManhoursRequired', '工时必填写', 'Manhours Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EndTimeRequired', '结束时间必须填写', 'End Time Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NoFilled', '未填写', 'Not filled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PlannedCompletionTimeRequired', '计划完成时间必须填写', 'Planned Completion Time Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CloseSuccess', '关单成功', 'Close Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SubmissionSuccessfullyCanceled', '取消提交成功', 'Submission successfully canceled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SubmissionForApprovalSuccess', '审批提交成功', 'Submission for Approval Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OperationSuccess', '操作成功', 'Operation Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Distribute', '派单', 'Distribute', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchDistributeSuccess', '批量派单成功', 'Batch Distribute Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCloseSuccess', '批量关单成功', 'Batch Close Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DisptachSuccess', '派工成功', 'Disptach Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Reassign', '转单', 'Reassign', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchReassignSuccess', '批量转单成功', 'Batch Reassign Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ApprovalSuccess', '审批成功', 'Approval Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UnderMaintenance', '保养中', 'Under Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCompleteSuccess', '批量完成成功', 'Batch Complete Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReassignSuccess', '转单成功', 'Reassign Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemporaryStorage', '暂时保存', 'Temporary Storage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemporaryStorageSuccess', '暂存成功', 'Temporary Storage Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TakeOrder', '接单', 'Take Order', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TakeOrderSuccess', '接单成功', 'Take Order Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Compete', '抢单', 'Compete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchTakeOrderSuccess', '批量接单成功', 'Batch Take Order Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCompeteSuccess', '批量抢单成功', 'Batch Compete Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaintainReportNotExist', '检查项对应的保养报告不存在', 'The maintenance report corresponding to the inspection item does not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchCancelSuccess', '批量取消成功', 'Batch cancelled successfully', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level1RulePM', '巡检', 'Level 1 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level2RulePM', '二级规则', 'Level 2 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level3RulePM', '三级规则', 'Level 3 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Level4RulePM', '质量控制', 'Level 4 Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfigurationDate', '配置日期', 'Configuration Date', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SetRulesInBatch', '批量设置规则', 'Set Rules in Batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchDelete', '批量删除', 'Batch Delete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BatchDeleRuleConfig', '批量删除资产规则配置', 'Batch Delete Asset Rule Donfiguration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'IsConfirmDeleRecord', '您确定要删除选中的记录么?', 'Are you sure you want to delete the selected record?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TwoOrThreeDelRecordDel', '提示：二级规则和三级规则都被删除时该条记录会被删除', 'Note:When both the level 2 rule and the level 3 rule are deleted, the record will be deleted.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'RuleName', '规则名称', 'Rule Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ElectricSafetyDataImport', '电气安全数据导入', 'Electric Safety Data Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentName', '设备名称', 'Equipment Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkorderID', '工单编号', 'Workorder ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AssetID', '资产编号', 'Asset ID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ProtectiveGroundImpedance', '保护接地阻抗', 'Protective Ground Impedance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'PatientElectricLeakage', '患者漏电流', 'Patient Electric Leakage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'GroundElectricLeakage', '对地漏电流', 'Ground Electric Leakage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ShellElectricLeakage', '外壳漏电流', 'Shell Electric Leakage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentElectricLeakage', '设备漏电流', 'Equipment Electric Leakage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DetectorTime', '检测仪时间', 'Detector Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MemoryTime', '存储器时间', 'Memory Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WarningMessage', '警告信息', 'Warning Message', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UploadElectricSafetyDataInBulk', '批量添加电气安全数据', 'Upload electric safety data in bulk', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OnlyPSFormat', '只支持ps格式的文件', 'Only supports the files of ps format', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FileSizeMustNotExceedFiveM', '文件大小不得超过5M', 'File size must not exceed 5M', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmUpload', '确认上传', 'Confirm Upload', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ImportElectricSafetyData', '文件中填入要导入的电气安全数据', 'Fill in the file with the electric safety data to be imported', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'WorkorderInformation', '工单信息', 'Workorder Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OverviewOfElectricSafetyDataImport', '电气安全数据导入预览', 'Overview of Electric Safety Data Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FinishedO', '已完工', 'Finished', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UnfinishedO', '未完工', 'Unfinished', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReportTemplateManagement', '报告模版管理', 'Report Template Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NotEnabled', '未启用', 'Not Enabled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Released', '已发布', 'Released', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Disabled', '停止使用', 'Disabled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SelectType', '选择类型', 'Select Type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'JSONTemplate', 'JSON模版', ' JSON Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ExcelTemplate', 'Excel模版', ' Excel Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OK', '确定', 'OK', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CreateTemplate', '创建模版', 'Create Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemplateEdit', '模版编辑', 'Template Edit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Update', '修改', 'Update', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ShiftUp', '上移', 'Shift Up', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ShiftDown', '下移', 'Shift  Down', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'AddGroup', '添加Group', 'Add Group', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MobilePreview', '手机预览', 'Mobile Preview', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DownloadTemplate', '下载模版', 'Download Template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TextView', '文本查看', 'Text View', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ViewExcel', 'Excel查看', ' View Excel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Test', '测试', 'Test', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstrumentDataImportConfiguration', '仪器数据导入配置', 'Instrument Data Import Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemplateLabel', '模板标签', 'Template Label', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstrumentDataField', '仪器数据字段', 'Instrument Data Field', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'BindKeyValuePairs', '绑定键值对', 'Bind Key-value Pairs', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'KeyValuePairsToBeBound', '待绑定键值对', 'Key-value Pairs to Be Bound', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'KeyValuePairsBound', '已绑定键值对', 'Key-value Pairs Bound', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Remark', '备注', 'Remark', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EditGroup', '编辑组', 'Edit Group', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FieldEdit', '字段编辑', 'Field Edit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ResultOutput', '结果输出', 'Result Output', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'FieldDescription', '字段说明', 'Field Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CalculationRule', '计算规则', 'Calculation Rule', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'None', '无', 'None', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperAndLowerLimits', '上下限', 'Upper and Lower Limits', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperAndLowerRangeLimits', '范围上下限', 'Upper and Lower Range Limits', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SetValueVerification', '设定值校验', 'Set Value Verification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DeviationVerification', '偏差校验', 'Deviation Verification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DecimalPlaces', '小数位数', 'Decimal Places', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperAndLowerLimitSettings', '上下限设置', 'Upper and Lower Limit Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalLowerLimit', '正常：下限', 'Normal: Lower Limit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Measurement', '测量值', 'Measurement', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperLimit', '上限', 'Upper Limit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'CalculationKey', '计算结果Key', 'Calculation Key', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LessThanOrEqualTo', '小于等于', 'Less than or equal to', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LessThan', '小于', 'Less than', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LowerLimit', '下限', 'Lower Limit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'GreaterThanOrEqualTo', '大于等于', 'Greater than or equal to', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'GreaterThan', '大于', 'Greater than', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ResultIdentifier', '结果标识', 'Result Identifier', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalOrAbnormal', '正常/不正常', 'Normal/Abnormal', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalOrTooHighOrTooLow', '正常/过高/过低', 'Normal/Too High/Too Low', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalLowerLimitRange)', '正常: (1-下限范围)', 'Normal: (1-Lower Limit Range)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'StandardValue', '标准值', 'Standard Value', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperLimitRangeStandardValue', '(1+上限范围)*标准值', '(1+Upper Limit Range)*Standard Value', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperLimitRange', '上限范围', 'Upper Limit Range', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'Or', '或', 'Or', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LowerLimitRange', '下限范围', 'Lower Limit Range', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfirmRangeNor', '正常: 偏差下限范围≤|Y-X|/X≤偏差上限范围 ; 误差下限范围≤|Y-Z|/Z≤误差上限范围', 'Normal: Lower Limit of Deviation≤|Y-X|/X≤Upper Limit of Deviation; Lower Limit of Error≤|Y-Z|/Z≤Upper Limit of Error', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SetValueX', '设定值(X)', 'Set Value (X)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EquipmentDisplayValueKey', '设备显示值Key', 'Equipment Display Value Key', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'InstrumentDisplayValueKey', '仪表显示值Key', 'Instrument Display Value Key', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperLimitOfDeviation', '偏差上限范围', 'Upper Limit of Deviation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LowerLimitOfDeviation', '偏差下限范围', 'Lower Limit of Deviation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'UpperLimitOfError', '误差上限范围', 'Upper Limit of Error', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'LowerLimitOfError', '误差下限范围', 'Lower Limit of Error', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalErrorBeyondLimitDeviationBeyondLimit', '正常/误差超限/偏差超限', 'Normal/Error beyond Limit/Deviation beyond Limit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OptionIdentifier', '选项标识', 'Option Identifier', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'OptionDisplayValue', '选项显示值', 'Option Display Value', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'EditOption', '编辑选项', 'Edit Option', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'NormalMaximumDeviation', '正常: |Y-Z|≤最大偏差', 'Normal: |Y-Z|≤Maximum Deviation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MaximumDeviation', '最大偏差', 'Maximum Deviation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TesterDataImport', '测试仪数据导入', 'Tester Data Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ExportExcel', '导出Excel', 'Export Excel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ReportTemplateConfiguration', '报表模版配置', 'Report Template Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'DownloadTheCurrentActiveTemplate', '下载当前生效模版', 'Download the current active template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'TemplateFile', '模版文件', 'Template File', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ConfigurationSuccess', '配置成功!', 'Configuration Success!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'SelectExportFiles', '请先选择要导出的文件!', 'Please select the file to export first', NULL, '-1');


--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentID', '设备编号 ', 'Equipment ID ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Loading', '加载中...', 'Loading', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ToBeTerminatedAssets', '待报废资产', 'Asset to be terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TerminatedAssets', '已报废资产', 'Terminated Asset', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TerminationAppraisal', '报废鉴定', 'Termination Appraisal', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchTerminate', '批量报废', 'Batch Terminate', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchTerminateConfirmation', '批量报废确认', 'Batch Terminate Confirmation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AreYouSureToConfirmTheRecordOfSelectedItemsScrapped', '您确定要报废选定的记录么?', 'Are you sure to confirm the record of selected items scrapped?', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UploadedFileTransformedToBeTerminated', '上传文件转报废', 'Uploaded File Transformed to Be Terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ToBeTerminated', '待报废', 'To be terminated', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LifeCycleBeyond', '使用年限超过国家或厂家规定', 'Life cycle beyond that is specified by the country or manufacturer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MainFunctionIsDamaged', '主要功能损坏，无法修复或维修成本过高', 'Main function is damaged, cannot be repaired or the maintenance cost is too high', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TheEquipmentIsDefective', '设备有缺陷，国家警示需强制报废', 'The equipment is defective and should be forcibly terminated according to the national warning', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AccessoriesOrConsumablesNotAvailable', '配件或耗材无法获取', 'Accessories or consumables not available', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UploadedFileTerminatedInBatch', '上传文件批量报废', 'Uploaded File Terminated in Batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetIDRequired', '文件中填入要批量报废的资产数据，资产编号必填', 'Fill in the file with the asset data to be terminated in batch with the asset ID required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BasicInformation', '基本资料', 'Basic Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VoiceDescription', '语音描述', 'Voice Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'VoiceList', '语音列表', 'Voice List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'None', '无', 'None', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ATotalOf', '共', 'A total of', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PlayTo', '播放至', 'Play To', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TextDescription', '文字描述', 'Text Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SelectAndSaveTheImageAsAnAttachmentToTheAsset', '选择图片作为资产附件保存', 'Select and save the image as an attachment to the asset', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RefuseToReturnBack', '拒绝退回', 'Refuse to Return Back', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReturnBackHistory', '退回原因历史', 'Return Back History', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReturnBackReason', '退回原因', 'Return Back Reason', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReasonRequired', '原因必须填写', 'Reason Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReturnBack', '退回', 'Return Back', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Image', '图片', 'Image', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineerGroupName', '维修工程师群组名称', 'Maintenance Engineer Group Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineerSelection', '维修工程师选择', 'Maintenance Engineer Selection', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineerToBeSelected', '待选维修工程师', 'Maintenance Engineer to Be Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineerSelected', '已选维修工程师', 'Maintenance Engineer Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentLabel', '设备标签', 'Equipment Label', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentLabelName', '设备标签名称', 'Equipment Label Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RelatedEquipment', '相关设备', 'Related Equipment', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RelatedMaintenanceEngineerGroup', '相关维修工程师群组', 'Related Maintenance Engineer Group', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'GroupOfMaintenanceEngineersToBeSelected', '待选维修工程师群组', 'Group of Maintenance Engineers to Be Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'GroupOfMaintenanceEngineersSelected', '已选维修工程师群组', 'Group of Maintenance Engineers Selected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentOwnerSettings', '设备责任人设置', 'Equipment Owner Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentTypeOwnerSettings', '设备类型责任人设置', 'Equipment Type Owner Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DepartmentOwnerSettings', '科室责任人设置', 'Department Owner Settings', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DepartmentOwnerSettings', '维修责任人', 'Repair Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentTypeSettings', '设备类型设置', 'Equipment Type Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SetPMOwner', '设置保养负责人', 'Set PM Owner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Region', '地区', 'Region', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InitialPassword', '初始密码', 'Initial Password', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AccessPermission', '访问权限', 'Access Permission', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Search2', '检索', 'Search', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentOwnerA', '设备负责人A', 'Equipment Owner A', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentOwnerB', '设备负责人B', 'Equipment Owner B', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FileUploadFailed', '文件上传失败!', 'File upload failed!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentCannotBeTerminated', '设备[%s]未处于待报废状态，不能报废！', 'The equipment [%s] is not to be termianted and cannot be terminated!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UnfinishedWorkorderCandBeTerminated', '设备[%s]存在未完成工单，不能报废！', 'The equipment name [%s] has an unfinished workorder and cannot be terminated!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AttributeName', '属性名称', 'Attribute Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AttributeDescription', '属性描述', 'Attribute Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ModifiedOn', '修改时间', 'Modified on', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EditSubattribute', '编辑子属性', 'Edit Subattribute', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AttributeList', '属性列表', 'Attribute List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SubattributeDescription', '子属性描述', 'Subattribute Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequiredOrNot', '是否必须', 'Required or not', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Description2', '描述', 'Description', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Required2', '必须', 'Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AttributeConfiguration', '属性配置', 'Attribute Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MessageConfigurationItem', '消息配置项', 'Message Configuration Item', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MessageConfigurationOptions', '消息配置选项', 'Message Configuration Options', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CommonConfiguration', '公共配置', 'Common Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SignatureRequiredForPartApproval', '备件审核是否需要签名', 'Signature required for part approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairConfiguration', '维修配置', 'Repair Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SystemAutomaticAcceptance', '系统自动验收(天)', 'System Automatic Acceptance (Days)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AutomaticallySendWorkers', '是否自动派工', 'Automatically send workers', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderCompetitionAllowed', '是否允许抢单', 'Workorder competition allowed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ManuallySendWorkers', '是否人工派工', 'Manually send workers', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoAcceptanceRequired', '工单完工不需要验收', 'No acceptance required upon completion of the workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AutomaticallyClosed', '工单完工自动关单', 'Automatically closed upon completion of the workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RatingRequired', '是否必须评价', 'Rating Required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AutomaticallyClosedForNoPart', '无备件自动关单', 'Automatically closed for no part', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'IsWorkingHoursRequired', '是否工时必填', 'Is working hours required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoTemporaryAccountAllowed', '不允许临时账号', 'No  temporary account allowed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TelephoneBasedRequestForRepairRequired', '是否电话报修', 'Telephone-based request for repair required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TelephoneBasedRequestStartTime', '电话报修开始时间', 'Telephone-based Request Start Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TelephoneBasedRequestEndTime', '电话报修结束时间', 'Telephone-based Request End Time', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TelephoneBasedRequestEndTime', '电话报修结束时间', 'Telephone-based Request End Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderRelatedLaborCost', '工单人工费用', 'Workorder-related Labor Cost', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'rmb_hour', '元/小时', 'RMB/hour', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EmergencyWorkorder', '紧急工单', 'Emergency Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SignatureRequiredForAcceptance', '验收是否需要签名', 'Signature required for acceptance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoInformation', '抢单模式不发送消息', 'No information sent by the competion mode', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CaseTypeRequired', '故障类别是否必填', 'is case type required', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceConfiguration', '保养配置', 'Maintenance Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Level2SimplifiedMaintenance', '二级简化保养', 'Level 2 Simplified Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PriorDaysOfMaintenanceReminder', '保养提醒提前天数', 'Prior Days of Maintenance Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FrequencyOfMaintenanceReminder', '保养提醒次数', 'Frequency of Maintenance Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceRelatedLaborCost', '保养工单人工费用', 'Maintenance -related Labor Cost', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementConfiguration', '计量配置', 'Measurement Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementRelatedLaborCost', '计量工单人工费用', 'Measurement-related Labor Cost', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSwapConfiguration', '设备调剂配置', 'Equipment Swap Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SwapCenter', '调剂中心', 'Swap Center', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WhetherOrNotToCalculateTheManhours', '是否计算借用工时', 'Whether or not to calculate the manhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RentManhourDefault', '借用工时默认值', 'Rent Manhour Default', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CalculateReturnTheManhours', '是否计算归还工时', 'Whether or not to calculate return the manhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReturnManhourDefault', '归还工时默认值', 'Return Manhour Default', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AutomaticConfirmationTime', '自动确认时间（分钟）', 'Automatic Confirmation Time (Minutes)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OtherConfiguration', '其他配置', 'Other Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetCreationApprover', '资产创建审核人', 'Asset Creation Approver', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PriorDaysOfReview', '质控评审文件过期提醒提前天数', 'Prior Days of Review Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PriorDaysOfMaintenance', '维保/质保提醒提前天数', 'Prior Days of Maintenance/Warranty Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FrequencyOfMaintenance', '维保/质保提醒次数', 'Frequency of Maintenance/Warranty Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PriorDaysOfMeasurementReminder', '计量提醒提前天数', 'Prior Days of Measurement Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FrequencyOfMeasurementReminder', '计量提醒提前次数', 'Frequency of Measurement Reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WhetherOrNotToCheck', '到货安装是否需要核对入库', 'Whether or not to check and store upon installation of the arrivals', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ThreeCodeConfiguration', '三码配置', 'Three-code Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InitialSerialNumber', '初始流水号', 'Initial Serial Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetIDPrefix', '资产编号前缀(最多三位)', 'Asset ID Prefix (up to three digits)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WebMenu', '网页端菜单', 'Web Menu', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WebAuthority', '网页端权限', 'Web Authority', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeChatCard', '微信端卡片', 'WeChat Card', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeChatAuthority', '微信端权限', 'WeChat Authority', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DeleteTheRecord', '确定要删除这条记录吗', 'Are you sure you want to delete the record', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Homepage', '主页', 'Homepage', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WebMenuConfiguration', '网页端菜单配置', 'Web Menu Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WebAuthorityConfiguration', '网页端权限配置', 'Web Authority Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeChatCardConfiguration', '微信端卡片配置', 'WeChat Card Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WeChatAuthorityConfiguration', '微信端权限配置', 'WeChat Authority Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InventoryWorkorder', '盘点工单', 'Inventory Workorder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ClinicalAssistant', '临床助手', 'Clinical Assistant', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CustomConfiguration', '自定义配置', 'Custom Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairMessageConfiguration', '维修消息配置', 'Repair Message Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DoesNotExist', '资产编号[%s]对应的设备不存在，不能报废！', 'The equipment corresponding to the asset ID [%s] does not exist and cannot be termiated!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Inconsistent', '所选设备的科室信息不一致', 'The department information for the selected equipment is inconsistent', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReasonInconsistent', '所选设备的报废原因不一致', 'The reason for the termination of the selected device is inconsistent', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeEncodingMode', '二维码编码模式', 'QR Code Encoding Mode', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Bold', '加粗', 'Bold', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Italic', '斜体', 'Italic', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Export', '导出', 'Export', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UsedSite', '使用院区', 'Used Site', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoQRCodeImported', '文件中填入要导入的二维码', 'Fill in the file with the QR code to be imported', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EnableTime', '启用时间', 'Enable Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InService', '使用中', 'In-service', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MessageConfigurationSaveSuccess', '消息配置保存成功', 'Message Configuration Save Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeCreate', '二维码生成', 'QR Code Create', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SiteCannotBeEmpty', '租户院区不能为空', 'Tenant’s site cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'HospitalCannotBeEmpty', '医院不能为空', 'Hospital cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeQuantity', '二维码数量', 'QR Code Quantity', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QuantityCannotBeEmpty', '数量不能为空', 'Quantity cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ConfirmCreate', '确定生成', 'Confirm Create', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeDisplay', '二维码展示', 'QR Code Display', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CreateQRCode', '生成二维码', 'Create QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportQRCode', '导入二维码', 'Import QR Code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Over64Digits', '二维码不能超过最大长度64位', 'QR code cannot exceed the maximum length of 64 digits', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoSuchQRCode', '无此二维码', 'No such QR code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeHasBeenInUse', '二维码已被使用', 'QR code has been in use', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TheSelectedSiteIsInvalid', '所选院区无效', 'The selected site is invalid', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Failed', '失败', 'Failed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeCannotBeEmpty', '二维码不能为空', 'QR code cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeIsTooLong', '二维码超长', 'QR code is too long', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Over4Digits', '二维码不能少于4位', 'QR code cannot be less than 4 digits', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCodeDuplicatedInTheFile', '文件中重复的二维码', 'QR code duplicated in the file', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExistingQRCode', '已经存在二维码，状态：', 'Existing QR Code，Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportSuccess', '导入成功', 'Import Success', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OrganizationPathEdit', '组织机构编辑', 'Organization Path Edit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OrganizationPathImport', '组织机构导入', 'Organization Path Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportInBatch', '批量导入组织机构信息', 'Import organization path information in batch', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OnlySupportsXlsOrXlsxFormat', '只支持xls或xlsx格式的文件', 'Only supports the files of xls or xlsx format', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportFromExcel', '从Excel导入', 'Import from Excel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FillInTheFileToBeImported', '文件中填入要导入的用户数据', 'Fill in the file with the user data to be imported', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportedData', '导入的数据', 'Imported Data', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Building', '楼', 'Building', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Floor', '层', 'Floor', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ModificationSaved', '修改已经保存', 'Modification Saved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'JSONIncorrect', 'JSON不正确', 'JSON Incorrect', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CannotParseJSON', '不能解析JSON', 'Cannot parse JSON', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchImport3', '批量导入', 'Batch Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportFailureData', '导入失败的数据', 'Import Failure Data', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Institution2', '所属集团*', 'Institution*', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'LoginUserName', '登录用户名', 'Login User Name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TelephoneNumber', '电话', 'Telephone Number', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Activate', '激活', 'Activate', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'HospitalDirector', '院长', 'Hospital Director', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DirectorOfTheMedicalEngineeringDepartment', '医学工程处主任', 'Director of the Medical Engineering Department', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Engineer', '工程师', 'Engineer', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TBD', '待定', 'TBD', NULL, '-1');



--INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','FileUploadFailed','文件上传失败!','File upload failed!',NULL,-1);
--INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','Close2','结单','Close',NULL,-1);
--INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','EquipmentCannotBeTerminated','设备[%s]未处于待报废状态，不能报废！','The equipment [%s] is not to be termianted and cannot be terminated!',NULL,-1);
--INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','UnfinishedWorkorderCandBeTerminated','设备名称[%s]存在未完成工单，不能报废！','The equipment name [%s] has an unfinished workorder and cannot be terminated!',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','SwappedInRentPleaseReturnItFirst','操作失败，设备名称[%s]，设备编号[%s]为在借中的调剂设备，请先归还！','Operation Failed. The equipment name [%s] and ID [%s] are swapped in rent, please return it first!',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','PleaseSetTheEquipmentClassification','请设置设备分类','Please set the equipment classification',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','PleaseSetTheRepairOwnerOrPMOwner','请设置维修负责人或保养负责人','Please set the repair owner or PM owner',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','SaveSuccess','保存成功','Save Success',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DeleteSuccess','删除成功','Delete Success',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','PleaseSetTheDepartment','请设置所属科室','Please set the department',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserIsAssignerAndCannotBeDeleted','该用户是派工人,不能删除.','The user is assigner and cannot be deleted.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserIsAssignerAndCannotBeDisable','该用户是派工人,不能停用.','The user is assigner and cannot be disable.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserIsEquipmentOwnerAndCannotBeDeleted','该用户是设备责任人,不能删除.','The user is equipment owner and cannot be deleted.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserIsEquipmentOwnerAndCannotBeDisable','该用户是设备责任人,不能停用.','The user is equipment owner and cannot be disable.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ListedInTheGroupOfMintenanceEngineersCannotBeDeleted','该用户在维修工程师群组中,不能删除.','The user is listed in the group of mintenance engineers and cannot be deleted.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ListedInTheGroupOfMintenanceEngineersCannotBeDisable','该用户在维修工程师群组中,不能停用.','The user is listed in the group of mintenance engineers and cannot be disable.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserHasAnOnGoingWorkOrderAndCannotBeDeleted','该用户有在修工单,不能删除.','The user has an on-going workorder and cannot be deleted.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','TheUserHasAnOnGoingWorkOrderAndCannotBeDisable','该用户有在修工单,不能停用.','The user has an on-going workorder and cannot be disable.',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ResetSuccess','重置成功','Reset Success!',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ActivateSuccess','激活成功！','Activate Success!',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ForTheSystemActivation','系统激活专用','For the System Activation',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','NoData','无数据','No Data!',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','NoCompletionOfTheAssetDepartment','使用科室未填，不能生成保养工单','Cannot generate the maintenance workorder for no completion of the asset department',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','CreateSuccess','创建成功','Create Success',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','CreateFailed','创建失败','Create Failed',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','MessageSent','消息已发送','Message Sent',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','CreatedAfterSaving','当前院区无配置,保存后创建','No configuration in the current site, created after saving',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ModifiedBy','修改人','Modified by',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','AssetFieldSettings','资产字段设置','Asset Field Settings',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayedField','显示的字段','Displayed Field',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ReadableRole','可读取角色','Readable Role',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ExportableRole','可导出角色','Exportable Role',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ModifiableRole','可修改角色','Modifiable Role',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','AssetListPage','资产列表页面','Asset List Page',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','AssetDetailPage','资产详细页面','Asset Detail Page',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','BasicInformationFieldset','基本信息字段组','Basic Information Fieldset',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DetailFields','详细信息字段组','Detail Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','AssetEditPage','资产编辑页面','Asset Edit Page',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','BasicFields','基本信息字段组','Basic Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','CodeFields','编码信息字段组','Code Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','StatusFields','状态信息字段组','Status Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','CategoryFields','分类信息字段组','Category Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DateFields','日期信息字段组','Date Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','FinancialFields','财务信息字段组','Financial Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','WeChatAssetPage','微信端资产页面','WeChat Asset Page',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','HeaderFields','头部信息字段组','Header Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','RepairFields','维修信息字段组','Repair Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','WeChatRequestPage','微信端报修页面','WeChat Request Page',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','AssetInformationDisplayFields','资产信息显示字段组','Asset Information Display Fields',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ProvideTheRequestInformation','报修信息填写','Provide the request information',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayPriority','显示紧急程度','Display Priority',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','Default','默认值','Default',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayAssetStatus','显示资产状态','Display Asset Status',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','RequestPageDisplay','报修页面显示','Request Page Display',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ProvideTheFaultDescription','故障描述填写','Provide the fault description',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayImageUploaded','显示上传图片','Display Image Uploaded',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','Required','必填','Required',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayVoiceUploaded','显示上传语音','Display Voice Uploaded',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','DisplayTextDescription','显示文字描述','Display Text Description',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','VoiceOrTextRequired','语音文字必填一项','Voice or Text Required',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','PartTypeDefault','备件类型默认值','Part Type Default',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','Onekey','一键','Onekey',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','BasicEquipmentInformationRequired','设备基本信息是否必填','Basic Equipment Information Required?',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','InstallationOfTheArrivalsRequired','到货安装是否必填','Installation of the arrivals required?',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','ColumnWidth','列宽','Column Width',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','NoAddableField','无可添加字段','No Addable Field',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','Asset','资产','Asset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Create2', '创建', 'Create', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','disableTenant','禁用租户','Disable Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','disableTenantConfirm','您确定要禁用此租户么?','Are you sure you want to disable this tenant?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deactivateAccount','禁用账户','Deactivate Account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deactivateAccountConfirm','您确定要禁用此账户么?','Are you sure you want to deactivate this account?',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentLocation', '设备定位', 'Equipment Location', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetApproval', '资产审核', 'Asset Approval', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSwap', '集中调剂', 'Equipment Swap', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestForBorrow', '借用申请', 'Request for Borrow', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BorrowAcceptance', '借用受理', 'Borrow Acceptance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestForReturn', '归还申请', 'Request for Return', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReturnAcceptance', '归还受理', 'Return Acceptance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BorrowAndReturnMaintenance', '借还维护', 'Borrow & Return Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BatchEquipmentSwap', '设备批量调剂', 'Batch Equipment Swap', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentCharging', '设备计费情况', 'Equipment Charging', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentInventory', '设备库存情况', 'Equipment Inventory', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SwappedEquipmentConfiguration', '调剂设备配置', 'Swapped Equipment Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SwappedEquipmentNameSettings', '调剂名称设置', 'Swapped Equipment Name Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementWorkbench', '计量工作台', 'Measurement Workbench', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementCategoryConfiguration', '计量类别设置', 'Measurement Category Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementInformation', '计量信息', 'Measurement Information', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MeasurementReportTemplateManagement', '计量报告模板管理', 'Measurement Report Template Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DailyCheck', '日常检查', 'Daily Check', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DailyCheckSettings', '日常检查设置', 'Daily Check Settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DailyCheckReport', '日常检查报表', 'Daily Check Report', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UseRegistrationCheckSettings', '使用登记检查设置', 'Use registration check settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UseRegistrationCheckItemSettings', '使用登记检查项设置', 'Use registration check item settings', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'StartupAndShutdownRegistration', '开关机登记', 'Startup/Shutdown Registration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetOptUnClosedList', '未关机设备列表', 'AssetOptUnClosedList', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UseRegistration', '使用登记', 'Use registration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BigScreenForEquipmentManagement', '设备管理大屏', 'Big Screen for Equipment Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DepartmentTransactionManagement', '科务管理', 'Department Transaction Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CTBulbTubePrediction', 'CT球管预测', 'CT Bulb Tube Prediction', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentPerformanceImport', '设备绩效导入', 'Equipment Performance Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentPerformanceReportForm', '设备绩效报表', 'Equipment Performance Report Form', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'BIPerformanceAnalysisPlatform', 'BI绩效分析平台', 'BI Performance Analysis Platform', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SmallScreenMessageConfiguration', '小屏消息配置', 'Small-screen Message Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MVSEquipmentManagement', 'MVS设备管理', 'MVS Equipment Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PurchaseManagement', '采购管理', 'Purchase Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExaminationManagement', '检测管理', 'Examination Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ProbeExamination', '探头检测', 'Probe Examination', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ProbeExaminationConfiguration', '探头检测配置', 'Probe Examination Configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SSOUserImport', 'SSO用户导入', 'SSOUserImport', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'APMPlatformManagement', 'APM平台管理', 'APM Platform Management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SRReportList', 'SR报告清单', 'SR Report List', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AlertSettingsList', '报警设置清单', 'Alert Settings List', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WelcomeToTheGEAPMAssetCloudPlatform', '欢迎使用 GE APM 资产云平台', 'Welcome to the GE APM Asset Cloud Platform', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FirstUserLoginActivation', '用户首次登录激活', 'First User Login Activation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AgreementAndConfirmToActivateTheAccountNotice', '我已阅读相关用户协议并确认激活账号', 'I have read the relevant user agreement and confirm to activate the account', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence1', '当您勾选选择“已阅读相关用户协议并确认激活账号”，即表示您已经达成本协议，承诺并遵守本协议的规定， 正式激活用户账号，同意并接受全部服务条款及条件。', 'When you select "I have read the relevant user agreement and confirm to activate the account",  it means that you have reached this agreement; promised to abide by the provisions of this agreement; formally activate the user account; and agree and accept all the terms and conditions of service.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence2', '用户在使用本服务时，应当遵守《中华人民共和国宪法》、《中华人民共和国刑法》、《中华人民共和国民法通则》、《中华人民共和国合同法》、《中华人民共和国著作权法》、《中华人民共和国电信条例》、《互联网信息服务管理办法》、《互联网电子公告服务管理规定》、《计算机信息网络国际互联网安全保护管理办法》等相关规定。配套增值电信服务由上海理想信息产业（集团）有限公司直接向客户提供，客户同意由电信负责云运行。用户不得利用本服务产品从事违反法律法规、政策以及侵犯他人合法权益的行为，包括但不限于下列行为：', 'When using this service, users shall abide by the Constitution of the People''s Republic of China, the Criminal Law of the People''s Republic of China, the General Principles of the Civil Law of the People''s Republic of China, the Contract Law of the People''s Republic of China, the Copyright Law of the People''s Republic of China, the Regulations of the People''s Republic of China on Telecommunications, the Measures for the Administration of Internet Information Services, the Regulations on the Administration of Internet Electronic Announcement Services, and the Measures for the Administration of Internet Security of Computer Information Networks. The supporting value-added telecommunications services are provided directly by the Shanghai Ideal Information Industry (Group) Co., Ltd., and the customers agree that the telecommunications is responsible for the cloud operation. Users may not use this service product to engage in violations of laws, regulations, policies, and violations of the lawful rights and interests of others, including but not limited to the following:', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence11', '1.1 利用服务产品发表、传送、传播、储存反对宪法所确定的基本原则的、危害国家安全、国家统一、社会稳定的、煽动民族仇恨、民族歧视、破坏民族团结的内容，或侮辱诽谤、色情、暴力、引起他人不安及任何违反国家法律法规政策的内容或者设置含有上述内容的网名、角色名。', '1.1 Use the service product to publish, transmit, disseminate and store any content that is contrary to the basic principles established by the Constitution, endangering national security, national unification and social stability, inciting ethnic hatred and ethnic discrimination, and undermining national unity, or constitutes any insult. slander, pornography and violence, causing uneasiness to others, and that violates the national laws, regulations and policies, or sets a name for the network name or role that contains the above content.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence12', '1.2 利用本服务发表、传送、传播、储存侵害他人知识产权、商业机密、肖像权、隐私权等合法权利或其他道德上令人反感的内容。', '1.2 Use this service to publish, transmit, disseminate and store any content that infringes on others’ legal rights (e.g. intellectual property rights, trade secrets, portrait rights and privacy rights), or is morally offensive.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence13', '1.3 进行任何危害计算机网络安全的行为，包括但不限于：使用未经许可的数据或进入未经许可的服务器/帐户; 未经允许进入公众计算机网络或者他人计算机系统并删除、修改、增加存储信息;未经许可，企图探查、扫描、测试本“软件”系统或网络的弱点或其它实施破坏网络安全的行为;企图干涉、破坏本“软件”系统或网站的正常运行，故意传播恶意程序或病毒以及其他破坏干扰正常网络信息服务的行为;伪造TCP/IP数据包名称或部分名称;自行或利用其他软件对本产品进行反向破解等违法行为。', '1.3 Have any act of endangering the computer network security, including but not limited to: use of unlicensed data or access to unlicensed servers/accounts; unauthorized access to public computer networks or others’ computer systems, and deletion, modification and addition of any stored information; attempt to probe, scan and test the weaknesses of the "software" system or network, or other actions that undermine network security; attempt to interfere with and disrupt the normal operation of the "software" system or website, and intentionally spread any malicious program or virus and other acts that disrupt or interfere with the normal network information services; falsifying the TCP/IP packet names or partial names; and illegally cracking the product by itself or by using any other software.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence14', '1.4 进行任何诸如发布广告、推广信息、销售商品的行为，或者进行任何非法的侵害服务提供商利益的行为。', '1.4 Have any act of advertising, information promotion, sales of goods, or any illegal infringement upon the service provider’s interests. ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Lisence15', '1.5 进行其他任何违法以及侵犯其他个人、公司、社会团体、组织的合法权益的行为或者法律、行政法规、规章、条例以及任何具有法律效力之规范所限制或禁止的其他行为。', '1.5 Have any other act that violates the laws and infringes the lawful rights and interests of other individuals, companies, social groups or organizations, or are restricted or prohibited by the laws, administrative regulations, rules, regulations, and any legally binding norms.', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WelcomeToAPMManagementPlatform', '欢迎登录APM管理平台！', 'Welcome to APM Management Platform!', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CheckAndStore', '核对入库', 'Check and Store', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetOutStock', '资产出入库', 'Assets in and out of warehouse', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'srNum_xlsx', '报修单号', 'RequestWorkorderNumber', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentID_xlsx', '设备编号', 'EquipmentID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetID_xlsx', '资产编号', 'AssetID', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSerialNumber_xlsx', '设备序列号', 'EquipmentSerialNumber', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentName_xlsx', '设备名称', 'EquipmentName', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderStatus_xlsx', '工单状态', 'WorkorderStatus', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Site_xlsx', '院区', 'Site', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Department_xlsx', '科室', 'Department', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Manufacturer_xlsx', '制造商', 'Manufacturer', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentSerialNumber_xlsx', '品牌', 'Brand', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentClassification_xlsx', '设备分类', 'EquipmentClassification', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MedicalDeviceClassification_xlsx', '医疗器械分类(旧)', 'MedicalDeviceClassification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MedicalDeviceSubclassification_xlsx', '医疗器械子分类(旧)', 'MedicalDeviceSubclassification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EquipmentModel_xlsx', '设备型号', 'EquipmentModel', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentOwner_xlsx', '当前责任人', 'CurrentOwner', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentStep_xlsx', '当前步骤', 'CurrentStep', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestTime_xlsx', '报修时间', 'RequestTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestorTelephone_xlsx', '报修人电话', 'RequestorTelephone', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FaultDescription_xlsx', '故障描述', 'FaultDescription', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssignTime_xlsx', '派工时间', 'AssignTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkorderType_xlsx', '工单类型', 'WorkorderType', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairType_xlsx', '维修类型', 'RepairType', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Acceptedby_xlsx', '接单人', 'Acceptedby', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AcceptTime_xlsx', '接单时间', 'AcceptTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CheckInTime_xlsx', '签到时间', 'CheckInTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairCompleteTime_xlsx', '维修完成时间', 'RepairCompleteTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceEngineerManhours_xlsx', '维修工程师工时', 'MaintenanceEngineerManhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TotalManhours_xlsx', '维修总工时', 'TotalManhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RepairPart_xlsx', '维修备件', 'RepairPart', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TotalPartPrice_xlsx', '备件总费用', 'TotalPartPrice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OtherExpenses_xlsx', '其他费用', 'OtherExpenses', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExternalExpenses_xlsx', '外修费用', 'ExternalExpenses', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TotalRepairPrice_xlsx', '维修总费用', 'TotalRepairPrice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AcknowledgedBy_xlsx', '验收人', 'AcknowledgedBy', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'maintanenceValue_xlsx', '维修价值', 'maintanenceValue', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AcceptTime_xlsx2', '验收时间', 'AcceptTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Closedby_xlsx', '关单人', 'Closedby', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CloseTime_xlsx', '关单时间', 'CloseTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CoworkerManhours_xlsx', '协作者工时', 'CoworkerManhours', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'DownTime_xlsx', '停机时间', 'DownTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RecoveryTime_xlsx', '恢复时间', 'RecoveryTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NearestWorkDays_xlsx', '修复后无故障天数', 'NearestWorkDays', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FaultType_xlsx', '故障类型', 'FaultType', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FinalRating_xlsx', '最终评价', 'FinalRating', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TestbeforeDelivery_xlsx', '交付前检测', 'TestbeforeDelivery', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'QRCode_xlsx', '二维码', 'QRCode', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ReasonDescription_xlsx', '原因描述', 'ReasonDescription', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CurrentLocation_xlsx', '当前位置', 'CurrentLocation', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'RequestStatus_xlsx', '报修状态', 'RequestStatus', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetName_xlsx', '资产名称', 'AssetName', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartName_xlsx', '备件名', 'PartName', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ContractPrice_xlsx', '合同价格', 'ContractPrice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SerialNumber_xlsx', '序列号', 'SerialNumber', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WarrantyDate_xlsx', '质保期', 'WarrantyDate', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ContractNumber_xlsx', '合同号', 'ContractNumber', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InvoiceNumber_xlsx', '发票号', 'InvoiceNumber', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'InvoiceTime_xlsx', '开票日期', 'InvoiceTime', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'EstimatedPrice_xlsx', '预计价格', 'EstimatedPrice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartName_xlsx2', '备件名称', 'PartName', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ClosedPrice_xlsx', '发生费用', 'ClosedPrice', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CloseStatus_xlsx', '结单状态', 'CloseStatus', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsPrice_xlsx', '单价', 'CloseStatus', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'GroundElectricLeakageSingleError', '对地漏电流(单一故障状态)', 'Ground Electric Leakage (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ShellElectricLeakageNormal', '外壳漏电流(正常状态)', 'Shell Electric Leakage (Normal)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ShellElectricLeakageSingleError', '外壳漏电流(单一故障状态)', 'Shell Electric Leakage (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientElectricLeakageACNormal', '患者漏电流AC(正常状态)', 'Patient Electric Leakage AC (Normal)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientElectricLeakageACSingleError', '患者漏电流AC(单一故障状态)', 'Patient Electric Leakage AC (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientElectricLeakageDCNormal', '患者漏电流DC(正常状态)', 'Patient Electric Leakage DC (Normal)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientElectricLeakageDCSingleError', '患者漏电流DC(单一故障状态)', 'Patient Electric Leakage DC (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientAssistElectricLeakageACNormal', '患者辅助电流AC(正常状态)', 'Patient Assist Electric Leakage AC (Normal)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientAssistElectricLeakageACSingleError', '患者辅助电流AC(单一故障状态)', 'Patient Assist Electric Leakage AC (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientAssistElectricLeakageDCNormal', '患者辅助电流DC(正常状态)', 'Patient Assist Electric Leakage DC (Normal)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PatientAssistElectricLeakageDCSingleError', '患者辅助电流DC(单一故障状态)', 'Patient Assist Electric Leakage DC (Single Error)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CityVoltageActualValue', '市电电压实际值', 'City Voltage Actual Value', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToLoadExcelTemplate', 'Excel模版加载失败', 'Failed to load Excel template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToReadTemplate', '读取模版失败', 'Failed to read template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NameCannotBeEmpty', '名称不能为空', 'Name cannot be empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NameCannotExceed64DigitsInLength', '名称最大长度64', 'Name cannot exceed 64 digits in length', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'JSONSaved', 'JSON已保存', 'JSON Saved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TheFieldChanged', '字段变动, JSON已保存为新版本', 'The field changed, and JSON is saved as a new version', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'KeyHasBeenUsed', 'Key已经被使用', 'Key has been used', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ResultKeyHasBeenUsed', '结果Key已经被使用', 'Result Key has been used', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CannotParseIntoATemplate', '不能解析成模版', 'Cannot parse into a template', NULL, '-1');
--INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToLoadExcelTemplate', 'Excel模版加载失败', 'Failed to load Excel template', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ExcelSaved', 'Excel已保存', 'Excel Saved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToGenerateExcelTemplate', 'Excel模版生成失败', 'Failed to generate Excel template', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OnlyContainEnglishNumbersAndUnderscores', '只能包含,英文,数字,下划线', 'Can only contain English, numbers and underscores', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'FailedToParseTemplate', '解析模版失败', 'Failed to parse template', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Saved', '已保存', 'Saved', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Submitted', '已提交', 'Submitted', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'NoCorrespondingDataFound', '未找到对应数据', 'No corresponding data found', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PMPlanTime', '保养计划时间', 'PM Plan Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'assetSearchingConditions', '相关设备查询条件([所属院区]为必选条件，其它查询条件最少要填写或选择一个)', 'Relevant equipment query conditions (the hospital area to which it belongs) is a mandatory condition, other query conditions must be filled in or selected at least one)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'assetSearchingConditions2', '相关设备查询条件([所属院区]为必选条件，其它查询条件设备分类和所属科室最少要填写或选择一个)', 'Relevant equipment query conditions (the hospital area to which it belongs) is a mandatory condition, other query conditions must be filled in or selected at least one)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'relationAsset', '相关设备', 'relationAsset', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'relationAttachment', '相关附件', 'relationAttachment', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbAttachmentType','ServiceManual','维修手册','ServiceManual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbAttachmentType','OperationManual','运营手册','OperationManual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbAttachmentType','OperationVideo','运营视频','OperationVideo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbAttachmentType','MaintenanceList','保养清单','MaintenanceList',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbAttachmentType','Others','其他','Others',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbOperationType','A','A类','A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbOperationType','C','C类','C',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbOperationType','M','M类','M',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbMainDirectory','DXR','DXR','DXR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbMainDirectory','OEC','OEC','OEC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbMainDirectory','CT','CT','CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbMainDirectory','ULS','ULS','ULS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbMainDirectory','Others','Others','Others',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','3','ZKB租户','ZKB Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantType','4','ZTB租户','ZTB Tenant',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mainDirectory','Modality','Modality',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','secondDirectory','Product','Product',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operationType','操作类型','Operation Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','attachmentList','附件列表','Attachment List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKnowledgeMgmt','智康保文档管理','ZKB Knowledge Mgmt',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKnowledgeView','智康保文档查看','ZKB Knowledge View',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersion','ZKBVersion','ZKB版','ZKB Version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','knowledge.zkbMgt','智康保文档管理','ZKB Document Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','knowledge.zkbView','智康保文档查看','ZKB Document view',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContactPerson','智康保联系人','ZKBContactPerson',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContactInfo','联系人信息','ZKBContactInfo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_tenant','租户','tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_name','负责人','name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_phone','电话','phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_email','邮箱','email',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_loginName','用户名','loginName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_position','职务','position',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_zipcode','邮编','zipcode',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_address','地址','address',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContact_remark','备注','remark',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','Engineer','工程师','engineer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','EngineerManager','工程师经理','engineer manager',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','AfterSales','售后销售','after sales',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','AfterSalesManager','售后销售经理','afterSales manager',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','MaintenanceManager','维修部经理','maintenance manager',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','AssetSales','设备销售','asset sales',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','AssetSalesManager','设备销售经理','asset sales manager',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','GeneralManager','总经理','general manager',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContactPosition','ZKBAdmin','智康保管理员','zkb admin',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer','智康保工程师','ZKBEngineer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineerInfo','工程师信息','ZKBEngineerInfo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineerTrackRecord','工程师工作经历','engineerTrackRecord',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineerAttachFileInfo','附件信息','AttachFile Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_tenant','租户','tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_name','姓名','name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_phone','电话','phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_email','邮箱','email',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_status','认证状态','status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_age','年龄','age',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_educationalBackground','学历','educationalBackground',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_graduateSchool','毕业院校','graduateSchool',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_specialty','专业','specialty',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_companyName','公司名称','companyName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_position','职务','position',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_hireDate','入职时间','hireDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_workSeniority','工作年限','workSeniority',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbEngineer_identityNumber','身份证号','identityNumber',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbBasicInfo','公司档案','ZKB Company Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbBasicInfo.agentContactPersonMgt','基本信息','ZKB Agent Contact Person Basic Info Mgt',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbBasicInfo.engineerMgt','工程师信息','ZKB Engineer Info Mgt',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbContract','智康保合同','ZKB contract Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbContract.contractMgt','智康保合同管理','ZKB contract Info Mgt',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','zkbContract.courseMgt','智康保课程管理','ZKB course Info Mgt',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ZKBBasicInfo','公司档案','Company Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ZKBAgentContactPersonMgt','基本信息','Contact Person Basic Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ZKBEngineerMgt','工程师信息','Engineer Info',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_agentName','代理商','agentName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_contractNo','合同号','contractNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_region','区域','region',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_contractType','合同类型','contractType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_startDate','合同开始日期','startDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_endDate','合同结束日期','endDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_contractState','合同状态','contractState',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_ibNumber','IB数量','ibNumber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareQuota','备件剩余名额','spareQuota',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareQuota_total','备件总额','spareQuotaTotal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareQuota_apply','备件申请名额','spareQuotaApply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareQuota_use','备件已使用名额','spareQuotaUse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_probeQuota','探头剩余名额','probeQuota',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_probeQuota_total','探头总额','probeQuotaTotal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_probeQuota_apply','探头申请名额','probeQuotaApply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_probeQuota_use','探头已使用名额','probeQuotaUse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_geArtificialSupportTimes','人工支持剩余次数','geArtificialSupportTimes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_geArtificialSupportTimes_total','人工支持总次数','geArtificialSupportTimesTotal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_geArtificialSupportTimes_apply','人工支持申请次数','geArtificialSupportTimesApply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_geArtificialSupportTimes_use','人工支持已使用次数','geArtificialSupportTimesUse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_remark','备注','remark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_maintenanceQuota','保养名额','maintenanceQuota',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareArtificail','备件+人工+保养剩余名额','spareArtificail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareArtificail_total','备件+人工+保养总额','spareArtificailTotal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareArtificail_apply','备件+人工+保养申请名额','spareArtificailApply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_spareArtificail_use','备件+人工+保养已使用名额','spareArtificailUse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_consumableQuota','耗材剩余名额','consumableQuota',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_consumableQuota_total','耗材总额','consumableQuotaTotal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_consumableQuota_apply','耗材申请名额','consumableQuotaApply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_consumableQuota_use','耗材已使用名额','consumableQuotaUse',NULL,-1);




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_principal','负责人','principal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_principalPhone','负责人电话','principalPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_principalEmail','负责人邮箱','principalEmail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_admin','管理员','admin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_adminPhone','管理员电话','adminPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_adminEmail','管理员邮箱','adminEmail',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContract_adminAddress','管理员地址','adminAddress',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','PCB','PCB','PCB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','Biomed','Biomed','Biomed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','CCS','CCS','CCS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','DI','DI','DI',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','PCB MVS','PCB MVS','PCB MVS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractType','CCSB','CCSB','CCSB',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractRegion','North','North','North',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractRegion','South','South','South',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractRegion','East','East','East',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractRegion','West','West','West',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractAssetType','highMachine','超声高端机','highMachine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractAssetType','lowMachine','超声低端机','lowMachine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractAssetType','empty','N/A','N/A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractAssetType','depot','depot','N/A',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractModifyType','OC','OC','OC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractModifyType','NextYearBalance','次年结余','Next Year Balance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractModifyType','Others','其他','Others',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractCatEnabled',true,'可用','true',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractCatEnabled',false,'不可用','true',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorCourseNameIsEmpty', '错误：课程名称为空', 'Error: Course Name is empty', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorCourseIdIsEmpty', '错误：课程ID为空', 'Error: Course ID is empty', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorCourseContractNoEmpty', '错误：培训合同号为空', 'Error: Course Contract No is empty', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorCourseIsNotExist', '错误：课程不存在', 'Error: Course is not Exist', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractNoIsEmpty', '错误：合同号为空', 'Error: ContractNo is empty', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractNoIsExist', '错误：合同号已存在', 'Error: ContractNo is Exist', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractIsNotExist', '错误：合同号不存在', 'Error: Contract is not Exist', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractCourseIsExist', '错误：合同课程已存在', 'Error: Contract Course is  Exist', NULL, -1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorZkbContractEndTimeEmpty', '错误：结束时间不能为空', 'Error: End Time is empty.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorZkbContractTenantUIDEmpty', '错误：代理商不能为空', 'Error: TenantUID is empty.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorZkbContractTenantUIDExist', '错误：代理商不存在', 'Error: TenantUID is exist.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorImpZkbQrCodeIsEmpty', '错误：设备二维码不能为空', 'Error: QrCode is empty.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorImpZkbQrCodeIsNotExist', '错误：设备二维码不存在', 'Error: QrCode is not exist.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorImpZkbContractAssetIsExist', '错误：合同设备已存在', 'Error:Contrac tAsset is Exist.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorImpZkbContractAssetIsNotExist', '错误：合同设备不存在', 'Error: Contract Asset is not Exist.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorZkbRegionIsNotRange', '错误：区域值不在取值区间内', 'Error: The regional value is not within the value range.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorZkbContractTypeIsNotRange', '错误：合同类型不在取值区间内', 'Error: The Contract type is not in the value range.', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorRegionIsEmpty', '错误：区域为空', 'Error: Region is empty', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorContractTypeIsEmpty', '错误：合同类型为空', 'Error: Contract Type is empty', NULL, -1);


--Other显示中文修改
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'Other002', '其他', 'Other', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','userPosition','职务','Position',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','dean','院长','Dean',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','deviceDirector','设备科主任','Device Director',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','hospitalTechnician','医院工程师','Hospital Technician',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','deviceOtherStaff','设备科其他人员','Device Other Staff',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','radiologyDirector','放射影像科主任','Radiology Director',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','radiologyTechnicianLead','放射科技师长','Radiology Technician Lead',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','radiologyTechnician','放射科技师','Radiology Technician',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','radiologyOtherStaff','放射科其他人员','Radiology Other Staff',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','otherDirector','其他科室主任','Other Director',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','headNurse','护士长','Head Nurse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','ultrasoundDirector','超声科主任','Ultrasound Director',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','ultrasoundTechnician','超声科技师','Ultrasound Technician',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','ultrasoundNurse','超声科护士','Ultrasound Nurse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','ultrasoundDoctor','超声科医师','Ultrasound Doctor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','ultrasoundOtherStaff','超声科其他人员','Ultrasound Other Staff',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','deviceRepairer','设备维修人','Device Repairer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('userPosition','other','其他','Other',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetTags','标签','Asset Tags',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','userTags','标签','User Tags',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','singleSiteImport','单院区导入','Single Site Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','crossSiteImport','跨院区导入','Cross Site Import',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','disableUploadInSiteSeparationMode','院区隔离下不能上传跨院区文件！','You cannot upload cross site asset file in site separation mode.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','organizationNotFound','机构没有找到！','Organization not found.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','organizationHierarchyWrong','机构层级设置错误！','Organization hierarchy wrong.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MyTrain','我的培训','My Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TrainReport','培训报告','Train Report',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'TrainBookTime', '培训预约时间', 'Train Appointment Time', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'connectToInsite', '连通状态', 'Connect Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('connectToInsite', '1', '未连通', 'Not Connect', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('connectToInsite', '2', '连通', 'Connecting', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('connectToInsite', '3', 'N/A', 'N/A', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ibDeviceStatus', 'IB状态', 'IB Status', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('ibDeviceStatus', '1', '未见异常', 'No Exception', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('ibDeviceStatus', '2', '报警', 'In Alarm', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('ibDeviceStatus', '3', '维修中', 'In Maintenance', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('ibDeviceStatus', '4', '无连通无报警', 'Not Connected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('ibDeviceStatus', '5', 'NA', 'No Exception Basic', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','TechnicalPending','待技术支持审批','Technical Support pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','ProductPending','待产品经理审批','Product pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','ManagePending','待经理审批','Manage pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','DirectorPending','待总监审批','Director pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','CommercialPending','待Commercial审批','Commercial pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','OrderExtraPending','待开单','UnOrder pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','OrderPending','待下单','Ordered pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','ReturnPending','待归还','Return pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','ClosePending','待审核','Close pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','Complete','完成','Complete',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','FourHundredCancel','400取消派工','FourHundredCancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','TsePending','待TSE审批','Tse pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','OsscPending','待OSSC确认','Ossc pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','OsscReturnPending','待OSSC取货寄回','Ossc return pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','CompletePending','待关单','Complete Pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoSteps','CompleteAuditPending','待关单审核','Complete Audit Pending',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','OrderCommon','400公共邮箱','Order Common',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','TechnicalPending','技术支持','Technical Support pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','ProductPending','产品经理','Product pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','ManagePending','区域经理','Manage pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','OrderExtraPending','400开单人','UnOrder pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','OrderPending','400下单人','Ordered pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','ClosePending','GE Admin','Close pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','DirectorPending','销售总监','Director pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','ProbeLetterSend','收函发送角色','Probe Letter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','ProbeLetterCC','收函抄送角色','Probe Letter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','TechnicalPendingCTCc','技术支持CT抄送','Technical Support pending cc',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','CommercialPending','Commercial审批人','Commercial pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','TsePending','TSE','Tse pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','OsscPending','OSSC','Ossc pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','CompleteAuditPending','关单审核人','Complete Audit Pending',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','OperationsTeam','运营团队','Operations Team',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole','EngineerQualificationPending','工程师资质审批','Engineer Qualification Pending',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','Requested','已申请','Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','TechnicalAccept','技术支持审批通过','Technical Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','TechnicalReject','技术支持审批拒绝','Technical Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','ProductAccept','产品经理审批通过','Product Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','ProductReject','产品经理审批拒绝','Product Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','ManageAccept','经理审批通过','Manage Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','ManageReject','经理审批拒绝','Manage Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','DirectorAccept','总监审批通过','Director Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','DirectorReject','总监审批拒绝','Director Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','UnOrdered','已开单','UnOrder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','UnOrderedReject','400开单拒绝','UnOrderReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','Ordered','已下单','Ordered',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','OrderedReject','400下单拒绝','OrderedReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','FourHundredCancel','400取消派工','FourHundredCancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CloseReject','关单拒绝','CloseReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','TseAccept','TSE审批通过','Tse Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','TseReject','TSE审批拒绝','Tse Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','OsscAccept','OSSC确认通过','Ossc Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','OsscReject','OSSC确认不通过','Ossc Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','PartReturned','归还中','PartReturned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','Returned','已归还','Returned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','Closed','已完成','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','Cleaned','已取消','Cleaned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CommercialAccept','Commercial审批通过','Commercial Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CommercialReject','Commercial审批拒绝','Commercial Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','ReturnCompleted','归还审核完成','ReturnCompleted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CloseCommited','关单已提交','CloseCommited',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CloseApproved','关单审核通过','CloseApproved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStatus','CloseFailed','关单审核不通过','closeFailed',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnDataStatus','Pending','待审批','Pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnDataStatus','Accept','审批通过','Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnDataStatus','Reject','审批不通过','Reject',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder','额度申请','zkbPartOrder',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_artificialType','机型','artificialType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_contractNo','合同号','contractNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_srNum','工单号','srNum',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_region','区域','region',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_orderType','订单类型','orderType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_partOrderDate','订单日期','partOrderDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_qty','数量','qty',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_remark','备注','remark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_requestUser','申请人','requestUser',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_requestDate','申请时间','requestDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_requestPhone','申请人电话','requestPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_hospitalName','医院名称','hospitalName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_hospitalRepairer','医院报修人姓名','hospitalRepairer',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_hospitalRepairerPhone','医院报修人电话','hospitalRepairerPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_productNumber','产品型号','productNumber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_softwareVersion','软件版本','softwareVersion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_faultDescription','故障描述','faultDescription',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_orderAnnotation','订单标注','orderAnnotation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_problemOrderNumber','发生问题的order number','problemOrderNumber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_urgentOrder','是否为紧急订单','urgentOrder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_urgencyReasons','订单紧急的原因','urgencyReasons',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_techRemark','技术支持审批备注','techRemark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_manageRemark','区域经理审批备注','manageRemark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_srNo','sr#','srNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_activityNo','Act#','activityNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_orderNo','order#','orderNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_orderedRemark','订货备注','orderedRemark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_returnType','归还类型','returnType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_waybillNo','运单号','waybillNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_receiver','收货人','receiver',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_receiverPhone','收货人电话','receiverPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_receiverAddress','收货地址','receiverAddress',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_returnDate','归还日期','returnDate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_returnRemark','归还备注','returnRemark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_closedRemark','关单备注','closedRemark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_whetherDeduct','是否扣除名额','whetherDeduct',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_oldCatSn','旧探头SN号','oldCatSn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_catNo','探头号','catNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_newCatSn','新探头SN编号','newCatSn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_geEngineerName','GE工程师','geEngineerName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_geEngineerPhone','GE工程师电话','geEngineerPhone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_zkbEngineerName','ZKB工程师姓名','zkbEngineerName',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbPartOrder_zkbEngineerPhone','ZKB工程师电话','zkbEngineerPhone',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','badReturn','坏还','Bad Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','goodReturn','好还','Good Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','DOA','DOA','DOA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','Wrong Part','Wrong Part','Wrong Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','Short Life','Short Life','Short Life',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','cancel','cancel','cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','useNoReturn','已使用申请不归还','已使用申请不归还',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','damagedShipment','Damaged in shipment','Damaged in shipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnType','singlePurchase','ZKB转单独采购','ZKB转单独采购',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnExternalPackaging','Damaged','Damaged(破损)','Damaged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnExternalPackaging','Good','Good(完好)','Good',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnExternalPackaging','Tripped','Tilt/Shock indicator Tripped(倾斜/震动标签异常)','Tilt/Shock indicator Tripped',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnInternalPackaging','esdBagDamaged','ESD Bag Damaged(防静电袋破损)','ESD Bag Damaged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnInternalPackaging','esdBagMissing','ESD Bag missing(防静电袋丢失)','ESD Bag missing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnInternalPackaging','Good','Good(完好)','Good',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnInternalPackaging','inadequateToProtectPart','Inadequate to protect part(内部缓冲保护不足)','Inadequate to protect part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnInternalPackaging','packagingInadequate','Packaging and ESD inadequate(包装和防静电不完整)','Packaging and ESD inadequate',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealType','greenFEReseal','Green FE Reseal(绿色标签FE重新密封)','Green FE Reseal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealType','greenSupplierSeal','Green Supplier Seal(绿色标签供应商密封)','Green Supplier Seal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealType','greenWarehouseSeal','Green Warehouse Seal(绿色标签仓库密封)','Green Warehouse Seal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealType','noSeal','No seal(没有密封标签)','No seal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealType','redOrPurple','Red or Purple(红色或紫色标签)','Red or Purple',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealStatus','Broken','Cut/Broken(破损的)','Cut/Broken',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealStatus','notSealBox','Intact But Did not Seal Box(完好无损但未封箱)','Intact But Did not Seal Box',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealStatus','good','Intact/Good(完好的)','Intact/Good',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnQualitySealStatus','noQualitySeal','No Quality Seal(没有密封标签)','No Quality Seal',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQuality','Damaged','Bad/Damaged(坏的)','Bad/Damaged',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQuality','IntermittentFailure','Intermittent Failure(间隙性坏的)','Intermittent Failure',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQuality','Unknown','Unknown(不知道)','Unknown',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnSafetyConcernType','Potential','Potential(该故障会导致潜在伤害)','Potential',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnSafetyConcernType','Actual','Actual(该故障已导致实际伤害)','Actual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnSafetyConcernType','No','No(该故障不会导致潜在或实际伤害)','No',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPatientUseType','Yes','Yes(有病人使用，存在病患风险)','Yes',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPatientUseType','No','No(没有病人使用，没有病患风险)','No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPatientUseType','NotReported','Not reported(没有告知是否有病患/未报告)','Not reported',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnProcedureType','Diagnosis','Diagnosis(诊断)','Diagnosis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnProcedureType','Treatment','Treatment(治疗)','Treatment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnProcedureType','Monitoring','Monitoring(监控)','Monitoring',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnProcedureType','Declined','Declined(用户拒绝透露)','Declined',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Boot Up Issue','Boot Up Issue(启动故障)','Boot Up Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Cosmetic Issue','Cosmetic Issue(外观目视差(生锈、磨损严重等))','Cosmetic Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Disassembled','Disassembled(部件结构分离)','Disassembled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Function Does Not Work','Function Does Not Work(功能失常)','Function Does Not Work',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Image Quality Issue','Image Quality Issue(影像质量功能)','Image Quality Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Missing/Swapped Component','Missing/Swapped Component(丢失/非常规组件)','Missing/Swapped Component',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Other','Other(其他)','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Physical Damage','Physical Damage(备件实体破损)','Physical Damage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoReturnPartQualityIssue','Used/Dirty','Used/Dirty(被用过/污迹)','Used/Dirty',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoWorkDateType','Travel','Travel(差旅时间)','Travel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoWorkDateType','onsite','onsite(现场维修时间)','onsite',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoWorkDateType','Phone Support','Phone Support(远程支持时间)','Phone Support',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','ContrasRadioPharm','Contrast/RadioPharm injected（造影剂/放射性药物注射）','ContrasRadio Pharmt injected',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','DeviceConnectedToPatient','Device connected to patient（器械连接到患者）','Device connected to patient',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','ImagesScansAndReviewed','Images/Scans acquired/reviewed（图像/扫描采集/审查）','Images/Scans acquired/reviewed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','InfantInWarmer','Infant in warmer/incubator（保暖台/保育箱中的婴儿）','Infant in warmer/incubator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','PatientInjectedWithContrast','Patient injected with contrast（注射造影剂的患者）','Patient injected with contrast',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','ProcedureProcess','Exam/Procedure was in progress（检查/手术进行中）','Exam/Procedure was in progress',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoRiskType','PatientOnTable','Patient was on table（患者位于检查床上）','Patient was on table',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoNoRiskType','PreUseCheckout','Pre use checkout/quality tests（使用前核对检查/质量测试）','Pre use checkout',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoNoRiskType','ServicingDevice','Servicing or handling device（器械检修或处理）','Servicing or handling device',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoNoReportType','RequestedDeclined','Requested, customer declined（已提出请求，客户已拒绝）','Requested, customer declined',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoNoReportType','UseNotReported','Use Not Reported（未报告使用情况）', 'Use Not Reported',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','ProcedureImagingCompleted','Procedure/imaging completed（手术/成像已完成）', 'Procedure/imaging completed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','CaseCompleteVitalsUnaffected','Case complete vitals unaffectd（病例完结，生命体征未受影响）', 'Case complete vitals unaffectd',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','ProcedureImagingRepeated','Procedure/imaging repeated（手术/成像重复）', 'Procedure/imaging repeated',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','ContrastRadioPharmReinjected','Contrast/RadioPharm reinjected（造影剂/放射性药物再注射）', 'Contrast/RadioPharm reinjected',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','ResumedOnAnotherSystem','Resumed on another system（在另一套系统上恢复）', 'Resumed on another system',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','ResumedOnCurrentSystem','Resumed on current system（在当前系统上恢复）', 'Resumed on current system',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','Rescheduled','Rescheduled（重新安排）', 'Rescheduled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','Xfer','Xfer Device, vitals unaffected（Xfer器械，生命体征未受影响）', 'Xfer Device, vitals unaffected',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','PersonInjured','Person Injured（人员受伤）', 'Person Injured',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','MisdiagnosedNeed','Misdiagnosed-Need More Info（误诊 - 需要更多信息）', 'Misdiagnosed-Need More Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','NoPatientInvolved','No Patient Involved（未累及患者）', 'No Patient Involved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','NotReported','Not Reported - Need More Info（未报告 - 需要更多信息）', 'Not Reported - Need More Info',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoPatientImpactOutcome','Other','Other - Impact in more info（其他 - 影响，需要更多信息）', 'Other - Impact in more info',NULL,-1);



-- 归还类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','badReturn','坏还','Bad Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','goodReturn','好还','Good Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','DOA','DOA','DOA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','Wrong Part','Wrong Part','Wrong Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','Short Life','Short Life','Short Life',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','cancel','cancel','cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnType','damagedShipment','Damaged in shipment','Damaged in shipment',NULL,-1);
--not ge  非ge设备归还类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnTypeNotGe','Repaired','已维修完成','Repaired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_zkbPoReturnTypeNotGe','notRepaired','未维修完成','Not repaired',NULL,-1);



-- 县域租户，备件归还类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partReturnType','1','原备件好还','Good Parts Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partReturnType','2','原备件坏还','Bad Parts Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partReturnType','3','更换后坏还','Return Bad Parts After Replacement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','returnNum','归还数量','Return Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','returnType','归还类型','Return Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','returnTime','归还时间','Return Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','countyAreaPartReturnError','关单前县域租户备件必须全部归还','All spare parts must be returned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selectedReturnTypeError','未选择退货类型','No return type selected',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbUrgentType','t','紧急','urgent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbUrgentType','f','不紧急','not urgent',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbUrgentReasonType','0','设备已宕机','Device is down',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbUrgentReasonType','1','场地维修有其他第三方竞争','Site maintenance has other third-party competition',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbUrgentReasonType','2','其他特殊原因（需注明在备注栏）','Other special reasonse',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoAssetTrainReasonFlag','t','是','t',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoAssetTrainReasonFlag','f','否','f',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoAssetTrainReasonFlag','empty','N/A','empty',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartOrderType','Parts','备件申请','Parts Apply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartOrderType','Probe','探头申请','Probe Apply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartOrderType','Artificial','人工申请','Artificial Apply',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartOrderType','Tool','工具申请','Tool Apply',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoArtificialType','1','放射类','Radiology',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoArtificialType','2','超声高端机','Ultrasonic high-end machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoArtificialType','3','超声低端机','Ultrasonic low-end machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoArtificialType','depot','超声Depot产品','超声Depot产品',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','detector','探测器','探测器',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','flatPanelDetector','平板探测器','平板探测器',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','imageIntensifier','影像增强器','影像增强器',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','ccd','CCD','CCD',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','photomultiplier','光电倍增管','光电倍增管',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPartsHighTypeGroup','generalParts','普通备件','普通备件',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbRadiationQuotaType','SpareArtificial','联合名额','联合名额',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbRadiationQuotaType','PartsHigh','高值备件名额','高值备件名额',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCommonYOrN','f','否','NO',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCommonYOrN','t','是','YES',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoValueType','HighValue','高值','HighValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoValueType','NormalValue','普通','NormalValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoValueType','SpecialValue','特殊','SpecialValue',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbExchangeType','artificial','现场人工支持','artificial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbExchangeType','application','现场应用支持','application',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCCsbQuotaType','Repair','维修申请','Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCCsbQuotaType','Maintain','保养申请','Maintain',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoOrderAnnotationType','NormalOrder','常规订单','Normal Order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoOrderAnnotationType','DOA','DOA','DOA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoOrderAnnotationType','Wrong Part','Wrong Part','Wrong Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoOrderAnnotationType','Short Life','Short Life','Short Life',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoProbeQuotaType','Any','任意名额','HighValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoProbeQuotaType','Special','特殊名额','HighValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoProbeQuotaType','Normal','普通名额','HighValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoProbeQuotaType','High','高值名额','HighValue',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoPartQuotaType','SpareArtificial','联合名额','SpareArtificial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoPartQuotaType','Part','备件名额','Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoPartQuotaType','PartsHigh','高值备件名额','PartsHigh',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbPoPartQuotaType','Consumable','耗材名额','Consumable',NULL,-1);




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','directory','目录','DIRECTORY',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('directoryLevel','1','主目录','Main Directory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('directoryLevel','2','子目录','Sub Directory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','directoryName','目录名称','Directory Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','importDirectorySystemId','系统ID','System Id',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','directoryLevel','目录级别','Directory Level',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorSubDirectoryExist', '错误：主目录包含子目录信息', 'Error: sub directory exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDirectoryNameExist', '错误：目录名称已存在', 'Error: directory name exist', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','DirectoryInfoImport','目录信息导入','Directory Infomation Import',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileNeedDataOfDirectory','文件中填入要导入的目录信息','Fill in the file with the directory information to be imported',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDirectorySystemIdIsEmpty', '错误：system id为空', 'Error: system id is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDirectoryMainDirectoryIsEmpty', '错误：主目录为空', 'Error: main directory is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDirectorySecondDirectoryIsEmpty', '错误：子目录为空', 'Error: second directory is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorDirectory', '错误：目录信息不存在', 'Error: directory information is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'ErrorCountDirectory', '错误：未找到需要更新的数据', 'Error:needed data has not been find', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'createdBy', '创建人', 'Created By', NULL, '-1');

-- 基本信息中重要字段修改留痕操作 start
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HistoryOperations','资产修改记录','Modify field history operation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','fieldOriginalValue','字段原值','Field original value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','modifiedValue','修改后值','Modified value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','modifiedTime','修改时间','Modified time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','modifierUploadedFile','修改人上传的文件','Modifier uploaded files',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ModifyRecord','修改记录','Modify record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SignatureFile','签字文件','Signed document',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NoRecords','暂无记录','No records',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetImportant','重要字段','Asset List Page',NULL,-1);
-- 基本信息中重要字段修改留痕操作 end

-- 设备风险评估操作 start
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetRiskAssessment','设备风险评估','Asset risk assessment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ItemsTransferred','元素移动完成','Items Transferred',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NotAssessmentScore','评分未完成','Not Assessment Score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RiskAssessmentSearchRequire','所属院区为必选条件，其他查询条件最少要填写或选择一个','RiskAssessment Search Require',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','HaveRiskAssessment','已有','Have Risk Assessment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NotHaveRiskAssessment','未有','Not Have Risk Assessment',NULL,-1);

-- 设备风险评估操作 end

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('extensionStatus','DeliveryPending','待到货','DeliveryPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('extensionStatus','AcceptancePending','待验收','AcceptancePending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('extensionStatus','AcceptanceComplete','验收完成','AcceptanceComplete',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('extensionStatus','ContractInvalid','合同作废','ContractInvalid',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('fundingSource','HospitalFunding','院经费','HospitalFunding',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('fundingSource','ScientificFunding','科研经费','ScientificFunding',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('fundingSource','ProjectFunding','项目经费','ProjectFunding',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('fundingSource','OtherFunding','其它经费','OtherFunding',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('paymentMode','DirectPayment','直接支付','DirectPayment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('paymentMode','AuthorizePayment','授权支付','AuthorizePayment',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AllInstitutions','所有集团','All Institutions',NULL,-1);

INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbHistory_orderHistory','订单历史','order history import',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbHistory_orderHistoryImport','订单历史导入','order history import',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_systemId','系统ID','System Id',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_dataType','数据类型','Data Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_region','Region','Region',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_agent','代理商','Agent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_contractNo','合同号','Contract No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_contractType','合同类型','Contract Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_contractBeginningTime','合同开始时间','Contract Beginning Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_contractEndingTime','合同结束时间','Contract Ending Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_quantityIb','IB数量','IB Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_labourQuantity','人工名额','人工名额',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_partsQuantity','备件名额','备件名额',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_probeQuantity','探头名额','探头名额',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_countQuantity','人工，备件，保养联合名额','人工，备件，保养联合名额',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_eventDate','发生日期','Event Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_type','类别','类别',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_systemNo','系统编号','System No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_faultDescription','故障描述','Fault Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_hospitalName','医院名称','Hospital Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_machineType','机型','Machine Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_sparePartsNo','备件号','Spare Parts No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_sparePartsPrice','备件价格','Spare Parts Price',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_description','探头描述','Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_orderNo','OrderNo','Order No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_RSESup','RSESup','RSESup',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_quantity','数量','Quantity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_sr','SR','SR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_act','Act','Act',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_sparePartsReturn','归还','Spare Parts Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_comment','Comments','Comments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_insuranceDescription','入保情况','Insurance Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_probeType','探头类型','探头类型',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_useStatus','是否使用名额Y/N','是否使用名额Y/N',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_callNo','CallNo','CallNo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_rowner','SR Owner','SR Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_ownerName','owner name','owner name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_status','SR Status','SR Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_totalContractProbeCount','合同探头总名额','Total Contract Probe Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_applyLimitCount','任意申请限额','Apply Limit Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_normalLimitCount','普通限额','Normal Limit Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_specialLimitCount','特殊限额','Special Limit Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbHistory_highValueLimitCount','高值限额','High Value Limit Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorDataTypeIsEmpty', '错误：数据类型为空', 'Error: data type is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorDataAgentIsEmpty', '错误：代理商为空', 'Error: agent is empty', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorSystemId', '错误：系统ID不存在', 'Error: system id is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorAgent', '错误：代理商不存在', 'Error: agent is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorRegion', '错误：Region不能为空', 'Error: region is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorContractNo', '错误：合同号不能为空', 'Error: contract No is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorContractType', '错误：合同类型不能为空', 'Error: contract type is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbHistory_ErrorSystemNo', '错误：系统编号不能为空', 'Error: system no is not exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('orderHistoryDataType','DI、PCB','DI、PCB','DI、PCB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('orderHistoryDataType','US、Biomed','US、Biomed','US、Biomed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('orderHistoryDataType','LCS','LCS','LCS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('orderHistoryDataType','US探头','US探头','US探头',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbHistory_FileNeedDataOfOrderHistory','文件中填入要导入的历史订单信息','Fill in the file with the order history information to be imported',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbHistory_OrderHistoryUpdateNote','系统ID不为空的时候做更新','Update data when filed of system id is not null',NULL,	-1	);

--备件申请单业务类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('simplePartsRequestType','1','备件申领','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('simplePartsRequestType','2','备件申购','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('simplePartsRequestType','3','备件调拨','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('simplePartsRequestType','4','备件库存盘点','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('simplePartsRequestType','5','备件归还入库','',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsName', '备件名称', 'Parts name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsSupplier', '备件供应商', 'Parts supplier', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsSpecification', '规格', 'Parts specification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsModel', '型号', 'Parts Model', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsBrand', '品牌', 'Parts Brand', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsUnit', '单位', 'Parts unit', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsCategoryId', '备件编码', 'Parts category Id', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsCategory', '备件分类', 'Parts category', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsAvgCost', '均摊成本', 'Avg Cost', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'partsInfo', '备件信息', 'Parts Basic Info', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'partsCategoryMgr', '备件分类管理', 'Parts grouping', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'partsCategoryEdit', '备件分类编辑', 'Parts Grouping Edit', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsEnglishName', '备件英文名称', 'Parts english name', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsItemCode', '备件号', 'Parts item code', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsUnitPrice', '标价', 'Parts Unit Price', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'addChildNode', '新增子节点', 'Add child node', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CreateTopLevelNode', '创建顶层节点', 'Create top level node', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'namePath', '节点路径', 'name Path', NULL, '-1');

-- 待报废权限
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','nurseSiteAssetScrapped','资产报废','Asset Scrapped',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteAssetScrapped','a','资产待报废','Asset ToBe Scrapped',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetScrapped','assetScrapped.tobe','资产待报废','Asset ToBe Scrapped',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'nurseSiteAssetScrapped', '资产报废', 'Asset ToBe Scrapped', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCard','MyRepairWorkorder','我的维修工单','My Repair Workorder',NULL,-1);

-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetImportant','重要字段','Asset List Page',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','GEIBTableExcel','全量IB台账信息导入','GE IB Table Info List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','DealerFEAndSalesExcel','代理商FE/Sales列表','Dealer FE and Sales List',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetClass','管理分类','Manage classifications',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetClass','1','医疗设备','Medical Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetClass','2','后勤资产','Logistical assets',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetClass','3','信息类设备','Information Equipment',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','oneQrCode','一机一码','One QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','oneQrCode','一机一码','One QR Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','oneQrCodeStatus','机码状态','One QR Code Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','oneQrCodeStatus','机码状态','One QR Code Status',NULL,-1);

----KPI---
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbKeyBehaviorIndex_kbi','KPI','KPI',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbKeyBehaviorIndex_kbiImport','KPI导入','KPI import',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_region','Region','Region',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_tenant','租户','Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_beginningTime','开始时间','Beginning Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_endingTime','结束时间','Ending Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_kbiDate','时间','Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_rank','名次','Rank',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_totalScore','总得分','Total Score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_totalFullScore','总权重','Total score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_totalFullScoreConfig','权重配置','Config total full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_partReturnFullScore','备件归还权重','Part return full socre',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_partReturnScore','备件归还得分','Part return socre',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_intactPartReturnRateFullScore','备件好还率权重','Intact part return rate full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_intactPartReturnRateScore','备件好还率得分','Intact part return rate score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_pasteQuickResponseRateFullScore','二维码粘贴率权重','Paste quick response rate full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_pasteQuickResponseRateScore','二维码粘贴率得分','Paste quick response rate score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_apmBillingRateFullScore','APM开单率权重','Apm billing rate full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_apmBillingRateScore','APM开单率得分','Apm billing rate score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_satisfactoryDegreeFullScore','顾客满意度回访权重','Client satisfactory gegree full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_satisfactoryDegreeScore','顾客满意度回访得分','Client satisfactory degree score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_complianceFullScore','合规及健康安全权重','Compliance full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_complianceScore','合规及健康安全得分','Compliance score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_engineerFullScore','工程师配置权重','Engineer full score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_engineerScore','工程师配置得分','Engineer score',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_totalFullScoreError', '错误：权重总分必须等于100', 'Error: Total full score must be 100', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_totalScoreError', '错误：总分不能大于100', 'Error: Total score must be less or equal 100', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_cannotChangeError', '错误：早期数据不能更改', 'Error: can not change old data', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_tenantExist', '错误：租户已经配置权重', 'Error: Information of tenant is alread exist', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyBehaviorIndex_remainDaysContract','剩余天数','Remain days of contract',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_systemIdError', '错误：系统ID不能为空', 'Error: System id can not be null', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbKeyBehaviorIndex_FileNeedDataOfKbi','文件中填入要导入的KPI信息','Fill in the file with the KPI information to be imported',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbKeyBehaviorIndex_FileNeedDataOfUserConfig','文件中填入要导入的USER CONIFG信息','Fill in the file with the user conifg information to be imported',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_fieldThanAnotherFieldErro', '不能大于', ' must not more than ', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'zkbKeyBehaviorIndex_charts', '图表查看', 'Charts', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'zkbKeyBehaviorIndex_quiry', '查询', 'Quiry', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_idCannotBeNull', 'ID不能为空', 'ID can not be null', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_yearCannotBeNull', '年不能为空', 'year can not be null', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_monthCannotBeNull', '月不能为空', 'month can not be null', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyBehaviorIndex_canNotFindTenantByUid', '代理商信息不正确', 'Tenant info not right', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_systemIdError', '错误：SYSTEM ID不存在', '错误：SYSTEM ID不存在', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_loginNameError', '错误：账号不能重复', '错误：账号不能重复', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_loginNameNotExist', 'APM用户不存在，请先在用户管理中创建用户', 'APM用户不存在，请先在用户管理中创建用户', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_emailError', '错误：邮箱必填', '错误：邮箱必填', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_configTypeError', '错误：类型值不对', '错误：类型值不对', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_assetTypeError', '错误：资产值不对', '错误：资产值不对', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbKeyUserConfig_userConfigImport','USER CONFIG导入','USER CONFIG IMPORT',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'zkbKeyUserConfig_deleteError', '错误：该条记录存在数据引用关系，不能删除', '错误：该条记录存在数据引用关系，不能删除', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_systemId','SYSTEM ID','SYSTEM ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_apm','APM用户名','APM Login Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_name','名称','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_email','邮箱','Email',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_phone','电话','Phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_sso','用户SSO','SSO',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_region','区域','Region',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_type','类型','Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_propertyType','资产类型','Property type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_usBackUp','UsBackUp','UsBackUp',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_remark','备注','Remark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_orderType','订单类型','Order type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbKeyUserConfig_holiday','是否休假','Holiday',NULL,-1);






INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_region','East','East','East',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_region','North','North','North',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_region','South','South','South',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_region','West','West','West',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','DI','DI','DI',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','PCB','PCB','PCB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','Biomed','Biomed','Biomed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','CCS','CCS','CCS',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','CCS-A','CCS-A','CCS-A',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHistory_contractType','CCS-B','CCS-B','CCS-B',NULL,-1);


INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_hospitalName','医院名称','Hospital name',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_functionType','设备型号','Function type',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_titile','二维码实施统计表','Statistic',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_countOfImage','图片个数','Count of image',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_contractNo','合同编号','contract no',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_qrCodeOfPaste','二维码是否已实施(粘贴及照片上传)','Qrcode of pasting',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_canNotPast','是否设备无法粘贴二维码','can not pasting',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbAssetStatistic_canNotPastReason','无法粘贴二维码原因','can not pasting reason',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','MSARecommendExcel','MSA方案推荐','MSA Recommend',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','PromotionActivityExcel','促销活动','Promotion Activity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','CarePackageExcel','关爱包','Care Package',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','CoilOpportunityExcel','MR线圈','Coil Opportunity',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','TrainingQuotaExcel','培训名额','Training Quota',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','MVSControlExcel','MVS权限','MVS Control',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','MVSMatrixExcel','MVS能力矩阵','MVS Matrix',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','SparePartExcel','备件信息','Spare Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','IBReportExcel','IBReport报表','IBReport',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','TubeInfoExcel','球管机会','tube',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','IBUInfoExcel','IBU信息','tube',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('gamePlanBatchFileType','ProbeRecommendExcel','探头推荐','tube',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','GamePlanBatchFileImport','Game Plan数据导入','Game plan Batch Data Import',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','installStatus','装机状态','Install Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('installStatus', '0', '已装机', 'Installed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('installStatus', '1', '未装机', 'Shipped', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('installStatus', '2', '拆机', 'Deinstalled', NULL, '-1');




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','201','麻醉机','麻醉机',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','202','呼吸机','呼吸机',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','203','监护仪','监护仪',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','204','母婴产品','母婴产品',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','221','心电产品-心电图机','心电产品-心电图机',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','222','心电产品-运动心电','心电产品-运动心电',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','223','MUSE网络','MUSE网络',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','231','电生理','电生理',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','232','除颤仪','除颤仪',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('geDeviceType','233','采集模块','采集模块',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority','1','紧急','Urgent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority','2','重要','Important',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority','3','一般','General',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority1','1','紧急','Urgent',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority1','2','重要','Important',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority1','3','一般','General',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_casePriority1','f','不紧急','No Urgent',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','FourHundredsTeam','400团队_备件','Four hundreds team',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','FourHundredsTeam_Probe','400团队_探头','Four hundreds team',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','Supplier','供应商','Supplier',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','Commercial','Commercial','Commercial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','GEAdmin','GEAdmin','GEAdmin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','OperationsTeam','运营团队','Operations Team',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType','PublicEmail','公共邮箱','公共邮箱',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_machineStatus','functionLimit','限制功能使用','Function limit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_machineStatus','stopUsing','停机','Stop using',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_isSubtractTotalStatus','true','是','YES',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_isSubtractTotalStatus','false','否','NO',NULL,-1);

INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_ZkbFePartOrder','GEFE 维修申请','Apply manually',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_systemId','SystemID/厂商报修ID','SystemID/厂商报修ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_location','当前位置','Location',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_reportDate','报修时间','Report date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_phenomenon','故障现象','Phenomenon',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_machineStatus','机器状态','Current status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_position','医院客户职务','Position',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_urgencyStatus','紧急状态','Urgency status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_currentStep','当前步骤','Current step',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_currentStatus','当前状态','Current status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_hospitalName','医院名称','Hospital name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_functionType','设备型号','Function type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_reportUser','创建者','Report user',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_hospitalClientName','医院客户姓名','Hospital client name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_hospitalClientPhone','医院客户电话','Hospital client phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderNo','订单编号','Order no',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_subtractTotalStatus','是否扣除名额','Subtract total status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_comment','代理商审批备注','Comment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_fourHundredComment','400审批备注','400审批备注',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_sr','SR','SR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_act','Act','Act',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_geEngineerName','GE工程师姓名','GE Engineer Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_geEngineerPhone','GE工程师电话','GE Engineer Phone',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_assetTypeDi','设备类型（DI）','Asset Type Di',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_artificialType','机型','Artificial Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_applyPerson','申请人','Apply person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_applyPersonPhone','申请人电话','Apply person phone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderAnnotation','订单标注','Order annotation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_productNumber','产品型号','Product number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_softwareVersion','软件版本','Software version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_assetNo','备件号','Asset no',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_assetDescription','备件描述','Asset description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_assetQty','数量','Asset qty',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderNoa','Order#','Order#',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_assetPrice','价格','Asset price',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_spareParts','备注','Comment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_returnType','归还类型','Asset parts',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_returnDate','归还时间','Return time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderTypeName','订单类型','Order type',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_cid','课程ID','Course ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_cname','课程名称','Course Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_modality','Modality','Modality',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_startDate','开始报名时间','Start Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_endDate','结束报名时间','End Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_trainStartDate','开始培训时间','Train Start Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_trainEndDate','结束培训时间','Train End Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_trainingDays','培训天数','Training Days',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_instructor','主讲老师','Instructor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_audience','观众','Audience',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_location','位置','location',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_studentClass','学习个数','Student Class',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_batchNo','批次号','Batch No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_valid','是否生效','Valid',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_deductCount','扣除名额','Deduct Count',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_type','课程类型','Course Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_reason','原因','Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbContractCourseSchedule_joinCount','参加人数','Join count',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbContractCourseSchedule_name','课程计划表','Course Schedule',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbContractCourseSchedul_reasonCanotBeNull','原因不能为空','Reason can not be null',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbContractCourseSchedul_joinCountCanotBeNull','参加个数不能为空','Join count can not be null',NULL,	-1	);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_tenantAssignUser','代理商审批人','Tenant assign user',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_tenantAssignDate','代理商审批时间','Tenant assign date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_probeOrderNo','申请单号','Apply Order No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_probeReportDate','申请时间','Apply date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_problemOrderNumber','发生问题的order number','problemOrderNumber',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_urgentOrder','是否为紧急订单','urgentOrder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_urgencyReasons','订单紧急的原因','urgencyReasons',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_deductType','扣除名额类型','Deduct type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_remark','备注','remark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_catNo','探头号','Cat No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_zkbPartOrderCat','探头型号','Cat No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_valueType','价值类型','Value Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_oldCatSn','旧探头SN编号','Old Cat Sn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_newProbe','是否全新探头','New probe',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_needProbeLetter','是否需要探头说明函','Need probe letter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderedPerson','下单人','Ordered Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_orderedDate','下单时间','Ordered Date',NULL,-1);
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_returnType','归还类型','Return Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_partSerialNumber','Part Serial Number','Part Serial Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_partQualityIssue','故障描述','Break Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_airWayBillNo','运单号','Air Way Bill No',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_returnDate1','归还日期','Return Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_newCatSn','新探头SN编号','newCatSn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_isVacation','是否休假','Vacation',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_needLetterFlag','f','不需要','Not need',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_needLetterFlag','t','需要','Need',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_yesOrNo','f','否','NO',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_yesOrNo','t','是','YES',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_emergency','f','不紧急','NO',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_emergency','t','紧急','YES',NULL,-1);

INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_systemIdError','错误：SystemID/厂商报修ID不能为空','错误：SystemID/厂商报修ID can not be null',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_contractOverdueError','错误：合同已到期','Error:Invalid contract',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_notFindContractError','错误：该设备不在合同中','Error:not find contract',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_contractCannotBeNull','错误：合同不可为空','Error:contract can not be null',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_contractSystemIdError','错误：SystemID/厂商报修ID和合同不一致','Error:SystemID/厂商报修ID and contract not consist',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_contractSupportTimesCannotBeZero','错误：合同剩余名额不足','Error:contract support times can not be zero',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_sparePartListError','错误：申请配件不能为空','Spare part list can not be null',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_assetFieldError','错误：回填APM信息不完整','Apm infomation not right',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_systemIdAndFatherPartOrderNoError','错误：SystemID/厂商报修ID和SR不能同时为空','错误：SystemID/厂商报修ID和订单编号不能同时为空',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_searchError','错误：暂未检索到对应报修单','错误：暂未检索到对应报修单',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_returnTooMaxError','错误：归还数量不能大于申请数量','错误：归还数量不能大于申请数量',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_returnListCannotBeNull','错误：请填写归还备件信息','错误：请填写归还备件信息',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_contractValidPeriodError','合同不在有效期内','Invalid contract',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_599contractSupportTimesCannotBeZero','当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件','当前备件申请的设备已经超过50%的IB入保数额，此IB无法申请备件',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_599contractHighMachineNull','合同中没有高端机设备','合同中没有高端机设备',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','zkbFePartOrder_599contractYearCountError','599方案合同计数年异常','599合同计数年异常',NULL,	-1	);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_steps','WaitingAssignment','待代理商确认','Waiting tenant assignment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_steps','WaitingCommercial','待Commercial确认','Waiting Commercial assignment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_steps','WaitingApm','待400开单/下单','Waiting 400 team full apm',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_steps','WaitingGEAdmin','待FE归还','Waiting GE Admin return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_steps','Complete','完成','Complete',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','Requested','已申请','Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','TenantAccept','代理商同意','Tenant accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','TenantReject','代理商不同意','Tenant reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','CommercialAccept','Commercial审批通过','Commercial accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','CommercialReject','Commercial审批拒绝','Commercial reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','FourHundredsAccept','400已下单','400 team accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','FourHundredsReject','400拒绝开单','400 team reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','GEAdminAcceptPart','FE已部分归还','FE已部分归还',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_status','GEAdminAccept','FE已归还','GE Admin return',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_orderType','ManMade','人工申请','Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_orderType','SpareParts','备件申请','SpareParts Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_orderType','Probe','探头申请','Probe Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_assetTypeDi','CT','CT','CT',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_assetTypeDi','XR','XR','XR',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_artificialType','High','超声高端机','High level machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_artificialType','Low','超声低端机','Low level machine',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_artificialType','Depot','超声Depot产品','超声Depot产品',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Boot Up Issue','Boot Up Issue','Boot Up Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Cosmetic Issue','Cosmetic Issue','Cosmetic Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Disassembled','Disassembled','Disassembled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Function Does Not Work','Function Does Not Work','Function Does Not Work',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Image Quality Issue','Image Quality Issue','Image Quality Issue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Missing/Swapped Component','Missing/Swapped Component','Missing/Swapped Component',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Other','Other','Other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Physical Damage','Physical Damage','Physical Damage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_partQualityIssue','Used/Dirty','Used/Dirty','Used/Dirty',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_sparePartsOrderAnnotation','NormalOrder','常规订单','Normal order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_sparePartsOrderAnnotation','DOA','DOA','DOA',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_sparePartsOrderAnnotation','Wrong Part','Wrong Part','Wrong part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_sparePartsOrderAnnotation','Short Life','Short Life','Short life',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractCourseSchedule_type','offline','ATC','ATC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractCourseSchedule_type','online','Online','Online',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_commercialAssignUser','Commercial审批人','Commercial assign user',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_commercialAssignDate','Commercial审批时间','Commercial assign date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','zkbFePartOrder_commercialComment','Commercial审批备注','Commercial comment',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','无法打印心电图','无法打印心电图','',NULL,-1,1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','打印心电图波形颜色较浅','打印心电图波形颜色较浅','',NULL,-1,2);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','做图干扰大','做图干扰大','',NULL,-1,3);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','卡纸','卡纸','',NULL,-1,4);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','打印机内部错误-无法打印','打印机内部错误-无法打印','',NULL,-1,5);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','无法采集心电图，采集到93%后停止','无法采集心电图，采集到93%后停止','',NULL,-1,6);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','纸盘打开','纸盘打开','',NULL,-1,7);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','客户端/服务器协议错误，要重试吗？','客户端/服务器协议错误，要重试吗？','',NULL,-1,8);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','无纸或夹纸','无纸或夹纸','',NULL,-1,9);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_221','其他','其他','',NULL,-1,99);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','电极贴好后导联直线','电极贴好后导联直线','',NULL,-1,1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','跑台噪音过大','跑台噪音过大','',NULL,-1,2);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','打印心电图过浅','打印心电图过浅','',NULL,-1,3);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','无法初始化采集模块','无法初始化采集模块','',NULL,-1,4);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','无法启动平板','无法启动平板','',NULL,-1,5);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_222','其他','其他','',NULL,-1,99);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_223','WEB浏览只显示波形不显示字库','WEB浏览只显示波形不显示字库','',NULL,-1,1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_223','无法登录客户端','无法登录客户端','',NULL,-1,2);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_223','没有患者信息','没有患者信息','',NULL,-1,3);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_223','医生工作站无法浏览报告','医生工作站无法浏览报告','',NULL,-1,4);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_223','其他','其他','',NULL,-1,99);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','潮气量不准','潮气量不准','',NULL,-1,1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','氧气浓度偏低','氧气浓度偏低','',NULL,-1,2);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','屏幕提示氧电池失效','屏幕提示氧电池失效','',NULL,-1,3);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','自检不通过','自检不通过','',NULL,-1,4);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','手控回路漏气','手控回路漏气','',NULL,-1,5);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','机控回路漏气','机控回路漏气','',NULL,-1,6);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','屏幕提示回路漏气？无法加压','屏幕提示回路漏气？无法加压','',NULL,-1,7);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','屏幕提示流量传感器失效','屏幕提示流量传感器失效','',NULL,-1,8);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','开机后黑屏','开机后黑屏','',NULL,-1,9);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','无法调节参数','无法调节参数','',NULL,-1,10);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','触摸屏失效','触摸屏失效','',NULL,-1,11);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','开机进入备用氧模式','开机进入备用氧模式','',NULL,-1,12);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','电源指示灯不亮','电源指示灯不亮','',NULL,-1,13);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','快捷键失效','快捷键失效','',NULL,-1,14);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','APL阀断裂','APL阀断裂','',NULL,-1,15);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','手控支臂断裂','手控支臂断裂','',NULL,-1,16);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_201','其他','其他','',NULL,-1,99);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','无创血压测量不出值','无创血压测量不出值','',NULL,-1,1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','袖带不能充满气体','袖带不能充满气体','',NULL,-1,2);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','袖带管漏气','袖带管漏气','',NULL,-1,3);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','心电波形杂乱','心电波形杂乱','',NULL,-1,4);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','屏幕提示心电导联脱落','屏幕提示心电导联脱落','',NULL,-1,5);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','无法识别模块','无法识别模块','',NULL,-1,6);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','开机后黑屏','开机后黑屏','',NULL,-1,7);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','电源指示灯不亮','电源指示灯不亮','',NULL,-1,8);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','开机后反复重启','开机后反复重启','',NULL,-1,9);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','呼末二氧化碳值不准确','呼末二氧化碳值不准确','',NULL,-1,10);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','氧浓度值不准确','氧浓度值不准确','',NULL,-1,11);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','麻醉气体MAC值不准','麻醉气体MAC值不准','',NULL,-1,12);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','肌松测试不出值','肌松测试不出值','',NULL,-1,13);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','提示“SRAM Error ”','提示“SRAM Error ”','',NULL,-1,14);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','调节时间不能保存','调节时间不能保存','',NULL,-1,15);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','体温测试值偏差大','体温测试值偏差大','',NULL,-1,16);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id,order_by) VALUES ('lcsFaultType_203','其他','其他','',NULL,-1,99);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CoilOpportunityImage','MR线圈图片上传','Coil Opportunity Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','IBUInfoImage','IBU图片上传','IBUOffer Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','cataLog','货号','cata Log',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','USOptionImage','USOption图片上传','USOption Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ProbeRecommendImage','探头图片上传','ProbeRecommend Image',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','geibTable_installedTime','装机时间','geibTable installed Time',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','1','保养报告上传','MaintenanceReportUpload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','2','保养微报上传','MaintenanceMicrosprintUplo',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','3','设备月报','MonthlyReports',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','4','关爱报告','CareReport',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','5','监控警报','AssetAlert',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','6','简报消息','Briefings',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','7','维修状态','MaintenanceWorks',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','8','维修报告上传','MaintenanceReportUploaded',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','9','超声应用指导','USApplication',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','10','增值服务','ValueAddedServices',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','11','应用培训状态','AppliedTrainingStatus',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('menuType','12','应用培训报告上传','ApplicationTrainingReportUpload',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sparePartQueryReason','1','备件业务机会','business chance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sparePartQueryReason','2','备件价格咨询（非业务机会）','price consulting',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sparePartQueryReason','3 ','服务报告信息补充','information supplement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sparePartQueryReason','4','其它原因','other reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('supplierUserBind','1','未绑定','UnBing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('supplierUserBind','2','已绑定','Bingding',NULL,-1);

-- 问题类型 问题类型 问题类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('questionType','设备原理相关','设备原理相关','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('questionType','常见故障相关','常见故障相关','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('questionType','备件相关','备件相关','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('questionType','保养相关','保养相关','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('questionType','其他','其他','',NULL,-1);

-- 云上大咖modality及大咖对应表
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('batchFileType','UserQAExcel','云上大咖modality及大咖对应表','Environmental Status Monitor',NULL,-1);

-- 设备附件类型配置
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label','AssetAttachmentCategoryConfiguration','设备附件类型配置','CategoryConfig',NULL,'-1');

-- 修改modalityType下移动DR
update i18n_message set msg_key='DR',value_zh='DR',value_en='DR' where msg_type='modalityType' and msg_key='移动DR';
--- 工程师说下设备类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','CT','CT','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','MR','MR','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','US','US','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','X-Ray','X-Ray','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','C臂机','C臂机','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','SPECT','SPECT','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','血管机','血管机','',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('engineerModalityType','LCS','LCS','',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ScheduleNoJoin','不报名','ScheduleNoJoin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ScheduleJoin','已报名','ScheduleJoin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','LetterSubmit','已上传确认函','ScheduleJoin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','LetterCancelJoin','确认时取消参加','LetterCancelJoin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ApplySubmit','已申请','ApplySubmit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ApplyCancelJoin','申请时取消参加','ApplyCancelJoin',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ApplyCancelAccepted','原因审批通过','ApplyCancelAccepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','ApplyCancelRejected','原因审批不通过','ApplyCancelRejected',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','AuditAccepted','申请审批通过','AuditAccepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStatus','AuditRejected','申请审批不通过','AuditRejected',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStep','LetterPending','待上传确认函','LetterPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStep','ApplyPending','待申请','ApplyPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStep','ApplyCancelAuditPending','取消原因待审批','ApplyCancelAuditPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStep','ApplyAuditPending','申请待审批','ApplyAuditPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainStep','Completed','完成','Completed',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditNodeStatus','0','未审核','Not reviewed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditNodeStatus','1','审核通过','examination passed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditNodeStatus','2','审核不通过','Audit not passed',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('approveStatus','1','未审核','Not reviewed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('approveStatus','2','审核通过','examination passed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('approveStatus','3','审核不通过','Audit not passed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('approveStatus','4','审核进行中','review in progress',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainParticipateStatus','Y','参加','Y',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbCourseTrainParticipateStatus','N','不参加','N',NULL,-1);

-- 2021.04.26 保养完成签名
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SignatureRequiredForPMWorkComplete', '保养完成是否需要签名', 'Signature required for work complete', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'pendingAcceptance', '待验收', 'Waiting for Acceptance', NULL, '-1');

-- 2021.06.29 到货安装是否提示保养设置
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MaintenanceSettingsForArrivalInstallation', '到货安装是否提示保养设置', 'Maintenance Settings For Arrival Installation', NULL, '-1');

-- Mvs工单
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MvsOrder', 'MVS工单', 'MVS Order', NULL, '-1');

-- 2021-05-24  start
-- '日志审计-用户修改记录'页面的字段
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','orgName','所属部门','org name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','comment','备注','comment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','isLocked','锁定？','is locked',NULL,-1);

-- menu菜单(日志审计： 用户修改记录， 资产修改记录， 登录历史记录)
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','logView','日志审计','log view',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','userModifyRecord','用户修改记录','user modify record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetModifyRecord','资产修改记录','asset modify record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','loginHistoryRecord','登录历史记录','login record',NULL,-1);

-- '日志审计-登录历史记录'页面的字段
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','loginName','登录姓名','login name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','loginUrl','登录网址','login url',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','loginIp','登录ip','login ip',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','loginTime','登录时间','login time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','logoutTime','登出时间','logout time',NULL,-1);
--2021-05-24  end

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','unattended','IoT设备使用概览','unattended',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ContractAdd','合同新增','ContractAdd',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ContractUpdate','合同修改','ContractUpdate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ZkbPartOrderDeduct','ZKB扣除','ZkbPartOrderDeduct',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ZkbPartOrderReturn','ZKB返还','ZkbPartOrderReturn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ZkbFePartOrderDeduct','GE扣除','ZkbFePartOrderDeduct',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','ZkbFePartOrderReturn','GE返还','ZkbFePartOrderReturn',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOpType','AutoAdd','系统自动增加','AutoAdd',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','Spare','备件名额','Spare',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','SpareArtificial','联合名额','SpareArtificial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','ProbeAny','探头任意名额','ProbeAny',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','ProbeNormal','探头普通名额','ProbeNormal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','ProbeSpecial','探头特殊名额','ProbeSpecial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','Artificial','人工名额','Artificial',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','Probe','探头名额','Probe',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','PartsHigh','备件高值名额','PartsHigh',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','CssbMaintenance','保养名额','CssbMaintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaOrderType','Consumable','耗材名额','Consumable',NULL,-1);



--多级审批模块(环球迁移)
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'MultiLevelConfig', '多级审批配置', 'MultiLevelApproveConfig', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MultiLevelApprovalConfig','多级审批配置','MultiLevelApprovalConfig',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('businessType', 'assetRepair', '设备维修', 'assetRepair', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('businessType', 'assetMaintain', '设备保养', 'assetMaintain', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('auditGroupName', 'coreGroupMembers', '核心组成员', 'coreGroupMembers', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('auditGroupName', 'medicalTeamLeaderGroup', '医工组长分组', 'medicalTeamLeaderGroup', NULL, '-1');
-- end
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'auditGroupConfiguration', '审核分组配置', 'auditGroupConfiguration', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaDeductType','Null','N/A','Null',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaDeductType','New','新逻辑扣减','New',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbContractQuotaDeductType','Old','旧逻辑扣减','Old',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','accountingDeptName','账户科室','accounting Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','monitorRate','主机功率','accounting Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','otherPower','其他功率','accounting Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','unitPriceElectricity','电费单价','accounting Department',NULL,-1);

-- 多级审批配置，添加不良事件
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('businessType', 'adverseEvents', '不良事件', 'adverseEvents', NULL, -1);

-- YHAAO-13407 质控评审 读取默认数据错误 👇
alter table public.audit_default_group add column if not exists clause text null;
-- YHAAO-13407 质控评审 读取默认数据错误 👆

-- 多级审批，不良事件字段名
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','workOrderNum','工单编号','Work order number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','reporterName','报告人','reporter',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','reportDate','报告时间','report time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','department','科室','department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','occurredPlace','发生地','Place of occurrence',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','deviceNum','设备编号','device ID',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','productName','产品名称','product name',NULL,-1);
-- 不良事件状态
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '1', '暂存', 'stage', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '2', '待审核', 'pending approve', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '3', '审核中', 'in the audit', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '4', '已退回', 'returned', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '5', '已作废', 'invalid', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '6', '审核通过', 'The audit was approved', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '7', '待上报', 'to be reported', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '8', '已上报', 'reported', NULL, -1);
-- 不良事件操作人
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventProcesser', '1', '医生', 'doctor', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventProcesser', '2', '护士', 'nurse', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventProcesser', '3', '患者', 'patient', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventProcesser', '4', '其他', 'other', NULL, -1);
-- 不良事件伤害程度
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventHarmDegree', '1', '死亡', 'death', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventHarmDegree', '2', '严重伤害', 'serious injury', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventHarmDegree', '3', '其他', 'other', NULL, -1);
-- 不良事件年龄类型
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventAgeType', '1', '岁', 'year', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventAgeType', '2', '月', 'month', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventAgeType', '3', '日', 'day', NULL, -1);
-- 不良事件患者性别
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventSex', '1', '男', 'male', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventSex', '2', '女', 'female', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventSex', '3', '未知', 'unknown', NULL, -1);
-- 不良事件使用场所
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventUsePlace', '1', '医疗机构', 'medical institutions', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventUsePlace', '2', '家庭', 'home', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventUsePlace', '3', '其他', 'other', NULL, -1);
-- 不良事件事件原因分析
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventCause', '1', '产品原因（包括说明书）', 'product reasons (including instructions)', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventCause', '2', '操作原因', 'the reason for the operation', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventCause', '3', '患者自身原因', 'patient own cause', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventCause', '4', '无法确定', 'not sure', NULL, -1);
-- 不良事件审核结果
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventApprovalResult', '1', '同意上报', 'agree to report', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventApprovalResult', '2', '同意不上报', 'agree not to report', NULL, -1);
--不良事件状态补充
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventStatus', '9', '待采购填写', 'to be filled in by the buyer', NULL, -1);
-- 不良事件补充（采购员角色微信卡片）
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('wechatCard', 'adverseEvents_supplement', '不良事件产品信息维护', 'adverseEvents supplement', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','adverseEvents_supplement','不良事件产品信息维护','adverseEvents supplement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventsSupplementCard','adverseEvents_supplement.filled','待填写','To be filled',NULL,-1);

-- 调剂名称设置页面新增字段
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','SwappedEquipmentNo','调剂编号','accounting Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','powerOnThreshold','开机电流阈值','start powerOnThreshold',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','rfGroup','射频码分组','rfGroup',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','rfId','射频码','rfId',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','currentId','电流ID','currentId',NULL,-1);

-- 探头信息维护 （是否有效isVaild）
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbProbe_isVaild', 'N', '无效', 'efficient', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbProbe_isVaild','Y','有效','invalid',NULL,-1);
-- 探头信息维护 （价值类型valueType）
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbProbe_valueType', 'HighValue', '高值', 'HighValue', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbProbe_valueType','NormalValue','普通','NormalValue',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbProbe_valueType','SpecialValue','特殊','SpecialValue',NULL,-1);
-- 探头信息维护
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ZkbPartOrderCat','探头信息','ZkbPartOrderCat',NULL,-1);

-- 销售人员信息管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ZkbSalesPersonInfo','销售人员信息','ZkbSalesPersonInfo',NULL,-1);

-- 无人值守
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WhetherToEnableUnattended', '是否启用无人值守', 'Whether To Enable Unattended', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AppointmentStatusLockedTime', '预约状态锁定时长', 'Appointment Status Locked Time', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'OutboundStatusCheckInterval', '出库状态检查间隔', 'Out bound Status Check Interval', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','rfGroup','射频码分组','rfGroup',NULL,-1);
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','rfId','射频码','rfId',NULL,-1);
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','powerOnThreshold','开机电流阈值','start powerOnThreshold',NULL,-1);
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','currentId','电流ID','currentId',NULL,-1);

--YHAAO-14128  加一个管理租户用户到APM中  审阅人
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbFePartOrder_configType', 'Reviewer', '审阅人', 'Reviewer', null, -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbPoStepsRole', 'Reviewer', '审阅人', 'Reviewer', null, -1);
-- 设备档案-基本信息添加新字段-设备性质
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetQuality','设备性质','Asset Quality',NULL,-1);
-- 设备性质code值
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetQuality','1','GE全程支持的GE设备','GE equipment supported by GE',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetQuality','2','GE全程支持的非GE设备','Non-GE equipment supported by GE',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetQuality','3','GE远程支持/县域场地支持的设备','GE Remote Support/County Site Support Equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetQuality','4','县域全程支持的设备','Equipment supported throughout the county Site',NULL,-1);


-- 县域租户工单为签到提示
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NotSignedInTips','维修工单未签到','Not Signed In',NULL,-1);
-- 县域租户工单添加新字段
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','siebleNumber','siebel单号','Siebel Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','callCenterNumber','呼叫中心单号','Call Center Number',NULL,-1);
--县域租户用件类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','partUseType','用件类型','Part use type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partUseType','1','保内用件','Parts under warranty',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('partUseType','2','保外用件','Out-of-warranty parts',NULL,-1);
--入库类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('storage_type', 'newgoods', '新货入库', 'newgoods', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('storage_type','virtual','虚拟入库','virtual',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('storage_type','okay','好还入库','okay',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','0','--------','--------',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','1','APM_IB','APM_IB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','2','智康保 IB','智康保 IB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','3','其他','其他',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','4','Acct重合IB','Acct重合IB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','5','智爱养护IB','智爱养护IB',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','6','OWOC-防','OWOC-防',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ib-status','10','远程守护','远程守护',NULL,-1);

--县域专用故障类别
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyArea', '1', '自然故障(设备老化/过度使用等)', 'Natural failure (equipment aging / overuse, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyArea', '2', '人为故障(操作不当/校正失调等)', 'Human failure (improper operation / correction of maladjustment, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyArea', '3', '环境故障(温湿度/灰尘/磁场干扰等)', 'Environmental failure (temperature and humidity / dust / magnetic field interference, etc.)', NULL, '-1');
--原4 设计故障(设备软硬设计故障)分拆成两个类别“8 硬件故障”及“9 软件故障“，去掉6可规避故障和5其他两项
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyArea', '8', '硬件故障', 'Hardware malfunction', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyArea', '9', '软件故障', 'Software failure', NULL, '-1');
--县域和旧故障类别合并，方便兼容历史数据的页面显示。
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '1', '自然故障(设备老化/过度使用等)', 'Natural failure (equipment aging / overuse, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '2', '人为故障(操作不当/校正失调等)', 'Human failure (improper operation / correction of maladjustment, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '3', '环境故障(温湿度/灰尘/磁场干扰等)', 'Environmental failure (temperature and humidity / dust / magnetic field interference, etc.)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '4', '设计故障(设备软硬设计故障)', 'Design failure (equipment hardware and software design failure)', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '5', '其它', 'other', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '6', '可规避故障', 'Avoidable fault', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '8', '硬件故障', 'Hardware malfunction', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('faultTypeCountyAreaMore', '9', '软件故障', 'Software failure', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','countyAreaOrderType','开单类型','Order Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('countyAreaOrderType', '1', '主动开单', 'Initiative Order', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('countyAreaOrderType', '2', '被动开单', 'Passive Order', NULL, '-1');

-- 质控评审
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.auditItemConfig','评审知识库配置-解读','Review Knowledge Base Configuration - Interpretation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AuditItemConfig','评审知识库配置-解读','Review Knowledge Base Configuration - Interpretation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','contentType','内容类型','Content Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','content','内容','Content',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mark','序号','Mark',NULL,-1);
-- 质控评审知识库内容类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditItemType','1','指引','Guidelines',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditItemType','2','概述','Summary',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditItemType','3','解读','Interpretation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditItemType','4','得分点','Scoring points',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditItemType','5','失分点','Lost score points',NULL,-1);
-- 质控评审参考模板
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.AuditTemplate','评审知识库配置-参考模板','Review knowledge base configuration - reference template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AuditTemplate','评审知识库配置-参考模板','Review knowledge base configuration - reference template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','TemplateName','模板名称','Template name',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('entryType','2','款','payment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('entryType','3','指标','target',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('Scoring','1','折算分制','Converted points system',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('Scoring','2','扣分制','deduction system',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('Scoring','3','累加分制','cumulative points system',NULL,-1);
-- 质控评审PM超管配置页面
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.AuditUserCase','省份&版本管理','Province & Version Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AuditUserCase','省份&版本管理','Province & Version Management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Target','目标','Target',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tenant','租户','Tenant',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Initialize','初始化','Initialize',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','InitializeConformation','初始化确认','Fallback Conformation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','InitializeConformationMsg','请务必确认是否执行初始化，执行初始化系统将初始化页面至初始设置项，历史资料暂不留存！','Please be sure to confirm whether to perform initialization, the initialization system will initialize the page to the initial settings item, and the historical data will not be retained!',NULL,-1);
-- 质控评审省份版本类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('VersionType','1','评审','Review',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('VersionType','2','质控','Quality control',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('targetType','1','三甲','top three',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('targetType','2','三乙','Trib',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('targetType','3','三丙','Three C',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('targetType','4','未定','undecided',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'provinceVersionConfiguration', '省份&版本配置', 'Province version configuration', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Success','成功','Success',NULL,-1);
-- 质控评审event
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.auditEvent','质控评审事件','Quality control review event',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AuditEvent','质控评审事件','Quality control review event',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operator','操作人','Operator',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','eventTime','事件时间','Event time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','eventType','事件类型','Event type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','eventResult','事件结果','Event result',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Version','版本','Version',NULL,-1);
-- 质控评审事件类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_CONFIG_ITEM','添加系统知识库','Add system item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EDIT_CONFIG_ITEM','编辑系统知识库','Edit system item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','DELETE_CONFIG_ITEM','删除系统知识库','Delete system item',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_TEMPLATE','添加系统模板','Add system template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EDIT_TEMPLATE','编辑系统模板','Edit system template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','DELETE_TEMPLATE','删除模板','Delete template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_SYSTEM_CASE','添加系统版本','Add system case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EDIT_SYSTEM_CASE','编辑系统版本','Edit system case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ENABLE_SYSTEM_CASE','启用系统版本','Enable system case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EXPIRE_SYSTEM_CASE','过期系统版本','Expire system case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','DELETE_SYSTEM_CASE','删除系统版本','Delete system case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_SYSTEM_ENTRY','添加系统条款','Add system entry',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EDIT_SYSTEM_ENTRY','编辑系统条款','Edit system entry',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','DELETE_SYSTEM_ENTRY','删除系统条款','Delete system entry',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ENABLE_TENANT_AUDIT','租户启用评审权限','Enable tenant audit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_CASE','创建租户适合的版本','Add tenant case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ARCHIEVE_CASE','归档租户版本','Archieve tenant case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','INIT_TENANT_AUDIT','初始化租户版本','Init tenant case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ASSIGN_POINT','分配自评到个人','Assign point',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','COMPUTE_CASE','启动自评','Compute case',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EXPORT_RESULT','导出结果excel','Export excel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','ADD_AUDIT_RESULT','添加实际评审结果','Add audit result',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','UPLOAD_FILE','用户上传文件','User upload file',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','UPLOAD_TEMPLATE','用户上传模板','User upload template',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','DELETE_FILE','用户删除文件','Delete file',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EDIT_POINT','编辑更新自评','Edit point',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','QUIT_OWNER','退回管理员重新分配','Quit owner',NULL,-1);
-- 质控评审事件结果
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEventResult','success','成功','Success',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEventResult','fail','失败','Fail',NULL,-1);
-- 质控评审配置菜单
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','config.auditConfig','质控评审配置','Quality control review configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','auditConfig','质控评审配置','Quality control review configuration',NULL,-1);
-- 质控评审管理-状态
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditCaseStatus','1','发布','publish',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditCaseStatus','2','过期','expired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('auditEvent','EXPIRED_TENANT_AUDIT','过期租户版本','Expired tenant version',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','expired','过期','Expired',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AuditCaseExpiredConformation','过期确认','Expired Conformation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message','AuditCaseExpiredConformationMsg','确认是否过期该版本？','Confirm if the version expires?',NULL,-1);
-- 供应商三证到期时间
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SupplierCertificateManage','供应商资质证书管理','Supplier qualification certificate management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','productionLicenseExpireDays','生产许可证到期提醒提前天数','The number of days in advance for the production license expiration reminder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ProductionLicenseExpireTimes','生产许可证到期提醒提前次数','The number of production license expiration reminder advances',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BusinessLicenseExpireDays','经营许可证到期提醒提前天数','The number of days in advance for the reminder of the expiration of the operating license',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BusinessLicenseExpireTimes','经营许可证到期提醒提前次数','The number of times the business license expires in advance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RegistrationCertificateExpireDays','注册证到期提醒提前天数','The number of days in advance of the reminder of the expiration of the registration certificate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RegistrationCertificateExpireTimes','注册证到期提醒提前次数','The number of times the registration certificate expires in advance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ProductionLicenseExpireDate','生产许可证到期时间','Production license expiration time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BusinessLicenseExpireDate','经营许可证到期时间','The expiry time of the operating license',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RegistrationCertificateExpireDate','注册证到期时间','Registration certificate expiry time',NULL,-1);

-- 维修->恢复时间不能小于接单时间
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RepairRecoveryTimeCheck','恢复时间不能小于接单时间','The recovery time cannot be less than the order time',NULL,-1);

-- 预算管理 👇
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Blank', '', ' ', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr', '预算管理', 'Budget Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','budgetMgr.deptPurchaseManage','科室预算管理','Department budget management',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetMgr', '预算管理', 'Budget Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetApplication', '预算申请', 'Budget Application', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetInfo', '预算详情', 'Budget Details', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PurchaseBudgetConfig', '年度申购开启记录', 'PurchaseBudgetConfig', NULL, -1);



INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetAsset', '预算设备', 'Budget Asset', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetAssetInfo', '预算设备详情', 'Budget Asset Details', NULL, -1);
-- INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr.deptPurchaseManage','科室预算管理','Department budget management',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr.application', '预算申请', 'Budget Application', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr.temporallyPurchase', '临时申购管理', 'Budget TemporallyPurchase Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr.yearPurchase', '年度申购管理', 'Budget YearPurchase Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu', 'budgetMgr.preSupplierMgr','预供应商管理','Pre-vendor management',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetApprovalInfo', '预算审核详情', 'Budget Approval Details', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'TemporallyPurchase', '临时申购管理', 'Temporally Purchase', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'YearPurchase', '年度申购管理', 'Year Purchase', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DeptPurchaseManage', '科室预算额度管理', 'Dept Purchase Manage', NULL, -1);




INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationDept', '申请科室', 'Application Department', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationDate', '申请日期', 'Application Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationNum', '申请编号', 'Application Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Applicant', '申请人', 'Applicant', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Asset2ndClassify', '设备二级分类', 'Equipment Secondary Classification', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PendingApproval', '待审核', 'Pending Approval', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApprovalInProgress', '审核中', 'Approval In Progress', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Approved4', '审核通过', 'Approved', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationTotalAmount', '申请总金额', 'Application Total Amount', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationStatus', '申请状态', 'Application Status', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApprovalProgress', '审核进度', 'Approval Progress', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Approver', '审核人', 'Approver', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApprovalDate', '审核日期', 'Approval Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'StockType', '申购类型', 'Stock Type', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductNumber', '产品编号', 'Product Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AssetBrand', '设备品牌', 'Asset Brand', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Unit', '单位', 'Unit', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Price', '价格', 'Price', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationQuantity', '申请数量', 'Application Quantity', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationPrice', '申请单价', 'Application Price', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApplicationAmount', '申请金额', 'Application Amount', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BudgetAmount', '预算金额', 'Budget Amount', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Declaration', '申请说明', 'Declaration', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'FundingSource', '经费来源', 'Funding Source', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'RecommendBrand', '推荐品牌', 'Recommend Brand', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LaboratoryEvidence', '科室论证材料上传', 'Laboratory Evidence', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PurchaseYear', '年份', 'Purchase Year', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AddNewAsset', '添加新设备', 'Add New Asset', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('businessType', 'budgetManagement', '申购管理', 'Budget Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('businessType', 'purchaseManagement', '采购管理', 'Purchase Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('businessType', 'maintenanceContractApplyPurchase', '维保合同申购管理', 'Maintenance Contract Apply Purchase', NULL, -1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '1', '未提交', 'Staging', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '2', '已提交', 'Submitted', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '3', '审核通过', 'Pass', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '4', '审核不通过', 'Not Pass', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '5', '取消', 'Cancel', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStatus', '6', '已完成', 'Complete', NULL, -1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStep', 'ToConfirmedInfo', '待信息确认', 'ToConfirmedInfo', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStep', 'ToConfirmedInfoMeet', '待过会确认', 'ToConfirmedInfoMeet', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStep', 'ToConfirmedInfoTendered', '待招标完成', 'ToConfirmedInfoTendered', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStep', 'Complete', '完成', 'Complete', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStepStatus', 'ConfirmedInfo', '信息已确认', 'ConfirmedInfo', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStepStatus', 'ConfirmedInfoMeet', '过会已完成', 'ConfirmedInfoMeet', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseStepStatus', 'Complete', '完成', 'Complete', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseType', 'SelfHarvesting', '自采', 'SelfHarvesting', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('purchaseType', 'CentralizedProcurement', '集采', 'CentralizedProcurement', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderBidType', 'Company', '招标公司', 'Company', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderBidType', 'Vender', '预供应商', 'Vender', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderProductionSize', 'LargeEnterprises', '大企业', 'LargeEnterprises', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderProductionSize', 'MediumEnterprises', '中企业', 'MediumEnterprises', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderProductionSize', 'SmallEnterprises', '小企业', 'SmallEnterprises', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('venderProductionSize', 'MicroEnterprises', '微企业', 'MicroEnterprises', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStep', 'ToSubmitted', '待提交', 'To be submitted', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStep', 'PendingApproval', '待审核', 'Pending Approval', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStep', 'PendingComplete', '待完成', 'Pending Complete', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationStep', 'Complete', '完成', 'Complete', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('procureStatus', 'Unpurchased', '未采购', 'Unpurchased', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('procureStatus', 'Purchased', '已采购', 'Purchased', NULL, -1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetConfirmStatus', 'UnConfirm', '未确认', 'Unpurchased', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetConfirmStatus', 'Confirmed', '已确认', 'Purchased', NULL, -1);



INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationT_F', 'T', '是', 'T', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetApplicationT_F', 'F', '否', 'F', NULL, -1);

--经费来源
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sourceFunding', 'SelfRaised', '自筹资金', 'Self raised funds', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sourceFunding', 'Appropriation', '拨款资金', 'Appropriation funds', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('sourceFunding', 'HorizontalSpecial', '横向专项经费(科研)', 'Horizontal special funds', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('temporallyPurchaseType', 'AssetPurchase', '设备采购', 'Asset Purchase', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('temporallyPurchaseType', 'AssetMaintenance', '设备维保', 'Asset maintenance', NULL, -1);



INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetStockType', '1', '新增', 'CREATE', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('budgetStockType', '2', '更新', 'UPDATE', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message', 'BudgetAlreadyInProgress', '操作失败, 该申请已在审批流程中', 'The operation failed, the application is already in the approval process', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message', 'BudgetAssetAlreadyInUsing', '操作失败, 该设备正在使用中', 'The operation failed, the asset is already in use', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('message', 'DeleteNurseSiteMaintainConfig', '删除配置项时会同时删除此配置项相关的将来未完成工单，确定要删除吗？', 'Deleting a configuration item also deletes future unfinished tickets related to this configuration item, are you sure you want to delete it?', NULL, -1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','supplierqualificationCertification','开通供应商是否资质认证','Supplier qualification Certification',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AllowDownloadTemplate','是否允许下载模板','Are template downloads allowed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AllowDownload','启用下载','Enable downloads',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NotAllowDownload','禁用下载','Disable downloads',NULL,-1);

-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'PartsItemCode', '备件编号', 'Part Item Code', NULL, '-1');



INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reasonCategorize', 'scrapped', '已拆机或已报废', 'scrapped', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reasonCategorize', 'notAllowed', '与医院无保修，医院不允许贴码', 'not allowed', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reasonCategorize', 'notFind', '设备找不到', 'not find', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reasonCategorize', 'other', '其他', 'other', NULL, -1);




INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetDisposalResults','资产处置结果',' asset Disposal Results',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetDisposalResultsType','1','产权注销','Cancellation of property rights',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetDisposalResultsType','2','产权转让','transfer of property rights',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemNum1','系统编号1',' system Num 1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemNum2','系统编号2',' system Num 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemNum3','系统编号3',' system Num 3',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemNum4','系统编号4',' system Num 4',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','systemNum5','系统编号5',' system Num 5',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','networkingStatus','联网情况',' Networking Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','isUseRegistration','是否使用登记',' Is Use Registration ?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('networkingStatus', '0', '未联网', 'Not Connected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('networkingStatus', '1', '内网', 'Intranet', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('networkingStatus', '2', '外网', 'Extranet', NULL, '-1');
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PaymentPlan','付款计划','Payment Plan',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','AddPaymentPlan','添加付款计划','Add Payment Plan',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','EstimatedPaymentDate','预计付款日期','Estimated payment date',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','PaymentProgress','付款进度','Payment progress',NULL,	-1	);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'login_disable_error', '该账号已被停用，请联系系统管理员', NULL, NULL, '-1');
-- 配置项管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemManage','配置项管理','Configure item management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemName','名称','Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemCategory','业务类别','Category',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemDesc','说明','Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemKey','配置键','Configure key',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemValue','配置值','Configure value',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ConfigItemUseParent','是否使用上级配置','Whether to use parent configuration',NULL,-1);
-- 物联绩效设备绑定
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','IotDeviceBind','物联绩效设备绑定','IoT performance device binding',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Device','设备','Device',NULL,-1);
-- 导出记录状态
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'REFUSE', '拒绝', 'refuse', -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'AUDIT', '审核', 'audit', -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'FAIL', '失败', 'fail', -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'FINISHED', '完成', 'finished', -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'RUN', '执行', 'run', -1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, site_id) VALUES ('exportRecordStatus', 'NEW', '新建', 'new', -1);
-- YHAAO-16709 system_client 管理页面
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SystemClientInfo','子系统账户','Subsystem account',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ClientName','账户名称','Client name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ClientType','账户类型','Client type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BindOrgInfo','关联组织','Associated organizations',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('clientType','IOT','IOT','IOT',NULL,-1);
-- YHAAO-17126 资产字段添加已使用年限
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetUsedLifecycle','设备已使用年限','Asset used lifecycle',NULL,-1);
-- YHAAO-20085 资产字段添加使用年限截止日期
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','assetLifecycleDeadline','设备使用年限截止日期','Asset lifecycle deadline',NULL,-1);
-- YHAAO-17161 数据字典自定义设置页面
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','i18nConfig','数据字典自定义配置','Data dictionary configuration',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','addI18nConfig','添加数据字典','Add data dictionary',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Chinese','中文','Chinese',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','English','英文','English',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Traditional','繁体','Traditional',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customI18n','transaction','事务名称','Transaction name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','13','参数讨论','Parameter discussion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('transaction','14','院内招标磋商','Bidding consultation in the hospital',NULL,-1);
-- YHAAO-17203 维修管理-web端分时段录入工时
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','entryType','工时填写方式','Hours EntryType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('hoursEntryType','0','填写总工时','Hours EntryType',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('hoursEntryType','1','填写工时段','Hours EntryType',NULL,-1);
-- YHAAO-17265 新增权限组菜单
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PermissionGroup','权限组管理','Permission group management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PermissionGroupSetting', '权限组设置', 'Permission group setting', NULL, '-1');
-- YHAAO-17126 设备消息订阅添加试用到期提醒事件
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ExpirationReminderEvent', '使用到期提醒事件', 'expiration reminder event', NULL, '-1');
-- 添加提醒消息推送菜单
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ReminderConfiguration', '消息提醒配置', 'Message reminder configuration', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'reminderRule', '提醒规则类型', 'asset expire reminder', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'Default', '默认到期提醒', 'default expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'AssetLifecycle', '资产到期提醒', 'asset expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'AssetWarrantyDate', '设备维保截止日期到期提醒', 'Equipment maintenance deadline expiration reminder', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'Measuring', '计量到期提醒', 'measuring expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'ContractOverdue', '合同到期提醒', 'contract expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'ContractAck', '合同验收提醒', 'contract acceptance reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'UnattendedApproval', '无人值守审批提醒', 'unattended expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'NurseSiteMaintainItem', '日常检查工单提醒', 'NurseSiteMaintainItem expire reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'UnattendedOverdue', '无人值守借用超期提醒', 'unattended overdue reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'SupplierReview', '供应商注册审核提醒', 'supplier registration review reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderRule', 'AdjustmentCenterReturn', '调剂中心归还提醒', 'adjustment center return reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'reminderType', '提醒类型', 'reminder type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderType', 'wechat', '微信', 'wechat', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderType', 'email', '邮件', 'email', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('reminderType', 'sms', '短信', 'sms', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'messageType', '消息类型', 'message wechat', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('messageType', 'expire', '到期消息提醒', 'Expiration message reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('messageType', 'overdue', '逾期消息提醒', 'Late message reminder', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'noticeType', '通知类型', 'notice type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('noticeType', 'noticeTypeRole', '角色', 'role', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('noticeType', 'noticeTypeUser', '用户', 'user', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('noticeType', 'noticeTypeCustom', '自定义', 'custom', NULL, '-1');
-- INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'noticeType', '通知类型', 'notice type', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'multiplePush', '多次推送', 'multiple push', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'noticeSetting', '通知配置', 'notice setting', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'pushSetting', '推送配置', 'push setting', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'wechatTemplateSetting', '微信模板配置', 'wechat template setting', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'pushCycle', '推送周期', 'push cycle', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'pushNum', '推送次数', 'number of push', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'reminderEvent', '提醒事件信息', 'reminder event', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name', 'reminderDate', '提醒日期', 'reminder date', NULL, '-1');
-- YHAAO-17272 维修管理-分时段录入工时导出
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','workHourDetail','维修工时详情','Details of maintenance hours',NULL,-1);
-- YHAAO-17289 漳州市医院演示环境-常用故障模板
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetRepairQuickTemplateType','1','故障原因','Asset Repair Quick Template Type 1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetRepairQuickTemplateType','2','解决方案','Asset Repair Quick Template Type 2',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetRepairQuickTemplateType','3','交付检测','Asset Repair Quick Template Type 3',NULL,-1);
-- YHAAO-17290 his 设备绩效管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform','His绩效分析','His Performance analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.deviceMatching','设备匹配','Device matching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.deptMatching','科室匹配','Device matching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.deviceGrouping','设备分组','Device grouping',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.chargeCode','收费代码与分组匹配','The charge code matches the group',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.manualTasks','手动任务抓取数据','Manual tasks fetch data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.chargeDetailSource','收费明细源数据','Charge details source data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','hisPerform.chargeDetailResult','收费明细结果数据','Charge details source result data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','repair.commonFaults','常用故障及分类','Common faults and classification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','hisPerform','综合绩效分析','His Performance analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceMatching','设备匹配','Device matching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deptMatching','科室匹配','Device matching',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceGrouping','设备分组','Device grouping',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','chargeCode','收费代码与分组匹配','The charge code matches the group',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manualTasks','手动任务抓取数据','Manual tasks fetch data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','chargeDetailSource','收费明细源数据','Charge details source data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','chargeDetailResult','收费明细结果数据','Charge details source result data',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','commonFaults','常用故障及分类','Common faults and classification',NULL,-1);
-- YHAAO-17037 需求-非医疗设备的管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customI18n','assetClass','管理分类','Manage classifications',NULL,-1);
-- YHAAO-17290 his 设备绩效管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','importData','导入数据','Import data',NULL,-1);
-- YHAAO-17403 预供应商管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'preSupplierMgr', '预供应商管理', 'Pre-vendor management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'taxpayerIdentityNumber', '纳税人识别号', 'Taxpayer identification number', NULL, '-1');
-- YHAAO-16852 增加字段“数量”
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetQuantity','数量','Quantity',NULL,-1);
-- YHAAO-17377 增加资产编辑权限支持
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customRoleAuth','assetInfoAuth','资产信息','Asset information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInfoAuth','a','创建资产信息','Create asset information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInfoAuth','m','修改资产信息','Modify asset information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetInfo','资产信息','Asset information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('assetInfo','assetInfo.modify','修改资产信息','Modify asset information',NULL,-1);


--
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHighPartType','highPart','高值备件','high part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbHighPartType','bulbTube','球管','Bulb tube',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbcontractAmount','oneMillionLess','100W/年以下','one million less',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbcontractAmount','oneMillionMore','100W/年以上','one million more',NULL,-1);
-- YHAAO-17769 重点医疗设备使用状况报表
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','keyAssetDataInput','重点医疗设备使用状况报表','Report on the use of key medical equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','formTitle','标题','Title',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','formFiller','填表人','Form filler',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','formFillTime','填表日期','Date of completion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','reportYear','报表年份','Report year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetDetail','设备明细','Device details',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','depreciation','折旧','Depreciation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairSpend','维修支出','Repair expenses',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenanceSpend','维保支出','Maintenance expenses',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','suppliesSpend','耗材支出','Consumables expenditure',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','assetOperation','设备运营情况','Equipment operation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','sumIncome','收入总和','Sum of income',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkPeopleNum','检查人数','Check the number of people',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.keyAssetDataInput','重点医疗设备使用状况报表','Report on the use of key medical equipment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','historicalGains','历史收益','Historical gains',NULL,-1);
-- YHAAO-17712 MVS租户--设备信息字段与GE系统匹配
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('tenantVersionMenu','asset.importMvsDataFile','MVS设备信息导入','Import MVS device information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','importMvsDataFile','MVS设备信息导入','Import MVS device information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','productFamily','产品系列','Product family',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','productSegment','产品细分','Product segment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('productSegment','1','Premium','Premium',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('productSegment','2','Performance','Performance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('productSegment','3','Value','Value',NULL,-1);
-- YHAAO-18093 供应商信息里增加授权书到期时间
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PowerOfAttorneyExpireDays','授权书到期提醒提前天数','The number of days in advance of the authorization letter expiration reminder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PowerOfAttorneyExpireTimes','授权书到期提醒提前次数','The number of times in advance that the power of attorney expiration reminder is advanced',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PowerOfAttorneyExpireDate','授权书到期时间','Power of attorney expiration',NULL,-1);




-- YHAAO-18242  报修400wx页面优化相关
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','1','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','2','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','21','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','22','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','23','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','24','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','31','不能开机，有干扰，图像质量问题','不能开机，有干扰，图像质量问题',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','32','不能开机，有干扰，图像质量问题','不能开机，有干扰，图像质量问题',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','33','不能开机，有干扰，图像质量问题','不能开机，有干扰，图像质量问题',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','4','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','5','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','51','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','6','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','7','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','8','不能开机，不能扫描，不能曝光，有伪影','不能开机，不能扫描，不能曝光，有伪影',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','101','不能开机，不能传输，显示器故障','不能开机，不能传输，显示器故障',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairRequest400','3','不能开机，有干扰，图像质量问题','不能开机，有干扰，图像质量问题',NULL,-1);


-- YHAAO-18221
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('field_name','EventRecord','事件记录','Event Record',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('label','EventRecord','事件记录','Event Record',NULL,	-1	);
--YHAAO-18340 邵逸夫报损鉴定表导出 支持院区字段
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'SiteInconsistent', '所选设备的院区信息不一致', 'The campus information of the selected device is inconsistent', NULL, '-1');
-- YHAAO-18322 维修流程-维修工单报表 错误提示问题
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'siteCannotEmpty', '院区不能为空', 'Site cannot be empty', NULL, '-1');
-- YHAAO-18549 重点医疗设备使用状况报表以年度统计
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','reportType','报表类型','Report type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('keyAssetReportType','1','月度分析','Monthly analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('keyAssetReportType','2','年度分析','Annual analysis',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkItemNum','检查项目数','Number of inspection items',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Year','年份','Year',NULL,-1);
-- YHAAO-18932
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileNeedHisAssetData','文件中填写要导入的联机设备数据','Fill in the file with the online device data to be imported',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','HospitalCode','医院机构代码','hospitalCode',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','PresetValue','预置值','eq_lb',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','EqId','设备唯一识别编号','eq_id',NULL,	-1	);
-- YHAAO-18934
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','HisDepartmentCode','His科室代码','DepartmentCode',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','HisDepartmentName','His科室名称','DepartmentName',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','TyBz','是否停用','TyBz',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','FileNeedHisDepartmentData','文件中填写要导入的科室数据','Fill in the department data to be imported in the file',NULL,	-1	);

-- 保养附件类型
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('pmAttachments','PmReport','保养报告','PmReport',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('pmAttachments','MeReport','计量报告','MeReport',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('pmAttachments','AuReport','质控报告','AuReport',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('pmAttachments','PIReport','巡检报告','PIReport',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('pmAttachments','Other','其他','Other',NULL,	-1	);

-- YHAAO-19442 资产分类
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'assetTypeGroup', '资产分类', 'Asset Type Group', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'assetTypeGroupEncoded', '资产分类编码', 'Asset Type Group Encoding', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetClassificationEdit', '资产分类编辑', 'Asset classification editing', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetClassificationImport', '资产分类导入', 'Asset classification Import', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetClassificationManagement', '资产分类管理', 'Asset classification management', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CreateAssetClassification', '新增资产分类', 'Create asset classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AssetTypeGroup', '资产分类', 'Asset classification', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'classificationCode', '资产分类编码', 'classificationCode', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'ImportGroupingInBatch', '批量导入资产分类信息', 'ImportGroupingInBatch', NULL, '-1');

-- YHAAO-19547 设备超期
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'deviceOverdue', '设备超限统计', 'device overdue', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('businessType', 'assetScrap', '设备报废', 'assetScrap', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('businessType', 'assetDepartmentConvert', '设备转科', 'assetDepartmentConvert', NULL, '-1');


INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('approvalNodeStatus','0','待审批','0',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('approvalNodeStatus','1','审批中','1',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('approvalNodeStatus','2','审批通过','2',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('approvalNodeStatus','3','审批不通过','3',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('approvalNodeStatus','4','审批退回','4',NULL,	-1	);
-- YHAAO-19745 风险等级
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('customI18n','riskGrade','风险等级','riskGrade',NULL,-1);
-- YHAAO-19724 供应商管理-增加批量导入功能
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','contactInformation','联系方式','Contact information',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('message', 'MultipleRulesRecordDel', '提示：二级规则、三级规则、巡检和质量控制都被删除时该条记录会被删除', 'Note: When the second level rule, third level rule, inspection, and quality control are all deleted, the record will be deleted.', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('purchaseStatus', 'Enabled', '启用', 'Enabled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('purchaseStatus', 'NotEnabled', '未启用', 'Not Enabled', NULL, '-1');


-- YHAAO-20326 加备用字段1-5
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare01', '备用字段01', 'spare01', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare02', '备用字段02', 'spare02', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare03', '备用字段03', 'spare03', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare04', '备用字段04', 'spare04', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare05', '备用字段05', 'spare05', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare06', '备用字段06', 'spare06', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare07', '备用字段07', 'spare07', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare08', '备用字段08', 'spare08', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare09', '备用字段09', 'spare09', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('customI18n', 'spare10', '备用字段10', 'spare10', NULL, '-1');
-- YHAAO-19641 新增中医诊疗设备分类
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01', '诊断', 'Diagnosis', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01', '四诊', 'Four Diagnoses', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-01', '舌诊设备', 'Tongue Diagnosis Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-02', '其他望诊设备', 'Other Observation Diagnosis Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-03', '闻诊设备', 'Auscultation Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-04', '脉诊设备', 'Pulse Diagnosis Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-05', '中医四诊综合设备', 'Comprehensive Equipment for Four Traditional Chinese Medicine Diagnoses', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-06', '中医体质辨识系统', 'Traditional Chinese Medicine Constitution Identification System', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-01-07', '中医专家系统', 'Traditional Chinese Medicine Expert System', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-02', '经络', 'Meridian', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-02-01', '经络检测分析设备', 'Meridian Detection and Analysis Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-02-02', '穴位探测设备', 'Acupoint Detection Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-03', '其他', 'Others', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-03-01', '红外热像检测设备', 'Infrared Thermography Detection Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '01-03-02', '其他诊断设备', 'Other Diagnostic Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02', '针疗', 'Acupuncture Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-01', '普通针具', 'Common Acupuncture Needles', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-01-01', '毫针、梅花针、三棱针', 'Filiform Needle, Plum Blossom Needle, Three-edged Needle', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-01-02', '芒针、火针、揿针、磁圆针、磁极针、金银针、浮针、滚针、塑柄针灸针、耳针', 'Long Needle, Fire Needle, Press Needle, Magnetic Round Needle, Magnetic Pole Needle, Gold and Silver Needle, Floating Needle, Roller Needle, Plastic Handle Acupuncture Needle, Ear Needle', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-02', '针刀', 'Acupotomy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-02-01', '铍针、带刃针、埋线针、钩鍉针、小针刀、水针刀、筋骨针、锋钩针', 'Pi Needle, Bladed Needle, Catgut Embedding Needle, Hook Needle, Small Acupotomy Needle, Water Acupotomy Needle, Tendon and Bone Needle, Sharp Hook Needle', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03', '电针', 'Electroacupuncture', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-01', '温热电针治疗设备', 'Warm and Electroacupuncture Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-02', '冷针针灸设备', 'Cold Acupuncture Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-03', '针灸电麻设备', 'Acupuncture Electroanesthesia Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-04', '电滚针治疗设备', 'Electro Roller Needle Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-05', '耳针治疗设备', 'Ear Needle Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-06', '分证型治疗设备', 'Syndrome-specific Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-07', '子午流注治疗设备', 'Ziwu Liuzhu Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '02-03-08', '其他电针设备', 'Other Electroacupuncture Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '03', '灸疗', 'Moxibustion Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '03-01', '灸疗', 'Moxibustion Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '03-01-01', '灸疗器具', 'Moxibustion Instruments', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '03-01-02', '灸疗设备', 'Moxibustion Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '03-01-03', '仿灸治疗设备', 'Moxibustion-like Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04', '罐疗', 'Cupping Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01', '罐疗', 'Cupping Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-01', '普通火罐', 'Common Fire Cupping', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-02', '电火罐', 'Electric Cupping', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-03', '磁疗罐', 'Magnetic Therapy Cupping', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-04', '真空拔罐器具', 'Vacuum Cupping Instruments', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-05', '药罐', 'Medicated Cupping', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '04-01-06', '多功能罐疗设备', 'Multifunctional Cupping Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '05', '刮痧', 'Gua Sha Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '05-01', '刮痧', 'Gua Sha Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '05-01-01', '刮痧板', 'Gua Sha Board', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '05-01-02', '电刮痧设备', 'Electric Gua Sha Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06', '中药外治', 'External Treatment with Traditional Chinese Medicine', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-01', '中药熏洗', 'Fumigation and Washing with Traditional Chinese Medicine', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-01-01', '熏药设备', 'Herbal Fumigation Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-01-02', '熏蒸设备', 'Fumigation Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-01-03', '熏洗设备', 'Fumigation and Washing Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-01-04', '泡洗设备', 'Soaking Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-02', '肠道灌洗', 'Intestinal Lavage', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-02-01', '结肠灌洗（透析）设备', 'Colon Lavage (Dialysis) Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-02-02', '中药灌肠设备', 'Traditional Chinese Medicine Enema Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-03', '其他', 'Others', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-03-01', '中药离子导入设备', 'Traditional Chinese Medicine Ion Introduction Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-03-02', '中药雾化治疗设备', 'Traditional Chinese Medicine Atomization Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '06-03-03', '中药透药设备', 'Traditional Chinese Medicine Drug Penetration Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '07', '推拿', 'Tuina Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '07-01', '推拿', 'Tuina Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '07-01-01', '推拿治疗设备', 'Tuina Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '07-01-02', '推拿辅助治疗设备', 'Tuina Auxiliary Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08', '牵引', 'Traction Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08-01', '牵引', 'Traction Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08-01-01', '颈椎牵引设备', 'Cervical Traction Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08-01-02', '腰椎牵引设备', 'Lumbar Traction Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08-01-03', '多功能牵引设备', 'Multifunctional Traction Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '08-01-04', '其他牵引设备', 'Other Traction Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09', '中医光疗', 'Traditional Chinese Medicine Phototherapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01', '中医光疗', 'Traditional Chinese Medicine Phototherapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-01', '红光治疗设备', 'Red Light Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-02', '红外光疗设备', 'Infrared Light Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-03', '红外偏振光疗设备', 'Infrared Polarized Light Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-04', '紫外线治疗设备', 'Ultraviolet Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-05', '激光治疗设备', 'Laser Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-06', '激光穴位治疗设备', 'Laser Acupoint Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '09-01-07', '其他光疗设备', 'Other Phototherapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10', '中医电疗', 'Traditional Chinese Medicine Electrotherapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-01', '高频', 'High Frequency', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-01-01', '高频治疗设备', 'High Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-01-02', '超短波治疗设备', 'Ultra-short Wave Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-01-03', '微波治疗设备', 'Microwave Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-01-04', '射频治疗设备', 'Radio Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-02', '中频', 'Medium Frequency', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-02-01', '中频治疗设备', 'Medium Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-02-02', '中频经络治疗设备', 'Medium Frequency Meridian Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-02-03', '温热中频治疗设备', 'Warm Medium Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-02-04', '干扰电治疗设备', 'Interferential Current Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-03', '低频', 'Low Frequency', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-03-01', '低频治疗设备', 'Low Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-03-02', '低频经络治疗设备', 'Low Frequency Meridian Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-03-03', '超低频治疗设备', 'Ultra-low Frequency Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-04', '其他电疗设备', 'Other Electrotherapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '10-04-01', '其他电疗设备', 'Other Electrotherapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '11', '中医超声治疗', 'Traditional Chinese Medicine Ultrasound Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '11-01', '中医超声治疗', 'Traditional Chinese Medicine Ultrasound Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '11-01-01', '超声波治疗设备', 'Ultrasound Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '11-01-02', '超声穴位治疗设备', 'Ultrasound Acupoint Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '11-01-03', '多功能超声波治疗设备', 'Multifunctional Ultrasound Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12', '中医磁疗', 'Traditional Chinese Medicine Magnetic Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12-01', '中医磁疗', 'Traditional Chinese Medicine Magnetic Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12-01-01', '磁振热治疗设备', 'Magnetic Vibration Heat Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12-01-02', '特定电磁波治疗设备', 'Specific Electromagnetic Wave Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12-01-03', '穴位磁疗设备', 'Acupoint Magnetic Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '12-01-04', '磁场效应治疗设备', 'Magnetic Field Effect Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13', '中医热疗', 'Traditional Chinese Medicine Heat Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13-01', '中医热疗', 'Traditional Chinese Medicine Heat Therapy', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13-01-01', '微波热疗设备', 'Microwave Heat Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13-01-02', '电热疗设备', 'Electric Heat Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13-01-03', '蜡疗设备', 'Wax Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '13-01-04', '热敷（干、湿、陶瓷）装置', 'Hot Compress (Dry, Wet, Ceramic) Device', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14', '其他', 'Others', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01', '其他', 'Others', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-01', '辅助排痰设备', 'Auxiliary Sputum Discharge Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-02', '空气波压力治疗设备', 'Air Wave Pressure Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-03', '体外冲击波治疗设备', 'Extracorporeal Shock Wave Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-04', '多功能治疗床', 'Multifunctional Therapy Bed', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-05', '康复训练设备', 'Rehabilitation Training Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('spare01', '14-01-06', '结扎治疗设备', 'Ligation Therapy Equipment', null, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AdjustmentEquipmentName', '调剂设备名称', 'Name of adjustment equipment', NULL, '-1');
INSERT INTO i18n_message(msg_type, msg_key, value_zh,value_en,value_tw, site_id) VALUES ('field_name', 'patrolInspectionOwnerName','巡检负责人', 'Patrol inspection owner name', NULL, '-1');
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en,value_tw, site_id) VALUES ('field_name','patrolInspectionOwnerTel','巡检负责人电话','Patrol inspection owner tel',  NULL, '-1');

INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('equipmentDisposalMethod','0','其他','0',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('equipmentDisposalMethod','1','捐赠','1',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('equipmentDisposalMethod','2','上交国家','2',NULL,	-1	);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('equipmentDisposalMethod','3','放置库房','3',NULL,	-1	);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'equipmentDisposalMethod', '设备处置方式', 'equipmentDisposalMethod', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'customDisposalMethod', '自定义处置方式', 'customDisposalMethod', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '-2', '已拒绝', 'rejected', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '-1', '已取消', 'Cancelled', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '0', '待同意', 'not agreed', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '1', '待领用', 'not delivered', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '2', '待归还', 'not returned', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '3', '已归还','returned', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('borrowedStatus', '4', '维修中', 'in maintenance', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('assetBorrowedStatus', '-1', '暂停借出', 'Suspend lending', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('assetBorrowedStatus', '0', '可借', 'Borrowable', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('assetBorrowedStatus', '1', '占用', 'On secondment', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','lastInventoryDate','上次盘点日期','Last Inventory Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'specialAsset', '特种设备', 'specialAsset', NULL, '-1');
-- 	 YHAAO-21537 调剂中心借用申请增加多级审批
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('businessType', 'equipmentAdjustment', '设备调剂', 'equipment adjustment', NULL, -1);

--报修发生地
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairReportSendPlace','报修发生地','repair Report Send Place',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairReportSendPlace','ward','病房','ward',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('repairReportSendPlace','non-ward','非病房','non-ward',NULL,-1);
INSERT INTO i18n_message (msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DisplayRepairReportSendPlace','显示报修发生地','Display repairReportSendPlace',NULL,-1);

--不良事件工单类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','adverseEventType','不良事件工单类型','adverse Event Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventType','1','医疗器械不良事件','1',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('adverseEventType','2','医疗器械不良事件哨点监测','2',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','Accounts','台账','Accounts',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','Maintenance','保养','Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','QualityControl','质控','QualityControl',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','DailyCheck','日常检查','DailyCheck',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','Measurement','计量','Measurement',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','Inventory','盘点','Inventory',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','Telephone','电话报修','Telephone',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('RepairSource','ServiceRequestNoAsset','无台账报修','ServiceRequestNoAsset',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','RepairSource','维修工单来源','RepairSource',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importTemplateType','ToBeTerminated','待报废模板','ToBeTerminated',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('importTemplateType','Terminated','报废模板','Terminated',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisAssetGroupType','Cost','成本','Cost',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisAssetGroupType','Income','收入','Income',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','LaborCosts','人工费','LaborCosts',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','DepreciationHouse','房屋折旧费','DepreciationHouse',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','MaintenanceFee','维修费','MaintenanceFee',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','WarrantyFee','维保费','WarrantyFee',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','MedicalSupplies','药物耗材费','MedicalSupplies',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('HisEntryType','Utilities','水电费','Utilities',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('field_name', 'nextMeteringDate', '下次计量日期', 'Next Metering Date', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','stockStatus','状态','Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','1','待审核','To be approved',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','2','待入库','Approval',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','3','已入库','In stock',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','4','已出库','Out stock',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','5','已退库','Returned stock',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('stockStatus','6','无效入库','Invalid in stock',NULL,-1);
--重点设备
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualRepairExpenses', '年维修支出', 'Annual repair expenses', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualMaintenanceExpenses', '年维保支出', 'Annual maintenance expenses', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualConsumablesExpenses', '年耗材支出', 'Annual consumables expenses', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualNetIncome', '年度净收入', 'Annual net income', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualIncome', '年收入', 'Annual income', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'annualNumberOfCheck', '年检查数(项目)', 'Annual number of check(project)', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'month', '月份', 'Month', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'netIncome', '净收入', 'Net income', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'inCome', '收入', 'Income', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'numberOfCheck', '检查数(项目)', 'Number of check', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'summary', '总结', 'summary', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES('label', 'equipmentSummary', '设备总结', 'Equipment summary', NULL, -1);
-- 工单状态
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Open','开单','Open',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Assigned','派工','Assigned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Accept','工程师接单','Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Closed','关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Cancelled','取消','Cancelled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Start','开始工作','Start',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Finish','工作完成','Finish',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Olcfix','远程支持','Olcfix',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderStatus','Equipment Up','设备上线','Esquipment Up',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('poCatBusinessType','Probe','探头','Probe',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('poCatBusinessType','ToolBox','工具箱','ToolBox',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('poCatBusinessType','SpecialPart','特殊备件','SpecialPart',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('poCatToolBoxType','Radiation','放射类设备工具箱','Radiation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('poCatToolBoxType','Ultrasonic','超声类设备工具箱','Ultrasonic',NULL,-1);



--积分申请类型
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderType','EngineerOnSiteRepair','GE工程师现场维修','Engineer On Site Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderType','EngineerOnSiteMaintenance','GE工程师现场保养','Engineer On Site Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderType','ApplicationSupport','GE应用支持','Application Support',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderType','ToolBox','工具箱','Tool Box',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','Requested','已申请','Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','ManageAccept','经理审批通过','Manage Support Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','ManageReject','经理审批拒绝','Manage Support Reject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','Ordered','已下单','Ordered',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','OrderedReject','400下单拒绝','OrderedReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','EngineerQualificationAccept','工程师资质审批通过','Engineer Qualification Accept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderStatus','EngineerQualificationReject','工程师资质审批拒绝','Engineer Qualification Reject',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderSteps','EngineerQualificationPending','待工程师资质审批','Engineer Qualification Pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderSteps','ManagePending','待经理审批','Manage pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderSteps','OrderPending','待下单','Ordered pending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('zkbIntegralOrderSteps','Complete','完成','Complete',NULL,-1);

-- YHAAO-23585 新增web端批量转科流程
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('businessType', 'bathAssetDepartmentConvert', '批量设备转科', 'bath asset department convert', NULL, '-1');



-- 培训管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainType','TechnologyTrain','技术培训','Technology Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainType','ClinicalTrain','临床培训','Clinical Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainType','SecurityTrain','安全培训','Security Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainType','QualityControlTrain','质控培训','Quality Control Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainType','ManageTrain','管理培训','Manage Train',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMode','SceneTrain','现场培训','Scene Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMode','OnlineTrain','在线培训','On Line Train',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMode','FileTrain','文档培训','File Train',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainStatus','Planned','已计划','Planned',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainStatus','Completed','已完成','Completed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainStatus','Canceled','已取消','Cancelled',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','trainManage','培训管理','trainManage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','createTrainMange','新增培训计划','createTrainMange',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','editTrainMange','编辑培训计划','editTrainMange',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsType','YYPX','应用培训','YYPX',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsType','YSXJPX','管理培训','YSXJPX',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsType','EHSAQJC','EHS检查','EHSAQJC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsType','EHSLLJJ','EHS培训','EHSLLJJ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsType','PX-OJT','OJT','PX-OJT',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsStatus','Open','进行中','Open',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsStatus','Cancelled','已取消','Cancelled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsStatus','Closed','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsStatus','Processed','已完成','Processed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('trainMvsStatus','Pending','待完成','Pending',NULL,-1);


-- 维修工单模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','XCWX','现场维修','Field Maintenance',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','YCZC','远程支持','Remote Support',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','JSZC','技术支持','Technical Support',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','WXPT','维修陪同','Maintenance Escort',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','ZBWC','转包/外采','Subcontract/External Procurement',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','WX-OJT','OJT','OJT',NULL,-1);

-- 入保前评估模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','RBQPG-1','入保前评估','Pre-insurance Assessment',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','RBQPG-JT','OJT','OJT',NULL,-1);

-- 销售支持模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','XSZC-1','销售支持','Sales Support',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','KHZC-XJ','客户支持-巡检','Customer Support - Inspection',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','KHZC-ZK','客户支持-质控','Customer Support - Quality Control',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','KHZC-BY','客户支持-保养','Customer Support - Maintenance',NULL,-1);

-- 保养模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','BZBY','标准保养','Standard Maintenance',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','SDBY','深度保养','Deep Maintenance',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','BY-OJT','OJT','OJT',NULL,-1);

-- 巡检模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','XJ-1','巡检','Inspection',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','XJ-OJT','OJT','OJT',NULL,-1);

-- 质控模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','ZK-1','质控','Quality Control',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','ZK-OJT','OJT','OJT',NULL,-1);

-- 非维修工作模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','BWZC','非维修类事务','Non-maintenance Tasks',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','GEFEZC','GE FE支持','GE FE Support',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','APMZC','APM支持','APM Support',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','PD','盘点','Inventory Check',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','QT-2','其他','Other',NULL,-1);

-- 强检模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','QJ','强检','Mandatory Inspection',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','JL-OJT','OJT','OJT',NULL,-1);

-- 培训模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','YYPX','应用培训','Application Training',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','YSXJPX','医师巡检培训','Physician Inspection Training',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','EHSAQJC','EHS检查','EHS Inspection',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','EHSLLJJ','EHS培训','EHS Training',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','PX-OJT','OJT','OJT',NULL,-1);

-- 客户拜访与服务报告
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','KHBF','客户拜访','Customer Visit',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','FWBG','服务报告','Service Report',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','PMOGL-OJT','OJT','OJT',NULL,-1);

-- 其他模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','QT-1','其他','Other',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','QT-OJT','OJT','OJT',NULL,-1);

-- 巡场模块
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhome','XC-1','巡场','Site Patrol',NULL,-1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'WX', '维修', 'Maintenance', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'RBQPG', '入保前评估', 'Pre-insurance Assessment', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'XSZC', '商务支持', 'Business Support', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'BY', '保养', 'Preventive Maintenance', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'XJ', '巡检', 'Inspection', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'ZK', '质控', 'Quality Control', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'XWXGZ', '事务性工作', 'Transactional Work', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'JL', '计量', 'Calibration', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'PX', '培训', 'Training', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'PMOGL', 'PMO管理', 'PMO Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'QT', '其他', 'Other', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderEhomeMani', 'XC', '巡场', 'Site Patrol', NULL, -1);

-- 工单状态国际化配置
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('woStatusEhome','Open','进行中','In Progress',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('woStatusEhome','Cancelled','已取消','Cancelled',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('woStatusEhome','Closed','已完成','Completed',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('woStatusEhome','Pending','已关单','Pending Closure',NULL,-1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','1','设备不清晰','Device Unclear',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','2','IOU/Support biz','IOU/Support Business',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','3','内部支持','Internal Support',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','4','GE FE支持','GE FE Support',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','5','保外支持','Out-of-Warranty Support',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','6','acct级别服务','Account-Level Service',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','7','巡场','Site Patrol',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('reportTypeAcctEhome','8','Precheck','Pre-inspection',NULL,-1);






-- 工时类型 - 差旅
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '1', '差旅', 'Travel', NULL, -1);

-- 工时类型 - 工作
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '2', '工作', 'Work', NULL, -1);

-- 工时类型 - 备件等待
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '3', '备件等待', 'Spare Parts Waiting', NULL, -1);

-- 工时类型 - 设备观察
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '4', '设备观察', 'Equipment Monitoring', NULL, -1);

-- 工时类型 - 远程预判
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '5', '远程预判', 'Remote Diagnosis', NULL, -1);

-- 工时类型 - 寄修
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '6', '寄修', 'Mail-in Repair', NULL, -1);

-- 工时类型 - 商务确认
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)
VALUES ('workOrderHoursTypeEhome', '7', '商务确认', 'Business Confirmation', NULL, -1);
-- 日常检查状态新增
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('nurseSiteMaintainStatus','9','有问题','Has Issue',NULL,-1);



--维保合同申购
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintenanceContractApplyDetail', '设备保养采购申请详情', 'Maintenance Contract Apply Detail', NULL, -1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','TempSave','暂存','TempSave',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','Requested','已提交','Requested',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','ApprovalAccept','审核通过','ApprovalAccept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','ApprovalReject','审核不通过','ApprovalReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','MeetingReasoningAccept','过会论证通过','MeetingReasoningAccept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','MeetingReasoningReject','过会论证不通过','MeetingReasoningReject',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','PurchaseConfirmationAccept','采购信息确认通过','PurchaseConfirmationAccept',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStatus','PurchaseConfirmationReject','采购信息确认不通过','PurchaseConfirmationReject',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStep','SubmitPending','待提交','SubmitPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStep','ApprovalPending','待审核','ApprovalPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStep','MeetingReasoningPending','待过会论证','MeetingReasoningPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStep','PurchaseConfirmationPending','采购信息确认','PurchaseConfirmationPending',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('mcApplyPurchaseStep','Complete','完成','Complete',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'WorkOrderTypeNew_xlsx', '工单分类', 'WorkOrderType', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CoworkerEntryType_xlsx', '协作者工时填写方式', 'CoworkerEntryType', NULL, '-1');
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'CoworkerEntryDetail_xlsx', '协作者工时详情', 'CoworkerEntryDetail', NULL, '-1');

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','020','保养（PM）','PM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','022','紧急维修（EMG）','EMG',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','023','远程维修（Phone Support）','Phone Support',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','021','合并保养（CPM）','CPM',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','024','移机（Remove）','Remove',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','025','升级优化（FMI）','FMI',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','026','质控（QC）','QC',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','027','其他（other)','other',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('workOrderType','028','合并紧急维修（CEMG）','CEMG',NULL,-1);


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'UnderReview', '审批中', 'Under review', NULL, '-1');



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES('label', 'AddMessage', '新增消息', 'AddMessage', NULL, '-1');


INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PasswordLogin','密码登录','Password Login',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SmsLogin','短信登录','SMS Login',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WelcomeToGeAPMPlatform','欢迎使用GE APM资产平台','Welcome to GE APM Platform',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ParentDevice','主设备','Parent Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ChildDevice','子设备','Child Device',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BatchModify','批量修改','Batch Modify',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PreviewPrintQRcode','预览打印二维码','Preview and Print QR code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MultiLevelApproval','多级审批','Multi-level approval',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MyRequests','我发起的','My Requests',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ApprovalNumber','审批单号','Approval Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BusinessNumber','业务单号','Business Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BusinessType','业务类型','Business Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RequestingDepartment','申请科室','Requesting Department',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RequestedBy','发起人','Requested By',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ApprovalTime','审批时间','Approval Time',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CompletionTime','完成时间','Completion Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DailyInspectionRecord','日常检查记录','Daily Inspection Record',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ScheduledInspectionDate','计划检查日期','Scheduled Inspection Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ActualInspectionDate','实际检查日期','Actual Inspection Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Inspector','检查人','Inspector',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','OperationLog','开关机记录','Operation Log',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','StartupTime','开机时间','Startup Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ShutdownTime','关机时间','Shutdown Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','StartupPerson','开机人','Startup Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ShutdownPerson','关机人','Shutdown Person',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RuntimeDuration','使用时长','Runtime Duration',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CertificateNo','计量证书编号','Certificate No.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','VerificationConclusion','检定结论','Verification Conclusion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','VerificationDate','计量日期','Verification Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AdverseEventRecords','不良事件记录','Adverse Event Records',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','WorkOrderNo','工单编号','Work Order No.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ReportedBy','报告人','Reported By',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ReportedTime','报告时间','Reported Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ApprovalWorkflow','设备流程记录','Approval Workflow',NULL,-1);



INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmCurrentSteps','Assign','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmCurrentSteps','Accept','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmCurrentSteps','Maintain','已计划','in Maintenance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('pmCurrentSteps','Acceptance','待验收','To be acknowledged',NULL,-1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LoginFailed', '登录失败', 'Login Failed', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MobilePhone', '登录手机号', 'Mobile Phone', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'VerificationCode', '短信验证码', 'Verification Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SendCode', '获取验证码', 'Get Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ResetPasswordTitle', '请修改密码', 'Please Reset Password', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRules1', '根据中国电信云平台最新安全规范要求，为保障用户权益和信息安全，', 'According to the latest security requirements, to protect user rights and information security,', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRules2', '请将密码至更高安全级别，密码设置规则如下:', 'please set your password to a higher security level with the following rules:', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRule1', '1.密码长度至少8位，不能包含空格，制表符或换页符', '1.Password must be at least 8 characters long, cannot contain spaces, tabs or form feeds', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRule2', '2.必须以大写英文字母开头，小写英文字母结尾', '2.Must start with uppercase letter and end with lowercase letter', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRule3', '3.密码必需包含4种字符：大写字母，小写字母，特殊字符(如: ! @ # $ % &)和数字', '3.Must contain 4 character types: uppercase, lowercase, special characters (!@#$%&) and numbers', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PasswordRule4', '4.密码不能有3个连续相同字符或3个连续字符 (如： 111, aaa, 123, abc...)', '4.Cannot have 3 consecutive identical or sequential characters (e.g. 111, aaa, 123, abc...)', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ReenterPassword', '请重输新密码', 'Re-enter New Password', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DeviceCount', '设备数量', 'Device Count', '設備數量', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Print', '打印', 'Print', '列印', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BatchConfigMaintenanceRule', '批量配置保养规则', 'Batch Configure Maintenance Rules', '批量配置保養規則', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SecondLevel', '二级', 'Second Level', '二級', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ThirdLevel', '三级', 'Third Level', '三級', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'RedirectToRuleManagement', '是否跳转到资产规则管理页面', 'Redirect to Asset Rule Management?', '是否跳轉到資產規則管理頁面', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Yes', '是', 'Yes', '是', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'No', '否', 'No', '否', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierImport', '供应商导入', 'Supplier Import', '供應商導入', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierRelation', '供应商关联', 'Supplier Relation', '供應商關聯', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierNotice', '供应商通知', 'Supplier Notice', '供應商通知', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'InvitationQRCode', '邀请二维码', 'Invitation QR Code', '邀請QR碼', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierCertExpiringSoon', '供应商证照即将到期', 'Supplier Certificate Expiring Soon', '供應商證照即將到期', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierCertExpired', '供应商证照已经到期', 'Supplier Certificate Expired', '供應商證照已經到期', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'source', '来源', 'Source', '來源', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintainedByHospital', '院方维护', 'Maintained by Hospital', '院方維護', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintainedBySupplier', '供应商维护', 'Maintained by Supplier', '供應商維護', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BatchDelete', '批量删除', 'Batch Delete', '批量刪除', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BatchImportSupplierData', '批量导入供应商数据', 'Batch Import Supplier Data', '批量導入供應商數據', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'OnlyXlsXlsxSupported', '只支持xls或xlsx格式的文件', 'Only xls or xlsx files are supported', '僅支持xls或xlsx格式的文件', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'FileSizeLimit5MB', '文件大小不得超过5M', 'File size cannot exceed 5MB', '文件大小不得超過5MB', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ConfirmUpload', '确认上传', 'Confirm Upload', '確認上傳', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Instructions', '说明', 'Instructions', '說明', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'FillInSupplierData', '文件中填入要导入的供应商数据', 'Fill in the supplier data to be imported in the file', '文件中填入要導入的供應商數據', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UploadFileToImport', '上传文件进行导入', 'Upload file to import', '上傳文件進行導入', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'FailedImportData', '导入失败数据', 'Failed Import Data', '導入失敗數據', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ExcelRowNumber', 'EXCEL行数', 'Excel Row Number', 'EXCEL行數', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ContactName', '联系人姓名', 'Contact Name', '聯繫人姓名', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ContactInfo', '联系方式', 'Contact Information', '聯繫方式', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ImportErrorInfo', '导入失败信息', 'Import Error Information', '導入失敗信息', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'VenderCompanyLogin', '供应商登录', 'Vender Company Login', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'corporate', '企业法人', 'Corporate', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'projectDockerName', '负责人姓名', 'Person in Charge''s Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'city', '城市', 'City', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'relation', '关联', 'Relation', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'NotRelated', '未关联', 'Not Related', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Related', '已关联', 'Related', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'VenderCompanyLoginUrlTitle', '供应商登录链接查看', 'Vender Company Login URL View', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CopyLink', '复制链接', 'Copy Link', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'noticeUser', '通知人', 'Notifier', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'noticeContent', '通知内容', 'Notice Content', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'replyUser', '回复人', 'Responder', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'replyContent', '回复内容', 'Reply Content', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'replyDate', '回复时间', 'Reply Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'NotCompleted', '未完成', 'Not Completed', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Completed', '已完成', 'Completed', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DeleteConfirmation', '删除确认', 'Delete Confirmation', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DeleteConfirmationMsg', '确定要删除这条记录吗？', 'Are you sure to delete this record?', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DeviceArchiveEditSupplierInfo', '设备档案  修改 供应商信息', 'Device Archive - Edit Supplier Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierBasicInfo', '供应商基本信息', 'Supplier Basic Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'supplier', '供应商', 'Supplier', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'supplierCode', '供应商编码', 'Supplier Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'supplierAbbreviation', '供应商名称缩写', 'Supplier Abbreviation', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'creditCode', '统一社会信用编码', 'Unified Social Credit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessLicenseInfo', '营业执照信息', 'Business License Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'licenseCode', '营业执照编码', 'Business License Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'licenseEndDate', '营业执照截止日期', 'Business License End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'licenseScope', '业务范围', 'Business Scope', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LicenseAttachments', '营业执照附件', 'Business License Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MedicalDevicePermitInfo', '医疗器械经营许可证/医疗器械经营备案凭证信息', 'Medical Device Operation Permit / Record Certificate Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'permitCode', '许可证编码', 'Permit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'permitEndDate', '许可证截止日期', 'Permit End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PermitAttachments', '许可证附件', 'Permit Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AfterServiceAttachments', '售后服务承诺附件', 'After-Sales Service Commitment Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AuthorizationAttachments', '授权书附件', 'Authorization Letter Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingPermitInfo', '生产许可证信息', 'Manufacturing Permit Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'manufacturingPermitCode', '生产许可证编码', 'Manufacturing Permit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'manufacturingPermitEndDate', '生产许可证截止日期', 'Manufacturing Permit End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingPermitAttachments', '生产许可证附件', 'Manufacturing Permit Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductRegistrationInfo', '产品注册证信息', 'Product Registration Certificate Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'productRegistrationCode', '产品注册证编号', 'Product Registration Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'productRegistrationStartDate', '产品注册证开始日期', 'Product Registration Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'productRegistrationEndDate', '产品注册证结束日期', 'Product Registration End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductRegistrationAttachments', '产品注册证附件', 'Product Registration Certificate Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManageSupplierResponsiblePersons', '管理供应商相关责任人', 'Manage Supplier Responsible Persons', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PendingApprovalSupplierResponsiblePerson', '待审批供应商责任人', 'Pending Approval Supplier Responsible Persons', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UsernameOrPhone', '用户名/手机号', 'Username/Phone Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Query', '查询', 'Query', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UserName', '用户名', 'User Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PhoneNumber', '手机号', 'Phone Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Email', '邮箱', 'Email', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Position', '职务', 'Position', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Approve', '通过', 'Approve', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ApprovedSupplierResponsiblePerson', '已审批供应商责任人', 'Approved Supplier Responsible Persons', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Operation', '操作', 'Operation', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Deactivate', '停用', 'Deactivate', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MiniProgramQRCode', '小程序二维码', 'Mini Program QR Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'WeChatOfficialAccountQRCode', '公众号二维码', 'WeChat Official Account QR Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Relate', '关联', 'Relate', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierDetailInfo', '供应商详细信息', 'Supplier Detail Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CorporateName', '法人姓名', 'Corporate Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CorporateContact', '法人联系方式', 'Corporate Contact', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UnifiedCreditCode', '统一社会信用代码', 'Unified Social Credit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProjectContactName', '项目对接人姓名', 'Project Contact Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProjectContactTel', '项目对接人电话', 'Project Contact Telephone', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProjectContactPosition', '项目对接人职位', 'Project Contact Position', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SupplierType', '供应商类型', 'Supplier Type', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LicenseCode', '营业执照编码', 'Business License Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LicenseStartDate', '营业执照开始日期', 'Business License Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LicenseEndDate', '营业执照截止日期', 'Business License End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'LicenseScope', '业务范围', 'Business License Scope', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintenanceQualificationInfo', '维修资质信息', 'Maintenance Qualification Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintenanceAttachments', '维修资质相关的附件', 'Maintenance Qualification Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PermitCode', '许可证编码', 'Permit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PermitStartDate', '许可证开始日期', 'Permit Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PermitEndDate', '许可证截止日期', 'Permit End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AfterSaleServiceAttachments', '售后服务承诺附件', 'After-Sales Service Commitment Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessFilingInfo', '医疗器械经营备案凭证信息', 'Business Filing Certificate Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessFilingCode', '备案凭证编码', 'Business Filing Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessFilingStartDate', '备案凭证开始日期', 'Business Filing Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessFilingEndDate', '备案凭证截止日期', 'Business Filing End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BusinessFilingAttachments', '备案凭证附件', 'Business Filing Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingPermitCode', '生产许可证编码', 'Manufacturing Permit Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingPermitStartDate', '生产许可证开始日期', 'Manufacturing Permit Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingPermitEndDate', '生产许可证截止日期', 'Manufacturing Permit End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingFilingInfo', '医疗器械生产备案凭证信息', 'Manufacturing Filing Certificate Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingFilingCode', '生产备案凭证编码', 'Manufacturing Filing Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingFilingStartDate', '生产备案凭证开始日期', 'Manufacturing Filing Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingFilingEndDate', '生产备案凭证截止日期', 'Manufacturing Filing End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ManufacturingFilingAttachments', '生产备案凭证附件', 'Manufacturing Filing Attachments', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductRegistrationCode', '产品注册证编号', 'Product Registration Code', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductRegistrationStartDate', '产品注册证开始日期', 'Product Registration Start Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ProductRegistrationEndDate', '产品注册证截止日期', 'Product Registration End Date', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SendNotice', '发送通知', 'Send Notice', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'NoticeContent', '通知内容', 'Notice Content', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'Send', '发送', 'Send', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AddSupplier', '添加供应商', 'Add Supplier', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ImportAssetInfo', '导入资产信息', 'Import Asset Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SingleCampusImport', '单院区导入', 'Single Campus Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'FileNeedAssetData', '文件需要包含资产数据', 'File needs to contain asset data', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UpFileImport', '上传文件导入', 'Upload File to Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CrossCampusImport', '跨院区导入', 'Cross Campus Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AllRecord', '所有记录', 'All Records', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ErrorRecord', '错误记录', 'Error Records', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'AssetImport', '资产导入', 'Asset Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'EXCELRowNumber', 'EXCEL行号', 'EXCEL Row Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CheckResults', '检查结果', 'Check Results', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'IfIsNullThenGenerate', '如果为空则生成', 'Generate if empty', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'DepartmentImport', '部门导入', 'Department Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'PersonnelImport', '人员导入', 'Personnel Import', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'orgInfoId', '机构信息', 'Organization Information', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'BindRulesNotice', '请分别绑定保养规则、临床助手规则和日常使用登记规则！', 'Please bind maintenance rules, clinical assistant rules and daily use registration rules respectively!', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'UncompletedInspectionNotice', '设备存在未完成的日常检查，需要删除吗？', 'There are uncompleted daily inspections for the equipment, do you want to delete them?', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'GenerateQrcodeIfEmpty', '如果为空则生成二维码', 'Generate QR code if empty', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'GeneratePmOrder', '同时生成保养工单', 'Generate maintenance work order at the same time', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'selectOneMessage', '请选择', 'Please select', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'ValidationFailed', '验证失败', 'Validation failed', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CreateWorkOrder', '创建工单', 'Create Work Order', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'requestorPhone', '报修人电话', 'Requestor Phone', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'customerName', '医院客户姓名', 'Hospital Customer Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'customerTel', '医院客户电话', 'Hospital Customer Phone', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'customerPosition', '医院客户职务', 'Hospital Customer Position', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'RepairTelephoneNumberTips', '维修电话号码提示', 'Repair Telephone Number Tips', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaxImageUploadLimit', '图片数量最多上传5份!', 'Maximum 5 images can be uploaded!', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'RepairReporterPhone', '报修人电话', 'Reporter Phone', NULL, -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','hospitalAreaName','院区名称','Hospital Area Name',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','updateDownTime','更新停机时间','Update Downtime',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairWorkorderSource','维修工单来源','Repair Workorder Source',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','printSparePartsList','打印备件单','Print Spare Parts List',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','latestMessages','最近消息','Latest Messages',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','countyManHoursInfo','县域工时信息','County Man-hours Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','countyTenantSparePartsReturn','县域租户备件归还','County Tenant Spare Parts Return',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','coWorkerManhours','协作人工时','Co-worker Man-hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','attachmentUpload','附件上传','Attachment Upload',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','sparePartsDescription','备件描述','Spare Parts Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalNumber','审批单号','Approval Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','businessNumber','业务单号','Business Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','businessType','业务类型','Business Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approver','审批人','Approver',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalLevel','审批级别','Approval Level',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalOpinion','审批意见','Approval Opinion',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalStatus','审批状态','Approval Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalNodeStatus','审批节点状态','Approval Node Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approvalTime','审批时间','Approval Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenancePersonInfo','维修人信息','Maintenance Person Information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','signatureImage','签名图片','Signature Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manHoursOwner','工时所属人','Man-hours Owner',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manHoursDate','工时日期','Man-hours Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','workHours','工作工时','Work Hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','travelHours','差旅工时','Travel Hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','waitingHours','等待工时','Waiting Hours',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manHoursDescription','工时描述','Man-hours Description',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','partsItemCode','备件编码','Parts Item Code',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','partType','备件类型','Part Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','applyTime','申请时间','Apply Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','estimatedArrivalTime','预计到达时间','Estimated Arrival Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','arrivalConfirmationTime','到达确认时间','Arrival Confirmation Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','operation','操作','Operation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','view','查看','View',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','workTimePeriod','工作时间段','Work Time Period',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','remark','备注','Remark',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usedPart','使用部件','Used Part',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','externalQuotation','外部报价','External Quotation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','latestDevelopments','最新进展','Latest Developments',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','externalExpenses','外部费用','External Expenses',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','workOrderType','工单类型','Work Order Type',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','checkInTime','签到时间','Check-in Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','requestWorkorder','请求工单','Request Workorder',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mvsContractStartDate','MVS合同开始日期','MVS Contract Start Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','mvsContractEndDate','MVS合同结束日期','MVS Contract End Date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','upTimeExcel','开机时间','Up Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','pickUpTime','取件时间','Pick-up Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','cancelor','取消人','Canceler',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','requestImage','请求图片','Request Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairImage','维修图片','Repair Image',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairAttachment','维修附件','Repair Attachment',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','repairVideo','报修视频','Repair Video',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditAssets','批量修改设备','Batch Edit Assets',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditAffectedDevices','此次操作将对','This operation will affect',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','devices','台设备生效','devices',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditBasicInfo','批量修改基本信息','Batch Edit Basic Information',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditWarning','如您未选择任何设备，此次批量修改将被应用到所有可见设备，请谨慎操作！','Warning: If no devices are selected, this batch edit will be applied to all visible devices. Please proceed with caution!',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEdit','批量修改','Batch Edit',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditErrorInfo','批量修改异常信息','Batch Edit Error Information',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','errorMessage','错误提示','Error Message',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','rotate','旋转','Rotate',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','enlarge','放大','Enlarge',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','shrink','缩小','Shrink',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','voice','语音','Voice',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','playing','播放','Playing',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','selected','选择','Selected',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','minute','分','min',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','second','秒','sec',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deleteUnfinishedCheckPrompt','设备存在未完成的日常检查，需要删除吗？','There are unfinished daily checks for this device. Delete them?',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','yes','是','Yes',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','no','否','No',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Rewrite','重写','Rewrite',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','IBContractCategory','IB合同类别','IB Contract Category',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DailyInspectionRule','日常检查规则','Daily Inspection Rule',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UsageRegistrationRule','使用登记规则','Usage Registration Rule',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','SpecialAssetInfo','特种设备信息','Special Asset Info',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RulesSettings','规则设置','Rules Settings',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ToSpecialEquipment','转特种设备','To Special Equipment',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ScrapApplication','报废申请','Scrap Application',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','reportName','报告名称','Report Name',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','testingUnit','检测单位','Testing Unit',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','testResults','检测结果','Test Results',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','certificateIssuingUnit','报告发证单位','Certificate Issuing Unit',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','effectiveTime','报告生效时间','Effective Time',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','expiryDate','报告到期时间','Expiry Date',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','certificateName','证件名称','Certificate Name',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','certificateType','证书类型','Certificate Type',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','holder','持证人','Holder',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AnnualInspectionReport','年检报告','Annual Inspection Report',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PermitInfo','证照信息','Permit Info',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','hospitalName','医院名称','Hospital Name',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','NormalUsage','可正常使用','Normal Usage',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','IfFilledMustBe13Digits','如填写，长度必须填为13位','If filled, must be 13 digits',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','IfFilledMustBe10Digits','如填写，长度必须填为10位','If filled, must be 10 digits',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'save', '保存', 'Save', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'maxUpload5Pics', '图片数量最多上传5份！', 'Maximum 5 pictures can be uploaded!', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'maxUpload5Docs', '文档数量最多上传5份！', 'Maximum 5 documents can be uploaded!', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'manHoursEntryType', '工时填写方式', 'Man Hours Entry Type', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'addManHoursSection', '增加工时段', 'Add Man Hours Section', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'enterTotalManHours', '填写总工时', 'Enter Total Man Hours', NULL, -1);


INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'totalHoursWorkedOnly', '仅总工时', 'Total hours worked only', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'duringWorkingHoursOnly', '仅工时段', 'During working hours only', NULL, -1);

INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'coworkerManHoursEntryType', '协作者工时填写方式', 'Collaborator Man Hours Entry Type', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'partsDescription', '备件描述', 'Parts Description', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'manHoursManagement', '工时管理', 'Man Hours Management', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'workOrderNumber', '工单编号', 'Work Order Number', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'MaintenanceRule', '保养规则', 'Maintenance Rule', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CustomerContact', '客户联系人', 'Customer Contact', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CustomerPhone', '客户电话', 'Customer Phone', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'HospitalName', '医院名称', 'Hospital Name', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'WorkOrderComment', '工单备注', 'Work Order Comment', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'CancelReasonCannotBeEmpty', '工单的取消原因不能为空', 'Cancel reason cannot be empty', NULL, -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AssetName','资产名称','Asset Name',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AcceptanceSignature','验收人签名','Acknowledgment Signature',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Arrival','到货','Arrived',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','maintenancePersonInformation','保养人信息','Maintenance Personnel Information',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ExpenseDetails','费用明细','Expense Details',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Manhours','工时','Man Hours',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AddParts','增加备件','Add Parts',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','MaintenancePhotos','保养前后照片','Maintenance Photos',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','BeforeMaintenancePhotos','保养前照片','Before Maintenance Photos',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AfterMaintenancePhotos','保养后照片','After Maintenance Photos',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AddAttachment','添加附件','Add Attachment',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AttachmentType','附件类型','Attachment Type',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','PartsDescription','备件描述','Parts Description',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Acknowledger','验收人','Acknowledger',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','Signature','签名','Signature',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ViewSignature','查看签名','View Signature',NULL,-1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'RemainingStock', '所剩库存', 'Remaining Stock', '剩餘庫存', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'SparePartCode', '备件编号', 'Spare Part Code', '備件編號', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'batchAcceptance', '批量验收', 'Batch Acceptance', '批量驗收', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label', 'batchApproval', '批量审批', 'Batch Approval', '批量審批', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES ('label', 'workHourManagement', '工时管理', 'Work Hour Management', '工時管理', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES ('label', 'InvalidCredentials', '用户名或密码错误。请重新输入用户名和密码！', 'Incorrect username or password. Please re-enter your username and password!', '用戶名或密碼錯誤。請重新輸入用戶名和密碼！', -1);
INSERT INTO i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id)VALUES ('label', 'AccountDisabled', '该账号已被停用，请联系系统管理员!', 'This account has been disabled. Please contact the system administrator!', '該帳號已被停用，請聯繫系統管理員！', -1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','cyclePeriodUnit','循环周期单位','Cyclic period unit',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('cyclePeriodUnit','y','年','Year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('cyclePeriodUnit','m','月','Month',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('cyclePeriodUnit','w','周','Week',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('cyclePeriodUnit','d','日','Day',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AffiliatedOrganization','所属机构','Affiliated Organization',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','deviceExpiryManagement','设备效期管理','Device expiry management',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','editUsageYear','修改设备使用年限','Edit usage year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchEditUsageYear','批量修改设备使用年限','Batch edit usage year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','saveUsageYearError','使用年限值错误或小于等于0无法修改！','Usage year error or less than or equal zero!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usageYearDate','使用年限填入','Usage tear date',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usageYearDateReferTo','使用年限参考','Usage tear date refer to',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usageYearDateComments','系统信息提示：基于APM效期知识库数据，系统将依据设备医疗器械注册证号、设备型号、设备厂商三个核心维度为您提取关联信息。','System information tip: Based on the APM validity knowledge base data, the system will extract relevant information for you according to the three core dimensions of equipment medical device registration certificate number, equipment model and equipment manufacturer. ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usageYearDateCommentsTwo','需要说明的是，本功能提供的内容为知识库匹配结果，仅供参考使用，具体请结合实际业务场景进一步核实。','It should be noted that the content provided by this function is a knowledge base matching result, which is for reference only. Please further verify it in combination with the actual business scenarios.',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','batchModifyRule','批量操作只支持相同注册号设备，请重新勾选！','Batch operations only support devices with the same registration number, please check it again!',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','usageYear','年','Usage year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manufactureDateYear','出厂年份','Usage year',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','matchFactor','型号匹配程度','Match factor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','manufactureDateInput','出厂日期','Manufacture Date Input',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','medicalDeviceRegistrationCertificate','医疗器械注册证号','medical device registration certificate number',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','stockSource','来源','Source',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('stockSource','1','台账入库','ledger',NULL, -1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('stockSource','2','转科入库','convert',NULL ,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('stockSource','3','到货入库','arrival',NULL, -1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('stockSource','4','退货入库','returned',NULL, -1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','qualityControlCalendar','质控日历','Quality Control Calendar',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','qualityControlRadBoard','放射科质控大屏','Quality Control Board',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srNoAssetSteps','2','待派工','To be dispatched',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srNoAssetSteps','3','待接单','To be accepted',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srNoAssetSteps','4','待关单','To Close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srNoAssetSteps','5','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('srNoAssetSteps','9','已取消','Cancelled',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','inventoryStatus','盘点状态','Inventory Status',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','inventoryMethod','盘点方式','Inventory Method',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('inventoryMethod','rfid','RFID','RFID',NULL, -1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('inventoryMethod','scanCode','扫码盘点','Scan Code',NULL ,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','approveAttactments','审核','lock',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','attactmentsApproved','审核通过','unlock',NULL,-1);

--ZKB 培训V2 i18
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbTrainCourseType','normal','基础课程','normal',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('ZkbTrainCourseType','advance','进阶课程','advance',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','EquipmentHasUnfinishedDailyInspectionDelete','设备存在未完成的日常检查，需要删除吗？','Equipment HasUnfinished Daily Inspection Delete?',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','CurrentEquipmentHasUnfinishedWorkOrder','当前设备有未完成工单,请进行处理','Current Equipment HasUnfinished Work Order',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','UnfinishedMaintenanceWorkOrderNumber','未完成维修工单号','Unfinished Maintenance Work Order Number',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','ApplicationReason','申请理由','Application Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AppraisalReason','鉴定理由','Appraisal Reason',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','DisposalMethod','处置方式','Disposal Method',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','AutoProcessUnfinishedClinicalAssistantWorkOrder','自动处理未完成临床助手工单','Auto Process Unfinished Clinical Assistant Work Order',NULL,-1);

INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','RentReason','借用原因','Rent Reason',NULL,-1);

-- YHAAO-26026 维修消息提示
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','notFoundMaintenanceContract','未查询到维保合同信息','Not Found Maintenance Contract',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','warrantyDateExpired','维保截止日期已过期','Warranty Date Expired',NULL,-1);
INSERT INTO i18n_message (msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES ('message','confirmRepair','请确认是否继续维修','Confirm Repair',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','addContractor','增加外修人','Add Contractor ',NULL,-1);

-- YHAAO-26184 外来设备管理
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','externalAssetManage','外来设备管理','External Asset Manage',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','probationApplication','试用申请','Probation Application',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tempSave','暂存','Temp Save',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','tempEngineerDetection','临工科检测','Temp Engineer Detection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','detectionSituation','检测情况','Detection Situation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','detectionPeople','检测人','Detection People',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','detectionTime','检测时间','Detection Time',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','probationConfirmor','临床试用确认人','Probation Confirmor',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','probationConfirmCancel','确定取消此试用？','Probation Confirm Cancel',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','probationStatus','当前状态','Probation Status ',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','0','暂存中','Temp Save',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','1','待检测','Pending detection',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','2','待试用','Pending probation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','3','试用中','Probation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','4','待撤离','Pending evacuation',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','5','待关单','Pending close',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','6','已关单','Closed',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('probationStatus','7','已取消','canceled',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','company','公司','Company',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','certificate','合格证','Certificate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','specification','说明书','Specification',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('specification','electronic','电子版','Electronic',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('specification','paper','纸质版','Paper',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('label','threeInformation','三证资料','Three information',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('threeInformation','license','公司营业执照','Company business license',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('threeInformation','permit','经营许可证或备案','Distributing license or filing',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('threeInformation','registered','产品注册证','Product registration certificate',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('threeInformation','colorPage','彩页','Color Page',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('threeInformation','nameplate','铭牌照片','Nameplate photos',NULL,-1);
INSERT INTO i18n_message( msg_type, msg_key, value_zh, value_en, value_tw, site_id) VALUES ('field_name','approvalCompletedDate','审批完成日期','Approval Completed Date',NULL,-1);