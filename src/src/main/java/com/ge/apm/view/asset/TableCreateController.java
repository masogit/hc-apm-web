package com.ge.apm.view.asset;

import com.ge.apm.view.*;
import com.ge.apm.domain.pure.TableSchema;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.asset.TableDefinitionService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class TableCreateController {

    private TableDefinitionRepository templateDao;
    private TableDefinitionService templateService;
    private TableSchema table;
    private String tableString;
    private TableGroup editGroup;
    private TableItem editTableItem;

    private TableOption selectedOption;

    private TableDefinition selectedTableDefinition;

    private Map<String, Object> tableValue;

    private Boolean isStringEditable;

    @PostConstruct
    protected void init() {
        tableValue = new HashMap<String, Object>();
        templateDao = WebUtil.getBean(TableDefinitionRepository.class);
        templateService = WebUtil.getBean(TableDefinitionService.class);
        String templateId = WebUtil.getRequestParameter("id");
        if (Strings.isNullOrEmpty(templateId)) {
            CreateTemplate();
            table = new TableSchema();
        } else {
            selectedTableDefinition = templateService.getTemplateById(templateId);
            table = JSONObject.parseObject(selectedTableDefinition.getSchema(), TableSchema.class);
            tableString = selectedTableDefinition.getSchema();
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

    public void saveTemplate() {
        if (Strings.isNullOrEmpty(selectedTableDefinition.getId())) {
            selectedTableDefinition.setSchema(JSONObject.toJSONString(table));
            selectedTableDefinition.setName(table.getTitle());
            templateDao.save(selectedTableDefinition);
            WebUtil.addSuccessMessage("已保存");
        } else {
            Integer count = templateService.getCountByTemplate(selectedTableDefinition.getId());
            if (count == 0 || !templateService.hasKeyChange(selectedTableDefinition.getSchema(), tableString)) {
                selectedTableDefinition.setSchema(tableString);
                selectedTableDefinition.setName(table.getTitle());
                templateDao.save(selectedTableDefinition);
                WebUtil.addSuccessMessage("已保存");
            } else {
                WebUtil.addErrorMessage("字段有变动,不能保存");
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

}
