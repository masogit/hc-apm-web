
package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.sharedasset.SharedAssetRequestService;
import com.ge.apm.service.sharedasset.SharedAssetService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.AllocationCenterUtils;
import com.ge.apm.service.utils.ChargingType;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import javaslang.Tuple3;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author caoyong
 */
@ManagedBean
@ViewScoped
public class SharedAssetRequestV2Controller extends GenericCRUDUUIDController<SharedAssetRequest>{
    UserOrgInfoRepository userOrgDao = null;
    OrgInfoRepository orgInfoDao = null;
    SharedAssetRequestRepository dao = null;
    SharedAssetReqDetailRepository asdDao = null;
    SharedAssetReqTypeRepository apdDao = null;
    AssetInfoRepository asiDao = null;
    SharedAssetTypeRepository swapDao = null;
    SharedAssetConfigRepository sharedAssetConfigDao = null;
    UserRoleRepository userRoleRepository = null;
    SysRoleRepository sysRoleDao;
    JdbcTemplate jdbcTemplate = null;

    String p = null;
    private Double total;
    private UaaService uuaService;
    private SharedAssetService sharedAssetService;
    private SharedAssetRequestService sharedAssetRequestService;
    private Integer queryIndex=-1;
    private String operation;
    private Date applyTimeTo = null;
    private Date applyTimeFrom = null;
    private String applyTimeFormatTime = null;
    private String filterApplyTime = null;
    private UserAccount selectedAssignee;
    private UserContextService userContextService;
    private Integer dialogTitle;
    private Integer stock=0;
    private boolean isReturn=false;
    private ConfigService configService;
    private ConfigItemValueRepository configItemValueRepository;
    private UserAccountRepository userAccountRepository;
    private MultiLevelApprovalService multiLevelApprovalService;
    private BlobFileService blobService;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private List<SharedAssetRequest> sharedAssetRequests;
    private List<V2_BlobObject> deleteAttachments;
    private String viewImageStr;
    private String signatureContent;
    private StreamedContent signatureContentStream;
    private V2_WorkOrderService v2_workOrderService;
    private OrgInfoRepository orgDao;
    List<Map<String, String>> userSignature;
    String lastSignatureId ;
    @Getter
    @Setter
    private WorkflowConfig currentWorkflowConfig;

    @Getter
    @Setter
    private SharedAssetReqDetail preSelectReqDetail;

    @Getter
    @Setter
    private String notificationContent = "";

    @Getter
    @Setter
    private String selectedSartId;

    private String workflowConfigJson;

    private SystemEventAuditAopAspect systemEventAuditAopAspect;

    @Getter @Setter
    private OrgInfo currentUserOrgInfo;

    public String getFilterApplyTime() {
        return filterApplyTime;
    }

    public void setFilterApplyTime(String filterApplyTime) {
        this.filterApplyTime = filterApplyTime;
    }

    public Date getApplyTimeTo() {
        return applyTimeTo;
    }
    
    public void setApplyTimeTo(Date applyTimeTo) {
        this.applyTimeTo = applyTimeTo;
    }

    public Date getApplyTimeFrom() {
        return applyTimeFrom;
    }

    public void setApplyTimeFrom(Date applyTimeFrom) {
        this.applyTimeFrom = applyTimeFrom;
    }
    
    public String getApplyTimeFormatTime() {

            return applyTimeFormatTime;
    }

    public void setApplyTimeFormatTime(String applyTimeFormatTime) {
        this.applyTimeFormatTime = applyTimeFormatTime;
    }
    
    private SharedAssetReqType selectedSart = new SharedAssetReqType();//借用申请优化机制

    public SharedAssetReqType getSelectedSart() {
        return selectedSart;
    }

    public void setSelectedSart(SharedAssetReqType selectedSart) {
        this.selectedSart = selectedSart;
    }
    
    private SharedAssetReqDetail selectedSard;//录入设备优化

    public SharedAssetReqDetail getSelectedSard() {
        return selectedSard;
    }

    public void setSelectedSard(SharedAssetReqDetail selectedSard) {
        this.selectedSard = selectedSard;
    }
    
    public UserAccount getSelectedAssignee() {
    	return this.selectedAssignee;
    }
    
    public void setSelectedAssignee(UserAccount ua) {
    	this.selectedAssignee = ua;
    }
    
    public void prepareCreateSart(){
        crudActionName = "Create";
        selectedSart = new SharedAssetReqType();
        setReturn(RequestContext.getCurrentInstance());
        this.setStock(0);
    }
    
    public void prepareEditSart(){
        crudActionName = "Edit";
        setReturn(RequestContext.getCurrentInstance());
        this.queryStock();
    }
    
