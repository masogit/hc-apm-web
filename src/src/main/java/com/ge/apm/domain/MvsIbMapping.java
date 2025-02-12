package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.NotFound;
import javax.persistence.*;
import java.io.Serializable;  
import java.util.Date;  
  
@Entity
@Data
@Table(name = "mvs_ib_mapping")
public class MvsIbMapping implements Serializable {
  
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ib_id")
    private Integer ibId;
  
    @Column(name = "mvs_ib_id")
    private Integer mvsIbId;
  
    @Column(name = "ib_system_num")
    private String ibSystemNum;

    @Column(name = "ib_system_id")
    private String ibSystemId;
  
    @Column(name = "ib_tenant_uid")
    private String ibTenantUid;

    @Column(name = "ib_tenant_name")
    private String ibTenantName;

    @Temporal(TemporalType.TIMESTAMP)  
    @Column(name = "create_time")
    private Date createTime;  
  
    @Column(name = "mvs_qrcode")
    private String mvsQrcode;  
  
    @Temporal(TemporalType.TIMESTAMP)  
    @Column(name = "modify_time")
    private Date modifyTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "mvs_ib_uid")
    private String mvsIbUid;

    @Column(name = "ib_uid")
    private String ibUid;

    @Column(name = "e_home_ib_uid")
    private String eHomeIbUid;

    @JoinColumn(name = "ib_id", referencedColumnName = "id", insertable = false, updatable = false,foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE) // 没有关联实体时忽略
    @ManyToOne(fetch = FetchType.EAGER)
    private AssetInfo assetInfo;


}