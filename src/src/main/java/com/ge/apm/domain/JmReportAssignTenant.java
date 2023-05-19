package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "jm_report_assign_tenant")
public class JmReportAssignTenant implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "jm_report_id", columnDefinition = "CHAR(32)")
    private String jmReportId;

    @Column(name = "assign_tenant", columnDefinition = "CHAR(32)")
    private String assignTenant;

    @Column(name = "assign_role")
    private String assignRole;

    public String getAssignRole() {
        return assignRole;
    }

    public void setAssignRole(String assignRole) {
        this.assignRole = assignRole;
    }

    public String getJmReportId() {
        return jmReportId;
    }

    public void setJmReportId(String jmReportId) {
        this.jmReportId = jmReportId;
    }

    public String getAssignTenant() {
        return assignTenant;
    }

    public void setAssignTenant(String assignTenant) {
        this.assignTenant = assignTenant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof JmReportAssignTenant)) {
            return false;
        }
        JmReportAssignTenant other = (JmReportAssignTenant) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
