package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "train_detail_sign")
public class TrainDetailSign {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "detail_id")
    private String detailId;

    @Column(name = "sign_order")
    private Integer signOrder;

    @Column(name = "sign_dept_uid")
    private String signDeptUid;

    @Column(name = "sign_dept_name")
    private String signDeptName;

    @Column(name = "signer_uid")
    private String signerUid;

    @Column(name = "signer_name")
    private String signerName;

    @Column(name = "sign_date")
    private Date signDate;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrainDetailSign)) {
            return false;
        }
        TrainDetailSign other = (TrainDetailSign) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
