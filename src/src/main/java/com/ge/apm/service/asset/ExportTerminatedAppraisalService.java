package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.bo.render.AccessoryTableRenderPolicy;
import com.ge.apm.domain.bo.render.AuditMsgTableRenderPolicy;
import com.ge.apm.domain.bo.render.CheckListTableRenderPolicy;
import com.ge.apm.domain.bo.render.MaintenanceTableRenderPolicy;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import com.ge.apm.view.asset.AssetInfoToBeTerminatedListController;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ge.apm.service.asset.ExportTerminatedAppraisalService.ASSET_DETAIL.*;


@Component
public class ExportTerminatedAppraisalService {

    private static final Logger logger = LoggerFactory.getLogger(ExportTerminatedAppraisalService.class);

    protected enum ASSET_DETAIL {
        site_name, asset_name, depart_num, financing_num, maker_short_name, function_type, serial_num, location_name,
        install_date, salvage_value, lifecycle,used_lifecycle, purchase_price, clinical_dept_name, apply_name, apply_date, applyResone,
        approvalResone, approvaAauditSignature, approvaAauditSignature1, approvaAauditSignature2, approvaAauditSignature3
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
    private AssetScrapRepository assetScrapRepository;
    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private MultiLevelApprovalRepository multiLevelApprovalRepository;


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

