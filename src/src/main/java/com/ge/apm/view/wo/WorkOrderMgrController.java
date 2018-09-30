/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetFaultTypeService;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.StringUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderMgrController extends  GenericCRUDUUIDController<V2_WorkOrder> implements Serializable, ServerEventInterface {

    V2_WorkOrderRepository dao;
    private String supplierName;
    private String fileterOrgName;
    private String fileterAssetDeptOrgName;
    private List<OrgInfo> clinicalDeptList;
    private boolean terminate = false;
    private OrgInfoRepository orgDao;
    private UserAccountRepository userDao;
    private V2_WorkOrderService woService;
    private UaaService uaaService;
    private UserContextService userContextService;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private WorkOrderDetailRepository detailDao;
    private V2_WorkOrderCoworkerRepository coWorkerDao;
    private V2_WorkOrderRepository woDao;
    private String locationName;
    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;
    private Set<String> clinicalDeptOrgUidSet;
    private Set<String> assetDeptOrgUidSet;
    private  UserOrgInfoRepository userOrgInfoRepository;
    private V2_ServiceRequest newServiceRequest;

    private V2_WorkOrder selectedWorkOrder;
    private List<V2_WorkOrder> selectedWorkOrderList;

    private AssetInfo selectedAsset;
    private String assetStatus;
    private Integer currentStatus = 1;
    private Boolean needConfirmTime = false;
    private String cancelReason;
    private String stepFileter;
    //for details form
    private List<V2_WorkOrder_Detail> accessoryList;
    private List<V2_WorkOrderCoworker> coWorkerList;

    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;

    private Date esTime;
    private String comments;
    private Date confirmedUpDate;
    private Date confirmedDownTime;
    private String checkInDate;
    private Integer rating;
    private String failReason;
    private String equipmentTaker;
    private Date takeTime;

    private List<V2_BlobObject> picList;
    private List<V2_BlobObject> audioList;
    private List<V2_BlobObject> attachList;


    private List<Integer> tagGroupAssets;
    private List<Integer> tagNoGroupAssets;

    private String requestAssetUid;
    private String queryAssetUid;
    private List<V2_WorkOrder_Detail> approvedDetails;
    private Map<String, Boolean> selectedMap;
    private String selectedApproverUid;
    private ServiceRequestApiService serviceRequestApiService;
    private WorkflowConfigRepository woConDao;
    private String token;
    private OrgInfoTreeService orgInfoTreeService;
    private AutoCompleteService autoCompleteService;

    private AssetInfoMgrService aimService;
    private List<Supplier> supplierList = new ArrayList<Supplier>();
    private Map<String, Object> selectedObjectMap = new HashMap();

    protected void init() {
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        userOrgInfoRepository=WebUtil.getBean(UserOrgInfoRepository.class);
        token = UserContextService.getAccessToken();
        dao = WebUtil.getBean(V2_WorkOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetFaultTypeService = WebUtil.getBean(AssetFaultTypeService.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        detailDao = WebUtil.getBean(WorkOrderDetailRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        woConDao = WebUtil.getBean(WorkflowConfigRepository.class);
        woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
        serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        String query = WebUtil.getRequestParameter("query");
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            requestAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUID");
            queryAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
            if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                query = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "query");
            }
        }
        if (Strings.isNullOrEmpty(query)) {
            queryIndex = 0;
        } else {
            queryIndex = Integer.valueOf(query);
            searchList(queryIndex);
        }
        this.filterBySite = false;
        this.filterByHospital = false;
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        loadClinicalDeptList();

    }

    ///////////////////////////////////////////start
//选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    //搜索字串
    private String oneSearchStr;
    //选定的院区
    private OrgInfo selectSite;

    public String getStepImg(V2_WorkOrder v2_workOrder){
        Integer stepId =v2_workOrder.getCurrentStepId();
        int mode =v2_workOrder.getDispatchMode();
        switch(stepId){
            case 7: //
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0){
                    if(v2_workOrder.getApprovalDate()==null){
                        return  "icons/6.png";
                    }
                    return "icons/5.png";
                }
                else{//如果没有备件 则显审核图标显示为灰色的
                    return "icons/6.png";
                }
            case 6: //
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0){
                    return "icons/5.png";
                }
                else{//如果没有备件 则显审核图标显示为灰色的
                    return "icons/6.png";
                }
            case 5: //待验收
            {
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0 && v2_workOrder.getApprovalDate()==null){//有备件 没有提交审核 则显审核图标显示为灰色的
                    return "icons/6.png";
                }
               else{
                   return "icons/5.png";
                }
            }
            case 8: //待审核
                return "icons/4.png";
            case 4: //维修中
                return "icons/3.png";
            case 3: //接单中
                return "icons/2.png";
            case 2:
                if(mode== 2 || mode ==3 ){//如果是抢单或抢单加手动模式
                    return "icons/2.png";
                }
            default:
                return "icons/1.png";
        }
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
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

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid())
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    private void setUserSearchFilters() {
        this.updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        if (terminate) {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, false));
        } else {
            this.updateSearchFilter(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
        }

        if (!this.hasFilterOnField("siteUID")) {
            UserAccount user = UserContextService.getCurrentUserAccount();
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

        if (!Strings.isNullOrEmpty(fileterAssetDeptOrgName)) {
            OrgInfo deptOrg = clinicalDeptList.stream().filter(item -> fileterAssetDeptOrgName.equals(item.getName())).findAny().get();
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.EQ, deptOrg.getUid(), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (!Strings.isNullOrEmpty(fileterOrgName)) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.EQ, fileterOrgName, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptName".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }

    }

    public String getFileterOrgName() {
        return fileterOrgName;
    }

    public void setFileterOrgName(String fileterOrgName) {
        this.fileterOrgName = fileterOrgName;
    }

    public String getFileterAssetDeptOrgName() {
        return fileterAssetDeptOrgName;
    }

    public void setFileterAssetDeptOrgName(String fileterAssetDeptOrgName) {
        this.fileterAssetDeptOrgName = fileterAssetDeptOrgName;
    }
    public void onSupplierChange() {
        Supplier supplier = (Supplier) selectedObjectMap.get("supplier");
        if (supplier == null)
            itemDetail.setSupplier(supplierName);
        else
            itemDetail.setSupplier(supplier.getName());
    }

    public String getManHourShow() {
        if (hourRequired) {
            return "工时(小时)*";
        } else {
            return "工时(小时)";
        }
    }

    private Boolean isRating;

    public List<String> autoCompleteSupplier(String query) {
        List<Supplier> suList = autoCompleteService.autoCompleteVendor(query, supplierList);
        List<String> sList = new ArrayList<String>();
        suList.forEach(item -> sList.add(item.getName()));
        return sList;
    }


    public Boolean needRating() {
        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                selectedWorkOrder.getAssetId());
        isRating = Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("ratingRequired")).get();
        //查看是否被评价过
        Integer feedback = woDao.findById(selectedWorkOrder.getId()).getFeedbackRating();
        //如果配置流里设置过必须评价，并且没有被评价过 返回true即显示需要评价
        if (isRating && feedback < 1) {
            return true;
        } else
            return false;
    }

    @Override
    protected GenericRepositoryUUID<V2_WorkOrder> getDAO() {
        return dao;
    }

    /*
     * select
        *
    from
        v2_work_order v2_workord0_ 
    left outer join
        v2_service_request v2_service1_ 
            on v2_workord0_.sr_id=v2_service1_.id cross 
    join
        v2_service_request v2_service2_ cross 
    join
        asset_info assetinfo4_ 
    where
        v2_workord0_.sr_id=v2_service2_.id 
        and v2_workord0_.asset_id=assetinfo4_.id         
        and v2_workord0_.status=1                           //1*
        and (
            v2_workord0_.current_step_id in (
                2 , 3
            )
        )                                                   //1*
        and (
            v2_workord0_.site_uid in (
                ?
            )                                                //1*2+
            or v2_workord0_.tenant_uid in (
                ?
            )                                                //1*2+
            or v2_workord0_.institution_uid in (
                ?
            )                                                //1*2+
            or v2_workord0_.hospital_uid in (
                ?
            )                                                 //1*2+
            or v2_service2_.requestor_id=2342                 //1*2+
            or v2_service2_.from_dept_uid in (
                ?
            )                                                 //1*2+
            or v2_workord0_.current_person_id=2342            //1*2+
        ) 
        and (
            (
                v2_workord0_.dispatch_mode in (
                    2 , 3
                )
            ) 
            and (
                v2_workord0_.current_person_id<>348 
                or v2_workord0_.current_person_id=348 
                and v2_workord0_.current_step_id=2
            ) 
            and (
                v2_workord0_.asset_id in (
                    0
                ) 
                or (
                    v2_workord0_.asset_id in (
                        0
                    )
                ) 
                and (
                    assetinfo1_.asset_owner_id=348 
                    or assetinfo1_.asset_owner_id2=348 
                    or assetinfo1_.asset_owner_id3=348 
                    or (
                        assetinfo1_.asset_owner_id2 is null
                    ) 
                    and (
                        assetinfo1_.asset_owner_id3 is null
                    ) 
                    or (
                        assetinfo1_.asset_owner_id is null
                    ) 
                    and (
                        assetinfo1_.asset_owner_id3 is null
                    ) 
                    or (
                        assetinfo1_.asset_owner_id is null
                    ) 
                    and (
                        assetinfo1_.asset_owner_id2 is null
                    )
                )
            ) 
            or v2_workord0_.current_person_id=348 
            and v2_workord0_.current_step_id=3
        )

     */
    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        removeFilterOnField("status");
        removeFilterOnField("currentStepId");
        removeFilterOnField("currentPersonId");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("srId");
        removeFilterOnField("feedbackRating");
        removeFilterOnField("assetId");
        removeFilterOnField("approvalStatus");
        removeFilterOnField("assetInfo.assetOwnerId");
        removeFilterOnField("assetInfo.assetOwnerId2");
        removeFilterOnField("assetInfo.assetOwnerId3");
        removeFilterOnField("assetInfo.uid");

        if (queryIndex == 1) {//已报修 可取消
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
            }
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.LTE, 3));
        } else if (queryIndex == 2) { //待派工
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));

