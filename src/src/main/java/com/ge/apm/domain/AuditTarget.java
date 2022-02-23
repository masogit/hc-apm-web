package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author 212579464
 */
@Entity
@Data
@Table(name = "audit_template")
public class AuditTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 模板名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 章节编号
     */
    @Column(name = "mark")
    private String mark;

    /**
     * 类型：1-用户 2-系统
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 文件名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件大小
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件uid
     */
    @Column(name = "object_uid")
    private String objectUId;

    /**
     * 上传时间
     */
    @Column(name = "upload_time")
    private Date uploadTime;

    /**
     * 上传人
     */
    @Column(name = "uploader")
    private String uploader;
}
