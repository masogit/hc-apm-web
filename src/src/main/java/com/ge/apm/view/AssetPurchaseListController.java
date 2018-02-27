package com.ge.apm.view;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.ge.apm.dao.AssetPurchaseListRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.domain.ArrivalList;
import com.ge.apm.domain.AssetPurchaseList;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetArrivedListEditController;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

@ManagedBean
@ViewScoped
public class AssetPurchaseListController extends GenericCRUDUUIDController<AssetPurchaseList> {

 
    private String operation;
    private V2_BlobObject blobObject;
    private String fileName;
    private BlobFileService blobService;
    private ArrivalList arrivalList;
    private String siteName;
    private UaaService uuaService;
    public ArrivalList getArrivalList() {
        return arrivalList;
    }

    public void setArrivalList(ArrivalList arrivalList) {
        this.arrivalList = arrivalList;
    }
//    private List<ArrivalList> arrivalList;
//    
//    public List<ArrivalList> getArrivalList(List<ArrivalList> arrivalList) {
//        return arrivalList;
//    }
//
//    public void setArrivalList(List<ArrivalList> arrivalList) {
//        this.arrivalList = arrivalList;
//    }
    AssetPurchaseListRepository dao = null;
    I18nMessageRepository I18dao = null;
    OrgInfoRepository orgDao =null; 
    SupplierRepository supplierdao = null;
    //ArrivalListRepository arrdao = null;
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    String encodeStr = request.getParameter("str");
    String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
    
    public void saved(){
      if (null != selected.getClinicalDeptUid()) {
            OrgInfo org = orgDao.getByUid(selected.getClinicalDeptUid());
            selected.setClinicalDeptName(org.getName());
            
        } else {
            
        }
         this.save();
    }
    public void saves(){
        super.save();
        AssetPurchaseList list=dao.findById(this.selected.getId());
        
        arrivalList.setAssetGroup(list.getAssetType());
        arrivalList.setStatus(list.getReachState());
        arrivalList.setQuantity(list.getReachQty());
        arrivalList.setStatus(list.getReachState());
        arrivalList.setQuantity(list.getReachQty());
        arrivalList.setSiteUID(list.getSiteUID());
        arrivalList.setTenantUID(list.getTenantUID());
        arrivalList.setHospitalUID(list.getHospitalUID());
        arrivalList.setInstitutionUID(list.getInstitutionUID());
        if (arrivalList.getSupplierId() !=null){
            String supplierName=supplierdao.findById(arrivalList.getSupplierId()).getName();
            arrivalList.setVendor(supplierName);
        }

        AssetArrivedListEditController arrivalController = WebUtil.getBean(AssetArrivedListEditController.class);
        arrivalController.setSelected(arrivalList);
        arrivalController.onBeforeNewObject(arrivalList);
        arrivalController.submitArrivalList();
/*        
        ArrivalListRepository arrival=WebUtil.getBean(ArrivalListRepository.class);
//        ArrivalList arrivalList=new ArrivalList();
        arrivalList.setAssetGroup(list.getAssetType());
        arrivalList.setStatus(list.getReachState());
        arrivalList.setQuantity(list.getReachQty());
        arrivalList.setStatus(list.getReachState());
        arrivalList.setQuantity(list.getReachQty());
        arrivalList.setSiteUID(list.getSiteUID());
        arrivalList.setTenantUID(list.getTenantUID());
        arrivalList.setHospitalUID(list.getHospitalUID());
        arrivalList.setInstitutionUID(list.getInstitutionUID());
        if (arrivalList.getSupplierId() !=null){
        String supplierName=supplierdao.findById(arrivalList.getSupplierId()).getName();
        arrivalList.setVendor(supplierName);}

        arrival.save(arrivalList);
*/
    }
    
