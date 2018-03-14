/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.service.asset.QrCodeTemplateService;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import org.tiefaces.components.websheet.TieWebSheetBean;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class TieFacesController extends TieWebSheetBean{
    
    QrCodeTemplate thisTemplate;
    List<QrCodeTemplateElement> templateElementList;
    
    @Override
    public void initialLoad() {
        QrCodeTemplateService templateService = WebUtil.getBean(QrCodeTemplateService.class);
        Map<String, Object> defaultTemplate = templateService.generateDefaultTemplate();
        templateElementList = (List<QrCodeTemplateElement>) defaultTemplate.get("elementList");
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("templateElementList", templateElementList);
        
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("tiefaces/tiefaces.xlsx");
        loadWebSheet(stream,context);        
    }

    public void saveTable(){
//        List<QrCodeTemplateElement> list = (List<QrCodeTemplateElement>)context.get("templateElementList");
        int s=0;
    }
    
    public QrCodeTemplate getThisTemplate() {
        return thisTemplate;
    }

    public void setThisTemplate(QrCodeTemplate thisTemplate) {
        this.thisTemplate = thisTemplate;
    }

    public List<QrCodeTemplateElement> getTemplateElementList() {
        return templateElementList;
    }

    public void setTemplateElementList(List<QrCodeTemplateElement> templateElementList) {
        this.templateElementList = templateElementList;
    }

    
}
