package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetTagsMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetImport;
import com.ge.apm.service.constant.UsageStatusConstant;
import com.ge.apm.service.orginfo.OrgInfoBuilder;
import com.ge.apm.service.orginfo.OrgInfoLocalCacheService;
import com.ge.apm.service.orginfo.SiteOrgWithDeptOrg;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.ge.apm.service.utils.ApmConstant.CHILD_CLINICAL_DEPT_SEPARATOR;

@Service
public class AssetFileImportSupportingService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportSupportingService.class);

    public static final String ASSET_CODE_MERGE_THREE = "DFQ";
    public static final String ASSET_CODE_MERGE_TWO = "DF";
    public static final String OLD_QRCODE_KEY = "old_qrcode";

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private SupplierRepository supplierDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private OrgInfoRepository orgDao;

    @Autowired
    private AssetInfoRepository assetDao;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private AssetTagsMapper assetTagsMapper;

    @Autowired
    private AssetTagsRepository assetTagsDao;

    @Autowired
    private OrgInfoLocalCacheService orgInfoLocalCacheService;

    @Transactional
    public OrgInfo importOrgIfNeeded(OrgInfo selectedOrgToImport, String name) {

        boolean isShortName = isShortName(name);

        OrgInfo orgInfo;

        if (isShortName) {
            //parent clinical
            orgInfo = orgDao.findFirstByParentUIDAndShortName(selectedOrgToImport.getUid(), name);
        } else {
            //child clinical
            orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), name, 4);
        }

        //only create new parent clinical org
        if (orgInfo == null && isShortName) {
            TenantInfo tenantInfo = tenantInfoDao.findByuid(selectedOrgToImport.getTenantUID());
            orgInfo = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, name, selectedOrgToImport, selectedOrgToImport.getOrgLevel());
            orgDao.save(orgInfo);
        }

        return orgInfo;

    }

    @Transactional
    public Supplier importSupplierIfNeeded(OrgInfo selectedOrgToImport, String name) {

        Supplier supplier;
        supplier = supplierDao.findByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);

        //only create new supplier
        if (null == supplier) {
            //TenantInfo tenantInfo = tenantInfoDao.findByuid(selectedOrgToImport.getTenantUID());
            supplier = new Supplier();
            supplier.setTenantUID(selectedOrgToImport.getTenantUID());
            supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
            supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            supplier.setSiteUID(selectedOrgToImport.getSiteUID());
            supplier.setName(name);
            supplier.setCreatedDate(new Date());
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public OrgInfo findOrgOnly(OrgInfo selectedOrgToImport, String name) {

        boolean isShortName = isShortName(name);

        OrgInfo orgInfo;

        if (isShortName) {
            //parent clinical
            orgInfo = orgDao.findFirstByParentUIDAndShortName(selectedOrgToImport.getUid(), name);
        } else {
            //child clinical
            orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), name, 4);
        }

        return orgInfo;

    }

    public boolean isShortName(String orgName) {
        return !orgName.contains(CHILD_CLINICAL_DEPT_SEPARATOR);
    }

    public boolean isLongName(String orgName) {
        return orgName.contains(CHILD_CLINICAL_DEPT_SEPARATOR);
    }

    @Transactional
    public Supplier importSupplierIfNeeded(OrgInfo selectedOrgToImport, String name, String tel) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setSupplierT(true);

            setDefaultValueForSupplier(supplier, selectedOrgToImport, name, tel);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getSupplierT() == null || !supplier.getSupplierT()) {
            supplier.setSupplierT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public synchronized Supplier importSupplierIfNeededSync(OrgInfo selectedOrgToImport, String name, String tel, UserAccount userAccount,int type) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            updateSupplierType(supplier,type);
            setDefaultValueForSupplierAsync(supplier, selectedOrgToImport, name, tel,userAccount);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getSupplierT() == null || !supplier.getSupplierT()) {
            updateSupplierType(supplier,type);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    public void updateSupplierType(Supplier supplier, int type) {
        switch (type) {
            case 1:
                supplier.setSupplierT(true);
                break;
            case 2:
                supplier.setMaintainerT(true);
                break;
            case 3:
                supplier.setManufacturerT(true);
                break;
        }
    }

    @Transactional
    public synchronized Supplier importSupplierIfNeededSync(OrgInfo selectedOrgToImport, String name, String tel, UserAccount userAccount) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setSupplierT(true);

            setDefaultValueForSupplierAsync(supplier, selectedOrgToImport, name, tel, userAccount);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getSupplierT() == null || !supplier.getSupplierT()) {
            supplier.setSupplierT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public Supplier importMaintainancerIfNeeded(OrgInfo selectedOrgToImport, String name, String tel) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setMaintainerT(true);

            setDefaultValueForSupplier(supplier, selectedOrgToImport, name, tel);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getMaintainerT() == null || !supplier.getMaintainerT() || (!StringUtils.isEmpty(tel) && !tel.equals(supplier.getTel()))) {
            supplier.setMaintainerT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public synchronized Supplier importMaintainancerIfNeededSync(OrgInfo selectedOrgToImport, String name, String tel, UserAccount userAccount) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setMaintainerT(true);

            setDefaultValueForSupplierAsync(supplier, selectedOrgToImport, name, tel, userAccount);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getMaintainerT() == null || !supplier.getMaintainerT() || (!StringUtils.isEmpty(tel) && !tel.equals(supplier.getTel()))) {
            supplier.setMaintainerT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public Supplier importManufacturerIfNeeded(OrgInfo selectedOrgToImport, String name) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setManufacturerT(true);

            setDefaultValueForSupplier(supplier, selectedOrgToImport, name, null);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getManufacturerT() == null || !supplier.getManufacturerT()) {
            supplier.setManufacturerT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    @Transactional
    public synchronized Supplier importManufacturerIfNeededSync(OrgInfo selectedOrgToImport, String name, UserAccount userAccount) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setManufacturerT(true);

            setDefaultValueForSupplierAsync(supplier, selectedOrgToImport, name, null, userAccount);
            supplierDao.save(supplier);
        }

        //existing records, update type
        if (supplier.getManufacturerT() == null || !supplier.getManufacturerT()) {
            supplier.setManufacturerT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    private void setDefaultValueForSupplier(Supplier supplier, OrgInfo selectedOrgToImport, String name, String tel) {

        Date now = new Date();

        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        supplier.setSiteId(selectedOrgToImport.getSiteId());
        supplier.setHospitalId(selectedOrgToImport.getId());
        supplier.setName(name);
        supplier.setTel(tel);

        supplier.setTenantUID(selectedOrgToImport.getTenantUID());
        supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
        supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
        supplier.setSiteUID(selectedOrgToImport.getSiteUID());

        supplier.setCreatedBy(userAccount.getLoginName());
        supplier.setCreatedDate(now);
        supplier.setLastModifiedBy(userAccount.getLoginName());
        supplier.setLastModifiedDate(now);

    }

    private void setDefaultValueForSupplierAsync(Supplier supplier, OrgInfo selectedOrgToImport, String name, String tel, UserAccount userAccount) {

        Date now = new Date();


        supplier.setSiteId(selectedOrgToImport.getSiteId());
        supplier.setHospitalId(selectedOrgToImport.getId());
        supplier.setName(name);
        supplier.setTel(tel);

        supplier.setTenantUID(selectedOrgToImport.getTenantUID());
        supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
        supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
        supplier.setSiteUID(selectedOrgToImport.getSiteUID());

        supplier.setCreatedBy(userAccount.getLoginName());
        supplier.setCreatedDate(now);
        supplier.setLastModifiedBy(userAccount.getLoginName());
        supplier.setLastModifiedDate(now);

    }

    public boolean isAssetDepartNumDuplicate(String siteUid, AssetInfo asset) {

        if (StringUtils.isEmpty(asset.getDepartNum())) {
            return false;
        }

        List<SearchFilter> assetFilters = new ArrayList<>();

        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));

        if (null != asset.getUid()) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
        }

        List<AssetInfo> resultList = assetDao.findBySearchFilter(assetFilters);
        if (resultList.size() > 0) {
            // logger.info("check depart num duplicate: departNUm: {}, siteUid:{}, asset uid:{}", asset.getDepartNum(), siteUid, asset.getUid());
            return true;
        } else {
            return false;
        }

    }

    public boolean isAssetDepartNumDuplicate(String siteUid, AssetImport asset) {

        if (StringUtils.isEmpty(asset.getDepartNum())) {
            return false;
        }

        List<SearchFilter> assetFilters = new ArrayList<>();

        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));

        if (null != asset.getUid()) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
        }

        List<AssetInfo> resultList = assetDao.findBySearchFilter(assetFilters);
        if (resultList.size() > 0) {
            // logger.info("check depart num duplicate: departNUm: {}, siteUid:{}, asset uid:{}", asset.getDepartNum(), siteUid, asset.getUid());
            return true;
        } else {
            return false;
        }

    }

    public boolean isAssetFieldDuplicate(String fieldName, String fieldValue, String siteUid, String assetUid) {
        if (StringUtils.isEmpty(fieldName)) {
            return false;
        }
        List<SearchFilter> assetFilters = new ArrayList<>();

        assetFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.EQ, fieldValue));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUid));

        if (null != assetUid) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, assetUid));
        }
        List<AssetInfo> resultList = assetDao.findBySearchFilter(assetFilters);
        if (resultList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean needGenerateFinancingNum(WorkflowConfig workflowConfig, AssetInfo assetInfo) {

        final int GENERATE_BY_NATIONAL_STANDARD_CODE = 1;
        final int GENERATE_BY_PREFIX_AND_SEQ = 2;
        final int GENERATE_BY_PREFIX_AND_FOUR_DIGITS_YEAR_AND_SEQ = 3;

        if (workflowConfig == null) return false;

        String assetCodeConfig = workflowConfig.getAssetCodeConfig();

        if (ASSET_CODE_MERGE_THREE.equals(assetCodeConfig) || ASSET_CODE_MERGE_TWO.equals(assetCodeConfig)) {
            return false;
        }

        if (workflowConfig != null && workflowConfig.getGenerateMethod() != null) {
            String financingNum = assetInfo.getFinancingNum();

            if (workflowConfig.getGenerateMethod() == GENERATE_BY_NATIONAL_STANDARD_CODE) {

                if (assetInfo.getUid() == null && !StringUtils.isEmpty(assetInfo.getNationalStandardCode()) && (StringUtils.isEmpty(financingNum) || financingNum.length() < 5)) {
                    return true;
                }

            } else if (workflowConfig.getGenerateMethod() == GENERATE_BY_PREFIX_AND_SEQ) {
                if (assetInfo.getUid() == null && (!StringUtils.isEmpty(financingNum) && financingNum.length() < 5)) {
                    return true;
                }
            } else if (workflowConfig.getGenerateMethod() == GENERATE_BY_PREFIX_AND_FOUR_DIGITS_YEAR_AND_SEQ) {
                return true;
            }
        }

        return false;
    }


    public void cleanRelatedFieldsWhenUpdating(AssetInfo asset) {
        if (asset.getAssetOwnerName() == null) {
            asset.setAssetOwnerId(null);
            asset.setAssetOwnerTel(null);
        }
        if (asset.getAssetOwnerName2() == null) {
            asset.setAssetOwnerId2(null);
            asset.setAssetOwnerTel2(null);
        }
        if (asset.getAssetOwnerName3() == null) {
            asset.setAssetOwnerId3(null);
            asset.setAssetOwnerTel3(null);
        }
        if (asset.getClinicalOwnerName() == null) {
            asset.setClinicalOwnerId(null);
            asset.setClinicalOwnerTel(null);
        }
        if (asset.getClinicalDeptName() == null) {
            asset.setClinicalDeptId(null);
            asset.setClinicalDeptUID(null);
        }
        if (asset.getVendor() == null) {
            asset.setSupplierId(null);
        }

    }

    public void markDefautValue(OrgInfo selectedOrgToImport, AssetInfo asset, Date now, boolean isNeedAudit) {
        if (StringUtils.isEmpty(asset.getUid())) {
            // new
            asset.setSiteId(selectedOrgToImport.getSiteId());
            asset.setHospitalId(selectedOrgToImport.getId());

            asset.setTenantUID(selectedOrgToImport.getTenantUID());
            asset.setHospitalUID(selectedOrgToImport.getHospitalUID());
            asset.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            asset.setSiteUID(selectedOrgToImport.getSiteUID());

            asset.setIsValid(true);
            asset.setIsDeleted(Boolean.FALSE);

            asset.setCreatedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setCreatedDate(now);
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setLastModifiedDate(now);

            if (asset.getDeviceStatus() == null) {
                asset.setDeviceStatus(0);
            }

            //only new asset need to set this
            if (isNeedAudit) {
                asset.setUsageStatus(UsageStatusConstant.UNDER_REVIEW); //待审核
            } else {
                asset.setUsageStatus(UsageStatusConstant.ARCHIVED); //已建档
            }

        } else {
            //update
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getName());
            asset.setLastModifiedDate(now);

            cleanRelatedFieldsWhenUpdating(asset);
        }
    }

    public void saveOrUpdateAssetTags(AssetInfo assetInfo, Map<String, ExcelDocument.ValueWrapper> rowMap) {
        String assetTagsJoined = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("assetTags")));
        if (StringUtils.isEmpty(assetTagsJoined)) {
            //if excel content is empty, keep the tags in db
            return;
        }

        if (AssetRow.DELETE_MARK.equalsIgnoreCase(assetTagsJoined)) {
            assetTagsMapper.removeTagsForAsset(assetInfo.getUid());
            return;
        }

        //merge tags
        List<String> existingTags = assetTagsMapper.getTagsByAsset(assetInfo.getUid());
        List<String> currentTags = separateTags(assetTagsJoined);

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            AssetTags assetTags = new AssetTags();
            assetTags.setAssetUid(assetInfo.getUid());
            assetTags.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
            assetTags.setTag(item);
            assetTagsDao.save(assetTags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> {
            assetTagsMapper.removeTag(assetInfo.getUid(), item);
        });

    }

    public void saveOrUpdateAssetTags(AssetInfo assetInfo, AssetImport assetImport) {
        String assetTagsJoined = assetImport.getTagStr();
        if (StringUtils.isEmpty(assetTagsJoined)) {
            //if excel content is empty, keep the tags in db
            return;
        }

        if (AssetRow.DELETE_MARK.equalsIgnoreCase(assetTagsJoined)) {
            assetTagsMapper.removeTagsForAsset(assetInfo.getUid());
            return;
        }

        //merge tags
        List<String> existingTags = assetTagsMapper.getTagsByAsset(assetInfo.getUid());
        List<String> currentTags = separateTags(assetTagsJoined);

        List<String> newTags = new ArrayList<>(currentTags);
        newTags.removeAll(existingTags);

        List<String> removeTags = new ArrayList<>(existingTags);
        removeTags.removeAll(currentTags);

        //add new tags
        newTags.stream().distinct().forEach(item -> {
            AssetTags assetTags = new AssetTags();
            assetTags.setAssetUid(assetInfo.getUid());
            assetTags.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
            assetTags.setTag(item);
            assetTagsDao.save(assetTags);
        });

        //remove useless tags
        removeTags.stream().distinct().forEach(item -> {
            assetTagsMapper.removeTag(assetInfo.getUid(), item);
        });

    }

    private List<String> separateTags(String assetTagsJoined) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(assetTagsJoined)) {
            return Collections.emptyList();
        }

        List<String> tags = Arrays.stream(assetTagsJoined.split("[, ]"))
                .filter(item -> !StringUtils.isEmpty(item))
                .collect(Collectors.toList());

        return tags;
    }

    /**
     * check whether qrcode is new or changed
     *
     * @param assetInfo
     * @param assetMap
     * @return
     */
    public boolean isQrCodeNewOrChanged(AssetInfo assetInfo, Map<String, Object> assetMap) {

        if (StringUtils.isEmpty(assetInfo.getQrCode())) {
            throw new IllegalArgumentException("QrCode should not be empty!");
        }

        if (StringUtils.isEmpty(assetInfo.getUid())) {
            return true;
        }

        String oldQrCode = (String) assetMap.get(OLD_QRCODE_KEY);

        if (assetInfo.getQrCode().equals(oldQrCode)) {
            return false;  //qrCode not changed
        }

        return true;
    }

    public boolean isQrCodeNewOrChanged(AssetInfo assetInfo, AssetImport assetImportData) {

        if (StringUtils.isEmpty(assetInfo.getQrCode())) {
            throw new IllegalArgumentException("QrCode should not be empty!");
        }

        if (StringUtils.isEmpty(assetInfo.getUid())) {
            return true;
        }

        String oldQrCode = assetImportData.getOldQrCode();

        if (assetInfo.getQrCode().equals(oldQrCode)) {
            return false;  //qrCode not changed
        }

        return true;
    }

    public boolean isQrCodeNewOrChanged(AssetImport assetInfo) {

        if (StringUtils.isEmpty(assetInfo.getQrCode())) {
            throw new IllegalArgumentException("QrCode should not be empty!");
        }

        if (StringUtils.isEmpty(assetInfo.getUid())) {
            return true;
        }

        String oldQrCode = assetInfo.getOldQrCode();

        if (assetInfo.getQrCode().equals(oldQrCode)) {
            return false;  //qrCode not changed
        }

        return true;
    }

    /**
     * release qrcode only one asset
     *
     * @param selectedOrgToImport
     * @param item
     */
    public void releaseOldQRCodeInUse(OrgInfo selectedOrgToImport, Map<String, Object> item) {

        AssetInfo newAsset = (AssetInfo) item.get("asset");

        //if site uid not match, ignore update
        if (newAsset.getUid() != null) {
            if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                return;
            }

        }

        String oldQrCode = (String) item.get(OLD_QRCODE_KEY);
        if (StringUtils.isEmpty(oldQrCode)) return;
        if (oldQrCode.equals(newAsset.getQrCode())) return;  //qrCode not changed

        boolean releaseable = true;
        if (!StringUtils.isEmpty(newAsset.getQrCode())) {
            boolean exists = qrCodeService.isQrcodeAvailableInStatus(newAsset.getQrCode(), selectedOrgToImport.getUid(), 1);
            if (!exists) {
                releaseable = false;
            }
        }

        if (releaseable) {
            qrCodeService.releaseQrCode(oldQrCode, selectedOrgToImport.getUid());
        }

    }

    /**
     * release qrcode only one asset
     *
     * @param selectedOrgToImport
     * @param item
     */
    public void releaseOldQRCodeInUse(OrgInfo selectedOrgToImport, AssetImport item) {
        //if site uid not match, ignore update
        if (item.getUid() != null) {
            if (!selectedOrgToImport.getUid().equals(item.getSiteUID())) {
                return;
            }

        }

        String oldQrCode = item.getOldQrCode();
        if (StringUtils.isEmpty(oldQrCode)) return;
        if (oldQrCode.equals(item.getQrCode())) return;  //qrCode not changed

        boolean releaseable = true;
        if (!StringUtils.isEmpty(item.getQrCode())) {
            boolean exists = qrCodeService.isQrcodeAvailableInStatus(item.getQrCode(), selectedOrgToImport.getUid(), 1);
            if (!exists) {
                releaseable = false;
            }
        }

        if (releaseable) {
            qrCodeService.releaseQrCode(oldQrCode, selectedOrgToImport.getUid());
        }

    }

    public void setAssetDeptId(AssetInfo assetInfo, OrgInfo selectedOrgToImport, Map<String, Object> item) {
        Map<String, Object> rowMap = (Map<String, Object>) item.get("rowData");
        String assetDeptName = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("assetDeptId")));
        if (!StringUtils.isEmpty(assetDeptName)) {
            OrgInfo orgInfo = importOrgIfNeeded(selectedOrgToImport, assetDeptName);
            if (orgInfo != null) {
                assetInfo.setAssetDeptId(orgInfo.getId());
                assetInfo.setAssetDeptUID(orgInfo.getUid());
            }
        }
    }

    public SiteOrgWithDeptOrg findSiteOrgInAssetExcelRow(Map<String, ExcelDocument.ValueWrapper> rowMap, Map<String, OrgInfo> orgNamesUriCache) {
        String institution = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("Institution")));
        String hospital = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("Hospital")));
        String site = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("hospitalId")));
        String clinicalDept = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("clinicalDeptId")));
        String assetDept = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("assetDeptId")));

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = orgInfoLocalCacheService.findSiteOrgByFlatternedMap(orgNamesUriCache, institution, hospital, site, clinicalDept, assetDept);

        return siteOrgWithDeptOrg;
    }

    public SiteOrgWithDeptOrg findSiteOrgInAssetExcelRow(AssetImport importData, Map<String, OrgInfo> orgNamesUriCache) {
        String institution = importData.getInstitutionName();
        String hospital = importData.getHospitalName();
        String site = importData.getSiteName();
        String clinicalDept = importData.getClinicalDeptName();
        String assetDept = importData.getAssetDeptName();

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = orgInfoLocalCacheService.findSiteOrgByFlatternedMap(orgNamesUriCache, institution, hospital, site, clinicalDept, assetDept);

        return siteOrgWithDeptOrg;
    }

    public boolean isParentAssetForIBTenant(AssetInfo assetInfo) {
        if (StringUtils.isEmpty(assetInfo.getDepartNum())) {
            return false;
        }

        if (StringUtils.isEmpty(assetInfo.getSystemId())) {
            return false;
        }

        return assetInfo.getDepartNum().equals(assetInfo.getSystemId());
    }

    public boolean isParentAssetForIBTenant(AssetImport assetInfo) {
        if (StringUtils.isEmpty(assetInfo.getDepartNum())) {
            return false;
        }

        if (StringUtils.isEmpty(assetInfo.getSystemId())) {
            return false;
        }

        return assetInfo.getDepartNum().equals(assetInfo.getSystemId());
    }

    public boolean isChildAssetForIBTenant(AssetInfo assetInfo, Map<String, AssetInfo> parentAssetsMap) {

        if (StringUtils.isEmpty(assetInfo.getSystemId())) {
            return false;
        }

        if (assetInfo.getSystemId().equals(assetInfo.getDepartNum())) {
            return false;
        }

        return parentAssetsMap.containsKey(assetInfo.getSystemId());
    }

    public Map<String, AssetInfo> getParentAssets(List<Map<String, Object>> importAssetMap) {
        Map<String, AssetInfo> resultMap = new HashMap<>();

        for (Map<String, Object> map : importAssetMap) {
            AssetInfo assetInfo = (AssetInfo) map.get("asset");
            if (this.isParentAssetForIBTenant(assetInfo)) {
                resultMap.put(assetInfo.getSystemId(), assetInfo);
            }
        }

        return resultMap;
    }

    public Map<String, AssetInfo> getParentAssetsNew(List<AssetImport> importAssetMap) {
        Map<String, AssetInfo> resultMap = new HashMap<>();
        for (AssetImport assetImport : importAssetMap) {
            if (this.isParentAssetForIBTenant(assetImport)) {
                AssetInfo assetInfo = new AssetInfo();
                BeanUtils.copyProperties(importAssetMap, assetImport);
                resultMap.put(assetImport.getSystemId(), assetInfo);
            }
        }

        return resultMap;
    }

    public void setParentAsset(AssetInfo childAsset, Map<String, AssetInfo> parentAssetsMap) {

        AssetInfo parentAsset = parentAssetsMap.get(childAsset.getSystemId());
        if (null != parentAsset) {
            childAsset.setParentAssetId(parentAsset.getId());
        }
    }

    public void parentAssetSetting(AssetImport assetImport, Map<String, AssetInfo> parentAssetMap) {

        if (!StringUtils.isEmpty(assetImport.getSystemId()) && !Objects.equals(assetImport.getSystemId(), assetImport.getDepartNum())) {
            AssetInfo parentAsset = parentAssetMap.get(assetImport.getSystemId());
            if (null != parentAsset) {
                assetImport.setParentAssetId(parentAsset.getId());
            }
        }


    }

    public void removeParentAssetIfNotExists(AssetInfo assetInfo) {
        if (null == assetInfo.getParentAssetId()) {
            return;
        }

        int count = assetDao.countByAssetIdAndNotDeleted(assetInfo.getParentAssetId().intValue());
        if (count == 0) {
            assetInfo.setParentAssetId(null);
        }
    }

    public boolean isClinicalDeptExists(String clinicalDeptName, List<Map<String, Object>> importOrgMap) {
        boolean found = importOrgMap.stream().anyMatch(item -> {
            OrgInfo newOrgInfo = (OrgInfo) item.get("org");
            return Objects.equals(clinicalDeptName, newOrgInfo.getName());
        });

        return found;
    }
}
