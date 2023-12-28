package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.AdverseEventsRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.MessageUtil;
import com.ge.apm.service.utils.TempFileUtil;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class AdverseEventApproveController extends JpaCRUDController<AdverseEvents> {
    private Logger log = Logger.getLogger(AdverseEventApproveController.class.getName());
    /*接口类*/
    private OrgInfoRepository orgInfoRepository;
    private I18nMessageRepository i18nDao;
    private UserContextService userContextService;
    private UaaService uaaService;
    private CustomConfigService ccService;
    private WordReportExportService exportService;
    private AdverseEventsRepository adverseEventsRepository;
    private BlobFileService blobService;
    private AdverseEventServiceClient adverseEventServiceClient;
    private ConfigCacheService configCacheService;

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
    @Getter@Setter
    private List<TempFileUtil> injuryPerformance;
    @Getter@Setter
    private List<TempFileUtil> mechanicalFailure;

    private boolean readOnlyCreateForm = true;

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

    @Override
    protected void init() {
        /*接口类*/
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        exportService = WebUtil.getBean(WordReportExportService.class);
        adverseEventsRepository = WebUtil.getBean(AdverseEventsRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        adverseEventServiceClient = WebUtil.getBean(AdverseEventServiceClient.class);
        /*属性类*/
        selectSite = userContextService.getLoginUserOrgInfo();
        String selectedId = WebUtil.getRequestParameter("selectedId");
        selectAdverseSite = selectSite;
        this.selectedMap = new HashMap();
        injuryPerformance = new ArrayList<>();
        mechanicalFailure = new ArrayList<>();
        /*初始化方法*/
        loadAdverseEventById(selectedId);
    }

    public void loadAdverseEventById(String id) {
        if (StringUtils.hasText(id)) {
            selected = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), id);
            if (selected.getStatus() != null && (selected.getStatus() == 2 || selected.getStatus() == 3)) {
                readOnlyEditForm = false;
            } else {
                readOnlyEditForm = true;
            }
        }
    }

    @Override
    protected Page<AdverseEvents> loadData(PageRequest pageRequest) {
        if (StringUtils.isEmpty(eventStatusFilter)) {
            eventStatusFilter = "2,3,4,6,7,8";
        }
        Page<AdverseEvents> pageResult = adverseEventServiceClient.getAdverseEventsById(UserContextService.getAccessToken(), pageRequest, eventStatusFilter, queryStr, "approve");
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

    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }

    public void tempSaveAdverseEvent(Integer status) throws RuntimeException {
        int oldStatus = selected.getStatus();
        selected.setStatus(status);
        try {
            AdverseEvents result = adverseEventServiceClient.tempSaveAdverseEvents(UserContextService.getAccessToken(), selected);
            if (!ObjectUtils.isEmpty(result)) {
                selected = result;
                if (status == 2 || status == 3) {
                    //提交后页面设置为只读并隐藏设备查询列表
                    readOnlyEditForm = false;
                } else {
                    readOnlyEditForm = true;
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
}
