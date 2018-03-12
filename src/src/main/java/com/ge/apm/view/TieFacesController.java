/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view;
import com.ge.apm.domain.QrCodeTemplate;
import java.io.InputStream;
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
    
    @Override
    public void initialLoad() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("tiefaces/tiefaces.xlsx");
        loadWebSheet(stream);        
    }
}
