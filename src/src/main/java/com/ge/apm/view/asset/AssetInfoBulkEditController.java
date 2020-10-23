package com.ge.apm.view.asset;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.KeyLabel;
import com.ge.apm.service.asset.AssetInfoBulkEditCheckService;
import com.ge.apm.service.asset.AssetInfoBulkEditService;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.constant.AssetConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AssetInfoBulkEditController extends JpaCRUDController<AssetInfo>  {

    static PageRequest pageRequest = new PageRequest(0, 100);

    private AssetInfoRepository dao;
    private AssetInfoService assetInfoService;
    private UaaService uaaService;
    private AssetInfoBulkEditService assetInfoBulkEditService;
    private AssetInfoBulkEditCheckService assetInfoBulkEditCheckService;
    private OrgInfoRepository orgInfoDao;
    private NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;
    private NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;
    private V2_WorkOrderRepository v2WorkerOrderRepository;
    private V2_WorkOrderDetailRepository v2WorkerOrderDetailRepository;
    private V2_WorkOrderStepRepository v2_WorkOrderStepRepository;
    //******* filter start

    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>(null, new ArrayList<>());

    private TreeNode orgTree;

    @Getter @Setter
    private AssetInfo filter = new AssetInfo();

    private List<OrgInfo> clinicalDeptListAsFilter = Collections.emptyList();

    @Getter @Setter
    private String oneSearchStr;

    //******* filter end


    //****** new value start
    private List<OrgInfo> clinicalDeptList = Collections.emptyList();
    //****** new value end

    @Getter
    private List<KeyLabel> candidateColumns = new ArrayList<>();

    @Getter @Setter
    private List<String> selectedColumns = new ArrayList<>();

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoBulkEditService = WebUtil.getBean(AssetInfoBulkEditService.class);
        assetInfoBulkEditCheckService = WebUtil.getBean(AssetInfoBulkEditCheckService.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        nurseSiteMaintainItemRepository = WebUtil.getBean(NurseSiteMaintainItemRepository.class);
        nurseSiteMaintainDataRepository = WebUtil.getBean(NurseSiteMaintainDataRepository.class);
        v2WorkerOrderRepository = WebUtil.getBean(V2_WorkOrderRepository.class);
        v2WorkerOrderDetailRepository= WebUtil.getBean(V2_WorkOrderDetailRepository.class);
        v2_WorkOrderStepRepository= WebUtil.getBean(V2_WorkOrderStepRepository.class);
        selected = new AssetInfo();
        initCandidateKeyLabels();
    }

    private void initCandidateKeyLabels() {
        KeyLabel keyLabel;

//        keyLabel = new KeyLabel("hospitalId", WebUtil.getMessage("hospitalId"));
//        candidateColumns.add(keyLabel);
//
//        keyLabel = new KeyLabel("clinicalDeptName", WebUtil.getMessage("clinicalDeptName"));
//        candidateColumns.add(keyLabel);

        keyLabel = new KeyLabel("assetGroup", WebUtil.getMessage("assetGroup"));
        candidateColumns.add(keyLabel);

    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return null;
    }

    public void initOrgTree() {

        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());
        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.setSelectable(false);
        if (null != treeNode) {
            rootNode.getChildren().add(treeNode);
        }
        rootNode.setExpanded(true);
    }

    public void onSelectTreeNodeAsFilter(NodeSelectEvent event) {
        OrgInfo filterSiteOrg = null;
        if (null == event || null == event.getTreeNode() || null == event.getTreeNode().getData()) {
            filterSiteOrg = null;
        } else {
            filterSiteOrg = (OrgInfo) event.getTreeNode().getData();
        }
        filter.setSiteOrg(filterSiteOrg);
        filter.setClinicalDeptOrg(null);

        if (null == filterSiteOrg) {
            clinicalDeptListAsFilter = Collections.emptyList();
        } else {
            clinicalDeptListAsFilter = assetInfoService.getHierachyClinicalDeptList(filter.getSiteOrg().getSiteUID());
        }
    }

    public List<OrgInfo> autoCompleteDeptAsFilter(String query) {
        if (clinicalDeptListAsFilter.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptListAsFilter.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void onClinicalDeptChangeAsFilter() {

    }

    public DualListModel<AssetInfo> getAssets() {

        prepareFilters();

        Page<AssetInfo> page = dao.findBySearchFilter(this.searchFilters, pageRequest);
        List<AssetInfo> source = new ArrayList<>(page.getContent());
        source.removeAll(assets.getTarget());
        assets.setSource(source);

        return assets;
    }

    private void prepareFilters() {
        searchFilters = new ArrayList<>();
        SearchFilter searchFilter;

        searchFilter = new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID());
        this.searchFilters.add(searchFilter);

        if (null != filter.getSiteOrg()) {
            List<String> orgUidList = orgInfoDao.getOrgInfoUidsByparentUid(filter.getSiteOrg().getUid());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (null != filter.getClinicalDeptOrg()) {
            searchFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filter.getClinicalDeptOrg().getUid()));
        }

        if (null != this.filter.getAssetGroup()) {
            searchFilter = new SearchFilter("assetGroup", SearchFilter.Operator.EQ, this.filter.getAssetGroup());
            this.searchFilters.add(searchFilter);
        }

        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            searchFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            searchFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }
    }

    public void onTransfer(TransferEvent event) {

    }

    public void onTabChange(TabChangeEvent event) {
        if ("选择资产".equals(event.getTab().getTitle())) {
            return;
        }

        setDefaultValueForSelectedAssets();
    }

    private void setDefaultValueForSelectedAssets() {
        List<AssetInfo> assetInfos = assets.getTarget();

        if (assetInfos.size() == 0) {
            return;
        }

        AssetInfo firstAsset = assetInfos.get(0);

        Object obj;
        obj = getSameValue(assets.getTarget(), "siteUID");
        if (null != obj) {
            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
            selected.setSiteOrg(foundOrgInfo);
        }

        obj = getSameValue(assets.getTarget(), "clinicalDeptUID");
        if (null != obj) {
            OrgInfo foundOrgInfo = orgInfoDao.findByUid((String)obj);
            selected.setClinicalDeptOrg(foundOrgInfo);
        }

        obj = getSameValue(assets.getTarget(), "assetGroup");
        if (null != obj) {
            selected.setAssetGroup((Integer)obj);
        }


    }

    private Object getSameValue(List<AssetInfo> assetInfos, String columnName) {
        try {
            Field field = AssetInfo.class.getDeclaredField(columnName);
            field.setAccessible(true);

            if (assetInfos.size() == 0) {
                return null;
            }

            if (assetInfos.size() == 1) {
                Object obj = field.get(assetInfos.get(0));
                return obj;
            }

            Object prevObj = field.get(assetInfos.get(0));

            for (int i = 1; i < assetInfos.size(); i++) {
                AssetInfo assetInfo = assetInfos.get(i);
                Object obj = field.get(assetInfo);

                if (!Objects.equals(obj, prevObj)) {
                    return null;
                }

                prevObj = obj;
            }

            return prevObj;

        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public void onSelectTreeNodeAsNewValue(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo siteOrg = (OrgInfo) node.getData();
        selected.setSiteOrg(siteOrg);

        selected.setClinicalDeptOrg(null);

        if (null == siteOrg) {
            clinicalDeptList = Collections.emptyList();
        } else {
            clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(selected.getSiteOrg().getSiteUID());
        }

    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList.size() == 0) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public boolean displayControl(String name) {
        for (String key: selectedColumns) {
            if (key.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void commitBulkEditSite() {

        if (!checkIsValidInput()) {
            return;
        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();

        //验证
        if(!assetInfoBulkEditCheckService.verify(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在调剂中，不能更换院区!");
            return;
        }

        if(!assetInfoBulkEditCheckService.verifyForMaintain(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在保养中，不能更换院区！");
            return;
        }

        if(!assetInfoBulkEditCheckService.verifyForInventory(selectedAssets)){
            WebUtil.addErrorMessage("有设备正在盘点中，不能更换院区!");
            return;
        }

        for(AssetInfo as:selectedAssets){
            List<V2_WorkOrder> pmOrderList = v2WorkerOrderRepository.findByAssetId(as.getId());
            if(CollectionUtils.isNotEmpty(pmOrderList)){
                for(V2_WorkOrder workerOder : pmOrderList){
                        List<V2_WorkOrder_Step> workOrderStepList = v2_WorkOrderStepRepository.findByWorkOrder(workerOder.getId());
                        workOrderStepList.stream().sorted(Comparator.comparing(V2_WorkOrder_Step:: getStepId));
                        List<V2_WorkOrder_Detail> workOrderDetailList = v2WorkerOrderDetailRepository.findPmOrderByWoId(workerOder.getId());
                        for(V2_WorkOrder_Step step : workOrderStepList){
                            if(! "未关单".equals(step.getStepName())){
                                WebUtil.addErrorMessage("有设备正在维修中，不能更换院区!");
                                return;
                            }
                        }
                        for(V2_WorkOrder_Detail detail : workOrderDetailList){
                            if(! detail.getClosed()){
                                WebUtil.addErrorMessage("有设备正在维修中，不能更换院区!");
                                return;
                            }
                        }
                    }
            }
        }

        //临床助手检查进行中
        Optional<String> optionalResult = assetInfoBulkEditCheckService.verifyForNurseSite(selectedAssets);
        if (optionalResult.isPresent()) {
            WebUtil.addErrorMessage(optionalResult.get());
            return;
        }

        try {
            assetInfoBulkEditService.commitBulkEditSite(selectedAssets, selected, selectedColumns);

            assets.setSource(new ArrayList<>());
            assets.setTarget(new ArrayList<>());

            WebUtil.addSuccessMessage("批量修改资产院区成功!");
        } catch(Exception e) {
            WebUtil.addErrorMessage(e.getMessage());
        }

    }

    public void commitBulkEdit() {

        if (!checkIsValidInput()) {
            return;
        }

        //commit changes after validated
        List<AssetInfo> selectedAssets = assets.getTarget();

        assetInfoBulkEditService.commitBulkEdit(selectedAssets, selected, selectedColumns);

        assets.setSource(new ArrayList<>());
        assets.setTarget(new ArrayList<>());

        WebUtil.addSuccessMessage("批量修改资产属性成功!");

    }

    private boolean checkIsValidInput() {
        if (assets.getTarget().size() == 0) {
            WebUtil.addErrorMessage("请选择相应资产!");
            return false;
        }

        if (null == this.selected.getSiteOrg()) {
            WebUtil.addErrorMessage("请选择院区!");
            return false;
        }

        if (null == this.selected.getClinicalDeptOrg()) {
            WebUtil.addErrorMessage("请选择科室!");
            return false;
        }

        return true;
    }

    public boolean disableBulkEditSiteButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        return false;
    }

    public boolean disableBulkEditButton() {
        if (assets.getTarget().size() == 0) {
            return true;
        }

        if (selectedColumns.size() == 0) {
            return true;
        }

        return false;
    }

}
