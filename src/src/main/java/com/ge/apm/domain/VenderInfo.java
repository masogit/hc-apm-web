package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author lyj
 */
@Entity
@Table(name = "vender_info")
public class VenderInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(32)")
    private String id;

    //
    @Column(name = "vender_name")
    private String venderName;

    @Column(name = "vender_address")
    private String venderAddress;

    @Column(name = "vender_phone")
    private String venderPhone;

    @Column(name = "vender_contacts")
    private String venderContacts;

    @Column(name = "opend_id")
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenderName() {
        return venderName;
    }

    public void setVenderName(String venderName) {
        this.venderName = venderName;
    }

    public String getVenderAddress() {
        return venderAddress;
    }

    public void setVenderAddress(String venderAddress) {
        this.venderAddress = venderAddress;
    }

    public String getVenderPhone() {
        return venderPhone;
    }

    public void setVenderPhone(String venderPhone) {
        this.venderPhone = venderPhone;
    }

    public String getVenderContacts() {
        return venderContacts;
    }

    public void setVenderContacts(String venderContacts) {
        this.venderContacts = venderContacts;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VenderInfo)) {
            return false;
        }
        VenderInfo other = (VenderInfo) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }
}
