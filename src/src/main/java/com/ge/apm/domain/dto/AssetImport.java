package com.ge.apm.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.asset.AssetImportBooleanConvert;
import com.ge.apm.service.asset.AssetImportIntegerConverter;
import com.ge.apm.service.utils.AssetImportDateConvert;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

public class AssetImport implements Serializable {

    private static final long serialVersionUID = 1L;

    private AssetFileImportBaseService.ImportStatus importStatus;
    private List<String> errMsg = new ArrayList<>();
    private List<String> deletedField = new ArrayList<>();
    private Integer id;

    private Integer rowNum;

    @ExcelProperty("UUID")
    private String uid;

    private Integer siteId;

    @ExcelProperty("EquipmentName")
    private String name;

    private String namePyCode;

    @ExcelProperty("EquipmentAlias")
    private String aliasName;//资产别名

    private Integer functionGroup;//功能分组   医疗器械分类(旧)
    @ExcelProperty(value = "medicalInstrumentClassification")
    private String functionGroupStr;//功能分组   医疗器械分类(旧)Str

    @ExcelProperty("医疗器械分类编码")
    private String medicalApparatusClassification; // 医疗器械分类

    @ExcelProperty("functionType")
    private String functionType;//设备型号

    @ExcelProperty(value = "functionGrade", converter = AssetImportIntegerConverter.class)
    private Integer functionGrade;//功能等级

    @ExcelProperty("manufacture")
    private String manufacture;//制造商

    private String manufactureId;//制造商Id

    private String manufactureTel;//制造商电话
    @ExcelProperty("Vendor")

    private String vendor;//供应商名称 =Supplier.name

    private Integer supplierId; //供应商Id =Supplier.id

    private String vendorTel;//供应商电话

    @ExcelProperty("Maintainer")
    private String maitananceId;

    private String maitanance;//维修商

    @ExcelProperty("MaintainerTelephone")
    private String maitananceTel;//维修电话

    @ExcelProperty("nationalStandardCode")
    private String nationalStandardCode;//2010国家编码

    @ExcelProperty("serialNum")
    private String serialNum;//制造商序列号

    @ExcelProperty("EquipmentID")
    private String departNum;//设备编号
    @ExcelProperty("AssetID")

    private String financingNum;//资产编号

    @ExcelProperty("BarCode")
    private String barcode;//条码

    @ExcelProperty("ITSystemID")
    private String modalityId;//院内IT系统编号

    @ExcelProperty("InstallationLocationCode")
    private String locationCode;//安装位置编码

    @ExcelProperty("InstallationLocation")
    private String locationName;//安装位置

    @ExcelProperty("InstallationLocationNotes")
    private String locationNotes;//安装位置备注

    private String nurseLocation;//护士检查设备位置

    private Integer clinicalDeptId;//所属科室

    private String clinicalDeptUID;//设备所属科室

    @ExcelProperty("clinicalDeptId")
    private String clinicalDeptName;//所属科室

    private String accountingDeptUID;//账号科室
    private String modalityType;
    @ExcelProperty("assetDeptName")
    private String assetDeptName;//使用科室

    private String deptName;//使用科室

    private Integer assetGroup;//设备分类


    @ExcelProperty(value = "assetGroup")
    private String assetGroupStr;//设备分类Str

    private Integer assetClass;//资产分类

    @ExcelProperty(value = "assetClass")
    private String assetClassStr;//资产分类Str

    private Integer assetGroupSecondary;//
    @ExcelProperty(value = "assetGroupSecondary")
    private String assetGroupSecondaryStr;//设备分类2级

    private Integer assetDeptId;//使用设备科

    private String assetDeptUID;

    @ExcelProperty("assetTypeGroupEncoded")
    private String assetType;

    private String assetCDFAType;

    private Integer assetOwnerId;//维修负责人
    @ExcelProperty("RepairOwner")

    private String assetOwnerName;//维修负责人

    private String assetOwnerTel;//维修负责人电话

    private Boolean isValid;//在用？

    //1:正常,2:停机,3:限制功能使用,4:待报废,5:停用
    private Integer status;//当前状态
    @ExcelProperty(value = "CurrentStatus")

    private String statusStr;

    @ExcelProperty(value = "manufactDate", converter = AssetImportDateConvert.class)
    private Date manufactDate;//出厂日期
    private Date lastMeasuringDate;
    @ExcelProperty(value = "purchaseDate", converter = AssetImportDateConvert.class)
    private Date purchaseDate;//采购日期

    @ExcelProperty(value = "arriveDate", converter = AssetImportDateConvert.class)
    private Date arriveDate;//到货日期

    @ExcelProperty(value = "StartUsingDate", converter = AssetImportDateConvert.class)
    private Date installDate;//启用日期

    @ExcelProperty(value = "warrantyDate", converter = AssetImportDateConvert.class)
    private Date warrantyDate;//维保截止日期

    @ExcelProperty(value = "terminateDate", converter = AssetImportDateConvert.class)
    private Date terminateDate;//报废日期

    @ExcelProperty(value = "lastPmDate", converter = AssetImportDateConvert.class)
    private Date lastPmDate;//3级保养 上次保养日期


    @ExcelProperty(value = "lastMeteringDate", converter = AssetImportDateConvert.class)
    private Date lastMeteringDate;//上次计量日期

    private Date lastInventoryDate;//上次盘点日期

    @ExcelProperty(value = "LastInspectionDate", converter = AssetImportDateConvert.class)
    private Date lastQaDate;//2级保养 上次保养日期

    private Date lastPmDateOne;//1级保养 上次保养日期
    private Date lastPmDateFour;//4级保养 上次保养日期

    private Date lastStockTakeDate;//

    private Integer clinicalOwnerId;//使用责任人

    @ExcelProperty("UseOwner")
    private String clinicalOwnerName;//使用责任人

    private String clinicalOwnerTel;//使用责任人电话

    @ExcelProperty("RegistrationNumber")
    private String registrationNo;//注册证号
    @ExcelProperty("factoryWarrantyDate")
    private Date factoryWarrantyDate;//质保截止日期

    @ExcelProperty("acceptDate")
    private Date acceptDate;//验收日期

    @ExcelProperty(value = "WarrantyPeriodMonths", converter = AssetImportIntegerConverter.class)
    private Integer warrantyMonth;//质保时间（月）

    @ExcelProperty("QRCode")
    private String qrCode;//二维码

    private String oldQrCode;

    private Integer assetOwnerId2;//维修负责人2
    @ExcelProperty("RepairOwner2")

    private String assetOwnerName2;//维修负责人2

    private String assetOwnerTel2;//维修负责人2电话

    private Integer assetOwnerId3;//维修负责人3

    @ExcelProperty("RepairOwner3")
    private String assetOwnerName3;//维修负责人3

