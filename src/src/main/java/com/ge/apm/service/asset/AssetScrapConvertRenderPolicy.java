package com.ge.apm.service.asset;

import com.deepoove.poi.data.PictureRenderData;
import com.ge.apm.service.wo.doctemplate.AbstractWoTableRenderPolicy;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class AssetScrapConvertRenderPolicy extends AbstractWoTableRenderPolicy {

    public void render(XWPFTable table, Object o) {

        if (o instanceof List) {
            List<Map<String, Object>> list = (List) o;
            for (Map<String, Object> assetDepartment : list) {
                XWPFTableRow row = addRow(table);
                addCell(row, extractValue(assetDepartment, "actualApproverName"));
                addCell(row, extractValue(assetDepartment, "approvalOpinion"));
                addCell(row, extractValue(assetDepartment, "actualApproverDate"));
                addCellObject(row, (InputStream) assetDepartment.get("signature"));
            }
        }
        table.removeRow(1);

    }

    @Override
    protected void addCell(XWPFTableRow row, String textValue) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        XWPFParagraph paragraph = xwpfTableCell.getParagraphs().get(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily("Calibri");
        run.setFontSize(11);
        run.setText(textValue);
    }


    protected void addCellObject(XWPFTableRow row, InputStream inputStream) {
        XWPFTableCell xwpfTableCell = row.addNewTableCell();
        xwpfTableCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        List<XWPFParagraph> paragraphs = xwpfTableCell.getParagraphs();
        XWPFParagraph paragraph;
        if (paragraphs.isEmpty()) {
            paragraph = xwpfTableCell.addParagraph();
        } else {
            paragraph = paragraphs.get(0);
        }
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        int pictureType = XWPFDocument.PICTURE_TYPE_PNG;
        int widthEMU = 100 * 9525;
        int heightEMU = 70 * 9525;
        try {
            run.addPicture(inputStream, pictureType, "image.png", widthEMU, heightEMU);
        } catch (Exception e) {
//            logger.error("报废鉴定表导出签名写入异常 e:",e);
        }
    }
}
