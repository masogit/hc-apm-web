package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Zkb_agent_train_course_quota")
public class ZkbAgentTrainCourseQuota {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "quota")
    private Integer quota;

    @Column(name = "remark")
    private String remark;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (tenantUid != null ? tenantUid.hashCode() : 0);
        hash += (quota != null ? quota.hashCode() : 0);
        hash += (remark != null ? remark.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbAgentTrainCourseQuota)) {
            return false;
        }
        ZkbAgentTrainCourseQuota other = (ZkbAgentTrainCourseQuota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
