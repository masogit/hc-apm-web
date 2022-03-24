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
@Table(name = "audit_uploaded_file")
public class AuditUploadedFile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    /**
     * 条款uid
     */
    @Column(name = "entry_uid")
    private String entryUid;

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
