package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author : 503189362
 * @date : 2021/6/2
 * @description :
 */
@Entity
@Table(name = "zkb_asset_qr_code_from_ib")
public class ZkbAssetQrCodeFromIb {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 供应商tenantUID
     */
    @Column(name = "tenant_uid")
    private String tenantUID;

    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "attachment_count")
    private Integer attachmentCount;

    @Column(name = "is_qr_code")
    private Boolean isQrCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantUID() {
        return tenantUID;
    }

    public void setTenantUID(String tenantUID) {
        this.tenantUID = tenantUID;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
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

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Boolean getQrCode() {
        return isQrCode;
    }

    public void setQrCode(Boolean qrCode) {
        isQrCode = qrCode;
    }
}
