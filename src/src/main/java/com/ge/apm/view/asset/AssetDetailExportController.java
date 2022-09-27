package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.nurseSite.AssetOptRecordService;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import org.primefaces.model.StreamedContent;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean
@ViewScoped
public class AssetDetailExportController {


    private AssetOptRecordService assetOptRecordService;
    private NurseSiteMaintainService nurseSiteMaintainService;
    private StreamedContent exportFile;

    private Date startDate;
    private Date endDate ;
    @PostConstruct
    protected void init() {
        assetOptRecordService = WebUtil.getBean(AssetOptRecordService.class);
        nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);

    }
    private boolean isDownload=true;
    private boolean isOpen=false;
    public void exportAllWoByAssetInfoId(AssetInfo assetInfo){
        if(startDate.after(endDate)){
            isDownload=false;
            WebUtil.addErrorMessage("下载失败,"+WebUtil.getMessage("EndTimeCannotBeEarlierThanStartTime"));
            return ;
        }
        exportFile=assetOptRecordService.exportAllWoByAssetInfoId(assetInfo,startDate,endDate);
    }
    public void exportNurseSiteMaintainByAssetInfoId(AssetInfo assetInfo){
        if(startDate.after(endDate)){
            isDownload=false;
            WebUtil.addErrorMessage("下载失败,"+WebUtil.getMessage("EndTimeCannotBeEarlierThanStartTime"));
            return ;
        }
        exportFile=nurseSiteMaintainService.exportNurseSiteMaintainByAssetInfoId(assetInfo,startDate,endDate);
    }


    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
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

    public void onScheduleValidate(){
        if(startDate == null || endDate == null || startDate.after(endDate) || endDate.before(startDate)){
            this.isDownload = true;
        }else {
            this.isDownload = false;
        }
    }
    public void initOpenDialog(){
        isDownload=true;
        startDate = null;
        endDate = null;
        isOpen=true;
    }
    public void closeOpenDialog(){
        isDownload=true;
        startDate = null;
        endDate = null;
        isOpen=false;
    }
    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
