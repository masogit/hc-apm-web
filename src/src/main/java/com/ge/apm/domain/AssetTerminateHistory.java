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
    private String uid;

    @Column(name = "asset_uid", nullable = false)
    private String assetUid;

    @Column(name = "asset_name")
    private String assetName;

    @Column(name = "change_person")   //变更人
    private String changePerson;

    @Column(name = "change_time")     //变更时间
    private Date changeTime;

    @Column(name = "apply_change_department")     //申请变更科室
    private String applyChangeDepartment;

    @Column(name = "audit_trail")      // 审计追踪
    private String auditTrail;
}
