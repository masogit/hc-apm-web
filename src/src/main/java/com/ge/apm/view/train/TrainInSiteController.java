package com.ge.apm.view.train;

import com.ge.apm.dao.TrainInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.OrgDto;
import com.ge.apm.domain.dto.TrainRoleDto;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.train.TrainManageService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class TrainInSiteController extends GenericCRUDUUIDController<TrainInfo> {

    private TrainInfoRepository dao;

    private UserAccount loginUser;

    private UaaService uaaService;
    private UserContextService userContextService;
    private TrainManageService trainManageService;

    @Getter @Setter
    private StreamedContent exportFile;

    @Getter @Setter
    private List<TrainDetail> trainDetailList = new ArrayList<>();

    @Getter@Setter
    private List<TrainDetailSign> trainDetailSignList = new ArrayList<>();

    @Getter @Setter
    private TrainDetail selectedTrainDetail;

    @Getter @Setter
    private TrainDetailSign selectedTrainDetailSign;

    @Getter @Setter
    private String actionName = "";

    @Getter @Setter
    private String detailActionName = "";
    @Setter @Getter
    List<String> selectedOrgUid;
    @Getter @Setter
    List<OrgDto> orgsForSelected = new ArrayList<>(); //待选科室
    @Getter @Setter
    List<String> selectedRoleId;
    @Getter @Setter
    List<TrainRoleDto> rolesForSelected = new ArrayList<>();

    @Getter@Setter
    List<V2_BlobObject> blobObjectList = new ArrayList<>();

    @Getter @Setter
    private boolean canEdit = true;
    @Getter@Setter
    private String filterClinicalDeptOrg; //选定的所属科室
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;
    @Getter @Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter @Setter
    private String filterSigner;
    @Getter @Setter
    private List<UserAccount> clinicalOwnerList;
    @Override
    protected void init() {
        super.init();
        dao = WebUtil.getBean(TrainInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        trainManageService = WebUtil.getBean(TrainManageService.class);
        selectSite = userContextService.getLoginUserOrgInfo();
    }

    @Override
    protected Page<TrainInfo> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        Page<TrainInfo> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        return pageResult;
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    public void prepareCreate(){
        selected = new TrainInfo();
        selected.setCreatedDate(new Date());
        actionName = "Create";
        orgsForSelected = trainManageService.onCreateInventory();
        rolesForSelected = trainManageService.queryTrainRole(loginUser);
        canEdit = true;
    }

    public void prepareEdit(){
        actionName = "Edit";
        if(CollectionUtils.isEmpty(orgsForSelected)){
            orgsForSelected = trainManageService.onCreateInventory();
        }
        if(StrUtil.isNotEmpty(selected.getTrainingDeptUid())){
            selectedOrgUid = Arrays.asList(selected.getTrainingDeptUid().split(","));
        }
        if(CollectionUtils.isEmpty(rolesForSelected)){
            rolesForSelected = trainManageService.queryTrainRole(loginUser);
        }
        if(StrUtil.isNotEmpty(selected.getTrainingRoleUid())){
            selectedRoleId = Arrays.asList(selected.getTrainingRoleUid().split(","));
        }
        trainDetailList = trainManageService.queryDetailByTrainId(selected.getId());
        if(!selected.getSiteUID().equals(loginUser.getSiteUID())){
            canEdit = false;
        }
    }

    public void saveData(){
        if(!trainManageService.saveCheck(selected, trainDetailList)){
            return;
        }
        trainManageService.save(selected, loginUser, selectedOrgUid, selectedRoleId);
    }

    public void prepareItemCreate(){
        detailActionName = "Create";
        selectedTrainDetail = new TrainDetail();
        selectedTrainDetail.setTrainId(selected.getId());
        //selectedTrainDetail.setDataStatus(TrainDetail.DataStatus.Create.name());
        blobObjectList = new ArrayList<>();
        //selectedTrainDetail.setCreatedDate(new Date());
    }

    public void prepareItemEdit(){
        detailActionName = "Edit";
        /*if(StrUtil.isEmpty(selectedTrainDetail.getDataStatus())) {
            selectedTrainDetail.setDataStatus(TrainDetail.DataStatus.Original.name());
        }*/
        blobObjectList = trainManageService.queryBlobByDetailId(selectedTrainDetail.getId());
        trainDetailSignList = trainManageService.queryDetailSignByDetailId(selectedTrainDetail.getId());
    }

    public void deleteItem(){
        if(selectedTrainDetail != null){
            for(TrainDetail t : trainDetailList){
                if(t.getId().equals(selectedTrainDetail.getId())){
                    trainDetailList.remove(t);
                    selectedTrainDetail = null;
                    break;
                }
            }
        }
    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2BlobObject = trainManageService.handleFileUpload(event);
        if(v2BlobObject != null){
            blobObjectList.add(v2BlobObject);
        }
    }
    public void removeFile(V2_BlobObject v2BlobObject){
        trainManageService.removeBlob(v2BlobObject);
    }

    public void saveDetail(){
        trainDetailList = trainManageService.saveDetail(selectedTrainDetail, detailActionName, blobObjectList, selected);

       /* if("Create".equals(detailActionName)){
            selectedTrainDetail.setId(UUID.randomUUID().toString());//临时插入id,方便列表操作
            selectedTrainDetail.setBlobObjectList(blobObjectList);
            trainDetailList.add(selectedTrainDetail);
        }else{
            selectedTrainDetail.setLastModifiedDate(new Date());
            selectedTrainDetail.setBlobObjectList(blobObjectList);
            for(TrainDetail t : trainDetailList){
                if(t.getId().equals(selectedTrainDetail.getId())){
                    trainDetailList.remove(t);
                    trainDetailList.add(selectedTrainDetail);
                    break;
                }
            }
        }*/
        selectedTrainDetail = new TrainDetail();

    }

    public void completeDetail(){
        trainManageService.completeDetail(selectedTrainDetail, blobObjectList, selected,loginUser);
    }

    public void cancelDetail(){
        trainManageService.cancelDetail(selectedTrainDetail, selected,loginUser);
    }

    public void exportAll(){

    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }else{
            OrgInfo orgInfo = clinicalDeptList.stream().filter(item -> filterClinicalDeptOrg.equals(item.getName())).findFirst().get();
            selectedTrainDetailSign.setSignDeptName(orgInfo.getName());
            selectedTrainDetailSign.setSignDeptUid(orgInfo.getUid());
        }
        if(StrUtil.isNotEmpty(filterClinicalDeptOrg)){
            getClinicalOwnerList();
        }
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public List<UserAccount> completeClinicalOwnerList(String query) {
        if (clinicalOwnerList == null) {
            return Collections.emptyList();
        }

        List<UserAccount> res = this.clinicalOwnerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public void getClinicalOwnerList() {
        if (StrUtil.isNotEmpty(selectedTrainDetailSign.getSignDeptUid())) {
            clinicalOwnerList = uaaService.getUsersByDeptUid(selectedTrainDetailSign.getSignDeptUid());
        } else {
            clinicalOwnerList = Collections.emptyList();
        }
    }
    public void onSignerFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterSigner = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterSigner) && !clinicalOwnerList.stream().anyMatch(item -> filterSigner.equals(item.getName()))) {
            filterSigner = null;
        }else{
            UserAccount us = clinicalOwnerList.stream().filter(item -> filterSigner.equals(item.getName())).findFirst().get();
            selectedTrainDetailSign.setSignerUid(us.getUid());
            selectedTrainDetailSign.setSignerName(us.getName());
        }
    }

    public void prepareSignCreate(){
        loadClinicalDeptList();
        selectedTrainDetailSign = new TrainDetailSign();
        selectedTrainDetailSign.setDetailId(selectedTrainDetail.getId());
    }

    public void prepareSignEdit(){
        loadClinicalDeptList();
        filterClinicalDeptOrg = selectedTrainDetailSign.getSignDeptUid();
    }

    public void deleteSign(){

    }

    public void saveSign(){
        trainManageService.saveSign(selectedTrainDetailSign);
        trainDetailSignList = trainManageService.queryDetailSignByDetailId(selectedTrainDetailSign.getDetailId());
        selectedTrainDetailSign = new TrainDetailSign();
    }
    @Override
    protected GenericRepositoryUUID<TrainInfo> getDAO() {
        return dao;
    }
}
