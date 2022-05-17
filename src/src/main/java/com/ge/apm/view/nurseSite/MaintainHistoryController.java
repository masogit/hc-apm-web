package com.ge.apm.view.nurseSite;

import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.NurseSiteMaintainHistoryService;
import com.ge.apm.service.nurseSite.WordReportExportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

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
    private List<OrgInfo> listCheckClinicalOrgInfo;
    private OrgInfo selectClinicalOrgInfo;
    private List<NurseSiteMaintainHistory> listMaintainData;
    private List<NurseSiteMaintainHistory> selectedListMaintain;
    private OrgInfo selectedParentOrgInfo;
    //show
    private List<OrgInfo> listOrgInfo;
    private List<String> orgUidList;
    private StreamedContent exportFile;
    private Date startDate;
    private Date endDate;
    private List<OrgInfo> checkListParentOrgInfo;
    private WordReportExportService wordReportExportService;
    private UserAuth customRoleAuth;
    @PostConstruct
    protected void init() {
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainHistoryService.class);
        nurseSiteMaintainService.init();
        currentUser = UserContextService.getCurrentUserAccount();
        orgUidList = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
        wordReportExportService = WebUtil.getBean(WordReportExportService.class);
        loadUserParentOrgInfo();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_MAINTAIN_EXPORT);
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
        Calendar ca = Calendar.getInstance();
        if (start == null) {
            ca.set(Calendar.DAY_OF_MONTH, 1);
            start = ca.getTime();
        }
        if (end == null) {
            ca.add(Calendar.MONTH, 1);
            end = ca.getTime();
        }
        if (listCheckClinicalOrgInfo == null || listCheckClinicalOrgInfo.size() == 0) {
            listMaintainData = findDefaultMaintain(start, end);
        } else {
            List<String> listUidTemp = new ArrayList<String>();
            for (int i = 0; i < listCheckClinicalOrgInfo.size(); ++i) {
                listUidTemp.add(listCheckClinicalOrgInfo.get(i).getUid());
            }
            listMaintainData = nurseSiteMaintainService.getDataByClinicalOrgUid(listUidTemp, start, end);
        }
        if (listMaintainData != null) {
            nurseSiteMaintainService.supplementMaintainWithoutItem(listMaintainData);
        }
    }

    private List<NurseSiteMaintainHistory> findDefaultMaintain(Date startDate, Date endDate) {
        List<NurseSiteMaintainHistory> listResult = null;
        listResult = nurseSiteMaintainService.getDefaultMaintainData(orgUidList, startDate, endDate);
        return listResult;
    }

    private void loadUserParentOrgInfo() {
        if (orgUidList.size() > 0) {
            checkListParentOrgInfo = nurseSiteMaintainService.getListParentSiteByUid(orgUidList);
            listOrgInfo = new ArrayList<OrgInfo>();
            listCheckClinicalOrgInfo = new ArrayList<OrgInfo>();
        }
    }

    private void loadUserOrgInfo() {
        if (orgUidList.size() > 0) {
//			listOrgInfo=nurseSiteMaintainService.getListClinicalByUid(orgUidList);
            listOrgInfo = nurseSiteMaintainService.getListClinicalByUidAndParent(orgUidList, selectedParentOrgInfo.getUid());
            listCheckClinicalOrgInfo = new ArrayList<OrgInfo>();
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

    public List<OrgInfo> getListCheckClinicalOrgInfo() {
        return listCheckClinicalOrgInfo;
    }

    public void setListCheckClinicalOrgInfo(List<OrgInfo> listCheckClinicalOrgInfo) {
        this.listCheckClinicalOrgInfo = listCheckClinicalOrgInfo;
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

    public void onOrgDeptChange() {
        try {
            if (this.selectClinicalOrgInfo != null) {
                if (listCheckClinicalOrgInfo == null) {
                    listCheckClinicalOrgInfo = new ArrayList<OrgInfo>();
                } else {
                    listCheckClinicalOrgInfo.clear();
                }
                listCheckClinicalOrgInfo.add(this.selectClinicalOrgInfo);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public OrgInfo getSelectClinicalOrgInfo() {
        return selectClinicalOrgInfo;
    }

    public void setSelectClinicalOrgInfo(OrgInfo selectClinicalOrgInfo) {
        this.selectClinicalOrgInfo = selectClinicalOrgInfo;
    }
}
