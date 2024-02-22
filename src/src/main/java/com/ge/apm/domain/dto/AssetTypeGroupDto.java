/*
authority group combined with org visibility to determine the authorities of the login user
 */
package com.ge.apm.domain.dto;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 *
 */
@Data
public class AssetTypeGroupDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PATH_SEPARATOR = ".";

    public AssetTypeGroupDto(){

    }

    public AssetTypeGroupDto(String name, String parentUid, String classificationCode, String encoded) {
        this.name = name;
        this.parentUid = parentUid;
        this.encoded=encoded;
        this.classificationCode=classificationCode;
    }


    private String uid;

//    @Column(name = "id")
//    private Integer id;


    private String name;

    private String encoded;

    private String classificationCode;

//    @Column(name = "classification_path", columnDefinition="ltree")
//    private String classificationPath;

    private String parentUid;


    private AssetTypeGroupDto parentAuthGroup;


    private String tenantUid;

    private String userUid;

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


    public AssetTypeGroupDto(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEncoded() {
        return encoded;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (uid != null ? uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AssetTypeGroupDto)) {
            return false;
        }
        AssetTypeGroupDto other = (AssetTypeGroupDto) object;
        return (this.uid != null || other.uid == null) && (this.uid == null || this.uid.equals(other.uid));
    }
}
