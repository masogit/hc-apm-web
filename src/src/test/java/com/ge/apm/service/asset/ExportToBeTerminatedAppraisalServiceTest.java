package com.ge.apm.service.asset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.model.StreamedContent;

import java.io.*;
import java.util.*;

import static com.ge.apm.service.asset.ExportToBeTerminatedAppraisalService.ASSET_DETAIL.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ExportToBeTerminatedAppraisalServiceTest {

    @Spy
    ExportToBeTerminatedAppraisalService service;

    @Test
    public void testExport() throws IOException {

        Map<ExportToBeTerminatedAppraisalService.ASSET_DETAIL, Object> map = new HashMap<>();
        map.put(financing_num, "assetInfo.getFinancingNum()");
        map.put(asset_name, "assetInfo.getName()");
        map.put(function_type, "assetInfo.getFunctionType()");
        map.put(install_date, new Date().toString());
        map.put(manufacture, "assetInfo.getManufacture()");
        map.put(vendor, "assetInfo.getVendor()");
        map.put(maintenance, "assetInfo.getMaitanance()");
        map.put(national_standard_code, "assetInfo.getNationalStandardCode()");
        map.put(serial_num, "assetInfo.getSerialNum()");
        map.put(depart_num, "assetInfo.getDepartNum()");
        map.put(system_id, "assetInfo.getSystemId()");
        map.put(purchase_price, "assetInfo.getPurchasePrice()");
        map.put(purchase_date, "assetInfo.getPurchaseDate()");
        List<Map<ExportToBeTerminatedAppraisalService.ASSET_DETAIL, Object>> list = new ArrayList<>();
        list.add(map);
        Map<String, Object> datas = new HashMap<>();
        datas.put("asset_detail_table", list);

        doReturn(datas).when(service).getData(any());

        StreamedContent export = service.export(new ArrayList<>(), IOUtils.toByteArray(new FileInputStream("d:\\1.docx")));
        FileUtils.writeByteArrayToFile(new File("d:\\test.docx"), IOUtils.toByteArray(export.getStream()));
    }


    @Test
    public void testGetAssets() throws Exception {
        service.getAssetInfo(new FileInputStream("d:\\APM_Asset_Template.xlsx"), null, new HashSet<>());
    }

}