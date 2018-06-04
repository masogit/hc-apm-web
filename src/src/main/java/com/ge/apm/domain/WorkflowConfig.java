package com.ge.apm.domain;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "workflow_config")
public class WorkflowConfig implements Serializable {

    public enum AssetCodeConfigType { DFQ , DF} 
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "site_id")
    private Integer siteId;
    @Basic(optional = false)
    @Column(name = "hospital_id")
    private Integer hospitalId;
    @Basic(optional = false)
    @Column(name = "dispatch_mode")
    private Integer dispatchMode;
    @Column(name = "dispatch_user_id")
    private Integer dispatchUserId;
    @Column(name = "dispatch_user_name")
    private String dispatchUserName;
    @Column(name = "timeout_dispatch")
    private Integer timeoutDispatch;
    @Column(name = "timeout_accept")
    private Integer timeoutAccept;
    @Column(name = "timeout_repair")
    private Integer timeoutRepair;
    @Column(name = "timeout_close")
    private Integer timeoutClose;
    
    @Column(name="order_reopen_timeframe")
    private Integer orderReopenTimeframe;//二次开单的最大时间间隔
    
    @Column(name="max_message_count")
    private Integer maxMessageCount;//推送消息的次数上限
    
    @Column(name = "dispatch_user_id2")
    private Integer dispatchUserId2;
    
    @Column(name = "dispatch_user_name2")
    private String dispatchUserName2;
     
    @Column(name = "is_take_order_enabled")
    private Boolean isTakeOrderEnabled; //是否允许抢单
    
    @Column(name = "is_manual_enabled")
    private Boolean isManualEnabled; //是否人工派工
    
    @Column(name = "is_auto_enabled")
    private Boolean isAutoEnabled; //是否自动派工

    @Column(name = "is_temp_user_report")
    private Boolean isTempUserReport;
    
    @Column(name = "auto_ack")
    private Boolean autoAck;//是否需要验收
    
    @Column(name = "auto_ack_days")
    private Integer autoAckDays;//系统自动验收时间,单位‘天’

    @Column(name = "auto_close")
    private Boolean autoClose;// 自动关单
    
    @Column(name="tenant_uid",columnDefinition = "CHAR(32)")
    private String tenantUID;
    
    @Column(name="institution_uid",columnDefinition = "CHAR(32)")
    private String institutionUID;
    
    @Column(name="hospital_uid",columnDefinition = "CHAR(32)")
    private String hospitalUID;
    
    @Column(name="site_uid",columnDefinition = "CHAR(32)")
    private String siteUID;
    
    //扫码建档审核人UID, null 表示不需要审核
    @Column(name="asset_approver" ,columnDefinition = "CHAR(32)")
    private String assetApprover;
    
    //设备编码组合  [null:不需要, DFQ:资产编号,设备编号,二维码 ,DF:资产编号,设备编号]
    @Column(name="asset_code_config")
    private String assetCodeConfig;
    
    @Column(name = "warranty_remind_days")
    private Integer warrantyRemindDays;//维保提前通知时间，单位：天

    
    @Column(name = "warranty_remind_times")
    private Integer warrantyRemindTimes;//维保提醒次数

    @Column(name = "dept_for_shared_asset",columnDefinition = "CHAR(32)")
    private String deptForSharedAsset;

    @Column(name = "wo_manhour_price")
    private Double woManhourPrice;

    /****for pm *****/
    @Column(name = "pm_manhour_price")
    private Double pmManhourPrice;

    @Column(name="pm_simplify")
    private Boolean pmSimplify; // 简化保养

    @Column(name = "is_pm_take_order_enabled")
    private Boolean isPmTakeOrderEnabled; //是否允许抢单

    @Column(name = "is_pm_manual_enabled")
    private Boolean isPmManualEnabled; //是否人工派工

    @Column(name = "is_pm_auto_enabled")
    private Boolean isPmAutoEnabled; //是否自动派工

    @Column(name = "pm_dispatch_user_id")
    private Integer pmDispatchUserId;

    @Column(name = "pm_dispatch_user_name")
    private String pmDispatchUserName;

    @Column(name = "pm_dispatch_user_id2")
    private Integer pmDispatchUserId2;

    @Column(name = "pm_dispatch_user_name2")
    private String pmDispatchUserName2;

    @Column(name = "pm_reminder_time")
    private Integer pmReminderTime;//计划开始时间之前提醒时间，单位'天'

    @Column(name = "pm_reminder_times")
    private Integer pmReminderTimes;//提醒次数
    
    @Column(name = "need_instorage_check")
    private Boolean needInstorageCheck;//到货安装是否需要核对入库 null=false

    /***
     * 资产编码配置
     */

    @Column(name = "generate_method")
    private Integer generateMethod;//资产编码生成方式 1、国标码+设备数 2、

    @Column(name="format")
    private String format;//初始流水号

    @Column(name = "asset_seria_num_level")
    private Integer assetSeriaNumLevel;//资产编码level 租户：4 集团：3，医院：2 院区：1

    private String prefix;//国标码前缀

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /***
     * 紧急工单配置
     */

    @Column(name = "emergency_timeout_dispatch")
    private Integer emergencyTimeoutDispatch;

    @Column(name = "emergency_timeout_accept")
    private Integer emergencyTimeoutAccept;

    @Column(name = "emergency_timeout_close")
    private Integer emergencyTimeoutClose;

    @Column(name="emergency_max_message_count")
    private Integer emergencyMaxMessageCount;//推送消息的次数上限

    public Integer getEmergencyTimeoutDispatch() {
        return emergencyTimeoutDispatch;
    }

    public void setEmergencyTimeoutDispatch(Integer emergencyTimeoutDispatch) {
        this.emergencyTimeoutDispatch = emergencyTimeoutDispatch;
    }

    public Integer getEmergencyTimeoutAccept() {
        return emergencyTimeoutAccept;
    }

    public void setEmergencyTimeoutAccept(Integer emergencyTimeoutAccept) {
        this.emergencyTimeoutAccept = emergencyTimeoutAccept;
    }

    public Integer getEmergencyTimeoutClose() {
        return emergencyTimeoutClose;
    }

    public void setEmergencyTimeoutClose(Integer emergencyTimeoutClose) {
        this.emergencyTimeoutClose = emergencyTimeoutClose;
    }

    public Integer getEmergencyMaxMessageCount() {
        return emergencyMaxMessageCount;
    }

    public void setEmergencyMaxMessageCount(Integer emergencyMaxMessageCount) {
        this.emergencyMaxMessageCount = emergencyMaxMessageCount;
    }

    public Integer getAssetSeriaNumLevel() {
        return assetSeriaNumLevel;
    }

    public void setAssetSeriaNumLevel(Integer assetSeriaNumLevel) {
        this.assetSeriaNumLevel = assetSeriaNumLevel;
    }

    public Integer getGenerateMethod() {
        return generateMethod;
    }

    public void setGenerateMethod(Integer generateMethod) {
        this.generateMethod = generateMethod;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getPmReminderTimes() {
        return pmReminderTimes;
    }

    public void setPmReminderTimes(Integer pmReminderTimes) {
        this.pmReminderTimes = pmReminderTimes;
    }

    public Integer getPmDispatchUserId() {
        return pmDispatchUserId;
    }

    public void setPmDispatchUserId(Integer pmDispatchUserId) {
        this.pmDispatchUserId = pmDispatchUserId;
    }

    public String getPmDispatchUserName() {
        return pmDispatchUserName;
    }

    public void setPmDispatchUserName(String pmDispatchUserName) {
        this.pmDispatchUserName = pmDispatchUserName;
    }

    public Integer getPmDispatchUserId2() {
        return pmDispatchUserId2;
    }

    public void setPmDispatchUserId2(Integer pmDispatchUserId2) {
        this.pmDispatchUserId2 = pmDispatchUserId2;
    }

    public String getPmDispatchUserName2() {
        return pmDispatchUserName2;
    }

    public void setPmDispatchUserName2(String pmDispatchUserName2) {
        this.pmDispatchUserName2 = pmDispatchUserName2;
    }

    public Boolean getIsPmTakeOrderEnabled() {
        return isPmTakeOrderEnabled;
    }

    public void setIsPmTakeOrderEnabled(Boolean isPmTakeOrderEnabled) {
        this.isPmTakeOrderEnabled = isPmTakeOrderEnabled;
    }

    public Boolean getIsPmManualEnabled() {
        return isPmManualEnabled;
    }

    public void setIsPmManualEnabled(Boolean isPmManualEnabled) {
        this.isPmManualEnabled = isPmManualEnabled;
    }

    public Boolean getIsPmAutoEnabled() {
        return isPmAutoEnabled;
    }

    public void setIsPmAutoEnabled(Boolean isPmAutoEnabled) {
        this.isPmAutoEnabled = isPmAutoEnabled;
    }

    public Integer getAutoAckDays() {
		return autoAckDays;
	}

	public void setAutoAckDays(Integer autoAckDays) {
		this.autoAckDays = autoAckDays;
	}

	public Integer getWarrantyRemindTimes() {
		return warrantyRemindTimes;
	}

	public void setWarrantyRemindTimes(Integer warrantyRemindTimes) {
		this.warrantyRemindTimes = warrantyRemindTimes;
	}

	public Integer getWarrantyRemindDays() {
		return warrantyRemindDays;
	}

	public void setWarrantyRemindDays(Integer warrantyRemindDays) {
		this.warrantyRemindDays = warrantyRemindDays;
	}

	public Integer getPmReminderTime() {
		return pmReminderTime;
	}

	public void setPmReminderTime(Integer pmReminderTime) {
		this.pmReminderTime = pmReminderTime;
	}

	public String getTenantUID() {
		return tenantUID;
	}

	public void setTenantUID(String tenantUID) {
		this.tenantUID = tenantUID;
	}

	public String getInstitutionUID() {
		return institutionUID;
	}

	public void setInstitutionUID(String institutionUID) {
		this.institutionUID = institutionUID;
	}

	public String getHospitalUID() {
		return hospitalUID;
	}

	public void setHospitalUID(String hospitalUID) {
		this.hospitalUID = hospitalUID;
	}

	public String getSiteUID() {
		return siteUID;
	}

	public void setSiteUID(String siteUID) {
		this.siteUID = siteUID;
	}

	public Boolean getIsManualEnabled() {
		return isManualEnabled;
	}

	public void setIsManualEnabled(Boolean isManualEnabled) {
		this.isManualEnabled = isManualEnabled;
	}

	public Boolean getIsAutoEnabled() {
		return isAutoEnabled;
	}

	public void setIsAutoEnabled(Boolean isAutoEnabled) {
		this.isAutoEnabled = isAutoEnabled;
	}

	public Boolean getAutoAck() {
		return autoAck;
	}

	public void setAutoAck(Boolean autoAck) {
		this.autoAck = autoAck;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDispatchMode(Integer dispatchMode) {
		this.dispatchMode = dispatchMode;
	}

	public Boolean getIsTakeOrderEnabled() {
		return isTakeOrderEnabled;
	}

	public void setIsTakeOrderEnabled(Boolean isTakeOrderEnabled) {
		this.isTakeOrderEnabled = isTakeOrderEnabled;
	}

	public Integer getOrderReopenTimeframe() {
		return orderReopenTimeframe;
	}

	public void setOrderReopenTimeframe(Integer orderReopenTimeframe) {
		this.orderReopenTimeframe = orderReopenTimeframe;
	}

	public Integer getMaxMessageCount() {
		return maxMessageCount;
	}

	public void setMaxMessageCount(Integer maxMessageCount) {
		this.maxMessageCount = maxMessageCount;
	}

	public Integer getDispatchUserId2() {
		return dispatchUserId2;
	}

	public void setDispatchUserId2(Integer dispatchUserId2) {
		this.dispatchUserId2 = dispatchUserId2;
	}

	public String getDispatchUserName2() {
		return dispatchUserName2;
	}

	public void setDispatchUserName2(String dispatchUserName2) {
		this.dispatchUserName2 = dispatchUserName2;
	}

	public WorkflowConfig() {
    }

    public WorkflowConfig(Integer id) {
        this.id = id;
    }

    public WorkflowConfig(Integer id, int siteId, int hospitalId, int dispatchMode) {
        this.id = id;
        this.siteId = siteId;
        this.hospitalId = hospitalId;
        this.dispatchMode = dispatchMode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Integer getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(int dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public Integer getDispatchUserId() {
        return dispatchUserId;
    }

    public void setDispatchUserId(Integer dispatchUserId) {
        this.dispatchUserId = dispatchUserId;
    }

    public String getDispatchUserName() {
        return dispatchUserName;
    }

    public void setDispatchUserName(String dispatchUserName) {
        this.dispatchUserName = dispatchUserName;
    }

    public Integer getTimeoutDispatch() {
        return timeoutDispatch;
    }

    public void setTimeoutDispatch(Integer timeoutDispatch) {
        this.timeoutDispatch = timeoutDispatch;
    }

    public Integer getTimeoutAccept() {
        return timeoutAccept;
    }

    public void setTimeoutAccept(Integer timeoutAccept) {
        this.timeoutAccept = timeoutAccept;
    }

    public Integer getTimeoutRepair() {
        return timeoutRepair;
    }

    public void setTimeoutRepair(Integer timeoutRepair) {
        this.timeoutRepair = timeoutRepair;
    }

    public Integer getTimeoutClose() {
        return timeoutClose;
    }

    public void setTimeoutClose(Integer timeoutClose) {
        this.timeoutClose = timeoutClose;
    }

    public Boolean getIsTempUserReport() {
        return isTempUserReport;
    }

    public void setIsTempUserReport(Boolean tempUserReport) {
        isTempUserReport = tempUserReport;
    }

    public String getAssetApprover() {
        return assetApprover;
    }

    public void setAssetApprover(String assetApprover) {
        this.assetApprover = assetApprover;
    }

    public Boolean getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Boolean autoClose) {
        this.autoClose = autoClose;
    }

    public Boolean getPmSimplify() {
        return pmSimplify;
    }

    public void setPmSimplify(Boolean pmSimplify) {
        this.pmSimplify = pmSimplify;
    }

    public String getDeptForSharedAsset() {
        return deptForSharedAsset;
    }

    public void setDeptForSharedAsset(String deptForSharedAsset) {
        this.deptForSharedAsset = deptForSharedAsset;
    }

    public Boolean getNeedInstorageCheck() {
        return needInstorageCheck;
    }

    public void setNeedInstorageCheck(Boolean needInstorageCheck) {
        this.needInstorageCheck = needInstorageCheck;
    }

    public String getAssetCodeConfig() {
        return assetCodeConfig;
    }

    public void setAssetCodeConfig(String assetCodeConfig) {
        this.assetCodeConfig = assetCodeConfig;
    }

    public Double getWoManhourPrice() {
        return woManhourPrice;
    }

    public void setWoManhourPrice(Double woManhourPrice) {
        this.woManhourPrice = woManhourPrice;
    }

    public Double getPmManhourPrice() {
        return pmManhourPrice;
    }

    public void setPmManhourPrice(Double pmManhourPrice) {
        this.pmManhourPrice = pmManhourPrice;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkflowConfig)) {
            return false;
        }
        WorkflowConfig other = (WorkflowConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.WorkflowConfig[ id=" + id + " ]";
    }
    
}
