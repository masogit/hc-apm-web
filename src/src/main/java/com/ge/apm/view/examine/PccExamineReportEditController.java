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
	private ApmStreamedContent tailAttach;
	private List<PccExamineReport> listSelected;
	private PccExamineReport selected;
	private boolean blnNewUpload=false;
	String updateViewIDs;
	UaaService uaaService;
	AssetInstallationService aiService;
	String contractInfoSiteUID;
	PccExamineReportService service;
	PccExamineReportRepository dao;
	SearchController searchController;
	@PostConstruct
	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		uaaService = WebUtil.getBean(UaaService.class);
		aiService = WebUtil.getBean(AssetInstallationService.class);
		service = WebUtil.getBean(PccExamineReportService.class);
		dao = WebUtil.getBean(PccExamineReportRepository.class);
		searchController = WebUtil.getBean(SearchController.class);
		searchController.setType(2);
		searchController.updateData();
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
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage());
				} 
	        } else if ("Edit".equalsIgnoreCase(actionName)) {
	            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
	            setSelected(selectedId);
	            prepareEdit();
	        }
		}
		
	}
	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub
	}
	public void save() {
		// TODO Auto-generated method stub
		prepareSave();
		boolean result=false;
		if(blnNewUpload){
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
		this.searchController.setSelectedAssetInfo(this.selected.getAssetInfo());
	}
	private void parseBlobObject(){
		String id=this.selected.getId();
		if(StringUtils.isNotEmpty(id)){
			this.listStreamedContent=service.parseBlobObjectImg(id);
			this.reportFile=service.parseBlobObjectDoc(id);
		}
	}
	private void prepareSave(){
		AssetInfo tempInfo=searchController.getSelectedAssetInfo();
		if(tempInfo!=null){
			this.selected.setAssetInfo(tempInfo);
			this.selected.setAssetUid(tempInfo.getUid());
			if(StringUtils.isEmpty(this.selected.getNum())){
				this.selected.setNum(tempInfo.getDepartNum());
			}
			if(this.selected.getManufactDate()==null){
				this.selected.setManufactDate(tempInfo.getManufactDate());
			}
			if(StringUtils.isEmpty(this.selected.getFunctionType())){
				this.selected.setFunctionType(tempInfo.getFunctionType());
			}
		}
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
		String strStatus=this.selected.getRecommandStatus();
		if(StringUtils.isNotEmpty(strStatus)){
			if(strStatus.contains("2")){
				this.selected.setStatus(2);
			}
			else{
				this.selected.setStatus(1);
			}
		}
		else{
			this.selected.setStatus(2);
		}
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
		reportFile=service.generateReport(this.selected,this.listStreamedContent);
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
				if (listStreamedContent.size() > 4) {
					listStreamedContent.remove(0);
				}
				listStreamedContent.add(e);
				addMessage(file.getFileName() + " 上传成功.");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		}
	}
	public void handleFileUpload(FileUploadEvent event) {
		blnNewUpload=true;
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				ApmStreamedContent e = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				listStreamedContent.add(e);
				while (listStreamedContent.size() > 4) {
					listStreamedContent.remove(0);
				}
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
		// V2_BlobObject blobObject = service.uploadFile(event);
		// String type = event.getComponent().getId();
		// type = type.substring(type.indexOf("type") + 4);
		// if (null != blobObject) {
		// attachList.add(blobObject);
		// blobObject.setBoType(V2_BlobObject.BO_TYPE_ArrivalList);
		// blobObject.setBoUuid(selected.getId());
		// blobObject.setBoSubType(type);
		// blobService.saveBlobObject(blobObject);
		// WebUtil.addSuccessMessage(blobObject.getObjectName() +
		// WebUtil.getMessage("SuccessUploaded"));
		// } else {
		// WebUtil.addErrorMessage("fail to upload file");
		// }
	}
	
	public void handleTailUpload(FileUploadEvent event){
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				tailAttach = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
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
	public ApmStreamedContent getTailAttach() {
		return tailAttach;
	}
	public void setTailAttach(ApmStreamedContent tailAttach) {
		this.tailAttach = tailAttach;
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
}
