package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.service.zkb.ZkbIntegralOrderListService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbIntegralOrderListController extends GenericCRUDUUIDController<ZkbIntegralOrder> {

    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;

    private ZkbIntegralOrderListService zkbIntegralOrderListService;

    private UserContextService userContextService;

    @Getter @Setter
    private String actionName;
    @Getter @Setter
    private String selectionTenantInfo;
    @Getter @Setter
    private String queryStatus = "";

    @Getter@Setter
    private ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo;

    private UserAccount currentUser;

    @Getter@Setter
    private StreamedContent exportFile;

    private Page<ZkbIntegralOrder> zkbIntegralOrderPage;

    @Getter @Setter
    private List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList ;


    @Override
    protected void init() {
        super.init();
        zkbIntegralOrderRepository = WebUtil.getBean(ZkbIntegralOrderRepository.class);
        zkbIntegralOrderListService = WebUtil.getBean(ZkbIntegralOrderListService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        selectionTenantInfo =WebUtil.getRequestParameter("tenantUID") == null ? "" : WebUtil.getRequestParameter("tenantUID");
        currentUser = UserContextService.getCurrentUserAccount();
        queryStatus = WebUtil.getRequestParameter("queryStatus") == null ? "" : WebUtil.getRequestParameter("queryStatus");
        if(userContextService.isZkbUser()) {
            queryStatus = StrUtil.isEmpty(queryStatus) ? "all" : queryStatus;
            this.selectionTenantInfo = currentUser.getTenantUID();
        }
        zkbIntegralOrderStatusVo = new ZkbIntegralOrderStatusVo();
        zkbPartOrderUserConfigList = new ArrayList<>();
        zkbIntegralOrderListService.queryUserConfig(zkbPartOrderUserConfigList, zkbIntegralOrderStatusVo, currentUser);
        zkbIntegralOrderListService.calcPendQty(userContextService.isZkbUser(), currentUser,zkbIntegralOrderStatusVo, zkbPartOrderUserConfigList);
        zkbIntegralOrderListService.calcStepQty(userContextService.isZkbUser(), currentUser, zkbIntegralOrderStatusVo);
    }

    @Override
    protected Page<ZkbIntegralOrder> loadData(PageRequest pageRequest) {
        Specification<ZkbIntegralOrder> specification = buildSpecification(this.searchFilters,"");
        zkbIntegralOrderPage = zkbIntegralOrderRepository.findBySpecification(specification, pageRequest);
        //searchFilters.clear();
        return zkbIntegralOrderPage;
    }

    public Specification<ZkbIntegralOrder> buildSpecification(List<SearchFilter> csearchFilters , String type){
        return  (Root<ZkbIntegralOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(currentUser.getLoginName().contains(ZkbPartOrder.DATA_TYPE_TEST)){
                if(StringUtils.isEmpty(type)){
                    predicates.add(builder.equal(root.get("dataType"), ZkbPartOrder.DATA_TYPE_TEST));
                }
            }else {
                predicates.add(builder.isNull(root.get("dataType")));
            }

            if(CollectionUtils.isNotEmpty(csearchFilters)){
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

            if(userContextService.isZkbUser()) {
                if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                    predicates.add(builder.equal(root.get("tenantUID"), selectionTenantInfo));
                }
                if(StringUtils.isEmpty(queryStatus)){

                }else if(!"all".equals(queryStatus)){
                    predicates.add(builder.equal(root.get("currentStep"), queryStatus));
                }
            }else{
                if(this.selectionTenantInfo != null && StringUtils.isNotEmpty(selectionTenantInfo)){
                    predicates.add(builder.equal(root.get("tenantUID"), selectionTenantInfo));
                }
                if(zkbPartOrderUserConfigList != null && zkbPartOrderUserConfigList.size() > 0){
                    List<Predicate> userConfigPredicates = new ArrayList<>();
                    for(ZkbPartOrderUserConfig zkbPartOrderUserConfig : zkbPartOrderUserConfigList) {
                        if(ZkbPartOrderStep.PartOrderStep.ManagePending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
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
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.OrderPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
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
                        }else if(ZkbPartOrderStep.PartOrderStep.CommercialPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CommercialPending.toString()));
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.CommercialPending.toString());
                                Predicate pre2 = builder.equal(root.get("commercialAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
                            }
                        }else if(ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString().equals(zkbPartOrderUserConfig.getConfigType())){
                            if(StringUtils.isEmpty(queryStatus)){
                                userConfigPredicates.add(builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString()));
                            }else if("all".equals(queryStatus)){
                                Predicate pre1 = builder.equal(root.get("currentStep"), ZkbPartOrderStep.PartOrderStep.EngineerQualificationPending.toString());
                                Predicate pre2 = builder.equal(root.get("engineerAuditerUid"), currentUser.getUid());
                                Predicate pre3 = builder.or(pre1, pre2);
                                userConfigPredicates.add(pre3);
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

    public String queryListByStatus(String status){
        if(StrUtil.isNotEmpty(status) && "notGeAsset".equals(status)){
            return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
        }else{
            queryStatus = status;
            return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
        }
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
    }

    public void doubleClick(SelectEvent event){
        selected = (ZkbIntegralOrder) event.getObject();
        String str = "action=Edit&selectedId=".concat(selected.getId())+"&queryStatus="+queryStatus;
        String url = "";
        if(StringUtils.isEmpty(selected.getCurrentStep())){
            url = "Edit.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(str);
        }else {
            url = "Audit.xhtml?str="+ UrlParamUtil.encodeUrlQueryString(str);
        }
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("Edit?action=Create&selectionTenantInfo=".concat(selectionTenantInfo)+"&queryStatus="+queryStatus);
    }
    public String editAction() {
        if(StringUtils.isEmpty(selected.getCurrentStep())){
            return UrlEncryptController.encodeUrlParam("Edit?action=Edit&selectedId=".concat(selected.getId())+"&queryStatus="+queryStatus);
        }else{
            return UrlEncryptController.encodeUrlParam("Audit?action=Edit&selectedId=".concat(selected.getId())+"&queryStatus="+queryStatus);
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbIntegralOrder> getDAO() {
        return zkbIntegralOrderRepository;
    }

}
