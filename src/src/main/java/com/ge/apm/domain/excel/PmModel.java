package com.ge.apm.domain.excel;

public class PmModel{

    private String siteName;
    private String assetName;
    private String serialNum;
    private String departNum;
    private String functionType;
    private String ruleLevel;
    private String pmType;
    private String clinicalDeptName;
    private String pmRuleName;
    private String pmRulePeriod;
    private String creatorName;
    private String ownerName;
    private String pmNum;
    private String currentStepName;
    private String intExtType;
    private String planTime;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(String ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public String getPmRuleName() {
        return pmRuleName;
    }

    public void setPmRuleName(String pmRuleName) {
        this.pmRuleName = pmRuleName;
    }

    public String getPmRulePeriod() {
        return pmRulePeriod;
    }

    public void setPmRulePeriod(String pmRulePeriod) {
        this.pmRulePeriod = pmRulePeriod;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPmNum() {
        return pmNum;
    }

    public void setPmNum(String pmNum) {
        this.pmNum = pmNum;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public String getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(String intExtType) {
        this.intExtType = intExtType;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }


    public PmModel(String siteName, String assetName, String serialNum, String departNum, String functionType, String ruleLevel, String pmType, String clinicalDeptName, String pmRuleName, String pmRulePeriod, String creatorName, String ownerName, String pmNum, String currentStepName, String intExtType, String planTime) {
        this.siteName = siteName;
        this.assetName = assetName;
        this.serialNum = serialNum;
        this.departNum = departNum;
        this.functionType = functionType;
        this.ruleLevel = ruleLevel;
        this.pmType = pmType;
        this.clinicalDeptName = clinicalDeptName;
        this.pmRuleName = pmRuleName;
        this.pmRulePeriod = pmRulePeriod;
        this.creatorName = creatorName;
        this.ownerName = ownerName;
        this.pmNum = pmNum;
        this.currentStepName = currentStepName;
        this.intExtType = intExtType;
        this.planTime = planTime;
    }
}
