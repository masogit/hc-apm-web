package com.ge.apm.mock;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetFileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
public class FakeData {

//    public static final Integer USER_ACCOUNT_DEFAULT_SITE_ID = -1;
//    public static final String USER_ACCOUNT_DEFAULT_TENANT_UID = "TENANT_UID_XXXXXXXXXX";
//    public static final String USER_ACCOUNT_DEFAULT_SITE_UID = "SITE_UID_XXXXXXXXXX";

//    @Autowired
//    AssetFileImportService service;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    UserAccountRepository userAccountDao;

    @Autowired
    TenantInfoRepository tenantInfoDao;

//    @Autowired
//    PlatformTransactionManager transactionManager;

//    TransactionTemplate transactionTemplate;

    TenantInfo tenantInfo;

    UserAccount userAccount;

    public void initTenantData() {

        List<TenantInfo> tenantInfoList = (List<TenantInfo>) tenantInfoDao.findAll();

        if (tenantInfoList.isEmpty()) {
            TenantInfo tenantInfo = new TenantInfo();
            tenantInfo.initializeUid();
            tenantInfo.setName("Tenant A");
            tenantInfoDao.save(tenantInfo);
        }

        tenantInfoList = (List<TenantInfo>) tenantInfoDao.findAll();

        if (tenantInfoList.isEmpty()) {
            throw new IllegalStateException("Tenant info is not built");
        }

        if (tenantInfoList.size() > 1) {
            throw new IllegalStateException("Tenant info is too much");
        }

        tenantInfo = tenantInfoList.get(0);

        initOrgTree();

        try {
            buildUserAccount();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private void initOrgTree() {

        OrgInfo orgTenantA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), tenantInfo.getName());
        if (orgTenantA == null) {
            orgTenantA = buildOrgTenant();
            orgInfoDao.save(orgTenantA);
            orgTenantA = orgInfoDao.getByUid(orgTenantA.getUid());

            if (orgTenantA == null) throw new IllegalStateException("Tenant A not found!");
        }

        String institutionA = "Institution A";
        OrgInfo orgInstitutionA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), institutionA);
        if (orgInstitutionA == null) {
            orgInstitutionA = buildOrgInstitution(institutionA);
            orgInfoDao.save(orgInstitutionA);
            orgInstitutionA = orgInfoDao.getByUid(orgInstitutionA.getUid());

            if (orgInstitutionA == null) throw new IllegalStateException("Institution A not found!");
        }

        String hospitalA = "Hospital A";
        OrgInfo orgHospitalA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), hospitalA);
        if (orgHospitalA == null) {
            orgHospitalA = buildOrgHospital(hospitalA, orgInstitutionA);
            orgInfoDao.save(orgHospitalA);
            orgHospitalA = orgInfoDao.getByUid(orgHospitalA.getUid());

            if (orgHospitalA == null) throw new IllegalStateException("Hospital A not found!");
        }

        String siteA = "Site A";
        OrgInfo orgSiteA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), siteA);
        if (orgSiteA == null) {
            orgSiteA = buildOrgSite(siteA, orgHospitalA);
            orgInfoDao.save(orgSiteA);
            orgSiteA = orgInfoDao.getByUid(orgSiteA.getUid());

            if (orgSiteA == null) throw new IllegalStateException("Site A not found!");
        }

        String clinicalDeptA = "Dept A";
        OrgInfo orgClinicalDeptA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), clinicalDeptA);
        if (orgClinicalDeptA == null) {
            orgClinicalDeptA = buildOrgClinicalDept(clinicalDeptA, orgSiteA);
            orgInfoDao.save(orgClinicalDeptA);
            orgClinicalDeptA = orgInfoDao.getByUid(orgClinicalDeptA.getUid());

            if (orgClinicalDeptA == null) throw new IllegalStateException("Dept A not found!");
        }

        String clinicalDeptB = "Dept B";
        OrgInfo orgClinicalDeptB = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), clinicalDeptB);
        if (orgClinicalDeptB == null) {
            orgClinicalDeptB = buildOrgClinicalDept(clinicalDeptB, orgSiteA);
            orgInfoDao.save(orgClinicalDeptB);
            orgClinicalDeptB = orgInfoDao.getByUid(orgClinicalDeptB.getUid());

            if (orgClinicalDeptB == null) throw new IllegalStateException("Dept B not found!");
        }

    }

    private OrgInfo buildOrgTenant() {

        OrgInfo orgInfo = new OrgInfo();
//        orgInfo.initializeUid();
        orgInfo.setUid(tenantInfo.getUid());

        orgInfo.setOrgLevel(0);
        orgInfo.setOrgType(0);
        orgInfo.setName(tenantInfo.getName());

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(tenantInfo.getUid());
        orgInfo.setHospitalUID(tenantInfo.getUid());
        orgInfo.setSiteUID(tenantInfo.getUid());
        orgInfo.setParentUID(null);

        return orgInfo;
    }

    private OrgInfo buildOrgInstitution(String name) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(1);
        orgInfo.setOrgType(1);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(orgInfo.getUid());
        orgInfo.setHospitalUID(orgInfo.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());
        orgInfo.setParentUID(tenantInfo.getUid());

        return orgInfo;
    }

    private OrgInfo buildOrgHospital(String name, OrgInfo parentOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(2);
        orgInfo.setOrgType(2);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(parentOrg.getUid());
        orgInfo.setHospitalUID(orgInfo.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());
        orgInfo.setParentUID(parentOrg.getUid());

        return orgInfo;
    }

    private OrgInfo buildOrgSite(String name, OrgInfo parentOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(3);
        orgInfo.setOrgType(3);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setTenantUID(tenantInfo.getUid());
        orgInfo.setInstitutionUID(parentOrg.getUid());
        orgInfo.setHospitalUID(parentOrg.getUid());
        orgInfo.setSiteUID(orgInfo.getUid());
        orgInfo.setParentUID(parentOrg.getUid());

        return orgInfo;
    }

    private OrgInfo buildOrgClinicalDept(String name, OrgInfo siteOrg) {

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.initializeUid();

        orgInfo.setOrgLevel(4);
        orgInfo.setOrgType(4);
        orgInfo.setName(name);

        orgInfo.setSiteId(tenantInfo.getId());
        orgInfo.setTenantUID(tenantInfo.getUid());

        orgInfo.setInstitutionUID(siteOrg.getUid());
        orgInfo.setHospitalUID(siteOrg.getUid());
        orgInfo.setSiteUID(siteOrg.getUid());
        orgInfo.setParentUID(siteOrg.getUid());

        return orgInfo;
    }

    private void buildUserAccount() throws NoSuchAlgorithmException {

        List<OrgInfo> orgInfoList = orgInfoDao.findByTenantUIDAndOrgType(tenantInfo.getUid(), 3);
        if (orgInfoList.isEmpty()) {
            throw new IllegalStateException("Specified org not found!");
        }
        OrgInfo orgInfo = orgInfoList.get(0);

        List<UserAccount> userAccountList = userAccountDao.getBySiteUID(orgInfo.getSiteUID());
        if (userAccountList.isEmpty()) {
            UserAccount userAccount = new UserAccount();
            userAccount.initializeUid();
            userAccount.setName("自动化测试人员");
            userAccount.setPlainPassword("test");
            userAccount.entryptPassword();

            userAccount.setLoginName("test");
            userAccount.setOrgInfoId(orgInfo.getId());
            userAccount.setOrgInfoUID(orgInfo.getUid());

            userAccount.setSiteId(orgInfo.getSiteId());
            userAccount.setTenantUID(orgInfo.getTenantUID());
            userAccount.setInstitutionUID(orgInfo.getInstitutionUID());
            userAccount.setHospitalUID(orgInfo.getHospitalUID());
            userAccount.setSiteUID(orgInfo.getSiteUID());

            userAccountDao.save(userAccount);
            userAccountList = userAccountDao.getBySiteUID(userAccount.getSiteUID());
        }

        this.userAccount = userAccountList.get(0);

    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

}
