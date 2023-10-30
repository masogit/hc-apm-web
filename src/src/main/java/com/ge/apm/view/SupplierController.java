package com.ge.apm.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.ArrivalListRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.SupplierRepository;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.WebUtil;
import org.apache.commons.codec.binary.Base64;

@ManagedBean
@ViewScoped
public class SupplierController extends JpaCRUDController<Supplier> {

    private SupplierRepository dao = null;
    private BlobFileService blobService;
    private List<V2_BlobObject> attachements;
    private UserAccount currentUser;

    private AssetInfoRepository assetInfoDao;
    private ArrivalListRepository arrivalListDao;
    private ArrivalListItemRepository arrivalListItemDao;
    private SupplierService supplierService;

    private String supplierType;
    private List<SupplierUser> approvedUserList;
    private List<SupplierUser> pendingApprovalUserList;
    private String queryStrPending;
    private String queryStrApproved;

    private Map<String, Boolean> selectedMapPending;
    private Map<String, Boolean> selectedMapApproved;

    private boolean isShowQrCode = Boolean.FALSE;

    private Boolean isShowSupplierCertificate = null;

    @Getter @Setter
    private List<Supplier> currentPageSuppliers;
    @Getter @Setter
    private Map<Integer, Boolean> selectedMap; //复选的供应商
    @Getter @Setter
    private Boolean isSelectedCurrentPage = false;

    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        arrivalListItemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = false;
        this.selectedMapPending = new HashMap();
        this.selectedMapApproved = new HashMap();
        this.selectedMap = new HashMap();
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
        Page<Supplier> res;
        if (this.searchFilters == null) {
            res = dao.findAll(pageRequest);
        } else {
            res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
        currentPageSuppliers = res.getContent();
        return res;
    }

    @Override
    public void onBeforeNewObject(Supplier supplier) {
        super.onBeforeNewObject(supplier);

        selected.setSiteId(currentUser.getSiteId());
        selected.setHospitalId(currentUser.getHospitalId());
        selected.setTenantUID(currentUser.getTenantUID());
        selected.setInstitutionUID(currentUser.getInstitutionUID());
        selected.setHospitalUID(currentUser.getHospitalUID());
        selected.setSiteUID(currentUser.getSiteUID());
        selected.setCreatedDate(new Date());
        selected.setCreatedBy(currentUser.getLoginName());
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        attachements = new ArrayList<>();
    }

    @Override
    public void prepareEdit(){
        super.prepareEdit();
        attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
    }

    @Override
    public String getKeyFieldNameValue(Supplier supplier){
        return WebUtil.getMessage("name")+"="+supplier.getName();
    }

    public void saveSubmit(){
        Boolean mark = this.selected.getManufacturerT() || this.selected.getSupplierT() || this.selected.getMaintainerT();
        if (!mark) {//表示供应商类型一个都没有选
            WebUtil.addErrorMessage(WebUtil.getMessage("TypeErrTip"));
            RequestContext.getCurrentInstance().addCallbackParam("spType", true);
            return;
        }

        selected.setLastModifiedDate(new Date());
        selected.setLastModifiedBy(currentUser.getLoginName());
        supplierService.saveSupplier(selected);
        onAfterNewObject(selected, true);
    }

    @Override
    public void onAfterNewObject(Supplier object, boolean isOK) {
        super.onAfterNewObject(object, isOK);
        if(isOK){
            if (attachements != null){
                for (V2_BlobObject blobObject: attachements) {
                    if (StringUtils.isEmpty(blobObject.getBoUuid())) {
                        blobObject.setBoUuid(object.getUid());
                        blobService.saveBlobObject(blobObject);
                    }
                }
            }
        }
    }

    @Override
    public void onAfterUpdateObject(Supplier object, boolean isOK) {
        super.onAfterUpdateObject(object, isOK);
    }

    public void batchDeleteSupplier() {
//        selectedMap.forEach((k,v) -> {
//            selected = currentPageSuppliers.stream().filter(s -> BooleanUtils.isTrue(v) && s.getId().compareTo(k) == 0 ).findFirst().orElse(null);
//            if (selected != null) {
//                deleteSupplier();
//            }
//        });
        selectedMap.clear();
    }

