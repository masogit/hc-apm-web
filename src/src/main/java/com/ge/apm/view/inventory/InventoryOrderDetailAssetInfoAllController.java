package com.ge.apm.view.inventory;

import com.alibaba.excel.util.MapUtils;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.InventoryOrderMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.EasyExcelExportUtil;
import com.ge.apm.service.inventory.InventoryOrderService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class InventoryOrderDetailAssetInfoAllController extends GenericCRUDUUIDController<InventoryOrderDetail> implements Serializable, ServerEventInterface {


    private static final Logger log = LoggerFactory.getLogger(InventoryOrderDetailAssetInfoAllController.class);

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
    private OrgInfoTreeService orgInfoTreeService;

    private OrgInfoRepository orgInfoRepository;
    private SystemTemplateService systemTemplateService;

    @Getter @Setter
    private String filterClinicalDeptOrg;
    @Getter @Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private Boolean inventoryDisplayRepairOwner = Boolean.FALSE;

    private StreamedContent excelFile;
	@Override
	protected void init() {
		dao = WebUtil.getBean(InventoryOrderDetailAssetInfoRepository.class);
        inventoryOrderService = WebUtil.getBean(InventoryOrderService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        inventoryOrderMapper = WebUtil.getBean(InventoryOrderMapper.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
        listClinicalDeptName= uaaService.getAllAccessibleClinicalDeptStringList();
        listAccessClinical=uaaService.findAccessClinicalUidByUserUid(currentUser.getUid());
        inventoryDisplayRepairOwner = Optional.ofNullable(inventoryOrderService.getConfigValue(currentUser, "InventoryDisplayRepairOwner")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);
        loadClinicalDeptList();
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
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        Map<String, Object> param = MapUtil.newHashMap();
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
        List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
        if (currentUser.isEnableAssetTypeGroup()) {
            param.put("assetTypes", currentUser.getAssetTypeGroupMap().keySet());
        }
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
        if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
            param.put("clinicalDeptUids", clinicalDeptOrgUidSet);
        }
        inventoryOrderService.covQueryCondition(searchFilters, param);
        List<InventoryOrderDetail> joinAssetList = inventoryOrderMapper.findJoinAssetList(param);
//        Map<String, List<InventoryOrderDetail>> listMap = joinAssetList.stream().collect(Collectors.groupingBy(map -> map.getStatus().toString()));
//        List<InventoryOrderDetail> mapList = ListUtil.newArrayList();
//        Optional.ofNullable(listMap.get("2")).map(mapList::addAll).orElse(false);
//        Optional.ofNullable(listMap.get("3")).map(mapList::addAll).orElse(false);
//        Optional.ofNullable(listMap.get("1")).map(mapList::addAll).orElse(false);
//        Optional.ofNullable(listMap.get("0")).map(mapList::addAll).orElse(false);
//        Optional.ofNullable(listMap.get("4")).map(mapList::addAll).orElse(false);
        Long count = inventoryOrderMapper.findJoinAssetCount(param);

//        List<InventoryOrderDetail> inventoryOrderDetailList = new ArrayList<>();
//        for (Map<String, Object> map : mapList) {
//            InventoryOrderDetail inventoryOrderDetail = new InventoryOrderDetail();
//            try {
//                BeanUtils.populate(inventoryOrderDetail, map);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//            inventoryOrderDetailList.add(inventoryOrderDetail);
//        }

//        List<InventoryOrderDetail> resultList = mapList.stream().map(map -> JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(map)), InventoryOrderDetail.class)).collect(Collectors.toList());
        return new PageImpl<>(joinAssetList, pageRequest, count);
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

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }

        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
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

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(StreamedContent excelFile) {
        this.excelFile = excelFile;
    }


    public void exportExcel() throws IOException {


        byte[] templateFileByte = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.INVENTORY_ORDER_LIST, currentUser);
        List<InventoryOrderDetail> resultList ;
        Map<String, Object> param = MapUtil.newHashMap();
        //before
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            resultList =new ArrayList<>();
        }else{

            List<SearchFilter> searchFilters = this.searchFilters;
            if (searchFilters == null) {
                searchFilters = new ArrayList<>();
            }
            List<String> orderIdList = inventoryOrderService.getInventoryOrderData(currentUser.getTenantUID(), listAccessClinical,false);
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());
            if (currentUser.isEnableAssetTypeGroup()) {
                param.put("assetTypes", currentUser.getAssetTypeGroupMap().keySet());
            }
            param.put("tenantUid", currentUser.getTenantUID());
            param.put("page", 50000);
            param.put("offset", 0);
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
            if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
                param.put("clinicalDeptUids", clinicalDeptOrgUidSet);
            }
            inventoryOrderService.covQueryCondition(searchFilters, param);
