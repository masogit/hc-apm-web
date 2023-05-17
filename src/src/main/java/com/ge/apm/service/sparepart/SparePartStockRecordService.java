package com.ge.apm.service.sparepart;

import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.domain.StockRecord;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SparePartStockRecordService {

    private Logger logger = LoggerFactory.getLogger(SparePartStockRecordService.class);

    public byte[] getTemplate(String fileName){
        byte[] templateBytes = null;
        try {
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().
                    getExternalContext().getResourceAsStream("/portal/SparePartManagement/template/" + fileName));
        } catch (IOException e) {
            logger.error("获取文件失败：", e);
        }
        return templateBytes;
    }

    public SparePartStockRecordService.RenderResult renderDocx(List<StockRecord> list, byte[] templateBytes, String fileName, Integer recordType) throws IOException {
        byte[] bytes = this.generateFile(list, templateBytes,recordType);
        SparePartStockRecordService.RenderResult renderResult = new SparePartStockRecordService.RenderResult(bytes, "application/msword", fileName);
        return renderResult;
    }

    public byte[] generateFile(List<StockRecord> list, byte[] templateBytes, Integer recordType) throws IOException {
        Map<String, Object> data = mapData(list);
        byte[] bytes;
        XWPFTemplate template = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            template = XWPFTemplate.compile(inputStream);
            if(1 == recordType){
                template.registerPolicy("parts_table", new InboundPartOrderTableRenderPolicy());
            }else if(2 == recordType){
                template.registerPolicy("parts_table", new OutboundPartOrderTableRenderPolicy());
            }
            template.render(data);
            template.write(out);
            bytes = out.toByteArray();
        } finally {
            try {
                if (template != null) {
                    template.close();
                }
            } catch (Exception e) {
                logger.error("设备出入库单导出，IO关闭异常", e);
            }
        }
        return bytes;
    }

    private Map<String, Object> mapData(List<StockRecord> list){
        Map<String, Object> data = new HashMap<>();

        List<Map<String, Object>> tableData = new ArrayList<>();
        int index = 1;
        BigDecimal totalPrice = new BigDecimal(0);
        for (StockRecord stockRecord : list) {
            Map<String, Object> map = new HashMap<>();

            map.put("index", index);
            map.put("name", stockRecord.getSparePart().getName());
            map.put("model", stockRecord.getSparePart().getModel());
            map.put("manufacturer", stockRecord.getSparePart().getManufacturer());
            map.put("unitPrice", stockRecord.getSparePart().getUnitPrice());
            map.put("quantity", stockRecord.getQuantity());
            map.put("unit", stockRecord.getSparePart().getUnit());
            map.put("invoiceNumber", stockRecord.getSparePart().getInvoiceNumber());

            BigDecimal bigDecimal = calcTotalPrice(stockRecord.getSparePart().getUnitPrice(), stockRecord.getQuantity());

            map.put("totalPrice", null == bigDecimal ? "" : bigDecimal.toString());

            if(null != bigDecimal){
                totalPrice = totalPrice.add(bigDecimal);
            }

            tableData.add(map);
            index++;
        }
        data.put("receive_dept", list.get(0).getReceivingDepartment());
        data.put("supplier", list.get(0).getSparePart().getSupplier());
        data.put("parts_table", tableData);
        data.put("total_price", totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        return data;
    }

    private BigDecimal calcTotalPrice(Double unitPrice, Integer quantity){
        if(null != unitPrice && 0 != unitPrice && null != quantity && 0 != quantity){
            BigDecimal total = new BigDecimal(unitPrice * quantity);
            return total.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
    
    public class RenderResult {
        RenderResult(byte[] result, String contentType, String fileName) {
            this.result = result;
            this.contentType = contentType;
            this.fileName = fileName;
        }

        public byte[] result;
        public String contentType;
        public String fileName;
    }
}
