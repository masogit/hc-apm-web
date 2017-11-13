package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Supplier;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedListController extends GenericCRUDUUIDController<ArrivalList> {

    private ArrivalListRepository dao = null;
    private ArrivalListItemRepository itemDao = null;

    private OrgInfo selectedOrg;

    private List<ArrivalListItem> arrivalItemList;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private TreeNode orgTree;
    
    private Supplier supplier;

    private TreeNode selectedNode;

    @Override
    protected void init() {

        dao = WebUtil.getBean(ArrivalListRepository.class);
        itemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(UserContextService.getCurrentUserAccount().getSiteUID(), UserContextService.getCurrentUserAccount().getUid());
        orgTree = new DefaultTreeNode("org", null, null);
        orgTree.getChildren().add(subTree);
        this.filterBySite = false;
        this.filterByHospital = false;
    }

    @Override
    protected ArrivalListRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalList> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    @Override
    public List<ArrivalList> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(ArrivalList object) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        object.setCreatorUID(user.getUid());
        object.setStatus(1);

        selectedOrg = aiService.getOrgInfoByUid(user.getSiteUID());
    }

    public void applyChange() {
        if (checkName()) {
            this.save();
        } else {
            WebUtil.addErrorMessage("到货单名称/编号 已经存在");
        }
    }

    private Boolean checkName() {
        if (Strings.isNullOrEmpty(selected.getId())) {
            List<SearchFilter> nameSearchFilterList = new ArrayList();
            nameSearchFilterList.add(new SearchFilter("name", SearchFilter.Operator.EQ, selected.getName()));
            nameSearchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectedOrg.getTenantUID()));
            List<ArrivalList> res = dao.findBySearchFilter(nameSearchFilterList);
            return (null == res || res.isEmpty());
        } else {
            return true;
        }
    }

    @Override
    public void onBeforeSave(ArrivalList object) {
        object.setTenantUID(selectedOrg.getTenantUID());
        object.setInstitutionUID(selectedOrg.getInstitutionUID());
        object.setHospitalUID(selectedOrg.getHospitalUID());
        object.setSiteUID(selectedOrg.getSiteUID());
    }

    public void cancel() {
        this.selected = null;
    }

    @Override
    public void delete() {
        if (selected.getStatus() == 1) {
            dao.delete(selected);
            this.selected = null;
        } else {
            WebUtil.addErrorMessage("到货清单已经提交,不能删除");
        }
    }

    public void onArrivalListSelected(SelectEvent event) {
        arrivalItemList = itemDao.findByArrivalListId(selected.getId());
        selectedOrg = aiService.getOrgInfoByUid(selected.getSiteUID());
        if(selected.getSupplierId()!=null){
            supplier = aiService.getSupplierById(selected.getSupplierId());
        }
        this.prepareEdit();
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.arrivalItemList = null;
    }

    public void submitArrivalList() {
        if (checkName()) {
            onBeforeSave(selected);
            dao.save(selected);
            if (aiService.submitArrivalList(selected.getId())) {
                WebUtil.addSuccessMessage("到货清单已经提交");
            } else {
                WebUtil.addErrorMessage("到货清单提交失败");
            };
            arrivalItemList = itemDao.findByArrivalListId(selected.getId());
            selected = dao.findById(selected.getId());
        } else {
            WebUtil.addErrorMessage("到货单名称/编号 已经存在");
        }
    }

    public void onSelectOrgFromTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
    }

    public void onSupplierChange() {
        if (null != supplier) {
            selected.setSupplierId(supplier.getId());
            selected.setVendor(supplier.getName());
        }
    }
    
    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    //geter and setter
    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public List<ArrivalListItem> getArrivalItemList() {
        return arrivalItemList;
    }

    public void setArrivalItemList(List<ArrivalListItem> arrivalItemList) {
        this.arrivalItemList = arrivalItemList;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

}
