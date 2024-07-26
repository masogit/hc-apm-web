package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "asset_empowerment")
@Data
public class AssetEmpowerment {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String uid;

    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "asset_id")
    private String assetUid;

    @Column(name = "supplier_uid")
    private String supplierUid;

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getUserUid() { return userUid; }

    public void setUserUid(String userUid) { this.userUid = userUid; }

    public String getAssetUid() { return assetUid; }

    public void setAssetUid(String assetUid) { this.assetUid = assetUid; }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }
}
