package com.ge.apm.view.report;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.dao.UnstructuredDataRepository;

import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.OrgInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UnstructuredData;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import java.util.stream.Collectors;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class TableDefinitionController extends GenericCRUDUUIDController<TableDefinition> {

    private TableDefinitionRepository dao;

    private OrgInfoRepository orgDao;
    private TableDefinitionService templateService;
    private Map<String, OrgInfo> orgInfoMap;

    private UaaService uaaService;

    private String createType = "JSON";

    @Override
    protected void init() {

        dao = WebUtil.getBean(TableDefinitionRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        templateService = WebUtil.getBean(TableDefinitionService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
        this.orgInfoMap = populateOrgInfoMap();
    }

    private Map<String, OrgInfo> populateOrgInfoMap() {
        Map<String, OrgInfo> mapping = new HashMap<>();

        Set<String> idc = new HashSet<>();
        for (TableDefinition item : this.getItemList()) {
            if (null != item.getSiteUID()) {
                idc.add(item.getSiteUID());
            }
        }
        if (!idc.isEmpty()) {
            List<OrgInfo> orgs = orgDao.findByUids(idc);
            for (OrgInfo org : orgs) {
                mapping.put(org.getUid(), org);
            }
        }

        return mapping;
    }

    @Override
    protected TableDefinitionRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TableDefinition> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            orgUidList.add(UserContextService.getCurrentUserAccount().getHospitalUID());
            orgUidList.add(UserContextService.getCurrentUserAccount().getInstitutionUID());
            orgUidList.add(UserContextService.getCurrentUserAccount().getTenantUID());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, TableDefinition.TableDefinitionStatus.Expired.toString()));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    @Override
    public List<TableDefinition> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    public void setToUsing(String id, Boolean isUsing) {
        TableDefinition selectedSchema = dao.findById(id);
        selectedSchema.setStatus(isUsing ? TableDefinition.TableDefinitionStatus.Draft.toString() : TableDefinition.TableDefinitionStatus.Released.toString());
        dao.save(selectedSchema);
    }

    public void deleteTemplate() {
        templateService.deleteTemplate(selected);
    }

    public String getEditUrl() {
        return "createTable.xhtml?faces-redirect=true&amp;id=" + selected.getId();
    }

    public String getCreateUrl() {
        return "JSON".equals(createType) ? "createTable.xhtml?faces-redirect=true" : "createExcelTable.xhtml?faces-redirect=true";
    }

    public void cancel() {
        this.selected = null;
    }

    public void onSchemaSelected() {

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
    }

    public boolean getIsReadOnly(TableDefinition tableItem) {
        return !Arrays.asList(tableItem.getTenantUID(), tableItem.getInstitutionUID(), tableItem.getHospitalUID(), tableItem.getSiteUID()).contains(UserContextService.getCurrentUserAccount().getSiteUID());
    }

    public void updateAllTemplatesJsonField(TableDefinition selectedTable) {
        TableSchema table = JSONObject.parseObject(selectedTable.getSchema(), TableSchema.class);
        //oldKey-newKey
        Map<String, String> tempMap = new HashMap();
        table.getGroups().forEach(groupItem -> {
            groupItem.getItems().forEach(fieldItem -> {
                if (!Strings.isNullOrEmpty(fieldItem.getKey()) && !fieldItem.getKey().matches("^[A-Za-z0-9_]+$")) {
                    if (tempMap.containsKey(fieldItem.getKey())) {
                        fieldItem.setKey(tempMap.get(fieldItem.getKey()));
                    } else {
                        String newKey = UUID.randomUUID().toString().replaceAll("-", "");
                        tempMap.put(fieldItem.getKey(), newKey);
                        fieldItem.setKey(newKey);
                    }
                }

                if (!Strings.isNullOrEmpty(fieldItem.getResKey()) && !fieldItem.getResKey().matches("^[A-Za-z0-9_]+$")) {
                    if (tempMap.containsKey(fieldItem.getResKey())) {
                        fieldItem.setResKey(tempMap.get(fieldItem.getResKey()));
                    } else {
                        String newKey = fieldItem.getKey().concat("_res");
                        tempMap.put(fieldItem.getResKey(), newKey);
                        fieldItem.setResKey(newKey);
                    }
                }

            });

        });

        table.getGroups().forEach(groupItem -> {
            groupItem.getItems().forEach(fieldItem -> {
                if (null != fieldItem.getExp() && null != fieldItem.getExp().get("Y_Key") && !fieldItem.getExp().get("Y_Key").matches("^[A-Za-z0-9_]+$")) {
                    if (tempMap.containsKey(fieldItem.getExp().get("Y_Key"))) {
                        fieldItem.getExp().put("Y_Key", tempMap.get(fieldItem.getExp().get("Y_Key")));
                    } else {
                        String newKey = UUID.randomUUID().toString().replaceAll("-", "");
                        tempMap.put(fieldItem.getExp().get("Y_Key"), newKey);
                        fieldItem.getExp().put("Y_Key", newKey);
                    }
                }

                if (null != fieldItem.getExp() && null != fieldItem.getExp().get("Z_Key") && !fieldItem.getExp().get("Z_Key").matches("^[A-Za-z0-9_]+$")) {
                    if (tempMap.containsKey(fieldItem.getExp().get("Z_Key"))) {
                        fieldItem.getExp().put("Z_Key", tempMap.get(fieldItem.getExp().get("Z_Key")));
                    } else {
                        String newKey = UUID.randomUUID().toString().replaceAll("-", "");
                        tempMap.put(fieldItem.getExp().get("Z_Key"), newKey);
                        fieldItem.getExp().put("Z_Key", newKey);
                    }
                }
            });
        });
        
        selectedTable.setSchema(JSONObject.toJSONString(table));
        dao.save(selectedTable);

        UnstructuredDataCollectionRepository udcDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        UnstructuredDataRepository udcDataDao = WebUtil.getBean(UnstructuredDataRepository.class);
        List<SearchFilter> searchFilterList = new ArrayList();
        searchFilterList.add(new SearchFilter("schemaId", SearchFilter.Operator.EQ, selectedTable.getId()));
        List<UnstructuredDataCollection> reportsList = udcDao.findBySearchFilter(searchFilterList);

        reportsList.forEach(reportItem -> {
            List<UnstructuredData> itemList = reportItem.getData();
            itemList.stream().filter(item -> tempMap.containsKey(item.getKey())).forEach(item -> item.setKey(tempMap.get(item.getKey())));
            udcDataDao.save(itemList);
        });

        WebUtil.addSuccessMessage("updated " + reportsList.size() + " reports");
        
    }

    
    String filedsFindTenantUid;

    public String getFiledsFindTenantUid() {
        return filedsFindTenantUid;
    }

    public void setFiledsFindTenantUid(String filedsFindTenantUid) {
        this.filedsFindTenantUid = filedsFindTenantUid;
    }
    
    public List<TableDefinition> findAllChineseKey() {
        List<TableDefinition> tableList;
        if(Strings.isNullOrEmpty(filedsFindTenantUid)){
            tableList = dao.find();
        }else{
            List<SearchFilter> searchFilterList = new ArrayList();
            searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, filedsFindTenantUid));
            tableList = dao.findBySearchFilter(searchFilterList);
        }
        
        
        System.out.println("===========count=" + tableList.size());
        return tableList.stream().filter(tableItem -> {
            try{
                TableSchema table = JSONObject.parseObject(tableItem.getSchema(), TableSchema.class);
            
            return table.getGroups().stream()
                    .anyMatch(groupItem -> groupItem.getItems().stream().anyMatch(fieldItem -> !fieldItem.getKey().matches("^[A-Za-z0-9_]+$")
                    || (!Strings.isNullOrEmpty(fieldItem.getResKey()) && !fieldItem.getResKey().matches("^[A-Za-z0-9_]+$"))
                    || (null != fieldItem.getExp() && null != fieldItem.getExp().get("Y_Key") && !fieldItem.getExp().get("Y_Key").matches("^[A-Za-z0-9_]+$"))
                    || (null != fieldItem.getExp() && null != fieldItem.getExp().get("Z_Key") && !fieldItem.getExp().get("Z_Key").matches("^[A-Za-z0-9_]+$"))));
            }catch(Exception ex){
                System.out.println("==================>Error:" +tableItem.getId() + "---" + tableItem.getSchema());
                return false;
            }
        }).collect(Collectors.toList());
    }

    //geter and setter
    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public Map<String, OrgInfo> getOrgInfoMap() {
        return orgInfoMap;
    }

    public void setOrgInfoMap(Map<String, OrgInfo> orgInfoMap) {
        this.orgInfoMap = orgInfoMap;
    }
}
