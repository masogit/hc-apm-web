package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.domain.simpleparts.SimplePartsWarehouse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    private String id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "model")
    private String model;
    @Column(name = "serial_number")
    private String serialNumber;
    @Column(name = "manufacturer")
    private String manufacturer;
    @Column(name = "supplier")
    private String supplier;
    @Column(name = "unit")
    private String unit;
    @Column(name = "quantity")
    private Integer quantity;
    @Column(name = "unit_price")
    private Double unitPrice;
    @Column(name = "parts_warehouse_id")
    private String partsWarehouseId;
    @Column(name = "status")
    private String status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parts_warehouse_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SimplePartsWarehouse partsWarehouse;
    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;
    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;
    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;
    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
    @Column(name = "clinical_dept_uid", columnDefinition = "CHAR(32)")
    private String clinicalDeptUID;
    @Column(name = "invoice_number")
    private String invoiceNumber;
    @Column(name = "remark")
    private String remark;
    @Column(name = "adapting_devices")
    private String adaptingDevices;
    @Column(name = "effective_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveDate;

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