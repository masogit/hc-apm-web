package com.ge.apm.domain.budget;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * lyj
 */
@Entity
@Table(name = "budget_application_asset")
public class BudgetApplicationAsset implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    // 申请id
    @Column(name = "application_id",updatable = false,columnDefinition = "CHAR(32)")
    private String applicationId;

    // 申请编号
    @Column(name = "application_num")
    private String applicationNum;


    // 申请科室id
    @Column(name = "applicant_dept_id")
    private Integer applicationDeptId;

    /**
     * *设备名称
     */
    @Column(name = "application_asset_name")
    private String applicationAssetName;


    /**
     * *设备品类
     */
    @Column(name = "application_asset_category")
    private String applicationAssetCategory;

    /**
     * *推荐品牌
     */
    @Column(name = "recommended_brand")
    private String recommendedBrand;

    /**
     * *规格
     */
    @Column(name = "standards")
    private String standards;

    /**
     * *型号
     */
    @Column(name = "model")
    private String model;

    // 申请数量
    @Column(name = "application_quantity")
    private Integer applicationQuantity;

    // 单位
    @Column(name = "unit")
    private String unit;

    // 预算单价
    @Column(name = "application_price", scale = 2)
    private BigDecimal applicationPrice;

    // 预算总价
    @Column(name = "application_amount", scale = 2)
    private BigDecimal applicationAmount;

    /**
     * *经费来源
     */
    @Column(name = "source_funding")
    private String sourceFunding;

    /**
     * *申请理由
     */
    @Column(name = "application_reason")
    @Size(max = 4000)
    private String applicationReason;

    /**
     * *拟购设备用途
     */
    @Column(name = "equipment_usage")
    private String equipmentUsage;

    /**
     * *需求说明/情况说明
     */
    @Column(name = "description")
    @Size(max = 4000)
    private String description;

    // 科室现有同类设备数量
    @Column(name = "similar_devices_number")
    private Integer similarDevicesNumber;

    //科室现有同类设备利用情况
    @Column(name = "similar_devices_use")
    private String similarDevicesUse;

    //是否采购进口产品
    @Column(name = "imported_or")
    private String importedOr;

    //申购仪器设备的主要功能
    @Column(name = "major_function")
    private String majorFunction;

    //申购仪器设备的配置
    @Column(name = "device_configuration")
    private String deviceConfiguration;

    //申购仪器设备的主要技术参数及要求
    @Column(name = "technical_parameter")
    private String technicalParameter;

    //申购仪器设备在本单位医疗、教学、科研工作中的利用
    @Column(name = "equipment_utilization")
    private String equipmentUtilization;

    //社会、经济效益分析
    @Column(name = "benefit_analysis")
    private String benefitAnalysis;

    //同类产品比较
    @Column(name = "product_comparison")
    private String productComparison;

    //使用年限
    @Column(name = "service_life")
    private String serviceLife;

    //收费标准
    @Column(name = "charging_standards")
    private BigDecimal chargingStandards;

    //年预估收入
    @Column(name = "year_income")
    private BigDecimal yearIncome;

    //所需耗材及使用量
    @Column(name = "usage")
    private String usage;

    //是否在手术室使用
    @Column(name = "used_operating_room")
    private String usedOperatingRoom;

    //设备是否有放置场地
    @Column(name = "place_site")
    private String placeSite;

    //护士长是否已签名
    @Column(name = "signature_nurse")
    private String signatureNurse;

    // 系统对接
    @Column(name = "system_docking")
    private String systemDocking;

    //连接外网
    @Column(name = "connect_network")
    private String connectNetwork;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    /*offline线下审批数据*/

    // 线下审批时间日期
    @Column(name = "offline_approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date offlineApprovalDate;
    // 线下审批意见
    @Column(name = "offline_approval_opinions")
    @Size(max = 4000)
    private String offlineApprovalOpinions;
    // 线下审批结论
    @Column(name = "offline_approval_conclusion")
    @Size(max = 4000)
    private String offlineApprovalConclusion;
    // 线下审批结论
    @Column(name = "offline_approval_remark")
    @Size(max = 4000)
    private String offlineApprovalRemark;
    // 线下审批记录人
    @Column(name = "offline_approval_recorder")
    private String offlineApprovalRecorder;

    @Column(name = "offline_application_quantity")
    private Integer offlineApplicationQuantity;

    @Column(name = "offline_application_price", scale = 2)
    private BigDecimal offlineApplicationPrice;

    @Column(name = "offline_application_amount", scale = 2)
    private BigDecimal offlineApplicationAmount;

    /*offline线下审批数据*/
//    序号	使用科室	设备品目	数量（台）	预算（万元）	国产占比（国家财政部）	国产占比（省财政部）	科室意见（是否进口）	公告及信息收集情况	主要功能和配置	主要参数和要求	意向品牌	拦标价（万元）	备注
    /*采购确认信息*/
    // 申请科室id
    @Column(name = "confirm_applicant_dept_id")
    private Integer confirmApplicationDeptId;
    // 申请科室
    @Transient
    private String confirmApplicationDeptName;

    @Column(name = "confirm_equipment_category")
    private String confirmEquipmentCategory;

    @Column(name = "confirm_quantity")
    private Integer confirmQuantity;

    @Column(name = "confirm_price", scale = 2)
    private BigDecimal confirmPrice;

    @Transient
    private BigDecimal confirmAmount;
    @Transient
    private BigDecimal confirmMaximumTotalPrice;

    @Column(name = "confirm_national_finance")
    private String confirmNationalFinance;

    @Column(name = "confirm_provincial_finance")
    private String confirmProvincialFinance;

    @Column(name = "confirm_import")
    private String confirmImport;

    @Column(name = "confirm_notice")
    private String confirmNotice;

    @Column(name = "confirm_major_function")
    private String confirmMajorFunction;

    @Column(name = "confirm_parameters")
    private String confirmParameters;

    @Column(name = "confirm_brand")
    private String confirmBrand;

    @Column(name = "confirm_maximum_price", scale = 2)
    private BigDecimal confirmMaximumPrice;

    @Column(name = "confirm_rmark", scale = 2)
    @Size(max = 1024)
    private String confirmRemark;

    /*采购确认信息*/
    // 删除标识 0: 未删除；1：已删除
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    // 申请人
    @Transient
    private String applicantName;

    // 申请科室
    @Transient
    private String applicationDeptName;

    // 申请日期
    @Transient
    private Date applicationDate;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "application_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private BudgetApplication budgetApplication;

    public Integer getOfflineApplicationQuantity() {
        return offlineApplicationQuantity;
    }

    public void setOfflineApplicationQuantity(Integer offlineApplicationQuantity) {
        this.offlineApplicationQuantity = offlineApplicationQuantity;
    }

    public BigDecimal getOfflineApplicationPrice() {
        return offlineApplicationPrice;
    }

    public void setOfflineApplicationPrice(BigDecimal offlineApplicationPrice) {
        this.offlineApplicationPrice = offlineApplicationPrice;
    }

    public BigDecimal getOfflineApplicationAmount() {
        return offlineApplicationAmount;
    }

    public void setOfflineApplicationAmount(BigDecimal offlineApplicationAmount) {
        this.offlineApplicationAmount = offlineApplicationAmount;
    }

    public String getConfirmApplicationDeptName() {
        return confirmApplicationDeptName;
    }

    public void setConfirmApplicationDeptName(String confirmApplicationDeptName) {
        this.confirmApplicationDeptName = confirmApplicationDeptName;
    }

    public Integer getConfirmApplicationDeptId() {
        return confirmApplicationDeptId;
    }

    public void setConfirmApplicationDeptId(Integer confirmApplicationDeptId) {
        this.confirmApplicationDeptId = confirmApplicationDeptId;
    }

    public String getConfirmEquipmentCategory() {
        return confirmEquipmentCategory;
    }

    public void setConfirmEquipmentCategory(String confirmEquipmentCategory) {
        this.confirmEquipmentCategory = confirmEquipmentCategory;
    }

    public Integer getConfirmQuantity() {
        return confirmQuantity;
    }

    public void setConfirmQuantity(Integer confirmQuantity) {
        this.confirmQuantity = confirmQuantity;
    }

    public BigDecimal getConfirmPrice() {
        return confirmPrice;
    }

    public void setConfirmPrice(BigDecimal confirmPrice) {
        this.confirmPrice = confirmPrice;
    }

    public String getConfirmNationalFinance() {
        return confirmNationalFinance;
    }

    public void setConfirmNationalFinance(String confirmNationalFinance) {
        this.confirmNationalFinance = confirmNationalFinance;
    }

    public String getConfirmProvincialFinance() {
        return confirmProvincialFinance;
    }

    public void setConfirmProvincialFinance(String confirmProvincialFinance) {
        this.confirmProvincialFinance = confirmProvincialFinance;
    }

    public String getConfirmImport() {
        return confirmImport;
    }

    public void setConfirmImport(String confirmImport) {
        this.confirmImport = confirmImport;
    }

    public String getConfirmNotice() {
        return confirmNotice;
    }

    public void setConfirmNotice(String confirmNotice) {
        this.confirmNotice = confirmNotice;
    }

    public String getConfirmMajorFunction() {
        return confirmMajorFunction;
    }

    public void setConfirmMajorFunction(String confirmMajorFunction) {
        this.confirmMajorFunction = confirmMajorFunction;
    }

    public String getConfirmParameters() {
        return confirmParameters;
    }

    public void setConfirmParameters(String confirmParameters) {
        this.confirmParameters = confirmParameters;
    }

    public String getConfirmBrand() {
        return confirmBrand;
    }

    public void setConfirmBrand(String confirmBrand) {
        this.confirmBrand = confirmBrand;
    }

    public BigDecimal getConfirmMaximumPrice() {
        return confirmMaximumPrice;
    }

    public void setConfirmMaximumPrice(BigDecimal confirmMaximumPrice) {
        this.confirmMaximumPrice = confirmMaximumPrice;
    }

    public String getConfirmRemark() {
        return confirmRemark;
    }

    public void setConfirmRemark(String confirmRemark) {
        this.confirmRemark = confirmRemark;
    }

    public Date getOfflineApprovalDate() {
        return offlineApprovalDate;
    }

    public void setOfflineApprovalDate(Date offlineApprovalDate) {
        this.offlineApprovalDate = offlineApprovalDate;
    }

    public String getOfflineApprovalOpinions() {
        return offlineApprovalOpinions;
    }

    public void setOfflineApprovalOpinions(String offlineApprovalOpinions) {
        this.offlineApprovalOpinions = offlineApprovalOpinions;
    }

    public String getOfflineApprovalConclusion() {
        return offlineApprovalConclusion;
    }

    public void setOfflineApprovalConclusion(String offlineApprovalConclusion) {
        this.offlineApprovalConclusion = offlineApprovalConclusion;
    }

    public String getOfflineApprovalRemark() {
        return offlineApprovalRemark;
    }

    public void setOfflineApprovalRemark(String offlineApprovalRemark) {
        this.offlineApprovalRemark = offlineApprovalRemark;
    }

    public String getOfflineApprovalRecorder() {
        return offlineApprovalRecorder;
    }

    public void setOfflineApprovalRecorder(String offlineApprovalRecorder) {
        this.offlineApprovalRecorder = offlineApprovalRecorder;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicationDeptName() {
        return applicationDeptName;
    }

    public void setApplicationDeptName(String applicationDeptName) {
        this.applicationDeptName = applicationDeptName;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUsedOperatingRoom() {
        return usedOperatingRoom;
    }

    public void setUsedOperatingRoom(String usedOperatingRoom) {
        this.usedOperatingRoom = usedOperatingRoom;
    }

    public Integer getSimilarDevicesNumber() {
        return similarDevicesNumber;
    }

    public void setSimilarDevicesNumber(Integer similarDevicesNumber) {
        this.similarDevicesNumber = similarDevicesNumber;
    }

    public String getSimilarDevicesUse() {
        return similarDevicesUse;
    }

    public void setSimilarDevicesUse(String similarDevicesUse) {
        this.similarDevicesUse = similarDevicesUse;
    }

    public String getImportedOr() {
        return importedOr;
    }

    public void setImportedOr(String importedOr) {
        this.importedOr = importedOr;
    }

    public String getMajorFunction() {
        return majorFunction;
    }

    public void setMajorFunction(String majorFunction) {
        this.majorFunction = majorFunction;
    }

    public String getDeviceConfiguration() {
        return deviceConfiguration;
    }

    public void setDeviceConfiguration(String deviceConfiguration) {
        this.deviceConfiguration = deviceConfiguration;
    }

    public String getTechnicalParameter() {
        return technicalParameter;
    }

    public void setTechnicalParameter(String technicalParameter) {
        this.technicalParameter = technicalParameter;
    }

    public String getEquipmentUtilization() {
        return equipmentUtilization;
    }

    public void setEquipmentUtilization(String equipmentUtilization) {
        this.equipmentUtilization = equipmentUtilization;
    }

    public String getBenefitAnalysis() {
        return benefitAnalysis;
    }

    public void setBenefitAnalysis(String benefitAnalysis) {
        this.benefitAnalysis = benefitAnalysis;
    }

    public String getProductComparison() {
        return productComparison;
    }

    public void setProductComparison(String productComparison) {
        this.productComparison = productComparison;
    }

    public String getServiceLife() {
        return serviceLife;
    }

    public void setServiceLife(String serviceLife) {
        this.serviceLife = serviceLife;
    }

    public BigDecimal getChargingStandards() {
        return chargingStandards;
    }

    public void setChargingStandards(BigDecimal chargingStandards) {
        this.chargingStandards = chargingStandards;
    }

    public BigDecimal getYearIncome() {
        return yearIncome;
    }

    public void setYearIncome(BigDecimal yearIncome) {
        this.yearIncome = yearIncome;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getPlaceSite() {
        return placeSite;
    }

    public void setPlaceSite(String placeSite) {
        this.placeSite = placeSite;
    }

    public String getSignatureNurse() {
        return signatureNurse;
    }

    public void setSignatureNurse(String signatureNurse) {
        this.signatureNurse = signatureNurse;
    }

    public String getSystemDocking() {
        return systemDocking;
    }

    public void setSystemDocking(String systemDocking) {
        this.systemDocking = systemDocking;
    }

    public String getConnectNetwork() {
        return connectNetwork;
    }

    public void setConnectNetwork(String connectNetwork) {
        this.connectNetwork = connectNetwork;
    }

    public String getApplicationAssetName() {
        return applicationAssetName;
    }

    public void setApplicationAssetName(String applicationAssetName) {
        this.applicationAssetName = applicationAssetName;
    }

    public String getApplicationAssetCategory() {
        return applicationAssetCategory;
    }

    public void setApplicationAssetCategory(String applicationAssetCategory) {
        this.applicationAssetCategory = applicationAssetCategory;
    }

    public String getRecommendedBrand() {
        return recommendedBrand;
    }

    public void setRecommendedBrand(String recommendedBrand) {
        this.recommendedBrand = recommendedBrand;
    }

    public String getStandards() {
        return standards;
    }

    public void setStandards(String standards) {
        this.standards = standards;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSourceFunding() {
        return sourceFunding;
    }

    public void setSourceFunding(String sourceFunding) {
        this.sourceFunding = sourceFunding;
    }

    public String getApplicationReason() {
        return applicationReason;
    }

    public void setApplicationReason(String applicationReason) {
        this.applicationReason = applicationReason;
    }

    public String getEquipmentUsage() {
        return equipmentUsage;
    }

    public void setEquipmentUsage(String equipmentUsage) {
        this.equipmentUsage = equipmentUsage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BudgetApplicationAsset)) {
            return false;
        }
        BudgetApplicationAsset other = (BudgetApplicationAsset) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }


    public Integer getApplicationQuantity() {
        return applicationQuantity;
    }

    public void setApplicationQuantity(Integer applicationQuantity) {
        this.applicationQuantity = applicationQuantity;
    }

    public BigDecimal getApplicationPrice() {
        return applicationPrice;
    }

    public void setApplicationPrice(BigDecimal applicationPrice) {
        this.applicationPrice = applicationPrice;
    }

    public BigDecimal getApplicationAmount() {
        return applicationAmount == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : applicationAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setApplicationAmount(BigDecimal applicationAmount) {
        this.applicationAmount = applicationAmount;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(String applicationNum) {
        this.applicationNum = applicationNum;
    }

    public Integer getApplicationDeptId() {
        return applicationDeptId;
    }

    public void setApplicationDeptId(Integer applicationDeptId) {
        this.applicationDeptId = applicationDeptId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public BudgetApplication getBudgetApplication() {
        return budgetApplication;
    }

    public void setBudgetApplication(BudgetApplication budgetApplication) {
        this.budgetApplication = budgetApplication;
    }

    public BigDecimal getConfirmAmount() {
        if(this!=null && this.getConfirmPrice()!= null && this.getConfirmQuantity()!= null){
            return this.getConfirmPrice().multiply(new BigDecimal(this.getConfirmQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return confirmAmount;

    }

    public void setConfirmAmount(BigDecimal confirmAmount) {
        this.confirmAmount = confirmAmount;
    }

    public BigDecimal getConfirmMaximumTotalPrice() {
        if(this!=null && this.getConfirmMaximumPrice()!= null && this.getConfirmQuantity()!= null){
            return this.getConfirmMaximumPrice().multiply(new BigDecimal(this.getConfirmQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return confirmMaximumTotalPrice;
    }

    public void setConfirmMaximumTotalPrice(BigDecimal confirmMaximumTotalPrice) {
        this.confirmMaximumTotalPrice = confirmMaximumTotalPrice;
    }
}
