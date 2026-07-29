package com.ge.apm.domain.surgeryAnesthesia;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 手麻系统设备表
 * 用于存储手麻系统中的设备信息,与APM台账进行匹配
 */
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_equipment_connect")
@Table(appliesTo = "surgery_anesthesia_equipment_connect", indexes = {
        @Index(name = "idx_saec_org_device", columnNames = {"org_code", "device_code"}),
        @Index(name = "idx_saec_matched", columnNames = "is_matched")
})
@Data
public class SurgeryAnesthesiaEquipmentConnect implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 手麻系统设备名称
     */
    @Column(name = "device_name")
    private String deviceName;


    /**
     * 手麻系统设备代码
     */
    @Column(name = "device_code")
    private String deviceCode;

    /**
     * 机构代码
     */
    @Column(name = "org_code")
    private String orgCode;
    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createdDate;

    /**
     * 是否匹配过,默认是false
     */
    @Column(name = "is_matched")
    private Boolean isMatched;

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

    //excel行数
    @Transient
    private int lineNum;

    public SurgeryAnesthesiaEquipmentConnect() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceCode, deviceName, orgCode);
    }

    public Boolean getMatched() {
        return isMatched;
    }

    public void setMatched(Boolean matched) {
        isMatched = matched;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
}
