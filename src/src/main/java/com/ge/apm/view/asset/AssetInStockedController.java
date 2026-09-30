package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.preview.QRPreViewerBean;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.primefaces.component.tree.Tree;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInStockedController extends GenericCRUDUUIDController<AssetInStockRecord> {

    private AssetInStockRecordRepository dao = null;
    private UaaService uaaService;
    private String filterClinicalDeptOrg;
    private AutoCompleteService autoCompleteService;
    private AssetInstallationService assetInstallationService;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private QRPreViewerBean qrPreViewerBean;
    private UserAccountRepository userAccountRepository;
    private SupplierRepository supplierRepository;

    private UserAccount currentUser;
    private AssetInStockRecord searchItem;
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
    private List<ArrivalListItem> selectedItems = ListUtil.newArrayList();
    @Getter @Setter
    private Date inStockBusinessStartDate;
    @Getter @Setter
    private Date inStockBusinessEndDate;
    @Getter @Setter
    private StreamedContent exportInStockFile;
    @Getter @Setter
    private StreamedContent exportOutStockFile;
    @Getter @Setter
    private String inStockNo;
    @Getter @Setter
    private String outStockNo;
    @Getter @Setter
    private Boolean inStock = Boolean.FALSE;
    @Getter @Setter
    private Boolean outStock = Boolean.FALSE;
    @Getter @Setter
    private Map<String, String> userMap;
    @Getter @Setter
    private Map<Integer, String> supplierMap;
    @Getter @Setter
    private StreamedContent exportExcelFile;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInStockRecordRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInstallationService = WebUtil.getBean(AssetInstallationService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        qrPreViewerBean = WebUtil.getBean(QRPreViewerBean.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        supplierList = supplierRepository.getBySiteId(currentUser.getSiteId());
        org = orgInfoRepository.findById(currentUser.getOrgInfoId());
        orgTreeEdit = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4));
        initSearchItem();
    }

    public void initSearchItem() {
        searchFilters = new ArrayList<>();
        supplier = new Supplier();
        inStockBusinessStartDate = null;
        inStockBusinessEndDate = null;
        startPrice = null;
        endPrice = null;
        searchItem = new AssetInStockRecord();
        queryRecruiteDept = null;
    }

    @Override
    protected AssetInStockRecordRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetInStockRecord> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<>();
        selectedItems = ListUtil.newArrayList();
        pckFilters(searchFilters);
        if (ObjectUtil.isNotEmpty(inStockBusinessStartDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.GTE, inStockBusinessStartDate, "1*"));
        }
        if (ObjectUtil.isNotEmpty(inStockBusinessEndDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.LTE, DateUtil.endOfDay(inStockBusinessEndDate).toDate(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getAssetName())) {
            searchFilters.add(new SearchFilter("assetInfo.name", SearchFilter.Operator.LIKE, searchItem.getAssetName(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(supplier.getId())) {
            searchFilters.add(new SearchFilter("assetInfo.supplierId", SearchFilter.Operator.EQ, supplier.getId(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(startPrice)) {
            searchFilters.add(new SearchFilter("assetInfo.purchasePrice", SearchFilter.Operator.GTE, startPrice, "1*"));
        }
        if (ObjectUtil.isNotEmpty(endPrice)) {
            searchFilters.add(new SearchFilter("assetInfo.purchasePrice", SearchFilter.Operator.LTE, endPrice, "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getFunctionType())) {
            searchFilters.add(new SearchFilter("assetInfo.functionType", SearchFilter.Operator.LIKE, searchItem.getFunctionType(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInvoiceNum())) {
            searchFilters.add(new SearchFilter("arrivalList.invoiceNum", SearchFilter.Operator.LIKE, searchItem.getInvoiceNum(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getInStockNo())) {
            searchFilters.add(new SearchFilter("inStockNo", SearchFilter.Operator.LIKE, searchItem.getInStockNo(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getContractNum())) {
            searchFilters.add(new SearchFilter("arrivalList.contractNum", SearchFilter.Operator.LIKE, searchItem.getContractNum(), "1*"));
        }
        if (ObjectUtil.isNotEmpty(searchItem.getStockStatus())) {
            searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, searchItem.getStockStatus(), "1*"));
        }
        Page<AssetInStockRecord> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInStockRecord> content = page.getContent();
        Set<String> userUidList = content.stream().map(AssetInStockRecord::getInStockUser).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(userUidList)) {
            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUidList);
            userMap = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, UserAccount::getName));
        }
        Set<Integer> supplierIds = content.stream().map(AssetInStockRecord::getAssetInfo).map(AssetInfo::getSupplierId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(supplierIds)) {
            List<Supplier> suppliers = supplierRepository.findByIdIn(supplierIds);
            supplierMap = suppliers.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName));
        }
        return page;
    }

    private void pckFilters(List<SearchFilter> filters) {
        boolean hasFieldFilter = false;
        for (SearchFilter filter : filters) {
            if ("assetInfo.siteUID".equals(filter.fieldName)) {
                hasFieldFilter = true;
                break;
            }
        }
        if (!hasFieldFilter) {
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            filters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
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
        AssetInStockRecord selected = (AssetInStockRecord) event.getObject();
        searchItem.setContractNum(selected.getContractNum());
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
        return selectedItems.stream().filter(t -> ObjectUtil.isNotEmpty(t.getPurchasePrice())).mapToDouble(ArrivalListItem::getPurchasePrice).sum();
    }

    public void generatedStockDoc(String stockType) {
        if ("inStock".equals(stockType)) {
            inStock = Boolean.FALSE;
        } else {
            outStock = Boolean.FALSE;
        }
    }

    public void printStockDoc(String stockType) {
        inStockNo = null;
        outStockNo = null;
        if ("inStock".equals(stockType)) {
            inStock = Boolean.TRUE;
        } else {
            outStock = Boolean.TRUE;
        }
        selectedItems = ListUtil.newArrayList();
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
            node.setSelectable(Objects.equals(org.getOrgType(), 4));
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
        if (!Strings.isNullOrEmpty(orgUid)) {
            OrgInfo org = orgInfoRepository.getOrgInfoByUid(orgUid);
            return org == null ? null : org.getName();
        }else {
            return null;
        }
    }

    public String getUserNameByUid(String userUid) {
        if (!Strings.isNullOrEmpty(userUid)) {
            UserAccount user = uaaService.getUserByUid(userUid);
            return user == null ? null : user.getName();
        }else {
            return null;
        }
    }

    public String getJmPrintReportViewUrl(String str) {
        if (str.startsWith("RK")) {
            return "/portal/JmPrintReportView.xhtml?type=assetStock&stockNo=" + str;
        } else {
            return "/portal/asset/contract/ContractView.xhtml?action=Edit&contractNum=" + str;
        }
    }

    public String openNewWindowView(String str) {
        if (str.startsWith("RK")) {
            return "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=" + str;
        } else {
            return "/portal/asset/contract/ContractView.xhtml?faces-redirect=true&amp;action=Edit&amp;contractNum=" + str;
        }
    }

    public void prepareViewQrcode(){
        if(CollectionUtils.isEmpty(this.selectedItems)){
            WebUtil.addErrorMessage("请选择设备");
            return;
        }
        List<String>  assetUids= this.selectedItems.stream().map(ArrivalListItem::getAssetUID).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(assetUids)){
            WebUtil.addErrorMessage("未查询到相关设备二维码");
            return;
        }
        List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetUids);
        List<String> collect = assetInfos.stream().filter(item -> StringUtils.hasText(item.getQrCode())).map(item -> item.getQrCode() + ":" + item.getSiteUID()).collect(Collectors.toList());
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        qrPreViewerBean.onServerEvent("",collect);
    }

    public String getFilterClinicalDeptOrg() {
        return filterClinicalDeptOrg;
    }

    public void setFilterClinicalDeptOrg(String filterClinicalDeptOrg) {
        this.filterClinicalDeptOrg = filterClinicalDeptOrg;
    }

    public AssetInStockRecord getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(AssetInStockRecord searchItem) {
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

    public List<ArrivalListItem> getSelectedItems() {
        return selectedItems;
    }

    public void exportExcel() {
        try {
            List<AssetInStockRecord> exportList;
            if (ObjectUtil.isNotEmpty(selectedItems)) {
                // selectedItems declared as List<ArrivalListItem> but dataTable fills AssetInStockRecord at runtime
                exportList = new ArrayList<>();
                for (Object obj : selectedItems) {
                    if (obj instanceof AssetInStockRecord) {
                        exportList.add((AssetInStockRecord) obj);
                    }
                }
            } else {
                List<SearchFilter> exportFilters = new ArrayList<>();
                pckFilters(exportFilters);
                if (ObjectUtil.isNotEmpty(inStockBusinessStartDate)) {
                    exportFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.GTE, inStockBusinessStartDate, "1*"));
                }
                if (ObjectUtil.isNotEmpty(inStockBusinessEndDate)) {
                    exportFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.LTE, DateUtil.endOfDay(inStockBusinessEndDate).toDate(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getAssetName())) {
                    exportFilters.add(new SearchFilter("assetInfo.name", SearchFilter.Operator.LIKE, searchItem.getAssetName(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(supplier.getId())) {
                    exportFilters.add(new SearchFilter("assetInfo.supplierId", SearchFilter.Operator.EQ, supplier.getId(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(startPrice)) {
                    exportFilters.add(new SearchFilter("assetInfo.purchasePrice", SearchFilter.Operator.GTE, startPrice, "1*"));
                }
                if (ObjectUtil.isNotEmpty(endPrice)) {
                    exportFilters.add(new SearchFilter("assetInfo.purchasePrice", SearchFilter.Operator.LTE, endPrice, "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getFunctionType())) {
                    exportFilters.add(new SearchFilter("assetInfo.functionType", SearchFilter.Operator.LIKE, searchItem.getFunctionType(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getInvoiceNum())) {
                    exportFilters.add(new SearchFilter("arrivalList.invoiceNum", SearchFilter.Operator.LIKE, searchItem.getInvoiceNum(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getInStockNo())) {
                    exportFilters.add(new SearchFilter("inStockNo", SearchFilter.Operator.LIKE, searchItem.getInStockNo(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getContractNum())) {
                    exportFilters.add(new SearchFilter("arrivalList.contractNum", SearchFilter.Operator.LIKE, searchItem.getContractNum(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getStockStatus())) {
                    exportFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, searchItem.getStockStatus(), "1*"));
                }
                Page<AssetInStockRecord> page = dao.findBySearchFilter(exportFilters, new PageRequest(0, 5000));
                exportList = page.getContent();
            }
            if (ObjectUtil.isEmpty(exportList)) {
                WebUtil.addErrorMessage("无导出数据！");
                return;
            }
            String[] headers = {"入库单号", "业务时间", msg("vendor"), "制单人", msg("financingNum"), msg("assetName"),
                    msg("arrivalListContractNum"), msg("installDate"), msg("functionType"), msg("serialNum"),
                    msg("comments"), "资产价值", msg("arrivalListInvoiceNum"), "发票日期", msg("stockStatus"), msg("stockSource")};
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            for (int i = 0; i < exportList.size(); i++) {
                AssetInStockRecord item = exportList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(nullSafe(item.getInStockNo()));
                row.createCell(1).setCellValue(item.getBusinessDate() != null ? sdf.format(item.getBusinessDate()) : "");
                row.createCell(2).setCellValue(nullSafe(supplierMap != null && item.getAssetInfo() != null ? supplierMap.get(item.getAssetInfo().getSupplierId()) : ""));
                row.createCell(3).setCellValue(nullSafe(userMap != null ? userMap.get(item.getInStockUser()) : getUserNameByUid(item.getInStockUser())));
                row.createCell(4).setCellValue(nullSafe(item.getAssetInfo() != null ? item.getAssetInfo().getFinancingNum() : ""));
                row.createCell(5).setCellValue(nullSafe(item.getAssetInfo() != null ? item.getAssetInfo().getName() : ""));
                row.createCell(6).setCellValue(nullSafe(item.getArrivalList() != null ? item.getArrivalList().getContractNum() : ""));
                row.createCell(7).setCellValue(item.getAssetInfo() != null && item.getAssetInfo().getInstallDate() != null ? sdf.format(item.getAssetInfo().getInstallDate()) : "");
                row.createCell(8).setCellValue(nullSafe(item.getAssetInfo() != null ? item.getAssetInfo().getFunctionType() : ""));
                row.createCell(9).setCellValue(nullSafe(item.getAssetInfo() != null ? item.getAssetInfo().getSerialNum() : ""));
                row.createCell(10).setCellValue(nullSafe(item.getComments()));
                row.createCell(11).setCellValue(item.getAssetInfo() != null && item.getAssetInfo().getPurchasePrice() != null ? item.getAssetInfo().getPurchasePrice() : 0);
                row.createCell(12).setCellValue(nullSafe(item.getArrivalList() != null ? item.getArrivalList().getInvoiceNum() : ""));
                row.createCell(13).setCellValue(item.getArrivalList() != null && item.getArrivalList().getInvoiceDate() != null ? sdf.format(item.getArrivalList().getInvoiceDate()) : "");
                row.createCell(14).setCellValue(nullSafe(com.ge.apm.view.sysutil.FieldValueMessageController.fieldValue("stockStatus", item.getStockStatus() != null ? String.valueOf(item.getStockStatus()) : "")));
                row.createCell(15).setCellValue(nullSafe(com.ge.apm.view.sysutil.FieldValueMessageController.fieldValue("stockSource", item.getStockSource() != null ? String.valueOf(item.getStockSource()) : "")));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();
            exportExcelFile = new DefaultStreamedContent(new ByteArrayInputStream(baos.toByteArray()), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "InStockedRecord_Export.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("导出失败：" + e.getMessage());
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String msg(String key) {
        try {
            return WebUtil.getMessage(key);
        } catch (Exception e) {
            return key;
        }
    }

    public void setSelectedItems(List<ArrivalListItem> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
