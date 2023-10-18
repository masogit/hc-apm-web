package com.ge.apm.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.domain.reminder.WechatParam;
import com.ge.apm.domain.reminder.rule.RuleParam;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503310146
 */
@Data
@Entity
@Table(name = "reminder_rule")
public class ReminderRule implements Serializable {

    private static final long serialVersionUID = 8492409962179388085L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private Integer state;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "reminder_type")
    private String reminderType;

    @Column(name = "parent_uid")
    private String parentUid;

    @Column(name = "parameters", columnDefinition = "text")
    private String parameters;

    @Transient
    @TableField(exist = false)
    private RuleParam ruleParam = new RuleParam();

    @Transient
    @TableField(exist = false)
    private WechatParam wechatParam = new WechatParam();

    @Column(name = "push_date_unit")
    private Integer pushDateUnit;

}
