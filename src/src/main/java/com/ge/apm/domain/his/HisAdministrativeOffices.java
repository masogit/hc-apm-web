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

    public HisAdministrativeOffices() {
    }

}
