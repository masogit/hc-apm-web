package com.ge.apm.view.wo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.domain.ServiceRequestReport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEventController5 extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable {

    private AssetLifecycleEventRepository dao;

    private UaaService uaaService;

    private FileDownloadService fileDownloadService;
    private List<AssetLifecycleEvent> selecteItems;

    protected void init() {
        uaaService = WebUtil.getBean(UaaService.class);
        dao = WebUtil.getBean(AssetLifecycleEventRepository.class);
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        this.filterBySite = false;
        this.filterByHospital = false;

    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {

        this.setSearchFilter();
        this.selected = null;
        this.selecteItems=null;
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, "5"));//3外部维修
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
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

    public void onSelectAssetLifecycleEvent() {
    }

    public String getMsStatus(AssetLifecycleEvent alse) {
        String strStatus = null;
        JSONObject parse = (JSONObject) JSON.parse(alse.getDescription());
        JSONArray status = (JSONArray) parse.get("messages");
        if (status != null) {
            {
                strStatus = StringUtils.removeEnd(status.get(0).toString(), "状态: ");
                strStatus = strStatus.replace("状态：", "");
            }

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

    public StreamedContent downloadSelectedReport(){
    	if(selecteItems==null || selecteItems.size()==0){
    		WebUtil.addErrorMessage("请选择需要下载报告的工单！");
    		return null;
    	}
    	Set<String> setReportIds=new HashSet<String>();
    	for(int i=0;i<selecteItems.size();++i){
			List<ServiceRequestReport> listReport=selecteItems.get(i).getReports();
			if(listReport!=null && listReport.size()>0){
				ServiceRequestReport tempReport=listReport.get(i);
				String reportStorageId=tempReport.getStorageId();
				if(StringUtils.isNotEmpty(reportStorageId)){
					setReportIds.add(reportStorageId);
				}
			}
		}
    	if(setReportIds.size()==0){
    		WebUtil.addErrorMessage("工单报告为空！");
    		return null;
    	}
    	try (ByteArrayOutputStream bout = new ByteArrayOutputStream(); ZipOutputStream zout = new ZipOutputStream(bout)){
    		Iterator<String> it=setReportIds.iterator();
    		while(it.hasNext()){
    			String reportStorageId=it.next();
    			StreamedContent file = fileDownloadService.getFile(reportStorageId);
                String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getName());
                String contentType = file.getContentType();
                InputStream stream = file.getStream();
                zout.putNextEntry(new ZipEntry(fileName));
                byte[] bytes = IOUtils.toByteArray(stream);
                if(bytes!=null){
                    IOUtils.write(bytes, zout);
                }
    		}
    		zout.closeEntry();
    		return new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", "Reports.zip");
        } catch (Exception e) {
            WebUtil.addErrorMessage("Fail to download report:" + e.getMessage());
        }
    	return null;
    }

	public List<AssetLifecycleEvent> getSelecteItems() {
		return selecteItems;
	}

	public void setSelecteItems(List<AssetLifecycleEvent> selecteItems) {
		this.selecteItems = selecteItems;
	}
    
}