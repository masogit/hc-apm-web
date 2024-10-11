package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "zkb_unreal")
public class ZkbZone  implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "region")
    private String region;

    @Column(name = "zone")
    private String zone;

    @Transient
    private Integer excelIdex;

    @Transient
    private String errorMsg = "";

    public void setErrorMsg(UploadFileErrorTypeEnum type,String errorMsg) {
        this.errorMsg += errorMsg;
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
        if (!(object instanceof ZkbZone)) {
            return false;
        }
        ZkbZone other = (ZkbZone) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.ZkbZone[ id=" + id + " ]";
    }

}
