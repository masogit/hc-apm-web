package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetScrapRepository;
import com.ge.apm.dao.AssetTerminationReportTemplateRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.context.LocaleHolder;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ge.apm.service.asset.ExportTerminatedAppraisalService.ASSET_DETAIL.lifecycle;
import static com.ge.apm.service.asset.ExportToBeTerminatedAppraisalService.ASSET_DETAIL.*;

@Component
public class ExportToBeTerminatedAppraisalService {
    private static final Logger logger = LoggerFactory.getLogger(ExportToBeTerminatedAppraisalService.class);

    protected enum ASSET_DETAIL {
        financing_num, asset_name, function_type, install_date, manufacture, vendor,
        maintenance, national_standard_code, serial_num, depart_num, system_id, purchase_price, purchase_date,
        asset_dept_name,asset_dept_owner_name,salvage_value,termination_cause,comments,num,accumulated_depreciation,
        application_date,clinical_dept_name,site,unit_price,qr_code,apply_audit_signature
    }

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private AssetTerminationReportTemplateRepository templateRepository;

    @Autowired
    private BlobFileService blobService;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private AssetLifecyleService assetLifecyleService;

    @Autowired
    private AssetScrapRepository assetScrapRepository;

    @Autowired
    private BlobFileService blobFileService;

    public List<AssetInfo> getAssetInfo(InputStream inputstream, String tenantUid, Set<String> siteIds) throws Exception {
        ExcelDocumentStreamingReader reader = new ExcelDocumentStreamingReader(inputstream, 0, 1, null, null);
        return getAssetMapFromTemplate(reader, tenantUid, siteIds);
    }

    public List<AssetInfo> getAssetMapFromTemplate(ExcelDocumentStreamingReader doc, String tenantUid, Set<String> siteIds) throws Exception {
        List<AssetInfo> result = new ArrayList<>();
        int dataRow = 1;

        try {
            for (Map<String, ExcelDocument.ValueWrapper> rowMap : doc) {
                AssetInfo asset = new AssetInfo();
                asset.setLineNum(dataRow++);

                String funNum = ExcelDocument.getCellStringValue(rowMap.get(WebUtil.getMessage("AssetID")));
                if (org.springframework.util.StringUtils.isEmpty(funNum)) {
                    //end of file
                    break;
                }

                List<AssetInfo> byFinancingNumAndTenantUID = assetInfoRepository.findByFinancingNumAndTenantUID(funNum, tenantUid);
                byFinancingNumAndTenantUID.removeIf(i -> !siteIds.contains(i.getSiteUID()));
                if (byFinancingNumAndTenantUID.isEmpty()) {
                    WebUtil.addErrorMessage(String.format(WebUtil.getMessage("DoesNotExist"), funNum));
                } else {
                    result.addAll(byFinancingNumAndTenantUID);
                }
            }

        } catch (Exception e) {
            throw new Exception(WebUtil.getMessage("Page") + dataRow + WebUtil.getMessage("DataError") + e.getMessage());
        }
        return result;
    }

    public StreamedContent export(List<String> selectedIds, byte[] templateBytes ) {
        Map<String, Object> datas = getData(selectedIds,true);
//        byte[] bytes;
//        XWPFTemplate template = null;
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
//             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
////            template = XWPFTemplate.compile(inputStream);
////            template.registerPolicy("asset_detail_table", new AssetDetailsRenderPolicy());
//
//            Configure config = Configure.newBuilder()
//                    .bind("asset_detail_table", new AssetDetailsRenderPolicy())
//                    .build();
//            template = XWPFTemplate.compile(inputStream, config);
//            template.render(datas);
//            template.write(out);
//            bytes = out.toByteArray();
//            return new ApmStreamedContent(new ByteArrayInputStream(bytes), "application/msword", "report.docx");
//        } catch (IOException e) {
//            return null;
//        } finally {
//            try {
//                if (template != null) {
//                    template.close();
//                }
//            } catch (Exception ignore) {
//
//            }
//        }
        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("asset_detail_table", new AssetDetailsRenderPolicy());
        byte[] bytes = WordExportServiceUtil.generate(datas, templateBytes, bindings);
        return new ApmStreamedContent(new ByteArrayInputStream(bytes), "application/msword", "report.docx");
    }


