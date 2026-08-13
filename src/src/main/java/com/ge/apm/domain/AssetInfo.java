/*
 */
package com.ge.apm.domain;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.service.utils.ChineseName;
import com.ge.apm.service.utils.PinyinUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author 212547631
 */
@Entity
@Table(name = "asset_info", indexes = {@Index(columnList = "asset_dept_uid"), @Index(columnList = "shared_asset_type")})
public class AssetInfo implements ChineseName, Serializable {

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
    private Integer siteId;

    @Basic(optional = false)
    @Size(min = 1, max = 128)
    @Column(name = "name")
    private String name;

    @Column(name = "name_py_code")
    private String namePyCode;

    @Size(max = 64)
    @Column(name = "alias_name")
    private String aliasName;//资产别名

    @Column(name = "function_group")
    private Integer functionGroup;//功能分组   医疗器械分类(旧)

    @Column(name = "medical_apparatus_classification")
    private String medicalApparatusClassification; // 医疗器械分类

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

    @Transient
    @TableField(exist = false)
    private String manufactureTel;//制造商电话

    @Size(max = 128)
    @Column(name = "vendor")
    private String vendor;//供应商名称 =Supplier.name

    @Column(name = "supplier_id")
    private Integer supplierId; //供应商Id =Supplier.id

    @Transient
    @TableField(exist = false)
    private String vendorTel;//供应商电话

    @Column(name = "maitanance_id")
    private String maitananceId;

    @Size(max = 64)
    @Column(name = "maitanance")
    private String maitanance;//维修商

    @Transient
    @TableField(exist = false)
    private String maitananceTel;//维修电话

    @Size(max = 64)
    @Column(name = "national_standard_code")
    private String nationalStandardCode;//2010国家编码

    @Size(max = 128)
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

    @Size(max = 64)
    @Column(name = "location_notes")
    private String locationNotes;//安装位置备注

    @Size(max = 64)
    @Column(name = "nurse_location")
    private String nurseLocation;//护士检查设备位置

    @Column(name = "clinical_dept_id")
    private Integer clinicalDeptId;//所属科室

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备所属科室

    @Size(max = 64)
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;//所属科室

    @Column(name = "accounting_dept_uid", columnDefinition = "CHAR(32)")
    private String accountingDeptUID;//账号科室

    @Transient
    @TableField(exist = false)
    private OrgInfo accountingDeptOrg;


    @Column(name = "modality_type")
    private String modalityType;

    @Transient
    @TableField(exist = false)
    private String assetDeptName;//使用科室

    @Column(name = "asset_group")
    private Integer assetGroup;//设备分类

    @Column(name = "asset_class") // 1 医疗设备  2 后勤资产 3 信息类设备
    private Integer assetClass;//资产分类

    @Column(name = "asset_group_secondary")
    private Integer assetGroupSecondary;//设备分类2级

    @Column(name = "asset_dept_id")
    private Integer assetDeptId;//使用设备科

    @Column(name = "asset_dept_uid", columnDefinition = "CHAR(32)") // 使用科室UID
    private String assetDeptUID;

    @Column(name = "asset_type") // 设备类型, 支持自定义
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
    private Date lastPmDate;//3级保养 上次保养日期

    @Column(name = "last_metering_date")
    @Temporal(TemporalType.DATE)
    private Date lastMeteringDate;//上次计量日期

    @Column(name = "last_inventory_date")
    @Temporal(TemporalType.DATE)
    private Date lastInventoryDate;//上次盘点日期

    @Column(name = "last_qa_date")
    @Temporal(TemporalType.DATE)
    private Date lastQaDate;//2级保养 上次保养日期

    @Column(name = "last_pm_date_one")
    @Temporal(TemporalType.DATE)
    private Date lastPmDateOne;//1级保养 上次保养日期
    @Column(name = "last_pm_date_four")
    @Temporal(TemporalType.DATE)
    private Date lastPmDateFour;//4级保养 上次保养日期

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
    private Date factoryWarrantyDate;//质保截止日期

    @Column(name = "accept_date")
    @Temporal(TemporalType.DATE)
    private Date acceptDate;//验收日期

    @Column(name = "warranty_month")
    private Integer warrantyMonth;//质保时间（月）

    @Size(max = 64)
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

    @Column(name = "networking_status")
    private Integer networkingStatus = 0;  //联网状态 0 未联网  1 内网  3 外网

    //null   1 empowerment  //这个字段不需要编辑和展示，所以只存null 和1
    @Column(name = "empowerment_status")
    private Integer empowermentStatus;

    @Column(name = "parent_asset_id")
    private Integer parentAssetId;
    @Column(name = "parent_asset_uid", columnDefinition = "CHAR(32)")
    private String parentAssetUid;

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
    @Transient
    private String remark;
   /* @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(insertable = false, nullable = false,updatable = false,name = "uid",referencedColumnName = "id")
    private MvsAssetInfo mvsAssetInfo;*/

    @Column(name = "hospital_id")
    @Basic(optional = false)
    @NotNull
    private Integer hospitalId;

