package com.ge.apm.view.sysutil;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.V2_WorkOrderWorkflowConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_WorkOrderWorkflowConfig;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkOrderWorkflowConfigController extends JpaCRUDController<V2_WorkOrderWorkflowConfig> {
	public static final String EVENT_NAME = "紧急工单工作流配置";
	private static final long serialVersionUID = 1L;
	private V2_WorkOrderWorkflowConfigRepository dao = null;
	private UserAccountRepository userDao;
	private UserAccount user;
	private UserAccount owner;
	private List<UserAccount> dispatchUserList;
	private List<UserInfo> userList;

	@Override
	protected void init() {
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(V2_WorkOrderWorkflowConfigRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		initDispatchUserList();
		
		selected = dao.findByTenantUID(user.getTenantUID());
				//dao.findByTenantUIDAndInstitutionUIDAndHospitalUIDAndSiteUID(user.getTenantUID(),user.getInstitutionUID(), user.getHospitalUID(), user.getSiteUID());
		if(selected == null){
			selected = new V2_WorkOrderWorkflowConfig();
			selected.setEventName(EVENT_NAME);
			selected.setTenantUID(user.getTenantUID());
			selected.setInstitutionUID(user.getTenantUID());
			selected.setHospitalUID(user.getTenantUID());
			selected.setSiteUID(user.getTenantUID());
//			selected.setInstitutionUID(user.getInstitutionUID());
//			selected.setHospitalUID(user.getHospitalUID());
//			selected.setSiteUID(user.getSiteUID());
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
	protected V2_WorkOrderWorkflowConfigRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<V2_WorkOrderWorkflowConfig> loadData(PageRequest pageRequest) {
		
		return dao.findAll(pageRequest);
	}

	public void onOwnerChange() {
		if (null != owner) {
			selected.setDispatchUserId(owner.getId());
			selected.setDispatchUserName(owner.getName());
		}
	}

	@Override
	public List<V2_WorkOrderWorkflowConfig> getItemList() {
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
	public void onBeforeSave(V2_WorkOrderWorkflowConfig config) {
		selected.setTenantUID(user.getTenantUID());
		selected.setInstitutionUID(user.getTenantUID());
		selected.setHospitalUID(user.getTenantUID());
		selected.setSiteUID(user.getTenantUID());
//		selected.setInstitutionUID(user.getInstitutionUID());
//		selected.setHospitalUID(user.getHospitalUID());
//		selected.setSiteUID(user.getSiteUID());
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

	public UserAccount getOwner() {
		return owner;
	}

	public void setOwner(UserAccount owner) {
		this.owner = owner;
	}
}