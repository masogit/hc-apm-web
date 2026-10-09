package com.ge.apm.service.sparepart;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.StockRecordRepository;
import com.ge.apm.domain.StockRecord;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.simpleparts.SimplePartsRequestLineItemTableRenderPolicy;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SparePartStockRecordService {

    private Logger logger = LoggerFactory.getLogger(SparePartStockRecordService.class);

    @Autowired
    private StockRecordRepository stockRecordRepository;

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
        Map<String, RenderPolicy> bindings = new HashMap<>();
        if(1 == recordType){
            bindings.put("parts_table", new InboundPartOrderTableRenderPolicy());
        }else if(2 == recordType){
            bindings.put("parts_table",  new OutboundPartOrderTableRenderPolicy());
        }
        return WordExportServiceUtil.generate(data, templateBytes, bindings);


//
//        byte[] bytes;
//        XWPFTemplate template = null;
//        Configure config=null;
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
//             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
////            template = XWPFTemplate.compile(inputStream);
//            if(1 == recordType){
////                template.registerPolicy("parts_table", new InboundPartOrderTableRenderPolicy());
//                config=  Configure.newBuilder()
//                        .bind("parts_table", new InboundPartOrderTableRenderPolicy())
//                        .build();
//            }else if(2 == recordType){
////                template.registerPolicy("parts_table", new OutboundPartOrderTableRenderPolicy());
//
//                config=  Configure.newBuilder()
//                        .bind("parts_table", new OutboundPartOrderTableRenderPolicy())
//                        .build();
//            }
//
//
//            template = XWPFTemplate.compile(inputStream, config);
//            template.render(data);
//            template.write(out);
//            bytes = out.toByteArray();
//        } finally {
//            try {
//                if (template != null) {
//                    template.close();
//                }
//            } catch (Exception e) {
//                logger.error("设备出入库单导出，IO关闭异常", e);
//            }
//        }
//        return bytes;
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
            Integer quantity = getValidQuantity(stockRecord);
            map.put("quantity", quantity);
            map.put("unit", stockRecord.getSparePart().getUnit());

            BigDecimal bigDecimal = calcTotalPrice(stockRecord.getSparePart().getUnitPrice(), quantity);

            map.put("totalPrice", null == bigDecimal ? "" : bigDecimal.toString());
            map.put("invoiceNumber", stockRecord.getSparePart().getInvoiceNumber());
            if (UserContextService.hasCurrentTenantName("")) {
                map.put("adaptingDevices", stockRecord.getSparePart().getAdaptingDevices());
                map.put("effectiveDate", stockRecord.getSparePart().getEffectiveDate());
            }
            totalPrice = totalPrice.add(bigDecimal);

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
        if(null != unitPrice && 0 != unitPrice && null != quantity && 0 != quantity) {
            BigDecimal total = new BigDecimal(unitPrice * quantity);
            return total.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(0);
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

    @Transactional(readOnly = true)
    public Integer getValidQuantity(StockRecord record) {
        Integer quantity = record.getQuantity();
        List<StockRecord> stockRecords = new ArrayList<>();
        if (record.getRecordType() == 1) {
            stockRecords = stockRecordRepository.findListByInboundNumber(record.getInboundNumber());
        }
        if (record.getRecordType() == 2) {
            stockRecords = stockRecordRepository.findListByOutboundNumber(record.getOutboundNumber());
        }
        return stockRecords.stream().mapToInt(StockRecord::getQuantity).reduce(quantity, (o, n) -> o - n);
    }
}
