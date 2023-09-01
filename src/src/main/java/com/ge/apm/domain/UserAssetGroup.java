/*
authority group combined with org visibility to determine the authorities of the login user
 */
package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author 212547631
 */
@Entity
@Table(name = "user_asset_group")
@Data
@IdClass(UserAssetGroupKey.class)
public class UserAssetGroup implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Column(columnDefinition = "CHAR(32)")
//    @Id
//    private String uid;

    @Id
    @NotNull
    @Column(name = "user_uid", columnDefinition = "CHAR(32)", nullable = false)
    private String userUid;

    @Id
    @NotNull
    @Column(name = "asset_group_uid", nullable = false)
    private String assetGroupUid;

    @Column(name = "classification_path", columnDefinition="ltree")
    private String classificationPath;

//    @PrePersist
//    public void initializeUid() {
//        if (uid == null) {
//            uid = UUID.randomUUID().toString().replace("-", "");
//        }
//
//    }



}
