package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.mock.ExistingAccountTestExecutionListener;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {ExistingAccountTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileDynamicTemplateTest {

    @Autowired
    AssetFileDynamicTemplate template;

    @Autowired
    UserAccountRepository userAccountDao;

    @Autowired
    CustomConfigService customConfigService;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Before
    public void setup() {
//        UserAccount userAccount = userAccountDao.getByLoginName("tq-clinicals");
//        UserContextService.setLoginUser(userAccount);
    }

    @Test
    public void buildMetaData() throws IOException {

        CustomConfigObject customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        List<String> readFields = customConfigService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
        List<String> customedColumns = customConfig.getAssetsListPageFields().stream().filter(item -> readFields.contains(item)).collect(Collectors.toList());

        List<String> exportFields = customConfigService.getExportAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        List<OrgInfo> sites = orgInfoDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<AssetInfo> assetList = assetInfoDao.findByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        InputStream inputStream = AssetFileDynamicTemplateTest.class.getClassLoader().getResourceAsStream("asset/template/APM_Asset_Dynamic_Template.xlsx");
        SXSSFWorkbook workBook = new SXSSFWorkbook(new XSSFWorkbook(inputStream));

        template.buildMetaData(workBook);

    }
}