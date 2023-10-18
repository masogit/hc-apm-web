package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503310146
 */
@Data
@Entity
@Table(name = "reminder_rule_org")
public class ReminderRuleOrg implements Serializable {
    private static final long serialVersionUID = -2986169799914766630L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "org_type", columnDefinition = "CHAR(32)")
    private Integer orgType;

    @Column(name = "org_uid", columnDefinition = "CHAR(32)")
    private String orgUid;

    @Column(name = "rule_uid", columnDefinition = "CHAR(32)")
    private String ruleUid;
}
