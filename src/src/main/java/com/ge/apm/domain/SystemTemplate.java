package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    private String fileType;  //模板类型

    @Column(name = "template_type")
    private String templateType;  //模板类型

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


    public enum SystemTemplateEnum{
        OUT_STOCKED("OUT_STOCKED","出库模板"),
        IN_STOCKED("IN_STOCKED","入库模板"),
        TECHNOLOGY("TECHNOLOGY","技术验收模板"),
        BUSINESS("BUSINESS","商务验收模板"),
        CLINICAL("CLINICAL","临床验收模板"),
        DETAIL("DETAIL","验收资料详单"),
        FIXASSET_BATCH("FIXASSET_BATCH","固定资产验收单(面单)"),
        FIXASSET_BATCH_BOTTOM("FIXASSET_BATCH_BOTTOM","固定资产验收单(底单)"),
        SUPPLIER_IMPORT("SUPPLIER_IMPORT","供应商导入模板"),
        DEPARTMENT_CONVERT("DEPARTMENT_CONVERT","设备转科模板"),
        ASSET_COUNTRYAREA_TEMPLATE("ASSET_COUNTRYAREA_TEMPLATE","资产导入模板-县域租户"),
        ASSET_COUNTRYAREA_TEMPLATE_EN("ASSET_COUNTRYAREA_TEMPLATE_EN","资产导入模板-县域租户_en"),
        ASSET_TEMPLATE("ASSET_TEMPLATE","资产导入模板"),
        ASSET_TEMPLATE_EN("ASSET_TEMPLATE_EN","资产导入模板_en"),


        ;
        String code;
        String name;

        SystemTemplateEnum(String code,String name) {
            this.code = code;

            this.name = name;
        }

        public static String getName(String code) {
            for (SystemTemplateEnum template : SystemTemplateEnum.values()) {
                if (template.getCode().equals(code)) {
                    return template.name;
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
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
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.SystemTemplate[ id=" + id + " ]";
    }

}
