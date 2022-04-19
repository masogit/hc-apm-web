package com.ge.apm.view.budget;

import java.util.Date;

/**
 * @author Yangfan, Liu
 * @date 4/8/2022 17:21
 */
public class BudgetSearchForm {
    // 申请科室
    private Integer deptId;
    // 申请日期
    private Date dateStart;
    // 申请日期
    private Date dateEnd;
    // 设备类型
    private String assetType;
    // 申请编号
    private String applicationNum;
    // 申请人
    private Integer applicantId;
    // 设备名称
    private String assetName;
    // 设备二级分类
    private Integer asset2ndClassify;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType == null ? null : assetType.trim();
    }

    public String getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(String applicationNum) {
        this.applicationNum = applicationNum == null ? null : applicationNum.trim();
    }

    public Integer getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Integer applicantId) {
        this.applicantId = applicantId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
    }

    public Integer getAsset2ndClassify() {
        return asset2ndClassify;
    }

    public void setAsset2ndClassify(Integer asset2ndClassify) {
        this.asset2ndClassify = asset2ndClassify;
    }
}