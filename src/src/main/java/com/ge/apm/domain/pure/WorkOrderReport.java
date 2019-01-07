package com.ge.apm.domain.pure;

/**
 * Created by 212605082 on 2017/12/8.
 */
public class WorkOrderReport {

    private String assetUID;
    private String assetName;
    private Integer clinicalDeptID;
    private String clinicalDeptName;
    private Integer assetGroup;
    private Integer caseType;
    private String caseTypeName;
    private Integer staffId;
    private String staffName;
    private Long totalSpendTime;
    private Long totalManHour;

    private Long totalCount;
    private Long openCount;
    private Long closeCount;
    private Long cleanCount;
    private Long allCount;

    public String getAssetUID() {
        return assetUID;
    }

    public void setAssetUID(String assetUID) {
        this.assetUID = assetUID;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getOpenCount() {
        return openCount;
    }

    public void setOpenCount(Long openCount) {
        this.openCount = openCount;
    }

    public Long getCloseCount() {
        return closeCount;
    }

    public void setCloseCount(Long closeCount) {
        this.closeCount = closeCount;
    }

    public Long getCleanCount() {
        return cleanCount;
    }

    public void setCleanCount(Long cleanCount) {
        this.cleanCount = cleanCount;
    }

    public Integer getClinicalDeptID() {
        return clinicalDeptID;
    }

    public void setClinicalDeptID(Integer clinicalDeptID) {
        this.clinicalDeptID = clinicalDeptID;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public String getCaseTypeName() {
        return caseTypeName;
    }

    public void setCaseTypeName(String caseTypeName) {
        this.caseTypeName = caseTypeName;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Long getTotalSpendTime() {
        return totalSpendTime;
    }

    public void setTotalSpendTime(Long totalSpendTime) {
        this.totalSpendTime = totalSpendTime;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public Long getTotalManHour() {
        return totalManHour;
    }

    public void setTotalManHour(Long totalManHour) {
        this.totalManHour = totalManHour;
    }
}
