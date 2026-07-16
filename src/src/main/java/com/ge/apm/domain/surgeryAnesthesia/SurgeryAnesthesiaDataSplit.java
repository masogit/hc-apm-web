package com.ge.apm.domain.surgeryAnesthesia;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 手麻系统源数据拆分表
 * 记录每条源数据(source)按天拆分后的明细，以及与匹配记录(match)的关联关系
 * 用于取消匹配时精准扣减result表中的汇总数据
 */
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_data_split")
@Table(appliesTo = "surgery_anesthesia_data_split", indexes = {
        @Index(name = "idx_sads_source_id", columnNames = "source_id"),
        @Index(name = "idx_sads_match_id", columnNames = "match_id"),
        @Index(name = "idx_sads_surgery_date", columnNames = "surgery_date"),
        @Index(name = "idx_sads_org_device", columnNames = {"org_code", "device_code"}),
        @Index(name = "idx_sads_cancel_query", columnNames = {"org_code", "match_id", "surgery_date"})
})
@Data
public class SurgeryAnesthesiaDataSplit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    // ==================== 关联ID ====================

    /**
     * 源数据ID (surgery_anesthesia_data_source.id)
     */
    @Column(name = "source_id")
    private String sourceId;

    /**
     * 匹配记录ID (surgery_anesthesia_equipment_match.id)
     */
    @Column(name = "match_id")
    private String matchId;

    // ==================== 拆分维度字段（对应result表聚合键） ====================

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
     * APM设备编码 (AssetInfo uid)
     */
    @Column(name = "asset_uid")
    private String assetUid;

    /**
     * 拆分后的手术日期（对应result表中的surgery_date，即拆到天）
     */
    @Column(name = "surgery_date")
    @Temporal(TemporalType.DATE)
    private Date surgeryDate;

    /**
     * 当天的占用时长（分钟）
     */
    @Column(name = "usage_duration")
    private BigDecimal usageDuration;

    /**
     * 当天的台次权重 (1/有效天数)
     */
    @Column(name = "usage_count")
    private BigDecimal usageCount;

    // ==================== 冗余源数据表字段 (surgery_anesthesia_data_source) ====================

    /**
     * 源数据-手术日期（原始值）
     */
    @Column(name = "source_surgery_date")
    @Temporal(TemporalType.DATE)
    private Date sourceSurgeryDate;

    /**
     * 源数据-计划开始时间
     */
    @Column(name = "scheduled_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date scheduledStartTime;

    /**
     * 源数据-手术开始时间
     */
    @Column(name = "surgery_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date surgeryStartTime;

    /**
     * 源数据-手术结束时间
     */
    @Column(name = "surgery_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date surgeryEndTime;

    /**
     * 源数据-离开手术室时间
     */
    @Column(name = "exit_from_or_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exitFromOrTime;

    /**
     * 源数据-住院号
     */
    @Column(name = "inp_no")
    private String inpNo;

    /**
     * 源数据-源数据ID（HIS系统ID）
     */
    @Column(name = "his_id")
    private String hisId;

    /**
     * 源数据-系统来源
     */
    @Column(name = "source_code")
    private String sourceCode;

    // ==================== 冗余匹配表字段 (surgery_anesthesia_equipment_match) ====================

    /**
     * 匹配-手麻系统设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 匹配-APM设备编号
     */
    @Column(name = "depart_num")
    private String departNum;

    /**
     * 匹配-APM科室名称
     */
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

    /**
     * 匹配-APM资产设备名称
     */
    @Column(name = "asset_name")
    private String assetName;

    /**
     * 匹配-surgery_anesthesia_equipment_connect表id
     */
    @Column(name = "connect_id")
    private String connectId;

    // ==================== 通用字段 ====================

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
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;
}
