package com.ge.apm.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2023/10/23
 * @description :
 */
@Entity
@Table(name = "zkb_part_order_close_work_time")
public class ZkbPartOrderCloseWorkTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Getter
    @Setter
    private String id;

    @Getter@Setter
    @Column(name = "close_id")
    private String closeId;

    @Getter@Setter
    @Column(name = "part_order_id")
    private String partOrderId;

    @Getter@Setter
    @Column(name = "part_order_no")
    private String partOrderNo;

    @Getter@Setter
    @Column(name = "sr_no")
    private String srNo;

    @Column(name = "date_type")
    @Getter
    @Setter
    private String dateType;

    @Column(name = "work_dated")
    @Getter
    @Setter
    private Date workDated;

    @Column(name = "start_time", columnDefinition = "Time")
    @Getter
    @Setter
    private Date startTime;

    @Column(name = "end_time")
    @Getter
    @Setter
    private Date endTime;


    @Transient
    @Getter
    @Setter
    private Integer index;

    @Column(name = "create_date")
    @Getter
    @Setter
    private Date createdDate;

}
