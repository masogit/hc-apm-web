/*
authority group combined with org visibility to determine the authorities of the login user
 */
package com.ge.apm.domain;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 */
@Data
public class UserAssetGroupKey implements Serializable {

    private String userUid;

    private String assetGroupUid;

}
