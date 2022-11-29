package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_asset_group_asset_match")
@Data
public class HisAssetGroupAssetMatch implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String uuid;

    //科室部门uid
    @Column(name = "clinical_dept_uid")
    private String clinicalDeptUid;

    //资产分组uid
    @Column(name = "asset_group_uid")
    private String assetGroupUid;

    //orgInfo表Uid
    @Column(name = "org_info_uid")
    private String orgInfoUid;

    @Column(name = "asset_group_name") // 设备分类名称
    private String assetGroupName;

    @Column(name = "clinical_dept_name") //所属科室名称
    private String clinicalDeptName;

    @Column(name = "is_sub_equipment") //是否子设备
    private Boolean isSubEquipment;

//    @Column(name = "group_rate") //占比
    @Transient
    private Double groupRate;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HisAssetGroupAssetMatch)) {
            return false;
        }
        HisAssetGroupAssetMatch other = (HisAssetGroupAssetMatch) object;
        if ((this.uuid == null && other.uuid != null) || (this.uuid != null && !this.uuid.equals(other.uuid))) {
            return false;
        }
        return true;
    }

}

