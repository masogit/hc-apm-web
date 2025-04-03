package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : 503189362
 * @date : 2021/2/2
 * @description :
 */
@Entity
@Table(name = "zkb_integral_record")
public class ZkbIntegralRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RecordType{
        KpiAdd,KpiModify,KpiDelete,QuotaDeduct,QuotaReturn,ScoreEdit,ContractEdit,ContractImport,ScheduleClear
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "record_type")
    private String recordType;

    @Column(name = "score")
    private Integer score;

    @Column(name = "current_kpi")
    private Integer currentKpi;

    @Column(name = "current_coefficient")
    private Integer currentCoefficient;

    @Column(name = "kpi_month")
    private String kpiMonth;

    @Column(name = "zkb_integral_id")
    private String zkbIntegralId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "remark")
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getCurrentKpi() {
        return currentKpi;
    }

    public void setCurrentKpi(Integer currentKpi) {
        this.currentKpi = currentKpi;
    }

    public Integer getCurrentCoefficient() {
        return currentCoefficient;
    }

    public void setCurrentCoefficient(Integer currentCoefficient) {
        this.currentCoefficient = currentCoefficient;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getZkbIntegralId() {
        return zkbIntegralId;
    }

    public void setZkbIntegralId(String zkbIntegralId) {
        this.zkbIntegralId = zkbIntegralId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getKpiMonth() {
        return kpiMonth;
    }

    public void setKpiMonth(String kpiMonth) {
        this.kpiMonth = kpiMonth;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ZkbIntegralRecord)) {
            return false;
        }
        ZkbIntegralRecord other = (ZkbIntegralRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
