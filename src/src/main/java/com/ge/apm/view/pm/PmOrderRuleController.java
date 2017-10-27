package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.dao.PmOrderRuleItemRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.PmOrderRuleCheckList;
import com.ge.apm.domain.PmOrderRuleItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class PmOrderRuleController extends JpaCRUDController<PmOrderRule> {
	private static final long serialVersionUID = -1L;
	private PmOrderRuleRepository dao = null;
	private UserAccount user;
	private UserAccountRepository userAccountRepository;
	private PmOrderRuleItemRepository pmOrderRuleItemRepository;
	private PmOrderRuleCheckListRepository pmOrderRuleCheckListRepository;
	private List<Integer> ids = new ArrayList<Integer>();
	private String filterPmRuleStatus;

	@Override
	protected void init() {
		filterBySite = false;
		dao = WebUtil.getBean(PmOrderRuleRepository.class);
		user = UserContextService.getCurrentUserAccount();
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		pmOrderRuleItemRepository = WebUtil.getBean(PmOrderRuleItemRepository.class);
		pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
	}

	@Override
	protected PmOrderRuleRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<PmOrderRule> loadData(PageRequest pageRequest) {
//		List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
		if(searchFilters == null){
			searchFilters = new ArrayList<SearchFilter>();
		}
		if(!this.hasFilterOnField("tenantUID")){
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		}
		return dao.findBySearchFilter(searchFilters, pageRequest);
		//return dao.findAll(pageRequest);
	}
	
	public List<Integer> getSelectedIds(){
		if(selected != null){
			ids = pmOrderRuleCheckListRepository.findItemIds(user.getTenantUID(),selected.getId());
		}
		return ids;
	}
	
	public List<PmOrderRuleItem> getAllItems(){
		List<PmOrderRuleItem> items = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
		if(CollectionUtils.isEmpty(items)){
			items = new ArrayList<PmOrderRuleItem>();
		}
		return items;
	}

	public List<PmOrderRuleCheckList> getRuleItemList() {
		List<PmOrderRuleCheckList> itemFromDB = pmOrderRuleCheckListRepository.findByTenantUID(user.getTenantUID());
		if (CollectionUtils.isEmpty(itemFromDB)) {
			itemFromDB = changeItem2CheckList();
		}
		return itemFromDB;
	}
	
	public List<PmOrderRuleCheckList> changeItem2CheckList(){
		List<PmOrderRuleItem> allItems = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
		List<PmOrderRuleCheckList> checkLists = new ArrayList<PmOrderRuleCheckList>();
		for (PmOrderRuleItem item : allItems) {
			PmOrderRuleCheckList cl = new PmOrderRuleCheckList();
			cl.setName(item.getName());
			cl.setPeriod(item.getPeriod());
			cl.setItemId(item.getId());
			cl.setTenantUID(item.getTenantUID());
			cl.setPmOrderRuleId(item.getId());
			cl.setStatus(0);
			checkLists.add(cl);
		}
		return checkLists;
	}

	@Override
	@Transactional
	public void onBeforeSave(PmOrderRule por) {
		pmOrderRuleCheckListRepository.deleteExistsRecord(por.getId());
	}

	public List<UserAccount> getApproveUserList(String tenantUID) {
		return userAccountRepository.getAssetHeadList(tenantUID);
	}

	public UserAccount getUser() {
		return user;
	}

	public void setUser(UserAccount user) {
		this.user = user;
	}

	@Override
	public void onBeforeNewObject(PmOrderRule por) {
		Integer period = pmOrderRuleItemRepository.fetchMinPeriod(user.getTenantUID());
		period =  period == null ? 1 : period;
		por.setPeriod(period);
		por.setTenantUID(user.getTenantUID());
		por.setStatus(0);
		por.setCreatedDate(new Date());
		por.setOwnerId(user.getId());
	}

	public String getUserName(Integer userId) {
		if (userId == null) {
			return null;
		}
		UserAccount user = userAccountRepository.findById(userId);
		if (user == null) {
			return null;
		}
		return user.getName();
	}

	@Transactional
	public void applyChange() {
		if (selected.getId() != null) {//更新
			onBeforeSave(selected);
		}
		dao.save(selected);
		Integer pmOrderRuleId = selected.getId();
		List<PmOrderRuleCheckList> checkLists = new ArrayList<PmOrderRuleCheckList>();
		List<PmOrderRuleItem> allItems = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
		if(CollectionUtils.isNotEmpty(allItems)){
			for (PmOrderRuleItem item : allItems) {
				PmOrderRuleCheckList cl = new PmOrderRuleCheckList();
				cl.setName(item.getName());
				cl.setPeriod(item.getPeriod());
				cl.setItemId(item.getId());
				cl.setTenantUID(item.getTenantUID());
				cl.setPmOrderRuleId(pmOrderRuleId);
				if (ids.contains(item.getId().toString())) {
					cl.setStatus(1);
				} else {
					cl.setStatus(0);
				}
				checkLists.add(cl);
			}
			pmOrderRuleCheckListRepository.save(checkLists);
		}
		ids.clear();
	}
	
	public void approve(){
		selected.setStatus(1);
		selected.setApproveUserId(user.getId());
		dao.save(selected);
		WebUtil.addSuccessMessage(WebUtil.getMessage("approveSuccess"));
	}
	
	 public void setAssetStatusFilter() {
	        if (filterPmRuleStatus == null) {
	            return;
	        }
	        if (searchFilters == null) {
	            searchFilters = new ArrayList<SearchFilter>();
	        }
	        if (filterPmRuleStatus != null) {
	            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, Integer.parseInt(filterPmRuleStatus)));
	        }
	    }

	public String getFilterPmRuleStatus() {
		return filterPmRuleStatus;
	}

	public void setFilterPmRuleStatus(String filterPmRuleStatus) {
		this.filterPmRuleStatus = filterPmRuleStatus;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	

	/*
	 * @Override public void onBeforeNewObject(PmOrderRule object) { }
	 * 
	 * @Override public void onAfterNewObject(PmOrderRule object, boolean isOK)
	 * { }
	 * 
	 * @Override public void onBeforeUpdateObject(PmOrderRule object) { }
	 * 
	 * @Override public void onAfterUpdateObject(PmOrderRule object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeDeleteObject(PmOrderRule object) { }
	 * 
	 * @Override public void onAfterDeleteObject(PmOrderRule object, boolean
	 * isOK) { }
	 * 
	 * @Override public void onBeforeSave(PmOrderRule object) { }
	 * 
	 * @Override public void onAfterDataChanged(){ };
	 */
}