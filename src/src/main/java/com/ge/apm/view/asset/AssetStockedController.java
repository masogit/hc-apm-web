package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetStockedController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository dao = null;
    private UaaService uaaService;
    private String filterClinicalDeptOrg;
    private AutoCompleteService autoCompleteService;
    private AssetInstallationService assetInstallationService;
    private AssetOutStockService assetOutStockService;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private UserAccount currentUser;
    private ArrivalListItem searchItem;
    private Supplier supplier;
    private OrgInfo recruiteDept;
    private OrgInfo org;
    private TreeNode orgTreeEdit;
    private String tabId;
    private Date varArrivalDateTo = null;
    private String arrivalFormatTime = null;
    private Date startDate;
    private Date endDate;
    private Double startPrice;
    private Double endPrice;
    private String searchDeptName;
    private List<Supplier> supplierList;
    private List<ArrivalListItem> selectedItems = ListUtil.newArrayList();
    @Getter @Setter
    private StreamedContent exportInStockFile;
    @Getter @Setter
    private StreamedContent exportOutStockFile;

    @Override
    protected void init() {
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInstallationService = WebUtil.getBean(AssetInstallationService.class);
        assetOutStockService = WebUtil.getBean(AssetOutStockService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        SupplierService supplierService = WebUtil.getBean(SupplierService.class);
        autoCompleteService=WebUtil.getBean(AutoCompleteService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        supplierList = supplierService.getSupplierList();
        org = orgInfoRepository.findById(currentUser.getOrgInfoId());
        orgTreeEdit = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4));
        initSearchItem();
    }

    public void initSearchItem() {
        searchFilters = new ArrayList<>();
        supplier = new Supplier();
        startDate = null;
        endDate = null;
        startPrice = null;
        endPrice = null;
        searchItem = new ArrivalListItem();
        searchItem.setArrivalList(new ArrivalList());
    }

    @Override
    protected ArrivalListItemRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<>();
        if (!this.hasFilterOnField("arrivalList.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, currentUser.getUid(), "1*2+"));
        }
        tabId = tabId == null ? "auditInStocked" : tabId;
        switch (tabId) {
            case "auditInStocked":
                searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, 1, "1*"));
                break;
            case "inStockedRecord":
                searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.IN, ListUtil.newArrayList(2, 3, 4), "1*"));
                break;
            case "outStockedRecord":
                searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.IN, ListUtil.newArrayList(3, 4), "1*"));
                break;
            default:
                break;
        }
        if (ObjectUtil.isNotEmpty(startDate)) {
            searchFilters.add(new SearchFilter("installDate", SearchFilter.Operator.GTE, startDate, "1*"));
        }
        if (ObjectUtil.isNotEmpty(endDate)) {
            searchFilters.add(new SearchFilter("installDate", SearchFilter.Operator.LTE, endDate, "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInstallDate())) {
            searchFilters.add(new SearchFilter("inStockNo", SearchFilter.Operator.LIKE, searchItem.getInStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInstallDate())) {
            searchFilters.add(new SearchFilter("outStockNo", SearchFilter.Operator.LIKE, searchItem.getOutStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getArrivalList().getName())) {
            searchFilters.add(new SearchFilter("arrivalList.assetName", SearchFilter.Operator.LIKE, searchItem.getArrivalList().getName(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(supplier.getId())) {
            searchFilters.add(new SearchFilter("arrivalList.supplierId", SearchFilter.Operator.EQ, supplier.getId(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getPurchasePrice())) {
            searchFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, searchItem.getPurchasePrice(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(startPrice)) {
            searchFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, startPrice, "1*"));
        }
        if (ObjectUtil.isNotEmpty(endPrice)) {
            searchFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.LTE, endPrice, "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getArrivalList().getFunctionType())) {
            searchFilters.add(new SearchFilter("arrivalList.functionType", SearchFilter.Operator.LIKE, searchItem.getArrivalList().getFunctionType(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getArrivalList().getInvoiceNum())) {
            searchFilters.add(new SearchFilter("arrivalList.invoiceNum", SearchFilter.Operator.LIKE, searchItem.getArrivalList().getInvoiceNum(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInStockNo())) {
            searchFilters.add(new SearchFilter("inStockNo", SearchFilter.Operator.LIKE, searchItem.getInStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getOutStockNo())) {
            searchFilters.add(new SearchFilter("outStockNo", SearchFilter.Operator.LIKE, searchItem.getOutStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getStockStatus())) {
            searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, searchItem.getStockStatus(), "1*"));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onTabChange(TabChangeEvent event) {
        tabId = event.getTab().getId();
    }

    public List<Supplier> autoCompleteSupplier(String query) {
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public void auditInStocked(ArrivalListItem arrivalListItem) {
        assetInstallationService.auditInStocked(UserContextService.getAccessToken(), arrivalListItem.getId());
    }

    public void bathAuditInStocked() {
        for (ArrivalListItem selectedItem : selectedItems) {
            assetInstallationService.auditInStocked(UserContextService.getAccessToken(), selectedItem.getId());
        }
    }

    public Double totalPurchasePrice() {
        return selectedItems.stream().mapToDouble(ArrivalListItem::getPurchasePrice).sum();
    }

    public void generatedInStockDoc() {
        if (selectedItems.stream().map(ArrivalListItem::getStockStatus).collect(Collectors.toList()).contains(4)) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("包含已出库的设备，无法生成入库单！");
            return;
        }
        String inStockNo = assetOutStockService.queryInStockNo("RK");
        for (ArrivalListItem selectedItem : selectedItems) {
            selectedItem.setInStockNo(inStockNo);
            selectedItem.setStockStatus(3);
        }
        dao.save(selectedItems);
    }

    public void generatedOutStockDoc() {
        if (selectedItems.stream().map(ArrivalListItem::getStockStatus).collect(Collectors.toList()).contains(4)) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("包含已出库的设备，无法生成出库单！");
            return;
        }
        if (ObjectUtil.isEmpty(recruiteDept)) {
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage("领用科室，不能为空！");
            return;
        }
        String inStockNo = assetOutStockService.queryOutStockNo("CK");
        Date now = DateUtil.now();
        List<String> assetIds = ListUtil.newArrayList();
        for (ArrivalListItem selectedItem : selectedItems) {
            selectedItem.setOutStockNo(inStockNo);
            selectedItem.setRecruiteDeptUid(recruiteDept.getUid());
            selectedItem.setClinicalDeptUID(recruiteDept.getUid());
            selectedItem.setRecruiter(currentUser.getUid());
            selectedItem.setOutStocDate(now);
            selectedItem.setStockStatus(4);
            assetIds.add(selectedItem.getAssetUID());
        }
        if (ObjectUtil.isNotEmpty(assetIds)) {
            assetInfoRepository.outStocked(assetIds, recruiteDept.getId(), recruiteDept.getUid(), recruiteDept.getName());
        }
        dao.save(selectedItems);
    }

    public void exportAll(String sockType) throws IOException {
        if (ObjectUtil.isEmpty(selectedItems)) {
            WebUtil.addErrorMessage("请选择打印设备！");
            return;
        }
        List<String> stockNos;
        List<Integer> stockStatus = selectedItems.stream().map(ArrivalListItem::getStockStatus).distinct().collect(Collectors.toList());
        if ("inStock".equals(sockType)) {
            stockNos = selectedItems.stream().map(ArrivalListItem::getInStockNo).distinct().collect(Collectors.toList());
        } else {
            stockNos = selectedItems.stream().map(ArrivalListItem::getOutStockNo).distinct().collect(Collectors.toList());
            if (stockStatus.size() > 1 || !stockStatus.contains(4)) {
                WebUtil.addErrorMessage("只能打印已出库的设备！");
                return;
            }
        }
        if (stockNos.size() > 1) {
            WebUtil.addErrorMessage("不同单号无法打印！");
            return;
        }
        if (ObjectUtil.isEmpty(stockNos.get(0))) {
            WebUtil.addErrorMessage("没有单号无法打印！");
            return;
        }
        Date now = DateUtil.now();
        if ("inStock".equals(sockType)) {
            exportInStockFile = assetOutStockService.exportWord(selectedItems, sockType);
            selectedItems.forEach(t -> t.setInStockDocumentsDate(now));
        } else {
            exportOutStockFile = assetOutStockService.exportWord(selectedItems, sockType);
            selectedItems.forEach(t -> t.setOutStockDocumentsDate(now));
        }
        dao.save(selectedItems);
    }

    public void treeSeachCondition(){
        TreeNode treeNodeEdit;
        if(StrUtil.isEmpty(searchDeptName)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4, searchDeptName);
        }
        orgTreeEdit = initOrgTree(treeNodeEdit);
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

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        recruiteDept = (OrgInfo) node.getData();
    }

    public String getOrgNameByUid(String orgUid) {
        if (!Strings.isNullOrEmpty(orgUid)) {
            OrgInfo org = orgInfoRepository.getOrgInfoByUid(orgUid);
            return org==null? null:org.getName();
        }else {
            return null;
        }
    }

    public String getArrivalFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varArrivalDateTo == null) {
            return arrivalFormatTime;
        } else {
            return "~" + sdf.format(varArrivalDateTo);
        }
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public ArrivalListItem getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(ArrivalListItem searchItem) {
        this.searchItem = searchItem;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier == null ? new Supplier() : supplier;
    }

    public OrgInfo getRecruiteDept() {
        return recruiteDept;
    }

    public void setRecruiteDept(OrgInfo recruiteDept) {
        this.recruiteDept = recruiteDept;
    }

    public OrgInfo getOrg() {
        return org;
    }

    public void setOrg(OrgInfo org) {
        this.org = org;
    }

    public TreeNode getOrgTreeEdit() {
        return orgTreeEdit;
    }

    public void setOrgTreeEdit(TreeNode orgTreeEdit) {
        this.orgTreeEdit = orgTreeEdit;
    }

    public Date getVarArrivalDateTo() {
        return varArrivalDateTo;
    }

    public void setVarArrivalDateTo(Date varArrivalDateTo) {
        this.varArrivalDateTo = varArrivalDateTo;
    }

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public Double getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Double endPrice) {
        this.endPrice = endPrice;
    }

    public String getSearchDeptName() {
        return searchDeptName;
    }

    public void setSearchDeptName(String searchDeptName) {
        this.searchDeptName = searchDeptName;
    }

    public List<ArrivalListItem> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<ArrivalListItem> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
