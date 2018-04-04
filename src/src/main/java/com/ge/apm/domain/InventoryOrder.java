package com.ge.apm.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by 212605082 on 2017/6/19.
 */
@Entity
@Table(name = "inventory_order")
public class InventoryOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    @Column(name = "site_id")
    private Integer siteId;
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Column(name = "plan_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date planStartTime;
    @Column(name = "plan_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date planEndTime;
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_finished")
    private Boolean isFinished;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_order_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<InventoryOrderDetail> inventoryOrderDetailList = new ArrayList<>();
    @Column(name = "assignee_org", columnDefinition = "TEXT")
    private String assigneeOrg;
    @Transient
    @JsonProperty
    private String name = "";
    @Transient
    @JsonProperty
    private String clinicalDeptLabel = "";

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<InventoryOrderDetail> getInventoryOrderDetailList() {
        return inventoryOrderDetailList;
    }

    public void setInventoryOrderDetailList(List<InventoryOrderDetail> inventoryOrderDetailList) {
        this.inventoryOrderDetailList = inventoryOrderDetailList;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean finished) {
        isFinished = finished;
    }

    public String getAssigneeOrg() {
        return assigneeOrg;
    }

    public void setAssigneeOrg(String assigneeOrg) {
        this.assigneeOrg = assigneeOrg;
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

    public String getName() {
        if (this.planStartTime != null && this.planStartTime != null) {
            this.name = dateFormat.format(this.planStartTime) + " 至 " + dateFormat.format(this.planEndTime);
        }
        this.name += this.isFinished ? " 已完成" : " 进行中";
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClinicalDeptLabel() {
        if(StringUtils.isNotEmpty(this.assigneeOrg)) {
            List<Map> clinicalDeptList = JSON.parseArray(this.assigneeOrg, Map.class);
            int i=0;
            for (Map map : clinicalDeptList) {
//                this.clinicalDeptLabel += map.get("orgName") + " ";
            	this.clinicalDeptLabel += map.get("orgName") + "\n";
            	if(i==2){
            		this.clinicalDeptLabel +=" 等...";
            		break;
            	}
            	i++;
            }
        }
        return this.clinicalDeptLabel;
    }

    public void setClinicalDeptLabel(String clinicalDeptLabel) {
        this.clinicalDeptLabel = clinicalDeptLabel;
    }
}
