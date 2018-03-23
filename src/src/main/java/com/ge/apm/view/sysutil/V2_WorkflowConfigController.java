package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
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
import com.ge.apm.service.common.UserCommonService;

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
	private List<UserAccount> dispatchUserList = new ArrayList<UserAccount>();
	private List<UserInfo> userList;
	private List<V2_DeviceType> deviceTypeList;
	private List<OrgInfo> institutionList;
	private List<OrgInfo> hospitalList;
	private List<OrgInfo> siteList;
	private OrgInfoRepository orgInfoRepository;
	private TenantInfoRepository tenantInfoRepository;
	private UserCommonService ucService;
	@Override
	protected void init() {
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(WorkflowConfigRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
		ucService = WebUtil.getBean(UserCommonService.class);
		institutionList = orgInfoRepository.findByTenantUIDAndOrgType(user.getTenantUID(),1);
		initDispatchUserList();
	}

	public void initDispatchUserList() {
		dispatchUserList = userDao.getByTenantUID(user.getTenantUID());
		/*
		dispatchUserList.clear();
		Set<String> uids = new HashSet<String>();
		String tenantUID = user.getTenantUID();
		if(selected == null){
			*//**
			 * 默认级别：租户
			 *//*
			dispatchUserList = userDao.getByTenantUID(tenantUID);
			return;
		}else{
			uids.add(selected.getTenantUID());
			selected.setTenantUID(tenantUID);
			
			*//***
			 * 租户级别
			 *//*
			if(isNullOrBelongs(selected.getInstitutionUID(),uids) || selected.getTenantUID().equals(selected.getInstitutionUID())){
				System.out.println("租户级别");
				dispatchUserList = userDao.getByTenantUID(tenantUID);
				return;
			}else{
				*//***
				 * 集团级别
				 *//*
				uids.add(selected.getInstitutionUID());
				if(isNullOrBelongs(selected.getHospitalUID(),uids) || selected.getInstitutionUID().equals(selected.getHospitalUID())){
					System.out.println("集团级别");
					dispatchUserList = userDao.getByInstitutionUID(selected.getInstitutionUID());
					return;
				}else{
					*//***
					 * 医院级别
					 *//*
					uids.add(selected.getHospitalUID());
					if(isNullOrBelongs(selected.getSiteUID(),uids) || selected.getHospitalUID().equals(selected.getSiteUID())){
						System.out.println("医院级别");
						dispatchUserList = userDao.getByHospitalUID(selected.getHospitalUID());
						return;
					}else{
						System.out.println("院区级别");
						dispatchUserList = userDao.getBySiteUID(selected.getSiteUID());
						return;
					}
				}
			}
		}
	*/}
	
	private boolean isNullOrBelongs(String uid,Set<String> uids){
		if(uid == null){
			return true;
		}
		if(uids.contains(uid)){
			return true;
		}
		return false;
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
	
	public String getInsNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 1){
			return oi.getName();
		}
		return null;
	}

	public String getHospitalNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 2){
			return oi.getName();
		}
		return null;
	}

	public String getSiteNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 3){
			return oi.getName();
		}
		return null;
	}
	
	public String getTenantName(String tenantUID){
		return tenantInfoRepository.findByuid(tenantUID).getName();
	}

        public List<UserAccount> getAssetCreateApproverlList(){
            return ucService.getAssetCreateApproverlList(UserContextService.getCurrentUserAccount().getTenantUID());
        }
	@Override
	protected Page<WorkflowConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
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

		//pm
		if (selected.getPmDispatchUserId() != null) {
			UserAccount pmDispather1 = uaDao.getById(config.getPmDispatchUserId());
			if (pmDispather1 != null) {
				selected.setPmDispatchUserName(pmDispather1.getName());
			}
		}else{
			selected.setDispatchUserName(null);
		}
		if (selected.getPmDispatchUserId2() != null) {
			UserAccount pmDispather2 = uaDao.getById(config.getPmDispatchUserId2());
			if (pmDispather2 != null) {
				selected.setPmDispatchUserName2(pmDispather2.getName());
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
		Boolean isPmManual = selected.getIsPmManualEnabled();
		Boolean isPmTakeOrder = selected.getIsPmTakeOrderEnabled();

    	if(isManual != null && isManual){
    		if(selected.getDispatchUserId() == null && selected.getDispatchUserId2() == null){
    			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
       		 	WebUtil.addErrorMessage(WebUtil.getMessage("WoNeedDispather"));
       		 	return;
    		}
    	}
    	if(isPmManual != null && isPmManual){
			if(selected.getPmDispatchUserId() == null && selected.getPmDispatchUserId2() == null){
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				WebUtil.addErrorMessage(WebUtil.getMessage("PmNeedDispather"));
				return;
			}
		}


    	if(isManual != null && isTakeOrder != null && !isManual && !isTakeOrder){
    		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
    		 WebUtil.addErrorMessage(WebUtil.getMessage("WoManualAndGrapMsg"));
    		 return;
    	}

    	if(isPmManual != null && isPmTakeOrder != null && !isPmManual && !isPmTakeOrder){
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("PmManualAndGrapMsg"));
			return;
		}

    	//onBeforeSave(selected);
    	super.save();
    }
	
	@Override
	public void prepareEdit(){
		if(selected != null){
			initDispatchUserList();
		}
		super.prepareEdit();
	}
	
	public void onSelect(){
		if(selected != null && selected.getId() != null){
			selected = dao.findById(selected.getId());
		}
	}
	
	public void updateHospitalAndSite(){
		selected.setHospitalUID(null);
		selected.setSiteUID(null);
		dispatchUserList = userDao.getByInstitutionUID(selected.getInstitutionUID());
	}
	
	public void updateSite(){
		selected.setSiteUID(null);
		dispatchUserList = userDao.getByHospitalUID(selected.getHospitalUID());
	}
	
	public void updateDispaters(){
		dispatchUserList = userDao.getBySiteUID(selected.getSiteUID());
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