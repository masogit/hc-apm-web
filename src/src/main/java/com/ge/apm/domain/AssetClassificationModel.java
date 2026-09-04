package com.ge.apm.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "asset_classification_model")
@Data
public class AssetClassificationModel implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;
    @Column(name = "source_name")
    private String sourceName;  //设备名称
    @Column(name = "factory")
    private String factory;     //制造商
    @Column(name = "pattern",columnDefinition = "VARCHAR(2048)")
    private String pattern;    //设备型号
    @Column(name = "brand")
    private String brand;    //品牌/制造商简称
    @Column(name = "category")
    private String category;   // 分类

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createdDate;

    @Column(name = "created_name")
    private String createdName;
    @Column(name = "created_uid")
    private String createdUid;

    @Size(max = 32)
    @Column(name = "tenant_uid")
    private String tenantUid;

    // ── 以下为 ONNX FULL 模型推理所需的扩展字段（不持久化，仅用于请求传递） ──
    @Transient
    private String department;       // 所属科室
    @Transient
    private String riskLevel;        // 风险等级
    @Transient
    private String meterLevel;       // 计量等级
    @Transient
    private String registrationNo;   // 注册证号
    @Transient
    private String isEmergencyDevice; // 是否急救设备
    @Transient
    private String purchasePrice;    // 采购价格

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTenantUid() {
        return tenantUid;
    }

    public void setTenantUid(String tenantUid) {
        this.tenantUid = tenantUid;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssetClassificationModel)) {
            return false;
        }
        AssetClassificationModel other = (AssetClassificationModel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "com.ge.apm.domain.AssetClassificationModel[ id=" + id + " ]";
    }
}
