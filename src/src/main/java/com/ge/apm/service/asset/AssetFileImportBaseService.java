/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.dao.mapper.UserAccountMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetImport;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.MvsUtil;
import com.ge.apm.service.zkb.ZkbFileDirectoryService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.service.DbResourcesUtil;

import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 212579464
 */
public class AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportBaseService.class);

    public static final String EXCEL_ASSET_TEMPLATE_VERSION = "v2.4";

    public static final String SHEET_ASSET = "Asset List";
    public static final String SHEET_ORG = "Org List";
    public static final String SHEET_TCM = "TCM List";
    public static final int EXCEL_ASSET_HEADER_ROW = 1;
    public static final int EXCEL_ASSET_DATA_ROW = 2;

    public static final int EXCEL_ORG_HEADER_ROW = 1;
    public static final int EXCEL_ORG_DATA_ROW = 2;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }
    protected FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);

    protected AssetCreateService assetCreateService = WebUtil.getBean(AssetCreateService.class);
    protected Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
    protected String tenantUID = UserContextService.getCurrentUserAccount().getTenantUID();
    public List<I18nMessage> assetFunctionTypes = FieldValueMessageController.getFieldValueList("assetFunctionType", siteId);
    public List<I18nMessage> assetGroup = FieldValueMessageController.getFieldValueList("assetGroup", siteId);
    public List<I18nMessage> assetClassList = FieldValueMessageController.getFieldValueList("assetClass", siteId);
    public List<I18nMessage> spare01List = FieldValueMessageController.getFieldValueList("spare01", siteId);
    public List<I18nMessage> spare02List = FieldValueMessageController.getFieldValueList("spare02", siteId);
    public List<I18nMessage> spare03List = FieldValueMessageController.getFieldValueList("spare03", siteId);
    public List<I18nMessage> spare04List = FieldValueMessageController.getFieldValueList("spare04", siteId);
    public List<I18nMessage> spare05List = FieldValueMessageController.getFieldValueList("spare05", siteId);
    public List<I18nMessage> depreciationMethod = FieldValueMessageController.getFieldValueList("depreciationMethodList", siteId);
    public List<I18nMessage> assetUsageList = FieldValueMessageController.getFieldValueList("assetUsage", siteId);
    public List<I18nMessage> capitalSourceList = FieldValueMessageController.getFieldValueList("capitalSource", siteId);
    public List<I18nMessage> measureGradeList = FieldValueMessageController.getFieldValueList("measureGrade", siteId);
    public List<I18nMessage> riskGradeList = FieldValueMessageController.getFieldValueList("riskGrade", siteId);
    public List<I18nMessage> emergencyEquipmentList = FieldValueMessageController.getFieldValueList("emergencyEquipment", siteId);
    public List<I18nMessage> importEquipmentList = FieldValueMessageController.getFieldValueList("importEquipment", siteId);
    public List<I18nMessage> accountingCategoryList = FieldValueMessageController.getFieldValueList("accountingCategory", siteId);
    public List<I18nMessage> measuringItemCategoryList = FieldValueMessageController.getFieldValueList("measuringItemCategory", siteId);
    public List<I18nMessage> repairFormList = FieldValueMessageController.getFieldValueList("repairForm", siteId);
    public List<I18nMessage> assetStatusList = assetCreateService.getAssetStatusList();
    public List<I18nMessage> assetValueCategoryList = FieldValueMessageController.getFieldValueList("assetValueCategory", siteId);
    public List<I18nMessage> capitalSource2List = FieldValueMessageController.getFieldValueList("capitalSource2", siteId);
    public List<I18nMessage> deviceStatusList = FieldValueMessageController.getFieldValueList("deviceStatus", siteId);
    public List<I18nMessage> networkingStatusList = FieldValueMessageController.getFieldValueList("networkingStatus", siteId);
    public List<I18nMessage> proprietaryClassList = FieldValueMessageController.getFieldValueList("proprietaryClass", siteId);
    public List<I18nMessage> assetQualityList = FieldValueMessageController.getFieldValueList("assetQuality", siteId);
    public List<I18nMessage> productSegmentList = FieldValueMessageController.getFieldValueList("productSegment", siteId);

    @Autowired
    protected AssetInfoRepository assetDao;

    @Autowired
    protected SupplierRepository supplierDao;

    @Autowired
    protected OrgInfoRepository orgDao;

    @Autowired
    protected UserAccountRepository userDao;

    @Autowired
    protected UserAccountMapper userAccountMapper;

    @Autowired
    protected QrCodeLibRepository qrcodeDao;

    @Autowired
    protected QrCodeService qrCodeService;

    @Autowired
    protected SysAuthGroupRepository authGroupDao;

    @Autowired
    protected AssetTypeGroupRepository assetTypeGroupRepository;

    @Autowired
    protected AssetFinacingNumGenerateService assetFinacingNumGenerateService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected AssetFileImportSupportingService supportingService;

    @Autowired
    private ZkbFileDirectoryService zkbFileDirectoryService;

    @Autowired
    protected AssetDepreciationService assetDepreciationService;

    @Autowired
    protected I18nMessageRepository i18nDao;

    @Autowired
    public AssetTagsMapper assetTagsMapper;

    @Autowired
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;

    @Autowired
    private V2ServiceRequestRepository v2ServiceRequestRepository;

    @Autowired
    MvsUtil mvsUtil;

    @Autowired
    private MqUtils mqUtils;


    protected OrgInfo selectedOrgToImport;

    protected String assetCodeConfig;

    protected WorkflowConfig workflowConfig;

    protected boolean isNeedAudit;

    protected boolean crossSite;

    protected Map<String, AssetInfo> parentAssetsMap = new HashMap<>();

    protected List<AssetTypeGroup> assetTypeGroupList = new ArrayList<>();
    protected Map<String, AssetTypeGroup> assetTypeGroupMap = new HashMap<>();

    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport, boolean crossSite) {
        this.selectedOrgToImport = selectedOrgToImport;
        this.crossSite = crossSite;

        workflowConfig = configService.findConfigBySite(selectedOrgToImport.getSiteUID());

        if (workflowConfig != null) {
            assetCodeConfig = workflowConfig.getAssetCodeConfig();
        }

        isNeedAudit = configService.isAssetNeedAudit(selectedOrgToImport.getSiteUID());

    }

    @Transactional
    public void importOrgData(List<Map<String, Object>> importOrgMap) {

        importOrgMap.forEach(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

            if (orgInfo == null) {
                supportingService.importOrgIfNeeded(selectedOrgToImport, newOrgInfo.getName());
            }

            item.put("status", orgInfo == null ? ImportStatus.Created : ImportStatus.Exist);
        });

    }

    @Transactional
    public void importSupplierData(List<Map<String, Object>> importOrgMap) {

        importOrgMap.stream().forEach(item -> {
            AssetInfo newAssetInfo = (AssetInfo) item.get("asset");
            Supplier supplier = supplierDao.findByTenantUIDAndName(selectedOrgToImport.getTenantUID(), newAssetInfo.getVendor());
            if (null == supplier) {
                supportingService.importSupplierIfNeeded(selectedOrgToImport, newAssetInfo.getVendor());
            }
        });
    }

    public void deleteNursiteMaintain(List<AssetInfo> uncheckedNursiteMaintainAssetList) {
        for (AssetInfo assetInfo : uncheckedNursiteMaintainAssetList) {
            assetInfoBulkEditCheckService.deleteNursiteMaintain(assetInfo);
        }
    }
    public void deleteNurseSiteMaintainConfigItem(List<AssetInfo> uncheckedNursiteMaintainAssetList) {
        for (AssetInfo assetInfo : uncheckedNursiteMaintainAssetList) {
            assetInfoBulkEditCheckService.deleteNurseSiteMaintainConfigItem(assetInfo);
        }
    }

    @Transactional
    public List<AssetInfo> importAssetData(List<Map<String, Object>> importAssetMap,
                                           Boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache) {

        LocalDateTime startTime = LocalDateTime.now();

        int row = 0;
        List<AssetInfo> saveList = ListUtil.newArrayList();
        try {
            if (UserContextService.getCurrentUserAccount().getSiteId() == ApmConstant.IB_TENANT_ID) {

                //locate parent assets first for later use
                parentAssetsMap = supportingService.getParentAssets(importAssetMap);

                //first iteration: import parent asset
                for (Map<String, Object> item : importAssetMap) {
                    AssetInfo assetInfo = (AssetInfo) item.get("asset");
                    if (supportingService.isParentAssetForIBTenant(assetInfo)) {
                        AssetInfo newAsset = importOneAsset(item, autoGenerateQrcode, orgNamesUriCache, importAssetMap);
                        if (ObjectUtil.isNotEmpty(newAsset)) {
                            saveList.add(newAsset);
                        }
                    }
                }

                //second iteration: import child asset and other asset
                for (Map<String, Object> item : importAssetMap) {
                    AssetInfo assetInfo = (AssetInfo) item.get("asset");
                    if (supportingService.isParentAssetForIBTenant(assetInfo)) {
                        continue;
                    }
                    if (supportingService.isChildAssetForIBTenant(assetInfo, parentAssetsMap)) {
                        supportingService.setParentAsset(assetInfo, parentAssetsMap);
                    }
                    AssetInfo newAsset = importOneAsset(item, autoGenerateQrcode, orgNamesUriCache, importAssetMap);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }

            } else {
                for (Map<String, Object> item : importAssetMap) {
                    if (row % 100 == 0) {
                        logger.info("imported {} rows", row);
                    }
                    ++row;
                    AssetInfo newAsset = importOneAsset(item, autoGenerateQrcode, orgNamesUriCache, importAssetMap);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }
            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
        return saveList;
    }

    public List<AssetInfo> importAssetDataNew(List<AssetImport> importDataList,
                                              Boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache) {

        LocalDateTime startTime = LocalDateTime.now();
        Map<String, OrgInfo> orgCache = new HashMap<>();
        Map<String, UserAccount> userCache = new HashMap<>();
        Map<String, Supplier> supplierCache = new HashMap<>();

        int row = 0;
        List<AssetInfo> saveList = ListUtil.newArrayList();
        DbResourcesUtil resourcesUtil=WebUtil.getBean(DbResourcesUtil.class);
        try {
            if (UserContextService.getCurrentUserAccount().getSiteId() == ApmConstant.IB_TENANT_ID) {

                //locate parent assets first for later use
                parentAssetsMap = supportingService.getParentAssetsNew(importDataList);

                //first iteration: import parent asset
                for (AssetImport item : importDataList) {
                    if (supportingService.isParentAssetForIBTenant(item)) {
                        AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, UserContextService.getCurrentUserAccount(),resourcesUtil);
                        if (ObjectUtil.isNotEmpty(newAsset)) {
                            saveList.add(newAsset);
                        }
                    }
                }

                //second iteration: import child asset and other asset
                for (AssetImport item : importDataList) {
                    if (supportingService.isParentAssetForIBTenant(item)) {
                        continue;
                    }
                    supportingService.parentAssetSetting(item, parentAssetsMap);
                    AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, UserContextService.getCurrentUserAccount(),resourcesUtil);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }

            } else {
                for (AssetImport item : importDataList) {
                    if (row % 100 == 0) {
                        logger.info("imported {} rows", row);
                    }
                    ++row;
                    AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, UserContextService.getCurrentUserAccount(),resourcesUtil);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }
            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
        return saveList;
    }

    public List<AssetInfo> importAssetDataAsync(List<AssetImport> importDataList,
                                                Boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache, UserAccount currentAccount,DbResourcesUtil resourcesUtil) {

        LocalDateTime startTime = LocalDateTime.now();
        Map<String, OrgInfo> orgCache = new HashMap<>();
        Map<String, UserAccount> userCache = new HashMap<>();
        Map<String, Supplier> supplierCache = new ConcurrentHashMap<>();

        int row = 0;
        List<AssetInfo> saveList = ListUtil.newArrayList();
        try {
            if (currentAccount.getSiteId() == ApmConstant.IB_TENANT_ID) {

                //locate parent assets first for later use
                parentAssetsMap = supportingService.getParentAssetsNew(importDataList);

                //first iteration: import parent asset
                for (AssetImport item : importDataList) {
                    if (supportingService.isParentAssetForIBTenant(item)) {
                        AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, currentAccount,resourcesUtil);
                        if (ObjectUtil.isNotEmpty(newAsset)) {
                            saveList.add(newAsset);
                        }
                    }
                }

                //second iteration: import child asset and other asset
                for (AssetImport item : importDataList) {
                    if (supportingService.isParentAssetForIBTenant(item)) {
                        continue;
                    }
                    supportingService.parentAssetSetting(item, parentAssetsMap);
                    AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, currentAccount,resourcesUtil);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }

            } else {
                for (AssetImport item : importDataList) {
                    if (row % 100 == 0) {
                        logger.info("imported {} rows", row);
                    }
                    ++row;
                    AssetInfo newAsset = importOneAssetNew(item, autoGenerateQrcode, orgNamesUriCache, orgCache, userCache, supplierCache, currentAccount,resourcesUtil);
                    if (ObjectUtil.isNotEmpty(newAsset)) {
                        saveList.add(newAsset);
                    }
                }
            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
        return saveList;
    }

    @Transactional
    protected AssetInfo importOneAsset(Map<String, Object> item, boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache, List<Map<String, Object>> importAssetMap) {
        AssetInfo newAsset = (AssetInfo) item.get("asset");
        AssetInfo dbAsset = (AssetInfo) item.get("oldAsset");
        Map<String, ExcelDocument.ValueWrapper> rowMap = (Map<String, ExcelDocument.ValueWrapper>) item.get("rowData");

        AssetRow.clearErrMessage(item);

        item.put("status", StringUtils.isEmpty(newAsset.getUid()) ? ImportStatus.Created : ImportStatus.Exist);

        OrgInfo siteOrg = selectedOrgToImport;
        if (crossSite) {
            siteOrg = supportingService.findSiteOrgInAssetExcelRow(rowMap, orgNamesUriCache).getSiteOrg();
        }
        //check depart num to avoid duplicate when update
        boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(siteOrg.getUid(), newAsset);

        if (isDuplicate) {
            item.put("status", ImportStatus.Failure);
            AssetRow.addErrMessage(item, "错误: 设备编号重复");
            return null;
        }

        String clinicalDeptName = newAsset.getClinicalDeptName();
        if (StringUtils.isEmpty(clinicalDeptName)) {
            if (StringUtils.isEmpty(newAsset.getUid())) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误: 科室为空");
                return null;
            }

        } else {
            OrgInfo clinicalDept = supportingService.findOrgOnly(siteOrg, clinicalDeptName);
            if (clinicalDept != null) {
                //new child clinical will reach here
                newAsset.setClinicalDeptId(clinicalDept.getId());
                newAsset.setClinicalDeptUID(clinicalDept.getUid());
            }
        }

        String assetDeptName = newAsset.getAssetDeptName();
        if (!StringUtils.isEmpty(assetDeptName)) {
            OrgInfo assetDept = supportingService.findOrgOnly(siteOrg, assetDeptName);
            if (assetDept != null) {
                newAsset.setAssetDeptId(assetDept.getId());
                newAsset.setAssetDeptUID(assetDept.getUid());
            } else {
                newAsset.setAssetDeptName(null);
            }

        } else {
            newAsset.setAssetDeptId(null);
            newAsset.setAssetDeptUID(null);
            newAsset.setAssetDeptName(null);
        }

//        supportingService.setAssetDeptId(newAsset, siteOrg, item);

        List<UserAccount> userAccounts;
        //for AssetOwner
        if (newAsset.getAssetOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + "未设置");
        } else {

            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                newAsset.setAssetOwnerName(dbAsset.getAssetOwnerName());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName") + newAsset.getAssetOwnerName() + "此用已停用");
                newAsset.setAssetOwnerName(null);
            } else {
                newAsset.setAssetOwnerId(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel(userAccounts.get(0).getTelephone());
            }
        }

        //for AssetOwner2
        if (newAsset.getAssetOwnerName2() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName2());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                newAsset.setAssetOwnerName2(dbAsset.getAssetOwnerName2());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("AssetOwnerName2") + newAsset.getAssetOwnerName2() + "此用户已停用");
                newAsset.setAssetOwnerName2(null);
            } else {
                newAsset.setAssetOwnerId2(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel2(userAccounts.get(0).getTelephone());
            }

        }

        //for AssetOwner3
        if (newAsset.getAssetOwnerName3() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName3());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                newAsset.setAssetOwnerName3(dbAsset.getAssetOwnerName3());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("assetOwnerName3") + newAsset.getAssetOwnerName3() + "此用户已停用");
                newAsset.setAssetOwnerName3(null);
            } else {
                newAsset.setAssetOwnerId3(userAccounts.get(0).getId());
                newAsset.setAssetOwnerTel3(userAccounts.get(0).getTelephone());
            }

        }

        //for ClinicalOwner
        if (newAsset.getClinicalOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getClinicalOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                newAsset.setClinicalOwnerName(dbAsset.getClinicalOwnerName());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("ClinicalOwnerName") + newAsset.getClinicalOwnerName() + "此用户已停用");
                newAsset.setClinicalOwnerName(null);
            } else {
                newAsset.setClinicalOwnerId(userAccounts.get(0).getId());
                newAsset.setClinicalOwnerTel(userAccounts.get(0).getTelephone());
            }

        }

        //for Pm OwnerName
        if (newAsset.getPmOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getPmOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + newAsset.getPmOwnerName() + "无此用户/无法关联");
                newAsset.setPmOwnerName(dbAsset.getPmOwnerName());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("pmOwnerName") + newAsset.getPmOwnerName() + "此用户已停用");
                newAsset.setPmOwnerName(null);
            } else {
                newAsset.setPmOwnerId(userAccounts.get(0).getId());
                newAsset.setPmOwnerTel(userAccounts.get(0).getTelephone());
            }

        }
        if (newAsset.getPatrolInspectionOwnerName() == null) {
            AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("patrolInspectionOwnerName") + "未设置");
        } else {
            userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getPatrolInspectionOwnerName());
            if (userAccounts.size() != 1) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("patrolInspectionOwnerName") + newAsset.getPatrolInspectionOwnerName() + "无此用户/无法关联");
                newAsset.setPatrolInspectionOwnerName(dbAsset.getPatrolInspectionOwnerName());
            } else if (userAccounts.get(0).getIsDisable()) {
                AssetRow.addErrMessage(item, "警告:" + WebUtil.getMessage("patrolInspectionOwnerName") + newAsset.getPatrolInspectionOwnerName() + "此用户已停用");
            } else {
                newAsset.setPatrolInspectionOwnerUID(userAccounts.get(0).getUid());
                newAsset.setPatrolInspectionOwnerTel(userAccounts.get(0).getTelephone());
            }

        }
        Supplier supplier;
        //导入供应商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getVendor())) {
            supplier = supportingService.importSupplierIfNeeded(siteOrg, newAsset.getVendor(), newAsset.getVendorTel());
            newAsset.setSupplierId(supplier.getId());
        } else {
            newAsset.setSupplierId(null);
        }

        ////导入维修商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getMaitanance())) {
            supplier = supportingService.importMaintainancerIfNeeded(siteOrg, newAsset.getMaitanance(), newAsset.getMaitananceTel());
            newAsset.setMaitananceId(supplier.getId().toString());
        } else {
            newAsset.setMaitananceId(null);
        }

        ////导入制造商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getManufacture())) {
            supplier = supportingService.importManufacturerIfNeeded(siteOrg, newAsset.getManufacture());
            newAsset.setManufactureId(supplier.getId().toString());
        } else {
            newAsset.setManufactureId(null);
        }

        //auto generate financing num
        String financingNum = newAsset.getFinancingNum();
        String nationalStandardCode = newAsset.getNationalStandardCode();

        if (supportingService.needGenerateFinancingNum(workflowConfig, newAsset)) {

            //修改的时候不需要生成资产编号
            financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    null,
                    nationalStandardCode,
                    newAsset.getSiteUID(),
                    null,
                    financingNum
            );

            if (financingNum == null) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "取资产编码失败！请检查相应微服务");
                return null;
            } else if (financingNum.startsWith("error:")) {
                item.put("status", ImportStatus.Failure);
                AssetRow.addErrMessage(item, financingNum);
                return null;
            } else {
                newAsset.setFinancingNum(financingNum);
            }

        }


        supportingService.releaseOldQRCodeInUse(siteOrg, item);

        //create qr code if needed, make new qrcode in used.
        if (StringUtils.isEmpty(newAsset.getQrCode())) {
            if (autoGenerateQrcode) {
                QrCodeLib newCode = qrCodeService.createOneQrCode(siteOrg, 3);
                newAsset.setQrCode(newCode.getQrCode());
            }
        } else {

            boolean changed = supportingService.isQrCodeNewOrChanged(newAsset, item);

            if (changed) {
                QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(newAsset.getQrCode(), newAsset.getSiteUID());
                if (qrCodeLib == null) {
                    qrCodeService.createSpecifiedQrCode(newAsset.getQrCode(), siteOrg, 3);

                } else {
                    if (qrCodeLib.getStatus() == 1) {//已发行
                        qrCodeLib.setStatus(3);//已建档
                        qrcodeDao.save(qrCodeLib);
                    } else {
                        item.put("status", ImportStatus.Failure);
                        AssetRow.addErrMessage(item, "错误: 该二维码已使用！");
                        return null;
                    }

                }
            }

        }

        supportingService.removeParentAssetIfNotExists(newAsset);

        //if import status failure, already returned, will not reach here.
