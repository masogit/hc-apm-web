package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderESHistory {
    // 工单v2_work_order.wo_id
    private String id;
    // 报修单号
    private String srNum;
    // 报修人id
    private Integer requestorId;
    // 报修人名称
    private String requestorName;
    // 报修人电话
    private String requestorPhone;
    //报修时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date requestTime;
    // 关单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closeTime;
    //报修原因
    private String requestReason;
    //报修语音
    private String requestReasonVoice;
    // 报修来源
    private Integer repairType;
    // 设备uid
    private String assetUid;
    // 设备名称
    private String assetName;
    //资产编号
    private String financingNum;
    // 设备qrcode
    private String qrCode;
    // 制造商序列号
    private String serialNum;
    // 设备编号（院方）
    private String departNum;
    // 设备型号
    private String functionType;
    // 所属科室uid
    private String clinicalDeptUID;
    // 所属科室名称
    private String clinicalDeptName;
    // 所属院区
    private String siteUID;
    // 所属院区名称
    private String siteName;
    // 所属 医院
    private String hospitalUID;
    // 所属 医院名称
    private String hospitalName;
    // 所属 集团
    private String institutionUID;
    // 所属 集团名称
    private String institutionName;
    // 所属租户名称
    private String tenantUID;
    // 所属租户名称
    private String tenantName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;


    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkOrderESHistory)) {
            return false;
        }
        WorkOrderESHistory other = (WorkOrderESHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
