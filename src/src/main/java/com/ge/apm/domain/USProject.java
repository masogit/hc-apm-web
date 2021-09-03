package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by 503218583  US方案
 */
@Entity
@Table(name = "us_project")
public class USProject implements Serializable {

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
     *  listPrice
     */
    @Column(name = "list_price")
    private String listPrice;

    /**
     * 描述
     */
    @Column(name = "descption", columnDefinition = "text")
    private String descption;

    /**
     * pdf
     */
    @Column(name = "pdf")
    private String pdf;

    /**
     * psi_description
     */
    @Column(name = "psi_description")
    private String psiDescription;

    public String getPsiDescription() {
        return psiDescription;
    }

    public void setPsiDescription(String psiDescription) {
        this.psiDescription = psiDescription;
    }

    /**
     * 创建人
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getDescption() {
        return descption;
    }

    public void setDescption(String descption) {
        this.descption = descption;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
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
