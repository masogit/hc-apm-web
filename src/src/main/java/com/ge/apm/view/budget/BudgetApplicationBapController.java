package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.budget.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.budget.AccessRecordsService;
import com.ge.apm.service.budget.BudgetApplicationExportService;
import com.ge.apm.service.budget.BudgetFileDynamicTemplate;
import com.ge.apm.service.budget.BudgetPersistService;
import com.ge.apm.service.common.SysRoleConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.budget.form.BudgetAssetSearchForm;
import com.ge.apm.view.budget.form.BudgetSearchForm;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AccessRecordsFeature;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.PageUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author lyj
 */
@ViewScoped
@ManagedBean(name = "budgetController")
public class BudgetApplicationBapController extends GenericCRUDUUIDController<BudgetApplication> {
    private static Logger logger = LoggerFactory.getLogger(BudgetApplicationBapController.class);

    private Page<BudgetApplication> emptyPage = new PageImpl<>(Collections.emptyList());
    private UserAccount account = UserContextService.getCurrentUserAccount();
    @Getter  @Setter
    private Integer tabIndex = 0;
    @Getter  @Setter
    private Integer tabIndexList = 0;
    public final static String CREATE_ACTION = "Create";
    public final static String EDIT_ACTION = "Edit";
    private BlobFileService blobService;
    private UaaService uaaService;

    /**
     * 编辑数据主键
     */
    @Getter  @Setter
    private String editId;
    @Getter  @Setter
    private BudgetApplicationAsset curSelectApplicationBudgetAsset = new BudgetApplicationAsset();
    @Getter  @Setter
    private BudgetAssetInfo curSelectBudgetAssetInfo;
    @Getter  @Setter
    private List<V2_BlobObject> attachList;
    private PageUtil<BudgetApplication> pageUtil = new PageUtil<>();
    // 弹窗列表
    @Getter  @Setter
    private List<BudgetAssetInfo> optionalBudgetAssets = new ArrayList<>();
    private List<SearchFilter> assetSearchFilters;
    private BudgetApplicationRepository dao;
    private BudgetAssetInfoRepository budgetAssetDao;
    private BlobObjectRepository blobObjectRepository;
    private OrgInfoRepository orgDao;

    private PurchaseBudgetConfigRepository purchaseBudgetConfigRepository;

    private UserContextService userContextService;
    private BudgetPersistService persistService;
    private BudgetApplicationExportService budgetApplicationExportService;
    @Getter  @Setter
    private BudgetSearchForm searchForm = new BudgetSearchForm();
    @Getter  @Setter
    private BudgetAssetSearchForm assetSearchForm = new BudgetAssetSearchForm();
    @Getter  @Setter
    private BudgetAssetInfo selectedAsset;
    @Getter  @Setter
    private OrgInfo org;
    @Getter  @Setter
    private TreeNode orgTree;
    @Getter  @Setter
    private TreeNode orgImportTree;
    @Getter  @Setter
    private TreeNode orgTreeEdit;
    @Getter  @Setter
    private OrgInfo selectedDept;
    @Getter @Setter
    private StreamedContent exportFile;
    @Getter @Setter
    private List<String> removeFileBlobId = new ArrayList<>();
    @Getter @Setter
    private String searchDeptName;

