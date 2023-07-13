package com.ge.apm.view.report;

import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.pure.TableSchema;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ge.apm.service.asset.BlobFileService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.asset.ElectricSafetyService;
import com.ge.apm.service.report.TableDefinitionService;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.event.FileUploadEvent;
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

    @Getter @Setter
    private Map<String, List<V2_BlobObject>> pictures;

    private TableDefinitionService tableService;
    
    private ElectricSafetyService electricSafetyService;
    private UnstructuredDataCollection report;
    private TableDefinition tableDefinition;
    private BlobFileService blobService;
    private boolean hasElectricSafetyData=false;
    @Getter@Setter
    private boolean isMaintainanceReport = true;
    @PostConstruct
    protected void init() {
        tableService = WebUtil.getBean(TableDefinitionService.class);
        electricSafetyService = WebUtil.getBean(ElectricSafetyService.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        String reportId = WebUtil.getRequestParameter("reportId");
        String action = WebUtil.getRequestParameter("action");
        String requestSource = WebUtil.getRequestParameter("requestSource");
        report = tableService.getReportById(reportId);
        if (StringUtils.isNotEmpty(requestSource) && "assetAchievements".equals(requestSource)) {
            tableDefinition = tableService.getTemplateByBaseId(report.getSchemaId());
        } else {
            tableDefinition = tableService.getTemplateById(report.getSchemaId());
        }
        if(StringUtils.isNotEmpty(tableDefinition.getInstrumentKeyValues())){
        	hasElectricSafetyData=true;
        }
        if (TableDefinition.TableTemplateType.AssetAchievements.name().equals(tableDefinition.getTemplateType())) {
            isMaintainanceReport = false;
        } else {
            isMaintainanceReport = true;
        }
        table = JSONObject.parseObject(tableDefinition.getSchema(), TableSchema.class);
        tableValue = new HashMap<String, Object>();
        pictures = new HashMap<>();
        List<UnstructuredData> dataList = report.getData();
        dataList.forEach(item -> {
            try{
                tableValue.put(item.getKey(),item.getValue());
                blobService.fetchBlobObject(item.getKey(), TableItem.ComponentType.Picture.name());
                pictures.put(item.getKey(), blobService.fetchBlobObject(item.getKey(), TableItem.ComponentType.Picture.name()));
            }catch(ClassNotFoundException e){
                tableValue.put(item.getKey(),item.getValueSerialized());
            }
        });
        table.getGroups().stream().flatMap(i-> i.getItems().stream())
                .filter(i-> !Strings.isNullOrEmpty(i.getDefaultValue()))
                .filter(i-> !tableValue.containsKey(i.getKey()))
                .forEach(i-> tableValue.put(i.getKey(), i.getDefaultValue()));

    }

    public void computeFormula(){
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
    }

    public void save() {
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
        tableService.saveTableDate(report,tableValue);
        WebUtil.addSuccessMessage(WebUtil.getMessage("Saved"));
    }


    public void submit(){
        tableValue.putAll(tableService.getRuleResultData(tableValue,table));
        tableService.saveTableDate(report,tableValue);
        tableService.submitReport(report);
         WebUtil.addSuccessMessage(WebUtil.getMessage("Submitted"));
    }
    
    public String getOptionValue(TableItem fieldItem){
        String optionKey = String.valueOf(tableValue.get(fieldItem.getKey()));
        if(!Strings.isNullOrEmpty(optionKey)){
            TableOption option = fieldItem.getOptions().stream().filter(item-> optionKey.equals(item.getKey())).findAny().orElse(null);
            return option==null?null:option.getValue();
        }
        return null;
    }

    public void removeAttachmentByIdAndKey(String blobId, String key) {
        blobService.deleteBlobFileById(blobId);
        if (tableValue.get(key) != null) {
            tableValue.put(key, "");
        }
    }

    public void handleFileUploadBefore(FileUploadEvent event) {
        UIComponent ui = event.getComponent();
        String key = ui.getId().substring(5);
        if (StringUtils.isEmpty(key)) {
            WebUtil.addErrorMessage("fail to upload file");
            return;
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            tableValue.put(key, blobObject.getObjectStorageId());
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
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
