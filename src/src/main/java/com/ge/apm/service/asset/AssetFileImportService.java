/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
@Scope("prototype")
public class AssetFileImportService {

    private static final String SHEET_ASSET = "Asset List";
    private static final String SHEET_ORG = "Org List";
//    private static final String SHEET_USER = "User List";

    public static enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    private Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    private String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
    private String orgUid = UserContextService.getCurrentUserAccount().getSiteUID();
    private List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    private List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    private List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);
    private List<I18nMessage> assetCategoryList = FieldValueMessageController.getFieldValueList("assetCategory", siteId);
    private List<I18nMessage> assetUsageList = FieldValueMessageController.getFieldValueList("assetUsage", siteId);
    private List<I18nMessage> capitalSourceList = FieldValueMessageController.getFieldValueList("capitalSource", siteId);
    private List<I18nMessage> measureGradeList = FieldValueMessageController.getFieldValueList("measureGrade", siteId);
    private List<I18nMessage> riskGradeList = FieldValueMessageController.getFieldValueList("riskGrade", siteId);
    private List<I18nMessage> emergencyEquipmentList = FieldValueMessageController.getFieldValueList("emergencyEquipment", siteId);
    private List<I18nMessage> importEquipmentList = FieldValueMessageController.getFieldValueList("importEquipment", siteId);
    private List<I18nMessage> usageStatusList = FieldValueMessageController.getFieldValueList("usageStatus", siteId);
    private List<I18nMessage> accountingCategoryList = FieldValueMessageController.getFieldValueList("accountingCategory", siteId);
    private List<I18nMessage> measuringItemCategoryList = FieldValueMessageController.getFieldValueList("measuringItemCategory", siteId);

    @Autowired
    private AssetInfoRepository assetDao;
    @Autowired
    private OrgInfoRepository orgDao;
    @Autowired
    private UserAccountRepository userDao;
    @Autowired
    private SupplierRepository supplierDao;
    @Autowired
    private QrCodeLibRepository qrcodeDao;
    @Autowired
    private AssetDepreciationService assetDepreciationService;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    public Map<String, Map<String, Object>> getOrgInfoMapFromTemplate(ExcelDocument doc) {

        Map<String, Map<String, Object>> result = new LinkedHashMap();
        int keyRow = 4;
        int dataRow = 5;
        OrgInfo currentOrg = orgDao.getByUid(orgUid);

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ORG, keyRow, ++dataRow)) {
            OrgInfo org = new OrgInfo();
            ExcelDocument.ValueWrapper valueWrapper = (ExcelDocument.ValueWrapper)rowMap.get("部门名称");
            String orgName = (String) valueWrapper.value;
            if (orgName == null || orgName.isEmpty()) {
                break;
            }
            org.setName(orgName);
            org.setHospitalId(currentOrg.getHospitalId());
            org.setParentOrg(currentOrg);
            org.setSiteId(currentOrg.getSiteId());

            org.setParentUID(orgUid);
            org.setHospitalUID(currentOrg.getHospitalUID());
            org.setTenantUID(currentOrg.getTenantUID());
            org.setInstitutionUID(currentOrg.getInstitutionUID());
            org.setSiteUID(currentOrg.getSiteUID());
            org.setOrgLevel(currentOrg.getOrgLevel());
            org.setOrgType(4);

            Map<String, Object> map = new LinkedHashMap();
            map.put("org", org);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);
            result.put(orgName, map);
        }
        return result;
    }

