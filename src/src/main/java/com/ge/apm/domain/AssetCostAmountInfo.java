package com.ge.apm.domain;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Data
@Table(name = "asset_cost_amount_info")
public class AssetCostAmountInfo implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name = "asset_uid")
    private String assetUid;

    @Column(name = "cost_amount")
    private Double  costAmount;
    @Column(name = "statistical_date")
    @Temporal(TemporalType.DATE)
    private Date statisticalDate;

//    @Column(name = "data_entry_uid")
//    private String dataEntryUid;



}
