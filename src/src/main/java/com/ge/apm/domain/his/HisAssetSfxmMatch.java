package com.ge.apm.domain.his;


import com.alibaba.fastjson.annotation.JSONField;
import com.ge.apm.domain.AssetInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "his_asset_sfxm_match")
public class HisAssetSfxmMatch implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //联机医院机构代码
    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "sfxm_id")//His收费项目代码
    private String sfxmId;

    @Column(name = "sfxm_name")//His收费项目名称
    private String sfxmName;

    @Column(name = "sfxm_lb")//收费项目分类
    private String sfxmLb;


    //apm资产设备uid
    @Column(name = "asset_uid")
    private String assetUid; //

    //apm资产设备名称
    @Column(name = "asset_name")
    private String assetName; //

    @Column(name = "create_by")
    private String createBy;//创建人

    @Column(name = "modify_by")
    private String modifyBy;//修改人

    @Column(name = "create_date")
    private Date createDate;//创建日期

    @Column(name = "modify_date")
    private Date modifyDate;//修改日期


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @JSONField(serialize = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;

}