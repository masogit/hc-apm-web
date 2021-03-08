package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
     * 是否能粘贴二维啊
     */
    @Column(name = "can_not_past_qr_code")
    private String canNotPastQrCode;

    /**
     * 不能粘贴理由
     */
    @Column(name = "reason")
    private String reason;

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
}
