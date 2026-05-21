package com.ge.apm.domain;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "v2_work_order")
@TableName("v2_work_order")
/*@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)*/
public class V2_WorkOrder implements Serializable, ApprovableTarget {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "sr_id")
    private String srId;

    @JoinColumn(name = "sr_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private V2_ServiceRequest serviceRequest;
    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @Column(name="asset_uid",columnDefinition = "CHAR(32)")
    private String assetUid;

    @JoinColumn(name = "asset_id", referencedColumnName = "id", insertable = false, updatable = false,foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE) // 没有关联实体时忽略
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;



    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false,foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.LAZY)
    private Set<OrderSnatchingPerson> orderSnatchingPerson;
    @Basic(optional = false)
    @NotNull
    @Column(name = "current_person_id")
    private Integer currentPersonId;

    @Size(max = 32)
    @Column(name = "current_person_name")
    private String currentPersonName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "current_step_id")
    private Integer currentStepId;

    @Column(name = "case_type")
    private Integer caseType;


    @Column(name = "sub_status")
    private Integer subStatus;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "current_step_name")
    private String currentStepName;

    @Column(name = "total_man_hour")
    private Double totalManHour;

    @Column(name = "total_price")
    private Double totalPrice;

    //1：流程中；2：已完成；3：已取消 (生命周期状态 :具体流程需查看步骤表)
    @Column(name = "status")
    private Integer status;

    @Column(name = "int_ext_type")
    private Integer intExtType;

    @Column(name = "parent_wo_id")
    private String parentWoId;

    @Column(name = "main_sub")
    private String mainSub;

    @Column(name = "feedback_rating")
    private Integer feedbackRating;

    @Column(name = "feedback_comment")
    private String feedbackComment;

    @Column(name = "pat_problems")
    private String patProblems;

    @Column(name = "pat_actions")
    private String patActions;

    @Column(name = "pat_tests")
    private String patTests;

    @Column(name = "diagnostic_process")
    private String  diagnosticProcess;

    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "status_start_time")
    private Date statusStartTime;
    @Column(name = "status_close_time")
    private Date statusCloseTime;

    @Column(name = "checkin")
    private Integer checkin;

    @Column(name="ack_name")
    private String ackName;

    @Column(name="ack_id")
    private Integer ackId;

    //	    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=V2_BlobObject.class) 
