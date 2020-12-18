package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2020/12/3
 * @description:人工申请信息
 */
@Entity
@Table(name = "zkb_fe_part_order")
public class ZkbFePartOrder {

    /**
     * Requested:已申请
     * TenantAccept:租户审批通过
     * TenantReject:租户批拒绝
     * FourHundredsAccept:400回填
     */
    public enum workFlowStatus {
        Requested,TenantAccept,TenantReject,FourHundredsAccept
    }

    /**
     * ManMade:人工申请
     */
    public enum OrderType {
        ManMade
    }

    /**
     * WaitingAssignment:待租户审批
     * WaitingApm:待400团队回填APM
     * Complete:完成
     */
    public enum workFlowSteps {
        WaitingAssignment,WaitingApm,Complete
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 额度申请唯一编号（系统自动生成）
     */
    @Column(name = "part_order_no")
    private String partOrderNo;

    /**
     * 唯一编号自增索引
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_order_index")
    private Integer partOrderIndex;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private String orderType;

    /**
     * 当前步骤
     */
    @Column(name = "current_step")
    private String currentStep;

    /**
     * 当前状态
     */
    @Column(name = "status")
    private String status;

    /**
     *SystemID/厂商报修ID
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 当前位置
     */
    @Column(name = "location")
    private String location;

    /**
     * 报修时间
     */
    @Column(name = "report_date")
    private Date reportDate;

    /**
     * 现象
     */
    @Column(name = "phenomenon")
    private String phenomenon;

    /**
     * 机器状态
     */
    @Column(name = "machine_status")
    private String machineStatus;

    /**
     * 医院职务
     */
    @Column(name = "position")
    private String position;

    /**
     * 紧急状态
     */
    @Column(name = "urgency_status")
    private String urgencyStatus = "1";

    /**
     * 医院名称
     */
    @Column(name = "hospital_name")
    private String hospitalName;

    /**
     * 设备类型
     */
    @Column(name = "function_type")
    private String functionType;

    /**
     * 报修人
     */
    @Column(name = "report_user")
    private String reportUser;

    /**
     * 医院客户姓名
     */
    @Column(name = "hospital_client_name")
    private String hospitalClientName;

    /**
     * 医院客户电话
     */
    @Column(name = "hospital_client_phone")
    private String hospitalClientPhone;

    /**
     * 是否扣除名额
     */
    @Column(name = "subtract_total_status")
    private String subtractTotalStatus;

    /**
     * 供应商审批备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 合同ID
     */
    @Column(name = "contract_id")
    private String contractId;

    /**
     * 派工信息SR#
     */
    @Column(name = "sr")
    private String sr;

    /**
     * 派工信息Act#
     */
    @Column(name = "act")
    private String act;

    /**
     * GE工程师姓名
     */
    @Column(name = "ge_engineer_name")
    private String geEngineerName;

    /**
     * GE工程师电话
     */
    @Column(name = "ge_engineer_phone")
    private String geEngineerPhone;

    /**
     * 派工信息是否扣除名额
     */
    @Column(name = "subtract_total_statusApm")
    private String subtractTotalStatusApm;

    /**
     * 派工备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 租户UID
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartOrderNo() {
        return partOrderNo;
    }

    public void setPartOrderNo(String partOrderNo) {
        this.partOrderNo = partOrderNo;
    }

    public Integer getPartOrderIndex() {
        return partOrderIndex;
    }

    public void setPartOrderIndex(Integer partOrderIndex) {
        this.partOrderIndex = partOrderIndex;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public String getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrgencyStatus() {
        return urgencyStatus;
    }

    public void setUrgencyStatus(String urgencyStatus) {
        this.urgencyStatus = urgencyStatus;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getReportUser() {
        return reportUser;
    }

    public void setReportUser(String reportUser) {
        this.reportUser = reportUser;
    }

    public String getHospitalClientName() {
        return hospitalClientName;
    }

    public void setHospitalClientName(String hospitalClientName) {
        this.hospitalClientName = hospitalClientName;
    }

    public String getHospitalClientPhone() {
        return hospitalClientPhone;
    }

    public void setHospitalClientPhone(String hospitalClientPhone) {
        this.hospitalClientPhone = hospitalClientPhone;
    }

    public String getSubtractTotalStatus() {
        return subtractTotalStatus;
    }

    public void setSubtractTotalStatus(String subtractTotalStatus) {
        this.subtractTotalStatus = subtractTotalStatus;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getGeEngineerPhone() {
        return geEngineerPhone;
    }

    public void setGeEngineerPhone(String geEngineerPhone) {
        this.geEngineerPhone = geEngineerPhone;
    }

    public String getSubtractTotalStatusApm() {
        return subtractTotalStatusApm;
    }

    public void setSubtractTotalStatusApm(String subtractTotalStatusApm) {
        this.subtractTotalStatusApm = subtractTotalStatusApm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getGeEngineerName() {
        return geEngineerName;
    }

    public void setGeEngineerName(String geEngineerName) {
        this.geEngineerName = geEngineerName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        if (!(object instanceof ZkbFePartOrder)) {
            return false;
        }
        ZkbFePartOrder other = (ZkbFePartOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbFePartOrder[ id=" + id + " ]";
    }
}
