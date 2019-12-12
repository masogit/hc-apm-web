package com.ge.apm.view.wo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;

import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.AssetLifecycleEventService;
import com.ge.apm.view.sysutil.FileDownloadService;

import webapp.framework.context.LocaleHolder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEvent5Controller extends AssetLifecycleEventController {

	
	protected void init() {
		super.init();
        tabStatus=5;
    }

    /***
     *  是否是中国 维修工作台在非中文状态下不显示外部工单和外部远程工单
     */
    public boolean isLocale(){
        Locale locale = LocaleHolder.getLocale();
        //System.out.println("locale  is "+locale);
        if(locale.getLanguage().equalsIgnoreCase("zh")){
            return true;
        } else {
            return false;
        }
    }
    
    protected void downloadFile(List<Map<String,Object>> data){
        String fileName = null;
        if (isLocale()){
            fileName = "/portal/pm/pmOrderListRemote_export_zh.xlsx";
        } else {
            fileName = "/portal/pm/pmOrderListRemote_export_en.xlsx";

        }
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(fileName);
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