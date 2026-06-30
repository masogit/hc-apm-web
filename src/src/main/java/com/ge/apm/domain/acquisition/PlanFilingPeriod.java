package com.ge.apm.domain.acquisition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.budget.PurchaseBudgetConfig;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "plan_filing_period")
public class PlanFilingPeriod  implements Serializable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    @Getter
    @Setter
    private String id;

    /**
     * 年度
     */
    @Column(name = "year")
    @Getter@Setter
    private String year;

    /**
     * 开始日期
     */
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter@Setter
    private Date startDate;

    /**
     * 结束日期
     */
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter@Setter
    private Date endDate;

    /**
     * 租户
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Getter@Setter
    private String tenantUID;

    @Column(name = "created_by", length = 50)
    @Getter@Setter
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter@Setter
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    @Getter@Setter
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Getter@Setter
    private Date lastModifiedDate;

    @Column(name = "operater_name")
    @Getter@Setter
    private String operaterName;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlanFilingPeriod)) {
            return false;
        }
        PlanFilingPeriod other = (PlanFilingPeriod) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

}
