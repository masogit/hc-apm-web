package com.ge.apm.view.asset;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.ContractExportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 212605082 on 2018/9/20.
 */
@ManagedBean
@ViewScoped
public class ContractInfoExportController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContractInfoExportController.class);

    private StreamedContent exportFile;

    private UaaService uaaService;
    private ContractExportService contractExportService;

    private String contractType;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    private OrgInfo selectedOrg;

    List<Map<String, Object>> contractResultList;
    List<Map<String, Object>> contractItemResultList;
    private List<Map<String, Object>> selectedItems;

    private Date startDateFilter;
    private Date endDateFilter;

    @PostConstruct
    protected void init() {

        contractExportService = WebUtil.getBean(ContractExportService.class);
        uaaService = WebUtil.getBean(UaaService.class);

        this.initOrgTree();

    }

    private void exportContractExcel(List<Map<String, Object>> contractData, List<Map<String, Object>> contractItemData) {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/contract/contractExport/APM_Contract_Export_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = contractExportService.exportData(doc, contractData, contractItemData);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ContractInfoData.xlsx");
            }
        } catch (IOException ex) {
            Logger.getLogger(ContractInfoExportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void previewData() {

        this.selectedItems = null;

        if (null == startDateFilter && null == endDateFilter) {
            showMsgBox("合同开始时间和合同结束时间不能同时为空");
            return;
        }

        contractResultList = contractExportService.createContractData(this.selectedOrg, this.contractType, this.startDateFilter, this.endDateFilter);
    }

    public void exportAll(){
        contractItemResultList = contractExportService.createContractItemData(contractResultList);
        this.exportContractExcel(contractResultList, contractItemResultList);
    }

    public void exportSelected(){
        contractItemResultList = contractExportService.createContractItemData(selectedItems);
        this.exportContractExcel(selectedItems, contractItemResultList);
    }

    private void showMsgBox(String msg) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
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

    public TreeNode getOrgTree() {
        return orgTree;
    }

    public void setOrgTree(TreeNode orgTree) {
        this.orgTree = orgTree;
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Date getStartDateFilter() {
        return startDateFilter;
    }

    public void setStartDateFilter(Date startDateFilter) {
        this.startDateFilter = startDateFilter;
    }

    public Date getEndDateFilter() {
        return endDateFilter;
    }

    public void setEndDateFilter(Date endDateFilter) {
        this.endDateFilter = endDateFilter;
    }

    public List<Map<String, Object>> getContractResultList() {
        return contractResultList;
    }

    public void setContractResultList(List<Map<String, Object>> contractResultList) {
        this.contractResultList = contractResultList;
    }

    public List<Map<String, Object>> getContractItemResultList() {
        return contractItemResultList;
    }

    public void setContractItemResultList(List<Map<String, Object>> contractItemResultList) {
        this.contractItemResultList = contractItemResultList;
    }

    public List<Map<String, Object>> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Map<String, Object>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}
