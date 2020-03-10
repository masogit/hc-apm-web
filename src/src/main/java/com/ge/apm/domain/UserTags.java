/*
 */
package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "user_tags")
@Data
public class UserTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String uid;

    @Column(name = "user_uid", columnDefinition = "CHAR(32)", nullable = false)
    @org.hibernate.annotations.Index(name="idx_user_uid", columnNames = {"user_uid"})
    private String userUid;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)", nullable = false)
    @org.hibernate.annotations.Index(name="idx_tenant_uid", columnNames = {"tenant_uid"})
    private String tenantUID;

    @NotNull
    @Column(name = "tag", nullable = false)
    private String tag;

    @PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }

    }

}
