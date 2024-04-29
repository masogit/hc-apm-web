/*
 */
package com.ge.apm.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "special_asset_permit_info")
public class SpecialAssetPermitInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "special_asset_id")
    private String specialAssetId;

    @Column(name = "permit_info_uid")
    private String PermitInfoUid;


    @JoinColumn(name = "special_asset_id", referencedColumnName = "asset_uid", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private SpecialAssetInfo specialAssetInfo;

    @JoinColumn(name = "permit_info_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    @ManyToOne( fetch = FetchType.EAGER)
    private PermitInfo permitInfo;



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSpecialAssetId() {
        return specialAssetId;
    }

    public void setSpecialAssetId(String specialAssetId) {
        this.specialAssetId = specialAssetId;
    }

    public String getPermitInfoUid() {
        return PermitInfoUid;
    }

    public void setPermitInfoUid(String permitInfoUid) {
        PermitInfoUid = permitInfoUid;
    }

    public SpecialAssetInfo getSpecialAssetInfo() {
        return specialAssetInfo;
    }

    public void setSpecialAssetInfo(SpecialAssetInfo specialAssetInfo) {
        this.specialAssetInfo = specialAssetInfo;
    }

    public PermitInfo getPermitInfo() {
        return permitInfo;
    }

    public void setPermitInfo(PermitInfo permitInfo) {
        this.permitInfo = permitInfo;
    }

    public SpecialAssetPermitInfo() {
    }
}