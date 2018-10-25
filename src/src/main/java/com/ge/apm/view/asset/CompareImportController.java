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
import webapp.framework.util.TimeUtil;

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
            case "installDate":
                date = getDateValue((String) selectedItem.get("使用日期"));
                if (null != date) {
                    selectedAsset.setInstallDate(date);
                    ArrivalListItem arrivalItem = (ArrivalListItem) selectedItem.get("arrivalListItem");
                    if(null != arrivalItem && null!= arrivalItem.getWarrantyYear()){
                        selectedAsset.setFactoryWarrantyDate(TimeUtil.toJodaDate(selectedAsset.getInstallDate()).plusMonths(arrivalItem.getWarrantyYear().intValue()*12).toDate());
                    }
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
