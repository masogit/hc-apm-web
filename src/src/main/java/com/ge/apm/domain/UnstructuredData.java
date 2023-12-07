package com.ge.apm.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "unstructured_data")
public class UnstructuredData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @JsonIgnore
    private String id;
//    @JoinColumn(name = "collection_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private UnstructuredDataCollection collection;
    @Column(name = "collection_id")
    @JsonIgnore
    private String collectionId;
    @Column(name = "key")
    private String key;
    @Column(name = "value")
    @JsonIgnore
    private String valueSerialized;
    @Transient
    @JsonProperty
    private Object value;
    @Column(name = "class", columnDefinition = "VARCHAR(64)")
    @JsonIgnore
    private String clazz;

    @Column(name = "name")
    @Size(min = 1, max = 128)
    private String name;//检查项名称

    @Column(name = "remark")
    @Size(max = 1024)
    private String remark;//项目内容

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValueSerialized() {
        return valueSerialized;
    }

    public void setValueSerialized(String valueSerialized) {
        this.valueSerialized = valueSerialized;
    }

    public Object getValue() throws ClassNotFoundException {
        this.value = JSON.parseObject(this.valueSerialized, Class.forName(this.clazz));
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.valueSerialized = (JSON.toJSONString(value));
        String className = this.value == null ? Object.class.getName() : this.value.getClass().getName();
        /* BigDecimal类型按照Double来处理 */
        className = BigDecimal.class.getName().equals(className) ? Double.class.getName() : className;
        this.clazz = className;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
