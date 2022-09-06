package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503266962
 */
@Entity
@Data
@Table(name = "rcc_etl_device")
public class RccEtlDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    RccEltDevicePK pk;

    @Column(name = "id")
    private Integer id;

    @JoinColumn(insertable = false, updatable = false, name = "site_uid", referencedColumnName = "uid")
    @OneToOne
    private OrgInfo orgInfo;

    @JoinColumn(insertable = false, updatable = false, name = "asset_uid", referencedColumnName = "uid")
    @OneToOne
    private AssetInfo assetInfo;

    @Column(name = "name")
    private String name;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "device_model")
    private String deviceModel;

    @Transient
    private String assetUid;

}
