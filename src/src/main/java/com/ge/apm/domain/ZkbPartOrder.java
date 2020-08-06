package com.ge.apm.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@Entity
@Table(name = "zkb_part_order")
public class ZkbPartOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Pending：申请 TechAccept：技术审批通过 TechReject：技术审批不通过 ManageAccept：区域经理审批通过
     * ManageReject：区域经理审批不通过 Ordered：发货 OrderedReject:发货取消
     * Returned：归还  Closed：关单
     */
    public enum PartOrderStatus {
        Requested,TechnicalAccept,TechnicalReject,ManageAccept,ManageReject,Ordered,OrderedReject,Returned,Closed
    }

    /**
     * Parts:备件申请
     * Probe:探头申请
     * Artificial:人工申请
     */
    public enum OrderType {
        Parts,Probe,Artificial
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
     * 合同id
     */
    @Column(name = "contract_id")
    private String contractId;

    /**
     * 工单ID
     */
    @Column(name = "work_order_id")
    private String workOrderId;

    /**
     * 设备ID
     */
    @Column(name = "asset_id")
    private String assetId;

    /**
     * 区域
     */
    @Column(name = "region")
    private String region;

    /**
     * 订单类型
     */
    @Column(name = "order_type")
    private String orderType;

    /**
     * 订单日期
     */
    @Column(name = "part_order_date")
    private Date partOrderDate;

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
     * 数量
     */
    @Column(name = "qty")
    private int qty;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 申请人
     */
    @Column(name = "request_user")
    private String requestUser;

    /**
     * 申请时间
     */
    @Column(name = "request_date")
    private Date requestDate;

    /**
     * 申请人电话
     */
    @Column(name = "request_phone")
    private String requestPhone;

    /**
     * 技术支持审批备注
     */
    @Column(name = "tech_remark")
    private String techRemark;

    /**
     * 区域经理审批备注
     */
    @Column(name = "manage_remark")
    private String manageRemark;

    /**
     *
     */
    @Column(name = "sr_no")
    private String srNo;

    /**
     *
     */
    @Column(name = "activity_no")
    private String activityNo;

    /**
     *
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 订货备注
     */
    @Column(name = "ordered_remark")
    private String orderedRemark;

    /**
     * 归还类型
     */
    @Column(name = "return_type")
    private String returnType;

    @Column(name = "return_qty")
    private int returnQty = 0;

    /**
     * 运单号
     */
    @Column(name = "waybill_no")
    private String waybillNo;

    /**
     * 收货人
     */
    @Column(name = "receiver")
    private String receiver;

    /**
     * 收货人电话
     */
    @Column(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 收货地址
     */
    @Column(name = "receiver_address")
    private String receiverAddress;

    /**
     * 归还日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    /**
     * 归还备注
     */
    @Column(name = "return_remark")
    private String returnRemark;

    /**
     * 关单备注
     */
    @Column(name = "closed_remark")
    private String closedRemark;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    /**
     * 租户
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    /**
     * 医院名称
     */
    @Column(name = "hospital_name")
    private String hospitalName;

    /**
     * 医院报修人姓名
     */
    @Column(name = "hospital_repairer")
    private String hospitalRepairer;

    /**
     * 医院报修人电话
     */
    @Column(name = "hospital_repairer_phone")
    private String hospitalRepairerPhone;

    /**
     * 产品型号
     */
    @Column(name = "product_number")
    private String productNumber;

    /**
     * 软件版本
     */
    @Column(name = "software_version")
    private String softwareVersion;

    /**
     * 故障描述
     */
    @Column(name = "fault_description")
    private String faultDescription;

    /**
     * 订单标注
     */
    @Column(name = "order_annotation")
    private String orderAnnotation;

    /**
     * 发生问题的order number
     */
    @Column(name = "problem_order_number")
    private String problemOrderNumber;

    /**
     * 是否为紧急订单
     */
    @Column(name = "urgent_order")
    private String urgentOrder;

    /**
     * 订单紧急的原因
     */
    @Column(name = "urgency_reasons")
    private String urgencyReasons;

    @Column(name = "online_engineer")
    private String onlineEngineer;

    @Transient
    List<ZkbPartOrderItem> zkbPartOrderItems = new ArrayList<>();

    @Transient
    Set<ZkbPartOrderStep> zkbPartOrderStepSet = new HashSet<>();

    @Column(name = "sr_num")
    private String srNum;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "cat_id")
    private String catId;

    @Column(name = "artificial_type")
    private String artificialType;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "cat_id", referencedColumnName = "id")
    private ZkbPartOrderCat zkbPartOrderCat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "asset_id", referencedColumnName = "uid")
    private AssetInfo assetInfo;

    @Transient
    private int remainQty = 0;

    public int getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(int returnQty) {
        this.returnQty = returnQty;
    }

    public String getArtificialType() {
        return artificialType;
    }

    public void setArtificialType(String artificialType) {
        this.artificialType = artificialType;
    }

    public int getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(int remainQty) {
        this.remainQty = remainQty;
    }

    public ZkbPartOrderCat getZkbPartOrderCat() {
        return zkbPartOrderCat;
    }

    public void setZkbPartOrderCat(ZkbPartOrderCat zkbPartOrderCat) {
        this.zkbPartOrderCat = zkbPartOrderCat;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getSrNum() {
        return srNum;
    }

    public void setSrNum(String srNum) {
        this.srNum = srNum;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public Set<ZkbPartOrderStep> getZkbPartOrderStepSet() {
        return zkbPartOrderStepSet;
    }

    public void setZkbPartOrderStepSet(Set<ZkbPartOrderStep> zkbPartOrderStepSet) {
        this.zkbPartOrderStepSet = zkbPartOrderStepSet;
    }

    public String getOnlineEngineer() {
        return onlineEngineer;
    }

    public void setOnlineEngineer(String onlineEngineer) {
        this.onlineEngineer = onlineEngineer;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
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

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getTechRemark() {
        return techRemark;
    }

    public void setTechRemark(String techRemark) {
        this.techRemark = techRemark;
    }

    public String getManageRemark() {
        return manageRemark;
    }

    public void setManageRemark(String manageRemark) {
        this.manageRemark = manageRemark;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderedRemark() {
        return orderedRemark;
    }

    public void setOrderedRemark(String orderedRemark) {
        this.orderedRemark = orderedRemark;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnRemark() {
        return returnRemark;
    }

    public void setReturnRemark(String returnRemark) {
        this.returnRemark = returnRemark;
    }

    public String getClosedRemark() {
        return closedRemark;
    }

    public void setClosedRemark(String closedRemark) {
        this.closedRemark = closedRemark;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<ZkbPartOrderItem> getZkbPartOrderItems() {
        return zkbPartOrderItems;
    }

    public void setZkbPartOrderItems(List<ZkbPartOrderItem> zkbPartOrderItems) {
        this.zkbPartOrderItems = zkbPartOrderItems;
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

    public Date getPartOrderDate() {
        return partOrderDate;
    }

    public void setPartOrderDate(Date partOrderDate) {
        this.partOrderDate = partOrderDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getFaultDescription() {
        return faultDescription;
    }

    public void setFaultDescription(String faultDescription) {
        this.faultDescription = faultDescription;
    }

    public String getOrderAnnotation() {
        return orderAnnotation;
    }

    public void setOrderAnnotation(String orderAnnotation) {
        this.orderAnnotation = orderAnnotation;
    }

    public String getUrgentOrder() {
        return urgentOrder;
    }

    public void setUrgentOrder(String urgentOrder) {
        this.urgentOrder = urgentOrder;
    }

    public String getUrgencyReasons() {
        return urgencyReasons;
    }

    public void setUrgencyReasons(String urgencyReasons) {
        this.urgencyReasons = urgencyReasons;
    }

    public String getRequestPhone() {
        return requestPhone;
    }

    public void setRequestPhone(String requestPhone) {
        this.requestPhone = requestPhone;
    }

    public String getHospitalRepairer() {
        return hospitalRepairer;
    }

    public void setHospitalRepairer(String hospitalRepairer) {
        this.hospitalRepairer = hospitalRepairer;
    }

    public String getHospitalRepairerPhone() {
        return hospitalRepairerPhone;
    }

    public void setHospitalRepairerPhone(String hospitalRepairerPhone) {
        this.hospitalRepairerPhone = hospitalRepairerPhone;
    }

    public String getProblemOrderNumber() {
        return problemOrderNumber;
    }

    public void setProblemOrderNumber(String problemOrderNumber) {
        this.problemOrderNumber = problemOrderNumber;
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
        if (!(object instanceof ZkbPartOrder)) {
            return false;
        }
        ZkbPartOrder other = (ZkbPartOrder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbPartOrder[ id=" + id + " ]";
    }

}
