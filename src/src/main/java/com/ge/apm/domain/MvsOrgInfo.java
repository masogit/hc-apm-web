package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "mvs_org_info")
@Data
public class MvsOrgInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "site_id")
    private String siteId;

    @Column(name = "location_code")
    private String locationCode;//安装位置编码

    @Column(name = "ucm_id")
    private String UCMId;

    @Column(name = "area")
    private String area;

    @Column(name = "province")
    private String province;

    @Column(name = "service_region")
    private String serviceRegion;

    @Column(name = "customer_chinese_address")
    private String customerChineseAddress;

    @Column(name = "pl")
    private String PL;

    @Column(name = "city")
    private String city;

    @Column(name = "hp_level")
    private String HPLevel;

    @Column(name = "sales_sso")
    private String salesSSO;

    @Column(name = "account_sales")
    private String accountSales;

    @Column(name = "sales_mgr")
    private String salesMgr;

    @Column(name = "region_mgr")
    private String regionMgr;

    @Column(name = "region2")
    private String region2;

    @Column(name = "enterprise")
    private String enterprise;

    @Column(name = "enterprise_vip")
    private String enterpriseVIP;

    @Column(name = "enterprise_vip_leader_sso")
    private String enterpriseVIPLeaderSSO;

    @Column(name = "enterprise_vip_leader_name")
    private String enterpriseVIPLeaderName;

    @Column(name = "commerial_svc_region")
    private String commerialSVCRegion;

    @Column(name = "account_segment_first_level")
    private String accountSegmentFirstLevel;

    @Column(name = "account_segment_second_level")
    private String accountSegmentSecondLevel;

    @Transient
    private boolean pass;

    @Transient
    private String checkResult;
}
