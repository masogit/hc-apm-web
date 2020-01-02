package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 212605082 on 2019/12/17.
 */
@Entity
@Table(name = "zkb_engineer_track_record")
public class ZkbEngineerTrackRecord {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "zkb_engineer_id")
    private String zkbEngineerId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "position")
    private String position;

    @Column(name = "hire_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hireDate;

    @Column(name = "work_seniority")
    private Integer workSeniority;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZkbEngineerId() {
        return zkbEngineerId;
    }

    public void setZkbEngineerId(String zkbEngineerId) {
        this.zkbEngineerId = zkbEngineerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Integer getWorkSeniority() {
        return workSeniority;
    }

    public void setWorkSeniority(Integer workSeniority) {
        this.workSeniority = workSeniority;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbContactPerson)) {
            return false;
        }
        ZkbEngineerTrackRecord other = (ZkbEngineerTrackRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbEngineerTrackRecord[ id=" + id + " ]";
    }
}
