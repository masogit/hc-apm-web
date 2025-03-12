package com.ge.apm.view.pm;

import com.deepoove.poi.xwpf.BodyContainer;
import com.deepoove.poi.xwpf.BodyContainerFactory;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *  附件图片表格
 */
public class PmPartsPictureTableRenderPolicy extends AbstractWoTableRenderPolicy {
    /**
     *  maxCell --> 1  540
     *  maxCell --> 1  260
     *  maxCell --> 3  180
     */
    private int maxCell;
    private int width;
    public PmPartsPictureTableRenderPolicy(){
        this(3);
    }

    public PmPartsPictureTableRenderPolicy(int maxCell){
        this.maxCell = maxCell > 3 ? 3:maxCell;
        this.width = this.maxCell == 1 ? 540 : this.maxCell == 2? 260:this.maxCell == 3?180:0;
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
            runTemplate.getRun().setText("", 0);
        } catch (Exception var10) {
//            logger.error("dynamic table error:" + var10.getMessage(), var10);
        }
    }
    public void render(XWPFTable table, Object data) {
        if(data instanceof List){
            List<byte[]>  partsPictureTableList= (List<byte[]>)data;
            if(CollectionUtils.isEmpty(partsPictureTableList)){
                table.removeRow(0);
                return;
            }
            int size = partsPictureTableList.size();
//            XWPFTableRow row = addRow(table);
//            addCell(row, "备件名称: ");
//            int maxCell = size > this.maxCell ? this.maxCell:size;
//            for (int i = 1; i < maxCell; i++) {
//                addCell(row, "");
//            }
            XWPFTableRow rowImage = null;
            for (int i = 0; i < partsPictureTableList.size(); i++) {
                byte[] image = partsPictureTableList.get(i);

                if(image == null){
                    break;
                }
                if(i % maxCell == 0){
                    rowImage = addRow(table);
                }
                addCellImages(rowImage, image);

            }
           // mergeHorizontal(table,1,0,maxCell-1);
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
    public void addCellImages(XWPFTableRow row,byte[] blob){
        try {
            addCell(row, blob);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addCell(XWPFTableRow row, byte[] picture) {
        addCell(row,new ByteArrayInputStream(picture));
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
            run.addPicture(picture, XWPFDocument.PICTURE_TYPE_PNG, "banner.png", Units.toEMU(width), Units.toEMU(width));
        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }
}
