/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.ImportFilesRepository;
import com.ge.apm.domain.ImportFiles;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.service.utils.FileScanService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javaslang.Tuple2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class BatchFileController extends GenericCRUDUUIDController<ImportFiles> {

    ImportFilesRepository dao;

    private List<Tuple2<ImportFiles, UploadedFile>> fileList;

    private FileScanService fileService;
    private UserCommonService userService;

    private String fileType;

    protected void init() {
        dao = WebUtil.getBean(ImportFilesRepository.class);
        fileService = WebUtil.getBean(FileScanService.class);
        userService = WebUtil.getBean(UserCommonService.class);

        this.filterBySite = false;
        this.filterByHospital = false;
        fileType = ImportFiles.ImportFileType.ColdHeaderExcel.toString();

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
        if (this.fileList == null) {
            fileList = new ArrayList();
        }
        ImportFiles itemFile = new ImportFiles();
        itemFile.setFileName(event.getFile().getFileName());
        itemFile.setFileType(fileType);
        itemFile.setUploadedTime(new Date());
        itemFile.setStatus(ImportFiles.STATUS_STARTING);
        itemFile.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());

        fileList.add(new Tuple2<>(itemFile, event.getFile()));
    }

    public void removeFile(Integer index) {
        fileList.remove(fileList.get(index));
    }

    public String getUserName(String userUid){
        return userService.getUserByUid(userUid).getName();
    }
    
    public void saveFiles() throws Exception {

        fileList.forEach(item -> {
            try {
                String savedfilePath = fileService.saveBatchUploadedFile(item._2);
                item._1.setFilePath(savedfilePath);
                dao.save(item._1);
            } catch (Exception ex) {
                WebUtil.addErrorMessage(item._2.getFileName() + "上传失败!");
            }
        });

        fileList.clear();
    }

    public void cancel() {
        this.selected = null;
        this.fileList.clear();
        this.fileType = ImportFiles.ImportFileType.ColdHeaderExcel.toString();
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

}
