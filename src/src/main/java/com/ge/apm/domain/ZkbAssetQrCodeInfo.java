package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 粘贴二维码
 * @author 503202755 2021/3/4
 */
@Entity
@Table(name = "zkb_asset_qr_code_info")
public class ZkbAssetQrCodeInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 系统ID
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 供应商tenantUID
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    /**
     * 供应商名称
     */
    @Column(name = "tenant_name")
    private String tenantName;

    /**
     * 是否能粘贴二维啊
     */
    @Column(name = "can_not_past_qr_code")
    private String canNotPastQrCode;

    /**
     * 不能粘贴理由
     */
    @Column(name = "reason")
    private String reason;

    @Column(name = "reason_date")
    private Date reasonDate;

    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;//设备所属科室
    /**
     * 医院名称
     */
    @Column(name = "clinical_dept_name")
    private String hospitalName;

    /**
     * 设备型号
     */
    @Column(name = "function_type")
    private String functionType;
    /**
     * 合同编号
     */
    @Column(name = "contract_no")
    private String contractNo;

    /**
     * 二维码是否已实施(粘贴及照片上传)（Y/N）
     */
    @Column(name = "qr_code_count_state")
    private String qrCodeCountState;
    /**
     * ib是否已贴一机一码（Y/N）
     */
    @Column(name = "ib_qr_code_state")
    private String ibQrCodeState;

    /**
     * ib是否GE已贴一机一码（Y/N）
     */
    @Column(name = "ge_flag")
    private String geFlag;
    /**
     * 是否已贴旧二维码（Y/N）
     */
    @Column(name = "qr_code_state")
    private String qrCodeState;

    /**
     *  记录保存时间
     */
    @Column(name = "created_date")
    private Date createdDate;

    /**
     * qrCodeCountState为“Y”时的记录时间
     */
    @Column(name = "qr_code_date")
    private Date qrCodeDate;

    /**
     * 临时存储（忘记加@Transient注解，导致数据库创建不规范字段ibqrcodetemporary，@Column是后面补上）
     */
    @Column(name = "ibqrcodetemporary")
    private Boolean ibQrCodeTemporary;

    /**
     * 装机状态 0-Installed 1-Shipped 2-Deinstalled
     */
    @Column(name = "install_status")
    private Integer installStatus;

    /**
     * 无法贴码原因归类
     * scrapped 已拆机或已报废
     * notAllowed 有三方保，医院不允许贴码
     * notFind 设备找不到
     * other  其他
     */
    @Column(name = "reason_categorize")
    private String reasonCategorize;

    @Transient
    private Integer excelIdex;

    @Transient
    private String errorInfo;

    public Integer getExcelIdex() {
        return excelIdex;
    }

    public void setExcelIdex(Integer excelIdex) {
        this.excelIdex = excelIdex;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public String getReasonCategorize() {
        return reasonCategorize;
    }

    public void setReasonCategorize(String reasonCategorize) {
        this.reasonCategorize = reasonCategorize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getCanNotPastQrCode() {
        return canNotPastQrCode;
    }

    public void setCanNotPastQrCode(String canNotPastQrCode) {
        this.canNotPastQrCode = canNotPastQrCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
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

    public String getQrCodeCountState() {
        return qrCodeCountState;
    }

    public void setQrCodeCountState(String qrCodeCountState) {
        this.qrCodeCountState = qrCodeCountState;
    }

    public String getIbQrCodeState() {
        return ibQrCodeState;
    }

    public void setIbQrCodeState(String ibQrCodeState) {
        this.ibQrCodeState = ibQrCodeState;
    }

    public String getQrCodeState() {
        return qrCodeState;
    }

    public void setQrCodeState(String qrCodeState) {
        this.qrCodeState = qrCodeState;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public Boolean getIbQrCodeTemporary() {
        return ibQrCodeTemporary;
    }

    public void setIbQrCodeTemporary(Boolean ibQrCodeTemporary) {
        this.ibQrCodeTemporary = ibQrCodeTemporary;
    }

    public Date getReasonDate() {
        return reasonDate;
    }

    public void setReasonDate(Date reasonDate) {
        this.reasonDate = reasonDate;
    }

    public Date getQrCodeDate() {
        return qrCodeDate;
    }

    public void setQrCodeDate(Date qrCodeDate) {
        this.qrCodeDate = qrCodeDate;
    }

    public String getClinicalDeptUID() {
        return clinicalDeptUID;
    }

    public void setClinicalDeptUID(String clinicalDeptUID) {
        this.clinicalDeptUID = clinicalDeptUID;
    }

    public String getGeFlag() {
        return geFlag;
    }

    public void setGeFlag(String geFlag) {
        this.geFlag = geFlag;
    }

    public Integer getInstallStatus() {
        return installStatus;
    }

    public void setInstallStatus(Integer installStatus) {
        this.installStatus = installStatus;
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
        if (!(object instanceof ZkbAssetQrCodeInfo)) {
            return false;
        }
        ZkbAssetQrCodeInfo other = (ZkbAssetQrCodeInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