//	    @Where(clause="bo_uuid=id")
//	    private List<V2_BlobObject> attachments ;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private List<V2_WorkOrder_Step> v2_workOrder_step_list;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrder_Detail> v2_workOrder_detail_list;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Fetch(FetchMode.SUBSELECT)
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<V2_WorkOrderCoworker> v2_workOrder_coworker_list;

    /**
     * 填写工时选择方式 0 输入填写 , 1 多个工时段填写
     */
    @Column(name="coworker_entry_type")
    private Integer coworkerEntryType;

    @Column(name = "comment")
    private String comment;

    @Column(name = "repair_type")
    private Integer repairType;

    @Column(name = "repair_result", length = 2)
    private String repairResult;

    @Column(name = "checkin_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date checkinTime;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "other_expense")
    private Double otherExpense;

    @Column(name = "man_hours")
    private Double manHours;

    /**
     * 补充维修工单 approvalStatus 字段含义
     * 0 初始化工单(新建工单审核状态), 1 待审核, 2 审核同意, 3 审核不同意, 4 审批部分同意
     */
    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "external_expense")
    private Double externalExpense;

    /**
     * 1: auto dispatch 2: grab and manual 3: grab 4: manual
     */
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;

    @Column(name = "approver_id")
    private Integer approverId;
    @Column(name = "approver_name")
    private String approverName;

    @Column(name="attributes")
    private String attributes;
    @Column(name = "signature")
    private String signature;

    @Column(name = "ack_signature")
    private String ackSignature;
    @Column(name="approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date approvalDate;
    //维修价值
    @Column(name = "maintenance_value")
    private Double maintenanceValue;

    //外部报价
    @Column(name = "external_quotation")
    private Double externalQuotation;
    
    //MVS工单类型
    @Column(name="mvs_type")
    private Integer mvsType;
    
//    @JoinColumn(name = "currentPersonId", referencedColumnName = "id", insertable = false, updatable = false)
//    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.DETACH)
//    @JsonIgnore
//    @org.hibernate.annotations.ForeignKey(name = "none")
//    @NotFound(action= NotFoundAction.IGNORE)
    @Transient
    private UserAccount FEOwner;

    //派工信息
    @Column(name="send_step_owner_Id")
    private Integer sendStepOwnerId;
    @Column(name="send_step_owner_name")
    private String sendStepOwnerName;
    @Column(name="send_step_start_time")
    private Date sendStepStartTime;

    //接单信息
    @Column(name="accept_step_owner_Id")
    private Integer acceptStepOwnerId;
    @Column(name="accept_step_owner_name")
    private String acceptStepOwnerName;
    @Column(name="accept_step_start_time")
    private Date acceptStepStartTime;

    //维修完成时间
    @Column(name="work_step_end_time")
    private Date workStepEndTime;

    //验收信息
    @Column(name="evaluate_step_owner_Id")
    private Integer evaluateStepOwnerId;
    @Column(name="evaluate_step_owner_name")
    private String evaluateStepOwnerName;
    @Column(name="evaluate_Step_End_Time")
    private Date evaluateStepEndTime;

    //关单信息
    @Column(name="close_step_owner_Id")
    private Integer closeStepOwnerId;
    @Column(name="close_step_owner_name")
    private String closeStepOwnerName;
    @Column(name="close_Step_End_Time")
    private Date closeStepEndTime;

    @Column(name = "import_by")
    private String importBy; //  工单导入人

    /**
     * 供应商派工时  供应商uid
     */
    @Column(name = "supplier_uid")
    private String supplierUid;
    /**
     * 核心组是否审核通过 0 未审批 1 医工审批 2 核心审批
     */
    @Column(name = "is_core_member_approval")
    private Integer isCoreMemberApproval;
    /**
     * 工单创建类型  1:医院创建   2:供应商创建
     */
    @Column(name = "create_type",columnDefinition=" integer default 1 ")
    private Integer createType = 1;

    @Transient
    private Integer orderCount;
    @Transient
    private BigDecimal totalPriceToBigDecimal;
    @Column(name="sieble_number") // 县域需求字段sieble单号
    private String siebleNumber;

    @Column(name="call_center_number") // 县域需求call center单号
    private String callCenterNumber;

    /**县域 开单类型： 1主动开单、2被动开单**/
    @Column(name="order_type")
    private String orderType;

    /**
     * 填写工时选择方式 0 输入填写 , 1 多个工时段填写
     */
    @Column(name="entry_type")
    private Integer entryType;

    /**
     * 工单分类
     */
    @Column(name="work_order_type")
    private String workOrderType;

    @Column(name="travel_time")
    private Double travelTime;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "wo_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @Fetch(FetchMode.SUBSELECT)
    private List<V2_WorkOrderManHoursDetail> workHoursList;
    @Transient
    private String partApproveSignature;

    @Transient
    private String repairName;

    @Transient
    private Integer repairPersonId;
    @Transient
    private ExternalTempUser externalTempUser;

    public Integer getCoworkerEntryType() {
        return coworkerEntryType;
    }

    public void setCoworkerEntryType(Integer coworkerEntryType) {
        this.coworkerEntryType = coworkerEntryType;
    }

    public Integer getRepairPersonId() {
        return repairPersonId;
    }

    public void setRepairPersonId(Integer repairPersonId) {
        this.repairPersonId = repairPersonId;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName;
    }

    public Integer getIsCoreMemberApproval() {
        return isCoreMemberApproval;
    }

    public void setIsCoreMemberApproval(Integer isCoreMemberApproval) {
        this.isCoreMemberApproval = isCoreMemberApproval;
    }
    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return totalPriceToBigDecimal;
    }

    public void setTotalPriceToBigDecimal(BigDecimal totalPriceToBigDecimal) {
        this.totalPriceToBigDecimal = totalPriceToBigDecimal;
    }

    @Override
    public String getBusinessNumber() {
        return getServiceRequest().getSrNum();
    }

    @Override
    public String getBusinessUid() {
        return getId();
    }

    @Override
    public int getBusinessType() {
        return  ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT;
    }
    @Override
    public String getAssetType() {
        if (null != assetInfo) {
            return assetInfo.getAssetType();
        }
        return null;
    }

    @Override
    public AssetInfo getApprovalAssetInfo() {
        if (null != assetInfo) {
            return assetInfo;
        }
        return null;
    }
    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(Integer dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
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

    public Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRepairType() {
        return repairType;
    }

    public void setRepairType(Integer repairType) {
        this.repairType = repairType;
    }

    public List<V2_WorkOrder_Step> getV2_workOrder_step_list() {
        return v2_workOrder_step_list;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setV2_workOrder_step_list(List<V2_WorkOrder_Step> v2_workOrder_step_list) {
        this.v2_workOrder_step_list = v2_workOrder_step_list;
    }

    public List<V2_WorkOrderCoworker> getV2_workOrder_coworker_list() {
        return v2_workOrder_coworker_list;
    }

    public void setV2_workOrder_coworker_list(List<V2_WorkOrderCoworker> v2_workOrder_coworker_list) {
        this.v2_workOrder_coworker_list = v2_workOrder_coworker_list;
    }

    public List<V2_WorkOrder_Detail> getV2_workOrder_detail_list() {
        return v2_workOrder_detail_list;
    }

    public void setV2_workOrder_detail_list(List<V2_WorkOrder_Detail> v2_workOrder_detail_list) {
        this.v2_workOrder_detail_list = v2_workOrder_detail_list;
    }

    public String getImportBy() { return importBy; }

    public void setImportBy(String importBy) { this.importBy = importBy; }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public Integer getCurrentPersonId() {
        return currentPersonId;
    }

    public void setCurrentPersonId(Integer currentPersonId) {
        this.currentPersonId = currentPersonId;
    }

    public String getCurrentPersonName() {
        return currentPersonName;
    }

    public void setCurrentPersonName(String currentPersonName) {
        this.currentPersonName = currentPersonName;
    }

    public Integer getCurrentStepId() {
        return currentStepId;
    }

    public void setCurrentStepId(Integer currentStepId) {
        this.currentStepId = currentStepId;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public Double getTotalManHour() {
        return totalManHour;
    }

    public void setTotalManHour(Double totalManHour) {
        this.totalManHour = totalManHour;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIntExtType() {
        return intExtType;
    }

    public void setIntExtType(Integer intExtType) {
        this.intExtType = intExtType;
    }

    public String getParentWoId() {
        return parentWoId;
    }

    public void setParentWoId(String parentWoId) {
        this.parentWoId = parentWoId;
    }

    public String getMainSub() {
        return mainSub;
    }

    public void setMainSub(String mainSub) {
        this.mainSub = mainSub;
    }

    public Integer getFeedbackRating() {
        return feedbackRating;
    }

    public void setFeedbackRating(Integer feedbackRating) {
        this.feedbackRating = feedbackRating;
    }

    public String getFeedbackComment() {
        return feedbackComment;
    }

    public void setFeedbackComment(String feedbackComment) {
        this.feedbackComment = feedbackComment;
    }

    public String getPatProblems() {
        return patProblems;
    }

    public void setPatProblems(String patProblems) {
        this.patProblems = patProblems;
    }

    public String getPatActions() {
        return patActions;
    }

    public void setPatActions(String patActions) {
        this.patActions = patActions;
    }

    public String getPatTests() {
        return patTests;
    }

    public void setPatTests(String patTests) {
        this.patTests = patTests;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getCaseType() {
        return caseType;
    }

    public void setCaseType(Integer caseType) {
        this.caseType = caseType;
    }

    public Integer getCheckin() {
        return checkin;
    }

    public void setCheckin(Integer checkin) {
        this.checkin = checkin;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Double getOtherExpense() {
        return otherExpense;
    }

    public void setOtherExpense(Double otherExpense) {
        this.otherExpense = otherExpense;
    }

    public Double getManHours() {
        return manHours;
    }

    public void setManHours(Double manHours) {
        this.manHours = manHours;
    }

    public V2_ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(V2_ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Double getExternalExpense() {
        return externalExpense;
    }

    public void setExternalExpense(Double externalExpense) {
        this.externalExpense = externalExpense;
    }

    public String getRepairResult() {
        return repairResult;
    }

    public void setRepairResult(String repairResult) {
        this.repairResult = repairResult;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAckSignature() {
        return ackSignature;
    }

    public void setAckSignature(String ackSignature) {
        this.ackSignature = ackSignature;
    }

    public Double getMaintenanceValue() {
        return maintenanceValue;
    }

    public Double getExternalQuotation() {
        return externalQuotation;
    }

    public void setExternalQuotation(Double externalQuotation) {
        this.externalQuotation = externalQuotation;
    }

    public void setMaintenanceValue(Double maintenanceValue) {
        this.maintenanceValue = maintenanceValue;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

	public Integer getMvsType() {
		return mvsType;
	}

	public void setMvsType(Integer mvsType) {
		this.mvsType = mvsType;
	}

	public UserAccount getFEOwner() {
		return FEOwner;
	}

	public void setFEOwner(UserAccount fEOwner) {
		FEOwner = fEOwner;
	}
    public String getAckName() {
        return ackName;
    }

    public void setAckName(String ackName) {
        this.ackName = ackName;
    }

    public Integer getSendStepOwnerId() {
        return sendStepOwnerId;
    }

    public void setSendStepOwnerId(Integer sendStepOwnerId) {
        this.sendStepOwnerId = sendStepOwnerId;
    }

    public String getSendStepOwnerName() {
        return sendStepOwnerName;
    }

    public void setSendStepOwnerName(String sendStepOwnerName) {
        this.sendStepOwnerName = sendStepOwnerName;
    }

    public Date getSendStepStartTime() {
        return sendStepStartTime;
    }

    public void setSendStepStartTime(Date sendStepStartTime) {
        this.sendStepStartTime = sendStepStartTime;
    }

    public Integer getAcceptStepOwnerId() {
        return acceptStepOwnerId;
    }

    public void setAcceptStepOwnerId(Integer acceptStepOwnerId) {
        this.acceptStepOwnerId = acceptStepOwnerId;
    }

    public String getAcceptStepOwnerName() {
        return acceptStepOwnerName;
    }

    public void setAcceptStepOwnerName(String acceptStepOwnerName) {
        this.acceptStepOwnerName = acceptStepOwnerName;
    }

    public Date getAcceptStepStartTime() {
        return acceptStepStartTime;
    }

    public void setAcceptStepStartTime(Date acceptStepStartTime) {
        this.acceptStepStartTime = acceptStepStartTime;
    }

    public Date getWorkStepEndTime() {
        return workStepEndTime;
    }

    public void setWorkStepEndTime(Date workStepEndTime) {
        this.workStepEndTime = workStepEndTime;
    }

    public Integer getEvaluateStepOwnerId() {
        return evaluateStepOwnerId;
    }

    public void setEvaluateStepOwnerId(Integer evaluateStepOwnerId) {
        this.evaluateStepOwnerId = evaluateStepOwnerId;
    }

    public String getEvaluateStepOwnerName() {
        return evaluateStepOwnerName;
    }

    public void setEvaluateStepOwnerName(String evaluateStepOwnerName) {
        this.evaluateStepOwnerName = evaluateStepOwnerName;
    }

    public Date getEvaluateStepEndTime() {
        return evaluateStepEndTime;
    }

    public void setEvaluateStepEndTime(Date evaluateStepEndTime) {
        this.evaluateStepEndTime = evaluateStepEndTime;
    }

    public Integer getCloseStepOwnerId() {
        return closeStepOwnerId;
    }

    public void setCloseStepOwnerId(Integer closeStepOwnerId) {
        this.closeStepOwnerId = closeStepOwnerId;
    }

    public String getCloseStepOwnerName() {
        return closeStepOwnerName;
    }

    public void setCloseStepOwnerName(String closeStepOwnerName) {
        this.closeStepOwnerName = closeStepOwnerName;
    }

    public Date getCloseStepEndTime() {
        return closeStepEndTime;
    }

    public void setCloseStepEndTime(Date closeStepEndTime) {
        this.closeStepEndTime = closeStepEndTime;
    }


    public Integer getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(Integer subStatus) {
        this.subStatus = subStatus;
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
        if (!(object instanceof V2_WorkOrder)) {
            return false;
        }
        V2_WorkOrder other = (V2_WorkOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public Integer getAckId() {
        return ackId;
    }

    public void setAckId(Integer ackId) {
        this.ackId = ackId;
    }

    public Date getStatusStartTime() {
        return statusStartTime;
    }

    public void setStatusStartTime(Date statusStartTime) {
        this.statusStartTime = statusStartTime;
    }

    public Date getStatusCloseTime() {
        return statusCloseTime;
    }

    public void setStatusCloseTime(Date statusCloseTime) {
        this.statusCloseTime = statusCloseTime;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public String getSiebleNumber() {
        return siebleNumber;
    }

    public void setSiebleNumber(String siebleNumber) {
        this.siebleNumber = siebleNumber;
    }

    public String getCallCenterNumber() {
        return callCenterNumber;
    }

    public void setCallCenterNumber(String callCenterNumber) {
        this.callCenterNumber = callCenterNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPartApproveSignature() {
        return partApproveSignature;
    }

    public void setPartApproveSignature(String partApproveSignature) {
        this.partApproveSignature = partApproveSignature;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public Integer getEntryType() {
        return entryType;
    }

    public void setEntryType(Integer entryType) {
        this.entryType = entryType;
    }

    public List<V2_WorkOrderManHoursDetail> getWorkHoursList() {
        return workHoursList;
    }

    public void setWorkHoursList(List<V2_WorkOrderManHoursDetail> workHoursList) {
        this.workHoursList = workHoursList;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }
    @Transient
    private String businessDigest;
    @Override
    public String getBusinessDigest() {
        if (null != assetInfo) {
            HashMap<String, String> map = new HashMap<>();
            map.put("assetName", assetInfo.getName());
            map.put("clinicalDeptName", assetInfo.getClinicalDeptName());
            return JSON.toJSONString(map);
        }
        return null;
    }


    public void setBusinessDigest(String businessDigest) {
        this.businessDigest = businessDigest;
    }

    public ExternalTempUser getExternalTempUser() {
        return externalTempUser;
    }

    public void setExternalTempUser(ExternalTempUser externalTempUser) {
        this.externalTempUser = externalTempUser;
    }

    public Double getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Double travelTime) {
        this.travelTime = travelTime;
    }

    public String getDiagnosticProcess() {
        return diagnosticProcess;
    }

    public void setDiagnosticProcess(String diagnosticProcess) {
        this.diagnosticProcess = diagnosticProcess;
    }

    public Set<OrderSnatchingPerson> getOrderSnatchingPerson() {
        return orderSnatchingPerson;
    }

    public void setOrderSnatchingPerson(Set<OrderSnatchingPerson> orderSnatchingPerson) {
        this.orderSnatchingPerson = orderSnatchingPerson;
    }
}
