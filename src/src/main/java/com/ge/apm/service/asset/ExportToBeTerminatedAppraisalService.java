package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTerminationReportTemplateRepository;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetTerminationReportTemplate;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.context.LocaleHolder;
import webapp.framework.web.WebUtil;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.ge.apm.service.asset.ExportToBeTerminatedAppraisalService.ASSET_DETAIL.*;

@Component
public class ExportToBeTerminatedAppraisalService {

    protected enum ASSET_DETAIL {
        financing_num, asset_name, function_type, install_date, manufacture, vendor,
        maintenance, national_standard_code, serial_num, depart_num, system_id, purchase_price, purchase_date
    }

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private AssetTerminationReportTemplateRepository templateRepository;

    @Autowired
    private BlobFileService blobService;

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
        Map<String, Object> datas = getData(selectedIds);

        byte[] bytes;
        XWPFTemplate template = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            template = XWPFTemplate.compile(inputStream);
            template.registerPolicy("asset_detail_table", new AssetDetailsRenderPolicy());
            template.render(datas);
            template.write(out);
            bytes = out.toByteArray();
            return new ApmStreamedContent(new ByteArrayInputStream(bytes), "application/msword", "report.docx");
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (template != null) {
                    template.close();
                }
            } catch (Exception ignore) {

            }
        }
    }

    public Map<String, Object> getData(List<String> selectedIds) {
        Map<String, Object> datas = new HashMap<>();
        Set<String> departmentName = new HashSet<>();
        Set<String> terminalCause = new HashSet<>();
        List<Map<ASSET_DETAIL, Object>> list = new ArrayList<>();
        for (String id : selectedIds) {
            AssetInfo assetInfo = assetInfoRepository.findByUid(id);
            setValue(departmentName, assetInfo.getClinicalDeptName(), WebUtil.getMessage("Inconsistent"));
            setValue(terminalCause, assetInfo.getTerminationCause(), WebUtil.getMessage("ReasonInconsistent"));
            Map<ASSET_DETAIL, Object> map = new HashMap<>();
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
            map.put(purchase_price, assetInfo.getPurchasePrice());
            map.put(purchase_date, assetInfo.getPurchaseDate());
            list.add(map);
        }
        datas.put("asset_detail_table", list);
        datas.put("asset_department_name", departmentName.stream().findFirst().orElse(""));
        datas.put("asset_termination_cause", terminalCause.stream().findFirst().orElse(""));
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
            templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
        }
        if (templateBytes == null) {
            String fileName = "tobeterminated_template.docx";
            if (LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")){
                fileName = "tobeterminated_template_zh.docx";
            } else {
                fileName = "tobeterminated_template_en.docx";
            }
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/"+fileName));
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

}
