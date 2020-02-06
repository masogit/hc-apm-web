/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.ImportFilesRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.ImportFiles;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.FileScanExportService;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.google.common.base.Strings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javaslang.Tuple2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class BatchFileController extends GenericCRUDUUIDController<ImportFiles> {

    ImportFilesRepository dao;

    private List<Tuple2<ImportFiles, UploadedFile>> fileList;

    private FileScanService fileService;
    private UserCommonService userService;
    private FileScanExportService fileScanExportService;

    private String fileType;

    private List<I18nMessage> batchFileType;

    private StreamedContent exportFile;

    MicroServiceUtil microServiceUtil;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ImportFilesRepository.class);
        fileService = WebUtil.getBean(FileScanService.class);
        userService = WebUtil.getBean(UserCommonService.class);
        fileScanExportService = WebUtil.getBean(FileScanExportService.class);
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);

        this.filterBySite = false;
        this.filterByHospital = false;
        fileType = ImportFiles.ImportFileType.ColdHeaderExcel.toString();

        FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
        batchFileType = fieldMsg.getFieldValueList("batchFileType");
        batchFileType.removeIf(i -> StringUtils.equalsIgnoreCase(i.getMsgKey(), "SrReportPdf"));
    }

    @Override
    protected GenericRepositoryUUID<ImportFiles> getDAO() {
        return dao;
    }

    @Override
    protected Page<ImportFiles> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public void uploadDataFile(FileUploadEvent event) {
        if (StringUtils.equalsIgnoreCase(fileType, ImportFiles.ImportFileType.SrReportPdf.toString())) {
            WebUtil.addErrorMessage("不支持服务报告类型文件上传，保存失败!");
            return;
        }
        if (this.fileList == null) {
            fileList = new ArrayList();
        }
        ImportFiles itemFile = new ImportFiles();
        itemFile.setFileName(event.getFile().getFileName());
        itemFile.setFileType(fileType);
        itemFile.setUploadedTime(new Date());
        itemFile.setStatus(ImportFiles.STATUS_STARTING);
        itemFile.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
        String savedfilePath = null;
        try {
            savedfilePath = fileService.saveBatchUploadedFile(event.getFile(), fileType);
        } catch (Exception ex) {
            WebUtil.addErrorMessage(event.getFile().getFileName() + "上传失败!");
        }
        if (Strings.isNullOrEmpty(savedfilePath)) {
            itemFile.setStatus("FAILEDUPLOAD");
            WebUtil.addErrorMessage(event.getFile().getFileName() + "保存失败!");
        } else {
            itemFile.setFilePath(savedfilePath);
        }

        fileList.add(new Tuple2<>(itemFile, event.getFile()));

        if (StringUtils.equals(fileType, ImportFiles.ImportFileType.SrExcel.toString())
                || StringUtils.equals(fileType, ImportFiles.ImportFileType.SrResetReport.toString())
                || StringUtils.equals(fileType, ImportFiles.ImportFileType.PMScheduleExcel.toString())) {
            microServiceUtil.synchBatchTask(new File(fileService.getUploadFileFullPath(savedfilePath)), fileType);
        }
    }

    public void removeFailedFile(String id) {
        ImportFiles file = dao.findById(id);
        if (null != file && Strings.isNullOrEmpty(file.getFilePath())) {
            dao.delete(file);
        }
    }

    public void removeFile(Integer index) {
        fileService.deleteBatchUploadedFile(fileList.get(index)._1.getFilePath());
        fileList.remove(fileList.get(index));
    }

    public String getUserName(String userUid) {
        UserAccount user = userService.getUserByUid(userUid);
        return null == user ? "" : user.getName();
    }

    public void saveFiles() throws Exception {

        List<ImportFiles> importFileList = fileList.stream().map(item -> item._1).collect(Collectors.toList());

        dao.save(importFileList);
        fileList.clear();
    }

    public void refreshData() {
        if (selected != null) {
            selected = dao.findById(selected.getId());
        }
    }

    public void restartBatchJob() {
        selected.setStatus(ImportFiles.STATUS_STARTING);
        dao.save(selected);
        WebUtil.addSuccessMessage("任务已经重新启动");
    }

    public void cancel() {
        this.selected = null;
        while (fileList != null && !fileList.isEmpty()) {
            removeFile(0);
        }
        this.fileList.clear();
        this.fileType = ImportFiles.ImportFileType.ColdHeaderExcel.toString();
    }

    public void exportData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/admin/batchFile/APM_ImportFiles_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = fileScanExportService.exportData(doc, fileScanExportService.createFileScanData(dao.findAllImportFilesVO()));
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ImportFiles.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(BatchFileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<I18nMessage> getBatchFileType() {
        return batchFileType;
    }

    public List<Tuple2<ImportFiles, UploadedFile>> getFileList() {
        return fileList;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}
