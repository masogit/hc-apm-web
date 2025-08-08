package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Entity
@Table(name = "zkb_train_course")
public class ZkbTrainCourse {

    public enum CourseTypeEnum{
        normal, advance
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "short_name")
    private String shortName;

    //课程类型普通 normal  进阶 advance
    @Column(name = "course_type")
    private String courseType;

    @Column(name = "normal_course_id")
    private String normalCourseId;

    @Column(name = "description")
    @Size(max = 2000)
    private String description;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Transient
    private Integer excelIdex;

    @Transient
    private String errorMsg = "";

    public void setErrorMsg(UploadFileErrorTypeEnum type,String errorMsg) {
        this.errorMsg += errorMsg;
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
        if (!(object instanceof ZkbCourseTrain)) {
            return false;
        }
        ZkbTrainCourse other = (ZkbTrainCourse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
