package com.ge.apm.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.Supplier;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class SupplierController extends JpaCRUDController<Supplier> {

    SupplierRepository dao = null;
    private BlobFileService blobService;
    @Getter @Setter
    private List<V2_BlobObject> attachements;

    AssetInfoRepository assetInfoDao;

    Boolean maintainerT =false;
    Boolean manufacturerT =false;
    Boolean supplierT =false;

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
public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
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
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
                blobService = WebUtil.getBean(BlobFileService.class);
        this.filterBySite = false;
    }

    public List<V2_BlobObject> getAttachList(String supplierUid, String type) {
        attachements = blobService.findBlobObjectsByBoUid(supplierUid);
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());

    }

/*
    public void maintainerTSet(){maintainerT=!maintainerT;}
    public void manufacturerTSet(){ manufacturerT=!manufacturerT;}
    public void supplierTSet(){supplierT=!supplierT;}*/

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

    public void onSelectSupplier(){
        if(selected!=null){
            maintainerT=selected.getMaintainerT();
            manufacturerT=selected.getManufacturerT();
            supplierT=selected.getSupplierT();
        }
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

    private void syncNameChangesWhenUpdating() {

        if (selected.getUid() == null) return;

        Supplier supplier = getDAO().findByUid(selected.getUid());
        if (supplier.getName().equals(selected.getName())) return;

        assetInfoDao.updateSupplierName(selected.getName(), selected.getId(), selected.getTenantUID());
        assetInfoDao.updateMaintainerName(selected.getName(), selected.getTel(), String.valueOf(selected.getId()), selected.getTenantUID());
        assetInfoDao.updateManufactureName(selected.getName(), String.valueOf(selected.getId()), selected.getTenantUID());



    }

    @Override
    protected SupplierRepository getDAO() {
        return dao;
    }

    public List<Supplier> getSupplierList() {
        return dao.getBySiteId(UserContextService.getSiteId());
    }
    
    public Supplier getSupplier(Integer id) {
        return dao.findById(id);
    }
    

    @Override
    public void onBeforeNewObject(Supplier supplier) {
        supplier.setSiteId(UserContextService.getSiteId());
        supplier.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
    }
    
    @Override
    public String getKeyFieldNameValue(Supplier supplier){
        return WebUtil.getMessage("name")+"="+supplier.getName();
    }
    public String supplierType;
    @Override
    protected Page<Supplier> loadData(PageRequest pageRequest) {
        if(this.searchFilters ==null){
            searchFilters =  new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID",
                SearchFilter.Operator.EQ,UserContextService.getCurrentUserAccount().getTenantUID()));
     //   this.selected = null;
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
                case "1": searchFilters.add(new SearchFilter("supplierT"
                        ,SearchFilter.Operator.EQ,true));break;
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

    public SupplierRepository getDao() {
        return dao;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public void setDao(SupplierRepository dao) {
        this.dao = dao;
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
}