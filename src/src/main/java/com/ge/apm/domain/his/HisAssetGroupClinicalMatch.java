package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_asset_group_clinical_match")
@Data
public class HisAssetGroupClinicalMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uuid;

    //his_asset_group 和 his_asset_group_asset_match 拼接
    @Column(name = "asset_group_clinical_match_id")
    private String assetGroupClinicalId;

    //apm资产设备uid
    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "depart_num")
    private String departNum;//设备编号，资产设备编码,todo 设备编号也是唯一吧

    @Column(name = "his_asset_group_uid")
    private String hisAssetGroupUId; //自定义分组表的id

    //科室部门uid
    @Column(name = "clinical_dept_uid")
    private String clinicalDeptUid;

}
