package com.ge.apm.domain;

import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "pm_order_rule_check_list")
public class PmOrderRuleCheckList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
    
    @Column(name = "period")
	private Integer period;//周期，以‘月’为单位
    
    @Column(name = "name")
	@Size(min = 1, max = 128)
	private String name;//检查项名称
    
    @Column(name = "rule_uid")
    private String ruleUID;//pm_order_rule.uid
    
    @Column(name = "remark")
	@Size(max = 1024)
	private String remark;//项目内容
    
    @Column(name = "item_id")
    private Integer itemId;
    
    @Column(name = "uid")
    private String uid;

	@Column(name = "maintenance_type")
	private Integer maintenanceType = 2;//项目类型（日常维护、周期维护）

	@Column(name = "executable")
	private Integer executable = 1;//评估（可执行、不可执行）

	@Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
	private String tenantUID;

	@Column(name = "institution_uid", columnDefinition = "CHAR(32)")
	private String institutionUID;

	@Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
	private String hospitalUID;

	@Column(name = "site_uid", columnDefinition = "CHAR(32)")
	private String siteUID;

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

	@PrePersist
    public void initializeUid() {
        if(uid==null)
            uid = UUID.randomUUID().toString().replace("-", "");
    }

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getRuleUID() {
		return ruleUID;
	}
	public void setRuleUID(String ruleUID) {
		this.ruleUID = ruleUID;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getMaintenanceType() {
		return maintenanceType;
	}

	public void setMaintenanceType(Integer maintenanceType) {
		this.maintenanceType = maintenanceType;
	}

	public Integer getExecutable() {
		return executable;
	}

	public void setExecutable(Integer executable) {
		this.executable = executable;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PmOrderRuleCheckList)) {
            return false;
        }
        PmOrderRuleCheckList other = (PmOrderRuleCheckList) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
