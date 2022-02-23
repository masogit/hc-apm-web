package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author 212579464
 */
@Entity
@Data
@Table(name = "audit_template")
public class AudiTtemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 类型：1-text 2-picture
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 内容类型
     */
    @Column(name = "content_type")
    private Integer contentType;
}
