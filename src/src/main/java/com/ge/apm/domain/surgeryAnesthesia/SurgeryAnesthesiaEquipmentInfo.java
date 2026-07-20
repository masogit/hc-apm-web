package com.ge.apm.domain.surgeryAnesthesia;

import com.ge.apm.domain.EfficiencyEquipmentInfo;
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
 * 手术室绩效设备配置表
 * 用于存储手术室绩效分析中的设备信息及阳性率设置
 */
@Data
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_equipment_info",
        uniqueConstraints = @UniqueConstraint(name = "uk_saei_tenant_asset", columnNames = {"tenant_uid", "asset_uid"}))
@Table(appliesTo = "surgery_anesthesia_equipment_info", indexes = {
        @Index(name = "idx_saei_tenant_uid", columnNames = "tenant_uid"),
        @Index(name = "idx_saei_tenant_site", columnNames = {"tenant_uid", "site_uid"}),
        @Index(name = "idx_saei_asset_uid", columnNames = "asset_uid")
})
public class SurgeryAnesthesiaEquipmentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    private String uid;

    /**
     * APM设备UID
     */
    @Column(name = "asset_uid", columnDefinition = "CHAR(32)")
    private String assetUid;

    /**
     * 设备名称
     */
    @Column(name = "asset_name")
    private String assetName;

    /**
     * 展示名称
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * 设备型号
     */
    @Column(name = "function_type")
    private String functionType;

    /**
     * 资产编号
     */
    @Column(name = "depart_num")
    private String departNum;

    /**
     * 制造商序列号
     */
    @Column(name = "serial_num")
    private String serialNum;

    /**
     * 二维码
     */
    @Column(name = "qr_code")
    private String qrCode;

    /**
     * 阳性率(%)
     */
    @Column(name = "positive_rate")
    private BigDecimal positiveRate;

    @Column(name = "report_config", columnDefinition = "TEXT")
    private String reportConfig;

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
     * 创建人UID
     */
    @Column(name = "created_by_uid")
    private String createdByUid;

    /**
     * 创建日期
     */
    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    // ========== 以下为Transient字段，用于页面展示 ==========

    /**
     * 品牌/制造商简称
     */
    @Transient
    private String makerShortName;

    /**
     * 所属科室
     */
    @Transient
    private String clinicalDeptName;

    /**
     * 设备分类
     */
    @Transient
    private Integer assetGroup;

    /**
     * 设备分类名称
     */
    @Transient
    private String assetGroupName;

    /**
     * 使用总台数（手麻源数据统计）
     */
    @Transient
    private Integer totalUsageCount;

    /**
     * 占用总时长（天）（手麻源数据统计）
     */
    @Transient
    private BigDecimal totalOccupationDays;

    /**
     * 手术室覆盖数（手麻源数据统计）
     */
    @Transient
    private Integer operatingRoomCoverageCount;




    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SurgeryAnesthesiaEquipmentInfo)) {
            return false;
        }
        SurgeryAnesthesiaEquipmentInfo other = (SurgeryAnesthesiaEquipmentInfo) object;
        return (this.uid != null || other.uid == null) && (this.uid == null || this.uid.equals(other.uid));
    }

}