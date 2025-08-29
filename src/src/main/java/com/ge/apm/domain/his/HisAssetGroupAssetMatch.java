package com.ge.apm.domain.his;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 设备分组科室匹配
 */
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


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_group_uid",referencedColumnName = "uuid", insertable = false,updatable = false)
    private HisAssetGroup hisAssetGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_info_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private OrgInfo orgInfo;



    @CreatedDate
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @CreatedBy
    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

//    @Column(name = "group_rate") //占比
    @Transient
    private Double groupRate;

    @Transient
    private String  groupNameOrgName;

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

    public String getGroupNameOrgName() {
        return hisAssetGroup.getName()+"---"+orgInfo.getName();
    }

    public void setGroupNameOrgName(String groupNameOrgName) {
        this.groupNameOrgName = groupNameOrgName;
    }
}

