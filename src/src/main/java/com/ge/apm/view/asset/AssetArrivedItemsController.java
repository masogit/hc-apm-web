package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;

import java.io.IOException;
import java.util.*;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tree.Tree;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetArrivedItemsController extends GenericCRUDUUIDController<ArrivalListItem> {

    private ArrivalListItemRepository dao = null;

    private AssetInstallationService aiService;
    private UaaService uaaService;
    private List<V2_BlobObject> picList;

    private ArrivalListItem selectedItem;

    private Date varArrivalDateTo = null;
    private String arrivalFormatTime = null;

    private OrgInfo fileterDeptOrg;
    
    private String exportQrCodeTemplateId;
    
    private QrCodeService qrCodeService;

    private OrgInfoTreeService orgInfoTreeService;

    private TreeNode selectedNode;

    private SupplierService supplierService;

    private ArrivalListRepository arrivalListDao = null;

    private String mergeContractNum;

    @Override
    protected void init() {
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        addDateTimeFields("arrivalList.arrivalDate");
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        getSavedArrivalContractNum();
    }

    @Override
    protected ArrivalListItemRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<ArrivalListItem> loadData(PageRequest pageRequest) {

        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        }
        if (!this.hasFilterOnField("arrivalList.processType")) {
            searchFilters.add(new SearchFilter("arrivalList.processType", SearchFilter.Operator.EQ, "Normal"));
        }

        if (null != fileterDeptOrg) {
            if (selectedNode.isLeaf()) {
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterDeptOrg.getUid(), "1*"));
            } else {
                List<OrgInfo> allClinicals = orgInfoTreeService.collectChildClinicalDeptByParent(selectedNode);
                List<String> clinicalUids = allClinicals.stream().map(OrgInfo::getUid).collect(Collectors.toList());
                this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, clinicalUids, "1*"));
            }


        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event);
    }

    public void onRowSelect(SelectEvent event) {
        this.selected = (ArrivalListItem) event.getObject();
        picList = aiService.getPicList(selected.getId());
    }

    public void prepareTransOrder(String itemId) {
        selectedItem = dao.findById(itemId);
    }

    public List<UserAccount> getWorkerList() {
        return aiService.getWorkerList();
    }

    public void reassignWorkOrder() {
        aiService.reAssignWorkOrder(selectedItem);
        cancel();
    }

    public void takeWorkOrder(String itemId) {
        ArrivalListItem item = dao.findById(itemId);
        item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
        aiService.reAssignWorkOrder(item);
        cancel();
    }

    public void takeWorkOrderBatch(String itemId) {
        ArrivalListItem item = dao.findById(itemId);
        String contractNum = item.getArrivalList().getContractNum();
        String clinicalDeptUID = item.getClinicalDeptUID();
        String errorMessage = "";
        if(StringUtils.isEmpty(contractNum) || StringUtils.isEmpty(clinicalDeptUID)){
            item.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
            Boolean responseStatus = aiService.reAssignWorkOrder(item);
            if(!responseStatus){
                errorMessage = "记录提交失败";
            }
        }else{
            List<ArrivalListItem> itemList = dao.getArrivalListItem(contractNum,clinicalDeptUID,"new");
            for(ArrivalListItem arrivalListItem:itemList){
                arrivalListItem.setCurrentPersonUID(UserContextService.getCurrentUserAccount().getUid());
                Boolean responseStatus = aiService.reAssignWorkOrder(arrivalListItem);
                if(!responseStatus){
                    String name = arrivalListItem.getArrivalList().getName();
                    if(errorMessage.contains("到货单名称/编号："+name)){
                        errorMessage = errorMessage + "、"+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }else{
                        errorMessage = errorMessage + "到货单名称/编号：" + name +","+ WebUtil.getMessage("financingNum")+"："+arrivalListItem.getFinancingNum()+"---->记录提交失败";
                    }
                }
            }
        }
        if(StringUtils.isBlank(errorMessage)){
            WebUtil.addSuccessMessage("接单成功");
        }else{
            WebUtil.addErrorMessage(errorMessage);
        }
        cancel();
    }

    public Boolean isAssignToMe(String currentPersonUID) {
        return UserContextService.getCurrentUserAccount().getUid().equals(currentPersonUID);
    }

    @Override
    public List<ArrivalListItem> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    public void cancel() {
        this.selected = null;
        selectedItem = null;
    }

    public void setSelected(String id) {
        if (Strings.isNullOrEmpty(id)) {
            selected = null;
        }
        selected = getDAO().findById(id);

    }

    public List<I18nMessage> getInstallStatusList() {
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
        List<I18nMessage> statusList = FieldValueMessageController.getFieldValueList("arrivalListItemStatus", siteId);
        if (!userContextService.needInstorageCheck()) {
            return statusList.stream().filter(item -> !ArrivalListItem.INSTALL_STATUS_FINISHED.equals(item.getMsgKey())).collect(Collectors.toList());
        }
        return statusList;
    }

    public String getOrgNameByUid(String orgUid) {
        if (!Strings.isNullOrEmpty(orgUid)) {
            OrgInfo org = aiService.getOrgInfoByUid(orgUid);
            return org==null? null:org.getName();
        }else {
            return null;
        }
    }

    public UserAccount getUserAccountByUid(String uid) {
        return aiService.getUserAccountByUid(uid);
    }

    public List<V2_BlobObject> getAckPicList(String type) {
        return picList == null ? null : picList.stream().filter(item -> type.equals(item.getBoSubType())).collect(Collectors.toList());
    }
    
    public String getInstallUrl(String itemId,String srcPage){
        return UrlEncryptController.encodeUrlParam("ItemEdit?faces-redirect=true&src=".concat(srcPage).concat("&itemId=").concat(itemId));
    }

    public ArrivalListItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ArrivalListItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void onClinicalDeptFilter(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        OrgInfo deptOrg = (OrgInfo) selectedNode.getData();
        if (deptOrg.getOrgType() == 4) {
            fileterDeptOrg = deptOrg;
        } else {
            fileterDeptOrg = null;
        }

    }
    
    public String getQrCodeImageForAsset() {
        AssetInfo asset = new AssetInfo();
        asset.setQrCode(selected.getQrCode());
        asset.setName(selected.getArrivalList().getAssetName());
        asset.setAssetGroup(selected.getArrivalList().getAssetGroup());
        asset.setSiteUID(selected.getArrivalList().getSiteUID());
        asset.setFunctionType(selected.getArrivalList().getFunctionType());
        asset.setClinicalDeptUID(selected.getClinicalDeptUID());
        asset.setSerialNum(selected.getSerialNum());
        asset.setDepartNum(selected.getDepartNum());
        asset.setFinancingNum(selected.getFinancingNum());
        asset.setModalityId(selected.getModalityId());
        asset.setSystemId(selected.getSystemId());
        return qrCodeService.getQrCodeImageBase64WithTemplate(asset, exportQrCodeTemplateId,selected.getArrivalList().getSiteUID() );
    }
    
    public List<QrCodeTemplate> getQrCodeTemplates() {
        QrCodeTemplateService qtService = WebUtil.getBean(QrCodeTemplateService.class);
        return qtService.getTemplateByUser(UserContextService.getCurrentUserAccount());
    }

    public String getArrivalFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        if (varArrivalDateTo == null) {
            return arrivalFormatTime;
        } else {
            return "~" + sdf.format(varArrivalDateTo);
        }
    }

    public void setArrivalFormatTime(String arrivalFormatTime) {
        this.arrivalFormatTime = arrivalFormatTime;
    }

    public Date getVarArrivalDateTo() {
        return varArrivalDateTo;
    }

    public void setVarArrivalDateTo(Date varArrivalDateTo) {
        this.varArrivalDateTo = varArrivalDateTo;
    }

    public OrgInfo getFileterDeptOrg() {
        return fileterDeptOrg;
    }

    public void setFileterDeptOrg(OrgInfo fileterDeptOrg) {
        this.fileterDeptOrg = fileterDeptOrg;
    }

    public String getExportQrCodeTemplateId() {
        return exportQrCodeTemplateId;
    }

    public void setExportQrCodeTemplateId(String exportQrCodeTemplateId) {
        this.exportQrCodeTemplateId = exportQrCodeTemplateId;
    }
    public boolean activeSupplier(){
        return supplierService.activeSupplier();
    }

    public void getSavedArrivalContractNum(){
//        List<SearchFilter> searchFilters = this.searchFilters;
//        if (this.searchFilters == null) {
//            searchFilters = new ArrayList();
//            this.searchFilters = searchFilters;
//        }
//        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
//        searchFilters.add(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("arrivalList.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("arrivalList.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("arrivalList.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
//        searchFilters.add(new SearchFilter("currentPersonUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
//        searchFilters.add(new SearchFilter("arrivalList.processType", SearchFilter.Operator.EQ, "Normal"));
//        searchFilters.add(new SearchFilter("arrivalList.contractNum", SearchFilter.Operator.IsNotNull, null));
//        searchFilters.add(new SearchFilter("arrivalList.supplierStatus", SearchFilter.Operator.EQ, ArrivalList.SupplierStatus.Saved.toString()));
//
//        List<ArrivalListItem> ArrivalListItem = dao.findBySearchFilter(searchFilters);
//        if(ArrivalListItem == null || ArrivalListItem.isEmpty()){
//            return ;
//        }
//        List<String> contractNumList = new ArrayList<>();
//        for(ArrivalListItem list : ArrivalListItem){
//            contractNumList.add(list.getArrivalList().getContractNum());
//        }
//        mergeContractNum = contractNumList.stream().distinct().collect(Collectors.toList()).toString();
        List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
        if(orgUidList != null && !orgUidList.isEmpty()){
            List<String> arrivalListContractNum = dao.getArrivalListNotInstalledContractNum(orgUidList,UserContextService.getCurrentUserAccount().getUid(),ArrivalList.SupplierStatus.Saved.name());
            mergeContractNum = arrivalListContractNum.toString().replace("[","").replace("]","");
        }
    }
    public String getMergeContractNum() {
        return mergeContractNum;
    }

    public void setMergeContractNum(String mergeContractNum) {
        this.mergeContractNum = mergeContractNum;
    }
}
