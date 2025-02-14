package com.ge.apm.view.budget.form;

import com.ge.apm.domain.OrgInfo;

import java.util.Date;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 17:21
 */
public class BudgetSearchForm {
    // 申请科室
    private OrgInfo dept;
    // 申请日期 start
    private Date dateStart;
    // 申请日期 end
    private Date dateEnd;
    // 申请编号
    private String applicationNum;
    // 采购编号
    private String purchaseNum;
    // 设备分类
    private Integer assetGroup;
    // 设备二级分类
    private Integer assetGroupSecondary;
    // 申请人
    private Integer applicantId;
    // 申请人
    private String applicantName;
    // 设备名称
    private String assetName;
    //申购年份
    private Date applicantYear;

    private String temporallyPurchaseType;

    public String getTemporallyPurchaseType() {
        return temporallyPurchaseType;
    }

    public void setTemporallyPurchaseType(String temporallyPurchaseType) {
        this.temporallyPurchaseType = temporallyPurchaseType;
    }

    public OrgInfo getDept() {
        return dept;
    }

    public String getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(String purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public void setDept(OrgInfo dept) {
        this.dept = dept;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(String applicationNum) {
        this.applicationNum = applicationNum == null ? null : applicationNum.trim();
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
    }

    public Date getApplicantYear() {
        return applicantYear;
    }

    public void setApplicantYear(Date applicantYear) {
        this.applicantYear = applicantYear;
    }
}