package com.ge.apm.domain;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.ge.apm.service.constant.ApprovalConstant.APPROVAL_SEPARATOR;

/**
 *
 * @author 505389829
 */
@Entity
@Table(name = "multi_level_approval")
@Data
public class MultiLevelApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /**
     * 业务单号
     */
    @Column(name = "business_number")
    private String businessNumber;
    /**
     * 审批单号
     */
    @Column(name = "approva_number")
    private String approvalNumber;
    /**
     * 业务UID
     */
    @Column(name = "business_uid")
    private String businessUid;
    /**
     * 业务类型
     */
    @Column(name = "business_type")
    private int businessType;

    @Column(name = "business_type_name")
    private String businessTypeName;

    /**
     * 是否正在审批
     */
    @Column(name = "approval_ing")
    private boolean approvalIng;
    /**
     * 审批类型 1角色审批 2用户审批
     */
    @Column(name = "approval_type")
    private Integer approvalType;
    /**
     * 审批UID
     */
    @Column(name = "approval_uid")
    private String approvalUid;
    /**
     * 审批流状态:0待审批 1审批中 2审批通过 3审批不同意
     */
    @Column(name = "approval_status")
    private Integer approvalStatus;
    /**
     * 审批节点状态0待审批  2审批通过 3审批不同意 4退回上一层节点
     */
    @Column(name = "approval_node_status")
    private Integer approvalNodeStatus;
    /**
     * 审批级别
     */
    @Column(name = "approval_level")
    private Integer approvalLevel;
    /**
     * 当前所在审批级别
     */
    @Column(name = "approval_level_now")
    private Integer approvalLevelNow;
    /**
     * 审批流ID
     */
    @Column(name = "workflow_config_approve_uid")
    private String workflowConfigApproveUid;
    /**
     * 审批意见
     */
    @Column(name = "approval_opinion")
    private String approvalOpinion;

    /**
     * 发起审批UID
     */
    @Column(name = "initiate_approver_uid")
    private String initiateApproverUid;


    /**
     * 多级审批申请人uid
     */
    @Column(name = "application_uid")
    private String applicationUid;


    /**
     * 多级审批申请人uid
     */
    @Column(name = "application_name")
    private String applicationName;
    /**
     * 多级审批申请科室
     */
    @Column(name = "application_department_uid")
    private String  applicationDepartmentUid;

    /**
     * 多级审批申请科室
     */
    @Column(name = "application_department")
    private String  applicationDepartment;



    /**
     * 实际审批人UID
     */
    @Column(name = "actual_approver_uid")
    private String actualApproverUid;
    /**
     * 实际审批人
     */
    @Column(name = "actual_approver_name")
    private String actualApproverName;
    /**
     * 实际审批人时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actual_approver_date")
    private Date actualApproverDate;

    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "VARCHAR(60)")
    private String createdBy;

    /**
     * 组织id
     */
    @Column(name = "hier_org_uid", columnDefinition = "CHAR(32)")
    private String hierOrgUid;

    /**
     * 租户
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    /**
     * 审批签名-同意/不同意时用
     */
    @Column(name = "audit_signature")
    private String auditSignature;

    //护士长是否已签名
    @Transient
    private String signatureNurse;

    /**
     * 摘要
     */
    @Column(name = "digest")
    private String digest;

    @Transient
    private List<V2_BlobObject> attachments;
//    @Transient
//    private String  applicationDepartment ;

    /**
     * 处理人
     */
    @Transient
    private String handlerPerson;

    @Transient
    private String assetName;
    @Transient
    private String makerShortName;
    @Transient
    private String functionType;
    @Transient
    private String applyUserName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private UserAccount applyUser;

    public String getSignatureNurse() {
        return signatureNurse;
    }

    public void setSignatureNurse(String signatureNurse) {
        this.signatureNurse = signatureNurse;
    }

    public boolean getApprovalIng() {
        return approvalIng;
    }

    public void setApprovalIng(boolean approvalIng) {
        this.approvalIng = approvalIng;
    }

    public String getApplicationDepartment() {
        return applicationDepartment;
    }

    public void setApplicationDepartment(String applicationDepartment) {
        this.applicationDepartment = applicationDepartment;
    }

    /**
     * surrounding multiple approval uids with both front end and back end commas;
     * @param approvalUid
     */
    public void setApprovalUid(String approvalUid) {
        if (StringUtils.isEmpty(approvalUid)) {
            return;
        }
        if (approvalUid.startsWith(APPROVAL_SEPARATOR)) {
            return;
        }

        this.approvalUid = APPROVAL_SEPARATOR + approvalUid + APPROVAL_SEPARATOR;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MultiLevelApproval)) {
            return false;
        }
        MultiLevelApproval other = (MultiLevelApproval) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    public UserAccount getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(UserAccount applyUser) {
        this.applyUser = applyUser;
    }

    public String getHandlerPerson() {
        return handlerPerson;
    }

    public void setHandlerPerson(String handlerPerson) {
        this.handlerPerson = handlerPerson;
    }
}
