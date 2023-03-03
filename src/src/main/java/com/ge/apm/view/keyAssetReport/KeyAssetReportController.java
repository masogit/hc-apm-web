package com.ge.apm.view.keyAssetReport;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.keyAssetReport.KeyAssetReportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class KeyAssetReportController extends GenericCRUDUUIDController<KeyAssetReport> {

    private KeyAssetReportRepository dao;
    private OrgInfoRepository orgDao;
    private AssetInfoRepository assetDao;

    private KeyAssetReportService service;

    private OrgInfo org;
    @Getter @Setter
    private TreeNode orgTree;
    @Getter @Setter
    private OrgInfo selectedDept;

    @Getter @Setter
    private String assetName;
    @Getter @Setter
    private String assetGroup;
    @Getter @Setter
    private String functionType;
    @Getter @Setter
    private String departNum;
    @Getter @Setter
    private List<AssetInfo> allAssetList;// 待选设备列表
    @Getter @Setter
    private List<AssetInfo> addedAssetList;// 已添加设备列表

    @Getter @Setter
    private Map<String, Boolean> selectedAssetUid;// 待选设备列表，选中的设备uid
    @Getter @Setter
    private Set<AssetInfo> selectedAssetList;// 待选设备列表，选中的设备
    @Getter @Setter
    private Map<String, Boolean> addedAssetUid;// 已添加设备列表，选中的设备uid
    @Getter @Setter
    private Set<AssetInfo> addedSelectAssetList;// 已添加设备列表，选中的设备

    @Getter @Setter
    private Boolean allListSelected = Boolean.FALSE;// 待选设备列表，是否全部选中
    @Getter @Setter
    private Boolean cancelAllSelected = Boolean.FALSE;// 已添加设备列表，是否全部选中

    @Getter @Setter
    private Set<Map<String, Object>> assetDetail;
    @Getter @Setter
    private List<Map<String, String>> assetOperation;
    @Getter @Setter
    private String filterFillTime = null;

    private MicroServiceUtil microServiceUtil;

    private String previewUrl;

    @Override
    protected void init() {
        dao = WebUtil.getBean(KeyAssetReportRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        service = WebUtil.getBean(KeyAssetReportService.class);
        microServiceUtil = WebUtil.getBean(MicroServiceUtil.class);

        org = orgDao.findById(UserContextService.getCurrentUserAccount().getOrgInfoId());

        UserAccount account = UserContextService.getCurrentUserAccount();
        UaaService uaaService = WebUtil.getBean(UaaService.class);
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithClinicalDeptsSelected(account.getOrgInfoUID(), account.getUid(), 4);
        orgTree = initOrgTree(treeNode);

        previewUrl = microServiceUtil.getGatewayURL(GatewayURL.JI_MU_REPORT).replace("8090","8085");
        previewUrl += "/788203969584615424?token=" + UserContextService.getAccessToken().replace("Bearer ", "") + "&id=";
    }

    @Override
    protected GenericRepositoryUUID<KeyAssetReport> getDAO() {
        return dao;
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        crudActionName = "Create";
        selectedDept = null;
        super.prepareCreate();
        this.selected.setFormFillTime(new Date());
        this.selected.setFormFiller(UserContextService.getCurrentUserAccount().getName());
        assetDetail = null;
        assetOperation = new ArrayList<>();
        for (int i = 1; i< 13; i++){
            Map<String, String> map = new HashMap<>();
            map.put("month", i + "月");
            map.put("order", i + "");
            assetOperation.add(map);
        }
    }

    @Override
    public void prepareEdit() {
        crudActionName = "Edit";
        selectedDept = selected.getClinicalDept();
        assetDetail = JSON.parseObject(selected.getAssetDetail(), Set.class);
        assetOperation = JSON.parseObject(selected.getAssetOperation(), List.class);
        super.prepareEdit();
    }

    @Override
    protected Page<KeyAssetReport> loadData(PageRequest pageRequest) {

        if(CollectionUtils.isEmpty(this.searchFilters)){
            this.searchFilters = new ArrayList<>();
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        PageRequest page = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        List<SearchFilter> searchFilters = this.searchFilters;
        return dao.findBySearchFilter(searchFilters, page);
    }

    public void saveKeyAssetReport(){
        if(null == this.selected.getClinicalDept()){
            WebUtil.addErrorMessage("请选择科室并选择设备!");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        int countReport = 0;
        if("Create".equals(this.crudActionName)){
            countReport = dao.countByReport(this.selected.getAssetName(), this.selectedDept.getId(), this.selected.getReportYear());
        }else if("Edit".equals(this.crudActionName)){
            countReport = dao.countByReportAndId(this.selected.getAssetName(), this.selectedDept.getId(),
                    this.selected.getReportYear(), this.selected.getId());
        }
        if(countReport > 0){
            WebUtil.addErrorMessage("年份+科室+设备名称重复,不能提交!");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if(null == assetDetail){
            WebUtil.addErrorMessage("请选择设备!");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }else{
            for (Map<String, Object> map : assetDetail) {
                if(null == map.get("price") || null == map.get("depreciation") || null == map.get("repairSpend")
                    || null == map.get("maintenanceSpend") || null == map.get("suppliesSpend")){
                    WebUtil.addErrorMessage("设备明细数据录入不全,请检查!");
                    RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                    return;
                }
            }
        }
        for (Map<String, String> map : assetOperation) {
            if(null == map.get("checkPeopleNum") || null == map.get("sumIncome")){
                WebUtil.addErrorMessage("设备运营情况数据录入不全,请检查!");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
        }
        if("Create".equals(crudActionName)){
            UserAccount userAccount = UserContextService.getCurrentUserAccount();
            this.selected.setTenantUID(userAccount.getTenantUID());
            this.selected.setInstitutionUID(userAccount.getInstitutionUID());
            this.selected.setHospitalUID(userAccount.getHospitalUID());
            this.selected.setSiteUID(userAccount.getSiteUID());
            this.selected.setCreator(userAccount.getName());
            this.selected.setCreateTime(new Date());
        }
        this.selected.setAssetDetail(JSON.toJSONString(assetDetail));
        this.selected.setAssetOperation(JSON.toJSONString(assetOperation));
        dao.save(this.selected);
    }

    @Override
    public void delete(){
        dao.delete(this.selected);
        WebUtil.addSuccessMessage(WebUtil.getMessage("DeleteSuccess"));
    }

    public void saveAssetList(){
        assetDetail = service.groupAsset(addedAssetList);
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
        selectedDept = (OrgInfo) node.getData();
        this.selected.setClinicalDept(selectedDept);
        this.selected.setDeptId(selectedDept.getId());
    }

    public void resetSelectAssetPage(){
        allAssetList = new ArrayList<>();
        addedAssetList = new ArrayList<>();
        selectedAssetUid = new HashMap<>(32);
        selectedAssetList = new HashSet<>();
        addedAssetUid = new HashMap<>(32);
        addedSelectAssetList = new HashSet<>();
        allListSelected = Boolean.FALSE;
        cancelAllSelected = Boolean.FALSE;
        assetName = "";
        assetGroup = "";
        functionType = "";
        departNum = "";
    }

    public void findAssetList(){
        List<SearchFilter> assetSearchFilter = new ArrayList<>();
        //assetSearchFilter.add(new SearchFilter("status", SearchFilter.Operator.NE, "4", "1*"));
        assetSearchFilter.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectedDept.getInstitutionUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectedDept.getHospitalUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectedDept.getSiteUID(), "1*"));
        assetSearchFilter.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, selectedDept.getUid(), "1*"));
        assetSearchFilter.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true, "1*"));
        assetSearchFilter.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false, "1*"));

        if(StringUtils.isNotBlank(this.getDepartNum())) {
            assetSearchFilter.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, this.getDepartNum(), "1*"));
        } else {
            if(StringUtils.isNotBlank(this.getAssetName())) {
                assetSearchFilter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
                assetSearchFilter.add(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, this.getAssetName(), "1*2+"));
            }
            if(StringUtils.isNotBlank(this.getAssetGroup())) {
                assetSearchFilter.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.getAssetGroup(), "1*"));
            }
            if(StringUtils.isNotBlank(this.getFunctionType())) {
                assetSearchFilter.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, this.getFunctionType(), "1*"));
            }
        }

        allAssetList = assetDao.findBySearchFilter(assetSearchFilter);
    }

    // 待选设备列表，选中/取消选中
    public void updateSelectedAssetList(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssetList.add(asset);
        } else {
            selectedAssetList.remove(asset);
        }
    }
    // 已添加设备列表，选中/取消选中
    public void updateAddedAssetList(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            addedSelectAssetList.add(asset);
        } else {
            addedSelectAssetList.remove(asset);
        }
    }

    // 待选设备列表，全选
    public void addAllAssetsOfCurrentPage() {
        if(allListSelected) {
            allAssetList.stream().forEach(t -> {
                selectedAssetUid.put(t.getUid(), Boolean.TRUE);
                selectedAssetList.add(t);
            });
        } else {
            allAssetList.stream().forEach(t -> {
                selectedAssetUid.put(t.getUid(), Boolean.FALSE);
                selectedAssetList.remove(t);
            });
        }
        allListSelected = !allListSelected;
    }

    // 已添加设备列表，全选
    public void addAddedAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        if(cancelAllSelected) {
            addedAssetList.stream().forEach(t -> {
                addedAssetUid.put(t.getUid(), Boolean.TRUE);
                addedSelectAssetList.add(t);
            });
        } else {
            addedAssetList.stream().forEach(t -> {
                addedAssetUid.put(t.getUid(), Boolean.FALSE);
                addedSelectAssetList.remove(t);
            });
        }
        cancelAllSelected = !cancelAllSelected;
    }
    public void addAssetInfo(){
        for(AssetInfo assetInfo: selectedAssetList){
            if(!addedAssetList.contains(assetInfo)){
                addedAssetList.add(assetInfo);
            }
        }
    }
    public void removeAssetInfo(){
        addedAssetList.removeAll(addedSelectAssetList);
        addedSelectAssetList.clear();
        addedAssetUid.clear();
    }

    public void clearAssetInfo(){
        addedAssetList.clear();
        addedAssetUid.clear();
        addedSelectAssetList.clear();
    }

    public String preview(String id){
        return previewUrl + id;
    }
}
