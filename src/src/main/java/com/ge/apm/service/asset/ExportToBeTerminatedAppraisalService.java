package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTerminationReportTemplateRepository;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.WorkOrderExportTemplate;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
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

import static com.ge.apm.service.asset.ExportToBeTerminatedAppraisalService.ASSET_DETAIL.*;

@Component
public class ExportToBeTerminatedAppraisalService {

    protected enum ASSET_DETAIL {financing_num, asset_name, quantity, book_value, function_type, create_time}

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
        List<Map<ASSET_DETAIL, Object>> list = new ArrayList<>();
        for (String id : selectedIds) {
            AssetInfo assetInfo = assetInfoRepository.findByUid(id);
            Map<ASSET_DETAIL, Object> map = new HashMap<>();
            map.put(financing_num, assetInfo.getFinancingNum());
            map.put(asset_name, assetInfo.getName());
            map.put(function_type, assetInfo.getFunctionType());
            Date createdDate = assetInfo.getCreatedDate();
            map.put(create_time, createdDate == null ? "" : createdDate.toString());
            list.add(map);
        }
        datas.put("asset_detail_table", list);
        return datas;
    }

    private class AssetDetailsRenderPolicy extends AbstractWoTableRenderPolicy {
        public void render(XWPFTable table, Object o) {
            int n = 2;
            if (o instanceof List) {
                List<Map<ASSET_DETAIL, Object>> parts = (List) o;
                for (Map<ASSET_DETAIL, Object> part : parts) {
                    XWPFTableRow row = addRow(table, n++);
                    addCell(row, extractValue(part, financing_num));
                    addCell(row, extractValue(part, asset_name));
                    addCell(row, extractValue(part, quantity));
                    addCell(row, extractValue(part, book_value));
                    addCell(row, extractValue(part, function_type));
                    addCell(row, extractValue(part, create_time));
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

        WorkOrderExportTemplate template = null;//getPrioritizedTemplate(uids);

        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
        }
        if (templateBytes == null) {
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/tobeterminated_template.docx"));
        }
        return templateBytes;
    }

    private WorkOrderExportTemplate getPrioritizedTemplate(List<String> uids) {
        List<WorkOrderExportTemplate> templates = templateRepository.findBySiteUIDList(uids);
        for (String uid : uids) {
            Optional<WorkOrderExportTemplate> optional = templates.stream().filter(i -> StringUtils.equals(i.getSiteUID(), uid)).findFirst();
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
