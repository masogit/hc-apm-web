package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 调剂协议表单数据实体
 * 存储协议编辑页面的表单数据（JSON格式），按医院+协议类型唯一存储，支持数据复用
 */
@Entity
@Table(name = "adjustment_agreement_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hospital_uid", "agreement_type"})
})
public class AdjustmentAgreementData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 协议类型：调剂缔结协议 */
    public static final String TYPE_ADJUSTMENT = "ADJUSTMENT";
    /** 协议类型：调剂退回协议 */
    public static final String TYPE_TERMINATION = "TERMINATION";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /** 所属医院UID（用于模板复用，同一医院共享数据） */
    @Column(name = "hospital_uid", nullable = false)
    private String hospitalUid;

    /** 协议类型: ADJUSTMENT=调剂协议, TERMINATION=退回协议 */
    @Column(name = "agreement_type", nullable = false)
    private String agreementType;

    /** 借出医院UID */
    @Column(name = "lending_hospital_uid")
    private String lendingHospitalUid;

    /** 借入医院UID */
    @Column(name = "borrowing_hospital_uid")
    private String borrowingHospitalUid;

    /** JSON格式，存储所有表单字段数据 */
    @Column(name = "template_data", columnDefinition = "TEXT")
    private String templateData;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    // ========== Getters & Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospitalUid() {
        return hospitalUid;
    }

    public void setHospitalUid(String hospitalUid) {
        this.hospitalUid = hospitalUid;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getLendingHospitalUid() {
        return lendingHospitalUid;
    }

    public void setLendingHospitalUid(String lendingHospitalUid) {
        this.lendingHospitalUid = lendingHospitalUid;
    }

    public String getBorrowingHospitalUid() {
        return borrowingHospitalUid;
    }

    public void setBorrowingHospitalUid(String borrowingHospitalUid) {
        this.borrowingHospitalUid = borrowingHospitalUid;
    }

    public String getTemplateData() {
        return templateData;
    }

    public void setTemplateData(String templateData) {
        this.templateData = templateData;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
