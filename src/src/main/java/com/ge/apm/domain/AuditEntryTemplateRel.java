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
@Table(name = "audit_entry_template_rel")
public class AuditEntryTemplateRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 模板uid
     */
    @Column(name = "template_uid")
    private String templateUid;

    /**
     * 条款uid(系统或用户的条款uid)
     */
    @Column(name = "entry_uid")
    private String entryUid;

    /**
     * 排序号
     */
    @Column(name = "order_by")
    private Integer orderBy;
}
