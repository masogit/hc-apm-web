package com.ge.apm.domain;


import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "msg_tenant")
@Cacheable(true)
public class MsgTenant implements Serializable {


    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;


    @Column(name = "tenant_uid")
    private String tenantUid;

    @Column(name = "msgconfig_Id")
    private Integer msgConfigId;

   /* @JoinColumn(name = "msgconfig_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
    @org.hibernate.annotations.ForeignKey(name = "none")
    @NotFound(action= NotFoundAction.IGNORE)
    private MsgConfig msgConfig;*/

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    public Integer getMsgConfigId() {
        return msgConfigId;
    }

    public void setMsgConfigId(Integer msgConfigId) {
        this.msgConfigId = msgConfigId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
