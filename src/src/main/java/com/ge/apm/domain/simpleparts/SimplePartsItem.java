package com.ge.apm.domain.simpleparts;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "simple_parts_item")
@Data
public class SimplePartsItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "name")
    private String name;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "supplier")
    private String supplier;

    @Column(name = "specification")
    private String specification;

    @Column(name = "model")
    private String model;

    @Column(name = "brand")
    private String brand;

    @Column(name = "unit")
    private String unit;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "parts_description")
    private String partsDescription;

    @Column(name = "maximum_quantity")
    private Long maximumQuantity;

    @Column(name = "minimum_quantity")
    private Long minimumQuantity;

    @Transient
    private String partsNameAndSpecification;

    public String getPartsNameAndSpecification(){
        String partsNameAndSpecification = this.name;
        if(StringUtils.isNotEmpty(this.specification)){
            partsNameAndSpecification = this.name + "(" + this.specification + ")";
        }
        return partsNameAndSpecification;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SimplePartsItem)) {
            return false;
        }
        SimplePartsItem other = (SimplePartsItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
