package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author : 503189362
 * @date : 2020/4/22
 * @description :
 */
@Entity
@Table(name = "zkb_contract_course")
public class ZkbContractCourse implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "course_id", referencedColumnName = "id")
    private ZkbCourse zkbCourse;

    @Column(name = "course_id")
    private String courseId;

    /**
     * 课程合同号
     */
    @Column(name = "course_contract_no")
    private String courseContractNo;

    @Column(name = "course_count")
    private Integer courseCount;

    @Column(name = "course_count_total")
    private Integer courseCountTotal;

    @Column(name = "course_count_apply")
    private Integer courseCountApply;

    @Column(name = "course_count_use")
    private Integer courseCountUse;

    @Column(name = "main_course_count")
    private Double mainCourseCount;

    @Column(name = "main_course_count_total")
    private Double mainCourseCountTotal;

    @Column(name = "main_course_count_apply")
    private Double mainCourseCountApply;

    @Column(name = "main_course_count_use")
    private Double mainCourseCountUse;

    @Column(name = "contract_id")
    @Size(max = 32)
    private String contractId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ZkbCourse getZkbCourse() {
        return zkbCourse;
    }

    public void setZkbCourse(ZkbCourse zkbCourse) {
        this.zkbCourse = zkbCourse;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public Integer getCourseCountTotal() {
        return courseCountTotal;
    }

    public void setCourseCountTotal(Integer courseCountTotal) {
        this.courseCountTotal = courseCountTotal;
    }

    public Integer getCourseCountApply() {
        return courseCountApply;
    }

    public void setCourseCountApply(Integer courseCountApply) {
        this.courseCountApply = courseCountApply;
    }

    public Integer getCourseCountUse() {
        return courseCountUse;
    }

    public void setCourseCountUse(Integer courseCountUse) {
        this.courseCountUse = courseCountUse;
    }

    public String getCourseContractNo() {
        return courseContractNo;
    }

    public void setCourseContractNo(String courseContractNo) {
        this.courseContractNo = courseContractNo;
    }

    public Double getMainCourseCount() {
        return mainCourseCount;
    }

    public void setMainCourseCount(Double mainCourseCount) {
        this.mainCourseCount = mainCourseCount;
    }

    public Double getMainCourseCountTotal() {
        return mainCourseCountTotal;
    }

    public void setMainCourseCountTotal(Double mainCourseCountTotal) {
        this.mainCourseCountTotal = mainCourseCountTotal;
    }

    public Double getMainCourseCountApply() {
        return mainCourseCountApply;
    }

    public void setMainCourseCountApply(Double mainCourseCountApply) {
        this.mainCourseCountApply = mainCourseCountApply;
    }

    public Double getMainCourseCountUse() {
        return mainCourseCountUse;
    }

    public void setMainCourseCountUse(Double mainCourseCountUse) {
        this.mainCourseCountUse = mainCourseCountUse;
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
        if (!(object instanceof ZkbContractCourse)) {
            return false;
        }
        if(this==object){
            return true;
        }
        ZkbContractCourse other = (ZkbContractCourse) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbContractCourse[ id=" + id + " ]";
    }
}
