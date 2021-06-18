package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.service.wo.doctemplate.PartTableRenderPolicy;
import com.ge.apm.view.sysutil.FieldValueMessageController;
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
import java.util.HashMap;
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
    public static final String REPORT_ASSET_TEMPLATE = "/portal/asset/arrived/reportTemplate/AssetReportTemplate.docx";
    public static final String REPORT_ASSET_TEMPLATE_BATCH = "/portal/asset/arrived/reportTemplate/AssetReportTemplateBatch.docx";
    public static final String REPORT_ASSET_TEMPLATE_BATCH_BOTTOM = "/portal/asset/arrived/reportTemplate/AssetReportTemplateBatchBottom.docx";

    public static final String REPORT_TECHNOLOGY_TITLE = "技术验收报告";
    public static final String REPORT_BUSINESS_TITLE = "商务验收报告";
    public static final String REPORT_CLINICAL_TITLE = "临床验收报告";
    public static final String REPORT_ASSET_TITLE = "固定资产验收单";
    public static final String REPORT_ASSET_TITLE_BATCH = "固定资产验收单(面单)";
    public static final String REPORT_ASSET_TITLE_BATCH_BOTTOM = "固定资产验收单(底单)";

    public static final String REPORT_TYPE_TECHNOLOGY = "technology";
    public static final String REPORT_TYPE_BUSINESS = "business";
    public static final String REPORT_TYPE_CLINICAL = "clinical";
    public static final String REPORT_TYPE_ASSET = "fixasset";
    public static final String REPORT_TYPE_ASSET_BATCH = "fixassetBatch";
    public static final String REPORT_TYPE_ASSET_BATCH_BOTTOM = "fixassetBatchBottom";

    @Autowired
    private OrgInfoRepository orgInfoDao;

    public Map<String, Object> createReportData(ArrivalListItem arrivalListItem){


        Map<String, Object> reportData = new HashedMap();

        reportData.put("asset_name", arrivalListItem.getArrivalList().getAssetName());//设备名称
        reportData.put("financing_num", arrivalListItem.getFinancingNum());//设备财产编号
        reportData.put("function_type", arrivalListItem.getFunctionType());//型号规格
        reportData.put("brand", arrivalListItem.getArrivalList().getBrand());//品牌
        reportData.put("manufact_num", arrivalListItem.getSerialNum());//出厂编号
        reportData.put("clinical_dept_name", orgInfoDao.findByUid(arrivalListItem.getClinicalDeptUID()));//设备所属科室
        reportData.put("import_equipment", arrivalListItem.getImportEquipment()==null?"":FieldValueMessageController.doGetFieldValue("importEquipment",arrivalListItem.getImportEquipment()+""));//是否进口设备[1:进口,2:国产]
        reportData.put("item_num", 1);//数量
        reportData.put("guarantee", arrivalListItem.getGuarantee()==null?"":arrivalListItem.getGuarantee());
        reportData.put("annual_maintenance_times", arrivalListItem.getAnnualMaintenanceTimes());//年保修次数
        reportData.put("hospital_num", arrivalListItem.getDepartNum());//医院编号
        reportData.put("receipt_number", arrivalListItem.getArrivalList().getReceiptNumber());//入库单号
        reportData.put("contract_num", arrivalListItem.getArrivalList().getContractNum());//合同号
        reportData.put("invoice_num", arrivalListItem.getArrivalList().getInvoiceNum());//发票号
        reportData.put("contract_price", arrivalListItem.getArrivalList().getContractPrice());//合同价格
        reportData.put("contract_qua", arrivalListItem.getArrivalList().getQuantity());//数量
        reportData.put("manufacture", arrivalListItem.getArrivalList().getManufacture());//生产厂家

        reportData.put("purchase_price", arrivalListItem.getPurchasePrice());

        //技术验收
        reportData.put("technical_third_party_name", "");//第三方检查单位
        reportData.put("technical_check_date", "");//检测日期
        reportData.put("technical_ack_name", "");//验收人/经办人

        //固定资产验收单
        reportData.put("supplier_name", arrivalListItem.getArrivalList().getVendor());//供应商
        reportData.put("contract_date", arrivalListItem.getArrivalList().getContractDate());//合同签订日期
        reportData.put("foreign_trade_agreements", arrivalListItem.getArrivalList().getForeignTradeAgreements());//外贸协议
        reportData.put("box_installation_date", arrivalListItem.getArrivalList().getBoxInstallationDate());//开箱安装日期
        reportData.put("using_date", arrivalListItem.getArrivalList().getUsingDate());//使用日期
        reportData.put("using_person", arrivalListItem.getUsingPerson());//使用人
        reportData.put("using_person_tel", arrivalListItem.getUsingPersonTel());//使用人电话
        reportData.put("contact_person", arrivalListItem.getArrivalList().getContactPerson());//联系人
        reportData.put("contact_person_tel", arrivalListItem.getArrivalList().getContactPersonTel());//联系人电话
        reportData.put("repairer", arrivalListItem.getArrivalList().getRepairer());//维修联系人
        reportData.put("repairer_tel", arrivalListItem.getArrivalList().getRepairerTel());//维修联系人电话
        // 安装位置
        String locationName = "";
        if(StringUtils.isNotBlank(arrivalListItem.getLocationName())){
            locationName = arrivalListItem.getLocationName();
        }
        if(StringUtils.isNotBlank(arrivalListItem.getFloorName())){
            locationName = arrivalListItem.getFloorName() + locationName;
        }
        if(StringUtils.isNotBlank(arrivalListItem.getBuildingName())){
            locationName = arrivalListItem.getBuildingName() + locationName;
        }
        reportData.put("location_name", locationName);//安置地点
        reportData.put("fixasset_ack_date", ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getFixassetAckStatus()) ? arrivalListItem.getFixassetAckDate() : "");//验收日期
        return reportData;
    }

    public Map<String, Object> createReportDataBatch(List<ArrivalListItem> arrivalListItemList){
        Map<String, Object> reportData = new HashedMap();

        List<Map<String, Object>> arrivalItemList = new ArrayList<>();
        for (ArrivalListItem arrivalListItem : arrivalListItemList) {
            Map<String, Object> arrivalItemMap = new HashMap<>();
            ArrivalList arrivalList = arrivalListItem.getArrivalList();
            arrivalItemMap.put("asset_name", arrivalList.getAssetName());//设备名称
            arrivalItemMap.put("brand", arrivalList.getBrand());//品牌
            arrivalItemMap.put("function_type", arrivalListItem.getFunctionType());//型号规格
            Integer importEquipment = arrivalListItem.getImportEquipment();
            if(importEquipment == null || importEquipment !=1){
                importEquipment = 2;//是否进口设备[1:进口,2:国产]
            }
            arrivalItemMap.put("import_equipment", FieldValueMessageController.doGetFieldValue("importEquipment",importEquipment.toString()));//是否进口设备[1:进口,2:国产]
            arrivalItemMap.put("item_num", 1);//数量
//            arrivalItemMap.put("guarantee", arrivalListItem.getGuarantee());
            arrivalItemMap.put("factory_warranty_date", arrivalListItem.getWarrantyMonth());//质保时间（月）
            arrivalItemMap.put("annual_maintenance_times", arrivalListItem.getAnnualMaintenanceTimes());//年保修次数
            arrivalItemMap.put("manufact_num", arrivalListItem.getSerialNum());//出厂SN编号
            arrivalItemMap.put("hospital_num", arrivalListItem.getDepartNum());//医院编号
            // 安装位置
            String locationName = "";
            if(StringUtils.isNotBlank(arrivalListItem.getLocationName())){
                locationName = arrivalListItem.getLocationName();
            }
            if(StringUtils.isNotBlank(arrivalListItem.getFloorName())){
                locationName = arrivalListItem.getFloorName() + locationName;
            }
            if(StringUtils.isNotBlank(arrivalListItem.getBuildingName())){
                locationName = arrivalListItem.getBuildingName() + locationName;
            }
            arrivalItemMap.put("location_name", locationName);//安置地点
            arrivalItemList.add(arrivalItemMap);
        }
        reportData.put("arrival_item_batch",arrivalItemList);

        ArrivalListItem arrivalListItem = arrivalListItemList.get(0);

        reportData.put("receipt_number", arrivalListItem.getArrivalList().getReceiptNumber());//入库单号
        reportData.put("asset_name", arrivalListItem.getArrivalList().getAssetName());//设备名称
        reportData.put("financing_num", arrivalListItem.getFinancingNum());//设备财产编号
        reportData.put("function_type", arrivalListItem.getArrivalList().getFunctionType());//型号规格
        reportData.put("brand", arrivalListItem.getArrivalList().getBrand());//品牌
        reportData.put("manufact_num", arrivalListItem.getSerialNum());//出厂编号
        reportData.put("clinical_dept_name", orgInfoDao.findByUid(arrivalListItem.getClinicalDeptUID()));//设备所属科室

        reportData.put("contract_num", arrivalListItem.getArrivalList().getContractNum());//合同号
        reportData.put("invoice_num", arrivalListItem.getArrivalList().getInvoiceNum());//发票号
        reportData.put("contract_price", arrivalListItem.getArrivalList().getContractPrice());//合同价格
        reportData.put("contract_qua", arrivalListItem.getArrivalList().getQuantity());//数量
        reportData.put("manufacture", arrivalListItem.getArrivalList().getManufacture());//生产厂家
        reportData.put("purchase_price", arrivalListItem.getPurchasePrice());

        //技术验收
        reportData.put("technical_third_party_name", "");//第三方检查单位
        reportData.put("technical_check_date", "");//检测日期
        reportData.put("technical_ack_name", "");//验收人/经办人

        //固定资产验收单
        reportData.put("supplier_name", arrivalListItem.getArrivalList().getVendor());//供应商
        reportData.put("contract_date", arrivalListItem.getArrivalList().getContractDate());//合同签订日期
        reportData.put("foreign_trade_agreements", arrivalListItem.getArrivalList().getForeignTradeAgreements());//外贸协议
        reportData.put("box_installation_date", arrivalListItem.getArrivalList().getArrivalDate());//开箱安装日期改为到货安装日期
        reportData.put("using_date", arrivalListItem.getArrivalList().getUsingDate());//使用日期
        reportData.put("using_person", arrivalListItem.getUsingPerson());//使用人
        reportData.put("using_person_tel", arrivalListItem.getUsingPersonTel());//使用人电话
        reportData.put("contact_person", arrivalListItem.getArrivalList().getContactPerson());//联系人
        reportData.put("contact_person_tel", arrivalListItem.getArrivalList().getContactPersonTel());//联系人电话
        reportData.put("repairer", arrivalListItem.getArrivalList().getRepairer());//维修联系人
        reportData.put("repairer_tel", arrivalListItem.getArrivalList().getRepairerTel());//维修联系人电话
//        reportData.put("location_name", locationName);//安置地点
        reportData.put("fixasset_ack_date", ArrivalListItem.ACK_STATUS_PASS.equals(arrivalListItem.getFixassetAckStatus()) ? arrivalListItem.getFixassetAckDate() : "");//验收日期
        reportData.put("approval_no", arrivalListItem.getArrivalList().getApprovalNo());//批文号
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

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(data, templateBytes))), "application/msword", this.buildFileName(data, reportType, false));

        return streamedContent;
    }
    //导出同一个合同和科室的记录
    public StreamedContent exportDocxBatch(Map<String, Object> data, byte[] templateBytes, String reportType) throws IOException {

        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocxBatch(data, templateBytes))), "application/msword", this.buildFileNameBatch(data, reportType, false));

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
        }else if(REPORT_TYPE_ASSET.equals(reportType)){
            templatePath = REPORT_ASSET_TEMPLATE;
        }else if(REPORT_TYPE_ASSET_BATCH.equals(reportType)){
            templatePath = REPORT_ASSET_TEMPLATE_BATCH;
        }else if(REPORT_TYPE_ASSET_BATCH_BOTTOM.equals(reportType)){
            templatePath = REPORT_ASSET_TEMPLATE_BATCH_BOTTOM;
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

    private byte[] renderDocxBatch(Map<String, Object> data, byte[] templateBytes) throws IOException {

        byte[] bytes;
        XWPFTemplate template = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = new ByteArrayInputStream(templateBytes);
            template = XWPFTemplate.compile(inputStream);
            template.registerPolicy("arrival_item_batch", new ArrivalItemBatchTableRenderPolicy());
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

        String assetName = "";
        if(data.get("asset_name") != null){
            assetName = data.get("asset_name").toString();
            assetName = StringUtils.replace(assetName, "\\", "_");
            assetName = StringUtils.replace(assetName, "/", "_");
        }

        String financingNum = "";
        if(data.get("financing_num") != null){
            financingNum = data.get("financing_num").toString();
            financingNum = StringUtils.replace(financingNum, "\\", "_");
            financingNum = StringUtils.replace(financingNum, "/", "_");
        }

        String reportTypeTitle = "";
        if(REPORT_TYPE_TECHNOLOGY.equals(reportType)){
            reportTypeTitle = REPORT_TECHNOLOGY_TITLE;
        }else if(REPORT_TYPE_BUSINESS.equals(reportType)){
            reportTypeTitle = REPORT_BUSINESS_TITLE;
        }else if(REPORT_TYPE_CLINICAL.equals(reportType)){
            reportTypeTitle = REPORT_CLINICAL_TITLE;
        }else if(REPORT_TYPE_ASSET.equals(reportType)){
            reportTypeTitle = REPORT_ASSET_TITLE;
        }else if(REPORT_TYPE_ASSET_BATCH.equals(reportType)){
            reportTypeTitle = REPORT_ASSET_TITLE_BATCH;
        }

        if(isEncoding){
            return this.encoding(reportTypeTitle) + "-" + this.encoding(assetName) + "[" + this.encoding(financingNum) + "].docx";
        }else {
            return reportTypeTitle + "-" + assetName + "[" + financingNum + "].docx";
        }
    }

    private String buildFileNameBatch(Map<String, Object> data, String reportType, boolean isEncoding){

        String contractNum = "";
        if(data.get("contract_num") != null){
            contractNum = data.get("contract_num").toString();
            contractNum = StringUtils.replace(contractNum, "\\", "_");
            contractNum = StringUtils.replace(contractNum, "/", "_");
        }

        String reportTypeTitle = "";
        if(REPORT_TYPE_ASSET_BATCH.equals(reportType)){
            reportTypeTitle = REPORT_ASSET_TITLE_BATCH;
        }else if(REPORT_TYPE_ASSET_BATCH_BOTTOM.equals(reportType)){
            reportTypeTitle = REPORT_ASSET_TITLE_BATCH_BOTTOM;
        }

        if(isEncoding){
            return contractNum +"-"+ this.encoding(reportTypeTitle) +".docx";
        }else {
            return contractNum +"-"+ reportTypeTitle +".docx";
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