    @JoinColumn(insertable = false, updatable = false, name = "hospital_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JsonBackReference
    @NotFound(action = NotFoundAction.IGNORE)
    private OrgInfo hospital;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    //资产价值分类
    @Column(name = "asset_value_category")
    private String assetValueCategory;

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
    //1:待完善,2:待审核,3:已经建档  4 审核不通过
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

    @Column(name = "is_use_registration")  //是否使用登记
    private Boolean isUseRegistration = Boolean.TRUE;

    @Column(name = "is_fixed")
    private Boolean isFixed = Boolean.TRUE;//是否固定资产

    @Column(name = "is_checked")
    private Boolean isChecked = Boolean.TRUE;//是否盘点

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

    //专有分类 YHAAO-3890 一般设备、仪器仪表及量具、专用设备 3类
    @Column(name = "proprietary_class")
    private String proprietaryClass;

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

    @Column(name = "pm_owner_id")
    private Integer pmOwnerId;//保养负责人

    @Column(name = "patrol_inspection_owner_uid")
    private String patrolInspectionOwnerUID;//保养巡检负责人

    @Column(name = "patrol_inspection_owner_name")
    private String patrolInspectionOwnerName;//保养巡检负责人名称

    @Column(name = "patrol_inspection_owner_tel")
    private String patrolInspectionOwnerTel;//保养巡检负责人电话

    @Column(name = "measuring_owner_id")
    private Integer measuringOwnerId;//计量负责人

    @Column(name = "pm_owner_name")
    private String pmOwnerName;

    @Column(name = "measuring_owner_name")
    private String measuringOwnerName;

    @Column(name = "pm_owner_tel")
    private String pmOwnerTel;

    @Column(name = "measuring_owner_tel")
    private String measuringOwnerTel;

    @Column(name = "mvs_equip")
    private Integer mvsEquip;

    // 所属课题项目
    @Column(name = "subject_project")
    private String subjectProject;

    @Column(name = "has_open_alert")
    private Boolean hasOpenAlert;

    @Size(max = 128)
    @Column(name = "termination_cause")
    private String terminationCause;

    @Column(name = "measuring_type")
    private String measuringType;

    @Column(name = "UCMID")
    private String UCMID;


    //射频码
    @Column(name = "rf_id")
    private String rfId;
    //射频码分组
    @Column(name = "rf_group")
    private String rfGroup;
    //开机电流阈值
    @Column(name = "power_on_threshold")
    private Double powerOnThreshold;
    //电源ID
    @Column(name = "current_id")
    private String currentId;

    @Column(name = "asset_disposal_results")
    private Integer assetDisposalResults;

//    @Size(max = 256)
//    @Column(name = "one_qr_code")
//    private String oneQrCode;
//
//    @Transient
//    @TableField(exist = false)
//    private String oneQrCodeStatus;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "one_qr_code", referencedColumnName = "qr_code", unique = true, insertable = false, updatable = false)
//    private QrCodeLib qrCodeLib;

    /**
     * 主目录
     */
    @Column(name = "main_directory")
    private String mainDirectory;

    /**
     * 子目录
     */
    @Column(name = "second_directory")
    private String secondDirectory;

    //联通状态，默认 1未联通，2 联通, 3 N/A
    @Column(name = "connect_to_insite")
    private Integer connectToInsite = 3;

    //默认1：未见异常、 2：报警、 3：维修中、 4: 无联通无报警、 5： 无显示
    @Column(name = "ib_device_status")
    private Integer ibDeviceStatus = 5;

    //全量IB客户可见性标记
    @Column(name = "visible_status")
    private Integer visibleStatus = 1;

    @Column(name = "monitor_rate")//主机功率
    private Double monitorRate;

    @Column(name = "other_power")//其他功率
    private Double otherPower;

    @Column(name = "unit_price_electricity")//电费单价
    private Double unitPriceElectricity;

    // 年保养次数
    @Column(name = "annual_maintenance_times")
    private Integer annualMaintenanceTimes;

    // 设备性质
    @Column(name = "asset_quality")
    private Integer assetQuality;

    //无人值守设备状态  0:关机  1:待机 2:在用
    @Column(name = "running_status")
    private Integer runningStatus;

    //1- 正常可借用（无人值守可以调剂）， 2-无人值守不可以调剂
    @Column(name = "shared_status")
    private Integer sharedStatus;

    //县域ib合同类别
    @Column(name = "contract_type")
    private String contractType;

    //临床功能得分
    @Column(name = "clinical_function_score")
    private Integer clinicalFunctionScore;

    //有形风险得分
    @Column(name = "tangible_risk_score")
    private Integer tangibleRiskScore;

    //问题避免率得分
    @Column(name = "problem_avoidance_rate_score")
    private Integer problemAvoidanceRateScore;

    //事故历史得分
    @Column(name = "accident_history_score")
    private Integer accidentHistoryScore;

    //制造商/管理部门特殊要求得分
    @Column(name = "special_requirement_score")
    private Integer specialRequirementScore;
    //是否风险评估
    @Column(name = "is_risk_assessment")
    private Boolean isRiskAssessment;
    @Getter
    @Setter
    @Column(name = "check_cycle")
    private String checkCycle; //检查周期
    @Getter
    @Setter
    @Column(name = "detection_cycle")
    private String detectionCycle; //检测周期
    @Getter
    @Setter
    @Column(name = "maintenance_period")
    private String maintenancePeriod; //保养周期
    @Getter
    @Setter
    @Column(name = "sum_risk_score")
    private String sumRiskScore; //风险总分
    @Getter
    @Setter
    @Column(name = "assessment_date")
    private Date assessmentDate; //评估日期
    @Getter
    @Setter
    @Column(name = "assessment_by")
    private String assessmentBy; //评估人员
    @Getter
    @Setter
    @Column(name = "asset_achievements")
    private String assetAchievements; //设备绩效

    @Getter
    @Setter
    @Column(name = "asset_quantity")
    private Integer assetQuantity; //设备数量 YHAAO-16852

    @Getter
    @Setter
    @Column(name = "product_family")
    private String productFamily; //系列
    @Getter
    @Setter
    @Column(name = "product_segment")
    private Integer productSegment;//高端/中端/低端
    @Getter
    @Setter
    @Column(name = "mvs_contract_start_date")
    private Date mvsContractStartDate;//续签合同start date
    @Getter
    @Setter
    @Column(name = "mvs_contract_end_date")
    private Date mvsContractEndDate;//续签合同end date
    @Getter
    @Setter
    @Column(name = "asset_lifecycle_deadline")
    private Date assetLifecycleDeadline;


    @Transient
    @TableField(exist = false)
    private int lineNum;

    @Transient
    @TableField(exist = false)
    private Set<SharedAssetReqDetail> sharedAssetReqDetailList;

    @Transient
    @TableField(exist = false)
    private SharedAssetReqDetail sharedAssetReqDetail;

    @Transient
    @TableField(exist = false)
    private SharedAssetType sharedAssetTypeEntity;

    @Transient
    @TableField(exist = false)
    private OrgInfo siteOrg;

    @Transient
    @TableField(exist = false)
    private OrgInfo assetDeptOrg;

    @Transient
    @TableField(exist = false)
    private Boolean canBorrow;

    @Transient
    @TableField(exist = false)
    private Boolean canReturn;

    @Transient
    @TableField(exist = false)
    private Boolean canEdit;

    @Transient
    @TableField(exist = false)
    private Integer verificationConclusion;

    @Transient
    @TableField(exist = false)
    private OrgInfo clinicalDeptOrg;

    @Transient
    @TableField(exist = false)
    private Supplier supplier;
    @Transient
    @TableField(exist = false)
    private String isPmValue;
    @Transient
    @TableField(exist = false)
    private String isCheckedValue;
    @Transient
    @TableField(exist = false)
    private String warrantyValue;
    @Transient
    @TableField(exist = false)
    @Getter
    @Setter
    private String errorMessage;
    @Transient
    @TableField(exist = false)
    @Getter
    @Setter
    private String branchIds;
    @Transient
    @TableField(exist = false)
    private String aet;
    @Transient
    @TableField(exist = false)
    @Getter
    @Setter
    private Integer rank;
    @Transient
    @TableField(exist = false)
    private Date studydate;
    @Transient
    @TableField(exist = false)
    private Date startTime;
    @Transient
    @TableField(exist = false)
    private Date endTime;
    @Transient
    @TableField(exist = false)
    private Double studyexposuretime;
    @Transient
    @TableField(exist = false)
    private String usageRate;
    @Transient
    @TableField(exist = false)
    private Double energyConsumption;
    @Transient
    @TableField(exist = false)
    private String dashboardPage;
    @Transient
    @TableField(exist = false)
    private String assetGroupName;//设备分类名称
    @Transient
    @TableField(exist = false)
    private List<String> tags = new ArrayList<>();
    @Transient
    @TableField(exist = false)
    private String encodeAssetDetailStr;

    @Transient
    @TableField(exist = false)
    private String sharedAssetSwapIndex;

    @Transient
    @TableField(exist = false)
    private Date checkDate;

    @Transient
    @TableField(exist = false)
    private String checkPerson;


    @Transient
    @TableField(exist = false)
    private String department;

    @Transient
    @TableField(exist = false)
    private String boUuid;

    @Transient
    @TableField(exist = false)
    @Setter
    private String assetUsedLifecycle;

    @Transient
    @TableField(exist = false)
    @Getter @Setter
    private String warrantyStatus;

    @Getter @Setter
    @Column(name = "offline_update_date")
    private Date offlineUpdateDate;

    @Getter @Setter
    @Column(name = "usage_count")
    private Integer usageCount;

    @Getter @Setter
    @Column(name = "rating_usage_count")
    private Integer ratingUsageCount;

    @Getter @Setter
    @Column(name = "epc")
    private String epc;

    public String getSharedAssetSwapIndexId() {
        return sharedAssetSwapIndexId;
    }

    public void setSharedAssetSwapIndexId(String sharedAssetSwapIndexId) {
        this.sharedAssetSwapIndexId = sharedAssetSwapIndexId;
    }

    @Transient
    @TableField(exist = false)
    private String sharedAssetSwapIndexId;

    //待报废操作人
    @Transient
    @TableField(exist = false)
    private String terminateOperation;
    @Transient
    @TableField(exist = false)
    private Date terminateOperationDate;
    //待报废操作人
    @Transient
    @TableField(exist = false)
    private String terminateBusinessNumber;
    @Transient
    @TableField(exist = false)
    private String workOrderId;


    @Column(name = "supplier_py_code")
    private String supplierPyCode;

    @Column(name = "is_cleaned")
    private Boolean isCleaned = true;

    @Temporal(TemporalType.DATE)
    private Date lastMeasuringDate;

    // 入库单号
    @Column(name = "receipt_number")
    private String receiptNumber;

    // 产地
    @Column(name = "product_place")
    private String productPlace;

    // 保修
    @Column(name = "guarantee")
    private String guarantee;

    @Column(name = "start_using_date")
    @Temporal(TemporalType.DATE)
    private Date startUsingDate;
    @Column(name = "address")
    private String address;

    @Column(name = "auth_group_uid")
    private String authGroupUid;

    @Transient
    @JsonProperty
    @Column(name = "is_mvs_tant")
    private Boolean isMvsTant;

    @Column(name = "sharable")
    private Boolean sharable;
    @Column(name = "is_open_tube")
    private Integer isOpenTube;//0：不开启 1：开启

    //是否开启设备失联监控
    @Column(name = "enable_loss_of_communication_monitoring")
    private Boolean enableLossOfCommunicationMonitoring;

    //设备状态失联时间（小时）
    @Column(name = "loc_duration_device_status")
    private Double locDurationDeviceStatus;

    //环境监控失联时间（小时）
    @Column(name = "loc_duration_env_monitoring")
    private Double locDurationEnvMonitoring;
    @Column(name = "connectivity")
    private Integer connectivity;

    // 所属科室代码
    @Column(name = "owner_dept_code")
    private String ownerDeptCode;

    // 使用科室代码
    @Column(name = "use_dept_code")
    private String useDeptCode;

    // 是否是计量设备
    @Column(name = "measure_device")
    private Integer measureDevice;

    // 是否有注册证
    @Column(name = "has_registration_no")
    private Integer hasRegistrationNo;

    /**
     * 异常反馈
     */
    @Column(name = "abnormal_feedback")
    private String abnormalFeedback;

    /**
     * 异常原因
     */
    @Column(name = "abnormal_reason")
    @Size(min = 0, max = 256)
    private String abnormalReason;
    @Column(name = "install_status")
    private Integer installStatus;
    /**
     * 智慧球服务
     * 0:关闭 1：开启
     */
    @Column(name = "tubewatch_status")
    private String tubeWatchStatus;

    /**
     * 当前位置
     */
    @Column(name = "asset_location")
    private String assetLocation;

    @Getter
    @Setter
    @Column(name = "spare01")
    private String spare01;
    @Getter
    @Setter
    @Column(name = "spare02")
    private String spare02;
    @Getter
    @Setter
    @Column(name = "spare03")
    private String spare03;
    @Getter
    @Setter
    @Column(name = "spare04")
    private String spare04;
    @Getter
    @Setter
    @Column(name = "spare05")
    private String spare05;
    @Getter
    @Setter
    @Column(name = "spare06")
    private String spare06;
    @Getter
    @Setter
    @Column(name = "spare07")
    private String spare07;
    @Getter
    @Setter
    @Column(name = "spare08")
    private String spare08;
    @Getter
    @Setter
    @Column(name = "spare09")
    private String spare09;
    @Getter
    @Setter
    @Column(name = "spare10")
    private String spare10;
    @Getter
    @Setter
    @Column(name = "spare11")
    private Date spare11;
    @Getter
    @Setter
    @Column(name = "spare12")
    private Date spare12;
    @Getter
    @Setter
    @Column(name = "spare13")
    private Date spare13;
    @Getter
    @Setter
    @Column(name = "spare14")
    private Date spare14;
    @Getter
    @Setter
    @Column(name = "spare15")
    private Date spare15;
    @Getter
    @Setter
    @Column(name = "billing_time")
    private Long billingTime; // 累计借用计费时长分钟
    @Getter
    @Setter
    @Column(name = "billing_cost", columnDefinition = "decimal(11,2)")
    private Double billingCost;//累计费用
    @Getter
    @Setter
    @Column(name = "borrowed_status")
    private Integer borrowedStatus;  // 借用状态 -1不可借出，0 可借，1占用
    @Getter
    @Setter
    @Column(name = "borrow_config_id")
    private String borrowConfigId;
    @Column(name = "is_special_asset", columnDefinition = "boolean default false")
    private boolean isSpecialAsset; //是否特种设备
    @Column(name = "asset_scrap_id")
    private String assetScrapId; //设备报废id

    @Column(name = "asset_classification32")
    private String assetClassification32;
    @Getter
    @Setter
    @Column(name = "tube_install_date")
    @Temporal(TemporalType.DATE)
    private Date tubeInstallDate;          //球管安装时间
    @Getter
    @Setter
    @Column(name = "total_exposure_count")
    private Integer totalExposureCount;    //曝光总次数
    @Getter
    @Setter
    @Column(name = "total_inspection_count")
    private Integer totalInspectionCount;  //检查总人数


    @Transient
    @TableField(exist = false)
    private SelectItemDto assetClassification32Dto;
    @Transient
    @TableField(exist = false)
    private String category1;
    @Transient
    @TableField(exist = false)
    private String category2;
    @Transient
    @TableField(exist = false)
    private String category3;
    @Transient
    @TableField(exist = false)
    private String categoryRadio;
    @Transient
    @TableField(exist = false)
    private boolean editing = false;
    @Transient
    @TableField(exist = false)
    private BigDecimal scaleAssessment;
    @Transient
    @TableField(exist = false)
    private String measuringAlertStatus;
    @Transient
    private String createType;
    @Transient
    private String existFileFlag;
    @Transient
    @Getter @Setter
    private String overrunState;@Getter
    @Setter
    @Column(name = "estimated_risk_level")
    private Integer estimatedRiskLevel; //风险等级

    @Getter
    @Setter
    @Column(name = "estimated_risk_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedRiskDate; //预估风险评估日期

    public String getMeasuringAlertStatus() {
        if (ObjectUtil.isNotEmpty(nextMeteringDate)) {
            DateTime dt = TimeUtil.toJodaDate(nextMeteringDate);
            if(dt.isBeforeNow()) {
                measuringAlertStatus = "measuringExpiredSoon30";
            } else if(dt.minusDays(30).isBeforeNow()) {
                measuringAlertStatus = "measuringExpiredSoon30";
            }else if(dt.minusDays(90).isBeforeNow()){
                measuringAlertStatus = "measuringExpiredSoon";
            }
        }
        return measuringAlertStatus;
    }

    public void setMeasuringAlertStatus(String measuringAlertStatus) {
        this.measuringAlertStatus = measuringAlertStatus;
    }

    public String getTerminateBusinessNumber() {
        return terminateBusinessNumber;
    }

    public void setTerminateBusinessNumber(String terminateBusinessNumber) {
        this.terminateBusinessNumber = terminateBusinessNumber;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public boolean isSpecialAsset() {
        return isSpecialAsset;
    }

    public void setSpecialAsset(boolean specialAsset) {
        isSpecialAsset = specialAsset;
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

    public Date getLastMeasuringDate() {
        return lastMeasuringDate;
    }

    public void setLastMeasuringDate(Date lastMeasuringDate) {
        this.lastMeasuringDate = lastMeasuringDate;
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

    public void setWarrantyAlertStatus(String warrantyAlertStatus) {
        this.warrantyAlertStatus = warrantyAlertStatus;
    }

    public Boolean getIsMvsTant() {
        return isMvsTant == null ? false : isMvsTant;
    }

    public void setIsMvsTant(Boolean isMvsTant) {
        this.isMvsTant = isMvsTant;
    }

    public String getSupplierPyCode() {
        return supplierPyCode;
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

    public Integer getAssetDisposalResults() {
        return assetDisposalResults;
    }

    public void setAssetDisposalResults(Integer assetDisposalResults) {
        this.assetDisposalResults = assetDisposalResults;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getDashboardPage() {
        return dashboardPage;
    }

    public void setDashboardPage(String dashboardPage) {
        this.dashboardPage = dashboardPage;
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

    public String getAet() {
        return aet;
    }

    public void setAet(String aet) {
        this.aet = aet;
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

    public Double getMonitorRate() {
        return monitorRate;
    }

    public void setMonitorRate(Double monitorRate) {
        this.monitorRate = monitorRate == null ? 0D : monitorRate;
    }

    public Double getUnitPriceElectricity() {
        return unitPriceElectricity;
    }

    public void setUnitPriceElectricity(Double unitPriceElectricity) {
        this.unitPriceElectricity = unitPriceElectricity;
    }

    public Double getOtherPower() {
        return otherPower;
    }

    public void setOtherPower(Double otherPower) {
        this.otherPower = otherPower == null ? 0D : otherPower;
    }

    /**
     * 新供应商uid 关联 tenant_info
     */
    @Column(name = "supplier_new_uid")
    private String supplierNewUID;


    public String getSupplierNewUID() {
        return supplierNewUID;
    }

    public void setSupplierNewUID(String supplierNewUID) {
        this.supplierNewUID = supplierNewUID;
    }

    public String getIsCheckedValue() {
        return isCheckedValue;
    }

    public void setIsCheckedValue(String isCheckedValue) {
        this.isCheckedValue = isCheckedValue;
    }

    public String getWarrantyValue() {
        return warrantyValue;
    }

    public void setWarrantyValue(String warrantyValue) {
        this.warrantyValue = warrantyValue;
    }

    public String getIsPmValue() {
        return isPmValue;
    }

    public void setIsPmValue(String isPmValue) {
        this.isPmValue = isPmValue;
    }

    public List<String> getTags() {
        if (tags == null) {
            return Collections.emptyList();
        } else {
            return tags;
        }
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAssetTagsView() {
        String assetTagsView = "";
        if (assetTags == null) {
            return assetTagsView;
        }
        for (AssetTags tag : assetTags) {
            assetTagsView += tag.getTag() + ",";
        }
        if (!"".equals(assetTagsView)) {
            assetTagsView = assetTagsView.substring(0, assetTagsView.length() - 1);
        }
        return assetTagsView;
    }

    public Set<AssetTags> getAssetTags() {
        return assetTags;
    }

    public void setAssetTags(Set<AssetTags> assetTags) {
        this.assetTags = assetTags;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Set<AssetTags> assetTags;

    @Column(name = "last_check_company")
    private String lastCheckCompany;

    public String getLastCheckCompany() {
        return lastCheckCompany;
    }

    public void setLastCheckCompany(String lastCheckCompany) {
        this.lastCheckCompany = lastCheckCompany;
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

    public OrgInfo getAssetDeptOrg() {
        return assetDeptOrg;
    }

    public void setAssetDeptOrg(OrgInfo assetDeptOrg) {
        this.assetDeptOrg = assetDeptOrg;
    }

    public Boolean getCanBorrow() {
        Boolean rst = Boolean.FALSE;
        if (this.clinicalDeptUID != null && this.clinicalDeptUID.equals(this.assetDeptUID) && (this.status == 1 || this.status == 3)) {
            rst = Boolean.TRUE;
        }
        return rst;
    }

    public Boolean getCanReturn() {
        Boolean rst = Boolean.FALSE;
        if (this.clinicalDeptUID != null && !this.clinicalDeptUID.equals(this.assetDeptUID)) {
            rst = Boolean.TRUE;
            if (this.sharedAssetReqDetail != null && "调剂无人值守".equals(this.sharedAssetReqDetail.getClinicalDeptName())) {
                rst = Boolean.FALSE;
            }
        }
        return rst;
    }

    //
    public Boolean getCanEdit() {
        Boolean rst = Boolean.FALSE;
        if (this.sharedAssetReqDetail != null && "调剂无人值守".equals(this.sharedAssetReqDetail.getClinicalDeptName())) {
            rst = Boolean.TRUE;
        }
        return rst;
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

    public OrgInfo getClinicalDeptOrg() {
        return clinicalDeptOrg;
    }

    public void setClinicalDeptOrg(OrgInfo clinicalDeptOrg) {
        this.clinicalDeptOrg = clinicalDeptOrg;
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

    public Integer getVerificationConclusion() {
        return verificationConclusion;
    }

    public void setVerificationConclusion(Integer verificationConclusion) {
        this.verificationConclusion = verificationConclusion;
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
        if (!StringUtils.isEmpty(name)) {
            this.namePyCode = PinyinUtil.getShortPinyin(name).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        }

    }

    public String getDepartNumAndName() {
        return name + (StringUtils.isEmpty(departNum) ? "" : departNum);
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
        if (!StringUtils.isEmpty(manufacture)) {
            this.manufacturePyCode = PinyinUtil.getShortPinyin(manufacture).toLowerCase().replaceAll("[^0-9a-zA-Z]", "");
        }

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

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
        if (!StringUtils.isEmpty(clinicalDeptName)) {
            this.clinicalDeptNamePyCode = PinyinUtil.getShortPinyin(clinicalDeptName).toLowerCase();
        }
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

    public Boolean getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Boolean isFixed) {
        this.isFixed = isFixed;
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

    public Integer getAssetGroupSecondary() {
        return assetGroupSecondary;
    }

    public void setAssetGroupSecondary(Integer assetGroupSecondary) {
        this.assetGroupSecondary = assetGroupSecondary;
    }

    public String getAssetValueCategory() {
        return assetValueCategory;
    }

    public void setAssetValueCategory(String assetValueCategory) {
        this.assetValueCategory = assetValueCategory;
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

    public String getPmOwnerName() {
        return pmOwnerName;
    }

    public void setPmOwnerName(String pmOwnerName) {
        this.pmOwnerName = pmOwnerName;
    }

    public String getPmOwnerTel() {
        return pmOwnerTel;
    }

    public void setPmOwnerTel(String pmOwnerTel) {
        this.pmOwnerTel = pmOwnerTel;
    }

    public Integer getMvsEquip() {
        return mvsEquip;
    }

    public void setMvsEquip(Integer mvsEquip) {
        this.mvsEquip = mvsEquip;
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

    public String getSubjectProject() {
        return subjectProject;
    }

    public void setSubjectProject(String subjectProject) {
        this.subjectProject = subjectProject;
    }

    public String getProprietaryClass() {
        return proprietaryClass;
    }

    public void setProprietaryClass(String proprietaryClass) {
        this.proprietaryClass = proprietaryClass;
    }

    public Boolean isHasOpenAlert() {
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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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
        return BooleanUtils.isTrue(isRiskAssessment);
    }

    public void setRiskAssessment(Boolean riskAssessment) {
        isRiskAssessment = riskAssessment;
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
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getPinYin() {
        return getNamePyCode();
    }

    public AssetInfo(Integer id, String name, String uid, String departNum, String serialNum, String functionType, String clinicalDeptName, String clinicalDeptUID, String nurseLocation) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.departNum = departNum;
        this.serialNum = serialNum;
        this.functionType = functionType;
        this.clinicalDeptName = clinicalDeptName;
        this.clinicalDeptUID = clinicalDeptUID;
        this.nurseLocation = nurseLocation;
    }

    public AssetInfo(Integer id, String name, String uid, String departNum, String serialNum, String functionType, String clinicalDeptName, String clinicalDeptUID, String nurseLocation, String qrCode) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.departNum = departNum;
        this.serialNum = serialNum;
        this.functionType = functionType;
        this.clinicalDeptName = clinicalDeptName;
        this.clinicalDeptUID = clinicalDeptUID;
        this.nurseLocation = nurseLocation;
        this.qrCode = qrCode;
    }

    public AssetInfo(Integer id, String name, String uid, String departNum, String serialNum, String functionType, String clinicalDeptName, String clinicalDeptUID, String nurseLocation, String qrCode, String siteUID, String hospitalUID, String institutionUID, String tenantUID) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.departNum = departNum;
        this.serialNum = serialNum;
        this.functionType = functionType;
        this.clinicalDeptName = clinicalDeptName;
        this.clinicalDeptUID = clinicalDeptUID;
        this.nurseLocation = nurseLocation;
        this.qrCode = qrCode;
        this.siteUID = siteUID;
        this.hospitalUID = hospitalUID;
        this.institutionUID = institutionUID;
        this.tenantUID = tenantUID;
    }

    public Map<String, Object> buildLeaseReqHeader() {
        Map<String, Object> reqHeader = new HashMap<>();
        reqHeader.put("clinicalDeptUID", this.getAssetDeptUID());
        if (this.getSharedAssetReqDetail().getLeaseRequest() == null) {
            reqHeader.put("type", "2");
        } else {
            reqHeader.put("type", this.getSharedAssetReqDetail().getLeaseRequest().getType());
        }
        reqHeader.put("details", new LinkedList<Map<String, String>>());
        return reqHeader;
    }

    public Map<String, Object> buildLeaseReqDetail() {
        Map<String, Object> reqDetail = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        reqDetail.put("assetInfoUID", this.getUid());
        reqDetail.put("chargingBeginTime", sdf.format(this.getSharedAssetReqDetail().getChargingBeginTime()));
        reqDetail.put("willBeDelivered", this.getSharedAssetReqDetail().getWillBeDelivered());
        reqDetail.put("patientNum", this.getSharedAssetReqDetail().getPatientNum());
        reqDetail.put("patientBedNum", this.getSharedAssetReqDetail().getPatientBedNum());
        reqDetail.put("remark", this.getSharedAssetReqDetail().getRemark());
        return reqDetail;
    }


    public Map<String, Object> buildReturnReqHeader() {
        Map<String, Object> reqHeader = new HashMap<>();
        reqHeader.put("clinicalDeptUID", this.getAssetDeptUID());
        reqHeader.put("type", this.getSharedAssetReqDetail().getReturnRequest().getType());
        reqHeader.put("details", new LinkedList<Map<String, String>>());
        return reqHeader;
    }

    public Map<String, Object> buildReturnReqDetail() {
        Map<String, Object> reqDetail = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        reqDetail.put("assetInfoUID", this.getUid());
        reqDetail.put("chargingBeginTime", sdf.format(this.getSharedAssetReqDetail().getChargingBeginTime()));
        reqDetail.put("chargingEndTime", sdf.format(this.getSharedAssetReqDetail().getChargingEndTime()));
        reqDetail.put("isIntact", this.getSharedAssetReqDetail().getIntact());
        reqDetail.put("compensationAmount", Optional.ofNullable(this.getSharedAssetReqDetail().getCompensationAmount()).orElse(BigDecimal.valueOf(0)).doubleValue());
        reqDetail.put("reqComment", this.getSharedAssetReqDetail().getReqComment());
        reqDetail.put("patientNum", this.getSharedAssetReqDetail().getPatientNum());
        reqDetail.put("patientBedNum", this.getSharedAssetReqDetail().getPatientBedNum());
        return reqDetail;
    }

    public Integer getMeasuringOwnerId() {
        return measuringOwnerId;
    }

    public void setMeasuringOwnerId(Integer measuringOwnerId) {
        this.measuringOwnerId = measuringOwnerId;
    }

    /*public MvsAssetInfo getMvsAssetInfo() {
        return mvsAssetInfo;
    }

    public void setMvsAssetInfo(MvsAssetInfo mvsAssetInfo) {
        this.mvsAssetInfo = mvsAssetInfo;
    }*/

    @Transient
    @TableField(exist = false)
    private String warrantyAlertStatus = null;

    public String getWarrantyAlertStatus() {
        return warrantyAlertStatus;
    }

    public void setWarrantyExpireAlertDays(Integer warrantyAlertDays) {
        if (warrantyAlertDays == null) return; // WarrantyAlertDays not configured, so no Alert for Warranty Alert

        if (warrantyDate != null) {
            // first check Warranty Date
            DateTime dt = TimeUtil.toJodaDate(warrantyDate);
            if (dt.isBeforeNow())
                warrantyAlertStatus = "warrantyExpired";
            else if (dt.minusDays(warrantyAlertDays).isBeforeNow())
                warrantyAlertStatus = "warrantyExpireSoon";
        }
        /*if (factoryWarrantyDate != null) {
            // then check factory Warranty Date
            DateTime dt = TimeUtil.toJodaDate(factoryWarrantyDate);
            if (dt.isBeforeNow())
                warrantyAlertStatus = "warrantyExpired";
            else if (dt.minusDays(warrantyAlertDays).isBeforeNow())
                warrantyAlertStatus = "warrantyExpireSoon";
        }*/
    }

    public String getMeasuringOwnerName() {
        return measuringOwnerName;
    }

    public void setMeasuringOwnerName(String measuringOwnerName) {
        this.measuringOwnerName = measuringOwnerName;
    }

    public String getMeasuringOwnerTel() {
        return measuringOwnerTel;
    }

    public void setMeasuringOwnerTel(String measuringOwnerTel) {
        this.measuringOwnerTel = measuringOwnerTel;
    }

    public String getParentAssetDisplayName() {

        return makeEmptyStringIfNull(this.getName()) + "/" + makeEmptyStringIfNull(this.getQrCode()) + "/" + makeEmptyStringIfNull(this.getSerialNum());

    }

    private String makeEmptyStringIfNull(String input) {
        if (input == null) {
            return "";
        } else {
            return input;
        }
    }

    public Double getPowerOnThreshold() {
        return powerOnThreshold;
    }

    public void setPowerOnThreshold(Double powerOnThreshold) {
        this.powerOnThreshold = powerOnThreshold;
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

    public String getUCMID() {
        return UCMID;
    }

    public void setUCMID(String UCMID) {
        this.UCMID = UCMID;
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

    public OrgInfo getSiteOrg() {
        return siteOrg;
    }

    public void setSiteOrg(OrgInfo siteOrg) {
        this.siteOrg = siteOrg;
    }

    public Integer getVisibleStatus() {
        return visibleStatus;
    }

    public void setVisibleStatus(Integer visibleStatus) {
        this.visibleStatus = visibleStatus;
    }

//    public String getOneQrCode() {
//        return oneQrCode;
//    }
//
//    public void setOneQrCode(String oneQrCode) {
//        this.oneQrCode = oneQrCode;
//    }
//
//    public String getOneQrCodeStatus() {
//        return oneQrCodeStatus;
//    }
//
//    public void setOneQrCodeStatus(String oneQrCodeStatus) {
//        this.oneQrCodeStatus = oneQrCodeStatus;
//    }
//
//    public QrCodeLib getQrCodeLib() {
//        return qrCodeLib;
//    }
//
//    public void setQrCodeLib(QrCodeLib qrCodeLib) {
//        this.qrCodeLib = qrCodeLib;
//    }

    public Integer getAnnualMaintenanceTimes() {
        return annualMaintenanceTimes;
    }

    public void setAnnualMaintenanceTimes(Integer annualMaintenanceTimes) {
        this.annualMaintenanceTimes = annualMaintenanceTimes;
    }

    public String getSharedAssetSwapIndex() {
        return sharedAssetSwapIndex;
    }

    public void setSharedAssetSwapIndex(String sharedAssetSwapIndex) {
        this.sharedAssetSwapIndex = sharedAssetSwapIndex;
    }

    public Integer getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Integer runningStatus) {
        this.runningStatus = runningStatus;
    }

    public Integer getAssetQuality() {
        return assetQuality;
    }

    public void setAssetQuality(Integer assetQuality) {
        this.assetQuality = assetQuality;
    }

    public Integer getSharedStatus() {
        return sharedStatus == null ? 1 : sharedStatus;
    }

    public void setSharedStatus(Integer sharedStatus) {
        this.sharedStatus = sharedStatus;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Integer getEmpowermentStatus() {
        return empowermentStatus;
    }

    public void setEmpowermentStatus(Integer empowermentStatus) {
        this.empowermentStatus = empowermentStatus;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public Integer getNetworkingStatus() {
        return networkingStatus;
    }

    public void setNetworkingStatus(Integer networkingStatus) {
        this.networkingStatus = networkingStatus;
    }

    public Boolean getIsUseRegistration() {
        return isUseRegistration;
    }

    public void setIsUseRegistration(Boolean isUseRegistration) {
        this.isUseRegistration = isUseRegistration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getParentAssetUid() {
        return parentAssetUid;
    }

    public void setParentAssetUid(String parentAssetUid) {
        this.parentAssetUid = parentAssetUid;
    }

    public void clearDefaultValue() {
        this.isChecked = null;
        this.isFixed = null;
        this.isWarranty = null;
        this.isCleaned = null;
        this.isPm = null;
        this.isUseRegistration = null;
        this.networkingStatus = null;
    }

    public String getAssetLocation() {
        return assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public String getAssetUsedLifecycle() {
        if (ObjectUtil.isNotEmpty(assetUsedLifecycle)) {
            return assetUsedLifecycle;
        }
        if (ObjectUtil.isEmpty(installDate) || ObjectUtil.isEmpty(lifecycle)) {
            return null;
        }
        java.time.temporal.Temporal temporal1 = LocalDate.parse(SDF.format(installDate));
        java.time.temporal.Temporal temporal2 = LocalDate.parse(SDF.format(System.currentTimeMillis()));
        return String.format("%.0f", Math.floor(Double.parseDouble(String.valueOf(ChronoUnit.MONTHS.between(temporal1, temporal2))) / 12));
    }

    public String getEncodeAssetDetailStr() {
        String url = "actionName=View&selectedid="
                + getUid()
                + "&asset_name=" + getName()
                + "&terminate=" + Boolean.parseBoolean(WebUtil.getRequestParameter("terminate"));
        url = UrlParamUtil.encodeUrlQueryString(url);
        return url;
    }

    public void setEncodeAssetDetailStr(String encodeAssetDetailStr) {
        this.encodeAssetDetailStr = encodeAssetDetailStr;
    }


    public String getAssetScrapId() {
        return assetScrapId;
    }

    public void setAssetScrapId(String assetScrapId) {
        this.assetScrapId = assetScrapId;
    }

    public BigDecimal getScaleAssessment() {
        return scaleAssessment;
    }

    public void setScaleAssessment(BigDecimal scaleAssessment) {
        this.scaleAssessment = scaleAssessment;
    }

    public OrgInfo getAccountingDeptOrg() {
        return accountingDeptOrg;
    }

    public void setAccountingDeptOrg(OrgInfo accountingDeptOrg) {
        this.accountingDeptOrg = accountingDeptOrg;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public String getAssetClassification32() {
        return assetClassification32;
    }

    public void setAssetClassification32(String assetClassification32) {
        this.assetClassification32 = assetClassification32;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public boolean isEditing() { return editing; }
    public void setEditing(boolean editing) { this.editing = editing; }

    public String getCategoryRadio() {
        return categoryRadio;
    }

    public void setCategoryRadio(String categoryRadio) {
        this.categoryRadio = categoryRadio;
    }

    public SelectItemDto getAssetClassification32Dto() {
        return assetClassification32Dto;
    }

    public void setAssetClassification32Dto(SelectItemDto assetClassification32Dto) {
        this.assetClassification32Dto = assetClassification32Dto;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getexistFileFlag() {
        return existFileFlag;
    }

    public void setexistFileFlag(String existFileFlag) {
        this.existFileFlag = existFileFlag;
    }

    @Transient
    @TableField(exist = false)
    private String uploadUserName;

    public String getUploadUserName() {
        return uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }

    @Transient
    @TableField(exist = false)
    private String certificateType;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }
    @Transient
    @TableField(exist = false)
    private Integer validityMonths;

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }

    @Transient
    @TableField(exist = false)
    private Double measureCycle;

    public Double getMeasureCycle() {
        return measureCycle;
    }

    public void setMeasureCycle(Double measureCycle) {
        this.measureCycle = measureCycle;
    }

    @Transient
    @TableField(exist = false)
    private Boolean hasDateWarning;

    @Transient
    @TableField(exist = false)
    private String dateWarningMessage;

    public Boolean getHasDateWarning() {
        return hasDateWarning;
    }

    public void setHasDateWarning(Boolean hasDateWarning) {
        this.hasDateWarning = hasDateWarning;
    }

    public String getDateWarningMessage() {
        return dateWarningMessage;
    }

    public void setDateWarningMessage(String dateWarningMessage) {
        this.dateWarningMessage = dateWarningMessage;
    }

    /**
     * 校验计量日期和下次计量日期
     * 返回校验结果，如果有警告则返回警告信息
     * @return 警告信息，如果没有警告则返回 null
     */
    public String validateMeasuringDates() {
        StringBuilder warningMsg = new StringBuilder();
        boolean hasWarning = false;

        // 1. 如果计量日期或下次计量日期为空
        if (checkDate == null && nextMeteringDate == null) {
            warningMsg.append("计量日期和下次计量日期为空");
            hasWarning = true;
        } else if (checkDate == null) {
            warningMsg.append("计量日期为空");
            hasWarning = true;
        } else if (nextMeteringDate == null) {
            warningMsg.append("下次计量日期为空");
            hasWarning = true;
        }

        // 2. 如果计量日期大于下次计量日期
        if (checkDate != null && nextMeteringDate != null && checkDate.after(nextMeteringDate)) {
            if (hasWarning) {
                warningMsg.append("; ");
            }
            warningMsg.append("计量日期不能大于下次计量日期");
            hasWarning = true;
        }

        return hasWarning ? warningMsg.toString() : null;
    }
}
