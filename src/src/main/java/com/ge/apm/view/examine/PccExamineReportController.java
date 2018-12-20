package com.ge.apm.view.examine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.PccExamineReportRepository;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.UserAccount;
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
public class PccExamineReportController extends GenericCRUDUUIDController<PccExamineReport> implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportController.class);
	
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
	private List<ApmDropDownItem> selectTipStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectElectricLeakageStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectFirstSeenStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectFirstTipStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectResultStatus;
	@Getter@Setter
	private List<ApmDropDownItem> selectRecommandStatus;

	protected UserAccount currentUser;
	protected I18nMessageRepository i18nDao;
	private UploadedFile file;
	private ApmStreamedContent reportFile;
	private List<ApmStreamedContent> listStreamedContent;
	private ApmStreamedContent tailAttach;
	private List<PccExamineReport> listSelected;
	private DefaultStreamedContent exportFile;
	String updateViewIDs;
	UaaService uaaService;
	AssetInstallationService aiService;
	String contractInfoSiteUID;
	PccExamineReportService service;
	PccExamineReportRepository dao;
	SearchController searchController;
	Integer statusFilter;

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
	}

	@Override
	protected Page<PccExamineReport> loadData(PageRequest pageRequest) {
		List<SearchFilter> searchFilters = this.searchFilters;
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
		// need list or set
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*2+"));
		searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID(), "1*2+"));
		searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID(), "1*2+"));
		searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*2+"));
		Page<PccExamineReport> pageResult = dao.findBySearchFilter(searchFilters, pageRequest);
		return pageResult;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub

	}

	@Override
	protected GenericRepositoryUUID<PccExamineReport> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		prepareSave();