    public void deleteSupplier() {

        List<AssetInfo> assetInfoList = assetInfoDao.findBySupplierIdOrMaitananceIdOrManufactureId(selected.getId(), selected.getUid(), selected.getUid());
        List<ArrivalList> arrivalListList = arrivalListDao.findBySupplierId(selected.getId());

        if(assetInfoList.isEmpty() && arrivalListList.isEmpty()){
            dao.delete(selected);
            List<V2_BlobObject> tempBlobObject = blobService.findBlobObjectsByBoUid(selected.getUid());
            blobService.deleteBlobFile(tempBlobObject);
        }

    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        String type = event.getComponent().getId();
        type = type.substring(type.indexOf("type") + 4);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_Supply);
            blobObject.setBoSubType(type);
            blobObject.setBoUuid(selected.getUid());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public List<V2_BlobObject> getAttachList(String type) {
        return attachements == null ? null : attachements.stream().filter(item -> item.getBoSubType() == null || item.getBoSubType().equals(type)).collect(Collectors.toList());

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

    public void initSupplierUser(){
        this.selectedMapPending = new HashMap();
        this.selectedMapApproved = new HashMap();
        queryStrPending = null;
        queryStrApproved = null;
        queryByPending();
        queryByApproved();
    }

    public void queryByPending(){
        pendingApprovalUserList = supplierService.getSupplierUserList(null, SupplierUser.Status.PendingApproval.name(), queryStrPending);
    }

    public void queryByApproved(){
        approvedUserList = supplierService.getSupplierUserList(null, SupplierUser.Status.Approved.name(), queryStrApproved);
    }

    public boolean isChecked() {
        Map<Integer, Boolean> map = this.selectedMap;
        for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public void onItemSelected() {
        isSelectedCurrentPage = !currentPageSuppliers.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        selected = null;
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageSuppliers.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (Supplier item : currentPageSuppliers) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
        isSelectedCurrentPage = !isSelectedAll;
        selected = null;
    }

    public void onSelectAllOfPagePending() {
        boolean isSelectedAll = !pendingApprovalUserList.stream().anyMatch(item -> !selectedMapPending.containsKey(item.getId()) || !selectedMapPending.get(item.getId()));
        for (SupplierUser item : pendingApprovalUserList) {
            selectedMapPending.put(item.getId(), !isSelectedAll);
        }
    }

    //供应商责任人通过
    public void onApprove(){
        selectedMapPending.forEach((key, value) -> {
            if (value) {
                supplierService.approveByID(key);
            }
        });
        initSupplierUser();
    }

    //供应商责任人拒绝
    public void onRefuse(){
        selectedMapPending.forEach((key, value) -> {
            if (value) {
                supplierService.refuseByID(key);
            }
        });
        initSupplierUser();
    }

    //供应商责任人停用
    public void onRefuse(SupplierUser supplierUser){
        supplierService.deleteByID(supplierUser.getId());
        initSupplierUser();
    }

    //供应商小程序二维码
    public String getWxaCodeImage(){

        byte [] tempPicByte = supplierService.getWxaCodeImage();
        if(tempPicByte != null){
            String res = "data:image/png;base64,";
            return  res + Base64.encodeBase64String(tempPicByte);
        }else {
            return "";
        }

    }

    //微信公众号二维码
    public String getWeChatQrCodeImage(){

        byte [] tempPicByte = supplierService.getWeChatCodeImage();
        if(tempPicByte != null){
            String res = "data:image/png;base64,";
            return  res + Base64.encodeBase64String(tempPicByte);
        }else {
            return "";
        }

    }

    private UploadedFile file;
    private StreamedContent exportFile;

    public void exportAssetExcel() throws IOException {
        List<Integer> selectedIds = selectedMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
        List<Supplier> exportList;
        if (ObjectUtil.isNotEmpty(selectedIds)) {
            exportList = dao.findBySearchFilter(ListUtil.newArrayList(new SearchFilter("id", SearchFilter.Operator.IN, selectedIds)));
        } else {
            exportList = dao.findBySearchFilter(this.searchFilters);
        }
        exportList = exportList.stream().sorted(Comparator.comparing(Supplier::getId).reversed()).collect(Collectors.toList());
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Supplier supplier : exportList) {
            Map<String, Object> map = MapUtil.newHashMap();
            map.put("uid", supplier.getUid());
            map.put("名称", supplier.getName());
            map.put("城市", supplier.getCity());
            map.put("地址", supplier.getAddress());
            map.put("邮编", supplier.getZipcode());
            map.put("联系人姓名", supplier.getContactor());
            map.put("联系方式", supplier.getTel());
            map.put("类型", getGongyingshangLeixing(supplier));
            resultList.add(map);
        }
        InputStream stream = SupplierController.class.getClassLoader().getResourceAsStream("template/APM_Supplier.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        String[] columnKeys = doc.getKeyRow(0, 1);
        int startNum = 1;
        for (Map<String, Object> map : resultList) {
            doc.writeRowData(0, columnKeys, ++startNum, map);
        }
        exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "供应商信息.xlsx");
    }

    public void handleExcelUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        String resultMessage = handleExcel(file);
        if (ObjectUtil.isNotEmpty(resultMessage)) {
            message = new FacesMessage(resultMessage, event.getFile().getFileName() + " 处理完成！");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private String handleExcel(UploadedFile file) {
        StringBuilder sb = new StringBuilder();
        int rowNum = 1;
        try (ExcelDocument doc = new ExcelDocument(file.getInputstream(), Objects.requireNonNull(ExcelDocument.parseTypeByFileName(file.getFileName())))) {
            List<Map<String, Object>> dataRows = doc.getAllDataRows(0, 1, 2);
            List<Supplier> saveList = ListUtil.newArrayList();
            List<String> updateUidList = dataRows.stream().map(map -> (ExcelDocument.ValueWrapper) map.get("uid")).map(wrapper -> (String) wrapper.getValue()).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            Map<String, Supplier> updateMap = dao.findByUidIn(updateUidList).stream().collect(Collectors.toMap(Supplier::getUid, t -> t));
            for (Map<String, Object> dataRow : dataRows) {
                ++rowNum;
                Supplier supplier = new Supplier();
                ExcelDocument.ValueWrapper uidWrapper = (ExcelDocument.ValueWrapper) dataRow.get("uid");
                if (ObjectUtil.isNotEmpty(uidWrapper.value)) {
                    supplier = updateMap.get(uidWrapper.value);
                }
                for (Map.Entry<String, Object> dataEntry : dataRow.entrySet()) {
                    ExcelDocument.ValueWrapper valueWrapper = (ExcelDocument.ValueWrapper) dataEntry.getValue();
                    Object value = valueWrapper.value;
                    switch (dataEntry.getKey()) {
                        case "名称":
                            if (ObjectUtil.isNotEmpty(value)) {
                                supplier.setName((String) value);
                            } else {
                                sb.append("第").append(rowNum).append("行：名称为必填！");
                            }
                            break;
                        case "城市":
                            supplier.setCity((String) value);
                            break;
                        case "地址":
                            supplier.setAddress((String) value);
                            break;
                        case "邮编":
                            supplier.setZipcode((String) value);
                            break;
                        case "联系人姓名":
                            supplier.setContactor((String) value);
                            break;
                        case "联系方式":
                            supplier.setTel((String) value);
                            break;
                        case "类型":
                            if (ObjectUtil.isNotEmpty(value)) {
                                String strValue = (String) value;
                                supplier.setSupplierT(strValue.contains("供应商"));
                                supplier.setMaintainerT(strValue.contains("维修商"));
                                supplier.setManufacturerT(strValue.contains("制造商"));
                            } else {
                                sb.append("第").append(rowNum).append("行：类型为必填！");
                            }
                            break;
                        default:
                            break;
                    }
                }
                supplier.setLastModifiedDate(new Date());
                supplier.setLastModifiedBy(currentUser.getLoginName());
                if (ObjectUtil.isEmpty(supplier.getUid())) {
                    selected = supplier;
                    onBeforeNewObject(supplier);
                } else {
                    updateUidList.add(supplier.getUid());
                }
                saveList.add(supplier);
            }
            for (Supplier supplier : saveList) {
                supplierService.saveSupplier(supplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean activeSupplier(){
        return supplierService.activeSupplier();
    }

    public boolean activeSupplierCertificateManage(){
        if(isShowSupplierCertificate == null){
            isShowSupplierCertificate = supplierService.activeSupplierCertificateManage();
        }
        return isShowSupplierCertificate;
    }

    public void onShowQrCode(){
        isShowQrCode = Boolean.TRUE;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public List<SupplierUser> getPendingApprovalUserList() {
        return pendingApprovalUserList;
    }

    public void setPendingApprovalUserList(List<SupplierUser> pendingApprovalUserList) {
        this.pendingApprovalUserList = pendingApprovalUserList;
    }

    public List<SupplierUser> getApprovedUserList() {
        return approvedUserList;
    }

    public void setApprovedUserList(List<SupplierUser> approvedUserList) {
        this.approvedUserList = approvedUserList;
    }

    public String getQueryStrPending() {
        return queryStrPending;
    }

    public void setQueryStrPending(String queryStrPending) {
        this.queryStrPending = queryStrPending;
    }

    public String getQueryStrApproved() {
        return queryStrApproved;
    }

    public void setQueryStrApproved(String queryStrApproved) {
        this.queryStrApproved = queryStrApproved;
    }

    public Map<String, Boolean> getSelectedMapPending() {
        return selectedMapPending;
    }

    public void setSelectedMapPending(Map<String, Boolean> selectedMapPending) {
        this.selectedMapPending = selectedMapPending;
    }

    public Map<String, Boolean> getSelectedMapApproved() {
        return selectedMapApproved;
    }

    public void setSelectedMapApproved(Map<String, Boolean> selectedMapApproved) {
        this.selectedMapApproved = selectedMapApproved;
    }

    public boolean isShowQrCode() {
        return isShowQrCode;
    }

    public void setShowQrCode(boolean showQrCode) {
        isShowQrCode = showQrCode;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public boolean verifyFileSuffixName(String fileName,String fileSuffixName){
        String[] fileSuffixNames = fileSuffixName.toUpperCase().split("\\|");
        for(int i=0;i<fileSuffixNames.length;i++){
            if(fileName.toUpperCase().endsWith(fileSuffixNames[i])){
                return true;
            }
        }
        return false;
    }
}