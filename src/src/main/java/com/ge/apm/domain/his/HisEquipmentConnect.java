package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "his_equipment_connect")
@Data
public class HisEquipmentConnect implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //LIS、RIS/PACS设备唯一识别编号
    @Column(name = "eq_id")
    private String eqId;

    //联机设备名称
    @Column(name = "eq_name")
    private String eqName;

    //预置值（LIS为0,RIS/PACS 为1）
    @Column(name = "eq_lb")
    private String eqLb;

    //联机医院机构代码
    @Column(name = "eq_hosid")
    private String eqHosid;

    @Column(name = "create_date")
    private Date createdDate; //自己加的

    @Column(name = "is_matched")//是否匹配过,默认是false
    private Boolean isMatched;



    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    //excel行数
    @Transient
    private int lineNum;

    public HisEquipmentConnect() {
    }

    public HisEquipmentConnect(String eqName, String eqLb, String eqHosid) {
        this.eqName = eqName;
        this.eqLb = eqLb;
        this.eqHosid = eqHosid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eqId, eqName, eqLb, eqHosid);
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


    public enum EqLb {
        LIS("0","LIS"),
        RIS("1","RIS/PACS"),
        ;
        private String key;
        private String value;

        EqLb(String key, String value) {
            this.key=key;
            this.value=value;
        }


        public static EqLb getLis(String key) {
            for (EqLb value : values()) {
                if(value.key.equals(key)){
                    return value;
                }
            }
            return null;
        }
    }
}