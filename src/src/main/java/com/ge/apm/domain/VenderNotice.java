package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "vender_notice")
public class VenderNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "status")
    private Integer status;

    @Column(name = "notice_user")
    private String noticeUser;

    @Column(name = "notice_content")
    private String noticeContent;

    @Column(name = "notice_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date noticeDate;

    @Column(name = "reply_user")
    private String replyUser;

    @Column(name = "reply_content")
    private String replyContent;

    @Column(name = "reply_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VenderNotice)) {
            return false;
        }
        VenderNotice other = (VenderNotice) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
