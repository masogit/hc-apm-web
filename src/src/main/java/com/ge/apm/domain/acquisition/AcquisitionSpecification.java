package com.ge.apm.domain.acquisition;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;

@Entity
@Table(name = "acquisition_specification")
public class AcquisitionSpecification {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 大类（设备/软件/器械）
     */
    @Column(name = "major_category")
    private String majorCategory;

    /**
     * 一级分类
     */
    @Column(name = "first_level_category")
    private String firstLevelCategory;

    /**
     * 二级分类
     */
    @Column(name = "second_level_category")
    private String secondLevelCategory;

    /**
     * 分类简述
     */
    @Column(name = "brief_description", length = 500)
    private String briefDescription;

    /**
     * 典型示例
     */
    @Column(name = "typical_examples", length = 1000)
    private String typicalExamples;


    @Column(name = "tenant_uid", length = 50)
    private String tenantUid;

    @Column(name = "device_grade")
    private Integer deviceGrade;
    @Column(name = "modality")
    private String modality;

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public Integer getDeviceGrade() {
        return deviceGrade;
    }

    public void setDeviceGrade(Integer deviceGrade) {
        this.deviceGrade = deviceGrade;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMajorCategory() {
        return majorCategory;
    }

    public void setMajorCategory(String majorCategory) {
        this.majorCategory = majorCategory;
    }

    public String getFirstLevelCategory() {
        return firstLevelCategory;
    }

    public void setFirstLevelCategory(String firstLevelCategory) {
        this.firstLevelCategory = firstLevelCategory;
    }

    public String getSecondLevelCategory() {
        return secondLevelCategory;
    }

    public void setSecondLevelCategory(String secondLevelCategory) {
        this.secondLevelCategory = secondLevelCategory;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getTypicalExamples() {
        return typicalExamples;
    }

    public void setTypicalExamples(String typicalExamples) {
        this.typicalExamples = typicalExamples;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AcquisitionSpecification)) {
            return false;
        }
        AcquisitionSpecification other = (AcquisitionSpecification) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