//            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
//            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
//            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
//            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
//            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*2+"));
        } else if (queryIndex == 3) { //待接单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3), "1*3+4*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getId(), "1*3+4*A+"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*A+B*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2, "1*3+4*A+B*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*5+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*5+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+4*5+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner3*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner3*"));
            //去除派工模式4且待派工的单子
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*3+7*"));
//            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(4,5), "1*3+7*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3, "1*3+7*"));
            //当前责任人是自己且已经派工的单子
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+9*"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3, "1*2+9*"));
            List<Integer> tagGroupAssets = getTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*3+4*5+"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*3+4*5+"));
            }
            tagGroupAssets = getNoTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*3+4*5+owner*"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*3+4*5+owner*"));
            }

        } else if (queryIndex == 4) { //维修中
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        } else if (queryIndex == 5) { //待验收
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            } else {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
            }
        } else if (queryIndex == 6) { //待评价
            if (userContextService.hasRole("AssetStaff")) {
                searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1, 2))));
            }
            searchFilters.add(new SearchFilter("feedbackRating", SearchFilter.Operator.EQ, 0));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
        } else if (queryIndex == 7) { //待关单
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
        } else if (queryIndex == 8) { //已关单
            if (!userContextService.hasRole("AssetHead") && !userContextService.isDispatcher()) {
                if (userContextService.hasRole("AssetStaff")) {
                    searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
                    searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
                }
            }
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
        } else if (queryIndex == 9) { //已取消
//            if (!userContextService.hasRole("AssetHead") && !userContextService.isDispatcher()) {
//                searchFilters.add(new SearchFilter("serviceRequest.requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId()));
//                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
//            }
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
        } else if (queryIndex == 10) { //审批
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
            searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("serviceRequest.status", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("approverId", SearchFilter.Operator.EQ, userContextService.getCurrentUserAccount().getId()));
        } else if (queryIndex == 11) { //全部维修单

        } else if (queryIndex == 12) { //指定设备queryAssetUid 工单 设备详细页面跳转
            if (!Strings.isNullOrEmpty(queryAssetUid)) {
                searchFilters.add(new SearchFilter("assetInfo.uid", SearchFilter.Operator.EQ, queryAssetUid, "1*"));
            }
        } else {//待办
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
        }

        this.selected = null;
        Page<V2_WorkOrder> bySearchFilter = dao.findBySearchFilter(this.searchFilters, pageRequest);

        return   bySearchFilter;

    }

    @Override
    public void setSearchFilter() {


        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!Strings.isNullOrEmpty(fileterOrgName)) {
            //先获取 filterOrgName-手术科的 uid
            List<String> orgUidList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            List<String> filterOrg = orgDao.getOrgBySite(orgUidList, fileterOrgName);
           if(!filterOrg.isEmpty())
            this.updateSearchFilter(new SearchFilter("serviceRequest.fromDeptUID", SearchFilter.Operator.IN, filterOrg, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "serviceRequest.fromDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("serviceRequest.requestorId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*2+"));
            searchFilters.add(new SearchFilter("serviceRequest.fromDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    public void arrivalTimeSet() {
        if (itemDetail == null) return;
        if (itemDetail.getArrived() == true)
            itemDetail.setArrivedTime(new Date());
        else {
            itemDetail.setArrivedTime(null);
        }
    }

    public void orderSet() {
        if (itemDetail == null) return;
        if (itemDetail.getClosed() == true)
            itemDetail.setClosedTime(new Date());
        else {
            itemDetail.setClosedTime(null);
        }
    }

    private List<Integer> getNoTagGroupAssets() {
        if (tagNoGroupAssets == null) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if (orgUidList == null || orgUidList.isEmpty()) {
                orgUidList = Arrays.asList("");
            }
            tagNoGroupAssets = dao.getAssetsByNoTagGroup(orgUidList);
        }
        return tagNoGroupAssets;
    }

    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
            tagGroupAssets = dao.getAssetsByTagGroup(UserContextService.getCurrentUserAccount().getId());
        }
        return tagGroupAssets;
    }

    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        confirmedUpDate = null;
        selected = null;
        newServiceRequest = null;
        esTime = null;
        comments = null;
        selectedWorkOrder = null;
        failReason = null;
        rating = null;
        confirmedDownTime = null;
        selectedWorkOrderList = null;
        equipmentTaker = null;
        takeTime = null;
        picList = null;
        attachList = null;
        selectedAsset = null;
//      requestAssetUid = null;
        locationName = null;
    }

    public void onSelectWorkOrder() {
        newServiceRequest = null;
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        currentStatus = selected.getAssetInfo().getStatus();
        needConfirmTime = false;
        selectedWorkOrderList = dao.findBySrId(selected.getServiceRequest().getId());
        audioList = getServiceRequestPicList(selected.getSrId(), 2, null);
        picList = getServiceRequestPicList(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_IMG);


        attachList = getServiceRequestPicList(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_DOCS);
    }

    public List<V2_BlobObject> getServiceRequestPicList(String srId, Integer type, String boSubType) { //type 1:picture 2:audio

        List<V2_BlobObject> pList = woService.getPicListBySrUid(srId, type, boSubType).stream().filter(item -> type == 1 ^
                (item.getObjectType() != null && item.getObjectType().contains("audio"))).collect(Collectors.toList());

        return pList;
    }


    /*
     *   if type =1 -->picList
     *
     * */
    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return woService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return woService.getWorkOrderCoworker(woId);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String woId) {
        return selected == null ? null : woService.getServiceRequestMessageList(woId);
    }

    public void prepareAct(String woid) {
        selectedWorkOrder = dao.findById(woid);
        currentStatus = selectedWorkOrder.getAssetInfo().getStatus();
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        selectedApproverUid = null;
    }

    public void prepareNewWorkOrder(String woid) {
        prepareAct(woid);
        picList = new ArrayList<V2_BlobObject>();
        attachList = new ArrayList<V2_BlobObject>();
    }

    public void prepareAproval(String woid) {
        prepareAct(woid);
        selectedMap = new HashMap<>();
        approvedDetails = getWorkOrderDetail(selectedWorkOrder.getId());
        approvedDetails.stream().forEach(item -> {
            if (item.getApproved() == null) {
                item.setApproved(false);
            }
            selectedMap.put(item.getId(), item.getApproved());
        });
    }

    public List<V2_WorkOrder_Detail> getNeedApprovalDetails() {
        return approvedDetails == null ? null : approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).collect(Collectors.toList());
    }

    public void prepareCloseOrder(String woId) {
        prepareAct(woId);
        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                selectedWorkOrder.getAssetId());
        isRating = Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("ratingRequired")).orElse(false);

        picList = new ArrayList<V2_BlobObject>();
        attachList = new ArrayList<V2_BlobObject>();
        equipmentTaker = selectedWorkOrder.getServiceRequest().getEquipmentTaker();
        takeTime = selectedWorkOrder.getServiceRequest().getTakeTime();
    }
    public void prepareEditCloseOrder(String woId) {
        prepareAct(woId);
        picList = new ArrayList<V2_BlobObject>();
        attachList = new ArrayList<V2_BlobObject>();
        equipmentTaker = selectedWorkOrder.getServiceRequest().getEquipmentTaker();
        takeTime = selectedWorkOrder.getServiceRequest().getTakeTime();
    }
    
    public void saveEditedWorkOrder(){
        
        dao.save(selectedWorkOrder);
    }

    private Boolean hourRequired;

    public void prepareCompleteOrder(String woid) {
        selectedWorkOrder = dao.findById(woid);
        //获取当前工单的所属流程配置

        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                selectedWorkOrder.getAssetId());
        hourRequired = Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("hourRequired")).orElse(false);
        if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() == 0.0) {
            selectedWorkOrder.setManHours(null);
        }
        confirmedUpDate = selectedWorkOrder.getServiceRequest().getConfirmedUpTime();
        currentStatus = 1; //selectedWorkOrder.getAssetInfo().getStatus();
        accessoryList = getWorkOrderDetail(selectedWorkOrder.getId());
        coWorkerList = getWorkOrderCoworker(selectedWorkOrder.getId());
        picList = woService.getAttachByBoSubType(selectedWorkOrder.getSrId(), V2_BlobObject.BO_SUB_TYPE_IMG);
        attachList = woService.getAttachByBoSubType(selectedWorkOrder.getSrId(), V2_BlobObject.BO_SUB_TYPE_DOCS);
    }

    public void saveRepairWorkOrder() {
        if (hourRequired) {
            if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() < 0.01) {
                WebUtil.addErrorMessage("所填工时不能小于0.01");
                RequestContext.getCurrentInstance().addCallbackParam("manHours", true);
                return;
            }
        }
        /*else if(selectedWorkOrder.getManHours()!=null && selectedWorkOrder.getManHours()==0.00){
                selectedWorkOrder.setManHours(null);
            }*/
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        if (!attachList.isEmpty())
            picList.addAll(attachList);
        woService.saveRepairWorkOrder(selectedWorkOrder, confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList);
        cancel();
    }

    public List<UserAccount> getWorkerList() {
        return woService.getWorkerList(selectedWorkOrder.getAssetId());
    }

    public void ackWorkOrder() {
        woService.ackWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void updateDownTime() {
        woService.updateDownTime(selected, confirmedDownTime);
        needConfirmTime = false;
        this.selected.getServiceRequest().setConfirmedDownTime(confirmedDownTime);
    }

    public void rejectWorkOrder() {
        woService.rejectWorkOrder(selectedWorkOrder, failReason, currentStatus, confirmedDownTime);
        cancel();
    }

    public void rateWorkOrder() {
        woService.rateWorkOrder(selectedWorkOrder, rating, comments);
        cancel();
    }

    public void dispatchWorkOrder() {
        woService.dispatchWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void createWorkOrder() {
        selectedWorkOrder.setComment(comments);
        woService.reCreateWorkOrder(selectedWorkOrder, comments, selectedWorkOrder.getIntExtType(), picList);
        cancel();
    }

    public void checkIn(String woid) {
        checkInDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        V2_WorkOrder checkInWorkOrder = dao.findById(woid);
        woService.checkIn(checkInWorkOrder);
    }

    public void repairWorkOrder() {
        boolean valid = true;
        if (confirmedUpDate ==
                null) {
            WebUtil.addErrorMessage("恢复时间" + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("confirmedUpDate", true);
            valid = false;
        }
        if (null == selectedWorkOrder.getCaseType()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("caseType") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("caseType", true);
            valid = false;
        }
        if (null == selectedWorkOrder.getRepairResult()) {
            WebUtil.addErrorMessage("维修结果" + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("repairResult", true);
            valid = false;
        }


       /* WorkflowConfig woc = woConDao.getBySiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        hourRequired=  woc.getHourRequired();*/
        if (hourRequired) { //如果工作流配置了工时必填，那么就要jian
            if (null == selectedWorkOrder.getManHours() || selectedWorkOrder.getManHours() < 0.01) {
                WebUtil.addErrorMessage("工时不能小于0.01");
                RequestContext.getCurrentInstance().addCallbackParam("manHours_input", true);
                valid = false;
            }
        } else if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() == 0.0) {
            selectedWorkOrder.setManHours(null);
        }

        if (!valid) {
            return;
        }
        //gl:1表示是正常
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        //将pdf的文件一起合并到图片的list中传给后台
        picList.addAll(attachList);
        woService.repairWorkOrder(selectedWorkOrder, confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList);
        cancel();
    }

    public void cancelWorkOrder() {

        woService.cancelWorkOrder(selectedWorkOrder.getId(), cancelReason, assetStatus);

        cancelReason = null;
        cancel();
    }

    public void takeEquip() {
        if (Strings.isNullOrEmpty(equipmentTaker)) {
            WebUtil.addErrorMessage("取物人" + WebUtil.getMessage("ValidationRequire"));
        } else if (equipmentTaker.trim().isEmpty()) {
            WebUtil.addErrorMessage("取物人" + WebUtil.getMessage("ValidationRequire"));
        } else if (takeTime != null && takeTime.after(new Date())) {
            WebUtil.addErrorMessage("取物时间大于当前时间");
            takeTime = null;
        } else {
            if (null == takeTime) {
                takeTime = new Date();
            }
            woService.takeEquip(selectedWorkOrder, equipmentTaker, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(takeTime));
        }
    }

    public void closeWorkOrder() {
        Integer feedback = woDao.findById(selectedWorkOrder.getId()).getFeedbackRating();
//           if(isRating && feedback<1){
//               WebUtil.addErrorMessage("请务必给予评价");
//               RequestContext.getCurrentInstance().addCallbackParam("isRate", true);
//               return;
//           }else {
//           }
        woService.closeWorkOrder(selectedWorkOrder, comments, picList);
        cancel();
    }

    public void createServiceRequest() {
        cancel();
        if (!Strings.isNullOrEmpty(requestAssetUid)) {
            selectedAsset = assetDao.findByUid(requestAssetUid);
            onSelectedAsset(selectedAsset);
            currentStatus = selectedAsset.getStatus();
            if (currentStatus == 1) {
                currentStatus = 3;
            }
        }
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
        picList = new ArrayList();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
    }

    public void saveServiceRequest() {
        if (null == selectedAsset) {
            WebUtil.addErrorMessage("没有选择设备,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("所选设备没有所属科室,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(locationName);
            woService.createServiceRequest(newServiceRequest, selectedAsset, picList);
            cancel();
        }
    }

    private void onSelectedAsset(AssetInfo asset) {
        if (Strings.isNullOrEmpty(asset.getLocationName())) {
            final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
            String lName = orgDao.getByUid(orgInfoUID).getName();
            asset.setLocationName(lName);
            locationName = lName;
        } else {
            locationName = asset.getLocationName();
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        onSelectedAsset(asset);
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;

        if (this.newServiceRequest == null) {
            return;
        }

    }

    public void onStatusChange() {
        if (selectedWorkOrder != null) {
            selectedAsset = selectedWorkOrder.getAssetInfo();
        }

        if (selectedAsset == null && selected == null) {
            return;
        }
        if (null != selectedAsset) {//new sr
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public void acceptWorkOrder() {
        Integer repairType = selectedWorkOrder.getRepairType();
        woService.acceptWorkOrder(selectedWorkOrder, esTime, repairType, comments);
        cancel();
    }

    public void reassignWorkOrder() {
        woService.reassignWorkOrder(selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), comments);
        cancel();
    }

    public void prepareEditAccDetail(String itemId) {
        if ("new".equals(itemId)) {
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            itemDetail.setWoId(selectedWorkOrder.getId());
            itemDetail.setApplyTime(new Date());
            supplierName = null;

        } else {
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
            supplierName = itemDetail.getSupplier();
        }
        supplierList = aimService.getSuplierListBySiteUid(selectedWorkOrder.getTenantUID());
    }

    public void prepareEditAccDetailonPrice(String itemId) {
        accessoryList = woService.getWorkOrderDetails(selected.getId());//?如果一条woId仅仅对应一条workOrderDetail 这条代码就不需要

        itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();

    }

    public void prepareEditCoWorkerDetail(String itemId) {
        if ("new".equals(itemId)) {
            coWorkerDetail = new V2_WorkOrderCoworker();
            coWorkerDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            coWorkerDetail.setWoId(selectedWorkOrder.getId());

        } else {
            coWorkerDetail = coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void addAcessItem() {
        if (!accessoryList.contains(itemDetail)) {
            accessoryList.add(itemDetail);
        }
//        detailDao.save(itemDetail);
        cancelWorkdetail();
    }
    public void updateAccessPrice(){
        woService.updateAccessPrice(itemDetail);
    }

    public void addCoWokerItem() {
        if (coWorkerList.contains(coWorkerDetail)) {
            coWorkerList.remove(coWorkerDetail);
        }
        UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
        coWorkerDetail.setCowokerUserName(coWorker.getName());
        coWorkerList.add(coWorkerDetail);
        cancelWorkdetail();
    }

    public void deleteWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));

            // detailDao.delete(itemId);
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
    }

    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
    }

    public List<AssetFaultType> getAssetFaultType() {
        AssetInfo assetinfo = assetDao.findById(this.selectedWorkOrder.getAssetId());
        return assetFaultTypeService.getFaultTypeList(assetinfo.getAssetGroup());
    }

    public String getFaultTypeName(Integer id) {
        return assetFaultTypeService.getFaultName(id);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return orgDao.getByUid(orgUid);
    }

    public String getRequestReasonInShort(String reason) {
        if (reason == null) {
            return null;
        }
        if (reason.length() <= 20) {
            return reason;
        } else {
            return reason.substring(0, 19) + "...";
        }
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }
    public void removeAttachmentByIdPDF(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }


    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ServiceRequest);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void handleFileUploadPDF(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_ServiceRequest);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_DOCS);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void submitWorkOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> {
            item.setCreatedDate(null);
            if (null != item.getApproved() && !item.getApproved()) {
                item.setApproved(null);
            }
        });
        woService.submitWorkOrder(selectedWorkOrder, accessoryList, this.comments, this.selectedApproverUid);
        cancel();
    }

    public void approveWorkOrder() {
        if (approvedDetails.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId())) && Strings.isNullOrEmpty(comments)) {
            WebUtil.addErrorMessage("审核不通过原因必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needComents", true);
            return;
        }
        approvedDetails.stream().filter(item -> selectedMap.get(item.getId())).forEach(item -> item.setApproved(true));
        approvedDetails.forEach(item -> {
            item.setCreatedDate(null);
            if (null == item.getApproved()) {
                item.setApproved(false);
            }
        });
        woService.approveAction(selectedWorkOrder, approvedDetails, comments);
        cancel();
    }

    public List<V2_BlobObject> getPicListByBoType(String boType) {
        if (picList != null) {
            List<V2_BlobObject> collect = picList.stream().filter(item -> boType.equals(item.getBoType())).collect
                    (Collectors.toList());
            return collect;
        } else {
            return null;
        }
    }

    public void onApprovedDetailsSelected(String id) {
        if ("All".equals(id)) {
            getNeedApprovalDetails().stream().filter(item -> !item.getApproved()).forEach(item -> selectedMap.put(item.getId(), selectedMap.get("All")));
        } else {
            selectedMap.put("All", !selectedMap.entrySet().stream().anyMatch(item -> !item.getKey().equals("All") && !item.getValue()));
//            approvedDetails.stream().filter(item->item.getId().equals(id)).findFirst().get().setApproved(selectedMap.get(id));
        }
    }

    public void accessoryArrival(String itemId) {
        accessoryList.stream().filter(item -> itemId.equals(item.getId())).forEach(item -> item.setArrived(true));
    }

    //是否允许内修
    public Boolean canCreateSr() {
        if (selectedAsset != null && selectedAsset.getIbStatus() != null && selectedAsset.getIbStatus() == 1) {
            if (selectedAsset.getRepairForm() != null && selectedAsset.getRepairForm() == 2) {
                if (null != selectedAsset.getGeDeviceType() && (selectedAsset.getGeDeviceType() == 22 || selectedAsset.getGeDeviceType() == 3)) {
                    return selectedAsset.getWarrantyDate() == null || selectedAsset.getWarrantyDate().before(new Date());
                }
                return false;
            }
        }
        return true;
    }

    public List<UserAccount> getApproverList() {
        return woService.getApproverListByAsset(selectedWorkOrder.getAssetInfo().getUid());
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public V2_WorkOrder getSelectedWorkOrder() {
        return selectedWorkOrder;
    }

    public void setSelectedWorkOrder(V2_WorkOrder selectedWorkOrder) {
        this.selectedWorkOrder = selectedWorkOrder;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public List<V2_WorkOrder_Detail> getAccessoryList() {
        if (accessoryList == null) {
            return null;
        }
        V2_WorkOrder_Detail emptyDetail = new V2_WorkOrder_Detail();
        emptyDetail.setId("000");
        if (!accessoryList.contains(emptyDetail)) {
            accessoryList.add(emptyDetail);
        } else {
            accessoryList.remove(emptyDetail);
            accessoryList.add(emptyDetail);
        }
        return accessoryList;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<V2_WorkOrderCoworker> getCoWorkerList() {
        if (coWorkerList == null) {
            return null;
        }
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        if (!coWorkerList.contains(emptyDetail)) {
            coWorkerList.add(emptyDetail);
        } else {
            coWorkerList.remove(emptyDetail);
            coWorkerList.add(emptyDetail);
        }
        return coWorkerList;
    }

    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_WorkOrder> getSelectedWorkOrderList() {
        return selectedWorkOrderList;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
    }

    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<V2_BlobObject> audioList) {
        this.audioList = audioList;
    }

    public String getRequestAssetUid() {
        return requestAssetUid;
    }

    public void setRequestAssetUid(String requestAssetUid) {
        this.requestAssetUid = requestAssetUid;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public List<V2_WorkOrder_Detail> getApprovedDetails() {
        return approvedDetails;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public String getSelectedApproverUid() {
        return selectedApproverUid;
    }

    public void setSelectedApproverUid(String selectedApproverUid) {
        this.selectedApproverUid = selectedApproverUid;
    }

    public List<V2_BlobObject> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<V2_BlobObject> attachList) {
        this.attachList = attachList;
    }

    public AutoCompleteService getAutoCompleteService() {
        return autoCompleteService;
    }

    public void setAutoCompleteService(AutoCompleteService autoCompleteService) {
        this.autoCompleteService = autoCompleteService;
    }

    public AssetInfoMgrService getAimService() {
        return aimService;
    }

    public void setAimService(AssetInfoMgrService aimService) {
        this.aimService = aimService;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }
}

