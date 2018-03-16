package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "unstructured_data_collection", uniqueConstraints = @UniqueConstraint(columnNames = {"bo_type", "bo_id", "bo_uuid"}))
public class UnstructuredDataCollection implements Serializable {
    
    public enum Status {
        Draft, Archived
    }
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;
    @Column(name = "schema_id")
    private String schemaId;
    @Column(name = "bo_type")
    private String boType;
    @Column(name = "bo_id")
    private Integer boId;
    @Column(name = "bo_uuid", columnDefinition = "CHAR(32)")
    private String boUuid;
    @OneToMany(cascade = ALL, fetch = EAGER)
    @JoinColumn(name = "collection_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @org.hibernate.annotations.ForeignKey(name = "none")
    private List<UnstructuredData> data = new ArrayList<>();

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdDate;
    
    @Column(name = "status")
    private String status = Status.Draft.toString();
    
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public List<UnstructuredData> getData() {
        return data;
    }

    public void setData(List<UnstructuredData> data) {
        this.data = data;
    }

    public String getBoType() {
        return boType;
    }

    public void setBoType(String boType) {
        this.boType = boType;
    }

    public Integer getBoId() {
        return boId;
    }

    public void setBoId(Integer boId) {
        this.boId = boId;
    }

    public String getBoUuid() {
        return boUuid;
    }

    public void setBoUuid(String boUuid) {
        this.boUuid = boUuid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UnstructuredDataCollection)) {
            return false;
        }
        UnstructuredDataCollection other = (UnstructuredDataCollection) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ge.apm.domain.UnstructuredDataCollection[ id=" + id + " ]";
    }
}
