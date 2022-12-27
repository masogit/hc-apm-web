package com.ge.apm.service.nurseSite.export;

import com.ge.apm.domain.NurseSiteMaintainConfig;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class NurseSiteMaintainExportUtil {
    private static final Logger log = LoggerFactory.getLogger(NurseSiteMaintainExportUtil.class);

    private static final String [] title={"计划检查日期","实际检查日期","检查人"};
    private static final int offsetRow=3;

    public  InputStream export(List<Map<String,String>> basicInfo, Map<String, List<NurseSiteMaintainExport>>  data, NurseSiteMaintainService siteMaintainService) {
        Workbook workbook=null;
        ByteArrayOutputStream byteArrayOutputStream=null;
        try {
            workbook= new XSSFWorkbook();
            if(CollectionUtils.isEmpty(data)){
                setSheetData(workbook,basicInfo,null,siteMaintainService,null);
            }else{
                for (Map.Entry<String, List<NurseSiteMaintainExport>> stringListEntry : data.entrySet()) {
                    NurseSiteMaintainConfig maintainConfigById = siteMaintainService.getMaintainConfigById(stringListEntry.getKey());
                    setSheetData(workbook,basicInfo,stringListEntry.getValue(),siteMaintainService,maintainConfigById.getName());
                }
            }
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
    private void setSheetData(Workbook workbook,List<Map<String,String>> basicInfo,List<NurseSiteMaintainExport> data,NurseSiteMaintainService siteMaintainService,String sheetName){
        Sheet sheet =null;
        if(StringUtils.isEmpty(sheetName)){
             sheet = workbook.createSheet();
        }else{
            sheet = workbook.createSheet(sheetName);
        }
        createAssetBasicInfo(workbook,sheet,basicInfo);
        List<String> header = new ArrayList<>();
        createTitle(workbook,sheet);
        createData(workbook,sheet,data,2,header,siteMaintainService);
    }
    private  void createAssetBasicInfo(Workbook workbook, Sheet sheet,List<Map<String,String>> titleMap){
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
    private  void  createTitle(Workbook workbook, Sheet sheet){
        Row row = sheet.createRow(1);
        for (int i = 0; i < title.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(title[i]);
        }
    }
    private  void cellRangeAddress(Sheet sheet,int row,Cell firstCol, Cell lastCol){
        CellRangeAddress cellRangeAddress = new CellRangeAddress(row,row,firstCol.getColumnIndex(),lastCol.getColumnIndex());
        sheet.addMergedRegion(cellRangeAddress);
    }
    private  CellStyle createCellStyle(Workbook workbook, boolean isBold){
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(isBold);
        cellStyle.setFont(font);
        return cellStyle;
    }
    private  void createData(Workbook workbook, Sheet sheet, List<NurseSiteMaintainExport> data, int offset, List<String> header, NurseSiteMaintainService siteMaintainService){
        Row rowOne = sheet.getRow(1);
        if(CollectionUtils.isEmpty(data)) {
            Cell cell = rowOne.createCell(offsetRow);
            cell.setCellValue("备注");
            Cell cell1 = rowOne.createCell(offsetRow + 1);
            cell1.setCellValue("签名");
            return;
        }
        for (int i=0;i<data.size();i++){
            NurseSiteMaintainExport aor = data.get(i);
            Row row = sheet.createRow(i + offset);
            createCell( workbook, sheet,row,aor,header);
        }

        if(!CollectionUtils.isEmpty(header)){
            Map<String,Integer> headerMap=new HashMap<>();
            for (int i = 0; i < header.size(); i++) {
                  int index =i+offsetRow;
                String s = header.get(i);
                Cell cell = rowOne.createCell(index);
                cell.setCellValue(s);
                headerMap.put(s,index);
            }
            Cell cell = rowOne.createCell(offsetRow + header.size());
            cell.setCellValue("备注");
            Cell cell1 = rowOne.createCell(offsetRow + header.size()+1);
            cell1.setCellValue("签名");
            for (int i=0;i<data.size();i++){
                NurseSiteMaintainExport aor = data.get(i);
                Row row = sheet.getRow(i + offset);
                createCellCheckData(workbook,sheet,row,aor,headerMap,header,siteMaintainService);
            }
        }else{
            Cell cell = rowOne.createCell(offsetRow);
            cell.setCellValue("备注");
            Cell cell1 = rowOne.createCell(offsetRow + 1);
            cell1.setCellValue("签名");
        }


        // 添加备注
    }
    private void createCellCheckData(Workbook workbook,Sheet sheet,Row row, NurseSiteMaintainExport aor ,  Map<String,Integer> headerMap,List<String> header, NurseSiteMaintainService siteMaintainService){
        List<NurseSiteMaintainData> checkData = aor.getCheckData();
        if(!CollectionUtils.isEmpty(checkData)){
            for (NurseSiteMaintainData checkDatum : checkData) {
                String checkItem = checkDatum.getCheckItem();
                if(headerMap.containsKey(checkItem)){
                        Integer index = headerMap.get(checkItem);
                         Cell cell = row.createCell(index);
                    String strValue = checkDatum.getStrValue();
                    cell.setCellValue(strValue);
                }
            }
        }
        int size = header.size();
        if(!StringUtils.isEmpty(aor.getComment())){
            Cell cell = row.createCell(offsetRow + size);
            cell.setCellValue(aor.getComment());
        }
        if(!StringUtils.isEmpty(aor.getSignatureId())){
            StreamedContent signatureIn =null;
            try {
                 signatureIn = siteMaintainService.getMaintainSignature(aor.getSignatureId());
                 if(signatureIn != null){
                     BufferedImage bufferImg = ImageIO.read(signatureIn.getStream());
                     ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
                     ImageIO.write(bufferImg, "png", byteArrayOut);
                     XSSFDrawing patriarch = ((XSSFSheet) sheet).createDrawingPatriarch();
                     XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0,
                             0, (short) offsetRow + size+1,row.getRowNum() , (short) offsetRow + size+2,row.getRowNum()+1 );
                     anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                     patriarch.createPicture(anchor,workbook.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
                 }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
    private  void createCell(Workbook workbook,Sheet sheet,Row row,NurseSiteMaintainExport aor,List<String> header){
        int current=0;
        for (int i = 0; i < offsetRow; i++) {
            CellStyle cellStyle = createCellStyle(workbook, false);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            Cell cell = row.createCell(current++);
            switch (i){
                case 0:
                    DataFormat dataFormat = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat.getFormat("yyyy-MM-DD"));
                    Date scheduleDate = aor.getScheduleDate();
                    if(aor.getScheduleDate() != null){
                        cell.setCellValue(scheduleDate);
                        cell.setCellStyle(cellStyle);
                    }else{
                        cell.setCellValue("");
                    }
                    break;
                case 1:
                    DataFormat dataFormat1 = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat1.getFormat("yyyy-MM-DD"));
                    cell.setCellValue(aor.getCheckDate());
                    Date checkDate = aor.getCheckDate();
                    if(aor.getScheduleDate() != null){
                        cell.setCellValue(checkDate);
                        cell.setCellStyle(cellStyle);
                    }else{
                        cell.setCellValue("");
                    }
                    break;
/*                case 2:
                    cell.setCellValue(aor.getPrincipal());
                    break;*/
                case 2:
                    cell.setCellValue(aor.getChecker());
                    break;
                default:
                    cell.setCellValue("");
            }
        }
        addHeader(header,aor);
    }
    public   void addHeader(List<String> header,NurseSiteMaintainExport aor){
        List<NurseSiteMaintainData> checkData = aor.getCheckData();
        if(CollectionUtils.isEmpty(checkData))return;
        for (NurseSiteMaintainData checkDatum : checkData) {
                if(!header.contains(checkDatum.getCheckItem())){
                    header.add(checkDatum.getCheckItem());
                }
        }
    }

}
