package com.ge.apm.view.report;

import com.ge.apm.dao.TableDefinitionRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class TableDefinitionController extends GenericCRUDUUIDController<TableDefinition> {

    private TableDefinitionRepository dao;

    private TableDefinitionService templateService;

    private UaaService uaaService;

    private String createType = "JSON";

    @Override
    protected void init() {

        dao = WebUtil.getBean(TableDefinitionRepository.class);
        templateService = WebUtil.getBean(TableDefinitionService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
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
    
     public boolean getIsReadOnly(TableDefinition tableItem){
        return !Arrays.asList(tableItem.getTenantUID(),tableItem.getInstitutionUID(),tableItem.getHospitalUID(),tableItem.getSiteUID()).contains(UserContextService.getCurrentUserAccount().getSiteUID());
    }

    //geter and setter
    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

}
