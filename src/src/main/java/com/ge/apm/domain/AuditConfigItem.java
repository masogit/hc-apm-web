package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author 503189829
 * @create 2022/2/23
 * @since 1.0.0
 */

@Entity
@Table(name = "audit_config_item")
@Data
public class AuditConfigItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;
    /**
     * 类型 :1文字(TEXT),2图片(PICTURE)
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 内容类型 :1原文，2概要，3解读, 4得失分点 …
     */
    @Column(name = "content_type")
    private Integer contentType;
    /**
     * 内容
     */
    @Column(name = "content")
    @Size(max = 1000)
    private String content;
    /**
     * 章节号
     */
    @Column(name = "mark")
    private String mark;

    /**
     * 创建人
     */
    @CreatedBy
    @Column(name = "creator", columnDefinition = "VARCHAR(60)")
    private String creator;
    /**
     * 创建日期
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;


}
