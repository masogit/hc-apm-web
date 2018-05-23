/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset.components;

import org.primefaces.component.column.Column;

/**
 *
 * @author 212579464
 */
public class CustomColumns extends Column {

    boolean isCustomVisible = true;

    String fieldKey;

    public CustomColumns(String fieldKey, boolean isVisible) {
        super();
        this.fieldKey = fieldKey;
        this.isCustomVisible = isVisible;
    }

    @Override
    public boolean isVisible() {
        return isCustomVisible;
    }

    public boolean isIsCustomVisible() {
        return isCustomVisible;
    }

    public void setIsCustomVisible(boolean isCustomVisible) {
        this.isCustomVisible = isCustomVisible;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
    
}
