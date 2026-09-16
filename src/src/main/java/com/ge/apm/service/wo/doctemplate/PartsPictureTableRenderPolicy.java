package com.ge.apm.service.wo.doctemplate;
import com.deepoove.poi.xwpf.BodyContainer;
import com.deepoove.poi.xwpf.BodyContainerFactory;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
//import com.deepoove.poi.NiceXWPFDocument;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.healthcare.cn.common.utils.render.AbstractWoTableRenderPolicy;
import com.ge.healthcare.cn.common.utils.render.DynamicColumnTableRenderPolicy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

/**
 *  附件图片表格
 */
public class PartsPictureTableRenderPolicy extends AbstractWoTableRenderPolicy {

    private int maxCell;

    private int width;

    private int height;

    private BlobFileService blobService;

    public PartsPictureTableRenderPolicy(BlobFileService blobService){
        this(blobService,3);
    }

    public PartsPictureTableRenderPolicy(BlobFileService blobService,int maxCell){
        this.blobService = blobService;
        this.maxCell = maxCell;
        this.width =  540 / this.maxCell;
        this.height =  540 / this.maxCell;
    }

    @Override
    public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
        NiceXWPFDocument doc = template.getXWPFDocument();
        RunTemplate runTemplate = (RunTemplate)eleTemplate;
        XWPFRun run = runTemplate.getRun();
        try {
//            XWPFTable table = doc.insertNewTable(run,1,maxCell);
            BodyContainer bodyContainer = BodyContainerFactory.getBodyContainer(run);
           XWPFTable table = bodyContainer.insertNewTable(run, 1, maxCell);
            this.render(table, data);
//            run.setText("", 23); // 清除页面标签
//            runTemplate.getRun().setText("", 0);
        } catch (Exception var10) {
            var10.printStackTrace();
//            logger.info("dynamic table error:" + var10.getMessage(), var10);
        }
        runTemplate.getRun().setText("", 0);
    }
    public void render(XWPFTable table, Object data) {
        if(data instanceof List){
            String[] dynamicParameter = DynamicColumnTableRenderPolicy.getDynamicParameter();
            if(dynamicParameter != null && dynamicParameter.length == 3){
                String[] parameters = dynamicParameter[1].split("[|,，、\\-]");
                this.maxCell = Integer.parseInt(parameters[0]);
                if(parameters.length > 2){
                    this.width =  Integer.parseInt(parameters[1]);
                    this.height =  Integer.parseInt(parameters[2]);
                }else{
                    this.width =  540 / this.maxCell;
                    this.height =  540 / this.maxCell;
                }
            }
            List<PartsPictureTableRenderPolicy.PartsPictureTable>  partsPictureTableList= (List<PartsPictureTableRenderPolicy.PartsPictureTable> )data;
            Set<Integer> merge = new HashSet<>();
            for (int i = 0; i < partsPictureTableList.size(); i++) {
                PartsPictureTableRenderPolicy.PartsPictureTable partsPictureTable = partsPictureTableList.get(i);
                XWPFTableRow row = addRow(table);
                addCell(row, "备件名称: "+ partsPictureTable.getPartsName());
                List<String> object = partsPictureTable.getObjectId();
                if(CollectionUtils.isEmpty(object)){
                    continue;
                }
//                int maxCell = object.size() > this.maxCell ? this.maxCell:object.size();
                for (int j = 0; j < this.maxCell-1; j++) {
                    addCell(row, "备件名称: "+ partsPictureTable.getPartsName());
                    merge.add(table.getNumberOfRows());
                }
                XWPFTableRow rowImage = null;
                for (int i1 = 0; i1 < object.size(); i1++) {
                    if(i1 % this.maxCell == 0){
                        rowImage = addRow(table);
                    }
                    addCellImages(rowImage, object.get(i1));
                }
            }
            Iterator<Integer> iterator = merge.iterator();
            while (iterator.hasNext()) {
                int rowNum = iterator.next();
                mergeHorizontal(table,rowNum-1,0,maxCell-1);
            }
        }

        table.removeRow(0);
    }


    /**
     * 合并列
     * @param table 表格
     * @param col  列索引
     * @param fromRow 开始行索引
     * @param toRow   结束行索引
     */
    private void mergeVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if (rowIndex == fromRow) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    /**
     * 合并行
     * @param table 表格
     * @param row   行索引
     * @param fromCell 开始列索引
     * @param toCell   结束列索引
     */
    private void mergeHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if ( cellIndex == fromCell ) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }
    public void addCellImages(XWPFTableRow row, String storageId){
        try {
            byte[] blob = getBlobSignature(storageId);
            addCell(row, blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addCell(XWPFTableRow row, byte[] picture) {
        addCell(row,new ByteArrayInputStream(picture));
    }

    private byte[] getBlobSignature(String storageId) {
        if (StringUtils.isNotEmpty(storageId)) {
            try (InputStream stream = blobService.getBlobObjectByStorageId(storageId).getStream();) {
                return IOUtils.toByteArray(stream);
            } catch (IOException e) {
                //ignore
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *  表格填充图片
     * @param row
     * @param picture
     */
    public void addCell(XWPFTableRow row, InputStream picture) {
        try {
            XWPFTableCell xwpfTableCell = row.addNewTableCell();
            xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);;
            XWPFRun run = paragraph.createRun();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run.addPicture(picture, XWPFDocument.PICTURE_TYPE_PNG, "banner.png", Units.toEMU(this.width), Units.toEMU(this.height));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class PartsPictureTable{

      private   String  partsName;

      private   List<byte[]> object;
      private   List<String> objectId;

        public String getPartsName() {
            return partsName;
        }

        public void setPartsName(String partsName) {
            this.partsName = partsName;
        }

        public List<byte[]> getObject() {
            return object;
        }

        public void setObject(List<byte[]> object) {
            this.object = object;
        }

        public List<String> getObjectId() {
            return objectId;
        }

        public void setObjectId(List<String> objectId) {
            this.objectId = objectId;
        }
    }
}
