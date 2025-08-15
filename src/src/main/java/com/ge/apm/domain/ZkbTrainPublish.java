package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "zkb_train_publish")
public class ZkbTrainPublish {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "train_date")
    private Date trainDate;

    //培训地点
    @Column(name = "location")
    private String location;

    //培训方式
    @Column(name = "train_mode")
    private String trainMode;

    //报名截止日期
    @Column(name = "sign_deadline")
    private Date signDeadline;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

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
        if (!(object instanceof ZkbTrainPublish)) {
            return false;
        }
        ZkbTrainPublish other = (ZkbTrainPublish) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
