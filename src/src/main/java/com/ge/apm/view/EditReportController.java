package com.ge.apm.view;

import com.ge.apm.domain.pure.TableSchema;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.service.asset.TableDefinitionService;
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
    
    private UnstructuredDataCollectionRepository udcDao;
    
    UnstructuredDataCollection report;
    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        udcDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        String reportId = WebUtil.getRequestParameter("reportId");
        
        report = tableService.getReportById(reportId);
        table = JSONObject.parseObject(tableService.getTableSchemaByTemplateId(report.getSchemaId()), TableSchema.class);
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
        
        List<UnstructuredData> dataList = report.getData();
        tableValue.entrySet().forEach(item -> {
            UnstructuredData dataItem = dataList.stream().filter(eachData -> eachData.getKey().equals(item.getKey())).findFirst().orElse(null);
            if(null == dataItem){
                dataItem = new UnstructuredData();
                dataItem.setCollectionId(report.getId());
                dataItem.setKey(item.getKey());
                dataItem.setValue(item.getValue());
                dataList.add(dataItem);
            }else{
                dataItem.setValue(item.getValue());
            }
        });
        report.setData(dataList);
        report.setCreatedDate(new Date());
        udcDao.save(report);
        WebUtil.addSuccessMessage("已保存");
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

}
