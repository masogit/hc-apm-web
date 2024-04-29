/*
 */
package com.ge.apm.domain;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateField;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import webapp.framework.util.TimeUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author 212547631
 */
@Entity
@Table(name = "special_asset_info")
public class SpecialAssetInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name="asset_id")
    private Integer assetId;
    @Column(name="asset_uid")
    private String assetUid;

    //放射诊疗许可证目录内是否登记
    @Column(name="radiotherapy_is_registration", columnDefinition = "boolean default false")
    private boolean radiotherapyIsRegistration;
    //预评情况
    @Column(name="assessment_situation")
    private String assessmentSituation;
    //预评批号
    @Column(name="assessment_no")
    private String assessmentNo;
    //预评文件时间
    @Column(name="assessment_file_date")
    @Temporal(TemporalType.DATE)
    private Date assessmentFileDate;

    //控评批号
    @Column(name="and_assessment_no")
    private String andAssessmentNo;
    //控评批复时间
    @Column(name="and_assessment_approval_date")
    @Temporal(TemporalType.DATE)
    private Date andAssessmentApprovalDate;

    //辐射安全许可证目录内是否登记
    @Column(name="radiation_safety_is_registration", columnDefinition = "boolean default false")
    private boolean radiationSafetyIsRegistration;

    //院内公示、    核技术网公示是否完成
    @Column(name="core_technology_finish", columnDefinition = "boolean default false")
    private boolean coreTechnologyFinish;

    //特种设备类别
    @Column(name="special_asset_type")
    private String specialAssetType;
    //设备管理类别
    @Column(name="asset_management_type")
    private String assetManagementType;


    //设备类型用途
    @Column(name="asset_type_purpose")
    private String assetTypePurpose;

    //办证情况
    @Column(name="registration_situation")
    private String  registrationSituation;

    //铅衣配备情况
    @Column(name="lead_apron_equipment")
    private String leadApronEquipment;

    //铅衣位置情况
    @Column(name="Lead_apron_position")
    private String LeadApronPosition;
    @Column(name="remarks")
    private String remarks;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "special_asset_uid",referencedColumnName = "id", insertable = false,updatable = false)
    private List<AssetAnnualInspectionReport> assetAnnualInspectionReports;


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;


    @Column(name = "created_by_uid", nullable = true, length = 50)
    private String createdByUid;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by_uid", nullable = true, length = 50)
    private String lastModifiedByUid;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id",referencedColumnName = "id", insertable = false,updatable = false)
    private AssetInfo assetInfo;


