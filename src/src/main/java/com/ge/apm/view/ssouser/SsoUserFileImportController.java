package com.ge.apm.view.ssouser;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.service.ssouser.SsoUserFileImportStreamingService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class SsoUserFileImportController {
    private static final Logger logger = LoggerFactory.getLogger(SsoUserFileImportController.class);

    private UploadedFile file;

    private List<Map<String, Object>> importUserMap;

    private SsoUserFileImportStreamingService streamingService;

    private UaaService uaaService;

    private OrgInfoRepository orgDao;

    private Boolean isImported = false;
    private Boolean hasExportData = false;

    private StreamedContent templateFile;

    private TreeNode orgTree;
    private TreeNode selectedNode;

    private boolean displayErrorsOnly = true;

    @PostConstruct
    protected void init() {
        streamingService = WebUtil.getBean(SsoUserFileImportStreamingService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
    }

    public void uploadDataFile(FileUploadEvent event) {

        this.file = event.getFile();

        try (ExcelDocumentStreamingReader uploadDocument = new ExcelDocumentStreamingReader(file.getInputstream(), 0, SsoUserFileImportStreamingService.EXCEL_SSO_USER_HEADER_ROW, 1, 5)) {

            if (!streamingService.checkDocVersion(uploadDocument)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--模版版本不正确");
                return;
            }

            importUserMap = streamingService.getUserMapFromTemplate(uploadDocument);

            if (importUserMap.size() > 5000) {
                WebUtil.addErrorMessage("导入Excel中行数最多5000条，请分批导入！");
                return;
            }

            filterErrorRecords();

        } catch (IOException e) {
            WebUtil.addErrorMessage("File can not be read!", null);
            logger.error(e.getMessage(), e);

        } catch (Throwable e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") + "--" + e.getMessage(), null);
            logger.error(e.getMessage(), e);
        }
    }

    public void exportExcelTemplate() throws IOException {
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/ssouser/import/Sso_User_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        templateFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "Sso_User_Template.xlsx");
    }

    public void cancel() {
        this.file = null;

    }

    public void filterErrorRecords() {

        if (!displayErrorsOnly) return;

        if (importUserMap == null) return;

        displayUsers = getUsersByStatus(SsoUserFileImportStreamingService.ImportStatus.CheckFail);
        displayUsers.addAll(getUsersByStatus(SsoUserFileImportStreamingService.ImportStatus.Failure));

        importedUsers = getUsersByStatus(SsoUserFileImportStreamingService.ImportStatus.New);
    }

    private List<Map<String, Object>> getUsersByStatus(SsoUserFileImportStreamingService.ImportStatus status) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map : importUserMap) {
            SsoUserFileImportStreamingService.ImportStatus importStatus = (SsoUserFileImportStreamingService.ImportStatus) map.get("status");
            if (importStatus == null) continue;

            if (importStatus == status) {
                result.add(map);
            }
        }

        return result;
    }

    List<Map<String, Object>> displayUsers;

    List<Map<String, Object>> importedUsers;

    public List<Map<String, Object>> getDisplayUsers() {
        if (displayErrorsOnly) {
            return displayUsers;
        } else {
            return importUserMap;
        }
    }

    public List<Map<String, Object>> getImportedUsers() {
        return importedUsers;
    }

    public void setImportedUsers(List<Map<String, Object>> importedUsers) {
        this.importedUsers = importedUsers;
    }

    public List<Map<String, Object>> getImportUserMap() {
        return importUserMap;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public Boolean getHasExportData() {
        return hasExportData;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public boolean isDisplayErrorsOnly() {
        return displayErrorsOnly;
    }

    public void setDisplayErrorsOnly(boolean displayErrorsOnly) {
        this.displayErrorsOnly = displayErrorsOnly;
    }

    public boolean isEmpty() {
        return (importUserMap == null || importUserMap.isEmpty());
    }

    public StreamedContent getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(StreamedContent templateFile) {
        this.templateFile = templateFile;
    }
}