    @Override
    protected void init() {
        arrivalList=new ArrivalList();
        uuaService = WebUtil.getBean(UaaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        dao = WebUtil.getBean(AssetPurchaseListRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        supplierdao=WebUtil.getBean(SupplierRepository.class);
        I18dao = WebUtil.getBean(I18nMessageRepository.class);
        //arrdao = WebUtil.getBean(ArrivalListRepository.class);
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
        else if ("View".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            //setSelected(dao.findById((String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
         
            prepareView();
        }
        else if ("Edit".equalsIgnoreCase(actionName)) {
//            setSelected(Integer.parseInt(WebUtil.getRequestParameter("selectedid")));
//            setSelected(Integer.parseInt((String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
//            prepareEdit();
            
            setSelected(dao.findById((String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            prepareEdit();
        }
        else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        } 
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }
   

    @Override
    protected AssetPurchaseListRepository getDAO() {
        return dao;
        
    }
    public void getViewPageA(String pageName, String actionName) {
        operation = pageName + "?actionName=" + actionName + "&selectedid=" + selected.getId();
    }
    private void setUserSearchFilters() {
//        searchFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        
        

    }

    @Override
    protected Page<AssetPurchaseList> loadData(PageRequest pageRequest) {
       selected = null;
       setUserSearchFilters();
        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    String url;
    public String getViewPage(String pageName, String actionName) throws IOException {
     url=pageName+".xhtml?actionName="+actionName+"&faces-redirect=true";
        if(actionName.equalsIgnoreCase("edit")){
        url= url.concat("&selectedId=").concat(this.selected.getId());
        }
        return url;
    }
    public void onSelectAsset() throws IOException {
        String url = "actionName=Edit&selectedid="
                + selected.getId();
        url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    @Override
    public List<AssetPurchaseList> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }
    public String getOperation() {
        return operation;
    }
    public void deleteobject() {
        this.delete();
    }
    public void handleFileUpload(FileUploadEvent event) {
           blobObject = blobService.uploadFile(event);
           if (null != blobObject) {
               fileName = blobObject.getObjectName();
               selected.setObjectId(blobObject.getObjectStorageId());
           } else {
               WebUtil.addErrorMessage("fail to upload file");
           }
    }
    public String getReachEditPage(String assetId) {
        
         AssetPurchaseList list=dao.findById(assetId);
         OrgInfo orgInfo = orgDao.getByUid(list.getSiteUID());
         siteName=orgInfo.getName();
         operation = "reachEdit.xhtml?actionName=Edit&selectedid=" + assetId;
         
         
        
        
        //ArrivalList o=new ArrivalList();
        return UrlEncryptController.encodeUrlParam(operation);
    }
    public String getSelectPage(String assetId) {
        operation = "Edit.xhtml?actionName=Edit&selectedid=" + assetId;
        return UrlEncryptController.encodeUrlParam(operation);
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
     public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }

    public void removeAttachment(String objectId) {
        if (blobService.deleteBlobFileById(objectId)) {
            selected.setName(null);
            selected.setObjectId(null);
            this.fileName = "";
        }
    }

    @Override
    public void onBeforeNewObject(AssetPurchaseList object) {
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setReachState(1);
        
    }
    /*
    @Override
    public void onAfterNewObject(AssetPurchaseList object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(AssetPurchaseList object) {
    }
    
    @Override
    public void onAfterUpdateObject(AssetPurchaseList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(AssetPurchaseList object) {
    }
    
    @Override
    public void onAfterDeleteObject(AssetPurchaseList object, boolean isOK) {
    }
    
    @Override
    public void onBeforeSave(AssetPurchaseList object) {
    }
    
    @Override
    public void onAfterDataChanged(){
    };
*/

    /**
     * @return the siteName
     */
    public String assetTypeName(Integer assetType){
        I18nMessage i18nMessage = I18dao.getByMsgTypeAndMsgKey("assetGroup", assetType.toString());
        return i18nMessage.getValueEn();
    }
    public String getSiteName() {
        AssetPurchaseList list=dao.findById(this.selected.getId());
         OrgInfo orgInfo = orgDao.getByUid(list.getSiteUID());
        return orgInfo.getName();
    }
}