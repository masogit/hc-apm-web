package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503266962
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
     * 操作人
     */
    @Column(name = "operator")
    private String operator;

    @Column(name = "site_uid")
    private String siteUid;

    /**
     * 内容(json for needed data)
     */
    @Column(name = "content", columnDefinition = "VARCHAR(1000)")
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
