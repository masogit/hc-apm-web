package com.ge.apm.view.asset;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.ZkbFileAttachmentService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 212605082 on 2019/12/2.
 */
@ManagedBean
@ViewScoped
public class ZkbFileAttachmentController extends GenericCRUDUUIDController<V2_BlobObject>{

    private BlobObjectRepository dao;
    private TenantInfoRepository tenantInfoDao;
    private ZkbFileAttachmentService zkbFileAttachmentService;

    private List<UploadedFile> attachements;
    private String actionName;
    private UserAccount currentUser;

    //复选的附件数据
    private Map<String, Boolean> selectedMap;
    private List<V2_BlobObject> currentPageBlobObject;

    private List<TenantInfo> tenantInfoList;
    private List<TenantInfo> selectedTenantInfoList;

    private Map<String, List<String>> zkbSecondDirectoryData;

    @Override
    protected void init() {

        dao = WebUtil.getBean(BlobObjectRepository.class);
        tenantInfoDao = WebUtil.getBean(TenantInfoRepository.class);
        zkbFileAttachmentService = WebUtil.getBean(ZkbFileAttachmentService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        this.selectedMap = new HashMap();
        this.attachements = new ArrayList<>();
        selectedTenantInfoList = new ArrayList<>();

        tenantInfoList = zkbFileAttachmentService.getSourceTenantInfoList();
        zkbSecondDirectoryData = zkbFileAttachmentService.initZkbSecondDirectory();
    }

    @Override
    protected BlobObjectRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<V2_BlobObject> loadData(PageRequest pageRequest) {

        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }

        searchFilters.add(new SearchFilter("boType", SearchFilter.Operator.EQ, V2_BlobObject.BO_TYPE_ZKB));

        Page<V2_BlobObject> blobObjectPage = dao.findBySearchFilter(searchFilters, pageRequest);

        currentPageBlobObject = blobObjectPage.getContent();

        return blobObjectPage;

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.actionName = "Create";

        selectedTenantInfoList = new ArrayList<>();
        attachements = new ArrayList<>();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.actionName = "Edit";
        selectedTenantInfoList = new ArrayList<>();

        String blobObjectID = null;
        for (Map.Entry<String, Boolean> tempData : selectedMap.entrySet()) {
            if(tempData.getValue()){
                blobObjectID = tempData.getKey();
            }
        }

        if(blobObjectID != null){
            this.selected = dao.findById(blobObjectID);
            this.selectedTenantInfoList = zkbFileAttachmentService.getTargetTenantInfo(this.selected);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile tempUploadedFile = event.getFile();
        attachements.add(tempUploadedFile);
    }

    public void removeAttachmentById(UploadedFile tempUploadedFile) {
        attachements = attachements.stream().filter(item -> !item.equals(tempUploadedFile)).collect(Collectors.toList());
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageBlobObject.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_BlobObject item : currentPageBlobObject) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
    }

    public void saveZkbFileAttachment(){

        if(this.actionName.equals("Create")){
            if(attachements.isEmpty()){
                WebUtil.addErrorMessage(WebUtil.getMessage("SelectUploadFile"));
                return;
            }
            zkbFileAttachmentService.saveZkbFileAttachment(this.selected, this.attachements, this.selectedTenantInfoList);
        }else if(this.actionName.equals("Edit")){
            zkbFileAttachmentService.updateZkbFileAttachment(this.selected, this.selectedTenantInfoList);
        }

        this.cancel();
    }

    public void deleteZkbFileAttachment() {

        selectedMap.forEach((key, value) -> {
            if (value) {
                zkbFileAttachmentService.deleteZkbFileAttachment(key);
            }
        });

    }

    public List<String> getZkbSecondDirectoryList(){
        if(this.selected != null){
            return this.zkbSecondDirectoryData.get(this.selected.getMainDirectory());
        }
        return new ArrayList<>();
    }

    public void cancel() {
        this.selected = null;
        this.selectedTenantInfoList = new ArrayList<>();
        this.attachements = new ArrayList<>();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public boolean isCheckedOne() {
        long count = selectedMap.values().stream().filter(item -> Boolean.TRUE.equals(item)).count();
        if(count == 1){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public boolean isZkbAdmin(){
        return zkbFileAttachmentService.isZkbAdmin(currentUser);
    }

    public List<UploadedFile> getAttachements() {
        return attachements;
    }

    public void setAttachements(List<UploadedFile> attachements) {
        this.attachements = attachements;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<TenantInfo> getTenantInfoList() {
        return tenantInfoList;
    }

    public void setTenantInfoList(List<TenantInfo> tenantInfoList) {
        this.tenantInfoList = tenantInfoList;
    }

    public List<TenantInfo> getSelectedTenantInfoList() {
        return selectedTenantInfoList;
    }

    public void setSelectedTenantInfoList(List<TenantInfo> selectedTenantInfoList) {
        this.selectedTenantInfoList = selectedTenantInfoList;
    }

}
