package com.ge.apm.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.SupplierService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SupplierController extends JpaCRUDController<Supplier> {

    private SupplierRepository dao = null;
    private BlobFileService blobService;
    private List<V2_BlobObject> attachements;

    private AssetInfoRepository assetInfoDao;
    private ArrivalListRepository arrivalListDao;
    private ArrivalListItemRepository arrivalListItemDao;
    private SupplierService supplierService;

    private Boolean maintainerT =false;
    private Boolean manufacturerT =false;
    private Boolean supplierT =false;

    private String supplierType;
    private List<UserAccount> tempStaffList;
    private List<UserAccount> approvedUserList;
    private List<SupplierUser> pendingApprovalUserList;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        arrivalListItemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        this.filterBySite = false;
    }

    @Override
    protected SupplierRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<Supplier> loadData(PageRequest pageRequest) {
        if(this.searchFilters ==null){
            searchFilters =  new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,UserContextService.getCurrentUserAccount().getTenantUID()));
        boolean nameFilter=false;
        Object nameValue="";
        for(SearchFilter filter: searchFilters){
            if(filter.fieldName.equals("name")){
                nameFilter=true;
                nameValue=filter.value;
                filter.conditionPath="1*2*3+";
            }
        }
        if(nameFilter){
            nameValue=((String)nameValue).toLowerCase();
            searchFilters.add(new SearchFilter("namePyCode",SearchFilter.Operator.LIKE,nameValue,"1*2*3+"));
        }
        if(this.hasFilterOnField("supplierType")){
            switch (supplierType){
                case "1": searchFilters.add(new SearchFilter("supplierT",SearchFilter.Operator.EQ,true));break;
                case "2": searchFilters.add(new SearchFilter("maintainerT",SearchFilter.Operator.EQ,true));break;
                case "3": searchFilters.add(new SearchFilter("manufacturerT",SearchFilter.Operator.EQ,true));break;

            }
            this.removeFilterOnField("supplierType");

        }
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }

    public void onSelectSupplier(){
        if(selected!=null){
            maintainerT=selected.getMaintainerT();
            manufacturerT=selected.getManufacturerT();
            supplierT=selected.getSupplierT();
        }
    }

    @Override
    public void onBeforeNewObject(Supplier supplier) {
        supplier.setSiteId(UserContextService.getSiteId());
        supplier.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        supplierT=null;
        maintainerT=null;
        manufacturerT=null;

        attachements=new ArrayList<V2_BlobObject>();
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
        attachements=getAttachList(selected.getUid(),"1");
    }

    @Override
    public void onAfterNewObject(Supplier object, boolean isOK) {
        if (isOK) {
            if (attachements != null){
                attachements.forEach(item -> {
                    if (item.getBoId() == null) {
                        item.setBoId(object.getId());
                        item.setBoUuid(object.getUid());
                        blobService.saveBlobObject(item);
                    }
                });
            }
        }
    }

    @Override
    public String getKeyFieldNameValue(Supplier supplier){
        return WebUtil.getMessage("name")+"="+supplier.getName();
    }

    public void saveSubmit(){
        if (!requiredCheck()) {
            return;
        }
        if(!selected.getMaintainerT()){
            selected.setMaintainerT(false);
        }
        if(!selected.getSupplierT()){
            selected.setSupplierT(false);
        }
        if(!selected.getManufacturerT()){
            selected.setManufacturerT(false);
        }
        UserAccount ua =UserContextService.getCurrentUserAccount();
        selected.setTenantUID(ua.getTenantUID());
        selected.setInstitutionUID(ua.getInstitutionUID());
        selected.setHospitalUID(ua.getHospitalUID());
        selected.setSiteUID(ua.getSiteUID());
        if(selected.getCreatedDate()==null)
          selected.setCreatedDate(new Date());
        selected.setCreatedBy(ua.getLoginName());
        selected.setLastModifiedDate(new Date());
        selected.setLastModifiedBy(ua.getLoginName());

        syncNameChangesWhenUpdating();

        super.save();
        selected=null;
        maintainerT=null;
        manufacturerT=null;
        supplierT=null;

    }

    public boolean requiredCheck(){
        Boolean mark = manufacturerT || supplierT || maintainerT;
        selected.setManufacturerT(manufacturerT);
        selected.setSupplierT(supplierT);
        selected.setMaintainerT(maintainerT);
        if(!mark){//表示供应商类型一个都没有选
            WebUtil.addErrorMessage(WebUtil.getMessage("TypeErrTip"));
            RequestContext.getCurrentInstance().addCallbackParam("spType", true);
            return false;
        }
        return true;
    }

    private void syncNameChangesWhenUpdating() {

        if (selected.getUid() == null) return;

        Supplier supplier = getDAO().findByUid(selected.getUid());
        if (supplier.getName().equals(selected.getName())) return;

        assetInfoDao.updateSupplierName(selected.getName(), selected.getId(), selected.getTenantUID());
        assetInfoDao.updateMaintainerName(selected.getName(), selected.getTel(), String.valueOf(selected.getId()), selected.getTenantUID());
        assetInfoDao.updateManufactureName(selected.getName(), String.valueOf(selected.getId()), selected.getTenantUID());

    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            String type = event.getComponent().getId();
            type = type.substring(type.indexOf("type") + 4);
            attachements.add(blobObject);
            // blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_Supply);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public List<V2_BlobObject> getAttachList(String supplierUid, String type) {
        attachements = blobService.findBlobObjectsByBoUid(supplierUid);
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());

    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    public List<Supplier> getSupplierList() {
        return dao.getBySiteId(UserContextService.getSiteId());
    }
    
    public String getGongyingshangLeixing(Supplier supplier){
        if(supplier==null){
            return "";
        }
        String name =new String();
        if(supplier.getMaintainerT() != null && supplier.getMaintainerT()){
           name= name.concat(WebUtil.getMessage("maitanance")+" ");
        }
        if(supplier.getManufacturerT() != null && supplier.getManufacturerT()){
            name= name.concat(WebUtil.getMessage("manufacture")+" ");
        }
        if(supplier.getSupplierT() != null && supplier.getSupplierT()){
            name= name.concat(WebUtil.getMessage("vendor")+" ");
        }

       name= name.replace(" ","/");
        if(name.length()>0 && name.charAt(name.length()-1)=='/'){
            name= name.substring(0,name.length()-1);
        }
        return name;
    }

    @Override
    public void delete(Supplier obj) {

        List<AssetInfo> assetInfoList = assetInfoDao.findBySupplierIdOrMaitananceIdOrManufactureId(obj.getId(), obj.getId(), obj.getId());
        List<ArrivalList> arrivalListList = arrivalListDao.findBySupplierId(obj.getId());

        if(assetInfoList.isEmpty() && arrivalListList.isEmpty()){
            super.delete(obj);
        }

    }

    public void initSupplierUser(){
        tempStaffList = supplierService.getTempStaffList(this.selected.getTenantUID());
        approvedUserList = supplierService.getApprovedUserList(this.selected.getId());
        pendingApprovalUserList = supplierService.getSupplierUserList(this.selected.getId(), SupplierUser.Status.PendingApproval.name());
    }

    public void onApprove(SupplierUser supplierUser){
        supplierService.approve(supplierUser);
        initSupplierUser();
    }

    public void onRefuse(SupplierUser supplierUser){
        supplierService.refuse(supplierUser);
        initSupplierUser();
    }

    public void saveSupplierUserList(){
        supplierService.updateSupplierUserList(this.selected, this.approvedUserList);
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public Boolean getMaintainerT() {
        return maintainerT;
    }

    public void setMaintainerT(Boolean maintainerT) {
        this.maintainerT = maintainerT;
    }

    public Boolean getManufacturerT() {
        return manufacturerT;
    }

    public void setManufacturerT(Boolean manufacturerT) {
        this.manufacturerT = manufacturerT;
    }

    public Boolean getSupplierT() {
        return supplierT;
    }

    public void setSupplierT(Boolean supplierT) {
        this.supplierT = supplierT;
    }

    public List<UserAccount> getTempStaffList() {
        return tempStaffList;
    }

    public void setTempStaffList(List<UserAccount> tempStaffList) {
        this.tempStaffList = tempStaffList;
    }

    public List<SupplierUser> getPendingApprovalUserList() {
        return pendingApprovalUserList;
    }

    public void setPendingApprovalUserList(List<SupplierUser> pendingApprovalUserList) {
        this.pendingApprovalUserList = pendingApprovalUserList;
    }

    public List<UserAccount> getApprovedUserList() {
        return approvedUserList;
    }

    public void setApprovedUserList(List<UserAccount> approvedUserList) {
        this.approvedUserList = approvedUserList;
    }

    public List<V2_BlobObject> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<V2_BlobObject> attachements) {
        this.attachements = attachements;
    }
}