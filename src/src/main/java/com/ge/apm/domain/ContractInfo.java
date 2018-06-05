/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author 212579464
 */
@Entity
@Table(name = "contract_info")
public class ContractInfo implements Serializable {

    public enum ContractType { Normal, Easy }
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    // 合同名称
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    // 合同编号    
    @Column(name = "contract_num")
    private String contractNum;
    
    //开始时间
    @Basic(optional = true)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    //结束时间
    @Basic(optional = true)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    //合同金额
    @Basic(optional = true)
    @Column(name = "amount")
    private double amount;
    
    //供应商
    @Column(name = "supplier_uid", columnDefinition = "CHAR(32)")
    private String supplierUid;
    
    //合同类型
    @Column(name = "contract_type")
    private String contractType;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUID;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUID;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUID;
}
