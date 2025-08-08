package com.ge.apm.view.zkb;

import com.ge.apm.dao.mapper.ZkbPartOrderMapper;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.*;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2020/6/22
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderController  extends GenericCRUDUUIDController<ZkbPartOrder> {

    private ZkbPartOrderRepository dao;
    private AssetInfoRepository assetInfoRepository;
    private ZkbPartOrderMapper zkbPartOrderMapper;
    private UserContextService userContextService;
    private UserAccount currentUser;
    private List<TenantInfo> tenantInfoList;
    private String actionName;
    private String selectionTenantInfo;
    private Page<ZkbPartOrder> zkbContractPage;
    private ZkbPartOrderService zkbPartOrderService;
    private ZkbPartOrderExcelService zkbPartOrderExcelService;
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = new ArrayList<>();
    private String queryStatus = "";
    private boolean showStatusBtn = true;
    private boolean disableNotGeAsset = false;
    /**
     * 按钮待办是否显示
     */
    private boolean showPendingStatusBtn = true;
    private boolean vacationBtn = false;
    private String isVacation;
    private boolean canDelete= false;
    private boolean operateDelete = false;
    private String notGeAsset;

    private Integer pendingQty = 0;
    private Integer pendingTechQty = 0;
    private Integer pendingManageQty = 0;
    private Integer pendingTseQty = 0;
    private Integer pendingOsscQty = 0;
    private Integer pendingCommercialQty = 0;
    private Integer pendingDirectorQty = 0;
    private Integer pendingOrderQty = 0;
    private Integer pendingOsscReturnQty = 0;
    private Integer pendingUnOrderQty = 0;
    private Integer pendingReturnQty = 0;
    private Integer pendingCloseQty = 0;
    private Integer pendingCompleteQty = 0;
    private Integer pendingCompleteAuditQty = 0;

    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(ZkbPartOrderRepository.class);
        zkbPartOrderMapper = WebUtil.getBean(ZkbPartOrderMapper.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        zkbPartOrderExcelService = WebUtil.getBean(ZkbPartOrderExcelService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        queryStatus =WebUtil.getRequestParameter("queryStatus");
        notGeAsset = WebUtil.getRequestParameter("notGeAsset");
        selectionTenantInfo =WebUtil.getRequestParameter("tenantUID");
        if(StringUtils.isEmpty(queryStatus)){
            queryStatus = "";
        }
        if(StringUtils.isEmpty(selectionTenantInfo)){
            selectionTenantInfo = "";
        }
        if(userContextService.isZkbUser()) {
            this.selectionTenantInfo = currentUser.getTenantUID();
            Integer c = zkbPartOrderService.getZkbContractRepository().fetchContractCount(selectionTenantInfo);
            if(c == null || c == 0){
                if(StrUtil.isEmpty(notGeAsset)){
                    notGeAsset = "";
                }
                disableNotGeAsset = true;
            }
        }else {
            zkbPartOrderUserConfigList = zkbPartOrderService.getZkbPartOrderUserConfigRepository().findByLoginName(currentUser.getLoginName());
            if(zkbPartOrderUserConfigList != null && zkbPartOrderUserConfigList.size() >0){
                ZkbPartOrderUserConfig zkbPartOrderUserConfig = zkbPartOrderUserConfigList.get(0);
                if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) ||
                    ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(zkbPartOrderUserConfig.getConfigType())
                ){
                    showStatusBtn =false;
                }
                if(ZkbPartOrderStep.PartOrderStep.Reviewer.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    showPendingStatusBtn =false;
                }
                if(zkbPartOrderUserConfigList.size() == 2){
                    for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                        if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(c.getConfigType())){
                            isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? ZkbConstantUtil.F : zkbPartOrderUserConfig.getIsVacation();
                            vacationBtn = true;
                            break;
                        }
                    }
                }else if(zkbPartOrderUserConfigList.size() == 1){
                    if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) && ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrderUserConfig.getOrderType())){
                        isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? ZkbConstantUtil.F : zkbPartOrderUserConfig.getIsVacation();
                        vacationBtn = true;
                    }
                    if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType()) && "US".equals(zkbPartOrderUserConfig.getAssetType())){
                        isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? ZkbConstantUtil.F : zkbPartOrderUserConfig.getIsVacation();
                        vacationBtn = true;
                    }
                    if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(zkbPartOrderUserConfig.getConfigType()) && "US".equals(zkbPartOrderUserConfig.getAssetType())){
                        isVacation = StringUtils.isEmpty(zkbPartOrderUserConfig.getIsVacation()) ? ZkbConstantUtil.F : zkbPartOrderUserConfig.getIsVacation();
                        vacationBtn = true;
                    }
                }
                for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                    if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(c.getConfigType())){
                        canDelete = true;
                        break;
                    }
                }
            }
            Map<String, String> mm =  FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            //if(!mm.containsKey("notGeAsset")){
                for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
                    if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(c.getConfigType()) ||
                        ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(c.getConfigType())
                    ){
                        notGeAsset = ZkbConstantUtil.T;
                        disableNotGeAsset = true;
                    }else if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(c.getConfigType()) ||
                            ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(c.getConfigType()) ||
                            ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(c.getConfigType())){
                        notGeAsset = "";
                        disableNotGeAsset = true;
                    }
                }
            //}
        }

        tenantInfoList = zkbPartOrderService.getSourceTenantInfoList();
        calcStepQty();
    }

    public Map<String, Object> initQtyMap(){
        Map<String, Object> mm = new HashMap<>();
        if(StrUtil.isNotEmpty(selectionTenantInfo)){
            mm.put("tenantUid", selectionTenantInfo);
        }
        if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
            mm.put("testLogin", ZkbConstantUtil.T);
        }else{
            mm.put("testLogin", ZkbConstantUtil.F);
        }
        return mm;
    }

    public void calcStepQty(){
        calcPendQty();

        if(showStatusBtn){
            Map<String, Object> mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
            pendingTechQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
            pendingManageQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TsePending.toString());
            pendingTseQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
            pendingOsscQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
            pendingDirectorQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
            pendingCommercialQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
            pendingOrderQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
            pendingUnOrderQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
            pendingReturnQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
            pendingCloseQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString());
            pendingOsscReturnQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString());
            pendingCompleteAuditQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);

            mm = initQtyMap();
            mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
            pendingCompleteQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);
        }
    }

    public void calcPendQty(){
        if(userContextService.isZkbUser()) {
            List<String> steps = new ArrayList<>();
            steps.add(ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
            steps.add(ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
            if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
                pendingQty = dao.fetchQtyByStepsForTest(steps, selectionTenantInfo);
            }else{
                pendingQty = dao.fetchQtyBySteps(steps, selectionTenantInfo);
            }
        }else{
            Map<String, Object> mm = new HashMap<>();
            if(StrUtil.isNotEmpty(selectionTenantInfo)){
                mm.put("tenantUid", selectionTenantInfo);
            }
            if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
                mm.put("testLogin", ZkbConstantUtil.T);
            }else{
                mm.put("testLogin", ZkbConstantUtil.F);
            }

            if(zkbPartOrderUserConfigList == null || zkbPartOrderUserConfigList.size() == 0){
            }else if(zkbPartOrderUserConfigList.size() == 1){
                boolean rsQuery = true;
                ZkbPartOrderUserConfig zkbPartOrderUserConfig = zkbPartOrderUserConfigList.get(0);
                if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if("US".equals(zkbPartOrderUserConfig.getAssetType())){
                        if(StrUtil.isNotEmpty(zkbPartOrderUserConfig.getZone())){
                            List<String> zones = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                            mm.put("zone", zones);
                        }
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
                    }else {
                        mm.put("assetTypeDi", zkbPartOrderUserConfig.getAssetType());
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if("US".equals(zkbPartOrderUserConfig.getAssetType())){
                        mm.put("onlineEngineer", zkbPartOrderUserConfig.getId());
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                    }else {
                        mm.put("assetTypeDi", zkbPartOrderUserConfig.getAssetType());
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(StringUtils.isNotEmpty(zkbPartOrderUserConfig.getZone())){
                        List<String> zones = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                        mm.put("zone", zones);
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                    }else {
                        rsQuery = false;
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    List<String> currentSteps = new ArrayList<>();
                    currentSteps.add(ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
                    currentSteps.add(ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString());
                    mm.put("currentSteps", currentSteps);
                }else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                }else if(ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ProductPending.toString());
                }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrderUserConfig.getOrderType())){
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                        mm.put("orderType", ZkbPartOrder.OrderType.Probe.toString());
                    }else{
                        mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                        mm.put("orderTypeNe", ZkbPartOrder.OrderType.Probe.toString());
                    }
                }else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString());
                }else if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                    mm.put("status", ZkbPartOrder.PartOrderStatus.PartReturned.toString());
                }else if(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                    mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString());
                }
                if(rsQuery){
                    pendingQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);
                }else {
                    pendingQty = 0;
                }
            }else if(zkbPartOrderUserConfigList.size() == 2){
                boolean is4ooRole = true;
                boolean isCommercialRole = false;
                for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList) {
                    if (StringUtils.isEmpty(c.getConfigType())) {
                        is4ooRole = false;
                    }
                    if (!c.getConfigType().contains("Order")) {
                        is4ooRole = false;
                    }
                    if(c.getConfigType().equals(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString())){
                        isCommercialRole = true;
                    }
                }
                if(is4ooRole) {
                    mm.put("ordersRole", ZkbConstantUtil.T);
                }
                if(isCommercialRole) {
                    List<String> currentSteps = new ArrayList<>();
                    currentSteps.add(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                    currentSteps.add(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                    mm.put("currentSteps", currentSteps);
                }
                if(is4ooRole || isCommercialRole){
                    pendingQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);
                }
            }else if(zkbPartOrderUserConfigList.size() == 3){
                mm.put("currentStep", ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                pendingQty = zkbPartOrderMapper.queryPendCountByUserConfig(mm);
            }
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrder> getDAO() {
        return dao;
    }

    @Override
    protected Page<ZkbPartOrder> loadData(PageRequest pageRequest) {
        Specification<ZkbPartOrder> specification = buildSpecification(this.searchFilters,"");
        zkbContractPage = dao.findBySpecification(specification, pageRequest);

        for(ZkbPartOrder zkbPartOrder : zkbContractPage.getContent()){
            if(StringUtils.isNotEmpty(zkbPartOrder.getTechAuditer())){
                continue;
            }
            if(StringUtils.isEmpty(zkbPartOrder.getZone())){
                continue;
            }

            if(ZkbPartOrder.OrderType.Tool.name().equals(zkbPartOrder.getOrderType()) || ZkbPartOrder.OrderType.Artificial.name().equals(zkbPartOrder.getOrderType())){
                continue;
            }
            AssetInfo assetInfo = assetInfoRepository.findByUid(zkbPartOrder.getAssetId());
            if(assetInfo.getAssetGroup() == null || assetInfo.getAssetGroup() != 2){
                continue;
            }
            List<ZkbPartOrderUserConfig> configList;
            if(ZkbPartOrder.DATA_TYPE_TEST.equals(zkbPartOrder.getDataType())){
                configList = zkbPartOrderService.getZkbPartOrderUserConfigRepository().findByConfigTypeAndZoneContainsAndLoginNameContains(ZkbPartOrderStep.PartOrderStep.TechnicalPending.name(), zkbPartOrder.getZone(), ZkbPartOrder.DATA_TYPE_TEST);
            }else{
                configList = zkbPartOrderService.getZkbPartOrderUserConfigRepository().findByConfigTypeAndZoneContainsAndLoginNameIsNotContaining(ZkbPartOrderStep.PartOrderStep.TechnicalPending.name(), zkbPartOrder.getZone(), ZkbPartOrder.DATA_TYPE_TEST);
            }
            if(CollectionUtils.isNotEmpty(configList)){
                List<String> names = configList.stream().map(ZkbPartOrderUserConfig::getName).collect(Collectors.toList());
                String str = String.join(", ", names);
                zkbPartOrder.setTechAuditer(str);
            }
        }
        //searchFilters.clear();
        return zkbContractPage;
    }

    public Specification<ZkbPartOrder> buildSpecification(List<SearchFilter> csearchFilters , String type){
        return  (Root<ZkbPartOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StrUtil.isEmpty(notGeAsset)){
                predicates.add(builder.isNull(root.get("notGeAsset")));
            }else{
                predicates.add(builder.equal(root.get("notGeAsset"), ZkbConstantUtil.T));
            }
            if(currentUser.getLoginName().indexOf(ZkbPartOrder.DATA_TYPE_TEST) > -1){
                if(StringUtils.isEmpty(type)){
                    predicates.add(builder.equal(root.get("dataType"), ZkbPartOrder.DATA_TYPE_TEST));
                }
            }else {
                predicates.add(builder.isNull(root.get("dataType")));
            }

            if(csearchFilters != null){
                Join join = root.join("assetInfo", JoinType.LEFT);
                for(SearchFilter searchFilter : csearchFilters){
                    if(searchFilter.fieldName.toString().indexOf("assetInfo.") > -1){
                        if(searchFilter.fieldName.toString().equals("assetInfo.name")){
                            predicates.add(builder.like(join.get("name"), "%" +searchFilter.value.toString() + "%"));
                        }else if(searchFilter.fieldName.toString().equals("assetInfo.systemId")){
                            predicates.add(builder.like(join.get("systemId"), "%" +searchFilter.value.toString() + "%"));
                        }else if(searchFilter.fieldName.toString().equals("assetInfo.assetGroup")){
                            predicates.add(builder.equal(join.get("assetGroup"), searchFilter.value));
                        }
                    }else{
                        if(searchFilter.operator.equals(SearchFilter.Operator.EQ)){
                            predicates.add(builder.equal(root.get(searchFilter.fieldName), searchFilter.value));
                        }else if(searchFilter.operator.equals(SearchFilter.Operator.LIKE)){
                            predicates.add(builder.like(root.get(searchFilter.fieldName), "%" + searchFilter.value.toString() + "%"));
                        }
                    }
                }
            }

            if("letter".equals(queryStatus)){
                if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                    predicates.add(builder.equal(root.get("tenantUID"), selectionTenantInfo));
                }
                predicates.add(builder.equal(root.get("needLetterFlag"), ZkbConstantUtil.T));
                predicates.add(builder.equal(root.get("uploadLetterFlag"), ZkbConstantUtil.F));
            }else if(userContextService.isZkbUser()) {
                if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                    predicates.add(builder.equal(root.get("tenantUID"), selectionTenantInfo));
                }
                if(StringUtils.isEmpty(queryStatus)){
                    Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ReturnPending.toString());
                    Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CompletePending.toString());
                    Predicate pre3 = builder.or(pre2, pre1);
                    predicates.add(pre3);
                }else if(!"all".equals(queryStatus)){
                    predicates.add(builder.equal(root.get("currentStep"), queryStatus));
                }
            }else{
                if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                    predicates.add(builder.equal(root.get("tenantUID"), selectionTenantInfo));
                }
                if(zkbPartOrderUserConfigList != null && zkbPartOrderUserConfigList.size() > 0){
                    List<Predicate> userConfigPredicates = new ArrayList<>();
                    HashMap<String,String> tseMap = new HashMap<>();//TSE特殊，配置3种角色同一账号，但是待办全部看到的信息应要一样
                    for(ZkbPartOrderUserConfig zkbPartOrderUserConfig : zkbPartOrderUserConfigList) {
                        if (ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(zkbPartOrderUserConfig.getConfigType())) {//技术支持
                            if ("US".equals(zkbPartOrderUserConfig.getAssetType())) {
                                if (StringUtils.isEmpty(queryStatus)) {
                                    if(StrUtil.isNotEmpty(zkbPartOrderUserConfig.getZone())){
                                        List<String> strs = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                                        Expression<String> exp = root.get("zone");
                                        Predicate pre1 = exp.in(strs);
                                        Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
                                        Predicate pre4 = builder.isNull(root.get("assetTypeDi"));
                                        Predicate pre5 = builder.equal(root.get("assetTypeDi"), "");
                                        Predicate pre6 = builder.or(pre4, pre5);
                                        Predicate pre3 = builder.and(pre1, pre2,pre6);
                                        userConfigPredicates.add(pre3);
                                    }else{
                                        predicates.add(builder.isFalse(builder.literal(true)));
                                    }
                                } else if ("all".equals(queryStatus)) {
                                    //userConfigPredicates.add(builder.equal(root.get("onlineEngineer"), zkbPartOrderUserConfig.getId()));
                                    if(StrUtil.isNotEmpty(zkbPartOrderUserConfig.getZone())){
                                        List<String> strs = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                                        Expression<String> exp = root.get("zone");
                                        Predicate pre1 = exp.in(strs);
                                        userConfigPredicates.add(pre1);
                                    }else{
                                        predicates.add(builder.isFalse(builder.literal(true)));
                                    }
                                }
                            } else {
                                if (StringUtils.isEmpty(queryStatus)) {
                                    Predicate pre1 = builder.equal(root.get("assetTypeDi"), zkbPartOrderUserConfig.getAssetType());
                                    Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString());
                                    Predicate pre3 = builder.and(pre2, pre1);
                                    userConfigPredicates.add(pre3);
                                } else if ("all".equals(queryStatus)) {
                                    userConfigPredicates.add(builder.equal(root.get("assetTypeDi"), zkbPartOrderUserConfig.getAssetType()));
                                }
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(tseMap.containsKey(ZkbPartOrderStep.PartOrderStep.TsePending.toString())){
                                continue;
                            }
                            if(StringUtils.isEmpty(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                                userConfigPredicates.add(pre1);
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                                Predicate pre2 = builder.equal(root.get("tseAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }
                            tseMap.put(ZkbPartOrderStep.PartOrderStep.TsePending.toString(), ZkbPartOrderStep.PartOrderStep.TsePending.toString());
                        }else if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                if(!StrUtil.isEmpty(zkbPartOrderUserConfig.getZone())){
                                    List<String> strs = Arrays.asList(zkbPartOrderUserConfig.getZone().split(","));
                                    Expression<String> exp = root.get("zone");
                                    Predicate pre1 = exp.in(strs);
                                    Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                                    Predicate pre3 = builder.and(pre1, pre2);
                                    userConfigPredicates.add(pre3);
                                }else{
                                    Predicate pre1 = builder.isNull(root.get("currentStep"));
                                    userConfigPredicates.add(pre1);
                                }
                            }else if("all".equals(queryStatus)){
                                /*Predicate pre1 = builder.equal(root.get("zone"), zkbPartOrderUserConfig.getZone());
                                Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ManagePending.toString());
                                Predicate pre3 = builder.equal(root.get("manageAuditerUid"), currentUser.getUid());
                                Predicate pre4 = builder.and(pre1, pre2);
                                Predicate pre5 = builder.or(pre4, pre3);
                                userConfigPredicates.add(pre5);*/
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.OsscPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if (StringUtils.isEmpty(queryStatus)) {
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
                                Predicate pre2 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }else if("all".equals(queryStatus)){
                               /* Predicate pre1 = builder.equal(root.get("osscAuditerUid"), currentUser.getUid());
                                Predicate pre2 = builder.equal(root.get("returnAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OsscPending.toString());
                                Predicate pre4 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OsscReturnPending.toString());
                                Predicate pre5 = builder.or(pre1, pre2, pre3, pre4);
                                userConfigPredicates.add(pre5);*/
                                Predicate pre1 = builder.equal(root.get("osscAuditerUid"), currentUser.getUid());
                                Predicate pre2 = builder.equal(root.get("returnAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.notEqual(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.Complete.toString());
                                Predicate pre4 = builder.or(pre1, pre2, pre3);
                                userConfigPredicates.add(pre4);
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.DirectorPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.DirectorPending.toString()));
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.DirectorPending.toString());
                                Predicate pre2 = builder.equal(root.get("directorAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CommercialPending.toString()));
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                                Predicate pre2 = builder.equal(root.get("commercialAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.ProductPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ProductPending.toString()));
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ProductPending.toString());
                                Predicate pre2 = builder.equal(root.get("productAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(ZkbPartOrder.OrderType.Probe.toString().equals(zkbPartOrderUserConfig.getOrderType())){
                                if(StringUtils.isEmpty(queryStatus)){
                                    Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                                    Predicate pre2 = builder.equal(root.get("orderType"), ZkbPartOrder.OrderType.Probe.toString());
                                    Predicate pre3 = builder.and(pre1, pre2);
                                    userConfigPredicates.add(pre3);
                                }else if(!"all".equals(queryStatus)){
                                    Predicate pre1 = builder.equal(root.get("currentStep"), queryStatus);
                                    Predicate pre2 = builder.equal(root.get("orderType"), ZkbPartOrder.OrderType.Probe.toString());
                                    Predicate pre3 = builder.and(pre1, pre2);
                                    userConfigPredicates.add(pre3);
                                }else{
                                    userConfigPredicates.add(builder.equal(root.get("orderType"), ZkbPartOrder.OrderType.Probe.toString()));
                                }
                            }else{
                                if(StringUtils.isEmpty(queryStatus)){
                                    Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OrderPending.toString());
                                    Predicate pre2 = builder.notEqual(root.get("orderType"), ZkbPartOrder.OrderType.Probe.toString());
                                    Predicate pre3 = builder.and(pre1, pre2);
                                    userConfigPredicates.add(pre3);
                                }else if(!"all".equals(queryStatus)){
                                    Predicate pre1 = builder.equal(root.get("currentStep"), queryStatus);
                                    Predicate pre2 = builder.notEqual(root.get("orderType"), ZkbPartOrder.OrderType.Probe.toString());
                                    Predicate pre3 = builder.and(pre1, pre2);
                                    userConfigPredicates.add(pre3);
                                }
                            }
                        } else if(ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.OrderExtraPending.toString()));
                            }else if(!"all".equals(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), queryStatus));
                            }
                        } else if(ZkbPartOrderStep.PartOrderStep.ClosePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.ClosePending.toString());
                                Predicate pre2 = builder.equal(root.get("status"), ZkbPartOrder.PartOrderStatus.PartReturned.toString());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }else if(!"all".equals(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), queryStatus));
                            }
                        } else if(ZkbPartOrderStep.PartOrderStep.Reviewer.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(!"all".equals(queryStatus)&&StringUtils.isNotBlank(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), queryStatus));
                            }
                        } else if(ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CompleteAuditPending.toString()));
                            }else if(!"all".equals(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), queryStatus));
                            }
                        }
                    }
                    if(userConfigPredicates.size() > 0){
                        predicates.add(builder.or(userConfigPredicates.toArray(new Predicate[0])));
                    }
                } else {
                    return null;
                    //predicates.add(builder.isNull(root.get("currentStep")));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    public String techAuditField(ZkbPartOrder zkbPartOrder){
        if(StringUtils.isNotEmpty(zkbPartOrder.getTechAuditer())){
            return zkbPartOrder.getTechAuditer();
        }else if(StringUtils.isNotEmpty(zkbPartOrder.getOnlineEngineer())){
            return zkbPartOrder.getOnlineEngine().getName();
        }else{
            return zkbPartOrder.getAssetTypeDi();
        }
    }

    public String scoreFlagField(String scoreFlag){
        if(StringUtils.isEmpty(scoreFlag)){
            return "名额申请";
        }else if(ZkbConstantUtil.T.equals(scoreFlag)){
            return "积分申请";
        }else if(ZkbConstantUtil.F.equals(scoreFlag)){
            return "名额申请";
        }else{
            return "名额申请";
        }
    }

    public void notGeAssetChange(){
        String url = "List.xhtml?notGeAsset=" + (StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String queryListByStatus(String status){
        if(StrUtil.isNotEmpty(status) && "notGeAsset".equals(status)){
            return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset);
        }else{
            queryStatus = status;
            return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset);
        }
    }

    public void doubleClick(SelectEvent event){
        selected = (ZkbPartOrder) event.getObject();
        String str = "action=Edit&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus +"&notGeAsset="+(StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset);
        String url = "AuditStep.xhtml?str="+UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public TenantInfo getTenantInfo(String tenantInfoUID){
        return zkbPartOrderService.getTenantInfo(tenantInfoUID);
    }

    /**
     * @author : 503189362
     * @description : 租户选择变化时，那么重新进行编辑页面
     * @date : 2020/4/21 15:06
     */
    public void tenantInfoSelect(){
        this.cancel();
        calcStepQty();
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        if(StringUtils.isEmpty(selected.getId())){
            operateDelete = Boolean.TRUE;
        }else if(ZkbPartOrder.PartOrderStatus.TechnicalReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.ManageReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.DirectorReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.OrderedReject.toString().equals(selected.getStatus()) ||
                ZkbPartOrder.PartOrderStatus.UnOrderedReject.toString().equals(selected.getStatus())
        ){
            operateDelete = Boolean.FALSE;
        }else{
            operateDelete = Boolean.TRUE;
        }
    }


    @Override
    public void delete() {
        //if(ZkbPartOrder.PartOrderStatus.Requested.toString().equals(selected.getStatus())){
            zkbPartOrderService.delete(this.selected,currentUser);
            this.selected= null;
        //}else {
           /* WebUtil.addErrorMessage("数据已审批不能删除！","");
            return;*/
        //}
    }

    public void saveVacation(){
        List<ZkbPartOrderUserConfig> backUpList = new ArrayList<>();
        for(ZkbPartOrderUserConfig c : zkbPartOrderUserConfigList){
            c.setIsVacation(isVacation);
            zkbPartOrderService.getZkbPartOrderUserConfigRepository().save(c);
            if(ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(c.getConfigType()) && "US".equals(c.getAssetType())){
                backUpList.add(c);
            }
            if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(c.getConfigType()) && "US".equals(c.getAssetType())){
                backUpList.add(c);
            }
        }

        if(backUpList.size() > 0){
            for(ZkbPartOrderUserConfig zkbPartOrderUserConfig : backUpList){
                List<ZkbPartOrder> zkbPartOrders = zkbPartOrderService.usVacationHandle(zkbPartOrderUserConfig,isVacation);
                if(zkbPartOrders != null && zkbPartOrders.size() > 0 ){
                    for(ZkbPartOrder o : zkbPartOrders){
                        zkbPartOrderService.sendEmail(o,"",UserContextService.getAccessToken());
                        zkbPartOrderService.sendMessage(o,UserContextService.getAccessToken());
                    }
                }
            }
        }
    }


    public String createAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Create&selectionTenantInfo=".concat(selectionTenantInfo)+"&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset));
    }
    public String editAction() {
        return UrlEncryptController.encodeUrlParam("AuditStep?action=Edit&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus+"&notGeAsset="+(StrUtil.isEmpty(notGeAsset) ? "" : notGeAsset));
    }
    private StreamedContent exportFile;

    public void exportAll(){
        Specification<ZkbPartOrder> specification = buildSpecification(this.searchFilters, "export");
        Sort sort=new Sort(Sort.Direction.DESC,"createdDate");
        List<ZkbPartOrder> zkbPartOrders =  dao.findBySpecification(specification,sort);
        HashMap<String, Object> dataMap = zkbPartOrderExcelService.createZkbPartOrderData(zkbPartOrders);

        try {
            String impFileName= "";
            if(StringUtils.isEmpty(notGeAsset) ){
                impFileName = "APM_ZKB_PartOrder_Exp_Template.xlsx";
            }else {
                impFileName = "APM_ZKB_PartOrder_Exp_Non_Template.xlsx";
            }
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/partOrder/excel/" + impFileName);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            zkbPartOrderExcelService.exportData(doc, dataMap);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ZkbPartOrderExcel_Export.xlsx");
        } catch (IOException ex) {
            Logger.getLogger(ZkbPartOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String copyAction(){
        /*try {
            ZkbPartOrder newPartOrder = zkbPartOrderService.copyZkbPartOrder(selected,currentUser);
            zkbPartOrderService.sendEmail(newPartOrder);
            zkbPartOrderService.sendMessage(newPartOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return UrlEncryptController.encodeUrlParam("Edit?action=Copy&selectionTenantInfo=".concat(selectionTenantInfo)+"&selectedid=".concat(selected.getId())+"&queryStatus="+queryStatus);

    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getSelectionTenantInfo() {
        return selectionTenantInfo;
    }

    public void setSelectionTenantInfo(String selectionTenantInfo) {
        this.selectionTenantInfo = selectionTenantInfo;
    }

    public Page<ZkbPartOrder> getZkbContractPage() {
        return zkbContractPage;
    }

    public void setZkbContractPage(Page<ZkbPartOrder> zkbContractPage) {
        this.zkbContractPage = zkbContractPage;
    }

    public String getQueryStatus() {
        return queryStatus;
    }

    public void setQueryStatus(String queryStatus) {
        this.queryStatus = queryStatus;
    }

    public boolean isShowStatusBtn() {
        return showStatusBtn;
    }

    public void setShowStatusBtn(boolean showStatusBtn) {
        this.showStatusBtn = showStatusBtn;
    }

    public boolean isShowPendingStatusBtn() {
        return showPendingStatusBtn;
    }

    public void setShowPendingStatusBtn(boolean showPendingStatusBtn) {
        this.showPendingStatusBtn = showPendingStatusBtn;
    }

    public String getIsVacation() {
        return isVacation;
    }

    public void setIsVacation(String isVacation) {
        this.isVacation = isVacation;
    }

    public boolean isVacationBtn() {
        return vacationBtn;
    }

    public void setVacationBtn(boolean vacationBtn) {
        this.vacationBtn = vacationBtn;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isOperateDelete() {
        return operateDelete;
    }

    public void setOperateDelete(boolean operateDelete) {
        this.operateDelete = operateDelete;
    }

    public String getNotGeAsset() {
        return notGeAsset;
    }

    public void setNotGeAsset(String notGeAsset) {
        this.notGeAsset = notGeAsset;
    }

    public boolean isDisableNotGeAsset() {
        return disableNotGeAsset;
    }

    public void setDisableNotGeAsset(boolean disableNotGeAsset) {
        this.disableNotGeAsset = disableNotGeAsset;
    }

    public Integer getPendingQty() {
        return pendingQty;
    }

    public void setPendingQty(Integer pendingQty) {
        this.pendingQty = pendingQty;
    }

    public Integer getPendingTechQty() {
        return pendingTechQty;
    }

    public void setPendingTechQty(Integer pendingTechQty) {
        this.pendingTechQty = pendingTechQty;
    }

    public Integer getPendingManageQty() {
        return pendingManageQty;
    }

    public void setPendingManageQty(Integer pendingManageQty) {
        this.pendingManageQty = pendingManageQty;
    }

    public Integer getPendingTseQty() {
        return pendingTseQty;
    }

    public void setPendingTseQty(Integer pendingTseQty) {
        this.pendingTseQty = pendingTseQty;
    }

    public Integer getPendingOsscQty() {
        return pendingOsscQty;
    }

    public void setPendingOsscQty(Integer pendingOsscQty) {
        this.pendingOsscQty = pendingOsscQty;
    }

    public Integer getPendingCommercialQty() {
        return pendingCommercialQty;
    }

    public void setPendingCommercialQty(Integer pendingCommercialQty) {
        this.pendingCommercialQty = pendingCommercialQty;
    }

    public Integer getPendingDirectorQty() {
        return pendingDirectorQty;
    }

    public void setPendingDirectorQty(Integer pendingDirectorQty) {
        this.pendingDirectorQty = pendingDirectorQty;
    }

    public Integer getPendingOrderQty() {
        return pendingOrderQty;
    }

    public void setPendingOrderQty(Integer pendingOrderQty) {
        this.pendingOrderQty = pendingOrderQty;
    }

    public Integer getPendingUnOrderQty() {
        return pendingUnOrderQty;
    }

    public void setPendingUnOrderQty(Integer pendingUnOrderQty) {
        this.pendingUnOrderQty = pendingUnOrderQty;
    }

    public Integer getPendingReturnQty() {
        return pendingReturnQty;
    }

    public void setPendingReturnQty(Integer pendingReturnQty) {
        this.pendingReturnQty = pendingReturnQty;
    }

    public Integer getPendingCloseQty() {
        return pendingCloseQty;
    }

    public void setPendingCloseQty(Integer pendingCloseQty) {
        this.pendingCloseQty = pendingCloseQty;
    }

    public Integer getPendingOsscReturnQty() {
        return pendingOsscReturnQty;
    }

    public void setPendingOsscReturnQty(Integer pendingOsscReturnQty) {
        this.pendingOsscReturnQty = pendingOsscReturnQty;
    }

    public Integer getPendingCompleteAuditQty() {
        return pendingCompleteAuditQty;
    }

    public void setPendingCompleteAuditQty(Integer pendingCompleteAuditQty) {
        this.pendingCompleteAuditQty = pendingCompleteAuditQty;
    }

    public Integer getPendingCompleteQty() {
        return pendingCompleteQty;
    }

    public void setPendingCompleteQty(Integer pendingCompleteQty) {
        this.pendingCompleteQty = pendingCompleteQty;
    }
}
