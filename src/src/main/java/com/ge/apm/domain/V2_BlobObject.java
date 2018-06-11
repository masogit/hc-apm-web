package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "v2_blob_object")
public class V2_BlobObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String BO_TYPE_ServiceRequest = "ServiceRequest";
    public static final String BO_TYPE_PmRequest = "PmRequest";
    public static final String BO_TYPE_WorkOrder = "WorkOrder";
    public static final String BO_TYPE_WorkOrderStep = "WorkOrderStep";
    public static final String BO_TYPE_WorkOrderDetail = "WorkOrderDetail";
    public static final String BO_TYPE_InspectionOrder = "InspectionOrder";
    public static final String BO_TYPE_AssetInfo = "AssetInfo";
    public static final String BO_TYPE_AssetContract = "AssetContract";
    public static final String BO_TYPE_QRCodeLib = "QRCodeLib";
    public static final String BO_TYPE_PmOrder = "PmOrder";
    public static final String BO_TYPE_ArrivalListItem = "ArrivalListItem";
    public static final String BO_TYPE_ArrivalList = "ArrivalList";
    public static final String BO_TYPE_TableDefinition = "TableDefinition";
    public static final String BO_SUB_TYPE_DOCS = "Docs";
    public static final String BO_SUB_TYPE_IMG = "Image";
    public static final String BO_SUB_TYPE_AssetInfo_Measuring = "Measuring";



    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "bo_uuid")
    private String boUuid;

    @Column(name = "bo_id")
    private Integer boId;

    @Column(name = "bo_type")
    private String boType;
    
    @Column(name = "bo_sub_type")
    private String boSubType;

    @Column(name = "object_storage_id")
    private String objectStorageId;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_size")
    private Long objectSize;

    @Column(name = "object_source")
    private V2_BlobObject_Source objectSource = V2_BlobObject_Source.STORED_IN_DB;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;
    
    @JoinColumn(insertable=false,updatable=false, name = "bo_uuid", referencedColumnName = "uid",nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private AssetInfo assetInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public Integer getBoId() {
        return boId;
    }

    public void setBoId(Integer boId) {
        this.boId = boId;
    }

    public String getObjectStorageId() {
        return objectStorageId;
    }

    public void setObjectStorageId(String objectStorageId) {
        this.objectStorageId = objectStorageId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(Long objectSize) {
        this.objectSize = objectSize;
    }

    public V2_BlobObject_Source getObjectSource() {
        return objectSource;
    }

    public void setObjectSource(V2_BlobObject_Source objectSource) {
        this.objectSource = objectSource;
    }

    public String getBoType() {
        return boType;
    }

    public void setBoType(String boType) {
        this.boType = boType;
    }

    public String getBoSubType() {
        return boSubType;
    }

    public void setBoSubType(String boSubType) {
        this.boSubType = boSubType;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_BlobObject)) {
            return false;
        }
        V2_BlobObject other = (V2_BlobObject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
