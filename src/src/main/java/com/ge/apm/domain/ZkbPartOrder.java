package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Entity
@Table(name = "zkb_part_order")
public class ZkbPartOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Transient
    public static String DATA_TYPE_TEST="test.";

    @Transient
    public static String CONFIG_US = "US";

    /**
     * Pending：申请 TechAccept：技术审批通过 TechReject：技术审批不通过 ManageAccept：区域经理审批通过
     * ManageReject：区域经理审批不通过 Ordered：发货 OrderedReject:发货取消 FourHundredCancel:400取消
     * Returned：归还  Closed：已完成
     * 已归还之后
     */
    public enum PartOrderStatus {
        Requested,TechnicalAccept,TechnicalReject,ProductAccept,ProductReject, ManageAccept,ManageReject,UnOrdered,
        UnOrderedReject, Ordered,OrderedReject,PartReturned,Returned,Closed,DirectorAccept, DirectorReject,CommercialAccept,
        CommercialReject,FourHundredCancel, CloseReject,TseAccept,TseReject,OsscAccept,OsscReject,OsscReturned,CloseCommited,
        ReturnCompleted,CloseApproved,CloseFailed
    }

    /**
     * Parts:备件申请
     * Probe:探头申请
     * Artificial:人工申请
     */
    public enum OrderType {
        Parts,Probe,Artificial,Tool
    }

    public enum ProbeQuotaTypeEnum{
        Any,Special,Normal,High
    }

    public enum CcsbQuotaTypeEnum{
        Repair, Maintain
    }

    public enum RadiationQuotaTypeEnum{
        SpareArtificial,PartsHigh
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 额度申请唯一编号（系统自动生成）
     */
    @Column(name = "part_order_no")
    private String partOrderNo;

    /**
     * 唯一编号自增索引
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_order_index")
    private Integer partOrderIndex;

    /**
     * 合同id
     */
    @Column(name = "contract_id")
    private String contractId;

    /**
     * 工单ID
     */
    @Column(name = "work_order_id")
    private String workOrderId;

    /**
     * 设备ID
     */
    @Column(name = "asset_id")
    private String assetId;

    /**
     * 区域
     */
    @Column(name = "region")
    private String region;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private String orderType;

    /**
     * 订单日期
     */
    @Column(name = "part_order_date")
    private Date partOrderDate;

    /**
     * 当前步骤
     */
    @Column(name = "current_step")
    private String currentStep;

    /**
     * 当前状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 数量
     */
    @Column(name = "qty")
    private Integer qty;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 申请人
     */
    @Column(name = "request_user")
    private String requestUser;

    /**
     * 申请时间
     */
    @Column(name = "request_date")
    private Date requestDate;

    /**
     * 申请人电话
     */
    @Column(name = "request_phone")
    private String requestPhone;

    /**
     * 技术支持审批备注
     */
    @Column(name = "tech_remark")
    private String techRemark;

    @Column(name = "tech_audit_date")
    private Date techAuditDate;

    @Column(name = "tech_auditer")
    private String techAuditer;

    @Column(name = "tech_auditer_uid")
    private String techAuditerUid;

    /**
     * 区域经理审批备注
     */
    @Column(name = "manage_remark")
    private String manageRemark;

    @Column(name = "manage_audit_date")
    private Date manageAuditDate;

    @Column(name = "manage_auditer")
    private String manageAuditer;
    @Column(name = "manage_auditer_uid")
    private String manageAuditerUid;
    /**
     * Tse审批备注
     */
    @Column(name = "tse_remark")
    private String tseRemark;

    @Column(name = "tse_audit_date")
    private Date tseAuditDate;

    @Column(name = "tse_auditer")
    private String tseAuditer;

    @Column(name = "tse_auditer_uid")
    private String tseAuditerUid;

    /**
     * ossc审批备注
     */

    @Column(name = "ossc_part_no")
    private String osscPartNo;
    @Column(name = "ossc_remark")
    private String osscRemark;

    @Column(name = "ossc_audit_date")
    private Date osscAuditDate;

    @Column(name = "ossc_auditer")
    private String osscAuditer;
    @Column(name = "ossc_auditer_uid")
    private String osscAuditerUid;



    @Column(name = "ossc_return_audit_date")
    private Date osscReturnAuditDate;

    @Column(name = "ossc_return_auditer")
    private String osscReturnAuditer;
    @Column(name = "ossc_return_auditer_uid")
    private String osscReturnAuditerUid;


    /**
     * 产品经理审批备注
     */
    @Column(name = "product_remark")
    private String productRemark;

    @Column(name = "product_audit_date")
    private Date productAuditDate;

    @Column(name = "product_auditer")
    private String productAuditer;
    @Column(name = "product_auditer_uid")
    private String productAuditerUid;

    /**
     * 销售总监审批备注
     */
    @Column(name = "director_remark")
    private String directorRemark;

    @Column(name = "director_audit_date")
    private Date directorAuditDate;

    @Column(name = "director_auditer")
    private String directorAuditer;
    @Column(name = "director_auditer_uid")
    private String directorAuditerUid;

    /**
     * commercial审批备注
     */
    @Column(name = "commercial_remark")
    private String commercialRemark;

    @Column(name = "commercial_audit_date")
    private Date commercialAuditDate;

    @Column(name = "commercial_auditer")
    private String commercialAuditer;
    @Column(name = "commercial_auditer_uid")
    private String commercialAuditerUid;

    /**
     *
     */
    @Column(name = "sr_no")
    private String srNo;

    /**
     *
     */
    @Column(name = "activity_no")
    private String activityNo;

    /**
     *
     */
    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "price")
    private String price;

    /**
     * 订货备注
     */
    @Column(name = "ordered_remark")
    private String orderedRemark;

    @Column(name = "un_order_remark")
    private String unOrderRemark;


    @Column(name = "ordered_date")
    private Date orderedDate;

    @Column(name = "un_order_date")
    private Date unOrderDate;

    @Column(name = "un_order_auditer")
    private String unOrderAuditer;

    @Column(name = "ordered_auditer")
    private String orderedAuditer;

    @Column(name = "un_order_auditer_uid")
    private String unOrderAuditerUid;

    @Column(name = "ordered_auditer_uid")
    private String orderedAuditerUid;

    /**
     * 归还类型
     */
    @Column(name = "return_type")
    private String returnType;

    @Column(name = "return_qty")
    private Integer returnQty = 0;

    /**
     * 运单号
     */
    @Column(name = "waybill_no")
    private String waybillNo;

    /**
     * 收货人
     */
    @Column(name = "receiver")
    private String receiver;

    /**
     * 收货人电话
     */
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @Column(name = "receiver_address")
    private String receiverAddress;

    /**
     * 归还日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    @Column(name = "part_serial_number")
    private String partSerialNumber;


    @Column(name = "part_quality_issue")
    private String partQualityIssue;

    @Column(name = "udi")
    private String udi;

    @Column(name = "internal_packaging")
    private String internalPackaging;

    @Column(name = "quality_seal_type")
    private String qualitySealType;

    @Column(name = "quality_seal_status")
    private String qualitySealStatus;

    @Column(name = "part_quality")
    private String partQuality;

    /**
     * 归还备注
     */
    @Column(name = "return_remark")
    private String returnRemark;

    @Column(name = "return_audit_date")
    private Date returnAuditDate;

    @Column(name = "return_auditer")
    private String returnAuditer;

    @Column(name = "return_auditer_uid")
    private String returnAuditerUid;

    /**
     * 关单备注
     */
    @Column(name = "closed_remark")
    private String closedRemark;

    @Column(name = "closed_date")
    private Date closedDate;

    @Column(name = "closed_auditer")
    private String closedAuditer;

    @Column(name = "closed_auditer_uid")
    private String closedAuditerUid;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    /**
     * 医院名称
     */
    @Column(name = "hospital_name")
    private String hospitalName;

    /**
     * 医院报修人姓名
     */
    @Column(name = "hospital_repairer")
    private String hospitalRepairer;

    /**
     * 医院报修人电话
     */
    @Column(name = "hospital_repairer_phone")
    private String hospitalRepairerPhone;

    /**
     * 产品型号
     */
    @Column(name = "product_number")
    private String productNumber;

    /**
     * 软件版本
     */
    @Column(name = "software_version")
    private String softwareVersion;

    /**
     * 故障描述
     */
    @Column(name = "fault_description")
    private String faultDescription;

    /**
     * 订单标注
     */
    @Column(name = "order_annotation")
    private String orderAnnotation;

    /**
     * 发生问题的order number
     */
    @Column(name = "problem_order_number")
    private String problemOrderNumber;

    /**
     * 是否为紧急订单
     */
    @Column(name = "urgent_order")
    private String urgentOrder;

    /**
     * 订单紧急的原因
     */
    @Column(name = "urgency_reasons")
    private String urgencyReasons;

    @Column(name = "online_engineer")
    private String onlineEngineer;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "online_engineer", referencedColumnName = "id")
    private ZkbPartOrderUserConfig onlineEngine;

    /**
     * GE工程师
     */
    @Column(name = "ge_engineer_name")
    private String geEngineerName;

    /**
     * GE工程师电话
     */
    @Column(name = "ge_engineer_phone")
    private String geEngineerPhone;

    /**
     * ZKB工程师姓名
     */
    @Column(name = "zkb_engineer_name")
    private String zkbEngineerName;

    /**
     * ZKB工程师电话
     */
    @Column(name = "zkb_engineer_phone")
    private String zkbEngineerPhone;

    @Transient
    List<ZkbPartOrderItem> zkbPartOrderItems = new ArrayList<>();

    @Transient
    Set<ZkbPartOrderStep> zkbPartOrderStepSet = new HashSet<>();

    @Column(name = "sr_num")
    private String srNum;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "cat_id")
    private String catId;

    @Column(name = "artificial_type")
    private String artificialType;

    /**
     * 探头号
     */
    @Column(name = "cat_no")
    private String catNo;

    /**
     * 旧探头SN号
     */
    @Column(name = "old_cat_sn")
    private String oldCatSn;

    /**
     * 新探头SN编号
     */
    @Column(name = "new_cat_sn")
    private String newCatSn;

    @Column(name = "cat_num")
    private Integer catNum;

    @Column(name = "cat_model_non_ge")
    private String catModelNonGe;

    @Column(name = "contract_cat_id")
    private String contractCatId;

    @Column(name = "contract_sn_no")
    private String contractSnNo;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "cat_id", referencedColumnName = "id")
    private ZkbPartOrderCat zkbPartOrderCat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "asset_id", referencedColumnName = "uid")
    private AssetInfo assetInfo;

    @Transient
    private Integer remainQty;

    @Column(name = "asset_type_di")
    private String assetTypeDi; //ct xr

    /**
     * 是否扣除名额
     * t f
     */
    @Column(name = "whether_deduct")
    private String whetherDeduct;

    /**
     * 是否全新探头
     */
    @Column(name = "new_cat_flag")
    private String newCatFlag;

    /**
     * 租户是否全新探头
     */
    @Column(name = "tenant_new_cat_flag")
    private String tenantNewCatFlag="f";

    /**
     * 是否上过培训课
     */
    @Column(name = "have_train_course_flag")
    private String haveTrainCourseFlag;

    @Column(name = "need_letter_flag")
    private String needLetterFlag = "f";

    @Column(name = "upload_letter_flag")
    private String uploadLetterFlag = "f";

    @Column(name = "upload_letter_file_name")
    private String uploadLetterFileName;

    @Column(name = "upload_letter_file_Id")
    private String uploadLetterFileId;

    @Column(name = "check_apply_num_flag")
    private String checkApplyNumFlag = "t";// t是校验通过 f：校验不通过

    /**
     * 用于区分数据是真实数据还是测试数据
     * 真实数据字段为空，测试数据值为test.
     */
    @Column(name = "data_type")
    private String dataType;

    /**
     * 控制数据是否被重复操作
     */
    @Column(name = "data_flag")
    private Integer dataFlag = 0;

    @Column(name = "score_flag")
    private String scoreFlag = "f";

    @Column(name = "high_cat_flag")
    private String highCatFlag;

    /**
     * asset_info systemid
     */
    @Transient
    private String systemId;

    @Column(name = "probe_quota_type")
    private String probeQuotaType;

    /**
     * t f
     */
    @Column(name = "modify_flag")
    private String modifyFlag;

    /**
     * t f
     */
    @Column(name = "ssa_key_flag")
    private String ssaKeyFlag;

    @Column(name = "maintain_flag")
    private String maintainFlag;

    @Column(name = "maintain_first_flag")
    private String maintainFirstFlag;

    /**
     * 兑换类型 artificial application
     */
    @Column(name = "exchange_type")
    private String exchangeType;

    @Column(name = "supplement_flag")
    private String supplementFlag;

    @Transient
    private String regionalManagerName;//区域经理姓名
    @Transient
    private String regionalManagerEmail;//区域经理邮箱
    @Transient
    private String regionalManagerPhone;//区域经理电话

    @Column(name = "ccsb_apply_type")
    private String ccsbApplyType;// apply maintain

    @Column(name = "radiation_quota_type")
    private String radiationQuotaType;

    @Column(name = "parts_high_type")
    private String partsHighType;

    /**
     * 是否非ge设备 t是非ge设备，空是ge设备
     */
    @Column(name = "not_ge_asset")
    private String notGeAsset;

    @Column(name = "repair_flag")
    private String repairFlag;

    @Column(name = "take_flag")
    private String takeFlag;

    @Column(name = "un_taked_reason")
    private String unTakedReason;

    @Column(name = "un_repair_reason")
    private String unRepairReason;

    @Column(name = "fault_un_closed_sr")
    private String faultUnClosedSr;

    /**
     * 是否工作站  t f
     */
    @Column(name = "work_station_flag")
    private String workStationFlag;

    @Column(name = "return_complete_auditer")
    private String returnCompleteAuditer;

    @Column(name = "return_complete_audit_date")
    private Date returnCompleteAuditDate;

    @Column(name = "return_complete_auditer_uid")
    private String returnCompleteAuditerUid;

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public String getInternalPackaging() {
        return internalPackaging;
    }

    public void setInternalPackaging(String internalPackaging) {
        this.internalPackaging = internalPackaging;
    }

    public String getQualitySealType() {
        return qualitySealType;
    }

    public void setQualitySealType(String qualitySealType) {
        this.qualitySealType = qualitySealType;
    }

    public String getQualitySealStatus() {
        return qualitySealStatus;
    }

    public void setQualitySealStatus(String qualitySealStatus) {
        this.qualitySealStatus = qualitySealStatus;
    }

    public String getPartQuality() {
        return partQuality;
    }

    public void setPartQuality(String partQuality) {
        this.partQuality = partQuality;
    }

    public String getReturnCompleteAuditer() {
        return returnCompleteAuditer;
    }

    public void setReturnCompleteAuditer(String returnCompleteAuditer) {
        this.returnCompleteAuditer = returnCompleteAuditer;
    }

    public Date getReturnCompleteAuditDate() {
        return returnCompleteAuditDate;
    }

    public void setReturnCompleteAuditDate(Date returnCompleteAuditDate) {
        this.returnCompleteAuditDate = returnCompleteAuditDate;
    }

    public String getReturnCompleteAuditerUid() {
        return returnCompleteAuditerUid;
    }

    public void setReturnCompleteAuditerUid(String returnCompleteAuditerUid) {
        this.returnCompleteAuditerUid = returnCompleteAuditerUid;
    }

    public String getWorkStationFlag() {
        return workStationFlag;
    }

    public void setWorkStationFlag(String workStationFlag) {
        this.workStationFlag = workStationFlag;
    }

    public String getFaultUnClosedSr() {
        return faultUnClosedSr;
    }

    public void setFaultUnClosedSr(String faultUnClosedSr) {
        this.faultUnClosedSr = faultUnClosedSr;
    }

    public String getCatModelNonGe() {
        return catModelNonGe;
    }

    public void setCatModelNonGe(String catModelNonGe) {
        this.catModelNonGe = catModelNonGe;
    }

    public String getTakeFlag() {
        return takeFlag;
    }

    public void setTakeFlag(String takeFlag) {
        this.takeFlag = takeFlag;
    }

    public String getUnTakedReason() {
        return unTakedReason;
    }

    public void setUnTakedReason(String unTakedReason) {
        this.unTakedReason = unTakedReason;
    }

    public String getUnRepairReason() {
        return unRepairReason;
    }

    public void setUnRepairReason(String unRepairReason) {
        this.unRepairReason = unRepairReason;
    }

    public String getRepairFlag() {
        return repairFlag;
    }

    public void setRepairFlag(String repairFlag) {
        this.repairFlag = repairFlag;
    }

    public String getNotGeAsset() {
        return notGeAsset;
    }

    public void setNotGeAsset(String notGeAsset) {
        this.notGeAsset = notGeAsset;
    }

    public String getPartsHighType() {
        return partsHighType;
    }

    public void setPartsHighType(String partsHighType) {
        this.partsHighType = partsHighType;
    }

    public String getRadiationQuotaType() {
        return radiationQuotaType;
    }

    public void setRadiationQuotaType(String radiationQuotaType) {
        this.radiationQuotaType = radiationQuotaType;
    }

    public String getProductRemark() {
        return productRemark;
    }

    public void setProductRemark(String productRemark) {
        this.productRemark = productRemark;
    }

    public Date getProductAuditDate() {
        return productAuditDate;
    }

    public void setProductAuditDate(Date productAuditDate) {
        this.productAuditDate = productAuditDate;
    }

    public String getProductAuditer() {
        return productAuditer;
    }

    public void setProductAuditer(String productAuditer) {
        this.productAuditer = productAuditer;
    }

    public String getProductAuditerUid() {
        return productAuditerUid;
    }

    public void setProductAuditerUid(String productAuditerUid) {
        this.productAuditerUid = productAuditerUid;
    }

    public String getCcsbApplyType() {
        return ccsbApplyType;
    }

    public void setCcsbApplyType(String ccsbApplyType) {
        this.ccsbApplyType = ccsbApplyType;
    }

    public String getRegionalManagerName() {
        return regionalManagerName;
    }

    public void setRegionalManagerName(String regionalManagerName) {
        this.regionalManagerName = regionalManagerName;
    }

    public String getRegionalManagerEmail() {
        return regionalManagerEmail;
    }

    public void setRegionalManagerEmail(String regionalManagerEmail) {
        this.regionalManagerEmail = regionalManagerEmail;
    }

    public String getRegionalManagerPhone() {
        return regionalManagerPhone;
    }

    public void setRegionalManagerPhone(String regionalManagerPhone) {
        this.regionalManagerPhone = regionalManagerPhone;
    }

    public String getSupplementFlag() {
        return supplementFlag;
    }

    public void setSupplementFlag(String supplementFlag) {
        this.supplementFlag = supplementFlag;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getMaintainFirstFlag() {
        return maintainFirstFlag;
    }

    public void setMaintainFirstFlag(String maintainFirstFlag) {
        this.maintainFirstFlag = maintainFirstFlag;
    }

    public String getMaintainFlag() {
        return maintainFlag;
    }

    public void setMaintainFlag(String maintainFlag) {
        this.maintainFlag = maintainFlag;
    }

    public String getSsaKeyFlag() {
        return ssaKeyFlag;
    }

    public void setSsaKeyFlag(String ssaKeyFlag) {
        this.ssaKeyFlag = ssaKeyFlag;
    }

    public String getModifyFlag() {
        return modifyFlag;
    }

    public void setModifyFlag(String modifyFlag) {
        this.modifyFlag = modifyFlag;
    }

    public String getContractCatId() {
        return contractCatId;
    }

    public void setContractCatId(String contractCatId) {
        this.contractCatId = contractCatId;
    }

    public String getContractSnNo() {
        return contractSnNo;
    }

    public void setContractSnNo(String contractSnNo) {
        this.contractSnNo = contractSnNo;
    }

    public String getHighCatFlag() {
        return highCatFlag;
    }

    public void setHighCatFlag(String highCatFlag) {
        this.highCatFlag = highCatFlag;
    }

    public String getProbeQuotaType() {
        return probeQuotaType;
    }

    public void setProbeQuotaType(String probeQuotaType) {
        this.probeQuotaType = probeQuotaType;
    }

    public String getScoreFlag() {
        return scoreFlag;
    }

    public Integer getDataFlag() {
        return dataFlag;
    }

    public void setDataFlag(Integer dataFlag) {
        this.dataFlag = dataFlag;
    }

    public void setScoreFlag(String scoreFlag) {
        this.scoreFlag = scoreFlag;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getUploadLetterFileId() {
        return uploadLetterFileId;
    }

    public void setUploadLetterFileId(String uploadLetterFileId) {
        this.uploadLetterFileId = uploadLetterFileId;
    }

    public String getCheckApplyNumFlag() {
        return checkApplyNumFlag;
    }

    public void setCheckApplyNumFlag(String checkApplyNumFlag) {
        this.checkApplyNumFlag = checkApplyNumFlag;
    }

    public String getUploadLetterFileName() {
        return uploadLetterFileName;
    }

    public void setUploadLetterFileName(String uploadLetterFileName) {
        this.uploadLetterFileName = uploadLetterFileName;
    }

    public String getNeedLetterFlag() {
        return needLetterFlag;
    }

    public void setNeedLetterFlag(String needLetterFlag) {
        this.needLetterFlag = needLetterFlag;
    }

    public String getUploadLetterFlag() {
        return uploadLetterFlag;
    }

    public void setUploadLetterFlag(String uploadLetterFlag) {
        this.uploadLetterFlag = uploadLetterFlag;
    }

    public String getHaveTrainCourseFlag() {
        return haveTrainCourseFlag;
    }

    public void setHaveTrainCourseFlag(String haveTrainCourseFlag) {
        this.haveTrainCourseFlag = haveTrainCourseFlag;
    }

    public String getPartSerialNumber() {
        return partSerialNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPartSerialNumber(String partSerialNumber) {
        this.partSerialNumber = partSerialNumber;
    }

    public String getPartQualityIssue() {
        return partQualityIssue;
    }

    public void setPartQualityIssue(String partQualityIssue) {
        this.partQualityIssue = partQualityIssue;
    }

    public String getTechAuditerUid() {
        return techAuditerUid;
    }

    public void setTechAuditerUid(String techAuditerUid) {
        this.techAuditerUid = techAuditerUid;
    }

    public String getManageAuditerUid() {
        return manageAuditerUid;
    }

    public void setManageAuditerUid(String manageAuditerUid) {
        this.manageAuditerUid = manageAuditerUid;
    }

    public String getDirectorRemark() {
        return directorRemark;
    }

    public void setDirectorRemark(String directorRemark) {
        this.directorRemark = directorRemark;
    }

    public Date getDirectorAuditDate() {
        return directorAuditDate;
    }

    public void setDirectorAuditDate(Date directorAuditDate) {
        this.directorAuditDate = directorAuditDate;
    }

    public String getDirectorAuditer() {
        return directorAuditer;
    }

    public void setDirectorAuditer(String directorAuditer) {
        this.directorAuditer = directorAuditer;
    }

    public String getDirectorAuditerUid() {
        return directorAuditerUid;
    }

    public void setDirectorAuditerUid(String directorAuditerUid) {
        this.directorAuditerUid = directorAuditerUid;
    }

    public String getUnOrderAuditerUid() {
        return unOrderAuditerUid;
    }

    public void setUnOrderAuditerUid(String unOrderAuditerUid) {
        this.unOrderAuditerUid = unOrderAuditerUid;
    }

    public String getOrderedAuditerUid() {
        return orderedAuditerUid;
    }

    public void setOrderedAuditerUid(String orderedAuditerUid) {
        this.orderedAuditerUid = orderedAuditerUid;
    }

    public String getReturnAuditerUid() {
        return returnAuditerUid;
    }

    public void setReturnAuditerUid(String returnAuditerUid) {
        this.returnAuditerUid = returnAuditerUid;
    }

    public String getClosedAuditerUid() {
        return closedAuditerUid;
    }

    public void setClosedAuditerUid(String closedAuditerUid) {
        this.closedAuditerUid = closedAuditerUid;
    }

    public String getNewCatFlag() {
        return newCatFlag;
    }

    public void setNewCatFlag(String newCatFlag) {
        this.newCatFlag = newCatFlag;
    }

    public String getTechAuditer() {
        return techAuditer;
    }

    public void setTechAuditer(String techAuditer) {
        this.techAuditer = techAuditer;
    }

    public String getManageAuditer() {
        return manageAuditer;
    }

    public void setManageAuditer(String manageAuditer) {
        this.manageAuditer = manageAuditer;
    }

    public String getUnOrderRemark() {
        return unOrderRemark;
    }

    public void setUnOrderRemark(String unOrderRemark) {
        this.unOrderRemark = unOrderRemark;
    }

    public Date getUnOrderDate() {
        return unOrderDate;
    }

    public void setUnOrderDate(Date unOrderDate) {
        this.unOrderDate = unOrderDate;
    }

    public String getUnOrderAuditer() {
        return unOrderAuditer;
    }

    public void setUnOrderAuditer(String unOrderAuditer) {
        this.unOrderAuditer = unOrderAuditer;
    }

    public String getOrderedAuditer() {
        return orderedAuditer;
    }

    public void setOrderedAuditer(String orderedAuditer) {
        this.orderedAuditer = orderedAuditer;
    }

    public String getReturnAuditer() {
        return returnAuditer;
    }

    public void setReturnAuditer(String returnAuditer) {
        this.returnAuditer = returnAuditer;
    }

    public String getClosedAuditer() {
        return closedAuditer;
    }

    public void setClosedAuditer(String closedAuditer) {
        this.closedAuditer = closedAuditer;
    }

    public Date getTechAuditDate() {
        return techAuditDate;
    }

    public void setTechAuditDate(Date techAuditDate) {
        this.techAuditDate = techAuditDate;
    }

    public Date getManageAuditDate() {
        return manageAuditDate;
    }

    public void setManageAuditDate(Date manageAuditDate) {
        this.manageAuditDate = manageAuditDate;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getReturnAuditDate() {
        return returnAuditDate;
    }

    public void setReturnAuditDate(Date returnAuditDate) {
        this.returnAuditDate = returnAuditDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getWhetherDeduct() {
        return whetherDeduct;
    }

    public void setWhetherDeduct(String whetherDeduct) {
        this.whetherDeduct = whetherDeduct;
    }

    public String getAssetTypeDi() {
        return assetTypeDi;
    }

    public void setAssetTypeDi(String assetTypeDi) {
        this.assetTypeDi = assetTypeDi;
    }

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getOldCatSn() {
        return oldCatSn;
    }

    public void setOldCatSn(String oldCatSn) {
        this.oldCatSn = oldCatSn;
    }

    public String getNewCatSn() {
        return newCatSn;
    }

    public void setNewCatSn(String newCatSn) {
        this.newCatSn = newCatSn;
    }

    public Integer getCatNum() {
        return catNum;
    }

    public void setCatNum(Integer catNum) {
        this.catNum = catNum;
    }

    public Integer getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(Integer returnQty) {
        this.returnQty = returnQty;
    }

    public String getArtificialType() {
        return artificialType;
    }

    public void setArtificialType(String artificialType) {
        this.artificialType = artificialType;
    }

    public Integer getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(Integer remainQty) {
        this.remainQty = remainQty;
    }

    public ZkbPartOrderCat getZkbPartOrderCat() {
        return zkbPartOrderCat;
    }

    public void setZkbPartOrderCat(ZkbPartOrderCat zkbPartOrderCat) {
        this.zkbPartOrderCat = zkbPartOrderCat;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Set<ZkbPartOrderStep> getZkbPartOrderStepSet() {
        return zkbPartOrderStepSet;
    }

    public void setZkbPartOrderStepSet(Set<ZkbPartOrderStep> zkbPartOrderStepSet) {
        this.zkbPartOrderStepSet = zkbPartOrderStepSet;
    }

    public String getOnlineEngineer() {
        return onlineEngineer;
    }

    public void setOnlineEngineer(String onlineEngineer) {
        this.onlineEngineer = onlineEngineer;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
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

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getTechRemark() {
        return techRemark;
    }

    public void setTechRemark(String techRemark) {
        this.techRemark = techRemark;
    }

    public String getManageRemark() {
        return manageRemark;
    }

    public void setManageRemark(String manageRemark) {
        this.manageRemark = manageRemark;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderedRemark() {
        return orderedRemark;
    }

    public void setOrderedRemark(String orderedRemark) {
        this.orderedRemark = orderedRemark;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnRemark() {
        return returnRemark;
    }

    public void setReturnRemark(String returnRemark) {
        this.returnRemark = returnRemark;
    }

    public String getClosedRemark() {
        return closedRemark;
    }

    public void setClosedRemark(String closedRemark) {
        this.closedRemark = closedRemark;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<ZkbPartOrderItem> getZkbPartOrderItems() {
        return zkbPartOrderItems;
    }

    public void setZkbPartOrderItems(List<ZkbPartOrderItem> zkbPartOrderItems) {
        this.zkbPartOrderItems = zkbPartOrderItems;
    }

    public String getPartOrderNo() {
        return partOrderNo;
    }

    public void setPartOrderNo(String partOrderNo) {
        this.partOrderNo = partOrderNo;
    }

    public Integer getPartOrderIndex() {
        return partOrderIndex;
    }

    public void setPartOrderIndex(Integer partOrderIndex) {
        this.partOrderIndex = partOrderIndex;
    }

    public Date getPartOrderDate() {
        return partOrderDate;
    }

    public void setPartOrderDate(Date partOrderDate) {
        this.partOrderDate = partOrderDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getOrderAnnotation() {
        return orderAnnotation;
    }

    public void setOrderAnnotation(String orderAnnotation) {
        this.orderAnnotation = orderAnnotation;
    }

    public String getUrgentOrder() {
        return urgentOrder;
    }

    public void setUrgentOrder(String urgentOrder) {
        this.urgentOrder = urgentOrder;
    }

    public String getUrgencyReasons() {
        return urgencyReasons;
    }

    public void setUrgencyReasons(String urgencyReasons) {
        this.urgencyReasons = urgencyReasons;
    }

    public String getRequestPhone() {
        return requestPhone;
    }

    public void setRequestPhone(String requestPhone) {
        this.requestPhone = requestPhone;
    }

    public String getHospitalRepairer() {
        return hospitalRepairer;
    }

    public void setHospitalRepairer(String hospitalRepairer) {
        this.hospitalRepairer = hospitalRepairer;
    }

    public String getHospitalRepairerPhone() {
        return hospitalRepairerPhone;
    }

    public void setHospitalRepairerPhone(String hospitalRepairerPhone) {
        this.hospitalRepairerPhone = hospitalRepairerPhone;
    }

    public String getProblemOrderNumber() {
        return problemOrderNumber;
    }

    public void setProblemOrderNumber(String problemOrderNumber) {
        this.problemOrderNumber = problemOrderNumber;
    }

    public String getGeEngineerName() {
        return geEngineerName;
    }

    public void setGeEngineerName(String geEngineerName) {
        this.geEngineerName = geEngineerName;
    }

    public String getGeEngineerPhone() {
        return geEngineerPhone;
    }

    public void setGeEngineerPhone(String geEngineerPhone) {
        this.geEngineerPhone = geEngineerPhone;
    }

    public String getZkbEngineerName() {
        return zkbEngineerName;
    }

    public void setZkbEngineerName(String zkbEngineerName) {
        this.zkbEngineerName = zkbEngineerName;
    }

    public String getZkbEngineerPhone() {
        return zkbEngineerPhone;
    }

    public void setZkbEngineerPhone(String zkbEngineerPhone) {
        this.zkbEngineerPhone = zkbEngineerPhone;
    }

    public ZkbPartOrderUserConfig getOnlineEngine() {
        return onlineEngine;
    }

    public void setOnlineEngine(ZkbPartOrderUserConfig onlineEngine) {
        this.onlineEngine = onlineEngine;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getTenantNewCatFlag() {
        return tenantNewCatFlag;
    }

    public void setTenantNewCatFlag(String tenantNewCatFlag) {
        this.tenantNewCatFlag = tenantNewCatFlag;
    }

    public String getCommercialRemark() {
        return commercialRemark;
    }

    public void setCommercialRemark(String commercialRemark) {
        this.commercialRemark = commercialRemark;
    }

    public Date getCommercialAuditDate() {
        return commercialAuditDate;
    }

    public void setCommercialAuditDate(Date commercialAuditDate) {
        this.commercialAuditDate = commercialAuditDate;
    }

    public String getCommercialAuditer() {
        return commercialAuditer;
    }

    public void setCommercialAuditer(String commercialAuditer) {
        this.commercialAuditer = commercialAuditer;
    }

    public String getCommercialAuditerUid() {
        return commercialAuditerUid;
    }

    public void setCommercialAuditerUid(String commercialAuditerUid) {
        this.commercialAuditerUid = commercialAuditerUid;
    }

    public String getTseRemark() {
        return tseRemark;
    }

    public void setTseRemark(String tseRemark) {
        this.tseRemark = tseRemark;
    }

    public Date getTseAuditDate() {
        return tseAuditDate;
    }

    public void setTseAuditDate(Date tseAuditDate) {
        this.tseAuditDate = tseAuditDate;
    }

    public String getTseAuditer() {
        return tseAuditer;
    }

    public void setTseAuditer(String tseAuditer) {
        this.tseAuditer = tseAuditer;
    }

    public String getTseAuditerUid() {
        return tseAuditerUid;
    }

    public void setTseAuditerUid(String tseAuditerUid) {
        this.tseAuditerUid = tseAuditerUid;
    }

    public String getOsscRemark() {
        return osscRemark;
    }

    public void setOsscRemark(String osscRemark) {
        this.osscRemark = osscRemark;
    }

    public Date getOsscAuditDate() {
        return osscAuditDate;
    }

    public void setOsscAuditDate(Date osscAuditDate) {
        this.osscAuditDate = osscAuditDate;
    }

    public String getOsscAuditer() {
        return osscAuditer;
    }

    public void setOsscAuditer(String osscAuditer) {
        this.osscAuditer = osscAuditer;
    }

    public String getOsscAuditerUid() {
        return osscAuditerUid;
    }

    public void setOsscAuditerUid(String osscAuditerUid) {
        this.osscAuditerUid = osscAuditerUid;
    }

    public String getOsscPartNo() {
        return osscPartNo;
    }

    public void setOsscPartNo(String osscPartNo) {
        this.osscPartNo = osscPartNo;
    }

    public Date getOsscReturnAuditDate() {
        return osscReturnAuditDate;
    }

    public void setOsscReturnAuditDate(Date osscReturnAuditDate) {
        this.osscReturnAuditDate = osscReturnAuditDate;
    }

    public String getOsscReturnAuditer() {
        return osscReturnAuditer;
    }

    public void setOsscReturnAuditer(String osscReturnAuditer) {
        this.osscReturnAuditer = osscReturnAuditer;
    }

    public String getOsscReturnAuditerUid() {
        return osscReturnAuditerUid;
    }

    public void setOsscReturnAuditerUid(String osscReturnAuditerUid) {
        this.osscReturnAuditerUid = osscReturnAuditerUid;
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
        if (!(object instanceof ZkbPartOrder)) {
            return false;
        }
        ZkbPartOrder other = (ZkbPartOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbPartOrder[ id=" + id + " ]";
    }

}
