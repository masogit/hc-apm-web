/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.dao.UnstructuredDataRepository;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.Objects;

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
    @Autowired
    private UnstructuredDataRepository udDao;
    @Autowired
    private BlobFileService blobService;
    @Autowired
    private V2_PmOrderService v2PmOrderService;
    

    public Integer getCountByTemplate(String templateId) {
        return udcDao.getCountByTemplate(templateId);
    }

    public List<String> getItemKeyList(String tableString) {
        return getItemKeyList(JSONObject.parseObject(tableString, TableSchema.class));
    }

    public List<String> getItemKeyList(TableSchema table) {
        List<String> keyList = new ArrayList();
        if (table.getGroups() != null) {
            table.getGroups().forEach(item -> {
                keyList.addAll(Optional.ofNullable(item.getItems()).orElse(new ArrayList<TableItem>()).stream().map(filedItem -> filedItem.getKey()).collect(Collectors.toList()));
            });
        }
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

    public Boolean hasDuplicatedKey(TableSchema table) {
        List<String> keyList = getItemKeyList(table);
        return keyList.size() != keyList.stream().distinct().collect(Collectors.toList()).size();
    }

    public TableDefinition getTemplateById(String templateId) {
        return templateDao.findById(templateId);
    }

    public TableDefinition getReleasedTemplateById(String templateId) {
        TableDefinition template = templateDao.findById(templateId);
        return TableDefinition.TableDefinitionStatus.Released.toString().equals(template.getStatus()) ? template : getTemplateByBaseId(template.getBaseId());
    }

    public TableDefinition getTemplateByBaseId(String BaseId) {
        return templateDao.findByBaseId(BaseId);
    }

    public void deleteTemplate(TableDefinition tableDefinition) {
        if (getCountByTemplate(tableDefinition.getId()) > 0) {
            WebUtil.addErrorMessage("模版使用中,不能删除");
        } else {
            if (null != tableDefinition.getExcelStorageId()) {
                blobService.deleteBlobFilesByStorageId(tableDefinition.getSchema());
            }
            templateDao.delete(tableDefinition);
        }
    }

    public UnstructuredDataCollection getReportById(String reportId) {
        return Strings.isNullOrEmpty(reportId) ? null : udcDao.findById(reportId);
    }

    public String getTableSchemaByTemplateId(String templateId) {
        TableDefinition template = Strings.isNullOrEmpty(templateId) ? null : templateDao.findById(templateId);
        return template == null ? "" : template.getSchema();
    }

    public String getTableSchema(String tableId) {
        TableDefinition template = Strings.isNullOrEmpty(tableId) ? null : templateDao.findById(tableId);
        return template == null ? "" : template.getSchema();
    }

    public Map<String, Object> getRuleResultData(Map<String, Object> tableValue, TableSchema table) {
        return TableResultReportService.getRuleResultData(tableValue, table);
    }

    @Transactional
    public TableDefinition updateExcelTemplate(TableDefinition selectedTableDefinition, V2_BlobObject blobObject) {
        if (getCountByTemplate(selectedTableDefinition.getId()) > 0) {
            //模版下有记录
            TableDefinition newTemplate = new TableDefinition();
            newTemplate.setName(selectedTableDefinition.getName());
            newTemplate.setAssetGroup(selectedTableDefinition.getAssetGroup());
            newTemplate.setBaseId(Strings.isNullOrEmpty(selectedTableDefinition.getBaseId()) ? selectedTableDefinition.getId() : selectedTableDefinition.getBaseId());
            newTemplate.setDescription(selectedTableDefinition.getDescription());
            newTemplate.setTenantUID(selectedTableDefinition.getTenantUID());
            newTemplate.setInstitutionUID(selectedTableDefinition.getInstitutionUID());
            newTemplate.setHospitalUID(selectedTableDefinition.getHospitalUID());
            newTemplate.setSiteUID(selectedTableDefinition.getSiteUID());
            newTemplate.setCreatedDate(new Date());
            newTemplate.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
            newTemplate.setStatus(selectedTableDefinition.getStatus());
            newTemplate.setExcelStorageId(blobObject == null ? null : blobObject.getObjectStorageId());
            newTemplate.setSchema(selectedTableDefinition.getSchema());
            newTemplate.setInstrumentKeyValues(selectedTableDefinition.getInstrumentKeyValues());
            newTemplate = templateDao.save(newTemplate);
            selectedTableDefinition.setStatus(TableDefinition.TableDefinitionStatus.Expired.toString());
            selectedTableDefinition.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
            selectedTableDefinition.setLastModifiedDate(new Date());
            templateDao.save(selectedTableDefinition);
            if (null != blobObject) {
                blobObject.setBoType(V2_BlobObject.BO_TYPE_TableDefinition);
                blobObject.setBoSubType("Excel");
                blobObject.setBoUuid(selectedTableDefinition.getId());
                blobService.saveBlobObject(blobObject);
            }
            return newTemplate;
        } else {
            //模版下无记录
            if (blobService.deleteBlobFilesByStorageId(selectedTableDefinition.getExcelStorageId())) {
                selectedTableDefinition.setExcelStorageId(blobObject == null ? null : blobObject.getObjectStorageId());
                selectedTableDefinition.setLastModifiedBy(UserContextService.getCurrentUserAccount().getUid());
                selectedTableDefinition.setLastModifiedDate(new Date());
                selectedTableDefinition = templateDao.save(selectedTableDefinition);
                if (null != blobObject) {
                    blobObject.setBoType(V2_BlobObject.BO_TYPE_TableDefinition);
                    blobObject.setBoSubType("Excel");
                    blobObject.setBoUuid(selectedTableDefinition.getId());
                    blobService.saveBlobObject(blobObject);
                }
            } else {
                WebUtil.addErrorMessage("更新失败");
            }

            return selectedTableDefinition;
        }

    }

    public void saveTableDate(UnstructuredDataCollection report, Map<String, Object> tableValue) {
        final String reportId = report.getId();
        List<UnstructuredData> dataList = udDao.findByCollectionId(reportId);
        if(dataList!=null){
        	Map<String, UnstructuredData> dataMap = dataList.stream().collect(Collectors.toMap(UnstructuredData::getKey, item -> item));
        	if(tableValue!=null){
        		tableValue.entrySet().forEach(item -> {
        			if(item!=null && item.getKey()!=null){
        				UnstructuredData dataItem = dataMap.get(item.getKey());
                        Object value = item.getValue();
                        if (item.getKey().contains("_res") && item.getValue()!=null) {
                            switch ((String) item.getValue()) {
                                case "合格":
                                case "正常":
                                    value = "Normal";
                                default:
                            }
                        }
                        if (null == dataItem) {
                            dataItem = new UnstructuredData();
                            dataItem.setCollectionId(reportId);
                            dataItem.setKey(item.getKey());
                            dataItem.setValue(value);
                            dataList.add(dataItem);
                        } else {
                            dataItem.setValue(value);
                        }
        			}
                });
        	}
        }
        report.setData(dataList);
        report.setCreatedDate(new Date());
        v2PmOrderService.saveReport(report);
    }

    public void submitReport(UnstructuredDataCollection report) {
        report.setStatus(UnstructuredDataCollection.Status.Archived.toString());
        v2PmOrderService.saveReport(report);
    }

    public void deleteReport(String reportId) {
        UnstructuredDataCollection report = udcDao.findById(reportId);
        if (null != report) {
            deleteReport(report);
        }
    }

    public void deleteReport(UnstructuredDataCollection report) {
        if (UnstructuredDataCollection.Status.Archived.toString().equals(report.getStatus())) {
            WebUtil.addErrorMessage("已归档报告不能删除");
        } else {
            udcDao.delete(report);
        }
    }

    public TableDefinition updateJSONTemplate(TableDefinition selectedTableDefinition, String tableString) {
        String operator = UserContextService.getCurrentUserAccount().getUid();
        Date operationTime = new Date();
        //模版下有记录
        TableDefinition newTemplate = new TableDefinition();
        newTemplate.setName(selectedTableDefinition.getName());
        newTemplate.setAssetGroup(selectedTableDefinition.getAssetGroup());
        newTemplate.setBaseId(Strings.isNullOrEmpty(selectedTableDefinition.getBaseId()) ? selectedTableDefinition.getId() : selectedTableDefinition.getBaseId());
        newTemplate.setDescription(selectedTableDefinition.getDescription());
        newTemplate.setTenantUID(selectedTableDefinition.getTenantUID());
        newTemplate.setInstitutionUID(selectedTableDefinition.getInstitutionUID());
        newTemplate.setHospitalUID(selectedTableDefinition.getHospitalUID());
        newTemplate.setSiteUID(selectedTableDefinition.getSiteUID());
        newTemplate.setCreatedDate(operationTime);
        newTemplate.setCreatedBy(operator);
        newTemplate.setStatus(selectedTableDefinition.getStatus());
        newTemplate.setExcelStorageId(selectedTableDefinition.getExcelStorageId());
        newTemplate.setInstrumentKeyValues(selectedTableDefinition.getInstrumentKeyValues());
        newTemplate.setSchema(tableString);
        templateDao.save(newTemplate);
        selectedTableDefinition.setStatus(TableDefinition.TableDefinitionStatus.Expired.toString());
        selectedTableDefinition.setLastModifiedBy(operator);
        selectedTableDefinition.setLastModifiedDate(operationTime);
        templateDao.save(selectedTableDefinition);
        return newTemplate;
    }

    public Map<String, Object> changSelectFommDataByJSON(TableDefinition tableDefinition, Map<String, Object> tableValue) {
        
        TableSchema table = JSONObject.parseObject(tableDefinition.getSchema(), TableSchema.class);
        if(null == table.getGroups() || table.getGroups().isEmpty()){
            return tableValue;
        }
        table.getGroups().stream().filter(groupItem-> null!= groupItem.getItems()).forEach(groupItem-> {
            groupItem.getItems().stream().filter(item-> TableItem.ComponentType.Select.equals(item.getComponent()))
                    .filter(item -> tableValue.containsKey(item.getKey()))
                    .forEach(item->{
                        item.getOptions().stream().filter(optionItem -> Objects.equals(optionItem.getKey(), tableValue.get(item.getKey())))
                                .findAny().ifPresent(option -> tableValue.put(item.getKey(), option.getValue()));
                    });
                });
        
        return tableValue;
    }
}
