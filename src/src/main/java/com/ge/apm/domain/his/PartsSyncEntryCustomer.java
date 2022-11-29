package com.ge.apm.domain.his;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 同步环球的客户表，备件同步到inca的时候需要customer id
 */
@Data
@Entity
@Table(name = "parts_sync_entry_customer")
public class PartsSyncEntryCustomer implements Serializable {

    private static final long serialVersionUID = -3219107586626148069L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "entry_customer_id")
    private int entryCustomerId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "entry_id")
    private int entryId;

    @Column(name = "entry_name")
    private String entryName;

}
