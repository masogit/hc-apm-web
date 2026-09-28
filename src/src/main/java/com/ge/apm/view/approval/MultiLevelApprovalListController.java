package com.ge.apm.view.approval;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.mapper.MultiLevelApprovalMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.constant.ApprovalConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.service.utils.ZipUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ge.apm.service.constant.ApprovalConstant.APPROVAL_SEPARATOR;

@ManagedBean
@ViewScoped
public class MultiLevelApprovalListController extends GenericCRUDUUIDController<MultiLevelApproval> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelApprovalListController.class);

    public static final String TAB_INITIATID_BY_ME = "TAB_INITIATID_BY_ME";
    public static final String TAB_PENDING_APPROVAL_BY_ME = "TAB_PENDING_APPROVAL_BY_ME";
    public static final String TAB_APPROVED_BY_ME = "TAB_APPROVED_BY_ME";
    public static final String TAB_APPROVED_ALL = "TAB_APPROVED_ALL";

    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private MultiLevelApprovalController multiLevelApprovalController;
    private MultiLevelApprovalMapper multiLevelApprovalMapper;

    private UserAccountRepository userAccountRepository;
    private OrgInfoRepository orgInfoRepository;
    private UaaService uaaService ;
    private I18nMessageRepository i18nDao;
    @Getter
    @Setter
    private Integer status=null;
    private Integer filterBusinessType  = null;

    @Getter
    @Setter
    private boolean display;

    protected String currentTabId = TAB_INITIATID_BY_ME;

    @Override
    protected GenericRepositoryUUID<MultiLevelApproval> getDAO() {
        return multiLevelApprovalRepository;
    }

    @Override
    protected void init() {
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        multiLevelApprovalController =  WebUtil.getBean(MultiLevelApprovalController.class);
        multiLevelApprovalMapper =  WebUtil.getBean(MultiLevelApprovalMapper.class);

        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        orgInfoRepository =  WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nDao =  WebUtil.getBean(I18nMessageRepository.class);
        getBusinessTypeMap();
    }

    /**
     *  获取我发起的审批的列表
     * @return
     */
    @Override
    public Page<MultiLevelApproval> loadData(PageRequest pageRequest) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        Wrapper<NurseSiteMaintainConfig> wrapper = new EntityWrapper<>();
        Pagination pagination = new Pagination(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        Sort sort = pageRequest.getSort();
        if (ObjectUtil.isNotEmpty(sort)) {
            for (Sort.Order order : sort) {
                Sort.Direction direction = order.getDirection();
                if(order.getProperty().contains(".")){
                    String property = ZipUtil.camelToUnderline(order.getProperty());
                    wrapper.orderBy(property , "ASC".equals(direction.name()));
                }else{
                    String property = ZipUtil.camelToUnderline(order.getProperty());
                    wrapper.orderBy("la." + property , "ASC".equals(direction.name()));
                }
            }
        }else {
            wrapper.orderBy("la.approva_number", false);
        }
        wrapper.andNew().eq("la.tenant_uid", user.getTenantUID());

        if (TAB_INITIATID_BY_ME.equals(currentTabId)) {
            wrapper.andNew().eq("la.initiate_approver_uid", user.getUid());
        } else if (TAB_PENDING_APPROVAL_BY_ME.equals(currentTabId)) {
            wrapper.andNew().eq("la.approval_status", ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING).or().eq("la.approval_status", ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_ING);
            wrapper.andNew().eq("la.approval_node_status", ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
            wrapper.andNew().like("la.approval_uid", APPROVAL_SEPARATOR + user.getUid() + APPROVAL_SEPARATOR);
        } else  if (TAB_APPROVED_BY_ME.equals(currentTabId)) {//approved by me
           wrapper.andNew().eq("la.actual_approver_uid", user.getUid());
            wrapper.andNew().ne("la.approval_node_status", ApprovalConstant.MULTI_LEVEL_STATUS_APPROVAL_WAITING);
        }else  if (TAB_APPROVED_ALL.equals(currentTabId)) {
            List<OrgInfo> siteList = uaaService.getUserAccessibleSiteList(UserContextService.getCurrentUserAccount().getUid());
            List<String> uids = siteList.stream().map(OrgInfo::getUid).collect(Collectors.toList());
            wrapper.andNew().in("la.config_org_uid", uids);
        }
        WrapperSearchFilterUtils.convert(searchFilters, wrapper);
        List<MultiLevelApproval> list = multiLevelApprovalMapper.loadData(pagination, wrapper, user.getTenantUID());
        Page<MultiLevelApproval> result = new PageImpl<>(list, pageRequest, pagination.getTotal());
        return result;
    }

    private void addApplicantClinicalDeptName(List<MultiLevelApproval> content) {
        if(!CollectionUtils.isEmpty(content)){
            Set<String> userUids = content.stream().map(item -> item.getInitiateApproverUid()).collect(Collectors.toSet());
            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUids);
            Set<String> orgUids = userAccounts.stream().map(ite -> ite.getOrgInfoUID()).collect(Collectors.toSet());
            List<OrgInfo> orgInfos = orgInfoRepository.findInUIds(orgUids);
            Map<String, UserAccount> userAccountMap = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, userAccount -> userAccount));
            Map<String, OrgInfo> orgInfoMap = orgInfos.stream().collect(Collectors.toMap(OrgInfo::getUid, orgInfo -> orgInfo));
            content.stream().forEach(item->{
                UserAccount userAccount = userAccountMap.get(item.getInitiateApproverUid());
                OrgInfo orgInfo = orgInfoMap.get(userAccount.getOrgInfoUID());
                if(Objects.equals(orgInfo.getOrgType(),4)){
                    item.setApplicationDepartment(orgInfo.getName());
                }
            });
        }
    }

    private PageRequest addRequiredSortByCreatedDate(PageRequest pageRequest) {
        final String FIELD_CREATD_DATE = "approvalNumber";//更改为按照审批号倒序
        Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
        boolean exists = false;
        while(orders.hasNext()){
            if(orders.next().getProperty().equals(FIELD_CREATD_DATE)){
                exists = true;
                break;
            }
        }
        if (exists) {
            return pageRequest;
        } else {
            Sort sort = new Sort(Sort.Direction.DESC, FIELD_CREATD_DATE);
            pageRequest= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort().and(sort));
        }
        return pageRequest;
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }

        MultiLevelApproval multiLevelApproval = (MultiLevelApproval) event.getObject();
        multiLevelApprovalController.onServerEvent(null, multiLevelApproval);
        display = true;
    }

    public void promptApproval(MultiLevelApproval multiLevelApproval) {
        display=false;
        multiLevelApprovalController.onServerEvent(null, multiLevelApproval);
    }

    public boolean isInitiatedByMe() {
        return TAB_INITIATID_BY_ME.equals(currentTabId);
    }

    public boolean isPendingApprovalByMe() {
        return TAB_PENDING_APPROVAL_BY_ME.equals(currentTabId);
    }

    public boolean isApprovedByMe() {
        return TAB_APPROVED_BY_ME.equals(currentTabId);
    }

    public boolean isApprovedAll() {
        return TAB_APPROVED_ALL.equals(currentTabId);
    }

    public void initiateApprovalByMe() {
        currentTabId = TAB_INITIATID_BY_ME;
        switchTab();
    }

    public void pendingApprovalByMe() {
        currentTabId = TAB_PENDING_APPROVAL_BY_ME;
        switchTab();
    }

    public void approvedApprovalByMe() {
        currentTabId = TAB_APPROVED_BY_ME;
        switchTab();
    }

    public void approvedApprovalByALL() {
        currentTabId = TAB_APPROVED_ALL;
        switchTab();
    }


    public void switchTab() {
        searchFilters = new ArrayList();
        multiLevelApprovalController.onServerEvent(null, null);
    }

    HashMap<Integer, String> businessMap = new HashMap<>();

    public Map<Integer,String> getBusinessTypeMap(){

        businessMap.put(ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT,ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT,ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT,ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT,ApprovalConstant.APPROVAL_ASSET_REPAIR_CHi);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT,ApprovalConstant.APPROVAL_ASSET_MAINTAIN_CHi);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT,ApprovalConstant.APPROVAL_ASSET_BIG_REPAIR);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW,ApprovalConstant.APPROVAL_ASSET_BORROW_CHI);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE,ApprovalConstant.APPROVAL_ASSET_RECEIVE_CHI);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN,ApprovalConstant.APPROVAL_ASSET_BORROW_RETURN_CHI);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN,ApprovalConstant.APPROVAL_ASSET_RECEIVE_RETURN_CHI);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER,ApprovalConstant.APPROVAL_ASSET_TRANSFER_CHI);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT,ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS,ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT,ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT,ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP,ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT,ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT,ApprovalConstant.BUSINESS_TYPE_BATH_ASSET_DEPARTMENT_CONVERT_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT,ApprovalConstant.BUSINESS_TYPE_ASSET_EQUIPMENT_ADJUSTMENT_NAME);
        businessMap.put(ApprovalConstant.BUSINESS_TYPE_MAINTENANCE_CONTRACT_APPLY_PURCHASE,ApprovalConstant.APPROVAL_MAINTENANCE_CONTRACT_APPLY_PURCHASE_CHI);
        return  businessMap;
    }

