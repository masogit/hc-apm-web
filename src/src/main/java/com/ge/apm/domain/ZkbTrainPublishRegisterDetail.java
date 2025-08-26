package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "zkb_train_publish_register_detail")
public class ZkbTrainPublishRegisterDetail {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "register_id")
    private String registerId;

    @Column(name = "engineer_id")
    private String engineerId;

    @Column(name = "engineer_name")
    private String engineerName;

    @Column(name = "engineer_phone")
    private String engineerPhone;
    @Column(name = "engineer_email")
    private String engineerEmail;
    @Column(name = "engineer_age")
    private Integer engineerAge;
    @Column(name = "engineer_sso")
    private String engineerSso;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Transient
    List<V2_BlobObject> v2_blobObjectList;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbTrainPublishRegisterDetail)) {
            return false;
        }
        ZkbTrainPublishRegisterDetail other = (ZkbTrainPublishRegisterDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
