package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedListController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;

    private AssetInstallationService aiService;
    private UaaService uaaService;


    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
    }

    @Override
    protected ArrivalListRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalList> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }
    
    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            WebUtil.addErrorMessage("到货清单已经提交,不能删除");
        }
    }

    public String createAction(){
        return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Create");
    }
    
    public String editAction(){
         return UrlEncryptController.encodeUrlParam("arrivalListEdit?action=Edit&listId="+selected.getId());
    }
    
    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }
}
