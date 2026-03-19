package com.ge.apm.domain.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "his_equipment_data_result") //抓取的数据源
@Data
public class HisEquipmentDataResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    /**
     * 机构代码
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 机构名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 总金额总和
     */
    @Column(name = "total_amount",scale = 4)
    private BigDecimal totalAmount;
    /**
     * 执行时间
     */
    @Column(name = "exec_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date execTime;

    /**
     * APM设备编码 存 AssetInfo uid
     */
    @Column(name = "asset_uid")
    private String assetUid;
    /**
     * apm设备资产名称
     */
    @Column(name = "asset_name")
    private String assetName;
    /**
     * apm设备编号
     */
    @Column(name = "depart_num")
    private String departNum;
    /**
     * APM科室编码
     */
    @Column(name = "ksbm")
    private String ksbm;
    /**
     * APM科室名称
     */
    @Column(name = "ks_name")
    private String KsName;
    /**
     * 创建日期
     */
    @Column(name = "create_date")
    private Date createDate;
    /**
     * 最近一次更新日期
     */
    @Column(name = "update_date")
    private Date updateDate;
    /**
     * 检查人次总和
     */
    @Column(name = "asset_counts",scale = 4)
    private BigDecimal assetCounts;


    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "receipt_num",scale = 4)
    private BigDecimal receiptNum;//项目数量


    @Column(name = "check_people_number",scale = 4)
    private BigDecimal checkPeopleNumber;
}
