package com.ge.apm.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class EasyExcelExportUtil {

    protected static final org.apache.log4j.Logger logger = Logger.getLogger(EasyExcelExportUtil.class);

    /**
     * 公共的 Excel 导出方法，返回文件字节数组
     *
     * @param templateFileByte 模板文件的字节数组
     * @param resultList       填充列表数据
     * @param map              填充 Map 数据
     * @return 导出文件的字节数组
     */
    public static byte[] exportExcel(byte[] templateFileByte, List<?> resultList, Map<String, Object> map) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建 ExcelWriter 并指定模板
            ExcelWriterBuilder writerBuilder = EasyExcel.write(baos).withTemplate(new ByteArrayInputStream(templateFileByte));
            ExcelWriter excelWriter = writerBuilder.build();
            // 创建写入工作表
            ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet();
            WriteSheet writeSheet = sheetBuilder.build();
            // 配置填充参数
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            // 填充列表数据
            excelWriter.fill(resultList, fillConfig, writeSheet);
            // 填充 Map 数据
            if (map != null) {
                excelWriter.fill(map, writeSheet);
            }
            excelWriter.finish();
            return baos.toByteArray();
        } catch (Exception e) {
           logger.error("EasyExcelExportUtil error: ",e);
           return new byte[0];
        }
    }

}
