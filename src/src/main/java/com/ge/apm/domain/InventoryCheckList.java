package com.ge.apm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by 212605082 on 2017/8/2.
 */
@Entity
@Table(name = "inventory_check_list")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class InventoryCheckList implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "item")
    private String item;

//    @ManyToMany(targetEntity = InventoryIssue.class, mappedBy = "inventoryCheckLists", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
//    private Set<InventoryIssue> inventoryIssues = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

//    public Set<InventoryIssue> getInventoryIssues() {
//        return inventoryIssues;
//    }
//
//    public void setInventoryIssues(Set<InventoryIssue> inventoryIssues) {
//        this.inventoryIssues = inventoryIssues;
//    }
}
