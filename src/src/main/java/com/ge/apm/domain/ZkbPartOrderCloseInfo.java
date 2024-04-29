package com.ge.apm.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2023/10/23
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_close_info")
public class ZkbPartOrderCloseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Getter@Setter
    private String id;

    @Getter@Setter
    @Column(name = "part_order_id")
    private String partOrderId;

    @Getter@Setter
    @Column(name = "part_order_no")
    private String partOrderNo;

    @Getter@Setter
    @Column(name = "sr_no")
    private String srNo;

    /**
     * 设备UDI
     */
    @Getter@Setter
    @Column(name = "udi")
    private String udi;

    /**
     * Safety Concern该故障是否会导致实际/潜在伤害
     */
    @Getter@Setter
    @Column(name = "safety_concern")
    private String safetyConcern;

    /**
     * Patient Use故障发生时是否有病人在使用机器
     */
    @Getter@Setter
    @Column(name = "patient_use")
    private String patientUse;
    /**
     * Event Date事件发生的时间
     */
    @Getter@Setter
    @Column(name = "event_dated")
    private Date eventDated;

    /**
     * Procedure Type过程类型
     */
    @Getter@Setter
    @Column(name = "procedure_type")
    private String procedureType;

    /**
     * 质量联系人
     */
    @Getter@Setter
    @Column(name = "quality_contact_person")
    private String qualityContactPerson;

    /**
     * 质量联系电话
     */
    @Getter@Setter
    @Column(name = "quality_contact_phone")
    private String qualityContactPhone;

    /**
     * Alleged Patient Consequence病人结果
     */
    @Getter@Setter
    @Size(max = 2000)
    @Column(name = "alleged_patient_consequence")
    private String allegedPatientConsequence;

    /**
     * Intervention Required需要的干涉
     */
    @Getter@Setter
    @Size(max = 2000)
    @Column(name = "intervention_required")
    private String interventionRequired;

    /**
     * Additional Details额外的信息
     */
    @Getter@Setter
    @Size(max = 2000)
    @Column(name = "additional_details")
    private String additionalDetails;

    /**
     * Problem服务原因
     */
    @Getter@Setter
    @Size(max = 4000)
    @Column(name = "problem_reason")
    private String problemReason;

    /**
     * 服务内容
     */
    @Getter@Setter
    @Size(max = 4000)
    @Column(name = "action_content")
    private String actionContent;

    /**
     * Test服务结果
     */
    @Getter@Setter
    @Column(name = "test_result")
    @Size(max = 4000)
    private String testResult;


    @Getter
    @Setter
    @Column(name = "complete_date")
    private Date completeDate;

    @Getter@Setter
    @Column(name = "complete_operator")
    private String completeOperator;

    @Getter@Setter
    @Column(name = "complete_operator_uid")
    private String completeOperatorUid;

    @Getter@Setter
    @Column(name = "completeaAudit_remark")
    private String completeAuditRemark;

    @Getter@Setter
    @Column(name = "complete_audit_date")
    private Date completeAuditDate;

    @Getter@Setter
    @Column(name = "complete_auditer")
    private String completeAuditer;

    @Getter@Setter
    @Column(name = "complete_auditer_uid")
    private String completeAuditerUid;


}
