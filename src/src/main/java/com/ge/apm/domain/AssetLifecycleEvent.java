package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by lsg on 31/1/2018.
 */
@Entity
@Table(name = "asset_lifecycle_event")
public class AssetLifecycleEvent implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "event_type")
    private Integer eventType; // 1-workOrder,2-pmOrder,3-外部工单(emg-external_work_order),4-外部保养,5-外部远程保养(ePM)

    @Column(name = "event_uid")
    private String eventUid;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    @JoinColumn(name = "report_object_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private V2_BlobObject srReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pm_report_object_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private V2_BlobObject pmReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checklist_report_object_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private V2_BlobObject checkListReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sha_report_object_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private V2_BlobObject shaReport;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getReportStorageId() {
        if (srReport != null) {
            return srReport.getObjectStorageId();
        }
        return "";
    }

    public V2_BlobObject getSrReport() {
        return srReport;
    }

    public void setSrReport(V2_BlobObject srReport) {
        this.srReport = srReport;
    }

    public V2_BlobObject getPmReport() {
        return pmReport;
    }

    public void setPmReport(V2_BlobObject pmReport) {
        this.pmReport = pmReport;
    }

    public V2_BlobObject getCheckListReport() {
        return checkListReport;
    }

    public void setCheckListReport(V2_BlobObject checkListReport) {
        this.checkListReport = checkListReport;
    }

    public V2_BlobObject getShaReport() {
        return shaReport;
    }

    public void setShaReport(V2_BlobObject shaReport) {
        this.shaReport = shaReport;
    }
}
