package com.ge.apm.view.asset;

import com.ge.apm.dao.ArrivalListItemRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import com.ge.apm.service.asset.QrCodeTemplateService;

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

    @Override
    protected void init() {
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        dao = WebUtil.getBean(ArrivalListItemRepository.class);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        addDateTimeFields("arrivalList.arrivalDate");
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
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
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
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, fileterDeptOrg.getUid(), "1*"));
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
        if (null != event && null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            OrgInfo deptOrg = (OrgInfo) event.getTreeNode().getData();
            if (deptOrg.getOrgType() == 4) {
                fileterDeptOrg = deptOrg;
                return;
            }
        }
        fileterDeptOrg = null;
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

}
