package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "zkb_integral_order")
public class ZkbIntegralOrder implements Serializable {


    private static final long serialVersionUID = 1L;

    public enum OrderTypeEnum {
        EngineerOnSiteRepair,EngineerOnSiteMaintenance,ApplicationSupport,ToolBox
    }

    public enum IntegralOrderStatusEnum{
        Requested,ManageAccept,ManageReject,Ordered,OrderedReject,EngineerQualificationAccept,EngineerQualificationReject,
        CommercialAccept, CommercialReject
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    @Size(max = 32)
    private String tenantUID;

    @Column(name = "tenant_name")
    private String tenantName;

    @Column(name = "integral_order_no")
    private String integralOrderNo;

    /**
     * 合同id
     */
    @Column(name = "contract_id")
    private String contractId;
    @Column(name = "contract_no")
    private String contractNo;

    /**
     * 工单ID
     */
    @Column(name = "work_order_id")
    private String workOrderId;

    @Column(name = "asset_id")
    private String assetId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "asset_id", referencedColumnName = "uid")
    private AssetInfo assetInfo;

    @Column(name = "sr_num")
    private String srNum;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private String orderType;

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

    @Column(name = "engineer_no")
    private String engineerNo;
    /**
     * 产品型号
     */
    @Column(name = "product_number")
    private String productNumber;

    @Column(name = "maintain_flag")
    private String maintainFlag;

    /**
     * 故障描述
     */
    @Column(name = "fault_description")
    private String faultDescription;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_uid")
    private String createdUid;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

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

    @Column(name = "region")
    private String region;

    @Column(name = "zone")
    private String zone;

    @Column(name = "artificial_type")
    private String artificialType;

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

    @Column(name = "cat_id")
    private String catId;

    @Column(name = "sr_no")
    private String srNo;

    @Column(name = "activity_no")
    private String activityNo;

    @Column(name = "ge_engineer_name")
    private String geEngineerName;

    @Column(name = "ge_engineer_phone")
    private String geEngineerPhone;

    @Column(name = "whether_deduct")
    private String whetherDeduct;

    @Column(name = "order_no")
    private String orderNo;

    //t  f
    @Column(name = "engineer_qualifications_flag")
    private String engineerQualificationsFlag;

    @Column(name = "engineer_remark")
    private String engineerRemark;

    @Column(name = "engineer_audit_date")
    private Date engineerAuditDate;

    @Column(name = "engineer_auditer")
    private String engineerAuditer;
    @Column(name = "engineer_auditer_uid")
    private String engineerAuditerUid;


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
     * 订货备注
     */
    @Column(name = "ordered_remark")
    private String orderedRemark;
    @Column(name = "ordered_date")
    private Date orderedDate;
    @Column(name = "ordered_auditer")
    private String orderedAuditer;
    @Column(name = "ordered_auditer_uid")
    private String orderedAuditerUid;

    /**
     * 是否上过培训课
     */
    @Column(name = "have_train_course_flag")
    private String haveTrainCourseFlag;

    @Transient
    private String regionalManagerName;//区域经理姓名
    @Transient
    private String regionalManagerEmail;//区域经理邮箱
    @Transient
    private String regionalManagerPhone;//区域经理电话

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbIntegralOrder)) {
            return false;
        }
        ZkbIntegralOrder other = (ZkbIntegralOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