    public StreamedContent exportZip(List<String> selectedIds, byte[] templateBytes ) {
        Map<String, Object> datas = getData(selectedIds,false);
        List<Map<ASSET_DETAIL, Object>> list= (List<Map<ASSET_DETAIL, Object>>) datas.get("asset_detail_table");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(out)) {
            list.forEach(item -> {
                Object o = item.get(asset_name);
                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        map.putAll(datas);
                        item.forEach((k, v) ->
                            map.put(k.name(), v)
                        );
//                        byte[] bytes = generate(map, templateBytes);

                        Map<String, RenderPolicy> bindings = new HashMap<>();
                        byte[] bytes = WordExportServiceUtil.generate(map, templateBytes, bindings);

                        if (bytes != null && !Arrays.equals(bytes, new byte[0])) {
                            String qrCode = item.get(qr_code)!=null?item.get(qr_code).toString():"";

                            String name = o.toString() + "-" +qrCode+"-"+ "资产报废鉴定表" + ".docx";
                            zout.putNextEntry(new ZipEntry(name));
                            IOUtils.write(bytes, zout);
                            zout.closeEntry();

                        }
                    } catch (Exception e) {
                        final String s = o.toString();
                        logger.error("assetUid {},待报废鉴定表导出异常：e", s, e);
                    }


            });
            zout.finish();
            return new ApmStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "报废鉴定.zip");
        } catch (Exception e) {
            logger.error("报废鉴定表导出失败 e:", e);
            return null;
        }
    }

