package com.ge.apm.view.pm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.PmOrderRuleCheckListRepository;
import com.ge.apm.dao.PmOrderRuleItemRepository;
import com.ge.apm.dao.PmOrderRuleRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.PmOrderRuleCheckList;
import com.ge.apm.domain.PmOrderRuleItem;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.Report;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.Optional;
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
	private String filterPmRuleLevel;
	private String defaultReport;
	private TableDefinitionRepository templateDao;
        
//        private String safetyReportJson;
//        private String performanceReportJson;

	@Override
	protected void init() {
		filterBySite = false;
		dao = WebUtil.getBean(PmOrderRuleRepository.class);
		user = UserContextService.getCurrentUserAccount();
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		pmOrderRuleItemRepository = WebUtil.getBean(PmOrderRuleItemRepository.class);
		pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
		templateDao = WebUtil.getBean(TableDefinitionRepository.class);
		//defaultReport = getDefalutReport();
	}

	@Override
	protected PmOrderRuleRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<PmOrderRule> loadData(PageRequest pageRequest) {
		if(searchFilters == null){
			searchFilters = new ArrayList<SearchFilter>();
		}
		if(!this.hasFilterOnField("tenantUID")){
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		}
		 Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		 pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		return dao.findBySearchFilter(searchFilters, pageRequest);
	}
	
	/***
	 * 获取勾选项
	 * @return
	 */
	public List<Integer> getSelectedIds(){
		if(selected != null){
			ids = pmOrderRuleCheckListRepository.findByRuleUID(selected.getUid());
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
	
	public void onSelect(){
		if(selected != null && selected.getId() != null){
			selected = dao.findById(selected.getId());
		}
	}
	
	public List<TableDefinition> getTableDefinition(){
		List<SearchFilter> tempFilter = new ArrayList<SearchFilter>();
		tempFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		tempFilter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Released"));
		//tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, selected.getAssetGroup()));
		if(this.selected != null && this.selected.getAssetGroup() != null) {
			tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, selected.getAssetGroup()));
		}
		return templateDao.findBySearchFilter(tempFilter);
	}
	
	public List<TableDefinition> getTableDefinitionForES(){
		List<SearchFilter> tempFilter = new ArrayList<SearchFilter>();
		tempFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		tempFilter.add(new SearchFilter("status", SearchFilter.Operator.EQ, "Released"));
		tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.IsNull, null));