    private String assetOwnerTel3;//维修负责人3电话

    private Integer feUserId;

    private Integer dispatchMode;//派工模式

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @ExcelProperty("PurchasePrice")
    private Double purchasePrice;//采购价格(元)

    @ExcelProperty("salvageValue")
    private Double salvageValue;//最终残值(元)

    private Integer lifecycle;//使用年限

    @ExcelProperty(value = "LifeCycle")
    private Double lifecycleInYear;//使用年限(年)

    private Integer depreciationMethod;//折旧算法

    @ExcelProperty(value = "DepreciationMethod")
    private String depreciationMethodStr;

    private String eamId;//EAM系统编号

    @ExcelProperty("systemId")
    private String systemId;//SystemID/厂商报修ID

    @ExcelProperty("systemNum1")
    private String systemNum1;

    @ExcelProperty("systemNum2")
    private String systemNum2;
    @ExcelProperty("systemNum3")
    private String systemNum3;
    @ExcelProperty("systemNum4")
    private String systemNum4;
    @ExcelProperty("systemNum5")
    private String systemNum5;

    private Boolean isDeleted;//是否删除

    // 0-normal 1-standby
    private Integer deviceStatus;//资产状态
    @ExcelProperty(value = "deviceStatus")

    private String deviceStatusStr;

    private Integer networkingStatus;  //联网状态 0 未联网  1 内网  3 外网
    @ExcelProperty(value = "networkingStatus")

    private String networkingStatusStr;  //联网状态 0 未联网  1 内网  3 外网

    //null   1 empowerment  //这个字段不需要编辑和展示，所以只存null 和1
    private Integer empowermentStatus;

    private Integer parentAssetId;
    private String parentAssetUid;

    private Integer inventoryStatus;

    private Integer inventoryOwnerId;

    private Date inventoryTime;

    //医疗器械子分类
    private Integer assetCode68;
    @ExcelProperty(value = "assetCode68")
    private String assetCode68Str;

    @ExcelProperty("Remark")
    private String comments;//备注

    private Brand brand;//品牌
    private String remark;
   /* @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(insertable = false, nullable = false,updatable = false,name = "uid",referencedColumnName = "id")
    private MvsAssetInfo mvsAssetInfo;*/

    private Integer hospitalId;

    private OrgInfo hospital;

    private String tenantUID;

    private String institutionUID;

    @ExcelProperty("Institution")
    private String institutionName;
    @ExcelProperty("Hospital")
    private String hospitalName;
    @ExcelProperty("hospitalId")
    private String siteName;

    private String hospitalUID;

    private String siteUID;

    //资产价值分类
    @ExcelProperty("assetValueCategory")
    private String assetValueCategory;

    //设备类型 [1:放射类,2:超声类,3:呼吸麻醉类,4:手术室类,5:供应消杀类,6:输注泵类,7:除颤仪,8:康复类,9:血液透析类,10:实验室类,11:内窥类,12:生命体征支持类,13:其他类]
    private Integer assetCategory;

    //用途[1:诊断类,2:治疗类,3:辅助类]
    private Integer assetUsage;
    @ExcelProperty(value = "UsageClassification")
    private String assetUsageStr;

    //资金来源[1:科研,2:教学,3:医疗,4:干保]
    private Integer capitalSource;
    @ExcelProperty(value = "CapitalSource")
    private String capitalSourceStr;

    //资金来源[1:自有资金,2:合资公司资金]
    private Integer capitalSource2;
    @ExcelProperty(value = "capitalSource2")

    private String capitalSource2Str;

    //风险等级[1:1类A,2:1类B,3:2类,4:3类]
    private Integer riskGrade;
    @ExcelProperty(value = "riskGrade")
    private String riskGradeStr;
    //计量[1:A,2:B,3:C,4:D]
    private Integer measureGrade;
    @ExcelProperty(value = "measureGrade")
    private String measureGradeStr;

    //是否急救设备[1:急救类,2:非急救类]
    private Integer emergencyEquipment;
    @ExcelProperty(value = "emergencyEquipment")
    private String emergencyEquipmentStr;

    //是否进口设备[1:进口,2:国产]
    private Integer importEquipment;
    @ExcelProperty(value = "IsImportEquipment")

    private String importEquipmentStr;

    //计量项目类别[1:医用CT,2:普通X光机, ...]
    private Integer measuringItemCategory;
    @ExcelProperty(value = "measuringItemCategory")
    private String measuringItemCategoryStr;

    //计量周期(月)
    private Integer meteringCycle;

    //设备状态[1:在用,2:停用,3:待报废,4:报废]
    //1:待完善,2:待审核,3:已经建档  4 审核不通过
    private Integer usageStatus;

    //核算类别[1:医用电子仪器,2:光学仪器及内窥镜,3:医用机械设备,4:电子产品及通讯设备,5:仪器仪表计量标准器具,6:其他设备]
    private Integer accountingCategory;
    @ExcelProperty(value = "AccountingCategory")
    private String accountingCategoryStr;

    //设备标准名称
    @ExcelProperty("EquipmentStandardName")
    private String deviceName;

    //品牌/制造商简称
    @Size(max = 64)
    @ExcelProperty("BrandManufacturerShortName")
    private String makerShortName;

    private Date nextMeteringDate;//下次计量日期
    @ExcelProperty(value = "isWarrantyNotice", converter = AssetImportBooleanConvert.class)
    private Boolean isWarranty;//是否对质保到期进行提醒(默认提醒)

    private String meteringNO;//计量编号

    private Integer repairForm; //1和null是纯内修。 2，纯外修。3，先内修再外修
    @ExcelProperty(value = "repairForm")

    private String repairFormStr; //1和null是纯内修。 2，纯外修。3，先内修再外修

    @ExcelProperty(value = "IsPM", converter = AssetImportBooleanConvert.class)
    private Boolean isPm;//是否保养

    @ExcelProperty("isUseRegistration")
    private Boolean isUseRegistration;

    @ExcelProperty(value = "isFixed", converter = AssetImportBooleanConvert.class)
    private Boolean isFixed;//是否固定资产

    @ExcelProperty(value = "IsChecked", converter = AssetImportBooleanConvert.class)
    private Boolean isChecked;//是否盘点

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    private Integer ibStatus;

    //专有分类 YHAAO-3890 一般设备、仪器仪表及量具、专用设备 3类
    @ExcelProperty("ProprietaryClass")
    private String proprietaryClass;

    @ExcelProperty(value = "geDeviceType", converter = AssetImportIntegerConverter.class)
    private Integer geDeviceType; // 0-其他，1-MR, 2-CT, 3-US

    private String sharedAssetTypeUid;
    private String swapTypeUid;
    private String swapTypeState;

