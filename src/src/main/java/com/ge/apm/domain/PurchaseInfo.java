package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 503331510
 */
@Data
@Entity
@Table(name = "purchase_info")
public class PurchaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    // 申请状态 1: 待审核, 2: 审核中, 3: 审核通过, 4: 审核未通过
    @Column(name = "application_status")
    private Integer applicationStatus;

    @Column(name = "product_name")
    private String productName; //产品名称

    @Column(name = "specification")
    private String specification; //规格

    @Column(name = "unit")
    private String unit;//单位

    @Column(name = "budget_amount")
    private String budget_amount;//预算金额

    @Column(name = "declaration")
    private String declaration;//申报说明

    @Column(name = "funding_source")
    private String fundingSource;//经费来源

    @Column(name = "brand")
    private String brand;//品牌

    @Column(name = "description")
    private String description;//说明

    @Column(name = "purchase_year")
    private String purchaseYear;//年份

    // 科室id
    @Column(name = "applicant_dept_id")
    private Integer applicationDeptId;

    // 科室
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "applicant_dept_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private OrgInfo applicationDept;

    @Column(name = "application_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applicationDate;     // 申请日期

    @Column(name = "applicant_id")
    private Integer applicantId;    // 申请人id

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "applicant_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount applicant;     // 申请人


    @Column(name = "approval_progress")
    private Integer approvalProgress;    // 审批进度

    // 审批日期
    @Column(name = "approval_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalDate;

    // 审批人id
    @Column(name = "approver_id")
    private Integer approverId;

    // 审批人
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "approver_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserAccount approver;

    // 审批流id
    @Column(name = "approval_config_id")
    private String approvalConfigId;

    // 审批流
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "approval_config_id",
            referencedColumnName = "id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action = NotFoundAction.IGNORE)
    private MultiLevelApprovalConfig approvalConfig;

    @Column(name = "asset_name")
    private String assetName;//设备名称

    @Column(name = "function_type")
    private String functionType;//设备型号

    @Column(name = "asset_group")
    private Integer assetGroup;//设备分类

    @Column(name = "quantity")
    private Integer quantity; //数量

    @Column(name = "amount")
    private BigDecimal amount;  //金额

    @Column(name = "approval_status")
    private Integer approvalStatus;//审核状态

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

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

}
