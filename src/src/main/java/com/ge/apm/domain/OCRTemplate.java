package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ocr_template")
@Data
public class OCRTemplate implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "remark")
    private String remark;

    @Column(name = "width", columnDefinition = "int")
    private Integer width = 0;

    @Column(name = "height", columnDefinition = "int")
    private Integer height = 0;

    @Column(name = "json_text",columnDefinition = "text")
    private String jsonText;

    @Column(name = "tenant_uid", columnDefinition = "CHAR(32)")
    private String tenantUID;

    @Column(name = "created_by", nullable = true, length = 50)
    private String createdBy;

    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @Column(name = "last_modified_by", nullable = true, length = 50)
    private String lastModifiedBy;

    @Column(name = "last_modified_date", nullable = true)
    private Date lastModifiedDate;
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OCRTemplate)) {
            return false;
        }
        OCRTemplate other = (OCRTemplate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