//        if(org.apache.commons.lang3.StringUtils.isEmpty(newAsset.getUid()) && newAsset.getId()==null){
//            String uid = UUID.randomUUID().toString().replace("-", "");
//            newAsset.setUid(uid);
//        }
        boolean isCreate = false;
        if (newAsset.getId() == null) {
            isCreate = true;
        }

        // 如果租户开启资产分类，导入资产分类为空，则默认赋值第一个分类
        if (UserContextService.getCurrentUserAccount().isEnableAssetTypeGroup() && ObjectUtil.isEmpty(newAsset.getAssetType())) {
            if (assetTypeGroupList.isEmpty()) {
                item.put("assetType", ImportStatus.Failure);
                AssetRow.addErrMessage(item, "错误: 资产分类为空！");
                return null;
            }
            newAsset.setAssetType(assetTypeGroupList.get(0).getEncoded());
        }

        assetDao.save(newAsset);
        AssetInfo assetInfo = new AssetInfo();
        BeanUtils.copyProperties(newAsset, assetInfo);
        if (isCreate) {
//            newAsset.setId(null);
            newAsset.setCreateType("Add");
        }else{
            newAsset.setCreateType("Edit");
        }
//        this.saveAssetHistory(assetInfo);

        assetDepreciationService.saveAssetDerpeciation(newAsset);

        supportingService.saveOrUpdateAssetTags(newAsset, rowMap);

        if (UserContextService.isMVSRole()) {
            if (newAsset.getMvsEquip() == null) {
                newAsset.setMvsEquip(1);
                assetDao.save(newAsset);
            }

            mvsUtil.copyToMvsTableIfMvsEquip(newAsset);

        }
        return assetInfo;
    }

    /**
     * 此方法已被改为被多线程调用，不能通过UserContextService.getCurrentUserAccount()方法获取当前用户
     * 而应使用方法参数列表中的userAccount
     *
     * @param importData
     * @param autoGenerateQrcode
     * @param orgNamesUriCache
     * @param orgCache
     * @param userCache
     * @param supplierCache
     * @param userAccount 当前用户
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    protected AssetInfo importOneAssetNew(AssetImport importData, boolean autoGenerateQrcode, Map<String, OrgInfo> orgNamesUriCache, Map<String, OrgInfo> orgCache, Map<String, UserAccount> userCache, Map<String, Supplier> supplierCache, UserAccount userAccount, DbResourcesUtil resourcesUtil) {
        AssetInfo newAsset = new AssetInfo();
        BeanUtils.copyProperties(importData, newAsset);
        newAsset.setWarranty(importData.getIsWarranty());
        AssetInfo dbAsset = null;
        if (!StringUtils.isEmpty(importData.getUid())) {
            dbAsset = assetDao.findByUid(importData.getUid());

        }
        AssetRow.clearErrMessage(importData);
        importData.setImportStatus(StringUtils.isEmpty(newAsset.getUid()) ? ImportStatus.Created : ImportStatus.Exist);

        OrgInfo siteOrg = selectedOrgToImport;
        if (crossSite) {
            siteOrg = supportingService.findSiteOrgInAssetExcelRow(importData, orgNamesUriCache).getSiteOrg();
        }
        //check depart num to avoid duplicate when update
        boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(siteOrg.getUid(), newAsset);

        if (isDuplicate) {
            importData.setImportStatus(ImportStatus.Failure);
            AssetRow.addErrMessage(importData, "错误: 设备编号重复");
            return null;
        }

        String clinicalDeptName = newAsset.getClinicalDeptName();
        String siteUID = siteOrg.getSiteUID();
        if (StringUtils.isEmpty(clinicalDeptName)) {
            if (StringUtils.isEmpty(newAsset.getUid())) {
                importData.setImportStatus(ImportStatus.Failure);
                AssetRow.addErrMessage(importData, "错误: 科室为空");
                return null;
            }

        } else {

            OrgInfo clinicalDept = orgCache.get(siteUID + clinicalDeptName);
            if (Objects.isNull(clinicalDept)) {
                clinicalDept = supportingService.findOrgOnly(siteOrg, clinicalDeptName);
            }
            if (clinicalDept != null) {
                orgCache.put(siteUID + clinicalDeptName, clinicalDept);
                //new child clinical will reach here
                newAsset.setClinicalDeptId(clinicalDept.getId());
                newAsset.setClinicalDeptUID(clinicalDept.getUid());
            }
        }

        String assetDeptName = newAsset.getAssetDeptName();
        if (!StringUtils.isEmpty(assetDeptName)) {
            OrgInfo assetDept = orgCache.get(siteUID + assetDeptName);
            if (Objects.isNull(assetDept)) {
                assetDept = supportingService.findOrgOnly(siteOrg, assetDeptName);
            }
            if (assetDept != null) {
                orgCache.put(siteUID + assetDeptName, assetDept);
                newAsset.setAssetDeptId(assetDept.getId());
                newAsset.setAssetDeptUID(assetDept.getUid());
            } else {
                newAsset.setAssetDeptName(null);
            }

        } else {
            newAsset.setAssetDeptId(null);
            newAsset.setAssetDeptUID(null);
            newAsset.setAssetDeptName(null);
        }

//        supportingService.setAssetDeptId(newAsset, siteOrg, item);

        List<UserAccount> userAccounts;
        //for AssetOwner
        if (newAsset.getAssetOwnerName() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getAssetOwnerName());
            if (Objects.nonNull(owner)) {
                newAsset.setAssetOwnerId(owner.getId());
                newAsset.setAssetOwnerTel(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName",Locale.CHINA) + newAsset.getAssetOwnerName() + "无此用户/无法关联");
                    newAsset.setAssetOwnerName(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName",Locale.CHINA) + newAsset.getAssetOwnerName() + "此用已停用");
                    newAsset.setAssetOwnerName(null);
                } else {
                    newAsset.setAssetOwnerId(userAccounts.get(0).getId());
                    newAsset.setAssetOwnerTel(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getAssetOwnerName(), userAccounts.get(0));
                }
            }
        }

        //for AssetOwner2
        if (newAsset.getAssetOwnerName2() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName2",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getAssetOwnerName2());
            if (Objects.nonNull(owner)) {
                newAsset.setAssetOwnerId2(owner.getId());
                newAsset.setAssetOwnerTel2(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName2());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName2",Locale.CHINA) + newAsset.getAssetOwnerName2() + "无此用户/无法关联");
                    newAsset.setAssetOwnerName2(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("AssetOwnerName2",Locale.CHINA) + newAsset.getAssetOwnerName2() + "此用户已停用");
                    newAsset.setAssetOwnerName2(null);
                } else {
                    newAsset.setAssetOwnerId2(userAccounts.get(0).getId());
                    newAsset.setAssetOwnerTel2(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getAssetOwnerName2(), userAccounts.get(0));
                }
            }
        }

        //for AssetOwner3
        if (newAsset.getAssetOwnerName3() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("assetOwnerName3",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getAssetOwnerName3());
            if (Objects.nonNull(owner)) {
                newAsset.setAssetOwnerId3(owner.getId());
                newAsset.setAssetOwnerTel3(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getAssetOwnerName3());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("assetOwnerName3",Locale.CHINA) + newAsset.getAssetOwnerName3() + "无此用户/无法关联");
                    newAsset.setAssetOwnerName3(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("assetOwnerName3",Locale.CHINA) + newAsset.getAssetOwnerName3() + "此用户已停用");
                    newAsset.setAssetOwnerName3(null);
                } else {
                    newAsset.setAssetOwnerId3(userAccounts.get(0).getId());
                    newAsset.setAssetOwnerTel3(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getAssetOwnerName3(), userAccounts.get(0));
                }
            }
        }

        //for ClinicalOwner
        if (newAsset.getClinicalOwnerName() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("ClinicalOwnerName",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getClinicalOwnerName());
            if (Objects.nonNull(owner)) {
                newAsset.setClinicalOwnerId(owner.getId());
                newAsset.setClinicalOwnerTel(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getClinicalOwnerName());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("ClinicalOwnerName",Locale.CHINA) + newAsset.getClinicalOwnerName() + "无此用户/无法关联");
                    newAsset.setClinicalOwnerName(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("ClinicalOwnerName",Locale.CHINA) + newAsset.getClinicalOwnerName() + "此用户已停用");
                    newAsset.setClinicalOwnerName(null);
                } else {
                    newAsset.setClinicalOwnerId(userAccounts.get(0).getId());
                    newAsset.setClinicalOwnerTel(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getClinicalOwnerName(), userAccounts.get(0));
                }
            }
        }

        //for Pm OwnerName
        if (newAsset.getPmOwnerName() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("pmOwnerName",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getPmOwnerName());
            if (Objects.nonNull(owner)) {
                newAsset.setPmOwnerId(owner.getId());
                newAsset.setPmOwnerTel(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getPmOwnerName());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("pmOwnerName",Locale.CHINA) + newAsset.getPmOwnerName() + "无此用户/无法关联");
                    newAsset.setPmOwnerName(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("pmOwnerName",Locale.CHINA) + newAsset.getPmOwnerName() + "此用户已停用");
                    newAsset.setPmOwnerName(null);
                } else {
                    newAsset.setPmOwnerId(userAccounts.get(0).getId());
                    newAsset.setPmOwnerTel(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getPmOwnerName(), userAccounts.get(0));
                }
            }
        }
        if (newAsset.getPatrolInspectionOwnerName() == null) {
            AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("patrolInspectionOwnerName",Locale.CHINA) + "未设置");
        } else {
            UserAccount owner = userCache.get(newAsset.getPatrolInspectionOwnerName());
            if (Objects.nonNull(owner)) {
                newAsset.setPatrolInspectionOwnerUID(owner.getUid());
                newAsset.setPatrolInspectionOwnerTel(owner.getTelephone());
            } else {
                userAccounts = userDao.findByTenantUIDAndNameAndValid(tenantUID, newAsset.getPatrolInspectionOwnerName());
                if (userAccounts.size() != 1) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("patrolInspectionOwnerName",Locale.CHINA) + newAsset.getPatrolInspectionOwnerName() + "无此用户/无法关联");
                    newAsset.setPatrolInspectionOwnerName(null);
                } else if (userAccounts.get(0).getIsDisable()) {
                    AssetRow.addErrMessage(importData, "警告:" + resourcesUtil.getMsgValue("patrolInspectionOwnerName",Locale.CHINA) + newAsset.getPatrolInspectionOwnerName() + "此用户已停用");
                } else {
                    newAsset.setPatrolInspectionOwnerUID(userAccounts.get(0).getUid());
                    newAsset.setPatrolInspectionOwnerTel(userAccounts.get(0).getTelephone());
                    userCache.put(newAsset.getPatrolInspectionOwnerName(), userAccounts.get(0));
                }
            }
        }
        Supplier supplier;
        //导入供应商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getVendor())) {
            supplier = supplierCache.get(newAsset.getVendor());
            if (Objects.isNull(supplier)) {
                supplier = supportingService.importSupplierIfNeededSync(siteOrg, newAsset.getVendor(), newAsset.getVendorTel(), userAccount, 1);
                supplierCache.put(newAsset.getVendor(), supplier);
            }
            newAsset.setSupplierId(supplier.getId());
        } else {
            newAsset.setSupplierId(null);
        }

        ////导入维修商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getMaitanance())) {
            Supplier maitananceSupplier = supplierCache.get(newAsset.getMaitanance());
            if (Objects.isNull(maitananceSupplier)) {
                maitananceSupplier = supportingService.importSupplierIfNeededSync(siteOrg, newAsset.getMaitanance(), newAsset.getMaitananceTel(), userAccount, 2);
            }
            newAsset.setMaitananceId(maitananceSupplier.getId().toString());
            supplierCache.put(newAsset.getMaitanance(), maitananceSupplier);
        } else {
            newAsset.setMaitananceId(null);
        }

        ////导入制造商，并修正ID
        if (!StringUtils.isEmpty(newAsset.getManufacture())) {
            Supplier manufacturSupplier = supplierCache.get(newAsset.getManufacture());
            if (Objects.isNull(manufacturSupplier)) {
                manufacturSupplier = supportingService.importSupplierIfNeededSync(siteOrg, newAsset.getManufacture(), newAsset.getManufactureTel(), userAccount, 3);
            }
            newAsset.setManufactureId(manufacturSupplier.getId().toString());
            supplierCache.put(newAsset.getManufacture(), manufacturSupplier);
        } else {
            newAsset.setManufactureId(null);
        }
        //auto generate financing num
        String financingNum = newAsset.getFinancingNum();
        String nationalStandardCode = newAsset.getNationalStandardCode();

        if (supportingService.needGenerateFinancingNum(workflowConfig, newAsset)) {

            //修改的时候不需要生成资产编号
            financingNum = assetFinacingNumGenerateService.autoGenerateFinacingNum(
                    null,
                    nationalStandardCode,
                    newAsset.getSiteUID(),
                    null,
                    financingNum
            );

            if (financingNum == null) {
                importData.setImportStatus(ImportStatus.Failure);
                AssetRow.addErrMessage(importData, "取资产编码失败！请检查相应微服务");
                return null;
            } else if (financingNum.startsWith("error:")) {
                importData.setImportStatus(ImportStatus.Failure);
                AssetRow.addErrMessage(importData, financingNum);
                return null;
            } else {
                newAsset.setFinancingNum(financingNum);
            }

        }


        supportingService.releaseOldQRCodeInUse(siteOrg, importData);

        //create qr code if needed, make new qrcode in used.
        if (StringUtils.isEmpty(newAsset.getQrCode())) {
            if (autoGenerateQrcode) {
                QrCodeLib newCode = qrCodeService.createOneQrCode(siteOrg, 3);
                newAsset.setQrCode(newCode.getQrCode());
            }
        } else {
            boolean changed = supportingService.isQrCodeNewOrChanged(newAsset, importData);
            if (changed) {
                QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(newAsset.getQrCode(), newAsset.getSiteUID());
                if (qrCodeLib == null) {
                    qrCodeService.createSpecifiedQrCode(newAsset.getQrCode(), siteOrg, 3);

                } else {
                    if (qrCodeLib.getStatus() == 1) {//已发行
                        qrCodeLib.setStatus(3);//已建档
                        qrcodeDao.save(qrCodeLib);
                    } else {
                        importData.setImportStatus(ImportStatus.Failure);
                        AssetRow.addErrMessage(importData, "错误: 该二维码已使用！");
                        return null;
                    }

                }
            }

        }

        supportingService.removeParentAssetIfNotExists(newAsset);

        //if import status failure, already returned, will not reach here.
//        if(org.apache.commons.lang3.StringUtils.isEmpty(newAsset.getUid()) && newAsset.getId()==null){
//            String uid = UUID.randomUUID().toString().replace("-", "");
//            newAsset.setUid(uid);
//        }
        boolean isCreate = false;
        if (newAsset.getId() == null) {
            isCreate = true;
        }

        // 如果租户开启资产分类，导入资产分类为空，则默认赋值第一个分类
        if (userAccount.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(newAsset.getAssetType())) {
            if (assetTypeGroupList.isEmpty()) {
                importData.setImportStatus(ImportStatus.Failure);
                AssetRow.addErrMessage(importData, "错误: 资产分类为空！");
                return null;
            }
            newAsset.setAssetType(assetTypeGroupList.get(0).getEncoded());
        }

        assetDao.save(newAsset);
        assetDepreciationService.saveAssetDerpeciation(newAsset);
        supportingService.saveOrUpdateAssetTags(newAsset, importData);

        if (isMvs(userAccount)) {
            if (newAsset.getMvsEquip() == null) {
                newAsset.setMvsEquip(1);
                assetDao.save(newAsset);
            }

            mvsUtil.copyToMvsTableIfMvsEquip(newAsset);

        }
        if (isCreate) {
//            newAsset.setId(null);
            newAsset.setCreateType("Add");
        }else{
            newAsset.setCreateType("Edit");
        }
        newAsset.setUsageCount(importData.getUsageCount());
        newAsset.setRatingUsageCount(importData.getRatingUsageCount());
        return newAsset;
    }

    public void saveAssetHistory(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setDescribe("资产导入添加");
        mqContext.setMethodName("importOneAsset");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetFileImportBaseService");
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        Locale locale = LocaleContextHolder.getLocale();
        mqContext.setLocale(locale.getLanguage());
        try {
            mqUtils.sendAssetToMQ(newAsset, currentUserAccount, mqContext);
        } catch (Exception e) {
            logger.error("importOneAsset send mq error:", e);
        }
    }

    public void saveAssetHistoryAsync(AssetInfo newAsset, UserAccount currentUser, Locale locale) {
        MqContext mqContext = new MqContext();
        mqContext.setDescribe("资产导入添加");
        mqContext.setMethodName("importOneAsset");
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetFileImportBaseService");
        mqContext.setLocale(locale.getLanguage());
        try {
            mqUtils.sendAssetToMQ(newAsset, currentUser, mqContext);
        } catch (Exception e) {
            logger.error("importOneAsset send mq error:", e);
        }
    }

    public Map checkData(List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap, List<AssetInfo> unCheckNurseSiteMaintainList) {
        LocalDateTime startTime = LocalDateTime.now();
        assetTypeGroupList = assetTypeGroupRepository.listAssetTypeGroupInHierarchyByUser(UserContextService.getCurrentUserAccount().getUid());
        assetTypeGroupMap = assetTypeGroupList.stream().collect(Collectors.toMap(AssetTypeGroup::getEncoded, t -> t));
        if (!crossSite) {
            //only selected site can import clinical dept, otherwise clinical dept excel sheet is meaningless.
            importOrgMap.forEach(item -> {
                OrgInfo newOrgInfo = (OrgInfo) item.get("org");

                //parent uid is not working for child clinical dept, so we use site id
                OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

                item.put("status", orgInfo == null ? ImportStatus.New : ImportStatus.Exist);
            });
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("canBeImport", Boolean.TRUE);
        result.put("unCheckedNurseSite", Boolean.FALSE);

        int row = 0;
        for (Map<String, Object> item : importAssetMap) {
            ++row;

            AssetInfo newAsset = (AssetInfo) item.get("asset");

            if (!StringUtils.isEmpty(newAsset.getUid())) {
                //检查是否输入非法UUID
                if (!tenantUID.equals(newAsset.getTenantUID())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, String.format("第%d行错误: 非法UUID！", row));
                    continue;
                }
            }

            if (null == newAsset.getAssetGroup()) {
                item.put("status", ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("assetGroup") + WebUtil.getMessage("ValidationRequire"));
            }

            if (StringUtils.isEmpty(newAsset.getDepartNum()) && StringUtils.isEmpty(newAsset.getUid())) {
                item.put("status", ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备编号是必须的");
                continue;
            }
            UserContextService userContextService = WebUtil.getBean(UserContextService.class);
            boolean zkbAccountBoolean = userContextService.isZkbAccount();
            if (zkbAccountBoolean) {
                if (StringUtils.isEmpty(newAsset.getMainDirectory())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 主目录名字不能为空");
                    continue;
                } else {
                    List<ZkbFileDirectory> mainDirectoryList = zkbFileDirectoryService.findByLevelAndName(zkbFileDirectoryService.MAIN_DIRECTORY_LEVEL_VALUE, newAsset.getMainDirectory());
                    if (mainDirectoryList.isEmpty()) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 主目录名字没有找到! " + newAsset.getMainDirectory());
                        continue;
                    }
                }
                if (StringUtils.isEmpty(newAsset.getSecondDirectory())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 子目录名字不能为空");
                    continue;
                } else {
                    List<ZkbFileDirectory> secondDirectoryList = zkbFileDirectoryService.findByLevelAndName(zkbFileDirectoryService.SUB_DIRECTORY_LEVEL_VALUE, newAsset.getSecondDirectory());
                    if (secondDirectoryList.isEmpty()) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 子目录名字找不到! " + newAsset.getSecondDirectory());
                        continue;
                    }
                }
            }
            //check clinical dept
            String clinicalDeptName = newAsset.getClinicalDeptName();
            List<AssetInfo> assets = new ArrayList<>();
            AssetInfo asset = assetDao.findByUid(newAsset.getUid());
            assets.add(asset);
            if (StringUtils.isEmpty(clinicalDeptName)) {
                //新增的时候不允许为空，更新的时候允许为空。
                if (StringUtils.isEmpty(newAsset.getUid())) {
                    item.put("status", ImportStatus.CheckFail);
                    AssetRow.addErrMessage(item, "错误: 所属科室为空");
                } else {
                    newAsset.setClinicalDeptId(null);
                    newAsset.setClinicalDeptUID(null);
                }
            } else {
                OrgInfo clinicalDept = null;
                //if child clinical dept not found, we don't create. we only create parent clinical dept in later steps.
                if (supportingService.isLongName(clinicalDeptName)) {
                    clinicalDept = orgDao.findFirstBySiteUIDAndNameAndOrgType(newAsset.getSiteUID(), clinicalDeptName, 4);
                    if (clinicalDept == null) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 所属科室没有找到! " + clinicalDeptName);
                    }

                } else {
                    if (crossSite) {
                        //single site mode, we will import org in later steps, so no need to check.
                        clinicalDept = orgDao.findFirstByParentUIDAndShortName(newAsset.getSiteUID(), clinicalDeptName);
                        if (clinicalDept == null) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误: 所属科室没有找到! " + clinicalDeptName);
                        }
                    } else {
                        if (!supportingService.isClinicalDeptExists(clinicalDeptName, importOrgMap)) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误: 所属科室没有找到! " + clinicalDeptName + ",请添加到科室列表页");
                        }
                    }

                }

            }

            //check asset dept
            String assetDeptName = newAsset.getAssetDeptName();
            if (!StringUtils.isEmpty(assetDeptName)) {
                OrgInfo assetDept;
                if (supportingService.isLongName(assetDeptName)) {
                    assetDept = orgDao.findFirstBySiteUIDAndNameAndOrgType(newAsset.getSiteUID(), assetDeptName, 4);
                    if (assetDept == null) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 使用科室没有找到! " + assetDeptName);
                    }
                } else {
                    if (crossSite) {
                        //single site mode, we will import org in later steps, so no need to check.
                        assetDept = orgDao.findFirstByParentUIDAndShortName(newAsset.getSiteUID(), assetDeptName);
                        if (assetDept == null) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误: 使用科室没有找到! " + assetDeptName);
                        }
                    } else {
                        if (!supportingService.isClinicalDeptExists(assetDeptName, importOrgMap)) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误: 使用科室没有找到! " + assetDeptName + ",请添加到科室列表页");
                        }
                    }
                }
            }
            if (asset != null) {
                OrgInfo orgInfo = orgDao.findByUid(asset.getAssetDeptUID());
                String assetDeptNameOld = null;
                if (orgInfo != null) {
                    assetDeptNameOld = orgInfo.getName();
                }

                if ((StringUtils.hasText(assetDeptNameOld) && !assetDeptNameOld.equals(assetDeptName)) ||
                        (StringUtils.hasText(asset.getClinicalDeptName()) && !asset.getClinicalDeptName().equals(clinicalDeptName))) {
                    if (asset.getSharedAssetTypeUid() != null) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 设备已绑定调剂名称,不能修改科室");
                    }
                    if (!assetInfoBulkEditCheckService.verifyForMaintain(assets)) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 设备有未完成的保养工单,不能修改科室");
                    }
                    if (!assetInfoBulkEditCheckService.verifyForInventory(assets)) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 设备有未盘点的工单,不能修改科室");
                    }
                    for (AssetInfo as : assets) {
                        boolean assetFlag = true;
                        List<V2_ServiceRequest> serviceRequestList = v2ServiceRequestRepository.findByAssetId(as.getId());
                        if (CollectionUtils.isNotEmpty(serviceRequestList)) {
                            for (V2_ServiceRequest service : serviceRequestList) {
                                if (service.getCloseTime() == null) {
                                    assetFlag = false;
                                }
                            }
                            if (assetFlag) {
                                continue;
                            } else {
                                item.put("status", ImportStatus.CheckFail);
                                result.put("canBeImport", Boolean.FALSE);
                                AssetRow.addErrMessage(item, "错误: 设备有未完成的维修工单,不能修改科室");
                            }
                        }
                    }
                    //临床助手检查进行中
                    unCheckNurseSiteMaintainList.addAll(assetInfoBulkEditCheckService.verifyForNurseSiteByBatchEdit(assets));
                }
                if (assetDao.secondment(asset.getUid()) > 0) {
                    boolean flag = false;
                    if (!StringUtils.isEmpty(assetDeptNameOld) && !assetDeptNameOld.equals(assetDeptName)) {
                        flag = true;
                    } else if (!StringUtils.isEmpty(assetDeptName) && !assetDeptName.equals(assetDeptNameOld)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 设备正在科室借调中,不能修改使用科室");
                    }
                    String clinicalDeptNameOld = asset.getClinicalDeptName();
                    String clinicalDeptNameNew = clinicalDeptName;
                    if (!StringUtils.isEmpty(clinicalDeptNameOld) && !clinicalDeptNameOld.equals(clinicalDeptNameNew)) {
                        flag = true;
                    } else if (!StringUtils.isEmpty(clinicalDeptNameNew) && !clinicalDeptNameNew.equals(clinicalDeptNameOld)) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 设备正在科室借调中,不能修改所属科室");
                    }
                }
            }

            //check depart num to avoid duplicate when update
            boolean isDuplicate = supportingService.isAssetDepartNumDuplicate(newAsset.getSiteUID(), newAsset);
            if (isDuplicate) {
                item.put("status", ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备编号重复");
            }

            if (!StringUtils.isEmpty(newAsset.getDepartNum())) {
                if (newAsset.getDepartNum().length() > 64) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 设备编号不能超过64位");
                    continue;
                }

                //配置了三码合一和设备编号不为空，则检查，Jira: YHAAO-1594
                if ((AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || AssetFileImportSupportingService.ASSET_CODE_MERGE_TWO.equals(assetCodeConfig))) {


                    if (!newAsset.getDepartNum().equals(newAsset.getFinancingNum()) && !StringUtils.isEmpty(newAsset.getFinancingNum())) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误: 资产编码与设备编码不相同不允许导入");
                    } else {
                        newAsset.setFinancingNum(newAsset.getDepartNum());

                        if (AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                            newAsset.setQrCode(newAsset.getDepartNum());
                        }

                    }


                }
            }

            if (!StringUtils.isEmpty(newAsset.getFinancingNum()) && newAsset.getFinancingNum().length() > 64) {

                item.put("status", ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 资产编码不能超过64位");
                continue;
            }

            //check QrCode
            if (!StringUtils.isEmpty(newAsset.getQrCode())) {
                if (supportingService.isQrCodeNewOrChanged(newAsset, item)) {
                    if (AssetFileImportSupportingService.ASSET_CODE_MERGE_THREE.equals(assetCodeConfig)) {
                        //三码合一情况下，可以创建新的二维码或者二维码已发行
                        QrCodeLib qrCodeLib = qrcodeDao.findByQrCode(newAsset.getQrCode(), newAsset.getSiteUID());
                        if (qrCodeLib != null && qrCodeLib.getStatus() != 1) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("InvalidQRCode") + newAsset.getQrCode());
                            continue;
                        }
                    } else {
                        //非三码合一情况下，二维码需要已发行
                        boolean isAvailable = qrCodeService.isQrcodeAvailableInStatus(newAsset.getQrCode(), newAsset.getSiteUID(), 1);
                        if (!isAvailable) {
                            item.put("status", ImportStatus.CheckFail);
                            result.put("canBeImport", Boolean.FALSE);
                            AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("InvalidQRCode") + newAsset.getQrCode());
                            continue;
                        }
                    }

                }
            }


            if (StringUtils.hasLength(newAsset.getMedicalApparatusClassification())) {
                SysAuthGroup selectedAuthGroup = authGroupDao.findByClassificationCode(newAsset.getMedicalApparatusClassification());
                if (selectedAuthGroup == null) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("medicalApparatusClassification") + "编码不存在");
                    continue;
                }
            }
            // 资产分类校验
            if (UserContextService.getCurrentUserAccount().isEnableAssetTypeGroup()) {
                if (ObjectUtil.isEmpty(newAsset.getAssetType())) {
                    item.put("status", ImportStatus.CheckFail);
                    result.put("canBeImport", Boolean.FALSE);
                    AssetRow.addErrMessage(item, "错误: 资产分类编码为空！");
                    continue;
                } else {
                    AssetTypeGroup selectedAssetTypeGroup = assetTypeGroupMap.get(newAsset.getAssetType());
                    if (selectedAssetTypeGroup == null) {
                        item.put("status", ImportStatus.CheckFail);
                        result.put("canBeImport", Boolean.FALSE);
                        AssetRow.addErrMessage(item, "错误:" + WebUtil.getMessage("assetTypeGroup") + "编码不存在");
                        continue;
                    }
                }
            }
            // 县域租户添加设备性质字段，且必填
            if (newAsset.getAssetQuality() == null && userContextService.isCountyArea()) {
                item.put("status", ImportStatus.CheckFail);
                result.put("canBeImport", Boolean.FALSE);
                AssetRow.addErrMessage(item, "错误: 设备性质是必须的");
                continue;
            }

        }

        if (CollectionUtils.isNotEmpty(unCheckNurseSiteMaintainList)) {
            result.put("unCheckedNurseSite", Boolean.TRUE);
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for checkData: {} seconds", duration.getSeconds());

        return result;
    }

    public String[] getNullFieldName(Objects source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullField = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : pds) {
            Object srcValue = src.getPropertyValue(propertyDescriptor.getName());
            if (srcValue == null) {
                nullField.add(propertyDescriptor.getName());
            }
        }
        String[] result = new String[nullField.size()];
        return nullField.toArray(result);
    }

    public boolean isMvs(UserAccount userAccount) {
        Integer mvsRoleType = userAccount.getMvsRoleType();
        return mvsRoleType != null && (mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_FE || mvsRoleType == ApmConstant.MVS_ROLE_TYPE_ADMIN_2);

    }
}