package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "service_request_msg")
public class ServiceRequestMsg implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    @Basic(optional = true)
    @Column(name = "sr_id")
    private String srId;
    @Basic(optional = false)
    @Column(name = "sender_user_id")
    private Integer senderUserId;
    @Column(name = "sender_user_name")
    private String senderUserName;
    @Column(name = "send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;
    @Column(name = "message")
    private String message;
    @Column(name = "audit_user_uid")
    private String auditUserUid;
    @Column(name = "audit_user_name")
    private String auditUserName;

    public String getAuditUserUid() {
        return auditUserUid;
    }

    public void setAuditUserUid(String auditUserUid) {
        this.auditUserUid = auditUserUid;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }


    public ServiceRequestMsg() {
    }

    public ServiceRequestMsg(String id) {
        this.id = id;
    }

    public ServiceRequestMsg(String id, String srId, int senderUserId) {
        this.id = id;
        this.srId = srId;
        this.senderUserId = senderUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public Integer getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        if (!(object instanceof ServiceRequestMsg)) {
            return false;
        }
        ServiceRequestMsg other = (ServiceRequestMsg) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkOrderMsg[ id=" + id + " ]";
    }
    
}
