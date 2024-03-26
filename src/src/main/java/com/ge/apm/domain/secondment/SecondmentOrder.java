package com.ge.apm.domain.secondment;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  科室借用订单
 */
@Entity
@Table(name = "secondment_order")
@Data
public class SecondmentOrder implements Serializable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "secondment_no")
    private String secondmentNo;

    @Column(name="asset_uid")
    private String assetUid;

    @Column(name="config_id")
    private String configId;

    @Column(name="status")
    private Integer status; // -2已拒绝 -1已取消  0 待同意 ，1 待交付， 2待归还， 3已归还

    @Column(name="need_spare_parts")
    private Boolean needSpareParts;

    @Column(name="spare_parts_remark",columnDefinition = "text")
    private String sparePartsRemark;

    @Column(name="applicant_uid")
    private String applicantUid; //借用科室id（借用申请人uid）

    @Column(name="apply_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyTime;

    @Column(name = "billing_type")
    private Integer billingType;

    @Column(name = "unit_price")
    private Double unitPrice;//单价

    @Column(name = "cost",columnDefinition = "decimal(11,2)")
    private Double cost;//费用

    @Column(name = "billing_time") // 借用时长 （计费结束时间-计费开始时间）
    private Long billingTime;

    @Column(name="billing_standards")
    private String billingStandards;  // 计费标准  （60/小时）

    @Column(name = "billing_begin_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billingBeginTime;//计费开始时间

    @Column(name = "billing_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date billingEndTime;//计费结束时间

    @Column(name = "borrow_dept_uid")//借用科室id（借用申请科室）
    private String borrowDeptUid;

    @Column(name = "lend_dept_uid")//借出科室id （设备所属科室uid）
    private String lendDeptUid;

    @Column(name = "config_snapshot",columnDefinition = "text")//配置快照
    private String configSnapshot;

    @Column(name="remark",columnDefinition = "text")
    private String remark;

    @Column(name = "created_by", columnDefinition = "VARCHAR(32)")
    private String createdBy;  // 创建人uid

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "modify_by", columnDefinition = "VARCHAR(32)")
    private String  modifyBy;// 修改人uid

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_Date")
    private Date modifyDate;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecondmentOrder)) {
            return false;
        }
        SecondmentOrder other = (SecondmentOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
