package com.ge.apm.service.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.orginfo.OrgInfoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssetFileImportSupportingService {

    public static final String ASSET_CODE_MERGE_THREE = "DFQ";
    public static final String ASSET_CODE_MERGE_TWO = "DF";

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

    public OrgInfo importOrgIfNeeded(OrgInfo selectedOrgToImport, String name) {

        OrgInfo orgInfo = orgDao.findFirstByParentUIDAndName(selectedOrgToImport.getUid(), name);

        if (orgInfo == null) {
            TenantInfo tenantInfo = tenantInfoDao.findByuid(selectedOrgToImport.getTenantUID());
            orgInfo = OrgInfoBuilder.buildOrgClinicalDept(tenantInfo, name, selectedOrgToImport, selectedOrgToImport.getOrgLevel());
            orgDao.save(orgInfo);
        }

        return orgInfo;

    }

    public Supplier importSupplierIfNeeded(OrgInfo selectedOrgToImport, String name, String tel) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setSupplierT(true);
            supplier.setSiteId(selectedOrgToImport.getSiteId());
            supplier.setName(name);
            supplier.setTel(tel);
            supplier.setTenantUID(selectedOrgToImport.getTenantUID());
            supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
            supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            supplier.setSiteUID(selectedOrgToImport.getSiteUID());
            supplierDao.save(supplier);
        }

        if (supplier.getSupplierT() == null || !supplier.getSupplierT()) {
            supplier.setSupplierT(true);
            supplierDao.save(supplier);
        }

        return supplier;

    }

    public Supplier importMaintainancerIfNeeded(OrgInfo selectedOrgToImport, String name, String tel) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setMaintainerT(true);
            supplier.setSiteId(selectedOrgToImport.getSiteId());
            supplier.setName(name);
            supplier.setTel(tel);
            supplier.setTenantUID(selectedOrgToImport.getTenantUID());
            supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
            supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            supplier.setSiteUID(selectedOrgToImport.getSiteUID());
            supplierDao.save(supplier);
        }

        if (supplier.getMaintainerT() == null || !supplier.getMaintainerT() || (!StringUtils.isEmpty(tel) && !tel.equals(supplier.getTel()))) {
            supplier.setMaintainerT(true);
            if (!StringUtils.isEmpty(tel)) {
                supplier.setTel(tel);
            }

            supplierDao.save(supplier);
        }

        return supplier;

    }

    public Supplier importManufacturerIfNeeded(OrgInfo selectedOrgToImport, String name) {

        Supplier supplier = supplierDao.findFirstByTenantUIDAndName(selectedOrgToImport.getTenantUID(), name);
        if (supplier == null) {
            supplier = new Supplier();
            supplier.setManufacturerT(true);
            supplier.setSiteId(selectedOrgToImport.getSiteId());
            supplier.setName(name);
            supplier.setTenantUID(selectedOrgToImport.getTenantUID());
            supplier.setHospitalUID(selectedOrgToImport.getHospitalUID());
            supplier.setInstitutionUID(selectedOrgToImport.getInstitutionUID());
            supplier.setSiteUID(selectedOrgToImport.getSiteUID());
            supplierDao.save(supplier);
        }

        if (supplier.getManufacturerT() == null || !supplier.getManufacturerT()) {
            supplier.setManufacturerT(true);
            supplierDao.save(supplier);
        }

        return supplier;

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
            }
        }

        return false;
    }

}
