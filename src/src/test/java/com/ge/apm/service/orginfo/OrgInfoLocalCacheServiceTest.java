package com.ge.apm.service.orginfo;


import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.mock.ExistingAccountTestExecutionListener;
import com.ge.apm.mock.FakeData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {ExistingAccountTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class OrgInfoLocalCacheServiceTest {

    @Autowired
    OrgInfoLocalCacheService orgInfoLocalCacheService;

    @Autowired
    TenantInfoRepository tenantInfoDao;

    @Autowired
    FakeData fakeData;

    @Before
    public void setUp() throws Exception {
        fakeData.initTenantData();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFlatterOrgHierachy() {
        Optional<TenantInfo> tenantInfo = tenantInfoDao.findFirstByName("Tenant A");
        Map<String, OrgInfo> orgNamesCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(tenantInfo.get().getUid());
        assertNotNull(orgNamesCache.get(OrgInfoLocalCacheService.TENANT_ORG_KEY));

    }

    @Test
    public void findDeptByFlatternedMap() {
        Optional<TenantInfo> tenantInfo = tenantInfoDao.findFirstByName("Tenant A");
        Map orgNamesCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(tenantInfo.get().getUid());

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = orgInfoLocalCacheService.
                findSiteOrgByFlatternedMap(orgNamesCache, "Institution A", "Hospital A", "Site A", "Dept A", "Dept B");
        assertEquals("Site A", siteOrgWithDeptOrg.getSiteOrg().getShortName());

        assertEquals("Dept A", siteOrgWithDeptOrg.getClinicalDeptOrg().get().getShortName());

        assertEquals("Dept B", siteOrgWithDeptOrg.getAssetDeptOrg().get().getShortName());
    }

    @Test
    public void testNotFoundClinicalDept() {
        Optional<TenantInfo> tenantInfo = tenantInfoDao.findFirstByName("Tenant A");
        Map orgNamesCache = orgInfoLocalCacheService.flatternLocalOrgNamesCache(tenantInfo.get().getUid());

        SiteOrgWithDeptOrg siteOrgWithDeptOrg = orgInfoLocalCacheService.
                findSiteOrgByFlatternedMap(orgNamesCache, "Institution A", "Hospital A", "Site A", "Dept C", null);
        assertEquals("Site A", siteOrgWithDeptOrg.getSiteOrg().getShortName());

        assertFalse("Dept C", siteOrgWithDeptOrg.getClinicalDeptOrg().isPresent());
    }
}
