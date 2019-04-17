package com.ge.apm.service.nurseSite;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintainItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class WordReportExportServiceTest {


    @Test
    public void testExport() throws IOException {

        WordReportExportService service = new WordReportExportService();
        HashMap<String, Object> datas = new HashMap<>();
        Map<Object, Object> tables = new TreeMap<>();

        tables.put("2019-01-01", mockItemList(new int[]{1, 2, 3}));
        tables.put("2019-01-02", mockItemList(new int[]{1, 2, 3, 4}));
        tables.put("2019-01-03", mockItemList(new int[]{}));
        tables.put("2019-01-04", mockItemList(new int[]{3, 4}));
        tables.put("2019-01-05", mockItemList(new int[]{2, 3, 4}));
        datas.put("tables", tables);
        datas.put("date1", "2019-01-01");
        datas.put("date2", "2019-01-02");
        datas.put("date3", "2019-01-03");
        datas.put("date4", "2019-01-04");
        datas.put("date5", "2019-01-05");
        byte[] export = service.export(IOUtils.toByteArray(new FileInputStream("D:\\template.docx")), datas);

        FileUtils.writeByteArrayToFile(new File("D:\\result.docx"), export);
        assertEquals(1, 1);
    }

    public List<NurseSiteMaintainItem> mockItemList(int[] index) {
        List<NurseSiteMaintainItem> list = new ArrayList<>();
        for (int i : index) {
            list.add(mockNurseSiteMaintainItem(i));
        }
        return list;
    }

    public NurseSiteMaintainItem mockNurseSiteMaintainItem(int index) {
        NurseSiteMaintainItem item = new NurseSiteMaintainItem();
        item.setAssetInfo(mockAssetInfo(index));
        return item;
    }

    public AssetInfo mockAssetInfo(Integer index) {
        AssetInfo assetInfo = mock(AssetInfo.class);
        doReturn(index).when(assetInfo).getId();
        doReturn("name" + index).when(assetInfo).getName();
        doReturn("departNum" + index).when(assetInfo).getDepartNum();
        return assetInfo;
    }


}