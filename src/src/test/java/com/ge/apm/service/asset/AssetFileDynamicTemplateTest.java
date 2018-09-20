package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.mock.ExistingAccountTestExecutionListener;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.view.sysutil.UserContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

    }

    @Test
    public void writeMetaData() throws IOException {

        template.buildMetaData();

        template.writeToFile(new File("./temp.xlsx"));
    }

//    @Ignore
    @Test
    public void writeAsset() throws IOException {

        CustomConfigObject customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        List<String> exportFields = customConfigService.getExportAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        List<OrgInfo> sites = orgInfoDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<AssetInfo> assetList = assetInfoDao.findByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        template.buildMetaData();

        template.applyFormula(exportColumnFields);

        template.writeAsset(assetList, sites, clinicalDeptList, exportColumnFields);

        template.writeToFile(new File("./temp.xlsx"));
    }


    @Test
    public void writeClinicalDeptList() throws IOException {

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());

        template.writeClinicalDeptList(clinicalDeptList);

        template.writeToFile(new File("./temp.xlsx"));
    }
}