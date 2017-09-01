package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_agent")
public class UserAgent implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @Basic(optional = false)
    @Column(name = "from_user_id")
    private Integer fromUserId;
    
    @Basic(optional = false)
    @Column(name = "to_user_id")
    private Integer toUserId;
    
    @Basic(optional = false)
    @Column(name = "fromDate")
    private Date fromDate;
    
    @Basic(optional = false)
    @Column(name = "toDate")
    private Date toDate;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Basic(optional = false)
    @Column(name = "agent_type")
    private Integer agentType;//1、管理员配置 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(Integer fromUserId) {
		this.fromUserId = fromUserId;
	}

	public Integer getToUserId() {
		return toUserId;
	}

	public void setToUserId(Integer toUserId) {
		this.toUserId = toUserId;
	}



	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getAgentType() {
		return agentType;
	}

	public void setAgentType(Integer agentType) {
		this.agentType = agentType;
	}
    
    
    
}
