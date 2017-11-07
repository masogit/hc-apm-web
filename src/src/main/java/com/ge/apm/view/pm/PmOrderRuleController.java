package com.ge.apm.view.pm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.dao.PmOrderRuleItemRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.PmOrderRuleCheckList;
import com.ge.apm.domain.PmOrderRuleItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.Report;
import com.ge.apm.view.sysutil.UserContextService;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class PmOrderRuleController extends JpaCRUDController<PmOrderRule> {
	private static final long serialVersionUID = -1L;
	private String safeReport = "";
	private String performanceReport = "";
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
		safeReport = getDefalutReport();
		performanceReport = safeReport;
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
	
	/***
	 * 获取勾选项
	 * @return
	 */
	public List<Integer> getSelectedIds(){
		if(selected != null){
			ids = pmOrderRuleCheckListRepository.findByRuleUID(selected.getUid());
			//pmOrderRuleCheckListRepository.findItemIds(user.getTenantUID(),selected.getId());
		}
		return ids;
	}
	
	/***
	 * 获取租户下面所有Items
	 * @return
	 */
	public List<PmOrderRuleItem> getAllItems(){
		List<PmOrderRuleItem> items = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
		if(CollectionUtils.isEmpty(items)){
			items = new ArrayList<PmOrderRuleItem>();
		}
		return items;
	}

	@Override
	@Transactional
	public void onBeforeSave(PmOrderRule por) {
		pmOrderRuleCheckListRepository.deleteExistsRecord(por.getUid());
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
		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
		if(!StringUtils.isEmpty(selected.getSafetyReport())){
			try{
				if(!selected.getSafetyReport().contains("title")){
					WebUtil.addErrorMessage("安全检测报告必须包含title元素！");
					return;
				}else if(!selected.getSafetyReport().contains("groups")){
					WebUtil.addErrorMessage("安全检测报告必须包含groups元素！");
					return;
				}
				JSON.parseObject(selected.getSafetyReport(),Report.class);
			}catch(Exception e){
				WebUtil.addErrorMessage("安全检测报告JSON不合法！");
				return;
			}
		}
		if(!StringUtils.isEmpty(selected.getPerformanceReport())){
			try{
				if(!selected.getPerformanceReport().contains("title")){
					WebUtil.addErrorMessage("性能检测报告必须包含title元素！");
					return;
				}else if(!selected.getPerformanceReport().contains("groups")){
					WebUtil.addErrorMessage("性能检测报告必须包含groups元素！");
					return;
				}
				JSON.parseObject(selected.getPerformanceReport(),Report.class);
			}catch(Exception e){
    			//RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				WebUtil.addErrorMessage("性能检测报告JSON不合法！");
				return;
			}
		}
		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
		if(selected.getPeriod() == null){
			List<Integer> itemIds = new ArrayList<Integer>();
			
			if(!ids.isEmpty()){
				Iterator it = ids.iterator();
				if(it.hasNext()){
					Object o = it.next();
					itemIds.add(Integer.parseInt((String)o));
				}
			}
			Integer period = pmOrderRuleItemRepository.fetchMinPeriod(user.getTenantUID(),itemIds);
			period =  period == null ? 1 : period;
			selected.setPeriod(period);
		}
		
		if (selected.getId() != null) {//更新
			onBeforeSave(selected);
		}
		dao.save(selected);
		String ruleUID = selected.getUid();
		if(!ids.isEmpty()){
			List<PmOrderRuleCheckList> checkLists = new ArrayList<PmOrderRuleCheckList>();
			List<PmOrderRuleItem> allItems = pmOrderRuleItemRepository.findByTenantUID(user.getTenantUID());
			if(CollectionUtils.isNotEmpty(allItems)){
				for (PmOrderRuleItem item : allItems) {
					if(ids.contains(item.getId().toString())){
						PmOrderRuleCheckList cl = new PmOrderRuleCheckList();
						cl.setName(item.getName());
						cl.setPeriod(item.getPeriod());
						cl.setRemark(cl.getRemark());
						cl.setRuleUID(ruleUID);
						cl.setItemId(item.getId());
						checkLists.add(cl);
					}
				}
				pmOrderRuleCheckListRepository.save(checkLists);
			}
		}
		ids.clear();
		WebUtil.addSuccessMessage(WebUtil.getMessage("保存成功！"));
	}
	
	@Transactional
	public void approve(){
		applyChange();
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
	
        public void onPriview(){
            
        }
        
	@Override
	@Transactional
	public void delete(){
		pmOrderRuleCheckListRepository.deleteExistsRecord(selected.getUid());
		super.delete();
	}

	public String getSafeReport() {
		return safeReport;
	}

	public void setSafeReport(String safeReport) {
		this.safeReport = safeReport;
	}

	public String getPerformanceReport() {
		return performanceReport;
	}

	public void setPerformanceReport(String performanceReport) {
		this.performanceReport = performanceReport;
	}
	
	public String getDefalutReport(){
		return dao.getDefalutReport(1);
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