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
import org.primefaces.model.DefaultStreamedContent;
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
public class PccExamineReportConfigController  implements Serializable{
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportConfigController.class);
	protected UserAccount currentUser;
	protected I18nMessageRepository i18nDao;
	private UploadedFile file;
	private ApmStreamedContent exportFile;
	PccExamineReportService service;
	@PostConstruct
	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		service = WebUtil.getBean(PccExamineReportService.class);
		service.clear();
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
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public String getEditPage(String id, String actionName) {
        String operation = "pccReportEdit.xhtml?actionName="+actionName+"&selectedid=" + id;
        return UrlEncryptController.encodeUrlParam(operation);
    }
	
	public void upload() {
		if (file != null) {
			try {
				ApmStreamedContent e = new ApmStreamedContent(file.getInputstream(), file.getContentType(), file.getFileName());
				boolean result=service.saveFile(e, "0",V2_BlobObject.BO_TYPE_PCCReport,V2_BlobObject.BO_SUB_TYPE_DOCS);
				if(result){
					addMessage(file.getFileName() + " 上传成功.");
				}
				else{
					addMessage(file.getFileName() + " 上传失败.");
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		}
	}
	
	public void getTemplateFile(){
		this.exportFile=null;
		this.exportFile=service.getTemplate();
	}
	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				ApmStreamedContent e = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				service.deleteFile("0", V2_BlobObject.BO_TYPE_PCCReport, V2_BlobObject.BO_SUB_TYPE_DOCS);
				boolean result=service.saveFile(e, "0",V2_BlobObject.BO_TYPE_PCCReport,V2_BlobObject.BO_SUB_TYPE_DOCS);
				if(result){
					addMessage(tempFile.getFileName() + " 上传成功！");
				}
				else{
					addMessage(tempFile.getFileName() + " 上传失败！");
				}
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
	public UploadedFile getFile() {
		return file;
	}
	public void setFile(UploadedFile file) {
		this.file = file;
	}
	public ApmStreamedContent getExportFile() {
		return exportFile;
	}
	public void setExportFile(ApmStreamedContent exportFile) {
		this.exportFile = exportFile;
	}
	
}
