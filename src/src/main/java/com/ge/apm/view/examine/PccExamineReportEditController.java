package com.ge.apm.view.examine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.PccExamineReportRepository;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.examine.PccExamineReportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;
/**
 * Created by 212605082 on 2018/5/8.
 */
@ManagedBean
@ViewScoped
public class PccExamineReportEditController implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportEditController.class);
	
	@Getter@Setter
	private List<ApmDropDownItem> selectLineStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectMirrorStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectShellStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectLinkerStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectOilStatus;
	@Getter@Setter
	private ApmDropDownItem selectTipStatus;
	@Getter@Setter
	private ApmDropDownItem selectElectricLeakageStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectFirstSeenStatus;
	@Getter@Setter
	private ApmDropDownItem selectFirstTipStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectResultStatus;
	@Getter@Setter
	private ApmDropDownItem selectRecommandStatus;
	protected UserAccount currentUser;
	protected I18nMessageRepository i18nDao;
	private UploadedFile file;
	private ApmStreamedContent reportFile;
	private List<ApmStreamedContent> listStreamedContent;
	private ApmStreamedContent picture1;
	private ApmStreamedContent picture2;
	private ApmStreamedContent picture3;
	private ApmStreamedContent picture4;
	private ApmStreamedContent pictureTail;
	private List<PccExamineReport> listSelected;
	private PccExamineReport selected;
	private boolean blnNewUpload=false;
	String updateViewIDs;
	UaaService uaaService;
	AssetInstallationService aiService;
	String contractInfoSiteUID;
	PccExamineReportService service;
	PccExamineReportRepository dao;
	@PostConstruct
	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		uaaService = WebUtil.getBean(UaaService.class);
		aiService = WebUtil.getBean(AssetInstallationService.class);
		service = WebUtil.getBean(PccExamineReportService.class);
		service.clear();
		dao = WebUtil.getBean(PccExamineReportRepository.class);
		if(listStreamedContent==null){
			listStreamedContent = new ArrayList<ApmStreamedContent>();
		}
		String encodeStr="";
		try {
			encodeStr = WebUtil.getRequestParameter("str");
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
		if(StringUtils.isNotEmpty(encodeStr)){
			String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
			if ("Create".equalsIgnoreCase(actionName)) {
				try {
					prepareCreate();
					String assetuid = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
					this.selected.setAssetUid(assetuid);
					this.selected.setAssetInfo(service.getAssetInfo(assetuid));
					this.selected.setStatus(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage());
				} 
	        } else if ("Edit".equalsIgnoreCase(actionName)) {
	            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
	            setSelected(selectedId);
	            this.selected.setAssetInfo(service.getAssetInfo(this.selected.getAssetUid()));
	            prepareEdit();
	        }
		}
	}
	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub
		try {
			if(eventObject!=null){
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error(e.getMessage());
		}
	}
	public void save() {
		// TODO Auto-generated method stub
		prepareSave();
		boolean result=false;
		if(blnNewUpload){
			listStreamedContent.clear();
			if(picture1!=null){
				listStreamedContent.add(picture1);
			}
			if(picture2!=null){
				listStreamedContent.add(picture2);
			}
			if(picture3!=null){
				listStreamedContent.add(picture3);
			}
			if(picture4!=null){
				listStreamedContent.add(picture4);
			}
			if(pictureTail!=null){
				listStreamedContent.add(pictureTail);
			}
			result=service.savePccReport(this.selected, listStreamedContent);
		}
		else{
			result=service.savePccReport(this.selected, null);
		}
		if(result){
			blnNewUpload=false;
			addMessage("PCC 探头检测报告保存成功！");
		}
		else{
			addMessage("PCC 探头检测报告保存失败！");
		}
	}
	
	public void submit(){
		boolean passValid=true;
		if(StringUtils.isEmpty(this.selected.getLineStatus())){
		    WebUtil.addErrorMessage("线缆" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("lineStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getMirrorStatus())){
		    WebUtil.addErrorMessage("透镜" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("mirrorStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getShellStatus())){
		    WebUtil.addErrorMessage("外壳" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("shellStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getOilStatus())){
		    WebUtil.addErrorMessage("油囊" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("oilStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getLinkerStatus())){
		    WebUtil.addErrorMessage("连接器" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("linkerStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getTipStatus())){
		    WebUtil.addErrorMessage("提示" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("tipStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getElectricLeakageStatus())){
		    WebUtil.addErrorMessage("探头漏电流检测结果" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("electricLeakageStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getFirstSeenStatus())){
		    WebUtil.addErrorMessage("所见" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("firstSeenStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getFirstTipStatus())){
		    WebUtil.addErrorMessage("提示" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("firstTipStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getResultStatus())){
		    WebUtil.addErrorMessage("检测结果" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("resultStatus", true);
		    passValid=false;
		}
		else if(StringUtils.isEmpty(this.selected.getRecommandStatus())){
		    WebUtil.addErrorMessage("建议" + WebUtil.getMessage("ValidationRequire"));
		    RequestContext.getCurrentInstance().addCallbackParam("recommandStatus", true);
		    passValid=false;
		}
        
		if(picture1==null){
			addMessage("图片1缺失，请上传！");
			return;
		}
		else if(picture2==null ){
			addMessage("图片2缺失，请上传！");
			return;
		}
		else if(picture3==null){
			addMessage("图片3缺失，请上传！");
			return;
		}
		else if(picture4==null){
			addMessage("图片4缺失，请上传！");
			return;
		}
		else if(pictureTail ==null){
			addMessage("尾页图片缺失，请上传！");
			return;
		}
		if(!passValid){
			return;
		}
		this.selected.setStatus(2);
		save();
	}
	public void delete() {
		// TODO Auto-generated method stub
//		super.delete();
		if(this.listSelected==null ||this.listSelected.size()==0){
			addMessage("请选择需要删除的报告");
			return;
		}
		if(this.listSelected!=null && this.listSelected.size()>1){
			addMessage("请逐一删除报告");
			return;
		}
		service.deletePccReport(this.listSelected.get(0));
		addMessage("PCC 探头检测报告删除成功！");
	}
	
	public void prepareCreate() {
		// TODO Auto-generated method stub
		selected=new PccExamineReport();
		selected.setTenantUID(currentUser.getTenantUID());
		selected.setInstitutionUID(currentUser.getInstitutionUID());
		selected.setHospitalUID(currentUser.getHospitalUID());
		selected.setSiteUID(currentUser.getSiteUID());
		selected.setCreateBy(currentUser);
		selected.setCreateByUid(currentUser.getUid());
		selected.setCreateDate(new Date());
	}
	public void prepareEdit() {
		// TODO Auto-generated method stub
		parseBlobObject();
		parseObject();
	}
	private void parseObject(){
		selectLineStatus=convertStrToDropDownList(this.selected.getLineStatus());
		selectMirrorStatus=convertStrToDropDownList(this.selected.getMirrorStatus());
		selectShellStatus=convertStrToDropDownList(this.selected.getShellStatus());
		selectLinkerStatus=convertStrToDropDownList(this.selected.getLinkerStatus());
		selectOilStatus=convertStrToDropDownList(this.selected.getOilStatus());
		selectTipStatus=convertStrToDropDown(this.selected.getTipStatus());
		selectElectricLeakageStatus=convertStrToDropDown(this.selected.getElectricLeakageStatus());
		selectFirstSeenStatus=convertStrToDropDownList(this.selected.getFirstSeenStatus());
		selectFirstTipStatus=convertStrToDropDown(this.selected.getFirstTipStatus());
		selectResultStatus=convertStrToDropDownList(this.selected.getResultStatus());
		selectRecommandStatus=convertStrToDropDown(this.selected.getRecommandStatus());
	}
	private void parseBlobObject(){
		String id=this.selected.getId();
		if(StringUtils.isNotEmpty(id)){
			this.listStreamedContent=service.parseBlobObjectImg(id);
			if(listStreamedContent!=null && listStreamedContent.size()>0){
				for(int i=0;i<listStreamedContent.size();++i){
					ApmStreamedContent tempContent=listStreamedContent.get(i);
					if(tempContent.getMark().equals("picture1")){
						picture1=tempContent;
					}
					else if(tempContent.getMark().equals("picture2")){
						picture2=tempContent;
					}
					else if(tempContent.getMark().equals("picture3")){
						picture3=tempContent;
					}
					else if(tempContent.getMark().equals("picture4")){
						picture4=tempContent;
					}
					else if(tempContent.getMark().equals("pictureTail")){
						pictureTail=tempContent;
					}
				}
			}
			this.reportFile=service.parseBlobObjectDoc(id);
		}
	}
	private void prepareSave(){
//		this.selected.setAssetUid(tempInfo.getUid());
		this.selected.setLineStatus(convertDropDownListToStr(selectLineStatus));
		this.selected.setMirrorStatus(convertDropDownListToStr(selectMirrorStatus));
		this.selected.setShellStatus(convertDropDownListToStr(selectShellStatus));
		this.selected.setLinkerStatus(convertDropDownListToStr(selectLinkerStatus));
		this.selected.setOilStatus(convertDropDownListToStr(selectOilStatus));
		this.selected.setTipStatus(convertDropDownToStr(selectTipStatus));
		this.selected.setElectricLeakageStatus(convertDropDownToStr(selectElectricLeakageStatus));
		this.selected.setFirstSeenStatus(convertDropDownListToStr(selectFirstSeenStatus));
		this.selected.setFirstTipStatus(convertDropDownToStr(selectFirstTipStatus));
		this.selected.setResultStatus(convertDropDownListToStr(selectResultStatus));
		this.selected.setRecommandStatus(convertDropDownToStr(selectRecommandStatus));
		this.selected.setModifyBy(currentUser);
		this.selected.setModifyByUid(currentUser.getUid());
		this.selected.setModifyDate(new Date());
	}
	private List<ApmDropDownItem> convertStrToDropDownList(String str){
		if(StringUtils.isNotEmpty(str)){
			List<ApmDropDownItem> listItem=new ArrayList<ApmDropDownItem>();
			String[] arr=str.split(",");
			for(int i=0;i<arr.length;++i){
				ApmDropDownItem item=new ApmDropDownItem();
				item.setKey(arr[i]);
				item.setLabel(arr[i]);
				listItem.add(item);
			}
			return listItem;
		}
		return null;
	}
	private ApmDropDownItem convertStrToDropDown(String str){
		if(StringUtils.isNotEmpty(str)){
			String[] arr=str.split(",");
			ApmDropDownItem item=null;
			for(int i=0;i<arr.length;++i){
				item=new ApmDropDownItem();
				item.setKey(arr[i]);
				item.setLabel(arr[i]);
			}
			return item;
		}
		return null;
	}
	private String convertDropDownListToStr(List<ApmDropDownItem> listItem){
		StringBuilder sb=new StringBuilder();
		if(listItem!=null && listItem.size()>0){
			for(int i=0;i<listItem.size();++i){
				ApmDropDownItem tempItem=listItem.get(i);
				sb.append(tempItem.getKey());
				sb.append(",");
			}
		}
		return sb.toString();
	}
	private String convertDropDownToStr(ApmDropDownItem tempItem){
		if(tempItem!=null){
			return tempItem.getKey();
		}
		return "";
	}
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public String getEditPage(String id, String actionName) {
        String operation = "pccReportEdit.xhtml?actionName="+actionName+"&selectedid=" + id;
        return UrlEncryptController.encodeUrlParam(operation);
    }
	public String jumpCreatePage(){
		try {
			prepareCreate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		return "pccReportEidt?faces-redirect=false";
	}
	public String jumpEditPage(){
		prepareEdit();
		return "pccReportEidt?faces-redirect=true";
	}
	public String jumpListPage(){
		return "pccReportList?faces-redirect=true";
	}
	
	public void deleteImg(int index){
		blnNewUpload=true;
		String storageId=listStreamedContent.get(index).getId();
		if(StringUtils.isNotEmpty(storageId)){
			service.deleteFileByStorageId(storageId);
		}
		listStreamedContent.remove(index);
	}
	public void generateReport(){
		if(StringUtils.isEmpty(this.selected.getId())){
			addMessage("请完善报告信息！");
			return;
		}
		reportFile=service.generateReport_bak(this.selected,this.listStreamedContent);
		if(reportFile==null){
			addMessage("请上传报告传模板");
		}
		else{
			addMessage("报告已生成！");
		}
	}
	public void upload() {
		if (file != null) {
			try {
				ApmStreamedContent e = new ApmStreamedContent(file.getInputstream(), file.getContentType(), file.getFileName());
				addMessage(file.getFileName() + " 上传成功.");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		}
	}
	public void handleFileUpload1(FileUploadEvent event) {
		blnNewUpload=true;
		picture1=null;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				picture1 = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				picture1.setMark("picture1");
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	public void handleFileUpload2(FileUploadEvent event) {
		blnNewUpload=true;
		picture2=null;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				picture2 = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				picture2.setMark("picture2");
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	public void handleFileUpload3(FileUploadEvent event) {
		blnNewUpload=true;
		picture3=null;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				picture3 = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				picture3.setMark("picture3");
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	public void handleFileUpload4(FileUploadEvent event) {
		blnNewUpload=true;
		picture4=null;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				picture4 = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				picture4.setMark("picture4");
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	
	public void handleTailUpload(FileUploadEvent event){
		blnNewUpload=true;
		pictureTail=null;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				pictureTail = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				pictureTail.setMark("pictureTail");
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	
	private void setSelected(String id) {
        selected = dao.findById(id);
    }
	public UserAccount getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}
	public I18nMessageRepository getI18nDao() {
		return i18nDao;
	}
	public void setI18nDao(I18nMessageRepository i18nDao) {
		this.i18nDao = i18nDao;
	}
	public String getUpdateViewIDs() {
		return updateViewIDs;
	}
	public void setUpdateViewIDs(String updateViewIDs) {
		this.updateViewIDs = updateViewIDs;
	}
	public UaaService getUaaService() {
		return uaaService;
	}
	public void setUaaService(UaaService uaaService) {
		this.uaaService = uaaService;
	}
	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	public List<ApmStreamedContent> getListStreamedContent() {
		return listStreamedContent;
	}
	public void setListStreamedContent(List<ApmStreamedContent> listStreamedContent) {
		this.listStreamedContent = listStreamedContent;
	}
	public List<ApmDropDownItem> fetchLineStatus(){
		return service.fetchLineStatus();
	}
	public List<ApmDropDownItem> fetchMirrorStatus(){
		return service.fetchMirrorStatus();
	}
	public List<ApmDropDownItem> fetchShellStatus(){
		return service.fetchShellStatus();
	}
	public List<ApmDropDownItem> fetchLinkerStatus(){
		return service.fetchLinkerStatus();
	}
	public List<ApmDropDownItem> fetchOilStatus(){
		return service.fetchOilStatus();
	}
	public List<ApmDropDownItem> fetchTipStatus(){
		return service.fetchTipStatus();
	}
	public List<ApmDropDownItem> fetchElectricLeakageStatus(){
		return service.fetchElectricLeakageStatus();
	}
	public List<ApmDropDownItem> fetchFirstSeenStatus(){
		return service.fetchFirstSeenStatus();
	}
	public List<ApmDropDownItem> fetchFirstTipStatus(){
		return service.fetchFirstTipStatus();
	}
	public List<ApmDropDownItem> fetchResultStatus(){
		return service.fetchResultStatus();
	}
	public List<ApmDropDownItem> fetchRecommandStatus(){
		return service.fetchRecommandStatus();
	}
	
	public ApmStreamedContent getReportFile() {
		return reportFile;
	}
	public void setReportFile(ApmStreamedContent reportFile) {
		this.reportFile = reportFile;
	}
	public List<PccExamineReport> getListSelected() {
		return listSelected;
	}
	public void setListSelected(List<PccExamineReport> listSelected) {
		if(listSelected!=null){
		}
		
		if(listSelected!=null && listSelected.size()==1){
			this.selected=listSelected.get(0);
		}
		else{
			this.selected=null;
		}
		this.listSelected = listSelected;
	}
	public PccExamineReport getSelected() {
		return selected;
	}
	public void setSelected(PccExamineReport selected) {
		this.selected = selected;
	}
	public ApmStreamedContent getPicture1() {
		return picture1;
	}
	public void setPicture1(ApmStreamedContent picture1) {
		this.picture1 = picture1;
	}
	public ApmStreamedContent getPicture2() {
		return picture2;
	}
	public void setPicture2(ApmStreamedContent picture2) {
		this.picture2 = picture2;
	}
	public ApmStreamedContent getPicture3() {
		return picture3;
	}
	public void setPicture3(ApmStreamedContent picture3) {
		this.picture3 = picture3;
	}
	public ApmStreamedContent getPicture4() {
		return picture4;
	}
	public void setPicture4(ApmStreamedContent picture4) {
		this.picture4 = picture4;
	}
	public ApmStreamedContent getPictureTail() {
		return pictureTail;
	}
	public void setPictureTail(ApmStreamedContent pictureTail) {
		this.pictureTail = pictureTail;
	}
}
