package com.ge.apm.domain.pure;


import com.alibaba.fastjson.annotation.JSONField;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApprovalNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(serialize = false)
    private String id;

    @JSONField(serialize = false)
    private Integer nodeId; //节点序号

    private String nodeName;  //节点名称

    @JSONField(serialize = false)
    private List<SysRole> approveRoles; //审核角色

    @JSONField(serialize = false)
    private List<UserAccount> approveUsers; //审核人

    private List<Integer> roleUids; //审核角色

    private List<String> userUids; //审核人

    private boolean usingRole; //是否启用角色

    public boolean getUsingRole() {
        return usingRole;
    }

    public void setUsingRole(boolean usingRole) {
        this.usingRole = usingRole;
    }

}
