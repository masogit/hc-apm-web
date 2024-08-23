package com.ge.apm.domain.his;

import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.domain.AssetInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * 设备分组科室匹配设备
 */
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

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "scale_assessment",columnDefinition = "decimal(11,2)")
    private Double  scaleAssessment;



    @Transient
    @TableField(exist = false)
    private Double  amount;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "clinical_dept_uid", referencedColumnName = "clinical_dept_uid", insertable = false,updatable = false),
            @JoinColumn(name = "his_asset_group_uid", referencedColumnName = "asset_group_uid", insertable = false,updatable = false)
    })
    private HisAssetGroupAssetMatch hisAssetGroupAssetMatch;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uuid != null ? uuid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HisAssetGroupClinicalMatch)) {
            return false;
        }
        HisAssetGroupClinicalMatch other = (HisAssetGroupClinicalMatch) object;
        return (this.uuid != null || other.uuid == null) && (this.uuid == null || this.uuid.equals(other.uuid));
    }

}
