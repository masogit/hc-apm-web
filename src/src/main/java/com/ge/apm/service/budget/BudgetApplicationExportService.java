package com.ge.apm.service.budget;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.BudgetApplicationAssetRepository;
import com.ge.apm.dao.BudgetApplicationRepository;
import com.ge.apm.domain.MultiLevelApproval;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.zkb.CopyByInvok;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author : 503189362
 * @date : 2023/10/16
 * @description :
 */
@Service
public class BudgetApplicationExportService {

    public static final String FILE_TITLE = "医疗仪器设备年度采购申报表";
    public static final String OVER_FIFTY_THOUSAND = "/portal/budget/application/docTemplate/over_fifty_thousand.docx";
    public static final String LESS_FIFTY_THOUSAND = "/portal/budget/application/docTemplate/less_fifty_thousand.docx";
    public static final int TOTAL_SPLIT = 50000;
    public static final String FILE_NAME_ROOT = "年度采购申报表";


    @Autowired
    BudgetApplicationRepository budgetApplicationRepository;

    @Autowired
    BudgetApplicationAssetRepository budgetApplicationAssetRepository;

    @Autowired
    BlobObjectRepository blobObjectRepository;

    private MicroServiceUtil msService;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
    }

    /**
     * 导出多个申购单
     */
    public StreamedContent exportZip(List<BudgetApplicationAsset> budgetApplicationAssetList) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);
        for(BudgetApplicationAsset budgetApplicationAsset : budgetApplicationAssetList){
            List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(budgetApplicationAsset.getId());
            exportZipStream(budgetApplicationAsset,blobObjects, zout);
        }
        zout.close();
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", "Export_Purchase.zip");
        return streamedContent;
    }


    public StreamedContent exportZip(BudgetApplicationAsset budgetApplicationAsset, List<V2_BlobObject> blobObjects) throws IOException {
        String base = FILE_NAME_ROOT + "-" + budgetApplicationAsset.getApplicationNum();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);
        exportZipStream(budgetApplicationAsset, blobObjects,zout);
        zout.close();
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(bout.toByteArray())), "application/zip", base+".zip");
        return streamedContent;
    }

    public void exportZipStream(BudgetApplicationAsset budgetApplicationAsset, List<V2_BlobObject> blobObjects, ZipOutputStream zout) throws IOException {
        //1、doc模板部分
        Map<String, Object> data = createReportData(budgetApplicationAsset);
        byte[] templateBytes;
        if(budgetApplicationAsset.getApplicationAmount().compareTo(new BigDecimal(TOTAL_SPLIT)) > 0){
            templateBytes = buildReportTemplate(OVER_FIFTY_THOUSAND);
        }else {
            templateBytes = buildReportTemplate(LESS_FIFTY_THOUSAND);
        }

        byte[] bytes = this.renderDocx(data, templateBytes);


        String fileName = buildFileName(data, false);
        zout.putNextEntry(new ZipEntry(FILE_NAME_ROOT +"/" + fileName));
        IOUtils.write(bytes, zout);
        zout.closeEntry();

        //2、上传附件部分
        HashMap<String,Integer> nameMap = new HashMap<>();
        //List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(budgetApplicationAsset.getId());
        if(blobObjects != null && blobObjects.size() > 0){
            for(V2_BlobObject v : blobObjects){
                String fn = v.getObjectName();
                String newFileName = handleRepeatFileName(nameMap, fn);
                StreamedContent sf = msService.downloadSingleFile(v.getObjectStorageId());
                zout.putNextEntry(new ZipEntry("附件" + "/" + newFileName));
                byte[] bys = new byte[1024 * 5];
                BufferedInputStream bufferStream = new BufferedInputStream(sf.getStream());
                int rd = 0;
                while ((rd = bufferStream.read(bys)) != -1) {
                    zout.write(bys, 0, rd);
                }
                zout.closeEntry();
            }
        }
    }

    /**
     * 压缩时重复文件名会引发异常，所以需要将重复文件名重新命名
     */
    public String handleRepeatFileName(HashMap<String,Integer> nameMap, String fileName){
        String newFileName = fileName;
        if(nameMap.containsKey(fileName)){
            Integer next = nameMap.get(fileName) + 1;
            int lo = fileName.lastIndexOf(".");
            if(lo >-1){
                String pre = fileName.substring(0,lo);
                pre += "(" + next +")";
                newFileName = pre + fileName.substring(lo);
                //递归检查新的fileName是否重复
                newFileName = handleRepeatFileName(nameMap, newFileName);
            }
            nameMap.put(fileName, next);
        }else{
            nameMap.put(fileName, 0);
        }
        return newFileName;
    }

    /**
      * 获取导出的doc模板数据
     */
    public Map<String, Object> createReportData(BudgetApplicationAsset budgetApplicationAsset) throws IOException {
        BudgetApplication budgetApplication = budgetApplicationRepository.findById(budgetApplicationAsset.getApplicationId());
        Map<String, Object> mm = new HashMap<>();
        if(budgetApplicationAsset != null){
            if(budgetApplication.getApplicationDept() != null){
                mm.put("deptName", budgetApplication.getApplicationDept().getName());
            }
            mm.put("assetName", budgetApplicationAsset.getApplicationAssetName());
            mm.put("num", budgetApplicationAsset.getApplicationQuantity());
            mm.put("price", budgetApplicationAsset.getApplicationPrice());
            mm.put("total", budgetApplicationAsset.getApplicationAmount());
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(budgetApplication.getApplicationDate());
            int year = calendar.get(Calendar.YEAR);
            mm.put("year", year);
            int applyYear = calendar.get(Calendar.YEAR);
            int applyMonth = calendar.get(Calendar.MONTH);
            int applyDay = calendar.get(Calendar.DAY_OF_MONTH);
            mm.put("applyYear", applyYear);
            mm.put("applyMonth", applyMonth);
            mm.put("applyDay", applyDay);
            mm.put("similarDevicesUse", budgetApplicationAsset.getSimilarDevicesUse());
            mm.put("majorFunction", budgetApplicationAsset.getMajorFunction());
            mm.put("technicalParameter", budgetApplicationAsset.getTechnicalParameter());
            mm.put("equipmentUtilization", budgetApplicationAsset.getEquipmentUtilization());
            mm.put("benefitAnalysis", budgetApplicationAsset.getBenefitAnalysis());
            mm.put("productComparison", budgetApplicationAsset.getProductComparison());
            mm.put("serviceLife", budgetApplicationAsset.getServiceLife());
            mm.put("chargingStandards", budgetApplicationAsset.getChargingStandards());
            mm.put("yearIncome", budgetApplicationAsset.getYearIncome());
            mm.put("usage", budgetApplicationAsset.getUsage());
            mm.put("description", budgetApplicationAsset.getDescription());
            mm.put("applicationReason", budgetApplicationAsset.getApplicationReason());
            mm.put("equipmentUsage", budgetApplicationAsset.getEquipmentUsage());
            mm.put("offlineApprovalOpinions", budgetApplicationAsset.getOfflineApprovalOpinions());

            if(budgetApplication.getBudgetApprovals() != null){
                MultiLevelApproval a = budgetApplication.getBudgetApprovals().iterator().next();
                if(a != null){
                    mm.put("machineDeptOpinions", a.getApprovalOpinion());
                }else{
                    mm.put("machineDeptOpinions", "");
                }
            }else{
                mm.put("machineDeptOpinions", "");
            }


            //是否采购进口产品
            if(StringUtils.isEmpty(budgetApplicationAsset.getImportedOr())){
                mm.put("importedOrYes", "");
            }else if(CopyByInvok.T.equals(budgetApplicationAsset.getImportedOr())){
                mm.put("importedOrYes", "是");
            }else {
                mm.put("importedOrYes", "否");
            }
            //设备是否有放置场地
            if(StringUtils.isEmpty(budgetApplicationAsset.getPlaceSite())){
                mm.put("placeSite", "");
            }else if(CopyByInvok.T.equals(budgetApplicationAsset.getPlaceSite())){
                mm.put("placeSite", "是");
            }else {
                mm.put("placeSite", "否");
            }
            //是否在手术室使用
            if(StringUtils.isEmpty(budgetApplicationAsset.getUsedOperatingRoom())){
                mm.put("usedOperatingRoom", "");
            }else if(CopyByInvok.T.equals(budgetApplicationAsset.getUsedOperatingRoom())){
                mm.put("usedOperatingRoom", "是");
            }else {
                mm.put("usedOperatingRoom", "否");
            }
            //是否需要同院内信息系统对接
            if(StringUtils.isEmpty(budgetApplicationAsset.getSystemDocking())){
                mm.put("systemDocking", "");
            }else if(CopyByInvok.T.equals(budgetApplicationAsset.getSystemDocking())){
                mm.put("systemDocking", "是");
            }else {
                mm.put("systemDocking", "否");
            }
            //是否需要连接外网
            if(StringUtils.isEmpty(budgetApplicationAsset.getConnectNetwork())){
                mm.put("connectNetwork", "");
            }else if(CopyByInvok.T.equals(budgetApplicationAsset.getConnectNetwork())){
                mm.put("connectNetwork", "是");
            }else {
                mm.put("connectNetwork", "否");
            }

            /*V2_BlobObject blobObject = blobObjectRepository.findById("402886818b3c5660018b3cc6b6000002");
            StreamedContent sf = msService.downloadSingleFile(blobObject.getObjectStorageId());
            byte[] pictureBytes = IOUtils.toByteArray(sf.getStream());
            mm.put("sign", new PictureRenderData(80, 25, "sign.png", pictureBytes));*/

        }
        return mm;
    }

    /**
     * 构建导出模板
     * @param templatePath
     * @return
     * @throws IOException
     */
    public byte[] buildReportTemplate(String templatePath) throws IOException {
        byte[] templateBytes = null;
        if(templatePath != null){
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(templatePath));
        }
        return templateBytes;
    }

    public StreamedContent exportDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {
        StreamedContent streamedContent = new DefaultStreamedContent((new ByteArrayInputStream(this.renderDocx(data, templateBytes))), "application/msword", this.buildFileName(data, false));
        return streamedContent;
    }

    private byte[] renderDocx(Map<String, Object> data, byte[] templateBytes) throws IOException {
        byte[] bytes;
        XWPFTemplate template = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream inputStream = new ByteArrayInputStream(templateBytes);
            template = XWPFTemplate.compile(inputStream);
            //template.registerPolicy("arrival_item", new ArrivalItemTableRenderPolicy());
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

    private String buildFileName(Map<String, Object> data, boolean isEncoding){
        String assetName = "";
        if(data.get("assetName") != null){
            assetName = data.get("assetName").toString();
            assetName = StringUtils.replace(assetName, "\\", "_");
            assetName = StringUtils.replace(assetName, "/", "_");
        }

        String reportTypeTitle = FILE_TITLE;

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
            return fileName;
        }
    }
}