    private String manufacturePyCode;

    private String clinicalDeptNamePyCode;

    private Integer pmOwnerId;//保养负责人

    private String patrolInspectionOwnerUID;//保养巡检负责人
    @ExcelProperty("patrolInspectionOwnerName")

    private String patrolInspectionOwnerName;//保养巡检负责人名称

    private String patrolInspectionOwnerTel;//保养巡检负责人电话

    private Integer measuringOwnerId;//计量负责人

    @ExcelProperty("PMOwner")
    private String pmOwnerName;

    @ExcelProperty("measuringOwner")
    private String measuringOwnerName;

    private String pmOwnerTel;

    private String measuringOwnerTel;

    private Integer mvsEquip;

    // 所属课题项目
    @ExcelProperty("subjectProject")
    private String subjectProject;

    private Boolean hasOpenAlert;

    private String terminationCause;

    private String measuringType;

    @ExcelProperty("UCMID")
    private String UCMID;


    @ExcelProperty("rfId")
    //射频码
    private String rfId;
    @ExcelProperty("rfGroup")
    //射频码分组
    private String rfGroup;
    @ExcelProperty("powerOnThreshold")
    //开机电流阈值
    private Double powerOnThreshold;
    //电源ID
    @ExcelProperty("currentId")
    private String currentId;

    private Integer assetDisposalResults;

//    @Size(max = 256)
//    @Column(name = "one_qr_code")
//    private String oneQrCode;
//
// 
//    
//    private String oneQrCodeStatus;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "one_qr_code", referencedColumnName = "qr_code", unique = true, insertable = false, updatable = false)
//    private QrCodeLib qrCodeLib;

    /**
     * 主目录
     */
    @ExcelProperty("mainDirectory")
    private String mainDirectory;

    /**
     * 子目录
     */
    @ExcelProperty("secondDirectory")
    private String secondDirectory;

    //联通状态，默认 1未联通，2 联通, 3 N/A
    private Integer connectToInsite;

    //默认1：未见异常、 2：报警、 3：维修中、 4: 无联通无报警、 5： 无显示
    private Integer ibDeviceStatus;


    //全量IB客户可见性标记
    private Integer visibleStatus;

    @ExcelProperty("monitorRate")
    private Double monitorRate;

    @ExcelProperty("otherPower")
    private Double otherPower;

    private Double unitPriceElectricity;

    // 年保养次数
    private Integer annualMaintenanceTimes;

    // 设备性质
    private Integer assetQuality;
    @ExcelProperty(value = "assetQuality")

    private String assetQualityStr;

    //无人值守设备状态  0:关机  1:待机 2:在用
    private Integer runningStatus;

    //1- 正常可借用（无人值守可以调剂）， 2-无人值守不可以调剂
    private Integer sharedStatus;

    //县域ib合同类别
    private String contractType;

    //临床功能得分
    private Integer clinicalFunctionScore;

    //有形风险得分
    private Integer tangibleRiskScore;

    //问题避免率得分
    private Integer problemAvoidanceRateScore;

    //事故历史得分
    private Integer accidentHistoryScore;

    //制造商/管理部门特殊要求得分
    private Integer specialRequirementScore;
    //是否风险评估
    private Boolean isRiskAssessment;
    @Getter
    @Setter
    private String checkCycle; //检查周期
    @Getter
    @Setter
    private String detectionCycle; //检测周期
    @Getter
    @Setter
    private String maintenancePeriod; //保养周期
    @Getter
    @Setter
    private String sumRiskScore; //风险总分
    @Getter
    @Setter
    private Date assessmentDate; //评估日期
    @Getter
    @Setter
    private String assessmentBy; //评估人员
    @Getter
    @Setter
    private String assetAchievements; //设备绩效

    @Getter
    @Setter
    @ExcelProperty(value = "assetQuantity", converter = AssetImportIntegerConverter.class)
    private Integer assetQuantity; //设备数量 YHAAO-16852

    @Getter
    @Setter
    @ExcelProperty("productFamily")
    private String productFamily; //系列
    @Getter
    @Setter
    private Integer productSegment;//高端/中端/低端
    @ExcelProperty(value = "productSegment")

    private String productSegmentStr;//高端/中端/低端
    @Getter
    @Setter
    @ExcelProperty("MVSContractStartDate")
    private Date mvsContractStartDate;//续签合同start date
    @Getter
    @Setter
    @ExcelProperty("MVSContractEndDate")
    private Date mvsContractEndDate;//续签合同end date


    private int lineNum;


    private Set<SharedAssetReqDetail> sharedAssetReqDetailList;


    private SharedAssetReqDetail sharedAssetReqDetail;


    private SharedAssetType sharedAssetTypeEntity;


    private OrgInfo siteOrg;


    private OrgInfo assetDeptOrg;


    private Integer verificationConclusion;


    private OrgInfo clinicalDeptOrg;

    private Supplier supplier;
    private String warrantyValue;
    @Getter
    @Setter
    private String errorMessage;
    @Getter
    @Setter
    private String branchIds;

    private String aet;

    @Getter
    @Setter
    private Integer rank;

    private Date studydate;


    private Date startTime;


    private Date endTime;


    private Double studyexposuretime;


    private String usageRate;


    private Double energyConsumption;


    private String dashboardPage;


    private String assetGroupName;//设备分类名称


    private List<String> tags = new ArrayList<>();


    private String sharedAssetSwapIndex;


    private Date checkDate;


    private String boUuid;


    @Setter
    private String assetUsedLifecycle;


    @Setter
    private String assetLifecycleDeadline;

    @Getter
    @Setter
    private Date offlineUpdateDate;


    private String sharedAssetSwapIndexId;


    //待报废操作人


    private String terminateOperation;


    private Date terminateOperationDate;


    private String supplierPyCode;

    private Boolean isCleaned = true;

    private String barCode;

    // 入库单号
    private String receiptNumber;

    // 产地
    private String productPlace;


    // 保修
    private String guarantee;


    private Date startUsingDate;
    private String address;

    private String authGroupUid;


    private Boolean isMvsTant;
    //    @Column(name = "swap_type")
//    private String swapType;
    private Boolean sharable;
    private Integer isOpenTube;//0：不开启 1：开启

    //是否开启设备失联监控
    private Boolean enableLossOfCommunicationMonitoring;

    //设备状态失联时间（小时）
    private Double locDurationDeviceStatus;

    //环境监控失联时间（小时）
    private Double locDurationEnvMonitoring;
    private Integer connectivity;

    // 所属科室代码
    private String ownerDeptCode;

    // 使用科室代码
    private String useDeptCode;

    // 是否是计量设备
    private Integer measureDevice;

    // 是否有注册证
    private Integer hasRegistrationNo;


    /**
     * 异常反馈
     */
    private String abnormalFeedback;

