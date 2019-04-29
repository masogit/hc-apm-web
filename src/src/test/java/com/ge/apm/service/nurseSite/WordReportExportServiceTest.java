package com.ge.apm.service.nurseSite;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.service.asset.BlobFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class WordReportExportServiceTest {

    @Spy
    WordReportExportService service = new WordReportExportService();

//    @Mock
//    BlobFileService blobFileService;

    @Before
    public void setup() throws IOException {
        FileInputStream inputStream = new FileInputStream("d:\\test.png");
        StreamedContent content = new DefaultStreamedContent(inputStream);
        byte[] bytes = IOUtils.toByteArray(content.getStream());
        doReturn(bytes).when(service).getSignature(anyString());
    }


    @Test
    public void testExport() throws IOException {
        HashMap<String, Object> datas = new HashMap<>();
        Map<Object, Object> tables = new TreeMap<>();

        tables.put("2019-01-01", mockItemList(new int[]{1, 2, 3}, 2));
        tables.put("2019-01-01", mockItemList(new int[]{1, 2, 3}, 3));
        tables.put("2019-01-02", mockItemList(new int[]{1, 2, 3, 4}, 3));
        tables.put("2019-01-03", mockItemList(new int[]{}, 3));
        tables.put("2019-01-04", mockItemList(new int[]{3, 4}, 3));
        tables.put("2019-01-05", mockItemList(new int[]{2, 3, 4}, 3));
        datas.put("tables", tables);
        datas.put("date1", "2019-01-01");
        datas.put("date2", "2019-01-02");
        datas.put("date3", "2019-01-03");
        datas.put("date4", "2019-01-04");
        datas.put("date5", "2019-01-05");
        byte[] export = service.export(IOUtils.toByteArray(new FileInputStream("src\\main\\webapp\\portal\\nurseSite\\template.docx")), datas);

        FileUtils.writeByteArrayToFile(new File("D:\\result.docx"), export);
        assertEquals(1, 1);
    }

    public List<NurseSiteMaintainItem> mockItemList(int[] index, int status) {
        List<NurseSiteMaintainItem> list = new ArrayList<>();
        for (int i : index) {
            list.add(mockNurseSiteMaintainItem(i, status));
        }
        return list;
    }

    public NurseSiteMaintainItem mockNurseSiteMaintainItem(int index, int status) {
        NurseSiteMaintainItem item = new NurseSiteMaintainItem();
        item.setAssetInfo(mockAssetInfo(index));
        ArrayList<NurseSiteMaintainData> listData = new ArrayList<>();
        listData.add(mockNurseSiteMaintainData(1));
        listData.add(mockNurseSiteMaintainData(2));
        item.setSignatureId("111");
        item.setSignatureSourceType(1);
        item.setStatus(status);
        item.setListData(listData);
        return item;
    }

    public NurseSiteMaintainData mockNurseSiteMaintainData(int index) {
        NurseSiteMaintainData nurseSiteMaintainData = new NurseSiteMaintainData();
        nurseSiteMaintainData.setCheckItem("ck" + index);
        nurseSiteMaintainData.setId("" + index);
        return nurseSiteMaintainData;
    }

    public AssetInfo mockAssetInfo(Integer index) {
        AssetInfo assetInfo = mock(AssetInfo.class);
        doReturn("" + index).when(assetInfo).getUid();
        doReturn("name" + index).when(assetInfo).getName();
        doReturn("departNum" + index).when(assetInfo).getDepartNum();
        return assetInfo;
    }

    @Test
    public void testZip() throws IOException {
        File file1 = new File("d:\\template.docx");
        File file2 = new File("d:\\result.docx");
        List<byte[]> bytes = new ArrayList<>();
        bytes.add(FileUtils.readFileToByteArray(file1));
        bytes.add(FileUtils.readFileToByteArray(file2));

        byte[] zip = service.zip(bytes);
        FileUtils.writeByteArrayToFile(new File("d:\\myzip.zip"), zip);
    }


}