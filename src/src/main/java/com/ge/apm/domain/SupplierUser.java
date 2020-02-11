package com.ge.apm.domain;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "supplier_user")
public class SupplierUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        PendingApproval, Approved, NotApproved
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "status")
    private String status;

    @Column(name = "supplier_id")
    private Integer supplierID;

    @Column(name = "supplier_uid")
    private String supplierUID;

    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "user_uid")
    private String userUID;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "user_id", referencedColumnName = "id")
    private UserAccount userAccount;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(Integer supplierID) {
        this.supplierID = supplierID;
    }

    public String getSupplierUID() {
        return supplierUID;
    }

    public void setSupplierUID(String supplierUID) {
        this.supplierUID = supplierUID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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
        if (!(object instanceof SupplierUser)) {
            return false;
        }
        SupplierUser other = (SupplierUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
