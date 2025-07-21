package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "supplier_contact_person")
public class SupplierContactPerson implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "supplier_id")
    private Integer supplierId;

    @Column(name = "supplier_uid")
    private String supplierUid;

    @Column(name = "vender_company_id")
    private String venderCompanyId;

    @Column(name = "name")
    private String name;

    @Column(name = "tel")
    private String tel;

    @Column(name = "city")
    private String city;

    @Column(name = "type")
    private String type;

    @Column(name = "supplier_t")
    private Boolean supplierT = Boolean.FALSE;

    @Column(name = "maintainer_t")
    private Boolean maintainerT = Boolean.FALSE;

    @Column(name = "manufacturer_t")
    private Boolean manufacturerT = Boolean.FALSE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierUid() {
        return supplierUid;
    }

    public void setSupplierUid(String supplierUid) {
        this.supplierUid = supplierUid;
    }

    public String getVenderCompanyId() {
        return venderCompanyId;
    }

    public void setVenderCompanyId(String venderCompanyId) {
        this.venderCompanyId = venderCompanyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSupplierT() {
        return supplierT;
    }

    public void setSupplierT(Boolean supplierT) {
        this.supplierT = supplierT;
    }

    public Boolean getMaintainerT() {
        return maintainerT;
    }

    public void setMaintainerT(Boolean maintainerT) {
        this.maintainerT = maintainerT;
    }

    public Boolean getManufacturerT() {
        return manufacturerT;
    }

    public void setManufacturerT(Boolean manufacturerT) {
        this.manufacturerT = manufacturerT;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SupplierContactPerson)) {
            return false;
        }
        SupplierContactPerson other = (SupplierContactPerson) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
