package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.dao.AdjustmentTransferApplicationRepository;
import com.ge.apm.dao.IdleEquipmentApplicationRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.view.uaa.OrgTreeController;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.ApmSystemRole;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@ManagedBean
@ViewScoped
public class AdjustmentTransferApplicationController extends GenericCRUDUUIDController<AdjustmentTransferApplication> implements Serializable, ServerEventInterface {


    private AdjustmentTransferApplicationRepository adjustmentTransferApplicationRepository;

    private OrgInfoRepository orgInfoRepository;
    private UserAccount currentUserAccount;
    private OrgTreeController orgTreeController;
    private ConfigCacheService cacheService;
    private UaaService uaaService;
    @Getter
    @Setter
    private List<String> visibleScope;
    @Getter
    @Setter
    private List<OrgInfo> orgList;
    @Getter
    @Setter
    private int buttonType = 1;  //1借出工单   2  借入工单
    @Getter
    @Setter
    private String filterAssetGroup;
    @Getter
    @Setter
    private Integer filterStatus;


    @Getter
    @Setter
    private Map<String, Object> filterObject;

    @Override
    protected void init() {
        adjustmentTransferApplicationRepository = WebUtil.getBean(AdjustmentTransferApplicationRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        cacheService = WebUtil.getBean(ConfigCacheService.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        visibleScope = orgInfoRepository.getUserDeptScope(currentUserAccount.getUid());
        orgTreeController = WebUtil.getBean(OrgTreeController.class);
        uaaService = WebUtil.getBean(UaaService.class);

        orgList = uaaService.getUserAccessibleSiteList(currentUserAccount.getUid());
        filterObject = new HashMap<>();
    }

    @Override
    protected GenericRepositoryUUID<AdjustmentTransferApplication> getDAO() {
        return adjustmentTransferApplicationRepository;
    }


    @Override
    public Page<AdjustmentTransferApplication> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        Set<String> functionList = UserContextService.getCurrentUserFunction();
        if (!functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name()) && !functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }

        this.updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, currentUserAccount.getTenantUID()));
        if (functionList.contains(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())) {

            if (buttonType == 1) {
                this.updateSearchFilter(new SearchFilter("stockSiteUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                this.updateSearchFilter(new SearchFilter("stockDeptUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                this.updateSearchFilter(new SearchFilter("status", SearchFilter.Operator.EQ, 6));
//                this.removeFilterOnField("borrowSiteUid");
//                this.removeFilterOnField("borrowDeptUid");
            } else {
                this.updateSearchFilter(new SearchFilter("borrowSiteUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
                this.updateSearchFilter(new SearchFilter("borrowDeptUid", SearchFilter.Operator.IN, visibleScope, "1*2+"));
//                this.removeFilterOnField("stockSiteUid");
//                this.removeFilterOnField("stockDeptUid");
//                this.removeFilterOnField("status");
            }
        }
        Page<AdjustmentTransferApplication> bySearchFilter = adjustmentTransferApplicationRepository.findBySearchFilter(searchFilters, pageRequest);
        return bySearchFilter;

    }



    public  void buttonTypeChange(int  buttonType){
        searchFilters = new ArrayList<>();
        this.buttonType=buttonType;
    }

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }

        String url = "actionName=View&selectedid="
                + selected.getUid();
        url = "AdjustmentTransferApplicationDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public TreeNode getSiteOrgTree() {

        return orgTreeController.getSiteOrgTree(orgList);
    }

    public String getSiteOrgName(String siteUID) {
        OrgInfo org = cacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    //1待审批  2  审批未通过 3 已取消  4 待交付  5  已交付 6 已完成 7 已退回
    public String statusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1:
                return "待审批";
            case 2:
                return "审批未通过";
            case 3:
                return "已取消";
            case 4:
                return "待交付";
            case 5:
                return "已交付";
            case 6:
                return "已完成";
            case 7:
                return "退回待审批";
            case 8:
                return "已退回";
            case 9:
                return "退回审批不通过";
            default:
                return "";
        }
    }


}
