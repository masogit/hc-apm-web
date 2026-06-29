package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 推理模型实体类
 * 用于管理系统中使用的各类AI推理模型文件
 */
@Entity
@javax.persistence.Table(name = "inference_model")
@Table(appliesTo = "inference_model", indexes = {
        @Index(name = "idx_inference_model_tenant_uid", columnNames = "tenant_uid"),
        @Index(name = "idx_inference_model_type", columnNames = "type")

})
public class InferenceModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键UID */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    private String uid;



    /** 模型类型 */
    @Column(name = "type", length = 100)
    private String type;

    /** 版本 */
    @Column(name = "version", length = 50)
    private String version;

    /** 租户UID */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    /** 上传人UID */
    @Column(name = "upload_user_uid", columnDefinition = "CHAR(32)")
    private String uploadUserUid;

    /** 上传人姓名 */
    @Column(name = "upload_user_name", length = 100)
    private String uploadUserName;

    /** 创建时间 */
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 修改时间 */
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 开启状态（true=启用，false=禁用） */
    @Column(name = "enabled")
    private Boolean enabled = false;

    /** 描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 文件名称 */
    @Column(name = "file_name", length = 255)
    private String fileName;

    /** 文件存储ID（关联BlobObject） */
    @Column(name = "storage_id", length = 100)
    private String storageId;

    // ==================== Getter & Setter ====================

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getUploadUserUid() {
        return uploadUserUid;
    }

    public void setUploadUserUid(String uploadUserUid) {
        this.uploadUserUid = uploadUserUid;
    }

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InferenceModel)) {
            return false;
        }
        InferenceModel other = (InferenceModel) object;
        return (this.uid != null || other.uid == null) && (this.uid == null || this.uid.equals(other.uid));
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.InferenceModel[ uid=" + uid + " ]";
    }
}