//		if(this.selected != null && this.selected.getAssetGroup() != null) {
//			tempFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator., selected.getAssetGroup()));
//		}
		return templateDao.findBySearchFilter(tempFilter);
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
//		por.setSafetyReport(defaultReport);
//		por.setPerformanceReport(defaultReport);
		por.setPeriod(1);//默认一个月
	}
	
	public void resetNewObject() {
		if(selected.getId() == null) {
			selected = null;
		}
	}

	public String getUserName(Integer userId) {
		if (userId == null) {
			return null;
		}
		if(userId <= 0) {
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
//		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//		if(!StringUtils.isEmpty(selected.getSafetyReport())){
//			try{
//				if(!selected.getSafetyReport().contains("title")){
//					WebUtil.addErrorMessage("安全检测报告必须包含title元素！");
//					return;
//				}else if(!selected.getSafetyReport().contains("groups")){
//					WebUtil.addErrorMessage("安全检测报告必须包含groups元素！");
//					return;
//				}
//				JSON.parseObject(selected.getSafetyReport(),Report.class);
//			}catch(Exception e){
//				WebUtil.addErrorMessage("安全检测报告JSON不合法！");
//				return;
//			}
//		}
//		if(!StringUtils.isEmpty(selected.getPerformanceReport())){
//			try{
//				if(!selected.getPerformanceReport().contains("title")){
//					WebUtil.addErrorMessage("性能检测报告必须包含title元素！");
//					return;
//				}else if(!selected.getPerformanceReport().contains("groups")){
//					WebUtil.addErrorMessage("性能检测报告必须包含groups元素！");
//					return;
//				}
//				JSON.parseObject(selected.getPerformanceReport(),Report.class);
//			}catch(Exception e){
//				WebUtil.addErrorMessage("性能检测报告JSON不合法！");
//				return;
//			}
//		}
//		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
		
		selected.setPeriod(getPeriod());
		
		if (selected.getId() != null) {//更新
			onBeforeSave(selected);
		}
		getName();
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
	
	public void getName(){
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotEmpty(selected.getAssetName())){
			sb.append(selected.getAssetName()+"|");
		}
		if(StringUtils.isNotEmpty(selected.getManufacture())){
			sb.append(selected.getManufacture()+"|");
		}
		if(StringUtils.isNotEmpty(selected.getFunctionType())){
			sb.append(selected.getFunctionType()+"|");
		}
		if(StringUtils.isNotEmpty(selected.getRemark())){
			sb.append(selected.getRemark());
		}
		String name = sb.toString();
		if(name.endsWith("|")){
			name = name.substring(0, name.lastIndexOf("|"));
		}
		selected.setName(name);
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
	        if (filterPmRuleLevel != null) {
	            searchFilters.add(new SearchFilter("level", SearchFilter.Operator.EQ, Integer.parseInt(filterPmRuleLevel)));
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
	
	@Transactional
	public void saveAs(){
		selected.setId(null);
		selected.setStatus(0);
        selected.setCreatedDate(new Date());
        selected.setUid(UUID.randomUUID().toString().replace("-", ""));
        selected.setOwnerId(user.getId());
        selected.setApproveUserId(null);
        applyChange();
	}
	
/*	public void saveOwn(){
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
				WebUtil.addErrorMessage("性能检测报告JSON不合法！");
				return;
			}
		}
		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
		
		selected.setPeriod(getPeriod());
		
		getName();
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
	}*/
	
	public String getDefalutReport(){
		return dao.getDefalutReport(1);
	}
	
    private Date varWarrantyDateTo = null;
    private String warrantyFormatTime = null;

    public String getWarrantyFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varWarrantyDateTo == null) {
            return warrantyFormatTime;
        } else {
            return "~" + sdf.format(varWarrantyDateTo);
        }
    }

	public Date getVarWarrantyDateTo() {
		return varWarrantyDateTo;
	}

	public void setVarWarrantyDateTo(Date varWarrantyDateTo) {
		this.varWarrantyDateTo = varWarrantyDateTo;
	}

	public void setWarrantyFormatTime(String warrantyFormatTime) {
		this.warrantyFormatTime = warrantyFormatTime;
	}

	public Integer getPeriod(){
		if(selected.getPeriod() != null && selected.getPeriod() > 0){
			return selected.getPeriod();
		}else{
			List<Integer> itemIds = new ArrayList<Integer>();
			if(!ids.isEmpty()){
				Iterator it = ids.iterator();
				while(it.hasNext()){
					Object o = it.next();
					itemIds.add(Integer.parseInt((String)o));
				}
			}
			Integer period = 1;
			if(itemIds.size() > 0){
				 period = pmOrderRuleItemRepository.fetchMinPeriod(user.getTenantUID(),itemIds);
			}
			if(period <= 0) {
				period = 1;
			}
			return period;
		}
	}
        
//        public void onSchemaChange(String type){
//            if("safetyReport".equals(type)){
//                TableDefinition schema = templateDao.findById(selected.getSafetyReport());
//                safetyReportJson = schema==null? null:schema.getSchema();
//            }else if("performanceReport".equals(type)){
//                TableDefinition schema = templateDao.findById(selected.getPerformanceReport());
//                performanceReportJson = schema==null? null:schema.getSchema();
//            }
//            
//        }

    public String getJsonValueById(String id) {
        if (!Strings.isNullOrEmpty(id)) {
            TableDefinition schema = templateDao.findById(id);
            return schema == null ? null : schema.getSchema();
        } else {
            return null;
        }
    }
        
        
	public void initSaveAs(){
		selected.setPeriod(null);
//		selected.setId(null);
//		selected.setStatus(0);
//        selected.setCreatedDate(new Date());
//        selected.setUid(UUID.randomUUID().toString().replace("-", ""));
//        selected.setOwnerId(user.getId());
        super.prepareEdit();
	}

	public String getDefaultReport() {
		return defaultReport;
	}

	public void setDefaultReport(String defaultReport) {
		this.defaultReport = defaultReport;
	}

	public String getFilterPmRuleLevel() {
		return filterPmRuleLevel;
	}

	public void setFilterPmRuleLevel(String filterPmRuleLevel) {
		this.filterPmRuleLevel = filterPmRuleLevel;
	}

//    public String getSafetyReportJson() {
//        return safetyReportJson;
//    }
//
//    public String getPerformanceReportJson() {
//        return performanceReportJson;
//    }
//    
//    public void setSafetyReportJson(String safetyReportJson) {
//        this.safetyReportJson = safetyReportJson;
//    }
//
//    public void setPerformanceReportJson(String performanceReportJson) {
//        this.performanceReportJson = performanceReportJson;
//    }

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
