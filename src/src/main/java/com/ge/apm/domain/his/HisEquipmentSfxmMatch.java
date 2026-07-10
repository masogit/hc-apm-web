package com.ge.apm.domain.his;

import com.ge.apm.domain.Supplier;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 503189826 on 7/26/2020.
 * 收费代码与分组匹配
 */

@Entity
@Table(name = "his_equipment_sfxm_match") //抓取收费代码匹配后的表
@Data
public class HisEquipmentSfxmMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "his_equipment_sfxm_id")//His收费项目代码 表主键ID
    private String hisEquipmentSfxmId;

    @Column(name = "sfxm_id")//His收费项目代码
    private String sfxmId;

    @Column(name = "sfxm_name")//His收费项目名称
    private String sfxmName;

    @Column(name = "sfxm_lb")//收费项目分类
    private String sfxmLb;

    @Column(name = "hos_id")//医院机构代码
    private String hosId;

    @Column(name = "his_group_match_uid") //HisAssetGroupAssetMatch 表主键uuid
    private String hisGroupMatchUid;

    @Column(name = "asset_group_uid") //自定义分组uid
    private String assetGroupUid;

    @Column(name = "clinical_dept_uid")//科室id
    private String clinicalDeptUid;

    @Column(name = "clinical_dept_name") //所属科室名称
    private String clinicalDeptName;

    @Column(name = "asset_group_name") // 自定义分组名称
    private String assetGroupName;

    @Column(name = "group_rate") //自定义分组占比
    private Double groupRate;

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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_group_uid",referencedColumnName = "uuid", insertable = false,updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private HisAssetGroup hisAssetGroup;

}