//		super.save();
		service.savePccReport(this.selected, listStreamedContent);
		addMessage("PCC 探头检测报告保存成功！");
	}
	
	@Override
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
		this.listSelected=null;
		this.selected=null;
	}
	
	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
	}
	@Override
    public void onBeforeNewObject(PccExamineReport object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
        object.setCreateBy(currentUser);
        object.setCreateByUid(currentUser.getUid());
        object.setCreateDate(new Date());
    }
	@Override
	public void prepareEdit() {
		// TODO Auto-generated method stub
		super.prepareEdit();
		parseBlobObject();
		parseObject();
	}
	private void parseObject(){
		selectLineStatus=convertStrToDropDownList(this.selected.getLineStatus());
		selectMirrorStatus=convertStrToDropDownList(this.selected.getMirrorStatus());
		selectShellStatus=convertStrToDropDownList(this.selected.getShellStatus());
		selectLinkerStatus=convertStrToDropDownList(this.selected.getLinkerStatus());
		selectOilStatus=convertStrToDropDownList(this.selected.getOilStatus());
		selectTipStatus=convertStrToDropDownList(this.selected.getTipStatus());
		selectElectricLeakageStatus=convertStrToDropDownList(this.selected.getElectricLeakageStatus());
		selectFirstSeenStatus=convertStrToDropDownList(this.selected.getFirstSeenStatus());
		selectFirstTipStatus=convertStrToDropDownList(this.selected.getFirstTipStatus());
		selectResultStatus=convertStrToDropDownList(this.selected.getResultStatus());
		selectRecommandStatus=convertStrToDropDownList(this.selected.getRecommandStatus());
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
		}
		this.selected.setLineStatus(convertDropDownListToStr(selectLineStatus));
		this.selected.setMirrorStatus(convertDropDownListToStr(selectMirrorStatus));
		this.selected.setShellStatus(convertDropDownListToStr(selectShellStatus));
		this.selected.setLinkerStatus(convertDropDownListToStr(selectLinkerStatus));
		this.selected.setOilStatus(convertDropDownListToStr(selectOilStatus));
		this.selected.setTipStatus(convertDropDownListToStr(selectTipStatus));
		this.selected.setElectricLeakageStatus(convertDropDownListToStr(selectElectricLeakageStatus));
		this.selected.setFirstSeenStatus(convertDropDownListToStr(selectFirstSeenStatus));
		this.selected.setFirstTipStatus(convertDropDownListToStr(selectFirstTipStatus));
		this.selected.setResultStatus(convertDropDownListToStr(selectResultStatus));
		this.selected.setRecommandStatus(convertDropDownListToStr(selectRecommandStatus));
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
	private String convertDropDownListToStr(List<ApmDropDownItem> listItem){
		StringBuilder sb=new StringBuilder();
		if(listItem!=null && listItem.size()>0){
			for(int i=0;i<listItem.size();++i){
				ApmDropDownItem tempItem=listItem.get(i);
				System.out.println(tempItem.getLabel());
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
	public String jumpCreatePage(){
		try {
			super.prepareCreate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		return "pccReportEidt?faces-redirect=false";
	}
	public String jumpEditPage(){
		super.prepareEdit();
		return "pccReportEidt?faces-redirect=true";
	}
	public String jumpListPage(){
		return "pccReportList?faces-redirect=true";
	}
	public void generateReport(){
		if(StringUtils.isEmpty(this.selected.getId())){
			addMessage("请完善报告信息！");
			return;
		}
		reportFile=service.generateReport(this.selected,this.listStreamedContent);
		addMessage("报告已生成！");
	}
	public void upload() {
		System.out.println("upload");
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
		System.out.println("FileUploadEvent");
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
				System.out.println(tempFile.getContentType());
				ApmStreamedContent e = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				if (listStreamedContent.size() > 4) {
					listStreamedContent.remove(0);
				}
				listStreamedContent.add(e);
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			System.out.println("FileUploadEvent is null");
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
				System.out.println(tempFile.getContentType());
				tailAttach = new ApmStreamedContent(tempFile.getInputstream(), tempFile.getContentType(), tempFile.getFileName());
				addMessage(tempFile.getFileName() + " 上传成功！");
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		} else {
			System.out.println("FileUploadEvent is null");
			addMessage("上传失败！" + tempFile.getFileName() + "是空文件");
		}
	}
	
	public void batchGenerateReport(){
		if(listSelected==null && listSelected.size()==0){
			addMessage("请选择需要批量生成报告的项！");
			return;
		}
		boolean result=service.batchGenerateReport(listSelected);
		if(result){
			addMessage("报告已批量生成！");
		}
		else{
			addMessage("请上传报告传模板");
		}
	}
	
	public void downloadSelectedReport(){
        this.exportFile=null;
        if(listSelected==null || listSelected.size()==0){
            WebUtil.addErrorMessage("请选择需要下载报告的工单！");
            return;
        }
        Set<String> setReportIds=new HashSet<String>();
//      Map<String,Integer> mapName=new HashMap<String,Integer>();
        for(int i=0;i<listSelected.size();++i){
        	PccExamineReport tempReport=listSelected.get(i);
            String reportStorageId=tempReport.getReportStorageId();
            if(StringUtils.isNotEmpty(reportStorageId)){
                setReportIds.add(reportStorageId);
            }
        }
        if(setReportIds.size()==0){
            WebUtil.addErrorMessage("工单报告为空！");
            return;
        }
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)){
            Iterator<String> it=setReportIds.iterator();
            int count=1;
            while(it.hasNext()){
                String reportStorageId=it.next();
                StreamedContent file = service.getSingleFile(reportStorageId);
                String fileName = file.getName();
                int index=fileName.lastIndexOf(".");
                fileName=fileName.substring(0,index)+"_"+(count++)+fileName.substring(index,fileName.length());
                String contentType = file.getContentType();
                InputStream stream = file.getStream();
                zout.putNextEntry(new ZipEntry(fileName));
                byte[] bytes = IOUtils.toByteArray(stream);
                if (bytes != null) {
                    IOUtils.write(bytes, zout);
                }
                zout.closeEntry();
            }
            zout.close();
            exportFile=new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "Reports.zip");
        } catch (Exception e) {
            WebUtil.addErrorMessage("Fail to download report:" + e.getMessage());
        }
    }
	
	private void setSelected(String id) {
        selected = dao.findById(id);
        setSelected(selected);
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
		System.out.println("set multiple select");
		if(listSelected!=null){
			System.out.println(listSelected.size());
		}
		
		if(listSelected!=null && listSelected.size()==1){
			this.selected=listSelected.get(0);
		}
		else{
			this.selected=null;
		}
		this.listSelected = listSelected;
	}

	public DefaultStreamedContent getExportFile() {
		return exportFile;
	}

	public void setExportFile(DefaultStreamedContent exportFile) {
		this.exportFile = exportFile;
	}

	public Integer getStatusFilter() {
		return statusFilter;
	}

	public void setStatusFilter(Integer statusFilter) {
		this.statusFilter = statusFilter;
	}

	
}
