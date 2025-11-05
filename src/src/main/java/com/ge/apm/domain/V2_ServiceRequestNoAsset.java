package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "v2_service_request_no_asset")
public class V2_ServiceRequestNoAsset implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //工单优先级
    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private Integer casePriority;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "dept_uid")
    private String deptUid;

    @Column(name = "dept_name")
    private String deptName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "requestor_id")
    private Integer requestorId;
    //报修人
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "requestor_name")
    private String requestorName;

    //报修人电话
    @Column(name = "requestor_phone")
    private String requestorPhone;

    //报修时间
    @Basic(optional = false)
    @NotNull
    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date requestTime;

    //故障现象
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;

    @Column(name = "request_reason_voice")
    private Integer requestReasonVoice;

    //工单状态
    @Column(name = "status")
    private Integer status;

    //1: 2：待派工 3：待接单  4：待关单 5：已关单  9：已取消
    @Column(name = "current_step_id")
    private Integer currentStepId;

    @Column(name = "current_step_name")
    private String currentStepName;

    @Column(name = "current_person_id")
    private Integer currentPersonId;

    @Column(name = "current_person_name")
    private String currentPersonName;

    /**
     * 1: auto dispatch 2: grab and manual 3: grab 4: manual
     */
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;
    
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "site_name")
    private String siteName;

    @Column(name = "sr_num")
    private String srNum;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_user_uid")
    private String createdUserUid;

    @Column(name = "created_user_name")
    private String createdUserName;

    @Column(name = "repair_type")
    private Integer repairType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "estimated_close_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedCloseTime;

    @Column(name = "close_time")
    private Date closeTime;


    //派工信息
    @Column(name="send_step_owner_Id")
    private Integer sendStepOwnerId;
    @Column(name="send_step_owner_name")
    private String sendStepOwnerName;
    @Column(name="send_step_start_time")
    private Date sendStepStartTime;

    //接单信息
    @Column(name="accept_step_owner_Id")
    private Integer acceptStepOwnerId;
    @Column(name="accept_step_owner_name")
    private String acceptStepOwnerName;
    @Column(name="accept_step_start_time")
    private Date acceptStepStartTime;

    @Column(name = "confirmed_down_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date confirmedDownTime;

    @Column(name = "asset_status")
    private Integer assetStatus;

    @Column(name = "sr_id")
    private String srId;

    //取消人
    @Column(name = "cancel_user_uid")
    private String cancelUserUid;

    //取消人
    @Column(name = "cancel_user_name")
    private String cancelUserName;
    //取消原因
    @Column(name = "cancel_reason")
    private String cancelReason;

    //维修人
    @Column(name = "repair_name")
    private String repairName;

    @Column(name = "wo_id")
    private String woId;

    @Column(name="sr_repair_source")
    private String srRepairSource;

    @Column(name = "voice_to_Text", columnDefinition = "text")
    private String voiceToText;

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_ServiceRequestNoAsset)) {
            return false;
        }
        V2_ServiceRequestNoAsset other = (V2_ServiceRequestNoAsset) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