//            List<Map<String, Object>> joinAssetList = inventoryOrderMapper.findJoinAssetList(param);
//            Map<String, List<Map<String, Object>>> listMap = joinAssetList.stream().collect(Collectors.groupingBy(map -> map.get("status").toString()));
//            List<Map<String, Object>> mapList = ListUtil.newArrayList();
//            Optional.ofNullable(listMap.get("2")).map(mapList::addAll).orElse(false);
//            Optional.ofNullable(listMap.get("3")).map(mapList::addAll).orElse(false);
//            Optional.ofNullable(listMap.get("1")).map(mapList::addAll).orElse(false);
//            Optional.ofNullable(listMap.get("0")).map(mapList::addAll).orElse(false);
//            Optional.ofNullable(listMap.get("4")).map(mapList::addAll).orElse(false);
//            resultList = mapList.stream().map(map -> JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(map)), InventoryOrderDetail.class)).collect(Collectors.toList());

        }

//        double totalPrice = resultList.stream()
//                .map(item -> Optional.ofNullable(item.getPurchasePrice()).orElse(0.0))
//                .mapToDouble(Double::doubleValue)
//                .sum();
//        Map<String, Object> map = MapUtils.newHashMap();
//        Map<String, Object> map =  new HashMap<>();
        OrgInfo orgInfo = orgInfoRepository.getByUid(currentUser.getSiteUID());

//        map.put("totalPrice", totalPrice);
//        map.put("sum", resultList.size());
//        map.put("hospitalName", orgInfo.getName());

//        byte[] bytes = EasyExcelExportUtil.exportExcel(templateFileByte, resultList, map);
        EasyExcelExportUtil.DataSupplier dataSupplier = new EasyExcelExportUtil.DataSupplier() {
            double totalPrice=0.0;

            @Override
            public Map<String, Object> getParamMap() {
                if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
                    return null;
                }
                Long count = inventoryOrderMapper.findJoinAssetCount(param);
                Map<String, Object> map = MapUtils.newHashMap();
                map.put("hospitalName", orgInfo.getName());
                map.put("totalPrice", totalPrice);
                map.put("sum", count);
                return map;
            }

            @Override
            public List<?> getData(int pageNum, int pageSize) {
                if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
                   return null;
                }
                param.put("page", pageSize);
                param.put("offset", (pageNum-1)*pageSize);
                List<InventoryOrderDetail> joinAssetList = inventoryOrderMapper.findJoinAssetList(param);
//                Map<String, List<InventoryOrderDetail>> listMap = joinAssetList.stream().collect(Collectors.groupingBy(map -> map.getStatus().toString()));
//                List<InventoryOrderDetail> mapList = ListUtil.newArrayList();
//                Optional.ofNullable(listMap.get("2")).map(mapList::addAll).orElse(false);
//                Optional.ofNullable(listMap.get("3")).map(mapList::addAll).orElse(false);
//                Optional.ofNullable(listMap.get("1")).map(mapList::addAll).orElse(false);
//                Optional.ofNullable(listMap.get("0")).map(mapList::addAll).orElse(false);
//                Optional.ofNullable(listMap.get("4")).map(mapList::addAll).orElse(false);

//                List<InventoryOrderDetail> collect = mapList.stream().map(map -> JSON.toJavaObject(JSON.parseObject(JSON.toJSONString(map)), InventoryOrderDetail.class)).collect(Collectors.toList());
                totalPrice+=joinAssetList.stream()
                        .map(item -> Optional.ofNullable(item.getPurchasePrice()).orElse(0.0))
                        .mapToDouble(Double::doubleValue)
                        .sum();
                return joinAssetList;
            }
        };
        HashMap<String, EasyExcelExportUtil.DataSupplier> dataSupplierMap = new HashMap<>();
        dataSupplierMap.put("dataList",dataSupplier);
        byte[] bytes = EasyExcelExportUtil.exportExcel(templateFileByte, dataSupplierMap);
        excelFile = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/vnd.ms-excel", "InventoryOrderDetailAssetInfo.xlsx");
    }
}
