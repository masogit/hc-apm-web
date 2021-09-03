package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 503218583  US方案 升级   关系表
 */
@Entity
@Table(name = "us_pro_up")
public class USProUp implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    /**
     * 方案名称
     */
    @Column(name = "name")
    private String name;

    /**
     *  psiDescription
     */
    @Column(name = "psi_description")
    private String psiDescription;

    /**
     * 创建人
     */
    @Column(name = "creator_uid", columnDefinition = "CHAR(32)")
    private String creatorUID;

    public String getPsiDescription() {
        return psiDescription;
    }

    public void setPsiDescription(String psiDescription) {
        this.psiDescription = psiDescription;
    }

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatorUID() {
        return creatorUID;
    }

    public void setCreatorUID(String creatorUID) {
        this.creatorUID = creatorUID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
