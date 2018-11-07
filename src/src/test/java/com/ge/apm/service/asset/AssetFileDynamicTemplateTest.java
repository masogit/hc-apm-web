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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {ExistingAccountTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileDynamicTemplateTest {

//    @Autowired
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
    public void setup() throws IOException {
        template = new AssetFileDynamicTemplate();
    }

    @Test
    public void writeMetaData() throws IOException {

        template.loadMetaDataAndApplyNamedRange();

        template.writeToFile(new File("./temp.xlsx"));
    }

    @Test
    public void exportAsset() throws IOException {

        CustomConfigObject customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        List<String> exportFields = customConfigService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());

//        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
//                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        List<OrgInfo> sites = orgInfoDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<AssetInfo> assetList = assetInfoDao.findByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        template.exportAsset(assetList, sites, clinicalDeptList, exportFields);

        template.writeToFile(new File("./asset_list.xlsx"));
    }

    @Test
    public void exportSampleAsset() throws IOException {

        CustomConfigObject customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        List<String> exportFields = customConfigService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());

//        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
//                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());

        template.exportSampleAsset(clinicalDeptList, exportFields);

        template.writeToFile(new File("./asset_sample.xlsx"));
    }


    @Test
    public void exportLargeAmountOfAsset() throws IOException {

        CustomConfigObject customConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        List<String> exportFields = customConfigService.getLocalizedExportableAssetFieldsByRoles(customConfig, UserContextService.getRoles());

//        List<String> exportColumnFields = Arrays.asList(AssetFileDynamicTemplate.columnTitles).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) ||
//                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());

        List<OrgInfo> clinicalDeptList = orgInfoDao.findByParentUID(UserContextService.getCurrentUserAccount().getSiteUID());
        List<OrgInfo> sites = orgInfoDao.getSitesByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        List<AssetInfo> assetList = assetInfoDao.findByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());

        int big_list = 21000;

        List<AssetInfo> bigList = new ArrayList<>(big_list);
        for (int i = 0; i < big_list; i++) {
            double random = Math.random() * assetList.size();
            int which = Double.valueOf(random).intValue();
            bigList.add(assetList.get(which));
        }

        template.exportAsset(bigList, sites, clinicalDeptList, exportFields);

        template.writeToFile(new File("./asset__big_list.xlsx"));
    }
}