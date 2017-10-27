package com.ge.apm.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "pm_order_rule")
public class PmOrderRule {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
    
    @Column(name = "name")
	private String name;
    
    @Column(name = "asset_group")
	private Integer assetGroup;//设备类型
    
    @Column(name = "manufacture")
	private String manufacture;//品牌
    
    @Column(name = "function_type")
	private String functionType;//设备型号
    
    @Column(name = "approve_user_id")
	private Integer approveUserId;//审核人
    
    @Column(name = "owner_id")
	private Integer ownerId;//制定者
    
    @Column(name = "created_date")
	private Date createdDate;//制定日期
    
    @Column(name = "period")
	private Integer period;//保养周期,单位‘月’
    
    @Column(name = "check_list_uid")
	private String checkListUID;//检查项uid
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name = "status")
    private Integer status;//0、待审核 1、已审核
    
    @Column(name="uid",columnDefinition = "CHAR(32)")
    private String uid;

    @PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }
    
	public String getUid() {
		return uid;
	}
    
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTenantUID() {
		return tenantUID;
	}
	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAssetGroup() {
		return assetGroup;
	}
	public void setAssetGroup(Integer assetGroup) {
		this.assetGroup = assetGroup;
	}
	public String getManufacture() {
		return manufacture;
	}
	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getFunctionType() {
		return functionType;
	}
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	public Integer getApproveUserId() {
		return approveUserId;
	}
	public void setApproveUserId(Integer approveUserId) {
		this.approveUserId = approveUserId;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public String getCheckListUID() {
		return checkListUID;
	}
	public void setCheckListUID(String checkListUID) {
		this.checkListUID = checkListUID;
	}
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PmOrderRule)) {
            return false;
        }
        PmOrderRule other = (PmOrderRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
