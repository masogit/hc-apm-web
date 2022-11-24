package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503310146
 */
@Data
@Entity
@Table(name = "permission_group_org")
public class PermissionGroupOrg implements Serializable {

    private static final long serialVersionUID = -5824153180797447236L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "org_type")
    private Integer orgType;

    @Column(name = "org_uid")
    private String orgUid;

    @Column(name = "permission_group_uid")
    private String permissionGroupUid;

}
