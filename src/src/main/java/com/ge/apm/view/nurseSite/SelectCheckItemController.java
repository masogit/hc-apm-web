package com.ge.apm.view.nurseSite;

import com.alibaba.fastjson.JSONArray;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.NurseSiteMaintainCheckItemRepository;
import com.ge.apm.domain.AssetDepartmentConvert;
import com.ge.apm.domain.NurseSiteMaintainCheckItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SelectCheckItemController extends GenericCRUDUUIDController<NurseSiteMaintainCheckItem> implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectCheckItemController.class);
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private NurseSiteMaintainCheckItemRepository dao;
	private Map<String, Boolean> selectedMap;
	private List<NurseSiteMaintainCheckItem> currentPage;

    private ServerEventInterface callingController;
    private OrgInfo orgInfo;
    protected void init() {
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        dao= WebUtil.getBean(NurseSiteMaintainCheckItemRepository.class);
        selectedMap = new HashMap<>();
    }

    @Override
    protected GenericRepositoryUUID<NurseSiteMaintainCheckItem> getDAO(){
        return dao;
    }

    @Override
    protected Page<NurseSiteMaintainCheckItem> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(orgInfo != null){
            List<String> list = Arrays.asList(orgInfo.getTenantUID(), orgInfo.getHospitalUID(), orgInfo.getInstitutionUID(), orgInfo.getSiteUID());
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, list, "1*"));
        }else{
            List<String> list = Arrays.asList(currentUser.getTenantUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getSiteUID());
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, list, "1*"));
        }
        //need list or set
        Page<NurseSiteMaintainCheckItem> pageResult=dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPage = pageResult.getContent();
        return pageResult;
    }

	public List<String> getGroupNames(){
		List<String> groupNames = dao.findGroupName(currentUser.getSiteUID());
		groupNames=groupNames.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
		return groupNames;
	}


    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPage.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (NurseSiteMaintainCheckItem item : currentPage) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void onServerEvent(String eventName, Object eventObject){
        if(eventObject instanceof OrgInfo){
            orgInfo = (OrgInfo)eventObject;
        }
    }

    public void setCallingController(ServerEventInterface callingController) {
        this.callingController = callingController;
    }

    public void saveSelectCheckItem(){
        List<String> collect = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("id", SearchFilter.Operator.IN,collect));
        List<NurseSiteMaintainCheckItem> bySearchFilter = dao.findBySearchFilter(searchFilters);
        ((MaintainConfigController)callingController).saveSelectCheckList(bySearchFilter);
        selectedMap.clear();

    }
}
