package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503266962
 */
@Entity
@Data
@Table(name = "audit_target")
public class AuditTarget implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 系统标准uid
     */
    @Column(name = "system_case_uid")
    private String systemCaseUid;

    /**
     * 目标(三甲、二甲...)
     */
    @Column(name = "target")
    private Integer target;

    /**
     * 目标分数
     */
    @Column(name = "score")
    private Double score;
}
