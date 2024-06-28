package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503189829
 * @create 2022/2/14
 * @since 1.0.0
 */
@Entity
@Table(name = "approve_schedule")
@Data
public class ApproveSchedule implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 保修工单号
     */
    @Column(name = "sr_num")
    private String srNum;
    /**
     * 备件提交人
     */
    @Column(name = "parts_by")
    private String partsBy;
    /**
     * 业务UID
     */
    @Column(name = "business_uid")
    private String businessUid;
    /**
     * 备件总金额
     */
    @Column(name = "parts_price")
    private Double partsPrice;
    /**
     *  审核总进度 1未审核 2通过 3不通过 4进行中
     */
    @Column(name = "approve_status")
    private Integer approveStatus;
    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ApproveSchedule)) {
            return false;
        }
        ApproveSchedule other = (ApproveSchedule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
