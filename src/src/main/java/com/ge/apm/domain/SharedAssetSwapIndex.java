package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author EchoWong
 * @description
 * @updateTime 11/4/2021 15:59
 */
@Entity
@Table(name = "shared_asset_swap_index")
public class SharedAssetSwapIndex implements Serializable{


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id",columnDefinition = "CHAR(32)")
    private String id;

    //调剂设备类型uid 对应shared_asset_type表主键
    @Column(name = "shared_asset_type")
    private String sharedAssetType;

    //设备uid  对应asset_info表uid
    @Column(name = "asset_uid")
    private String assetUid;

    //调剂编号 同一调剂设备类型shared_asset_type下应唯一
    @Column(name = "swap_index")
    private String swapIndex;
    //
    @Column(name="remark",columnDefinition = "text")
    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSharedAssetType() {
        return sharedAssetType;
    }

    public void setSharedAssetType(String sharedAssetType) {
        this.sharedAssetType = sharedAssetType;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getSwapIndex() { return swapIndex; }

    public void setSwapIndex(String swapIndex) {
        this.swapIndex = swapIndex;
    }

//    @PrePersist
//    public void initializeUid() {
//        if (getId() == null)
//            setId(UUID.randomUUID().toString().replace("-", ""));
//    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
