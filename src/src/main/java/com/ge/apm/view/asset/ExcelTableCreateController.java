/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.TableDefinitionService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.tiefaces.components.websheet.TieWebSheetBean;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class ExcelTableCreateController extends TieWebSheetBean {

    private UploadedFile templateFile;

    private TableDefinitionService tableService;

    private Map<String, Object> tableFields;

    private TableDefinition selectedTableDefinition;

    private BlobFileService blobService;

    private TableDefinitionRepository templateDao;
    private HashMap<String, Object> context = new HashMap<String, Object>();

    @Override
    public void initialLoad() {
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        tableService = WebUtil.getBean(TableDefinitionService.class);
        tableFields = new HashMap<>();
        tableFields.put("XY", 60);

        String templateId = WebUtil.getRequestParameter("id");
        if (Strings.isNullOrEmpty(templateId)) {
            //新建
            CreateTemplate();
        } else {
            //修改
            selectedTableDefinition = templateDao.findById(templateId);
            if(null == selectedTableDefinition){
                WebUtil.addErrorMessage("fail to load this template");
            }else{
                StreamedContent file = blobService.getBlobObjectByStorageId(selectedTableDefinition.getSchema());
                loadWebSheetByStream(file.getStream());
            }
        }
    }

    private void CreateTemplate() {
        selectedTableDefinition = new TableDefinition();
        selectedTableDefinition.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        selectedTableDefinition.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        selectedTableDefinition.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        selectedTableDefinition.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        selectedTableDefinition.setStatus(TableDefinition.TableDefinitionStatus.Draft.toString());
        selectedTableDefinition.setTableType(TableDefinition.TableDefinitionType.EXCEL.toString());
    }

    public void saveTemplate() {
        if (templateFile != null) {
            V2_BlobObject blobObject = blobService.uploadFile(templateFile);
            if (null != blobObject) {
                blobObject.setBoType(V2_BlobObject.BO_TYPE_TableDefinition);
                blobObject.setBoSubType("Excel");
                selectedTableDefinition.setSchema(blobObject.getObjectStorageId());
                selectedTableDefinition = templateDao.save(selectedTableDefinition);
                blobObject.setBoUuid(selectedTableDefinition.getId());
                blobService.saveBlobObject(blobObject);
                WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
            }
        } else {
            WebUtil.addErrorMessage("fail to save template");
        }

    }

    public void cancelUpload() {
        this.templateFile = null;
    }

    public void uploadDataFile(FileUploadEvent event) {
        this.templateFile = event.getFile();
    }

    public void finishUpload() {
        try {
            loadWebSheetByStream(templateFile.getInputstream());
        } catch (IOException ex) {
            WebUtil.addErrorMessage(ex, "读取模版失败");
        }
    }
    
    public void testSubmit(){
        tableFields = context.get("fields");
        int i=3;
    }
    
    
    private void loadWebSheetByStream(InputStream inputStream){
        try {
            context.put("fields", tableFields);
            loadWebSheet(inputStream, context);
        } catch (Exception e) {
            WebUtil.addErrorMessage(e, "读取模版失败");
        }
    }

    public UploadedFile getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(UploadedFile templateFile) {
        this.templateFile = templateFile;
    }

    public TableDefinition getSelectedTableDefinition() {
        return selectedTableDefinition;
    }

    public void setSelectedTableDefinition(TableDefinition selectedTableDefinition) {
        this.selectedTableDefinition = selectedTableDefinition;
    }

    public Map<String, Object> getTableFields() {
        return tableFields;
    }

    public void setTableFields(Map<String, Object> tableFields) {
        this.tableFields = tableFields;
    }
    
}
