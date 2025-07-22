package com.ge.apm.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.SupplierService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.preview.PreViewerBean;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.mvc.JpaCRUDController;
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
    private SupplierContactPersonRepository supplierContactPersonRepository;

    private String supplierType;
    private String source;
    private List<SupplierUser> approvedUserList;
    private List<SupplierUser> pendingApprovalUserList;
    private String queryStrPending;
    private String queryStrApproved;
    private SupplierContactPerson supplierContactPerson;

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
    @Getter @Setter
    private List<SupplierContactPerson> contactPersonList = ListUtil.newArrayList();
    private boolean activeSupplier;
    private boolean activeVender;
    private FileDownloadService fileDownloadService;
    private PreViewerBean preViewerBean;
    @Override
    protected void init() {
        dao = WebUtil.getBean(SupplierRepository.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        arrivalListDao = WebUtil.getBean(ArrivalListRepository.class);
        arrivalListItemDao = WebUtil.getBean(ArrivalListItemRepository.class);
        supplierService = WebUtil.getBean(SupplierService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        supplierContactPersonRepository = WebUtil.getBean(SupplierContactPersonRepository.class);
        preViewerBean = WebUtil.getBean(PreViewerBean.class);

        currentUser = UserContextService.getCurrentUserAccount();
        activeSupplier = supplierService.activeSupplier();
        activeVender = supplierService.activeVender();
        this.filterBySite = false;
        this.selectedMapPending = new HashMap<>();
        this.selectedMapApproved = new HashMap<>();
        this.selectedMap = new HashMap<>();
        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if (ObjectUtil.isNotEmpty(selectedId)) {
            this.selected = dao.findByUid(selectedId);
            if(selected!=null){
                attachements = blobService.findBlobObjectsByBoUid(selected.getUid());

            }
        }
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
        if(this.hasFilterOnField("source")){
            switch (source){
                case "1": searchFilters.add(new SearchFilter("venderCompanyId", SearchFilter.Operator.IsNull,null));break;
                case "2": searchFilters.add(new SearchFilter("venderCompanyId", SearchFilter.Operator.IsNotNull,null));break;
            }
            this.removeFilterOnField("source");
        }
        Page<Supplier> res;
        if (this.searchFilters == null) {
            res = dao.findAll(pageRequest);
        } else {
            res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
        currentPageSuppliers = res.getContent();
        if (!activeSupplier) {
            currentPageSuppliers.forEach(t -> t.setSupplierAlertStatus("0"));
        }
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

    public void prepareAddContactPersonInfo() {
        this.supplierContactPerson = new SupplierContactPerson();
        supplierContactPerson.setSupplierId(this.selected.getId());
        supplierContactPerson.setSupplierUid(this.selected.getUid());
    }

    public List<SupplierContactPerson> getPersonList() {
        if (ObjectUtil.isNotEmpty(this.selected)) {
            contactPersonList = supplierContactPersonRepository.findBySupplierUid(this.selected.getUid());
        }
        return contactPersonList;
    }

    public void prepareEditContactPerson(SupplierContactPerson contactPerson) {
        supplierContactPerson = contactPerson;
    }

    public void removeContactPerson(SupplierContactPerson contactPerson) {
        contactPersonList.remove(contactPerson);
        supplierContactPersonRepository.delete(contactPerson);
        WebUtil.addSuccessMessage(WebUtil.getMessage("vendor") + WebUtil.getMessage("contactPerson") + WebUtil.getMessage("DeleteSuccess"));
    }

    public void saveContactPerson() {
        Boolean mark = this.supplierContactPerson.getManufacturerT() || this.supplierContactPerson.getSupplierT() || this.supplierContactPerson.getMaintainerT();
        if (!mark) {//表示供应商类型一个都没有选
            WebUtil.addErrorMessage(WebUtil.getMessage("TypeErrTip"));
            RequestContext.getCurrentInstance().addCallbackParam("spType", true);
            return;
        }
        supplierContactPerson.setType(getGongyingshangLeixing(supplierContactPerson));
        supplierContactPersonRepository.save(supplierContactPerson);
        WebUtil.addSuccessMessage(WebUtil.getMessage("vendor") + WebUtil.getMessage("contactPerson") + WebUtil.getMessage("SaveSuccess"));
        if (!contactPersonList.contains(supplierContactPerson)) {
            contactPersonList.add(supplierContactPerson);
        }
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
        selectedMap.forEach((k,v) -> {
            selected = currentPageSuppliers.stream().filter(s -> BooleanUtils.isTrue(v) && s.getId().compareTo(k) == 0 ).findFirst().orElse(null);
            if (selected != null) {
                deleteSupplier();
            }
        });
        selectedMap.clear();
    }

    public void deleteSupplier() {

        List<AssetInfo> assetInfoList = assetInfoDao.findCanDelete(selected.getId(), selected.getId() + "", selected.getId() + "");
        List<ArrivalList> arrivalListList = arrivalListDao.findBySupplierId(selected.getId());

        if(assetInfoList.isEmpty() && arrivalListList.isEmpty()){
            try {
                dao.delete(selected);
            } catch (Exception e) {
                WebUtil.addErrorMessage("供应商已绑定资产/责任人，无法删除");
                return;
            }
            WebUtil.addSuccessMessage("供应商删除成功！");
            List<V2_BlobObject> tempBlobObject = blobService.findBlobObjectsByBoUid(selected.getUid());
            blobService.deleteBlobFile(tempBlobObject);
        } else {
            WebUtil.addErrorMessage("供应商已绑定资产/责任人，无法删除");
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
            try {
                blobService.sendImageCompressMQ(blobObject);
            } catch (Exception e) {
                logger.error("供应商，图片生成缩略图MQ发生失败：", e);
            }
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

    public String getGongyingshangLeixing(Supplier supplier) {
        if(supplier==null){
            return "";
        }
        return getGongyingshangLeixing(supplier.getSupplierT(), supplier.getMaintainerT(), supplier.getManufacturerT());
    }

    public String getGongyingshangLeixing(SupplierContactPerson supplier) {
        if(supplier==null){
            return "";
        }
        return getGongyingshangLeixing(supplier.getSupplierT(), supplier.getMaintainerT(), supplier.getManufacturerT());
    }

    public String getGongyingshangLeixing(Boolean supplierT, Boolean maintainerT, Boolean manufacturerT){
        String name =new String();
        if(maintainerT != null && maintainerT){
           name= name.concat(WebUtil.getMessage("maitanance")+" ");
        }
        if(manufacturerT != null && manufacturerT){
            name= name.concat(WebUtil.getMessage("manufacture")+" ");
        }
        if(supplierT != null && supplierT){
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

    public void onSelectAsset() throws IOException {
        if (null == selected) {
            return;
        }
        String url = "selectedid=" + selected.getUid();
        if (ObjectUtil.isNotEmpty(selected.getVenderCompanyId())) {
            url = "venderCompanyId=" + selected.getVenderCompanyId();
        }
        url = "View.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
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
        try {
            supplierService.deleteByID(supplierUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private List<Supplier> noFoundList;

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
//        InputStream stream = SupplierController.class.getClassLoader().getResourceAsStream("template/APM_Supplier.xlsx");

        InputStream stream =fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.SUPPLIER.getCode(), SystemTemplateEnum.SUPPLIER.getFileType());

        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        String[] columnKeys = doc.getKeyRow(0, 1);
        int startNum = 1;
        for (Map<String, Object> map : resultList) {
            doc.writeRowData(0, columnKeys, ++startNum, map);
        }
        exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Supplier_export.xlsx");
    }

    public void handleExcelUpload(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        handleExcel(file);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private List<Supplier> handleExcel(UploadedFile file) {
        int rowNum = 1;
        noFoundList = ListUtil.newArrayList();
        try (ExcelDocument doc = new ExcelDocument(file.getInputstream(), Objects.requireNonNull(ExcelDocument.parseTypeByFileName(file.getFileName())))) {
            List<Map<String, Object>> dataRows = doc.getAllDataRows(0, 1, 2);
            List<Supplier> saveList = ListUtil.newArrayList();
            List<String> updateUidList = dataRows.stream().map(map -> (ExcelDocument.ValueWrapper) map.get("uid")).map(wrapper -> (String) wrapper.getValue()).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
            Map<String, Supplier> updateMap = dao.findByUidIn(updateUidList).stream().collect(Collectors.toMap(Supplier::getUid, t -> t));
            List<Supplier> supplierList = dao.getByTenantUID(currentUser.getTenantUID());
            Map<String, String> supplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getUid, Supplier::getName));
            for (Map<String, Object> dataRow : dataRows) {
                StringBuilder sb = new StringBuilder();
                ++rowNum;
                Supplier supplier = new Supplier();
                ExcelDocument.ValueWrapper uidWrapper = (ExcelDocument.ValueWrapper) dataRow.get("uid");
                Supplier temp = null;
                if (ObjectUtil.isNotEmpty(uidWrapper.value)) {
                    temp = updateMap.get(uidWrapper.value);
                    if (ObjectUtil.isNotEmpty(temp)) {
                        supplier = updateMap.get(uidWrapper.value);
                    }
                } else {
                    supplier.setCreatedDate(new Date());
                    supplier.setCreatedBy(currentUser.getLoginName());
                }
                if (ObjectUtil.isEmpty(temp)) {
                    supplier.setCreatedDate(new Date());
                    supplier.setCreatedBy(currentUser.getLoginName());
                }
                supplier.setExcelIndex(rowNum);
                for (Map.Entry<String, Object> dataEntry : dataRow.entrySet()) {
                    ExcelDocument.ValueWrapper valueWrapper = (ExcelDocument.ValueWrapper) dataEntry.getValue();
                    String value = valueWrapper.rawValue;
                    switch (dataEntry.getKey()) {
                        case "名称":
                            if (ObjectUtil.isNotEmpty(value)) {
                                if (ObjectUtil.isNotEmpty(supplier.getUid())) {
                                    supplierMap.remove(supplier.getUid());
                                }
                                if (supplierMap.containsValue(value)) {
                                    sb.append("名称重复！");
                                }
                                supplier.setName(value);
                            } else {
                                sb.append("名称为必填！");
                                supplier.setName(null);
                            }
                            break;
                        case "城市":
                            supplier.setCity(value);
                            break;
                        case "地址":
                            supplier.setAddress(value);
                            break;
                        case "邮编":
                            supplier.setZipcode(value);
                            break;
                        case "联系人姓名":
                            supplier.setContactor(value);
                            break;
                        case "联系方式":
                            supplier.setTel(value);
                            break;
                        case "类型":
                            if (ObjectUtil.isNotEmpty(value)) {
                                supplier.setSupplierT(value.contains("供应商"));
                                supplier.setMaintainerT(value.contains("维修商"));
                                supplier.setManufacturerT(value.contains("制造商"));
                            } else {
                                sb.append("类型为必填！");
                                supplier.setSupplierT(null);
                                supplier.setMaintainerT(null);
                                supplier.setManufacturerT(null);
                            }
                            break;
                        default:
                            break;
                    }
                }
                if (sb.length() > 0) {
                    supplier.setErrorInfo(sb.toString());
                    noFoundList.add(supplier);
                } else {
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
            }
            if (ObjectUtil.isEmpty(noFoundList)) {
                for (Supplier supplier : saveList) {
                    supplierService.saveSupplier(supplier);
                }
            } else {
                noFoundList.addAll(saveList);
                noFoundList = noFoundList.stream().sorted(Comparator.comparing(Supplier::getExcelIndex)).collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            Supplier supplier = new Supplier();
            supplier.setExcelIndex(rowNum);
            supplier.setErrorInfo(e.getMessage());
            noFoundList.add(supplier);
        }
        return noFoundList;
    }

    public void preViewed(String objectId){
        preViewerBean.onServerEvent("",objectId);
    }

    public boolean activeSupplier(){
        return activeSupplier;
    }

    public boolean activeVender(){
        return activeVender;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public List<Supplier> getNoFoundList() {
        return noFoundList;
    }

    public void setNoFoundList(List<Supplier> noFoundList) {
        this.noFoundList = noFoundList;
    }

    public SupplierContactPerson getSupplierContactPerson() {
        return supplierContactPerson;
    }

    public void setSupplierContactPerson(SupplierContactPerson supplierContactPerson) {
        this.supplierContactPerson = supplierContactPerson;
    }
}