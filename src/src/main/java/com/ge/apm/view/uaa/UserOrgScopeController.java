/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.uaa;

import com.ge.apm.service.uaa.UaaService;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */

@ManagedBean
@ViewScoped
public class UserOrgScopeController {
    
    private DefaultTreeNode orgTree;
    
    @PostConstruct
    protected void init() {
    }

    public DefaultTreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(DefaultTreeNode orgTree) {
        this.orgTree = orgTree;
    }
    
    
    
}
