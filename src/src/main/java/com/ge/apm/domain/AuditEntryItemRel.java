package com.ge.apm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

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
