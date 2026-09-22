package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.mq.MqUtils;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.preview.QRPreViewerBean;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.mq.MqContext;
import com.ge.healthcare.cn.common.cons.ModifySourceEnum;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetStockedController extends GenericCRUDUUIDController<AssetStocked> {

    private AssetStockedRepository dao = null;
    private ArrivalListRepository arrivalListRepository;
    private ArrivalListItemRepository arrivalListItemRepository;
    private AssetInStockRecordRepository assetInStockRecordRepository;
    private AssetOutStockRecordRepository assetOutStockRecordRepository;
    private UaaService uaaService;
    private String filterClinicalDeptOrg;
    private AutoCompleteService autoCompleteService;
    private AssetInstallationService assetInstallationService;
    private AssetOutStockService assetOutStockService;
    private OrgInfoRepository orgInfoRepository;
    private AssetInfoRepository assetInfoRepository;
    private QRPreViewerBean qrPreViewerBean;
    private ConfigItemValueRepository configItemValueRepository;
    private UserAccountRepository userAccountRepository;
    private SupplierRepository supplierRepository;
    private MqUtils mqUtils;

    private UserAccount currentUser;
    private AssetStocked searchItem;
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
    private List<AssetStocked> selectedItems = ListUtil.newArrayList();
    @Getter @Setter
    private Date startDate;
    @Getter @Setter
    private Date endDate;
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
    private StreamedContent exportExcelFile;
    @Getter @Setter
    private BigDecimal ownFunds;
    @Getter @Setter
    private BigDecimal govtFunds;
    @Getter@ Setter
    private BigDecimal sciFunds;
    @Getter @Setter
    private String enableShowSourceFunds;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetStockedRepository.class);
        arrivalListRepository = WebUtil.getBean(ArrivalListRepository.class);
        arrivalListItemRepository = WebUtil.getBean(ArrivalListItemRepository.class);
        assetInStockRecordRepository = WebUtil.getBean(AssetInStockRecordRepository.class);
        assetOutStockRecordRepository = WebUtil.getBean(AssetOutStockRecordRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInstallationService = WebUtil.getBean(AssetInstallationService.class);
        assetOutStockService = WebUtil.getBean(AssetOutStockService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        qrPreViewerBean = WebUtil.getBean(QRPreViewerBean.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        supplierRepository = WebUtil.getBean(SupplierRepository.class);
        mqUtils = WebUtil.getBean(MqUtils.class);
        currentUser = UserContextService.getCurrentUserAccount();
        supplierList = supplierRepository.getBySiteId(currentUser.getSiteId());
        org = orgInfoRepository.findById(currentUser.getOrgInfoId());
        orgTreeEdit = initOrgTree(uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(currentUser.getOrgInfoUID(), currentUser.getUid(), 4));
        initSearchItem();
        enableShowSourceFunds = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "enableShowSourceFunds");
    }

    public void initSearchItem() {
        searchFilters = new ArrayList<>();
        supplier = new Supplier();
        startDate = null;
        endDate = null;
        startPrice = null;
        endPrice = null;
        searchItem = new AssetStocked();
        queryRecruiteDept = null;
        searchItem.setArrivalList(new ArrivalList());
    }

    @Override
    protected AssetStockedRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetStocked> loadData(PageRequest pageRequest) {
        searchFilters = new ArrayList<>();
        selectedItems = ListUtil.newArrayList();
        pckFilters(searchFilters);
        // YHAAO-25213 取消审核入库 增加库存管理
        String setAssetOutStockDept = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "setAssetOutStockDept");
        if(ObjectUtil.isEmpty(setAssetOutStockDept)){
            return new PageImpl<>(new ArrayList<>(),pageRequest, 0);
        }
//        searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, setAssetOutStockDept, "1*"));
        searchFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.IN, ListUtil.newArrayList(2, 3), "1*"));
        if (ObjectUtil.isNotEmpty(startDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.GTE, startDate, "1*"));
        }
        if (ObjectUtil.isNotEmpty(endDate)) {
            searchFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.LTE, DateUtil.endOfDay(endDate).toDate(), "1*"));
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
            searchFilters.add(new SearchFilter("arrivalList.contractNum", SearchFilter.Operator.LIKE, searchItem.getContractNum(), "1*"));
        }
        Page<AssetStocked> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetStocked> content = page.getContent();
        Set<String> userUidList = content.stream().map(AssetStocked::getInStockUser).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(userUidList)) {
            List<UserAccount> userAccounts = userAccountRepository.findByUidIn(userUidList);
            userMap = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, UserAccount::getName));
        }
        Set<Integer> supplierIds = content.stream().map(AssetStocked::getAssetInfo).map(AssetInfo::getSupplierId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        if (ObjectUtil.isNotEmpty(supplierIds)) {
            List<Supplier> suppliers = supplierRepository.findByIdIn(supplierIds);
            supplierMap = suppliers.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName));
        }
        return page;
    }

    private void pckFilters(List<SearchFilter> searchFilters) {
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
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
        AssetStocked selected = (AssetStocked) event.getObject();
        if (ObjectUtil.isNotEmpty(selected.getArrivalList())) {
            searchItem.setContractNum(selected.getArrivalList().getContractNum());
        }
    }

    public List<Supplier> autoCompleteSupplier(String query) {
        return autoCompleteService.autoCompleteVendor(query, supplierList);
    }

    public Collection<String> autoCompleteInStockNo(String query) {
        List<SearchFilter> searchFilterList = ListUtil.newArrayList();
        pckFilters(searchFilterList);
        searchFilterList.add(new SearchFilter("stockStatus", SearchFilter.Operator.IN, ListUtil.newArrayList(3, 4), "1*"));
        searchFilterList.add(new SearchFilter("inStockNo", SearchFilter.Operator.LIKE, query, "1*"));
        List<AssetStocked> listItems = dao.findBySearchFilter(searchFilterList);
        return listItems.stream().map(AssetStocked::getInStockNo).distinct().collect(Collectors.toList());
    }

    public Collection<String> autoCompleteOutStockNo(String query) {
        List<SearchFilter> searchFilterList = ListUtil.newArrayList();
        pckFilters(searchFilterList);
        searchFilterList.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, 4, "1*"));
        searchFilterList.add(new SearchFilter("outStockNo", SearchFilter.Operator.LIKE, query, "1*"));
        List<AssetStocked> listItems = dao.findBySearchFilter(searchFilterList);
        return listItems.stream().map(AssetStocked::getOutStockNo).distinct().collect(Collectors.toList());
    }

    public void onNoChange(String stockType) {
        if ("inStock".equals(stockType) && ObjectUtil.isNotEmpty(inStockNo)) {
            selectedItems = dao.findByInStockNo(inStockNo);
        } else if (ObjectUtil.isNotEmpty(outStockNo)) {
            selectedItems = dao.findByOutStockNo(outStockNo);
        }
    }

    public void auditInStocked(AssetStocked assetStocked) {
        assetInstallationService.auditInStocked(UserContextService.getAccessToken(), assetStocked.getId());
    }

    public void bathAuditInStocked() {
        for (AssetStocked selectedItem : selectedItems) {
            assetInstallationService.auditInStocked(UserContextService.getAccessToken(), selectedItem.getId());
        }
    }

    public Double totalPurchasePrice() {
        return selectedItems.stream().map(AssetStocked::getAssetInfo).filter(t -> ObjectUtil.isNotEmpty(t.getPurchasePrice())).mapToDouble(AssetInfo::getPurchasePrice).sum();
    }

    public void prepareGeneratedInStockDoc() {
        if (selectedItems.stream().map(AssetStocked::getArrivalListId).collect(Collectors.toSet()).size() > 1) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedInStockDoc", false);
            WebUtil.addErrorMessage("包含不同到货单，无法生成入库单！");
            return;
        }
        if (selectedItems.stream().map(AssetStocked::getStockStatus).collect(Collectors.toList()).contains(4)) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedInStockDoc", false);
            WebUtil.addErrorMessage("包含已出库的设备，无法生成入库单！");
            return;
        }
        if (ObjectUtil.isNotEmpty(selectedItems)) {
            AssetStocked assetStocked = selectedItems.get(0);
            if (ObjectUtil.isNotEmpty(assetStocked.getArrivalListId())) {
                List<ArrivalListItem> arrivalListItems = arrivalListItemRepository.findByArrivalListId(assetStocked.getArrivalListId());
                notInstalled = arrivalListItems.stream().filter(item -> "new".equals(item.getInstallStatus())).count();
                contractNum = assetStocked.getArrivalList().getContractNum();
                arrivalName = assetStocked.getArrivalList().getName();
                installed = arrivalListItems.size() - notInstalled;
            }
        }
    }

    public void generatedInStockDoc() {
        String inStockNo = assetOutStockService.queryInStockNo("RK");
        Date now = DateUtil.now();
        List<AssetInStockRecord> inStockRecords = ListUtil.newArrayList();
        for (AssetStocked selectedItem : selectedItems) {
            if (ObjectUtil.isEmpty(selectedItem.getInStockNo())) {
                selectedItem.setOutStockBusinessDate(now);
            }
            selectedItem.setInStockNo(inStockNo);
            selectedItem.setInStockUser(currentUser.getUid());
            selectedItem.setStockStatus(3);
            //updateAssetModify(selectedItem);
            AssetInStockRecord assetInStockRecord = new AssetInStockRecord();
            if (ObjectUtil.isNotEmpty(selectedItem.getArrivalList())) {
                assetInStockRecord.setContractNum(selectedItem.getArrivalList().getContractNum());
                assetInStockRecord.setInvoiceNum(selectedItem.getArrivalList().getInvoiceNum());
                assetInStockRecord.setInvoiceDate(selectedItem.getArrivalList().getInvoiceDate());
                assetInStockRecord.setVendor(selectedItem.getArrivalList().getVendor());
                assetInStockRecord.setSiteUid(selectedItem.getArrivalList().getSiteUID());
                assetInStockRecord.setHospitalUid(selectedItem.getArrivalList().getHospitalUID());
                assetInStockRecord.setInstitutionUid(selectedItem.getArrivalList().getInstitutionUID());
                assetInStockRecord.setTenantUid(selectedItem.getArrivalList().getTenantUID());
            } else {
                assetInStockRecord.setVendor(selectedItem.getAssetInfo().getVendor());
                assetInStockRecord.setSiteUid(selectedItem.getAssetInfo().getSiteUID());
                assetInStockRecord.setHospitalUid(selectedItem.getAssetInfo().getHospitalUID());
                assetInStockRecord.setInstitutionUid(selectedItem.getAssetInfo().getInstitutionUID());
                assetInStockRecord.setTenantUid(selectedItem.getAssetInfo().getTenantUID());

            }
            assetInStockRecord.setAssetUid(selectedItem.getAssetUid());
            assetInStockRecord.setInStockNo(selectedItem.getInStockNo());
            assetInStockRecord.setBusinessDate(now);
            assetInStockRecord.setInStockUser(selectedItem.getInStockUser());
            assetInStockRecord.setFinancingNum(selectedItem.getAssetInfo().getFinancingNum());
            assetInStockRecord.setAssetName(selectedItem.getAssetInfo().getName());
            assetInStockRecord.setInstallDate(selectedItem.getAssetInfo().getInstallDate());
            assetInStockRecord.setFunctionType(selectedItem.getAssetInfo().getFunctionType());
            assetInStockRecord.setComments(selectedItem.getComments());
            assetInStockRecord.setPurchasePrice(selectedItem.getAssetInfo().getPurchasePrice());
            assetInStockRecord.setArrivalListId(selectedItem.getArrivalListId());
            assetInStockRecord.setArrivalItemId(selectedItem.getId());
            assetInStockRecord.setStockStatus(3);
            assetInStockRecord.setStockSource(selectedItem.getStockSource());
            inStockRecords.add(assetInStockRecord);
        }
        // 修改上次已入库为无效入库
        assetInStockRecordRepository.updateStatusByInStock(selectedItems.stream().map(AssetStocked::getAssetUid).collect(Collectors.toList()));
        assetInStockRecordRepository.save(inStockRecords);
        dao.save(selectedItems);
    }

    public void updateAssetModify(AssetStocked selectedItem){
        AssetStocked original = dao.findById(selectedItem.getId());
        if (original == null) {
            return;
        }
        boolean isModify = false;
        AssetInfo assetInfo = selectedItem.getAssetInfo();
        String originalComments = original.getComments();
        String editedComments = selectedItem.getComments();

        if (!Objects.equals(originalComments, editedComments)) {
            assetInfo.setComments(editedComments);
            isModify = true;
        }

        // 2. 保存 AssetInfo（Hibernate 脏检查会自动处理 ownFunds、govtFunds、sciFunds、purchasePrice 的变更）
        //    只生成实际变更字段的 UPDATE SQL，未变更字段不会更新
        assetInfoRepository.save(assetInfo);
    }

    public void saveBatchModifyFunds(){
        for (AssetStocked item : selectedItems) {
            AssetInfo itemAssetInfo = item.getAssetInfo();
            if (itemAssetInfo == null) {
                continue;
            }

            // 自有资金：目标为 null 时才同步
            if (ownFunds != null && itemAssetInfo.getOwnFunds() == null) {
                itemAssetInfo.setOwnFunds(ownFunds);
            }

            // 财政拨款：目标为 null 时才同步
            if (govtFunds != null && itemAssetInfo.getGovtFunds() == null) {
                itemAssetInfo.setGovtFunds(govtFunds);
            }

            // 科教资金：目标为 null 时才同步
            if (sciFunds != null && itemAssetInfo.getSciFunds() == null) {
                itemAssetInfo.setSciFunds(sciFunds);
            }
            assetInfoRepository.save(itemAssetInfo);
        }
    }

    public void onRowEdit(RowEditEvent event) {
        AssetStocked edited = (AssetStocked) event.getObject();
        if (edited == null || edited.getId() == null) {
            return;
        }

        AssetInfo original = assetInfoRepository.findByUid(edited.getAssetUid());
        AssetInfo editedAssetInfo = edited.getAssetInfo();

        if (original == null) {
            return;
        }

        String editedComments = editedAssetInfo.getComments();
        BigDecimal ownFunds = editedAssetInfo.getOwnFunds();
        BigDecimal govtFunds = editedAssetInfo.getGovtFunds();
        BigDecimal sciFunds = editedAssetInfo.getSciFunds();
        String financingNum = editedAssetInfo.getFinancingNum();


        boolean changed = false;
        if (!Objects.equals(original.getComments(), editedComments)) {
            changed = true;
        }
        if (!Objects.equals(original.getOwnFunds(), ownFunds)) {
            changed = true;
        }
        if (!Objects.equals(original.getGovtFunds(), govtFunds)) {
            changed = true;
        }
        if (!Objects.equals(original.getSciFunds(), sciFunds)) {
            changed = true;
        }
        if (!Objects.equals(original.getFinancingNum(), financingNum)) {
            changed = true;
        }
        assetInfoRepository.save(editedAssetInfo);
    }

    public void prepareGeneratedOutStockDoc() {
        if (selectedItems.stream().map(AssetStocked::getInStockNo).anyMatch(ObjectUtil::isEmpty)) {
            RequestContext.getCurrentInstance().addCallbackParam("generatedOutStockDoc", false);
            WebUtil.addErrorMessage("设备未生成入库单号，不允许出库！");
            return;
        }
    }

    public void generatedOutStockDoc() {
        if (selectedItems.stream().map(AssetStocked::getStockStatus).collect(Collectors.toList()).contains(4)) {
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
        List<AssetInfo> assetInfos = ListUtil.newArrayList();
        List<AssetOutStockRecord> outStockRecords = ListUtil.newArrayList();
        for (AssetStocked selectedItem : selectedItems) {
            selectedItem.setOutStockNo(inStockNo);
            selectedItem.setRecruiteDeptUid(recruiteDept.getUid());
            selectedItem.setRecruiter(currentUser.getUid());
            selectedItem.setOutStockBusinessDate(now);
            selectedItem.setOutStockUser(currentUser.getUid());
            selectedItem.setStockStatus(4);
            assetInfos.add(selectedItem.getAssetInfo());
            AssetOutStockRecord assetOutStockRecord = new AssetOutStockRecord();
            if (ObjectUtil.isNotEmpty(selectedItem.getArrivalList())) {
                assetOutStockRecord.setContractNum(selectedItem.getArrivalList().getContractNum());
                assetOutStockRecord.setInvoiceNum(selectedItem.getArrivalList().getInvoiceNum());
                assetOutStockRecord.setInvoiceDate(selectedItem.getArrivalList().getInvoiceDate());
                assetOutStockRecord.setVendor(selectedItem.getArrivalList().getVendor());
                assetOutStockRecord.setSiteUid(selectedItem.getArrivalList().getSiteUID());
                assetOutStockRecord.setHospitalUid(selectedItem.getArrivalList().getHospitalUID());
                assetOutStockRecord.setInstitutionUid(selectedItem.getArrivalList().getInstitutionUID());
                assetOutStockRecord.setTenantUid(selectedItem.getArrivalList().getTenantUID());
            } else {
                assetOutStockRecord.setVendor(selectedItem.getAssetInfo().getVendor());
                assetOutStockRecord.setSiteUid(selectedItem.getAssetInfo().getSiteUID());
                assetOutStockRecord.setHospitalUid(selectedItem.getAssetInfo().getHospitalUID());
                assetOutStockRecord.setInstitutionUid(selectedItem.getAssetInfo().getInstitutionUID());
                assetOutStockRecord.setTenantUid(selectedItem.getAssetInfo().getTenantUID());
            }
            assetOutStockRecord.setAssetUid(selectedItem.getAssetUid());
            assetOutStockRecord.setOutStockNo(selectedItem.getOutStockNo());
            assetOutStockRecord.setBusinessDate(now);
            assetOutStockRecord.setOutStockUser(selectedItem.getOutStockUser());
            assetOutStockRecord.setFinancingNum(selectedItem.getAssetInfo().getFinancingNum());
            assetOutStockRecord.setAssetName(selectedItem.getAssetInfo().getName());
            assetOutStockRecord.setInstallDate(selectedItem.getAssetInfo().getInstallDate());
            assetOutStockRecord.setFunctionType(selectedItem.getAssetInfo().getFunctionType());
            assetOutStockRecord.setComments(selectedItem.getComments());
            assetOutStockRecord.setPurchasePrice(selectedItem.getAssetInfo().getPurchasePrice());
            assetOutStockRecord.setArrivalListId(selectedItem.getArrivalListId());
            assetOutStockRecord.setArrivalItemId(selectedItem.getId());
            assetOutStockRecord.setRecruiteDeptUid(selectedItem.getRecruiteDeptUid());
            assetOutStockRecord.setStockStatus(4);
            assetOutStockRecord.setStockSource(selectedItem.getStockSource());
            outStockRecords.add(assetOutStockRecord);
        }
        String outStockUpdateInstallDateFlag = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "outStockUpdateInstallDateFlag");
        if (ObjectUtil.isNotEmpty(assetInfos)) {
            for (AssetInfo assetInfo : assetInfos) {
                assetInfo.setTenantUID(recruiteDept.getTenantUID());
                assetInfo.setInstitutionUID(recruiteDept.getInstitutionUID());
                assetInfo.setHospitalUID(recruiteDept.getHospitalUID());
                assetInfo.setSiteUID(recruiteDept.getSiteUID());
                assetInfo.setClinicalDeptId(recruiteDept.getId());
                assetInfo.setClinicalDeptUID(recruiteDept.getUid());
                assetInfo.setClinicalDeptName(recruiteDept.getName());
                if(StrUtil.isNotEmpty(outStockUpdateInstallDateFlag) && "true".equals(outStockUpdateInstallDateFlag)){
                    assetInfo.setInstallDate(now);
                }
            }
            assetInfoRepository.save(assetInfos);
            for (AssetInfo assetInfo : assetInfos) {
                saveAssetHistory(assetInfo);
            }
        }
        assetOutStockRecordRepository.save(outStockRecords);
        dao.save(selectedItems);
    }

    public void updateAssetInstallDate(){

    }

    private void saveAssetHistory(AssetInfo newAsset) {
        MqContext mqContext = new MqContext();
        mqContext.setServiceName("hc-apm-web");
        mqContext.setClassName("AssetStockedController");
        mqContext.setMethodName("generatedOutStockDoc");
        mqContext.setDescribe("设备出库修改科室");
        mqContext.setModifySource(ModifySourceEnum.STOCK_IN_OUT.getCode());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stockedType", "assetOutStock");
        mqContext.setJsonObject(jsonObject);
        mqUtils.sendAssetToMQ(newAsset, currentUser, mqContext);
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

    public void exportAll(String stockType) throws IOException {
        if (ObjectUtil.isEmpty(selectedItems)) {
            WebUtil.addErrorMessage("请选择打印设备！");
            return;
        }
        List<String> stockNos;
        List<Integer> stockStatus = selectedItems.stream().map(AssetStocked::getStockStatus).distinct().collect(Collectors.toList());
        if ("inStock".equals(stockType)) {
            stockNos = selectedItems.stream().map(AssetStocked::getInStockNo).distinct().collect(Collectors.toList());
        } else {
            stockNos = selectedItems.stream().map(AssetStocked::getOutStockNo).distinct().collect(Collectors.toList());
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
        if ("inStock".equals(stockType)) {
            exportInStockFile = assetOutStockService.exportWord(selectedItems, stockType);
            selectedItems.forEach(t -> t.setInStockDocumentsDate(now));
        } else {
            exportOutStockFile = assetOutStockService.exportWord(selectedItems, stockType);
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

    public String getJmPrintReportViewUrl(String stockNo) {
        String viewUrl = "/portal/JmPrintReportView.xhtml?type=assetStock&stockNo=";
        if (stockNo.startsWith("RK")) {
            return viewUrl + stockNo;
        } else {
            return viewUrl + stockNo + "&userName=" + currentUser.getName();
        }
    }

    public String openNewWindowView(String stockNo) {
        String viewUrl = "/portal/JmPrintReportView.xhtml?faces-redirect=true&amp;type=assetStock&amp;stockNo=";
        if (stockNo.startsWith("RK")) {
            return viewUrl + stockNo;
        } else {
            return viewUrl + stockNo + "&amp;userName=" + currentUser.getName();
        }
/*
        if (stockNo.startsWith("RK")) {
            return "/portal/asset/arrivedOutStock/stockJmPrintReportView.xhtml?faces-redirect=true&amp;stockNo=" + stockNo;
        } else {
            return "/portal/asset/arrivedOutStock/stockJmPrintReportView.xhtml?faces-redirect=true&amp;stockNo=" + stockNo + "&amp;userName=" + currentUser.getName();
        }
*/
    }

    public void prepareViewQrcode(){
        if(CollectionUtils.isEmpty(this.selectedItems)){
            WebUtil.addErrorMessage("请选择设备");
            return;
        }
        List<String>  assetUids= this.selectedItems.stream().map(AssetStocked::getAssetUid).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
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

    public AssetStocked getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(AssetStocked searchItem) {
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

    public List<AssetStocked> getSelectedItems() {
        return selectedItems;
    }

    public void exportExcel() {
        try {
            List<AssetStocked> exportList;
            if (ObjectUtil.isNotEmpty(selectedItems)) {
                exportList = selectedItems;
            } else {
                List<SearchFilter> exportFilters = new ArrayList<>();
                pckFilters(exportFilters);
                String setAssetOutStockDept = configItemValueRepository.findByKeyAndSiteForUseParent(currentUser.getSiteUID(), currentUser.getHospitalUID(), currentUser.getInstitutionUID(), currentUser.getTenantUID(), "setAssetOutStockDept");
                if (ObjectUtil.isEmpty(setAssetOutStockDept)) {
                    WebUtil.addErrorMessage("无导出数据！");
                    return;
                }
                exportFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.IN, ListUtil.newArrayList(2, 3), "1*"));
                if (ObjectUtil.isNotEmpty(startDate)) {
                    exportFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.GTE, startDate, "1*"));
                }
                if (ObjectUtil.isNotEmpty(endDate)) {
                    exportFilters.add(new SearchFilter("businessDate", SearchFilter.Operator.LTE, DateUtil.endOfDay(endDate).toDate(), "1*"));
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
                if (ObjectUtil.isNotEmpty(searchItem.getOutStockNo())) {
                    exportFilters.add(new SearchFilter("outStockNo", SearchFilter.Operator.LIKE, searchItem.getOutStockNo(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getStockStatus())) {
                    exportFilters.add(new SearchFilter("stockStatus", SearchFilter.Operator.EQ, searchItem.getStockStatus(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(queryRecruiteDept)) {
                    exportFilters.add(new SearchFilter("recruiteDeptUid", SearchFilter.Operator.EQ, queryRecruiteDept.getUid(), "1*"));
                }
                if (ObjectUtil.isNotEmpty(searchItem.getContractNum())) {
                    exportFilters.add(new SearchFilter("arrivalList.contractNum", SearchFilter.Operator.LIKE, searchItem.getContractNum(), "1*"));
                }
                Page<AssetStocked> page = dao.findBySearchFilter(exportFilters, new PageRequest(0, 5000));
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
            // header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            // data rows
            for (int i = 0; i < exportList.size(); i++) {
                AssetStocked item = exportList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(nullSafe(item.getInStockNo()));
                row.createCell(1).setCellValue(item.getBusinessDate() != null ? sdf.format(item.getBusinessDate()) : "");
                row.createCell(2).setCellValue(nullSafe(item.getArrivalList() != null ? item.getArrivalList().getVendor() : ""));
                row.createCell(3).setCellValue(nullSafe(getUserNameByUid(item.getInStockUser())));
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
            exportExcelFile = new DefaultStreamedContent(new ByteArrayInputStream(baos.toByteArray()), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "StockedManage_Export.xlsx");
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

    public void setSelectedItems(List<AssetStocked> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