//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "special_asset_permit_info",  joinColumns = {
//            @JoinColumn(name = "special_asset_id", referencedColumnName = "id",insertable = false,updatable = false)}, inverseJoinColumns = {
//            @JoinColumn(name = "permit_info_uid", referencedColumnName = "uid",insertable = false,updatable = false)})

    @Transient
    private List<PermitInfo> permitInfos;

    @Transient
    @TableField(exist = false)
    private OrgInfo clinicalDeptOrg;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }


    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getInstitutionUID() {
        return institutionUID;
    }

    public void setInstitutionUID(String institutionUID) {
        this.institutionUID = institutionUID;
    }

    public String getHospitalUID() {
        return hospitalUID;
    }

    public void setHospitalUID(String hospitalUID) {
        this.hospitalUID = hospitalUID;
    }

    public String getSiteUID() {
        return siteUID;
    }

    public void setSiteUID(String siteUID) {
        this.siteUID = siteUID;
    }

    public String getCreatedByUid() {
        return createdByUid;
    }

    public void setCreatedByUid(String createdByUid) {
        this.createdByUid = createdByUid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedByUid() {
        return lastModifiedByUid;
    }

    public void setLastModifiedByUid(String lastModifiedByUid) {
        this.lastModifiedByUid = lastModifiedByUid;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isRadiotherapyIsRegistration() {
        return radiotherapyIsRegistration;
    }

    public void setRadiotherapyIsRegistration(boolean radiotherapyIsRegistration) {
        this.radiotherapyIsRegistration = radiotherapyIsRegistration;
    }

    public String getAssessmentSituation() {
        return assessmentSituation;
    }

    public void setAssessmentSituation(String assessmentSituation) {
        this.assessmentSituation = assessmentSituation;
    }

    public String getAssessmentNo() {
        return assessmentNo;
    }

    public void setAssessmentNo(String assessmentNo) {
        this.assessmentNo = assessmentNo;
    }

    public Date getAssessmentFileDate() {
        return assessmentFileDate;
    }

    public void setAssessmentFileDate(Date assessmentFileDate) {
        this.assessmentFileDate = assessmentFileDate;
    }

    public String getAndAssessmentNo() {
        return andAssessmentNo;
    }

    public void setAndAssessmentNo(String andAssessmentNo) {
        this.andAssessmentNo = andAssessmentNo;
    }

    public Date getAndAssessmentApprovalDate() {
        return andAssessmentApprovalDate;
    }

    public void setAndAssessmentApprovalDate(Date andAssessmentApprovalDate) {
        this.andAssessmentApprovalDate = andAssessmentApprovalDate;
    }

    public boolean isRadiationSafetyIsRegistration() {
        return radiationSafetyIsRegistration;
    }

    public void setRadiationSafetyIsRegistration(boolean radiationSafetyIsRegistration) {
        this.radiationSafetyIsRegistration = radiationSafetyIsRegistration;
    }

    public boolean isCoreTechnologyFinish() {
        return coreTechnologyFinish;
    }

    public void setCoreTechnologyFinish(boolean coreTechnologyFinish) {
        this.coreTechnologyFinish = coreTechnologyFinish;
    }

    public String getSpecialAssetType() {
        return specialAssetType;
    }

    public void setSpecialAssetType(String specialAssetType) {
        this.specialAssetType = specialAssetType;
    }

    public String getAssetManagementType() {
        return assetManagementType;
    }

    public void setAssetManagementType(String assetManagementType) {
        this.assetManagementType = assetManagementType;
    }

    public String getAssetTypePurpose() {
        return assetTypePurpose;
    }

    public void setAssetTypePurpose(String assetTypePurpose) {
        this.assetTypePurpose = assetTypePurpose;
    }

    public String getRegistrationSituation() {
        return registrationSituation;
    }

    public void setRegistrationSituation(String registrationSituation) {
        this.registrationSituation = registrationSituation;
    }

    public String getLeadApronEquipment() {
        return leadApronEquipment;
    }

    public void setLeadApronEquipment(String leadApronEquipment) {
        this.leadApronEquipment = leadApronEquipment;
    }

    public String getLeadApronPosition() {
        return LeadApronPosition;
    }

    public void setLeadApronPosition(String leadApronPosition) {
        LeadApronPosition = leadApronPosition;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public List<AssetAnnualInspectionReport> getAssetAnnualInspectionReports() {
        return assetAnnualInspectionReports;
    }

    public void setAssetAnnualInspectionReports(List<AssetAnnualInspectionReport> assetAnnualInspectionReports) {
        this.assetAnnualInspectionReports = assetAnnualInspectionReports;
    }


//    public List<SpecialAssetPermitInfo> getSpecialAssetPermitInfos() {
//        return specialAssetPermitInfos;
//    }
//
//    public void setSpecialAssetPermitInfos(List<SpecialAssetPermitInfo> specialAssetPermitInfos) {
//        this.specialAssetPermitInfos = specialAssetPermitInfos;
//    }


    public List<PermitInfo> getPermitInfos() {
        return permitInfos;
    }

    public void setPermitInfos(List<PermitInfo> permitInfos) {
        this.permitInfos = permitInfos;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SpecialAssetInfo)) {
            return false;
        }
        SpecialAssetInfo other = (SpecialAssetInfo) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }


    public OrgInfo getClinicalDeptOrg() {
        return clinicalDeptOrg;
    }

    public void setClinicalDeptOrg(OrgInfo clinicalDeptOrg) {
        this.clinicalDeptOrg = clinicalDeptOrg;
    }


}
