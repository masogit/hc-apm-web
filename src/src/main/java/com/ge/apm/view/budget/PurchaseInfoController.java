package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PurchaseInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.budget.BudgetApplication;
import com.ge.apm.domain.budget.BudgetApplicationAsset;
import com.ge.apm.domain.budget.BudgetAssetInfo;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.SysRoleConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ViewScoped
@ManagedBean(name = "PurchaseInfoController")
public class PurchaseInfoController extends JpaCRUDController<PurchaseInfo> {
    private static Logger logger = LoggerFactory.getLogger(PurchaseInfoController.class);

    private Page<PurchaseInfo> emptyPage = new PageImpl<>(Collections.emptyList());
    private UserAccount account = UserContextService.getCurrentUserAccount();

    /*接口类*/
    private PurchaseInfoRepository dao;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private UserContextService userContextService;
    private BudgetPersistService persistService;
    private CustomConfigService ccService;
    private UaaService uaaService;

    /*属性类*/
    @Getter@Setter
    private PurchaseInfo selected;
    @Getter@Setter
    private OrgInfo org;
    @Getter@Setter
    private CustomConfigObject customConfig;
    @Getter@Setter
    private TreeNode orgTree;
    @Getter@Setter
    private TreeNode orgTreeEdit;
    @Getter@Setter
    private OrgInfo selectedDept;
    @Getter@Setter
    private Integer assetGroup;
    @Getter@Setter
    private List<I18nMessage> assetGroupSecondaryList;
    @Getter@Setter
    private String functionType; //设备型号
    @Getter@Setter
    private String assetName; //设备名称
    @Getter@Setter
    private String assetBrand; //设备品牌

    @Override
    protected void init() {
        dao = WebUtil.getBean(PurchaseInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        persistService = WebUtil.getBean(BudgetPersistService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        ccService = WebUtil.getBean(CustomConfigService.class);
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());

        uaaService = WebUtil.getBean(UaaService.class);
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 0);
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(treeNode);
        orgTreeEdit = initOrgTree(treeNodeEdit);
    }

    @Override
    protected Page<PurchaseInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void assetGroupChangeListener() {
        if (assetGroup == null) {
            assetGroupSecondaryList = Collections.emptyList();
        } else {
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + assetGroup.intValue(), userContextService.getLoginUser().getSiteId());
        }
    }

    @Override
    public void save() {

    }

    @Override
    public void delete() {

    }

    public TreeNode initOrgTree(TreeNode treeNode) {
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        TreeNode orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        } else {
            // 用户在科室级别时, 可选择所在科室
            TreeNode node = new DefaultTreeNode("org", org, null);
            node.setSelectable(org.getOrgType() == 4);
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedDept = null;
        Date date = new Date();
        selected = new PurchaseInfo();
        selected.setUid(UUID.randomUUID().toString().replace("-", ""));
        selected.setApplicant(account);
        selected.setApplicantId(account.getId());
        selected.setApplicationDate(new Date());
        selected.setApplicationStatus(BudgetConstant.BUDGET_APPLICATION_STATUS_PENDING);
        selected.setApprovalProgress(0);
        selected.setCreatedBy(account.getLoginName());
        selected.setCreatedDate(date);
        selected.setLastModifiedBy(account.getLoginName());
        selected.setLastModifiedDate(date);
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        selectedDept = selected.getApplicationDept();
    }

    @Override
    public void prepareView() {
        crudActionName = "View";
    }

    @Override
    public void prepareDelete() {
        crudActionName = "Delete";
    }

    @Override
    protected GenericRepository<PurchaseInfo> getDAO() {
        return dao;
    }
}