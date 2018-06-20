/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "contract_info_item")
public class ContractInfoItem implements Serializable {

    public enum ContractInfoItemType {
        ExistAsset, Purchase, ArrivalledItem
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "contract_id", columnDefinition = "CHAR(32)")
    private String contractId;

    @JoinColumn(name = "contract_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ContractInfo contractInfo;

    @Column(name = "item_type")
    private String itemType;

    //'ExistAsset',  'ArrivalledItem'
    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false, nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;

    //到货单条目
    @Column(name = "arrival_item_id", columnDefinition = "CHAR(32)")
    private String arrivalItemId;

    @JoinColumn(name = "arrival_item_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private ArrivalListItem arrivalListItem;

    //订购项
    @Column(name = "purchase_item_id", columnDefinition = "CHAR(32)")
    private String PurchaseItemId;

    
    //申请科室
    @Column(name = "apply_dept")
    private String applyDept;
//    //交货期
//    @Basic(optional = true)
//    @Column(name = "delivered_date")
//    @Temporal(TemporalType.DATE)
//    private Date deliveredDate;

    //通知供货时间
    @Basic(optional = true)
    @Column(name = "notice_date")
    @Temporal(TemporalType.DATE)
    private Date noticeDate;

    //应到货时间
    @Basic(optional = true)
    @Column(name = "planned_arrive_date")
    @Temporal(TemporalType.DATE)
    private Date plannedArriveDate;

//    //实际到货时间
//    @Basic(optional = true)
//    @Column(name = "arrive_date")
//    @Temporal(TemporalType.DATE)
//    private Date arriveDate;

//    //是否在规定时间内到货
//    @Column(name = "in_time_arrived")
//    private Boolean inTimeArrived;

    //合同内设备名称
    @Column(name = "contract_asset_name")
    private String contractAssetName;

    //合同标价（万元）
    @Column(name = "contract_asset_price")
    private Double contractAssetPrice;

    //合同内设备数量
    @Column(name = "contract_asset_quantity")
    private Integer contractAssetQuantity;

    //制造商
    @Column(name = "manufacturer")
    private String manufacturer;

    //规格型号
    @Column(name = "pattern")
    private String pattern;

    //交货期(天)
    @Column(name = "delivery_days")
    private Integer deliveryDays;

    //预估单价(万元)
    @Column(name = "estimated_price")
    private Double estimatedPrice;

    //价值类别
    @Column(name = "price_type")
    private String priceType;

    //发票号
    @Size(max = 64)
    @Column(name = "invoice_num")
    private String invoiceNum;

    //报送首付日期
    @Basic(optional = true)
    @Column(name = "down_payment_date")
    @Temporal(TemporalType.DATE)
    private Date downPaymentDate;

    //供应商申请尾款日期
    @Basic(optional = true)
    @Column(name = "apply_payment_date")
    @Temporal(TemporalType.DATE)
    private Date applyPaymentDate;

    //报送尾款日期
    @Basic(optional = true)
    @Column(name = "tail_payment_date")
    @Temporal(TemporalType.DATE)
    private Date tailPaymentDate;

    //备注
    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public ContractInfo getContractInfo() {
        return contractInfo;
    }

    public void setContractInfo(ContractInfo contractInfo) {
        this.contractInfo = contractInfo;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Date getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(Date noticeDate) {
        this.noticeDate = noticeDate;
    }

    public Date getPlannedArriveDate() {
        return plannedArriveDate;
    }

    public void setPlannedArriveDate(Date plannedArriveDate) {
        this.plannedArriveDate = plannedArriveDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getArrivalItemId() {
        return arrivalItemId;
    }

    public void setArrivalItemId(String arrivalItemId) {
        this.arrivalItemId = arrivalItemId;
    }

    public String getContractAssetName() {
        return contractAssetName;
    }

    public void setContractAssetName(String contractAssetName) {
        this.contractAssetName = contractAssetName;
    }

    public Double getContractAssetPrice() {
        return contractAssetPrice;
    }

    public void setContractAssetPrice(Double contractAssetPrice) {
        this.contractAssetPrice = contractAssetPrice;
    }

    public Integer getContractAssetQuantity() {
        return contractAssetQuantity;
    }

    public void setContractAssetQuantity(Integer contractAssetQuantity) {
        this.contractAssetQuantity = contractAssetQuantity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getPurchaseItemId() {
        return PurchaseItemId;
    }

    public void setPurchaseItemId(String PurchaseItemId) {
        this.PurchaseItemId = PurchaseItemId;
    }

    public ArrivalListItem getArrivalListItem() {
        return arrivalListItem;
    }

    public void setArrivalListItem(ArrivalListItem arrivalListItem) {
        this.arrivalListItem = arrivalListItem;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public Date getDownPaymentDate() {
        return downPaymentDate;
    }

    public void setDownPaymentDate(Date downPaymentDate) {
        this.downPaymentDate = downPaymentDate;
    }

    public Date getApplyPaymentDate() {
        return applyPaymentDate;
    }

    public void setApplyPaymentDate(Date applyPaymentDate) {
        this.applyPaymentDate = applyPaymentDate;
    }

    public Date getTailPaymentDate() {
        return tailPaymentDate;
    }

    public void setTailPaymentDate(Date tailPaymentDate) {
        this.tailPaymentDate = tailPaymentDate;
    }

    public String getApplyDept() {
        return applyDept;
    }

    public void setApplyDept(String applyDept) {
        this.applyDept = applyDept;
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
        if (!(object instanceof ContractInfoItem)) {
            return false;
        }
        ContractInfoItem other = (ContractInfoItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
