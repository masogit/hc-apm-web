package com.ge.apm.view.asset;

import com.ge.apm.dao.DataEntryRepository;

import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.domain.DataEntry;

import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.service.asset.DataEntryService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.his.HisAssetGroupEditController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.TabChangeEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class DataEntryController extends GenericCRUDUUIDController<DataEntry> implements Serializable, ServerEventInterface {


    private DataEntryRepository dataEntryRepository;
    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchDao;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;

    private DataEntryCreateController dataEntryCreateController;

    private UserAccount user;
    private UaaService uaaService;

    private DataEntryService dataEntryService;

    public int type=1;

    @Getter
    @Setter
    private String filterEntryType;

    @Getter
    @Setter
    public Map<String, Object> selectedObject;
    @Override
    protected GenericRepositoryUUID<DataEntry> getDAO() {
        return null;
    }


    @Override
    protected void init() {
        dataEntryRepository = WebUtil.getBean(DataEntryRepository.class);
        dataEntryService = WebUtil.getBean(DataEntryService.class);

        hisAssetGroupAssetMatchDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        dataEntryCreateController = WebUtil.getBean(DataEntryCreateController.class);

        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        this.selectedObject = new HashMap<>();
    }

    @Override
    protected Page<DataEntry> loadData(PageRequest pageRequest) {

        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if(type==1){
            this.updateSearchFilter(new SearchFilter("groupOrSingle", SearchFilter.Operator.EQ, 1 ));

        }else{
            this.updateSearchFilter(new SearchFilter("groupOrSingle", SearchFilter.Operator.EQ, 2 ));

        }
        this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        if (!this.hasFilterOnField("siteUid")) {
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        Page<DataEntry> page = dataEntryRepository.findBySearchFilter(this.searchFilters, pageRequest);
        return page;
    }

    @Override
    public void prepareCreate(){
        selected=null;
        dataEntryCreateController.onServerEvent(null, null);

    }

    @Override
    public void prepareEdit() {
        dataEntryCreateController.onServerEvent(null, selected);
    }



   public DataEntry onSelectDataEntry(){
        return selected;
   }

    @Override
    public void delete() {

        if(selected==null){
            return;
        }

        dataEntryService.deleteDataEntry(selected);


    }

    public void onTabChange(TabChangeEvent event) {
//        FacesMessage msg = new FacesMessage("activeTab", event.getTab().getTitle());
//
//        selected=null;
//        this.selectedObject = new HashMap<>();
//        filterEntryType=null;
//        this.removeFilterOnField("entryType");
//        this.removeFilterOnField("endDate");
//        this.removeFilterOnField("startDate");
//        this.removeFilterOnField("lastModifiedDate");

    }
}
