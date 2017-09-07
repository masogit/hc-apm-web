package com.ge.apm.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v2_work_order_workflow_config")
public class V2_WorkOrderWorkflowConfig implements Serializable{
	
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "timeout_dispatch")
    private Integer timeoutDispatch;
    
    @Column(name = "timeout_accept")
    private Integer timeoutAccept;
   
    @Column(name = "timeout_close")
    private Integer timeoutClose;
    
    @Column(name="max_message_count")
    private Integer maxMessageCount;//推送消息的次数上限
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
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

	public Integer getMaxMessageCount() {
		return maxMessageCount;
	}

	public void setMaxMessageCount(Integer maxMessageCount) {
		this.maxMessageCount = maxMessageCount;
	}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimeoutDispatch() {
        return timeoutDispatch;
    }

    public void setTimeoutDispatch(Integer timeoutDispatch) {
        this.timeoutDispatch = timeoutDispatch;
    }

    public Integer getTimeoutAccept() {
        return timeoutAccept;
    }

    public void setTimeoutAccept(Integer timeoutAccept) {
        this.timeoutAccept = timeoutAccept;
    }

    public Integer getTimeoutClose() {
        return timeoutClose;
    }

    public void setTimeoutClose(Integer timeoutClose) {
        this.timeoutClose = timeoutClose;
    }
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_WorkOrderWorkflowConfig)) {
            return false;
        }
        V2_WorkOrderWorkflowConfig other = (V2_WorkOrderWorkflowConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    
}
