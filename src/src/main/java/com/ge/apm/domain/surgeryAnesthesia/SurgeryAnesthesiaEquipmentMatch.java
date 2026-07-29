package com.ge.apm.domain.surgeryAnesthesia;

import com.alibaba.fastjson.annotation.JSONField;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * 手麻系统设备匹配表
 * 存储APM台账与手麻系统设备的匹配关系
 */
@Entity
@javax.persistence.Table(name = "surgery_anesthesia_equipment_match")
@Table(appliesTo = "surgery_anesthesia_equipment_match", indexes = {
        @Index(name = "idx_saem_org_device", columnNames = {"org_code", "device_code"}),
        @Index(name = "idx_saem_org_device_tenant_site", columnNames = {"org_code", "device_code", "tenant_uid", "site_uid"}),
        @Index(name = "idx_saem_connect_id", columnNames = "connect_id")
})
@Data
public class SurgeryAnesthesiaEquipmentMatch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    /**
     * 手麻系统设备代码 (来自 SurgeryAnesthesiaEquipmentConnect.deviceCode)
     */
    @Column(name = "device_code")
    private String deviceCode;

    /**
     * APM设备编号 (来自 AssetInfo.departNum)
     */
    @Column(name = "depart_num")
    private String departNum;

    /**
     * APM科室UID (来自 AssetInfo.clinicalDeptUID)
     */
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUid;

    /**
     * APM科室名称 (来自 AssetInfo.clinicalDeptName)
     */
    @Column(name = "clinical_dept_name")
    private String clinicalDeptName;

    /**
     * 机构代码 (来自 SurgeryAnesthesiaEquipmentConnect.orgCode)
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * surgery_anesthesia_equipment_connect 表id
     */
    @Column(name = "connect_id")
    private String connectId;

    /**
     * 手麻系统设备名称 (来自 SurgeryAnesthesiaEquipmentConnect.deviceName)
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * APM资产设备UID (来自 AssetInfo.uid)
     */
    @Column(name = "asset_uid")
    private String assetUid;

    /**
     * APM资产设备名称 (来自 AssetInfo.name)
     */
    @Column(name = "asset_name")
    private String assetName;

    /**
     * 创建人
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 修改人
     */
    @Column(name = "modify_by")
    private String modifyBy;

    /**
     * 创建日期
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 修改日期
     */
    @Column(name = "modify_date")
    private Date modifyDate;

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

    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid", referencedColumnName = "uid", insertable = false, updatable = false)
    private AssetInfo assetInfo;

    public SurgeryAnesthesiaEquipmentMatch() {
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SurgeryAnesthesiaEquipmentMatch)) {
            return false;
        }
        SurgeryAnesthesiaEquipmentMatch other = (SurgeryAnesthesiaEquipmentMatch) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }


    @Override
    public String toString() {
        return "SurgeryAnesthesiaEquipmentMatch{" +
                "deviceCode='" + deviceCode + '\'' +
                ", departNum='" + departNum + '\'' +
                ", orgCode=" + orgCode +
                '}';
    }
}
