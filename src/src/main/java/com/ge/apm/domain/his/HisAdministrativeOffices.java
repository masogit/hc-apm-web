package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "his_administrative_offices") //收费项目科室匹配
@Data
public class HisAdministrativeOffices implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    //His科室代码
    @Column(name = "his_ksid")
    private String hisKsid;

    //His科室名称
    @Column(name = "his_ksname")
    private String hisKsname;

    //医院机构代码
    @Column(name = "hos_id")
    private String hosId;

    //是否停用（0在用，1停用）
    @Column(name = "ty_bz")
    private String tyBz;

    @Column(name = "is_matched")//辅助字段，是否匹配过,默认是false
    private Boolean isMatched;

    //excel行数
    @Transient
    private int lineNum;

    public HisAdministrativeOffices() {
    }



    public enum TyBz {
        OPEN("0","OPEN"),
        STOP("1","STOP"),
        ;
        private String key;
        private String value;TyBz(String key, String value) {
            this.key=key;
            this.value=value;
        }


        public static TyBz getTyBz(String key) {
            for (TyBz value : values()) {
                if(value.key.equals(key)){
                    return value;
                }
            }
            return null;
        }
    }
}
