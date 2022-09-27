package com.ge.apm.service.nurseSite.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetOptRecordExportUtil {

    public static String[] title={"开机时间","关机时间","开机人","关机人","使用时长","备注"};
    public static List<Map<String,String>> titleMap=new ArrayList<>();


    public static InputStream export(List<Map<String,String>> basicInfo, List<AssetOptRecordExport> data) {
        Workbook workbook=null;
        ByteArrayOutputStream byteArrayOutputStream=null;
        try {
            workbook= new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            createAssetBasicInfo(workbook,sheet,basicInfo);
            createTitle(workbook,sheet);
            createData(workbook,sheet,data,2);
            byteArrayOutputStream = new ByteArrayOutputStream();
            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(workbook != null){
                    workbook.close();
                }
                if(byteArrayOutputStream != null){
                    byteArrayOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void createAssetBasicInfo(Workbook workbook, Sheet sheet,List<Map<String,String>> titleMap){
        Row row = sheet.createRow(0);
        int current=0;
        for (int i = 0; i < titleMap.size(); i++) {
            Map<String, String> stringStringMap = titleMap.get(i);
            Cell cell = row.createCell(current++);
            cell.setCellValue(stringStringMap.get("key"));
            cell.setCellStyle(createCellStyle(workbook, true));
            Cell cell1 = row.createCell(current++);
            cell1.setCellValue(stringStringMap.get("value"));
        }
    }
    //    合并单元格语法： 开始行、结束行、开始列、结束列
//
//    对应代码：new CellRangeAddress(startRowIndex, rowIndex - 1, i, i);
//
//    合并代码：sxssfSheet.addMergedRegion(cellRangeAddress);
    private static void  createTitle(Workbook workbook, Sheet sheet){
        Row row = sheet.createRow(1);
        int current=0;
        for (int i = 0; i < title.length; i++) {
            CellStyle cellStyle = createCellStyle(workbook, false);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            Cell cell = row.createCell(current++);
            cell.setCellValue(title[i]);
            cell.setCellStyle(cellStyle);
            Cell cell1 = row.createCell(current++);
            cell1.setCellValue("");
            cellRangeAddress(sheet,1,cell,cell1);
        }
    }
    private static void cellRangeAddress(Sheet sheet,int row,Cell firstCol, Cell lastCol){
        CellRangeAddress cellRangeAddress = new CellRangeAddress(row,row,firstCol.getColumnIndex(),lastCol.getColumnIndex());
        sheet.addMergedRegion(cellRangeAddress);
    }
    private static CellStyle createCellStyle(Workbook workbook, boolean isBold){
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(isBold);
        cellStyle.setFont(font);
        return cellStyle;
    }
    private static void createData(Workbook workbook,Sheet sheet,List<AssetOptRecordExport> data,int offset){
        if(CollectionUtils.isEmpty(data)) return;
        for (int i=0;i<data.size();i++){
            AssetOptRecordExport aor = data.get(i);
            Row row = sheet.createRow(i + offset);
            createCell( workbook, sheet,row,aor);
        }
    }

    private static void createCell(Workbook workbook,Sheet sheet,Row row,AssetOptRecordExport aor){
        int current=0;
        for (int i = 0; i < title.length; i++) {
            CellStyle cellStyle = createCellStyle(workbook, false);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            Cell cell = row.createCell(current++);
            switch (i){
                case 0:
                    DataFormat dataFormat = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-DD HH:mm:ss"));
                    cell.setCellValue(aor.getStartDate());
                    break;
                case 1:
                    DataFormat dataFormat1 = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat1.getFormat("yyyy-MM-DD HH:mm:ss"));
                    cell.setCellValue(aor.getEndDate());
                    break;
                case 2:
                    cell.setCellValue(aor.getCreateByUid());
                    break;
                case 3:
                    cell.setCellValue(aor.getModifyByUid());
                    break;
                case 4:
                    cell.setCellValue(aor.getCostTime());
                    break;
                case 5:
                    cell.setCellValue(aor.getComment());
                    break;
                default:
                    cell.setCellValue("");
                    break;
            }

            cell.setCellStyle(cellStyle);
            Cell cell1 = row.createCell(current++);
            cell1.setCellValue("");
            cellRangeAddress(sheet,row.getRowNum(),cell,cell1);
        }
    }
}
