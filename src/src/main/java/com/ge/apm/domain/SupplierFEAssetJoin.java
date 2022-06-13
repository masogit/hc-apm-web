package com.ge.apm.domain;


import lombok.Data;

import javax.persistence.*;

/**
 * 供应商设备绑定
 */
@Data
@Entity
@Table(name = "supplier_fe_asset_join")
public class SupplierFEAssetJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_account_uid")
    private String userAccountUid;

    @Column(name = "asset_info_uid")
    private String assetInfoUid;


}
