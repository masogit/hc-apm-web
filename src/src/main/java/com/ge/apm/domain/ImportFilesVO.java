package com.ge.apm.domain;

import java.util.Date;

/**
 * @author 212605082 on 2019/10/14.
 */
public class ImportFilesVO {


    private String id;

    //文件名称(显示)
    private String fileName;

    //文件保存路径
    private String filePath;

    //文件类型
    private String fileType;

    //上传时间
    private Date uploadedTime;

    //开始处理时间
    private Date processStartTime;

    //结束处理时间
    private Date processEndTime;

    // 1:new 2:processing 3.finished 4.failed
    private String status;

    //上传人
    private String creatorUID;

    public ImportFilesVO(String id, String fileName, String filePath, String fileType, Date uploadedTime, Date processStartTime, Date processEndTime, String status, String creatorUID) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedTime = uploadedTime;
        this.processStartTime = processStartTime;
        this.processEndTime = processEndTime;
        this.status = status;
        this.creatorUID = creatorUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(Date uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public Date getProcessStartTime() {
        return processStartTime;
    }

    public void setProcessStartTime(Date processStartTime) {
        this.processStartTime = processStartTime;
    }

    public Date getProcessEndTime() {
        return processEndTime;
    }

    public void setProcessEndTime(Date processEndTime) {
        this.processEndTime = processEndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorUID() {
        return creatorUID;
    }

    public void setCreatorUID(String creatorUID) {
        this.creatorUID = creatorUID;
    }
}
