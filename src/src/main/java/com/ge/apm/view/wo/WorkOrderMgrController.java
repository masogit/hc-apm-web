/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.wo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.ConfigItemMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.domain.simpleparts.SimplePartsInventory;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.simpleparts.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.RepairSourceEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateField;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class WorkOrderMgrController extends  GenericCRUDUUIDController<V2_WorkOrder> implements Serializable, ServerEventInterface {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderMgrController.class);
    private SupplierController supplierController;
    @Setter @Getter
    UserAccount approver;
    @Setter @Getter
    Double c_woManhourPrice =0d;
    @Setter @Getter
    Double c_repairManCost=0d;
    @Setter @Getter
    Date statusStartTime;
    @Setter @Getter
    Date statusCloseTime;
    @Setter @Getter
    private Double c_repairTotalCost=0d;
    MvsAssetInfo mvsAssetInfo;
    @Setter @Getter
    private Supplier newSupplier;
    @Setter@Getter
    Boolean switchTmp=false;
    @Setter@Getter
    private Boolean nochange=false;
    @Setter@Getter
    private Boolean needTwoSubStatus=false;
    private UserAccount ua1;
    @Getter @Setter
    private UserAccount acceptPerson;
    private V2_WorkOrderRepository dao;
    private String supplierName;
    private String fileterOrgName;
    @Getter @Setter
    private String fileterOrgNameMVS;
    private String fileterAssetDeptOrgName;
    private List<OrgInfo> clinicalDeptList;
    private List<OrgInfo> clinicalDeptListMVS;
    private boolean terminate = false;
    private OrgInfoRepository orgDao;
    private UserAccountRepository userDao;
    private V2_WorkOrderService woService;
    private AuditGroupConfigurationRepository auditGroupConfigurationRepository;
    private UaaService uaaService;
    private UserContextService userContextService;
    private AssetFaultTypeService assetFaultTypeService;
    private AssetInfoRepository assetDao;
    private BlobFileService blobService;
    private WorkOrderDetailRepository detailDao;
    private WorkOrderDetailReturnRepository detailReturnDao;
    private V2_WorkOrderCoworkerRepository coWorkerDao;
    private V2_WorkOrderRepository woDao;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private ConfigItemMapper configItemMapper;
    private String locationName;
    private String assigneeId;
    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;
    private Set<String> clinicalDeptOrgUidSet;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSetMVS;
    private BlobObjectRepository blobObjectRepository;
    private V2_ServiceRequest newServiceRequest;
    private ConfigService configService;
    private MvsAssetInfoRepository mvsAssetInfoRepository;
    private V2_WorkOrder selectedWorkOrder;
    private List<V2_WorkOrder> selectedWorkOrderList;
    private AssetInfoMapper assetInfoMapper;
    private AssetInfo selectedAsset;
    private String assetStatus;
    private Integer currentStatus = 1;
    private Integer subStatus =1;
    private Boolean needConfirmTime = false;
    private String cancelReason;
    private String stepFileter;
    //for details form
    private List<V2_WorkOrder_Detail> accessoryList;
    private Boolean approveFlag = false;
    private List<V2_WorkOrderCoworker> coWorkerList;

    private List<MultiLevelApproval> approveList;

    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;
    private Map<String, Boolean> selectedBatchMap;
    @Getter
    @Setter
    private List<V2_WorkOrder> currentPageWorkOrder;
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
    private List<V2_BlobObject> videoList;
    private List<V2_BlobObject> attachList;
    public static List<AssetInfo> assetInfos = null;

    private List<Integer> tagGroupAssets;
    private List<Integer> tagNoGroupAssets;

    private String requestAssetUid;
    private String queryAssetUid;
    private List<V2_WorkOrder_Detail> approvedDetails;
    private Map<String, Boolean> selectedMap;
    private String selectedApproverUid;
    private ServiceRequestApiService serviceRequestApiService;
    private AuditGroupConfigurationDetailRepository auditGroupConfigurationDetailRepository;
    private ConfigCacheService cacheService;
    private String token;
    private OrgInfoTreeService orgInfoTreeService;
    private AutoCompleteService autoCompleteService;
    private AssetInfoMgrService aimService;
    private List<Supplier> supplierList = new ArrayList<Supplier>();
    private Map<String, Object> selectedObjectMap = new HashMap();
    private Map<String, Integer> woStatus = new HashMap<String, Integer>();
    @Getter
    @Setter
    private String plannedCompletionTimeFormat = null;
    //选定科室过滤
    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    //搜索字串
    private String oneSearchStr;
    //选定的院区
    private OrgInfo selectSite;
    //tab Title
    @Getter
    private String tabTitle;

    public String faultReason;
    private CustomConfigObject currentCustomConfig;
    private CustomConfigService ccService;
    private CustomConfig customConfig;
    private TenantInfo tenantInfo;
    @Getter
    @Setter
    private int statusFlag;
    @Getter
    @Setter
    private SimplePartsItem partsItem;
    @Getter
    @Setter
    private SimplePartsInventory partsInventory;
    @Getter
    @Setter
    private Integer currentQuantity;
    @Setter@Getter
    private boolean hasConfigMultiLevelApproval = false;
    private SimplePartsRequestService partsRequestService;
    private SimplePartsInventoryService partsInventoryService;
    private SimplePartsItemService partsItemService;
    private SimplePartsRequestWorkOrderService partsRequestWorkOrderService;
    private SimplePartsConfigService partsConfigService;
    private MultiLevelApprovalService multiLevelApprovalService;
    private ExternalTempUserRepository externalTempUserDao;
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    @Setter@Getter
    private String highlevelApprovalComments;
    List<V2_WorkOrder_Detail_Return> v2WorkOrderDetailReturnList;

    private V2_WorkOrderManHoursRepository manHoursDao;
    private Date applyTimeFrom = null;
    private String applyTimeFormatTime = null;
    private String filterApplyTime = null;
    List<V2_WorkOrderManHours> v2WorkOrderManHoursList;
    // 签名相关
    private String signatureContent;
    private StreamedContent signatureContentStream;
    private String signatureId;
    private boolean newSignature = true;

    private V2_WorkOrderManHoursDetail selectedWorkOrderManHoursDetail;
    private List<V2_WorkOrderManHoursDetail> workHoursList;
    @Getter @Setter
    private String siteUidFilterMVS;
    @Getter @Setter
    private String siteUidFilter;
    @Getter @Setter
    private List<OrgInfo> orgListMVS;
    @Getter @Setter
    List<V2_WorkOrder> bySearchFilterMVS = null;
    @Getter @Setter
    List<V2_WorkOrder> bySearchFilterInternal = null;
    @Getter @Setter
    private List<OrgInfo> orgList;
    @Getter @Setter
    private boolean hospitalShow = false;
    private UserAccount loginUser;
    @Getter @Setter
    private ExternalTempUser externalTempUser;
    @Getter @Setter
    private List<Map<String,String>> maintenancePersonInformation;

    private ConfigItemValueRepository configItemValueRepository;
    private ContractInfoRepository contractInfoRepository;
    @Getter @Setter
    private boolean viewIng = false;
    @Getter @Setter
    private boolean repaFlag = false;
    private Boolean enableAuditGroupConfiguration;
    @Getter @Setter
    private boolean enableMaintenanceContract = false;
    @Getter @Setter
    private boolean enableMaintenanceDate = false;
    @Getter @Setter
    boolean showLocationNameFlag;
    @Getter @Setter
    boolean enableSignNeedScanQrCode;
    @Getter @Setter
    private OrgInfo currentUserOrgInfo;
    @Getter @Setter
    private String newMessage;
    @Getter @Setter
    private Map<String, String> repairTipMessageMap = MapUtil.newHashMap();

    @Getter @Setter
    private boolean enableEngineersWriteRepairOutside;

    private V2_WorkOrderDetailRepository v2WorkOrderDetailRepository;

    private MultiLevelApprovalConfigRepository multiLevelApprovalConfigRepository;
    @Override
    protected void init() {
        try {
            supplierController = WebUtil.getBean(SupplierController.class);
            ccService = WebUtil.getBean(CustomConfigService.class);
            contractInfoRepository = WebUtil.getBean(ContractInfoRepository.class);
            blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
            auditGroupConfigurationDetailRepository = WebUtil.getBean(AuditGroupConfigurationDetailRepository.class);
            loginUser = UserContextService.getCurrentUserAccount();
            currentCustomConfig = ccService.getCurrentCustomConfig(
                    loginUser.getSiteUID());
            aimService = WebUtil.getBean(AssetInfoMgrService.class);
            mvsAssetInfoRepository = WebUtil.getBean(MvsAssetInfoRepository.class);
            token = UserContextService.getAccessToken();
            dao = WebUtil.getBean(V2_WorkOrderRepository.class);
            auditGroupConfigurationRepository = WebUtil.getBean(AuditGroupConfigurationRepository.class);
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
            cacheService = WebUtil.getBean(ConfigCacheService.class);
            woDao = WebUtil.getBean(V2_WorkOrderRepository.class);
            assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
            configService = WebUtil.getBean(ConfigService.class);
            serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);
            orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
            autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
            partsRequestService = WebUtil.getBean(SimplePartsRequestService.class);
            partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
            partsItemService = WebUtil.getBean(SimplePartsItemService.class);
            partsRequestWorkOrderService = WebUtil.getBean(SimplePartsRequestWorkOrderService.class);
            partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
            multiLevelApprovalService = WebUtil.getBean(MultiLevelApprovalService.class);
            multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
            detailReturnDao = WebUtil.getBean(WorkOrderDetailReturnRepository.class);
            manHoursDao = WebUtil.getBean(V2_WorkOrderManHoursRepository.class);
            externalTempUserDao = WebUtil.getBean(ExternalTempUserRepository.class);
            assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
            configItemMapper = WebUtil.getBean(ConfigItemMapper.class);
            configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
            v2WorkOrderDetailRepository = WebUtil.getBean(V2_WorkOrderDetailRepository.class);
            multiLevelApprovalConfigRepository = WebUtil.getBean(MultiLevelApprovalConfigRepository.class);
            selectSite = cacheService.findOrgByUid(loginUser.getSiteUID());
            tenantInfo = userContextService.getTenantInfo();
            customConfig = ccService.getCustomConfigBySite(selectSite.getUid());
            needTwoSubStatus = isConfigNeedTwoSubStatusByUser(userContextService.getLoginUser());
            ConfigItemValue enableMaintenanceContractValue = configItemValueRepository.findFirstByKeyAndSiteUid("enableMaintenanceContract", loginUser.getSiteUID());
            enableMaintenanceContract = Optional.ofNullable(enableMaintenanceContractValue).map(ConfigItemValue::getValue).map("true"::equalsIgnoreCase).orElse(false);
            ConfigItemValue enableMaintenanceDateValue = configItemValueRepository.findFirstByKeyAndSiteUid("enableMaintenanceDate", loginUser.getSiteUID());
            enableMaintenanceDate = Optional.ofNullable(enableMaintenanceDateValue).map(ConfigItemValue::getValue).map("true"::equalsIgnoreCase).orElse(false);
            selectedBatchMap = new HashMap<>();
            currentUserOrgInfo = orgDao.findByUid(loginUser.getSiteUID());
            enableAuditGroupConfiguration();
            String query = WebUtil.getRequestParameter("query");
            String enCodeParameters = WebUtil.getRequestParameter("str");
            String queryAssetUID_href = WebUtil.getRequestParameter("queryAssetUID");
            if ("View".equals((String) UrlEncryptController.getValueFromMap(enCodeParameters, "actionName"))) {
                queryIndex = Integer.valueOf(UrlEncryptController.getValueFromMap(enCodeParameters, "queryIndex").toString());
                selected = dao.findById((String) UrlEncryptController.getValueFromMap(enCodeParameters, "selectedid"));
                viewIng = true;
                onSelectWorkOrder();
            } else {
                if (!Strings.isNullOrEmpty(queryAssetUID_href)) {
                    queryAssetUid = queryAssetUID_href;
                }
                if (!Strings.isNullOrEmpty(enCodeParameters)) {
                    requestAssetUid = (String) UrlEncryptController
                            .getValueFromMap(enCodeParameters, "assetUID");
                    queryAssetUid = (String) UrlEncryptController
                            .getValueFromMap(enCodeParameters, "queryAssetUID");
                    if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                        query = (String) UrlEncryptController
                                .getValueFromMap(enCodeParameters, "query");
                    }
                }
                if (Strings.isNullOrEmpty(query)) {
                    //云南阜外由默认全部改为默认待办
                    queryIndex = 0;
                } else {
                    queryIndex = Integer.valueOf(query);
                    searchList(queryIndex);
                }
                this.filterBySite = false;
                this.filterByHospital = false;
                terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
                List<I18nMessage> ix2 = FieldValueMessageController
                        .getFieldValueListByMsgType("woSteps");
                ix2.stream().forEach(
                        item -> woStatus.put(item.getValueZh(), Integer.parseInt(item.getMsgKey())));
                loadClinicalDeptList();
            }
        } catch (Exception e) {
            LOGGER.error("init occur exception", e);
            throw e;
        }
        initLoadInternalSiteNameList();
        String showLocationNameFlagStr = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        showLocationNameFlag = StrUtil.isNotEmpty(showLocationNameFlagStr) && "true".equals(showLocationNameFlagStr);
        String enableSignNeedScanQrCodeStr = userContextService.getConfigItemValue("enableSignNeedScanQrCode");
        enableSignNeedScanQrCode = StringUtils.isEmpty(enableSignNeedScanQrCodeStr) || !Boolean.parseBoolean(enableSignNeedScanQrCodeStr);
    }

    public void setSelected(MvsAssetInfo mvsAssetInfo) {
        this.mvsAssetInfo = mvsAssetInfo;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = currentPageWorkOrder.stream().noneMatch(item -> !selectedBatchMap.containsKey(item.getId()) || !selectedBatchMap.get(item.getId()));
        for (V2_WorkOrder item : currentPageWorkOrder) {
            selectedBatchMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void partsPriceUpdate(){
        if(itemDetail.getApartsQuantity() == null || itemDetail.getPartsPrice() == null ){
            itemDetail.setClosedPrice(0.0);
        }else{
            itemDetail.setClosedPrice(itemDetail.getPartsPrice()*itemDetail.getApartsQuantity());
        }
    }

    @Setter
    public boolean displayTime=false;

    public UserAccount getUa1() {
        return ua1;
    }

    public void setUa1(UserAccount ua1) {
        this.ua1 = ua1;
    }

    private List<UserAccount> acker;
    public List<UserAccount> completeAckList(String query) {
        if (acker == null) {
            return Collections.emptyList();
        }

        List<UserAccount> res = this.acker.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        if (loginUser.isEnableAssetTypeGroup()) {
            // 已开启资产分类，需要过滤掉没有资产分类权限的人
            List<String> userUidList = assetTypeGroupRepository.getUserUidListByEncoded(selectedWorkOrder.getAssetInfo().getAssetType());
            res = res.stream().filter(user -> userUidList.contains(user.getUid())).collect(Collectors.toList());
        }
        return res;
    }
    public void changeStatus() {
        partsItem = null;
        partsInventory = null;
        currentQuantity = 0;
        itemDetail.setParts(null);
        itemDetail.setPartsPrice(null);
        itemDetail.setClosedPrice(null);
        if (itemDetail != null) {
            setStatusFlagByType(itemDetail.getPartsType());
        }
    }
    public void onSparePartsValueChange(SelectEvent event) {
        Object object = event.getObject();

        if (null != object) {
            partsItem = (SimplePartsItem) object;
            itemDetail.setParts(partsItem.getPartsNameAndSpecification());
            SimplePartsItemValue partsItemValue = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(partsItem.getId(),null).get();
            itemDetail.setPartsPrice(partsItemValue.getEstimatedUnitPrice().doubleValue());
            itemDetail.setClosedPrice(partsItemValue.getEstimatedUnitPrice().doubleValue());
            itemDetail.setCurrentQuantity(partsItemValue.getInStockNum());
            itemDetail.setPartsItemId(partsItem.getId());
            itemDetail.setApartsQuantity(null);
            if(userContextService.isCountyArea() || partsConfigService.isEnableTenantLocal()){
                itemDetail.setPartsPrice(null);
                if(partsItem.getUnitPrice() != null){
                    itemDetail.setPartsPrice(partsItem.getUnitPrice().doubleValue());
                }
                itemDetail.setPartsDescription(partsItem.getPartsDescription());
                itemDetail.setCurrentQuantity(partsRequestService.getTenantTotalStockNum(partsItem.getId()));
            }
        }
    }
    public void onSparePartsMasterValueChange(SelectEvent event) {
        SimplePartsItem partsItem = (SimplePartsItem) event.getObject();
        if (partsItem == null) return;
        itemDetail.setParts(partsItem.getName());
        itemDetail.setPartsItemId(partsItem.getId());
        if(userContextService.isCountyArea()){
            itemDetail.setPartsPrice(null);
            if(partsItem.getUnitPrice() != null){
                itemDetail.setPartsPrice(partsItem.getUnitPrice().doubleValue());
            }
            itemDetail.setPartsDescription(partsItem.getPartsDescription());
        }
    }
    public List<SimplePartsItem> autosparePartName(String name) {
        List<SimplePartsItem> all = null;
        if(userContextService.isCountyArea() || partsConfigService.isEnableTenantLocal()){
            all = initSparePartsMaster(name);
        }else{
            all = partsInventoryService.findAllByName(name);
        }

        /*for (SimplePartsItem simplePartsItem : all) {
            if(StringUtils.isNotEmpty(simplePartsItem.getName()) && StringUtils.isNotEmpty(simplePartsItem.getSpecification())){
                simplePartsItem.setName(simplePartsItem.getName() + "(" + simplePartsItem.getSpecification() + ")");
            }
        }*/

        return all;
    }
    public List<SimplePartsItem> initSparePartsMaster(String name) {
        List<SimplePartsItem> partsItemList = partsItemService.findAllByName(name);
        return partsItemList;
    }
    public boolean isEnableIntegrationWithWorkOrder() {
        return partsConfigService.isEnableIntegrationWithWorkOrder();
    }
    public void repairResultSet(){
        if(selectedWorkOrder.getRepairResult().equalsIgnoreCase ("1")){
            currentStatus =1;
        }
        else if(selectedWorkOrder.getRepairResult().equalsIgnoreCase("2")){
            currentStatus =3;
        } else if(selectedWorkOrder.getRepairResult().equalsIgnoreCase("3")){
            currentStatus =4;
        }else if(selectedWorkOrder.getRepairResult().equalsIgnoreCase("4")){
            currentStatus =3;
        }else if(selectedWorkOrder.getRepairResult().equalsIgnoreCase("5")){
            currentStatus =1;
        }
    }
    public void selectSubstatus(){
        if(selectedWorkOrder.getSubStatus() == null){
            displayTime=false;
        }else if(selectedWorkOrder.getSubStatus().intValue()==ApmConstant.SUBSTATUS1){
            subStatus =1;
            displayTime=true;
        }else if(selectedWorkOrder.getSubStatus().intValue()==ApmConstant.SUBSTATUS2){
            subStatus =2;
            displayTime=false;
        } else if(selectedWorkOrder.getSubStatus().intValue()==ApmConstant.SUBSTATUS3){
            subStatus =3;
            displayTime=false;
        }
    }

    public boolean getDisplayTime(){
        if(selectedWorkOrder.getSubStatus() == null){
            return false;
        }
        return this.displayTime;
    }

    public void closePrcieUpdate(){
        itemDetail.setClosedPrice(itemDetail.getClosedPrice());
    }
    public String getStepImg(V2_WorkOrder v2_workOrder){
        Locale locale = LocaleContextHolder.getLocale();
        boolean en = locale.ENGLISH.equals(locale);
        Integer stepId =v2_workOrder.getCurrentStepId();
        int mode =v2_workOrder.getDispatchMode()!=null?v2_workOrder.getDispatchMode().intValue():0;
        switch(stepId){
            case 7: //待评价
                return en?"/images/icons/en_5.png":"/images/icons/5.png";
            case 6: //
                return en?"/images/icons/en_5.png":"/images/icons/5.png";
            case 5: //待验收
            {
                if( v2_workOrder.getV2_workOrder_detail_list().size()>0 && v2_workOrder.getApprovalDate()==null){//有备件 没有提交审核 则显审核图标显示为灰色的
                    return en?"/images/icons/en_6.png":"/images/icons/6.png";
                }
                if(v2_workOrder.getV2_workOrder_detail_list().size() ==0){
                    return en?"/images/icons/en_6.png":"/images/icons/6.png";
                }
                else{
                    return en?"/images/icons/en_5.png":"/images/icons/5.png";
                }
            }
            case 8: //待审核
                return en?"/images/icons/en_4.png":"/images/icons/4.png";
            case 4: //维修中
            case 10: //待送修
            case 11: //待返还
            {
                if(v2_workOrder.getApprovalDate()!=null){
                    return   en?"/images/icons/en_4.png":"/images/icons/4.png";
                }
                return  en?"/images/icons/en_3.png":"/images/icons/3.png";
            }

            case 3: //接单中
                return en?"/images/icons/en_2.png":"/images/icons/2.png";
            case 2:
                if(mode== 2 || mode ==3 ){//如果是抢单或抢单加手动模式
                    return en?"/images/icons/en_2.png":"/images/icons/2.png";
                }
            default:
                return en?"/images/icons/en_1.png":"/images/icons/1.png";
        }
    }
    public String getStepName(V2_WorkOrder v2_workOrder){
        Integer stepId =v2_workOrder.getCurrentStepId();
        switch(stepId){
            case 2:
            case 3:
                return "待接单";
            case 4:
                return "维修中";
            case 7:
            case 9:
                return "已关单";
            default:
                return "";
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

    public List<String> autoCompleteDeptNameMVS(String query) {
        if (clinicalDeptListMVS == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptListMVS.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        selectedBatchMap.clear();
        if (event instanceof SelectEvent) {
            fileterOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterOrgName.equals(item.getName()))) {
            fileterOrgName = null;
        }

        if (fileterOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void onClinicalDeptNameFilterChangeMVS(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgNameMVS = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgNameMVS) && !clinicalDeptListMVS.stream().anyMatch(item -> fileterOrgNameMVS.equals(item.getName()))) {
            fileterOrgNameMVS = null;
        }

        if (fileterOrgNameMVS != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptListMVS.stream().filter(item -> item.getName().equals(fileterOrgNameMVS)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptListMVS);
            clinicalDeptOrgUidSetMVS = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSetMVS = Collections.emptySet();
        }
    }

    private void loadClinicalDeptList() {
        try {
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    //tab切换
    public void onTabChange(TabChangeEvent event) {
        FacesMessage msg = new FacesMessage("activeTab", event.getTab().getTitle());
        tabTitle = msg.getDetail();
        queryIndex = 0;
    }

    public void updateQueryIndex(int query){
        queryIndex = query;
        selected = null;
    }

    private Boolean isRating;

    public List<String> autoCompleteSupplier(String query) {
        supplierList = aimService.getSuplierListBySiteUid(tenantInfo.getUid());
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

    /*stepName:StepId 报修:1 待派工: 2 , 待接单:3, 维修中:4 , 待验收:5 待关单:6 ， 待评价:7  , 待审核:8 ， 已计划: 0*/
    @Override
    protected Page<V2_WorkOrder> loadData(PageRequest pageRequest) {
        Page<V2_WorkOrder> pageResult = null;
        try {
            if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(loginUser.getAssetTypeGroupList())) {
                return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
            }
            this.setSearchFilter();
            String[] fields = new String[]{"status", "currentStepId", "currentStepName", "currentPersonId", "dispatchMode"
                    , "srId", "feedbackRating", "assetId", "approvalStatus", "assetInfo.assetOwnerId", "assetInfo.assetOwnerId2"
                    , "assetInfo.assetOwnerId3", "assetInfo.uid", "assetInfo.clinicalDeptUID", "approverId", "serviceRequest.status"};
            Arrays.asList(fields).forEach(field -> removeFilterOnField(field));
            Integer fillterStepId;
            if (queryIndex == 1) {//已报修 可取消
                if (userContextService.hasRole("AssetStaff") && !userContextService.hasRole("AssetHead")) {
                    searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1))));
                }
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.LTE, 3));
            } else if (queryIndex == 2) { //待派工
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2));
                searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
            } else if (queryIndex == 3) { //待接单
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
                searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3), "1*3+4*"));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, loginUser.getId(), "1*3+4*A+"));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+4*A+B*"));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 2, "1*3+4*A+B*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+4*5+owner*6+"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+4*5+owner*6+"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+4*5+owner*6+"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner1*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner1*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner2*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner2*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner3*"));
                searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*3+4*5+owner*6+owner3*"));
                //去除派工模式4且待派工的单子
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*3+7*"));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 3, "1*3+7*"));
                //当前责任人是自己且已经派工的单子
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+9*"));
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
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+"));
            } else if (queryIndex == 5) { //待验收
                /*工程师和主任， 如果两个角色同时存在，则优先以主任角色.*/
                if (userContextService.hasRole("AssetStaff") && !userContextService.hasRole("AssetHead")) {
                    searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1)), "1*3+"));
                    searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
                    searchFilters.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.EQ, loginUser.getOrgInfoUID(), "1*3+"));
                } else {
                    searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                    searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 5));
                }
            } else if (queryIndex == 6) { //待评价
                if (userContextService.hasRole("AssetStaff") && !userContextService.hasRole("AssetHead")) {
                    searchFilters.add(new SearchFilter("srId", SearchFilter.Operator.IN, woService.getSRListByStatus(Arrays.asList(1, 2))));
                }
                searchFilters.add(new SearchFilter("feedbackRating", SearchFilter.Operator.EQ, 0));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.NE, 10));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.NE, 11));
            } else if (queryIndex == 7) { //待关单
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.GTE, 6));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.NE, 10));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.NE, 11));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+"));
            } else if (queryIndex == 8) { //已关单
                if (!userContextService.hasRole("AssetHead") && !userContextService.isDispatcher()) {
                    if (userContextService.hasRole("AssetStaff")) {
                        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId()));
                        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+"));
                    }
                }
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
            } else if (queryIndex == 9) { //已取消
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 3));
            } else if (queryIndex == 10) { //待审批
                String tenantUID = loginUser.getTenantUID();
                if (enableAuditGroupConfiguration) {
                    List<UserAccount> userAccountList = new ArrayList<>();
                    AuditGroupConfiguration auditGroupConfiguration = auditGroupConfigurationRepository.findByHierOrgUidAndGroupName(loginUser.getHospitalUID(), WorkOrderConstant.CORE_GROUP_MEMBERS);
                    if (auditGroupConfiguration != null) {
                        String groupMemberItem = auditGroupConfiguration.getGroupMemberItem();
                        List<AuditGroupConfigurationNode> auditGroupConfigurationNodes = JSON.parseObject(groupMemberItem, new TypeReference<List<AuditGroupConfigurationNode>>() {
                        });
                        if (auditGroupConfigurationNodes != null && !auditGroupConfigurationNodes.isEmpty()) {
                            for (int i = 0; i < auditGroupConfigurationNodes.size(); i++) {
                                AuditGroupConfigurationNode auditGroupConfigurationNode = auditGroupConfigurationNodes.get(i);
                                List<String> userUids = auditGroupConfigurationNode.getUserUids();
                                if (userUids != null) {
                                    UserAccount account = userDao.getByUid(userUids.get(0));
                                    userAccountList.add(account);
                                }
                            }

                        }
                    }
                    if (userAccountList.contains(loginUser)) {
                        searchFilters.add(new SearchFilter("isCoreMemberApproval", SearchFilter.Operator.EQ, 1, "1*approve+"));
                        searchFilters.add(new SearchFilter("approverId", SearchFilter.Operator.EQ, loginUser.getId(), "1*approve+9*"));
                        searchFilters.add(new SearchFilter("isCoreMemberApproval", SearchFilter.Operator.EQ, 0, "1*approve+9*"));
                    } else {
                        searchFilters.add(new SearchFilter("approverId", SearchFilter.Operator.EQ, loginUser.getId()));
                        searchFilters.add(new SearchFilter("isCoreMemberApproval", SearchFilter.Operator.EQ, 0));
                    }
                } else {
                    if (!(userContextService.hasRole("AssetHead") || userContextService.hasRole("DeptHead"))) {
                        searchFilters.add(new SearchFilter("approverId", SearchFilter.Operator.EQ, loginUser.getId()));
                    }
                    if (userContextService.hasRole("DeptHead")) {
                        List<String> clinicalUidList = orgDao.findClinicalUidBySiteUid(Arrays.asList(loginUser.getSiteUID()));
                        searchFilters.add(new SearchFilter("approverId", SearchFilter.Operator.EQ, loginUser.getId()));
                        searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, clinicalUidList, "1*2+"));
                    }
                }
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 4));
                searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
                searchFilters.add(new SearchFilter("serviceRequest.status", SearchFilter.Operator.EQ, "1"));


            } else if (queryIndex == 11) { //全部维修单
                if (stepFileter != null) {
                    fillterStepId = woStatus.get(stepFileter);
                    if (fillterStepId > 6) {
                        if (userContextService.hasCurrentTenantName("")) {
                            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(7, 9)));
                        } else if(fillterStepId==10 ||fillterStepId==11){
                            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, fillterStepId));
                        }else {
                            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 7));
                        }
                    } else {
                        if (fillterStepId == 3 && userContextService.hasCurrentTenantName("")) {
                            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
                        } else {
                            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, fillterStepId));
                        }
                    }
                }

            } else if (queryIndex == 12) { //指定设备queryAssetUid 工单 设备详细页面跳转
                if (!Strings.isNullOrEmpty(queryAssetUid)) {
                    searchFilters.add(new SearchFilter("assetInfo.uid", SearchFilter.Operator.EQ, queryAssetUid, "1*"));
                }
            } else if (queryIndex == 13) {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 10));

            } else if (queryIndex == 14) {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 11));
            }else {//待办
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 1));
                if(stepFileter!=null) {
                    fillterStepId = woStatus.get(stepFileter);
                    if (fillterStepId > 6 &&(fillterStepId != 10 && fillterStepId != 11)) {
                        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, 7));
                    } else {
                        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, fillterStepId));
                    }
                }
            }
            this.selected = null;
            String value = null;
            boolean isId= false;
            for (SearchFilter searchFilter :searchFilters) {
                if ("id".equals(searchFilter.fieldName)) {
                    if(searchFilter.value instanceof java.util.List){
                        isId = false;
                    }else{
                        value = (String)searchFilter.value;
                        isId = true;
                    }
                }
            }
            if(isId) {
                List<V2_WorkOrder_Step> vs = woService.getWorkOrderDetailByownerName(value);
                List<String> woIdList = new ArrayList<>();
                if (!vs.isEmpty()) {
                    for (V2_WorkOrder_Step step : vs) {
                        woIdList.add(step.getWoId());
                    }
                    searchFilters.removeIf(e -> e.fieldName.equals("id"));
                    searchFilters.add(new SearchFilter("id", SearchFilter.Operator.IN, woIdList, "1*"));
                }
            }
            //查询Mvs工单
            if("MVS工单".equals(tabTitle)){
                List<SearchFilter> filters = buildMvsSearchFilter();
                logger.info("workordermgrmvsfilter "+JSONObject.toJSONString(filters));
                pageResult = dao.findBySearchFilter(filters, pageRequest);
                bySearchFilterMVS = pageResult.getContent();
                //loadMVSClinicalDeptList();
            }else{
                pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
                bySearchFilterInternal = pageResult.getContent();
            }
            List<V2_WorkOrder> workOrderList = new ArrayList<>();
            if(enableAuditGroupConfiguration && queryIndex == 10){
                List<V2_WorkOrder> v2_workOrders = dao.findBySearchFilter(this.searchFilters);
                if(v2_workOrders!=null && !v2_workOrders.isEmpty()){
                    for (V2_WorkOrder v2_workOrder :v2_workOrders) {
                        if (getSign(v2_workOrder.getId(), loginUser)) {
                            workOrderList.add(v2_workOrder);
                        }
                    }
                    int size = workOrderList.size();
                    workOrderList= this.pageNum(workOrderList, pageRequest);
                    pageResult = new PageImpl<V2_WorkOrder>(workOrderList, pageRequest, size);
                }
            }
        } catch (Exception e) {
            LOGGER.error("load data occur exception,{}", e.getMessage());
            throw e;
        }
        if (!userContextService.hasCurrentTenantName("")) {
            needTwoSubStatusBatch(pageResult.getContent());
        }
        currentPageWorkOrder = pageResult.getContent();
        return pageResult;
    }
    private List<V2_WorkOrder> pageNum(List<V2_WorkOrder> v2_workOrderList,PageRequest pageRequest){
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize() - 1;
        int numStart = pageNumber * pageSize;
        numStart = numStart + pageNumber;
        int numEnd = numStart + pageSize + 1;
        int size = v2_workOrderList.size();
        if(size > numEnd){
            v2_workOrderList = v2_workOrderList.subList(numStart ,numEnd);
        }else {
            v2_workOrderList = v2_workOrderList.subList(numStart ,size);
        }
        return v2_workOrderList;
    }
    public Boolean getSign(String woId,UserAccount userAccount){
        List<AuditGroupConfigurationDetail> details = auditGroupConfigurationDetailRepository.findByBusinessUidAndActualAuditUidAndDeleteFlag(woId, userAccount.getUid(),0);
        if(details==null || details.isEmpty()){
            return true;
        }else {
            for (AuditGroupConfigurationDetail auditGroupConfigurationDetail:details) {
                if(auditGroupConfigurationDetail.getAuditNodeStatus()==0){
                    return true;
                }
            }
        }
        return false;
    }
    public List<SearchFilter> buildMvsSearchFilter(){
        List<SearchFilter> filters = new ArrayList<>();
        List<String> assetUIds = serviceRequestApiService.getVisableMvsAssetUidSystemId(UserContextService.getAccessToken());
        logger.info("workorder.buildMvsSearchFilter" +JSONObject.toJSONString(assetUIds));
        if (CollectionUtils.isEmpty(assetUIds)) {
            assetUIds = new ArrayList<>();
            assetUIds.add("-------");
            filters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, assetUIds));
        } else {
            filters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, assetUIds));
        }
        if (!CollectionUtils.isEmpty(clinicalDeptOrgUidSetMVS)) {
            filters.add(new SearchFilter("mvsIbMapping.assetInfo.clinicalDeptUID",SearchFilter.Operator.IN, clinicalDeptOrgUidSetMVS));
        }
        if (!StringUtils.isEmpty(siteUidFilterMVS)) {
            filters.add(new SearchFilter("mvsIbMapping.assetInfo.siteUID",SearchFilter.Operator.EQ, siteUidFilterMVS));
        }
        for (SearchFilter searchFilter :searchFilters) {
            if (("status,currentStepId,approvalStatus,serviceRequest.status,feedbackRating,mvsIbMapping.assetInfo.name,mvsIbMapping.assetInfo.serialNum,mvsIbMapping.assetInfo.departNum,mvsIbMapping.assetInfo.functionType" +
                    ",serviceRequest.srNum,serviceRequest.casePriority").contains(searchFilter.fieldName)) {
                filters.add(searchFilter);
            }
        }
        return filters;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ,loginUser.getTenantUID(), "1*"));

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("serviceRequest.fromDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "serviceRequest.fromDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (StringUtils.isNotEmpty(siteUidFilter)) {
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUidFilter, "1*"));
        }
        if (!this.hasFilterOnField("siteUID")) {
            if(enableAuditGroupConfiguration && queryIndex == 10) {
                String hospitalUID = loginUser.getHospitalUID();
                updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, hospitalUID));
            }else {
                List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

                updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
                updateSearchFilter(new SearchFilter("serviceRequest.requestorId", SearchFilter.Operator.EQ, loginUser.getId(), "1*2+"));
                updateSearchFilter(new SearchFilter("serviceRequest.fromDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            }
        }

        // 资产分类权限
        if (loginUser.isEnableAssetTypeGroup()) {
            searchFilters.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, loginUser.getAssetTypeGroupMap().keySet(), "1*"));
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
            List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if (orgUidList == null || orgUidList.isEmpty()) {
                orgUidList = Arrays.asList("");
            }
            tagNoGroupAssets = dao.getAssetsByNoTagGroup(orgUidList);
        }
        return tagNoGroupAssets;
    }

    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
            tagGroupAssets = dao.getAssetsByTagGroup(loginUser.getId());
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
        v2WorkOrderDetailReturnList = null;
        signatureId = null;
        signatureContentStream = null;
        signatureContent = null;
        newSignature = true;
        workHoursList = null;
        selectedApproverUid = null;
    }

    public void onSelectWorkOrder() {
        newServiceRequest = null;
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        currentStatus = selected.getAssetInfo().getStatus();
        needConfirmTime = false;
        selectedWorkOrderList = dao.findBySrId(selected.getServiceRequest().getId());
        audioList = woService.getPicListBySrUid(selected.getSrId(), 2, null);
        videoList = woService.getPicListBySrUid(selected.getSrId(), 3, null);
        picList = woService.getPicListBySrUid(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_IMG);
        attachList = woService.getPicListBySrUid(selected.getSrId(), 1, V2_BlobObject.BO_SUB_TYPE_DOCS);
        // 获取temp user信息
        initExternalTempUser();
    }

    public void onSelectOrder(String selectedId) throws IOException {
        if (ObjectUtil.isNotEmpty(selectedId)) {
            List<V2_WorkOrder> orders = dao.findBySrId(selectedId);
            selected = orders.get(0);
        }
        onSelectWorkOrder();
        String url = "actionName=View&selectedid="
                + selected.getId()
                + "&queryIndex=" + queryIndex
                ;
        url = "workOrderList.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    private void initExternalTempUser(){
        initExternalTempUser(selected);
        if(ObjectUtil.isNotEmpty(externalTempUser)){
            maintenancePersonInformation = new ArrayList<>();
            maintenancePersonInformation.add(initMap("userName","姓名",externalTempUser.getUserName()));
            maintenancePersonInformation.add(initMap("tel","联系人电话",externalTempUser.getTel()));
            maintenancePersonInformation.add(initMap("comName","公司名称",externalTempUser.getComName()));
        }

    }

    private void initExternalTempUser(V2_WorkOrder workOrder) {
        externalTempUser = externalTempUserDao.findByWoId(workOrder.getId());
    }

    private Map<String,String> initMap(String fieldName,String key,String label){
        Map<String,String> row=new HashMap<>();
        row.put("fieldName",fieldName);
        row.put("key",key);
        row.put("label",label);
        return row;
    }
    public List<V2_BlobObject> getServiceRequestPicList(String srId, Integer type, String boSubType) { //type 1:picture 2:audio

        /*List<V2_BlobObject> pList = woService.getPicListBySrUid(srId, type, boSubType).stream().filter(item -> type == 1 ^
                (item.getObjectType() != null && (item.getObjectType().contains("audio") || item.getObjectType().contains("video")))).collect(Collectors.toList());

        return pList;*/
        return woService.getPicListBySrUid(srId, type, boSubType);
    }

    /*
     *   if type =1 -->picList
     *
     * */
    public List<V2_WorkOrder_Step> getWOrkOrderStep(String woId) {
        return woService.getWorkOrderSteps(woId);
    }
    public String getRepairResponer(String woId,Integer step){
        List <V2_WorkOrder_Step >vs=null;
        vs =woService.getWorkOrderDetailResponsor(woId,step);
        if(vs !=null && vs.size()>0){
            return vs.get(0).getOwnerName();
        }else {
            return "";
        }

    }
    public String getRepairPersonName(String woId,Integer step){
        List <V2_WorkOrder_Step >vs=null;
        vs =woService.getWorkOrderRepaireName(woId,step);
        if(vs !=null && vs.size()>0){
            if(vs.size()==1){
                return vs.get(0).getOwnerName();
            }else{
                V2_WorkOrder_Step lastRepaireName = vs.get(0);
                for(V2_WorkOrder_Step woStep : vs){
                    boolean before = lastRepaireName.getStartTime().before(woStep.getStartTime());
                    if(before){
                        lastRepaireName = woStep;
                    }
                }
                return lastRepaireName.getOwnerName();
            }
        }else {
            return "";
        }

    }

    public String getRepairNameRefract(String woId, Integer step){
        List<V2_WorkOrder_Step> v2WorkOrderStep = woService.getMaxStartTimeWorkOrderStepByWoIdAndStepId(woId, step);
        return CollectionUtils.isEmpty(v2WorkOrderStep)?"":v2WorkOrderStep.get(0).getOwnerName();
    }

    public V2_WorkOrder_Step getRepairPerson(String woId, Integer step){
        List<V2_WorkOrder_Step> v2WorkOrderStep = woService.getMaxStartTimeWorkOrderStepByWoIdAndStepId(woId, step);
        return CollectionUtils.isEmpty(v2WorkOrderStep)?null:v2WorkOrderStep.get(0);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        //获取多级备件审批节点详情
        try {
            approveList = multiLevelApprovalRepository.getByBusinessUidList(woId);
            if (!CollectionUtils.isEmpty(approveList)) {
                MultiLevelApprovalConfig config = multiLevelApprovalService.getApproveWorkflowConfig(approveList.get(0).getWorkflowConfigApproveUid());
                List<ApprovalNode> approvalNodes = JSON.parseObject(config.getApprovalItem(), new TypeReference<List<ApprovalNode>>(){});
                boolean isOwner = isOwner();
                approveList.forEach(v -> {
                    ApprovalNode node = approvalNodes.get(v.getApprovalLevel() - 1);
                    if (isOwner) {
                        switch (v.getApprovalNodeStatus()) {
                            case 0:
                            case 1:
                                v.setActualApproverName(userDao.findByUidIn(Arrays.stream(v.getApprovalUid().split(",")).filter(c -> StringUtils.isNotEmpty(c)).collect(Collectors.toList())).stream().map(UserAccount::getName).collect(Collectors.joining(",")));
                                break;
                        }
                    } else {
                        v.setActualApproverName(node.getNodeName());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<V2_WorkOrder_Detail> workOrderDetails = woService.getWorkOrderDetails(woId);

        if (!CollectionUtils.isEmpty(approveList)) {
            List<String> workflowConfigApproveUids = approveList.stream().map(MultiLevelApproval::getWorkflowConfigApproveUid).collect(Collectors.toList());
            Map<String, List<MultiLevelApproval>> listMap = approveList.stream()
                    .filter(item -> item.getApprovalLevelNow() != -1 && item.getApprovalLevelNow() != 0)
                    .collect(Collectors.groupingBy(MultiLevelApproval::getWorkflowConfigApproveUid));
            HashMap<String, List<MultiLevelApproval>> stringListHashMap = new HashMap<>();

            List<MultiLevelApprovalConfig> multiLevelApprovalConfigs = multiLevelApprovalConfigRepository.getByIds(workflowConfigApproveUids);
            for (MultiLevelApprovalConfig multiLevelApprovalConfig : multiLevelApprovalConfigs) {
                Map currentConfig = JSON.parseObject(multiLevelApprovalConfig.getConfigration(), Map.class);
                List<String> currentConfigPartsType = (List<String>) currentConfig.get("selectAssetPartsTypes");
                List<MultiLevelApproval> multiLevelApprovals = listMap.get(multiLevelApprovalConfig.getId());
                for (String s : currentConfigPartsType) {
                    stringListHashMap.put(s,multiLevelApprovals);
                }
            }
            workOrderDetails.forEach(item->{
                if(item.getApproved()!=null){
                    item.setApprovalStatusName((item.getApproved()?"审批通过":"审批不通过"));
                }else{
                    if(selected!=null && selected.getApprovalStatus()!=null){
                        if("1".equals(selected.getApprovalStatus())){
                            item.setApprovalStatusName("未审批");
                        }else if("5".equals(selected.getApprovalStatus())){
                            List<MultiLevelApproval> multiLevelApprovals = stringListHashMap.get(item.getPartsType());
                            if(!CollectionUtils.isEmpty(multiLevelApprovals) && multiLevelApprovals.size()==1){
                                item.setApprovalStatusName("未审批");
                            }else{
                                item.setApprovalStatusName("审批中");
                            }
                        }else{
                            item.setApprovalStatusName("未提交");
                        }
                    }
                }
            });
        }

        return workOrderDetails;
    }

    public boolean isOwner() {
        if (null != selected.getRepairPersonId() && selected.getRepairPersonId().compareTo(loginUser.getId()) == 0) {
            return true;
        }
        return false;
    }

    public List<MultiLevelApproval> getApproveList() {
        return approveList;
    }

    public void setApproveList(List<MultiLevelApproval> approveList) {
        this.approveList = approveList;
    }

    public List<V2_WorkOrder_Detail_Return> getWorkOrderDetailReturn(String woId) {
        if(v2WorkOrderDetailReturnList == null){
            v2WorkOrderDetailReturnList = woService.getWorkOrderDetailReturn(woId);
        }
        return v2WorkOrderDetailReturnList;
    }

    public List<V2_WorkOrder_Detail_Return> getWorkOrderDetailReturnView(String woId) {
        return  woService.getWorkOrderDetailReturn(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return woService.getWorkOrderCoworker(woId);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String woId) {
        return selected == null ? null : woService.getServiceRequestMessageList(woId);
    }
    public void saveMsg(){
        woService.saveServiceRequestMessage(newMessage, loginUser, selected.getId());
        newMessage = "";
    }

    public void prepareActSetting(String woid){
        prepareAct(woid);
        V2_WorkOrder temp=new V2_WorkOrder();
        BeanUtils.copyProperties(selectedWorkOrder,temp);
        setSelectedWorkOrder(temp);
        String  key= "repairTypeDefaultValueConfiguration";
        String repairTypeValue = configItemValueRepository.findByKeyAndSiteForUseParent(temp.getSiteUID(), temp.getHospitalUID(), temp.getInstitutionUID(), temp.getTenantUID(), key);
        if(StringUtils.isNumeric(repairTypeValue)){
            selectedWorkOrder.setRepairType(Integer.valueOf(repairTypeValue));
        }
    }

    public void prepareActCancel(String woId) {
        selectedWorkOrder = dao.findById(woId);
        // 维修中
        if (selectedWorkOrder.getStatus() == 1 && selectedWorkOrder.getCurrentStepId() == 4) {
            if (multiLevelApprovalRepository.getIncompleteApprovalBusiness(woId) > 0) {
                RequestContext requestContext = RequestContext.getCurrentInstance();
                requestContext.addCallbackParam("approvalProcessIsComplete", false);
                WebUtil.addErrorMessage("当前工单有备件审批未完成，请完成后再取消工单！");
                return;
            }
        }
        prepareAct(woId);
    }

    public void prepareAct(String woId) {
        selectedWorkOrder = dao.findById(woId);
        currentStatus = selectedWorkOrder.getAssetInfo().getStatus();
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        selectedApproverUid = null;
        Long time = System.currentTimeMillis();
        time += 120*1000*60;
        esTime = new Date(time);


    }

    public boolean isStep(V2_WorkOrder workOrder){
        if(workOrder==null){
            return false;
        }
        //2-待派工/3-已派工
        if(workOrder.getCurrentStepId()==2 ||workOrder.getCurrentStepId()==3 ||workOrder.getCurrentStepId()==4 ){
            return false;
        }else{
            return true;
        }

    }

    public void prepareNewWorkOrder(String woid) {
        prepareAct(woid);
        picList = new ArrayList<V2_BlobObject>();
        attachList = new ArrayList<V2_BlobObject>();
    }

    public void prepareAproval(V2_WorkOrder v2WorkOrder) {
        submitForApproveCost(v2WorkOrder);
        prepareAct(v2WorkOrder.getId());
        selectedMap = new HashMap<>();
        approvedDetails = getWorkOrderDetail(selectedWorkOrder.getId());
        TenantInfo tenantInfo = cacheService.getTenantByUid(loginUser.getTenantUID());
        if(enableAuditGroupConfiguration){
            List<V2_WorkOrder_Detail> approvedDetailList = new ArrayList<>();
            approvedDetails.stream().forEach(item -> {
                if (item.getApproved() == null || !item.getApproved()) {
                    item.setApproved(false);
                    selectedMap.put(item.getId(), item.getApproved());
                    approvedDetailList.add(item);
                }
            });
            approvedDetails = new ArrayList<>();
            approvedDetails.addAll(approvedDetailList);
        }else {
            approvedDetails.stream().forEach(item -> {
                if (item.getApproved() == null) {
                    item.setApproved(false);
                }
                selectedMap.put(item.getId(), item.getApproved());
            });
        }
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
        if(userContextService.isCountyArea()){
            v2WorkOrderDetailReturnList = woService.getWorkOrderDetailReturn(woId);
        }
    }
    public void prepareEditCloseOrder(String woId) {
        prepareAct(woId);
        picList = new ArrayList<V2_BlobObject>();
        attachList = new ArrayList<V2_BlobObject>();
        equipmentTaker = selectedWorkOrder.getServiceRequest().getEquipmentTaker();
        takeTime = selectedWorkOrder.getServiceRequest().getTakeTime();
    }

    Boolean isSave = Boolean.FALSE;

    public void saveEditedWorkOrder(){
        //故障类型必填校验
        if (requireCaseType() && null == selectedWorkOrder.getCaseType()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("caseType") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("caseType", true);
            return;
        }
        if(!picList.isEmpty()){
            String srId = selectedWorkOrder.getSrId();
            for (V2_BlobObject bo : picList) {
                bo.setBoUuid(srId);
                bo.setBoType(V2_BlobObject.BO_TYPE_WorkOrder);
            }
            blobObjectRepository.save(picList);
        }
        if(!attachList.isEmpty()) {
            String srId = selectedWorkOrder.getSrId();
            for (V2_BlobObject bo : attachList) {
                bo.setBoUuid(srId);
                bo.setObjectType(V2_BlobObject.BO_SUB_TYPE_DOCS);
            }
            blobObjectRepository.save(attachList);
        }
        if (ObjectUtil.isNotEmpty(takeTime)) {
            isSave = Boolean.TRUE;
            takeEquip();
        }
        dao.save(selectedWorkOrder);
    }

    private Boolean hourRequired = false;


    public void prepareCompleteOrder(String woid) {
        approveFlag =false;
        selectedWorkOrder = dao.findById(woid);
        MultiLevelApprovalConfig approval = multiLevelApprovalService.checkMultiLevelApprovalPersonAndSubmit(selectedWorkOrder);
        hasConfigMultiLevelApproval = false;
        if (approval != null) {
            logger.info("维修-----------多级审核已激活");
            hasConfigMultiLevelApproval = true;
        }

        acker=woService.getAckerList(selectedWorkOrder.getId());
        acceptPerson = acker == null ? null : acker.get(0);
        //如果子状态为1 说明是等待备件 需要显示日期

        if(selectedWorkOrder.getSubStatus()!=null && selectedWorkOrder.getSubStatus()==1){
            displayTime=true;
        }
        Integer userId = selectedWorkOrder.getAckId();
        if( userId!=null){
            ua1 = userDao.getById(userId);
        }else{
            ua1 = userDao.getById(selectedWorkOrder.getServiceRequest().getRequestorId());

        }
        //如果是远东本地版推过来的mvs工单，因为ServiceRequest中RequestorId为本地版的数据，所以ua1为空那么久默认取当前登录人
        if("3".equals(selectedWorkOrder.getServiceRequest().getExternalWorkOrder())){
            ua1 = loginUser;
        }
        //工时类型从配置下读取 YHAAO-25807
        String result = selectedWorkOrder.getRepairResult();
        if(result!=null) {
            if (result.equalsIgnoreCase("2")||result.equalsIgnoreCase("4")) {
                currentStatus = 3;
            }
            if (result.equalsIgnoreCase("3")) {
                currentStatus = 4;
            }

            if (result.equalsIgnoreCase("1")) {
                currentStatus = 1;
            }
        }

        //获取当前工单的所属流程配置
        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                selectedWorkOrder.getAssetId());
        hourRequired = Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("hourRequired")).orElse(false);

        if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() == 0.0) {
            selectedWorkOrder.setManHours(null);
        }
        confirmedUpDate = selectedWorkOrder.getServiceRequest().getConfirmedUpTime();
        statusStartTime=selectedWorkOrder.getStatusStartTime();
        statusCloseTime=selectedWorkOrder.getStatusCloseTime();
        accessoryList = getWorkOrderDetail(selectedWorkOrder.getId());
        coWorkerList = getWorkOrderCoworker(selectedWorkOrder.getId());
        picList = woService.getAttachByBoSubType(selectedWorkOrder.getSrId(), V2_BlobObject.BO_SUB_TYPE_IMG);
        attachList = woService.getAttachByBoSubType(selectedWorkOrder.getSrId(), V2_BlobObject.BO_SUB_TYPE_DOCS);
        workHoursList = getWorkOrderManHoursDetail(selectedWorkOrder.getId());
        enableEngineersWriteRepairOutside =Boolean.TRUE.toString().equalsIgnoreCase(userContextService.getConfigItemValue("enableEngineersWriteRepairOutside"));
        if(enableEngineersWriteRepairOutside){
            initExternalTempUser(selectedWorkOrder);
            if(ObjectUtil.isEmpty(externalTempUser)){
                externalTempUser = new ExternalTempUser();
                externalTempUser.setId("000");
            }
        }
        workHoursConfig();
    }

    private void workHoursConfig() {
        String key = "repairEntryTypeSet";
        String repairEntryTypeSet = configItemValueRepository.findByKeyAndSiteForUseParent(selectedWorkOrder.getSiteUID(), selectedWorkOrder.getHospitalUID(), selectedWorkOrder.getInstitutionUID(), selectedWorkOrder.getTenantUID(), key);
        if (StrUtil.isNotEmpty(repairEntryTypeSet)) {
            Integer oldCoworkerEntryType = selectedWorkOrder.getCoworkerEntryType();
            if (selectedWorkOrder.getEntryType() == null) {
                selectedWorkOrder.setEntryType(Integer.parseInt(repairEntryTypeSet));
            } else {
                if (Integer.parseInt(repairEntryTypeSet) == 2 || Integer.parseInt(repairEntryTypeSet) == 3) {
                    selectedWorkOrder.setEntryType(Integer.parseInt(repairEntryTypeSet));
                }
            }

            if (selectedWorkOrder.getCoworkerEntryType() == null) {
                selectedWorkOrder.setCoworkerEntryType(Integer.parseInt(repairEntryTypeSet));
            } else {
                if (Integer.parseInt(repairEntryTypeSet) == 2 || Integer.parseInt(repairEntryTypeSet) == 3) {
                    selectedWorkOrder.setCoworkerEntryType(Integer.parseInt(repairEntryTypeSet));
                }
                if ((oldCoworkerEntryType == 0 && Integer.parseInt(repairEntryTypeSet) == 3) || (oldCoworkerEntryType == 1 && Integer.parseInt(repairEntryTypeSet) == 2)) {
                    coWorkerList = new ArrayList<>();
                }
            }

        }

    }

    public List<V2_WorkOrderManHoursDetail> getWorkOrderManHoursDetail(String woId) {
        return woService.getWorkOrderManHoursDetail(woId);
    }

    //故障类型是否必填
    public Boolean requireCaseType(){
        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                selectedWorkOrder.getAssetId());
        return Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data"))
                .map(data -> (Boolean) data.get("isCasetypeRequired")).orElse(false);
    }

    // 走多级审批流,提交审核
    public void submitForApproval() throws Exception {
        String woId = selectedWorkOrder.getId();
        saveRepairWorkOrder();
        selectedWorkOrder=woService.getWorkOrderById(woId);
        if(selectedWorkOrder == null){
            return;
        }
        List<V2_WorkOrder_Detail> v2WorkOrderDetails = new ArrayList<>();

        BigDecimal totalPriceToBigDecimal = BigDecimal.ZERO;
        if(accessoryList!=null && !accessoryList.isEmpty()){
            for (V2_WorkOrder_Detail detail:accessoryList) {
                if(detail.getApproved()==null || !detail.getApproved()){
                    BigDecimal bigDecimal = new BigDecimal(detail.getClosedPrice()==null?0:detail.getClosedPrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    totalPriceToBigDecimal = totalPriceToBigDecimal.add(bigDecimal);
                    detail.setApproved(null);
                    v2WorkOrderDetails.add(detail);
                }
            }
        }
        selectedWorkOrder.setTotalPriceToBigDecimal(totalPriceToBigDecimal);


        HashMap<String,Object> map = multiLevelApprovalService.getMultiLevelApprovalConfigs(selectedWorkOrder);
        boolean isApprovalAll = (boolean) map.get("isApprovalAll");
        List<MultiLevelApprovalConfig>  multiLevelApprovalConfigs = (List<MultiLevelApprovalConfig>) map.get("list");
        if(!isApprovalAll &&  !CollectionUtils.isEmpty(multiLevelApprovalConfigs)  ){
           //存在有部分审批流
            WebUtil.addErrorMessage("审批流发起失败,请配置审批流!");
            return ;
        }
        if(!isApprovalAll &&  CollectionUtils.isEmpty(multiLevelApprovalConfigs)  ){
            //没有匹配的审批流走老逻辑
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("checkApproval", true);
            return ;
        }

        MultiLevelApproval approval = multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmitWorkOrder(selectedWorkOrder, null, false);
        if (approval == null) {
            WebUtil.addErrorMessage("审批流发起失败,请配置审批流!");
            return ;
        }
        selectedWorkOrder.setApprovalStatus(WorkOrderConstant.APPROVAL_STATUS_PENDING);
        dao.save(selectedWorkOrder);
        v2WorkOrderDetails.forEach(item->item.setV2WorkOrder(null));
        v2WorkOrderDetailRepository.save(v2WorkOrderDetails);


        WebUtil.addErrorMessage("审批发起成功!");
        return ;

    }

    //  提交审核前的计算
    private  void submitForApproveCost(V2_WorkOrder v2WorkOrder){
        accessoryList = getWorkOrderDetail(v2WorkOrder.getId());
        coWorkerList = getWorkOrderCoworker(v2WorkOrder.getId());

        ResponseEntity<LinkedHashMap> reponseBody = serviceRequestApiService.invokeWorkFlowConfig(token,
                v2WorkOrder.getAssetId());
        c_woManhourPrice=Optional.ofNullable(reponseBody).map(ResponseEntity::getBody).map(body -> (Map) body.get("data")).map(data -> (Double) data.get("woManhourPrice")).orElse(new Double(0));
        Double tHours = coWorkerList.stream().mapToDouble(p->p.getManHours()).sum();
        //维修人工费用=工单人工费用（元/小时）*总计工时（“工单人工费用”需在流程配置项中配置），若未配置则按照0计算
        //总计工时=维修工程师工时+协作者工时
        if(v2WorkOrder.getManHours()==null)
            v2WorkOrder.setManHours(new Double(0.0));
        c_repairManCost=c_woManhourPrice*(tHours+v2WorkOrder.getManHours());
        //备件费用
        Double accPrice=accessoryList.stream().mapToDouble(p->p.getClosedPrice()).sum();
        //维修总金额=备件费用+维修人工费用+外部费用+其他费用
        if(v2WorkOrder.getOtherExpense()==null){
            v2WorkOrder.setOtherExpense(0.0);
        }
        if(v2WorkOrder.getExternalExpense()==null){
            v2WorkOrder.setExternalExpense(0.0);
        }
        c_repairTotalCost = accPrice+c_repairManCost+v2WorkOrder.getExternalExpense()+v2WorkOrder.getOtherExpense();
    }

    public void saveRepairWorkOrder() throws Exception{
        assigneeId =ua1.getId().toString();
        if (hourRequired) {
            if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() < 0.01) {
                WebUtil.addErrorMessage(WebUtil.getMessage("TheCompletedManhoursTips"));
                RequestContext.getCurrentInstance().addCallbackParam("manHours", true);
                return;
            }
        }

        if (selectedWorkOrder.getEntryType() == 1 || selectedWorkOrder.getEntryType() == 3) {
            workHoursList = workHoursList.stream()
                    .filter(item -> !"000".equals(item.getId()))
                    .collect(Collectors.toList());
            if (workHoursList == null || workHoursList.size() == 0) {
                WebUtil.addSuccessMessage("增加工时段记录不能为空");
                return;
            }
            workHoursList.forEach(item -> item.setCreatedDate(null));
            selectedWorkOrder.setWorkHoursList(workHoursList);
        }

        V2_WorkOrder_Detail tmp = new V2_WorkOrder_Detail();
        List<V2_WorkOrder_Detail> tmpList = new ArrayList<>();
        tmpList.addAll(accessoryList);
        for (V2_WorkOrder_Detail vwd:accessoryList) {
            if(vwd.getId().equalsIgnoreCase("000")){
                tmp=vwd;
            }
        }
        tmpList.remove(tmp);
        accessoryList= tmpList;
        /* accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));*/
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        if (!attachList.isEmpty())
            picList.addAll(attachList);
        updateSelectedWorkOrder(selectedWorkOrder);
        woService.saveRepairWorkOrder(selectedWorkOrder, assigneeId,confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList.stream().filter(ite-> V2_BlobObject.BO_TYPE_WorkOrder.equals(ite.getBoType())).collect(Collectors.toList()));
        cancel();
    }

    private void updateSelectedWorkOrder(V2_WorkOrder v2_workOrder){
        if(subStatus==ApmConstant.SUBSTATUS1) {
            selectedWorkOrder.setStatus(ApmConstant.SUBSTATUS1);
            selectedWorkOrder.setStatusStartTime(statusStartTime);
            selectedWorkOrder.setStatusCloseTime(statusCloseTime);
        }
        if(subStatus==ApmConstant.SUBSTATUS2) {
            selectedWorkOrder.setStatus(ApmConstant.SUBSTATUS2);
        }
        if(subStatus==ApmConstant.SUBSTATUS3) {
            selectedWorkOrder.setStatus(ApmConstant.SUBSTATUS3);
        }
        if(ObjectUtil.isNotEmpty(externalTempUser) && ObjectUtil.isNotEmpty(externalTempUser.getWoId())){
            selectedWorkOrder.setExternalTempUser(externalTempUser);
        }
    }

    public List<UserAccount> getWorkerList() {
        return woService.getWorkerList(selectedWorkOrder.getAssetId());
    }
    //待验收
    public List<UserAccount> getAcker() {
        return woService.getAckerList(selectedWorkOrder.getId());
    }

    public void ackWorkOrder() {
        woService.ackWorkOrder(selectedWorkOrder);
        cancel();
    }

    public void batchAckWorkOrder() {
        selectedBatchMap.forEach((k, v) -> {
            if (v) {
                woService.ackWorkOrder(dao.findById(k));
            }
        });
        selectedBatchMap.clear();
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

    public void batchRejectWorkOrder() {
        selectedBatchMap.forEach((k, v) -> {
            if (v) {
                woService.rejectWorkOrder(dao.findById(k), failReason, currentStatus, confirmedDownTime);
            }
        });
        selectedBatchMap.clear();
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
        ConfigItemValue configItemValue = configItemMapper.findByKey("enableCheckInTime", selectedWorkOrder.getSiteUID());
        if (ObjectUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue.getValue()) && ObjectUtil.isEmpty(selectedWorkOrder.getCheckinTime())) {
            WebUtil.addErrorMessage("签到时间不能为空！");
            RequestContext.getCurrentInstance().addCallbackParam("checkin_time", true);
            valid = false;
        }
        if (confirmedUpDate ==
                null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("recoveryTime") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("confirmedUpDate", true);
            valid = false;
        }else if(this.confirmedUpDate.getTime() < selectedWorkOrder.getAcceptStepStartTime().getTime()){
            // 恢复时间不能小于接单时间
            WebUtil.addErrorMessage(WebUtil.getMessage("RepairRecoveryTimeCheck"));
            RequestContext.getCurrentInstance().addCallbackParam("confirmedUpDate", true);
            valid = false;
        }
        if (requireCaseType() && null == selectedWorkOrder.getCaseType()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("caseType") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("caseType", true);
            valid = false;
        }
        if (null == selectedWorkOrder.getRepairResult()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("RepairResult") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("repairResult", true);
            valid = false;
        }
        if(userContextService.isCountyArea() && !whetherCheckIn()){ //关单前县域租户备件必须签到
            WebUtil.addErrorMessage(WebUtil.getMessage("NotSignedInTips"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            RequestContext.getCurrentInstance().addCallbackParam("approvalMust", true);
            valid = false;
        };

        WorkflowConfig woc = configService.findConfigBySite(loginUser.getSiteUID());
        /*hourRequired=  woc.getHourRequired();*/
        if(null!=woc) {
            if(woc.getPatProblems()!=null && woc.getPatProblems() && selectedWorkOrder.getPatProblems()==null) {
                WebUtil.addErrorMessage("原因描述不能为空！");
                RequestContext.getCurrentInstance().addCallbackParam("pat_problems", true);
                valid = false;
            }
            if(woc.getPatActions()!=null && woc.getPatActions() && selectedWorkOrder.getPatActions()==null) {
                WebUtil.addErrorMessage("解决方案不能为空！");
                RequestContext.getCurrentInstance().addCallbackParam("pat_actions", true);
                valid = false;
            }
            if(woc.getPatTests()!=null && woc.getPatTests() && selectedWorkOrder.getPatTests()==null) {
                WebUtil.addErrorMessage("交付前检测不能为空！");
                RequestContext.getCurrentInstance().addCallbackParam("pat_tests", true);
                valid = false;
            }
            if(woc.getWorkOrderImagesUpload()!=null && woc.getWorkOrderImagesUpload() && CollectionUtils.isEmpty(picList)) {
                WebUtil.addErrorMessage("请上传维修图片！");
                RequestContext.getCurrentInstance().addCallbackParam("imges", true);
                valid = false;
            }
        }
        if (hourRequired) { //如果工作流配置了工时必填，那么就要jian
            if (null == selectedWorkOrder.getManHours() || selectedWorkOrder.getManHours() < 0.01) {
                WebUtil.addErrorMessage(WebUtil.getMessage("TheCompletedManhoursTips"));
                RequestContext.getCurrentInstance().addCallbackParam("manHours_input", true);
                valid = false;
            }
        } else if (selectedWorkOrder.getManHours() != null && selectedWorkOrder.getManHours() == 0.0) {
            selectedWorkOrder.setManHours(null);
        }

        //判断如果工单分类必填
        String value = configItemValueRepository.findByKeyAndSiteForUseParent(currentUserOrgInfo.getSiteUID(), currentUserOrgInfo.getHospitalUID(), currentUserOrgInfo.getInstitutionUID(), currentUserOrgInfo.getTenantUID(), "showWorkOrderType");
        if(StrUtil.isNotEmpty(value)){
            FieldColumnDto fieldColumnDto = JSON.parseObject(value, FieldColumnDto.class);
            if(fieldColumnDto.getRequired()){
                if(StrUtil.isEmpty(selectedWorkOrder.getWorkOrderType())){
                    WebUtil.addErrorMessage(WebUtil.getMessage(fieldColumnDto.getRequiredMessage()));
                    RequestContext.getCurrentInstance().addCallbackParam("workOrderType", true);
                    valid = false;
                }
            }
        }
        if(enableEngineersWriteRepairOutside && !accessoryList.isEmpty()){
            boolean  externalPurchase = accessoryList.stream().anyMatch(item -> "3".equals(item.getPartsType()));
            if(externalPurchase){ // 外修采购
                if(ObjectUtil.isNotEmpty(externalTempUser) && ObjectUtil.isEmpty(externalTempUser.getWoId())){
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    RequestContext.getCurrentInstance().addCallbackParam("approvalMust", true);
                    WebUtil.addSuccessMessage("增加外修人记录不能为空");
                    valid = false;
                }
            }
        }

        if (!valid) {
            return;
        }
        assigneeId =ua1.getId().toString();

        //gl:1表示是正常
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));

        if (selectedWorkOrder.getEntryType() == 1) {
            workHoursList = workHoursList.stream()
                    .filter(item -> !"000".equals(item.getId()))
                    .collect(Collectors.toList());
            if (workHoursList == null || workHoursList.size() == 0) {
                WebUtil.addSuccessMessage("增加工时段记录不能为空");
                return;
            }
            workHoursList.forEach(item -> item.setCreatedDate(null));
            selectedWorkOrder.setWorkHoursList(workHoursList);
        }
        //将pdf的文件一起合并到图片的list中传给后台
        picList.addAll(attachList);
        //更新维修子状态
        updateSelectedWorkOrder(selectedWorkOrder);
        woService.repairWorkOrder(selectedWorkOrder,assigneeId, confirmedUpDate, currentStatus, accessoryList, coWorkerList, comments, picList);
        saveCountyAreaPartReturnInfo();
        cancel();
    }

    /**
     * 保存县域租户待归还备件信息
     */
    public void saveCountyAreaPartReturnInfo(){
        if(accessoryList != null && userContextService.isCountyArea()){
            V2_WorkOrder v2WorkOrder = dao.findById(selectedWorkOrder.getId());
            if(v2WorkOrder.getCurrentStepId()>4){
                for(V2_WorkOrder_Detail v2_workOrder_detail:accessoryList){
                    for(int i=0;i< v2_workOrder_detail.getApartsQuantity();i++){
                        V2_WorkOrder_Detail_Return v2WorkOrderDetailReturn = new V2_WorkOrder_Detail_Return();
                        v2WorkOrderDetailReturn.setWoId(v2_workOrder_detail.getWoId());
                        v2WorkOrderDetailReturn.setWorkOrderDetailId(v2_workOrder_detail.getId());
                        v2WorkOrderDetailReturn.setPartsItemId(v2_workOrder_detail.getPartsItemId());
                        v2WorkOrderDetailReturn.setPartName(v2_workOrder_detail.getParts());
                        v2WorkOrderDetailReturn.setReturnNum(1);
                        v2WorkOrderDetailReturn.setSerialNum(v2_workOrder_detail.getSerialNum());
                        v2WorkOrderDetailReturn.setCreatedDate(new Date());
                        v2WorkOrderDetailReturn.setCreatedBy(v2_workOrder_detail.getCreatedBy());
                        detailReturnDao.save(v2WorkOrderDetailReturn);
                    }
                }
            }
        }
    }

    public boolean whetherCheckIn(){
        V2_WorkOrder workOrder = dao.findById(selectedWorkOrder.getId());
        if(workOrder != null){
            Integer checkin = workOrder.getCheckin();
            return  checkin==null?false:checkin == 1;
        }
        return false;
    }
    public void cancelWorkOrder() {
//tbc: cancelReason assetStatus
        //  assetStatus=  assetDao.getById(selectedWorkOrder.getAssetId()).getStatus().toString();
        woService.cancelWorkOrder(selectedWorkOrder.getId(), cancelReason, assetStatus);

        cancelReason = null;
        cancel();
    }

    public void takeEquip() {
        if (Strings.isNullOrEmpty(equipmentTaker)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("equipmentTaker") + WebUtil.getMessage("ValidationRequire"));
        } else if (equipmentTaker.trim().isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("equipmentTaker") + WebUtil.getMessage("ValidationRequire"));
        } else if (takeTime != null && takeTime.after(new Date())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("TakingTimeIsGreaterThanCurrentTimeTip"));
            takeTime = null;
        } else {
            if (Boolean.FALSE.equals(isSave)) {
                takeTime = new Date();
            } else {
                isSave = Boolean.FALSE;
            }
            woService.takeEquip(selectedWorkOrder, equipmentTaker, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(takeTime));
        }
    }

    public void closeWorkOrder() {
        //故障类型必填校验
        if (requireCaseType() && null == selectedWorkOrder.getCaseType()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("caseType") + WebUtil.getMessage("ValidationRequire"));
            RequestContext.getCurrentInstance().addCallbackParam("caseType", true);
            return;
        }

        if(userContextService.isCountyArea()){//关单前县域租户备件必须全部归还
            if(v2WorkOrderDetailReturnList != null){
                for(V2_WorkOrder_Detail_Return v2WorkOrderDetailReturn:v2WorkOrderDetailReturnList){
                    if(v2WorkOrderDetailReturn.getReturnType() == null){
                        WebUtil.addErrorMessage(WebUtil.getMessage("countyAreaPartReturnError"));
                        RequestContext.getCurrentInstance().addCallbackParam("caseType", true);
                        return ;
                    }
                }
            }

        }
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
        //从配置信息获取紧急程度
        CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        //从配置信息获取当前状态
        String strPriority =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].priority.defaultValue[0]").toString();
        String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
        newServiceRequest = new V2_ServiceRequest();
        Object requestReason=JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.defaultValue[0]");


        if(requestReason==null){
            newServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }else{
            newServiceRequest.setRequestReason(requestReason.toString());
        }

        Object repairReportSendPlaceFlag =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.enable[0]");
        if(repairReportSendPlaceFlag != null){
            repaFlag= Boolean.valueOf(repairReportSendPlaceFlag.toString());
        }
        Object repairReportSendPlace=JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.defaultValue[0]");

        newServiceRequest = new V2_ServiceRequest();
        picList = new ArrayList();
        newServiceRequest.setCasePriority(Integer.valueOf(strPriority));//紧急程度默认是设置为一般
        currentStatus= Integer.valueOf(strAssetStatus);
        newServiceRequest.setStatus(currentStatus);
        newServiceRequest.setRequestorId(loginUser.getId());
        newServiceRequest.setFromDeptId(loginUser.getOrgInfoId());
        newServiceRequest.setRequestorName(loginUser.getName());
        newServiceRequest.setRequestorPhone(loginUser.getTelephone());
        newServiceRequest.setRequestTime(new Date());
        if(repairReportSendPlace != null){
            newServiceRequest.setRepairReportSendPlace(repairReportSendPlace.toString());
        }
        if(selectedAsset != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(selectedAsset.getSiteUID());
            newServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newServiceRequest.setHospitalName(orgInfo.getName());//这里只需要开单页面显示，asset微服务会保存医院Uid和名称。
        }
        faultReason=WebUtil.getMessage("EquipmentFault");
        selected = null;
    }
    public void createBatchServiceRequest() {

        cancel();

        if (!Strings.isNullOrEmpty(requestAssetUid)) {
            selectedAsset = assetDao.findByUid(requestAssetUid);
            onSelectedAsset(selectedAsset);
            currentStatus = selectedAsset.getStatus();
            if (currentStatus == 1) {
                currentStatus = 3;
            }
        }
        //从配置信息获取紧急程度
        CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        //从配置信息获取当前状态
        String strPriority =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].priority.defaultValue[0]").toString();
        String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
        newServiceRequest = new V2_ServiceRequest();
        Object requestReason=JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.defaultValue[0]");

        if(requestReason==null){
            newServiceRequest.setRequestReason(WebUtil.getMessage("FailedToMeasure"));
        }else{
            newServiceRequest.setRequestReason(requestReason.toString());
        }

        newServiceRequest = new V2_ServiceRequest();
        picList = new ArrayList();
        newServiceRequest.setCasePriority(Integer.valueOf(strPriority));//紧急程度默认是设置为一般
        currentStatus= Integer.valueOf(strAssetStatus);
        newServiceRequest.setStatus(currentStatus);
        newServiceRequest.setRequestorId(loginUser.getId());
        newServiceRequest.setFromDeptId(loginUser.getOrgInfoId());
        newServiceRequest.setRequestorName(loginUser.getName());
        newServiceRequest.setRequestTime(new Date());
        if(selectedAsset != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(selectedAsset.getSiteUID());
            newServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newServiceRequest.setHospitalName(orgInfo.getName());//这里只需要开单页面显示，asset微服务会保存医院Uid和名称。
        }
        faultReason=WebUtil.getMessage("FailedToMeasure");
        selected = null;
    }
    public void saveBatchServiceRequest(){
        if(assetInfos!=null && !assetInfos.isEmpty()){
            for (int i = 0; i < assetInfos.size(); i++) {
                AssetInfo assetInfo = assetInfos.get(i);
                WorkflowConfig woc = configService.findConfigBySite(assetInfo.getSiteUID());
                if (woc != null && woc.getIsWoSupplierAutoEnabled() !=null && woc.getIsWoSupplierAutoEnabled()) {
                    this.saveServiceRequestSupplier();
                }
                if(StringUtils.isEmpty(faultReason)){
                    newServiceRequest.setRequestReason(WebUtil.getMessage("FailedToMeasure"));
                }else{
                    newServiceRequest.setRequestReason(faultReason);
                }

                if (assetInfo.getStatus() == 4) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("ToBeTerminatedCannotBeRequestedForRepairTip"));
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("validationFailed", true);
                } else if (Strings.isNullOrEmpty(assetInfo.getClinicalDeptUID())) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("NoAssetDepartmentNoRepaired"));
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("validationFailed", true);
                } else {
                    assetInfo.setStatus(currentStatus);
                    onSelectedAsset(assetInfo);
                    newServiceRequest.setAssetLocation(locationName);
                    if (i==1 && picList!=null && !picList.isEmpty()){
                        for (V2_BlobObject blobObject : picList){
                            blobObject.setId(null);
                        }
                    }
                    newServiceRequest.setSrRepairSource(RepairSourceEnum.Measurement.name());

                    woService.createServiceRequest(newServiceRequest, assetInfo, picList,null);

                }
            }
            assetInfos=null;
        }else {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoEquipmentSelectedNoRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        cancel();
        String operation= "/portal/measuring/measuringResultMain.xhtml?faces-redirect=true";
        WebUtil.navigateTo(operation);
    }

    public boolean repairTip(V2_WorkOrder  curWorkOrder) {
        boolean disable = true;
        String repairTipMessage = "";
        if (enableMaintenanceContract) {
            AssetInfo curAsset = curWorkOrder.getAssetInfo();
            List<ContractInfo> contractInfos = contractInfoRepository.findByAssetUidAndRequestTime(curAsset.getUid());
            if (ObjectUtil.isEmpty(contractInfos)) {
                repairTipMessage += WebUtil.getMessage("notFoundMaintenanceContract") + "，";
                disable = false;
            }
        }
        if (enableMaintenanceDate) {
            AssetInfo curAsset = curWorkOrder.getAssetInfo();
            Date warrantyDate = curAsset.getWarrantyDate();
            boolean dateDisable = Optional.ofNullable(warrantyDate).map(date -> DateUtil.now().before(date)).orElse(false);
            if (!dateDisable) {
                repairTipMessage += WebUtil.getMessage("warrantyDateExpired") + "，";
                disable = false;
            }
        }
        repairTipMessage += WebUtil.getMessage("confirmRepair") + "！";
        repairTipMessageMap.put(curWorkOrder.getId(), repairTipMessage);
        return disable;
    }

    public void saveServiceRequest() {
        if(userContextService.isCountyArea()){
            String msg = null;
            if(newServiceRequest.getSiebleNumber() != null && newServiceRequest.getSiebleNumber().length() != 13){
                msg = WebUtil.getMessage("siebleNumber")+"长度必须填为13位";
            }
            if(newServiceRequest.getCallCenterNumber() != null && newServiceRequest.getCallCenterNumber().length() != 10){
                msg = WebUtil.getMessage("callCenterNumber")+"长度必须填为10位";
            }
            if(StringUtils.isNotBlank(msg)){
                WebUtil.addErrorMessage(msg);
                //前端弹窗不退出并提示错误信息
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
        if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoEquipmentSelectedNoRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        WorkflowConfig woc = configService.findConfigBySite(selectedAsset.getSiteUID());
        if (woc != null && woc.getIsWoSupplierAutoEnabled() !=null && woc.getIsWoSupplierAutoEnabled()) {
            this.saveServiceRequestSupplier();
        }
        if(StringUtils.isEmpty(faultReason)){
            newServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }else{
            newServiceRequest.setRequestReason(faultReason);
        }

        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ToBeTerminatedCannotBeRequestedForRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoAssetDepartmentNoRepaired"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(locationName);
            newServiceRequest.setSrRepairSource(RepairSourceEnum.Accounts.name());
            woService.createServiceRequest(newServiceRequest, selectedAsset, picList,null);
            cancel();
        }
    }


    public void saveServiceRequestSupplier() {
        if(StringUtils.isEmpty(faultReason)){
            newServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }else{
            newServiceRequest.setRequestReason(faultReason);
        }
        if(selectedAsset.getSupplierNewUID() == null){
            WebUtil.addErrorMessage("设备未关联供应商,无法使用供应商派工");
            return;
        }
        Map<String, Object> map = woService.findSupplierFEByAssetUid(selectedAsset);
        List<UserAccount>  data = (List<UserAccount>)map.get("data");
        if(ObjectUtils.isEmpty(data)){
            WebUtil.addErrorMessage("设备未绑定供应商维修工程师");
            return;
        }
        int size = data.size();
        if(size>1){
            WebUtil.addErrorMessage("设备绑定工程师过多");
            return;
        }
        if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoEquipmentSelectedNoRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
            return;
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ToBeTerminatedCannotBeRequestedForRepairTip"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoAssetDepartmentNoRepaired"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(locationName);
            newServiceRequest.setSrRepairSource(RepairSourceEnum.Accounts.name());

            woService.createServiceRequest(newServiceRequest, selectedAsset, picList,2);
            cancel();
        }
    }

    private void onSelectedAsset(AssetInfo asset) {
        String lName ="";
        /*final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
        OrgInfo org = cacheService.findOrgByUid(orgInfoUID);*/
        if(StringUtils.isNotEmpty(asset.getAssetDeptUID())){
            OrgInfo org = cacheService.findOrgByUid(asset.getAssetDeptUID());
            lName= org.getName();
        }else{
            lName= asset.getClinicalDeptName();
        }
        asset.setLocationName(lName);
        locationName = lName;

        if(newServiceRequest != null && userContextService.isCountyArea()){
            OrgInfo orgInfo = cacheService.findOrgByUid(asset.getSiteUID());
            newServiceRequest.setCustomerName(orgInfo.getContactPerson());
            newServiceRequest.setCustomerTel(orgInfo.getContactPhone());
            newServiceRequest.setHospitalName(orgInfo.getName());
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

        if(customConfig ==null){
            if (currentStatus == AssetConstant.ASSET_STATUS_NORMAL
                    || currentStatus == AssetConstant.ASSET_STATUS_TO_TERMINATED
                    || currentStatus == AssetConstant.ASSET_STATUS_STOP) {
                currentStatus = AssetConstant.ASSET_STATUS_ILL;
            }
            needConfirmTime = false;
        }else{ //如果有自定义配置
            String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
            currentStatus= Integer.valueOf(strAssetStatus);
            if(currentStatus == AssetConstant.ASSET_STATUS_ILL) //限制功能使用
                needConfirmTime=false;
            if(currentStatus == AssetConstant.ASSET_STATUS_DOWN)//停机
                needConfirmTime=true;
        }


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
        if(currentStatus==2){ //选择停机状态
            needConfirmTime=true;
        }
        if(currentStatus==3 || currentStatus==1){//选择 限制功能使用 或可正常使用
            needConfirmTime=false;
        }

        /*if (null != selectedAsset) {//by hai bin gan
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }*/
    }
    public void onBatchStatusChange() {
        if (assetInfos == null) {
            return;
        }
        if(currentStatus==2){ //选择停机状态
            needConfirmTime=true;
        }
        if(currentStatus==3 || currentStatus==1){//选择 限制功能使用 或可正常使用
            needConfirmTime=false;
        }

    }
    public void acceptWorkOrder() {
        Integer repairType = selectedWorkOrder.getRepairType();

        woService.acceptWorkOrder(selectedWorkOrder, esTime, repairType, comments);
        cancel();
    }
    public void confirmSend(String woid) {

        selectedWorkOrder = dao.findById(woid);
        Integer repairType = selectedWorkOrder.getRepairType();

        woService.confirmSend(selectedWorkOrder);
        cancel();
    }
    public void confirmReturn(String woid) {

        selectedWorkOrder = dao.findById(woid);
        Integer repairType = selectedWorkOrder.getRepairType();

        woService.confirmReturn(selectedWorkOrder);
        cancel();
    }

    public void reassignWorkOrder() {
        if(StringUtils.isEmpty(comments)){
            comments=WebUtil.getMessage("WorkRequirements");
        }
        woService.reassignWorkOrder(selectedWorkOrder, selectedWorkOrder.getCurrentPersonId(), comments);
        cancel();
    }

    public void prepareNewAccDetail(){
        itemDetail = new V2_WorkOrder_Detail();

        itemDetail.setWoId(selectedWorkOrder.getId());
        itemDetail.setApplyTime(new Date());
        supplierName = null;
        selectedObjectMap = new HashMap();
        switchTmp=true;
        currentCustomConfig = ccService.getCurrentCustomConfig(selectedWorkOrder.getSiteUID());
        Object eval = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType");
        itemDetail.setPartsType(String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType")));
    }
    public void prepareEditAccDetail(String itemId) {
        statusFlag = WorkOrderConstant.STATUS_FLAG_OTHER;
        Object eval;
        if ("new".equals(itemId)) {
            selectedObjectMap = new HashMap();
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setWoId(selectedWorkOrder.getId());
            itemDetail.setApplyTime(new Date());
            supplierName = null;
            switchTmp=true;
            currentCustomConfig = ccService.getCurrentCustomConfig(selectedWorkOrder.getSiteUID());
            eval = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType");
            itemDetail.setPartsType(String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType")));
            String partsType = String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType"));
            if (WorkOrderConstant.PARTS_TYPE_RECEIVING.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_RECEIVING;
            }
            if (WorkOrderConstant.PARTS_TYPE_PURCHASE.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_PURCHASE;
            }
            selectedObjectMap.put("selectedOrg", orgDao.findByUid(loginUser.getSiteUID()));
        } else {
            switchTmp=false;
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(selectedWorkOrder.getId())).findAny().get();
            supplierName = itemDetail.getSupplier();
        }
        supplierList = aimService.getSuplierListBySiteUid(selectedWorkOrder.getTenantUID());
        nochange=false;
        saveCountyAreaAddressInfo();
    }
    public void prepareEditAccDetail(V2_WorkOrder_Detail vwd) {
        if (vwd != null) {
            setStatusFlagByType(vwd.getPartsType());
        }
        switchTmp=false;
        accessoryList = woService.getWorkOrderDetails(selectedWorkOrder.getId());
        if (accessoryList.stream().filter(item -> item.getId().equals(vwd.getId())).findAny().isPresent()){
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(vwd.getId())).findAny().get();
            supplierName = itemDetail.getSupplier();
        }
        supplierList = aimService.getSuplierListBySiteUid(selectedWorkOrder.getTenantUID());
        selectedObjectMap.put("supplier",supplierName);
        nochange=false;
        partsItem = partsItemService.getPartsItem(vwd.getPartsItemId(), vwd.getParts());
    }

    public void prepareEditAccDetailonPrice(String itemId) {
        itemDetail = detailDao.findById(itemId);
//        accessoryList = woService.getWorkOrderDetails(woId);//?如果一条woId仅仅对应一条workOrderDetail 这条代码就不需要
//        itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        supplierList = aimService.getSuplierListBySiteUid(selected.getTenantUID());

        supplierName=itemDetail.getSupplier();

        nochange = true;
        setStatusFlagByType(itemDetail.getPartsType());
        partsItem = partsItemService.getPartsItem(itemDetail.getPartsItemId(), itemDetail.getParts());
    }

    private void setStatusFlagByType(String type) {
        if (WorkOrderConstant.PARTS_TYPE_RECEIVING.equals(type)) {
            statusFlag = WorkOrderConstant.STATUS_FLAG_RECEIVING;
        } else if (WorkOrderConstant.PARTS_TYPE_PURCHASE.equals(type)) {
            statusFlag = WorkOrderConstant.STATUS_FLAG_PURCHASE;
        } else {
            statusFlag = WorkOrderConstant.STATUS_FLAG_OTHER;
        }
    }

    public boolean allowChanged(){
        //is closed
        if(selectedWorkOrder==null){
            return true;
        }
        if( selectedWorkOrder.getStatus()==2) {//关单后
            if(userContextService.hasAnyRole("AssetHead")){
                return true;
            }else{
                return false;
            }
        }else{
            if(userContextService.hasAnyRole("AssetHead")||userContextService.hasAnyRole("AssetStaff")){
                return true;
            }
        }
        return true;
    }

    public boolean allowChangedForDisable(){
        if(selected.getStatus()==2) {//关单后
            if(userContextService.hasAnyRole("AssetHead")){
                return false;
            }else{
                return true;
            }
        }else{
            if(userContextService.hasAnyRole("AssetHead")||userContextService.hasAnyRole("AssetStaff")){
                return false;
            }else{
                return true;
            }
        }

    }

    public List<V2_BlobObject> getPartPictures(String id){
        if(StringUtils.isNotEmpty(id)) {
            accessoryList = getWorkOrderDetail(id);
            Set<String> ids = accessoryList.stream().map(p -> p.getId()).collect(Collectors.toSet());
            if(!ids.isEmpty()){
                return blobObjectRepository.getPartPictures(ids);
            }
            return  null;
        } else {
            logger.warn("V2WorkOrderController selected is null");
            return null;
        }
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
    public void saveAndadd(int oper){
        //照旧原来的添加一个备件
        if(oper ==1){ //来自保存新增，从新建一个item
            itemDetail.setId(null);
        }
        saveItem();
        V2_WorkOrder_Detail vwd= new V2_WorkOrder_Detail();
        try {
            BeanUtils.copyProperties(itemDetail,vwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vwd.setId(itemDetail.getId());
        // clear 备件名称 备件数量  预估价格
        vwd.setParts(null);
        vwd.setApartsQuantity(null);
        vwd.setPartsPrice(null);
        vwd.setClosedPrice(null);
        vwd.setWarrantyDate(null);
        vwd.setEstimatedArriveTime(null);
        vwd.setSerialNum(null);
        vwd.setComment(null);
        vwd.setPartsType(null);
        selectedObjectMap = new HashMap();
        partsItem = null;
        itemDetail= vwd;
        itemDetail.setPartUseType(null);
        itemDetail.setPartsDescription(null);
        saveCountyAreaAddressInfo();
        accessoryList = getWorkOrderDetail(selectedWorkOrder.getId());
    }
    //保存一个备件
    public void saveItem(){
        //合同价格"默认等于"预估单价
        if(itemDetail.getParts()==null && partsItem==null) {
            WebUtil.addErrorMessage("备件名称必填！");
            return;
        }
        if (isEnableIntegrationWithWorkOrder()==true){
            if (itemDetail.getPartsType().equals("1")){
                if(itemDetail.getPartsPrice() != null && itemDetail.getPartsPrice() < 0.01) {
                    WebUtil.addErrorMessage("预估单价不能小于0.01元");
                    return;
                }
            }
            if (itemDetail.getPartsType().equals("2")){
                if(itemDetail.getPartsPrice() != null && itemDetail.getPartsPrice() < 0.01) {
                    WebUtil.addErrorMessage("预估单价不能小于0.01元");
                    return;
                }
            }

        }

        if (itemDetail.getClosedPrice() == null) {
            itemDetail.setClosedPrice(itemDetail.getPartsPrice());
        }
        if(itemDetail.getApartsQuantity() !=null && itemDetail.getApartsQuantity() <= 0){
            WebUtil.addErrorMessage("备件数量不能小于0");
            return;
        }
        itemDetail.setArrivedGood(false);
        itemDetail.setArrivedGoodReturn(false);
        itemDetail.setSiteUID(loginUser.getSiteUID());
        itemDetail.setTenantUID(loginUser.getTenantUID());
        itemDetail.setInstitutionUID(loginUser.getInstitutionUID());
        itemDetail.setHospitalUID(loginUser.getHospitalUID());
        accessoryList.add(itemDetail);

        //调用微服务来存附件
        if(switchTmp==true){
            itemDetail.setId(null);
        }
        serviceRequestApiService.addAccess(token,selectedWorkOrder.getAssetInfo().getUid(),itemDetail);
    }
    //保存备件操作
    public void addAcessItem() {
        saveItem();
        accessoryList = getWorkOrderDetail(selectedWorkOrder.getId());
        cancelWorkdetail();
        for (V2_WorkOrder_Detail v2_workOrder_detail :accessoryList){
            if(v2_workOrder_detail.getApproved()==null || !v2_workOrder_detail.getApproved()){
                approveFlag =true;
            }
        }
        System.out.println(accessoryList);
    }
    public void updateAccessPrice(){
        woService.updateAccessPrice(itemDetail);
    }

    public void addCoWokerItem() {
        if (coWorkerList.contains(coWorkerDetail)) {
            coWorkerList.remove(coWorkerDetail);
        }
        if(selectedWorkOrder.getCoworkerEntryType() == 1 || selectedWorkOrder.getCoworkerEntryType() == 3){
            String strHours = "0";
            long milliSeconds = coWorkerDetail.getEndTime().getTime() - coWorkerDetail.getStartTime().getTime();
            if (milliSeconds > 0) {
                DecimalFormat df = new DecimalFormat("0.0");
                strHours = df.format((float) milliSeconds / (1000 * 60 * 60));
                BigDecimal b = new BigDecimal(strHours);
                float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                f1 = Float.parseFloat(df.format(f1));
                strHours = df.format(f1);
            } else {
                WebUtil.addErrorMessage("结束时间不能小于开始时间");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
            coWorkerDetail.setManHours(Double.valueOf(strHours));
        }
        UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
        coWorkerDetail.setCowokerUserName(coWorker.getName());
        coWorkerList.add(coWorkerDetail);
        cancelWorkdetail();
    }
    //index == 1 是删除备件  ，index=2是删除协作人
    public void deleteWoDetail(String itemId, Integer index) {
        if (index == 1) {

           /* itemDetail =accessoryList.get(0);
            detailDao.delete(itemDetail);*/

            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
            //调用微服务删除备件
            serviceRequestApiService.delAccess(token,itemId);

        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
    }

    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
        selectedWorkOrderManHoursDetail = null;
        partsItem = null;
    }
    public List<AssetFaultType> getAssetFaultType() {
        AssetInfo assetinfo = assetDao.findById(this.selectedWorkOrder.getAssetId());
        return assetFaultTypeService.getFaultTypeList(assetinfo.getAssetGroup());
    }
    public String getFaultTypeName(Integer id) {
        return assetFaultTypeService.getFaultName(id);
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return cacheService.findOrgByUid(orgUid);
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
        synchronized (picList){
            if(getPicListByBoType(V2_BlobObject.BO_TYPE_WorkOrder) != null && getPicListByBoType(V2_BlobObject.BO_TYPE_WorkOrder).size() >= 5){
                WebUtil.addWarnMessage("上传图片数量不能超过5个！");
                return;
            }
            V2_BlobObject blobObject = blobService.uploadFile(event);
            if (null != blobObject) {
                picList.add(blobObject);
                blobObject.setBoType(V2_BlobObject.BO_TYPE_WorkOrder);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
                blobService.saveBlobObject(blobObject);
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }
        }


    }

    public void handleFileUploadPDF(FileUploadEvent event) {
        synchronized (attachList){
            if(attachList != null && attachList.size() >= 5){
                WebUtil.addErrorMessage("上传文件数量不能超过5个！");
                return;
            }
            V2_BlobObject blobObject = blobService.uploadFile(event);
            if (null != blobObject) {
                attachList.add(blobObject);
                blobObject.setBoType(V2_BlobObject.BO_TYPE_WorkOrder);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_DOCS);
                blobService.saveBlobObject(blobObject);
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }
        }
    }

    public void handleFileUploadWorkDetail(FileUploadEvent event) {

            V2_BlobObject blobObject = blobService.uploadFile(event);
            if (null != blobObject) {
                itemDetail.getAttachments().add(blobObject);
                blobObject.setBoType(V2_BlobObject.BO_TYPE_PartAttachment);
                blobService.saveBlobObject(blobObject);
            } else {
                WebUtil.addErrorMessage("fail to upload file");
            }

    }

    public void removeWorkDetailAttachmentBy(String blobId) {
        blobService.deleteBlobFileById(blobId);
        itemDetail.getAttachments().removeAll(itemDetail.getAttachments().stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void submitWorkOrder() {

        String approverUid=this.selectedApproverUid;
        try {
            String woId = selectedWorkOrder.getId();
            Integer currentStepId = selectedWorkOrder.getCurrentStepId();

            saveRepairWorkOrder();
            selectedWorkOrder=woService.getWorkOrderById(woId);
            selectedWorkOrder.setCurrentStepId(currentStepId);
        }catch (Exception e){
            logger.error("submitWorkOrder saveRepairWorkOrder error: ",e);
        }

        if(StringUtils.isEmpty(comments)){
            comments= WebUtil.getMessage("QuotationApproval");
        }
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> {
            item.setCreatedDate(null);
            if (null != item.getApproved() && !item.getApproved()) {
                item.setApproved(null);
            }
        });
        woService.submitWorkOrder(selectedWorkOrder, accessoryList, this.comments, approverUid);
        cancel();
    }

    public void approveHighLevelWorkOrder() {
        if (com.alibaba.druid.util.StringUtils.isEmpty(highlevelApprovalComments)) {
            highlevelApprovalComments = WebUtil.getMessage("highlevelApproval");
        }
        approvedDetails.stream().filter(item -> selectedMap.get(item.getId())).forEach(item -> item.setApproved(true));
        approvedDetails.forEach(item -> {
            item.setCreatedDate(null);
            if (null == item.getApproved()) {
                item.setApproved(false);
            }
        });
        woService.approveHighLevelAction(selectedWorkOrder, approvedDetails, highlevelApprovalComments, selectedApproverUid);
        cancel();
    }

    public void getSignature() {
        this.signatureContent = null;
        this.signatureContentStream = null;

        List<Map<String, String>> signatureList = woService.getUserSignature();

        if (!CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        }else{
            this.newSignature = true;
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }

    public void preApproveWorkOrder(){
        // 审核不通过，必须填写备注
        if (approvedDetails.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId())) && Strings.isNullOrEmpty(comments)) {
            WebUtil.addErrorMessage(WebUtil.getMessage(WebUtil.getMessage("ApprovalFallureReasonRequired")));
            RequestContext.getCurrentInstance().addCallbackParam("needComents", true);
            return;
        }

        // 备件审核是否需要签名
        WorkflowConfig workflowConfig = configService.findConfigBySite(this.selectedWorkOrder.getSiteUID());

        if(workflowConfig != null && workflowConfig.getPartsNeedSign() != null && workflowConfig.getPartsNeedSign()){
            // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            response.setStatus(205);
            return;
        }
        approveWorkOrder();
    }

    public void batchPreApproveWorkOrder(){
        // 备件审核是否需要签名
        WorkflowConfig workflowConfig = configService.findConfigBySite(this.currentPageWorkOrder.get(0).getSiteUID());

        if(workflowConfig != null && workflowConfig.getPartsNeedSign() != null && workflowConfig.getPartsNeedSign()){
            // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            response.setStatus(205);
            return;
        }
        selectedBatchMap.forEach((k,v) -> {
            if (v) {
                selectedWorkOrder = dao.findById(k);
                selectedMap = new HashMap<>();
                approvedDetails = new ArrayList<>();
                detailDao.findByWoId(k).forEach(i -> {
                    approvedDetails.add(i);
                    selectedMap.put(i.getId(), true);
                });
                approveWorkOrder();
            }
        });
        selectedBatchMap.clear();
    }

    public void saveSignatureAndSubmitApprove(){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }

        if(this.newSignature){
            if (StringUtils.isNotEmpty(signatureContent)) {

                try {
                    String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] decode = decoder.decodeBuffer(base64);

                    V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                    if (null != blobObject) {
                        this.signatureId = blobObject.getObjectStorageId();
                        woService.addUserSignature(this.signatureId);
                    }else{
                        WebUtil.addErrorMessage("签名保存失败！");
                        response.setStatus(206);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e.getMessage());
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            }
        }
        approveWorkOrder();
    }

    public void saveSignatureAndBatchSubmitApprove() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }
        if(this.newSignature){
            if (StringUtils.isNotEmpty(signatureContent)) {

                try {
                    String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] decode = decoder.decodeBuffer(base64);

                    V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                    if (null != blobObject) {
                        this.signatureId = blobObject.getObjectStorageId();
                        woService.addUserSignature(this.signatureId);
                    }else{
                        WebUtil.addErrorMessage("签名保存失败！");
                        response.setStatus(206);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e.getMessage());
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            }
        }
        selectedBatchMap.forEach((k,v) -> {
            if (v) {
                selectedWorkOrder = dao.findById(k);
                selectedMap = new HashMap<>();
                approvedDetails = new ArrayList<>();
                detailDao.findByWoId(k).forEach(i -> {
                    approvedDetails.add(i);
                    selectedMap.put(i.getId(), true);
                });
                approveWorkOrder();
            }
        });
        selectedBatchMap.clear();
    }

    public void approveWorkOrder() {
        // 设置备件审批签名
        this.selectedWorkOrder.setPartApproveSignature(this.signatureId);

        if (selectedApproverUid !=null) {
            approveHighLevelWorkOrder();
            return;
        }
        List<V2_WorkOrder_Detail> approvedDetailsNew = new ArrayList<>();

        for (int i = 0; i < approvedDetails.size(); i++) {
            V2_WorkOrder_Detail v2_workOrder_detail = approvedDetails.get(i);
            if (selectedMap!=null && selectedMap.get(v2_workOrder_detail.getId())) {
                if (v2_workOrder_detail.getApproved() == null || !v2_workOrder_detail.getApproved()) {
                    approvedDetailsNew.add(v2_workOrder_detail);
                }
                v2_workOrder_detail.setApproved(true);
            }else{
                v2_workOrder_detail.setApproved(false);
//                approvedDetailsNew.add(v2_workOrder_detail);
            }
        }

        if(this.isEnableIntegrationWithWorkOrder()){
            //最后一个审批通过的时候 生成相应单子
            List<V2_WorkOrder_Detail> accessorySparePartsBuyList = new ArrayList<>();//申购
            List<V2_WorkOrder_Detail> accessorySparePartsRequestList = new ArrayList<>();//申领
            if (!approvedDetailsNew.isEmpty()) {
                for (int i = 0; i < approvedDetailsNew.size(); i++) {
                    V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                    if (ApmConstant.TREASURY_USE.equals(v2_workOrder_detail.getPartsType())) {//库房领用
                        accessorySparePartsRequestList.add(v2_workOrder_detail);
                    } else if (ApmConstant.MEDICAL_WORKERS_PURCHASING.equals(v2_workOrder_detail.getPartsType())) {//医工采购
                        accessorySparePartsBuyList.add(v2_workOrder_detail);
                    }
                }
                if (!accessorySparePartsRequestList.isEmpty()) {
                    V2_WorkOrder v2_WorkOrder = dao.findById(accessorySparePartsRequestList.get(0).getWoId());
                    partsRequestWorkOrderService.savePartsRequest(accessorySparePartsRequestList, SimplePartsConstant.WORK_ORDER, userDao.getById(v2_WorkOrder.getCurrentPersonId()));//生成申领单
                }
                if (!accessorySparePartsBuyList.isEmpty()) {
                    V2_WorkOrder v2_WorkOrder = dao.findById(accessorySparePartsBuyList.get(0).getWoId());
                    partsRequestWorkOrderService.savePartsPurchase(accessorySparePartsBuyList, SimplePartsConstant.WORK_ORDER, userDao.getById(v2_WorkOrder.getCurrentPersonId()));//生成申购单
                }
            }
        }
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

    public List<UserAccount> getApproverList(String name) {
//        Boolean hospitalNameBoolean = UserContextService.isHospitalName("");
        List<UserAccount> uaList=new ArrayList<>();
        if (enableAuditGroupConfiguration){
            AuditGroupConfiguration auditGroupConfiguration = auditGroupConfigurationRepository.findByHierOrgUidAndGroupName(loginUser.getHospitalUID(), WorkOrderConstant.MEDICAL_TEAM_GROUP);
            if(auditGroupConfiguration!=null){
                String groupMemberItem = auditGroupConfiguration.getGroupMemberItem();
                List<AuditGroupConfigurationNode> auditGroupConfigurationNodes = JSON.parseObject(groupMemberItem, new TypeReference<List<AuditGroupConfigurationNode>>() {});
                if (auditGroupConfigurationNodes != null && !auditGroupConfigurationNodes.isEmpty()) {
                    for (int i = 0; i < auditGroupConfigurationNodes.size(); i++) {
                        AuditGroupConfigurationNode auditGroupConfigurationNode = auditGroupConfigurationNodes.get(i);
                        List<String> userUids = auditGroupConfigurationNode.getUserUids();
                        if(userUids!=null){
                            UserAccount userAccount = userDao.getUsers(userUids.get(0));
                            uaList.add(userAccount);
                        }
                    }

                }
            }else {
                WebUtil.addErrorMessage("未配置医工分组,请先配置!");
            }
        }else {
            uaList= woService.getApproverListByAsset(selectedWorkOrder.getAssetInfo().getUid(),name);
        }

        if (loginUser.isEnableAssetTypeGroup()) {
            // 已开启资产分类，需要过滤掉没有资产分类权限的人
            List<String> userUidList = assetTypeGroupRepository.getUserUidListByEncoded(selectedWorkOrder.getAssetInfo().getAssetType());
            uaList = uaList.stream().filter(user -> userUidList.contains(user.getUid())).collect(Collectors.toList());
        }

        return uaList;
    }

    public void prepareEditWorkOrderManHoursDetail(String itemId) {
        if ("new".equals(itemId)) {
            selectedWorkOrderManHoursDetail = new V2_WorkOrderManHoursDetail();
            selectedWorkOrderManHoursDetail.setId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            selectedWorkOrderManHoursDetail = workHoursList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public V2_WorkOrderManHoursDetail getSelectedWorkOrderManHoursDetail() {
        return selectedWorkOrderManHoursDetail;
    }

    public void setSelectedWorkOrderManHoursDetail(V2_WorkOrderManHoursDetail selectedWorkOrderManHoursDetail) {
        this.selectedWorkOrderManHoursDetail = selectedWorkOrderManHoursDetail;
    }

    public void deleteWorkOrderManHoursDetail(String itemId) {
        workHoursList.removeAll(workHoursList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        Double hours = 0d;
        for (V2_WorkOrderManHoursDetail info : workHoursList) {
            if (info != null && info.getHours() != null) {
                hours = hours + info.getHours();
            }
        }
        selectedWorkOrder.setManHours(hours);
    }

    public void addManHoursDetail(Date firstDate, Date secondDate) {
        String strHours = "0";
        long milliSeconds = secondDate.getTime() - firstDate.getTime();
        if (milliSeconds > 0) {
            DecimalFormat df = new DecimalFormat("0.0");
            strHours = df.format((float) milliSeconds / (1000 * 60 * 60));
            BigDecimal b = new BigDecimal(strHours);
            float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f1 = Float.parseFloat(df.format(f1));
            strHours = df.format(f1);
        } else {
            WebUtil.addErrorMessage("结束时间不能小于开始时间");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        selectedWorkOrderManHoursDetail.setHours(Double.valueOf(strHours));
        selectedWorkOrderManHoursDetail.setStartTime(firstDate);
        selectedWorkOrderManHoursDetail.setEndTime(secondDate);
        if (workHoursList.contains(selectedWorkOrderManHoursDetail)) {
            workHoursList.remove(selectedWorkOrderManHoursDetail);
        }

        // 重复性校验
        boolean check = false;
        for (V2_WorkOrderManHoursDetail detail : workHoursList) {
            if("000".equals(detail.getId())){
                continue;
            }
            // 开始时间结束时间不能相等
            if(firstDate.getTime() == detail.getStartTime().getTime()
                    || secondDate.getTime() == detail.getEndTime().getTime()){
                check = true;
                break;
            }
            // 当前开始时间小于结束时间 && 大于开始时间
            if(firstDate.getTime() < detail.getEndTime().getTime()
                    && firstDate.getTime() > detail.getStartTime().getTime()){
                check = true;
                break;
            }
            // 当前结束时间大于开始时间 && 小于结束时间
            if(secondDate.getTime() > detail.getStartTime().getTime()
                    && firstDate.getTime() < detail.getEndTime().getTime()){
                check = true;
                break;
            }
        }
        if(check){
            WebUtil.addErrorMessage("工时段重复，请重新填写");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        workHoursList.add(selectedWorkOrderManHoursDetail);
        Double hours = 0d;
        for (V2_WorkOrderManHoursDetail info : workHoursList) {
            if (info != null && info.getHours() != null) {
                hours = hours + info.getHours();
            }
        }
        selectedWorkOrder.setManHours(hours);
        cancelWorkdetail();
    }

    public void updateEntryType(Integer entryType) {
        if (selectedWorkOrder == null) {
            selectedWorkOrder = new V2_WorkOrder();
        }
        selectedWorkOrder.setEntryType(entryType);
        if (entryType == 1) {
            workHoursList = null;
        }
        selectedWorkOrder.setManHours(null);
    }

    public void updateCoworkerEntryType(Integer entryType) {
        if (selectedWorkOrder == null) {
            selectedWorkOrder = new V2_WorkOrder();
        }
        selectedWorkOrder.setCoworkerEntryType(entryType);
        coWorkerList = new ArrayList<>();
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        coWorkerList.add(emptyDetail);
    }

    public void setNothing(){}

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
        for (V2_WorkOrder_Detail v2_workOrder_detail :accessoryList){
            if(v2_workOrder_detail.getApproved()==null || !v2_workOrder_detail.getApproved()){
                approveFlag =true;
            }
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
    public void saveNewSupplier() {
        this.newSupplier.setSiteId(loginUser.getSiteId());
        this.newSupplier.setHospitalId(loginUser.getHospitalId());
        this.newSupplier.setTenantUID(loginUser.getTenantUID());
        this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
        this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
        this.newSupplier.setSiteUID(loginUser.getSiteUID());
        this.newSupplier.setCreatedDate(new Date());
        this.newSupplier.setCreatedBy(loginUser.getLoginName());

        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
        supplierList.add(newSupplier);
        if (org.apache.commons.lang.StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
            supplierList.add(newSupplier);
        }
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

    public List<V2_BlobObject> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<V2_BlobObject> videoList) {
        this.videoList = videoList;
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

    public Boolean getApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(Boolean approveFlag) {
        this.approveFlag = approveFlag;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }


    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
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
        String supplier = (String) selectedObjectMap.get("supplier");
        if (supplier == null)
            itemDetail.setSupplier(supplierName);
        else
            itemDetail.setSupplier(supplier);
    }


    public Boolean getNochange() {
        return nochange;
    }

    public void setNochange(Boolean nochange) {
        this.nochange = nochange;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getManHourShow() {
        if (hourRequired) {
            return WebUtil.getMessage("manhours")+"*";
        } else {
            return WebUtil.getMessage("manhours");
        }
    }

    public void prepareCreateSupplier() {
        newSupplier = new Supplier();
    }

    public String getFaultReason() {
        return faultReason;
    }

    public void setFaultReason(String faultReason) {
        this.faultReason = faultReason;
    }

    public void setAcker(List<UserAccount> acker) {
        this.acker = acker;
    }

    public Supplier getNewSupplier() {
        return newSupplier;
    }

    public void setNewSupplier(Supplier newSupplier) {
        this.newSupplier = newSupplier;
    }

    public MvsAssetInfo getMvsContract(String assetUid){
        MvsAssetInfo mvsAssetInfo = mvsAssetInfoRepository.findById(assetUid);
        if(mvsAssetInfo==null){
            return new MvsAssetInfo();
        }else{
            return mvsAssetInfo;
        }
    }

    public Integer getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(Integer subStatus) {
        this.subStatus = subStatus;
    }

    public List<I18nMessage> initStatus(String ... msgKey){
        List<String> msgKeys = new ArrayList<>(Arrays.asList(msgKey));
        String msgType="assetStatus";
        List<I18nMessage> fieldValueListByMsgType = FieldValueMessageController.getFieldValueListByMsgType(msgType);
        boolean countyArea = userContextService.isCountyArea();
        if(countyArea){
            msgKeys.add("6");
        }else{
            msgKeys.remove("6");
        }

        fieldValueListByMsgType = fieldValueListByMsgType.stream().filter(item -> msgKeys.contains(item.getMsgKey())).collect(Collectors.toList());
        return fieldValueListByMsgType;
    }

    /**
     *  县域： 登录人是否有报修权限
     * @param role
     * @return
     */
    public boolean  canCreate(String role){
        UserContextService userContextService = this.userContextService;
        return userContextService.isCountyArea() ? userContextService.hasRole(role):Boolean.TRUE;
    }

    /**
     * 县域：更新备件归还信息。
     */
    @Transactional
    public void savePartReturnInfo(V2_WorkOrder_Detail_Return v2WorkOrderDetailReturn){
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        if(v2WorkOrderDetailReturn.getReturnType() == null){
            WebUtil.addErrorMessage(WebUtil.getMessage("selectedReturnTypeError"));
            return;
        }
        if(v2WorkOrderDetailReturn != null){
            v2WorkOrderDetailReturn.setReturnTime(new Date());
            v2WorkOrderDetailReturn.setReturnBy(userContextService.getLoginUser().getLoginName());
            detailReturnDao.save(v2WorkOrderDetailReturn);
            v2WorkOrderDetailReturnList = woService.getWorkOrderDetailReturn(v2WorkOrderDetailReturn.getWoId());
        }
    }

    /**
     * 保存县域租户备件收货信息
     */
    public void saveCountyAreaAddressInfo(){
        if(userContextService.isCountyArea()){
            //县域租户拼凑收货信息：“收货地址”、“收货人+联系方式”、”工程师姓名“、”工程师联系电话
            UserAccount userAccount = userContextService.getLoginUser();
            if(!userAccount.getId().equals(selectedWorkOrder.getAcceptStepOwnerId())){
                userAccount = userDao.getById(selectedWorkOrder.getAcceptStepOwnerId());
            }
            AssetInfo assetInfo = selectedWorkOrder.getAssetInfo();
            OrgInfo orgInfo = cacheService.findOrgByUid(assetInfo.getHospitalUID());
            StringBuilder sb = new StringBuilder();
            sb.append("收货地址：").append(orgInfo.getAddress()==null?"":orgInfo.getAddress()).append("，")
                    .append("收货人：").append(orgInfo.getContactPerson()==null?"":orgInfo.getContactPerson()).append("，")
                    .append("联系方式：").append(orgInfo.getContactPhone()==null?"":orgInfo.getContactPhone()).append("，")
                    .append("工程师姓名：").append(userAccount.getName()==null?"":userAccount.getName()).append("，")
                    .append("联系电话：").append(userAccount.getTelephone()==null?"":userAccount.getTelephone());
            itemDetail.setComment(sb.toString());
        }
    }

    public void verifyDatalength(String data,String labelName,int len){
        if(StringUtils.isNotBlank(data)){
            if(data.length() != len){
                WebUtil.addErrorMessage(labelName +"长度必须为："+len+"位");
                return;
            }
        }
    }

    public String getPartsItemCode(V2_WorkOrder_Detail v2WorkOrderDetail) {
        if(v2WorkOrderDetail == null){
            if(partsItem != null){
                return partsItem.getItemCode();
            }
        }else{
            SimplePartsItem simplePartsItem = partsItemService.getPartsItem(v2WorkOrderDetail.getPartsItemId(),v2WorkOrderDetail.getParts());
            if(simplePartsItem != null){
                return simplePartsItem.getItemCode();
            }
        }
        return null;
    }

    public List<V2_WorkOrderManHours> getV2WorkOrderManHoursList() {
        return this.v2WorkOrderManHoursList;
    }

    public void setV2WorkOrderManHoursList(List<V2_WorkOrderManHours> v2WorkOrderManHoursList) {
        this.v2WorkOrderManHoursList = v2WorkOrderManHoursList;
    }

    public void prepareMoreManHoursView(){
        this.v2WorkOrderManHoursList = manHoursDao.findByWoIdSort(selected.getId());
        List<V2_WorkOrderManHours> tempManHoursList = v2WorkOrderManHoursList.stream().filter(item -> (item.getId().equals("000") && item.getWoId()==null)).collect(Collectors.toList());
        if(tempManHoursList == null || tempManHoursList.size() < 1){
            V2_WorkOrderManHours workOrderManHours = new V2_WorkOrderManHours();
            workOrderManHours.setId("000");
            workOrderManHours.setCreatedTime(new Date());
            this.v2WorkOrderManHoursList.add(0,workOrderManHours);
        }
    }

    /**
     * 行编辑数据保存
     * @param event
     */
    public void onRowEdit(RowEditEvent event) {
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);

        V2_WorkOrderManHours workOrderManHours = (V2_WorkOrderManHours) event.getObject();
        if(StringUtils.isBlank(workOrderManHours.getManHoursOwner())){
            WebUtil.addErrorMessage("工时所属人不能为空");
            return;
        }
        V2_WorkOrderManHours v2_workOrderManHours = manHoursDao.findByManHoursOwnerAndManHoursDateAndWoId(
                workOrderManHours.getManHoursOwner(),workOrderManHours.getManHoursDate(),selected.getId());
        if(v2_workOrderManHours != null && !v2_workOrderManHours.getId().equals(workOrderManHours.getId())){
            WebUtil.addErrorMessage("当前日期工时所属人"+workOrderManHours.getManHoursOwner()+"已有记录，请选择其他日期");
            return;
        }
        if(workOrderManHours.getId().equals("000")){//新增时保存记录处理
            workOrderManHours.setCreatedBy(userContextService.getUserLoginName());
            workOrderManHours.setCreatedTime(new Date());
        }else{
            workOrderManHours.setModifiedBy(userContextService.getUserLoginName());
            workOrderManHours.setModifiedTime(new Date());
        }
        if(workOrderManHours.getWorkHours() == null && workOrderManHours.getTravelHours() == null
                && workOrderManHours.getWaitingHours() == null){
            WebUtil.addErrorMessage("工作工时、差旅工时和等待工时三者必须填一个！！！");
            return;
        }
        if (workOrderManHours.getWorkHours() != null && workOrderManHours.getWorkHours() < 0.01) {
            WebUtil.addErrorMessage("工作工时不能小于0.01");
            return;
        }
        if (workOrderManHours.getTravelHours() != null && workOrderManHours.getTravelHours() < 0.01) {
            WebUtil.addErrorMessage("差旅工时不能小于0.01");
            return;
        }
        if (workOrderManHours.getWaitingHours() != null && workOrderManHours.getWaitingHours() < 0.01) {
            WebUtil.addErrorMessage("等待工时不能小于0.01");
            return;
        }
        if(workOrderManHours.getId().equals("000")){
            workOrderManHours.setId(null);//这个不能提前设null，否则异常情况页面显示不对。
        }
        manHoursDao.save(workOrderManHours);
        updateManHours(selected.getId());
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
    }

    public void onRowCancel(RowEditEvent event) {
        resetManHoursInfo((V2_WorkOrderManHours) event.getObject());
    }

    public void resetManHoursInfo(V2_WorkOrderManHours workOrderManHours){
        if(workOrderManHours.getId().equals("000")){
            v2WorkOrderManHoursList.stream().forEach(item -> {
                if(item.getId().equals("000")){
                    item.setComment(null);
                    item.setWaitingHours(null);
                    item.setTravelHours(null);
                    item.setWorkHours(null);
                    item.setManHoursDate(null);
                    item.setManHoursOwner(null);
                }
            });
        }else{
            V2_WorkOrderManHours v2_workOrderManHoursTemp = manHoursDao.findById(workOrderManHours.getId());
            v2WorkOrderManHoursList.stream().forEach(item -> {
                if(item.getId().equals(v2_workOrderManHoursTemp.getId())){
                    item.setComment(v2_workOrderManHoursTemp.getComment());
                    item.setWaitingHours(v2_workOrderManHoursTemp.getWaitingHours());
                    item.setTravelHours(v2_workOrderManHoursTemp.getTravelHours());
                    item.setWorkHours(v2_workOrderManHoursTemp.getWorkHours());
                    item.setManHoursDate(v2_workOrderManHoursTemp.getManHoursDate());
                    item.setManHoursOwner(v2_workOrderManHoursTemp.getManHoursOwner());
                }
            });
        }
    }

    public void initManHours() {
        v2WorkOrderManHoursList.stream().forEach(item -> {
            if(item.getId().equals("000")){
                item.setWoId(selected.getId());
                item.setSrNum(selected.getServiceRequest().getSrNum());
            }
        });
        V2_WorkOrderManHours workOrderManHours = new V2_WorkOrderManHours();
        workOrderManHours.setId("000");
        workOrderManHours.setCreatedTime(new Date());//这里加时间只为了后续删除记录时定位删除。
        v2WorkOrderManHoursList.add(0,workOrderManHours);
    }

    public void deleteManHours(V2_WorkOrderManHours v2_workOrderManHours) {
        if(v2_workOrderManHours.getId().equals("000")){
            for(int i=0;i<v2WorkOrderManHoursList.size();i++){
                V2_WorkOrderManHours item = v2WorkOrderManHoursList.get(i);
                if(item.getCreatedTime().getTime() == v2_workOrderManHours.getCreatedTime().getTime()){
                    v2WorkOrderManHoursList.remove(i);
                    return;
                }
            }
        }else{
            manHoursDao.delete(v2_workOrderManHours.getId());
            v2WorkOrderManHoursList.remove(v2_workOrderManHours);
            updateManHours(selected.getId());
        }
    }

    /**
     * 县域需求：每次操作工时必须同步更新workOrder工时。
     * @param woId
     */
    @Transactional
    public void updateManHours(String woId){
        Double totalManHours = 0.00;
        List<V2_WorkOrderManHours> v2WorkOrderManHoursList = manHoursDao.findByWoIdSort(woId);
        if(v2WorkOrderManHoursList != null && v2WorkOrderManHoursList.size()>0){
            for(V2_WorkOrderManHours item : v2WorkOrderManHoursList){
                Double workHours = item.getWorkHours()==null?0.00:item.getWorkHours();
                Double travelHours = item.getTravelHours()==null?0.00:item.getTravelHours();
                Double waitingHours = item.getWaitingHours()==null?0.00:item.getWaitingHours();
                totalManHours = totalManHours + workHours + travelHours + waitingHours;
            }
        }
        if(totalManHours > 0.00){
            dao.updateManHours(totalManHours,woId);
        }
    }

    public String getMaintainSignatureWeb(String signatureId) {
        StreamedContent streamedContent = blobService.getBlobObjectByStorageId(signatureId);
        InputStream input = streamedContent.getStream();
        byte[] b = new byte[1024];
        StringBuilder sb = new StringBuilder();
        String result = "";
        int i = 0;
        try {
            while ((i = input.read(b)) > 0) {
                String str = new String(b, 0, i);
                sb.append(str);
            }
            input.close();
        } catch (Exception e) {
            // TODO: handle exception
            try {
                input.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                LOGGER.error(e.getMessage());
            }
            LOGGER.error(e.getMessage());
            return "";
        }
        result = sb.toString();
        return result;
    }

    public List<V2_WorkOrderManHours> getWorkOrderManHours(String woId){
        return manHoursDao.findByWoIdSort(woId);
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

    public String getFilterApplyTime() {
        return filterApplyTime;
    }

    public void setFilterApplyTime(String filterApplyTime) {
        this.filterApplyTime = filterApplyTime;
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

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public List<V2_WorkOrderManHoursDetail> getWorkHoursList() {
        if (workHoursList == null) {
            workHoursList = new ArrayList<>();
        }
        V2_WorkOrderManHoursDetail v2_WorkOrderManHoursDetail2 = new V2_WorkOrderManHoursDetail();
        v2_WorkOrderManHoursDetail2.setId("000");
        boolean flag = false;
        for (V2_WorkOrderManHoursDetail info : workHoursList) {
            String id = info.getId();
            if ("000".equals(id)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            workHoursList = workHoursList.stream()
                    .filter(item -> !"000".equals(item.getId()))
                    .collect(Collectors.toList());
            workHoursList.add(v2_WorkOrderManHoursDetail2);
        } else {
            workHoursList.add(v2_WorkOrderManHoursDetail2);
        }
        return workHoursList;
    }

    public List<OrgInfo> LoadSiteNameList() {
        if (CollectionUtils.isEmpty(orgList)) {
            if (!CollectionUtils.isEmpty(bySearchFilterInternal)) {
                orgList = bySearchFilterInternal.stream().map(item -> item.getSiteUID()).collect(Collectors.toSet()).stream()
                        .map(val -> cacheService.findOrgByUid(val)).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(orgList)) {
                orgList = orgList.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
            }
        }
        return orgList;
    }

    public void initLoadInternalSiteNameList() {
        List<OrgInfo> orgInfoList = uaaService.getUserAccessibleSiteList(loginUser.getUid());
        orgList = orgInfoList.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
        hospitalShow = orgList.size() > 1;
    }

    public List<OrgInfo> getLoadInternalSiteNameList() {
        return orgList;
    }



    public boolean getHospitalShow() {
        return hospitalShow;
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }



    public void setWorkHoursList(List<V2_WorkOrderManHoursDetail> workHoursList) {
        this.workHoursList = workHoursList;
    }


    public boolean hasPermissionConfirmReturn(String requestorId){
        return Objects.equals(requestorId , String.valueOf(userContextService.getLoginUserId()));
    }
    public boolean hasPermissionConfirmSend(String currentPersonId){
        return Objects.equals(currentPersonId , String.valueOf(userContextService.getLoginUserId()));
    }

    public boolean needTwoSubStatus(String woId){
        V2_WorkOrder workOrder = dao.findById(woId);
        return isConfigNeedTwoSubBySiteUid(workOrder.getAssetInfo().getSiteUID())&& (workOrder.getRepairType() == 2 || workOrder.getRepairType() == 3) && workOrder.getCurrentStepId() == 10;
    }

    public void needTwoSubStatusBatch(List<V2_WorkOrder> workOrders){
        workOrders.forEach(workOrder -> {
            V2_WorkOrder_Step workOrder_step = new V2_WorkOrder_Step();
            String tenantUID = loginUser.getTenantUID();
            if(StringUtils.isNotBlank(tenantUID) && tenantUID.equals(workOrder.getTenantUID())){
                workOrder_step = isConfigNeedTwoSubBySiteUid(workOrder.getAssetInfo().getSiteUID()) && (workOrder.getRepairType() == 2 || workOrder.getRepairType() == 3) && workOrder.getCurrentStepId() == 10 ? getRepairPerson(workOrder.getId(), 10):getRepairPerson(workOrder.getId(), 4);
            }else if(StringUtils.isNotBlank(tenantUID) && !tenantUID.equals(workOrder.getTenantUID())){
               /* if(workOrder.getMvsIbMapping() != null  && workOrder.getMvsIbMapping().getAssetInfo() != null){
                    workOrder_step = isConfigNeedTwoSubBySiteUid(workOrder.getMvsIbMapping().getAssetInfo().getSiteUID()) && (workOrder.getRepairType() == 2 || workOrder.getRepairType() == 3) && workOrder.getCurrentStepId() == 10 ? getRepairPerson(workOrder.getId(), 10):getRepairPerson(workOrder.getId(), 4);
                }*/
            }
            workOrder.setRepairName(ObjectUtils.isEmpty(workOrder_step)?"":workOrder_step.getOwnerName());
            workOrder.setRepairPersonId(ObjectUtils.isEmpty(workOrder_step)?null:workOrder_step.getOwnerId());
        });
    }

    public Boolean isConfigNeedTwoSubBySiteUid(String siteUid) {
        WorkflowConfig woc = configService.findConfigBySite(siteUid);
        return woc != null&&woc.getEnableSendRepair()!=null&&woc.getEnableSendRepair();
    }


    public Boolean isConfigNeedTwoSubStatusByUser(UserAccount userAccount) {
        WorkflowConfig woc =  configService.findConfigBySite(userAccount.getSiteUID());
        return woc != null&&woc.getEnableSendRepair()!=null&&woc.getEnableSendRepair();
    }

    public List<I18nMessage> getWoSteps(String stepIds){
        List<String> stepIdsList = Arrays.asList(stepIds.split(","));
        return FieldValueMessageController.doGetFieldValueList("woSteps", -1).stream()
                .filter(item -> stepIdsList.contains(item.getMsgKey())).collect(Collectors.toList());
    }

    public void onFilterWo(FilterEvent event) {
        selectedBatchMap.clear();
        onFilterCustom(event, this::filters);
    }

    public boolean filters(String fieldName, String filterVal) {
        if ("serviceRequest.requestTime".equals(fieldName)) {
            processTimeFiled(filterVal, fieldName);
            return true;
        }
        if ("id".equals(fieldName)) {
            List<String> stepIds = new ArrayList<>();
            List<V2_WorkOrder_Step> steps = woService.getWorkStepsByStepId(4, tenantInfo.getUid());
            if (!CollectionUtils.isEmpty(steps)) {
                Map<String, List<V2_WorkOrder_Step>> map = steps.stream().collect(Collectors.groupingBy(V2_WorkOrder_Step::getWoId));
                List<V2_WorkOrder_Step> stepList = map.values().stream().map(group -> group.stream()
                                .max(Comparator.comparing(V2_WorkOrder_Step::getStartTime))
                                .orElse(null))
                        .filter(val -> val!=null&&val.getOwnerName().contains(filterVal))
                        .collect(Collectors.toList());
                removeFilterOnField("id");
                stepIds = stepList.stream().map(V2_WorkOrder_Step::getWoId).collect(Collectors.toList());
            }
            searchFilters.add(new SearchFilter(fieldName, SearchFilter.Operator.IN, stepIds));
            return true;
        }
        return false;
    }

    public void onRowEditExternalTempUser(RowEditEvent event) {
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        Map<String,String> row =(Map) event.getObject();
        String label = row.get("label");
        if(StringUtils.isBlank(label)){
            WebUtil.addErrorMessage(row.get("key")+"不能为空");
            return;
        }
        try {
            Class<ExternalTempUser> externalTempUserClass = ExternalTempUser.class;
            Field fieldName = externalTempUserClass.getDeclaredField(row.get("fieldName"));
            fieldName.setAccessible(true);
            fieldName.set(externalTempUser,label);
            externalTempUserDao.save(externalTempUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
    }

    public void onRowCancelExternalTempUser(RowEditEvent event) {
        try {
            Map<String,String> row =(Map) event.getObject();
            Class<ExternalTempUser> externalTempUserClass = ExternalTempUser.class;
            Field fieldName = externalTempUserClass.getDeclaredField(row.get("fieldName"));
            fieldName.setAccessible(true);
            Object o = fieldName.get(externalTempUser);
            if(!ObjectUtils.isEmpty(o)){
                row.put("label",o.toString());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Boolean> getSelectedBatchMap() {
        return selectedBatchMap;
    }

    public void setSelectedBatchMap(Map<String, Boolean> selectedBatchMap) {
        this.selectedBatchMap = selectedBatchMap;
    }

    public boolean batchReviewPassButton() {
        if (CollectionUtils.isEmpty(selectedBatchMap)) {
            return true;
        }
        if (!selectedBatchMap.containsValue(true)) {
            return true;
        }
        return false;
    }

    /**
     * 控制报修发送地的显示隐藏
     */
    public boolean repairReportSendPlaceRender(){
        //从配置信息获取紧急程度
        CustomConfigObject currentCustomConfig = ccService.getCurrentCustomConfig(loginUser.getSiteUID());
        //从配置信息获取当前状态
        Object repairReportSendPlace =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.enable[0]");
        if(repairReportSendPlace == null){
            return false;
        }

        return Boolean.valueOf(repairReportSendPlace.toString());
    }

    private boolean enableAuditGroupConfiguration(){
        if(enableAuditGroupConfiguration == null){
            String value = userContextService.getConfigItemValue("enableAuditGroupConfiguration");
            enableAuditGroupConfiguration = Boolean.TRUE.toString().equalsIgnoreCase(value);
        }
       return enableAuditGroupConfiguration;
    }
    public String openNewWindowView(String srId) {
        return "/portal/woMgr/woPartsJmPrintReportView.xhtml?faces-redirect=true&amp;sr_id=" +  srId ;
    }

    public boolean approvedCompleted(){
        return approveList.stream().anyMatch(item -> item.getApprovalStatus() == 0 || item.getApprovalStatus() == 1);
    }
    public Date getMaxDate(){
        if(selectedWorkOrderManHoursDetail.getStartTime() != null){
            Date date = DateUtil.offset(selectedWorkOrderManHoursDetail.getStartTime(), DateField.HOUR, 24);
            return date;
        }
        return TimeUtil.now();
    }

    public ExternalTempUser getExternalTempUser() {
        return externalTempUser;
    }

    public void setExternalTempUser(ExternalTempUser externalTempUser) {
        this.externalTempUser = externalTempUser;
    }

    public void editContractor() {
        String id = externalTempUser.getId();
        if ("000".equals(id)) {
            externalTempUser.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        externalTempUser.setWoId(selectedWorkOrder.getId());
    }

    public  void deleteContractorl(){
        externalTempUser.setId("000");
        externalTempUser.setUserName(null);
        externalTempUser.setSignature(null);
        externalTempUser.setTel(null);
        externalTempUser.setComName(null);
        externalTempUser.setWoId(null);
    }

    public boolean showEnableEngineersWriteRepairOutside(){
        boolean  externalPurchase = accessoryList.stream().anyMatch(item -> "3".equals(item.getPartsType()));
        return enableEngineersWriteRepairOutside && externalPurchase;
    }
}

