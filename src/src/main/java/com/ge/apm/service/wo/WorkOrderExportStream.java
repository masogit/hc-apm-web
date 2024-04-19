package com.ge.apm.service.wo;

import com.ge.apm.domain.Progress;
import com.ge.apm.service.wo.doctemplate.WorkOrderDocxRenderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorkOrderExportStream extends InputStream {

    private final Logger log = LoggerFactory.getLogger(WorkOrderExportStream.class);


    private List<Map<String, Object>> workOrderData;

    private byte[] templateBytes;

    private ByteArrayInputStream biStream;

    private WorkOrderDocxRenderService renderService;
    private ByteArrayOutputStream bout;

    private ZipOutputStream zout;

    private String contentType;
    private String fileName;

    private int read = 0;

    private Progress progress;

    public WorkOrderExportStream(List<Map<String, Object>> workOrderData, byte[] templateBytes, WorkOrderDocxRenderService renderService, Progress progress) {
        this.workOrderData = workOrderData;
        this.templateBytes = templateBytes;
        bout = new ByteArrayOutputStream();
        zout = new ZipOutputStream(bout);
        this.renderService = renderService;
        this.progress = progress;
        if (CollectionUtils.isNotEmpty(workOrderData) && workOrderData.size() > 1) {
            this.setContentType("application/zip");
            this.setFileName("WorkOrderDetails.zip");
            exportReport(read);
        } else if (CollectionUtils.isNotEmpty(workOrderData)) {
            this.setContentType("application/msword");
            this.setFileName(getFileName(workOrderData.get(0), 1));
            exportDocReport();
        }
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int available = biStream.available();
        int i = biStream.read(b);
        if (available <= b.length && read < workOrderData.size() && i != -1) {
            if (biStream != null) {
                biStream.close();
            }
            exportReport(read++);
        } else if (i == -1 && read < workOrderData.size()) {
            exportReport(read++);
            return 0;
        }
        return i;

    }

    @Override
    public int available() throws IOException {
        return biStream.available();
    }

    @Override
    public void close() throws IOException {
        progress.setValue(100);
        biStream.close();
    }

    private String getFileName(Map<String, Object> map, int index) {
        String siteName = (String) map.get("院区");
        String deviceName = (String) map.get(WebUtil.getMessage("EquipmentName_xlsx"));
        deviceName = StringUtils.replace(deviceName, "\\", "-");
        deviceName = StringUtils.replace(deviceName, "/", "-");
        return siteName + index + "-" + deviceName + "-" + map.get(WebUtil.getMessage("srNum_xlsx")) + ".docx";
    }

    public void exportReport(int index) {
        Map<String, Object> map = workOrderData.get(index);
        String fileName = getFileName(map, index + 1);
        try {
            bout.reset();
            zout.putNextEntry(new ZipEntry(fileName));
            log.info("Adding zip entry for: " + fileName);
            byte[] wordData = renderService.renderDocx(map, templateBytes);
            OutputStream outputStream = new FileOutputStream("/tmp/" + fileName);
            outputStream.write(wordData);
            outputStream.close();
            IOUtils.write(wordData, zout);
            zout.closeEntry();
            if (read == workOrderData.size()) {
                zout.close();
                bout.close();
            }
            biStream = new ByteArrayInputStream(bout.toByteArray());
            int currentProgress = (index+1)*100/ workOrderData.size();
            if (currentProgress == 100) {
                progress.setValue(99);
            } else {
                progress.setValue(currentProgress);
            }

        } catch (IOException e) {
            log.error("export report to zip error:{}", e.getMessage());
        }
    }

    /**
     * 单个报告的时候导出word
     *
     * @return
     * @throws IOException
     */
    public void exportDocReport() {
        try {
            biStream = new ByteArrayInputStream(renderService.renderDocx(workOrderData.get(0), templateBytes));
            read = 1;
            progress.setValue(99);
        } catch (IOException e) {
            log.error("export doc report error:{}", e.getMessage());
        }

    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
