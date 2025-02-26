package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 212688574
 */
@Entity
@Table(name = "system_template")
@Data
public class SystemTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "file_name", columnDefinition = "CHAR(64)")
    private String fileName;


    @Column(name = "file_type")
    private String fileType;

    @Column(name = "template_type")
    private String templateType;  //模板类型
    @Column(name = "template_type_name")
    private String templateTypeName;  //模板类型名称
    @Column(name = "business_type")
    private String businessType;  //业务类型

    @Column(name = "storage_id")
    private String storageId;



    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_name")
    private String createName;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_name", length = 50)
    private String lastModifiedName;


    @Column(name = "last_modified_date")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate = new Date();

    @Transient
    private  String valueId;


    @Transient
    private  String onlyId;


    @Transient
    private  String templateTypeNameValue;

    @Transient
    private  String fileNameValue;

    @Transient
    private  String valueStorageId;
    @Transient
    private String siteUid;

    @Transient
    private boolean isEdit;

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

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedName() {
        return lastModifiedName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setLastModifiedName(String lastModifiedName) {
        this.lastModifiedName = lastModifiedName;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTemplateTypeName() {
        return templateTypeName;
    }

    public void setTemplateTypeName(String templateTypeName) {
        this.templateTypeName = templateTypeName;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public String getTemplateTypeNameValue() {
        return templateTypeNameValue;
    }

    public void setTemplateTypeNameValue(String templateTypeNameValue) {
        this.templateTypeNameValue = templateTypeNameValue;
    }

    public String getOnlyId() {
        return onlyId;
    }

    public void setOnlyId(String onlyId) {
        this.onlyId = onlyId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (onlyId != null ? onlyId.hashCode() : 0);
        return hash;
    }

    /**
     * Warning: this method won't work in the case the id fields are not set
     *
     * @param object compare target
     * @return true if the object has the same id
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SystemTemplate)) {
            return false;
        }
        SystemTemplate other = (SystemTemplate) object;
        return (this.onlyId != null || other.onlyId == null) && (this.onlyId == null || this.onlyId.equals(other.onlyId));
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SystemTemplate[ id=" + id + " ]";
    }

}
