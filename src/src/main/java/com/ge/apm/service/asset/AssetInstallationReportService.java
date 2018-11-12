package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.ArrivalListItem;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 212605082 on 2018/11/5.
 */
@Component
public class AssetInstallationReportService {

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AssetInstallationReportService.class);

    public static final String REPORT_TECHNOLOGY_TEMPLATE = "/portal/asset/arrived/reportTemplate/TechnologyReportTemplate.docx";
    public static final String REPORT_BUSINESS_TEMPLATE = "/portal/asset/arrived/reportTemplate/BusinessReportTemplate.docx";
    public static final String REPORT_CLINICAL_TEMPLATE = "/portal/asset/arrived/reportTemplate/ClinicalReportTemplate.docx";

    public static final String REPORT_TECHNOLOGY_TITLE = "技术验收报告";
    public static final String REPORT_BUSINESS_TITLE = "商务验收报告";
    public static final String REPORT_CLINICAL_TITLE = "临床验收报告";

    public static final String REPORT_TYPE_TECHNOLOGY = "technology";
    public static final String REPORT_TYPE_BUSINESS = "business";
    public static final String REPORT_TYPE_CLINICAL = "clinical";

    @Autowired
    private OrgInfoRepository orgInfoDao;

    public Map<String, Object> createReportData(ArrivalListItem arrivalListItem){

        Map<String, Object> reportData = new HashedMap();

        reportData.put("asset_name", arrivalListItem.getArrivalList().getAssetName());//设备名称
        reportData.put("financing_num", arrivalListItem.getFinancingNum());//设备财产编号
        reportData.put("function_type", arrivalListItem.getArrivalList().getFunctionType());//型号规格
        reportData.put("manufact_num", "");//出厂编号
        reportData.put("clinical_dept_name", orgInfoDao.findByUid(arrivalListItem.getClinicalDeptUID()));//设备所属科室

        //技术验收
        reportData.put("technical_third_party_name", "");//第三方检查单位
        reportData.put("technical_check_date", "");//检测日期
        reportData.put("technical_ack_name", "");//验收人/经办人

        return reportData;
    }

    public List<Map<String, Object>> createReportDataList(List<ArrivalListItem> arrivalListItemList){

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (ArrivalListItem arrivalListItem : arrivalListItemList) {
            dataList.add(this.createReportData(arrivalListItem));
        }

        return dataList;
    }

    /*public StreamedContent exportDocx1(List<Map<String, Object>> dataList, String reportType) throws IOException {

        StreamedContent streamedContent = null;
        byte[] templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/arrived/reportTemplate/template.docx"));

        if(dataList.size() == 1){
            streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(dataList.get(0), templateBytes))), "application/msword", this.buildFileName(dataList.get(0), reportType));
        }else {
            streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderZip(dataList, templateBytes, reportType))), "application/zip", "Reports.zip");
        }

        return streamedContent;
    }*/

    //导出一条记录
    public StreamedContent exportDocx(Map<String, Object> data, byte[] templateBytes, String reportType) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(data, templateBytes))), "application/msword", this.buildFileName(data, reportType, true));

        return streamedContent;
    }

    //导出多条记录
    public StreamedContent exportZip(List<Map<String, Object>> dataList, byte[] templateBytes, String reportType) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderZip(dataList, templateBytes, reportType))), "application/zip", "Reports.zip");

        return streamedContent;
    }

    //构建导出模板
    public byte[] buildReportTemplate(String reportType) throws IOException {

        byte[] templateBytes = null;
        String templatePath = null;

        if(REPORT_TYPE_TECHNOLOGY.equals(reportType)){
            templatePath = REPORT_TECHNOLOGY_TEMPLATE;
        }else if(REPORT_TYPE_BUSINESS.equals(reportType)){
            templatePath = REPORT_BUSINESS_TEMPLATE;
        }else if(REPORT_TYPE_CLINICAL.equals(reportType)){
            templatePath = REPORT_CLINICAL_TEMPLATE;
        }

        if(templatePath != null){
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templatePath));
        }

        return templateBytes;
    }

    private byte[] renderZip(List<Map<String, Object>> dataList, byte[] templateBytes, String reportType) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);
        for (Map<String, Object> data : dataList) {
            String fileName = buildFileName(data, reportType, false);
            zout.putNextEntry(new ZipEntry(fileName));
            //logger.info("Adding zip entry for: " + fileName);
            byte[] bytes = this.renderDocx(data, templateBytes);
            IOUtils.write(bytes, zout);
            zout.closeEntry();
        }

        zout.close();
        return bout.toByteArray();
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {

        byte[] bytes;
        XWPFTemplate template = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = new ByteArrayInputStream(templateBytes);
            template = XWPFTemplate.compile(inputStream);

            template.render(data);
            template.write(out);
            bytes = out.toByteArray();
        }finally {
            if (template != null) {
                template.close();
            }
        }
        return bytes;
    }

    private String buildFileName(Map<String, Object> data, String reportType, boolean isEncoding){

        String assetName = data.get("asset_name").toString();
        assetName = StringUtils.replace(assetName, "\\", "-");
        assetName = StringUtils.replace(assetName, "/", "-");

        String financingNum = data.get("financing_num").toString();
        assetName = StringUtils.replace(assetName, "\\", "-");
        assetName = StringUtils.replace(assetName, "/", "-");

        String reportTypeTitle = "";
        if(REPORT_TYPE_TECHNOLOGY.equals(reportType)){
            reportTypeTitle = REPORT_TECHNOLOGY_TITLE;
        }else if(REPORT_TYPE_BUSINESS.equals(reportType)){
            reportTypeTitle = REPORT_BUSINESS_TITLE;
        }else if(REPORT_TYPE_CLINICAL.equals(reportType)){
            reportTypeTitle = REPORT_CLINICAL_TITLE;
        }

        if(isEncoding){
            return this.encoding(reportTypeTitle) + "-" + this.encoding(assetName) + "[" + this.encoding(financingNum) + "].docx";
        }else {
            return reportTypeTitle + "-" + assetName + "[" + financingNum + "].docx";
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
