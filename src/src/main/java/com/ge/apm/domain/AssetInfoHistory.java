package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "asset_info_history")
public class AssetInfoHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "asset_uid",columnDefinition = "CHAR(32)")
    private String assetUid;

    @Column(name = "name")
    private String name;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "inventory_order_detail",columnDefinition = "text")
    private String inventoryOrderDetail;

    @Column(name = "asset_lifecycle_event",columnDefinition = "text")
    private String assetLifecycleEvent;

    @Column(name = "work_order",columnDefinition = "text")
    private String workOrder;

    @Column(name = "work_order_step",columnDefinition = "text")
    private String workOrderStep;

    @Column(name = "pm_order",columnDefinition = "text")
    private String pmOrder;

    @Column(name = "asset_info",columnDefinition = "text")
    private String assetInfo;
    @Column(name = "inventory_issue",columnDefinition = "text")
    private String inventoryIssue;
    @Column(name = "contract_info_item",columnDefinition = "text")
    private String contractInfoItem;
    @Column(name = "create_by")
    private String createBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_date")
    private Date createDate;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getInventoryOrderDetail() {
        return inventoryOrderDetail;
    }

    public void setInventoryOrderDetail(String inventoryOrderDetail) {
        this.inventoryOrderDetail = inventoryOrderDetail;
    }

    public String getAssetLifecycleEvent() {
        return assetLifecycleEvent;
    }

    public void setAssetLifecycleEvent(String assetLifecycleEvent) {
        this.assetLifecycleEvent = assetLifecycleEvent;
    }

    public String getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public String getWorkOrderStep() {
        return workOrderStep;
    }

    public void setWorkOrderStep(String workOrderStep) {
        this.workOrderStep = workOrderStep;
    }

    public String getPmOrder() {
        return pmOrder;
    }

    public void setPmOrder(String pmOrder) {
        this.pmOrder = pmOrder;
    }

    public String getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(String assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getInventoryIssue() {
        return inventoryIssue;
    }

    public void setInventoryIssue(String inventoryIssue) {
        this.inventoryIssue = inventoryIssue;
    }

    public String getContractInfoItem() {
        return contractInfoItem;
    }

    public void setContractInfoItem(String contractInfoItem) {
        this.contractInfoItem = contractInfoItem;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
