package com.ge.apm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "table_spare_part")
public class SparePart implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false, unique = true)
    private String serialNumber;
    @Column(nullable = false)
    private String manufacturer;
    @Column(nullable = false)
    private String supplier;
    @Column(nullable = false)
    private String unit;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private double unitPrice;
    @Column(nullable = false)
    private double totalPrice;
    @Column(nullable = false)
    private String warehouse;

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JmReport)) {
            return false;
        }
        SparePart other = (SparePart) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}