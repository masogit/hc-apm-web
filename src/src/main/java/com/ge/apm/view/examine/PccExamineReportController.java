package com.ge.apm.view.examine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.mapper.PccExamineReportMapper;
import com.ge.apm.domain.ApmDropDownItem;
import com.ge.apm.domain.ApmStreamedContent;
import com.ge.apm.domain.PccExamineReport;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInstallationService;
import com.ge.apm.service.examine.PccExamineReportService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;

import lombok.Getter;
import lombok.Setter;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2018/5/8.
 */
@ManagedBean
@ViewScoped
public class PccExamineReportController extends MyCRUDController<PccExamineReport> implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(PccExamineReportController.class);

	@Getter
	@Setter
	private List<ApmDropDownItem> selectLineStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectMirrorStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectShellStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectLinkerStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectOilStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectTipStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectElectricLeakageStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectFirstSeenStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectFirstTipStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectResultStatus;
	@Getter
	@Setter
	private List<ApmDropDownItem> selectRecommandStatus;
	protected UserAccount currentUser;
	protected I18nMessageRepository i18nDao;
	private UploadedFile file;
	private ApmStreamedContent reportFile;
	private List<ApmStreamedContent> listStreamedContent;
	private ApmStreamedContent tailAttach;
	private List<PccExamineReport> listSelected;
	private Map<Integer,List<PccExamineReport>> mapListSelected;
	private DefaultStreamedContent exportFile;
	private boolean blnEditable = false;
	String updateViewIDs;
	UaaService uaaService;
	AssetInstallationService aiService;
	String contractInfoSiteUID;
	PccExamineReportService service;
	PccExamineReportMapper dao;
	Integer statusFilter;
	String recommandStatusFilter;
	private List<SearchFilter> listNewSearchFilter;
	private List<PccExamineReport> listDetail;
	private List<String> userRoleNames;
	private Set<String> setUserUids;
	private int currentPage;

	protected void init() {
		currentUser = UserContextService.getCurrentUserAccount();
		userRoleNames = currentUser.getRoleNames();
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		uaaService = WebUtil.getBean(UaaService.class);
		aiService = WebUtil.getBean(AssetInstallationService.class);
		service = WebUtil.getBean(PccExamineReportService.class);
		service.clear();
		dao = WebUtil.getBean(PccExamineReportMapper.class);
		if (listStreamedContent == null) {
			listStreamedContent = new ArrayList<ApmStreamedContent>();
		}
		if (userRoleNames.contains("AssetStaff") || currentUser.getIsSuperAdmin()) {
			blnEditable = true;
		}
		setUserUids = service.findAccessOrgUidListByUserUid(currentUser.getUid());
		mapListSelected=new HashMap<Integer,List<PccExamineReport>>();
	}

	@Override
	protected List<PccExamineReport> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
		if (this.searchFilters == null) {
			this.searchFilters = new HashSet<SearchFilter>();
		}
		currentPage=pageRequest.getCurrent();
		if(mapListSelected.containsKey(currentPage)){
			listSelected=mapListSelected.get(currentPage);
		}
		// need list or set
		searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, setUserUids, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, setUserUids, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, setUserUids, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, setUserUids, "1*2+"));
		searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, setUserUids, "1*2+"));
		// if(userRoleNames.contains("AssetHead")||userRoleNames.contains("AssetStaff")){
		// searchFilters.add(new SearchFilter("assetInfo.tenantUID",
		// SearchFilter.Operator.EQ, currentUser.getTenantUID(), "1*"));
		// }
		// else{
		// searchFilters.add(new SearchFilter("assetInfo.siteUID",
		// SearchFilter.Operator.EQ, currentUser.getSiteUID(), "1*"));
		// }
		// updateFilter();
		this.lazyModel.setRowCount(dao.getAllCount(searchFilters));
		listDetail = dao.getAll(searchFilters, sortField, isAscending, pageRequest.getLimit(), pageRequest.getOffset());
		return listDetail;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub
	}

	@Override
	protected BaseMapper<PccExamineReport> getDAO() {
		// TODO Auto-generated method stub
		return dao;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		// super.save();
		service.savePccReport(this.selected, listStreamedContent);
		addMessage("PCC 探头检测报告保存成功！");
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		// super.delete();
		if (this.listSelected == null || this.listSelected.size() == 0) {
			addMessage("请选择需要删除的报告");
			return;
		}
		if (this.listSelected != null && this.listSelected.size() > 1) {
			addMessage("请逐一删除报告");
			return;
		}
		service.deletePccReport(this.listSelected.get(0));
		addMessage("PCC 探头检测报告删除成功！");
		this.listSelected = null;
		this.selected = null;
	}

	public void delete(PccExamineReport pcc) {
		service.deletePccReport(pcc);
		addMessage("PCC 探头检测报告删除成功！");
		this.listSelected = null;
		this.selected = null;
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
	}

	private void updateFilter() {
		Iterator<SearchFilter> it = searchFilters.iterator();
		Set<SearchFilter> copySearchFilter = new HashSet<SearchFilter>();
		while (it.hasNext()) {
			SearchFilter tempFilter = it.next();
			if (tempFilter.fieldName.equals("assetInfo.siteUID")) {
				continue;
			} else if (tempFilter.fieldName.equals("assetInfo.hospitalUID")) {
				continue;
			} else if (tempFilter.fieldName.equals("assetInfo.institutionUID")) {
				continue;
			} else if (tempFilter.fieldName.equals("assetInfo.tenantUID")) {
				continue;
			} else {
				copySearchFilter.add(tempFilter);
			}
		}
		if (listNewSearchFilter != null) {
			copySearchFilter.addAll(listNewSearchFilter);
		}
		searchFilters = copySearchFilter;
	}

	private List<ApmDropDownItem> convertStrToDropDownList(String str) {
		if (StringUtils.isNotEmpty(str)) {
			List<ApmDropDownItem> listItem = new ArrayList<ApmDropDownItem>();
			String[] arr = str.split(",");
			for (int i = 0; i < arr.length; ++i) {
				ApmDropDownItem item = new ApmDropDownItem();
				item.setKey(arr[i]);
				item.setLabel(arr[i]);
				listItem.add(item);
			}
			return listItem;
		}
		return null;
	}

	private String convertDropDownListToStr(List<ApmDropDownItem> listItem) {
		StringBuilder sb = new StringBuilder();
		if (listItem != null && listItem.size() > 0) {
			for (int i = 0; i < listItem.size(); ++i) {
				ApmDropDownItem tempItem = listItem.get(i);
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
		String operation = "pccReportEdit.xhtml?actionName=" + actionName + "&selectedid=" + id;
		return UrlEncryptController.encodeUrlParam(operation);
	}
	
	public void onPageSelect(PageEvent event){
		if(listSelected!=null && listSelected.size()>0){
			mapListSelected.put(currentPage, listSelected);
		}
	}

	public String getEditPage(String id, String assetuid, String actionName) {
		String operation = "pccReportEdit.xhtml?actionName=" + actionName + "&selectedid=" + id + "&assetuid=" + assetuid;
		return UrlEncryptController.encodeUrlParam(operation);
	}

	public String jumpCreatePage() {
		try {
			super.prepareCreate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return "pccReportEidt?faces-redirect=false";
	}

	public String jumpEditPage() {
		super.prepareEdit();
		return "pccReportEidt?faces-redirect=true";
	}

	public String jumpListPage() {
		return "pccReportList?faces-redirect=true";
	}

	public void generateReport_bak() {
		reportFile = service.generateReport(this.selected);
		if (reportFile != null) {
			addMessage("报告已生成！");
		} else {
			addMessage("报告生成失败！");
		}
	}

	public void generateReport(PccExamineReport pcc) {
		reportFile = null;
		reportFile = service.generateReport(pcc);
		if (reportFile != null) {
			addMessage("报告已生成！");
		} else {
			addMessage("报告生成失败！");
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
		UploadedFile tempFile = event.getFile();
		if (tempFile != null) {
			try {
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

	public void handleTailUpload(FileUploadEvent event) {
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

	public void batchGenerateReport() {
		if (listSelected == null || listSelected.size() == 0) {
			addMessage("请选择需要批量生成报告的项！");
			return;
		}
		boolean result = service.batchGenerateReport_bak(listSelected);
		if (result) {
			addMessage("报告已批量生成！");
		} else {
			addMessage("请上传报告传模板");
		}
	}

	public void downloadSelectedReport() {
		this.exportFile = null;
		onPageSelect(null);
		List<PccExamineReport> listTotalReport=new ArrayList<PccExamineReport>();
		if (mapListSelected == null || mapListSelected.size() == 0) {
			WebUtil.addErrorMessage("请选择需要下载报告的工单！");
			return;
		}
		for(List<PccExamineReport> listTemp:mapListSelected.values()){
			listTotalReport.addAll(listTemp);
		}
		Set<PccExamineReport> setReportIds = new HashSet<PccExamineReport>();
		// Map<String,Integer> mapName=new HashMap<String,Integer>();
		for (int i = 0; i < listTotalReport.size(); ++i) {
			PccExamineReport tempPcc = listTotalReport.get(i);
			String tempBoId = tempPcc.getId();
			Integer status = tempPcc.getStatus();
			if (status != null && status == 2) {
				setReportIds.add(tempPcc);
			}
		}
		if (setReportIds.size() == 0) {
			WebUtil.addErrorMessage("工单报告为空！");
			return;
		}
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)) {
			Iterator<PccExamineReport> it = setReportIds.iterator();
			int count = 1;
			while (it.hasNext()) {
				PccExamineReport pcc = it.next();
				ApmStreamedContent file = service.generateReport(pcc);
				String fileName = file.getName();
				int index = fileName.lastIndexOf(".");
				fileName = fileName.substring(0, index) + "_" + (count++) + fileName.substring(index, fileName.length());
				InputStream stream = file.getStream();
				zout.putNextEntry(new ZipEntry(fileName));
				byte[] bytes = IOUtils.toByteArray(stream);
				if (bytes != null) {
					IOUtils.write(bytes, zout);
				}
				zout.closeEntry();
			}
			zout.close();
			exportFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "Reports.zip");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			WebUtil.addErrorMessage("下载报告失败！");
		}
	}

	public void downloadSelectedReport_bak() {
		this.exportFile = null;
		if (listSelected == null || listSelected.size() == 0) {
			WebUtil.addErrorMessage("请选择需要下载报告的工单！");
			return;
		}
		Set<String> setReportIds = new HashSet<String>();
		// Map<String,Integer> mapName=new HashMap<String,Integer>();
		for (int i = 0; i < listSelected.size(); ++i) {
			PccExamineReport tempReport = listSelected.get(i);
			String reportStorageId = tempReport.getReportStorageId();
			if (StringUtils.isNotEmpty(reportStorageId)) {
				setReportIds.add(reportStorageId);
			}
		}
		if (setReportIds.size() == 0) {
			WebUtil.addErrorMessage("工单报告为空！");
			return;
		}
		try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)) {
			Iterator<String> it = setReportIds.iterator();
			int count = 1;
			while (it.hasNext()) {
				String reportStorageId = it.next();
				StreamedContent file = service.getSingleFile(reportStorageId);
				String fileName = file.getName();
				fileName = new String(fileName.getBytes("iso-8859-1"), "utf-8");
				int index = fileName.lastIndexOf(".");
				fileName = fileName.substring(0, index) + "_" + (count++) + fileName.substring(index, fileName.length());
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
			exportFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "Reports.zip");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			WebUtil.addErrorMessage("下载报告失败！");
		}
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

	public List<ApmDropDownItem> fetchLineStatus() {
		return service.fetchLineStatus();
	}

	public List<ApmDropDownItem> fetchMirrorStatus() {
		return service.fetchMirrorStatus();
	}

	public List<ApmDropDownItem> fetchShellStatus() {
		return service.fetchShellStatus();
	}

	public List<ApmDropDownItem> fetchLinkerStatus() {
		return service.fetchLinkerStatus();
	}

	public List<ApmDropDownItem> fetchOilStatus() {
		return service.fetchOilStatus();
	}

	public List<ApmDropDownItem> fetchTipStatus() {
		return service.fetchTipStatus();
	}

	public List<ApmDropDownItem> fetchElectricLeakageStatus() {
		return service.fetchElectricLeakageStatus();
	}

	public List<ApmDropDownItem> fetchFirstSeenStatus() {
		return service.fetchFirstSeenStatus();
	}

	public List<ApmDropDownItem> fetchFirstTipStatus() {
		return service.fetchFirstTipStatus();
	}

	public List<ApmDropDownItem> fetchResultStatus() {
		return service.fetchResultStatus();
	}

	public List<ApmDropDownItem> fetchRecommandStatus() {
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
		if (listSelected != null) {
		}

		if (listSelected != null && listSelected.size() == 1) {
			this.selected = listSelected.get(0);
		} else {
			this.selected = null;
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

	public String getRecommandStatusFilter() {
		return recommandStatusFilter;
	}

	public void setRecommandStatusFilter(String recommandStatusFilter) {
		this.recommandStatusFilter = recommandStatusFilter;
	}

	public boolean isBlnEditable() {
		return blnEditable;
	}

	public void setBlnEditable(boolean blnEditable) {
		this.blnEditable = blnEditable;
	}

}