    /**
     * 异常原因
     */
    private String abnormalReason;
    private Integer installStatus;
    /**
     * 智慧球服务
     * 0:关闭 1：开启
     */
    private String tubeWatchStatus;


    /**
     * 当前位置
     */
    private String assetLocation;


    @Getter
    @Setter
    @ExcelProperty("spare01")
    private String spare01;
    @Getter
    @Setter
    @ExcelProperty("spare02")
    private String spare02;
    @Getter
    @Setter
    @ExcelProperty("spare03")
    private String spare03;
    @Getter
    @Setter
    @ExcelProperty("spare04")
    private String spare04;
    @Getter
    @Setter
    @ExcelProperty("spare05")
    private String spare05;
    @Getter
    @Setter
    @ExcelProperty("spare06")
    private String spare06;
    @Getter
    @Setter
    @ExcelProperty("spare07")
    private String spare07;
    @Getter
    @Setter
    @ExcelProperty("spare08")
    private String spare08;
    @Getter
    @Setter
    @ExcelProperty("spare09")
    private String spare09;
    @Getter
    @Setter
    @ExcelProperty("spare10")
    private String spare10;
    @Getter
    @Setter
    private Long billingTime; // 累计借用计费时长分钟

    @Getter
    @Setter
    private Double billingCost;//累计费用
    @Getter
    @Setter
    private Integer borrowedStatus;  // 借用状态 -1不可借出，0 可借，1占用
    @Getter
    @Setter
    private String borrowConfigId;


    /**
     * 新供应商uid 关联 tenant_info
     */
    private String supplierNewUID;

    private Set<AssetTags> assetTags;
    @ExcelProperty("assetTags")

    private String tagStr;

    private String lastCheckCompany;

    @Getter @Setter
    @ExcelProperty("usageCount")
    private Integer usageCount;
    @Getter @Setter
    @ExcelProperty("ratingUsageCount")
    private Integer ratingUsageCount;

    public AssetImport() {
    }

    public AssetImport(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }


    /*public MvsAssetInfo getMvsAssetInfo() {
        return mvsAssetInfo;
    }

    public void setMvsAssetInfo(MvsAssetInfo mvsAssetInfo) {
        this.mvsAssetInfo = mvsAssetInfo;
    }*/


    private String warrantyAlertStatus = null;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePyCode() {
        return namePyCode;
    }

