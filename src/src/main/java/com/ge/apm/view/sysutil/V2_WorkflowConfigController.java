package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkflowConfigRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_DeviceType;
import com.ge.apm.domain.V2_WorkflowConfig;
import com.ge.apm.domain.WorkflowConfig;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkflowConfigController extends JpaCRUDController<WorkflowConfig> {

	private static final long serialVersionUID = 1L;
	private WorkflowConfigRepository dao = null;
	private UserAccountRepository userDao;
	private UserAccount user;
	private UserAccount owner;
	private List<UserAccount> dispatchUserList;
	private List<UserInfo> userList;
	private List<V2_DeviceType> deviceTypeList;
	private List<OrgInfo> institutionList;
	private List<OrgInfo> hospitalList;
	private List<OrgInfo> siteList;
//	private String selectedHospitalId;
//	private String selectedSiteId;
//	private String selectedInstitutionId;
	private OrgInfoRepository orgInfoRepository;
	private TenantInfoRepository tenantInfoRepository;
	@Override
	protected void init() {
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(WorkflowConfigRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
		institutionList = orgInfoRepository.findByTenantUIDAndOrgType(user.getTenantUID(),1);
		initDispatchUserList();
	}

	public void initDispatchUserList() {
		String tenantUID = user.getTenantUID();
		if(selected == null){
			dispatchUserList = userDao.getByTenantUID(tenantUID);
		}else{
			if(selected.getSiteUID() != null){
				dispatchUserList = userDao.getBySiteUID(selected.getSiteUID());
				return;
			}else if(selected.getHospitalUID() != null){
				dispatchUserList = userDao.getByHospitalUID(selected.getHospitalUID());
				return;
			}else if(selected.getInstitutionUID() != null){
				dispatchUserList = userDao.getByInstitutionUID(selected.getInstitutionUID());
				return;
			}else {
				dispatchUserList = userDao.getByTenantUID(tenantUID);
			}
			
		}
		
/*		List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
		String tenantUID = user.getTenantUID();
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUID));
		if(selected != null){
			String institutionUID =selected.getInstitutionUID();
			String hospitalUID = selected.getHospitalUID();
			String siteUID = selected.getSiteUID();
			if(institutionUID != null && !institutionUID.equals(tenantUID)){
				searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, institutionUID));
				if(hospitalUID != null && !hospitalUID.equals(institutionUID)){
					searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, hospitalUID));
					if(siteUID != null && !siteUID.equals(hospitalUID)){
						searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUID));
					}
				}
			}
		}
		dispatchUserList = userDao.findBySearchFilter(searchFilters);*/
		
		
/*		if(selectedSiteId != null){
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
		}*/
	
	}

	public List<OrgInfo> getIncludeOrgInfo(String parentUID,Integer orgType){
		return orgInfoRepository.findByParentUIDAndOrgType(parentUID,orgType);
	}
	
	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	@Override
	protected WorkflowConfigRepository getDAO() {
		return dao;
	}
	
	public String getOrgNameById(Integer orgId){
		OrgInfo oi = orgInfoRepository.findById(orgId);
		if(oi == null){
			return null;
		}
		return oi.getName();
	}
	
	public String getOrgNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		return oi.getName();
	}
	
	public String getTenantName(String tenantUID){
		return tenantInfoRepository.findByuid(tenantUID).getName();
	}

	@Override
	protected Page<WorkflowConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
//        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
//        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
//        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
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
	public List<WorkflowConfig> getItemList() {
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

	@Transactional
	@Override
	public void onBeforeSave(WorkflowConfig config) {
		selected.setTenantUID(user.getTenantUID());
		selected.setSiteId(user.getSiteId());
		selected.setHospitalId(user.getHospitalId());
		if(selected.getInstitutionUID() == null){//租户级别
			selected.setInstitutionUID(selected.getTenantUID());
			selected.setHospitalUID(selected.getTenantUID());
			selected.setSiteUID(selected.getTenantUID());
		}
		if(selected.getHospitalUID() == null){//集团级别
			selected.setHospitalUID(selected.getInstitutionUID());
			selected.setSiteUID(selected.getInstitutionUID());
		}
		if(selected.getSiteUID() == null){//医院级别
			selected.setSiteUID(selected.getHospitalUID());
		}

		UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
		if (selected.getDispatchUserId() != null) {
			UserAccount dispather1 = uaDao.getById(config.getDispatchUserId());
			if (dispather1 != null) {
				selected.setDispatchUserName(dispather1.getName());
			}
		}else{
			selected.setDispatchUserName(null);
		}
		if (selected.getDispatchUserId2() != null) {
			UserAccount dispather2 = uaDao.getById(config.getDispatchUserId2());
			if (dispather2 != null) {
				selected.setDispatchUserName2(dispather2.getName());
			}
		}else{
			selected.setDispatchUserName2(null);
		}
		dao.deleteBySiteUID(selected.getSiteUID());
	}
	
	@Transactional
    public void applyChange() {
    	Boolean isManual = selected.getIsManualEnabled();
    	Boolean isTakeOrder = selected.getIsTakeOrderEnabled();
    	if(isManual != null && isManual){
    		if(selected.getDispatchUserId() == null && selected.getDispatchUserId2() == null){
    			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
       		 	WebUtil.addErrorMessage(WebUtil.getMessage("NeedDispather"));
       		 	return;
    		}
    	}
    	if(isManual != null && isTakeOrder != null && !isManual && !isTakeOrder){
    		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
    		 WebUtil.addErrorMessage(WebUtil.getMessage("ManualAndGrapMsg"));
    		 return;
    	}
    	onBeforeSave(selected);
    	super.save();
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
	
}