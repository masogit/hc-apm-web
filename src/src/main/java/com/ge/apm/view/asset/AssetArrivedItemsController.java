package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedItemsController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository dao = null;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private List<V2_BlobObject> picList;
    
    private ArrivalListItem  selectedItem;
    
    @Override
    protected void init() {
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
    }

    @Override
    protected ArrivalListItemRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }

        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onRowSelect(SelectEvent event) {
        this.selected = (ArrivalListItem) event.getObject();
        picList = aiService.getPicList(selected.getId());
    }

    public void prepareTransOrder(String itemId){
        selectedItem = dao.findById(itemId);
    }
    
    public List<UserAccount> getWorkerList(){
        return aiService.getWorkerList();
    }
    
    public void reassignWorkOrder(){
        aiService.reAssignWorkOrder(selectedItem);
        cancel();
    }
    
    public Boolean isAssignToMe(String currentPersonUID){
        return UserContextService.getCurrentUserAccount().getUid().equals(currentPersonUID);
    }
    
    @Override
    public List<ArrivalListItem> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    public void cancel() {
        this.selected = null;
        selectedItem = null;
    }

    public OrgInfo getOrgInfoByUid(String orgUid){
        return aiService.getOrgInfoByUid(orgUid);
    }
    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public List<V2_BlobObject> getAckPicList(String type) {
        return picList == null ? null : picList.stream().filter(item -> type.equals(item.getBoSubType())).collect(Collectors.toList());
    }

    public ArrivalListItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ArrivalListItem selectedItem) {
        this.selectedItem = selectedItem;
    }

}
