package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.io.Serializable;



@Entity
@Table(name = "key_asset_report_org")
@Data
public class KeyAssetReportOrg implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;



    @Column(name = "key_asset_report_id")
    private String keyAssetReportId;


    @Column(name = "org_id")
    private String orgId;

}
