package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author 503202755 2020/9/1
 */
@Entity
@Table(name = "zkb_order_history")
public class ZkbOrderHistory {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 数据类型
     * 1.DI、PCB
     * 2.US、Biomed
     * 3.LCS
     * 4.US探头
     */
    @Column(name = "data_type")
    private String dataType;

    /**
     * 地区
     */
    @Column(name = "region")
    private String region;

    /**
     * 代理商
     */
    @Column(name = "agent")
    private String agent;

    /**
     * 合同编号
     */
    @Column(name = "contract_no")
    private String contractNo;

    /**
     * 合同类型
     */
    @Column(name = "contract_type")
    private String contractType;

    /**
     * 合同开始时间
     */
    @Column(name = "contract_beginning_time")
    private Date contractBeginningTime;

    /**
     * 合同结束时间
     */
    @Column(name = "contract_ending_time")
    private Date contractEndingTime;

    /**
     * 总IB数量
     */
    @Column(name = "quantity_ib")
    private Integer quantityIb;

    /**
     * 人工名额
     */
    @Column(name = "labour_quantity")
    private Integer labourQuantity;

    /**
     * 配件名额
     */
    @Column(name = "parts_quantity")
    private Integer partsQuantity;

    /**
     * 探头名额
     */
    @Column(name = "probe_quantity")
    private Integer probeQuantity;

    /**
     * 人工,备件，保险联合名额
     */
    @Column(name = "count_quantity")
    private Integer countQuantity;

    /**
     * 发生日期
     */
    @Column(name = "event_date")
    private Date eventDate;

    /**
     * 类别
     */
    @Column(name = "type")
    private String type;

    /**
     * 系统编号
     */
    @Column(name = "system_no")
    private String systemNo;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;


    /**
     * 故障描述
     */
    @Column(name = "fault_description")
    private String faultDescription;

    /**
     * 医院名称
     */
    @Column(name = "hospital_name")
    private String hospitalName;

    /**
     * 机型
     */
    @Column(name = "machine_type")
    private String machineType;

    /**
     * 备件号
     */
    @Column(name = "spare_parts_no")
    private String sparePartsNo;

    /**
     * 备件价格
     */
    @Column(name = "spare_parts_price")
    private Double sparePartsPrice;

    /**
     * 描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * RSESup
     */
    @Column(name = "RSE_sup")
    private String RSESup;

    /**
     *数量
     */
    @Column(name = "quantity")
    private Integer quantity;

    /**
     *sr
     */
    @Column(name = "sr")
    private String sr;

    /**
     *act
     */
    @Column(name = "act")
    private String act;

    /**
     *配件归还
     */
    @Column(name = "spare_parts_return")
    private String sparePartsReturn;

    /**
     *评论
     */
    @Column(name = "comment")
    private String comment;

    /**
     *入保情况
     */
    @Column(name = "insurance_description")
    private String insuranceDescription;

    /**
     * 探头类型
     */
    @Column(name = "probe_type")
    private String probeType;

    /**
     *是否使用名额
     */
    @Column(name = "use_status")
    private String useStatus;

    /**
     *电话号码
     */
    @Column(name = "call_no")
    private String callNo;

    /**
     *Srowner
     */
    @Column(name = "rowner")
    private String rowner;

    /**
     * owner name
     */
    @Column(name = "owner_name")
    private String ownerName;

    /**
     *进展状态
     */
    @Column(name = "status")
    private String status;

    /**
     *tenant_info表里uid
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    /**
     * 合同探头总名额
     */
    @Column(name = "total_contract_probe_count")
    private Integer totalContractProbeCount;

    /**
     * 任意申请限额
     */
    @Column(name = "apply_limit_count")
    private Integer applyLimitCount;

    /**
     * 普通限额
     */
    @Column(name = "normal_limit_count")
    private Integer normalLimitCount;

