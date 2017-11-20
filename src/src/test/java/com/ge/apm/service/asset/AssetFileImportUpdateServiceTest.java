package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/config/applicationContext.xml"})
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

    @Test(expected = EntityNotFoundException.class)
    public void checkExcelData_ID_Not_Exists() throws Exception {

        Map<String, Object> record = new HashMap<>();
        AssetInfo assetInfo = service.checkExcelRowData(record, 0);

    }


}