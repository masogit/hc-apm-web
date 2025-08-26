package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "zkb_train_publish_register")
public class ZkbTrainPublishRegister {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "register_date")
    private Date registerDate;

    @Column(name = "pid")
    private String pid;

    @Column(name = "tenant_uid")
    private String tenantUid;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false,name = "register_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<ZkbTrainPublishRegisterDetail> zkbTrainPublishRegisterDetails = new ArrayList<ZkbTrainPublishRegisterDetail>();

    //状态 0:暂存  1：已报名
    @Column(name = "status")
    public Integer status = 0;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable=false, updatable=false, name = "pid", referencedColumnName = "id")
    private ZkbTrainPublishInfo zkbTrainPublishInfo;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZkbTrainPublishRegister)) {
            return false;
        }
        ZkbTrainPublishRegister other = (ZkbTrainPublishRegister) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
