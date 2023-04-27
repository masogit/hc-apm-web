/*
 */
package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
@Entity
@Table(name = "user_account_history")
@Data
public class UserAccountHistory implements Serializable {

    private static final long serialVersionUID = 5124885951264607325L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "login_name")
    private String loginName;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "is_locked")
    private String isLocked;

    @Column(name = "user_roles")
    private String userRoles;

    @Column(name = "position")
    private String position;

    @Column(name = "user_tags")
    private String userTags;

    @Column(name = "comment")
    private String comment;

    @Column(name = "hospital_uid")
    private String hospitalUID;

    @Column(name = "institution_uid")
    private String institutionUID;

    @Column(name = "site_uid")
    private String siteUID;

    @Column(name = "tenant_uid")
    private String tenantUID;

    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "json_data", columnDefinition = "text")
    private String jsonData;

    @Column(name = "user_json", columnDefinition = "text")
    private String userJson;
}
