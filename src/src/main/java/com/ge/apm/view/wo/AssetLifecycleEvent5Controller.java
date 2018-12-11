package com.ge.apm.view.wo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;

import com.ge.apm.service.utils.ExcelDocument;

import webapp.framework.dao.SearchFilter;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEvent5Controller extends AssetLifecycleEventController {

	
    protected void addFilter(){
    	searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "5"));//3外部维修
    }
    
    protected void downloadFile(List<Map<String,Object>> data){
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/pm/pmOrderListRemote_export.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = service.exportData(doc, data);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "PmOrder_export.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
	}
}