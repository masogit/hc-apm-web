package com.ge.apm.domain.budget;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.ApprovableTarget;
import com.ge.apm.service.constant.ApprovalConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 维保合同申购
 */
@Entity
@Data
@Table(name = "maintenance_contract_apply_purchase")
public class MaintenanceContractApplyPurchase implements ApprovableTarget, Serializable {

    public enum ApplicationStepEnum {
        SubmitPending,ApprovalPending, MeetingReasoningPending,PurchaseConfirmationPending,Complete
    }

    public enum StatusEnum {
        TempSave,Requested,ApprovalAccept,ApprovalReject, MeetingReasoningAccept, MeetingReasoningReject, PurchaseConfirmationAccept, PurchaseConfirmationReject
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    @Getter
    @Setter
    private String id;

    /**
     * 申购单号
     */
    @Column(name = "application_num")
    private String applicationNum;

    /**
     * 状态
     */
    @Column(name = "application_status")
    private String applicationStatus;

    @Column(name = "application_step")
    private String applicationStep;

    // 申请日期
    @Column(name = "application_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationDate;

    @Column(name = "applicant_name")
    private String applicantName;

    @Column(name = "applicant_id")
    private Integer applicantId;

    // 申请科室id
    @Column(name = "applicant_dept_id")
    private Integer applicationDeptId;

    // 院区
    @Column(name = "courtyard_area")
    private String courtyardArea;

    // 院区id
    @Column(name = "courtyard_area_id")
    private Integer courtyardAreaId;
    // 病区代码
    @Column(name = "ward_code")
    private String wardCode;

    @Column(name = "remark")
    private String remark;

    @Column(name = "equipment_description")
    private String equipmentDescription;

    // 审批流id
    @Column(name = "approval_config_id")
    private String approvalConfigId;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "org_uid", columnDefinition = "CHAR(32)")
    private String orgUid;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_by_uid")
    private String createdByUid;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // 删除标识 0: 未删除；1：已删除
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedDate;

    @Column(name = "application_dept_name")
    private String applicationDeptName;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "applicant_dept_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private OrgInfo applicationDept;

    @Column(name = "tech_analysis_discussion")
    private String techAnalysisDiscussion;
    @Column(name = "tech_comment")
    private String techComment;
    @Column(name = "tech_auditer")
    private String techAuditer;
    @Column(name = "tech_audit_date")
    private Date techAuditDate;

    @Column(name = "meet_audit_comment")
    private String meetAuditComment;
    @Column(name = "meet_audit_date")
    private Date meetAuditDate;
    @Column(name = "meet_reasoning_result")
    private String meetReasoningResult;
    @Column(name = "record_person_name")
    private String recordPersonName;
    @Column(name = "meet_audit_remark")
    private String meetAuditRemark;
    @Column(name = "purchase_confirmer")
    private String purchaseConfirmer;
    @Column(name = "purchase_confirm_date")
    private Date purchaseConfirmDate;
    @Column(name = "purchase_confirm_comment")
    private String purchaseConfirmComment;

    @Override
    public BigDecimal getTotalPriceToBigDecimal() {
        return null;
    }

    @Override
    public String getBusinessNumber() {
        return applicationNum;
    }

    @Override
    public String getBusinessUid() {
        return id;
    }

    @Override
    public int getBusinessType() {
        return ApprovalConstant.BUSINESS_TYPE_MAINTENANCE_CONTRACT_APPLY_PURCHASE;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaintenanceContractApplyPurchase)) {
            return false;
        }
        MaintenanceContractApplyPurchase other = (MaintenanceContractApplyPurchase) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
