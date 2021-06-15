package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "asset_terminate_history")
@Data
public class AssetTerminateHistory {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "asset_uid", nullable = false)
    private String assetUid;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "change_person")   //变更人 报废操作人
    private String changePerson;

    @Column(name = "change_person_uid")   //变更人 报废操作人
    private String changePersonUid;

    @Column(name = "change_time")     //变更时间  报废操作时间
    private Date changeTime;

    @Column(name = "apply_change_department")     //申请变更科室
    private String applyChangeDepartment;

    @Column(name = "apply_change_department_uid")
    private String applyChangeDepartmentUid;

    @Column(name = "apply_time")     //变更时间  报废操作时间
    private Date applyTime;

    @Column(name = "apply_person")     // 申请人
    private String applyPerson;

    @Column(name = "apply_person_uid")     // 申请人
    private String applyPersonUid;
}
