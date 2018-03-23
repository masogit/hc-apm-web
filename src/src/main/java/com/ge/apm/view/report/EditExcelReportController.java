/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.report;

import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.report.TieFacesService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.StreamedContent;
import org.tiefaces.components.websheet.TieWebSheetBean;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class EditExcelReportController extends TieWebSheetBean {

    private TableDefinitionService tableService;

    private UnstructuredDataCollectionRepository udcDao;

    private Map<String, Object> tableValue;
    
    private UnstructuredDataCollection report;
    
    private BlobFileService blobService;
    
    private HashMap<String, Object> context = new HashMap<String, Object>();
    
    private TableDefinition tableDefinition;
    
    @Override
    public void initialLoad() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        udcDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        String reportId = WebUtil.getRequestParameter("reportId");
        report = tableService.getReportById(reportId);
        tableDefinition = tableService.getTemplateById(report.getSchemaId());
        tableValue = new HashMap<String, Object>();
        List<UnstructuredData> dataList = report.getData();
        dataList.forEach(item -> {
            try{
                tableValue.put(item.getKey(),item.getValue());
            }catch(ClassNotFoundException e){
                tableValue.put(item.getKey(),item.getValueSerialized());
            }
        });
        StreamedContent excelFile = blobService.getBlobObjectByStorageId(tableDefinition.getSchema());
        try {
            this.setTieCommandAliasList(TieFacesService.commonAliasList());
            context.put("fields", tableValue);
            loadWebSheet(excelFile.getStream(), context);
        } catch (Exception e) {
            WebUtil.addErrorMessage(e, "读取模版失败");
        }
    }
    
    public void saveData(){
        tableService.saveTableDate(report, tableValue);
        WebUtil.addSuccessMessage("已保存");
    }
    
    public void submit(){
        tableService.saveTableDate(report,tableValue);
        tableService.submitReport(report);
        WebUtil.addSuccessMessage("已提交");
    }

    public Map<String, Object> getTableValue() {
        return tableValue;
    }

    public void setTableValue(Map<String, Object> tableValue) {
        this.tableValue = tableValue;
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    public void setTableDefinition(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
    }

    public UnstructuredDataCollection getReport() {
        return report;
    }
    
    
}
