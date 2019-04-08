package com.ge.apm.view.wo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.ge.apm.view.sysutil.UrlEncryptController;
import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.domain.ServiceRequestReport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.wo.AssetLifecycleEventService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 *
 */
public class AssetLifecycleEventController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable, ServerEventInterface {
	protected static final Logger LOGGER = LoggerFactory.getLogger(AssetLifecycleEventController.class);
	protected AssetLifecycleEventRepository dao;

	protected UaaService uaaService;
	protected AssetLifecycleEventService service;
	protected FileDownloadService fileDownloadService;
	protected Set<AssetLifecycleEvent> setSelectedItems;
	protected StreamedContent exportFile;
	protected boolean blnAllSelected;
	protected int tabStatus;
	protected String descriptionStatusFilter;

	protected String queryAssetUid;

    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        service=WebUtil.getBean(AssetLifecycleEventService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
        blnAllSelected=false;
        setSelectedItems=new HashSet<AssetLifecycleEvent>();
        tabStatus=3;

		String enCodeParameters = WebUtil.getRequestParameter("str");
		if (!Strings.isNullOrEmpty(enCodeParameters)) {
			queryAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
		}
    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }
    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        if (!this.hasFilterOnField("eventType")) {
        	searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, tabStatus));//3外部维修
        }
        Page<AssetLifecycleEvent> pageResult=dao.findBySearchFilter(this.searchFilters, pageRequest);
        updateSelectStatus(pageResult);
        return pageResult;
    }
    
    private void updateSelectStatus(Page<AssetLifecycleEvent> pageResult){
    	boolean tempAllSelect=false;
    	if(pageResult!=null){
        	List<AssetLifecycleEvent> tempList=pageResult.getContent();
        	if(tempList!=null && tempList.size()>0){
        		tempAllSelect=true;
        		for(int i=0;i<tempList.size();++i){
        			AssetLifecycleEvent tempEvent=tempList.get(i);
        			if(this.setSelectedItems.contains(tempEvent)){
        				tempEvent.setBlnSelected(true);
        			}
        			else{
        				tempAllSelect=false;
        			}
        		}
        	}
        	setBlnAllSelected(tempAllSelect);
        }
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (StringUtils.isNotEmpty(queryAssetUid)) {
			searchFilters.add(new SearchFilter("assetInfo.uid", SearchFilter.Operator.EQ, queryAssetUid, "1*"));

		}
    }

    public void onSelectAllItemsEvent() {
    	List<AssetLifecycleEvent> listCurrentItems=this.lazyModel.getWrappedData();
    	if(listCurrentItems!=null && listCurrentItems.size()>0){
    		for(int i=0;i<listCurrentItems.size();++i){
    			AssetLifecycleEvent tempEvent=listCurrentItems.get(i);
    			tempEvent.setBlnSelected(blnAllSelected);
    		}
    	}
    	if(blnAllSelected){
    		this.setSelectedItems.addAll(listCurrentItems);
    	}
    	else{
    		this.setSelectedItems.removeAll(listCurrentItems);
    	}
    }
    
    public void onItemSelect(AssetLifecycleEvent tempEvent){
    	if(tempEvent.isBlnSelected()){
    		this.setSelectedItems.add(tempEvent);
    	}
    	else{
    		this.setSelectedItems.remove(tempEvent);
    	}
    }
    
    private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str,null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void exportSelected(){
		this.exportFile=null;
		if(setSelectedItems==null || setSelectedItems.size()==0){
			downloadFile(service.parseEventToMap(dao.findBySearchFilter(this.searchFilters),tabStatus));
		}
		else{
			downloadFile(service.parseEventToMap(setSelectedItems,tabStatus));
		}
	}
    
    protected void downloadFile(List<Map<String,Object>> data){
		try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/woMgr/woMgrOutList_export.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            Boolean exportFlag = service.exportData(doc, data);
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "woMgr_export.xlsx");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
	}

    public String getMsStatus(AssetLifecycleEvent alse) {
        return service.getMsStatus(alse);
    }

    public StreamedContent getAnonymousReport(String reportStorageId) {
        try {
            StreamedContent file = fileDownloadService.getFile(reportStorageId);
            String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getName());
            String contentType = file.getContentType();
            InputStream stream = file.getStream();
            byte[] bytes = IOUtils.toByteArray(stream);
            return new DefaultStreamedContent((new ByteArrayInputStream(bytes)), contentType, fileName);
        } catch (IOException e) {
            WebUtil.addErrorMessage("Fail to download report:" + e.getMessage());
        }
        return null;
    }
    public void downloadSelectedReport(){
    	this.exportFile=null;
    	if(setSelectedItems==null || setSelectedItems.size()==0){
    		WebUtil.addErrorMessage("请选择需要下载报告的工单！");
    		return;
    	}
    	Set<String> setReportIds=new HashSet<String>();
    	Map<String,String> mapReportName=new HashMap<String,String>();
//    	Map<String,Integer> mapName=new HashMap<String,Integer>();
    	Iterator<AssetLifecycleEvent> itEvent=setSelectedItems.iterator();
		while(itEvent.hasNext()){
			AssetLifecycleEvent tempEvent=itEvent.next();
			List<ServiceRequestReport> listReport=tempEvent.getReports();
			if(listReport!=null && listReport.size()>0){
				for(int j=0;j<listReport.size();++j){
					ServiceRequestReport tempReport=listReport.get(j);
					String reportStorageId=tempReport.getStorageId();
					if(StringUtils.isNotEmpty(reportStorageId)){
						setReportIds.add(reportStorageId);
						mapReportName.put(reportStorageId, tempReport.getFileName());
					}
				}
			}
		}
    	if(setReportIds.size()==0){
    		WebUtil.addErrorMessage("工单报告为空！");
    		return;
    	}
    	try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)){
    		Iterator<String> it=setReportIds.iterator();
    		while(it.hasNext()){
    			String reportStorageId=it.next();
    			StreamedContent file = fileDownloadService.getFile(reportStorageId);
                String fileName = mapReportName.get(reportStorageId);
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

	public Set<AssetLifecycleEvent> getSetSelectedItems() {
		return setSelectedItems;
	}

	public void setSetSelectedItems(Set<AssetLifecycleEvent> setSelectedItems) {
		this.setSelectedItems = setSelectedItems;
	}

	public StreamedContent getExportFile() {
		return exportFile;
	}

	public void setExportFile(StreamedContent exportFile) {
		this.exportFile = exportFile;
	}
	

	public boolean isBlnAllSelected() {
		return blnAllSelected;
	}

	public void setBlnAllSelected(boolean blnAllSelected) {
		this.blnAllSelected = blnAllSelected;
	}

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		// TODO Auto-generated method stub
		
	}
	public String getDescriptionStatusFilter() {
		return descriptionStatusFilter;
	}

	public void setDescriptionStatusFilter(String descriptionStatusFilter) {
		this.descriptionStatusFilter = descriptionStatusFilter;
	}
	
}