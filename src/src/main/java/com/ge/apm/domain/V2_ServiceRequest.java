package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "v2_service_request")
public class V2_ServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Column(name = "site_id")
    private Integer siteId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "hospital_id")
    private Integer hospitalId;

    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "asset_id")
    @Basic(optional = false)
    @NotNull
    private Integer assetId;

    @Column(name="asset_uid",columnDefinition = "CHAR(32)")
    private String assetUid;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "asset_name")
    private String assetName;

    @Basic(optional = false)
    @NotNull
    @Column(name = "requestor_id")
    private Integer requestorId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 16)
    @Column(name = "requestor_name")
    private String requestorName;

    @Column(name = "requestor_phone")
    private String requestorPhone;

    @Basic(optional = false)
    @NotNull
    @Column(name = "request_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date requestTime;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "request_reason")
    private String requestReason;

    @Column(name = "request_reason_voice")
    private Integer requestReasonVoice;

    @Basic(optional = false)
    @NotNull
    @Column(name = "case_priority")
    private Integer casePriority;

    @Column(name = "confirmed_down_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date confirmedDownTime;

    @Column(name = "confirmed_up_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedUpTime;

    @Column(name = "from_dept_id")
    private Integer fromDeptId;
    
    @Column(name = "from_dept_uid")
    private String fromDeptUID;

    @Column(name = "from_dept_name")
    @Size(max = 64)
    private String fromDeptName;

    @Column(name = "reponse_time")
    private Date responseTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "estimated_close_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedCloseTime;

    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "status")
    private Integer status;

    @Column(name = "equipment_taker")
    private String equipmentTaker;

    @Column(name = "nearest_sr_days")
    private Integer nearestSrDays;

    @Column(name = "nearest_sr_id") 
    private String nearestSrId;//上次报修的id 

    @Column(name = "sr_repair_type")
    private String srRepairType;

    @Column(name="report_source")
    private String reportSource; // 1: web(default) 2: wechat 3:rfid

    @Column(name="asset_location")
    private String assetLocation;


/*    @Column(name = "created_by")
    private String createdBy = "system";

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private ZonedDateTime lastModifiedDate = ZonedDateTime.now();*/



    @Column(name = "take_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takeTime;

//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,targetEntity=V2_BlobObject.class)
//    @JoinColumn(name="bo_uuid",referencedColumnName="id", foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//    @Fetch(FetchMode.SUBSELECT)
    //@Where(clause="bo_uuid=id")
//    private List<V2_BlobObject> attachments;

    @JoinColumn(name="sr_id")
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<V2_WorkOrder> v2_workOrder_List ;

    @Column(name = "cancel_person")
    private String cancelPerson;
    
    @Column(name = "cancel_reason")
    private String cancelReason;
    
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

    @Column(name = "sr_num")
    private String srNum;

    @Column(name = "origin_sr_num")
    private String originSrNum;

    @Column(name="customer_name")
    private String customerName;

    @Column(name="customer_tel")
    private String customerTel;

    @Column(name="customer_position")
    private String customerPosition;

    @Transient
    private String clinicalDeptName;
    @Transient
    private String siebleNumber;
    @Transient
    private String callCenterNumber;
    /**县域 开单类型： 1主动开单、2被动开单**/
    @Transient
    private String orderType;

    /**
     * 报修发送地  病房(ward)/非病房(non-ward)
     */
    @Column(name="repair_report_send_place")
    private String repairReportSendPlace;

    @Column(name="sr_repair_source")
    private String srRepairSource;

    @Column(name="external_work_order")//是否是外部工单
    private String externalWorkOrder;

    @Column(name = "voice_to_text", columnDefinition = "text")
    private String voiceToText;

    @Column(name="ib_status")
    private String ibStatus;

    public String getIbStatus() {
        return ibStatus;
    }

    public void setIbStatus(String ibStatus) {
        this.ibStatus = ibStatus;
    }

    public String getExternalWorkOrder() {
        return externalWorkOrder;
    }

    public void setExternalWorkOrder(String externalWorkOrder) {
        this.externalWorkOrder = externalWorkOrder;
    }

    public String getRepairReportSendPlace() {
        return repairReportSendPlace;
    }

    public void setRepairReportSendPlace(String repairReportSendPlace) {
        this.repairReportSendPlace = repairReportSendPlace;
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

    public List<V2_WorkOrder> getV2_workOrder_List() {
        return v2_workOrder_List;
    }

    public void setV2_workOrder_List(List<V2_WorkOrder> v2_workOrder_List) {
        this.v2_workOrder_List = v2_workOrder_List;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getRequestorId() {
        return requestorId;
    }

    public void setRequestorId(Integer requestorId) {
        this.requestorId = requestorId;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public String getRequestorPhone() {
        return requestorPhone;
    }

    public void setRequestorPhone(String requestorPhone) {
        this.requestorPhone = requestorPhone;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getRequestReasonVoice() {
        return requestReasonVoice;
    }

    public void setRequestReasonVoice(Integer requestReasonVoice) {
        this.requestReasonVoice = requestReasonVoice;
    }

    public Integer getCasePriority() {
        return casePriority;
    }

    public void setCasePriority(Integer casePriority) {
        this.casePriority = casePriority;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public Date getConfirmedUpTime() {
        return confirmedUpTime;
    }

    public void setConfirmedUpTime(Date confirmedUpTime) {
        this.confirmedUpTime = confirmedUpTime;
    }

    public Integer getFromDeptId() {
        return fromDeptId;
    }

    public void setFromDeptId(Integer fromDeptId) {
        this.fromDeptId = fromDeptId;
    }

    public String getFromDeptName() {
        return fromDeptName;
    }

    public void setFromDeptName(String fromDeptName) {
        this.fromDeptName = fromDeptName;
    }

    public Date getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public Date getEstimatedCloseTime() {
        return estimatedCloseTime;
    }

    public void setEstimatedCloseTime(Date estimatedCloseTime) {
        this.estimatedCloseTime = estimatedCloseTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

//    public List<V2_BlobObject> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(List<V2_BlobObject> attachments) {
//        this.attachments = attachments;
//    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
    }

    public Integer getNearestSrDays() {
        return nearestSrDays;
    }

    public void setNearestSrDays(Integer nearestSrDays) {
        this.nearestSrDays = nearestSrDays;
    }

    public String getNearestSrId() {
        return nearestSrId;
    }

    public void setNearestSrId(String nearestSrId) {
        this.nearestSrId = nearestSrId;
    }

    public String getSrNum() { return srNum; }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public String getOriginSrNum() { return originSrNum; }

    public void setOriginSrNum(String originSrNum) { this.originSrNum = originSrNum; }

    public String getFromDeptUID() {
        return fromDeptUID;
    }

/*    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }

    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public ZonedDateTime getLastModifiedDate() { return lastModifiedDate; }

    public void setLastModifiedDate(ZonedDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }*/

    public void setFromDeptUID(String fromDeptUID) {
        this.fromDeptUID = fromDeptUID;
    }

    public String getClinicalDeptName() {
        return clinicalDeptName;
    }

    public void setClinicalDeptName(String clinicalDeptName) {
        this.clinicalDeptName = clinicalDeptName;
    }

    public String getCancelPerson() {
        return cancelPerson;
    }

    public void setCancelPerson(String cancelPerson) {
        this.cancelPerson = cancelPerson;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getSrRepairType() {
        return srRepairType;
    }

    public void setSrRepairType(String srRepairType) { this.srRepairType = srRepairType; }

    public String getReportSource() { return reportSource; }

    public void setReportSource(String reportSource) { this.reportSource = reportSource; }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerTel() {
        return customerTel;
    }

    public void setCustomerTel(String customerTel) {
        this.customerTel = customerTel;
    }

    public String getCustomerPosition() {
        return customerPosition;
    }

    public void setCustomerPosition(String customerPosition) {
        this.customerPosition = customerPosition;
    }

    public String getAssetLocation() {
        return assetLocation;
    }

    public void setAssetLocation(String assetLocation) {
        this.assetLocation = assetLocation;
    }

    public String getSiebleNumber() {
        return siebleNumber;
    }

    public void setSiebleNumber(String siebleNumber) {
        this.siebleNumber = siebleNumber;
    }

    public String getCallCenterNumber() {
        return callCenterNumber;
    }

    public void setCallCenterNumber(String callCenterNumber) {
        this.callCenterNumber = callCenterNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getSrRepairSource() {
        return srRepairSource;
    }

    public void setSrRepairSource(String srRepairSource) {
        this.srRepairSource = srRepairSource;
    }
    public String getVoiceToText() {
        return voiceToText;
    }

    public void setVoiceToText(String voiceToText) {
        this.voiceToText = voiceToText;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof V2_ServiceRequest)) {
            return false;
        }
        V2_ServiceRequest other = (V2_ServiceRequest) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
