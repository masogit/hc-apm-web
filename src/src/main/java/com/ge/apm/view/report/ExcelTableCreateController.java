/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.report;

import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.report.TieFacesService;
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

    private UploadedFile uploadedFile;
    private StreamedContent savedFile;

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

        String templateId = WebUtil.getRequestParameter("id");
        if (Strings.isNullOrEmpty(templateId)) {
            //新建
            CreateTemplate();
        } else {
            //修改
            selectedTableDefinition = templateDao.findById(templateId);
            if (null == selectedTableDefinition) {
                WebUtil.addErrorMessage("fail to load this template");
            } else {
                savedFile = blobService.getBlobObjectByStorageId(selectedTableDefinition.getSchema());
                loadWebSheetByStream(savedFile.getStream());
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
        if (Strings.isNullOrEmpty(selectedTableDefinition.getSchema())) {
            //新建
            if (uploadedFile != null) {
                V2_BlobObject blobObject = blobService.uploadFile(uploadedFile);
                if (null != blobObject) {
                    blobObject.setBoType(V2_BlobObject.BO_TYPE_TableDefinition);
                    blobObject.setBoSubType("Excel");
                    selectedTableDefinition.setSchema(blobObject.getObjectStorageId());
                    selectedTableDefinition = templateDao.save(selectedTableDefinition);
                    blobObject.setBoUuid(selectedTableDefinition.getId());
                    blobService.saveBlobObject(blobObject);
                    WebUtil.addSuccessMessage(selectedTableDefinition.getName() + "保存成功");
                }
            } else {
                WebUtil.addErrorMessage("模版文件未上传");
            }
        } else {
            //修改
            if (uploadedFile != null) {
                //Eexcel 模版有变动
                tableService.updateExcelTemplate(selectedTableDefinition, uploadedFile);
            } else {
                selectedTableDefinition = templateDao.save(selectedTableDefinition);
            }
        }

        
    }

    public void cancelUpload() {
        this.uploadedFile = null;
    }

    public void uploadDataFile(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
        selectedTableDefinition.setName(uploadedFile.getFileName().substring(0, uploadedFile.getFileName().lastIndexOf(".") < 0
                ? uploadedFile.getFileName().length() : uploadedFile.getFileName().lastIndexOf(".")));
        this.savedFile = null;
    }

    public void finishUpload() {
        try {
            loadWebSheetByStream(uploadedFile.getInputstream());
        } catch (IOException ex) {
            WebUtil.addErrorMessage(ex, "读取模版失败");
        }
    }

    public void testSubmit() {

        int i = 3;
    }

    private void loadWebSheetByStream(InputStream inputStream) {
        try {
            this.setTieCommandAliasList(TieFacesService.commonAliasList());
            tableFields.clear();
            context.put("fields", tableFields);
            loadWebSheet(inputStream, context);
        } catch (Exception e) {
            WebUtil.addErrorMessage(e, "读取模版失败");
        }
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

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public StreamedContent getSavedFile() {
        return savedFile;
    }

    public void setSavedFile(StreamedContent savedFile) {
        this.savedFile = savedFile;
    }

}