//    public Map<String, Map<String, Object>> getUserMapFromTemplate(ExcelDocument doc) {
//        Map<String, Map<String, Object>> result = new LinkedHashMap();
//        int keyRow = 4;
//        int dataRow = 5;
//
//        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_USER, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_USER, keyRow, ++dataRow)) {
//            String userName = ExcelDocument.getCellStringValue(rowMap.get("人员名字"));
//            String loginName = ExcelDocument.getCellStringValue(rowMap.get("用户名"));
//            if (userName == null || userName.isEmpty() || loginName == null || loginName.isEmpty()) {
//                break;
//            }
//
//            Map<String, Object> map = new LinkedHashMap();
//            map.put("rowData", rowMap);
//            result.put(userName, map);
//        }
//        return result;
//    }
    public Map<String, Map<String, Object>> getAssetMapFromTemplate(ExcelDocument doc) throws Exception {

        Map<String, Map<String, Object>> result = new LinkedHashMap();
        int keyRow = 6;
        int dataRow = 7;

        OrgInfo currentOrg = orgDao.getByUid(orgUid);

        for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(SHEET_ASSET, keyRow, ++dataRow)) {
            try{
            AssetInfo asset = new AssetInfo();
            String assetName = ExcelDocument.getCellStringValue(rowMap.get("设备名称"));
            if (assetName == null || assetName.isEmpty()) {
                break;
            }
            Map<String, Object> map = new LinkedHashMap();
            String departNum = ExcelDocument.getCellStringValue(rowMap.get("设备编号"));
            asset.setDepartNum(departNum);
            asset.setName(assetName);
            asset.setAliasName(ExcelDocument.getCellStringValue(rowMap.get("设备别名")));
            asset.setSiteId(currentOrg.getSiteId());
            asset.setHospitalId(currentOrg.getHospitalId());

            asset.setTenantUID(currentOrg.getTenantUID());
            asset.setHospitalUID(currentOrg.getHospitalUID());
            asset.setInstitutionUID(currentOrg.getInstitutionUID());
            asset.setSiteUID(currentOrg.getSiteUID());

            asset.setIsValid(true);
            asset.setStatus(1);
            asset.setDeviceStatus(0);
            asset.setIsDeleted(Boolean.FALSE);
            asset.setAssetOwnerName(ExcelDocument.getCellStringValue(rowMap.get("维修负责人")));
            asset.setAssetOwnerName2(ExcelDocument.getCellStringValue(rowMap.get("维修负责人2")));
            asset.setAssetOwnerName3(ExcelDocument.getCellStringValue(rowMap.get("维修负责人3")));

            asset.setClinicalDeptName(ExcelDocument.getCellStringValue(rowMap.get("使用科室")));
            asset.setManufacture(ExcelDocument.getCellStringValue(rowMap.get("制造商")));
            asset.setRegistrationNo(ExcelDocument.getCellStringValue(rowMap.get("注册证号")));
            asset.setVendor(ExcelDocument.getCellStringValue(rowMap.get("供应商")));
            String functionGroup = ExcelDocument.getCellStringValue(rowMap.get("医疗器械分类"));
            asset.setFunctionGroup(getI18nMsgKey(assetFunctionTypes, functionGroup,map,"医疗器械分类"));
            if (functionGroup != null && functionGroup.length() > 4) {
                String assetCode68 = ExcelDocument.getCellStringValue(rowMap.get("医疗器械子分类"));
                String str = functionGroup.substring(0, 3);
                List<I18nMessage> list = FieldValueMessageController.getFieldValueList(functionGroup.substring(0, 4), siteId);
                Integer msgKey = getI18nMsgKey(FieldValueMessageController.getFieldValueList(functionGroup.substring(0, 4), siteId), assetCode68,map,"医疗器械子分类");
                asset.setAssetCode68(msgKey == null ? null : msgKey.toString());
            }
            String assetGroupName = ExcelDocument.getCellStringValue(rowMap.get("设备分类"));
            asset.setAssetGroup(getI18nMsgKey(assetGroup, assetGroupName,map,"设备分类"));
            asset.setFunctionGrade(ExcelDocument.getCellIntegerValue(rowMap.get("功能等级")));
            asset.setFunctionType(ExcelDocument.getCellStringValue(rowMap.get("设备型号")));
            asset.setSerialNum(ExcelDocument.getCellStringValue(rowMap.get("制造商序列号")));
            asset.setSystemId(ExcelDocument.getCellStringValue(rowMap.get("SystemID/厂商报修ID")));
            asset.setBarcode(ExcelDocument.getCellStringValue(rowMap.get("条码")));
            String nationalStandardCode = ExcelDocument.getCellStringValue(rowMap.get("2010国家编码"));
            asset.setNationalStandardCode(nationalStandardCode);
            String financingNum = ExcelDocument.getCellStringValue(rowMap.get("资产编号"));
            asset.setFinancingNum(financingNum);
            asset.setModalityId(ExcelDocument.getCellStringValue(rowMap.get("IT系统编号")));
            asset.setPurchasePrice(ExcelDocument.getCellDoubleValue(rowMap.get("采购价格")));
            asset.setSalvageValue(ExcelDocument.getCellDoubleValue(rowMap.get("最终残值")));
            asset.setLifecycleInYear(ExcelDocument.getCellDoubleValue(rowMap.get("使用年限")));
            String depreciationMethodNmae = ExcelDocument.getCellStringValue(rowMap.get("折旧算法"));
            asset.setDepreciationMethod(getI18nMsgKey(depreciationMethod, depreciationMethodNmae,map,"折旧算法"));
            asset.setManufactDate(getDateField(rowMap.get("生产日期"), "生产日期"));
            asset.setPurchaseDate(getDateField(rowMap.get("采购日期"), "采购日期"));
            asset.setArriveDate(getDateField(rowMap.get("到货日期"), "到货日期"));
            asset.setInstallDate(getDateField(rowMap.get("启用日期"), "启用日期"));
            asset.setFactoryWarrantyDate(getDateField(rowMap.get("质保日期"), "质保日期"));
            asset.setWarrantyDate(getDateField(rowMap.get("维保截止日期"), "维保截止日期"));
            asset.setTerminateDate(getDateField(rowMap.get("报废日期"), "报废日期"));
            asset.setLastPmDate(getDateField(rowMap.get("上次维护日期"), "上次维护日期"));
            asset.setLastMeteringDate(getDateField(rowMap.get("上次计量日期"), "上次计量日期"));
            asset.setLastQaDate(getDateField(rowMap.get("上次质检日期"), "上次质检日期"));
            asset.setMaitanance(ExcelDocument.getCellStringValue(rowMap.get("维护商")));
            Double maitananceTel = ExcelDocument.getCellDoubleValue(rowMap.get("维护商电话"));
            asset.setMaitananceTel(maitananceTel == null ? "" : String.valueOf(maitananceTel.longValue()));
            String inputQrcode = ExcelDocument.getCellStringValue(rowMap.get("二维码"));
            if (inputQrcode != null) {
                asset.setQrCode(inputQrcode.replace("-", ""));
            }
            asset.setLocationCode(ExcelDocument.getCellStringValue(rowMap.get("安装位置编码")));
            asset.setLocationName(ExcelDocument.getCellStringValue(rowMap.get("安装位置")));
            asset.setClinicalOwnerName(ExcelDocument.getCellStringValue(rowMap.get("使用责任人")));
            asset.setComments(ExcelDocument.getCellStringValue(rowMap.get("备注")));
//            String assetType = ExcelDocument.getCellStringValue(rowMap.get("设备类型"));
//            asset.setAssetCategory(getI18nMsgKey(assetCategoryList, assetType,map,"设备类型"));
            String assetUsage = ExcelDocument.getCellStringValue(rowMap.get("用途分类"));
            asset.setAssetUsage(getI18nMsgKey(assetUsageList, assetUsage,map,"用途分类"));
            String capitalSource = ExcelDocument.getCellStringValue(rowMap.get("资金来源"));
            asset.setCapitalSource(getI18nMsgKey(capitalSourceList, capitalSource,map,"资金来源"));
            String riskGrade = ExcelDocument.getCellStringValue(rowMap.get("风险等级"));
            asset.setRiskGrade(getI18nMsgKey(riskGradeList, riskGrade,map,"风险等级"));
            String measureGrade = ExcelDocument.getCellStringValue(rowMap.get("计量等级"));
            asset.setMeasureGrade(getI18nMsgKey(measureGradeList, measureGrade,map,"计量等级"));
            String measuringItemCategory = ExcelDocument.getCellStringValue(rowMap.get("计量项目类别"));
            asset.setMeasuringItemCategory(getI18nMsgKey(measuringItemCategoryList, measuringItemCategory,map,"计量项目类别"));
            String emergencyEquipment = ExcelDocument.getCellStringValue(rowMap.get("是否急救设备"));
            asset.setEmergencyEquipment(getI18nMsgKey(emergencyEquipmentList, emergencyEquipment,map,"是否急救设备"));
            String importEquipment = ExcelDocument.getCellStringValue(rowMap.get("是否进口设备"));
            asset.setImportEquipment(getI18nMsgKey(importEquipmentList, importEquipment,map,"是否进口设备"));
            String usageStatus = ExcelDocument.getCellStringValue(rowMap.get("使用状态"));
            asset.setUsageStatus(getI18nMsgKey(usageStatusList, usageStatus,map,"使用状态"));
            String accountingCategory = ExcelDocument.getCellStringValue(rowMap.get("核算类别"));
            asset.setAccountingCategory(getI18nMsgKey(accountingCategoryList, accountingCategory,map,"核算类别"));

            map.put("asset", asset);
            map.put("status", ImportStatus.New);
            map.put("rowData", rowMap);

            if (departNum == null) {
                departNum = "";
            }
            result.put(departNum.concat(assetName).concat(financingNum == null ? "" : financingNum), map);
            }catch(Exception e){
                throw new Exception("第"+dataRow+"行数据错误" + e.getMessage());
            }
        }

        return result;
    }

    private Date getDateField(Object object, String filedSrcName) throws Exception {


        ExcelDocument.ValueWrapper valueWrapper = null;

        if (object instanceof ExcelDocument.ValueWrapper) {
            valueWrapper = (ExcelDocument.ValueWrapper) object;
            object = valueWrapper.value;
        }

        if (object == null) {
            return null;
        }

        if (object instanceof Date) {
            return (Date) object;
        }
        if (object instanceof String) {
            object = ((String) object).trim();
            Date res = TimeUtils.getDateFromStr((String) object, "MM/dd/yyyy");
            if (res != null && res instanceof Date) {
                return res;
            }
            res = TimeUtils.getDateFromStr((String) object, "MM/dd/yy");
            if (res != null && res instanceof Date) {
                return res;
            }
            res = TimeUtils.getDateFromStr((String) object, "dd/MM/yy");
            if (res != null && res instanceof Date) {
                return res;
            }
            res = TimeUtils.getDateFromStr((String) object, "dd/MM/yyyy");
            if (res != null && res instanceof Date) {
                return res;
            }
            res = TimeUtils.getDateFromStr((String) object, "yyyy-MM-dd");
            if (res != null && res instanceof Date) {
                return res;
            }
            throw new Exception(filedSrcName + "[" + (String) object + "]无效的日期格式");
        } else {
            throw new Exception(filedSrcName + "无效的日期格式");
        }
    }

    private Integer getI18nMsgKey(List<I18nMessage> msgList, String value, Map map, String fieldName) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        I18nMessage msg = msgList.stream().filter(t -> t.getValueZh().equals(value)).findAny().orElse(null);
        if (msg == null) {
            addErrMessage(map, "警告:无效值" + fieldName + "[" + value + "]");
            return null;
        } else {
            return Integer.parseInt(msg.getMsgKey());
        }
    }

    private void addErrMessage(Map<String, Object> item, String comments) {
        if (item.containsKey("errmsg")) {
            ((List<String>) item.get("errmsg")).add(comments);
        } else {
            List<String> c = new ArrayList();
            c.add(comments);
            item.put("errmsg", c);
        }
    }

    public void importData(Map<String, Map<String, Object>> importOrgMap, Map<String, Map<String, Object>> importAssetMap,
                           Boolean autoGenerateQrcode, Boolean autoGenerateFinancingNum) {
        OrgInfo currentOrg = orgDao.getByUid(orgUid);
        importOrgMap.values().forEach(item -> {
            List<OrgInfo> orgs = getOrgInfoByName((OrgInfo) item.get("org"));
            if (null == orgs || orgs.isEmpty()) {
                orgDao.save((OrgInfo) item.get("org"));
                item.put("status", ImportStatus.Created);
            } else if (orgs.size() > 1) {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, "错误:" + WebUtil.getMessage("存在重名的") + WebUtil.getMessage("OrgInfo") + ":" + ((OrgInfo) item.get("org")).getName());
            } else {
                item.put("org", orgs.get(0));
                item.put("status", ImportStatus.Exist);
            }
        });

        importAssetMap.values().forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            String orgName = newAsset.getClinicalDeptName();
            if (importOrgMap.containsKey(orgName)) {
                OrgInfo clinicalDept = (OrgInfo) importOrgMap.get(orgName).get("org");
                if (clinicalDept.getId() == null || clinicalDept.getId() <= 0) {
                    item.put("status", ImportStatus.Failure);
                    addErrMessage(item, "警告:" + WebUtil.getMessage("OrgInfo") + orgName + "无法关联");
                    newAsset.setClinicalDeptName(null);
                } else {
                    newAsset.setClinicalDeptName(orgName);
                    newAsset.setClinicalDeptId(clinicalDept.getId());
                    newAsset.setClinicalDeptUID(clinicalDept.getUid());
                }
            } else {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, "警告:" + WebUtil.getMessage("OrgInfo") + orgName + "不在列表页中");
                newAsset.setClinicalDeptName(null);
            }

            //for AssetOwner
            List<UserAccount> userList = getUserInfosByName(newAsset.getAssetOwnerName());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId(assetOwner.getId());
                newAsset.setAssetOwnerTel(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName() == null) {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + "未设置");
                } else {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName(null);
            }
            //for AssetOwner2
            userList = getUserInfosByName(newAsset.getAssetOwnerName2());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId2(assetOwner.getId());
                newAsset.setAssetOwnerTel2(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName2() == null) {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + "未设置");
                } else {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName2(null);
            }

            //for AssetOwner3
            userList = getUserInfosByName(newAsset.getAssetOwnerName3());
            if (userList != null && userList.size() == 1) {
                UserAccount assetOwner = userList.get(0);
                newAsset.setAssetOwnerId3(assetOwner.getId());
                newAsset.setAssetOwnerTel3(assetOwner.getTelephone());
            } else {
                if (newAsset.getAssetOwnerName3() == null) {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + "未设置");
                } else {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                }
                newAsset.setAssetOwnerName3(null);
            }

            //for ClinicalOwner
            userList = getUserInfosByName(newAsset.getClinicalOwnerName());
            if (userList != null && userList.size() == 1) {
                UserAccount clinicalOwner = userList.get(0);
                newAsset.setClinicalOwnerId(clinicalOwner.getId());
                newAsset.setClinicalOwnerName(clinicalOwner.getTelephone());
            } else {
                if (newAsset.getClinicalOwnerName() == null) {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + "未设置");
                } else {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                }
                newAsset.setClinicalOwnerName(null);
            }

            if (newAsset.getVendor() != null && !newAsset.getVendor().isEmpty()) {
                List<Supplier> supplierList = getSupplierListByName(newAsset.getVendor());
                if (null == supplierList || supplierList.isEmpty()) {
                    Supplier newSupplier = new Supplier();
                    newSupplier.setSiteId(currentOrg.getSiteId());
                    newSupplier.setName(newAsset.getVendor());
                    newSupplier.setTenantUID(currentOrg.getTenantUID());
                    newSupplier.setHospitalUID(currentOrg.getHospitalUID());
                    newSupplier.setInstitutionUID(currentOrg.getInstitutionUID());
                    newSupplier.setSiteUID(currentOrg.getSiteUID());
                    supplierDao.save(newSupplier);
                    newAsset.setSupplierId(newSupplier.getId());
                } else if (supplierList.size() == 1) {
                    Supplier supplier = supplierList.get(0);
                    newAsset.setSupplierId(supplier.getId());
                } else {
                    addErrMessage(item, "警告:" + WebUtil.getMessage("Vendor") + (String) item.get("superlierName") + "无法关联");
                    newAsset.setVendor(null);
                }
            }

            //auto generate financing num
            String financingNum = newAsset.getFinancingNum();
            String nationalStandardCode = newAsset.getNationalStandardCode();

            if (StringUtils.isEmpty(financingNum) && !StringUtils.isEmpty(nationalStandardCode) && autoGenerateFinancingNum) {
                financingNum = assetFinacingNumGenerateService.generateFinancingNum(
                        null,
                        nationalStandardCode,
                        newAsset.getSiteUID()
                );
                newAsset.setFinancingNum(financingNum);
            }

            List<AssetInfo> assetList = getAssetInfos(newAsset);
            if (null == assetList || assetList.isEmpty()) {
                String qrCode = newAsset.getQrCode();

                if ((qrCode == null || qrCode.isEmpty()) && autoGenerateQrcode) {
                    QrCodeLib newCode = qrCodeService.createOneQrCode(currentOrg, 3);
                    newAsset.setQrCode(newCode.getQrCode());
                    createAsset(newAsset);
                    item.put("status", ImportStatus.Created);
                } else if (isAvailableQrcode(qrCode, item)) {
                    createAsset(newAsset);
                    item.put("status", ImportStatus.Created);
                } else {
                    item.put("status", ImportStatus.Failure);
//                    addErrMessage(item, WebUtil.getMessage("InvalidParameter") +WebUtil.getMessage("qrCode")+ qrCode);
                }

            } else if (assetList.size() > 1) {
                item.put("status", ImportStatus.Failure);
                addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("AssetInfo") + newAsset.getName() + "/" + newAsset.getDepartNum() + "/" + newAsset.getFinancingNum());
            } else {
                item.put("asset", assetList.get(0));
                item.put("status", ImportStatus.Exist);
            }

        });

    }

    private Boolean isAvailableQrcode(String qrcode, Map<String, Object> item) {
        if (null == qrcode || qrcode.isEmpty()) {
            return true;
        }

        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(qrcode);
        if (qrCodeLib == null) {
            addErrMessage(item, "错误:" + WebUtil.getMessage("InvalidQRCode") + qrcode);
            return false;
        }
        if (!qrCodeLib.getSiteUID().equals(orgUid)) {
            addErrMessage(item, "错误:" + WebUtil.getMessage("WrongHospitalQRCode"));
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            addErrMessage(item, "错误:" + WebUtil.getMessage("AlreadyUsingQrCode"));
            return false;
        }

        return true;
    }

    public Boolean exportFailData(ExcelDocument doc, Map<String, Map<String, Object>> importOrgMap, Map<String, Map<String, Object>> importAssetMap) {
        Integer keyRowOrg = 4;
        Integer dataRowOrg = 5;
        Boolean hasFailData = false;
        doc.clearRowsData(SHEET_ORG, 5, 4 + importOrgMap.values().size());
        for (Map<String, Object> item : importOrgMap.values()) {
            doc.writeRowData(SHEET_ORG, keyRowOrg, dataRowOrg++, (Map<String, Object>) item.get("rowData"));
            hasFailData = item.get("status").equals(ImportStatus.Failure);
        }

//        Integer keyRowUser = 4;
//        Integer dataRowUser = 5;
//        doc.clearRowsData(SHEET_USER, 5, 4 + importUserMap.values().size());
//        for (Map<String, Object> item : importUserMap.values()) {
//            doc.writeRowData(SHEET_USER, keyRowUser, dataRowUser++, (Map<String, Object>) item.get("rowData"));
//        }
        Integer keyRowAsset = 6;
        Integer dataRowAsset = 7;
        doc.clearRowsData(SHEET_ASSET, 7, 6 + importAssetMap.values().size());
        for (Map<String, Object> item : importAssetMap.values()) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_ASSET, keyRowAsset, dataRowAsset++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }
