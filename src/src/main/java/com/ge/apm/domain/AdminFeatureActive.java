package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "admin_feature_active")
public class AdminFeatureActive {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "feature_key", columnDefinition = "CHAR(32)")
    private String featureKey;

    @Column(name = "active_role", columnDefinition = "CHAR(255)")
    private String activeRole;

    @Column(name = "active_user", columnDefinition = "CHAR(64)")
    private String activeUser;

    // 创建日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
    @Column(name = "create_date")
    private Date createDate;

    // 激活日期
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "GMT+8")
    @Column(name = "modify_date")
    private Date activeDate;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;

}
