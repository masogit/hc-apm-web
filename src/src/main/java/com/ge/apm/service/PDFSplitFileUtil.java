package com.ge.apm.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class PDFSplitFileUtil {


    public static List<File> splitPdfByPage(File sourcePdf){
        List<File> resultFiles = new ArrayList<>();

        PdfReader reader = null;
        String parentDir = sourcePdf.getParent();
        String baseName = sourcePdf.getName().replaceAll("\\.[^.]+$", ""); // 移除扩展名
        try {
            reader = new PdfReader(sourcePdf.getAbsolutePath());
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
        } catch (Throwable e) {
            String newFileName = parentDir + File.separator + baseName + "_page.pdf";
            File newFile = new File(newFileName);
            try(FileInputStream fileInputStream = new FileInputStream(sourcePdf);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);){
                FileChannel channel = fileInputStream.getChannel();
                FileChannel channel1 = fileOutputStream.getChannel();
                channel.transferTo(0, sourcePdf.length(), channel1);
                resultFiles.add(newFile);
            }  catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }finally {
            if (reader != null) {
                reader.close(); // 手动关闭 reader（iText 5 不支持 try-with-resources）
            }
        }
        return resultFiles;
    }
}
