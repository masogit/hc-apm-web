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
public class CustomConfigAssetField {
    String key;
    Double widthInListPage;
    List<String> exportRoles;
    List<String> readRoles;
    List<String> editRoles;
    boolean required = false;


    public CustomConfigAssetField(String key,List<String> roles) {
        this.key = key;
        this.exportRoles = roles;
        this.readRoles = roles;
        this.editRoles = roles;
    }
    
    public CustomConfigAssetField() {
    }
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getWidthInListPage() {
        return widthInListPage;
    }

    public void setWidthInListPage(Double widthInListPage) {
        this.widthInListPage = widthInListPage;
    }

    public List<String> getExportRoles() {
        return exportRoles;
    }

    public void setExportRoles(List<String> exportRoles) {
        this.exportRoles = exportRoles;
    }

    public List<String> getReadRoles() {
        return readRoles;
    }

    public void setReadRoles(List<String> readRoles) {
        this.readRoles = readRoles;
    }

    public List<String> getEditRoles() {
        return editRoles;
    }

    public void setEditRoles(List<String> editRoles) {
        this.editRoles = editRoles;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

}
