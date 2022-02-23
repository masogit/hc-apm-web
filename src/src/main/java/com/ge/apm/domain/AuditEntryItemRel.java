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
@Table(name = "audit_entry_item_rel")
public class AuditEntryItemRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private AuditEntryTemplatePK id;

    @Embeddable
    @EqualsAndHashCode
    @Data
    public static class AuditEntryTemplatePK implements Serializable {
        /**
         * 模板uid
         */
        @Column(name = "item_uid")
        private String itemUid;

        /**
         * 条款uid
         */
        @Column(name = "entry_uid")
        private String entryUid;
    }
}
