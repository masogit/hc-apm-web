package com.ge.apm.view.report;

import com.ge.apm.domain.pure.TableSchema;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.service.asset.ElectricSafetyService;
import com.ge.apm.service.report.TableDefinitionService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import webapp.framework.web.WebUtil;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class EditReportController {

    private TableSchema table;

    private Map<String, Object> tableValue;

    private TableDefinitionService tableService;
    
    private ElectricSafetyService electricSafetyService;
    private UnstructuredDataCollection report;
    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        electricSafetyService = WebUtil.getBean(ElectricSafetyService.class);

        String reportId = WebUtil.getRequestParameter("reportId");
        String action = WebUtil.getRequestParameter("action");
        report = tableService.getReportById(reportId);
        TableDefinition tableDefinition = tableService.getTemplateById(report.getSchemaId());
        table = JSONObject.parseObject(tableDefinition.getSchema(), TableSchema.class);
        tableValue = new HashMap<String, Object>();
        List<UnstructuredData> dataList = report.getData();
        dataList.forEach(item -> {
            try{
                tableValue.put(item.getKey(),item.getValue());
            }catch(ClassNotFoundException e){
                tableValue.put(item.getKey(),item.getValueSerialized());
            }
        });
        if(StringUtils.isNotEmpty(action) && action.equals("edit")){
        	electricSafetyService.parseTableValue(tableValue, tableDefinition.getInstrumentKeyValues(),tableDefinition.getSchema(), report.getBoUuid());
        }
    }

    public void save() {
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
        tableService.saveTableDate(report,tableValue);
        WebUtil.addSuccessMessage("已保存");
    }


    public void submit(){
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
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

    public TableSchema getTable() {
        return table;
    }

    public void setTable(TableSchema table) {
        this.table = table;
    }

    public UnstructuredDataCollection getReport() {
        return report;
    }

}
