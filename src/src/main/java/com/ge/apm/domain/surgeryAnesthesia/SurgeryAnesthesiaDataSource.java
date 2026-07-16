package com.ge.apm.domain.surgeryAnesthesia;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 手术麻醉源数据表
 * 用于存储手麻系统接口上传的手术数据
 */
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_data_source")
@Table(appliesTo = "surgery_anesthesia_data_source", indexes = {
        @Index(name = "idx_sads_src_matched_date", columnNames = {"is_matched", "surgery_date"}),
        @Index(name = "idx_sads_src_org_device", columnNames = {"org_code", "device_code"}),
        @Index(name = "idx_sads_src_tenant_date", columnNames = {"tenant_uid", "surgery_date"})
})
@Data
public class SurgeryAnesthesiaDataSource implements Serializable {

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
     * 计划开始时间
     */
    @Column(name = "scheduled_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date scheduledStartTime;

    /**
     * 手术开始时间
     */
    @Column(name = "surgery_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date surgeryStartTime;

    /**
     * 手术结束时间
     */
    @Column(name = "surgery_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date surgeryEndTime;

    /**
     * 离开手术室时间
     */
    @Column(name = "exit_from_or_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exitFromOrTime;

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
     * 住院号
     */
    @Column(name = "inp_no")
    private String inpNo;

    /**
     * 源数据ID
     */
    @Column(name = "his_id")
    private String hisId;
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
     * 是否匹配过,默认是false
     */
    @Column(name = "is_matched")
    private Boolean isMatched;

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

    //excel行数
    @Transient
    private int lineNum;

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }
}