    private FileDownloadService fileDownloadService;
    @Override
    protected void init() {
        AccessRecordsService accessRecordsService = WebUtil.getBean(AccessRecordsService.class);
        accessRecordsService.accessRecords(AccessRecordsFeature.BUDGET.getFeatureCode(), "/budget/application/List.xhtml", UserContextService.getCurrentUserAccount());
        dao = WebUtil.getBean(BudgetApplicationRepository.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        budgetAssetDao = WebUtil.getBean(BudgetAssetInfoRepository.class);
        purchaseBudgetConfigRepository = WebUtil.getBean(PurchaseBudgetConfigRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        budgetApplicationExportService = WebUtil.getBean(BudgetApplicationExportService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        persistService = WebUtil.getBean(BudgetPersistService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        uaaService = WebUtil.getBean(UaaService.class);
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelectedForReturn(account.getSiteUID(), null);
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        TreeNode treeNodeImport = uaaService.getOrgTreeByParentIdWithSelectedChildren(account.getSiteUID(), account.getUid());
        orgImportTree = initOrgTree(treeNodeImport);
        orgTree = initOrgTree(treeNode);
        orgTreeEdit = initOrgTree(treeNodeEdit);  //
        String action = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "action");
        this.editId = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "editId");
        attachList = new ArrayList<>();
        String tabIndexStr = (String) WebUtil.getRequestParameter("tabIndex");
        if(StringUtils.isNotBlank(tabIndexStr)){
            this.tabIndex = Integer.parseInt(tabIndexStr);
        }
        String tabIndexList = (String) UrlEncryptController.getValueFromMap(WebUtil.getRequestParameter("str"), "tabIndexList");
        if(StringUtils.isNotBlank(tabIndexList)){
            this.tabIndexList = Integer.parseInt(tabIndexList);
        }
        try {
            curSelectApplicationBudgetAsset = new BudgetApplicationAsset();
            if (BudgetApplicationBapController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
            } else if (BudgetApplicationBapController.EDIT_ACTION.equals(action)) {
                this.prepareEdit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected GenericRepositoryUUID<BudgetApplication> getDAO() {
        return dao;
    }

   /* public List<BudgetAssetInfo> autoCompleteAssetName(String query) {
        optionalBudgetAssets.clear();
        List<BudgetAssetInfo> assetInfoList = budgetAssetDao.findByTenantUIDAndIsDeleted(selectedDept.getTenantUID(),0);
        assetInfoList = assetInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(assetInfoList)?assetInfoList:new ArrayList<BudgetAssetInfo>();
    }*/

    public void onBudgetAssetInfoChange(){
        this.curSelectApplicationBudgetAsset.setApplicationAssetName(this.curSelectBudgetAssetInfo.getName());
        this.curSelectApplicationBudgetAsset.setApplicationAssetCategory(this.curSelectBudgetAssetInfo.getApplicationAssetCategory());
        this.curSelectApplicationBudgetAsset.setRecommendedBrand(this.curSelectBudgetAssetInfo.getAssetBrand());
        this.curSelectApplicationBudgetAsset.setModel(this.curSelectBudgetAssetInfo.getAssetModel());
        this.curSelectApplicationBudgetAsset.setStandards(this.curSelectBudgetAssetInfo.getStandards());
    }

    public boolean curModelDisables(){
        return "View".equals(crudActionName)?true:false;
    }


    public void onTabChange(TabChangeEvent event) {
        selected = null;
        switch (event.getTab().getId()) {
            case "s1":
                tabIndex = 1;
                break;
            case "s2":
                tabIndex = 2;
                break;
            case "s3":
                tabIndex = 3;
                break;
            case "s4":
                tabIndex = 4;
                break;
            case "s5":
                tabIndex = 5;
                break;
            default:
                tabIndex = 0;
                break;
        }
    }


    protected Page<BudgetApplication> loadData(PageRequest pageRequest) {
        if (userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)
                || userContextService.hasRole(SysRoleConstant.ROLE_ASSET_STAFF)
                || userContextService.hasRole(SysRoleConstant.ROLE_DEPT_HEAD)
                ||userContextService.hasRole(SysRoleConstant.ROLE_CLINICAL_STAFF)) {
            this.searchFilters = this.getSearchFilter(this.tabIndex);
        } else {
            return emptyPage;
        }
        List<BudgetApplication> dataList = persistService.loadDataList(this.searchFilters);
        return pageUtil.pageNum(dataList,pageRequest);
    }

    public void exportExcel(String orderType){
        this.searchFilters = this.getSearchFilter(this.tabIndex);
        List<BudgetApplication> dataList = persistService.loadDataList(this.searchFilters);
        List<Map<String, Object>> budgetDataList = persistService.createBudgeApplicationData(dataList,orderType);
        try {
//            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/budget/application/excel/APM_Budget_BAP_Exp_Template.xlsx");
            InputStream stream = fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.BUDGET_BAP_EXP.getCode(), SystemTemplateEnum.BUDGET_BAP_EXP.getFileType());
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            persistService.exportData(doc, budgetDataList,orderType);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Budget_BAP.xlsx");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(BudgetApplicationBapController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<SearchFilter> getSearchFilter(Integer tabIndex) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        updateSearchFilter(new SearchFilter("orderType", SearchFilter.Operator.EQ,
                BudgetApplication.OrderType.BAP.toString()));
        updateSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
        removeFilterOnField("applicationStatus");removeFilterOnField("applicationStep");removeFilterOnField("confirmStatus");
        if (tabIndex == 0) {//待办
            searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1,4)));
        } else if(tabIndex == 1){//待审核
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "PendingApproval"));
        } else if(tabIndex == 2){//待完成
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "PendingComplete"));
        } else if(tabIndex == 3){//完成
            searchFilters.add(new SearchFilter("applicationStep", SearchFilter.Operator.EQ, "Complete"));
        } else if(tabIndex == 4){//采购信息确认
            searchFilters.add(new SearchFilter("confirmStatus", SearchFilter.Operator.IsNull, null));
        }else if(tabIndex == 5){//全部
            searchFilters.add(new SearchFilter("applicationStatus", SearchFilter.Operator.IN, Arrays.asList(1, 2, 3,4,5,6)));
        }

        // 临床科室主任, 医学工程处主任  工程师   // 临床科室人员
        queryByUserRole(org);
        return searchFilters;
    }


    /**
     * 根据参数跳转相关模式
     *
     * @return
     */
    public String intoActionModel(String action) throws ParseException {
        if(BudgetApplicationBapController.CREATE_ACTION.equals(action)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<PurchaseBudgetConfig> purchaseBudgetConfigList = purchaseBudgetConfigRepository.queryEffiveConfig(account.getTenantUID(),
                    account.getSiteUID(),sdf.parse(sdf.format(new Date())));
            String url = new String();
            if(CollectionUtils.isNotEmpty(purchaseBudgetConfigList)){
                url = UrlEncryptController.encodeUrlParam("Edit?action=" + BudgetApplicationBapController.CREATE_ACTION+"&tabIndexList="+tabIndex);
            }
            WebUtil.addSuccessMessage("年度采购入口未开启，请检查后重试！");
            return url;
        }else if(BudgetApplicationBapController.EDIT_ACTION.equals(action)){
            return UrlEncryptController.encodeUrlParam("Edit?action=" + BudgetApplicationBapController.EDIT_ACTION
                    + "&editId=" + this.selected.getId()+"&tabIndexList="+tabIndex);

        }else{
            return null;
        }
    }

    public void doubleClick(SelectEvent event) {
        String str = "action=" + BudgetApplicationBapController.EDIT_ACTION + "&editId=" + this.selected.getId()+"&tabIndexList="+tabIndex;
        String url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(str);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    public void saveApplication() throws InvocationTargetException, IllegalAccessException {

        persistService.handleCommonField(selected,1,BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString(),
                account,crudActionName,this.curSelectBudgetAssetInfo);
        boolean isOk = persistService.saveApplication(selected,this.attachList,this.removeFileBlobId);
        this.curSelectApplicationBudgetAsset = selected.getBudgetAssets().iterator().next();
        actionMessage(isOk);
    }

    public String confirmApplication() {
        this.selected.setConfirmStatus("Confirmed");
        boolean isOk = persistService.saveApplication(selected,this.attachList,this.removeFileBlobId);
        this.curSelectApplicationBudgetAsset = selected.getBudgetAssets().iterator().next();
        actionMessage(isOk);
        return this.backList();
    }

    public String applicationDataFlow(Integer applicationStatus,String applicationStep) {
        if(BudgetApplication.APPLICATIONSTEP.PendingApproval.name().equals(applicationStep)){
            if(!checkSubmit()){
                return "";
            }
            BudgetApplicationAsset applicationAsset = selected.getBudgetAssets().iterator().next();
            if(applicationAsset.getApplicationPrice().compareTo(new BigDecimal(500000)) == 1){
                if(attachList.isEmpty()){
                    WebUtil.addSuccessMessage("失败:附件必须上传！");
                    return "";
                }
            }
            //无可用的审批流
            boolean resultMultiLevelApproval = persistService.checkMultiLevelApproval(selected);
            if(!resultMultiLevelApproval){
                WebUtil.addSuccessMessage("失败:无可用的审批流,请检查后重试！");
                return "";
            }
            persistService.handleCommonField(selected,applicationStatus,applicationStep,
                    account,crudActionName,this.curSelectBudgetAssetInfo);
            persistService.submitApplication(selected,this.attachList);
        }else{
            persistService.handleCommonField(selected,applicationStatus,applicationStep,
                    account,crudActionName,this.curSelectBudgetAssetInfo);
            persistService.saveApplication(selected,this.attachList,this.removeFileBlobId);
        }
        return this.backList();
    }

    public boolean checkSubmit(){
        boolean result = true;
        try{
            BudgetApplicationAssetVo budgetApplicationAssetVo = new BudgetApplicationAssetVo();
            BeanUtils.copyProperties(budgetApplicationAssetVo, curSelectApplicationBudgetAsset);
            budgetApplicationAssetVo.setApplicationDeptName(selected.getApplicationDeptName());
            budgetApplicationAssetVo.setWardCode(selected.getWardCode());
            ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<BudgetApplicationAssetVo>> checkSet = validator.validate(budgetApplicationAssetVo, BudgetApplicationGroup.class);

            if(CollectionUtils.isNotEmpty(checkSet)){
                for (ConstraintViolation<BudgetApplicationAssetVo> c : checkSet){
                    WebUtil.addSuccessMessage(c.getMessage());
                }
                result = false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return result;
    }

    public String backList() {
        return "List.xhtml?faces-redirect=true&tabIndex="+tabIndexList;
    }


    @Override
    public void delete() {
        BudgetApplication application = dao.findById(selected.getId());
        boolean isOk = persistService.deleteApplication(application);
        selected = null;
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetApplication") + WebUtil.getMessage("Blank")
                + "删除" + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
    }

    public void saveAsset() {
        // 保存时设置科室·
        //selectedAsset.setDept(selectedDept);
        //selectedAsset.setDeptId(selectedDept.getId());
        selectedAsset.setTenantUID(account.getTenantUID());
        selectedAsset.setInstitutionUID(account.getInstitutionUID());
        selectedAsset.setHospitalUID(account.getHospitalUID());
        selectedAsset.setSiteUID(account.getSiteUID());
        boolean isOk = persistService.saveAsset(selectedAsset);
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetAsset") + WebUtil.getMessage("Blank")
                + WebUtil.getMessage("Create") + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
        this.optionalBudgetAssets = budgetAssetDao.findBySearchFilter(assetSearchFilters,new Sort(Sort.Direction.DESC, "createdDate"));
    }

    private void actionMessage(boolean isOk) {
        WebUtil.addSuccessMessage(WebUtil.getMessage("BudgetApplication") + WebUtil.getMessage("Blank")
                + WebUtil.getMessage(crudActionName) + WebUtil.getMessage("Blank")
                + (isOk ? WebUtil.getMessage("Successfully") : WebUtil.getMessage("Failed")));
    }

    public void search() {
        selected = null;
        clearSearchFilter();
        addSearchFilter();
    }

    public void resetSearch() {
        selected = null;
        clearSearchFilter();
        searchForm = new BudgetSearchForm();
        addSearchFilter();
    }

    private void clearSearchFilter() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        } else {
            searchFilters.clear();
        }
    }

    private void addSearchFilter() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        if (null != searchForm.getDept()) {
            OrgInfo orgInfo = searchForm.getDept();
            searchFilters.add(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, orgInfo.getId()));
        }
        if (null != searchForm.getDateStart()) {
            updateSearchFilter(new SearchFilter("applicationDate", SearchFilter.Operator.GTE,  formatter.format(searchForm.getDateStart())));
        }
        if (null != searchForm.getDateEnd()) {
            this.searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.LT, formatter.format(new DateTime(searchForm.getDateEnd()).plusDays(1).toDate())));
        }
        if (null != searchForm.getApplicantYear()) {
            this.searchFilters.add(new SearchFilter("applicationDate", SearchFilter.Operator.EQ, formatter.format(searchForm.getApplicantYear())));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicationNum())) {
            updateSearchFilter(new SearchFilter("applicationNum", SearchFilter.Operator.LIKE, searchForm.getApplicationNum()));
        }
        if (StringUtils.isNotBlank(searchForm.getApplicantName())) {
            updateSearchFilter(new SearchFilter("applicantName", SearchFilter.Operator.EQ, searchForm.getApplicantName()));
        }
        if (StringUtils.isNotBlank(searchForm.getAssetName())) {
            updateSearchFilter(new SearchFilter("applicationAssetName", SearchFilter.Operator.LIKE, searchForm.getAssetName()));
        }
    }

    private void queryByUserRole(OrgInfo org){
        if(org == null) return;
        if (userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)
                || userContextService.hasRole(SysRoleConstant.ROLE_DEPT_HEAD)) {
            // 临床科室主任可以查看自己科室的数据, 医学工程处主任可以查看当前所属机构的单子   // 临床科室人员只能查看自己创建的数据
            int switchArg = userContextService.hasRole(SysRoleConstant.ROLE_ASSET_HEAD)?org.getOrgLevel():org.getOrgType();
            switch (switchArg) {
                case 4:
                    // 科室
                    searchFilters.add(new SearchFilter("applicationDeptId", SearchFilter.Operator.EQ, org.getId()));
                    break;
                case 3:
                    // 院区
                    searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, org.getSiteUID()));
                    break;
                case 2:
                    // 医院
                    searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, org.getHospitalUID()));
                    break;
                case 1:
                    // 集团
                    searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, org.getInstitutionUID()));
                    break;
                default:
                    // 租户
                    searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, org.getTenantUID()));
            }
        } else {
            searchFilters.add(new SearchFilter("applicantId", SearchFilter.Operator.EQ, account.getId()));
        }
    }
    public void calcAmount(BudgetApplicationAsset budgetApplicationAsset) {
        if(budgetApplicationAsset.getApplicationPrice()!= null && budgetApplicationAsset.getApplicationQuantity()!= null){
            budgetApplicationAsset.setApplicationAmount(budgetApplicationAsset.getApplicationPrice()
                    .multiply(new BigDecimal(budgetApplicationAsset.getApplicationQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            //budgetApplicationAsset.setConfirmPrice(budgetApplicationAsset.getApplicationPrice());
            //budgetApplicationAsset.setConfirmQuantity(budgetApplicationAsset.getApplicationQuantity());
        }
    }

    public void calcAmountOffline(BudgetApplicationAsset budgetApplicationAsset) {
        if(budgetApplicationAsset.getOfflineApplicationPrice()!= null && budgetApplicationAsset.getOfflineApplicationQuantity()!= null){
            budgetApplicationAsset.setOfflineApplicationAmount(budgetApplicationAsset.getOfflineApplicationPrice()
                    .multiply(new BigDecimal(budgetApplicationAsset.getOfflineApplicationQuantity()))
                    .setScale(2, RoundingMode.HALF_UP));
            //budgetApplicationAsset.setConfirmPrice(budgetApplicationAsset.getOfflineApplicationPrice());
            //budgetApplicationAsset.setConfirmQuantity(budgetApplicationAsset.getOfflineApplicationQuantity());
        }
    }

    public void prepareCreateAsset() {
        selectedAsset = new BudgetAssetInfo();
        selectedAsset.setIsDeleted(0);
        selectedAsset.setCreatedBy(account.getLoginName());
        selectedAsset.setCreatedDate(new Date());
        selectedAsset.setLastModifiedBy(account.getLoginName());
        selectedAsset.setLastModifiedDate(new Date());
        searchAsset();
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
            node.setSelectable(Objects.equals(org.getOrgType(), 4));
            node.setRowKey("org_" + org.getId());
            rootNode.getChildren().add(node);
        }
        rootNode.setExpanded(true);
        return orgTree;
    }

    /**
     * 查询 区域部门选择
     * @param event
     */
    public void onSearchTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        searchForm.setDept(orgInfo);
    }

    /**
     * 编辑区域部门选择
     * @param event
     */
    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        selectedDept = orgInfo;
        this.selected.setApplicationDeptName(selectedDept.getName());
        this.selected.setApplicationDept(selectedDept);
    }



    public void clearSelected() {
        if (StringUtils.isEmpty(selected.getId())) {
            selected = null;
        }
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
        selected = new BudgetApplication();
        persistService.initCreateObject(selected,this.curSelectApplicationBudgetAsset,account,BudgetApplication.OrderType.BAP.toString());
        this.selectedDept = selected.getApplicationDept();
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        buildCurDetail(this.editId);
    }

    @Override
    public void prepareView() {
        crudActionName = "View";
        buildCurDetail(this.editId);
    }

    public void buildCurDetail(String id){
        this.selected = this.dao.findById(id);
        this.curSelectApplicationBudgetAsset = CollectionUtils.isNotEmpty(this.selected.getBudgetAssets())?
                this.selected.getBudgetAssets().iterator().next():new BudgetApplicationAsset();
        this.selectedDept = this.selected.getApplicationDept();
        this.selected.setApplicationDeptName(this.selectedDept.getName());
        if(this.curSelectApplicationBudgetAsset!=null ){
            this.attachList = blobObjectRepository.findByBoUuid(this.curSelectApplicationBudgetAsset.getId());
        }
    }

    public boolean budgetApprovalsDisplay(){
        return (CollectionUtils.isNotEmpty(this.selected.getBudgetApprovals())&&this.selected.getBudgetApprovals().size()>0)?true:false;
    }

    public void removeAttachmentById(String blobId) {
        this.removeFileBlobId.add(blobId);
        attachList.removeAll(attachList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject ) {
            attachList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_BUDGET);
           /* if (Optional.ofNullable(selected.getId()).isPresent()) { // 编辑
                blobObject.setBoUuid(selected.getBudgetAssets().iterator().next().getId());
            }*/
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }
    public void searchAsset() {
        clearAssetSearchFilter();
        addAssetSearchFilter();
        optionalBudgetAssets.clear();
        this.optionalBudgetAssets = budgetAssetDao.findBySearchFilter(assetSearchFilters,new Sort(Sort.Direction.DESC, "createdDate"));
    }

    public void resetAssetSearch() {
        assetSearchForm = new BudgetAssetSearchForm();
        searchAsset();
    }

    private void clearAssetSearchFilter() {
        if (assetSearchFilters == null) {
            assetSearchFilters = new ArrayList<>();
        } else {
            assetSearchFilters.clear();
        }
    }
    private void updateAssetSearchFilter(SearchFilter searchFilter) {
        removeAssetFilterOnField(searchFilter.fieldName);
        assetSearchFilters.add(searchFilter);
    }

    private void removeAssetFilterOnField(String fieldName) {
        assetSearchFilters.removeAll(assetSearchFilters.stream().filter(item -> fieldName.equals(item.fieldName)).collect(Collectors.toList()));
    }

    private void addAssetSearchFilter() {
        if (StrUtil.isNotEmpty(assetSearchForm.getApplicationAssetCategory())) {
            updateAssetSearchFilter(new SearchFilter("applicationAssetCategory", SearchFilter.Operator.EQ, assetSearchForm.getApplicationAssetCategory()));
        }
        if (StrUtil.isNotEmpty(assetSearchForm.getStandards())) {
            updateAssetSearchFilter(new SearchFilter("standards", SearchFilter.Operator.LIKE, assetSearchForm.getStandards()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetBrand())) {
            updateAssetSearchFilter(new SearchFilter("assetBrand", SearchFilter.Operator.LIKE, assetSearchForm.getAssetBrand()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetModel())) {
            updateAssetSearchFilter(new SearchFilter("assetModel", SearchFilter.Operator.LIKE, assetSearchForm.getAssetModel()));
        }
        if (StringUtils.isNotBlank(assetSearchForm.getAssetName())) {
            updateAssetSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, assetSearchForm.getAssetName()));
        }
        // 科室
//        updateAssetSearchFilter(new SearchFilter("deptId", SearchFilter.Operator.EQ, selectedDept.getId()));
        // 现在只要是同一租户下的设备都能看到
        updateAssetSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, account.getTenantUID()));
        updateAssetSearchFilter(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, 0));
    }

    public void exportAll()  {
        try {
            buildCurDetail(selected.getId());
            if(this.curSelectApplicationBudgetAsset!=null ){
                this.attachList = blobObjectRepository.findByBoUuid(this.curSelectApplicationBudgetAsset.getId());
            }
            exportFile = budgetApplicationExportService.exportZip(curSelectApplicationBudgetAsset, attachList, BudgetApplicationExportService.FILE_NAME_ROOT_YEAR);

        }catch (IOException ex) {
            java.util.logging.Logger.getLogger(BudgetApplicationBapController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean editButtonDisplay(){
        boolean render = true;
        if(!BudgetApplication.APPLICATIONSTEP.ToSubmitted.toString().equals(this.selected.getApplicationStep())){
            render = false;
        }
        return render;
    }

    /**  **************************批量导入************************** **/
    @Getter  @Setter
    private OrgInfo selectedImportOrg;
    @Getter  @Setter
    private String selectedImportOrgName;
    @Getter  @Setter
    private UploadedFile file;
    @Getter  @Setter
    private List<BudgetApplication> errorImportList;
    @Getter  @Setter
    private StreamedContent budgetTemplateFile;

    public void handleFileUploadImport(FileUploadEvent event) throws IOException, ParseException, NoSuchFieldException, IllegalAccessException {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,"导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        if(this.selectedImportOrg==null){
            WebUtil.addErrorMessage("请先选择所属院区！",null);
            return;
        }
        errorImportList = persistService.handleExcel(file, account,selectedImportOrg,BudgetApplication.OrderType.BAP.toString());
        //有错误数据 warning 没有错误数据  info
        if (CollectionUtils.isNotEmpty(errorImportList)){
            message = new FacesMessage(FacesMessage.SEVERITY_WARN,"导入完成", event.getFile().getFileName() + " 处理完成，但有错误数据！");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void exportBudgetExcelTemplate() throws IOException {
        List<OrgInfo> orgList = new ArrayList<OrgInfo>();
        if(this.selectedImportOrg != null){
            orgList = orgDao.findBySiteUIDAndOrgType(this.selectedImportOrg.getUid(), 4);
        }
        BudgetFileDynamicTemplate template = new BudgetFileDynamicTemplate();
        template.exportSampleAsset(orgList);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        budgetTemplateFile = new DefaultStreamedContent(template.getInputStream(), "application/vnd.ms-excel", "Budget_Import_Template.xlsx");
        logger.info(" export budget excel template successful. ");
    }

    public List<BudgetApplication> getErrorImportList() {
        return errorImportList;
    }

    /**
     * 编辑区域部门选择
     * @param event
     */
    public void onSelectedImportTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        this.setSelectedImportOrgName(orgInfo.getName());
        this.setSelectedImportOrg(orgInfo);
    }

    /**
     * 删除按钮
     * @param
     */
    public boolean deleteButtonStatus() {
        boolean args = true;
        if(this.selected != null &&
                (this.selected.getApplicationStatus() == 1||
                        ("EXCEL_IMPORT".equals(this.selected.getDataSource()) && ("Unpurchased".equals(this.selected.getProcureStatus()))))){
            args = false;
        }
        return args;
    }
    /**  **************************批量导入**************************           **/


    public void treeSeachCondition(String type){
        if("list".equals(type)){
            TreeNode treeNode = null;
            if(StrUtil.isEmpty(searchDeptName)){
                treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
            }else{
                treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4, searchDeptName);
            }
            orgTree = initOrgTree(treeNode);
        }else{
            TreeNode treeNodeEdit = null;
            if(StrUtil.isEmpty(searchDeptName)){
                treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
            }else{
                treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4, searchDeptName);
            }
            orgTreeEdit = initOrgTree(treeNodeEdit);
        }

    }

}
