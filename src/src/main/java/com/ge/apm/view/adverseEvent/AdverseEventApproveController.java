package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.AdverseEventsRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.MultiLevelApprovalRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.adverseEvent.AdverseEventsService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.utils.TempFileUtil;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AdverseEventApproveController extends AdverseEventBaseController{
    private Logger log = Logger.getLogger(AdverseEventApproveController.class.getName());
    /*接口类*/
    private AdverseEventsRepository adverseEventsRepository;
    private UserContextService userContextService;
    private AdverseEventServiceClient adverseEventServiceClient;
    private MultiLevelApprovalRepository multiLevelApprovalRepository;
    private ConfigCacheService configCacheService;
    private AdverseEventsService adverseEventsService;
    /*属性类*/
    @Getter@Setter
    private OrgInfo selectSite; //选定的所属院区
    @Getter@Setter
    protected AdverseEvents selected;
    @Getter@Setter
    private Map<String, Boolean> selectedMap;
    @Getter@Setter
    private Sort lastPageSort; //export use
    @Getter@Setter
    private List<AdverseEvents> currentPageAdverse;
    @Getter@Setter
    private OrgInfo selectAdverseSite; //选定的所属院区

    private boolean readOnlyCreateForm = false;

    private boolean readOnlyEditForm = false;
    @Getter@Setter
    private String eventStatusFilter = "";
    @Getter@Setter
    private String queryStr = "";
    @Getter@Setter
    private String emails;
    @Getter@Setter
    private Integer assetType = 1;

    private boolean canSendMail;
    @Getter@Setter
    private StreamedContent exportPdf;
    @Getter@Setter
    private StreamedContent excelFile;

    @Getter@Setter
    private String[] selectedOptions;

    @Setter
    @Getter
    private List<MultiLevelApproval> multiLevelApprovals;

    @Setter @Getter
    private String filterDeptOrg;

    private List<OrgInfo> clinicalDeptList;

    private Set<String> filterClinicalDeptOrg;

    private OrgInfoTreeService orgInfoTreeService;

    private UaaService uaaService;

    @Override
    protected void init() {
        /*接口类*/
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        adverseEventServiceClient = WebUtil.getBean(AdverseEventServiceClient.class);
        adverseEventsService = WebUtil.getBean(AdverseEventsService.class);
        multiLevelApprovalRepository = WebUtil.getBean(MultiLevelApprovalRepository.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        /*属性类*/
        selectSite = userContextService.getLoginUserOrgInfo();
        String selectedId = WebUtil.getRequestParameter("selectedId");
        selectAdverseSite = selectSite;
        this.selectedMap = new HashMap();
        injuryPerformance = new ArrayList<>();
        mechanicalFailure = new ArrayList<>();
        /*初始化方法*/
        loadAdverseEventById(selectedId);
        loadClinicalDeptList();
    }

    public void loadAdverseEventById(String id) {
        if (StringUtils.hasText(id)) {
            selected = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), id);
            if (selected.getStatus() != null && (selected.getStatus() == 2 || selected.getStatus() == 3)) {
                readOnlyEditForm = false;
                readOnlyCreateForm = false;
            } else {
                readOnlyEditForm = true;
                readOnlyCreateForm = true;
            }
            //设备类型赋值
            assetType = selected.getDevicesClass() == null ? 1 : selected.getDevicesClass();
            //事件原因分析由字符串转为数组
            if(StrUtil.isNotEmpty(selected.getEventCauseAnalyze())){
                selectedOptions = selected.getEventCauseAnalyze().split(",");
            }
            multiLevelApprovals = multiLevelApprovalRepository.findByBusinessUid(selected.getUid());
        }
    }

    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }

    @Override
    protected Page<AdverseEvents> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (!CollectionUtils.isEmpty(filterClinicalDeptOrg)) {
            searchFilters.add(new SearchFilter("department",SearchFilter.Operator.EQ, filterClinicalDeptOrg.iterator().next()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for(SearchFilter searchFilter : searchFilters){
            Map<String, Object> mm = new HashMap<>();
            mm.put("fieldName", searchFilter.fieldName);
            if("reportDate".equals(searchFilter.fieldName)){
                mm.put("valueDate", searchFilter.value);
            }else{
                mm.put("valueString", searchFilter.value);
            }
            mm.put("operator", searchFilter.operator.name());
            list.add(mm);
        }
        if (StringUtils.isEmpty(eventStatusFilter)) {
            eventStatusFilter = "2,3,4,6,7,8";
        }
        Page<AdverseEvents> pageResult = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), pageRequest, eventStatusFilter, queryStr, "approve", list);
        currentPageAdverse = pageResult.getContent();
        return pageResult;
    }

    public String reporterList(String uid) {
        return Optional.ofNullable(configCacheService.findOrgByUid(uid)).orElse(new OrgInfo()).getName();
    }

    public void setEventStatus(String status) {
        selectedMap.clear();
        eventStatusFilter = status;
    }

    public void setQueryStr(String queryStr) {
        selectedMap.clear();
        this.queryStr = queryStr;
    }

    public void onAdverseSelectSiteTreeNode(NodeSelectEvent event) {
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectAdverseSite = (OrgInfo) event.getTreeNode().getData();
        }
        selected.setDepartment(selectAdverseSite.getUid());
    }

    public void onSelectAdverseEvent(Integer id) throws IOException {
        String url = "selectedId=" + id;
        url = "adverseEventApproveEdit.xhtml?" + url;
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAdverse.stream().anyMatch(item -> !selectedMap.containsKey(item.getWorkOrderNum()) || !selectedMap.get(item.getWorkOrderNum()));
        for (AdverseEvents item : currentPageAdverse) {
            selectedMap.put(item.getWorkOrderNum(), !isSelectedAll);
        }
    }

    public boolean getCanSendMail() {
        this.canSendMail = !selectedMap.values().stream().anyMatch(Boolean::booleanValue);
        return canSendMail;
    }


    public void tempSaveAdverseEventApprove(Integer status) throws RuntimeException {
        int oldStatus = selected.getStatus();
        selected.setStatus(status);
        processFile(selected);
        try {
            AdverseEvents result = adverseEventServiceClient.tempSaveAdverseEvents(UserContextService.getAccessToken(), selected);
            if (!ObjectUtils.isEmpty(result)) {
                selected = result;
                if (status == 2 || status == 3) {
                    //提交后页面设置为只读并隐藏设备查询列表
                    readOnlyEditForm = false;
                    readOnlyCreateForm = false;
                } else {
                    readOnlyEditForm = true;
                    readOnlyCreateForm = true;
                }
                injuryPerformance.clear();
                mechanicalFailure.clear();
                if(CollectionUtils.isEmpty(selected.getHarmPerformFile())){
                    selected.setHarmPerformFile(new ArrayList<>());
                }
                if(CollectionUtils.isEmpty(selected.getDeviceFaultPerformFile())){
                    selected.setDeviceFaultPerformFile(new ArrayList<>());
                }
                MessageUtil.outPutReturnFacesMessage("保存成功", FacesMessage.SEVERITY_INFO);
            }
        } catch (Exception e) {
            selected.setStatus(oldStatus);
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void sendReportMail() {
        if (CollectionUtils.isEmpty(selectedMap) || !StringUtils.hasText(emails)) {
            MessageUtil.outPutReturnFacesMessage("请勾选需要上报的工单", FacesMessage.SEVERITY_INFO);
            return;
        }
        try {
            selectedMap = selectedMap.entrySet().stream()
                    .filter(entry -> entry.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            adverseEventServiceClient.sendReportMail(UserContextService.getAccessToken(), emails.split(","), selectedMap.keySet().toArray(new String[selectedMap.size()]));
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
        selectedMap.clear();
        MessageUtil.outPutReturnFacesMessage("发送成功！", FacesMessage.SEVERITY_INFO);
    }

    public void sendSingleReportMail() {
        if (ObjectUtils.isEmpty(this.selected) || !StringUtils.hasText(emails)) {
            MessageUtil.outPutReturnFacesMessage("无效工单或邮箱！", FacesMessage.SEVERITY_INFO);
            return;
        }
        adverseEventServiceClient.sendReportMail(UserContextService.getAccessToken(), emails.split(","), new String[]{this.selected.getWorkOrderNum()});
        selectedMap.clear();
        MessageUtil.outPutReturnFacesMessage("发送成功！", FacesMessage.SEVERITY_INFO);
    }

    public void downLoadPdf() {
        if (CollectionUtils.isEmpty(selectedMap)) {
            MessageUtil.outPutReturnFacesMessage("请勾选需要的工单", FacesMessage.SEVERITY_INFO);
            return;
        }
        try {
            selectedMap = selectedMap.entrySet().stream()
                    .filter(entry -> entry.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            TempFileUtil tempFileUtil = adverseEventServiceClient.downLoadPdf(UserContextService.getAccessToken(), selectedMap.keySet().toArray(new String[selectedMap.size()]));
            exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(tempFileUtil.getContents())), tempFileUtil.getContentType(), tempFileUtil.getFileName());
        } catch (Exception e) {
            MessageUtil.outPutReturnFacesMessage(e.getMessage(), FacesMessage.SEVERITY_INFO);
        }
    }

    public void exportExcel(){
        if (StringUtils.isEmpty(eventStatusFilter)) {
            eventStatusFilter = "2,3,4,6,7,8";
        }
        List<SearchFilter> searchFilters = this.searchFilters;
        if (!CollectionUtils.isEmpty(filterClinicalDeptOrg)) {
            searchFilters.add(new SearchFilter("department",SearchFilter.Operator.EQ, filterClinicalDeptOrg.iterator().next()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for(SearchFilter searchFilter : searchFilters){
            Map<String, Object> mm = new HashMap<>();
            mm.put("fieldName", searchFilter.fieldName);
            if("reportDate".equals(searchFilter.fieldName)){
                mm.put("valueDate", searchFilter.value);
            }else{
                mm.put("valueString", searchFilter.value);
            }
            mm.put("operator", searchFilter.operator.name());
            list.add(mm);
        }
        List<AdverseEvents> adverseEventsList = adverseEventServiceClient.queryAdverseEvents(UserContextService.getAccessToken(), eventStatusFilter, queryStr, "approve", list);
        for(AdverseEvents adverseEvents : adverseEventsList){
            MultiLevelApproval recentApprove = multiLevelApprovalRepository.getRecentApprove(adverseEvents.getUid());
            if (recentApprove != null) {
                adverseEvents.setActualApproverName(recentApprove.getActualApproverName());
                adverseEvents.setActualApproverDate(recentApprove.getActualApproverDate());
                adverseEvents.setApprovalOpinion(recentApprove.getApprovalOpinion());
            }
        }
        InputStream inputStream = adverseEventsService.exportExcel(adverseEventsList);
        excelFile = new DefaultStreamedContent(inputStream, "application/vnd.ms-excel", "AdverseEvents_Export.xlsx");
    }

    public List<String> autoCompleteDeptName(String query) {
        if (CollectionUtils.isEmpty(clinicalDeptList)) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onDeptNameFilterChange(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            filterDeptOrg = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterDeptOrg.equals(item.getName()))) {
            filterDeptOrg = null;
        }
        if (filterDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterDeptOrg)).collect(Collectors.toList());
            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            filterClinicalDeptOrg = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            filterClinicalDeptOrg = Collections.emptySet();
        }
    }

    public TreeNode getDeptOrgTree() {
        return uaaService.getOrgAccessTreeByUser(UserContextService.getCurrentUserAccount());
    }

    public void removeTempHarmPerform(TempFileUtil file) {
        injuryPerformance.remove(file);
    }

    public void removeTempDeviceFaultPerform(TempFileUtil file) {
        mechanicalFailure.remove(file);
    }

    public void removeHarmPerformFile(AdverseEventsFile file) {
        selected.getHarmPerformFile().remove(file);
    }

    public void removeDeviceFaultPerformFile(AdverseEventsFile file) {
        selected.getDeviceFaultPerformFile().remove(file);
    }

    public boolean getReadOnlyCreateForm() {
        return this.readOnlyCreateForm;
    }

    public boolean getReadOnlyEditForm() {
        return this.readOnlyEditForm;
    }

    public void preReturn() {
        this.selected.setReturnReason(null);
    }

    public boolean harmDegreeLever(Integer harmDegree) {
        return harmDegree != null && harmDegree == 1;
    }

    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }
}
