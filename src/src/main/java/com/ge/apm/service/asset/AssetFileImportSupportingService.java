package com.ge.apm.service.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.orginfo.OrgInfoBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AssetFileImportSupportingService {

    @Autowired
    private UserAccountRepository userDao;

    @Autowired
    private SupplierRepository supplierDao;

    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private OrgInfoRepository orgDao;

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

}