//    public List<Integer> getBusinessTypeList(){
//        List<Integer> businessTypeList = Arrays.asList(ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_REQUEST_INT, ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_PURCHASE_INT, ApprovalConstant.BUSINESS_TYPE_FILE_INFORMATION_MODIFICATION_INT,
//                ApprovalConstant.BUSINESS_TYPE_ASSET_REPAIR_INT, ApprovalConstant.BUSINESS_TYPE_ASSET_MAINTAIN_INT, ApprovalConstant.BUSINESS_TYPE_ASSET_OVERHAUL_INT,
//               ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW, ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE, ApprovalConstant.BUSINESS_TYPE_ASSET_BORROW_RETURN, ApprovalConstant.BUSINESS_TYPE_ASSET_RECEIVE_RETURN, ApprovalConstant.BUSINESS_TYPE_ASSET_TRANSFER, ApprovalConstant.BUSINESS_TYPE_SPARE_PARTS_RETURN_INT, ApprovalConstant.BUSINESS_TYPE_ADVERSE_EVENTS, ApprovalConstant.BUSINESS_TYPE_BUDGET_MANAGEMENT, ApprovalConstant.BUSINESS_TYPE_PURCHASE_MANAGEMENT, ApprovalConstant.BUSINESS_TYPE_ASSET_SCRAP, ApprovalConstant.BUSINESS_TYPE_ASSET_DEPARTMENT_CONVERT, ApprovalConstant.BUSINESS_TYPE_EQUIPMENT_ADJUSTMENT
//        );
//        return  businessTypeList;
//    }

    public List<I18nMessage> getBusinessTypeList(){
        return i18nDao.findByMsgTypeOrderById(ApprovalConstant.BUSINESS_TYPE);
    }

    public Integer getFilterBusinessType() {
        return filterBusinessType;
    }

    public void setFilterBusinessType(Integer filterBusinessType) {
        this.filterBusinessType = filterBusinessType;
    }

    public HashMap<Integer, String> getBusinessMap() {
        return businessMap;
    }

    public void setBusinessMap(HashMap<Integer, String> businessMap) {
        this.businessMap = businessMap;
    }
}
