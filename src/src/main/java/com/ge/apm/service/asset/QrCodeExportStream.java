/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
public class QrCodeExportStream extends InputStream {

    private QrCodeService qrCodeService = WebUtil.getBean(QrCodeService.class);

    private List<Object> qrCodeLibList;

    private ByteArrayOutputStream bout;

    private ZipOutputStream out;

    private int exportPos;
    
    private String templateId;
    
    private Map<String,String> orgNamesMap;

    private ByteArrayInputStream biStream;

    public QrCodeExportStream(List<Object> qrCodeLibList, String templateId,Map<String,String> orgNamesMap) {
        this.qrCodeLibList = qrCodeLibList;
        exportPos = 0;
        bout = new ByteArrayOutputStream();
        out = new ZipOutputStream(bout);
        this.templateId = templateId;
        this.orgNamesMap = orgNamesMap;
        exportBatchQrCode();
    }

    @Override
    public int read() throws IOException {

        return biStream.read();
    }

    @Override
    public void close() throws IOException {
        biStream.close();
    }

    @Override
    public int available() throws IOException {
        return biStream.available();
    }

    @Override
    public int read(byte[] b) throws IOException {

        int available = biStream.available();

        int i = biStream.read(b);
        if (available <= b.length && exportPos < qrCodeLibList.size()) {
            exportBatchQrCode();
        }
        return i;
    }

    private int exportBatchQrCode() {

        bout.reset();
        List<Object> batchList = qrCodeLibList.subList(exportPos, Math.min(exportPos + 10, qrCodeLibList.size()));

        try {
            qrCodeService.writeZipOutputStream(batchList,out,templateId,orgNamesMap);
            exportPos += batchList.size();
            if (exportPos == qrCodeLibList.size()) {
                out.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(QrCodeExportStream.class.getName()).log(Level.SEVERE, null, ex);
        }

        biStream = new ByteArrayInputStream(bout.toByteArray());
        return biStream.available();
    }

    public int getExportPos() {
        return exportPos;
    }

}
