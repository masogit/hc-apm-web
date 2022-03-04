package com.ge.apm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503266962
 */
@Entity
@Data
@Table(name = "audit_entry_item_rel")
public class AuditEntryItemRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    /**
     * item uid
     */
    @Column(name = "item_uid")
    private String itemUid;

    /**
     * 条款uid
     */
    @Column(name = "system_entry_uid")
    private String systemEntryUid;

    /**
     * 排序号
     */
    @Column(name = "order_by")
    private Integer orderBy;
}
