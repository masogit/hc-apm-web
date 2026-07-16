package com.ge.apm.domain.surgeryAnesthesia;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 手术麻醉源数据计算结果表
 * 用于存储手麻系统接口上传的手术数据经过计算后的数据，目前记录为：匹配设备每天在每个手术室使用时长数据
 */
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_data_result",
        uniqueConstraints = @UniqueConstraint(name = "uk_sadr_org_device_date_room_asset",
                columnNames = {"org_code", "device_code", "surgery_date", "operating_room_name", "asset_uid"}))
@Table(appliesTo = "surgery_anesthesia_data_result", indexes = {
        @Index(name = "idx_sadr_tenant_date", columnNames = {"tenant_uid", "surgery_date"}),
        @Index(name = "idx_sadr_tenant_site_date", columnNames = {"tenant_uid", "site_uid", "surgery_date"}),
        @Index(name = "idx_sadr_asset_date", columnNames = {"asset_uid", "surgery_date"})
})
@Data
public class SurgeryAnesthesiaDataResult implements Serializable {


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
     * 设备代码
     */
    @Column(name = "device_code")
    private String deviceCode;

    /**
     * 手术日期
     */
    @Column(name = "surgery_date")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date surgeryDate;

    /**
     * 手术室名称
     */
    @Column(name = "operating_room_name")
    private String operatingRoomName;

    /**
     * 手术室代码
     */
    @Column(name = "operating_room_code")
    private String operatingRoomCode;

    /**
     * 使用台次
     */
    @Column(name = "usage_count")
    private BigDecimal usageCount;

    /**
     * 占用时长（分钟）
     */
    @Column(name = "usage_duration")
    private BigDecimal usageDuration;

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
     * APM科室名称 (来自 AssetInfo.clinicalDeptName)
     */
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

    /**
     * 租户UID
     */
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    /**
     * 机构UID
     */
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    /**
     * 系统来源
     */
    @Column(name = "source_code")
    private String sourceCode;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 最近一次更新日期
     */
    @Column(name = "update_date")
    private Date updateDate;
}
