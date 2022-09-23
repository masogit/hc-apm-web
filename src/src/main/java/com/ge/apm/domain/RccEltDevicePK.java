package com.ge.apm.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503266962
 */
@Data
@Embeddable
public class RccEltDevicePK implements Serializable{

    private static final long serialVersionUID = 1L;

    @Column(name = "aet")
    private String aet;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "org_id")
    private Integer orgId;

    @Column(name = "login_name")
    private String loginName;
}
