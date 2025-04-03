package com.ge.apm.domain.budget;

import com.ge.apm.domain.AssetInfo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 维保合同申购
 */
@Entity
@Data
@Table(name = "maintenance_contract_apply_purchase_asset")
public class MaintenanceContractApplyPurchaseAsset {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    @Getter
    @Setter
    private String id;

    // 申请id
    @Column(name = "application_id",updatable = false,columnDefinition = "CHAR(32)")
    private String applicationId;

    @Column(name = "asset_uid")
    private String assetUid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "asset_uid", referencedColumnName = "uid")
    private AssetInfo assetInfo;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MaintenanceContractApplyPurchaseAsset)) {
            return false;
        }
        MaintenanceContractApplyPurchaseAsset other = (MaintenanceContractApplyPurchaseAsset) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