    /**
     * 特殊限额
     */
    @Column(name = "special_limit_count")
    private Integer specialLimitCount;

    /**
     * 高值限额
     */
    @Column(name = "high_value_limit_count")
    private Integer highValueLimitCount;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbOrderHistory)) {
            return false;
        }
        ZkbOrderHistory other = (ZkbOrderHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.RequireData[ id=" + id + " ]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Date getContractBeginningTime() {
        return contractBeginningTime;
    }

    public void setContractBeginningTime(Date contractBeginningTime) {
        this.contractBeginningTime = contractBeginningTime;
    }

    public Date getContractEndingTime() {
        return contractEndingTime;
    }

    public void setContractEndingTime(Date contractEndingTime) {
        this.contractEndingTime = contractEndingTime;
    }

    public Integer getQuantityIb() {
        return quantityIb;
    }

    public void setQuantityIb(Integer quantityIb) {
        this.quantityIb = quantityIb;
    }

    public Integer getLabourQuantity() {
        return labourQuantity;
    }

    public void setLabourQuantity(Integer labourQuantity) {
        this.labourQuantity = labourQuantity;
    }

    public Integer getPartsQuantity() {
        return partsQuantity;
    }

    public void setPartsQuantity(Integer partsQuantity) {
        this.partsQuantity = partsQuantity;
    }

    public Integer getProbeQuantity() {
        return probeQuantity;
    }

    public void setProbeQuantity(Integer probeQuantity) {
        this.probeQuantity = probeQuantity;
    }

    public Integer getCountQuantity() {
        return countQuantity;
    }

    public void setCountQuantity(Integer countQuantity) {
        this.countQuantity = countQuantity;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSystemNo() {
        return systemNo;
    }

    public void setSystemNo(String systemNo) {
        this.systemNo = systemNo;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getSparePartsNo() {
        return sparePartsNo;
    }

    public void setSparePartsNo(String sparePartsNo) {
        this.sparePartsNo = sparePartsNo;
    }

    public Double getSparePartsPrice() {
        return sparePartsPrice;
    }

    public void setSparePartsPrice(Double sparePartsPrice) {
        this.sparePartsPrice = sparePartsPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getRSESup() {
        return RSESup;
    }

    public void setRSESup(String RSESup) {
        this.RSESup = RSESup;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getSparePartsReturn() {
        return sparePartsReturn;
    }

    public void setSparePartsReturn(String sparePartsReturn) {
        this.sparePartsReturn = sparePartsReturn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInsuranceDescription() {
        return insuranceDescription;
    }

    public void setInsuranceDescription(String insuranceDescription) {
        this.insuranceDescription = insuranceDescription;
    }

    public String getProbeType() {
        return probeType;
    }

    public void setProbeType(String probeType) {
        this.probeType = probeType;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getRowner() {
        return rowner;
    }

    public void setRowner(String rowner) {
        this.rowner = rowner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public Integer getTotalContractProbeCount() {
        return totalContractProbeCount;
    }

    public void setTotalContractProbeCount(Integer totalContractProbeCount) {
        this.totalContractProbeCount = totalContractProbeCount;
    }

    public Integer getApplyLimitCount() {
        return applyLimitCount;
    }

    public void setApplyLimitCount(Integer applyLimitCount) {
        this.applyLimitCount = applyLimitCount;
    }

    public Integer getNormalLimitCount() {
        return normalLimitCount;
    }

    public void setNormalLimitCount(Integer normalLimitCount) {
        this.normalLimitCount = normalLimitCount;
    }

    public Integer getSpecialLimitCount() {
        return specialLimitCount;
    }

    public void setSpecialLimitCount(Integer specialLimitCount) {
        this.specialLimitCount = specialLimitCount;
    }

    public Integer getHighValueLimitCount() {
        return highValueLimitCount;
    }

    public void setHighValueLimitCount(Integer highValueLimitCount) {
        this.highValueLimitCount = highValueLimitCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
