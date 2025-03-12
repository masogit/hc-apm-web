package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Data
@Table(name = "mvs_transaction_work_order")
public class EHomeTransactionWorkOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ucm_id")
    private String ucmId;

    @Column(name = "sr_id")
    private String srId;

    @Column(name = "sr_num")
    private String srNum;

    @Column(name = "total_man_hour")
    private Double totalManHour;

    @Column(name = "pat_problems", length = 500)
    private String patProblems;

    @Column(name = "pat_actions", length = 500)
    private String patActions;

    @Column(name = "pat_tests", length = 500)
    private String patTests;

    @Column(name = "status")
    private String status;

    @Column(name = "current_person_name")
    private String currentPersonName;

    @Column(name = "type_key")
    private String typeKey;

    @Column(name = "parent_key")
    private String parentKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "close_time")
    private Date closeTime;

    @Column(name = "comment")
    private String comment;

    @Column(name = "sr_context", columnDefinition = "TEXT")
    private String srContext;

    @Column(name = "wo_context", columnDefinition = "TEXT")
    private String woContext;

    @Column(name = "attachments_context", columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "parts_context", columnDefinition = "TEXT")
    private String parts;

    @Column(name = "hours_context", columnDefinition = "TEXT")
    private String hours;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date modifiedDate;

    @Transient
    private String courseName;
}
