package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "adverse_events_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@Data
@EntityListeners(AuditingEntityListener.class)
public class AdverseEventsFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uid", updatable = false)
    private String uid;

    /**
     * 不良事件uid
     */
    @Column(name = "adverse_events_uid")
    private String adverseEventsUid;

    /**
     * 是否有效
     */
    @Column(name = "is_valid")
    private Boolean isValid;

    /**
     * 文件id
     */
    @Column(name = "object_id")
    @Size(min = 1, max = 100)
    private String objectId;

    /**
     * 文件名称
     */
    @Column(name = "object_name")
    @Size(min = 1, max = 100)
    private String objectName;

    /**
     * 文件类型 img,doc等等
     */
    @Column(name = "object_type")
    @Size(min = 1, max = 100)
    private String objectType;

    /**
     * 文件大小
     */
    @Column(name = "object_size")
    private Long objectSize;

    /**
     * 文件类型 业务类型
     */
    @Column(name = "file_type")
    private Integer fileType;

    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date")
    @JsonIgnore
    private Date createdDate = new Date();

    /*@PrePersist
    public void initializeUid() {
        if (uid == null) {
            uid = UUID.randomUUID().toString().replace("-", "");
        }
    }*/
}
