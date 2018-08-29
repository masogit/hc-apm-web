/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;


import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetReqTypeRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import webapp.framework.web.WebUtil;

/**
 *
 * @author caoyong
 */
@Entity
@Table(name = "shared_asset_request")//借还申请
public class SharedAssetRequest implements Serializable{
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;
    
    @Column(name = "applicant")//借用申请人
    private String applicant;

    @Formula("(select a.name from user_account a where a.login_name = applicant)")
    private String applicantName;
    
    @Column(name = "msg_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date msgEndTime;
    
    @Column(name = "msg_ccepter")
    private String msgAccepter;
    
    @Column(name = "msg_begin_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date msgBeginTime;
    
    @Column(name = "applicant_phone")//借用申请人虚拟电话网
    private String applicantPhone;
    
    @Column(name = "accepter")//接收人\
    private String accepter;
    
    @Column(name = "accepter_number")//接收人编号
    private String accepterNumber;
    
    @Column(name = "clinical_dept_uid")//借用申请科室id
    private String clinicalDeptUid;
    
    @Column(name = "clinical_dept_name")//借用申请科室名称
    private String clinicalDeptName;
    
    @Column(name = "apply_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyTime;//申请日期
    
    @Column(name = "re_applicant")//归还申请人
    private String reApplicant;
  
    @Column(name = "re_applicant_phone")//归还申请人虚拟电话网
    private String reApplicantPhone;
    
    @Column(name = "re_clinical_dept_uid")//归还申请科室id
    private String reClinicalDeptUid;
    
    @Column(name = "re_clinical_dept_name")//归还申请科室名称
    private String reClinicalDeptName;
    
    @Column(name = "re_apply_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reApplyTime;//归还申请日期
    
    @Column(name = "invalid_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invalidTime;//申请失效日期
    
    @Column(name = "state")//状态
    private int state;
    
    @Column(name = "remark")//备注
    private String remark;
    
    @Column(name = "type")//类型：正常借还、维修借还、工程师维护
    private String type;
    
    @Column(name = "owner")
    private String owner;
    
    //新增字段（数据权限四个字段）
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    
    @Column(name = "create_by")
    private String createBy;//创建人
    
    @Column(name = "modify_by")
    private String modifyBy;//修改人
    
    @Column(name = "create_date")
    @Temporal(TemporalType.DATE)
    private Date createDate;//创建日期
    
    @Column(name = "modify_date")
    @Temporal(TemporalType.DATE)
    private Date modifyDate;//修改日期
    
    @Transient//设备信息（临时）
    private String applyAssetInfo;
    
    @Column(name = "behavior")//借用归还状态 1借用 2归还
    private Integer behavior;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getAccepter() {
        return accepter;
    }

    public void setAccepter(String accepter) {
        this.accepter = accepter;
    }

    public String getAccepterNumber() {
        return accepterNumber;
    }

    public void setAccepterNumber(String accepterNumber) {
        this.accepterNumber = accepterNumber;
    }

    public String getClinicalDeptUid() {
        return clinicalDeptUid;
    }

    public void setClinicalDeptUid(String clinicalDeptUid) {
        this.clinicalDeptUid = clinicalDeptUid;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    
    public void setReApplicant(String reApplicant) {
        this.reApplicant = reApplicant;
    }

    public void setReApplicantPhone(String reApplicantPhone) {
        this.reApplicantPhone = reApplicantPhone;
    }

    public void setReClinicalDeptUid(String reClinicalDeptUid) {
        this.reClinicalDeptUid = reClinicalDeptUid;
    }

    public void setReClinicalDeptName(String reClinicalDeptName) {
        this.reClinicalDeptName = reClinicalDeptName;
    }

    public void setReApplyTime(Date reApplyTime) {
        this.reApplyTime = reApplyTime;
    }

    public String getReApplicant() {
        return reApplicant;
    }

    public String getReApplicantPhone() {
        return reApplicantPhone;
    }

    public String getReClinicalDeptUid() {
        return reClinicalDeptUid;
    }

    public String getReClinicalDeptName() {
        return reClinicalDeptName;
    }

    public Date getReApplyTime() {
        return reApplyTime;
    }
    
    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
    
    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof SharedAssetRequest)) {
            return false;
        }
        SharedAssetRequest other = (SharedAssetRequest) object;
        if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.Iou.Iou[ id=" + getId() + " ]";
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @PrePersist
    public void initializeUid() {
        if(getId()==null)
            setId(UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getApplyAssetInfo() {
        return applyAssetInfo;
    }

    public void setApplyAssetInfo(String applyAssetInfo) {
        this.applyAssetInfo = applyAssetInfo;
    }
    
    @Transient
    private List<SharedAssetReqType> sartList;//借用申请明细，优化保存机制
    
    @Transient
    SharedAssetReqTypeRepository apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
    
    @Transient
    SharedAssetReqDetailRepository asdDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
    
    public List<SharedAssetReqType> getSartList() {
        if(sartList!=null) return sartList;
        if (id != null) {
            List<SharedAssetReqType> dataList = apdDao.findSwapTypeId(id);
            if (dataList != null && dataList.size() > 0) {
                sartList = dataList;
            } else {
                sartList = new ArrayList<SharedAssetReqType>();
            }
        }
        return sartList;
    }

    public void setSartList(List<SharedAssetReqType> sartList) {
        this.sartList = sartList;
    }
    
    @Transient
    private List<SharedAssetReqDetail> sardList;//录入设备，优化保存机制

    public List<SharedAssetReqDetail> getSardList() {
        if(sardList!=null) return sardList;
        if(id != null){
            List<SharedAssetReqDetail> dataList = asdDao.findByReturnId(id);
            if (dataList != null && dataList.size() > 0) {
                sardList = dataList;
            } else {
                sardList = new ArrayList<SharedAssetReqDetail>();
            }
        }
        return sardList;
    }

    public void setSardList(List<SharedAssetReqDetail> sardList) {
        this.sardList = sardList;
    }
    
     @Transient
    private List<AssetInfo> infoList;//录入设备，优化保存机制

    public List<AssetInfo> getInfoList() {
        if(infoList!=null) return infoList;
        
        infoList = new ArrayList<AssetInfo>();
        return infoList;
    }

    public void setInfoList(List<AssetInfo> infoList) {
        this.infoList = infoList;
    }

    /**
     * @return the msgAccepter
     */
    public String getMsgAccepter() {
        return msgAccepter;
    }

    /**
     * @param msgAccepter the msgAccepter to set
     */
    public void setMsgAccepter(String msgAccepter) {
        this.msgAccepter = msgAccepter;
    }

    /**
     * @return the msgBeginTime
     */
    public Date getMsgBeginTime() {
        return msgBeginTime;
    }

    /**
     * @param msgBeginTime the msgBeginTime to set
     */
    public void setMsgBeginTime(Date msgBeginTime) {
        this.msgBeginTime = msgBeginTime;
    }

    /**
     * @return the msgEndTime
     */
    public Date getMsgEndTime() {
        return msgEndTime;
    }

    /**
     * @param msgEndTime the msgEndTime to set
     */
    public void setMsgEndTime(Date msgEndTime) {
        this.msgEndTime = msgEndTime;
    
    }

    /**
     * @return the behavior
     */
    public Integer getBehavior() {
        return behavior;
    }

    /**
     * @param behavior the behavior to set
     */
    public void setBehavior(Integer behavior) {
        this.behavior = behavior;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
}