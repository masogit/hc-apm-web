package com.ge.apm.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AuditGroupConfigurationNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(serialize = false)
    private String id;


    @JSONField(serialize = false)
    private Integer nodeId; //节点序号


    @JSONField(serialize = false)
    private List<UserAccount> approveUsers; //审核人集合

    @JSONField(serialize = false)
    private UserAccount auditUser; //审核人


    private List<String> userUids; //审核人


}
