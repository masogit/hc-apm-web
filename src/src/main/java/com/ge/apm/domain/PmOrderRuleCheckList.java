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
	private String name;//检查项名称
    
    @Column(name = "rule_uid")
    private String ruleUID;//pm_order_rule.uid
    
    @Column(name = "remark")
	private String remark;//备注
    
    @Column(name = "item_id")
    private Integer itemId;
    
    @Column(name = "uid")
    private String uid;
    
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
        PmOrderRuleCheckList other = (PmOrderRuleCheckList) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
