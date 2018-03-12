/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import java.io.InputStream;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.tiefaces.components.websheet.TieWebSheetBean;

/**
 *
 * @author 212579464
 */
@Named
@ManagedBean
@ViewScoped
public class TieFacesController extends TieWebSheetBean{
    
    QrCodeTemplate thisTemplate;
    List<QrCodeTemplateElement> thisTemplateElementList;
    
    @Override
    public void initialLoad() {
        
        thisTemplateElementList = (List<QrCodeTemplateElement>) defaultTemplate.get("elementList");
        
        
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("tiefaces/tiefaces.xlsx");
        loadWebSheet(stream);        
    }

    public QrCodeTemplate getThisTemplate() {
        return thisTemplate;
    }

    public void setThisTemplate(QrCodeTemplate thisTemplate) {
        this.thisTemplate = thisTemplate;
    }

    public List<QrCodeTemplateElement> getThisTemplateElementList() {
        return thisTemplateElementList;
    }

    public void setThisTemplateElementList(List<QrCodeTemplateElement> thisTemplateElementList) {
        this.thisTemplateElementList = thisTemplateElementList;
    }
    
    
    
}
