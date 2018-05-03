/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "arrival_list_item")
public class ArrivalListItem implements Serializable {

    
    public static final String ACK_STATUS_NONE = "none";
    public static final String ACK_STATUS_PASS = "pass";
    public static final String ACK_STATUS_REJECT = "reject";
    
    public static final String INSTALL_STATUS_NEW = "new";
    public static final String INSTALL_STATUS_INSTORAGE = "instorage";
    public static final String INSTALL_STATUS_REJECT = "rejected";
    public static final String INSTALL_STATUS_FINISHED = "finished";
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "arrival_list_id", columnDefinition = "CHAR(32)")
    private String arrivalListId;

    //到货清单序号
    @Column(name = "item_num")
    private Integer itemNum;

    //设备使用科室
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;

    //资产财产编号
    @Size(max = 64)
    @Column(name = "financing_num")
    private String financingNum;
    
    //制造商序列号
    @Size(max = 64)
    @Column(name = "serial_num")
    private String serialNum;
    
    //第三方检测单位
    @Size(max = 64)
    @Column(name = "third_party_name")
    private String thirdPartyName;
    
    //检测日期
    @Column(name = "detecting_date")
    @Temporal(TemporalType.DATE)
    private Date detectingDate;
    
    //验收人
    @Size(max = 64)
    @Column(name = "ack_name")
    private String ackName;

    @JoinColumn(name = "arrival_list_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ArrivalList arrivalList;

    //商务验收状态 [none:待验收,pass:验收通过,reject:验收不通过]
    @Size(max = 64)
    @Column(name = "business_ack_status")
    private String businessAckStatus = ACK_STATUS_NONE;
    
    //商务验收日期
    @Column(name = "business_ack_date")
    @Temporal(TemporalType.DATE)
    private Date businessAckDate;
    
    //技术验收状态 [none:待验收,pass:验收通过,reject:验收不通过]
    @Size(max = 64)
    @Column(name = "technical_ack_status")
    private String technicalAckStatus = ACK_STATUS_NONE;
    
    //技术验收日期
    @Column(name = "technical_ack_date")
    @Temporal(TemporalType.DATE)
    private Date technicalAckDate;
    
    //临床验收状态 [none:待验收,pass:验收通过,reject:验收不通过]
    @Size(max = 64)
    @Column(name = "clinical_ack_status")
    private String clinicalAckStatus = ACK_STATUS_NONE;
    
    //技术验收日期
    @Column(name = "clinical_ack_date")
    @Temporal(TemporalType.DATE)
    private Date clinicalAckDate;
    
    //安装流程状态 [new:安装中,instorage:已入库,rejected:已退货]
    @Size(max = 64)
    @Column(name = "install_status")
    private String installStatus = INSTALL_STATUS_NEW;
    
    //创建出的资产UID
    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUID;
    
    //当前处理人
    @Column(name = "current_person_uid", columnDefinition = "CHAR(32)")
    private String currentPersonUID;
    
    //安装工时
    @Column(name = "work_hours")
    private Double workHours;
    
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date lastModifiedDate;
    
    
    //fields for AssetInfo
    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;//资产别名

    @Column(name = "function_group")
    private Integer functionGroup;//功能分组   医疗器械分类

    @Column(name = "function_grade")
    private Integer functionGrade;//功能等级

    @Size(max = 64)
    @Column(name = "maitanance")
    private String maitanance;//维修商

    @Size(max = 32)
    @Column(name = "maitanance_tel")
    private String maitananceTel;//维修电话

    @Size(max = 64)
    @Column(name = "national_standard_code")
    private String nationalStandardCode;//2010国家编码

    @Size(max = 64)
    @Column(name = "depart_num")
    private String departNum;//设备编号

    @Size(max = 64)
    @Column(name = "barcode")
    private String barcode;//条码

    @Size(max = 64)
    @Column(name = "modality_id")
    private String modalityId;//院内IT系统编号

    @Size(max = 64)
    @Column(name = "location_code")
    private String locationCode;//安装位置编码

    @Size(max = 64)
    @Column(name = "location_name")
    private String locationName;//安装位置

    @Column(name = "asset_owner_uid")
    private String assetOwnerUID;//维修负责人

    @Column(name = "asset_owner_uid2")
    private String assetOwnerUID2;//维修负责人2

    @Column(name = "asset_owner_uid3")
    private String assetOwnerUID3;//维修负责人3

    @Column(name = "manufact_date")
    @Temporal(TemporalType.DATE)
    private Date manufactDate;//出厂日期

    @Column(name = "purchase_date")
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;//采购日期

//    @Column(name = "arrive_date")
//    @Temporal(TemporalType.DATE)
//    private Date arriveDate;//到货日期

    @Column(name = "install_date")
    @Temporal(TemporalType.DATE)
    private Date installDate;//启用日期

    @Column(name = "warranty_date")
    @Temporal(TemporalType.DATE)
    private Date warrantyDate;//维保截止日期

    @Column(name = "terminate_date")
    @Temporal(TemporalType.DATE)
    private Date terminateDate;//报废日期

    @Column(name = "last_pm_date")
    @Temporal(TemporalType.DATE)
    private Date lastPmDate;//上次维护日期

    @Column(name = "last_metering_date")
    @Temporal(TemporalType.DATE)
    private Date lastMeteringDate;//上次计量日期

    @Column(name = "last_qa_date")
    @Temporal(TemporalType.DATE)
    private Date lastQaDate;//上次质检日期

    @Column(name = "last_stocktake_date")
    @Temporal(TemporalType.DATE)
    private Date lastStockTakeDate;//

    @Column(name = "clinical_owner_uid")
    private String clinicalOwnerUid;//使用责任人

    @Size(max = 64)
    @Column(name = "registration_no")
    private String registrationNo;//注册证号

    @Column(name = "factory_warranty_date")
    @Temporal(TemporalType.DATE)
    private Date factoryWarrantyDate;//质保日期

    @Size(max = 256)
    @Column(name = "qr_code")
    private String qrCode;//二维码
    
    @Column(name = "purchase_price")
    private Double purchasePrice;//采购价格(元)

    @Column(name = "salvage_value")
    private Double salvageValue;//最终残值(元)

    @Column(name = "lifecycle")
    private Integer lifecycle;//使用年限

    @Column(name = "depreciation_method")
    private Integer depreciationMethod;//折旧算法

    @Column(name = "eam_id")
    private String eamId;//EAM系统编号

    @Column(name = "system_id")
    private String systemId;//SystemID/厂商报修ID

    @Column(name = "parent_asset_id")
    private Integer parentAssetId;

    @Column(name = "inventory_status")
    private Integer inventoryStatus;

    @Column(name = "inventory_owner_id")
    private Integer inventoryOwnerId;

    @Column(name = "inventory_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inventoryTime;

    //医疗器械子分类
    @Column(name = "asset_code68")
    private Integer assetCode68;
    
    @Column(name = "comments")
    @Size(min = 0, max = 256)
    private String comments;//备注

    //设备类型 [1:放射类,2:超声类,3:呼吸麻醉类,4:手术室类,5:供应消杀类,6:输注泵类,7:除颤仪,8:康复类,9:血液透析类,10:实验室类,11:内窥类,12:生命体征支持类,13:其他类]
    @Column(name = "asset_category")
    private Integer assetCategory;

    //用途[1:诊断类,2:治疗类,3:辅助类]
    @Column(name = "asset_usage")
    private Integer assetUsage;

    //资金来源[1:科研,2:教学,3:医疗,4:干保]
    @Column(name = "capital_source")
    private Integer capitalSource;

    //风险等级[1:1类A,2:1类B,3:2类,4:3类]
    @Column(name = "risk_grade")
    private Integer riskGrade;

    //计量[1:A,2:B,3:C,4:D]
    @Column(name = "measure_grade")
    private Integer measureGrade;

    //是否急救设备[1:急救类,2:非急救类]
    @Column(name = "emergency_equipment")
    private Integer emergencyEquipment;

    //是否进口设备[1:进口,2:国产]
    @Column(name = "import_equipment")
    private Integer importEquipment;

    //计量项目类别[1:医用CT,2:普通X光机, ...]
    @Column(name = "measuring_item_category")
    private Integer measuringItemCategory;

    //设备状态[1:在用,2:停用,3:待报废,4:报废]
    @Column(name = "usage_status")
    private Integer usageStatus;

    //计量状态[1:已检,2:待检,3:过期]
    @Column(name = "accounting_category")
    private Integer accountingCategory;
    
    @Column(name = "warranty_year")
    private Double warrantyYear;
    
    @Column(name = "is_generate_pmorder")
    private Boolean isGeneratePmOrder;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArrivalListId() {
        return arrivalListId;
    }

    public void setArrivalListId(String arrivalListId) {
        this.arrivalListId = arrivalListId;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
    }

    public Date getDetectingDate() {
        return detectingDate;
    }

    public void setDetectingDate(Date detectingDate) {
        this.detectingDate = detectingDate;
    }

    public String getAckName() {
        return ackName;
    }

    public void setAckName(String ackName) {
        this.ackName = ackName;
    }

    public ArrivalList getArrivalList() {
        return arrivalList;
    }

    public void setArrivalList(ArrivalList arrivalList) {
        this.arrivalList = arrivalList;
    }

    public String getBusinessAckStatus() {
        return businessAckStatus;
    }

    public void setBusinessAckStatus(String businessAckStatus) {
        this.businessAckStatus = businessAckStatus;
    }

    public String getTechnicalAckStatus() {
        return technicalAckStatus;
    }

    public void setTechnicalAckStatus(String technicalAckStatus) {
        this.technicalAckStatus = technicalAckStatus;
    }

    public String getClinicalAckStatus() {
        return clinicalAckStatus;
    }

    public void setClinicalAckStatus(String clinicalAckStatus) {
        this.clinicalAckStatus = clinicalAckStatus;
    }

    public String getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(String installStatus) {
        this.installStatus = installStatus;
    }

    public String getAssetUID() {
        return assetUID;
    }

    public void setAssetUID(String assetUID) {
        this.assetUID = assetUID;
    }

    public String getCurrentPersonUID() {
        return currentPersonUID;
    }

    public void setCurrentPersonUID(String currentPersonUID) {
        this.currentPersonUID = currentPersonUID;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Integer getFunctionGrade() {
        return functionGrade;
    }

    public void setFunctionGrade(Integer functionGrade) {
        this.functionGrade = functionGrade;
    }

    public String getMaitanance() {
        return maitanance;
    }

    public void setMaitanance(String maitanance) {
        this.maitanance = maitanance;
    }

    public String getMaitananceTel() {
        return maitananceTel;
    }

    public void setMaitananceTel(String maitananceTel) {
        this.maitananceTel = maitananceTel;
    }

    public String getNationalStandardCode() {
        return nationalStandardCode;
    }

    public void setNationalStandardCode(String nationalStandardCode) {
        this.nationalStandardCode = nationalStandardCode;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getModalityId() {
        return modalityId;
    }

    public void setModalityId(String modalityId) {
        this.modalityId = modalityId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Date getManufactDate() {
        return manufactDate;
    }

    public void setManufactDate(Date manufactDate) {
        this.manufactDate = manufactDate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Boolean getIsGeneratePmOrder() {
        return isGeneratePmOrder;
    }

    public void setIsGeneratePmOrder(Boolean isGeneratePmOrder) {
        this.isGeneratePmOrder = isGeneratePmOrder;
    }

    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public Date getWarrantyDate() {
        return warrantyDate;
    }

    public void setWarrantyDate(Date warrantyDate) {
        this.warrantyDate = warrantyDate;
    }

    public Date getTerminateDate() {
        return terminateDate;
    }

    public void setTerminateDate(Date terminateDate) {
        this.terminateDate = terminateDate;
    }

    public Date getLastPmDate() {
        return lastPmDate;
    }

    public void setLastPmDate(Date lastPmDate) {
        this.lastPmDate = lastPmDate;
    }

    public Date getLastMeteringDate() {
        return lastMeteringDate;
    }

    public void setLastMeteringDate(Date lastMeteringDate) {
        this.lastMeteringDate = lastMeteringDate;
    }

    public Date getLastQaDate() {
        return lastQaDate;
    }

    public void setLastQaDate(Date lastQaDate) {
        this.lastQaDate = lastQaDate;
    }

    public Date getLastStockTakeDate() {
        return lastStockTakeDate;
    }

    public void setLastStockTakeDate(Date lastStockTakeDate) {
        this.lastStockTakeDate = lastStockTakeDate;
    }

    public String getClinicalOwnerUid() {
        return clinicalOwnerUid;
    }

    public void setClinicalOwnerUid(String clinicalOwnerUid) {
        this.clinicalOwnerUid = clinicalOwnerUid;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Date getFactoryWarrantyDate() {
        return factoryWarrantyDate;
    }

    public void setFactoryWarrantyDate(Date factoryWarrantyDate) {
        this.factoryWarrantyDate = factoryWarrantyDate;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getAssetOwnerUID() {
        return assetOwnerUID;
    }

    public void setAssetOwnerUID(String assetOwnerUID) {
        this.assetOwnerUID = assetOwnerUID;
    }

    public String getAssetOwnerUID2() {
        return assetOwnerUID2;
    }

    public void setAssetOwnerUID2(String assetOwnerUID2) {
        this.assetOwnerUID2 = assetOwnerUID2;
    }

    public String getAssetOwnerUID3() {
        return assetOwnerUID3;
    }

    public void setAssetOwnerUID3(String assetOwnerUID3) {
        this.assetOwnerUID3 = assetOwnerUID3;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSalvageValue() {
        return salvageValue;
    }

    public void setSalvageValue(Double salvageValue) {
        this.salvageValue = salvageValue;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Integer getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(Integer depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    public String getEamId() {
        return eamId;
    }

    public void setEamId(String eamId) {
        this.eamId = eamId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Integer getParentAssetId() {
        return parentAssetId;
    }

    public void setParentAssetId(Integer parentAssetId) {
        this.parentAssetId = parentAssetId;
    }

    public Integer getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(Integer inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    public Integer getInventoryOwnerId() {
        return inventoryOwnerId;
    }

    public void setInventoryOwnerId(Integer inventoryOwnerId) {
        this.inventoryOwnerId = inventoryOwnerId;
    }

    public Date getInventoryTime() {
        return inventoryTime;
    }

    public void setInventoryTime(Date inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    public Integer getAssetCode68() {
        return assetCode68;
    }

    public void setAssetCode68(Integer assetCode68) {
        this.assetCode68 = assetCode68;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(Integer assetCategory) {
        this.assetCategory = assetCategory;
    }

    public Integer getAssetUsage() {
        return assetUsage;
    }

    public void setAssetUsage(Integer assetUsage) {
        this.assetUsage = assetUsage;
    }

    public Integer getCapitalSource() {
        return capitalSource;
    }

    public void setCapitalSource(Integer capitalSource) {
        this.capitalSource = capitalSource;
    }

    public Integer getRiskGrade() {
        return riskGrade;
    }

    public void setRiskGrade(Integer riskGrade) {
        this.riskGrade = riskGrade;
    }

    public Integer getMeasureGrade() {
        return measureGrade;
    }

    public void setMeasureGrade(Integer measureGrade) {
        this.measureGrade = measureGrade;
    }

    public Integer getEmergencyEquipment() {
        return emergencyEquipment;
    }

    public void setEmergencyEquipment(Integer emergencyEquipment) {
        this.emergencyEquipment = emergencyEquipment;
    }

    public Integer getImportEquipment() {
        return importEquipment;
    }

    public void setImportEquipment(Integer importEquipment) {
        this.importEquipment = importEquipment;
    }

    public Integer getMeasuringItemCategory() {
        return measuringItemCategory;
    }

    public void setMeasuringItemCategory(Integer measuringItemCategory) {
        this.measuringItemCategory = measuringItemCategory;
    }

    public Integer getUsageStatus() {
        return usageStatus;
    }

    public void setUsageStatus(Integer usageStatus) {
        this.usageStatus = usageStatus;
    }

    public Integer getAccountingCategory() {
        return accountingCategory;
    }

    public void setAccountingCategory(Integer accountingCategory) {
        this.accountingCategory = accountingCategory;
    }

    public Integer getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(Integer functionGroup) {
        this.functionGroup = functionGroup;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Double getWorkHours() {
        return workHours;
    }

    public void setWorkHours(Double workHours) {
        this.workHours = workHours;
    }
    
    public Double getLifecycleInYear() {
    	if(lifecycle == null){
    		return null;
    	}
        return new Double(lifecycle)/12.0;
    }

    public void setLifecycleInYear(Double lifecycle) {
    	if(lifecycle == null){
    		return;
    	}
       this.lifecycle = new Double(lifecycle * 12).intValue();
    }

    public Double getWarrantyYear() {
        return warrantyYear;
    }

    public void setWarrantyYear(Double warrantyYear) {
        this.warrantyYear = warrantyYear;
    }

    public Date getBusinessAckDate() {
        return businessAckDate;
    }

    public void setBusinessAckDate(Date businessAckDate) {
        this.businessAckDate = businessAckDate;
    }

    public Date getTechnicalAckDate() {
        return technicalAckDate;
    }

    public void setTechnicalAckDate(Date technicalAckDate) {
        this.technicalAckDate = technicalAckDate;
    }

    public Date getClinicalAckDate() {
        return clinicalAckDate;
    }

    public void setClinicalAckDate(Date clinicalAckDate) {
        this.clinicalAckDate = clinicalAckDate;
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
        if (!(object instanceof ArrivalListItem)) {
            return false;
        }
        ArrivalListItem other = (ArrivalListItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
