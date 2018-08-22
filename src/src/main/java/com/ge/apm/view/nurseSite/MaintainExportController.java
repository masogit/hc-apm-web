package com.ge.apm.view.nurseSite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.web.WebUtil;

/**
 * Created by 212605082 on 2018/5/8.
 */
@ManagedBean
@ViewScoped
public class MaintainExportController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainExportController.class);
	private NurseSiteMaintainService nurseSiteMaintainService;
	private UserAccount currentUser;
	private List<OrgInfo> listCheckClinicalOrgInfo;
	private List<NurseSiteMaintain> listMaintainData;
	private List<NurseSiteMaintain> selectedListMaintain;
	private OrgInfo selectedParentOrgInfo;
    //show
    private List<OrgInfo> listOrgInfo; 
    private Set<String> orgUidList;
    private StreamedContent exportFile;
    private Date startDate;
    private Date endDate;
    private List<OrgInfo> checkListParentOrgInfo;

	@PostConstruct
	protected void init() {
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		currentUser = UserContextService.getCurrentUserAccount();
		orgUidList = nurseSiteMaintainService.findAccessOrgUidListByUserUid(currentUser.getUid());
		loadUserParentOrgInfo();
	}
	
	private void initValue(){
		this.selectedListMaintain=new ArrayList<NurseSiteMaintain>();
	}
	
	private void searchMaintainData(Date start,Date end){
		Calendar ca=Calendar.getInstance();
		if(start==null){
			ca.set(Calendar.DAY_OF_MONTH, 1);
			start=ca.getTime();
		}
		if(end==null){
			ca.add(Calendar.MONTH, 1);
			end=ca.getTime();
		}
		else{
			Calendar endCa=Calendar.getInstance();
			endCa.setTime(end);
			endCa.add(Calendar.DATE, 1);
			end=endCa.getTime();
		}
		if(listCheckClinicalOrgInfo==null || listCheckClinicalOrgInfo.size()==0){
			listMaintainData=findDefaultMaintain(start,end);
		}
		else{
			List<String> listUidTemp=new ArrayList<String>();
			for(int i=0;i<listCheckClinicalOrgInfo.size();++i){
				listUidTemp.add(listCheckClinicalOrgInfo.get(i).getUid());
			}
			listMaintainData=nurseSiteMaintainService.getDataByClinicalOrgUid(listUidTemp, start, end);
		}
	}
	
	private List<NurseSiteMaintain> findDefaultMaintain(Date startDate,Date endDate){
		List<NurseSiteMaintain> listResult=null;
		listResult=nurseSiteMaintainService.getDefaultMaintainData(orgUidList, startDate, endDate);
		return listResult;
	}
	
	private void loadUserParentOrgInfo(){
		if(orgUidList.size()>0){
			checkListParentOrgInfo=nurseSiteMaintainService.getListParentSiteByUid(orgUidList);
			listOrgInfo=new ArrayList<OrgInfo>();
			listCheckClinicalOrgInfo=new ArrayList<OrgInfo>();
		}
	}
	
	private void loadUserOrgInfo(){
		if(orgUidList.size()>0){
//			listOrgInfo=nurseSiteMaintainService.getListClinicalByUid(orgUidList);
			listOrgInfo=nurseSiteMaintainService.getListClinicalByUidAndParent(orgUidList, selectedParentOrgInfo.getUid());
			listCheckClinicalOrgInfo=new ArrayList<OrgInfo>();
		}
	}
	
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void onParentOrgInfoChange(){
		loadUserOrgInfo();
	}
	
	public void previewData(){
		searchMaintainData(startDate, endDate);
		initValue();
	}
	
	public void exportSelected(){
		if(selectedListMaintain==null || selectedListMaintain.size()==0){
			addMessage("未选中的需要导出的记录");
			return;
		}
		downloadFile(nurseSiteMaintainService.parseMaintainToMap(selectedListMaintain));
	}
	
	public void exportAllDetail(){
		if(listMaintainData==null || listMaintainData.size()==0){
			addMessage("无数据");
			return;
		}
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/nurseSite/APM_NurseSiteMaintainDetail_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            byte[] result= nurseSiteMaintainService.writeMaintainDetailToExcel(doc,listMaintainData);
            if (result!=null) {
                exportFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/vnd.ms-excel", "NurseSiteMaintainDetail.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
	}
	
	public void exportSelectedDetail(){
		if(selectedListMaintain==null || selectedListMaintain.size()==0){
			addMessage("未选中的需要导出的记录");
			return;
		}
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/nurseSite/APM_NurseSiteMaintainDetail_Template.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            byte[] result= nurseSiteMaintainService.writeMaintainDetailToExcel(doc,selectedListMaintain);
            if (result!=null) {
                exportFile = new DefaultStreamedContent(new ByteArrayInputStream(result), "application/vnd.ms-excel", "NurseSiteMaintainDetail.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
	}
	
	private void downloadFile(List<Map<String,Object>> data){
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
	
	public boolean isValideDate() {
		Calendar calender = Calendar.getInstance();
		calender.setTime(new Date());
		calender.add(Calendar.DAY_OF_MONTH, -1);
		return true;
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

	public List<NurseSiteMaintain> getListMaintainData() {
		return listMaintainData;
	}

	public void setListMaintainData(List<NurseSiteMaintain> listMaintainData) {
		this.listMaintainData = listMaintainData;
	}

	public Set<String> getOrgUidList() {
		return orgUidList;
	}

	public void setOrgUidList(Set<String> orgUidList) {
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

	public List<NurseSiteMaintain> getSelectedListMaintain() {
		return selectedListMaintain;
	}

	public void setSelectedListMaintain(List<NurseSiteMaintain> selectedListMaintain) {
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
	
}
