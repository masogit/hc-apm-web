/*
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;

/**
 * @author 212547631
 */
@Entity
@Table(name = "asset_info")
public class AssetInfo implements ChineseName,Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private int siteId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    @Column(name = "name_py_code")
    private String namePyCode;

    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;//资产别名

    @Column(name = "function_group")
    private Integer functionGroup;//功能分组   医疗器械分类

    @Column(name = "function_type")
    private String functionType;//设备型号

    @Column(name = "function_grade")
    private Integer functionGrade;//功能等级

    @Size(max = 64)
    @Column(name = "manufacture")
    private String manufacture;//制造商
    
    @Size(max = 64)
    @Column(name = "manufacture_id")
    private String manufactureId;//制造商Id

    @Size(max = 64)
    @Column(name = "vendor")
    private String vendor;//供应商名称 =Supplier.name

    @Column(name = "supplier_id")
    private Integer supplierId; //供应商Id =Supplier.id

    @Transient
    private String vendorTel;//供应商电话

    @Column(name = "maitanance_id")
    private String maitananceId;

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
    @Column(name = "serial_num")
    private String serialNum;//制造商序列号

    @Size(max = 64)
    @Column(name = "depart_num")
    private String departNum;//设备编号

    @Size(max = 64)
    @Column(name = "financing_num")
    private String financingNum;//资产编号

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

    @Column(name = "clinical_dept_id")
    private Integer clinicalDeptId;//使用科室

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备使用科室

    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//使用科室

    @Column(name = "asset_group")
    private Integer assetGroup;//设备分类

    @Column(name = "asset_dept_id")
    private Integer assetDeptId;//所属设备科

    @Column(name = "asset_dept_uid", columnDefinition = "CHAR(32)") // 所属科室UID
    private String assetDeptUID;
    
    @Column(name = "asset_type", columnDefinition = "CHAR(32)") // 设备类型, 支持自定义
    private String assetType;
    
    @Column(name = "asset_CDFA_type") // CDFA设备类型
    private String assetCDFAType;

    @Column(name = "asset_owner_id")
    private Integer assetOwnerId;//维修负责人

    @Size(min = 1, max = 32)
    @Column(name = "asset_owner_name")
    private String assetOwnerName;//维修负责人

    @Size(max = 16)
    @Column(name = "asset_owner_tel")
    private String assetOwnerTel;//维修负责人电话

    @Basic(optional = false)
    @NotNull
    @Column(name = "is_valid")
    private boolean isValid;//在用？

    //1:正常,2:停机,3:限制功能使用,4:待报废,5:停用
    @Basic(optional = false)
    @NotNull
    @Column(name = "status")
    private Integer status;//当前状态

    @Column(name = "manufact_date")
    @Temporal(TemporalType.DATE)
    private Date manufactDate;//出厂日期

    @Column(name = "purchase_date")
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;//采购日期

    @Column(name = "arrive_date")
    @Temporal(TemporalType.DATE)
    private Date arriveDate;//到货日期

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
    private Date lastPmDate;//上次保养日期

    @Column(name = "last_metering_date")
    @Temporal(TemporalType.DATE)
    private Date lastMeteringDate;//上次计量日期

    @Column(name = "last_qa_date")
    @Temporal(TemporalType.DATE)
    private Date lastQaDate;//上次巡检日期

    @Column(name = "last_stocktake_date")
    @Temporal(TemporalType.DATE)
    private Date lastStockTakeDate;//

    @Column(name = "clinical_owner_id")
    private Integer clinicalOwnerId;//使用责任人

    @Size(max = 32)
    @Column(name = "clinical_owner_name")
    private String clinicalOwnerName;//使用责任人

    @Size(max = 16)
    @Column(name = "clinical_owner_tel")
    private String clinicalOwnerTel;//使用责任人电话

    @Size(max = 64)
    @Column(name = "registration_no")
    private String registrationNo;//注册证号

    @Column(name = "factory_warranty_date")
    @Temporal(TemporalType.DATE)
    private Date factoryWarrantyDate;//质保日期

    @Size(max = 256)
    @Column(name = "qr_code")
    private String qrCode;//二维码

    @Column(name = "asset_owner_id2")
    private Integer assetOwnerId2;//维修负责人2

    @Column(name = "asset_owner_name2")
    private String assetOwnerName2;//维修负责人2

    @Column(name = "asset_owner_tel2")
    private String assetOwnerTel2;//维修负责人2电话

    @Column(name = "asset_owner_id3")
    private Integer assetOwnerId3;//维修负责人3

    @Column(name = "asset_owner_name3")
    private String assetOwnerName3;//维修负责人3

    @Column(name = "asset_owner_tel3")
    private String assetOwnerTel3;//维修负责人3电话

    @Column(name = "fe_user_id")
    private Integer feUserId;

    @Column(name = "dispatch_mode")
    private Integer dispatchMode;//派工模式

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
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

    @Column(name = "system_num1")
    private String systemNum1;

    @Column(name = "system_num2")
    private String systemNum2;

    @Column(name = "system_num3")
    private String systemNum3;

    @Column(name = "system_num4")
    private String systemNum4;

    @Column(name = "system_num5")
    private String systemNum5;

    @Column(name = "is_deleted")
    private Boolean isDeleted;//是否删除

    // 0-normal 1-standby
    @Column(name = "device_status")
    private Integer deviceStatus;//资产状态

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;//品牌

    @Column(name = "hospital_id")
    @Basic(optional = false)
    @NotNull
    private Integer hospitalId;

    @JoinColumn(insertable = false, updatable = false, name = "hospital_id", referencedColumnName = "id")
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    private OrgInfo hospital;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    //设备类型 [1:放射类,2:超声类,3:呼吸麻醉类,4:手术室类,5:供应消杀类,6:输注泵类,7:除颤仪,8:康复类,9:血液透析类,10:实验室类,11:内窥类,12:生命体征支持类,13:其他类]
    @Column(name = "asset_category")
    private Integer assetCategory;

    //用途[1:诊断类,2:治疗类,3:辅助类]
    @Column(name = "asset_usage")
    private Integer assetUsage;

    //资金来源[1:科研,2:教学,3:医疗,4:干保]
    @Column(name = "capital_source")
    private Integer capitalSource;
    
  //资金来源[1:自有资金,2:合资公司资金]
    @Column(name = "capital_source2")
    private Integer capitalSource2;

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

    //计量周期(月)
    @Column(name = "metering_cycle")
    private Integer meteringCycle;

    //设备状态[1:在用,2:停用,3:待报废,4:报废]
    //1:待完善,2:待审核,3:已经建档
    @Column(name = "usage_status")
    private Integer usageStatus;

    //核算类别[1:医用电子仪器,2:光学仪器及内窥镜,3:医用机械设备,4:电子产品及通讯设备,5:仪器仪表计量标准器具,6:其他设备]
    @Column(name = "accounting_category")
    private Integer accountingCategory;

    //设备标准名称
    @Size(max = 64)
    @Column(name = "device_name")
    private String deviceName;

    //品牌/制造商简称
    @Size(max = 64)
    @Column(name = "make_short_name")
    private String makerShortName;

    @Column(name = "next_metering_date")
    @Temporal(TemporalType.DATE)
    private Date nextMeteringDate;//下次计量日期

    @Column(name = "is_warranty")
    private Boolean isWarranty = Boolean.TRUE;//是否对质保到期进行提醒(默认提醒)

    @Column(name = "metering_no")
    private String meteringNO;//计量编号

    @Column(name = "repair_form")
    private Integer repairForm; //1和null是纯内修。 2，纯外修。3，先内修再外修

    @Column(name = "is_pm")
    private Boolean isPm = Boolean.TRUE;//是否保养

    @Column(name = "is_checked")
    private Boolean isChecked = Boolean.TRUE;//是否盘点

