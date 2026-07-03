package com.ge.apm.view.nurseSite;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.AssetOptRecordMapper;
import com.ge.apm.domain.AssetOptRecord;
import com.ge.apm.domain.CustomRoleAuthConstant;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserAuth;
import com.ge.apm.service.nurseSite.AssetOptRecordService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;

import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

/**
 * @author Zhou
 */
@ManagedBean
@ViewScoped
public class AssetOptRecordController extends MyCRUDController<AssetOptRecord> {
	private UserAccount currentUser;
	private AssetOptRecordService service;
	private AssetOptRecordMapper dao;
	private UaaService uaaService;
	private String assetGroupFilter;
	private OrgInfo selectOrgInfo;
	private UserAuth customRoleAuth;
	private List<String> orgUidList;
	private List<OrgInfo> clinicalDeptList;
	private OrgInfo filterAssetDeptOrg;
	@Override
	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		service = WebUtil.getBean(AssetOptRecordService.class);
		SearchController searchController = WebUtil.getBean(SearchController.class);
		searchController.setCallingController(this);
		searchController.setUpdateViewIDs(":optRecordListForm:datalist");
		searchController.setBlnOrgTreeUpdate(true);
		searchController.setType(5);
		searchController.updateData();
		dao = WebUtil.getBean(AssetOptRecordMapper.class);
		uaaService = WebUtil.getBean(UaaService.class);
		orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
		clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
		this.selectOrgInfo = service.fetchOrgInfoByUid(currentUser.getSiteUID());
		customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_OPTRECORD);
	}

	@Override
	protected List<AssetOptRecord> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())){
			return ListUtil.empty();
		}
		if (this.searchFilters == null) {
			this.searchFilters = new HashSet<>();
		}
		searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
		if (selectOrgInfo.getOrgType() == 1) {
			searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
		} else if (selectOrgInfo.getOrgType() == 2) {
			searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
		} else if (selectOrgInfo.getOrgType() == 3) {
			searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectOrgInfo.getUid(), "1*"));
		}
		searchFilters.add(new SearchFilter("assetInfo.orgUidList", SearchFilter.Operator.IN, orgUidList, "1*2+"));
		if (currentUser.isEnableAssetTypeGroup()) {
			searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, currentUser.getAssetTypeGroupMap().keySet(), "1*"));
		}
		this.lazyModel.setRowCount(dao.getAllCount(searchFilters));
		List<AssetOptRecord> listAssetOptRecord = dao.getAll(searchFilters, sortField, isAscending, pageRequest.getLimit(), pageRequest.getOffset());
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
			e.printStackTrace();
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

	@Override
    public void update() {
		save();
	}

	@Override
	public void delete() {
		this.selected = null;
	}

	@Override
	protected String getActionMessage(String actionName) {
		return "开关机记录" + " " + WebUtil.getMessage(actionName);
	}

	@Override
	protected BaseMapper<AssetOptRecord> getDAO() {
		return dao;
	}

	public String getAssetGroupFilter() {
		return assetGroupFilter;
	}

	public void setAssetGroupFilter(String assetGroupFilter) {
		this.assetGroupFilter = assetGroupFilter;
	}

	public OrgInfo getSelectOrgInfo() {
		return selectOrgInfo;
	}

	public void setSelectOrgInfo(OrgInfo selectOrgInfo) {
		this.selectOrgInfo = selectOrgInfo;
	}

	public UserAuth getCustomRoleAuth() {
		return customRoleAuth;
	}

	public void setCustomRoleAuth(UserAuth customRoleAuth) {
		this.customRoleAuth = customRoleAuth;
	}

	public List<AssetOptRecord> getAssetOptRecordList(String assetUid){
		return service.getAssetOptRecordList(assetUid);
	}

	public OrgInfo getOrgInfoByUid(String uid) {
		return service.fetchOrgInfoByUid(uid);
	}


	public void onAssetDeptNameFilterChange() {
		if (this.searchFilters == null) {
			this.searchFilters = new HashSet<>();
		}
		// Remove existing assetDeptUID filter
		this.searchFilters.removeIf(f -> "assetInfo.assetDeptUID".equals(f.fieldName));
		// Add new filter if a department is selected
		if (filterAssetDeptOrg != null && filterAssetDeptOrg.getUid() != null) {
			this.searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.EQ, filterAssetDeptOrg.getUid(), "1*3+"));
		}
	}

	public List<OrgInfo> getClinicalDeptList() {
		return clinicalDeptList;
	}

	public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
		this.clinicalDeptList = clinicalDeptList;
	}

	public OrgInfo getFilterAssetDeptOrg() {
		return filterAssetDeptOrg;
	}

	public void setFilterAssetDeptOrg(OrgInfo filterAssetDeptOrg) {
		this.filterAssetDeptOrg = filterAssetDeptOrg;
	}
}