    public StreamedContent export(List<String> selectedIds, byte[] templateBytes) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(out)) {
            selectedIds.forEach(item -> {
                AssetScrap assetScrap = assetScrapRepository.findByAssetUid(item);
                if (assetScrap != null) {
                    try {
                        byte[] bytes = generate(assetScrap, templateBytes);
                        if (bytes != null) {
                            String name = assetScrap.getAssetName() + "-" + "资产报废单" + ".docx";
                            zout.putNextEntry(new ZipEntry(name));
                            IOUtils.write(bytes, zout);
                            zout.closeEntry();

                        }
                    } catch (Exception e) {
                        logger.error("assetUid:{},报废鉴定表导出异常：e", assetScrap.getAssetUid(), e);
                    }
                }else{
                    logger.info("assetUid:{},没有报废流", item);

                }

            });
            zout.close();
            return new ApmStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "报废鉴定.zip");
        } catch (Exception e) {
            logger.error("报废鉴定表导出失败 e:", e);
            return null;
        }
    }

    private byte[] generate(AssetScrap assetScrap, byte[] templateBytes) {
        Map<String, Object> datas = getData(assetScrap);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XWPFTemplate template = XWPFTemplate.compile(new ByteArrayInputStream(templateBytes));
        template.render(datas);
        try {
            template.write(out);
        } catch (IOException e) {
            logger.error("报废鉴定报导出失败 generate error:", e);
            return null;
        }
        return out.toByteArray();
    }

    public Map<String, Object> getData(AssetScrap assetScrap) {
        Map<String, Object> map = new HashMap<>();

        AssetInfo assetInfo = assetInfoRepository.findByUid(assetScrap.getAssetUid());
        OrgInfo orgInfo = orgInfoRepository.findByUid(assetInfo.getSiteUID());
        map.put(site_name.name(), orgInfo.getName());

        map.put(asset_name.name(), assetInfo.getName());
        map.put(depart_num.name(), assetInfo.getDepartNum());
        map.put(financing_num.name(), assetInfo.getFinancingNum());
        map.put(maker_short_name.name(), assetInfo.getMakerShortName());
        map.put(function_type.name(), assetInfo.getFunctionType());
        map.put(serial_num.name(), assetInfo.getSerialNum());
        map.put(location_name.name(), assetInfo.getLocationName());
        map.put(salvage_value.name(), assetInfo.getSalvageValue());

        Date installedDate = assetInfo.getInstallDate();
        map.put(install_date.name(), installedDate == null ? "" : installedDate.toString());

        if(assetInfo.getInstallDate()!=null){
            Date installDate = assetInfo.getInstallDate();
//            LocalDate localDate = installDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//            LocalDate now=LocalDate.now();
//            long days = ChronoUnit.DAYS.between(localDate, now);
//            BigDecimal scale = BigDecimal.valueOf(days / 365).setScale(2, BigDecimal.ROUND_HALF_UP);
//            map.put(used_lifecycle.name(), scale);
            String assetUsedLifecycle = getAssetUsedLifecycle(installDate);
            map.put(used_lifecycle.name(), assetUsedLifecycle);

        }
        if(assetInfo.getLifecycle()!=null){
            double  lifecycleDouble= new Double(assetInfo.getLifecycle()) / 12.0;
            map.put(lifecycle.name(),lifecycleDouble);
        }
        if(assetInfo.getPurchasePrice()!=null){
            String normalNumber = new BigDecimal(assetInfo.getPurchasePrice()).toPlainString();

//            DecimalFormat format = new DecimalFormat("0.00");
//            String formattedNumber = format.format(normalNumber);
            map.put(purchase_price.name(), normalNumber);

        }
        map.put(clinical_dept_name.name(), assetInfo.getClinicalDeptName());
        map.put(apply_name.name(), assetScrap.getCreatedBy());
        String date = SDF.format(assetScrap.getCreatedDate());
        map.put(apply_date.name(), date);
        map.put(applyResone.name(), assetScrap.getApplyReason());
        map.put(approvalResone.name(), assetScrap.getApprovalReason());
        if(StringUtils.isNotBlank(assetScrap.getApprovalAuditSignature())){
            byte[] signatureBytes = getSignatureBytes(assetScrap.getApprovalAuditSignature());
            map.put(approvaAauditSignature.name(), new PictureRenderData(100, 80, "signature.png", signatureBytes));
        }

        List<MultiLevelApproval> multiLevelApprovals1 = new ArrayList<>();

        List<MultiLevelApproval> multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(assetScrap.getBusinessUid());
        Map<Integer, List<MultiLevelApproval>> listMap = multiLevelApprovals.stream().filter(item -> item.getApprovalStatus() == 2).collect(Collectors.groupingBy(MultiLevelApproval::getApprovalLevel));
        listMap.forEach((id, approvalList) -> {
            List<MultiLevelApproval> collect = approvalList.stream().sorted(Comparator.comparing(MultiLevelApproval::getActualApproverDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
            multiLevelApprovals1.add(collect.get(0));
        });

        List<MultiLevelApproval> levelApprovals = multiLevelApprovals1.stream().sorted(Comparator.comparing(MultiLevelApproval::getApprovalLevel, Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
        for (int i=1;i<=levelApprovals.size();i++){
            if(i>3){
                logger.info("审批节点大于三");
                break;
            }
            if(StringUtils.isBlank(levelApprovals.get(i-1).getAuditSignature())){
                continue;
            }
            byte[] levelSignatureBytes = getSignatureBytes(levelApprovals.get(i-1).getAuditSignature());
            map.put(approvaAauditSignature.name()+i, new PictureRenderData(100, 80, "signature.png", levelSignatureBytes));
        }
        return map;
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public static String getAssetUsedLifecycle(Date installDate) {
        if (ObjectUtil.isEmpty(installDate)) {
            return null;
        }
        Temporal temporal1 = LocalDate.parse(SDF.format(installDate));
        Temporal temporal2 = LocalDate.parse(SDF.format(System.currentTimeMillis()));
        return String.format("%.0f", Math.floor(Double.parseDouble(String.valueOf(ChronoUnit.MONTHS.between(temporal1, temporal2))) / 12));
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

    public byte[] getActiveTemplateBytes(boolean isInsulate, String siteUID, String hospitalUID, String institutionUID, String tenantUID) throws IOException {
        byte[] templateBytes = null;
        List<String> uids = new ArrayList<>();
        populateUidList(uids, siteUID);
        if (!isInsulate) {
            populateUidList(uids, hospitalUID);
            populateUidList(uids, institutionUID);
            populateUidList(uids, tenantUID);
        }

        AssetTerminationReportTemplate template = getPrioritizedTemplate(uids);

        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            try {
                templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
            } catch (Exception e) {
                templateBytes = null;
            }
        }
        if (templateBytes == null) {
            String fileName = "terminated_template.docx";
            if (LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")) {
                fileName = "terminated_template_zh.docx";
            } else {
                fileName = "terminated_template_en.docx";
            }
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/" + fileName));
        }
        return templateBytes;
    }

    private AssetTerminationReportTemplate getPrioritizedTemplate(List<String> uids) {
        List<AssetTerminationReportTemplate> templates = templateRepository.findBySiteUIDList(uids);
        for (String uid : uids) {
            Optional<AssetTerminationReportTemplate> optional = templates.stream().filter(i -> StringUtils.equals(i.getSiteUID(), uid)).findFirst();
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
        List list = templateRepository.findBySiteUID(siteUid);
        return list != null && !list.isEmpty();
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
