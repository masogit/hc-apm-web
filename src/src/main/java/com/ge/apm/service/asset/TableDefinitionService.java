/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class TableDefinitionService {

    @Autowired
    private TableDefinitionRepository templateDao;
    @Autowired
    private UnstructuredDataCollectionRepository udcDao;

    public Integer getCountByTemplate(String templateId) {
        return udcDao.getCountByTemplate(templateId);
    }

    public List<String> getItemKeyList(String tableString) {
        return getItemKeyList(JSONObject.parseObject(tableString, TableSchema.class));
    }

    public List<String> getItemKeyList(TableSchema table) {
        List<String> keyList = new ArrayList();
        table.getGroups().forEach(item -> {
            keyList.addAll(Optional.ofNullable(item.getItems()).orElse(new ArrayList<TableItem>()).stream().map(filedItem -> filedItem.getKey()).collect(Collectors.toList()));
        });
        return keyList;
    }

    public Boolean hasKeyChange(String oldStr, String newStr) {
        List<String> oldList = getItemKeyList(oldStr);
        List<String> newList = getItemKeyList(newStr);
        if (oldList == null && newList == null) {
            return false;
        }
        if (oldList != null && newList != null) {
            if (oldList.size() != newList.size()) {
                return true;
            }
            List<String> addedKeys = newList.stream().filter(item -> !oldList.contains(item)).collect(Collectors.toList());
            return addedKeys.size() > 0;
        } else {
            return true;
        }
    }
    
    public Boolean hasDuplicatedKey(TableSchema table){
        List<String> keyList = getItemKeyList(table);
        return keyList.size() != keyList.stream().distinct().collect(Collectors.toList()).size();
    }

    public TableDefinition getTemplateById(String templateId) {
        return templateDao.findById(templateId);
    }

    public void deleteTemplateById(String id) {
        if (getCountByTemplate(id) > 0) {
            WebUtil.addErrorMessage("模版使用中,不能删除");
        } else {
            templateDao.delete(id);
        }

    }

    public UnstructuredDataCollection getReportById(String reportId) {
        return Strings.isNullOrEmpty(reportId) ? null : udcDao.findById(reportId);
    }

    public String getTableSchemaByTemplateId(String templateId) {
        TableDefinition template = Strings.isNullOrEmpty(templateId) ? null : templateDao.findById(templateId);
        return template == null ? "" : template.getSchema();
    }
}
