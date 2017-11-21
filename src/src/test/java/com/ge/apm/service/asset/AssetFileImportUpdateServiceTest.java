package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class AssetFileImportUpdateServiceTest {

    @Autowired
    AssetFileImportUpdateService service;

    @Before
    public void setUp() throws Exception {

//        FacesContext mockFacesContext = Mockito.mock(FacesContext.class);
//        Method method = FacesContext.class.getDeclaredMethod("setCurrentInstance");
//        ReflectionUtils.makeAccessible(method);
//        method.invoke(null, mockFacesContext);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore
    @Test
    public void updateAll() throws Exception {
    }

    @Ignore
    @Test
    public void clearBeforeUpdateAll() throws Exception {
    }

    @Test(expected = Exception.class)
    public void checkExcelData_ID_Column_Not_Exists() throws Exception {

        Map<String, Object> record = new HashMap<>();
        service.checkExcelRowData(record, 0);

    }

    @Test(expected = EntityNotFoundException.class)
    public void checkExcelData_ID_Value_Not_Exists() throws Exception {

        Map<String, Object> record = new HashMap<>();
        record.put("ID", "111");
        AssetInfo assetInfo = service.checkExcelRowData(record, 0);

    }


}