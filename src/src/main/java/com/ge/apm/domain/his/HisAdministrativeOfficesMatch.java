package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "his_administrative_offices_match")
@Data
public class HisAdministrativeOfficesMatch implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "his_adm_id")
    private String hisAdmId; // 联机表主键

    @Column(name = "his_ksid")//His科室代码
    private String hisKsid;

    @Column(name = "name_py_code")//APM科室代码
    private String namePyCode;

    @Column(name = "his_ksname")
    private String hisKsname;//His科室名称

    @Column(name = "org_name")
    private String  orgName;//APM科室名称

    @Column(name = "hos_id")
    private String hosId;

    @Column(name = "org_info_uid")
    private String orgInfoUid;

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

    public HisAdministrativeOfficesMatch() {
    }

}
