package com.ge.apm.mock;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    @Autowired
    UserOrgInfoRepository userOrgInfoDao;

    @Autowired
    UserRoleRepository userRoleDao;

    @Autowired
    SysRoleRepository sysRoleDao;

    @Autowired
    I18nMessageRepository i18nDao;

    public static final String DEFAULT_SITE_NAME = "Site A";

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

            initI18nMessages();

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

//        String siteA = "Site A";
        OrgInfo orgSiteA = orgInfoDao.findByTenantUIDAndName(tenantInfo.getUid(), DEFAULT_SITE_NAME);
        if (orgSiteA == null) {
            orgSiteA = buildOrgSite(DEFAULT_SITE_NAME, orgHospitalA);
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

        UserAccount userAccount = userAccountDao.findByTenantUIDAndName(orgInfo.getTenantUID(), "自动化测试人员");
        if (userAccount == null) {
            userAccount = buildUserAccount("自动化测试人员", "tel-1", orgInfo);
            userAccountDao.save(userAccount);

            userAccount = userAccountDao.findByTenantUIDAndName(orgInfo.getTenantUID(), "自动化测试人员");
            assert userAccount != null;

        }
        this.userAccount = userAccount;

        userAccount = userAccountDao.findByTenantUIDAndName(orgInfo.getTenantUID(), "自动化测试人员2");
        if (userAccount == null) {
            userAccount = buildUserAccount("自动化测试人员2", "tel-2", orgInfo);
            userAccountDao.save(userAccount);
        }

        userAccount = userAccountDao.findByTenantUIDAndName(orgInfo.getTenantUID(), "自动化测试人员3");
        if (userAccount == null) {
            userAccount = buildUserAccount("自动化测试人员3", "tel-3", orgInfo);
            userAccountDao.save(userAccount);
        }

        Assert.notNull(userAccount.getId());


        List<UserOrgInfo> userOrgInfoList = userOrgInfoDao.findByUserUid(userAccount.getUid());
        if (userOrgInfoList.isEmpty()) {
            UserOrgInfo userOrgInfo = new UserOrgInfo();
            userOrgInfo.setUserUid(userAccount.getUid());
            userOrgInfo.setOrgType(orgInfo.getOrgType());
            userOrgInfo.setOrgUid(orgInfo.getUid());
            userOrgInfoDao.save(userOrgInfo);
        }

//        List<SysRole> sysRoleList = (List<SysRole>) sysRoleDao.findAll();
//        if (sysRoleList.isEmpty()) {
//            SysRole sysRole = new SysRole();
//            sysRole.setName("AssetStaff");
//        }
//
//        List<UserRole> userRoleList = userRoleDao.findByUserId(userAccount.getId());
//        if (userRoleList.isEmpty()) {
//            UserRole userRole = new UserRole();
//            userRole.setUserId(userAccount.getId());
//        }


    }

    private UserAccount buildUserAccount(String name, String tel, OrgInfo orgInfo) throws NoSuchAlgorithmException {
        UserAccount userAccount = new UserAccount();
        userAccount.initializeUid();
        userAccount.setName(name);
        userAccount.setTelephone(tel);
        userAccount.setPlainPassword("test");
        userAccount.entryptPassword();

        userAccount.setLoginName(name);
        userAccount.setOrgInfoId(orgInfo.getId());
        userAccount.setOrgInfoUID(orgInfo.getUid());

        userAccount.setSiteId(orgInfo.getSiteId());
        userAccount.setTenantUID(orgInfo.getTenantUID());
        userAccount.setInstitutionUID(orgInfo.getInstitutionUID());
        userAccount.setHospitalUID(orgInfo.getHospitalUID());
        userAccount.setSiteUID(orgInfo.getSiteUID());

        return userAccount;
    }

    private void initI18nMessages() {

        List<String[]> messages = new ArrayList<>();
        messages.add(new String[]{"assetFunctionType", "6801", "医疗器械分类A"});
        messages.add(new String[]{"assetFunctionType", "6802", "医疗器械分类B"});
        messages.add(new String[]{"6801", "1", "医疗器械子分类A"});
        messages.add(new String[]{"6801", "2", "医疗器械子分类B"});
        messages.add(new String[]{"assetGroup", "1", "设备分类A"});
        messages.add(new String[]{"assetGroup", "2", "设备分类B"});
        messages.add(new String[]{"depreciationMethodList", "1", "折旧算法A"});
        messages.add(new String[]{"depreciationMethodList", "2", "折旧算法B"});
        messages.add(new String[]{"assetUsage", "1", "用途分类A"});
        messages.add(new String[]{"assetUsage", "2", "用途分类B"});
        messages.add(new String[]{"capitalSource", "1", "资金来源A"});
        messages.add(new String[]{"capitalSource", "2", "资金来源B"});
        messages.add(new String[]{"riskGrade", "1", "风险等级A"});
        messages.add(new String[]{"riskGrade", "2", "风险等级B"});
        messages.add(new String[]{"measureGrade", "1", "计量等级A"});
        messages.add(new String[]{"measureGrade", "2", "计量等级B"});
        messages.add(new String[]{"measuringItemCategory", "1", "计量项目类别A"});
        messages.add(new String[]{"measuringItemCategory", "2", "计量项目类别B"});

        messages.add(new String[]{"emergencyEquipment", "1", "是否急救设备A"});
        messages.add(new String[]{"emergencyEquipment", "2", "是否急救设备B"});
        messages.add(new String[]{"importEquipment", "1", "是否进口设备A"});
        messages.add(new String[]{"importEquipment", "2", "是否进口设备B"});
        messages.add(new String[]{"usageStatus", "1", "使用状态A"});
        messages.add(new String[]{"usageStatus", "2", "使用状态B"});
        messages.add(new String[]{"accountingCategory", "1", "核算类别A"});
        messages.add(new String[]{"accountingCategory", "2", "核算类别B"});


        for (String[] element: messages) {
            I18nMessage message = i18nDao.getByMsgTypeAndMsgKey(element[0], element[1]);
            if (message == null) {
                message = new I18nMessage(element[0], element[1], element[2]);
                i18nDao.save(message);
            }
        }


    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

}
