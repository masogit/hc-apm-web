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
@Table(name = "permission_group_user")
public class PermissionGroupUser implements Serializable {

    private static final long serialVersionUID = -5824153180797447236L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid", columnDefinition = "CHAR(32)")
    private String uid;

    @Column(name = "user_uid")
    private String userUid;

    @Column(name = "permission_group_uid")
    private String permissionGroupUid;

}
