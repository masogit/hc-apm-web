package com.ge.apm.domain;

import com.ge.apm.service.zkb.ZkbFePartOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
     * CommercialAccept Commercial审批通过
     * CommercialReject Commercial拒绝
     * FourHundredsAccept:400回填
     * FourHundredsAccept:400拒绝
     * GEAdminAccept GEAdmin 全部归还
     * GEAdminAcceptPart GEAdmin 部分归还
     */
    public enum workFlowStatus {
        Requested, TenantAccept, TenantReject, CommercialAccept, CommercialReject, FourHundredsAccept, FourHundredsReject, GEAdminAccept, GEAdminAcceptPart
    }

    /**
     * ManMade:人工申请
     * SpareParts：备件申请
     * Probe:探头申请
     */
    public enum OrderType {
        ManMade, SpareParts, Probe
    }

    /**
     * WaitingAssignment:待租户审批
     * WaitingApm:待400团队回填APM
     * 400团队 拒绝
     * Complete:完成
     */
    public enum workFlowSteps {
        Requested, WaitingAssignment, WaitingCommercial, WaitingApm,WaitingGEAdmin, Complete
    }

    public enum zkbPoProbeQuotaType {
        Any, Special, Normal, High
    }

    /**
     * 流程步骤
     */
    public static String[] stepAndRoles = new String[]
            {
                    workFlowSteps.Requested.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString(),
                    workFlowSteps.WaitingAssignment.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString(),
                    workFlowSteps.WaitingCommercial.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Commercial.toString(),
                    workFlowSteps.WaitingApm.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString(),
                    workFlowSteps.WaitingGEAdmin.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString(),
                    workFlowSteps.Complete.toString()
            };

    /**
     * 流程步骤
     */
    public static String[] probeStepAndRoles = new String[]
            {
                    workFlowSteps.Requested.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString(),
                    workFlowSteps.WaitingAssignment.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString(),
                    workFlowSteps.WaitingCommercial.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Commercial.toString(),
                    workFlowSteps.WaitingApm.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam_Probe.toString(),
                    workFlowSteps.WaitingGEAdmin.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString(),
                    workFlowSteps.Complete.toString()
            };

    /**
     * 流程步骤
     */
    public static String[] manmadeStepAndRoles = new String[]
            {
                    workFlowSteps.Requested.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString(),
                    workFlowSteps.WaitingAssignment.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString(),
                    workFlowSteps.WaitingCommercial.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.Commercial.toString(),
                    workFlowSteps.WaitingApm.toString() + "," + ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString(),
                    workFlowSteps.Complete.toString()
            };

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
     * SystemID/厂商报修ID
     */
    @Column(name = "system_id")
    private String systemId;
    /**
     * 设备Uid
     */
    @Column(name = "asset_id")
    private String assetId;

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
     * 派工备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 租户UID
     */
    @Column(name = "tenant_uid")
    private String tenantUid;

    /**
     * 租户名称
     */
    @Transient
    private String tenantName;

    /**
     * 合同编号
     */
    @Transient
    private String contractNo;

    /**
     * 区域
     */
    @Transient
    private String region;

    /**
     * 合同类型
     */
    @Transient
    private String contractType;

    /**
     * 备件号
     */
    @Transient
    private String assetNo;


    /**
     * 归还备注
     */
    @Transient
    private String assetDescription;

    /**
     * 价值类型
     */
    @Transient
    private String valueType;

    @Transient
    private String catModel;

    /**
     * 设备类型（DI）
     */
    @Column(name = "asset_type_di")
    private String assetTypeDi;

    /**
     * 机型
     */
    @Column(name = "artificial_type")
    private String artificialType;

    /**
     * 申请人电话
     */
    @Column(name = "apply_person_phone")
    private String applyPersonPhone;

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
     * 订单号
     */
    @Column(name = "father_part_order_no")
    private String fatherPartOrderNo;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false, name = "cat_id", referencedColumnName = "id")
    private ZkbPartOrderCat zkbPartOrderCat;

    /**
     * 是否全新探头
     */
    @Column(name = "new_cat_flag")
    private String newCatFlag;

    /**
     * 是都可以提供全新合同
     */
    @Column(name = "provide_new_contract")
    private String provideNewContract;


    /**
     * 是否全新探头
     */
    @Column(name = "second_new_cat_flag")
    private String secondNewCatFlag;

    /**
     * 是都可以提供全新合同
     */
    @Column(name = "second_provide_new_contract")
    private String secondProvideNewContract;

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
     * 剩余数量
     */
    @Transient
    private Integer remainQty;

    /**
     * 数量
     */
    @Column(name = "qty")
    private Integer qty;

    /**
     * 是否为紧急订单
     */
    @Column(name = "urgent_order")
    private String urgentOrder = ZkbFePartOrderService.urgentOrderFalse;

    /**
     * 订单紧急的原因
     */
    @Column(name = "urgency_reasons")
    private String urgencyReasons;

    /**
     * 是否需要探头说明函
     */
    @Column(name = "need_letter_flag")
    private String needLetterFlag = "f";

    /**
     * 设备分类 1.放射类 2.超声类
     */
    private String assetGroup;

    /**
     * 探头号
     */
    @Column(name = "cat_no")
    private String catNo;

    /**
     * 探头型号
     */
    @Column(name = "cat_id")
    private String catId;

    /**
     * 旧探头SN号
     */
    @Column(name = "old_cat_sn")
    private String oldCatSn;

    /**
     * 数量
     */
    @Column(name = "cat_num")
    private Integer catNum;

    /**
     * 供应商审批备注
     */
    @Column(name = "comment")
    private String comment;

    /**
     * 审批人
     */
    @Column(name = "tenant_assign_user")
    private String tenantAssignUser;

    /**
     * 供应商审批时间
     */
    @Column(name = "tenant_assign_date")
    private Date tenantAssignDate;

    /**
     * Order#
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 价格
     */
    @Column(name = "price")
    private String price;

    /**
     * 回填APM备注
     */
    @Column(name = "apm_comment")
    private String apmComment;

    /**
     * 填充APM信息人
     */
    @Column(name = "full_apm_info_user")
    private String fullApmInfoUser;

    /**
     * 填充APM信息时间
     */
    @Column(name = "full_apm_info_date")
    private Date fullApmInfoDate;

    /**
     * 归还类型
     */
    @Column(name = "return_type")
    private String returnType;

    /**
     * Part Serial Number*
     */
    @Column(name = "part_serial_number")
    private String partSerialNumber;

    /**
     * 故障描述
     */
    @Column(name = "part_quality_issue")
    private String partQualityIssue;

    /**
     * 运单号
     */
    @Column(name = "waybill_no")
    private String waybillNo;

    /**
     * 归还日期
     */
    @Column(name = "return_date")
    private Date returnDate;

    /**
     * 新探头SN编号
     */
    @Column(name = "new_cat_sn")
    private String newCatSn;

    /**
     * GE Admin归还备注
     */
    @Column(name = "ge_admin_comment")
    private String geAdminComment;

    /**
     * GE Admin  非ge设备未维修完成备注
     */
    @Column(name = "ge_admin_Nge_comment")
    private String geAdminNgeComment;

    /**
     * 是否已经上传催收函
     */
    @Column(name = "has_upload_letter")
    private String hasUploadLetter = "f";

    /**
     * 扣除名额类型
     */
    @Column(name = "zkb_po_probe_quota_type")
    private String zkbPoProbeQuotaType;

    @Column(name = "test_status")
    private String testStatus;

    @Transient
    private String yesOrNo;

    @Column(name = "double_qty")
    private Boolean doubleQty;

    /**
     * 申请备件列表
     */
    @Transient
    private List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList;
    /**
     * 需要归还申请备件列表
     */
    @Transient
    private List<ZkbFePartOrderAssetInfo> zkbReturnFePartOrderAssetInfoList;

    /**
     * 申请备件归还列表
     */
    @Transient
    private List<ZkbFePartOrderReturn> zkbFePartOrderReturnList;

    /**
     * 设备类型
     */
    @Transient
    private String assetType;
    /**
     * 设备供应商
     */
    @Transient
    private String manufacture;

    @Transient
    private String qrCode;//二维码

    @Transient
    private String departNum;//主机序列号

    /**
     * 是否高值探头
     */
    @Column(name = "high_cat_flag")
    private String highCatFlag;

    @Column(name = "contract_cat_id")
    private String contractCatId;

    /**
     * 因四合一修改，需保存原contract_cat_id数据，contract_cat_id则保存zkb_contract_cat表的id
     */
    @Column(name = "contract_cat_sn_no")
    private String contractCatSnNo;
    /**
     * Commercial审批备注
     */
    @Column(name = "commercial_comment")
    private String commercialComment;

    /**
     * Commercial 审批人
     */
    @Column(name = "commercial_assign_user")
    private String commercialAssignUser;

    /**
     * Commercial审批时间
     */
    @Column(name = "commercial_assign_date")
    private Date commercialAssignDate;

    /**
     * 保养申请*
     */
    @Column(name = "maintain_flag")
    private String maintainFlag;

    /** * 是否非ge设备  t/f */
    @Column(name = "not_ge_asset")
    private String notGeAsset;

    @Column(name = "cat_model_non_ge")
    private String catModelNonGe;

    @Transient
    private Integer repairForm; //1和null是纯内修。 2，纯外修。3，先内修再外修

    public Integer getRepairForm() {
        return repairForm;
    }

    public void setRepairForm(Integer repairForm) {
        this.repairForm = repairForm;
    }

    public String getProvideNewContract() {
        return provideNewContract;
    }

    public void setProvideNewContract(String provideNewContract) {
        this.provideNewContract = provideNewContract;
    }

    public String getCatModelNonGe() {
        return catModelNonGe;
    }

    public void setCatModelNonGe(String catModelNonGe) {
        this.catModelNonGe = catModelNonGe;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getGeAdminNgeComment() {
        return geAdminNgeComment;
    }

    public void setGeAdminNgeComment(String geAdminNgeComment) {
        this.geAdminNgeComment = geAdminNgeComment;
    }

    public String getManufacture() {
        return manufacture;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public String getNotGeAsset() {
        return notGeAsset;
    }

    public void setNotGeAsset(String notGeAsset) {
        this.notGeAsset = notGeAsset;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

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

    public Date getTenantAssignDate() {
        return tenantAssignDate;
    }

    public void setTenantAssignDate(Date tenantAssignDate) {
        this.tenantAssignDate = tenantAssignDate;
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

    public String getTenantAssignUser() {
        return tenantAssignUser;
    }

    public void setTenantAssignUser(String tenantAssignUser) {
        this.tenantAssignUser = tenantAssignUser;
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

    public String getAssetTypeDi() {
        return assetTypeDi;
    }

    public void setAssetTypeDi(String assetTypeDi) {
        this.assetTypeDi = assetTypeDi;
    }

    public String getArtificialType() {
        return artificialType;
    }

    public void setArtificialType(String artificialType) {
        this.artificialType = artificialType;
    }

    public String getFatherPartOrderNo() {
        return fatherPartOrderNo;
    }

    public void setFatherPartOrderNo(String fatherPartOrderNo) {
        this.fatherPartOrderNo = fatherPartOrderNo;
    }

    public List<ZkbFePartOrderAssetInfo> getZkbFePartOrderAssetInfoList() {
        return zkbFePartOrderAssetInfoList;
    }

    public void setZkbFePartOrderAssetInfoList(List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList) {
        this.zkbFePartOrderAssetInfoList = zkbFePartOrderAssetInfoList;
    }

    public List<ZkbFePartOrderAssetInfo> getZkbReturnFePartOrderAssetInfoList() {
        List<ZkbFePartOrderAssetInfo> templeList = new ArrayList<>();
        if (CollectionUtils.isEmpty(zkbFePartOrderAssetInfoList)) {
            return templeList;
        }
        for (ZkbFePartOrderAssetInfo item : this.zkbFePartOrderAssetInfoList) {
            if (Objects.equals(item.getAssetQty(), 0))
                continue;
            templeList.add(item);
        }
        return templeList;
    }

    public void setZkbReturnFePartOrderAssetInfoList(List<ZkbFePartOrderAssetInfo> zkbReturnFePartOrderAssetInfoList) {
        this.zkbReturnFePartOrderAssetInfoList = zkbReturnFePartOrderAssetInfoList;
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

    public String getOrderAnnotation() {
        return orderAnnotation;
    }

    public void setOrderAnnotation(String orderAnnotation) {
        this.orderAnnotation = orderAnnotation;
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

    public String getAssetGroup() {
        return assetGroup;
    }

    public void setAssetGroup(String assetGroup) {
        this.assetGroup = assetGroup;
    }

    public List<ZkbFePartOrderReturn> getZkbFePartOrderReturnList() {
        return zkbFePartOrderReturnList;
    }

    public void setZkbFePartOrderReturnList(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList) {
        this.zkbFePartOrderReturnList = zkbFePartOrderReturnList;
    }

    public String getApplyPersonPhone() {
        return applyPersonPhone;
    }

    public void setApplyPersonPhone(String applyPersonPhone) {
        this.applyPersonPhone = applyPersonPhone;
    }

    public String getFullApmInfoUser() {
        return fullApmInfoUser;
    }

    public void setFullApmInfoUser(String fullApmInfoUser) {
        this.fullApmInfoUser = fullApmInfoUser;
    }

    public Date getFullApmInfoDate() {
        return fullApmInfoDate;
    }

    public void setFullApmInfoDate(Date fullApmInfoDate) {
        this.fullApmInfoDate = fullApmInfoDate;
    }

    public String getProblemOrderNumber() {
        return problemOrderNumber;
    }

    public void setProblemOrderNumber(String problemOrderNumber) {
        this.problemOrderNumber = problemOrderNumber;
    }

    public String getGeAdminComment() {
        return geAdminComment;
    }

    public void setGeAdminComment(String geAdminComment) {
        this.geAdminComment = geAdminComment;
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

    public Integer getRemainQty() {
        return remainQty;
    }

    public void setRemainQty(Integer remainQty) {
        this.remainQty = remainQty;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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

    public String getNeedLetterFlag() {
        return needLetterFlag;
    }

    public void setNeedLetterFlag(String needLetterFlag) {
        this.needLetterFlag = needLetterFlag;
    }

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getOldCatSn() {
        return oldCatSn;
    }

    public void setOldCatSn(String oldCatSn) {
        this.oldCatSn = oldCatSn;
    }

    public Integer getCatNum() {
        return catNum;
    }

    public void setCatNum(Integer catNum) {
        this.catNum = catNum;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getApmComment() {
        return apmComment;
    }

    public void setApmComment(String apmComment) {
        this.apmComment = apmComment;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getPartSerialNumber() {
        return partSerialNumber;
    }

    public void setPartSerialNumber(String partSerialNumber) {
        this.partSerialNumber = partSerialNumber;
    }

    public String getPartQualityIssue() {
        return partQualityIssue;
    }

    public void setPartQualityIssue(String partQualityIssue) {
        this.partQualityIssue = partQualityIssue;
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

    public String getNewCatSn() {
        return newCatSn;
    }

    public void setNewCatSn(String newCatSn) {
        this.newCatSn = newCatSn;
    }

    public ZkbPartOrderCat getZkbPartOrderCat() {
        return zkbPartOrderCat;
    }

    public void setZkbPartOrderCat(ZkbPartOrderCat zkbPartOrderCat) {
        this.zkbPartOrderCat = zkbPartOrderCat;
    }

    public String getNewCatFlag() {
        return newCatFlag;
    }

    public void setNewCatFlag(String newCatFlag) {
        this.newCatFlag = newCatFlag;
    }

    public String getHasUploadLetter() {
        return hasUploadLetter;
    }

    public void setHasUploadLetter(String hasUploadLetter) {
        this.hasUploadLetter = hasUploadLetter;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getZkbPoProbeQuotaType() {
        return zkbPoProbeQuotaType;
    }

    public void setZkbPoProbeQuotaType(String zkbPoProbeQuotaType) {
        this.zkbPoProbeQuotaType = zkbPoProbeQuotaType;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getCatModel() {
        return catModel;
    }

    public void setCatModel(String catModel) {
        this.catModel = catModel;
    }

    public String getYesOrNo() {
        return yesOrNo;
    }

    public void setYesOrNo(String yesOrNo) {
        this.yesOrNo = yesOrNo;
    }

    public String getHighCatFlag() {
        return highCatFlag;
    }

    public void setHighCatFlag(String highCatFlag) {
        this.highCatFlag = highCatFlag;
    }

    public String getContractCatId() {
        return contractCatId;
    }

    public void setContractCatId(String contractCatId) {
        this.contractCatId = contractCatId;
    }

    public Boolean getDoubleQty() {
        return doubleQty;
    }

    public void setDoubleQty(Boolean doubleQty) {
        this.doubleQty = doubleQty;
    }

    public String getSecondNewCatFlag() {
        return secondNewCatFlag;
    }

    public void setSecondNewCatFlag(String secondNewCatFlag) {
        this.secondNewCatFlag = secondNewCatFlag;
    }

    public String getCommercialComment() {
        return commercialComment;
    }

    public void setCommercialComment(String commercialComment) {
        this.commercialComment = commercialComment;
    }

    public String getCommercialAssignUser() {
        return commercialAssignUser;
    }

    public void setCommercialAssignUser(String commercialAssignUser) {
        this.commercialAssignUser = commercialAssignUser;
    }

    public Date getCommercialAssignDate() {
        return commercialAssignDate;
    }

    public void setCommercialAssignDate(Date commercialAssignDate) {
        this.commercialAssignDate = commercialAssignDate;
    }

    public String getContractCatSnNo() {
        return contractCatSnNo;
    }

    public void setContractCatSnNo(String contractCatSnNo) {
        this.contractCatSnNo = contractCatSnNo;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getMaintainFlag() {
        return maintainFlag;
    }

    public void setMaintainFlag(String maintainFlag) {
        this.maintainFlag = maintainFlag;
    }

    public String getSecondProvideNewContract() {
        return secondProvideNewContract;
    }

    public void setSecondProvideNewContract(String secondProvideNewContract) {
        this.secondProvideNewContract = secondProvideNewContract;
    }
}