//        doc.clearRowsData(SHEET_ASSET, dataRowAsset, 6+importAssetMap.values().size());

//        doc.clearRowsData(SHEET_USER, 5, 6);
        return hasFailData;
    }

    @Transactional
    void createAsset(AssetInfo asset) {
        assetDao.save(asset);
        if (null != asset.getQrCode() && !asset.getQrCode().isEmpty()) {
            QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(asset.getQrCode());
            if (qrCodeLib != null) {
                qrCodeLib.setStatus(3);
                qrcodeDao.save(qrCodeLib);
            }
        }
        assetDepreciationService.saveAssetDerpeciation(asset);
    }

    private List<OrgInfo> getOrgInfoByName(OrgInfo org) {
        List<SearchFilter> OrgInfoFilters = new ArrayList<>();
        OrgInfoFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));
        OrgInfoFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        OrgInfoFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, org.getName()));
        return orgDao.findBySearchFilter(OrgInfoFilters);
    }

    private List<AssetInfo> getAssetInfos(AssetInfo asset) {
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        assetFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, asset.getName()));
        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        if (null != asset.getFinancingNum() && !asset.getFinancingNum().isEmpty()) {
            assetFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.EQ, asset.getFinancingNum()));
        }
        List<AssetInfo> tempList = assetDao.findBySearchFilter(assetFilters);
        return tempList;
    }

    private List<UserAccount> getUserInfosByName(String userName) {
        List<SearchFilter> userFilters = new ArrayList<>();
//        userFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));
        userFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        userFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, userName));
        return userDao.findBySearchFilter(userFilters);
    }

    private List<Supplier> getSupplierListByName(String supplierName) {
        List<SearchFilter> supplierFilters = new ArrayList<>();
        supplierFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
        supplierFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, supplierName));
        return supplierDao.findBySearchFilter(supplierFilters);
    }

    public Boolean checkData(Map<String, Map<String, Object>> importOrgMap, Map<String, Map<String, Object>> importAssetMap) {

        HashSet<Boolean> result = new HashSet<>();
        importOrgMap.values().forEach(item -> {
            List<OrgInfo> orgs = getOrgInfoByName((OrgInfo) item.get("org"));
            if (null == orgs || orgs.isEmpty()) {
                item.put("status", ImportStatus.New);
            } else if (orgs.size() > 1) {
                item.put("status", ImportStatus.CheckFail);
                addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("OrgIngo") + ((OrgInfo) item.get("org")).getName());
                result.add(Boolean.FALSE);
            } else {
                item.put("org", orgs.get(0));
                item.put("status", ImportStatus.Exist);
            }
        });

        importAssetMap.values().forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            List<AssetInfo> assetList = getAssetInfos(newAsset);
            if (null == assetList || assetList.isEmpty()) {
                String qrCode = newAsset.getQrCode();
                if (isAvailableQrcode(qrCode, item)) {
                    item.put("status", ImportStatus.New);
                } else {
                    item.put("status", ImportStatus.CheckFail);
                    result.add(Boolean.FALSE);
//                    addErrMessage(item, WebUtil.getMessage("InvalidParameter") +WebUtil.getMessage("qrCode")+ qrCode);
                }

            } else if (assetList.size() > 1) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                addErrMessage(item, "错误:" + WebUtil.getMessage("存在重复的") + WebUtil.getMessage("AssetInfo") + newAsset.getName() + "/" + newAsset.getDepartNum() + "/" + newAsset.getFinancingNum());
            } else {
                item.put("asset", assetList.get(0));
                item.put("status", ImportStatus.Exist);
            }

            if (null == newAsset.getAssetGroup()) {
                item.put("status", ImportStatus.CheckFail);
                result.add(Boolean.FALSE);
                addErrMessage(item, "错误:" + WebUtil.getMessage("assetGroup") + WebUtil.getMessage("ValidationRequire"));
            }

        });
        return result.isEmpty();
    }

    public boolean checkDocVersion(ExcelDocument doc) {
        String inputVersion = ExcelDocument.getCellStringValue(doc.getCellValue(SHEET_ASSET, 2, 3));
        return "v1.7".equalsIgnoreCase(inputVersion);
    }
}