    public void setNamePyCode(String namePyCode) {
        this.namePyCode = namePyCode;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Integer getFunctionGroup() {
        return functionGroup;
    }

    public void setFunctionGroup(Integer functionGroup) {
        this.functionGroup = functionGroup;
    }

    public String getMedicalApparatusClassification() {
        return medicalApparatusClassification;
    }

    public void setMedicalApparatusClassification(String medicalApparatusClassification) {
        this.medicalApparatusClassification = medicalApparatusClassification;
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
    }

    public String getManufactureId() {
        return manufactureId;
    }

    public void setManufactureId(String manufactureId) {
        this.manufactureId = manufactureId;
    }

    public String getManufactureTel() {
        return manufactureTel;
    }

    public void setManufactureTel(String manufactureTel) {
        this.manufactureTel = manufactureTel;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getVendorTel() {
        return vendorTel;
    }

    public void setVendorTel(String vendorTel) {
        this.vendorTel = vendorTel;
    }

    public String getMaitananceId() {
        return maitananceId;
    }

    public void setMaitananceId(String maitananceId) {
        this.maitananceId = maitananceId;
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

    public String getLocationNotes() {
        return locationNotes;
    }

    public void setLocationNotes(String locationNotes) {
        this.locationNotes = locationNotes;
    }

    public String getNurseLocation() {
        return nurseLocation;
    }

    public void setNurseLocation(String nurseLocation) {
        this.nurseLocation = nurseLocation;
    }

    public Integer getClinicalDeptId() {
        return clinicalDeptId;
    }

    public void setClinicalDeptId(Integer clinicalDeptId) {
        this.clinicalDeptId = clinicalDeptId;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public String getAccountingDeptUID() {
        return accountingDeptUID;
    }

    public void setAccountingDeptUID(String accountingDeptUID) {
        this.accountingDeptUID = accountingDeptUID;
    }

    public String getModalityType() {
        return modalityType;
    }

    public void setModalityType(String modalityType) {
        this.modalityType = modalityType;
    }

    public String getAssetDeptName() {
        return assetDeptName;
    }

    public void setAssetDeptName(String assetDeptName) {
        this.assetDeptName = assetDeptName;
    }

    public Integer getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(Integer assetGroup) {
        this.assetGroup = assetGroup;
    }

    public Integer getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(Integer assetClass) {
        this.assetClass = assetClass;
    }

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public Integer getAssetDeptId() {
        return assetDeptId;
    }

    public void setAssetDeptId(Integer assetDeptId) {
        this.assetDeptId = assetDeptId;
    }

    public String getAssetDeptUID() {
        return assetDeptUID;
    }

    public void setAssetDeptUID(String assetDeptUID) {
        this.assetDeptUID = assetDeptUID;
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

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean valid) {
        isValid = valid;
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

    public Date getLastMeasuringDate() {
        return lastMeasuringDate;
    }

    public void setLastMeasuringDate(Date lastMeasuringDate) {
        this.lastMeasuringDate = lastMeasuringDate;
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

    public Date getLastInventoryDate() {
        return lastInventoryDate;
    }

    public void setLastInventoryDate(Date lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }

    public Date getLastQaDate() {
        return lastQaDate;
    }

    public void setLastQaDate(Date lastQaDate) {
        this.lastQaDate = lastQaDate;
    }

    public Date getLastPmDateOne() {
        return lastPmDateOne;
    }

    public void setLastPmDateOne(Date lastPmDateOne) {
        this.lastPmDateOne = lastPmDateOne;
    }

    public Date getLastPmDateFour() {
        return lastPmDateFour;
    }

    public void setLastPmDateFour(Date lastPmDateFour) {
        this.lastPmDateFour = lastPmDateFour;
    }

    public Date getLastStockTakeDate() {
        return lastStockTakeDate;
    }

    public void setLastStockTakeDate(Date lastStockTakeDate) {
        this.lastStockTakeDate = lastStockTakeDate;
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

    public Date getAcceptDate() {
        return acceptDate;
    }

    public void setAcceptDate(Date acceptDate) {
        this.acceptDate = acceptDate;
    }

    public Integer getWarrantyMonth() {
        return warrantyMonth;
    }

    public void setWarrantyMonth(Integer warrantyMonth) {
        this.warrantyMonth = warrantyMonth;
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

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Integer getNetworkingStatus() {
        return networkingStatus;
    }

    public void setNetworkingStatus(Integer networkingStatus) {
        this.networkingStatus = networkingStatus;
    }

    public Integer getEmpowermentStatus() {
        return empowermentStatus;
    }

    public void setEmpowermentStatus(Integer empowermentStatus) {
        this.empowermentStatus = empowermentStatus;
    }

    public Integer getParentAssetId() {
        return parentAssetId;
    }

    public void setParentAssetId(Integer parentAssetId) {
        this.parentAssetId = parentAssetId;
    }

    public String getParentAssetUid() {
        return parentAssetUid;
    }

    public void setParentAssetUid(String parentAssetUid) {
        this.parentAssetUid = parentAssetUid;
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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getAssetValueCategory() {
        return assetValueCategory;
    }

    public void setAssetValueCategory(String assetValueCategory) {
        this.assetValueCategory = assetValueCategory;
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

    public Integer getMeasuringItemCategory() {
        return measuringItemCategory;
    }

    public void setMeasuringItemCategory(Integer measuringItemCategory) {
        this.measuringItemCategory = measuringItemCategory;
    }

    public Integer getMeteringCycle() {
        return meteringCycle;
    }

    public void setMeteringCycle(Integer meteringCycle) {
        this.meteringCycle = meteringCycle;
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

    public Date getNextMeteringDate() {
        return nextMeteringDate;
    }

    public void setNextMeteringDate(Date nextMeteringDate) {
        this.nextMeteringDate = nextMeteringDate;
    }

    public Boolean getIsWarranty() {
        return isWarranty;
    }

    public void setIsWarranty(Boolean warranty) {
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

    public Boolean getIsPm() {
        return isPm;
    }

    public void setIsPm(Boolean pm) {
        isPm = pm;
    }

    public Boolean getUseRegistration() {
        return isUseRegistration;
    }

    public void setUseRegistration(Boolean useRegistration) {
        isUseRegistration = useRegistration;
    }

    public Boolean getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Boolean fixed) {
        isFixed = fixed;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
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

    public String getProprietaryClass() {
        return proprietaryClass;
    }

    public void setProprietaryClass(String proprietaryClass) {
        this.proprietaryClass = proprietaryClass;
    }

    public Integer getGeDeviceType() {
        return geDeviceType;
    }

    public void setGeDeviceType(Integer geDeviceType) {
        this.geDeviceType = geDeviceType;
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

    public Integer getPmOwnerId() {
        return pmOwnerId;
    }

    public void setPmOwnerId(Integer pmOwnerId) {
        this.pmOwnerId = pmOwnerId;
    }

    public String getPatrolInspectionOwnerUID() {
        return patrolInspectionOwnerUID;
    }

    public void setPatrolInspectionOwnerUID(String patrolInspectionOwnerUID) {
        this.patrolInspectionOwnerUID = patrolInspectionOwnerUID;
    }

    public String getPatrolInspectionOwnerName() {
        return patrolInspectionOwnerName;
    }

    public void setPatrolInspectionOwnerName(String patrolInspectionOwnerName) {
        this.patrolInspectionOwnerName = patrolInspectionOwnerName;
    }

    public String getPatrolInspectionOwnerTel() {
        return patrolInspectionOwnerTel;
    }

    public void setPatrolInspectionOwnerTel(String patrolInspectionOwnerTel) {
        this.patrolInspectionOwnerTel = patrolInspectionOwnerTel;
    }

    public Integer getMeasuringOwnerId() {
        return measuringOwnerId;
    }

    public void setMeasuringOwnerId(Integer measuringOwnerId) {
        this.measuringOwnerId = measuringOwnerId;
    }

    public String getPmOwnerName() {
        return pmOwnerName;
    }

    public void setPmOwnerName(String pmOwnerName) {
        this.pmOwnerName = pmOwnerName;
    }

    public String getMeasuringOwnerName() {
        return measuringOwnerName;
    }

    public void setMeasuringOwnerName(String measuringOwnerName) {
        this.measuringOwnerName = measuringOwnerName;
    }

    public String getPmOwnerTel() {
        return pmOwnerTel;
    }

    public void setPmOwnerTel(String pmOwnerTel) {
        this.pmOwnerTel = pmOwnerTel;
    }

    public String getMeasuringOwnerTel() {
        return measuringOwnerTel;
    }

    public void setMeasuringOwnerTel(String measuringOwnerTel) {
        this.measuringOwnerTel = measuringOwnerTel;
    }

    public Integer getMvsEquip() {
        return mvsEquip;
    }

    public void setMvsEquip(Integer mvsEquip) {
        this.mvsEquip = mvsEquip;
    }

    public String getSubjectProject() {
        return subjectProject;
    }

    public void setSubjectProject(String subjectProject) {
        this.subjectProject = subjectProject;
    }

    public Boolean getHasOpenAlert() {
        return hasOpenAlert;
    }

    public void setHasOpenAlert(Boolean hasOpenAlert) {
        this.hasOpenAlert = hasOpenAlert;
    }

    public String getTerminationCause() {
        return terminationCause;
    }

    public void setTerminationCause(String terminationCause) {
        this.terminationCause = terminationCause;
    }

    public String getMeasuringType() {
        return measuringType;
    }

    public void setMeasuringType(String measuringType) {
        this.measuringType = measuringType;
    }

    public String getUCMID() {
        return UCMID;
    }

    public void setUCMID(String UCMID) {
        this.UCMID = UCMID;
    }

    public String getRfId() {
        return rfId;
    }

    public void setRfId(String rfId) {
        this.rfId = rfId;
    }

    public String getRfGroup() {
        return rfGroup;
    }

    public void setRfGroup(String rfGroup) {
        this.rfGroup = rfGroup;
    }

    public Double getPowerOnThreshold() {
        return powerOnThreshold;
    }

    public void setPowerOnThreshold(Double powerOnThreshold) {
        this.powerOnThreshold = powerOnThreshold;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public Integer getAssetDisposalResults() {
        return assetDisposalResults;
    }

    public void setAssetDisposalResults(Integer assetDisposalResults) {
        this.assetDisposalResults = assetDisposalResults;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }

    public String getSecondDirectory() {
        return secondDirectory;
    }

    public void setSecondDirectory(String secondDirectory) {
        this.secondDirectory = secondDirectory;
    }

    public Integer getConnectToInsite() {
        return connectToInsite;
    }

    public void setConnectToInsite(Integer connectToInsite) {
        this.connectToInsite = connectToInsite;
    }

    public Integer getIbDeviceStatus() {
        return ibDeviceStatus;
    }

    public void setIbDeviceStatus(Integer ibDeviceStatus) {
        this.ibDeviceStatus = ibDeviceStatus;
    }

    public Integer getVisibleStatus() {
        return visibleStatus;
    }

    public void setVisibleStatus(Integer visibleStatus) {
        this.visibleStatus = visibleStatus;
    }

    public Double getMonitorRate() {
        return monitorRate;
    }

    public void setMonitorRate(Double monitorRate) {
        this.monitorRate = monitorRate;
    }

    public Double getOtherPower() {
        return otherPower;
    }

    public void setOtherPower(Double otherPower) {
        this.otherPower = otherPower;
    }

    public Double getUnitPriceElectricity() {
        return unitPriceElectricity;
    }

    public void setUnitPriceElectricity(Double unitPriceElectricity) {
        this.unitPriceElectricity = unitPriceElectricity;
    }

    public Integer getAnnualMaintenanceTimes() {
        return annualMaintenanceTimes;
    }

    public void setAnnualMaintenanceTimes(Integer annualMaintenanceTimes) {
        this.annualMaintenanceTimes = annualMaintenanceTimes;
    }

    public Integer getAssetQuality() {
        return assetQuality;
    }

    public void setAssetQuality(Integer assetQuality) {
        this.assetQuality = assetQuality;
    }

    public Integer getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Integer runningStatus) {
        this.runningStatus = runningStatus;
    }

    public Integer getSharedStatus() {
        return sharedStatus;
    }

    public void setSharedStatus(Integer sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Integer getClinicalFunctionScore() {
        return clinicalFunctionScore;
    }

    public void setClinicalFunctionScore(Integer clinicalFunctionScore) {
        this.clinicalFunctionScore = clinicalFunctionScore;
    }

    public Integer getTangibleRiskScore() {
        return tangibleRiskScore;
    }

    public void setTangibleRiskScore(Integer tangibleRiskScore) {
        this.tangibleRiskScore = tangibleRiskScore;
    }

    public Integer getProblemAvoidanceRateScore() {
        return problemAvoidanceRateScore;
    }

    public void setProblemAvoidanceRateScore(Integer problemAvoidanceRateScore) {
        this.problemAvoidanceRateScore = problemAvoidanceRateScore;
    }

    public Integer getAccidentHistoryScore() {
        return accidentHistoryScore;
    }

    public void setAccidentHistoryScore(Integer accidentHistoryScore) {
        this.accidentHistoryScore = accidentHistoryScore;
    }

    public Integer getSpecialRequirementScore() {
        return specialRequirementScore;
    }

    public void setSpecialRequirementScore(Integer specialRequirementScore) {
        this.specialRequirementScore = specialRequirementScore;
    }

    public Boolean getRiskAssessment() {
        return isRiskAssessment;
    }

    public void setRiskAssessment(Boolean riskAssessment) {
        isRiskAssessment = riskAssessment;
    }

    public String getCheckCycle() {
        return checkCycle;
    }

    public void setCheckCycle(String checkCycle) {
        this.checkCycle = checkCycle;
    }

    public String getDetectionCycle() {
        return detectionCycle;
    }

    public void setDetectionCycle(String detectionCycle) {
        this.detectionCycle = detectionCycle;
    }

    public String getMaintenancePeriod() {
        return maintenancePeriod;
    }

    public void setMaintenancePeriod(String maintenancePeriod) {
        this.maintenancePeriod = maintenancePeriod;
    }

    public String getSumRiskScore() {
        return sumRiskScore;
    }

    public void setSumRiskScore(String sumRiskScore) {
        this.sumRiskScore = sumRiskScore;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getAssessmentBy() {
        return assessmentBy;
    }

    public void setAssessmentBy(String assessmentBy) {
        this.assessmentBy = assessmentBy;
    }

    public String getAssetAchievements() {
        return assetAchievements;
    }

    public void setAssetAchievements(String assetAchievements) {
        this.assetAchievements = assetAchievements;
    }

    public Integer getAssetQuantity() {
        return assetQuantity;
    }

    public void setAssetQuantity(Integer assetQuantity) {
        this.assetQuantity = assetQuantity;
    }

    public String getProductFamily() {
        return productFamily;
    }


    public void setProductFamily(String productFamily) {
        this.productFamily = productFamily;
    }

    public Integer getProductSegment() {
        return productSegment;
    }

    public void setProductSegment(Integer productSegment) {
        this.productSegment = productSegment;
    }

    public Date getMvsContractStartDate() {
        return mvsContractStartDate;
    }

    public void setMvsContractStartDate(Date mvsContractStartDate) {
        this.mvsContractStartDate = mvsContractStartDate;
    }

    public Date getMvsContractEndDate() {
        return mvsContractEndDate;
    }

    public void setMvsContractEndDate(Date mvsContractEndDate) {
        this.mvsContractEndDate = mvsContractEndDate;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

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

    public SharedAssetType getSharedAssetTypeEntity() {
        return sharedAssetTypeEntity;
    }

    public void setSharedAssetTypeEntity(SharedAssetType sharedAssetTypeEntity) {
        this.sharedAssetTypeEntity = sharedAssetTypeEntity;
    }

    public OrgInfo getSiteOrg() {
        return siteOrg;
    }

    public void setSiteOrg(OrgInfo siteOrg) {
        this.siteOrg = siteOrg;
    }

    public OrgInfo getAssetDeptOrg() {
        return assetDeptOrg;
    }

    public void setAssetDeptOrg(OrgInfo assetDeptOrg) {
        this.assetDeptOrg = assetDeptOrg;
    }

    public Integer getVerificationConclusion() {
        return verificationConclusion;
    }

    public void setVerificationConclusion(Integer verificationConclusion) {
        this.verificationConclusion = verificationConclusion;
    }

    public OrgInfo getClinicalDeptOrg() {
        return clinicalDeptOrg;
    }

    public void setClinicalDeptOrg(OrgInfo clinicalDeptOrg) {
        this.clinicalDeptOrg = clinicalDeptOrg;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getWarrantyValue() {
        return warrantyValue;
    }

    public void setWarrantyValue(String warrantyValue) {
        this.warrantyValue = warrantyValue;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(String branchIds) {
        this.branchIds = branchIds;
    }

    public String getAet() {
        return aet;
    }

    public void setAet(String aet) {
        this.aet = aet;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Date getStudydate() {
        return studydate;
    }

    public void setStudydate(Date studydate) {
        this.studydate = studydate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getStudyexposuretime() {
        return studyexposuretime;
    }

    public void setStudyexposuretime(Double studyexposuretime) {
        this.studyexposuretime = studyexposuretime;
    }

    public String getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(String usageRate) {
        this.usageRate = usageRate;
    }

    public Double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(Double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public String getDashboardPage() {
        return dashboardPage;
    }

    public void setDashboardPage(String dashboardPage) {
        this.dashboardPage = dashboardPage;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSharedAssetSwapIndex() {
        return sharedAssetSwapIndex;
    }

    public void setSharedAssetSwapIndex(String sharedAssetSwapIndex) {
        this.sharedAssetSwapIndex = sharedAssetSwapIndex;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public String getAssetUsedLifecycle() {
        return assetUsedLifecycle;
    }

    public void setAssetUsedLifecycle(String assetUsedLifecycle) {
        this.assetUsedLifecycle = assetUsedLifecycle;
    }

    public String getAssetLifecycleDeadline() {
        return assetLifecycleDeadline;
    }

    public void setAssetLifecycleDeadline(String assetLifecycleDeadline) {
        this.assetLifecycleDeadline = assetLifecycleDeadline;
    }

    public Date getOfflineUpdateDate() {
        return offlineUpdateDate;
    }

    public void setOfflineUpdateDate(Date offlineUpdateDate) {
        this.offlineUpdateDate = offlineUpdateDate;
    }

    public String getSharedAssetSwapIndexId() {
        return sharedAssetSwapIndexId;
    }

    public void setSharedAssetSwapIndexId(String sharedAssetSwapIndexId) {
        this.sharedAssetSwapIndexId = sharedAssetSwapIndexId;
    }

    public String getTerminateOperation() {
        return terminateOperation;
    }

    public void setTerminateOperation(String terminateOperation) {
        this.terminateOperation = terminateOperation;
    }

    public Date getTerminateOperationDate() {
        return terminateOperationDate;
    }

    public void setTerminateOperationDate(Date terminateOperationDate) {
        this.terminateOperationDate = terminateOperationDate;
    }

    public String getSupplierPyCode() {
        return supplierPyCode;
    }

    public void setSupplierPyCode(String supplierPyCode) {
        this.supplierPyCode = supplierPyCode;
    }

    public Boolean getCleaned() {
        return isCleaned;
    }

    public void setCleaned(Boolean cleaned) {
        isCleaned = cleaned;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getProductPlace() {
        return productPlace;
    }

    public void setProductPlace(String productPlace) {
        this.productPlace = productPlace;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public void setGuarantee(String guarantee) {
        this.guarantee = guarantee;
    }

    public Date getStartUsingDate() {
        return startUsingDate;
    }

    public void setStartUsingDate(Date startUsingDate) {
        this.startUsingDate = startUsingDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuthGroupUid() {
        return authGroupUid;
    }

    public void setAuthGroupUid(String authGroupUid) {
        this.authGroupUid = authGroupUid;
    }

    public Boolean getMvsTant() {
        return isMvsTant;
    }

    public void setMvsTant(Boolean mvsTant) {
        isMvsTant = mvsTant;
    }

    public Boolean getSharable() {
        return sharable;
    }

    public void setSharable(Boolean sharable) {
        this.sharable = sharable;
    }

    public Integer getIsOpenTube() {
        return isOpenTube;
    }

    public void setIsOpenTube(Integer isOpenTube) {
        this.isOpenTube = isOpenTube;
    }

    public Boolean getEnableLossOfCommunicationMonitoring() {
        return enableLossOfCommunicationMonitoring;
    }

    public void setEnableLossOfCommunicationMonitoring(Boolean enableLossOfCommunicationMonitoring) {
        this.enableLossOfCommunicationMonitoring = enableLossOfCommunicationMonitoring;
    }

    public Double getLocDurationDeviceStatus() {
        return locDurationDeviceStatus;
    }

    public void setLocDurationDeviceStatus(Double locDurationDeviceStatus) {
        this.locDurationDeviceStatus = locDurationDeviceStatus;
    }

    public Double getLocDurationEnvMonitoring() {
        return locDurationEnvMonitoring;
    }

    public void setLocDurationEnvMonitoring(Double locDurationEnvMonitoring) {
        this.locDurationEnvMonitoring = locDurationEnvMonitoring;
    }

    public Integer getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(Integer connectivity) {
        this.connectivity = connectivity;
    }

    public String getOwnerDeptCode() {
        return ownerDeptCode;
    }

    public void setOwnerDeptCode(String ownerDeptCode) {
        this.ownerDeptCode = ownerDeptCode;
    }

    public String getUseDeptCode() {
        return useDeptCode;
    }

    public void setUseDeptCode(String useDeptCode) {
        this.useDeptCode = useDeptCode;
    }

    public Integer getMeasureDevice() {
        return measureDevice;
    }

    public void setMeasureDevice(Integer measureDevice) {
        this.measureDevice = measureDevice;
    }

    public Integer getHasRegistrationNo() {
        return hasRegistrationNo;
    }

    public void setHasRegistrationNo(Integer hasRegistrationNo) {
        this.hasRegistrationNo = hasRegistrationNo;
    }

    public String getAbnormalFeedback() {
        return abnormalFeedback;
    }

    public void setAbnormalFeedback(String abnormalFeedback) {
        this.abnormalFeedback = abnormalFeedback;
    }

    public String getAbnormalReason() {
        return abnormalReason;
    }

    public void setAbnormalReason(String abnormalReason) {
        this.abnormalReason = abnormalReason;
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
    }

    public String getTubeWatchStatus() {
        return tubeWatchStatus;
    }

    public void setTubeWatchStatus(String tubeWatchStatus) {
        this.tubeWatchStatus = tubeWatchStatus;
    }

    public String getAssetLocation() {
        return assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }

    public String getSpare01() {
        return spare01;
    }

    public void setSpare01(String spare01) {
        this.spare01 = spare01;
    }

    public String getSpare02() {
        return spare02;
    }

    public void setSpare02(String spare02) {
        this.spare02 = spare02;
    }

    public String getSpare03() {
        return spare03;
    }

    public void setSpare03(String spare03) {
        this.spare03 = spare03;
    }

    public String getSpare04() {
        return spare04;
    }

    public void setSpare04(String spare04) {
        this.spare04 = spare04;
    }

    public String getSpare05() {
        return spare05;
    }

    public void setSpare05(String spare05) {
        this.spare05 = spare05;
    }

    public Long getBillingTime() {
        return billingTime;
    }

    public void setBillingTime(Long billingTime) {
        this.billingTime = billingTime;
    }

    public Double getBillingCost() {
        return billingCost;
    }

    public void setBillingCost(Double billingCost) {
        this.billingCost = billingCost;
    }

    public Integer getBorrowedStatus() {
        return borrowedStatus;
    }

    public void setBorrowedStatus(Integer borrowedStatus) {
        this.borrowedStatus = borrowedStatus;
    }

    public String getBorrowConfigId() {
        return borrowConfigId;
    }

    public void setBorrowConfigId(String borrowConfigId) {
        this.borrowConfigId = borrowConfigId;
    }

    public String getSupplierNewUID() {
        return supplierNewUID;
    }

    public void setSupplierNewUID(String supplierNewUID) {
        this.supplierNewUID = supplierNewUID;
    }

    public Set<AssetTags> getAssetTags() {
        return assetTags;
    }

    public void setAssetTags(Set<AssetTags> assetTags) {
        this.assetTags = assetTags;
    }

    public String getLastCheckCompany() {
        return lastCheckCompany;
    }

    public void setLastCheckCompany(String lastCheckCompany) {
        this.lastCheckCompany = lastCheckCompany;
    }

    public String getWarrantyAlertStatus() {
        return warrantyAlertStatus;
    }

    public void setWarrantyAlertStatus(String warrantyAlertStatus) {
        this.warrantyAlertStatus = warrantyAlertStatus;
    }

    public AssetFileImportBaseService.ImportStatus getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(AssetFileImportBaseService.ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    public List<String> getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(List<String> errMsg) {
        this.errMsg = errMsg;
    }

    public void addErrMsg(String msg) {
        this.errMsg.add(msg);
    }

    public String getOldQrCode() {
        return oldQrCode;
    }

    public void setOldQrCode(String oldQrCode) {
        this.oldQrCode = oldQrCode;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }

    public String getFunctionGroupStr() {
        return functionGroupStr;
    }

    public void setFunctionGroupStr(String functionGroupStr) {
        this.functionGroupStr = functionGroupStr;
    }

    public String getAssetGroupStr() {
        return assetGroupStr;
    }

    public void setAssetGroupStr(String assetGroupStr) {
        this.assetGroupStr = assetGroupStr;
    }

    public String getAssetClassStr() {
        return assetClassStr;
    }

    public void setAssetClassStr(String assetClassStr) {
        this.assetClassStr = assetClassStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getDepreciationMethodStr() {
        return depreciationMethodStr;
    }

    public void setDepreciationMethodStr(String depreciationMethodStr) {
        this.depreciationMethodStr = depreciationMethodStr;
    }

    public String getDeviceStatusStr() {
        return deviceStatusStr;
    }

    public void setDeviceStatusStr(String deviceStatusStr) {
        this.deviceStatusStr = deviceStatusStr;
    }

    public String getNetworkingStatusStr() {
        return networkingStatusStr;
    }

    public void setNetworkingStatusStr(String networkingStatusStr) {
        this.networkingStatusStr = networkingStatusStr;
    }

    public String getAssetUsageStr() {
        return assetUsageStr;
    }

    public void setAssetUsageStr(String assetUsageStr) {
        this.assetUsageStr = assetUsageStr;
    }

    public String getCapitalSourceStr() {
        return capitalSourceStr;
    }

    public void setCapitalSourceStr(String capitalSourceStr) {
        this.capitalSourceStr = capitalSourceStr;
    }

    public String getCapitalSource2Str() {
        return capitalSource2Str;
    }

    public void setCapitalSource2Str(String capitalSource2Str) {
        this.capitalSource2Str = capitalSource2Str;
    }

    public String getRiskGradeStr() {
        return riskGradeStr;
    }

    public void setRiskGradeStr(String riskGradeStr) {
        this.riskGradeStr = riskGradeStr;
    }

    public String getMeasureGradeStr() {
        return measureGradeStr;
    }

    public void setMeasureGradeStr(String measureGradeStr) {
        this.measureGradeStr = measureGradeStr;
    }

    public String getEmergencyEquipmentStr() {
        return emergencyEquipmentStr;
    }

    public void setEmergencyEquipmentStr(String emergencyEquipmentStr) {
        this.emergencyEquipmentStr = emergencyEquipmentStr;
    }

    public String getImportEquipmentStr() {
        return importEquipmentStr;
    }

    public void setImportEquipmentStr(String importEquipmentStr) {
        this.importEquipmentStr = importEquipmentStr;
    }

    public String getMeasuringItemCategoryStr() {
        return measuringItemCategoryStr;
    }

    public void setMeasuringItemCategoryStr(String measuringItemCategoryStr) {
        this.measuringItemCategoryStr = measuringItemCategoryStr;
    }

    public String getAccountingCategoryStr() {
        return accountingCategoryStr;
    }

    public void setAccountingCategoryStr(String accountingCategoryStr) {
        this.accountingCategoryStr = accountingCategoryStr;
    }

    public String getRepairFormStr() {
        return repairFormStr;
    }

    public void setRepairFormStr(String repairFormStr) {
        this.repairFormStr = repairFormStr;
    }

    public String getAssetQualityStr() {
        return assetQualityStr;
    }

    public void setAssetQualityStr(String assetQualityStr) {
        this.assetQualityStr = assetQualityStr;
    }

    public String getProductSegmentStr() {
        return productSegmentStr;
    }

    public void setProductSegmentStr(String productSegmentStr) {
        this.productSegmentStr = productSegmentStr;

    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public String getAssetCode68Str() {
        return assetCode68Str;
    }

    public void setAssetCode68Str(String assetCode68Str) {
        this.assetCode68Str = assetCode68Str;
    }

    public Double getLifecycleInYear() {
        if (lifecycle == null) {
            return null;
        }
        return new Double(lifecycle) / 12.0;
    }

    public void setLifecycleInYear(Double lifecycleInYear) {
        if (lifecycleInYear == null) {
            this.lifecycle = null;
        } else {
            this.lifecycle = new Double(lifecycleInYear * 12).intValue();
        }
    }

    public List<String> getDeletedField() {
        return deletedField;
    }

    public void setDeletedField(List<String> deletedField) {
        this.deletedField = deletedField;
    }

    /**
     * 导入新数据资产的时候统一设置默认值
     */
    public void setDefultValue() {
        if (Objects.isNull(status)) {
            this.setStatus(1);
        }
        if (Objects.isNull(isPm)) {
            this.setIsPm(true);
        }
        //新增资产默认内修
        if (Objects.isNull(uid)) {
            this.setRepairForm(1);
        }
        if (Objects.isNull(isChecked)) {
            this.setIsChecked(true);
        }
        if (Objects.isNull(isUseRegistration)) {
            this.setUseRegistration(true);
        }
        if (Objects.isNull(isWarranty)) {
            this.setIsWarranty(true);
        }
        if (Objects.isNull(isFixed)) {
            this.setIsFixed(true);
        }
        if (Objects.isNull(networkingStatus)) {
            this.setNetworkingStatus(0);
        }
        this.setIsValid(true);
        this.setConnectToInsite(3);
        this.setIbDeviceStatus(5);
        this.setVisibleStatus(1);
    }

    public String getAssetGroupSecondaryStr() {
        return assetGroupSecondaryStr;
    }

    public void setAssetGroupSecondaryStr(String assetGroupSecondaryStr) {
        this.assetGroupSecondaryStr = assetGroupSecondaryStr;
    }
}
