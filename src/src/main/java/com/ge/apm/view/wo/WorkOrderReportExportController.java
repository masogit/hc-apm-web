package com.ge.apm.view.wo;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.WorkOrderExportService;
import com.ge.apm.service.wo.WorkOrderExportTemplateService;
import com.ge.apm.service.wo.WorkOrderPublishRenderService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2017/7/26.
 */
@ManagedBean
@ViewScoped
public class WorkOrderReportExportController implements Serializable {

    private static final long serialVersionUID = -2591740986699150398L;

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(WorkOrderReportExportController.class);

    private WorkOrderExportService workOrderExportService;
    private WorkOrderPublishRenderService workOrderPublishRenderService;
    private WorkOrderExportTemplateService workOrderExportTemplateService;

    private StreamedContent exportFile;

    private StreamedContent exportPdf;

    private StreamedContent exportDocx;

    private UserAccount currentUser;

    private OrgInfoRepository orgDao;

    private List<OrgInfo> institutionList;
    private List<OrgInfo> hospitalList;
    private List<OrgInfo> siteList;

    private String currentInstitutionUID;
    private String currentHospitalUID;
    private String currentSiteUID;

    private Boolean isStatusOpen;
    private Boolean isStatusClose;
    private Boolean isStatusCancel;

    private Date startTime;
    private Date endTime;
    private Date closedStartTime;
    private Date closedEndTime;

    private List<Map<String, Object>> workOrderList;

    private UaaService uaaService;

    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    private List<Map<String, Object>> selectedItems;

    @PostConstruct
    protected void init() {

        workOrderExportService = WebUtil.getBean(WorkOrderExportService.class);
        workOrderPublishRenderService = WebUtil.getBean(WorkOrderPublishRenderService.class);
        workOrderExportTemplateService = WebUtil.getBean(WorkOrderExportTemplateService.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);

        currentUser = UserContextService.getCurrentUserAccount();
        currentInstitutionUID = currentUser.getInstitutionUID();
        currentHospitalUID = currentUser.getHospitalUID();
        currentSiteUID = currentUser.getSiteUID();

        institutionList = orgDao.findByTenantUIDAndOrgType(currentUser.getTenantUID(), 1);
        hospitalList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(currentUser.getInstitutionUID(), 2);
        siteList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(currentUser.getHospitalUID(), 3);

        isStatusOpen = Boolean.TRUE;
        isStatusClose = Boolean.TRUE;
        isStatusCancel = Boolean.TRUE;

        initOrgTree();

    }

    private void exportWoData(List<Map<String, Object>> data) {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExport/APM_WorkOrder_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = workOrderExportService.exportData(doc, data);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "WorkOrderData.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(WorkOrderReportExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportAllWo() {
        exportWoData(workOrderList);
    }

    public void exportSelectedWo() {
        exportWoData(selectedItems);
    }

    public void exportAllWoPdf() {
        export2Pdf(workOrderList);
    }

    public void exportSelectedWoPdf() {
        export2Pdf(selectedItems);
    }

