package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503310146
 */
@Data
@Entity
@Table(name = "reminder_event")
public class ReminderEvent implements Serializable {

    private static final long serialVersionUID = 1978571635587839518L;

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

    @Column(name = "bo_uuid")
    private String boUuid;

    @Column(name = "name")
    private String name;

    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "status")
    private String status;

    @Column(name = "finish_time")
    private Date finishTime;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "res_data")
    private String resData;

    @Column(name = "type")
    private String type;

    @Column(name = "reminder_type")
    private String reminderType;

}
