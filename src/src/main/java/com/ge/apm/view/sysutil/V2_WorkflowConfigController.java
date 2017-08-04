package com.ge.apm.view.sysutil;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.DeviceTypeRepository;
import com.ge.apm.dao.V2_WorkflowConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_DeviceType;
import com.ge.apm.domain.V2_WorkflowConfig;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkflowConfigController extends JpaCRUDController<V2_WorkflowConfig> {

	private static final long serialVersionUID = 1L;
	private V2_WorkflowConfigRepository dao = null;
	private DeviceTypeRepository deviceTypeDao;
	private UserAccountRepository userDao;
	private UserAccount user;
	private UserAccount owner;
	private List<UserAccount> dispatchUserList;
	private List<UserInfo> userList;
	private List<V2_DeviceType> deviceTypeList;

	@Override
	protected void init() {
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(V2_WorkflowConfigRepository.class);
		deviceTypeDao = WebUtil.getBean(DeviceTypeRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		
		initDispatchUserList();
		
		deviceTypeList = deviceTypeDao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(user.getTenantUID(),
				user.getInstitutionUID(), user.getHospitalUID(), user.getSiteUID());
		
		selected = dao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(user.getTenantUID(),
				user.getInstitutionUID(), user.getHospitalUID(), user.getSiteUID());
		if(selected == null){
			selected = new V2_WorkflowConfig();
			selected.setTenantUID(user.getTenantUID());
			selected.setInstitutionUID(user.getInstitutionUID());
			selected.setHospitalUID(user.getHospitalUID());
			selected.setSiteUID(user.getSiteUID());
		}
	}

	private void initDispatchUserList() {
		userList = userDao.findByInstitutionUID(user.getInstitutionUID());
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

	@Override
	protected Page<V2_WorkflowConfig> loadData(PageRequest pageRequest) {
		return dao.findAll(pageRequest);
//		if (this.searchFilters == null) {
//			return dao.findAll(pageRequest);
//		} else {
//			return dao.findBySearchFilter(this.searchFilters, pageRequest);
//		}
	}

	public void onOwnerChange() {
		if (null != owner) {
			selected.setDispatchUserId(owner.getId());
			selected.setDispatchUserName(owner.getName());
		}
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
		UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
		if (selected.getDispatchUserId() != null) {
			UserAccount dispather1 = uaDao.getById(selected.getDispatchUserId());
			if (dispather1 != null) {
				config.setDispatchUserName(dispather1.getName());
			}
		}
		if (selected.getDispatchUserId2() != null) {
			UserAccount dispather2 = uaDao.getById(selected.getDispatchUserId2());
			if (dispather2 != null) {
				config.setDispatchUserName2(dispather2.getName());
			}
		}
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
}