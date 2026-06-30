package com.ge.apm.domain.acquisition;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "acquisition_plan_step")
public class AcquisitionPlanStep implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "pid")
    private String pid;

    @Column(name = "step")
    private Integer step;

    @Column(name = "pending_audit_user_uid")
    private String pendingAuditUserUid;

    @Column(name = "pending_audit_user_name")
    private String pendingAuditUserName;

    @Column(name = "actual_audit_user_id")
    private String actualAuditUserUid;

    @Column(name = "actual_audit_user_name")
    private String actualAuditUserName;

    @Column(name = "audit_status")
    private String auditStatus;

    @Column(name = "node_status")
    private String nodeStatus;

    @Column(name = "audit_date")
    private Date auditDate;

    @Column(name = "audit_result")
    private String auditResult;

    @Column(name = "audit_comment")
    private String auditComment;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getActualAuditUserUid() {
        return actualAuditUserUid;
    }

    public void setActualAuditUserUid(String actualAuditUserUid) {
        this.actualAuditUserUid = actualAuditUserUid;
    }

    public String getActualAuditUserName() {
        return actualAuditUserName;
    }

    public void setActualAuditUserName(String actualAuditUserName) {
        this.actualAuditUserName = actualAuditUserName;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
    }

    public String getAuditComment() {
        return auditComment;
    }

    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }

    public String getPendingAuditUserUid() {
        return pendingAuditUserUid;
    }

    public void setPendingAuditUserUid(String pendingAuditUserUid) {
        this.pendingAuditUserUid = pendingAuditUserUid;
    }

    public String getPendingAuditUserName() {
        return pendingAuditUserName;
    }

    public void setPendingAuditUserName(String pendingAuditUserName) {
        this.pendingAuditUserName = pendingAuditUserName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AcquisitionPlanStep)) {
            return false;
        }
        AcquisitionPlanStep other = (AcquisitionPlanStep) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
