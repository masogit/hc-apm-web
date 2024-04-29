package com.ge.apm.view.inventory;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.InventoryOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import org.primefaces.event.NodeSelectEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoAllController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {

	private UserAccount currentUser;
	private InventoryOrderDetailAssetInfoRepository dao;
    private InventoryOrderService inventoryOrderService;
    private UserAccountRepository userAccountRepository;
    private InventoryOrderMapper inventoryOrderMapper;
    private UaaService uaaService;
    private UserContextService userContextService;
    private List<String> listClinicalDeptName;
    private List<String> listAccessClinical;
    private String queryAssetUid;
    //选定的院区
    private OrgInfo selectSite;
    private PageUtil pageUtil = new PageUtil<>();
	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        inventoryOrderMapper = WebUtil.getBean(InventoryOrderMapper.class);
        userContextService = WebUtil.getBean(UserContextService.class);
		currentUser = UserContextService.getCurrentUserAccount();
        listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            queryAssetUid = (String) UrlEncryptController
                    .getValueFromMap(enCodeParameters, "queryAssetUID");
        }
	}

	@Override
	protected GenericRepositoryUUID<InventoryOrderDetail> getDAO() {
		return dao;
	}
    public String changePage(boolean flag){
	    if(flag){
            return "inventoryOrder?faces-redirect=true";
        }
        return "inventoryOrderDetailAssetInfoMainAll?faces-redirect=true";
    }

    @Override
    protected Page<InventoryOrderDetail> loadData(PageRequest pageRequest) {
        //before
        /*if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }*/
        Map<String, Object> param = MapUtil.newHashMap();
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
        List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        /*if (currentUser.isEnableAssetTypeGroup()) {
            param.put("assetTypes", currentUser.getAssetTypeGroupMap().keySet());
        }*/
        param.put("tenantUid", currentUser.getTenantUID());
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        if (ObjectUtil.isNotEmpty(orderIdList)) {
            param.put("orderIdList", orderIdList);
        }
        if (ObjectUtil.isNotEmpty(selectSite)) {
            param.put("selectedOrgUid", selectSite.getUid());
            searchFilters.add(new SearchFilter("selectedOrgUid", SearchFilter.Operator.EQ, selectSite.getUid()));
        }
        if (ObjectUtil.isNotEmpty(orgUidList)) {
            param.put("orgUidList", orgUidList);
        }
        if (ObjectUtil.isNotEmpty(queryAssetUid)) {
            param.put("queryAssetUid", queryAssetUid);
            searchFilters.add(new SearchFilter("queryAssetUid", SearchFilter.Operator.EQ, queryAssetUid));
        }
        List<Map<String, Object>> joinAssetList = inventoryOrderMapper.findJoinAssetList(param);
        joinAssetList = inventoryOrderService.covQueryCondition(searchFilters, joinAssetList);
        Map<String, List<Map<String, Object>>> listMap = joinAssetList.stream().collect(Collectors.groupingBy(map -> map.get("status").toString()));
        List<Map<String, Object>> mapList = ListUtil.newArrayList();
        Optional.ofNullable(listMap.get("2")).map(mapList::addAll).orElse(false);
        Optional.ofNullable(listMap.get("3")).map(mapList::addAll).orElse(false);
        Optional.ofNullable(listMap.get("1")).map(mapList::addAll).orElse(false);
        Optional.ofNullable(listMap.get("0")).map(mapList::addAll).orElse(false);
        Optional.ofNullable(listMap.get("4")).map(mapList::addAll).orElse(false);
        Page<Map<String, Object>> page = pageUtil.pageNum(mapList, pageRequest);
//        Long count = inventoryOrderMapper.findJoinAssetCount(param);
        List<InventoryOrderDetail> resultList = page.getContent().stream().map(map -> JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(map)), InventoryOrderDetail.class)).collect(Collectors.toList());
        return new PageImpl<>(resultList, pageRequest, mapList.size());
    }

    public String  getUserName(String loginName){
	    if( null == loginName){
	        return null;
        }
        UserAccount userAccount = userAccountRepository.getByLoginName(loginName);
        if (userAccount != null) {
            return userAccount.getName();
        }
        return null;
    }

	@Override
	public void onBeforeNewObject(InventoryOrderDetail object) {
		super.onBeforeNewObject(object);
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());
	}

    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }
    }

	public List<String> getListClinicalDeptName() {
		return listClinicalDeptName;
	}

	public void setListClinicalDeptName(List<String> listClinicalDeptName) {
		this.listClinicalDeptName = listClinicalDeptName;
	}

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
}
