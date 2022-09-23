package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503266962
 */
@Entity
@Data
@Table(name = "system_client_info")
public class SystemClientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @JoinColumn(name = "bind_site_uid", referencedColumnName = "uid")
    @OneToOne
    private OrgInfo orgInfo;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_type")
    private String clientType;

    @Transient
    private String plainPassword;

}
