package com.ge.apm.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();ExcelWriter excelWriter =EasyExcel.write(baos).withTemplate(new ByteArrayInputStream(templateFileByte)).build()) {
            // 创建 ExcelWriter 并指定模板
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

    /**
     * 公共的 Excel 导出方法，返回文件字节数组
     *
     * @param templateFileByte 模板文件的字节数组
     * @param listMap       填充列表数据   key是sheetName    value是 填充列表数据
     * @param paramMap       其他的补充数据    key是sheetName   value是 填充其他非列表数据
     * @return 导出文件的字节数组
     */
    public static byte[] exportExcel(byte[] templateFileByte, HashMap<String,Object> listMap, Map<String, Object> paramMap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();ExcelWriter excelWriter =EasyExcel.write(baos).withTemplate(new ByteArrayInputStream(templateFileByte)).build()) {
            // 创建 ExcelWriter 并指定模板
;
            for (Map.Entry<String, Object> stringObjectEntry : listMap.entrySet()) {

                ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet().sheetName(stringObjectEntry.getKey());
                WriteSheet writeSheet = sheetBuilder.build();

                FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

                excelWriter.fill(stringObjectEntry.getValue(), fillConfig, writeSheet);
                Object o = paramMap.get(stringObjectEntry.getKey());

                if (o != null) {
                    excelWriter.fill(o, writeSheet);
                }
            }
            excelWriter.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("EasyExcelExportUtil error: ",e);
            return new byte[0];
        }
    }


    public interface DataSupplier {


        Map<String, Object> getParamMap();


        List<?> getData(int  pageNum,int pageSize);
    }
    /**
     * 公共的 Excel 导出方法，返回文件字节数组
     *
     * @param templateFileByte 模板文件的字节数组
     * @param dataSupplierMap    key是sheetName    value是 填充列表数据
     * @return 导出文件的字节数组
     */
    public static byte[] exportExcel(byte[] templateFileByte, HashMap<String,DataSupplier> dataSupplierMap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();ExcelWriter excelWriter =EasyExcel.write(baos).withTemplate(new ByteArrayInputStream(templateFileByte)).build()) {

            // 配置填充参数
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            for (Map.Entry<String, DataSupplier> dataSupplierEntry : dataSupplierMap.entrySet()) {
                String sheetName = dataSupplierEntry.getKey();
                DataSupplier dataSupplier = dataSupplierEntry.getValue();
                // 创建 ExcelWriter 并指定模板
                // 创建写入工作表
                ExcelWriterSheetBuilder sheetBuilder = EasyExcel.writerSheet().sheetName(sheetName);
                WriteSheet writeSheet = sheetBuilder.build();
                int pageNum = 1;
                int pageSize = 10000;
                List<?> resultList = dataSupplier.getData(pageNum, pageSize);
                while (!resultList.isEmpty()) {
                    excelWriter.fill(resultList, fillConfig, writeSheet);
                    pageNum++;
                    resultList = dataSupplier.getData(pageNum, pageSize);
                    if (resultList.isEmpty()) {
                        break;
                    }
                }
                // 填充 Map 数据
                Map<String, Object> paramMap = dataSupplier.getParamMap();
                if (paramMap != null) {
                    excelWriter.fill(paramMap, writeSheet);
                }
            }

            excelWriter.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("EasyExcelExportUtil error: ",e);
            return new byte[0];
        }
    }



}
