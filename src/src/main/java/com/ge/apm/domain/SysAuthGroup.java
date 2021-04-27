/*
authority group combined with org visibility to determine the authorities of the login user
 */
package com.ge.apm.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "sys_auth_group")
@Data
public class SysAuthGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "CHAR(32)")
    private String uid;

    @Size(max = 32)
    @Column(name = "name")
    private String name;

//    @Column(name = "encoded")
//    private String encoded;

    @Column(name = "classification_code")
    private String classificationCode;

    @Column(name = "parent_uid", columnDefinition = "VARCHAR(32)")
    private String parentUid;

    @Transient
    private boolean accessible;

    public SysAuthGroup() {
    }

    public SysAuthGroup(String name) {
        this.name = name;
    }

    @PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }
    }

    public String getDisplayName() {
        if (StringUtils.isEmpty(classificationCode)) {
            return name;
        } else {
            return  classificationCode + " " + name;
        }
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

//    public String getEncoded() {
//        return encoded;
//    }
}
