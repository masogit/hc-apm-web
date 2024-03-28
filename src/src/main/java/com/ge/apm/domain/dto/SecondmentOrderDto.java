package com.ge.apm.domain.dto;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
public class SecondmentOrderDto implements Serializable {

    private String id;
    private String assetName;
    private String functionType;
    private Integer assetGroup;
    private String departNum;
    private String qrCode;
    private String secondmentNo;
    private Integer num;
    private String assetUid;

    private String configId;

    private Integer status; // -2已拒绝 -1已取消  0 待同意 ，1 待交付， 2待归还， 3已归还

    private Boolean needSpareParts;

    private String sparePartsRemark;

    private String applicantUid; //借用科室id（借用申请人uid）

    @Temporal(TemporalType.TIMESTAMP)
    private Date applyTime;

    private Integer billingType;

    private Double unitPrice;//单价

    private Double cost;//费用

    private Long billingTime;

    private String billingStandards;  // 计费标准  （60/小时）

    @Temporal(TemporalType.TIMESTAMP)
    private Date billingBeginTime;//计费开始时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date billingEndTime;//计费结束时间

    private String borrowDeptUid;

    private String lendDeptUid;

    private String configSnapshot;

    private String remark;

    private String createdBy;  // 创建人uid

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    private String  modifyBy;// 修改人uid

    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyDate;

    private String tenantUID;

    private String institutionUID;

    private String hospitalUID;

    private String siteUID;

    private String denialReason;

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SecondmentOrderDto)) {
            return false;
        }
        SecondmentOrderDto other = (SecondmentOrderDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


}
