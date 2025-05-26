package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="trigger_record")
@Data
public class TriggerRecord implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    /**
     *  门id
     */
    @Column(name = "door_id")
    private String doorId;
    /**
     *  设备rfId
     */
    @Column(name = "rf_id")
    private String rfId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_time")
    private Date dateTime;

    /**
     *  方向 入库、出库
     */
    @Column(name = "direction")
    private String direction;
    /**
     *  状态：
     *     0：发送失败
     *     1：发送成功
     *     2：已重新发送
     */
    @Column(name="status")
    private Integer status;


    @Column(name="sync")
    private Boolean sync;

    /**
     *  内容
     */
    @Column(name = "context",columnDefinition = "text")
    private String context;

    @Column(name = "account")
    private String account;

    /**
     *  创建时间
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    private Date createdDate;

    /**
     *  修改时间
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;



}
