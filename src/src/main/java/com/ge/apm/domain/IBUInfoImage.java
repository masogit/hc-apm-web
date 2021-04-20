package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 503211108  IBU图片信息
 */
@Entity
@Table(name = "ibu_info_image")
public class IBUInfoImage implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    /**
     * IBU Offer
     */
    @Column(name = "ibu_offer")
    private String ibuOffer;

    /**
     * 上传人
     */
    @Column(name = "creator_uid", columnDefinition = "CHAR(32)")
    private String creatorUID;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIbuOffer() {
        return ibuOffer;
    }

    public void setIbuOffer(String ibuOffer) {
        this.ibuOffer = ibuOffer;
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