    public void deleteSart(){
        if(selectedSart!=null)
            selected.getSartList().remove(selectedSart);
        selectedSart = null;
    }

    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        sharedAssetService = WebUtil.getBean(SharedAssetService.class);
        dao = WebUtil.getBean(SharedAssetRequestRepository.class);
        asdDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        apdDao = WebUtil.getBean(SharedAssetReqTypeRepository.class);
        asiDao = WebUtil.getBean(AssetInfoRepository.class);
        swapDao = WebUtil.getBean(SharedAssetTypeRepository.class);
        userOrgDao = WebUtil.getBean(UserOrgInfoRepository.class);
        sysRoleDao = WebUtil.getBean(SysRoleRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        configService = WebUtil.getBean(ConfigService.class);
        sharedAssetConfigDao = WebUtil.getBean(SharedAssetConfigRepository.class);
        userRoleRepository = WebUtil.getBean(UserRoleRepository.class);
        sharedAssetRequestService = WebUtil.getServiceBean(SharedAssetRequestService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        jdbcTemplate = WebUtil.getServiceBean("jdbcTemplate", JdbcTemplate.class);
        nurseSiteMaintainService = WebUtil.getBean( NurseSiteMaintainService.class);
        v2_workOrderService = WebUtil.getBean(V2_WorkOrderService.class);
        systemEventAuditAopAspect = WebUtil.getBean(SystemEventAuditAopAspect.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);

        currentUserOrgInfo = orgDao.findByUid(UserContextService.getCurrentUserAccount().getSiteUID());

        sharedAssetRequests = new ArrayList<>();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        p=request.getServletPath();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(SharedAssetRequestV2Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
        else if ("View".equalsIgnoreCase(actionName)) {
            prepareView();
        }
        else if ("Edit".equalsIgnoreCase(actionName)) {
            setSelected(dao.findById((String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            if(selected != null){
                selected.setAttachments(initAttachments(selected.getId()));
                if(selected.getBehavior() == 2){
                    List<SharedAssetReqDetail> sharedAssetReqDetails = asdDao.findByReturnId(selected.getId());
                    for (SharedAssetReqDetail sharedAssetReqDetail : sharedAssetReqDetails) {
                        addSharedAssetRequests(sharedAssetReqDetail);
                    }
                }

               initSignatureContent();
            }
            prepareEdit();
        }
        else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        } 
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }

        if(currentWorkflowConfig == null) {
            currentWorkflowConfig = getWorkflowConfig();
            if(selected != null && selected.getBehavior() != null) {
                if(selected.getBehavior().intValue() == 1) {
                    //借用
                    if(currentWorkflowConfig.getEnableLeaseManHour() != null && currentWorkflowConfig.getEnableLeaseManHour()) {
                        if(selected.getEffort() == null) {
                            selected.setEffort(currentWorkflowConfig.getDefaultLeaseManHour());
                        }
                    }
                }
                if(selected.getBehavior().intValue() == 2) {
                    //归还
                    if(currentWorkflowConfig.getEnableReturnManHour() != null && currentWorkflowConfig.getEnableReturnManHour()) {
                        if(selected.getEffort() == null) {
                            selected.setEffort(currentWorkflowConfig.getDefaultReturnManHour());
                        }
                    }
                }
            }
            //JIRA#4576
            if(currentWorkflowConfig != null && StringUtils.isNotBlank(currentWorkflowConfig.getSiteUID())) {
                workflowConfigJson = currentWorkflowConfig.getSwapCenterConfig();
                List<SharedAssetConfig> sharedAssetConfigList = sharedAssetConfigDao.findBySiteUID(currentWorkflowConfig.getSiteUID());
                String virtualCellNum = "";
                if(sharedAssetConfigList != null && sharedAssetConfigList.size() > 0) {
                    virtualCellNum = sharedAssetConfigList.get(0).getTel();
                }
                this.setNotificationContent(virtualCellNum);
            }
        }
    }
    private WorkflowConfig getWorkflowConfig(){
        OrgInfo selectSite = userContextService.getSelectSite();
        if(selectSite != null){
          return   configService.findConfigBySite(selectSite.getUid());
        }else{
            return  configService.findConfigBySite(UserContextService.getCurrentUserAccount().getSiteUID());
        }
    }
    @Override
    protected SharedAssetRequestRepository getDAO() {
        return dao;
    }

    public String getViewPage(String id, String actionName) {
        operation = "Edit.xhtml?actionName=Edit&selectedid=" + "1";
        return UrlEncryptController.encodeUrlParam(operation);
    }
    
    @Override
    protected Page<SharedAssetRequest> loadData(PageRequest pageRequest) {
//       setSearchFilter();
       
       setUserSearchFilters();

        if (queryIndex == -2) {
            List<String> stateList = new ArrayList<String>();
            stateList.add("0");
            stateList.add("1");
            stateList.add("2");
            stateList.add("3");
            stateList.add("4");
            stateList.add("5");
            stateList.add("6");
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }else if (queryIndex == -3) {
            List<String> stateList = new ArrayList<String>();
            stateList.add("7");
            stateList.add("8");
            stateList.add("9");
            stateList.add("10");
            stateList.add("11");
            stateList.add("12");
            stateList.add("13");
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }else if(queryIndex == -1){
            if(p.equalsIgnoreCase("/portal/BorrowApplyV2/List.xhtml")){//分配页面初始List
                List<String> stateList = new ArrayList<String>();
                stateList.add("0");
                stateList.add("1");
                stateList.add("2");
                stateList.add("3");
                stateList.add("4");
                stateList.add("5");
                stateList.add("6");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/ReturnApplyV2/List.xhtml")){//分配页面初始List
                reviseState();
                List<String> stateList = new ArrayList<String>();
                stateList.add("7");
                stateList.add("8");
                stateList.add("9");
                stateList.add("10");
                stateList.add("11");
                stateList.add("12");
                stateList.add("13");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/BorrowAcceptanceV2/List.xhtml")){
                List<String> stateList = new ArrayList<String>();
                stateList.add("1");
                stateList.add("2");
                stateList.add("5");
//                stateList.add("6");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }else if(p.equalsIgnoreCase("/portal/ReturnAcceptanceV2/List.xhtml")){
                List<String> stateList = new ArrayList<String>();
                stateList.add("7");
                stateList.add("8");
                stateList.add("11");
                searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
            }
        }else {
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.EQ,queryIndex.toString()));
        }

//        currentWorkflowConfig = wfCfigDao.findFirstBySiteUID(UserContextService.getCurrentUserAccount().getSiteUID());

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    public void reviseState(){
        if(!CollectionUtils.isEmpty(searchFilters)){
          boolean flag = searchFilters.stream().anyMatch(item -> "state".equals(item.fieldName) && "14".equals(item.value));
          if(flag){
              SearchFilter searchFilterState = new SearchFilter("state", SearchFilter.Operator.EQ,"12");
              updateSearchFilter(searchFilterState);
              SearchFilter searchFilterSignatureId = new SearchFilter("signatureId", SearchFilter.Operator.IsNull,null);
              searchFilters.add(searchFilterSignatureId);
          }
        }
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public List<SharedAssetRequest> getItemList() {
        return dao.find();
    }
    
    @Override
    public void onBeforeSave(SharedAssetRequest object) {
        OrgInfo userOrg = orgInfoDao.getByUid(object.getClinicalDeptUid());
        object.setClinicalDeptName(userOrg.getName());
        object.setApplyAssetInfo(null);
    }
    
    @Override
    public void onBeforeNewObject(SharedAssetRequest object) {
     
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setState(1);
        object.setApprovalStatus(-1);
        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/Edit.xhtml")){
            //借用待受理
            object.setState(1);
            object.setBehavior(1);
        }else if(p.equalsIgnoreCase("/portal/ReturnApplyV2/Edit.xhtml")){
            //归还待受理
            object.setState(7);
            object.setBehavior(2);
        }

        //object.setCreateDate(new Date());
        object.setApplyTime(new Date());
        object.setClinicalDeptUid(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setCreateBy(UserContextService.getCurrentUserAccount().getUid());
        
        object.setApplicant(UserContextService.getCurrentUserAccount().getLoginName());
        object.setApplicantPhone(UserContextService.getCurrentUserAccount().getTelephone());
        
        String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
        OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
        
        if(userOrg.getOrgType()==4){
            object.setClinicalDeptName(userOrg.getName());
            object.setClinicalDeptUid(userOrg.getUid());
        }
       
        //Date today = getNexDate(new Date());
//        today.setHours(0);
//        today.setMinutes(0);
//        today.setSeconds(0);
//        Calendar c = Calendar.getInstance();
//        c.setTime(today);
//        c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
//        c.add(Calendar.);
//        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
//        ft.format(today);
//        object.setInvalidTime(today);
        
        
    }
    private String generateOrderNumber(){
        StringBuilder sb = new StringBuilder("SAR");
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        String dateStr = format.format(new Date());
        sb.append(dateStr);
        long count = dao.count();
        if(count < 1){
            sb.append(StringUtils.leftPad(String.valueOf(count),7,"0"));
        }else{
            sb.append(StringUtils.leftPad(String.valueOf(count),7,"0"));
        }
        return sb.toString();
    }

    //根据登录人信息配置权限
    private void setUserSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        List<String> orgUidList =new ArrayList<>();
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = userContextService.getSelectSite();
        UserAccount user = UserContextService.getCurrentUserAccount();
        if(selectSite == null){
            orgUidList = userOrgDao.findAccessUidByUserUid(user.getUid());

        }else{
            orgUidList = orgInfoDao.getOrgInfoUidsByparentUid(selectSite.getUid());
        }

        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
//        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*"));

//        if(p.equalsIgnoreCase("/portal/BorrowApplyV2/List.xhtml")||p.equalsIgnoreCase("/portal/ReturnApplyV2/List.xhtml")){
//            searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        }

    }
    
    public Date getNexDate(Date date){
        int n = 1;
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");  
        SimpleDateFormat dd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
        return new Date(date.getTime() + n * 24 * 60 * 60 * 1000L); 
        
    }
    
    public void onSelectB() throws IOException {
        String pageName = "Edit_1";
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    
    public void onSelectD() throws IOException {
        
        String pageName = "Edit_1";
       
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }
    
    public void onSelectE() {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();

        String pageName = "";
        if(sta == 8){
            pageName = "Edit_1";
            selected.setMsgEndTime(new Date());
            selected.setMsgAccepter(this.getNameByLoginName(selected.getApplicant()));
//            selected.setMsgEndTime(this.getTime("归还"));
            selected.setState(sta);
            this.save();
        }else if(sta == 7 || sta == 11){
            pageName = "Edit";
        }else if(sta == 12 || sta == 9 || sta == 10 || sta == 13){
            pageName = "Edit_2";
        }

        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        requestContext.addCallbackParam("pageName", pageName);
    }
    
    public void onSelectC() {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();

        String pageName = "";
        selected.getSartList();
        if(sta == 2){
            pageName = "Edit_1";
//            selected.setMsgAccepter(this.selected.getApplicant());
            selected.setAccepter(this.getNameByLoginName(selected.getApplicant()));
//            selected.setMsgBeginTime(this.getTime("借用"));
            selected.setMsgBeginTime(new Date());
            selected.setState(sta);
            this.save();
        }else if(sta == 1||sta == 5){
            pageName = "Edit";
        }else if(sta == 6 || sta == 3 || sta == 4 || sta == 0){
            pageName = "Edit_2";
        }

        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        requestContext.addCallbackParam("pageName", pageName);

    }
    
    public String queryBorrowInfo(String assetId,String clinicalDeptUID){

        List<String> borrowIds = asdDao.getBorrowDetail(assetId, clinicalDeptUID);
        if(borrowIds.size()!=1){
            WebUtil.addErrorMessage("找不到借用详情！");
            return "";
        }else{
            operation = "EditForQuery.xhtml?actionName=Edit&selectedid=" + borrowIds.get(0);
            //ArrivalList o=new ArrivalList();
            return UrlEncryptController.encodeUrlParam(operation);
        }

    }


     public void getAssetInfoR(Date times,String msgAccepter) throws ParseException{//查询并插入归还设备信息
         Optional.ofNullable(dao.findById(selected.getId()).getState()).ifPresent(val -> {
             if (val != 8) {
                 WebUtil.addErrorMessage("申请已取消或已完成交付，不能录入设备，请返回并刷新！");
                 return;
             }
         });
         List<AssetInfo> assetInfoList = selected.getInfoList();
         //根据二维码or设备编号or资产编号查询设备信息
         List<AssetInfo> infoList = asiDao.findSwapTypeInfo(selected.getApplyAssetInfo(), selected.getApplyAssetInfo(), selected.getApplyAssetInfo(), this.selected.getSiteUID());
         if (!CollectionUtils.isEmpty(infoList)) {
             //根据设备UID剔除已录入设备
             for (AssetInfo selectedAssetInfo : assetInfoList) {
                 infoList = infoList.stream().filter(assetInfo -> !assetInfo.getUid().equals(selectedAssetInfo.getUid())).collect(Collectors.toList());
             }
             if (infoList.size() < 1) {
                 WebUtil.addWarnMessage("该设备已录入,请勿重复操作!");
                 return;
             }
             infoList.forEach(infoVo -> {
                 List<String> depts = dao.findDeptForSharedAsset(this.selected.getSiteUID());
                 String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
                 OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
                 //if current user has role like AssetHead(2), AssetStaff(3), SharedAssetMgmt(10), this user will be treated as allocation center admin
                 if (null == infoVo.getSharedAssetTypeUid() || infoVo.getSharedAssetTypeUid().equalsIgnoreCase("0")) {
                     WebUtil.addWarnMessage("该设备不属于调剂设备类型！");
                     return;
                 }
                 //在asset_info中判断是否属于 调剂中心科室
                 if(depts.contains(infoVo.getAssetDeptUID())){
                     WebUtil.addWarnMessage("该设备已归还！");
                     return;
                 }
                 if (!isAllocationCenterAdmin()) {
                     if (!depts.contains(userOrg.getUid())) {
                         WebUtil.addErrorMessage("当前角色非调剂中心人员，请核实后操作！");
                         return;
                     }
                 }
                 if(selected.getClinicalDeptUid() == null || !selected.getClinicalDeptUid().equals(infoVo.getAssetDeptUID())) {
                     WebUtil.addWarnMessage("该设备由" + orgInfoDao.findNameByUID(infoVo.getAssetDeptUID()) + "借用");
                     return;
                 }
                 List<SharedAssetReqType> sharedAssetTypes = Optional.of(selected.getSartList()).orElse(new ArrayList<>());
                 for (SharedAssetReqType applySwapType : sharedAssetTypes) {
                     if (this.isLogPatientInfoEnabled()) {
                         if (StringUtils.isNotBlank(this.selectedSartId) && !this.selectedSartId.equals(applySwapType.getId())) {
                             continue;
                         }
                     }
                     if (infoVo.getSharedAssetTypeUid().equals(applySwapType.getSharedAssetTypeId())) {
                         selectedSart = applySwapType;//明细表联动选中
                         if (applySwapType.getActualQty() < applySwapType.getApplyQty()) {
                             String sql = "select a.* from shared_asset_req_detail a inner join shared_asset_request b on b.id = a.borrow_id where a.asset_info_id = '"+infoVo.getUid()+"' and b.state in (3, 6) AND a.return_id is NULL";
                             List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
                             ArrayList<SharedAssetReqDetail> detail_list = new ArrayList<>();
                             for (int i = 0; i < data.size(); i++) {
                                 Map<String, Object>list1 = data.get(i);
                                 SharedAssetReqDetail request2 = JSON.parseObject(JSON.toJSONString(list1), SharedAssetReqDetail.class);
                                 if(list1.containsKey("shared_asset_type")){
                                     String sharedAssetType =(String) list1.get("shared_asset_type");
                                     request2.setSharedAssetTypeId(sharedAssetType);
                                 }
                                 detail_list.add(request2);
                             }
                             if (detail_list.size() == 1) {
                                 if (!infoVo.getAssetDeptUID().equals(detail_list.get(0).getClinicalDeptUid())) {
                                     WebUtil.addErrorMessage("该设备当前使用科室与申请科室不一致，使用科室是:" + infoVo);
                                     return;
                                 }
                             } else {
                                 WebUtil.addErrorMessage("获取该设备借用信息有误！");
                                 return;
                             }

                             if(preSelectReqDetail == null) {
                                 preSelectReqDetail = detail_list.get(0);
                             }
                             RequestContext context = RequestContext.getCurrentInstance();
                             //半点计费结束时间需要特殊处理
                             if(ChargingType.PAY_HALF_HOUR.getIndex().equals(preSelectReqDetail.getChargingType())) {
                                 //时间往前推
                                 preSelectReqDetail.setChargingEndTime(this.setEndTime(times));
                             } else {
                                 preSelectReqDetail.setChargingEndTime(times);
                             }
                             // perform validation for charging begin time
                             if(preSelectReqDetail.validateChargingBeginTime()) {
                                 //TODO
                             } else {
                                 WebUtil.addErrorMessage("计费开始时间必须在当前时间之前");
                                 context.addCallbackParam("isValidReqDetail", Boolean.FALSE);
                                 return;
                             }
                             // perform validation for charging end time
                             if(preSelectReqDetail.validateChargingEndTime()) {
                                 //TODO
                             } else {
                                 WebUtil.addErrorMessage("计费结束时间必须在计费开始时间之后，并在未来两小时内");
                                 context.addCallbackParam("isValidReqDetail", Boolean.FALSE);
                                 return;
                             }

                             SharedAssetReqDetail sard = preSelectReqDetail;
                             sard.setReturnId(selected.getId());
                             sard.setAssetInfoId(infoVo.getUid());
                             sard.setAssetName(infoVo.getName());
                             sard.setAssetNumber(infoVo.getDepartNum());
                             sard.setBorrowDealUid(UserContextService.getCurrentUserAccount().getUid());
                             sard.setFinancingNum(infoVo.getFinancingNum());
                             sard.setClinicalDeptForReturnUid(this.selected.getClinicalDeptUid());
                             sard.setClinicalDeptForReturnName(this.selected.getClinicalDeptName());
                             sard.setMsgAccepter(msgAccepter.isEmpty() ? selected.getApplicant() : msgAccepter);
                             SharedAssetType swapTypeVo = swapDao.findById(applySwapType.getSharedAssetTypeId());

                             if (times == null) {
                                 sard.setChargingEndTime(this.setEndTime(new Date()));
                             }
                             sard.setChargingType(swapTypeVo.getChargingType());
                             if (sard.getUnitPrice() == null) {
                                 sard.setUnitPrice(swapTypeVo.getUnitPrice());
                             }
                             sard.setUnit(swapTypeVo.getUnit());
                             //JIRA#3043
                             sard.setCost(this.getTotalAmount(sard));
                             //附件状态默认全选
                             for(SharedAssetTypeAccesssory item:sard.getAccessoryList()){
                                 if (item.getBorrowStatus()) {
                                     item.setReturnStatus(true);
                                 }
                             }
                             //附件状态默认全选
                             sard.getAccessoryList().forEach(item -> {if (item.getBorrowStatus() == null) {item.setBorrowStatus(true);}});
                             logger.info("isLogPatientInfoEnabled: " + isLogPatientInfoEnabled());
                             if (isLogPatientInfoEnabled() && StringUtils.isNotBlank(applySwapType.getPatientNum())) {
                                 sard.setPatientNum(applySwapType.getPatientNum());
                                 sard.setPatientBedNum(applySwapType.getPatientBedNum());
                             }
                             if (selected.getSardList() != null) {
                                 selectedSard = sard;
                                 selected.getSardList().add(sard);
                                 addSharedAssetRequests(sard);
                             }
                             preSelectReqDetail = null;
                             //更改使用科室
                             if(userOrg.getOrgType()==3 || userOrg.getOrgType()==4){
                                 depts.stream().findFirst().ifPresent(infoVo::setAssetDeptUID);
                                 infoVo.setAssetDeptId(infoVo.getClinicalDeptId());
                                 String clinicalDeptUid = infoVo.getClinicalDeptUID();
                                 if(StringUtils.isNotBlank(clinicalDeptUid)) {
                                     OrgInfo clinicalDept = orgInfoDao.findByUid(clinicalDeptUid);
                                     infoVo.setClinicalDeptName(clinicalDept.getName());
                                 }
                             }
                             assetInfoList.add(infoVo);
                             applySwapType.setActualQty(applySwapType.getActualQty() + 1);
                             System.out.println("Context: " + context.getCallbackParams().get("isValidReqDetail"));
                             WebUtil.addSuccessMessage("录入成功！");
                         }else{
                             WebUtil.addWarnMessage("该设备类型 ‘"+applySwapType.getSwapName()+"’ 申请数量已满！");
                         }
                         return;
                     }
                 }
                 WebUtil.addWarnMessage("该设备不属于归还申请的设备类型！");
                 return;
             });
         }else{
             WebUtil.addWarnMessage("录入信息不符，请核对后录入！");
             return;
         }
    }
    private void initSignatureContent(){
        String signatureId = selected.getSignatureId();
        if(StringUtils.isNotBlank(signatureId)){
            signatureContentStream = nurseSiteMaintainService.getMaintainSignature(signatureId);
        }else{
            try {
                 userSignature = v2_workOrderService.getUserSignature();
                if(!CollectionUtils.isEmpty(userSignature)){
                    lastSignatureId = userSignature.get(userSignature.size() - 1).get("signatureUid");
                    this.signatureContentStream = nurseSiteMaintainService.getMaintainSignature(lastSignatureId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addSharedAssetRequests(SharedAssetReqDetail sard){
        if(sard == null)return;
        String borrowId = sard.getBorrowId();
        SharedAssetRequest request = dao.findById(borrowId);
        List<V2_BlobObject> blobObjects = initAttachments(borrowId);
        List<SharedAssetReqDetail> sardList = new ArrayList<>();
        sardList.add(sard);
        request.setSardList(sardList);
        request.setAttachments(blobObjects);
        sharedAssetRequests.add(request);
    }
    
    public void setCost(SharedAssetReqDetail sard){
        if (sard.getChargingEndTime() != null) {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long diff = sard.getChargingEndTime().getTime() - (sard.getChargingBeginTime()).getTime();
            Integer countQty = null;
            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            long min = diff % nd % nh / nm;

            double number = day * 24 + hour + min/60.0;
            number = number < 0 ? 0 : number;

            if ("1".equals(sard.getChargingType())) {
                number = 1;
            } else if ("3".equals(sard.getChargingType())) {
                number = day;
            }
            sard.setCost(number * sard.getUnitPrice());
        }
    }
    
    public void onSelectR() throws IOException {
        
        String pageName = "Edit_2";
        
        String url = "actionName=Edit&selectedid=" + selected.getId();
        url = pageName+".xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public Date setBeginTime(Date date){
        Calendar cal = Calendar.getInstance();
//        Date date = new Date();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>0 && minutes <=30){
            cal.set(Calendar.MINUTE,30);
        }else if (minutes > 30){
            cal.set(Calendar.MINUTE,0);
            cal.add(Calendar.HOUR,1);
        }
        return cal.getTime();
    }

    public Date setEndTime(Date date){
        Calendar cal = Calendar.getInstance();
//        Date date = new Date();
        cal.setTime(date);
        cal.set(Calendar.SECOND,0);
        int minutes = cal.get(Calendar.MINUTE);
        if (minutes>=0 && minutes <30){
            cal.set(Calendar.MINUTE,0);
        }else {
            cal.set(Calendar.MINUTE,30);
        }
        return cal.getTime();
    }

     public Date getTime(String type) throws ParseException{
            if("借用".equals(type)){
                return setBeginTime(new Date());
            }
            else{
                return setEndTime(new Date());
            }
            
     }
    
    public void getAssetInfo(Date times,String accepter) throws ParseException {//查询并插入借用设备信息
        Optional.ofNullable(dao.findById(selected.getId()).getState()).ifPresent(val -> {
            if (val != 2) {
                WebUtil.addErrorMessage("申请已取消或已完成交付，不能录入设备，请返回并刷新！");
                return;
            }
        });
        List<AssetInfo> assetInfoList = selected.getInfoList();
        //根据二维码or设备编号or资产编号查询设备信息
        List<AssetInfo> infoList = asiDao.findSwapTypeInfo(selected.getApplyAssetInfo(), selected.getApplyAssetInfo(), selected.getApplyAssetInfo(), this.selected.getSiteUID());
        if (!CollectionUtils.isEmpty(infoList)) {
            //根据设备UID剔除已录入设备
            for (AssetInfo selectedAssetInfo : assetInfoList) {
                infoList = infoList.stream().filter(assetInfo -> !assetInfo.getUid().equals(selectedAssetInfo.getUid())).collect(Collectors.toList());
            }
            if (infoList.size() < 1) {
                WebUtil.addWarnMessage("该设备已借出！");
                return;
            }
            infoList.forEach(infoVo -> {
                List<String> depts = dao.findDeptForSharedAsset(this.selected.getSiteUID());
                String orgUid = UserContextService.getCurrentUserAccount().getOrgInfoUID();
                OrgInfo userOrg = orgInfoDao.getByUid(orgUid);
                //if current user has role like AssetHead(2), AssetStaff(3), SharedAssetMgmt(10), this user will be treated as allocation center admin
                if (null == infoVo.getSharedAssetTypeUid() || infoVo.getSharedAssetTypeUid().equalsIgnoreCase("0")) {
                    WebUtil.addWarnMessage("该设备不属于调剂设备类型！");
                    return;
                }
                if (infoVo.getStatus() != 1 && infoVo.getStatus() != 3) {
                    WebUtil.addWarnMessage("该设备故障，不可借用！");
                    return;
                }
                //在asset_info中判断是否属于 调剂中心科室
                if (!depts.contains(infoVo.getAssetDeptUID())) {
                    WebUtil.addWarnMessage("该设备已借出！");
                    return;
                }
                if (!isAllocationCenterAdmin()) {
                    if (!depts.contains(userOrg.getUid())) {
                        WebUtil.addErrorMessage("当前角色非调剂中心人员，请核实后操作！");
                        return;
                    }
                }
                List<SharedAssetReqType> sharedAssetTypes = Optional.of(selected.getSartList()).orElse(new ArrayList<>());
                for (SharedAssetReqType applySwapType : sharedAssetTypes) {
                    if (this.isLogPatientInfoEnabled()) {
                        if (StringUtils.isNotBlank(this.selectedSartId) && !this.selectedSartId.equals(applySwapType.getId())) {
                            continue;
                        }
                    }
                    if (applySwapType.getSharedAssetTypeId().equals(infoVo.getSharedAssetTypeUid())) {
                        selectedSart = applySwapType;//明细表联动选中
                        if (applySwapType.getActualQty() < applySwapType.getExceptQty()) {
                            SharedAssetReqDetail assetDetailVo = new SharedAssetReqDetail();
                            assetDetailVo.setBorrowId(selected.getId());
                            assetDetailVo.setAssetInfoId(infoVo.getUid());
                            assetDetailVo.setAssetName(infoVo.getName());
                            assetDetailVo.setAssetNumber(infoVo.getDepartNum());
                            assetDetailVo.setQrCode(infoVo.getQrCode());
                            assetDetailVo.setFinancingNum(infoVo.getFinancingNum());
                            assetDetailVo.setSharedId(selectedSart.getSharedId());
                            assetDetailVo.setClinicalDeptUid(selected.getClinicalDeptUid());
                            assetDetailVo.setClinicalDeptName(selected.getClinicalDeptName());
                            //权限字段
                            assetDetailVo.setHospitalUID(selected.getHospitalUID());
                            assetDetailVo.setInstitutionUID(selected.getInstitutionUID());
                            assetDetailVo.setSiteUID(selected.getSiteUID());
                            assetDetailVo.setTenantUID(selected.getTenantUID());
                            //受理人
                            assetDetailVo.setBorrowDealUid(UserContextService.getCurrentUserAccount().getUid());
                            assetDetailVo.setFinancingNum(infoVo.getFinancingNum());//资产编号
                            assetDetailVo.setSharedAssetTypeId(applySwapType.getSharedAssetTypeId());

                            SharedAssetType swapTypeVo = swapDao.findById(assetDetailVo.getSharedAssetTypeId());
                            assetDetailVo.setAccepter(accepter.isEmpty() ? selected.getApplicant() : accepter);
                            //时间往后推
                            assetDetailVo.setChargingBeginTime("2".equals(swapTypeVo.getChargingType()) ? this.setBeginTime(times == null ? new Date() : times)
                                    : times == null ? this.setBeginTime(new Date()) : times);
                            //附件状态默认全选
                            swapTypeVo.getAccessoryList().forEach(item -> item.setBorrowStatus(true));
                            assetDetailVo.setAccessoryList(swapTypeVo.getAccessoryList());
                            assetDetailVo.setChargingType(swapTypeVo.getChargingType());//计价方式
                            assetDetailVo.setUnitPrice(swapTypeVo.getUnitPrice());//单价
                            assetDetailVo.setCreateDate(new Date());
                            assetDetailVo.setCreateBy(UserContextService.getCurrentUserAccount().getUid());
                            logger.info("Asset Uid: " + infoVo.getUid() + ", start duration: " + swapTypeVo.getStartingDuration() + ", start price: " + swapTypeVo.getStartingPrice());
                            if (swapTypeVo.getStartingDuration() != null && swapTypeVo.getStartingPrice() != null) {
                                assetDetailVo.setStartingDuration(swapTypeVo.getStartingDuration());
                                assetDetailVo.setStartingPrice(swapTypeVo.getStartingPrice());
                            }
                            assetDetailVo.setId(UUID.randomUUID().toString().replace("-", ""));
                            logger.info("isLogPatientInfoEnabled: " + isLogPatientInfoEnabled());
                            if (isLogPatientInfoEnabled() && StringUtils.isNotBlank(applySwapType.getPatientNum())) {
                                assetDetailVo.setPatientNum(applySwapType.getPatientNum());
                                assetDetailVo.setPatientBedNum(applySwapType.getPatientBedNum());
                            }
                            if (selected.getSardList() != null) {
                                selectedSard = assetDetailVo;
                                selected.getSardList().add(assetDetailVo);
                            }
                            //更改使用科室
                            OrgInfo findByUid = orgInfoDao.findByUid(this.selected.getClinicalDeptUid());
                            infoVo.setAssetDeptId(findByUid.getId());
                            infoVo.setAssetDeptUID(this.selected.getClinicalDeptUid());
                            assetInfoList.add(infoVo);
                            applySwapType.setActualQty(applySwapType.getActualQty() + 1);
                            WebUtil.addSuccessMessage("录入成功！");
                        } else {
                            if (this.isLogPatientInfoEnabled()) {
                                WebUtil.addWarnMessage("该设备类型 ‘" + applySwapType.getSwapName() + "-" + applySwapType.getPatientNum() + "-" + applySwapType.getPatientBedNum() + "’ 预计借用数量已满！");
                            } else {
                                WebUtil.addWarnMessage("该设备类型 ‘" + applySwapType.getSwapName() + "’ 预计借用数量已满！");
                            }
                        }
                        return;
                    }
                }
                WebUtil.addWarnMessage("该设备不属于借用申请的设备类型！");
                return;
            });
        } else {
            WebUtil.addWarnMessage("录入信息不符，请核对后录入！");
        }
        return;
    }

    private Boolean isAllocationCenterAdmin() {
        List<UserRole> userRoleList = userRoleRepository.findByUserId(UserContextService.getCurrentUserAccount().getId());
        return userRoleList.stream().anyMatch(userRole ->
                "AssetHead,AssetStaff,SharedAssetMgmt".contains(sysRoleDao.findById(userRole.getRoleId()) == null ? "" : sysRoleDao.findById(userRole.getRoleId()).getName()));
    }

    /**
     录入设备的删除
     */
     public void deleteBy(String uid){
        List<SharedAssetReqDetail> sardList = selected.getSardList();
        List<AssetInfo> assetInfoList = selected.getInfoList();
        List<SharedAssetReqType> sartList = selected.getSartList();
         for (SharedAssetReqDetail sharedAssetReqDetail : sardList) {
             if (sharedAssetReqDetail.getId().equals(uid)) {
                 //清理infoList
                 String infoId = sharedAssetReqDetail.getAssetInfoId();
                 for (AssetInfo assetInfo : assetInfoList) {
                     if (assetInfo.getUid().equals(infoId)) {
                         //处理sartList
                         for (SharedAssetReqType sharedAssetReqType : sartList) {
                             if (sharedAssetReqType.getSharedAssetTypeId().equals(assetInfo.getSharedAssetTypeUid())) {
                                 sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                                 selectedSart = sharedAssetReqType;//明细表联动选中
                                 break;
                             }
                         }
                         assetInfoList.remove(assetInfo);
                         break;
                     }
                 }
                 //清理sardList
                 sardList.remove(sharedAssetReqDetail);
                 if(!CollectionUtils.isEmpty(this.sharedAssetRequests)){
                     this.sharedAssetRequests=this.sharedAssetRequests.stream().filter(item -> !item.getSardList().stream().allMatch(detail->detail.getId().equals(sharedAssetReqDetail.getId()))).collect(Collectors.toList());
                 }
                 break;
             }
         }
    }
    //录入设备修改前
    private List<String> selectedBorrowAccessoryList = null;
    private List<String> selectedReturnAccessoryList = null;

    public List<String> getSelectedBorrowAccessoryList() {
        return selectedBorrowAccessoryList;
    }

    public void setSelectedBorrowAccessoryList(List<String> selectedBorrowAccessoryList) {
        this.selectedBorrowAccessoryList = selectedBorrowAccessoryList;
    }

    public List<String> getSelectedReturnAccessoryList() {
        return selectedReturnAccessoryList;
    }

    public void setSelectedReturnAccessoryList(List<String> selectedReturnAccessoryList) {
        this.selectedReturnAccessoryList = selectedReturnAccessoryList;
    }
    
    public void prepareEditSard(){
        selectedBorrowAccessoryList = new ArrayList<String>();
        for(SharedAssetTypeAccesssory item: this.selectedSard.getAccessoryList()){
            if(item.getBorrowStatus() !=null&&item.getBorrowStatus()){
                selectedBorrowAccessoryList.add(item.getName());
            }
        }
        selectedReturnAccessoryList = new ArrayList<String>();
        for(SharedAssetTypeAccesssory item: this.selectedSard.getAccessoryList()){
            if(selectedReturnAccessoryList!=null){
                if(item.getReturnStatus() !=null&&item.getReturnStatus()){
                    selectedReturnAccessoryList.add(item.getName());
                }
            }
        }

    }
    //录入设备修改
    public void saveSard(String type){
        List<SharedAssetReqDetail> sardList = selected.getSardList();
        List<SharedAssetTypeAccesssory> accessoryList = selectedSard.getAccessoryList();
         
        for (int j = 0; j < accessoryList.size(); j++) {
            SharedAssetTypeAccesssory sharedAssetTypeAccesssory = accessoryList.get(j);
            boolean flag = true;
            if (selectedBorrowAccessoryList != null && selectedBorrowAccessoryList.size() > 0) {
                 for (int i = 0; i < selectedBorrowAccessoryList.size(); i++) {
                    String accessoryName = selectedBorrowAccessoryList.get(i);
                    if (sharedAssetTypeAccesssory.getName().equals(accessoryName)) {
                        flag = false;
                    }
                }
                 if (flag) {
                     sharedAssetTypeAccesssory.setBorrowStatus(false);
                 }
            }else{
                sharedAssetTypeAccesssory.setBorrowStatus(false);
            }
        }
        
        for (int j = 0; j < accessoryList.size(); j++) {
            SharedAssetTypeAccesssory sharedAssetTypeAccesssory = accessoryList.get(j);
            boolean flag = true;
            if (selectedReturnAccessoryList != null && selectedReturnAccessoryList.size() > 0) {
                 for (int i = 0; i < selectedReturnAccessoryList.size(); i++) {
                    String accessoryName = selectedReturnAccessoryList.get(i);
                    if (sharedAssetTypeAccesssory.getName().equals(accessoryName)) {
                        flag = false;
                    }
                }
                 if (flag) {
                     sharedAssetTypeAccesssory.setReturnStatus(false);
                 }
            }else{
                sharedAssetTypeAccesssory.setReturnStatus(false);
            }
 
        }
     
        selectedSard.setAccessoryList(accessoryList);
        this.setCost(selectedSard);
    }

    
    public void searchList(Integer buttonIndex) {
        queryIndex = buttonIndex;
        this.searchFilters = null;
        cancel();
    }

    public void cancel() {
        this.selected = null;
    }
    
    public void submmit0() {
        List<SharedAssetReqType> sartList = selected.getSartList();
        if (sartList==null || sartList.size() == 0) {
                String url = "norespond" ;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("url", url);
                
               WebUtil.addWarnMessage("请先添加申请明细，再进行提交！");
               return;
       } else {
            if(!isReturn && Boolean.TRUE.equals(this.currentWorkflowConfig.getEnableStockShow())){
                for (SharedAssetReqType sharedAssetReqType : sartList) {
                    Integer stock = this.selectStock(sharedAssetReqType.getSharedAssetTypeId());
                    int applyQty = sharedAssetReqType.getApplyQty();
                    if(applyQty > stock){
                        String message = "wrong" ;
                        RequestContext requestContext = RequestContext.getCurrentInstance();
                        requestContext.addCallbackParam("message", message);
                        WebUtil.addWarnMessage("调剂设备："+sharedAssetReqType.getSwapName()+",库存不足，当前库存数量："+stock);
                        return;
                    }
                }
            }
            String name = orgInfoDao.getByUid(selected.getClinicalDeptUid()).getName();
            selected.setClinicalDeptName(name);
            selected.setApplyTime(new Date());
            selected.setCreateDate(new Date());

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            selected.setInvalidTime(cal.getTime());
            setDataType();
            selected.setRequestNum(generateOrderNumber());
            dao.save(selected);
            apdDao.save(selected.getSartList());
            saveAttachments(selected.getAttachments());
            submmitApproval();
            String url = "List.xhtml?faces-redirect=true";
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("url", url);
            return;
       }
       
    }

    private void submmitApproval() {
        if(selected.getBehavior().equals(1)){
            MultiLevelApproval multiLevelApproval = multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmit(selected, null, false);
            if (multiLevelApproval == null) {
                try {
                    sharedAssetService.pushWxMsg(selected.getId());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                selected.setApprovalStatus(0);
                dao.save(selected);
                WebUtil.addSuccessMessage("审批流发起成功,请尽快审批!");
            }
        }else{
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void submmitR0() throws Exception {
       this.save();
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       
       if(sta == 7){
           state = 8;
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else{
           WebUtil.addErrorMessage("当前状态不是可提交状态！");
           return;
       }
       
    }
    
    public void refers() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 4;//4为取消借用
       if(sta == 0||sta == 1||sta ==2|| sta == 5){
           dao.updateState(uid, state);

           String iouId = uid;
                
            List<SharedAssetReqDetail> ListAll = asdDao.selectBySharedId(iouId);
            int x = 0;
            if(ListAll.size()>0){

                for(SharedAssetReqDetail dtl : ListAll){
                    
                    String assetInfoId = dtl.getAssetInfoId();
                    AssetInfo infoVo = new AssetInfo();
                    infoVo = asiDao.findByUid(assetInfoId);
                    infoVo.setSwapTypeState("0");
                    asiDao.save(infoVo);//将AssetInfo的借用状态还原
                }
            }
             asdDao.delete(ListAll);//删除借用设备信息
            saveAttachments(selected.getAttachments());

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消借用！");
           return;
       }
       
    }
    public void submmitR() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 12;//12归还待确认—确认—>6借用完成
       
       if(sta == 9){//借用待确认状态
           dao.updateState(uid, state);
           clearOwner(uid);
           saveAttachments(selected.getAttachments());
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认归还！");
           return;
       }
       
    }
    private void clearOwner(String uid){
        String modifyingTheDeviceOwner ="enableModifyingTheDeviceOwner";
        String configItemValue = userContextService.getConfigItemValue(modifyingTheDeviceOwner);
        if(Boolean.TRUE.toString().equals(configItemValue)){
            List<SharedAssetReqDetail> details = asdDao.findByReturnId(uid);
            if(CollectionUtils.isEmpty(details)) return;
            Set<String> assetUid = details.stream().map(SharedAssetReqDetail::getAssetInfoId).collect(Collectors.toSet());
            List<AssetInfo> assetInfos = asiDao.fetchListByUid(assetUid);
            if(CollectionUtils.isEmpty(assetInfos)) return;
            for (AssetInfo assetInfo : assetInfos) {
                assetInfo.setClinicalOwnerId(null);
                assetInfo.setClinicalOwnerName(null);
                assetInfo.setClinicalOwnerTel(null);
            }
            asiDao.save(assetInfos);
        }
    }
    public void Accept() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 2;//2借用待交付状态

       List<SharedAssetReqType> sartList = selected.getSartList();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 1) {
            String useUid = UserContextService.getCurrentUserAccount().getUid();
        	dao.updateStateAndOwner(uid, state, UserContextService.getCurrentUserAccount().getLoginName(),useUid,new Date());
        } 
        if(sta == 5){
        	dao.updateState(uid, state);
        }
        if(sta ==1|| sta == 5){//借用待受理状态/借用搁置状态
            apdDao.save(sartList);

            //调用微服务
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                sharedAssetService.remoteAcceptRent(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            saveAttachments(selected.getAttachments());
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
            return;
        }else {
            WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
            requestContext.addCallbackParam("url","norespond");
            return;
        }
    }
    public void rejectApply(){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Map<String, Object> params = new HashMap<>();
        String id = selected.getId();
        params.put("id", id);
        params.put("rejectShelveRemark",selected.getRejectShelveRemark());
        ResponseEntity<LinkedHashMap> result = null;
        try {
            result = sharedAssetService.rejectApply(GatewayURL.REJECT_APPLY, params);
            saveAttachments(selected.getAttachments());
            logger.info("拒绝借用申请：id="+id);
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
            requestContext.addCallbackParam("url","norespond");
        }

    }
    public void rejectReturn(){
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Map<String, Object> params = new HashMap<>();
        String id = selected.getId();
        params.put("id", id);
        params.put("rejectShelveRemark",selected.getRejectShelveRemark());
        ResponseEntity<LinkedHashMap> result = null;
        try {
            result = sharedAssetService.rejectApply(GatewayURL.REJECT_RETURN, params);
            saveAttachments(selected.getAttachments());
            logger.info("拒绝归还申请：id="+id);
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
            requestContext.addCallbackParam("url","norespond");
        }

    }
    public void AcceptR() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 8;//2归还待交付状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 7) {
            String useUid = UserContextService.getCurrentUserAccount().getUid();
            dao.updateStateAndOwner(uid, state, UserContextService.getCurrentUserAccount().getLoginName(),useUid,new Date());
            saveAttachments(selected.getAttachments());
        }
        if(sta == 11) {
        	dao.updateState(uid, state);
        }
       if(sta  == 7 || sta == 11){//归还待受理状态
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
           String url = "List.xhtml?faces-redirect=true";
           requestContext.addCallbackParam("url", url);
           return;
       }else {
           WebUtil.addErrorMessage("申请已取消或已受理，请返回并刷新！");
           requestContext.addCallbackParam("url","norespond");
           return;
       }
    }
    
    public void Shelve() throws Exception {
       String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 5;//5为借用搁置状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
       if(sta == 1){//借用待受理状态
           //dao.updateState(uid, state);
           realSAR.setState(state);
           realSAR.setRejectShelveRemark(selected.getRejectShelveRemark());
            dao.save(realSAR);
            saveAttachments(selected.getAttachments());
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
           String url = "List.xhtml?faces-redirect=true";
           requestContext.addCallbackParam("url", url);
           return;
       }else {
           WebUtil.addErrorMessage("申请已取消或已搁置受理，请返回并刷新！");
           requestContext.addCallbackParam("url","norespond");
           return;
       }
    }

    public void ShelveR() throws Exception {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
        int state = 11;//归还搁置状态
        RequestContext requestContext = RequestContext.getCurrentInstance();
        if(sta == 7){//待受理状态
            dao.updateState(uid, state);

            //调用微服务
            try {
                sharedAssetService.pushWxMsg(selected.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
            String url = "List.xhtml?faces-redirect=true";
            requestContext.addCallbackParam("url", url);
            return;
        }else {
            WebUtil.addErrorMessage("当前状态不可搁置！");
            requestContext.addCallbackParam("url","norespond");
            return;
        }
    }

    public void deliver() throws Exception {

        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
        int state = 3;//完成交付进入借用待确认状态
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(sta == 2){
           //处理多用户申请
           String msg = "";
           List<AssetInfo> infoList = selected.getInfoList();
           List<SharedAssetReqDetail> sardList = selected.getSardList();
           List<SharedAssetReqType> sartList = selected.getSartList();
           if (sardList == null || sardList.size() == 0) {
               String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
               WebUtil.addWarnMessage("请录入设备！");
               return;
           }
            String accepter = selected.getAccepter();
            sardList.forEach(item->{
                item.setAccepter(accepter);
            });
           // 只能同院区调剂不存在多个WorkflowConfig
            WorkflowConfig configBySite = configService.findConfigBySite(selected.getSiteUID());
            String deptForSharedAsset = "";
            if(configBySite != null){
                 deptForSharedAsset = configBySite.getDeptForSharedAsset();
            }
            for (int i = 0; i < infoList.size(); i++) {
               AssetInfo info = infoList.get(i);
               AssetInfo realInfo = asiDao.findByUid(info.getUid());
                String clinicalDeptUID = realInfo.getClinicalDeptUID();
               if (StringUtils.isNotBlank(deptForSharedAsset) && !deptForSharedAsset.equals(clinicalDeptUID)) {
                   msg = msg + "设备 ‘" + info.getDepartNum() +"’ 刚已被科室 ‘" + realInfo.getClinicalDeptName() + "’ 借走，设备已移除。 ";
                   for (int j = 0; j < sardList.size(); j++) {
                       SharedAssetReqDetail sard = sardList.get(j);
                       if (sard.getAssetNumber().equals(info.getDepartNum())) {
                           sardList.remove(j);
                           break;
                       }
                   }
                    //处理sartList
                    for (SharedAssetReqType sharedAssetReqType : sartList) {
                        if (sharedAssetReqType.getSharedAssetTypeId().equals(info.getSharedAssetTypeUid())) {
                            sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                            selectedSart = sharedAssetReqType;//明细表联动选中
                            break;
                        }
                    }
                   infoList.remove(i);
                   i--;
               }
           }
           if (!msg.equals("")) {
               requestContext.addCallbackParam("url", "norespond");
               WebUtil.addWarnMessage(msg);
               return;
           } else {
               selected.setState(state);
               selected.setModifyDate(new Date());
               sharedAssetRequestService.save(selected);
               saveAttachments(selected.getAttachments());
               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               } catch (Exception e) {
                   logger.warn("SharedAssetRequestService[]pushWxMsg[]error", e);
               }
               String url = "List.xhtml?faces-redirect=true";
               requestContext.addCallbackParam("url", url);
               return;
           }
          
       }else {
           WebUtil.addErrorMessage("当前不是可交付状态，请返回！");
           requestContext.addCallbackParam("url", "norespond");
           return;
       }
    }

    public void deliverR() throws Exception {
        String uid = selected.getId();
        SharedAssetRequest realSAR = dao.findById(uid);
        int sta = realSAR.getState();
       int state = 9;//完成交付
        RequestContext requestContext = RequestContext.getCurrentInstance();

        if(sta == 8){//借用待交付
          //处理多用户申请
           String msg = "";
           List<AssetInfo> infoList = selected.getInfoList();
           List<SharedAssetReqDetail> sardList = selected.getSardList();
           List<SharedAssetReqType> sartList = selected.getSartList();
           if (sardList == null || sardList.size() == 0) {
               String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
               WebUtil.addWarnMessage("请录入设备！");
               return;
           }
            String msgAccepter = selected.getMsgAccepter();
            sardList.forEach(item->{
                item.setMsgAccepter(msgAccepter);
            });
            // 只能同院区调剂不存在多个WorkflowConfig
            WorkflowConfig configBySite = configService.findConfigBySite(selected.getSiteUID());
            String deptForSharedAsset = "";
            if(configBySite != null){
                deptForSharedAsset = configBySite.getDeptForSharedAsset();
            }
           for (int i = 0; i < infoList.size(); i++) {
               AssetInfo info = infoList.get(i);
               AssetInfo realInfo = asiDao.findByUid(info.getUid());
               String assetDeptUID = realInfo.getAssetDeptUID();
               if (StringUtils.isNotBlank(deptForSharedAsset) && deptForSharedAsset.equals(assetDeptUID)) {
                   msg = msg + "设备 ‘" + info.getDepartNum() +"’ 刚刚已被归还，请重新录入！ ";
                   for (int j = 0; j < sardList.size(); j++) {
                       SharedAssetReqDetail sard = sardList.get(j);
                       if (sard.getAssetNumber().equals(info.getDepartNum())) {
                           sardList.remove(j);
                           break;
                       }
                   }
                   //处理sartList
                    for (SharedAssetReqType sharedAssetReqType : sartList) {
                        if (sharedAssetReqType.getSharedAssetTypeId().equals(info.getSharedAssetTypeUid())) {
                            sharedAssetReqType.setActualQty(sharedAssetReqType.getActualQty()-1);
                            selectedSart = sharedAssetReqType;//明细表联动选中
                            break;
                        }
                    }
                   infoList.remove(i);
                   i--;
               }
           }
           if (!msg.equals("")) {
                String url = "norespond" ;
                requestContext.addCallbackParam("url", url);
                
               WebUtil.addWarnMessage(msg);
               return;
           } else {
                selected.setState(state);
                dao.save(selected);
                asiDao.save(selected.getInfoList());
//               sharedAssetRequestService.sendMq(selected.getInfoList());

               apdDao.save(selected.getSartList());

                asdDao.save(selected.getSardList());
                saveAttachments(selected.getAttachments());
               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               }catch (Exception e){
                   e.printStackTrace();
               }
               String url = "List.xhtml?faces-redirect=true";
                requestContext.addCallbackParam("url", url);
                return;
           }
       }else {
           WebUtil.addErrorMessage("当前不是可交付状态，请返回！");
            requestContext.addCallbackParam("url", "norespond");
           return;
       }
    }


    public int getSwapQty2(String Uid,String clinicalDeptUID){
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return 0;
        }else{
            List<AssetInfo> objectList = new ArrayList<AssetInfo>();
            objectList = swapDao.findbyBorrowQty(clinicalDeptUID,Uid);
          
            return objectList.size();
        }
    }
    public int getSwapQty(){
        String Uid = this.selectedSart.getSharedAssetTypeId();
        if(null == Uid||Uid.equalsIgnoreCase("")){
            return 0;
        }else{
            List<AssetInfo> objectList = new ArrayList<AssetInfo>();
            UserAccount user = UserContextService.getCurrentUserAccount();
            objectList = swapDao.findbyBorrowQty(user.getOrgInfoUID(),Uid);

            return objectList.size();
        }
    }
    
    //
    private String filterState = null;

    public String getFilterState() {
        return filterState;
    }

    public void setFilterState(String filterState) {
        this.filterState = filterState;
    }

    public void setStatusFilter() {
        if (filterState == null && filterApplyTime == null) {
            return;
        }
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        if (filterState != null) {
            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.EQ, filterState));
//            searchFilters.add(new SearchFilter("state", SearchFilter.Operator.IN,stateList));
        }
        /*if (assetId != null) {
            searchFilters.add(new SearchFilter("id", SearchFilter.Operator.EQ, assetId));
        }*/
        if (filterApplyTime != null && "1".equals(filterApplyTime)) {
            Date applyTime = new Date();
            //2个月内，则是保修到期时间在2个月后时间之前所有
            Calendar c = Calendar.getInstance();
            c.setTime(applyTime);
            c.add(Calendar.MONTH, 2);
            applyTimeTo = c.getTime();
            searchFilters.add(new SearchFilter("applyTime", SearchFilter.Operator.LTE, applyTimeTo));
//            searchFilters.add(new SearchFilter("isWarranty", SearchFilter.Operator.EQ, true));
//            searchFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));
            searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getSiteId()));
            searchFilters.add(new SearchFilter("hospitalId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getHospitalId()));
            //filterApplyTime = null;
        }
    }
    
    public void saveSart(){

        int applyQty = selectedSart.getApplyQty();
        if (applyQty == 0){
            String message = "wrong" ;
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("message", message);

            WebUtil.addWarnMessage("申请数必须大于0！");
            return;
        }
        if("-1".equals(selectedSart.getSharedAssetTypeId())){
            String message = "wrong" ;
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("message", message);
            WebUtil.addWarnMessage("调剂设备名称不能为空！");
            return;
        }
        Date expectDate = selectedSart.getExpectDate();
        if(!isReturn && expectDate != null){
             LocalDate localDate = expectDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
             long between = ChronoUnit.DAYS.between(LocalDate.now(), localDate);
             if(between < 0){
                 String message = "wrong" ;
                 RequestContext requestContext = RequestContext.getCurrentInstance();
                 requestContext.addCallbackParam("message", message);
                 WebUtil.addWarnMessage("预计归还时间不能小于当前时间！");
                 return;
             }
        }
        String swapName = swapDao.findById(selectedSart.getSharedAssetTypeId()).getSwapName();
        if(!isReturn && Boolean.TRUE.equals(this.currentWorkflowConfig.getEnableStockShow())) {
            Integer stock = this.selectStock(selectedSart.getSharedAssetTypeId());
            if(applyQty > stock){
                String message = "wrong" ;
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("message", message);
                WebUtil.addWarnMessage("调剂设备："+swapName+",库存不足，当前库存数量："+stock);
                return;
            }
        }

        List<SharedAssetReqType> sartList = selected.getSartList();
        if(this.isLogPatientInfoEnabled()) {
            if("Create".equals(super.crudActionName)) {
                selectedSart.setId(UUID.randomUUID().toString().replace("-", ""));
                selectedSart.setSwapName(swapName);
                selectedSart.setSharedId(selected.getId());
                sartList.add(selectedSart);
            } else {
                Map<String, SharedAssetReqType> sharedAssetReqTypeMap = new HashMap();

                Optional<SharedAssetReqType> optionalSharedAssetReqType = selected.getSartList()
                    .stream().filter(sart -> sart.getId().equals(selectedSart.getId()))
                    .findFirst();
                if(optionalSharedAssetReqType.isPresent()) {
                    try {
                        SharedAssetReqType sharedAssetReqType = optionalSharedAssetReqType.get();
                        BeanUtils.copyProperties(sharedAssetReqType, selectedSart);
                        logger.info("selected shared_asset_req_type.shared_id: " + selectedSart.getSharedId());
                        //update shared asset req detail if necessary
                        this.selected.getSardList()
                            .stream()
                            .filter(sard -> sharedAssetReqType.getSharedId().equals(sard.getSharedId()))
                            .forEach(sard -> {
                                sard.setPatientNum(sharedAssetReqType.getPatientNum());
                                sard.setPatientBedNum(sharedAssetReqType.getPatientBedNum());
                            });
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            selectedSart.setId(UUID.randomUUID().toString().replace("-", ""));
            selectedSart.setSwapName(swapName);
            selectedSart.setSharedId(selected.getId());
            Iterator<SharedAssetReqType> iterator = sartList.iterator();
            while(iterator.hasNext()) {
                SharedAssetReqType sharedAssetReqType = iterator.next();
                if(sharedAssetReqType.getSwapName().equals(selectedSart.getSwapName())) {
                    iterator.remove();
                }
            }
            sartList.add(selectedSart);
        }
    }
    
    
    
    /**
     * 以下是老版本的一些方法暂做保留
     */
    
    public void refersR() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
              int state = 10;//11为取消归还
       if(sta == 7 ||sta == 8 || sta == 11){

           dao.updateState(uid, state);
            String iouId = uid;
            List<SharedAssetReqDetail> ListAll = asdDao.selectByIouId(iouId);
            int x = 0;
            if(ListAll.size()>0){

                for(SharedAssetReqDetail dtl : ListAll){
                    
                    String assetInfoId = dtl.getAssetInfoId();
                    AssetInfo infoVo = new AssetInfo();
                    infoVo = asiDao.findByUid(assetInfoId);
                    infoVo.setSwapTypeState("1");
                    asiDao.save(infoVo);//将AssetInfo的借用状态还原

                }
            }
            asdDao.delete(ListAll);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消归还！");
           return;
       }
       
    }
    public void onRowEditSart(RowEditEvent event){
        SharedAssetReqType sart = (SharedAssetReqType) event.getObject();
        List<SharedAssetReqType> sartList = selected.getSartList();
        for (SharedAssetReqType sharedAssetReqType : sartList) {
            if (sharedAssetReqType.getId() == sart.getId()){
                sharedAssetReqType.setExceptQty(sart.getExceptQty());
            }
        }

    }
    
    public void submmit() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;//3借用待确认—确认—>6借用完成
       
       if(sta == 3){//借用待确认状态
           dao.updateState(uid, state);
           modifyingTheDeviceOwner(uid);
           saveAttachments(selected.getAttachments());
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认借用！");
           return;
       }
       
    }

    private void modifyingTheDeviceOwner(String uid) {
        String modifyingTheDeviceOwner ="enableModifyingTheDeviceOwner";
        String configItemValue = userContextService.getConfigItemValue(modifyingTheDeviceOwner);
        if(Boolean.TRUE.toString().equals(configItemValue)){
            List<SharedAssetReqDetail> details = asdDao.selectBySharedId(uid);
            if(CollectionUtils.isEmpty(details)) return;
            Set<String> assetUid = details.stream().map(SharedAssetReqDetail::getAssetInfoId).collect(Collectors.toSet());
            List<AssetInfo> assetInfos = asiDao.fetchListByUid(assetUid);
            if(CollectionUtils.isEmpty(assetInfos)) return;
            String applicant = selected.getApplicant();
            List<SearchFilter> searchFilters = new ArrayList<>();
            searchFilters.add(new SearchFilter("uid", SearchFilter.Operator.EQ,applicant,"1*2+"));
            searchFilters.add(new SearchFilter("loginName", SearchFilter.Operator.EQ,applicant,"1*2+"));
            List<UserAccount> userAccounts = userAccountRepository.findBySearchFilter(searchFilters);
            UserAccount owner=null;
            if(CollectionUtils.isEmpty(userAccounts)){
                owner = UserContextService.getCurrentUserAccount();
            }else{
                Optional<UserAccount> first = userAccounts.stream().findFirst();
                if (first.isPresent()) {
                    owner = first.get();
                }
            }
            if(owner == null) return;
            for (AssetInfo assetInfo : assetInfos) {
                assetInfo.setClinicalOwnerId(owner.getId());
                assetInfo.setClinicalOwnerName(owner.getName());
                assetInfo.setClinicalOwnerTel(owner.getTelephone());
            }
            asiDao.save(assetInfos);
        }
    }

    public void submmit1() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 2;//
     
       if(sta == 1){//借用待确认状态
           dao.updateState(uid, state);

           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可受理！");
           return;
}

    }
        
    public void submmit2() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       
       if(1==0){//判断是否部分归还(还未做，暂用1==0代替判断条件)
           state = 5;//5为部分归还状态
       }
       
       if(sta == 7){
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可确认归还！");
           return;
       }
       
    }
        
    public void submmit5() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;//
     
       if(sta == 5){//借用待确认状态
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可受理！");
           return;
        
       }

    }
    
    
    public void refers2() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 0;
       if(1==0){//判断是否有部分已经归还(还未做，暂用1==0代替判断条件)
           state = 4;//4为待归还状态
       }else{
           state = 5;//5为部分归还状态

       }
           if(sta == 6||sta == 7){
           dao.updateState(uid, state);
               //调用微服务
               try {
                   sharedAssetService.pushWxMsg(selected.getId());
               }catch (Exception e){
                   e.printStackTrace();
               }
       }else {
           WebUtil.addErrorMessage("当前状态不可取消归还！");
           return;
       }
    }
    
    
    public void upLoad() throws Exception {
       String uid = selected.getId();
       int sta = selected.getState();
       int state = 6;
       if(sta == 4||sta == 5){
           dao.updateState(uid, state);
           //调用微服务
           try {
               sharedAssetService.pushWxMsg(selected.getId());
           }catch (Exception e){
               e.printStackTrace();
           }
       }else {
           WebUtil.addErrorMessage("当前状态不可申请归还！");
           return;
       }
    }