//    public Integer getAssetCategory() {
//        return assetCategory;
//    }
//
//    public void setAssetCategory(Integer assetCategory) {
//        this.assetCategory = assetCategory;
//    }

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Column(name = "ib_status")
    private Integer ibStatus;

    @Column(name = "ge_device_type")
    private Integer geDeviceType; // 0-其他，1-MR, 2-CT, 3-US

    @Column(name = "shared_asset_type")//调剂设备uid
    private String sharedAssetTypeUid;
    @Column(name = "swap_type")//调剂设备uid
    private String swapTypeUid;
    @Column(name = "swap_type_state")//调剂设备状态（"0"：正常，"1"：借用中）
    private String swapTypeState;

    @Column(name = "manufacture_py_code")
    private String manufacturePyCode;

    @Column(name = "clinical_dept_name_py_code")
    private String clinicalDeptNamePyCode;

    @Transient
    private int lineNum;

    @Transient
    private Set<SharedAssetReqDetail> sharedAssetReqDetailList;
    
    @Transient
    private SharedAssetReqDetail sharedAssetReqDetail;

    @Transient
    private SharedAssetType sharedAssetType;
    
    public Set<SharedAssetReqDetail> getSharedAssetReqDetailList() {
		return sharedAssetReqDetailList;
	}

	public void setSharedAssetReqDetailList(Set<SharedAssetReqDetail> sharedAssetReqDetailList) {
		this.sharedAssetReqDetailList = sharedAssetReqDetailList;
	}

	public SharedAssetReqDetail getSharedAssetReqDetail() {
		return sharedAssetReqDetail;
	}

	public void setSharedAssetReqDetail(SharedAssetReqDetail sharedAssetReqDetail) {
		this.sharedAssetReqDetail = sharedAssetReqDetail;
	}
	
	public SharedAssetType getSharedAssetType() {
		return sharedAssetType;
	}

	public void setSharedAssetType(SharedAssetType sharedAssetType) {
		this.sharedAssetType = sharedAssetType;
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

    public Integer getCapitalSource2() {
		return capitalSource2;
	}

	public void setCapitalSource2(Integer capitalSource2) {
		this.capitalSource2 = capitalSource2;
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

    public Integer getAssetOwnerId3() {
        return assetOwnerId3;
    }

    public void setAssetOwnerId3(Integer assetOwnerId3) {
        this.assetOwnerId3 = assetOwnerId3;
    }

    public String getAssetOwnerName3() {
        return assetOwnerName3;
    }

    public void setAssetOwnerName3(String assetOwnerName3) {
        this.assetOwnerName3 = assetOwnerName3;
    }

    public String getAssetOwnerTel3() {
        return assetOwnerTel3;
    }

    public void setAssetOwnerTel3(String assetOwnerTel3) {
        this.assetOwnerTel3 = assetOwnerTel3;
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

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public String getSharedAssetTypeUid() {
        return sharedAssetTypeUid;
    }

    public void setSharedAssetTypeUid(String sharedAssetTypeUid) {
        this.sharedAssetTypeUid = sharedAssetTypeUid;
    }

    public String getSwapTypeUid() {
        return swapTypeUid;
    }

    public void setSwapTypeUid(String swapTypeUid) {
        this.swapTypeUid = swapTypeUid;
    }

    public String getSwapTypeState() {
        return swapTypeState;
    }

    public void setSwapTypeState(String swapTypeState) {
        this.swapTypeState = swapTypeState;
    }

    public AssetInfo() {
    }

    public AssetInfo(Integer id) {
        this.id = id;
    }

    public AssetInfo(Integer id, int siteId, String name, Integer assetDeptId, int assetOwnerId, String assetOwnerName, boolean isValid, int status) {
        this.id = id;
        this.siteId = siteId;
        this.name = name;
        this.assetDeptId = assetDeptId;
        this.assetOwnerId = assetOwnerId;
        this.assetOwnerName = assetOwnerName;
        this.isValid = isValid;
        this.status = status;
    }

    @PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
//        if (supplierId == null) {
//            supplierId = -1;
//        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (!StringUtils.isEmpty(name)) {
            this.namePyCode = PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        }

    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public OrgInfo getHospital() {
        return hospital;
    }

    public void setHospital(OrgInfo hospital) {
        this.hospital = hospital;
    }

    public Integer getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(Integer functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public Integer getFunctionGrade() {
        return functionGrade;
    }

    public void setFunctionGrade(Integer functionGrade) {
        this.functionGrade = functionGrade;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
        if (!StringUtils.isEmpty(manufacture)) {
            this.manufacturePyCode = PinyinUtil.getShortPinyin(manufacture).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        }

    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendorTel() {
        return vendorTel;
    }

    public void setVendorTel(String vendorTel) {
        this.vendorTel = vendorTel;
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

    public String getFinancingNum() {
        return financingNum;
    }

    public void setFinancingNum(String financingNum) {
        this.financingNum = financingNum;
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

    public Integer getClinicalDeptId() {
        return clinicalDeptId;
    }

    public void setClinicalDeptId(Integer clinicalDeptId) {
        this.clinicalDeptId = clinicalDeptId;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
        if (!StringUtils.isEmpty(clinicalDeptName)) {
            this.clinicalDeptNamePyCode = PinyinUtil.getShortPinyin(clinicalDeptName).toLowerCase();
        }
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetDeptId() {
        return assetDeptId;
    }

    public void setAssetDeptId(Integer assetDeptId) {
        this.assetDeptId = Optional.ofNullable(assetDeptId).orElse(0);
    }

    public Integer getAssetOwnerId() {
        return assetOwnerId;
    }

    public void setAssetOwnerId(Integer assetOwnerId) {
        this.assetOwnerId = assetOwnerId;
    }

    public String getAssetOwnerName() {
        return assetOwnerName;
    }

    public void setAssetOwnerName(String assetOwnerName) {
        this.assetOwnerName = assetOwnerName;
    }

    public String getAssetOwnerTel() {
        return assetOwnerTel;
    }

    public void setAssetOwnerTel(String assetOwnerTel) {
        this.assetOwnerTel = assetOwnerTel;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
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

    public Double getLifecycleInYear() {
        if (lifecycle == null) {
            return null;
        }
        return new Double(lifecycle) / 12.0;
    }

    public void setLifecycleInYear(Double lifecycle) {
        if (lifecycle == null) {
            this.lifecycle = null;
        } else {
            this.lifecycle = new Double(lifecycle * 12).intValue();
        }

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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getAssetOwnerId2() {
        return assetOwnerId2;
    }

    public void setAssetOwnerId2(Integer assetOwnerId2) {
        this.assetOwnerId2 = assetOwnerId2;
    }

    public String getAssetOwnerName2() {
        return assetOwnerName2;
    }

    public void setAssetOwnerName2(String assetOwnerName2) {
        this.assetOwnerName2 = assetOwnerName2;
    }

    public String getAssetOwnerTel2() {
        return assetOwnerTel2;
    }

    public void setAssetOwnerTel2(String assetOwnerTel2) {
        this.assetOwnerTel2 = assetOwnerTel2;
    }

    public Integer getFeUserId() {
        return feUserId;
    }

    public void setFeUserId(Integer feUserId) {
        this.feUserId = feUserId;
    }

    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public Integer getClinicalOwnerId() {
        return clinicalOwnerId;
    }

    public void setClinicalOwnerId(Integer clinicalOwnerId) {
        this.clinicalOwnerId = clinicalOwnerId;
    }

    public String getClinicalOwnerName() {
        return clinicalOwnerName;
    }

    public void setClinicalOwnerName(String clinicalOwnerName) {
        this.clinicalOwnerName = clinicalOwnerName;
    }

    public String getClinicalOwnerTel() {
        return clinicalOwnerTel;
    }

    public void setClinicalOwnerTel(String clinicalOwnerTel) {
        this.clinicalOwnerTel = clinicalOwnerTel;
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

    public String getSystemNum1() {
        return systemNum1;
    }

    public void setSystemNum1(String systemNum1) {
        this.systemNum1 = systemNum1;
    }

    public String getSystemNum2() {
        return systemNum2;
    }

    public void setSystemNum2(String systemNum2) {
        this.systemNum2 = systemNum2;
    }

    public String getSystemNum3() {
        return systemNum3;
    }

    public void setSystemNum3(String systemNum3) {
        this.systemNum3 = systemNum3;
    }

    public String getSystemNum4() {
        return systemNum4;
    }

    public void setSystemNum4(String systemNum4) {
        this.systemNum4 = systemNum4;
    }

    public String getSystemNum5() {
        return systemNum5;
    }

    public void setSystemNum5(String systemNum5) {
        this.systemNum5 = systemNum5;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMakerShortName() {
        return makerShortName;
    }

    public void setMakerShortName(String makerShortName) {
        this.makerShortName = makerShortName;
    }

    public Integer getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(Integer assetCategory) {
        this.assetCategory = assetCategory;
    }

    public Integer getMeteringCycle() {
        return meteringCycle;
    }

    public void setMeteringCycle(Integer meteringCycle) {
        this.meteringCycle = meteringCycle;
    }

    public Date getNextMeteringDate() {
        return nextMeteringDate;
    }

    public void setNextMeteringDate(Date nextMeteringDate) {
        this.nextMeteringDate = nextMeteringDate;
    }

    public Boolean getWarranty() {
        return isWarranty;
    }

    public void setWarranty(Boolean warranty) {
        isWarranty = warranty;
    }

    public String getMeteringNO() {
        return meteringNO;
    }

    public void setMeteringNO(String meteringNO) {
        this.meteringNO = meteringNO;
    }

    public Integer getRepairForm() {
        return repairForm;
    }

    public void setRepairForm(Integer repairForm) {
        this.repairForm = repairForm;
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

    public Integer getIbStatus() {
        return ibStatus;
    }

    public void setIbStatus(Integer ibStatus) {
        this.ibStatus = ibStatus;
    }

    public Integer getGeDeviceType() {
        return geDeviceType;
    }

    public void setGeDeviceType(Integer geDeviceType) {
        this.geDeviceType = geDeviceType;
    }

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
    }

    public String getManufacturePyCode() {
        return manufacturePyCode;
    }

    public void setManufacturePyCode(String manufacturePyCode) {
        this.manufacturePyCode = manufacturePyCode;
    }

    public String getClinicalDeptNamePyCode() {
        return clinicalDeptNamePyCode;
    }

    public void setClinicalDeptNamePyCode(String clinicalDeptNamePyCode) {
        this.clinicalDeptNamePyCode = clinicalDeptNamePyCode;
    }

    public String getAssetDeptUID() {
        return assetDeptUID;
    }

    public void setAssetDeptUID(String assetDeptUID) {
        this.assetDeptUID = assetDeptUID;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getMaitananceId() {
        return maitananceId;
    }

    public void setMaitananceId(String maitananceId) {
        this.maitananceId = maitananceId;
    }

   public Boolean getIsPm() {
        return isPm;
    }

    public void setIsPm(Boolean isPm) {
        this.isPm = isPm;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(String manufactureId) {
        this.manufactureId = manufactureId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetCDFAType() {
        return assetCDFAType;
    }

    public void setAssetCDFAType(String assetCDFAType) {
        this.assetCDFAType = assetCDFAType;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetInfo)) {
            return false;
        }
        AssetInfo other = (AssetInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public String getPinYin() {
		return getNamePyCode();
	}

}
