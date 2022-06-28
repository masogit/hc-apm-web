package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "v2_work_order_man_hours")
public class V2_WorkOrderManHours implements Serializable, Cloneable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //工单id
    @Column(name = "wo_id")
    private String woId;

    //报修单号
    @Column(name = "sr_num")
    private String srNum;

    //工时所属人
    @Column(name = "man_hours_owner")
    private String manHoursOwner;

    //工时日期
    @Column(name = "man_hours_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date manHoursDate;

    //工作工时
    @Column(name = "work_hours")
    private Double workHours;

    //差旅工时
    @Column(name = "travel_hours")
    private Double travelHours;

    //等待工时
    @Column(name = "waiting_hours")
    private Double waitingHours;

    @Column(name="comment", length = 250)
    private String comment;

    //创建人
    @Column(name = "created_by")
    private String createdBy;

    //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    //修改人
    @Column(name = "modified_by")
    private String modifiedBy;

    //修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "modified_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWoId() { return woId; }

    public void setWoId(String woId) { this.woId = woId; }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public String getManHoursOwner() {
        return manHoursOwner;
    }

    public void setManHoursOwner(String manHoursOwner) {
        this.manHoursOwner = manHoursOwner;
    }

    public Double getWorkHours() { return workHours; }

    public void setWorkHours(Double workHours) {
        this.workHours = workHours;
    }

    public Double getTravelHours() {
        return travelHours;
    }

    public void setTravelHours(Double travelHours) {
        this.travelHours = travelHours;
    }

    public Double getWaitingHours() {
        return waitingHours;
    }

    public void setWaitingHours(Double waitingHours) {
        this.waitingHours = waitingHours;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Date getManHoursDate() {
        return manHoursDate;
    }

    public void setManHoursDate(Date manHoursDate) {
        this.manHoursDate = manHoursDate;
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
        if (!(object instanceof V2_WorkOrderManHours)) {
            return false;
        }
        V2_WorkOrderManHours other = (V2_WorkOrderManHours) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
