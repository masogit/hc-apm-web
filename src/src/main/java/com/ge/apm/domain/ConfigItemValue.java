package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 503266962
 */

@Entity
@Table(name = "config_item_value")
@Data
public class ConfigItemValue implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * config_item.key
     */
    @Column(name = "key")
    private String key;

    @Column(name = "value")
    @Type(type = "text")
    private String value;

    @Column(name = "custom_value")
    @Type(type = "text")
    private String customValue;    //院区自定义添加类型 枚举 等

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUid;

    @Column(name = "institution_uid", columnDefinition = "CHAR(32)")
    private String institutionUid;

    @Column(name = "hospital_uid", columnDefinition = "CHAR(32)")
    private String hospitalUid;

    @Column(name = "site_uid", columnDefinition = "CHAR(32)")
    private String siteUid;
}
