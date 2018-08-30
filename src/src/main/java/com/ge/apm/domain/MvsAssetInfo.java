package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author 212688574
 */
@Entity
@Table(name = "mvs_asset_info")
public class MvsAssetInfo {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "mvs_type")
    private String mvsType;

    @Column(name = "asset_uid")
    private String assetUid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false, unique = true)
    @JsonBackReference
    private AssetInfo assetInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getDeviceType() {
        return deviceType;
    }


    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public String getMvsType() {
        return mvsType;
    }

    public void setMvsType(String mvsType) {
        this.mvsType = mvsType;
    }
}
