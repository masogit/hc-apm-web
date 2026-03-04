
package com.ge.apm.view.his;

import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.HisEquipmentDataSourceMapper;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.HisRequestParamsDto;
import com.ge.apm.domain.his.*;
import com.ge.apm.service.his.load.HisEquipmentDataSourceImportService;
import com.ge.apm.service.his.load.HisEquipmentImportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.view.asset.ContractImportController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisEquipmentDataSourceController extends GenericCRUDUUIDController<HisEquipmentDataSource> {
    private OrgInfoRepository orgInfoRepository;
    private HisEquipmentDataSourceRepository sourceDao;
    private HisAssetGroupAssetMatchRepository groupDao;
    private UserAccount selectedAssignee;
    private UaaService uaaService;
    private DualListModel<HisEquipmentSfxm> availableAssets;
    private List<HisEquipmentSfxm> sourceAssets;
    private List<HisEquipmentSfxm> targetAssets;
    private OrgInfo selectedOrg;//selected
    private UserAccount loginUser;
    private String treeNodeNameFilter;
    private TreeNode selectedNode;
    private TreeNode orgTree;
    private TreeNode enableOrgTree;

    private String name; //设备名称
    private String departNum;//设备编号

    private String sfxmId;  //过滤His收费代码
    private String sfxmName;//过滤His收费名称



    @Getter @Setter
    private List<OrgInfo> execDeptList;
    @Getter @Setter
    private String filterExecDept;
    private List<HisAssetGroupAssetMatch> currentPagePmOrders;

    List<HisAssetGroupAssetMatch> startList = new ArrayList<>();
    //复选的设备
    private Map<String, Boolean> selectedMap = new HashMap<>();
    List<String> pmIdList = new ArrayList<>(); //选中的

    @Getter
    @Setter
    private HisRequestParamsDto hisRequestParamsDto;

    private HisEquipmentDataSourceImportService hisEquipmentDataSourceImportService;
    private ExcelImportRecordRepository excelImportRecordRepository;
    private HisOrgComponentController orgComponentController;

    private HisEquipmentDataSourceMapper hisEquipmentDataSourceMapper;

    private List<ExcelImportRecord> excelImportRecords;

    private HisAdministrativeOfficesRepository hisAdministrativeOfficesRepository;

    @Getter @Setter
    private Set<String> execDeptOrgUidSet;
    @Getter @Setter
    private Long datalistSize;

    @Override
    protected void init() {
        sourceDao =  WebUtil.getBean(HisEquipmentDataSourceRepository.class);
        groupDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        selectedOrg = orgInfoRepository.getByUid(loginUser.getOrgInfoUID());
        rebuildOrgTree(loginUser.getTenantUID());
        hisRequestParamsDto = new HisRequestParamsDto();
        hisEquipmentDataSourceImportService = WebUtil.getBean(HisEquipmentDataSourceImportService.class);
        excelImportRecordRepository = WebUtil.getBean(ExcelImportRecordRepository.class);
        hisAdministrativeOfficesRepository = WebUtil.getBean(HisAdministrativeOfficesRepository.class);

        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        hisEquipmentDataSourceMapper = WebUtil.getBean(HisEquipmentDataSourceMapper.class);
        orgComponentController.registerController(this);
        initExcelImportRecords();
        hisRequestParamsDto.setEndDate(new Date());
        hisRequestParamsDto.setStartDate(DateUtil.addMonth(new Date(), -3));
        loadExecDeptList();
    }

    public void initExcelImportRecords() {

        UserAccount user = UserContextService.getCurrentUserAccount();
        excelImportRecords=excelImportRecordRepository.findByModuleAndTenantUid("HisEquipmentDataSource",user.getTenantUID());



    }


    public void rebuildOrgTree(String tenantUid) {
        orgTree = uaaService.buildOrgTree(tenantUid, "", -1, -1);
        enableOrgTree = uaaService.buildEnableOrgTree(loginUser.getUid(), loginUser.getOrgInfoUID(), selectedOrg);
    }

    private void loadExecDeptList() {
        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();

        List<HisAdministrativeOffices> lists = hisAdministrativeOfficesRepository.findByTenantUid(loginUser.getTenantUID());
        execDeptList = new ArrayList<>();
        for (HisAdministrativeOffices list : lists) {
            OrgInfo orgInfo = new OrgInfo();
            orgInfo.setName(list.getHisKsname());
            orgInfo.setUid(list.getHisKsid());
            orgInfo.setSiteUID(list.getSiteUid());
            execDeptList.add(orgInfo);
        }
        if (null != selectedOrg) {
            execDeptList = execDeptList.stream().filter(item -> item.getSiteUID().equals(selectedOrg.getSiteUID())).collect(Collectors.toList());
        }
    }

    public List<String> autoCompleteDeptName(String query) {
        if (execDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.execDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }


    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterExecDept = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterExecDept) && !execDeptList.stream().anyMatch(item -> filterExecDept.equals(item.getName()))) {
            filterExecDept = null;
        }
        if (filterExecDept != null) {
            List<OrgInfo> orgInfosMatchingByName = this.execDeptList.stream().filter(item -> item.getName().equals(filterExecDept)).collect(Collectors.toList());
            execDeptOrgUidSet=orgInfosMatchingByName.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            execDeptOrgUidSet = Collections.emptySet();
        }
    }

    @Override
    protected Page<HisEquipmentDataSource> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        removeFilterOnField("orgCode");
        removeFilterOnField("siteUid");
        removeFilterOnField("tenantUid");
        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        if (selectedOrg != null) {
            searchFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        }
