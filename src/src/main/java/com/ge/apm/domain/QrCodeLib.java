/*
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "qr_code_lib")
public class QrCodeLib implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;
//    @Basic(optional = false)
//    @Column(name = "site_id")
//    private int siteId;
//    @Basic(optional = false)
//    @Column(name = "hospital_id")
//    private int hospitalId;
    @Basic(optional = false)
    @Column(name = "qr_code")
    @Size(max = 64)
    private String qrCode;
    @Column(name = "issue_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date issueDate;
    @Column(name = "submit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submitDate;
    @Column(name = "submit_wechat_id")
    private String submitWechatId;

    /**
     * 同步 新增资产建档-文字表述
     */
    @Column(name = "comment")
    private String comment;

    @Column(name = "feedback")
    private String feedback;
    @Basic(optional = false)
    @Column(name = "status")
    private int status;
    @Column(name = "asset_name")
    private String assetName;
    @Column(name = "asset_group")
    private Integer assetGroup;
    @Column(name = "asset_class") // 1 医疗设备  2 后勤资产 3 信息类设备
    private Integer assetClass;//管理分类
    @Column(name = "org_id")
    private Integer orgId;
    @Column(name = "user_id")
    private Integer userId;

    /**码类型 null/0标识模板码 1标识一机一码**/
    @Column(name = "type")
    private Integer type;

    @Column(name = "serial_num")
    private String serialNum;
    @Column(name = "model_num")
    private String modelNum;
    @Column(name = "manufacture_date")
    private Date manufactureDate;
    @Column(name = "parent_asset_id")
    private Integer parentAssetId;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "asset_dept_id")
    private Integer assetDeptId;

    @Column(name = "depart_num")
    private String departNum;

    @Column(name = "manufacture_id")
    private String manufactureId;

    @Column(name = "make_short_name")
    private String makerShortName;

    @Column(name = "registration_no")
    private String registrationNo;

    /**
     * 安装位置
     */
    @Size(max = 64)
    @Column(name = "location_name")
    private String locationName;

    /**
     * 备注- 同步 assetInfo.comments 字段
     */
    @Column(name = "comments")
    @Size( max = 256)
    private String comments;

    @Column(name = "asset_type")
    private String assetType;

    //是否急救设备[1:急救类,2:非急救类]
    @Column(name = "emergency_equipment")
    private Integer emergencyEquipment;
    // 使用期限
    @Column(name = "lifecycle")
    private Double lifecycleInYear;

    @Column(name = "asset_classification32")
    private String assetClassification32;
    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(String manufactureId) {
        this.manufactureId = manufactureId;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
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

    public Integer getAssetDeptId() {
        return assetDeptId;
    }

    public void setAssetDeptId(Integer assetDeptId) {
        this.assetDeptId = assetDeptId;
    }

    public QrCodeLib() {
    }


    public QrCodeLib(Integer id, int siteId, int hospitalId, String qrCode, Date issueDate, int status) {
        this.qrCode = qrCode;
        this.issueDate = issueDate;
        this.status = status;
    }

    @PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public int getSiteId() {
//        return siteId;
//    }
//
//    public void setSiteId(int siteId) {
//        this.siteId = siteId;
//    }
//
//    public int getHospitalId() {
//        return hospitalId;
//    }
//
//    public void setHospitalId(int hospitalId) {
//        this.hospitalId = hospitalId;
//    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getSubmitWechatId() {
        return submitWechatId;
    }

    public void setSubmitWechatId(String submitWechatId) {
        this.submitWechatId = submitWechatId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public Integer getParentAssetId() {
        return parentAssetId;
    }

    public void setParentAssetId(Integer parentAssetId) {
        this.parentAssetId = parentAssetId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QrCodeLib)) {
            return false;
        }
        QrCodeLib other = (QrCodeLib) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.QrCodeLib[ uid=" + uid + " ]";
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAssetClass() { return assetClass; }

    public void setAssetClass(Integer assetClass) { this.assetClass = assetClass; }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public Integer getEmergencyEquipment() {
        return emergencyEquipment;
    }

    public void setEmergencyEquipment(Integer emergencyEquipment) {
        this.emergencyEquipment = emergencyEquipment;
    }

    public Double getLifecycleInYear() {
        return lifecycleInYear;
    }

    public void setLifecycleInYear(Double lifecycleInYear) {
        this.lifecycleInYear = lifecycleInYear;
    }

    public String getAssetClassification32() {
        return assetClassification32;
    }

    public void setAssetClassification32(String assetClassification32) {
        this.assetClassification32 = assetClassification32;
    }
}
