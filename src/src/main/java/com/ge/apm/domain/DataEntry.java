package com.ge.apm.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.domain.his.HisAssetGroup;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "data_entry")
@Data
public class DataEntry implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;
    @Column(name = "asset_group_uid")
    private String  assetGroupUid;

    @Column(name = "asset_group_org_info_uid")
    private String  assetGroupOrgInfoUid;


//    @Column(name = "org_info_uid")
//    private String  orgInfoUid;
//
//    @Column(name = "clinical_dept_uid")
//    private String  clinicalDeptUid;

    @Column(name = "his_asset_group_asset_match_uid")
    private String hisAssetGroupAssetMatchUid;

    @Column(name = "entry_type")
    private String entryType; //录入类型

    @Column(name = "entry_amount",precision = 18, scale = 6)
    private BigDecimal entryAmount;  //录入金额

    @Column(name = "created_date")
    private Date createdDate;//创建时间

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Column(name = "operation_user_uid")
    private String operationUserUid;

    @Column(name = "asset_uid")
    private String assetUid;
    @Column(name = "group_or_single")
    private int  groupOrSingle;  //  1分组  2单台设备

    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUid;//所属科室
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @LastModifiedBy
    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_name", nullable = true, length = 50)
    private String createdName;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "asset_group_uid",referencedColumnName = "uuid", insertable = false,updatable = false)
    private HisAssetGroup hisAssetGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "operation_user_uid",referencedColumnName = "uid", insertable = false,updatable = false)
//    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "last_modified_by",referencedColumnName = "uid", insertable = false,updatable = false)
    private UserAccount lastModifiedUserAccount;

    @Transient
    @TableField(exist = false)
    private String entryTypeZh;
    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataEntry)) {
            return false;
        }
        DataEntry other = (DataEntry) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
