package com.ge.apm.view.budget;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.budget.BudgetPersistService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExportExcelUtil;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ViewScoped
@ManagedBean
public class YearPurchaseInfoController extends JpaCRUDController<YearPurchaseInfo> {
    private static Logger logger = LoggerFactory.getLogger(YearPurchaseInfoController.class);

    private UserAccount account = UserContextService.getCurrentUserAccount();

    /*接口类*/
    private YearPurchaseInfoRepository dao;
    private I18nMessageRepository i18nDao;
    private OrgInfoRepository orgDao;
    private UserContextService userContextService;
    private BudgetPersistService persistService;
    private CustomConfigService ccService;
    private UaaService uaaService;
    private BlobFileService blobService;
    private BlobObjectRepository blobDao;
    private OrgInfoTreeService orgInfoTreeService;

    /*属性类*/
    private boolean resultStatus;
    @Getter@Setter
    private List<V2_BlobObject> attachements;
    @Getter@Setter
    private YearPurchaseInfo selected;
    @Getter@Setter
    private OrgInfo org;
    @Getter@Setter
    private CustomConfigObject customConfig;
    @Getter@Setter
    private TreeNode orgTree;
    @Getter@Setter
    private TreeNode orgTreeEdit;
    @Getter@Setter
    private StreamedContent zipFile;
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
    @Getter@Setter
    private Map<String, Boolean> selectedMap; //复选
    @Getter@Setter
    private List<YearPurchaseInfo> currentPage;
    @Getter@Setter
    private String filterClinicalDeptOrg; //选定的所属科室
    @Getter@Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter@Setter
    private OrgInfo selectSite;
    @Getter@Setter
    private Set<String> clinicalDeptOrgUidSet;
    @Getter@Setter
    private Sort lastPageSort; //export use

    @Override
    protected void init() {
        dao = WebUtil.getBean(YearPurchaseInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        persistService = WebUtil.getBean(BudgetPersistService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);

        org = orgDao.findById(account.getOrgInfoId());
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 0);
        TreeNode treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(treeNode);
        orgTreeEdit = initOrgTree(treeNodeEdit);
        this.selectedMap = new HashMap();

        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if (!StringUtils.isEmpty(actionName) && "Edit".equals(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(dao.findByUid(selectedId));
        }else if (!StringUtils.isEmpty(actionName) && "Create".equals(actionName)) {
            prepareCreate();
        }
        loadClinicalDeptList();
    }

    public void setUserSearchFilters() {
        if (null != assetGroup) {
            this.updateSearchFilter(new SearchFilter("assetGroup", SearchFilter.Operator.EQ,  assetGroup, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetGroup".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(functionType)) {
            this.updateSearchFilter(new SearchFilter("functionType", SearchFilter.Operator.LIKE,  functionType, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "functionType".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(assetName)) {
            this.updateSearchFilter(new SearchFilter("assetName", SearchFilter.Operator.LIKE,  assetName, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetName".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(assetBrand)) {
            this.updateSearchFilter(new SearchFilter("assetBrand", SearchFilter.Operator.LIKE,  assetBrand, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetBrand".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
    }

    public void reset() {
        searchFilters.clear();
        assetGroup = null;
        functionType = null;
        assetName = null;
        assetBrand = null;
    }

    @Override
    protected Page<YearPurchaseInfo> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        setUserSearchFilters();
        Page<YearPurchaseInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPage = res.getContent();
        lastPageSort = pageRequest.getSort();
        return res;
    }

    public void assetGroupChangeListener() {
        if (assetGroup == null) {
            assetGroupSecondaryList = Collections.emptyList();
        } else {
            assetGroupSecondaryList = i18nDao.getByMsgTypeAndSiteId("assetGroupSecondary_" + assetGroup.intValue(), userContextService.getLoginUser().getSiteId());
        }
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
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

    public String getEditPage(String uid) {
        String operation = "YearEdit.xhtml?actionName=Edit&selectedid=" + uid;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public Object valueOfToString(Object obj) {
        if (null != obj) {
            return "" + obj;
        }
        return null;
    }

    @Override
    public void save() {
        String str;
        try {
            if (selected.getApplicationDeptId() == null) {
                OrgInfo orgInfo = orgDao.findByUid(clinicalDeptOrgUidSet.stream().findFirst().get());
                selected.setApplicationDeptId(orgInfo.getId());
                selected.setDeptName(orgInfo.getName());
            }
            dao.save(selected);
            str = "SaveSuccess";
        } catch (Exception e) {
            str = "PersistenceErrorOccured";
        }
        MessageUtil.outPutReturnFacesMessage(str, FacesMessage.SEVERITY_INFO);
    }

    @Override
    public void delete(YearPurchaseInfo purchaseInfo) {
        dao.delete(purchaseInfo);
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            attachements.add(blobObject);
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetInfo);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }


    public List<V2_BlobObject> getAttachList(String uid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, uid));
            attachements = blobDao.findBySearchFilter(sfs);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
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

    @Override
    public void onAfterNewObject(YearPurchaseInfo object, boolean isOK) {
        resultStatus = isOK;
        if (isOK) {
            attachements.forEach(item -> {
                if (item.getBoId() == null) {
                    item.setBoId(object.getId());
                    item.setBoUuid(object.getUid());
                    blobDao.save(item);
                }
            });
        }
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public void exportExcel() throws IOException {
        List<YearPurchaseInfo> list = getExportList();
        String[] title = new String[]{"年份", "科室", "设备名称", "设备型号", "数量", "金额"};
        String[] column = new String[]{"purchaseYear", "deptName", "assetName", "functionType", "quantity", "budgetAmount"};
        zipFile = new DefaultStreamedContent(new ByteArrayInputStream(ExportExcelUtil.ExportExcel(list, title, column, 1000)), "application/vnd.ms-excel", "yearPurchase.xlsx");
    }

    public List<YearPurchaseInfo> getExportList() {
        List<String> assetUids = selectedMap.entrySet().stream().filter(item -> item.getValue()).map(item -> item.getKey()).collect(Collectors.toList());
        List<YearPurchaseInfo> resultList;
        if (assetUids.isEmpty()) {
            resultList = dao.findBySearchFilter(searchFilters, lastPageSort);
        } else {
            resultList = dao.findByUidIn(assetUids, lastPageSort);
        }
        return resultList;
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

    public void onSelect() {
        if (selected != null && selected.getId() != null) {
            selected = dao.findById(selected.getId());
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPage.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (YearPurchaseInfo item : currentPage) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    @Override
    public void prepareCreate() {
        crudActionName = "Create";
        selectedDept = null;
        Date date = new Date();
        selected = new YearPurchaseInfo();
        selected.setUid(UUID.randomUUID().toString().replace("-", ""));
        selected.setSiteId(account.getSiteId());
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
    protected GenericRepository<YearPurchaseInfo> getDAO() {
        return dao;
    }
}