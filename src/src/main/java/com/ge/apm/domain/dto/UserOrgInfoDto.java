/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.dto;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author 212579464
 */

    

public class UserOrgInfoDto implements Serializable{

    private static final long serialVersionUID = 1L;


    private String id;
    
    private String userUid;
    private String orgUid;
    private Integer orgType;

    private String orgInfoUid;

    public String getOrgInfoUid() {
        return orgInfoUid;
    }

    public void setOrgInfoUid(String orgInfoUid) {
        this.orgInfoUid = orgInfoUid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getOrgUid() {
        return orgUid;
    }

    public void setOrgUid(String orgUid) {
        this.orgUid = orgUid;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserOrgInfoDto)) {
            return false;
        }
        UserOrgInfoDto other = (UserOrgInfoDto) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "UserOrgInfo[ id=" + id + " ]";
    }
    
}
