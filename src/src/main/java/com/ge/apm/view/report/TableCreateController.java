package com.ge.apm.view.report;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.ElectricSafetyData;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.report.TieFacesService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ReportTemplateDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.transaction.annotation.Transactional;
import org.tiefaces.components.websheet.TieWebSheetBean;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class TableCreateController extends TieWebSheetBean {

    private static final String FILE_SUFFIX = ".xlsx";
    private static final String DEFAULT_EXCEL_TEMPLATE_FILE = "ReportTemplate";

    private UaaService uaaService;
    private OrgInfoRepository orgDao;
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

    private Boolean isReadOnly = false;
    private OrgInfo selectedOrg;
    private List<OrgInfo> selectedOrgList;
    //for Excel
    private UploadedFile uploadedFile;
    private BlobFileService blobService;
    private Map<String, Object> tableFields;
    private HashMap<String, Object> context = new HashMap<String, Object>();
    private V2_BlobObject uploadedFileObject;

    //for instrument data import
    private Collection<String> instrumentDataLabels;
    private Collection<String> templateLabels;
    private Map<String, String> mapTemplateKeyValues;
    private DualListModel listInstrumentMppingLabels = new DualListModel<>();
    private String instrumentDataLabel;
    private String templateLabel;
    private static final String SPLIT_STR = " == ";

    // cache for exported file.
    private StreamedContent exportedFile;

    @Override
    public void initialLoad() {
        tableValue = new HashMap<String, Object>();
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        templateService = WebUtil.getBean(TableDefinitionService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        tableFields = new HashMap<>();

        String templateId = WebUtil.getRequestParameter("id");
        if (Strings.isNullOrEmpty(templateId)) {
            CreateTemplate();
            table = new TableSchema();
        } else {
            selectedTableDefinition = templateService.getTemplateById(templateId);
            table = JSONObject.parseObject(selectedTableDefinition.getSchema(), TableSchema.class);
            if (null != table) {
                table.setSchemaId(selectedTableDefinition.getId());
                tableString = JSONObject.toJSONString(table);
            } else {
                table = new TableSchema();
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

            if (StringUtils.isNotEmpty(selectedTableDefinition.getInstrumentKeyValues())) {
                List<String> targetList = new ArrayList<String>();
                listInstrumentMppingLabels.setTarget(targetList);
                getTemplateLabels();
                JSONObject obj = JSONObject.parseObject(selectedTableDefinition.getInstrumentKeyValues());
                Map<String, String> map = ElectricSafetyData.getLabelAttributeMapping();
                for (Map.Entry<String, String> entity : mapTemplateKeyValues.entrySet()) {
                    for (String key : obj.keySet()) {
                        if (key.equals(entity.getKey())) {
                            targetList.add(entity.getValue() + SPLIT_STR + map.get(obj.get(key)));
                        }
                    }
                }
            }
        }

        selectedOrg = orgDao.findByUid(selectedTableDefinition.getSiteUID());
        selectedOrgList =  uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());
        if(!selectedOrgList.contains(selectedOrg)){
            selectedOrgList.add(selectedOrg);
            isReadOnly = true;
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

    //main save
    @Transactional
    public void saveTemplate() {
        tableString = JSONObject.toJSONString(table);
        if (StringUtils.isEmpty(table.getTitle())) {
            WebUtil.addErrorMessage("名称不能为空");
            return;
        }
        if (!Strings.isNullOrEmpty(table.getTitle()) && table.getTitle().length() > 64) {
            WebUtil.addErrorMessage("名称最大长度64");
            return;
        }
        if (Strings.isNullOrEmpty(selectedTableDefinition.getId())) {
            selectedTableDefinition.setName(table.getTitle());
            //save instrument data
            selectedTableDefinition.setInstrumentKeyValues(saveSelectedInstrumentData());
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
                //save instrument data
                selectedTableDefinition.setInstrumentKeyValues(saveSelectedInstrumentData());
                selectedTableDefinition.setSchema(tableString);
                selectedTableDefinition.setName(table.getTitle());
                templateDao.save(selectedTableDefinition);
                WebUtil.addSuccessMessage("JSON已保存");
            } else {
                //save instrument data
                selectedTableDefinition.setInstrumentKeyValues(saveSelectedInstrumentData());
                selectedTableDefinition = templateService.updateJSONTemplate(selectedTableDefinition, tableString);
                WebUtil.addErrorMessage("字段变动, JSON已保存为新版本");
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
//            blobService.deleteBlobFile(uploadedFileObject);
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

    public void removeExcelTemplate() {
        this.uploadedFile = null;
        saveExcelTable();
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

            saveExcelTable();
        }
    }

    private void saveExcelTable() {

        if (Strings.isNullOrEmpty(selectedTableDefinition.getName())) {
            selectedTableDefinition.setName(table.getTitle());
        }
        if (Strings.isNullOrEmpty(selectedTableDefinition.getSchema())) {
            if (Strings.isNullOrEmpty(selectedTableDefinition.getId())) {
                selectedTableDefinition = templateDao.save(selectedTableDefinition);
                table.setSchemaId(selectedTableDefinition.getId());
            }
            tableString = JSONObject.toJSONString(table);
            selectedTableDefinition.setSchema(tableString);
        }

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
                if (Strings.isNullOrEmpty(selectedTableDefinition.getBaseId())) {
                    selectedTableDefinition.setBaseId(selectedTableDefinition.getId());
                }
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
            } else {
                //删除Excel
                selectedTableDefinition = templateService.updateExcelTemplate(selectedTableDefinition, null);
            }
        }
    }

    private String genExcelTemplateTableName(TableSchema table) {
        String title;
        if (table == null || table.getTitle() == null) {
            title = DEFAULT_EXCEL_TEMPLATE_FILE;
        } else {
            try {
                title = URLEncoder.encode(table.getTitle(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                title = DEFAULT_EXCEL_TEMPLATE_FILE;
            }
        }
        return title + FILE_SUFFIX;
    }

    /**
     * Generate Excel Template File in XLSX format.
     */
    public void generateExcelTemplateFile() {
        String fname = genExcelTemplateTableName(table);
        try (Workbook wb = new XSSFWorkbook()) {
            exportedFile = null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ReportTemplateDocument report = new ReportTemplateDocument(wb, table, "Template");
            report.write(out);
            InputStream doc = new ByteArrayInputStream(out.toByteArray());
            exportedFile = new DefaultStreamedContent(doc, "application/vnd.ms-excel", fname);
        } catch (Exception ex) {
            Logger.getLogger(TableCreateController.class.getName()).log(Level.WARNING, "Error happens while generate Excel template", ex);
            WebUtil.addErrorMessage("Excel模版生成失败");
        }
    }

    /**
     * Getter method for Exported File.
     *
     * @return Primefaces {@link StreamedContent} object.
     */
    public StreamedContent getExportedFile() {
        return exportedFile;
    }

    public void bindInstrumentData() {
        if (listInstrumentMppingLabels == null) {
            listInstrumentMppingLabels = new DualListModel();
        }
        if (this.instrumentDataLabel == null || this.templateLabel == null) {
            return;
        }
        List listSource = null;
        List listTarget = null;
        if (listInstrumentMppingLabels.getSource() == null) {
            listSource = new ArrayList<String>();
            listInstrumentMppingLabels.setSource(listSource);
        }
        if (listInstrumentMppingLabels.getTarget() == null) {
            listTarget = new ArrayList<String>();
            listInstrumentMppingLabels.setTarget(listTarget);
        }
        listSource = listInstrumentMppingLabels.getSource();
        String temp = this.templateLabel + SPLIT_STR + this.instrumentDataLabel;
        if (!listSource.contains(temp)) {
            listSource.add(temp);
        }
    }

    public String saveSelectedInstrumentData() {
        String result = "";

        if (listInstrumentMppingLabels != null) {
            JSONObject obj = new JSONObject();
            List<String> targetList = listInstrumentMppingLabels.getTarget();
            if (targetList != null && targetList.size() > 0) {
                String strArr[];
                for (int i = 0; i < targetList.size(); ++i) {
                    strArr = targetList.get(i).split(SPLIT_STR);
                    if (strArr != null && strArr.length == 2) {
                        String keyTemplate = "";
                        String keyInstrument = "";
                        for (Entry<String, String> entity : mapTemplateKeyValues.entrySet()) {
                            if (entity.getValue().equals(strArr[0])) {
                                keyTemplate = entity.getKey();
                            }
                        }
                        for (Entry<String, String> entity : ElectricSafetyData.getLabelAttributeMapping().entrySet()) {
                            if (entity.getValue().equals(strArr[1])) {
                                keyInstrument = entity.getKey();
                            }
                        }
                        obj.put(keyTemplate, keyInstrument);
                    }
                }
            }
            result = obj.toString();
        }
        return result;
    }

    public void testSubmit() {
    }


    public void onSiteChange() {
        selectedTableDefinition.setTenantUID(selectedOrg.getTenantUID());
        selectedTableDefinition.setInstitutionUID(selectedOrg.getInstitutionUID());
        selectedTableDefinition.setHospitalUID(selectedOrg.getHospitalUID());
        selectedTableDefinition.setSiteUID(selectedOrg.getSiteUID());
    }
    
    public boolean getIsReadOnly(){
        return isReadOnly;
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

    public Collection<String> getInstrumentDataLabels() {
        instrumentDataLabels = ElectricSafetyData.getLabelAttributeMapping().values();
        return instrumentDataLabels;
    }

    public void setInstrumentDataLabels(Collection<String> instrumentDataLabels) {
        this.instrumentDataLabels = instrumentDataLabels;
    }

    public String getInstrumentDataLabel() {
        return instrumentDataLabel;
    }

    public void setInstrumentDataLabel(String instrumentDataLabel) {
        this.instrumentDataLabel = instrumentDataLabel;
    }

    public String getTemplateLabel() {
        return templateLabel;
    }

    public void setTemplateLabel(String templateLabel) {
        this.templateLabel = templateLabel;
    }

    public DualListModel getListInstrumentMppingLabels() {
        return listInstrumentMppingLabels;
    }

    public void setListInstrumentMppingLabels(DualListModel listInstrumentMppingLabels) {
        this.listInstrumentMppingLabels = listInstrumentMppingLabels;
    }

    public Collection<String> getTemplateLabels() {
        mapTemplateKeyValues = new HashMap<String, String>();
        if (table != null) {
            try {
                for (TableGroup group : table.getGroups()) {
                    for (TableItem item : group.getItems()) {
                        mapTemplateKeyValues.put(item.getKey(), item.getLabel() + "(" + item.getKey() + ")");
                    }
                }
                templateLabels = mapTemplateKeyValues.values();
            } catch (Exception e) {
                // TODO: handle exception
                WebUtil.addErrorMessage(e, "解析模版失败");
            }
        }
        return templateLabels;
    }
    

    public void setTemplateLabels(Collection<String> templateLabels) {
        this.templateLabels = templateLabels;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<OrgInfo> getSelectedOrgList() {
        return selectedOrgList;
    }
    
}
