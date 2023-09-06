/*
authority group combined with org visibility to determine the authorities of the login user
 */
package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 *
 */
@Entity
@Table(name = "asset_type_group",
        indexes = {
                @Index(name = "asset_type_group_id_uk", columnList = "id"),
//                @Index(name = "asset_type_group_classification_path_uk",  columnList = "classification_path"),
//                @Index(name = "asset_type_group_classification_code_uk", columnList = "classification_code"),
                @Index(name = "asset_type_group_encoded_uk",  columnList = "encoded")
        })
@Data
public class AssetTypeGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PATH_SEPARATOR = ".";

    public AssetTypeGroup(){

    }

    public AssetTypeGroup(String name, String parentUid, String classificationCode, String encoded) {
        this.name = name;
        this.parentUid = parentUid;
        this.encoded=encoded;
        this.classificationCode=classificationCode;
    }

    @Id
    @Column(columnDefinition = "CHAR(32)")
    private String uid;

//    @Column(name = "id")
//    private Integer id;

    @Size(max = 32)
    @Column(name = "name")
    private String name;

    @Column(name = "encoded")
    private String encoded;

    @Column(name = "classification_code")
    private String classificationCode;

//    @Column(name = "classification_path", columnDefinition="ltree")
//    private String classificationPath;

    @Column(name = "parent_uid", columnDefinition = "CHAR(32)")
    private String parentUid;

    @JoinColumn(name = "parent_uid", referencedColumnName = "uid", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "sys_auth_group_parent_uid_fk"))
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private AssetTypeGroup parentAuthGroup;


    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Transient
    private boolean accessible;

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

//    @PostPersist
//    public void onSave(){
//        if (null == parentAuthGroup) {
//            this.classificationPath = String.valueOf(this.getId());
//        } else {
//            this.classificationPath = parentAuthGroup.getClassificationPath() + PATH_SEPARATOR + this.getId();
//        }
//    }


    public AssetTypeGroup(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEncoded() {
        return encoded;
    }
}
