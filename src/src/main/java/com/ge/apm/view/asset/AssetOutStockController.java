package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.ConfigItemMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetOutStockDto;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.AssetOutStockService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class AssetOutStockController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository arrivalListItemRepository;

    private AssetInfoRepository assetInfoRepository;

    private OrgInfoRepository orgDao;

    private UaaService uaaService;

    private OrgInfoTreeService orgInfoTreeService;

    private AssetInstallationService aiService;

    private AssetOutStockService assetOutStockService;

    private ConfigItemMapper configItemMapper;

    @Getter@Setter
    private List<OrgInfo> clinicalDeptList;

    @Getter@Setter
    private String filterClinicalDeptOrg;

    @Getter@Setter
    private Set<String> clinicalDeptOrgUidSet;

    private String arrivalFormatTime = null;

    private Date varArrivalDateTo = null;

    //UnOutStock、OutStocked
    public String tabType;
    private UserAccount account = UserContextService.getCurrentUserAccount();
    public final static String TAB_TYPE_UN_OUT_STOCK = "UnOutStock";
    public final static String TAB_TYPE_OUT_STOCKED = "UnOutStocked";

    @Getter@Setter
    private AssetOutStockDto assetOutStockDto;

    @Getter@Setter
    private Map<String, Boolean> selectedMap;

    Page<ArrivalListItem> arrivalListItemPage;

    List<ArrivalListItem> arrivalListItemList;

    @Getter@Setter
    private List<ArrivalListItem> selectedArrivalItems = new ArrayList<>();

    @Getter@Setter
    private String searchDeptName;

    @Getter  @Setter
    private TreeNode orgTreeEdit;

    @Getter  @Setter
    private OrgInfo org;

    @Getter @Setter
    private StreamedContent exportFile;

    @Override
    protected void init() {
        super.init();
        arrivalListItemRepository = WebUtil.getBean(ArrivalListItemRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        configItemMapper = WebUtil.getBean(ConfigItemMapper.class);
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList().stream().filter(item -> Objects.equals(item.getOrgType(), 4)).collect(Collectors.toList());
        assetOutStockService = WebUtil.getBean(AssetOutStockService.class);
        selectedMap = new HashMap<>();
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        org = orgDao.findById(account.getOrgInfoId());
        arrivalFormatTime = null;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {
        ConfigItemValue configItemValue = configItemMapper.findByKey("setAssetOutStockDept", account.getSiteUID());
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }

        if(configItemValue == null || StrUtil.isEmpty(configItemValue.getValue())){
            return new PageImpl<>(new ArrayList<>(),pageRequest, 0);
        }

        List<String> status = Arrays.asList("instorage", "finished");
        this.updateSearchFilter(new SearchFilter("installStatus", SearchFilter.Operator.IN, status, "1*"));

        if (!this.hasFilterOnField("arrivalList.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(OrgInfo::getSiteUID).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }

        if("UnOutStock".equals(tabType)){
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, configItemValue.getValue(), "1*"));
            this.updateSearchFilter(new SearchFilter("outStockNo", SearchFilter.Operator.IsNull, "", "1*"));
        }else{
            this.updateSearchFilter(new SearchFilter("recruiteDeptUid", SearchFilter.Operator.NE, configItemValue.getValue(), "1*"));
            this.updateSearchFilter(new SearchFilter("outStockNo", SearchFilter.Operator.IsNotNull, "", "1*"));
        }

        if (ObjectUtil.isNotEmpty(clinicalDeptOrgUidSet)) {
            this.updateSearchFilter(new SearchFilter("recruiteDeptUid", SearchFilter.Operator.IN, clinicalDeptOrgUidSet, "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "recruiteDeptUid".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        arrivalListItemPage = arrivalListItemRepository.findBySearchFilter(this.searchFilters, pageRequest);
        arrivalListItemList = arrivalListItemPage.getContent();
        return arrivalListItemPage;
    }

    public List<I18nMessage> getInstallStatusList() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
        List<I18nMessage> statusList = FieldValueMessageController.getFieldValueList("arrivalListItemStatus", siteId);
        if (Boolean.FALSE.equals(userContextService.needInstorageCheck())) {
            return statusList.stream().filter(item -> !ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getMsgKey())).collect(Collectors.toList());
        }
        return statusList;
    }

    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !arrivalListItemList.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (ArrivalListItem item : arrivalListItemList) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
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

    public String getOrgNameByUid(String orgUid) {
        if (!Strings.isNullOrEmpty(orgUid)) {
            OrgInfo org = aiService.getOrgInfoByUid(orgUid);
            return org==null? null:org.getName();
        }else {
            return null;
        }
    }

    public String getArrivalFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varArrivalDateTo == null) {
            System.out.println(this.getClass());
            return arrivalFormatTime;
        } else {
            return "~" + sdf.format(varArrivalDateTo);
        }
    }

    public void treeSeachCondition(){
        TreeNode treeNodeEdit = null;
        if(StrUtil.isEmpty(searchDeptName)){
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        }else{
            treeNodeEdit = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4, searchDeptName);
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
        rootNode.setExpanded(true);
        return orgTree;
    }


    public void batchStock(){
        for(String key : selectedMap.keySet()){
            if(selectedMap.get(key)){
                ArrivalListItem arrivalListItem = arrivalListItemRepository.findById(key);
                selectedArrivalItems.add(arrivalListItem);
            }
        }
        assetOutStockDto = new AssetOutStockDto();
        //assetOutStockDto.setOutStockNo(assetOutStockService.queryStockNo());
        assetOutStockDto.setQty(selectedArrivalItems.size());
        if(selectedArrivalItems.get(0).getPurchasePrice() != null){
            assetOutStockDto.setPrice(new BigDecimal(selectedArrivalItems.get(0).getPurchasePrice()));
            calcAmount();
        }
    }

    public void prepareOperate(String id){
        selected = arrivalListItemRepository.findById(id);
        assetOutStockDto = new AssetOutStockDto();
        assetOutStockDto.setQty(1);
        if(selected.getPurchasePrice() != null){
            assetOutStockDto.setPrice(new BigDecimal(selected.getPurchasePrice()));
            calcAmount();
        }
    }

    public void onClinicalDeptChange(SelectEvent event) {
        OrgInfo dept = clinicalDeptList.stream().filter(item -> item.getName().equals(assetOutStockDto.getStockedDeptName())).findFirst().orElse(null);
        assetOutStockDto.setStockedDeptUid(dept.getUid());
        assetOutStockDto.setStockedDeptId(dept.getId());
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        assetOutStockDto.setStockedDeptUid(orgInfo.getUid());
        assetOutStockDto.setStockedDeptId(orgInfo.getId());
        assetOutStockDto.setStockedDeptName(orgInfo.getName());
    }
    public void cancel(){
        assetOutStockDto = null;
        selectedArrivalItems = new ArrayList<>();
    }

    public void exportAll() throws IOException {
        exportFile = assetOutStockService.exportWord(assetOutStockDto, selectedArrivalItems);
    }

    public void exportStocked() throws IOException {
        if(selected == null){
            return;
        }
        if(StrUtil.isEmpty(selected.getOutStockNo())){
            return;
        }
        AssetOutStockDto ex= new AssetOutStockDto();
        AssetInfo assetInfo = assetInfoRepository.findByUid(selected.getAssetUID());
        ex.setStockedDeptName(assetInfo.getClinicalDeptName());
        ex.setUnit(selected.getUnit());
        ex.setRecruiter(selected.getRecruiter());
        ex.setOutStockNo(selected.getOutStockNo());
        ex.setOutStocDate(selected.getOutStocDate());
        List<ArrivalListItem> arrivalListItems = arrivalListItemRepository.findBySiteUidAndOutStockNoAndOutStockNoNotNull(selected.getSiteUid(), selected.getOutStockNo());
        exportFile = assetOutStockService.exportWord(ex, arrivalListItems);
    }

    public void stock(){
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        assetOutStockService.updateClinicalByOutStock(assetOutStockDto, selectedArrivalItems);
        selectedArrivalItems = new ArrayList<>();
        selectedMap = new HashMap<>();
    }

    public void calcAmount(){
        if(assetOutStockDto.getPrice()!= null && assetOutStockDto.getQty()!= null){
            assetOutStockDto.setTotal(assetOutStockDto.getPrice()
                    .multiply(new BigDecimal(assetOutStockDto.getQty()))
                    .setScale(2, RoundingMode.HALF_UP));
        }
    }

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    @Override
    protected GenericRepositoryUUID<ArrivalListItem> getDAO() {
        return arrivalListItemRepository;
    }
}


