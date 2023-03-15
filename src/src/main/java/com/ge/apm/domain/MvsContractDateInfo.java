package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "mvs_contract_date_info")
@Data
public class MvsContractDateInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "svc_offering")
    private String SVCOffering;

    @Column(name = "package_no")
    private String packageNo;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "dealer_name")
    private String dealerName;

    @Column(name = "status")
    private String status;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "oc_status")
    private String OCStatus;

    @Column(name = "oc_submit_date")
    private Date OCSubmitDate;

    @Column(name = "order_change_date")
    private Date orderChangeDate;//批准时间

    @Column(name = "oc_svc_start_date")
    private Date OCSvcStartDate;

    @Column(name = "oc_svc_end_date")
    private Date OCSvcEndDate;

    @Transient
    private boolean pass;

    @Transient
    private String checkResult;

}