    public String getNameByLoginName(String loginName){
        List<String> nameList = dao.findNameByLoginName(loginName);
        if (nameList != null && nameList.size() > 0)
            return nameList.get(0);
        return loginName;
    }

    public String getNameByLoginName2(){
        return this.getNameByLoginName(selected.getApplicant());
    }

    /**
     * @return the total
     */
    public Double getTotal() {
        
        List<SharedAssetReqDetail> detailList  = asdDao.findByReturnId(this.selected.getId());
        Double sum;
        sum = 0.0;
        if(detailList.size()>0){
            for(SharedAssetReqDetail detail :detailList){
                sum = sum + detail.getCost();
            }
            return sum;
        }
        else{
            return 0.0;
        }
    }

    /**
     * @param total the total to set
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }
    private void setReturn(RequestContext requestContext){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String uri=((HttpServletRequest) ctx.getRequest()).getRequestURI();
        if(uri.contains("BorrowApplyV2")){
            isReturn=false;  // 创建借用申请
        }else{
            isReturn=true;  // 创建归还申请
        }
    }
    public String workOrderRouter() {
    	String retrunUrl = "";
    	RequestContext requestContext = RequestContext.getCurrentInstance();
        setReturn(requestContext);
        Boolean enableAllocateOperation = Boolean.TRUE;
      Boolean willShowNotification = Boolean.FALSE;
    	if(StringUtils.isNotBlank(workflowConfigJson)) {
          JSONObject scObject = JSON.parseObject(workflowConfigJson);
          String startTime = scObject.getString("periodStart");
          String endTime = scObject.getString("periodEnd");
          // process to check if operations in allocation center is enabled
          if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
              DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
              // catch parse exception
              try {
                  LocalTime startLocalTime = LocalTime.parse(startTime, dateTimeFormatter);
                  LocalTime endLocalTime = LocalTime.parse(endTime, dateTimeFormatter);
                  LocalTime currentTime = LocalTime.now();
                  if(currentTime.isBefore(startLocalTime) || currentTime.isAfter(endLocalTime)) {
                      // operations in allocation center may not be enabled
//                      enableAllocateOperation = scObject.getBoolean("openService");
//                      willShowNotification = scObject.getBoolean("enableHint");
                      enableAllocateOperation = Boolean.FALSE;
                      willShowNotification = Boolean.TRUE;
                  }
              } catch (Exception ex) {
                  logger.info("Configuration regarding to allocation center may be wrong: " + workflowConfigJson);
              }
          }
      }

    	requestContext.addCallbackParam("showNotification", willShowNotification);
    	if(enableAllocateOperation) {
          requestContext.addCallbackParam("url", UrlEncryptController.encodeUrlParam("Edit?actionName=Create"));
          retrunUrl = UrlEncryptController.encodeUrlParam("Edit?actionName=Create");
      }

    	return retrunUrl;
    }
    
    public String getOnCallNumber() {
    	List<SharedAssetConfig> sharedAssetConfigList = sharedAssetConfigDao.findBySiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
    	String onCallNumber = "";
    	if(sharedAssetConfigList != null && sharedAssetConfigList.size() > 0) {
    		onCallNumber = Optional.ofNullable(sharedAssetConfigList.get(0)).map(item -> item.getTel()).orElse("");
    	}
    	return onCallNumber;
    }
    
    public void prepareTransferRequest() {
    	selectedAssignee = new UserAccount();
    }

    public List getAvailableAssignees() {
    	List<LinkedHashMap> rst = null;
        try {
        	ResponseEntity<LinkedHashMap> response = sharedAssetService.getAssigneeList(selected.getId());
            LinkedHashMap respBody = response.getBody();
            if("OK".equals(respBody.get("bizStatusCode"))) {
            	List<LinkedHashMap> assignees = (List) respBody.get("data");
            	UserAccount user = UserContextService.getCurrentUserAccount();
            	rst = assignees.stream()
            						.filter(item -> !user.getUid().equals(item.get("uid")))
            						.collect(Collectors.toList());
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return rst;
    }
    
    public void transferRequest() {
    	String url = "List.xhtml?faces-redirect=true";
    	RequestContext requestContext = RequestContext.getCurrentInstance();
    	try {
    		sharedAssetService.remoteAssignRequest(selected.getId(), selectedAssignee.getUid());
    	} catch(Exception ex) {
    		url = "norespond";
    		WebUtil.addErrorMessage("转单失败，请返回重新申请！");
    		ex.printStackTrace();
    	}
    	requestContext.addCallbackParam("url", url);
    }

    public boolean validateAllExpectedQty() {
        List<SharedAssetReqType> sartList = selected.getSartList();
        Boolean expectedQtyLTZero = Boolean.FALSE;
        for (SharedAssetReqType sart : sartList) {
            if (sart.getExceptQty() > 0){
            } else {
                expectedQtyLTZero = Boolean.TRUE;
                break;
            }
        }
        logger.debug("validateTotalExpectedQty result: " + expectedQtyLTZero);
        return expectedQtyLTZero;
    }

    public String getOrgLocation() {
        StringBuilder orgLoc = new StringBuilder();
        if(this.selected != null) {
            String orgUid = this.selected.getClinicalDeptUid();
            if(StringUtils.isBlank(orgUid))return orgLoc.toString();
            OrgInfo orgInfo = orgInfoDao.findByUid(orgUid);
            if(StringUtils.isNotBlank(orgInfo.getBuildingNum())) {
                orgLoc.append(orgInfo.getBuildingNum());
                orgLoc.append("楼");
            }
            if(StringUtils.isNotBlank(orgInfo.getFloorNum())) {
                orgLoc.append(orgInfo.getFloorNum());
                orgLoc.append("层");
            }
            if(StringUtils.isNotBlank(orgInfo.getOther())) {
                orgLoc.append(orgInfo.getOther());
            }
        }
        return orgLoc.toString();
    }

    public Double getTotalAmount(SharedAssetRequest request) {
        double totalAmount = 0d;
        SharedAssetReqDetail reqDetail = null;
        List<SharedAssetReqDetail> reqDetailList = null;
        if("1".equals(request.getBehavior())) {//借用请求
            reqDetailList = asdDao.findByBorrowId(request.getId());
        } else {
            reqDetailList = asdDao.findByReturnId(request.getId());
        }
        reqDetail = reqDetailList.stream().findFirst().orElse(null);
        if(reqDetail != null) {
            AssetInfo assetInfo = asiDao.findByUid(reqDetail.getAssetInfoId());
            SharedAssetType sharedAssetType = swapDao.findById(assetInfo.getSharedAssetTypeUid());
            Tuple3<Long, String, BigDecimal> billingDetail = AllocationCenterUtils.getBillingDetail(sharedAssetType, reqDetail);
            totalAmount = billingDetail._3.doubleValue();
        }
        return totalAmount;
    }

    public Double getTotalAmount(SharedAssetReqDetail reqDetail) {
        double totalAmount = 0d;
        if(reqDetail != null) {
            AssetInfo assetInfo = asiDao.findByUid(reqDetail.getAssetInfoId());
            SharedAssetType sharedAssetType = swapDao.findById(assetInfo.getSharedAssetTypeUid());
            Tuple3<Long, String, BigDecimal> billingDetail = AllocationCenterUtils.getBillingDetail(sharedAssetType, reqDetail);
            totalAmount = billingDetail._3.doubleValue();
        }
        return totalAmount;
    }

    public Date getMaxChargingBeginTime(SharedAssetReqDetail reqDetail) {
        Date maxChargingBeginTime = new Date();
        if(ChargingType.PAY_HALF_HOUR.getIndex().equals(reqDetail)) {
            maxChargingBeginTime = this.setBeginTime(maxChargingBeginTime);
        }
        return maxChargingBeginTime;
    }

    public Date getMaxChargingEndTime(SharedAssetReqDetail reqDetail) {
        Date maxChargingEndTime = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));;
        if(ChargingType.PAY_HALF_HOUR.getIndex().equals(reqDetail)) {
            maxChargingEndTime = this.setEndTime(maxChargingEndTime);
        }
        return maxChargingEndTime;
    }

    public Boolean isLogPatientInfoEnabled() {
        Boolean result = Boolean.FALSE;
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        OrgInfo selectSite = bean.getSelectSite();
        WorkflowConfig wfConfig=null;
        if(selectSite != null){
             wfConfig = configService.findConfigBySite(selectSite.getUid());
        }else{
            wfConfig=configService.findConfigBySite(UserContextService.getCurrentUserAccount().getSiteUID());
        }
        if(wfConfig != null) {
            result = (wfConfig.getEnableLogPatientInfo() != null) ? wfConfig.getEnableLogPatientInfo() : Boolean.FALSE;
        }
        return result;
    }
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        UserContextService bean = WebUtil.getBean(UserContextService.class);
        searchFilters.clear();
        bean.onSelectSiteTreeNode(event);

    }
    public void editDialogTitle(Integer dialog){
        this.dialogTitle=dialog;
    }
    public Integer getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(Integer dialogTitle) {
        this.dialogTitle = dialogTitle;
    }
    public void queryStock(){
        String sharedAssetTypeId = this.getSelectedSart().getSharedAssetTypeId();
        if("-1".equals(sharedAssetTypeId)){
            SharedAssetReqType selectedSart = this.getSelectedSart();
            selectedSart.setId(null);
            selectedSart.setSwapName(null);
            this.setStock(0);
            return;
        }

        SharedAssetType sharedAssetType = swapDao.findById(sharedAssetTypeId);
        if(sharedAssetType != null){
            this.getSelectedSart().setSwapName(sharedAssetType.getSwapName());
        }
        if(Boolean.TRUE.equals(this.currentWorkflowConfig.getEnableStockShow())){
            this.setStock(selectStock(sharedAssetTypeId));
        }else{
            this.setStock(0);
        }
    }
    private Integer selectStock(String sharedAssetTypeId){
        Integer stock =0;
        if(StringUtils.isBlank(sharedAssetTypeId)) return 0;
        String siteUID = UserContextService.getCurrentUserAccount().getSiteUID();
        List<AssetInfo> count = getCount(siteUID, sharedAssetTypeId);// 总数
        if(CollectionUtils.isEmpty(count)) return stock;
        Integer returningCount = getReturningCount(count);
        return count.size()-returningCount;
    }
    private Integer getReturningCount( List<AssetInfo> collect) {
        // 过滤借出的
        List<AssetInfo> collect1 = collect.stream().filter(item -> {
            SharedAssetReqDetail byAssetInfoSharedAssetReqDetail = asdDao.findByAssetInfoSharedAssetReqDetail(item.getUid());
            if(byAssetInfoSharedAssetReqDetail == null){
                return  false;
            }
            String returnId = byAssetInfoSharedAssetReqDetail.getReturnId();
            if (StringUtils.isNotBlank(returnId)) {
                List<SharedAssetRequest> returnRequestList = dao.findByDept(returnId);
                if(CollectionUtils.isEmpty(returnRequestList)) return false;
                SharedAssetRequest returnRequest = returnRequestList.get(0);
                int returnState = returnRequest.getState();
                if(returnState ==7 || returnState == 8){
                    return true;
                }
            }else{
                String borrowId = byAssetInfoSharedAssetReqDetail.getBorrowId();
                List<SharedAssetRequest> borrowRequestList = dao.findByDept(borrowId);
                if(CollectionUtils.isEmpty(borrowRequestList)) return false;
                SharedAssetRequest borrowRequest = borrowRequestList.get(0);
                if(borrowRequest == null){
                    return false;
                }
                int borrowState = borrowRequest.getState();
                if(borrowState == 3 || borrowState ==6){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return collect1.size();
    }


    private List<AssetInfo> getCount(String siteUid,String sharedAssetTypeId) {
        return asiDao.findSharedAssetTypeIdCount(siteUid,sharedAssetTypeId);
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public boolean showAndDisabled(){
        if(this.currentWorkflowConfig == null){
            return Boolean.FALSE;
        }
        Boolean enableStockShow = this.currentWorkflowConfig.getEnableStockShow();
        if(Boolean.TRUE.equals(enableStockShow)){
            return this.getStock() < 1;
        }else{
            return Boolean.FALSE;
        }
    }
    private void setDataType(){
        boolean b = checkWhethertoenableunattended(selected.getSiteUID(), selected.getHospitalUID(), selected.getInstitutionUID(), selected.getTenantUID());
        if(b){
            selected.setDataType(2);
        }else{
            selected.setDataType(1);
        }
    }
    private boolean checkWhethertoenableunattended(String siteUid,String hospitalUID,String InstitutionUID,String tenantUID){
        String key="whetherToEnableUnattended";
        String byKeyAndSiteForUseParent = configItemValueRepository.findByKeyAndSiteForUseParent(siteUid,
                hospitalUID,
                InstitutionUID,
                tenantUID, key);
        return Boolean.TRUE.toString().equalsIgnoreCase(byKeyAndSiteForUseParent);
    }

    public void updateAssetReqType(){
        SharedAssetReqType selectedSard = this.selectedSart;
        String sharedAssetTypeId = selectedSard.getSharedAssetTypeId();
        if("-1".equals(sharedAssetTypeId)){
            selectedSard.setId(null);
            selectedSard.setSwapName(null);
            return;
        }else{
            SharedAssetType sharedAssetType = swapDao.findById(sharedAssetTypeId);
            selectedSard.setSwapName(sharedAssetType.getSwapName());
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
         List<V2_BlobObject> attachments = selected.getAttachments();
        if(CollectionUtils.isEmpty(attachments)){
            attachments= new ArrayList<>();
            selected.setAttachments(attachments);
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getFile().getContentType().startsWith("image")?"Image":"File";
            blobObject.setBoType(V2_BlobObject.AttachmentType.getKeyByValue("多级审批"));
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getId());
            blobObject.setTenantUid(selected.getTenantUID());
//            blobService.saveBlobObject(blobObject);
            attachments.add(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    private void saveAttachments(List<V2_BlobObject> Attachments){
        if(!CollectionUtils.isEmpty(Attachments)){
            blobService.saveBlobObject(Attachments);
        }
        if(!CollectionUtils.isEmpty(deleteAttachments)){
            blobService.deleteBlobFile(deleteAttachments);
            deleteAttachments = new ArrayList<>();
        }

    }

    private List<V2_BlobObject> initAttachments(String id){
        if(StringUtils.isEmpty(id)) return null;
        return blobService.findBlobObjectsByBoUid(id);
    }
    public void removeAttachmentById(String blobId) {
        if(CollectionUtils.isEmpty(deleteAttachments)){
            deleteAttachments = new ArrayList<>();
        }
         List<V2_BlobObject> attachments = selected.getAttachments();
        attachments.forEach(item->{
            if (item.getId().equals(blobId)) {
                deleteAttachments.add(item);
            }
        });
        attachments = attachments.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
        selected.setAttachments(attachments);
    }
    public List<SharedAssetRequest> getSharedAssetRequests() {
        return sharedAssetRequests;
    }

    public void setSharedAssetRequests(List<SharedAssetRequest> sharedAssetRequests) {
        this.sharedAssetRequests = sharedAssetRequests;
    }

    public String getViewImageStr() {
        return viewImageStr;
    }

    public void setViewImageStr(String viewImageStr) {
        this.viewImageStr = viewImageStr;
    }

    public String getSignatureContent() {
        return signatureContent;
    }

    public void setSignatureContent(String signatureContent) {
        this.signatureContent = signatureContent;
    }

    public StreamedContent getSignatureContentStream() {
        return signatureContentStream;
    }

    public void setSignatureContentStream(StreamedContent signatureContentStream) {
        this.signatureContentStream = signatureContentStream;
    }

    public void saveSignature() {
        V2_BlobObject blobObject = null;
            if(StringUtils.isNotBlank(lastSignatureId)){
                selected.setSignatureId(lastSignatureId);
                selected.setSignatory(UserContextService.getCurrentUserAccount().getName());
                selected.setSignatureTime(new Date());
                dao.save(selected);
            }else{
                try {
                    if(StringUtils.isNotBlank(signatureContent)){
                        String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                        BASE64Decoder decoder = new BASE64Decoder();
                        byte[] decode = decoder.decodeBuffer(base64);
                        blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                        if(blobObject != null){
                            String objectStorageId = blobObject.getObjectStorageId();
                            selected.setSignatureId(objectStorageId);
                            selected.setSignatory(UserContextService.getCurrentUserAccount().getName());
                            selected.setSignatureTime(new Date());
                            dao.save(selected);
                            v2_workOrderService.addUserSignature(objectStorageId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            initSignatureContent();
        if(selected.getSignatureId()!=null){
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            SystemEventAudit systemEventAudit =systemEventAuditAopAspect.buildEventAudit(userAccount, SystemEventAuditEnum.SHARED_RETURN_SIGNATURE, selected.getId(), true, null,"shared_asset_request",null);
            systemEventAuditAopAspect.sendSystemAuditMessage(systemEventAudit);
        }
    }

    public void resetSignature() {
        this.lastSignatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
    }
}
    
    
    