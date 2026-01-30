package com.ge.apm.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFSplitFileUtil {


    public static List<File> splitPdfByPage(File sourcePdf) throws IOException, DocumentException {
        List<File> resultFiles = new ArrayList<>();

        String parentDir = sourcePdf.getParent();
        String baseName = sourcePdf.getName().replaceAll("\\.[^.]+$", ""); // 移除扩展名

        PdfReader reader = new PdfReader(sourcePdf.getAbsolutePath());
        int numberOfPages = reader.getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++) {
            String newFileName = parentDir + File.separator + baseName + "_page_" + i + ".pdf";
            File newFile = new File(newFileName);

            // 创建新文档
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(newFile));
            document.open();

            // 导入并添加第 i 页
            PdfImportedPage page = copy.getImportedPage(reader, i);
            copy.addPage(page);

            document.close(); // 自动 flush 并关闭流
            resultFiles.add(newFile);
        }

        reader.close(); // 手动关闭 reader（iText 5 不支持 try-with-resources）
        return resultFiles;
    }
}
