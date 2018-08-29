/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.CompareImportService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class CompareImportController {

    private UploadedFile file;

    private Map<String, Map<String, Object>> importAssetMap;

    private CompareImportService ciService;

    private Map<String, Object> selectedItem;

    private UaaService uaaService;

    private AssetInfo selectedAsset;

    private UserAccountRepository userDao;

    private QrCodeService qrCodeService;

    private AssetInfoRepository assetDao;

    private ConfigService configService;

    private byte[] fileContents;

    @PostConstruct
    protected void init() {
        ciService = WebUtil.getBean(CompareImportService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
    }

    public void uploadDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        fileContents = file.getContents();
    }

    public void cancelUpload() {
        this.file = null;
        selectedItem = null;
        importAssetMap = null;
        selectedAsset = null;
    }

    public void finishUpload() {
        ExcelDocument doc = null;
        try {
            InputStream in = new ByteArrayInputStream(fileContents);
            doc = new ExcelDocument(in, ExcelDocument.parseTypeByFileName(file.getFileName()));
            importAssetMap = ciService.getAssetMapFromTemplate(doc);
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            Logger.getLogger(CompareImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" + e.getMessage());
            Logger.getLogger(CompareImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    public void onSelectedChange() {
        selectedAsset = (AssetInfo) selectedItem.get("asset");
    }

    public void updateField(String fieldName) {
        Date date = null;
        Integer index = null;
        Boolean canUpdate = true;
        switch (fieldName) {
            case "All":
            case "name":
                selectedAsset.setName((String) selectedItem.get("设备名称"));
                if (!"All".equals(fieldName)) {
                    break;
                }
            case "serialNum":
                selectedAsset.setSerialNum((String) selectedItem.get("出厂编号"));
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "aliasName":
//                selectedAsset.setAliasName((String) selectedItem.get("设备别名"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
            case "functionType":
                selectedAsset.setFunctionType((String) selectedItem.get("规格型号"));
                if (!"All".equals(fieldName)) {
                    break;
                }
            case "departNum":
                selectedAsset.setDepartNum((String) selectedItem.get("设备编号"));
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "assetGroup":
//                index = getDropDownValue("assetGroup", (String) selectedItem.get("设备分类"));
//                if (null != index) {
//                    selectedAsset.setAssetGroup(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的设备分类:" + (String) selectedItem.get("设备分类"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
            case "clinicalDeptName":
                List<OrgInfo> orgList = uaaService.getClinicalDeptListBySiteUID(selectedAsset.getSiteUID());
                OrgInfo org = orgList.stream().filter(t -> t.getName().equals((String) selectedItem.get("使用单位"))).findAny().orElse(null);
                if (null == org) {
                    WebUtil.addErrorMessage("无效的科室名称:" + (String) selectedItem.get("使用单位"));
                    canUpdate = false;
                } else {
                    selectedAsset.setClinicalDeptId(org.getId());
                    selectedAsset.setClinicalDeptName(org.getName());
                    selectedAsset.setClinicalDeptUID(org.getUid());
                }
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "nationalStandardCode":
//                selectedAsset.setNationalStandardCode((String) selectedItem.get("2010国家编码"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
            case "vendor":
                List<SearchFilter> supplierFilters = new ArrayList<>();
                supplierFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedAsset.getTenantUID()));
                supplierFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, (String) selectedItem.get("供货单位名称")));
                SupplierRepository supplierDao = WebUtil.getBean(SupplierRepository.class);
                List<Supplier> supplierList = supplierDao.findBySearchFilter(supplierFilters);
                if (null == supplierList || supplierList.isEmpty() || supplierList.size() > 1) {
                    WebUtil.addErrorMessage("无效的供应商名称:" + (String) selectedItem.get("供货单位名称"));
                    canUpdate = false;
                } else {
                    Supplier supplier = supplierList.get(0);
                    selectedAsset.setVendor(supplier.getName());
                    selectedAsset.setSupplierId(supplier.getId());
                }
                if (!"All".equals(fieldName)) {
                    break;
                }
            case "manufacture":
                selectedAsset.setManufacture((String) selectedItem.get("生产厂家"));
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "warrantyDate":
//                date = getDateValue((String) selectedItem.get("维保截止日期"));
//                if (null != date) {
//                    selectedAsset.setWarrantyDate(date);
//                } else {
//                    WebUtil.addErrorMessage("无效的维保截止日期:" + (String) selectedItem.get("维保截止日期"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "assetOwner":
//                UserAccount owner = getUserByName((String) selectedItem.get("维修负责人"));
//                if (null != owner) {
//                    selectedAsset.setAssetOwnerId(owner.getId());
//                    selectedAsset.setAssetOwnerName(owner.getName());
//                    selectedAsset.setAssetOwnerTel(owner.getTelephone());
//                } else {
//                    WebUtil.addErrorMessage("未找到用户:" + (String) selectedItem.get("维修负责人"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "assetOwner2":
//                UserAccount owner2 = getUserByName((String) selectedItem.get("维修负责人2"));
//                if (null != owner2) {
//                    selectedAsset.setAssetOwnerId2(owner2.getId());
//                    selectedAsset.setAssetOwnerName2(owner2.getName());
//                    selectedAsset.setAssetOwnerTel2(owner2.getTelephone());
//                } else {
//                    WebUtil.addErrorMessage("未找到用户:" + (String) selectedItem.get("维修负责人2"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "assetOwner3":
//                UserAccount owner3 = getUserByName((String) selectedItem.get("维修负责人3"));
//                if (null != owner3) {
//                    selectedAsset.setAssetOwnerId3(owner3.getId());
//                    selectedAsset.setAssetOwnerName3(owner3.getName());
//                    selectedAsset.setAssetOwnerTel3(owner3.getTelephone());
//                } else {
//                    WebUtil.addErrorMessage("未找到用户:" + (String) selectedItem.get("维修负责人3"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "clinicalOwner":
//                UserAccount clinicalOwner = getUserByName((String) selectedItem.get("使用责任人"));
//                if (null != clinicalOwner) {
//                    selectedAsset.setClinicalOwnerId(clinicalOwner.getId());
//                    selectedAsset.setClinicalOwnerName(clinicalOwner.getName());
//                    selectedAsset.setClinicalOwnerTel(clinicalOwner.getTelephone());
//                } else {
//                    WebUtil.addErrorMessage("未找到用户:" + (String) selectedItem.get("使用责任人"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "assetUsage":
//                index = getDropDownValue("assetUsage", (String) selectedItem.get("用途分类"));
//                if (null != index) {
//                    selectedAsset.setAssetUsage(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的用途分类:" + (String) selectedItem.get("用途分类"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "capitalSource":
//                index = getDropDownValue("capitalSource", (String) selectedItem.get("资金来源"));
//                if (null != index) {
//                    selectedAsset.setCapitalSource(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的资金来源:" + (String) selectedItem.get("资金来源"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "measureGrade":
//                index = getDropDownValue("measureGrade", (String) selectedItem.get("计量等级"));
//                if (null != index) {
//                    selectedAsset.setMeasureGrade(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的计量等级:" + (String) selectedItem.get("计量等级"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "riskGrade":
//                index = getDropDownValue("riskGrade", (String) selectedItem.get("风险等级"));
//                if (null != index) {
//                    selectedAsset.setRiskGrade(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的风险等级:" + (String) selectedItem.get("风险等级"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "emergencyEquipment":
//                index = getDropDownValue("emergencyEquipment", (String) selectedItem.get("是否急救设备"));
//                if (null != index) {
//                    selectedAsset.setEmergencyEquipment(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的是否急救设备值:" + (String) selectedItem.get("是否急救设备"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "importEquipment":
//                index = getDropDownValue("importEquipment", (String) selectedItem.get("是否进口设备"));
//                if (null != index) {
//                    selectedAsset.setImportEquipment(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的是否进口设备值:" + (String) selectedItem.get("是否进口设备"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "measuringItemCategory":
//                index = getDropDownValue("measuringItemCategory", (String) selectedItem.get("计量项目类别"));
//                if (null != index) {
//                    selectedAsset.setMeasuringItemCategory(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的计量项目类别值:" + (String) selectedItem.get("计量项目类别"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "accountingCategory":
//                index = getDropDownValue("accountingCategory", (String) selectedItem.get("核算类别"));
//                if (null != index) {
//                    selectedAsset.setAccountingCategory(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的核算类别值:" + (String) selectedItem.get("核算类别"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "systemId":
//                selectedAsset.setSystemId((String) selectedItem.get("SystemID/厂商报修ID"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "assetCode68":
//            case "functionGroup":
//                Integer functionGroup = getDropDownValue("functionGroup", (String) selectedItem.get("医疗器械分类"));
//                Integer assetCode68 = null;
//                if (null != functionGroup) {
//                    assetCode68 = getDropDownValue(String.valueOf(functionGroup), (String) selectedItem.get("医疗器械子分类"));
//                } else {
//                    WebUtil.addErrorMessage("无效的医疗器械分类值:" + (String) selectedItem.get("医疗器械分类"));
//                    canUpdate = false;
//                }
//                selectedAsset.setFunctionGroup(functionGroup);
//                selectedAsset.setAssetCode68(assetCode68);
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "functionGrade":
//                index = getDropDownValue("functionGrade", (String) selectedItem.get("功能等级"));
//                if (null != index) {
//                    selectedAsset.setFunctionGrade(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的功能等级值:" + (String) selectedItem.get("功能等级"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "maitanance":
//                selectedAsset.setMaitanance((String) selectedItem.get("维修商"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "maitananceTel":
//                selectedAsset.setMaitananceTel((String) selectedItem.get("维修电话"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "financingNum":
//                selectedAsset.setFinancingNum((String) selectedItem.get("资产编号"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "barcode":
//                selectedAsset.setBarcode((String) selectedItem.get("条码"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "modalityId":
//                selectedAsset.setModalityId((String) selectedItem.get("院内IT系统编号"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "locationCode":
//                selectedAsset.setLocationCode((String) selectedItem.get("安装位置编码"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "locationName":
//                selectedAsset.setLocationName((String) selectedItem.get("安装位置"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "registrationNo":
//                selectedAsset.setRegistrationNo((String) selectedItem.get("注册证号"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "manufactDate":
//                date = getDateValue((String) selectedItem.get("出厂日期"));
//                if (null != date) {
//                    selectedAsset.setManufactDate(date);
//                } else {
//                    WebUtil.addErrorMessage("无效的出厂日期:" + (String) selectedItem.get("出厂日期"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "purchaseDate":
//                date = getDateValue((String) selectedItem.get("采购日期"));
//                if (null != date) {
//                    selectedAsset.setPurchaseDate(date);
//                } else {
//                    WebUtil.addErrorMessage("无效的采购日期:" + (String) selectedItem.get("采购日期"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "arriveDate":
//                date = getDateValue((String) selectedItem.get("到货日期"));
//                if (null != date) {
//                    selectedAsset.setArriveDate(date);
//                } else {
//                    WebUtil.addErrorMessage("无效的日期:" + (String) selectedItem.get("到货日期"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
            case "installDate":
                date = getDateValue((String) selectedItem.get("使用日期"));
                if (null != date) {
                    selectedAsset.setInstallDate(date);
                } else {
                    WebUtil.addErrorMessage("无效的启用日期:" + (String) selectedItem.get("使用日期"));
                    canUpdate = false;
                }
                if (!"All".equals(fieldName)) {
                    break;
                }
            case "terminateDate":
                date = getDateValue((String) selectedItem.get("报损日期"));
                if (null != date) {
                    selectedAsset.setTerminateDate(date);
                } else {
                    WebUtil.addErrorMessage("无效的报废日期:" + (String) selectedItem.get("报损日期"));
                    canUpdate = false;
                }
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "factoryWarrantyDate":
//                date = getDateValue((String) selectedItem.get("质保日期"));
//                if (null != date) {
//                    selectedAsset.setFactoryWarrantyDate(date);
//                } else {
//                    WebUtil.addErrorMessage("无效的质保日期:" + (String) selectedItem.get("质保日期"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
            case "purchasePrice":
                try {
                    Double price = Double.valueOf(((String) selectedItem.get("单价")));
                    if (null != price) {
                        selectedAsset.setPurchasePrice(price);
                    }
                } catch (NumberFormatException e) {
                    WebUtil.addErrorMessage("无效的采购价格数据:" + (String) selectedItem.get("单价"));
                    canUpdate = false;
                }
                if (!"All".equals(fieldName)) {
                    break;
                }
//            case "salvageValue":
//                try {
//                    Double salvageValue = Double.valueOf(((String) selectedItem.get("最终残值(元)")));
//                    if (null != salvageValue) {
//                        selectedAsset.setSalvageValue(salvageValue);
//                    }
//                } catch (NumberFormatException e) {
//                    WebUtil.addErrorMessage("无效的最终残值数据:" + (String) selectedItem.get("最终残值(元)"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "lifecycle":
//                try {
//                    Double lifecycle = Double.valueOf(((String) selectedItem.get("使用年限")));
//                    if (null != lifecycle) {
//                        selectedAsset.setPurchasePrice(lifecycle);
//                    }
//                } catch (NumberFormatException e) {
//                    WebUtil.addErrorMessage("无效的使用年限:" + (String) selectedItem.get("使用年限"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "depreciationMethod":
//                index = getDropDownValue("depreciationMethodList", (String) selectedItem.get("折旧算法"));
//                if (null != index) {
//                    selectedAsset.setDepreciationMethod(index);
//                } else {
//                    WebUtil.addErrorMessage("无效的折旧算法:" + (String) selectedItem.get("折旧算法"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "qrCode":
//                if (isAvailableQrcode((String) selectedItem.get("二维码"))) {
//                    selectedAsset.setQrCode((String) selectedItem.get("二维码"));
//                } else {
//                    WebUtil.addErrorMessage("无效的二维码:" + (String) selectedItem.get("折旧算法"));
//                    canUpdate = false;
//                }
//                if (!"All".equals(fieldName)) {
//                    break;
//                }
//            case "comments":
//                selectedAsset.setComments((String) selectedItem.get("备注"));
//                if (!"All".equals(fieldName)) {
//                    break;
//                }

        }

        if (canUpdate) {
            if (assetDao.save(selectedAsset) == null) {
                WebUtil.addErrorMessage("更新失败");
            } else {
                WebUtil.addSuccessMessage("更新成功");
            }
        } else {
            WebUtil.addErrorMessage("不能更新");
            selectedAsset = assetDao.findByUid(selectedAsset.getUid());
        }
    }

    @Transactional
    public void instorage(ArrivalListItem item) {
        if (ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getInstallStatus())) {
            item.setInstallStatus(ArrivalListItem.INSTALL_STATUS_INSTORAGE);
            if (configService.isNeedAssetApprover(item.getArrivalList().getSiteUID())) {
                //待审核
                AssetInfo instorageAsset = assetDao.findByUid(item.getAssetUID());
                instorageAsset.setUsageStatus(3);
                assetDao.save(instorageAsset);
            }
            ciService.updateArrivalListItem(item);

            WebUtil.addSuccessMessage("入库成功");
        } else {
            WebUtil.addErrorMessage("不能更新");
        }
    }

    private Boolean isAvailableQrcode(String qrcode) {
        String qrCodeStatus = qrCodeService.isAvailableQrcode(qrcode, selectedAsset.getSiteUID());
        if ("Normal".equals(qrCodeStatus)) {
            return true;
        } else {
            WebUtil.addErrorMessage("错误:" + WebUtil.getMessage(qrCodeStatus));
            return false;
        }
    }

    private UserAccount getUserByName(String name) {
        List<SearchFilter> userFilters = new ArrayList<>();
        userFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedAsset.getTenantUID()));
        userFilters.add(new SearchFilter("name", SearchFilter.Operator.EQ, name));
        List<UserAccount> userList = userDao.findBySearchFilter(userFilters);
        if (null != userList && userList.size() == 1) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    public UserAccount getUserByUid(String userUid) {
        if (Strings.isNullOrEmpty(userUid)) {
            return new UserAccount();
        }
        return userDao.getByUid(userUid);
    }

    public Boolean compareDouble(String keyOfMap, Double value) {
        if (Strings.isNullOrEmpty((String) selectedItem.get(keyOfMap))) {
            return value == null || value == 0.0;
        }
        if (null == value) {
            return false;
        }
        try {
            Double doubleValue = Double.valueOf(((String) selectedItem.get(keyOfMap)));
            return doubleValue.compareTo(value) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Date getDateValue(String fieldValue) {
        return TimeUtils.toDateFromString(fieldValue, "yyyy/MM/dd HH:mm", "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy/MM/dd", "yyyy-MM-dd", "MM/dd/yy", "dd/MM/yy");
    }

    private Integer getDropDownValue(String fieldKey, String fieldValue) {
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
        List<I18nMessage> dropDownList = FieldValueMessageController.getFieldValueList(fieldKey, siteId);

        if (Strings.isNullOrEmpty(fieldValue)) {
            return null;
        }
        I18nMessage msg = dropDownList.stream().filter(t -> t.getValueZh().equals(fieldValue)).findAny().orElse(null);
        if (msg == null) {
            WebUtil.addErrorMessage("无效的值:" + fieldValue);
            return null;
        } else {
            return Integer.parseInt(msg.getMsgKey());
        }
    }

    //Getter and Setter
    public Map<String, Map<String, Object>> getImportAssetMap() {
        return importAssetMap;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Map<String, Object> getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Map<String, Object> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

}
