package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.AssetOptRecordRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.mapper.AssetOptRecordMapper;
import com.ge.apm.domain.AssetOptRecord;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.AssetOptRecordService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class AssetOptRecordController extends MyCRUDController<AssetOptRecord> implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainConfigController.class);
	private I18nMessageRepository i18nDao;
	private UserAccount currentUser;
	private AssetOptRecordService service;
	private AssetOptRecordMapper dao;
	private List<AssetOptRecord> listAssetOptRecord;
	private Set<String> setOrgUid;
	private List<String> listOrgUid;
	private String assetGroupFilter;
	private SearchController searchController;
	private OrgInfo selectOrgInfo;
	private Set<String> setUserUids;

	@Override
	protected void init() {
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		service = WebUtil.getBean(AssetOptRecordService.class);
		searchController = WebUtil.getBean(SearchController.class);
		searchController.setCallingController(this);
		searchController.setUpdateViewIDs(":optRecordListForm:datalist");
		searchController.setBlnOrgTreeUpdate(true);
		searchController.setType(5);
		searchController.updateData();
		dao = WebUtil.getBean(AssetOptRecordMapper.class);
		setUserUids = service.findAccessClinicalUidListByUserUid(currentUser.getUid());
		if (setOrgUid != null) {
			listOrgUid = new ArrayList<String>();
			listOrgUid.addAll(setOrgUid);
		}
		this.selectOrgInfo = service.fetchOrgInfoByUid(currentUser.getSiteUID());
		// test
	}

	@Override
	protected List<AssetOptRecord> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
		if (this.searchFilters == null) {
			this.searchFilters = new HashSet<SearchFilter>();
		}
		// need list or set
		searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, setUserUids, "1*"));
		if (selectOrgInfo.getOrgType() == 0) {
			searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
		} else if (selectOrgInfo.getOrgType() == 1) {
			searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
		} else if (selectOrgInfo.getOrgType() == 2) {
			searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
		} else if (selectOrgInfo.getOrgType() == 3) {
			searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*2+"));
		}
		this.lazyModel.setRowCount(dao.getAllCount(searchFilters));
		listAssetOptRecord = dao.getAll(searchFilters, sortField, isAscending, pageRequest.getLimit(), pageRequest.getOffset());
		service.supplementCostTime(listAssetOptRecord);
		return listAssetOptRecord;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		try {
			OrgInfo orgInfo = (OrgInfo) eventObject;
			if (orgInfo != null) {
				selectOrgInfo = orgInfo;
				searchFilters.clear();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onBeforeNewObject(AssetOptRecord object) {
		super.onBeforeNewObject(object);
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());
	}

	@Override
	public void save() {
		this.selected = null;
		WebUtil.addSuccessMessage("保存成功");
	}

	public void update() {
		this.selected = null;
		WebUtil.addSuccessMessage("保存成功");
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		// boolean
		// result=nurseSiteMaintainService.deleteMaintainConfig(this.selected);
		// if(result){
		// WebUtil.addSuccessMessage("删除成功！");
		// }
		// else{
		// WebUtil.addSuccessMessage("不允许删除！");
		// }
		this.selected = null;

	}

	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "开关机记录" + " " + WebUtil.getMessage(actionName);
	}

	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
	}

	@Override
	public void prepareEdit() {
		// TODO Auto-generated method stub
		super.prepareEdit();
	}

	@Override
	protected BaseMapper<AssetOptRecord> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	public String getAssetGroupFilter() {
		return assetGroupFilter;
	}

	public void setAssetGroupFilter(String assetGroupFilter) {
		this.assetGroupFilter = assetGroupFilter;
	}

	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public OrgInfo getSelectOrgInfo() {
		return selectOrgInfo;
	}

	public void setSelectOrgInfo(OrgInfo selectOrgInfo) {
		this.selectOrgInfo = selectOrgInfo;
	}

}
