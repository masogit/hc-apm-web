package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_DeviceType;
import com.ge.apm.domain.V2_WorkflowConfig;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkflowConfigController extends JpaCRUDController<V2_WorkflowConfig> {

	private static final long serialVersionUID = 1L;
	private V2_WorkflowConfigRepository dao = null;
	private UserAccountRepository userDao;
	private UserAccount user;
	private UserAccount owner;
	private List<UserAccount> dispatchUserList;
	private List<UserInfo> userList;
	private List<V2_DeviceType> deviceTypeList;
	private List<OrgInfo> institutionList;
	private List<OrgInfo> hospitalList;
	private List<OrgInfo> siteList;
	private Integer selectedHospitalId;
	private Integer selectedSiteId;
	private OrgInfoRepository orgInfoRepository;
	private Integer selectedInstitutionId;
	@Override
	protected void init() {
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(V2_WorkflowConfigRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		institutionList = orgInfoRepository.findByTenantUIDAndOrgType(user.getTenantUID(),1);
		initDispatchUserList();
	}

	private void initDispatchUserList() {
		List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
		if(selectedSiteId != null){
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
	        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
	        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
	        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
		}else if(selectedHospitalId != null){
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
	        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
	        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
		}else if(selectedInstitutionId != null){
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
	        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
		}else{
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		}
		dispatchUserList = userDao.findBySearchFilter(searchFilters);
	}

	public List<OrgInfo> getIncludeOrgInfo(Integer orgId,Integer orgType){
		return orgInfoRepository.findByParentUIDAndOrgType(orgInfoRepository.findById(orgId).getUid(),orgType);
	}
	
	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	@Override
	protected V2_WorkflowConfigRepository getDAO() {
		return dao;
	}
	
	public String getOrgNameById(Integer orgId){
		return orgInfoRepository.findById(orgId).getName();
	}
	
/*	public void test(){
		System.out.println("++++++++++++++"+selectedInstitutionId);
	}*/

	@Override
	protected Page<V2_WorkflowConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
        return dao.findBySearchFilter(searchFilters, pageRequest);
	}

	public void onOwnerChange() {
		if (null != owner) {
			selected.setDispatchUserId(owner.getId());
			selected.setDispatchUserName(owner.getName());
		}
	}
	
	public String getTypeName(Integer deviceType){
		for (V2_DeviceType dt : deviceTypeList) {
			if(dt.getId().equals(deviceType)){
				return dt.getName();
			}
		}
		return null;
	}

	@Override
	public List<V2_WorkflowConfig> getItemList() {
		return dao.find();
	}

	public UserAccount getUser() {
		return user;
	}

	public void setUser(UserAccount user) {
		this.user = user;
	}

	public List<UserAccount> getDispatchUserList() {
		return dispatchUserList;
	}

	public void setDispatchUserList(List<UserAccount> dispatchUserList) {
		this.dispatchUserList = dispatchUserList;
	}

	@Override
	public void onBeforeSave(V2_WorkflowConfig config) {
		selected.setTenantUID(user.getTenantUID());
		selected.setInstitutionUID(user.getInstitutionUID());
		selected.setHospitalUID(user.getHospitalUID());
		selected.setSiteUID(user.getSiteUID());
		selected.setLevelInstitutionId(selectedInstitutionId);
		selected.setLevelHospitalId(selectedHospitalId);
		selected.setLevelSiteId(selectedSiteId);
		UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
		if (selected.getDispatchUserId() != null) {
			UserAccount dispather1 = uaDao.getById(config.getDispatchUserId());
			if (dispather1 != null) {
				selected.setDispatchUserName(dispather1.getName());
			}
		}
		if (selected.getDispatchUserId2() != null) {
			UserAccount dispather2 = uaDao.getById(config.getDispatchUserId2());
			if (dispather2 != null) {
				selected.setDispatchUserName2(dispather2.getName());
			}
		}
	}
	
    public void save() {
    	onBeforeSave(selected);
        if (selected.hashCode() == 0) {
            create();
        } else {
            update();
        }
        selected.setLevelInstitutionId(null);
		selected.setLevelHospitalId(null);
		selected.setLevelSiteId(null);
    }

	public UserAccount getOwner() {
		return owner;
	}

	public void setOwner(UserAccount owner) {
		this.owner = owner;
	}

	public List<V2_DeviceType> getDeviceTypeList() {
		return deviceTypeList;
	}

	public void setDeviceTypeList(List<V2_DeviceType> deviceTypeList) {
		this.deviceTypeList = deviceTypeList;
	}

	public List<OrgInfo> getInstitutionList() {
		return institutionList;
	}

	public void setInstitutionList(List<OrgInfo> institutionList) {
		this.institutionList = institutionList;
	}

	public List<OrgInfo> getHospitalList() {
		return hospitalList;
	}

	public void setHospitalList(List<OrgInfo> hospitalList) {
		this.hospitalList = hospitalList;
	}

	public List<OrgInfo> getSiteList() {
		return siteList;
	}

	public void setSiteList(List<OrgInfo> siteList) {
		this.siteList = siteList;
	}

	public Integer getSelectedHospitalId() {
		return selectedHospitalId;
	}

	public void setSelectedHospitalId(Integer selectedHospitalId) {
		this.selectedHospitalId = selectedHospitalId;
	}

	public Integer getSelectedSiteId() {
		return selectedSiteId;
	}

	public void setSelectedSiteId(Integer selectedSiteId) {
		this.selectedSiteId = selectedSiteId;
	}

	public Integer getSelectedInstitutionId() {
		return selectedInstitutionId;
	}

	public void setSelectedInstitutionId(Integer selectedInstitutionId) {
		this.selectedInstitutionId = selectedInstitutionId;
	}
	
	
}