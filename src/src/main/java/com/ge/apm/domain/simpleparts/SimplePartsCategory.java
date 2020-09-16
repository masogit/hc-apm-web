package com.ge.apm.domain.simpleparts;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "simple_parts_category")
@Data
public class SimplePartsCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @Size(max = 32)
    @Column(name = "name")
    private String name;

    @Column(name = "encoded")
    private String encoded;

    @Column(name = "parent_uid", columnDefinition = "CHAR(32)")
    private String parentUid;

    @Column(name = "tenant_uid")
    private String tenantUid;


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEncoded() {
        return encoded;
    }
}
