package com.ge.apm.service.asset;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.commons.io.IOUtils;
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

@Component
public class ExportToBeTerminatedAppraisalService {

    private enum ASSET_DETAIL {financing_num, asset_name, quantity, book_value, function_type, create_time}

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    public StreamedContent export(List<String> selectedIds) {
        Map<String, Object> datas = getData(selectedIds);

        byte[] bytes;
        XWPFTemplate template = null;
        byte[] templateBytes;
        try {
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/template/tobeterminated_template.docx"));
        } catch (IOException e) {
            return null;
        }
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
            map.put(ASSET_DETAIL.financing_num, assetInfo.getFinancingNum());
            map.put(ASSET_DETAIL.asset_name, assetInfo.getName());
            map.put(ASSET_DETAIL.function_type, assetInfo.getFunctionType());
            Date createdDate = assetInfo.getCreatedDate();
            map.put(ASSET_DETAIL.create_time, createdDate == null ? "" : createdDate.toString());
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
                    addCell(row, extractValue(part, ASSET_DETAIL.financing_num));
                    addCell(row, extractValue(part, ASSET_DETAIL.asset_name));
                    addCell(row, extractValue(part, ASSET_DETAIL.quantity));
                    addCell(row, extractValue(part, ASSET_DETAIL.book_value));
                    addCell(row, extractValue(part, ASSET_DETAIL.function_type));
                    addCell(row, extractValue(part, ASSET_DETAIL.create_time));
                }
            }
            table.removeRow(1);
        }
    }
}
