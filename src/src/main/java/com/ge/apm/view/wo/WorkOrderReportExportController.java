package com.ge.apm.view.wo;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.map.HashedMap;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.WorkOrderExportService;
import com.ge.apm.view.sysutil.UserContextService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.DefaultMapper;

import webapp.framework.web.WebUtil;
import com.deepoove.poi.XWPFTemplate;

/**
 * Created by 212605082 on 2017/7/26.
 */
@ManagedBean
@ViewScoped
public class WorkOrderReportExportController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2591740986699150398L;

    private WorkOrderExportService workOrderExportService;

    private StreamedContent exportFile;

    private StreamedContent exportPdf;

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
        try {
            String woXml = getWoXml(data);

            File xsl = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/wo2fo.xsl").getPath());

            File conf = new File(WorkOrderReportExportController.class.getClassLoader().
                    getResource("../../portal/pdf/fop.xml").getPath());

            FopFactory fopFactory = FopFactory.newInstance(conf);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            TransformerFactory factory = TransformerFactory.newInstance();
            try {
                Transformer transformer = factory.newTransformer(new StreamSource(xsl));
                Source src = new StreamSource(new StringReader(woXml));
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);
            } finally {
                exportPdf = new DefaultStreamedContent((new ByteArrayInputStream(out.toByteArray())), MimeConstants.MIME_PDF, "WorkOrderDetails.pdf");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getWoXml(List<Map<String, Object>> data) {
        XStream xStream = new XStream(new DomDriver());
        xStream.registerConverter(new Map2XmlConverter(new DefaultMapper(XStream.class.getClassLoader())));
        xStream.alias("List", List.class);
        xStream.alias("map", HashedMap.class);

        return xStream.toXML(data);
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

        //orgTree = uaaService.getOrgTreeBySite(loginUser.getSiteUID());

        TreeNode treeNode = uaaService.getOrgTreeByParentIdWithSelectedChildren(loginUser.getSiteUID(), loginUser.getUid());

        OrgInfo rootOrg = new OrgInfo();
        rootOrg.setId(-1);
        rootOrg.setName(WebUtil.getMessage("AllHospitals"));
        orgTree = new DefaultTreeNode("root", null, null);
        TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
        rootNode.getChildren().add(treeNode);
        rootNode.setExpanded(true);

    }

    public void exportAllWoDocx() {
        export2Docx(workOrderList);
    }

    public void exportSelectedWoDocx() {
        export2Docx(selectedItems);
    }

    private void export2Docx(List<Map<String, Object>> selectedItems) {
        File file = Paths.get("D:\\tmp\\test.docx").toFile();

        XWPFTemplate template = null;
        try (FileOutputStream out = new FileOutputStream(file);
             InputStream inputStream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExportTemplate/template.docx")) {
            template = XWPFTemplate.compile(inputStream);
            Map<String, Object> datas = new HashMap<>();
            Map<String, Object> map = selectedItems.get(0);
            datas.put("device_name", map.get("设备名称"));
            datas.put("device_type", map.get("设备类型"));
            datas.put("device_serial", map.get("设备序列号"));
            datas.put("device_qr", map.get("二维码"));
            datas.put("device_department", map.get("科室"));
            datas.put("device_oem", map.get("制造商"));
            datas.put("device_number", map.get("设备编号"));
            datas.put("device_location", map.get("当前位置"));

            datas.put("issue_number", map.get("报修单号"));
            datas.put("issue_reporter", map.get("报修人"));
            datas.put("issue_reporter_phone", map.get("联系电话"));
            datas.put("issue_report_time", map.get("报修时间"));
            datas.put("issue_report_status", map.get("报修状态"));
            datas.put("issue_device_stop_time", map.get("停机时间"));
            datas.put("issue_desc", map.get("故障描述"));

            datas.put("fix_status", map.get("工单状态"));
            datas.put("fix_hours", map.get("维修工时"));
            datas.put("fix_fee", map.get("维修总费用"));
            datas.put("fix_sign_time", map.get("签到时间"));
            datas.put("fix_type", map.get("工单类型"));
            datas.put("fix_error_type", map.get("故障类型"));
            //todo
            datas.put("fix_owner", map.get("当前责任人"));
            datas.put("fix_reason", map.get("原因描述"));
            datas.put("fix_solution", map.get("解决方案"));
            datas.put("fix_validation", map.get("交付前检测"));

            datas.put("fix_co1", map.get("协作者1"));
            datas.put("fix_co2", map.get("协作者2"));
            datas.put("fix_cohour1", map.get("工时1"));
            datas.put("fix_cohour2", map.get("工时2"));

            datas.put("fix_man_hour", map.get("工时"));

            datas.put("part_table", map.get("备件信息"));
            datas.put("proceed_table", map.get("维修流程"));

            template.registerPolicy("part_table", new PartTableRenderPolicy());
            template.registerPolicy("proceed_table", new ProceedTableRenderPolicy());

            template.render(datas);
            template.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (template != null) {
                    template.close();
                }
            } catch (Exception ignore) {

            }
        }
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
}
