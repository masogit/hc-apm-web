package com.ge.apm.mock;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.asset.AssetFileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Component
public class FakeData {

    public static final Integer USER_ACCOUNT_DEFAULT_SITE_ID = -1;
    public static final String USER_ACCOUNT_DEFAULT_TENANT_UID = "TENANT_UID_XXXXXXXXXX";
    public static final String USER_ACCOUNT_DEFAULT_SITE_UID = "SITE_UID_XXXXXXXXXX";

    @Autowired
    AssetFileImportService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    TenantInfoRepository tenantInfoDao;

    @Autowired
    PlatformTransactionManager transactionManager;

    TransactionTemplate transactionTemplate;

    public void initTenantData() {

        List<TenantInfo> tenantInfoList = (List<TenantInfo>) tenantInfoDao.findAll();

        if (tenantInfoList.isEmpty()) {
            TenantInfo tenantInfo = new TenantInfo();
            tenantInfo.setName("Demo tenant");
            tenantInfo.setUid(USER_ACCOUNT_DEFAULT_TENANT_UID);
            tenantInfoDao.save(tenantInfo);
        }

        tenantInfoList = (List<TenantInfo>) tenantInfoDao.findAll();

        if (tenantInfoList.isEmpty()) {
            throw new IllegalStateException("Tenant info is not built");
        }

        TenantInfo tenantInfo = tenantInfoList.get(0);

        OrgInfo orgInfoInstitution = new OrgInfo();
        orgInfoInstitution.setSiteId(tenantInfo.getId());
        orgInfoInstitution.setOrgLevel(1);
        orgInfoInstitution.setOrgType(1);
        orgInfoInstitution.setName("XXXX Hospital Group");
        orgInfoInstitution.setUid("HG-xxxxxxx");
        orgInfoInstitution.setTenantUID(tenantInfo.getUid());
        orgInfoInstitution.setInstitutionUID(orgInfoInstitution.getUid());
        orgInfoInstitution.setHospitalUID(orgInfoInstitution.getUid());
        orgInfoInstitution.setSiteUID(orgInfoInstitution.getUid());
        orgInfoInstitution.setParentUID(tenantInfo.getUid());

        orgInfoDao.save(orgInfoInstitution);







    }

}