//    private byte[] generate( Map<String, Object> map, byte[] templateBytes) {
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = (new ByteArrayInputStream(templateBytes));
//        template.render(map);
//        try {
//            template.write(out);
//        } catch (IOException e) {
//            logger.error("报废鉴定报导出失败 generate error:", e);
//            return new byte[0];
//        }
//        return out.toByteArray();
//    }
    public Map<String, Object> getData(List<String> selectedIds,boolean single) {
        Map<String, Object> datas = new HashMap<>();
        Set<String> departmentName = new HashSet<>();
        Set<String> terminalCause = new HashSet<>();
        Set<String> siteName = new HashSet<>();
        List<Map<ASSET_DETAIL, Object>> list = new ArrayList<>();
        List<AssetInfo> assetInfos = assetInfoRepository.getByUids(new HashSet<>(selectedIds));
        Map<String, AssetInfo> assetInfoMap = assetInfos.stream().collect(Collectors.toMap(e -> e.getUid(), e -> e));
        Set<String> siteUidSet = assetInfos.stream().map(i -> i.getSiteUID()).collect(Collectors.toSet());

        List<OrgInfo> orgInfos = orgInfoRepository.findByUids(siteUidSet);
        Map<String, OrgInfo> orgInfoMap = orgInfos.stream().collect(Collectors.toMap(e -> e.getSiteUID(), e -> e));

        for (String id : selectedIds) {
            AssetInfo assetInfo = assetInfoMap.get(id);
            if(single){
                setValue(departmentName, assetInfo.getClinicalDeptName(), WebUtil.getMessage("Inconsistent"));
                setValue(terminalCause, assetInfo.getTerminationCause(), WebUtil.getMessage("ReasonInconsistent"));
                setValue(siteName, assetInfo.getSiteUID(), WebUtil.getMessage("SiteInconsistent"));
            }
            Map<ASSET_DETAIL, Object> map = new EnumMap<>(ASSET_DETAIL.class);
            map.put(qr_code, assetInfo.getQrCode());
            map.put(financing_num, assetInfo.getFinancingNum());
            map.put(asset_name, assetInfo.getName());
            map.put(function_type, assetInfo.getFunctionType());
            Date installedDate = assetInfo.getInstallDate();
            map.put(install_date, installedDate == null ? "" : installedDate.toString());
            map.put(manufacture, assetInfo.getManufacture());
            map.put(vendor, assetInfo.getVendor());
            map.put(maintenance, assetInfo.getMaitanance());
            map.put(national_standard_code, assetInfo.getNationalStandardCode());
            map.put(serial_num, assetInfo.getSerialNum());
            map.put(depart_num, assetInfo.getDepartNum());
            map.put(system_id, assetInfo.getSystemId());
            if(assetInfo.getPurchasePrice()!=null){
                map.put(unit_price, new BigDecimal(assetInfo.getPurchasePrice()));
                map.put(purchase_price, new BigDecimal(assetInfo.getPurchasePrice()));

            }
            map.put(purchase_date, assetInfo.getPurchaseDate());
//            if(assetInfo.getSalvageValue()!=null){
//                map.put(salvage_value,new BigDecimal(assetInfo.getSalvageValue()));   //最终残值
//            }
             String assetDeptUid = assetInfo.getAssetDeptUID();
             if(StringUtils.isNotBlank(assetDeptUid)){
                 OrgInfo byUid = orgInfoRepository.findByUid(assetDeptUid);
                 map.put(asset_dept_name, byUid.getName());   //使用科室
             }
            map.put(termination_cause, assetInfo.getTerminationCause());   //报废原因
            map.put(asset_dept_owner_name, "");   //科室负责人
            map.put(clinical_dept_name, assetInfo.getClinicalDeptName());   //所属科室
            map.put(site, orgInfoMap.get(assetInfo.getSiteUID()));   //院区
            if(StringUtils.isNotBlank(assetInfo.getAssetScrapId())){
                AssetScrap assetScrap = assetScrapRepository.findById(assetInfo.getAssetScrapId());
                map.put(termination_cause, assetScrap.getApplyReason());   //报废原因
                map.put(comments, assetScrap.getApprovalReason());   //维修部门意见
                byte[] applySignatureBytes = getSignatureBytes(assetScrap.getApplyAuditSignature());
                map.put(apply_audit_signature, new PictureRenderData(100, 70, PictureType.PNG, applySignatureBytes));
            }
            map.put(num, 1);   //数量
            String assetUsedLifecycle = assetInfo.getAssetUsedLifecycle();

            long  usedMonth=0;
            if (!ObjectUtil.isEmpty(assetInfo.getPurchaseDate())) {

                LocalDate localInstallDate =new Date(assetInfo.getPurchaseDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = LocalDate.now();
                 usedMonth = ChronoUnit.MONTHS.between(localInstallDate, now);
            }
            Integer lifecycle = assetInfo.getLifecycle();
            Double lifecycleInYear = assetInfo.getLifecycleInYear();
            Double purchasePrice = assetInfo.getPurchasePrice();
            if(usedMonth>0 && lifecycle!=null && lifecycle!=0   && purchasePrice!=null){

                logger.info("assetUsedLifecycleDouble :{},lifecycle:{},purchasePrice:{}",usedMonth, lifecycle,purchasePrice);
//                BigDecimal bigDecimal = BigDecimal.valueOf(assetUsedLifecycleDouble).divide(BigDecimal.valueOf(lifecycleInYear), BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(purchasePrice)).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal bigDecimal = BigDecimal.valueOf(usedMonth).multiply(BigDecimal.valueOf(purchasePrice)).divide(BigDecimal.valueOf(lifecycle),5, RoundingMode.HALF_UP);
                if(bigDecimal.compareTo(BigDecimal.valueOf(purchasePrice))>=0){
                    map.put(accumulated_depreciation, BigDecimal.valueOf(purchasePrice));   //累计折旧

                    map.put(salvage_value,0);   //最终残值
                }else {
                    map.put(accumulated_depreciation, bigDecimal.setScale(2,RoundingMode.HALF_UP));   //累计折旧
                    map.put(salvage_value,BigDecimal.valueOf(purchasePrice).subtract(bigDecimal).setScale(2,RoundingMode.HALF_UP));   //最终残值
                }
            }else if(purchasePrice!=null){
                map.put(salvage_value,BigDecimal.valueOf(purchasePrice).setScale(2,RoundingMode.HALF_UP));   //最终残值
            }
            AssetLifecycle lifecycles= assetLifecyleService.selectByAssetUidAndStatusOne(assetInfo.getUid(), AssetConstant.ASSET_STATUS_TO_TERMINATED);
            if(lifecycles!=null){
//                map.put(application_date, lifecycles!=null?lifecycles.getModifyTime().toString():"");   //申报时间
                Date modifyTime = lifecycles.getModifyTime();
                final Instant instant = modifyTime.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDate localDate = instant.atZone(zoneId).toLocalDate();
                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                int day = localDate.getDayOfMonth();
                map.put(application_date, year+"年"+month+"月"+day+"日");   //申报时间
            }
            list.add(map);
        }
        datas.put("asset_detail_table", list);
        datas.put("asset_department_name", departmentName.stream().findFirst().orElse(""));
        datas.put("asset_termination_cause", terminalCause.stream().findFirst().orElse(""));

        String assetSiteName = "";
        if(siteName.size() > 0){
            OrgInfo orgInfo = orgInfoRepository.findByUid(siteName.stream().findFirst().get());
            if(null != orgInfo){
                assetSiteName = orgInfo.getName();
            }
        }
        datas.put("asset_site_name", assetSiteName);

        return datas;
    }

    public void setValue(Set<String> original, String input, String errorMessage) {
        String temp = StringUtils.isBlank(input) ? "" : input;
        original.add(temp);
        if (original.size() > 1) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private class AssetDetailsRenderPolicy extends AbstractWoTableRenderPolicy {
        public void render(XWPFTable table, Object o) {
            XWPFTableRow labelRow = table.getRow(1);
            List<XWPFTableCell> tableCells = labelRow.getTableCells();
            List<String> values = tableCells.stream().map(i -> i.getText()).collect(Collectors.toList());
            table.removeRow(1);

            int n = 2;
            if (o instanceof List) {
                List<Map<ASSET_DETAIL, Object>> parts = (List) o;
                for (Map<ASSET_DETAIL, Object> part : parts) {
                    XWPFTableRow row = addRow(table, n++);
                    values.forEach(i -> {
                        if (StringUtils.isBlank(i)) {
                            addCell(row, "");
                        } else {
                            addCell(row, extractValue(part, ASSET_DETAIL.valueOf(i)));
                        }
                    });
                }
            }
            table.removeRow(1);
        }

    }

    public byte[] getActiveTemplateBytes(boolean isInsulate, String siteUID, String hospitalUID, String institutionUID, String tenantUID,String templateType) throws IOException {
        byte[] templateBytes = null;
        AssetTerminationReportTemplate template = getAssetTerminationReportTemplate(isInsulate, siteUID, hospitalUID, institutionUID, tenantUID,templateType);
        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            try {
                templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
            } catch (IOException e) {
                templateBytes=null;
            }
        }
//        if (templateBytes == null) {
//            Map<String, String> templateMapping = new HashMap<>();
//            templateMapping.put("ToBeTerminated", "tobeterminated_template");
//            templateMapping.put("Terminated", "terminated_template");
//            templateMapping.put("zh", "_zh.docx");
//            templateMapping.put("en", "_en.docx");
//             String localeSuffix = LocaleHolder.getLocale().toString().substring(0, 2).toLowerCase();
//             String fileNameBase = templateMapping.get(templateType);
//             String fileExtension = templateMapping.get(localeSuffix);
//             String fileName = fileNameBase + fileExtension;
//             templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/"+fileName));
//        }


        return templateBytes;
    }

    public AssetTerminationReportTemplate getAssetTerminationReportTemplate(boolean isInsulate, String siteUID, String hospitalUID, String institutionUID, String tenantUID,String templateType) {
        List<String> uids = new ArrayList<>();
        populateUidList(uids, siteUID);
        if (!isInsulate) {
            populateUidList(uids, hospitalUID);
            populateUidList(uids, institutionUID);
            populateUidList(uids, tenantUID);
        }

        AssetTerminationReportTemplate prioritizedTemplate = getPrioritizedTemplate(uids,templateType);
        return prioritizedTemplate;
    }

    private AssetTerminationReportTemplate getPrioritizedTemplate(List<String> uids,String templateType) {
        List<AssetTerminationReportTemplate> templates = templateRepository.findBySiteUIDList(uids);
        for (String uid : uids) {
            Optional<AssetTerminationReportTemplate> optional = templates.stream().filter(i -> StringUtils.equals(i.getSiteUID(), uid)   && StringUtils.equals(i.getTemplateType(),templateType)    ).findFirst();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    private void populateUidList(List<String> uids, String siteUID) {
        if (StringUtils.isNotEmpty(siteUID)) {
            uids.add(siteUID);
        }
    }

    public boolean existsSiteTemplate(String siteUid) {
        List<AssetTerminationReportTemplate> list = templateRepository.findBySiteUID(siteUid);
        return list != null && !list.isEmpty() && list.size()>1  ;
    }

    public List<AssetTerminationReportTemplate> siteTemplate(String siteUid) {
       return  templateRepository.findBySiteUID(siteUid);
    }
    public byte[] getSignatureBytes(String storageId) {
        byte[] signatureBytes = null;

        try {
            signatureBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(storageId).getStream());
        } catch (IOException e) {
            logger.info("Error happens while get signature bytes", e);
        }

        return signatureBytes;
    }
}
