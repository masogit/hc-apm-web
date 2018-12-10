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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.domain.ServiceRequestReport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class AssetLifecycleEventController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable, ServerEventInterface {

	protected AssetLifecycleEventRepository dao;

	protected UaaService uaaService;

	protected FileDownloadService fileDownloadService;
	protected List<AssetLifecycleEvent> listSelectedItems;
	protected StreamedContent exportFile;
	protected boolean blnAllSelected;
    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        this.filterBySite = false;
        this.filterByHospital = false;
        blnAllSelected=false;
        
    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }
    protected void addFilter(){
    	searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "3"));//3外部维修
    }
    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        addFilter();
        Page<AssetLifecycleEvent> pageResult=dao.findBySearchFilter(this.searchFilters, pageRequest);
        return pageResult;
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
    		this.listSelectedItems=new ArrayList<AssetLifecycleEvent>(listCurrentItems);
    	}
    	else{
    		this.listSelectedItems=null;
    	}
    }
    
    public void onItemSelect(){
    	listSelectedItems=new ArrayList<AssetLifecycleEvent>();
    	List<AssetLifecycleEvent> listCurrentItems=this.lazyModel.getWrappedData();
    	if(listCurrentItems!=null && listCurrentItems.size()>0){
    		for(int i=0;i<listCurrentItems.size();++i){
    			AssetLifecycleEvent tempEvent=listCurrentItems.get(i);
    			if(tempEvent.isBlnSelected()){
    				listSelectedItems.add(tempEvent);
    			}
    		}
    	}
    }

    public String getMsStatus(AssetLifecycleEvent alse) {
        String strStatus = null;
        try {
            JSONObject parse = (JSONObject) JSON.parse(alse.getDescription());
            if (parse != null) {
                strStatus = parse.getString("status");
            }
        } catch (Exception e) {
            logger.error("Fail to parse description for status: " + alse.getId(), e);
        }
        return strStatus;
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
    	if(listSelectedItems==null || listSelectedItems.size()==0){
    		WebUtil.addErrorMessage("请选择需要下载报告的工单！");
    		return;
    	}
    	Set<String> setReportIds=new HashSet<String>();
    	Map<String,String> mapReportName=new HashMap<String,String>();
//    	Map<String,Integer> mapName=new HashMap<String,Integer>();
    	for(int i=0;i<listSelectedItems.size();++i){
			List<ServiceRequestReport> listReport=listSelectedItems.get(i).getReports();
			if(listReport!=null && listReport.size()>0){
				for(int j=0;j<listReport.size();++j){
					ServiceRequestReport tempReport=listReport.get(j);
					String reportStorageId=tempReport.getStorageId();
					if(StringUtils.isNotEmpty(reportStorageId)){
						setReportIds.add(reportStorageId);
//						String tempName=tempReport.getDisplayName();
//						if(!mapName.keySet().contains(tempName)){
//							mapName.put(tempName, 1);
//						}
//						else{
//							mapName.put(tempName, mapName.get(tempName)+1);
//						}
//						mapReportName.put(reportStorageId, tempName+"_"+mapName.get(tempName));
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

	public List<AssetLifecycleEvent> getListSelectedItems() {
		return listSelectedItems;
	}

	public void setListSelectedItems(List<AssetLifecycleEvent> listSelectedItems) {
		this.listSelectedItems = listSelectedItems;
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
}