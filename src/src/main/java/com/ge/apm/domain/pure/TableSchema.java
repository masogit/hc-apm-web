/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.List;

/**
 *
 * @author 212579464
 */
public class TableSchema {

    private String title;
    private List<TableGroup> groups;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TableGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<TableGroup> groups) {
        this.groups = groups;
    }
    
}