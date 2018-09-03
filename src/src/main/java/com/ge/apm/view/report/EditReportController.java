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
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.asset.ElectricSafetyService;
import com.ge.apm.service.report.TableDefinitionService;
import com.google.common.base.Strings;
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
    private TableDefinition tableDefinition;
    private boolean hasElectricSafetyData=false;
    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        electricSafetyService = WebUtil.getBean(ElectricSafetyService.class);

        String reportId = WebUtil.getRequestParameter("reportId");
        String action = WebUtil.getRequestParameter("action");
        report = tableService.getReportById(reportId);
        tableDefinition = tableService.getTemplateById(report.getSchemaId());
        if(StringUtils.isNotEmpty(tableDefinition.getInstrumentKeyValues())){
        	hasElectricSafetyData=true;
        }
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
    
    public String getOptionValue(TableItem fieldItem){
        String optionKey = (String)tableValue.get(fieldItem.getKey());
        if(!Strings.isNullOrEmpty(optionKey)){
            TableOption option = fieldItem.getOptions().stream().filter(item-> optionKey.equals(item.getKey())).findAny().orElse(null);
            return option==null?null:option.getValue();
        }
        return null;
    }
    
    public void importElectricSafetyData(){
    	electricSafetyService.parseTableValue(tableValue, tableDefinition.getInstrumentKeyValues(),tableDefinition.getSchema(), report.getBoUuid());
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

	public boolean isHasElectricSafetyData() {
		return hasElectricSafetyData;
	}

	public void setHasElectricSafetyData(boolean hasElectricSafetyData) {
		this.hasElectricSafetyData = hasElectricSafetyData;
	}

    
}
