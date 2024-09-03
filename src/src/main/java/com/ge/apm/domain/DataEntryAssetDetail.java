package com.ge.apm.domain;


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
@Table(name = "data_entry_asset_detail")
@Data
public class DataEntryAssetDetail implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name = "data_entry_uid")
    private String  dataEntryUid;

    @Column(name = "asset_group_uid")
    private String  assetGroupUid;

    @Column(name = "asset_group_org_info_uid")
    private String  assetGroupOrgInfoUid;

    @Column(name = "entry_type")
    private String entryType; //录入类型

    @Column(name = "entry_amount",precision = 18, scale = 6)
    private BigDecimal entryAmount;  //录入金额

    @Column(name = "cost_amount",precision = 18, scale = 6)
    private BigDecimal  costAmount;

    @Column(name = "created_date")
    private Date createdDate;//创建时间


    @Column(name = "statistical_date")
    @Temporal(TemporalType.DATE)
    private Date statisticalDate;


    @Column(name = "asset_uid")
    private String assetUid;



    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;





    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DataEntryAssetDetail)) {
            return false;
        }
        DataEntryAssetDetail other = (DataEntryAssetDetail) object;
        if ((this.uid == null && other.uid != null) || (this.uid != null && !this.uid.equals(other.uid))) {
            return false;
        }
        return true;
    }
}
