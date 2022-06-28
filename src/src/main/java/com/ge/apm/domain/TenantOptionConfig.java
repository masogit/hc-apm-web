package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tenant_option_config")
@Data
public class TenantOptionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "order_by_create_time_by_default")
    private Boolean orderByCreateTimeByDefault;

    @Column(name = "allow_asset_batch_update")
    private Boolean allowAssetBatchUpdate;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "active_supplier")
    private Boolean activeSupplier;

    @Column(name = "show_is_pm")
    private Boolean showIsPm;

    @Column(name = "synchronize_work_order")
    private Boolean synchronizeWorkOrder;
}
