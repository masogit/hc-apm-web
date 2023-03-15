package com.ge.apm.domain;

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
@Table(name = "table_stock_record")
public class StockRecord implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "spare_part_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SparePart sparePart;
    @Column(name = "record_type")
    private Integer recordType;
    @Column(name = "quantity")
    private Integer quantity;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "record_date")
    private Date recordDate;
    @Column(name = "warehouse_in_person")
    private String warehouseOprPerson;
    @Column(name = "warehouse_in_person_uid", columnDefinition = "CHAR(32)")
    private String warehouseOprPersonUid;
    @Column(name = "receiving_department")
    private String receivingDepartment;
    @Column(name = "receiving_department_uid", columnDefinition = "CHAR(32)")
    private String receivingDepartmentUid;
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
        StockRecord other = (StockRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}