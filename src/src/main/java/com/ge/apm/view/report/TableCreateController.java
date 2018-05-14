package com.ge.apm.view.report;

import com.ge.apm.domain.pure.TableSchema;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.report.TieFacesService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import org.tiefaces.components.websheet.TieWebSheetBean;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class TableCreateController extends TieWebSheetBean {

    private TableDefinitionRepository templateDao;
    private TableDefinitionService templateService;
    private TableSchema table;
    private String tableString;
    private TableGroup editGroup;
    private TableItem editTableItem;

    private TableOption selectedOption;

    private TableDefinition selectedTableDefinition;

    private Map<String, Object> tableValue;

    private Boolean isStringEditable = false;

    //for Excel
    private UploadedFile uploadedFile;
    private BlobFileService blobService;
    private Map<String, Object> tableFields;
    private HashMap<String, Object> context = new HashMap<String, Object>();
    private V2_BlobObject uploadedFileObject;

    @Override
    public void initialLoad() {
        tableValue = new HashMap<String, Object>();
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        templateService = WebUtil.getBean(TableDefinitionService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        tableFields = new HashMap<>();

        String templateId = WebUtil.getRequestParameter("id");
        if (Strings.isNullOrEmpty(templateId)) {
            CreateTemplate();
            table = new TableSchema();
        } else {
            selectedTableDefinition = templateService.getTemplateById(templateId);
            table = JSONObject.parseObject(selectedTableDefinition.getSchema(), TableSchema.class);
            if (Strings.isNullOrEmpty(table.getSchemaId())) {
                table.setSchemaId(selectedTableDefinition.getId());
                tableString = JSONObject.toJSONString(table);
            } else {
                tableString = selectedTableDefinition.getSchema();
            }

            if (null != selectedTableDefinition.getExcelStorageId()) {
                try {
                    StreamedContent savedFile = blobService.getBlobObjectByStorageId(selectedTableDefinition.getExcelStorageId());
                    loadWebSheetByStream(savedFile.getStream());
                } catch (Exception e) {
                    WebUtil.addErrorMessage("Excel模版加载失败");
                }
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
    }

    private void loadWebSheetByStream(InputStream inputStream) {
        try {
            this.setTieCommandAliasList(TieFacesService.commonAliasList());
            tableFields.clear();
            context.put("X", tableFields);
            loadWebSheet(inputStream, context);
        } catch (Exception e) {
            WebUtil.addErrorMessage(e, "读取模版失败");
        }
    }

    @Transactional
    public void saveTemplate() {
        if (!Strings.isNullOrEmpty(table.getTitle()) && table.getTitle().length() > 64) {
            WebUtil.addErrorMessage("名称最大长度64");
            return;
        }
        if (Strings.isNullOrEmpty(selectedTableDefinition.getId())) {
            selectedTableDefinition.setName(table.getTitle());
            templateDao.save(selectedTableDefinition);
            table.setSchemaId(selectedTableDefinition.getId());
            tableString = JSONObject.toJSONString(table);
            selectedTableDefinition.setSchema(tableString);
            templateDao.save(selectedTableDefinition);
            selectedTableDefinition.setBaseId(selectedTableDefinition.getId());
            templateDao.save(selectedTableDefinition);
            WebUtil.addSuccessMessage("JSON已保存");
        } else {
            Integer count = templateService.getCountByTemplate(selectedTableDefinition.getId());
            if (count == 0 || !templateService.hasKeyChange(selectedTableDefinition.getSchema(), tableString)) {
                selectedTableDefinition.setSchema(tableString);
                selectedTableDefinition.setName(table.getTitle());
                templateDao.save(selectedTableDefinition);
                WebUtil.addSuccessMessage("JSON已保存");
            } else {
                selectedTableDefinition = templateService.updateJSONTemplate(selectedTableDefinition, tableString);
                WebUtil.addErrorMessage("字段有变动,JSON不能保存");
            }

        }

    }

    public void onTitleChange() {
        tableString = JSONObject.toJSONString(table);
    }

    public void prepareAddGroup() {
        editGroup = new TableGroup();
    }

    public void prepareEditGroup(TableGroup group) {
        editGroup = group;
    }

    public void editGroup() {
        if (null == table.getGroups()) {
            table.setGroups(new ArrayList<TableGroup>());
        }
        if (!table.getGroups().contains(editGroup)) {
            table.getGroups().add(editGroup);
        }
        editGroup = null;
        tableString = JSONObject.toJSONString(table);
    }

    public void moveGroup(TableGroup group, String oper) {
        int index = table.getGroups().indexOf(group);
        if ("up".equals(oper) && index >= 1) {
            table.getGroups().set(index, table.getGroups().get(index - 1));
            table.getGroups().set(index - 1, group);
        }
        if ("down".equals(oper) && index < table.getGroups().size() - 1) {
            table.getGroups().set(index, table.getGroups().get(index + 1));
            table.getGroups().set(index + 1, group);
        }
        tableString = JSONObject.toJSONString(table);
    }

    public void moveItem(TableGroup group, TableItem item, String oper) {
        int index = group.getItems().indexOf(item);
        if ("up".equals(oper) && index >= 1) {
            group.getItems().set(index, group.getItems().get(index - 1));
            group.getItems().set(index - 1, item);
        }
        if ("down".equals(oper) && index < group.getItems().size() - 1) {
            group.getItems().set(index, group.getItems().get(index + 1));
            group.getItems().set(index + 1, item);
        }
        tableString = JSONObject.toJSONString(table);
    }

    public void removeGroup(TableGroup group) {
        table.getGroups().remove(group);
        tableString = JSONObject.toJSONString(table);
    }

    public void removeItem(TableGroup group, TableItem item) {
        group.getItems().remove(item);
        tableString = JSONObject.toJSONString(table);
    }

    public void prepareAddItem(TableGroup group) {
        editGroup = group;
        editTableItem = new TableItem();
        editTableItem.setExp(new HashMap<String, String>());

    }

    public void prepareEditItem(TableGroup group, TableItem item) {
        editGroup = group;
        editTableItem = item;
    }

    public void editItem() {
        if (null == editGroup.getItems()) {
            editGroup.setItems(new ArrayList<TableItem>());
        }
        if (templateService.hasDuplicatedKey(table) || !editGroup.getItems().contains(editTableItem) && templateService.getItemKeyList(table).contains(editTableItem.getKey())) {
            WebUtil.addErrorMessage("Key已经被使用");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            editTableItem.setKey(null);
            return;
        }
        if (!editGroup.getItems().contains(editTableItem)) {
            editGroup.getItems().add(editTableItem);
        }

        cancel();
        tableString = JSONObject.toJSONString(table);
    }

    public void cancel() {
        editTableItem = null;
        editGroup = null;
    }

    public void onOptionChange() {
        if (editTableItem.getComponent().equals(TableItem.ComponentType.Select)) {
            if (editTableItem.getOptions() == null || editTableItem.getOptions().isEmpty()) {
                editTableItem.setOptions(new ArrayList<TableOption>());
            }
        }
        if (null != editTableItem.getExp()) {
            editTableItem.getExp().clear();
        }

    }

    public void onStringEdit() {
        this.isStringEditable = true;
    }

    public void onStringSave() {
        try {
            if (templateService.hasDuplicatedKey(JSONObject.parseObject(tableString, TableSchema.class))) {
                WebUtil.addErrorMessage("Key已经被使用");
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("validationFailed", true);
                tableString = JSONObject.toJSONString(table);
                return;
            }
            table = JSONObject.parseObject(tableString, TableSchema.class);
        } catch (Exception e) {
            WebUtil.addErrorMessage("不能解析成模版");
            return;
        }
        this.isStringEditable = false;
    }

    public void prepareAddOption() {
        selectedOption = new TableOption();
    }

    public void prepareEditOption(TableOption option) {
        selectedOption = option;
    }

    public void removeOption(TableOption option) {
        editTableItem.getOptions().remove(option);
        tableString = JSONObject.toJSONString(table);
    }

    public void saveOption() {
        for (TableOption item : editTableItem.getOptions()) {
            if (item.getKey() != null && item.getKey().equals(selectedOption.getKey())) {
                item.setValue(selectedOption.getValue());
                selectedOption = null;
                return;
            }
        }
        editTableItem.getOptions().add(selectedOption);
        selectedOption = null;
        tableString = JSONObject.toJSONString(table);
    }

    public void onRuleChange() {

        if (!Strings.isNullOrEmpty(editTableItem.getKey())) {
            editTableItem.setResKey(editTableItem.getKey().concat("_res"));
        }
    }

    public void cancelUpload() {
        this.uploadedFile = null;
        if (null != uploadedFileObject) {
            blobService.deleteBlobFile(uploadedFileObject);
            uploadedFileObject = null;
        }
        if (null != selectedTableDefinition.getExcelStorageId()) {
            try {
                StreamedContent savedFile = blobService.getBlobObjectByStorageId(selectedTableDefinition.getExcelStorageId());
                loadWebSheetByStream(savedFile.getStream());
            } catch (Exception e) {
                WebUtil.addErrorMessage("Excel模版加载失败");
            }
        }
    }

    public void uploadDataFile(FileUploadEvent event) {
        this.uploadedFile = event.getFile();
        uploadedFileObject = blobService.uploadFile(uploadedFile);
    }

    public void finishUpload() {
        if (null != uploadedFileObject) {
            StreamedContent savedFile = blobService.getBlobObjectByStorageId(uploadedFileObject.getObjectStorageId());
            table.setTitle(uploadedFileObject.getObjectName().indexOf(".") > 0 ? uploadedFileObject.getObjectName().substring(0, uploadedFileObject.getObjectName().indexOf(".")) : uploadedFileObject.getObjectName());
            loadWebSheetByStream(savedFile.getStream());
            if (Strings.isNullOrEmpty(selectedTableDefinition.getName())) {
                selectedTableDefinition.setName(table.getTitle());
            }
            saveExcelTable();
        }
    }

    private void saveExcelTable() {
        //for excel
        if (Strings.isNullOrEmpty(selectedTableDefinition.getExcelStorageId())) {
            //新建
//                V2_BlobObject blobObject = blobService.uploadFile(uploadedFile);
            if (null != uploadedFileObject) {
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_TableDefinition);
                uploadedFileObject.setBoSubType("Excel");
                selectedTableDefinition.setExcelStorageId(uploadedFileObject.getObjectStorageId());
                if (Strings.isNullOrEmpty(selectedTableDefinition.getId())) {
                    selectedTableDefinition = templateDao.save(selectedTableDefinition);
                }
                uploadedFileObject.setBoUuid(selectedTableDefinition.getId());
                selectedTableDefinition.setBaseId(selectedTableDefinition.getId());
                blobService.saveBlobObject(uploadedFileObject);
                templateDao.save(selectedTableDefinition);
                WebUtil.addSuccessMessage("Excel已保存");
            }
        } else {
            //修改

            if (uploadedFile != null) {
                //Eexcel 模版有变动
                selectedTableDefinition = templateService.updateExcelTemplate(selectedTableDefinition, uploadedFileObject);
                WebUtil.addSuccessMessage("Excel已保存");
            }
        }
    }

    public void testSubmit() {
    }

    //getter and setter
    public TableSchema getTable() {
        return table;
    }

    public void setTable(TableSchema table) {
        this.table = table;
    }

    public String getTableString() {
        return tableString;
    }

    public void setTableString(String tableString) {
        this.tableString = tableString;
    }

    public TableGroup getEditGroup() {
        return editGroup;
    }

    public void setEditGroup(TableGroup editGroup) {
        this.editGroup = editGroup;
    }

    public TableItem getEditTableItem() {
        return editTableItem;
    }

    public void setEditTableItem(TableItem editTableItem) {
        this.editTableItem = editTableItem;
    }

    public Map<String, Object> getTableValue() {
        return tableValue;
    }

    public void setTableValue(Map<String, Object> tableValue) {
        this.tableValue = tableValue;
    }

    public TableOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(TableOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public TableDefinition getSelectedTableDefinition() {
        return selectedTableDefinition;
    }

    public void setSelectedTableDefinition(TableDefinition selectedTableDefinition) {
        this.selectedTableDefinition = selectedTableDefinition;
    }

    public Boolean getIsStringEditable() {
        return isStringEditable;
    }

    public void setIsStringEditable(Boolean isStringEditable) {
        this.isStringEditable = isStringEditable;
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

}