//        if (hisRequestParamsDto.getStartDate() != null) {
//            searchFilters.add(new SearchFilter("execTime", SearchFilter.Operator.GTE, hisRequestParamsDto.getStartDate()));
//        }
//        if (hisRequestParamsDto.getEndDate() != null) {
//            searchFilters.add(new SearchFilter("execTime", SearchFilter.Operator.LT, DateUtil.getDateAddOneDay(hisRequestParamsDto.getEndDate())));
//        }
//        return sourceDao.findBySearchFilter(searchFilters,pageRequest);
        Map<String, Object> param = new HashMap<String, Object>();
        covQueryCondition(searchFilters, param);
        param.put("page", pageRequest.getPageSize());
        param.put("offset", pageRequest.getPageNumber() * pageRequest.getPageSize());
        if (hisRequestParamsDto.getStartDate() != null) {
            param.put("startDate", hisRequestParamsDto.getStartDate());
        }
        if (hisRequestParamsDto.getEndDate() != null) {
            param.put("endDate",DateUtil.getDateAddOneDay(hisRequestParamsDto.getEndDate()));
        }
        if(CollectionUtils.isNotEmpty(execDeptOrgUidSet)){
            param.put("execDept", execDeptOrgUidSet);
        }

        Sort sort = pageRequest.getSort();
        StringBuilder orderBy = new StringBuilder();
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            boolean ascending = order.isAscending();
            switch (property){
                case "id":
                    orderBy.append("source."+("id")).append(ascending ? " ASC" : " DESC").append(",");
                    break;
                case "totalAmount":
                    orderBy.append("source."+("total_amount")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "execTime":
                    orderBy.append("source."+("exec_time")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "receiptNum":
                    orderBy.append("source."+("receipt_num")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "deviceCode":
                    orderBy.append("source."+("device_code")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "deviceDeptCode":
                    orderBy.append("source."+("device_dept_code")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                case "execDeptCode":
                    orderBy.append("source."+("exec_dept_code")).append(ascending ? " ASC" : " DESC").append(" nulls last").append(",");
                    break;
                default:
                    orderBy.append("source."+("id")).append(ascending ? " ASC" : " DESC").append(",");
                    break;
            }
        }
        if(orderBy.length()>0){
            param.put("orderBy", orderBy.substring(0, orderBy.length() - 1));

        }



        List<HisEquipmentDataSource> hisEquipmentDataSourceList = hisEquipmentDataSourceMapper.queryList(param);
        datalistSize = hisEquipmentDataSourceMapper.selectListCount(param);

        return  new PageImpl<>(hisEquipmentDataSourceList, pageRequest, datalistSize);

    }

    public void covQueryCondition(List<SearchFilter> searchFilters, Map<String, Object> param) {
        for (SearchFilter searchFilter :searchFilters) {
            String fieldName = searchFilter.fieldName;
            switch (fieldName) {
                case "orgCode":
                    param.put("orgCode", searchFilter.value);
                    break;
                case "siteUid":
                    param.put("siteUid", searchFilter.value);
                    break;
                case "tenantUid":
                    param.put("tenantUid", searchFilter.value);
                    break;
                case "deviceCode":
                    param.put("deviceCode", searchFilter.value);
                    break;
                case "deviceDeptCode":
                    param.put("deviceDeptCode", searchFilter.value);
                    break;
                case "execDeptCode":
                    param.put("execDeptCode", searchFilter.value);
                    break;
                case "sourceCode":
                    param.put("sourceCode", searchFilter.value);
                    break;
                case "isMatched":
                    param.put("isMatched", searchFilter.value);
                    break;
                case "sfxmName":
                    param.put("sfxmName", searchFilter.value);
                    break;

                case "sfxmLb":
                    param.put("sfxmLb", searchFilter.value);
                    break;
                case "patientRegistrationNum":
                    param.put("patientRegistrationNum", searchFilter.value);
                    break;

                case "prescriptionNum":
                    param.put("prescriptionNum", searchFilter.value);
                    break;
                case "execTime":
                    param.put("execTime", searchFilter.value);
                    break;
                case "receiptNum":
                    double receiptNum = Double.parseDouble(searchFilter.value.toString());
                    param.put("receiptNum", receiptNum);
                    break;
                case "totalAmount":
                    double totalAmount = Double.parseDouble(searchFilter.value.toString());
                    param.put("totalAmount", totalAmount);
                    break;
                default:
                    break;
            }
        }
    }





    @Override
    public List<HisEquipmentDataSource> getItemList() {
        return sourceDao.find();
    }

    public void onSelectCurrent(String id) {
        if(selectedMap !=null && selectedMap.size() >0 ) {
            selectedMap.remove(id);
            startList = new ArrayList<>();
            pmIdList = new ArrayList<>();
            selectedMap.forEach((k, v) -> {
                if (Boolean.TRUE.equals(v))
                    pmIdList.add(k);
            });
            if(!pmIdList.isEmpty()) {
                List<HisAssetGroupAssetMatch> results = groupDao.findByIds(pmIdList);
                startList.addAll(results);
            }
        }
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        selected = null;
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
    }

    public String workOrderRouter() {
        String retrunUrl = "";
        RequestContext requestContext = RequestContext.getCurrentInstance();
        Boolean willShowNotification = Boolean.FALSE;
        requestContext.addCallbackParam("showNotification", willShowNotification);
        if (true) {
            requestContext.addCallbackParam("url", UrlEncryptController.encodeUrlParam("transferAsset?actionName=Create"));
            retrunUrl = UrlEncryptController.encodeUrlParam("transferAsset?actionName=Create");
        }

        return retrunUrl;
    }

    public void filterTreeNode() {
        uaaService.filterTreeNode(orgTree, treeNodeNameFilter, -1, -1);
    }

    public List<OrgInfo> getOrgDeptList(String siteUid) {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<OrgInfo> orgInfos = uaaService.getOrgDeptList(siteUid, user.getUid());
        orgInfos.sort(TreeNodeUtil.orgInfoComparator);
        return orgInfos;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void pick(){
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (Boolean.TRUE.equals(v))
                pmIdList.add(k);

        });
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getUuid()) || !selectedMap.get(item.getUuid()));
        for (HisAssetGroupAssetMatch item : currentPagePmOrders) {
            selectedMap.put(item.getUuid(), !isSelectedAll);
        }
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (Boolean.TRUE.equals(v))
                pmIdList.add(k);

        });
    }

    //选中确认
    public void confirm(){
        if(CollectionUtils.isNotEmpty(pmIdList)) {
            startList = new ArrayList<>();
            List<HisAssetGroupAssetMatch> results = groupDao.findByIds(pmIdList);
            startList.addAll(results);
        }
    }

    private UploadedFile file;

    private List<Map<String, Object>> importMapList;
    private List<Map<String, Object>> errorList;
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;

        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            //检查导入模板版本
