package com.ge.apm.view.asset;

import cn.hutool.core.util.ObjectUtil;
import com.ge.apm.dao.ExportRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.ExportRecord;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.ExportService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@Data
public class ExportRecordController extends JpaCRUDController<ExportRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportRecordController.class);

    private static final long serialVersionUID = -1L;

    private ExportService exportService;
    private ExportRepository dao;
    private FileDownloadService fileDownloadService;
    private UserAccountRepository userAccountRepository;

    private boolean downloadFlag = true;
    private String loginType;
    private String tenantUid;
    private String filePassword;
    private ExportRecord item;
    private UserAccount loginUser;
    private Map<Long, Boolean> selectedMap;
    private List<ExportRecord> currentPageRecord;

    public ExportRecord getItem() {
        return ObjectUtil.isEmpty(item) ? new ExportRecord() : item;
    }

    public void setItem(ExportRecord paramItem) {
        item = ObjectUtil.isEmpty(paramItem) ? new ExportRecord() : paramItem;
    }

    public void saveItem(String saveType) {
        Date now = TimeUtil.now();
        String loginName = loginUser.getLoginName();
        switch (saveType) {
            case "save":
                if (ObjectUtil.isEmpty(item.getId())) {
                    item.setTenantUid(tenantUid);
                    item.setStatus(ExportRecord.Status.NEW.name());
                    item.setCreateDate(now);
                } else {
                    if (ExportRecord.Status.AUDIT.name().equals(item.getStatus()) && ExportRecord.Status.RUN.name().equals(item.getStatus())) {
                        WebUtil.addErrorMessage("The current task is in progress. Modification is not allowed.");
                        return;
                    }
                }
                break;
            case "apply":
                item.setApplyBy(loginName);
                item.setApplyDate(now);
                item.setStatus(ExportRecord.Status.AUDIT.name());
                break;
            case "audit":
                item.setAuditBy(loginName);
                item.setAuditDate(now);

                List<ExportRecord> auditBatch = Collections.singletonList(item);
                if (isChecked() && ObjectUtil.isNull(item.getId())) {
                    // 批量审核
                    auditBatch = dao.findByIdIn(getSelectIds());
                    for (ExportRecord auditItem : auditBatch) {
                        auditItem.setUpdateDate(now);
                        auditItem.setAuditDate(now);
                        auditItem.setAuditBy(loginName);
                        auditItem.setAuditStatus(item.getAuditStatus());
                        auditItem.setAuditDescription(item.getAuditDescription());
                    }
                }

                saveAuditBatch(auditBatch);
                initSelectMap();
                return;
            default:
                break;
        }

        item.setUpdateDate(now);
        dao.save(item);
        initSelectMap();
    }

    public void saveAuditBatch(List<ExportRecord> itemBatch) {
        boolean writeFlag = ExportRecord.AuditStatus.PASS.name().equals(item.getAuditStatus());
        itemBatch.forEach(item -> item.setStatus(writeFlag ? ExportRecord.Status.RUN.name() : ExportRecord.Status.REFUSE.name()));
        dao.save(itemBatch);
        if (writeFlag) {
            String token = UserContextService.getAccessToken();
            Thread t = new Thread(() -> exportService.queryData(itemBatch, token, loginUser.getUid()));
            t.start();
        }
    }

    public void downloadDocument(Integer id) {
        try {
            V2_BlobObject blob = exportService.getBlob(id);
            StreamedContent file = fileDownloadService.getFile(blob.getObjectStorageId());
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
            StreamUtils.copy(file.getStream(), response.getOutputStream());
            facesContext.responseComplete();
            facesContext.renderResponse();
            downloadFlag = true;
        } catch (Exception e) {
            downloadFlag = false;
            LOGGER.error(e.getMessage(), e);
            WebUtil.addErrorMessage("Fail to download data.");
        }
    }

    public void setPassword(Long id) {
        ExportRecord exportRecord = dao.findByIdAndTenantUidAndStatus(id, tenantUid, ExportRecord.Status.FINISHED.name());
        filePassword = ObjectUtil.isNotNull(exportRecord) ? exportRecord.getPassword() : "";
    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = false;
        for (ExportRecord item : currentPageRecord) {
            if ("tenant".equals(loginType)) {
                if (!(ExportRecord.Status.RUN.name().equals(item.getStatus()) || ExportRecord.Status.AUDIT.name().equals(item.getStatus()))) {
                    isSelectedAll = !(!selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
                }
            } else {
                if (ExportRecord.Status.AUDIT.name().equals(item.getStatus())) {
                    isSelectedAll = !(!selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
                }
            }
        }
        for (ExportRecord item : currentPageRecord) {
            if ("tenant".equals(loginType)) {
                if (!(ExportRecord.Status.RUN.name().equals(item.getStatus()) || ExportRecord.Status.AUDIT.name().equals(item.getStatus()))) {
                    selectedMap.put(item.getId(), !isSelectedAll);
                }
            } else {
                if (ExportRecord.Status.AUDIT.name().equals(item.getStatus())) {
                    selectedMap.put(item.getId(), !isSelectedAll);
                }
            }
        }
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }

    public void deleteBatch() {
        List<Long> ids = getSelectIds();
        if (checkDelete(ids)) {
            dao.deleteByIds(ids);
        } else {
            WebUtil.addErrorMessage("There is currently a task in progress. Deletion is not allowed.");
        }
        initSelectMap();
    }

    public void deleteById(Long id) {
        if (checkDelete(Collections.singletonList(id))) {
            dao.delete(id);
        } else {
            WebUtil.addErrorMessage("The current task is in progress. Deletion is not allowed.");
        }
        initSelectMap();
    }

    public String downloadRedirect() {
        return downloadFlag ? "false" : "TenantRecord?faces-redirect=true";
    }

    public String redirect() {
        return "SystemRecord?faces-redirect=true";
    }

    private boolean checkDelete(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            ids = Collections.singletonList(-1L);
        }
        List<ExportRecord> recordList = dao.findByIdIn(ids);
        Map<Long, String> statusMap = recordList.stream().collect(Collectors.toMap(ExportRecord::getId, ExportRecord::getStatus));
        return !(statusMap.containsValue(ExportRecord.Status.AUDIT.name()) || statusMap.containsValue(ExportRecord.Status.RUN.name()));
    }

    public String getStatusColor(String status) {
        return ExportRecord.Status.getStatusValue(status);
    }

    public String getUserName(String loginName) {
        if (null == loginName) {
            return null;
        }

        UserAccount userAccount = userAccountRepository.getByLoginName(loginName);
        if (null == userAccount) {
            return null;
        }

        return userAccount.getName();
    }

    private List<Long> getSelectIds() {
        if (CollectionUtils.isEmpty(selectedMap.keySet())) {
            return Collections.singletonList(-1L);
        }
        List<Long> ids = new ArrayList<>();
        for (Long id : selectedMap.keySet()) {
            if (selectedMap.get(id)) {
                ids.add(id);
            }
        }
        return ids;
    }

    private void initSelectMap() {
        selectedMap = new HashMap<>(20);
    }

    @Override
    protected void init() {
        dao = WebUtil.getBean(ExportRepository.class);
        exportService = WebUtil.getServiceBean(ExportService.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        tenantUid = loginUser.getTenantUID();
        initSelectMap();
        item = new ExportRecord();
        this.filterBySite = false;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
    }

    @Override
    protected Page<ExportRecord> loadData(PageRequest pageRequest) {
        if (ObjectUtil.isEmpty(loginType)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
            loginType = request.getRequestURI().contains("tenant") ? "tenant" : "system";
        }

        if ("tenant".equals(loginType)) {
            if (!this.hasFilterOnField("tenantUid")) {
                searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, tenantUid));
            }
        } else {
            if (!this.hasFilterOnField("status")) {
                searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, ExportRecord.Status.NEW.name()));
            }
        }

        Page<ExportRecord> recordPage = dao.findBySearchFilter(searchFilters, pageRequest);
        currentPageRecord = recordPage.getContent();
        return recordPage;
    }

    @Override
    protected ExportRepository getDAO() {
        return dao;
    }

}
