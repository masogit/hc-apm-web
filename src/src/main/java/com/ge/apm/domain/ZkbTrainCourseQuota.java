package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
@Table(name = "zkb_train_course_quota")
public class ZkbTrainCourseQuota {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "modality")
    private String modality;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "contract_no")
    private String contractNo;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "quota")
    private Integer quota = 0;

    @Column(name = "total_quota")
    private Integer totalQuota = 0;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "course_id", referencedColumnName = "id")
    private ZkbTrainCourse zkbTrainCourse;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbCourseTrain)) {
            return false;
        }
        ZkbTrainCourseQuota other = (ZkbTrainCourseQuota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