    private void export2Pdf(List<Map<String, Object>> data) {
        WorkOrderPublishRenderService.RenderResult renderResult = workOrderPublishRenderService.renderPdf(data);
        exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(renderResult.result)), renderResult.contentType, renderResult.fileName);
    }

    public void exportAllWoDocx() {
        export2Docx(workOrderList);
    }

    public void exportSelectedWoDocx() {
        export2Docx(selectedItems);
    }

    private void export2Docx(List<Map<String, Object>> data) {
        exportDocx = null;
        if (data.isEmpty()) {
            WebUtil.addErrorMessage("未选中的需要导出的记录");
            return;
        }
        try {
            byte[] templateBytes = workOrderExportTemplateService.getActiveTemplateBytes(currentSiteUID);
            WorkOrderPublishRenderService.RenderResult renderResult = workOrderPublishRenderService.render(data, templateBytes);
            exportDocx = new DefaultStreamedContent((new ByteArrayInputStream(renderResult.result)), renderResult.contentType, renderResult.fileName);
        } catch (IOException e) {
            logger.error("Fail to export work orders", e);
            WebUtil.addErrorMessage("导出失败");
        }
    }


    public void previewData(ActionEvent actionEvent) {

        this.selectedItems = null;

        if (null == startTime && null == endTime && null == closedStartTime && null == closedEndTime) {
            showMsgBox("报修时间范围和关单时间范围需任填一组（最早时间/最晚时间）");
            return;
        } else {
            if ((null != startTime && null == endTime)) {
                showMsgBox("请选择最晚报修时间");
                return;
            }
            if ((null == startTime && null != endTime)) {
                showMsgBox("请选择最早报修时间");
                return;
            }
            if (null != closedStartTime && null == closedEndTime) {
                showMsgBox("请选择最晚关单时间");
                return;
            }
            if (null == closedStartTime && null != closedEndTime) {
                showMsgBox("请选择最早关单时间");
                return;
            }

            workOrderList = workOrderExportService.createData(selectedOrg, startTime, endTime, closedStartTime, closedEndTime, getStatusList());
        }

    }

    private void showMsgBox(String msg) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private List<Integer> getStatusList() {
        List<Integer> statusList = new ArrayList<>();
        if (isStatusOpen) {
            statusList.add(1);
        }
        if (isStatusClose) {
            statusList.add(2);
        }
        if (isStatusCancel) {
            statusList.add(3);
        }

        if (statusList.isEmpty()) {
            //当工单状态没有选择时
            statusList.add(-999);
        }

        return statusList;
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        OrgInfo orgInfo = (OrgInfo) node.getData();
        if (orgInfo.getId() == -1) {
            selectedOrg = null;
        } else {
            selectedOrg = (OrgInfo) node.getData();
        }

    }

    public void initOrgTree() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);

    }

    public void filterSiteByHospital() {
        siteList = orgDao.findByParentUIDAndOrgTypeOrderByNameAsc(this.getCurrentHospitalUID(), 3);
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public List<OrgInfo> getInstitutionList() {
        return institutionList;
    }

    public void setInstitutionList(List<OrgInfo> institutionList) {
        this.institutionList = institutionList;
    }

    public List<OrgInfo> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<OrgInfo> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<OrgInfo> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<OrgInfo> siteList) {
        this.siteList = siteList;
    }

    public String getCurrentInstitutionUID() {
        return currentInstitutionUID;
    }

    public void setCurrentInstitutionUID(String currentInstitutionUID) {
        this.currentInstitutionUID = currentInstitutionUID;
    }

    public String getCurrentHospitalUID() {
        return currentHospitalUID;
    }

    public void setCurrentHospitalUID(String currentHospitalUID) {
        this.currentHospitalUID = currentHospitalUID;
    }

    public String getCurrentSiteUID() {
        return currentSiteUID;
    }

    public void setCurrentSiteUID(String currentSiteUID) {
        this.currentSiteUID = currentSiteUID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getClosedStartTime() {
        return closedStartTime;
    }

    public void setClosedStartTime(Date closedStartTime) {
        this.closedStartTime = closedStartTime;
    }

    public Date getClosedEndTime() {
        return closedEndTime;
    }

    public void setClosedEndTime(Date closedEndTime) {
        this.closedEndTime = closedEndTime;
    }

    public Boolean getStatusOpen() {
        return isStatusOpen;
    }

    public void setStatusOpen(Boolean statusOpen) {
        isStatusOpen = statusOpen;
    }

    public Boolean getStatusClose() {
        return isStatusClose;
    }

    public void setStatusClose(Boolean statusClose) {
        isStatusClose = statusClose;
    }

    public Boolean getStatusCancel() {
        return isStatusCancel;
    }

    public void setStatusCancel(Boolean statusCancel) {
        isStatusCancel = statusCancel;
    }

    public List<Map<String, Object>> getWorkOrderList() {
        return workOrderList;
    }

    public void setWorkOrderList(List<Map<String, Object>> workOrderList) {
        this.workOrderList = workOrderList;
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

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }


    public List<Map<String, Object>> getSelectedItems() {
        return selectedItems;
    }


    public void setSelectedItems(List<Map<String, Object>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public StreamedContent getExportPdf() {
        return exportPdf;
    }

    public void setExportPdf(StreamedContent exportPdf) {
        this.exportPdf = exportPdf;
    }

    public StreamedContent getExportDocx() {
        return exportDocx;
    }

    public void setExportDocx(StreamedContent exportDocx) {
        this.exportDocx = exportDocx;
    }
}
