package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "adverse_events")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@Data
@DynamicUpdate
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class AdverseEvents implements Serializable, ApprovableTarget {

    private static final long serialVersionUID = 1L;

    @Transient
    public static String ALL = "1";

    @Transient
    public static String EQUIPMENT = "2";

    @Transient
    public static String CONSUMABLES = "3";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uid", updatable = false)
    private String uid;

    /**
     * 工单编号
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "work_order_num")
    private String workOrderNum;

    /**
     * 报告人姓名
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "reporter_name")
    private String reporterName;

    /**
     * 报告时间
     */
    @Basic(optional = false)
    @NotNull
    @Column(name = "report_date")
    private Date reportDate;

    /**
     * 科室
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "department")
    private String department;

    /**
     * 发生地
     */
    @Column(name = "occurred_place")
    @Size(min = 1, max = 100)
    private String occurredPlace;

    /**
     * 资产名称
     */
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "asset_name")
    private String assetName;

    /**
     * 设备编号
     */
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "device_num")
    private String deviceNum;

    /**
     * 产品名称
     */
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "product_name")
    private String productName;

    /**
     * 注册证编号
     */
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @Column(name = "registration_number")
    private String registrationNum;

    /**
     * 曾用注册证编号
     */
    @Column(name = "previously_registration_num")
    @Size( max = 100)
    private String previouslyRegistrationNum;

    /**
     * 曾用注册证编号是否上报
     */
    @Column(name = "previously_registration_report")
    private Boolean previouslyRegistrationReport;

    /**
     * 型号
     */
    @Column(name = "model")
    @Size(max = 100)
    private String model;

    /**
     * 规格
     */
    @Column(name = "standard")
    @Size(max = 100)
    private String standard;

    /**
     * 产品类别
     */
    @Column(name = "product_category")
    @Size(max = 50)
    private String productCategory;

    /**
     * 产地
     */
    @Column(name = "origin_place")
    private String originPlace;

    /**
     * 产品批号
     */
    @Column(name = "product_lot_num")
    @Size(max = 100)
    private String productLotNum;

    /**
     * 产品编号
     */
    @Column(name = "product_num")
    @Size(max = 100)
    private String productNum;

    /**
     * udi
     */
    @Column(name = "udi")
    private String udi;

    /**
     * 生产日期
     */
    @Column(name = "production_date")
    private Date productionDate;

    /**
     * 有效期至
     */
    @Column(name = "valid_until")
    private Date validUntil;

    /**
     * 停用日期
     */
    @Column(name = "retirement_date")
    private Date retirementDate;

    /**
     * 上市许可证持有人
     */
    @Column(name = "marketing_auth_holder")
    private String marketingAuthHolder;

    /**
     * 操作人
     */
    @Column(name = "operator")
    private Integer operator;

    /**
     * 操作人描述
     */
    @Column(name = "operator_desc")
    @Size(max = 50)
    private String operatorDesc;

    /**
     * 事件发生日期
     */
    @Column(name = "event_occurred_date")
    private Date eventOccurredDate;

    /**
     * 事件发现或获知日期
     */
    @Column(name = "event_discover_date")
    private Date eventDiscoverDate;

    /**
     * 伤害程度 1-死亡 2-严重伤害 3-其他
     */
    @Column(name = "harm_degree")
    private Integer harmDegree;

    /**
     * 伤害程度描述
     */
    @Column(name = "harm_degree_desc")
    @Size(max = 1000)
    private String harmDegreeDesc;

    /**
     * 伤害表现
     */
    @Column(name = "harm_perform")
    @Size(max = 1000)
    private String harmPerform;

    /**
     * 伤害表现附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> harmPerformFile;

    /**
     * 器械故障表现
     */
    @Column(name = "device_fault_perform")
    @Size(max = 1000)
    private String deviceFaultPerform;

    /**
     * 器械故障表现附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> deviceFaultPerformFile;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name")
    @Size(max = 50)
    private String patientName;

    /**
     * 患者性别
     */
    @Column(name = "patient_sex")
    private Integer patientSex;

    /**
     * 患者出生日期
     */
    @Column(name = "patient_born_date")
    private Date patientBornDate;

    /**
     * 患者年龄
     */
    @Column(name = "patient_age")
    private String patientAge;

    /**
     * 患者年龄类型 1-岁 2-月 3-日
     */
    @Column(name = "patient_age_type")
    private Integer patientAgeType;

    /**
     * 患者病历号
     */
    @Column(name = "medical_case_num")
    @Size(max = 100)
    private String medicalCaseNum;

    /**
     * 既往病史
     */
    @Column(name = "medical_history")
    @Size(max = 1000)
    private String medicalHistory;

    /**
     * 预期治疗疾病或者作用
     */
    @Column(name = "except_affect")
    @Size(max = 1000)
    private String exceptAffect;

    /**
     * 器械使用日期
     */
    @Column(name = "device_used_date")
    private Date deviceUsedDate;

    /**
     * 使用场所 1-医疗机构 2-家庭 3-其他
     */
    @Column(name = "use_place")
    private Integer usePlace;

    /**
     * 场所名称
     */
    @Column(name = "place_name")
    @Size(max = 100)
    private String placeName;

    /**
     * 使用过程
     */
    @Column(name = "use_process")
    @Size(max = 1000)
    private String useProcess;

    /**
     * 合并用药/器械情况说明
     */
    @Column(name = "device_desc")
    @Size(max = 1000)
    private String deviceDesc;

    /**
     * 事件原因分析
     */
    @Column(name = "event_cause_analyze")
    @Size(max = 1000)
    private String eventCauseAnalyze;

    /**
     * 事件原因分析描述
     */
    @Column(name = "event_cause_desc")
    @Size(max = 1000)
    private String eventCauseDesc;

    /**
     * 事件原因分析描述附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> eventCauseDescFile;

    /**
     * 初步处置情况
     */
    @Column(name = "init_dispose_situ")
    @Size(max = 1000)
    private String initDisposeSitu;

    /**
     * 初步处置情况附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> initDisposeSituFile;

    /**
     * 状态
     * 1-暂存 2-	待审核 3-已取消 4-已退回 5-已作废（暂时保留）
     * 6-审核通过 7-待上报 8-已上报
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 是否有效
     */
    @Column(name = "is_valid")
    private Boolean isValid;

    /**
     * 修改字段
     */
    @Column(name = "update_col")
    @Size(max = 5000)
    private String updateCol;

    /**
     * 是否开展了调查
     */
    @Column(name = "has_survey")
    private Boolean hasSurvey;

    /**
     * 调查情况
     */
    @Column(name = "survey_situ")
    @Size(max = 1000)
    private String surveySitu;

    /**
     * 调查情况附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> surveySituFile;

    /**
     * 关联性评价
     */
    @Column(name = "relevance_eval")
    @Size(max = 1000)
    private String relevanceEval;

    /**
     * 评价结果-事件原因分析
     */
    @Column(name = "eval_cause_analyze")
    @Size(max = 1000)
    private String evalCauseAnalyze;

    /**
     * 评价结果-事件原因分析附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> evalCauseAnalyzeFile;

    /**
     * 是否已采取控制措施
     */
    @Column(name = "has_control_measure")
    private Boolean hasControlMeasure;

    /**
     * 具体控制措施
     */
    @Column(name = "control_measure_desc")
    @Size(max = 1000)
    private String controlMeasureDesc;

    /**
     * 具体控制措施附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> controlMeasureDescFile;

    /**
     * 未采取控制措施原因
     */
    @Column(name = "not_control_cause")
    @Size(max = 1000)
    private String notControlCause;

    /**
     * 是否错报误报
     */
    @Column(name = "has_false_report")
    private Boolean hasFalseReport;

    /**
     * 错报误报原因
     */
    @Column(name = "false_report_cause")
    @Size(max = 1000)
    private String falseReportCause;

    /**
     * 错报误报原因附件
     */
    @Transient
    @JsonProperty
    private List<AdverseEventsFile> falseReportCauseFile;

    /**
     * 当前审核人uid
     */
    @Column(name = "audit_user_uid")
    private String auditUserUid;

    /**
     * 当前审核人姓名
     */
    /*@Column(name = "audit_user_name")
    @Size(min = 1, max = 50)
    private String auditUserName;*/

    /**
     * 退回原因
     */
    @Column(name = "return_reason")
    @Size(max = 1000)
    private String returnReason;

    /**
     * 审核结果
     */
    @Column(name = "approval_result")
    private Integer approvalResult;

    /**
     * pdf文件的file id
     */
    @Column(name = "pdf_object_id")
    private Integer pdfObjectId;

    /**
     * 是否合并报告
     */
    @Column(name = "report_merge")
    private Boolean reportMerge;

    /**
     * 报告合并编码
     */
    @Column(name = "report_merge_code")
    @Size(max = 100)
    private String reportMergeCode;

    /**
     * 器械分类
     * 1-医疗器械 2-耗材
     * 2024-04-25 add
     */
    @Column(name = "devices_class")
    private Integer devicesClass;


    @Column(name = "created_by", nullable = true, length = 50, updatable = false)
    @JsonIgnore
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    @JsonIgnore
    @CreatedDate
    private Date createdDate;

    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @JsonIgnore
    @LastModifiedDate
    private Date lastModifiedDate = new Date();

    @Column(name="create_user_uid")
    private String createUserUID;

    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;

    /**
     * 审批意见
     */
    @Transient
    @JsonProperty
    private String approvalOpinion;

    /**
     * 审批人
     */
    @Transient
    @JsonProperty
    private String actualApproverName;

    /**
     * 审批时间
     */
    @Transient
    @JsonProperty
    private Date actualApproverDate;


    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return new BigDecimal("0.00");
    }

    @Override
    public String getBusinessNumber() {
        return getWorkOrderNum();
    }

    @Override
    public String getBusinessUid() {
        return getUid();
    }

    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS;
    }

    /**
     * 不良事件工单类型
     * 1.医疗器械不良事件   2.医疗器械不良事件哨点监测
     */
    @Column(name = "adverse_event_type")
    private String adverseEventType;

    /**
     * 报修发生地  病房/非病房
     */
    @Column(name="repair_report_send_place")
    private String repairReportSendPlace;

    /**
     * 故障零部件或结构
     */
    @Column(name = "faulty_parts")
    private String faultyParts;

    /**
     * 潜在风险（可能导致的临床风险）
     */
    @Column(name = "potential_risks")
    @Size(max=2000)
    private String potentialRisks;

    /**
     * 可能导致的伤害程度	1.死亡 2.严重伤害 3.其他
     */
    @Column(name = "may_fal_ht_type")
    private String mayFalHtType;
    /**
     * 是否有维修方案
     * 1.是 2.否
     */
    @Column(name = "has_maintenance_plan")
    private String hasMaintenancePlan;

    /**
     * 维修方案具体内容
     */
    @Column(name = "maintenance_plan_info")
    @Size(max = 2000)
    private String maintenancePlanInfo;

    /**
     * 是否有有效维护保养措施
     * 1.是 2.否
     */
    @Column(name = "has_maintenance_measures")
    private String hasMaintenanceMeasures;

    /**
     * 有效维护保养措施具体内容
     */
    @Column(name = "maintenance_measures_info")
    @Size(max = 2000)
    private String maintenanceMeasuresInfo;


    /**
     * 设备启用日期
     */
    @Column(name = "device_start_date")
    private Date deviceStartDate;

    /**
     * 设备报废（停用）日期
     */
    @Column(name = "device_end_date")
    private Date deviceEndDate;

    /**
     * 维修机构
     * 1.持有人 2.第三方（请输入机构名称） 3.其他（请描述）
     */
    @Column(name = "maintenance_organization")
    private String maintenanceOrganization;

    /**
     * 机构名称或描述
     */
    @Column(name = "maintenance_organization_info")
    private String maintenanceOrganizationInfo;

    /**
     * 故障处理方式
     * 1.硬件维修或更换 2.软件升级  3.其他（请描述）
     */
    @Column(name = "processing_mode")
    private String processingMode;

    /**
     * 故障处理方式描述
     */
    @Column(name = "processing_mode_info")
    @Size(max = 2000)
    private String processingModeInfo;

    /**
     * 使用次数
     */
    @Column(name = "use_cnt")
    private Integer useCnt;

    @Column(name = "wo_id")
    private String woId;

    @Transient
    @TableField(exist = false)
    private UdidRegistryNumInfo registrationInfo;

    public String getUdidProductName() {
        if (Objects.isNull(registrationInfo)) {
            return null;
        }
        return registrationInfo.getProductName();
    }

    public String getUdidRegistrantName() {
        if (Objects.isNull(registrationInfo)) {
            return null;
        }
        return registrationInfo.getRegistrantName();
    }
}
