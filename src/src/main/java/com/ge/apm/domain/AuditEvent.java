package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 212579464
 */
@Entity
@Data
@Table(name = "audit_event")
public class AuditEvent implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 事件类型
     */
    @Column(name = "event_type")
    private String eventType;

    /**
     * 事件时间
     */
    @Column(name = "event_time")
    private Date eventTime;

    /**
     * 操作
     */
    @Column(name = "operator")
    private String operator;

    @Column(name = "tenant_uid")
    private String tenantUid;

    /**
     * 内容(json for needed data)
     */
    @Column(name = "content")
    private String content;

    /**
     * 事件对象id
     */
    @Column(name = "event_object_uid")
    private String eventObjectUid;

    /**
     * 结果
     */
    @Column(name = "result")
    private String result;
}
