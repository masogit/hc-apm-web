package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.AssetOutStockService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.preview.QRPreViewerBean;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.tree.Tree;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetOutStockedController extends GenericCRUDUUIDController<AssetOutStockRecord> {

    private AssetOutStockRecordRepository dao = null;
    private AssetStockedRepository assetStockedRepository;
    private ArrivalListItemRepository arrivalListItemRepository;
    private ConfigItemValueRepository configItemValueRepository;
    private UaaService uaaService;
    private String filterClinicalDeptOrg;
    private AutoCompleteService autoCompleteService;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private QRPreViewerBean qrPreViewerBean;
    private AssetOutStockService assetOutStockService;
    private UserAccountRepository userAccountRepository;
    private SupplierRepository supplierRepository;
    private ConfigCacheService configCacheService;
    private AssetInStockRecordRepository assetInStockRecordRepository;
    private MqUtils mqUtils;

    private UserAccount currentUser;
    private AssetOutStockRecord searchItem;
    private Supplier supplier;
    private OrgInfo recruiteDept;
    @Getter @Setter
    private OrgInfo queryRecruiteDept;
    private OrgInfo org;
    private TreeNode orgTreeEdit;
    private String tabId;
    private Date varArrivalDateTo = null;
    private Double startPrice;
    private Double endPrice;
    private String searchDeptName;
    private List<Supplier> supplierList;
    private List<AssetOutStockRecord> selectedItems = ListUtil.newArrayList();
    @Getter @Setter
    private Date outStockBusinessStartDate;
    @Getter @Setter
    private Date outStockBusinessEndDate;
    @Getter @Setter
    private StreamedContent exportInStockFile;
    @Getter @Setter
    private StreamedContent exportOutStockFile;
    @Getter @Setter
    private String inStockNo;
    @Getter @Setter
    private String outStockNo;
    @Getter @Setter
    private Long installed;
    @Getter @Setter
    private Long notInstalled;
    @Getter @Setter
    private String contractNum;
    @Getter @Setter
    private String arrivalName;
    @Getter @Setter
    private Map<String, String> userMap;
    @Getter @Setter
    private Map<Integer, String> supplierMap;
    @Getter @Setter
    private String setAssetOutStockDept;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetOutStockRecordRepository.class);
        assetStockedRepository = WebUtil.getBean(AssetStockedRepository.class);
        arrivalListItemRepository = WebUtil.getBean(ArrivalListItemRepository.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        qrPreViewerBean = WebUtil.getBean(QRPreViewerBean.class);
        assetOutStockService = WebUtil.getBean(AssetOutStockService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetInStockRecordRepository = WebUtil.getBean(AssetInStockRecordRepository.class);
        mqUtils = WebUtil.getBean(MqUtils.class);
        currentUser = UserContextService.getCurrentUserAccount();
        supplierList = supplierRepository.getBySiteId(currentUser.getSiteId());
        org = orgInfoRepository.findById(currentUser.getOrgInfoId());
        orgTreeEdit = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4));
        setAssetOutStockDept = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "setAssetOutStockDept");
        initSearchItem();
    }

    public void initSearchItem() {
        searchFilters = new ArrayList<>();
        supplier = new Supplier();
        outStockBusinessStartDate = null;
        outStockBusinessEndDate = null;
        startPrice = null;
        endPrice = null;
        searchItem = new AssetOutStockRecord();
        queryRecruiteDept = null;
    }

    @Override
    protected AssetOutStockRecordRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetOutStockRecord> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<>();
        selectedItems = ListUtil.newArrayList();
        pckFilters(searchFilters);
        if (ObjectUtil.isNotEmpty(outStockBusinessStartDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.GTE, outStockBusinessStartDate, "1*"));
        }
        if (ObjectUtil.isNotEmpty(outStockBusinessEndDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.LTE, DateUtil.endOfDay(outStockBusinessEndDate).toDate(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getAssetName())) {
            searchFilters.add(new SearchFilter("assetName", SearchFilter.Operator.LIKE, searchItem.getAssetName(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(supplier.getId())) {
            searchFilters.add(new SearchFilter("supplierId", SearchFilter.Operator.EQ, supplier.getId(), "1*"));
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
        if (ObjectUtil.isNotEmpty(searchItem.getFunctionType())) {
            searchFilters.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, searchItem.getFunctionType(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInvoiceNum())) {
            searchFilters.add(new SearchFilter("invoiceNum", SearchFilter.Operator.LIKE, searchItem.getInvoiceNum(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getOutStockNo())) {
            searchFilters.add(new SearchFilter("outStockNo", SearchFilter.Operator.LIKE, searchItem.getOutStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getStockStatus())) {
            searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, searchItem.getStockStatus(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(queryRecruiteDept)) {
            searchFilters.add(new SearchFilter("recruiteDeptUid", SearchFilter.Operator.EQ, queryRecruiteDept.getUid(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getContractNum())) {
            searchFilters.add(new SearchFilter("contractNum", SearchFilter.Operator.LIKE, searchItem.getContractNum(), "1*"));
        }
        Page<AssetOutStockRecord> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetOutStockRecord> content = page.getContent();
        Set<String> userUidList = content.stream().map(AssetOutStockRecord::getOutStockUser).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(userUidList)) {
            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUidList);
            userMap = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, UserAccount::getName));
        }
        Set<Integer> supplierIds = content.stream().map(AssetOutStockRecord::getAssetInfo).map(AssetInfo::getSupplierId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(supplierIds)) {
            List<Supplier> suppliers = supplierRepository.findByIdIn(supplierIds);
            supplierMap = suppliers.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName));
        }
        return page;
    }

    private void pckFilters(List<SearchFilter> searchFilters) {
        if (!this.hasFilterOnField("siteUid")) {
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onTabChange(TabChangeEvent event) {
        tabId = event.getTab().getId();
        initSearchItem();
    }

    public void onRowSelect(SelectEvent event) {
        if (null == event.getObject()) {
            return;
        }
        AssetOutStockRecord selected = (AssetOutStockRecord) event.getObject();
        searchItem.setContractNum(selected.getContractNum());
    }

    public List<Supplier> autoCompleteSupplier(String query) {
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public Double totalPurchasePrice() {
        return selectedItems.stream().map(AssetOutStockRecord::getAssetInfo).filter(assetInfo -> ObjectUtil.isNotEmpty(assetInfo.getPurchasePrice())).mapToDouble(AssetInfo::getPurchasePrice).sum();
    }

    public void prepareGeneratedReturnedStockDoc() {
        if (selectedItems.stream().map(AssetOutStockRecord::getArrivalListId).collect(Collectors.toSet()).size() > 1) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedReturnedStockDoc", false);
            WebUtil.addErrorMessage("包含不同到货单，无法生成退库单！");
            return;
        }
        if (selectedItems.stream().map(AssetOutStockRecord::getRecruiteDeptUid).collect(Collectors.toSet()).size() > 1) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedReturnedStockDoc", false);
            WebUtil.addErrorMessage("包含不同领用科室，无法生成退库单！");
            return;
        }
        if (selectedItems.stream().map(AssetOutStockRecord::getStockStatus).collect(Collectors.toSet()).contains(5)) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedReturnedStockDoc", false);
            WebUtil.addErrorMessage("包含已退库的设备，无法生成退库单！");
            return;
        }
        AssetInfoEditController assetInfoEditController = WebUtil.getBean(AssetInfoEditController.class);
        if (assetInfoEditController.checkAssetOrderIsComplete(selectedItems.stream().map(AssetOutStockRecord::getAssetUid).collect(Collectors.toList()))) {
            return;
        }
        boolean checked = assetOutStockService.checkAssetInfo(selectedItems.stream().map(AssetOutStockRecord::getAssetUid).collect(Collectors.toList()));
        if (!checked) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedReturnedStockDoc", false);
            WebUtil.addErrorMessage("设备有未处理的工单，无法生成退库单！");
            return;
        }
        notInstalled = 0L;
        contractNum = "";
        arrivalName = "";
        installed = 0L;
        if (ObjectUtil.isNotEmpty(selectedItems)) {
            AssetOutStockRecord assetOutStockRecord = selectedItems.get(0);
            if (ObjectUtil.isNotEmpty(assetOutStockRecord.getArrivalListId())) {
                List<ArrivalListItem> arrivalListItems = arrivalListItemRepository.findByArrivalListId(assetOutStockRecord.getArrivalListId());
                notInstalled = arrivalListItems.stream().filter(item -> "new".equals(item.getInstallStatus())).count();
                contractNum = assetOutStockRecord.getArrivalList().getContractNum();
                arrivalName = assetOutStockRecord.getArrivalList().getName();
                installed = arrivalListItems.size() - notInstalled;
            }
        }
    }

    public void generatedReturnedStockDoc() {
        Date now = DateUtil.now();
        List<String> assetUidList = selectedItems.stream().map(AssetOutStockRecord::getAssetUid).collect(Collectors.toList());
        String returnedStockNo = assetOutStockService.queryReturnedStockNo("TR");
        for (AssetOutStockRecord selectedItem : selectedItems) {
            selectedItem.setStockStatus(5);
            selectedItem.setComments(searchItem.getComments());
            selectedItem.setReturnedDate(now);
            selectedItem.setReturnedStockNo(returnedStockNo);
        }
        List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetUidList);
        for (AssetInfo assetInfo : assetInfos) {
            OrgInfo orgInfo = orgInfoRepository.getByUid(getStockDept(assetInfo));
            assetInfo.setTenantUID(orgInfo.getTenantUID());
            assetInfo.setInstitutionUID(orgInfo.getInstitutionUID());
            assetInfo.setHospitalUID(orgInfo.getHospitalUID());
            assetInfo.setSiteUID(orgInfo.getSiteUID());
            assetInfo.setClinicalDeptId(orgInfo.getId());
            assetInfo.setClinicalDeptUID(orgInfo.getUid());
            assetInfo.setClinicalDeptName(orgInfo.getName());
        }
        assetInfoRepository.save(assetInfos);
        for (AssetInfo assetInfo : assetInfos) {
            saveAssetHistory(assetInfo, searchItem.getComments());
        }
        // 修改上次已入库为已退库
        assetInStockRecordRepository.updateStatusByReturnedStock(selectedItems.stream().map(AssetOutStockRecord::getAssetUid).collect(Collectors.toList()));
        dao.save(selectedItems);
    }

    private void saveAssetHistory(AssetInfo newAsset, String comment) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetOutStockedController");
        mqContext.setMethodName("generatedReturnedStockDoc");
        mqContext.setDescribe("设备退库修改科室");
        mqContext.setSource("departmentReturned");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stockedType", "assetReturnedStock");
        jsonObject.put("comment", comment);
        mqContext.setJsonObject(jsonObject);
        mqUtils.sendAssetToMQ(newAsset, currentUser, mqContext);
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
        TreeNodeUtil.expandTreeNode(orgTree);
        return orgTree;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        Tree source = (Tree) event.getSource();
        String id = source.getId();
        if ("orgTreeQuery".equals(id)) {
            queryRecruiteDept = (OrgInfo) node.getData();
        } else {
            recruiteDept = (OrgInfo) node.getData();
        }
    }

    public String getOrgNameByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid).getName();
    }

    public String getUserNameByUid(String userUid) {
        if (!Strings.isNullOrEmpty(userUid)) {
            UserAccount user = uaaService.getUserByUid(userUid);
            return user == null ? null : user.getName();
        }else {
            return null;
        }
    }

    public String getJmPrintReportViewUrl(AssetOutStockRecord outStockRecord, String type) {
        switch (type) {
            case "outStocked" :
                return "/portal/JmPrintReportView.xhtml?type=assetStock&stockNo=" + outStockRecord.getOutStockNo() + "&userName=" + currentUser.getName();
            case "returnedStocked" :
                OrgInfo returnedDept = orgInfoRepository.findByUid(getStockDept(outStockRecord.getAssetInfo()));
                if( null == returnedDept ){
                    returnedDept = orgInfoRepository.findByUid(getStockDept1(outStockRecord));
                }
                return "/portal/JmPrintReportView.xhtml?type=assetStock&stockNo=" + outStockRecord.getReturnedStockNo() + "&userName=" + currentUser.getName() + "&returnedStockDept=" + returnedDept.getName();
            case "assetInfo" :
                return "/portal/asset/info/AssetDetail.xhtml?actionName=View&terminate=false&assetUid=" + UrlParamUtil.encodeUrlQueryString("assetUid=" + outStockRecord.getAssetUid());
            case "contractInfo" :
                return "/portal/asset/contract/ContractView.xhtml?action=Edit&contractNum=" + Optional.ofNullable(outStockRecord.getArrivalList()).map(ArrivalList::getContractNum).orElse("");
            default:
                return "";
        }
    }

    public String openNewWindowView(AssetOutStockRecord outStockRecord, String type) {
        switch (type) {
            case "outStocked" :
                return "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=" + outStockRecord.getOutStockNo() + "&amp;userName=" + currentUser.getName();
            case "returnedStocked" :
                OrgInfo returnedDept = orgInfoRepository.findByUid(getStockDept(outStockRecord.getAssetInfo()));
                return "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=" + outStockRecord.getReturnedStockNo() + "&amp;userName=" + currentUser.getName() + "&amp;returnedStockDept=" + returnedDept.getName();
            case "assetInfo" :
                return "/portal/asset/info/AssetDetail.xhtml?faces-redirect=true&amp;actionName=View&amp;terminate=false&amp;assetUid=" + UrlParamUtil.encodeUrlQueryString("assetUid=" + outStockRecord.getAssetUid());
            case "contractInfo" :
                return "/portal/asset/contract/ContractView.xhtml?faces-redirect=true&amp;action=Edit&amp;contractNum=" + outStockRecord.getArrivalList().getContractNum();
            default:
                return "";
        }
    }

    public void prepareViewQrcode(){
        if(CollectionUtils.isEmpty(this.selectedItems)){
            WebUtil.addErrorMessage("请选择设备");
            return;
        }
        List<String> assetUids = this.selectedItems.stream().map(AssetOutStockRecord::getAssetUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(assetUids)){
            WebUtil.addErrorMessage("未查询到相关设备二维码");
            return;
        }
        if(selectedItems.stream().map(AssetOutStockRecord::getStockStatus).collect(Collectors.toSet()).contains(5)){
            WebUtil.addErrorMessage("选中设备中包含已退库设备，请重新选择后再操作！");
            return;
        }
        List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetUids);
        List<String> collect = assetInfos.stream().filter(item -> StringUtils.hasText(item.getQrCode())).map(item -> item.getQrCode() + ":" + item.getSiteUID()).collect(Collectors.toList());
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        qrPreViewerBean.onServerEvent("",collect);
    }

    private String getStockDept(AssetInfo assetInfo) {
        return configItemValueRepository.findByKeyAndSiteForUseParent(assetInfo.getSiteUID(), assetInfo.getHospitalUID(), assetInfo.getInstitutionUID(), assetInfo.getTenantUID(), "setAssetOutStockDept");
    }
    private String getStockDept1(AssetOutStockRecord outStockRecord) {
        return configItemValueRepository.findByKeyAndSiteForUseParent(outStockRecord.getSiteUid(), outStockRecord.getHospitalUid(), outStockRecord.getInstitutionUid(), outStockRecord.getTenantUid(), "setAssetOutStockDept");
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public AssetOutStockRecord getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(AssetOutStockRecord searchItem) {
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

    public List<AssetOutStockRecord> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<AssetOutStockRecord> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
