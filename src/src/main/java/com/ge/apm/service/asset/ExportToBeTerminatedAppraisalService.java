package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTerminationReportTemplateRepository;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetTerminationReportTemplate;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        String departmentName = null;
        String terminalCause = null;
        List<Map<ASSET_DETAIL, Object>> list = new ArrayList<>();
        for (String id : selectedIds) {
            AssetInfo assetInfo = assetInfoRepository.findByUid(id);
            departmentName = setValue(departmentName, assetInfo.getClinicalDeptName(), "所选设备的科室信息不一致");
            terminalCause = setValue(terminalCause, assetInfo.getTerminationCause(), "所选设备的报废原因不一致");
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
        datas.put("asset_department_name", departmentName);
        datas.put("asset_termination_cause", terminalCause);
        return datas;
    }

    public String setValue(String original, String temp, String errorMessage) {
        if (original == null) {
            original = temp;
        } else if (!StringUtils.equals(original, temp)) {
            throw new IllegalStateException(errorMessage);
        }
        return original;
    }

    private class AssetDetailsRenderPolicy extends AbstractWoTableRenderPolicy {
        public void render(XWPFTable table, Object o) {
            XWPFTableRow labelRow = table.getRow(1);
            List<XWPFTableCell> tableCells = labelRow.getTableCells();
            List<String> values = tableCells.stream().map(i -> i.getText()).collect(Collectors.toList());

            int n = 3;
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
            table.removeRow(2);
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
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/tobeterminated_template.docx"));
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

}
