package com.ge.apm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author 212579464
 */
@Entity
@Data
@Table(name = "audit_entry_template_rel")
public class AuditEntryTemplateRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private AuditEntryTemplatePK id;

    /**
     * 排序号
     */
    @Column(name = "order")
    private Integer order;

    @Embeddable
    @EqualsAndHashCode
    @Data
    public static class AuditEntryTemplatePK implements Serializable {
        /**
         * 模板uid
         */
        @Column(name = "template_uid")
        private String templateUid;

        /**
         * 条款uid
         */
        @Column(name = "entry_uid")
        private String entryUid;
    }
}
