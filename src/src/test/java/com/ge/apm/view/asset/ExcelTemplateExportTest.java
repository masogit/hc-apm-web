package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class ExcelTemplateExportTest implements ServletContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ExcelTemplateExportTest.class);

    ServletContext servletContext;

    @Autowired
    ExcelTemplateExport export;

    @Autowired
    AssetInfoRepository assetInfoDao;

    @Test
    public void loadTemplate() throws MalformedURLException {

        Pageable pageable = new PageRequest(0, 10);

        List<AssetInfo> assetList = assetInfoDao.findAll(pageable).getContent();
        Map<String, Object> context = new HashMap<>();
        context.put("assetList", assetList);

        export.exportExcel(context);

        assertNotNull(export.getExportFile());

        logger.info(export.getExportFile().getName());

    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}