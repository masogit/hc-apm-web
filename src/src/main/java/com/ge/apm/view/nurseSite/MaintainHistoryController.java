package com.ge.apm.view.nurseSite;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.NurseSiteMaintainItemHistoryRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainHistoryService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class MaintainHistoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainHistoryController.class);
    private NurseSiteMaintainHistoryService nurseSiteMaintainService;
    private UserAccount currentUser;
    private List<NurseSiteMaintainHistory> listMaintainData;
    private List<NurseSiteMaintainHistory> selectedListMaintain;
    private OrgInfo selectedParentOrgInfo;
    private NurseSiteMaintainItemHistoryRepository maintainItemHistoryRepository;
    //show
    private List<OrgInfo> listOrgInfo;
    private List<String> orgUidList;
    private StreamedContent exportFile;
    private Date startDate;
    private Date endDate;
    private List<OrgInfo> checkListParentOrgInfo;
    private WordReportExportService wordReportExportService;
    private UserAuth customRoleAuth;

    private UaaService uaaService;
    private AssetInstallationService aiService;
    private OrgInfo selectedOrg;
    private TreeNode orgTree;
    private TreeNode selectedNode;
    List<OrgInfo> listOrgDept;
    OrgInfo selectedOrgDepart;

    @PostConstruct
    protected void init() {
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainHistoryService.class);
        nurseSiteMaintainService.init();
        currentUser = UserContextService.getCurrentUserAccount();
        orgUidList = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
        wordReportExportService = WebUtil.getBean(WordReportExportService.class);
        maintainItemHistoryRepository = WebUtil.getBean(NurseSiteMaintainItemHistoryRepository.class);
        loadUserParentOrgInfo();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_MAINTAIN_EXPORT);
        aiService = WebUtil.getBean(AssetInstallationService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        fetchOrgTree();
    }

    private void initValue() {
        this.selectedListMaintain = new ArrayList<NurseSiteMaintainHistory>();
    }

    public Date getMaxDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR,year-1);
        calendar.add(Calendar.DATE,-1);
        return calendar.getTime();
    }

    private void searchMaintainData(Date start, Date end) {
        if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
            listMaintainData = ListUtil.empty();
            return;
        }
        Calendar ca = Calendar.getInstance();
        if (start == null) {
            ca.set(Calendar.DAY_OF_MONTH, 1);
            start = ca.getTime();
        }
        if (end == null) {
            ca.add(Calendar.MONTH, 1);
            end = ca.getTime();
        }
        Wrapper<NurseSiteMaintainHistory> wrapper = new EntityWrapper<>();
        if (currentUser.isEnableAssetTypeGroup()) {
            wrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
        }
        wrapper.ge("nsm.schedule_date", start);
        wrapper.le("nsm.schedule_date", end);
        if (this.selectedOrg == null &&  (this.selectedOrgDepart == null || this.selectedOrgDepart.getId() == -1)) {
            wrapper.in("nsm.clinical_dept_uid", orgUidList);
//            listMaintainData = findDefaultMaintain(start, end);
        } else {
            List<String> listUidTemp = new ArrayList<String>();

            if(this.selectedOrg != null){
                listUidTemp.add(this.selectedOrg.getUid());
                // 如果只选择了医院/院区，就获取到下属科室列表，查询下属科室的所有数据
                if(this.selectedOrgDepart == null || this.selectedOrgDepart.getId() == -1){
                    if(CollectionUtils.isNotEmpty(listOrgDept)){
                        for (OrgInfo orgInfo : listOrgDept) {
                            listUidTemp.add(orgInfo.getUid());
                        }
                    }
                }
            }
            if(this.selectedOrgDepart != null && this.selectedOrgDepart.getId() != -1){
                listUidTemp.add(this.selectedOrgDepart.getUid());
            }
            wrapper.in("nsm.clinical_dept_uid", listUidTemp);
//            listMaintainData = nurseSiteMaintainService.getDataByClinicalOrgUid(listUidTemp, start, end);
        }

        wrapper.groupBy("nsm.id");
        wrapper.orderBy("nsm.schedule_date");
        listMaintainData = nurseSiteMaintainService.findJoinAssetList(wrapper);
        if (ObjectUtil.isNotEmpty(listMaintainData)) {
            Set<String> maintainIds = listMaintainData.stream().map(NurseSiteMaintainHistory::getId).collect(Collectors.toSet());
            Wrapper<NurseSiteMaintainItemHistory> itemWrapper = new EntityWrapper<>();
            itemWrapper.in("nsmi.parent_id", maintainIds);
            if (currentUser.isEnableAssetTypeGroup()) {
                itemWrapper.in("ai.asset_type", currentUser.getAssetTypeGroupMap().keySet());
            }
            Map<String, List<NurseSiteMaintainItemHistory>> itemMap = nurseSiteMaintainService.findItemJoinAssetList(new Pagination(), itemWrapper).stream().collect(Collectors.groupingBy(NurseSiteMaintainItemHistory::getParentId));
            for (NurseSiteMaintainHistory maintain : listMaintainData) {
                maintain.setListNurseSiteMaintainItem(itemMap.get(maintain.getId()));
            }
            nurseSiteMaintainService.supplementMaintainWithoutItem(listMaintainData);
        }
    }

    private List<NurseSiteMaintainHistory> findDefaultMaintain(Date startDate, Date endDate) {
        return nurseSiteMaintainService.getDefaultMaintainData(orgUidList, startDate, endDate);
    }

    private void loadUserParentOrgInfo() {
        if (orgUidList.size() > 0) {
            checkListParentOrgInfo = nurseSiteMaintainService.getListParentSiteByUid(orgUidList);
            listOrgInfo = new ArrayList<OrgInfo>();
        }
    }

    private void loadUserOrgInfo() {
        if (orgUidList.size() > 0) {
//			listOrgInfo=nurseSiteMaintainService.getListClinicalByUid(orgUidList);
            listOrgInfo = nurseSiteMaintainService.getListClinicalByUidAndParent(orgUidList, selectedParentOrgInfo.getUid());
        }
    }

    private void addMessage(String str) {
        FacesMessage message = new FacesMessage(str, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onParentOrgInfoChange() {
        loadUserOrgInfo();
    }

    public void previewData() {
        searchMaintainData(startDate, endDate);
        initValue();
    }

    public void exportAll() {
        clearStream();

        if (listMaintainData == null || listMaintainData.size() == 0) {
            addMessage("无数据");
            return;
        }
        nurseSiteMaintainService.supplementMaintainWithoutItem(listMaintainData);
        downloadFile(nurseSiteMaintainService.parseMaintainToMap(listMaintainData));
    }

    public void exportSelected() {
        clearStream();

        if (selectedListMaintain == null || selectedListMaintain.size() == 0) {
            addMessage("未选中需要导出的记录");
            return;
        }
        nurseSiteMaintainService.supplementMaintainWithoutItem(selectedListMaintain);
        downloadFile(nurseSiteMaintainService.parseMaintainToMap(selectedListMaintain));
    }

    public void exportAllDetail() {
        clearStream();

        if (listMaintainData == null || listMaintainData.size() == 0) {
            addMessage("无数据");
            return;
        }
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/nurseSite/APM_NurseSiteMaintainDetail_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            byte[] result = nurseSiteMaintainService.writeMaintainDetailToExcel(doc, new ArrayList<NurseSiteMaintainHistory>(listMaintainData));
            if (result != null) {
                exportFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/vnd.ms-excel", "NurseSiteMaintainDetail.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public void exportSelectedDetail() {
        clearStream();

        if (selectedListMaintain == null || selectedListMaintain.size() == 0) {
            addMessage("未选中需要导出的记录");
            return;
        }
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/nurseSite/APM_NurseSiteMaintainDetail_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            byte[] result = nurseSiteMaintainService.writeMaintainDetailToExcel(doc, selectedListMaintain);
            if (result != null) {
                exportFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/vnd.ms-excel", "NurseSiteMaintainDetail.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public void exportSelected2Docx() {
        clearStream();

        if (selectedListMaintain == null || selectedListMaintain.size() == 0) {
            addMessage("未选中需要导出的记录");
            return;
        }
        exportDocx(selectedListMaintain);
        return;
    }

    public void exportAll2Docx() {
        clearStream();

        if (listMaintainData == null || listMaintainData.size() == 0) {
            addMessage("无数据");
            return;
        }
        exportDocx(listMaintainData);
    }

    public void exportDocx(List<NurseSiteMaintainHistory> selectedListMaintain) {
        try {
            long t1 = System.currentTimeMillis();
            LOGGER.info("Export docx: start");
            List<NurseSiteMaintainHistory> checkedMaintainList = selectedListMaintain.stream().filter(i -> i.getCheckDate() != null).collect(Collectors.toList());
            String deptUid = null;
            for (NurseSiteMaintainHistory maintain : checkedMaintainList) {
                if (deptUid == null) {
                    deptUid = maintain.getClinicalDeptUID();
                } else if (!StringUtils.equals(deptUid, maintain.getClinicalDeptUID())) {
                    throw new IllegalStateException("所属科室不唯一");
                }
            }
            checkedMaintainList.forEach(i -> nurseSiteMaintainService.supplementMaintain(i, 0, 0, true));
            long t2 = System.currentTimeMillis();
            LOGGER.info("Export docx: loading data: {}", t2 - t1);

            byte[] result = wordReportExportService.writeMaintainHistoryDetailToDocx(checkedMaintainList);
            if (result != null) {
                exportFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/zip", "Report.zip");
            }
            long t3 = System.currentTimeMillis();
            LOGGER.info("Export docx: convert data: {}", t3 - t2);
            LOGGER.info("Export docx: {}", t3 - t1);
        } catch (Exception ex) {
            addMessage("报告导出失败：" + ex.getMessage());
            exportFile = null;
            LOGGER.error("Fail to export word document", ex);
        }
    }

    private void downloadFile(List<Map<String, Object>> data) {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/nurseSite/APM_NurseSiteMaintain_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = nurseSiteMaintainService.exportData(doc, data);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "NurseSiteMaintain.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserAccount currentUser) {
        this.currentUser = currentUser;
    }


    public List<OrgInfo> getListOrgInfo() {
        return listOrgInfo;
    }

    public void setListOrgInfo(List<OrgInfo> listOrgInfo) {
        this.listOrgInfo = listOrgInfo;
    }


    public List<NurseSiteMaintainHistory> getListMaintainData() {
        return listMaintainData;
    }

    public void setListMaintainData(List<NurseSiteMaintainHistory> listMaintainData) {
        this.listMaintainData = listMaintainData;
    }

    public List<String> getOrgUidList() {
        return orgUidList;
    }

    public void setOrgUidList(List<String> orgUidList) {
        this.orgUidList = orgUidList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<NurseSiteMaintainHistory> getSelectedListMaintain() {
        return selectedListMaintain;
    }

    public void setSelectedListMaintain(List<NurseSiteMaintainHistory> selectedListMaintain) {
        this.selectedListMaintain = selectedListMaintain;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public OrgInfo getSelectedParentOrgInfo() {
        return selectedParentOrgInfo;
    }

    public void setSelectedParentOrgInfo(OrgInfo selectedParentOrgInfo) {
        this.selectedParentOrgInfo = selectedParentOrgInfo;
    }

    public List<OrgInfo> getCheckListParentOrgInfo() {
        return checkListParentOrgInfo;
    }

    public void setCheckListParentOrgInfo(List<OrgInfo> checkListParentOrgInfo) {
        this.checkListParentOrgInfo = checkListParentOrgInfo;
    }

    public UserAuth getCustomRoleAuth() {
		return customRoleAuth;
	}

	public void setCustomRoleAuth(UserAuth customRoleAuth) {
		this.customRoleAuth = customRoleAuth;
	}

    /**
     * clear the stream, if not, the second export will download the previous stream even if there is no data
     */
    private void clearStream() {
        exportFile = null;
    }

    private void fetchOrgTree() {
        String siteUid = currentUser.getSiteUID();
        String uid = currentUser.getUid();
        TreeNode subTree = uaaService.getOrgTreeByParentIdWithSelectedChildren(siteUid, uid);
        orgTree = new DefaultTreeNode("org", null, null);
        orgTree.getChildren().add(subTree);
        selectedOrg = aiService.getOrgInfoByUid(siteUid);
        fetchDeptOrgList(selectedOrg);
    }

    public void onSelectOrgTree(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        selectedOrg = (OrgInfo) node.getData();
        if (null != selectedOrg) {
            fetchDeptOrgList(selectedOrg);
        }
    }

    public void fetchDeptOrgList(OrgInfo orgInfo) {
        listOrgDept = uaaService.findAccessClinicalUidByUserUid(currentUser.getUid(),orgInfo.getSiteUID());
        if(listOrgDept==null){
            listOrgDept=new ArrayList<>();
        }
        OrgInfo tempOrgInfo=new OrgInfo();
        tempOrgInfo.setId(-1);
        tempOrgInfo.setName("请选择...");
        listOrgDept.add(0,tempOrgInfo);
    }

    public OrgInfo getSelectedOrg() {
        return selectedOrg;
    }

    public void setSelectedOrg(OrgInfo selectedOrg) {
        this.selectedOrg = selectedOrg;
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

    public List<OrgInfo> getListOrgDept() {
        return listOrgDept;
    }

    public void setListOrgDept(List<OrgInfo> listOrgDept) {
        this.listOrgDept = listOrgDept;
    }

    public OrgInfo getSelectedOrgDepart() {
        return selectedOrgDepart;
    }

    public void setSelectedOrgDepart(OrgInfo selectedOrgDepart) {
        this.selectedOrgDepart = selectedOrgDepart;
    }
}
