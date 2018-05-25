/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset.components;

import java.util.List;
import javax.faces.component.UIComponent;
import org.primefaces.component.panelgrid.PanelGrid;

/**
 *
 * @author 212579464
 */
public class CustomPanelGrid extends PanelGrid {

    private List<UIComponent> customUIComponents;
    
    @Override
    public List<UIComponent> getChildren() {
//        return super.getChildren(); 
        return this.customUIComponents;
    }

    public List<UIComponent> getCustomUIComponents() {
        return customUIComponents;
    }

    public void setCustomUIComponents(List<UIComponent> customUIComponents) {
        this.customUIComponents = customUIComponents;
    }

    
}
