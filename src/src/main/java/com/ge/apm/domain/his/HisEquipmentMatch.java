package com.ge.apm.domain.his;

import com.alibaba.fastjson.annotation.JSONField;
import com.ge.apm.domain.AssetInfo;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "his_equipment_match")
@Data
public class HisEquipmentMatch implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //LIS、RIS/PACS设备唯一识别编号
    @Column(name = "eq_id")
    private String eqId;

    //APM设备编码 depart_num
    @Column(name = "sbbm")
    private String sbbm;

    @Column(name = "ksbm")
    private String ksbm;

    @Column(name = "ks_name")
    private String ksName;

    //预置值（LIS为0,RIS/PACS 为1）
    @Column(name = "eq_lb")
    private String eqLb;

    //联机医院机构代码
    @Column(name = "eq_hosid")
    private String eqHosid;

    //his_equipment_connect 表id
    @Column(name = "connect_id")
    private String connectId; //

    //联机设备名称
    @Column(name = "eq_name")
    private String eqName;

    //apm资产设备uid
    @Column(name = "asset_uid")
    private String assetUid; //

    //apm资产设备名称
    @Column(name = "asset_name")
    private String assetName; //

    @Column(name = "create_by")
    private String createBy;//创建人

    @Column(name = "modify_by")
    private String modifyBy;//修改人

    @Column(name = "create_date")
    private Date createDate;//创建日期

    @Column(name = "modify_date")
    private Date modifyDate;//修改日期


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;


    @JSONField(serialize = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_uid",referencedColumnName = "uid", insertable = false,updatable = false)
    private AssetInfo assetInfo;


    public HisEquipmentMatch() {
    }

    public HisEquipmentMatch(String sbbm, String eqLb, String eqHosid) {
        this.sbbm = sbbm;
        this.eqLb = eqLb;
        this.eqHosid = eqHosid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HisEquipmentMatch that = (HisEquipmentMatch) o;
        return Objects.equals(eqId, that.eqId) &&
                Objects.equals(sbbm, that.sbbm) &&
                Objects.equals(eqLb, that.eqLb) &&
                Objects.equals(eqHosid, that.eqHosid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eqId, sbbm, eqLb, eqHosid);
    }

    @Override
    public String toString() {
        return "HisEquipmentMatch{" +
                "eqId='" + eqId + '\'' +
                ", sbbm='" + sbbm + '\'' +
                ", eqLb=" + eqLb +
                ", eqHosid=" + eqHosid +
                '}';
    }
}
