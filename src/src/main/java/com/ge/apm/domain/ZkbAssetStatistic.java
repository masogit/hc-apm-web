package com.ge.apm.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 设备信息统计
 * @author 503202755 2020/10/42
 */
public class ZkbAssetStatistic {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 设备型号
     */
    private String functionType;

    /**
     * 系统ID
     */
    private String systemId;
    /**
     * 租户ID
     */
    private String tenantUid;
    /**
     * 租户名称
     */
    private String name;
    /**
     * 资产UID
     */
    private String assetUid;

    /**
     * 资产二维码数量
     */
    private String qrCodeCount;

    /**
     * 资产二维码状态
     */
    private String qrCodeCountState;

    /**
     * blob_object uuid
     */
    private String boUuid;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 是否能粘贴二维啊
     */
    private String canNotPastQrCode;

    /**
     * 不能粘贴理由
     */
    private String reason;

    /**
     * 是否粘贴二维码
     */
    private Boolean isQrCode;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getQrCodeCount() {
        if(StringUtils.isBlank(qrCodeCount))
        {
            return "0";
        }
        return qrCodeCount;
    }

    public void setQrCodeCount(String qrCodeCount) {
        this.qrCodeCount = qrCodeCount;
    }

    public String getQrCodeCountState() {
        if(StringUtils.isNotBlank(this.qrCodeCount))
        {
            Integer integerQrCodeCount = new Integer(this.qrCodeCount);
            if(Objects.equals(integerQrCodeCount, 3))
            {
                return "Y";
            }

        }
        if(this.isQrCode!=null&&this.isQrCode)
        {
            return "Y";
        }
        return "N";
    }

    public void setQrCodeCountState(String qrCodeCountState) {
        this.qrCodeCountState = qrCodeCountState;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getQrCode() {
        return isQrCode;
    }

    public void setQrCode(Boolean qrCode) {
        isQrCode = qrCode;
    }
}
