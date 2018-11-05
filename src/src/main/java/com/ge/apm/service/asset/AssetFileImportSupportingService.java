package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoBuilder;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AssetFileImportSupportingService {

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

    public boolean isShortName(String orgName) {
        return !orgName.contains("-");
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
    public Supplier importMaintainancerIfNeeded(OrgInfo selectedOrgToImport, String name, String tel) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setMaintainerT(true);

            setDefaultValueForSupplier(supplier, selectedOrgToImport, name, tel);
            supplierDao.save(supplier);
        }

        //existing records, update type or update telephone
        if (supplier.getMaintainerT() == null || !supplier.getMaintainerT() || (!StringUtils.isEmpty(tel) && !tel.equals(supplier.getTel()))) {
            supplier.setMaintainerT(true);
            if (!StringUtils.isEmpty(tel)) {
                supplier.setTel(tel);
            }

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

    private void setDefaultValueForSupplier(Supplier supplier, OrgInfo selectedOrgToImport, String name, String tel) {

        Date now = new Date();

        UserAccount userAccount = UserContextService.getCurrentUserAccount();

        supplier.setSiteId(selectedOrgToImport.getSiteId());
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

    public boolean isAssetDepartNumDuplicate(OrgInfo selectedOrgToImport, AssetInfo asset) {

        if (StringUtils.isEmpty(asset.getDepartNum())) {
            return false;
        }

        List<SearchFilter> assetFilters = new ArrayList<>();
        String orgUid = selectedOrgToImport.getUid();

        assetFilters.add(new SearchFilter("departNum", SearchFilter.Operator.EQ, asset.getDepartNum()));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, orgUid));

        if (null != asset.getUid()) {
            assetFilters.add(new SearchFilter("uid", SearchFilter.Operator.NE, asset.getUid()));
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

            asset.setCreatedBy(UserContextService.getCurrentUserAccount().getLoginName());
            asset.setCreatedDate(now);
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            asset.setLastModifiedDate(now);

            if (asset.getDeviceStatus() == null) {
                asset.setDeviceStatus(0);
            }

            //only new asset need to set this
            if(isNeedAudit){
                asset.setUsageStatus(2); //待审核
            } else {
                asset.setUsageStatus(3); //已建档
            }

        } else {
            //update
            asset.setLastModifiedBy(UserContextService.getCurrentUserAccount().getLoginName());
            asset.setLastModifiedDate(now);

            cleanRelatedFieldsWhenUpdating(asset);
        }
    }

    /**
     * check whether qrcode is new or changed
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

        String oldQrCode = (String)assetMap.get(OLD_QRCODE_KEY);

        if (assetInfo.getQrCode().equals(oldQrCode)) {
            return false;  //qrCode not changed
        }

        return true;
    }

    public void releaseOldQRCodeInUse(OrgInfo selectedOrgToImport, List<Map<String, Object>> importAssetMap) {

        importAssetMap.forEach(item -> {
            AssetInfo newAsset = (AssetInfo) item.get("asset");

            //if site uid not match, ignore update
            if (newAsset.getUid() != null) {
                if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                    return ;
                }

            }

            String oldQrCode = (String)item.get(OLD_QRCODE_KEY);
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

        });

    }

    /**
     * release qrcode only one asset
     * @param selectedOrgToImport
     * @param item
     */
    public void releaseOldQRCodeInUse(OrgInfo selectedOrgToImport, Map<String, Object> item) {

        AssetInfo newAsset = (AssetInfo) item.get("asset");

        //if site uid not match, ignore update
        if (newAsset.getUid() != null) {
            if (!selectedOrgToImport.getUid().equals(newAsset.getSiteUID())) {
                return ;
            }

        }

        String oldQrCode = (String)item.get(OLD_QRCODE_KEY);
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

    public void setAssetDeptId(AssetInfo assetInfo, OrgInfo selectedOrgToImport, Map<String, Object> item) {
        Map< String, Object> rowMap = (Map<String, Object>) item.get("rowData");
        String assetDeptName = ExcelDocument.getCellStringValue(rowMap.get("使用科室"));
        if (!StringUtils.isEmpty(assetDeptName)) {
            OrgInfo orgInfo = importOrgIfNeeded(selectedOrgToImport, assetDeptName);
            if (orgInfo != null) {
                assetInfo.setAssetDeptId(orgInfo.getId());
                assetInfo.setAssetDeptUID(orgInfo.getUid());
            }
        }
    }

    public String tryFormatQrCodeIfPossible(Map<String, ExcelDocument.ValueWrapper> rowMap, AssetInfo assetInfo) {
        String inputQrcode = ExcelDocument.getCellStringValue(rowMap.get("二维码"));
        if (!StringUtils.isEmpty(inputQrcode) && inputQrcode.length() > 16) {
            inputQrcode = inputQrcode.replace("-", "");
        }

        assetInfo.setQrCode(inputQrcode);

        return inputQrcode;
    }

}
