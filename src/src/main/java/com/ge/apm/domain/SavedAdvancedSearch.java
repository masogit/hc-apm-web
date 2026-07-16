package com.ge.apm.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "saved_advanced_search")
public class SavedAdvancedSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "uid")
    private String uid;

    @Column(name = "user_uid", nullable = false, columnDefinition = "CHAR(32)")
    private String userUid;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "conditions_json", columnDefinition = "TEXT")
    @Type(type = "text")
    private String conditionsJson;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getUserUid() { return userUid; }
    public void setUserUid(String userUid) { this.userUid = userUid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getConditionsJson() { return conditionsJson; }
    public void setConditionsJson(String conditionsJson) { this.conditionsJson = conditionsJson; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
