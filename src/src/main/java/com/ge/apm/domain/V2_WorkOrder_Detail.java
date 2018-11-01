package com.ge.apm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "v2_work_order_detail")
public class V2_WorkOrder_Detail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @NotNull
    @Column(name = "wo_id")
    private String woId;

    @Size(max = 60)
    @Column(name = "parts")
    private String parts;

    @Column(name = "parts_quantity")
    private Integer apartsQuantity;

    @Column(name = "parts_price")
    private Double partsPrice;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Size(max = 64)
    @Column(name = "serial_num")
    private String serialNum;

    @Column(name = "warranty_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date warrantyDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "apply_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "estimated_arrive_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedArriveTime;

    //收货时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "arrived_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivedTime;

    @Column(name="closed_price")
    private Double closedPrice;

    @Column(name = "arrived")
    private Boolean arrived;

    @Column(name = "parts_type")
    private String partsType;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name="invoice_num")
    private String invoiceNum;

    @Column(name="closed")
    private Boolean closed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "closed_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedTime;
    @Column(name="supplier")
    private String supplier;

    @Column(name="comment", length = 500)
    private String comment;

    @Column(name="contract_num")
    private String contractNum;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @Column(name = "invoice_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceTime;
    
    //是否完好
    @Column(name = "arrived_good")
    private Boolean arrivedGood;
    
    //好还/坏还
    @Column(name = "arrived_good_return")
    private Boolean arrivedGoodReturn;
    
    //采购时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "purchase_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseTime;
    
    //发货时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "deliver_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliverTime;
    
    //普通/加急,0普通，1加急
    @Column(name="priority")
    private String priority;
    
    @Column(name = "mvs_price")
	private Double mvsPrice;
    
//    @Transient
    @JoinColumn(name = "wo_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @JsonIgnore
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action= NotFoundAction.IGNORE)
    private V2_WorkOrder v2WorkOrder;
    
    @Transient
    private OrgInfo hospitalOrg;
    
    @Transient
    private OrgInfo siteOrg;

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }

    public String getId() {
        return id;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWoId() {
        return woId;
    }

    public void setWoId(String woId) {
        this.woId = woId;
    }

    public String getParts() {
        return parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public Integer getApartsQuantity() {
        return apartsQuantity;
    }

    public void setApartsQuantity(Integer apartsQuantity) {
        this.apartsQuantity = apartsQuantity;
    }

    public Double getPartsPrice() {
        return partsPrice;
    }

    public void setPartsPrice(Double partsPrice) {
        this.partsPrice = partsPrice;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public Date getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(Date warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public Date getEstimatedArriveTime() {
        return estimatedArriveTime;
    }

    public void setEstimatedArriveTime(Date estimatedArriveTime) {
        this.estimatedArriveTime = estimatedArriveTime;
    }

    public Boolean getArrived() {
        return arrived;
    }

    public void setArrived(Boolean arrived) {
        this.arrived = arrived;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }


    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(Date arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public Double getClosedPrice() {
        return closedPrice;
    }

    public void setClosedPrice(Double closedPrice) {
        this.closedPrice = closedPrice;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Date getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(Date closedTime) {
        this.closedTime = closedTime;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public Date getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(Date invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Double getMvsPrice() {
		return mvsPrice;
	}

	public void setMvsPrice(Double mvsPrice) {
		this.mvsPrice = mvsPrice;
	}

	@Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public String getPartsType() {
        return partsType;
    }

    public void setPartsType(String partsType) {
        this.partsType = partsType;
    }

	public Boolean getArrivedGood() {
		return arrivedGood;
	}

	public void setArrivedGood(Boolean arrivedGood) {
		this.arrivedGood = arrivedGood;
	}

	public Boolean getArrivedGoodReturn() {
		return arrivedGoodReturn;
	}

	public void setArrivedGoodReturn(Boolean arrivedGoodReturn) {
		this.arrivedGoodReturn = arrivedGoodReturn;
	}

	public Date getPurchaseTime() {
		return purchaseTime;
	}

	public void setPurchaseTime(Date purchaseTime) {
		this.purchaseTime = purchaseTime;
	}

	public Date getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}

	public V2_WorkOrder getV2WorkOrder() {
		return v2WorkOrder;
	}

	public void setV2WorkOrder(V2_WorkOrder v2WorkOrder) {
		this.v2WorkOrder = v2WorkOrder;
	}

	public OrgInfo getHospitalOrg() {
		return hospitalOrg;
	}

	public void setHospitalOrg(OrgInfo hospitalOrg) {
		this.hospitalOrg = hospitalOrg;
	}

	public OrgInfo getSiteOrg() {
		return siteOrg;
	}

	public void setSiteOrg(OrgInfo siteOrg) {
		this.siteOrg = siteOrg;
	}

	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_WorkOrder_Detail)) {
            return false;
        }
        V2_WorkOrder_Detail other = (V2_WorkOrder_Detail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