//            if(!checkDocVersion(doc)){
//                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--"+WebUtil.getMessage("TemplateVersionIncorrect"));
//                return;
//            }

            int maxRowNum = HisEquipmentDataSourceImportService.EXCEL_CONTRACT_HEADER_ROW + 30000;
            if(doc.exceedMaxRowNum(HisEquipmentDataSourceImportService.SHEET_SERVICE_CONTRACT, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            importMapList = hisEquipmentDataSourceImportService.getMapFromTemplate(doc);
            hisEquipmentDataSourceImportService.importData(importMapList);
            errorList = hisEquipmentDataSourceImportService.filterErrorRecords(importMapList);
            hisEquipmentDataSourceImportService.saveDBAndToEs(importMapList);
            if(errorList.size()<importMapList.size()){
                ExcelImportRecord excelImportRecord = new ExcelImportRecord();
                UserAccount user = UserContextService.getCurrentUserAccount();
                excelImportRecord.setImportName(user.getLoginName());
                excelImportRecord.setCreatedDate(new Date());
                excelImportRecord.setFileName(file.getFileName());
                excelImportRecord.setModule("HisEquipmentDataSource");
                excelImportRecord.setTenantUid(user.getTenantUID());
                excelImportRecordRepository.save(excelImportRecord);
            }

            if(!errorList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }

    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }







    public void cancel() {
        this.selected = null;
    }

    private String filterState = null;

    public String getFilterState() {
        return filterState;
    }
    public void setFilterState(String filterState) {
        this.filterState = filterState;
    }

    public UserAccount getSelectedAssignee() {
        return this.selectedAssignee;
    }

    public void setSelectedAssignee(UserAccount ua) {
        this.selectedAssignee = ua;
    }

    public DualListModel<HisEquipmentSfxm> getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<HisEquipmentSfxm> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public List<HisEquipmentSfxm> getSourceAssets() {
        return sourceAssets;
    }

    public void setSourceAssets(List<HisEquipmentSfxm> sourceAssets) {
        this.sourceAssets = sourceAssets;
    }

    public List<HisEquipmentSfxm> getTargetAssets() {
        return targetAssets;
    }

    public void setTargetAssets(List<HisEquipmentSfxm> targetAssets) {
        this.targetAssets = targetAssets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartNum() {
        return departNum;
    }

    public void setDepartNum(String departNum) {
        this.departNum = departNum;
    }

    public String getTreeNodeNameFilter() {
        return treeNodeNameFilter;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public UserAccount getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(UserAccount loginUser) {
        this.loginUser = loginUser;
    }

    public void setTreeNodeNameFilter(String treeNodeNameFilter) {
        this.treeNodeNameFilter = treeNodeNameFilter;
    }

    public TreeNode getEnableOrgTree() {
        return enableOrgTree;
    }

    public void setEnableOrgTree(TreeNode enableOrgTree) {
        this.enableOrgTree = enableOrgTree;
    }

    public String getSfxmName() {
        return sfxmName;
    }

    public void setSfxmName(String sfxmName) {
        this.sfxmName = sfxmName;
    }

    public String getSfxmId() {
        return sfxmId;
    }

    public void setSfxmId(String sfxmId) {
        this.sfxmId = sfxmId;
    }
    public void prepareCreateSart() {
        crudActionName = "Create";
    }

    @Override
    protected HisEquipmentDataSourceRepository getDAO() {
        return sourceDao;
    }

    public void prepareEditSart() {
        crudActionName = "Edit";
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }


    public List<ExcelImportRecord> getExcelImportRecords() {
        return excelImportRecords;
    }

    public void setExcelImportRecords(List<ExcelImportRecord> excelImportRecords) {
        this.excelImportRecords = excelImportRecords;
    }

    public Date getMaxDate() {
        if(hisRequestParamsDto.getStartDate()!=null){
            return DateUtil.addMonth(hisRequestParamsDto.getStartDate(),12);
        }
        return null;
    }

    public void getEndDateChange() {
        if(hisRequestParamsDto.getStartDate()!=null){
            if(hisRequestParamsDto.getEndDate()!=null &&  !hisRequestParamsDto.getEndDate().before(DateUtil.addMonth(hisRequestParamsDto.getStartDate(),12))){
                hisRequestParamsDto.setEndDate(DateUtil.addMonth(hisRequestParamsDto.getStartDate(),12));
            }

        }


    }

    public String tenantMaxExecDate(){

        String tenantUID = loginUser.getTenantUID();
         //    CREATE INDEX CONCURRENTLY idx_ds_tenant_time_all ON his_equipment_data_source (tenant_uid, exec_time DESC);
        Date  execDate=sourceDao.getTenantMaxExecDate(tenantUID);
        if (execDate!=null){
            String format = "yyyy-MM-dd";
            String dateStr = DateUtil.getFormatStrByDate(execDate, format);
            return dateStr;
        }
        return null;

    }
}


