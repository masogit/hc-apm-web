package com.ge.apm.service.asset;

import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ArrivalSupplierReportService {

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ArrivalSupplierReportService.class);

//    public static final String REPORT_ARRIVAL_SUPPLIER_TEMPLATE = "/portal/asset/arrived/reportTemplate/ArrivalSupplierReportTemplate.docx";

    public static final String REPORT_ARRIVAL_SUPPLIER_TITLE = "到货安装单";

    @Autowired
    private ArrivalListItemRepository arrivalListItemDao;


    public Map<String, Object> createReportData(ArrivalList arrivalList){

        Map<String, Object> arrivalMap = new HashMap<>();
        if(arrivalList != null && arrivalList.getId() != null){
            arrivalMap.put("asset_name", arrivalList.getAssetName());
            arrivalMap.put("installation_date", arrivalList.getBoxInstallationDate());
            arrivalMap.put("using_date", arrivalList.getUsingDate());
            arrivalMap.put("using_person", arrivalList.getUsingPerson());
            arrivalMap.put("using_person_tel", arrivalList.getUsingPersonTel());
            arrivalMap.put("contact_person", arrivalList.getContactPerson());
            arrivalMap.put("contact_person_tel", arrivalList.getContactPersonTel());
            arrivalMap.put("repairer", arrivalList.getRepairer());
            arrivalMap.put("repairer_tel", arrivalList.getRepairerTel());
            arrivalMap.put("training_info", arrivalList.getTrainingInfo());
            arrivalMap.put("contract_date", arrivalList.getContractDate());

            List<Map<String, Object>> arrivalItemList = new ArrayList<>();
            List<ArrivalListItem> arrivalListItemList = arrivalListItemDao.findByArrivalListId(arrivalList.getId());
            for (ArrivalListItem arrivalListItem : arrivalListItemList) {
                Map<String, Object> arrivalItemMap = new HashMap<>();

                arrivalItemMap.put("asset_name", arrivalList.getAssetName());
                arrivalItemMap.put("asset_group", arrivalList.getAssetGroup() != null? FieldValueMessageController.fieldValue("assetGroup", arrivalList.getAssetGroup().toString()) : "");
                arrivalItemMap.put("function_type", arrivalList.getFunctionType());
                arrivalItemMap.put("depart_num", arrivalListItem.getDepartNum());
                arrivalItemMap.put("serial_num", arrivalListItem.getSerialNum());
                arrivalItemMap.put("work_hours", arrivalListItem.getWorkHours());
                arrivalItemMap.put("location_name", arrivalListItem.getLocationName());

                arrivalItemList.add(arrivalItemMap);
            }
            arrivalMap.put("arrival_item", arrivalItemList);
        }

        return arrivalMap;
    }

    //导出一条记录
    public StreamedContent exportDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(data, templateBytes))), "application/msword", this.buildFileName(data, false));

        return streamedContent;
    }

    //导出多条记录
    public StreamedContent exportZip(List<Map<String, Object>> dataList, byte[] templateBytes) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderZip(dataList, templateBytes))), "application/zip", "Reports.zip");

        return streamedContent;
    }

    //构建导出模板
    public byte[] buildReportTemplate() throws IOException {

        byte[] templateBytes = null;
//        String templatePath = REPORT_ARRIVAL_SUPPLIER_TEMPLATE;
//
//        if(templatePath != null){
//            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templatePath));
//        }
//
        FileDownloadService fileDownloadService = WebUtil.getBean(FileDownloadService.class);

        InputStream  inputStream=fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.ARRIVAL_SUPPLIER_REPORT.getCode(), SystemTemplateEnum.ARRIVAL_SUPPLIER_REPORT.getFileType());
        if(inputStream != null){
            try {
                templateBytes = IOUtils.toByteArray(inputStream);
            } finally {
                inputStream.close();
            }
        }
        return templateBytes;
    }

    private byte[] renderZip(List<Map<String, Object>> dataList, byte[] templateBytes) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (ZipOutputStream zout = new ZipOutputStream(bout)) {
            for (Map<String, Object> data : dataList) {
                String fileName = buildFileName(data, false);
                zout.putNextEntry(new ZipEntry(fileName));
                //logger.info("Adding zip entry for: " + fileName);
                byte[] bytes = this.renderDocx(data, templateBytes);
                IOUtils.write(bytes, zout);
                zout.closeEntry();
            }
        }
        return bout.toByteArray();
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {

//        byte[] bytes;
//        XWPFTemplate template = null;
//        try {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            InputStream inputStream = new ByteArrayInputStream(templateBytes);
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("arrival_item", new ArrivalItemTableRenderPolicy());
//
//
//            Configure config = Configure.newBuilder()
//                    .bind("arrival_item", new ArrivalItemTableRenderPolicy())
//                    .build();
//            template = XWPFTemplate.compile(inputStream, config);
//            template.render(data);
//            template.write(out);
//            bytes = out.toByteArray();
//        }finally {
//            if (template != null) {
//                template.close();
//            }
//        }
//        return bytes;

        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("arrival_item", new ArrivalItemTableRenderPolicy());
        return WordExportServiceUtil.generate(data, templateBytes, bindings);
    }

    private String buildFileName(Map<String, Object> data, boolean isEncoding){

        String assetName = "";
        if(data.get("asset_name") != null){
            assetName = data.get("asset_name").toString();
            assetName = StringUtils.replace(assetName, "\\", "_");
            assetName = StringUtils.replace(assetName, "/", "_");
        }

        String reportTypeTitle = REPORT_ARRIVAL_SUPPLIER_TITLE;

        if(isEncoding){
            return this.encoding(reportTypeTitle) + "-" + this.encoding(assetName) + ".docx";
        }else {
            return reportTypeTitle + "-" + assetName + ".docx";
        }
    }

    private String encoding(String fileName) {
        try {
            return URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //return original name if the encode is failed.
            return fileName;
        }
    }
}
