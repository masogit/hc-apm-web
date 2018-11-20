package com.ge.apm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Data
@Table(name = "msg_config")
public class MsgConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Size(max = 256)
    @Column(name = "msg_bind_field")
    private String msgBindField;

    @Size(max = 256)
    @Column(name = "msg_name")
    private String msgName;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMsgName() {
        return msgName;
    }

    public void setMsgName(String msgName) {
        this.msgName = msgName;
    }

    public String getMsgBindField() {
        return msgBindField;
    }

    public void setMsgBindField(String msgBindField) {
        this.msgBindField = msgBindField;
    }
}
