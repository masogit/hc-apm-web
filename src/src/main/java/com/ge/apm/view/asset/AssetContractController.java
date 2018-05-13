package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetContractRepository;
import com.ge.apm.dao.AssetDepreciationRepository;
import com.ge.apm.dao.AssetInfoRepository;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class AssetContractController extends JpaCRUDController<AssetContract> {

    AssetContractRepository dao = null;

    AssetDepreciationRepository depredao = null;
    
    AssetInfo selectedAsset;

    BlobFileService blobService;

    UaaService uaaService;
    
    AssetInfoService assetService;

    String fileName;

    Integer pmCount;
    
    V2_BlobObject blobObject;

    AssetInfoRepository assetInfoDao;

    List<Integer>  contractType;
    @Override
    protected void init() {

        dao = WebUtil.getBean(AssetContractRepository.class);
        depredao = WebUtil.getBean(AssetDepreciationRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        assetService = WebUtil.getBean(AssetInfoService.class); 
        uaaService = WebUtil.getBean(UaaService.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        if (null != encodeStr) {
            String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
            String requestAssetId = (String) UrlEncryptController.getValueFromMap(encodeStr, "assetId");
            if (null != actionName && actionName.equals("Create")) {
                try {
                    this.prepareCreate();
                    this.selected.setAssetId(Integer.parseInt(requestAssetId));
                    this.selectedAsset = assetService.getAssetInfo(Integer.parseInt(requestAssetId));
                    this.selected.setHospitalId(selectedAsset.getHospitalId());
                } catch (InstantiationException ex) {
                    Logger.getLogger(AssetContractController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AssetContractController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

            }
        }

        this.filterBySite = false;

//        this.setSiteFilter();
        contractType = Arrays.asList(1,2,3);
    }

    @Override
    protected AssetContractRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetContract> loadData(PageRequest pageRequest) {
        if(searchFilters == null){
            searchFilters = new ArrayList();
        }
        
        if(!this.hasFilterOnField("siteUID")){
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("contractType", SearchFilter.Operator.IN, contractType));
        }
        
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void removeAttachmentById(V2_BlobObject blobObject) {
        blobService.deleteBlobFileById(blobObject.getId());
        //删除附件的时候清空
        selected.setObjectId(null);
    }



    @Override
    public void onFilter(FilterEvent event) {
        selected = null;
        super.onFilter(event); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AssetContract> getItemList() {
        return dao.findBySearchFilter(searchFilters);
    }

    @Override
    public void onBeforeNewObject(AssetContract object) {
        object.setSiteId(UserContextService.getSiteId());
        this.selectedAsset = null;
    }
    @Override
    public void save() {
        if (selected.getEndDate().before(selected.getStartDate())) {
            WebUtil.addErrorMessage(MessageFormat.format(WebUtil.getMessage("shouldEarly"), WebUtil.getMessage("startTime"), WebUtil.getMessage("endTime")));
        } else if (null == selectedAsset) {
            WebUtil.addErrorMessage(WebUtil.getMessage("assetName") + WebUtil.getMessage("ValidationRequire"));
        } else if (null == selected.getObjectId()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
        } else {
            selected.setAssetId(selectedAsset.getId());
            super.save();
            blobObject.setBoId(selected.getId());
            blobObject.setBoType(V2_BlobObject.BO_TYPE_AssetContract);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            saveDepreciation();
            //结束时间晚于维保时间，那么就更新维保时间



            udpateAssetInfoWarranty();
//            if (this.selected.getContractType() == 5) {
//                pmOrderService.savePmOrder(selectedAsset, this.selected.getStartDate(), this.selected.getEndDate(), pmCount, null);
//            }
            cancel();
        }


    }

    @Override
    public void onBeforeSave(AssetContract object) {
        DateTime temp;
        if (null != object.getStartDate()) {
            temp = new DateTime(object.getStartDate());
            object.setStartDate(temp.plusHours(12).toDate());
        }
        if (null != object.getEndDate()) {
            temp = new DateTime(object.getEndDate());
            object.setEndDate(temp.plusHours(12).toDate());
        }
    }

    private void udpateAssetInfoWarranty(){
        if(null==selectedAsset.getWarrantyDate()){
            selectedAsset.setWarrantyDate(selected.getEndDate());
            assetInfoDao.save(selectedAsset);
        }else if(selected.getEndDate().after(selectedAsset.getWarrantyDate())){
            selectedAsset.setWarrantyDate(selected.getEndDate());
            assetInfoDao.save(selectedAsset);
        }
    }
    private void saveDepreciation() {
        List<AssetDepreciation> depreList = depredao.getByContractId(selected.getId());
        depredao.delete(depreList);
        AssetDepreciation depre = new AssetDepreciation();
        depre.setAssetId(selected.getAssetId());
        depre.setSiteId(selected.getSiteId());
        depre.setContractId(selected.getId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selected.getEndDate());
        int endMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);
        calendar.setTime(selected.getStartDate());
        int startMonth = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH);
        int times = (endMonth - startMonth + 1);
        depre.setDeprecateAmount(selected.getContractAmount() / times);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        depre.setDeprecateDate(calendar.getTime());
        depredao.save(depre);
        for (int i = 1; i < times; i++) {
            calendar.add(Calendar.MONTH, 1);
            depre.setDeprecateDate(calendar.getTime());
            depre.setId(null);
            depredao.save(depre);
        }

    }

    public void cancel() {
        this.selected = null;
        this.selectedAsset = null;
    }

    @Override
    public void delete() {
        List<V2_BlobObject> blobList = blobService.getBlobObjectsByStorageId(selected.getObjectId());
        if(blobList== null || blobList.size()!=1){
            WebUtil.addErrorMessage("不能删除");
            return;
        }
        if (blobService.deleteBlobFileById(blobList.get(0).getId())) {
            List<AssetDepreciation> depreList = depredao.getByContractId(selected.getId());
            depredao.delete(depreList);
            dao.delete(selected);
        }
        this.selected = null;
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



    public void onAssetSelected(SelectEvent event) {
        onServerEvent("assetSelected", event.getObject());
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }

        if (this.selected == null) {
            return;
        }
        this.selected.setAssetId(asset.getId());
        this.selected.setHospitalId(asset.getHospitalId());
        this.selected.setTenantUID(asset.getTenantUID());
        this.selected.setInstitutionUID(asset.getInstitutionUID());
        this.selected.setHospitalUID(asset.getHospitalUID());
        this.selected.setSiteUID(asset.getSiteUID());
        this.selectedAsset = asset;
    }

    public void onSelectedChange() {
        selectedAsset = assetService.getAssetInfo(selected.getAssetId());
        List<V2_BlobObject> fileObjectList = blobService.getBlobObjectsByStorageId(selected.getObjectId());
        if(fileObjectList== null || fileObjectList.size()!=1){
            WebUtil.addErrorMessage("找不到附件");
            return;
        }
        V2_BlobObject fileObject = fileObjectList.get(0);
        blobObject=fileObject;
        fileName = fileObject==null?"":fileObject.getObjectName();
        crudActionName = "Edit";
    }

    public boolean isCreatAction() {
        return "Create".equals(crudActionName);
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getPmCount() {
        return pmCount;
    }

    public void setPmCount(Integer pmCount) {
        this.pmCount = pmCount;
    }

    public V2_BlobObject getBlobObject() {
        return blobObject;
    }

    public void setBlobObject(V2_BlobObject blobObject) {
        this.blobObject = blobObject;
    }

    public List<I18nMessage> getContractTypeList(){
        return FieldValueMessageController.doGetFieldValueList("contractType", -1).stream()
                .filter(item -> contractType.contains(Integer.parseInt(item.getMsgKey()))).collect(Collectors.toList());
    }
    
    
}
