package com.ge.apm.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pm_order_rule_item")
public class PmOrderRuleItemOrderRuleItem {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
	private Integer id;
    
    @Column(name = "tenant_uid")
	private String tenantUID;//租户uid
    
    @Column(name = "period")
	private Integer period;//周期，以‘月’为单位
    
    @Column(name = "name")
	private String name;//检查项名称
    
    @Column(name = "remark")
	private String remark;//备注
    
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
	public String getTenantUID() {
		return tenantUID;
	}
	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
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
        PmOrderRuleItemOrderRuleItem other = (PmOrderRuleItemOrderRuleItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
