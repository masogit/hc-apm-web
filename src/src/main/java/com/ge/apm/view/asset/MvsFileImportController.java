package com.ge.apm.view.asset;

import com.ge.apm.dao.MvsContractDateInfoRepository;
import com.ge.apm.dao.MvsOrgInfoRepository;
import com.ge.apm.domain.MvsContractDateInfo;
import com.ge.apm.domain.MvsOrgInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetFileImportService;
import com.ge.apm.service.asset.MvsFileImportService;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 503266962
 */
@ManagedBean
@ViewScoped
public class MvsFileImportController {
    private static final Logger logger = LoggerFactory.getLogger(MvsFileImportController.class);

    private MvsFileImportService service;
    private MvsContractDateInfoRepository mvsContractRepository;
    private MvsOrgInfoRepository mvsOrgInfoRepository;

    @Getter @Setter
    private UploadedFile file;

    private UserAccount userAccount;
    @Getter @Setter
    private boolean displayErrorsOnly = false;
    @Getter @Setter
    private List<MvsContractDateInfo> allContractList;
    @Getter @Setter
    private List<MvsContractDateInfo> errorContractList;
    @Getter @Setter
    private List<MvsOrgInfo> allOrgList;
    @Getter @Setter
    private List<MvsOrgInfo> errorOrgList;

    @PostConstruct
    protected void init() {
        service = WebUtil.getBean(MvsFileImportService.class);
        mvsContractRepository = WebUtil.getBean(MvsContractDateInfoRepository.class);
        mvsOrgInfoRepository = WebUtil.getBean(MvsOrgInfoRepository.class);
        userAccount = UserContextService.getCurrentUserAccount();
    }

    public void uploadContractDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        try {
            allContractList = service.handleContractExcel(file);
            if(CollectionUtils.isNotEmpty(allContractList)){
                errorContractList = new ArrayList<>();
                for (MvsContractDateInfo dateInfo : allContractList) {
                    if(!dateInfo.isPass()){
                        errorContractList.add(dateInfo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("mvs合同信息excel导入失败：", e);
            WebUtil.addErrorMessage("文件读取失败，请联系系统管理员！");
        }

    }

    public List<MvsContractDateInfo> getContractInfoList(){
        if(this.displayErrorsOnly){
            return errorContractList;
        }
        return allContractList;
    }

    public void importContractData(){
        List<MvsContractDateInfo> list = new ArrayList<>();
        for (MvsContractDateInfo dateInfo : allContractList) {
            if(dateInfo.isPass()){
                list.add(dateInfo);
            }
        }
        service.saveContractInfo(list);
        this.file = null;
        this.allContractList = new ArrayList<>();
        this.errorContractList = new ArrayList<>();
        WebUtil.addSuccessMessage("导入成功！");
    }

    public void uploadSiteDataFile(FileUploadEvent event) {
        this.file = event.getFile();
        try {
            allOrgList = service.handleSiteExcel(file);
            if(CollectionUtils.isNotEmpty(allOrgList)){
                errorOrgList = new ArrayList<>();
                for (MvsOrgInfo orgInfo : allOrgList) {
                    if(!orgInfo.isPass()){
                        errorOrgList.add(orgInfo);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("mvs院区信息excel导入失败：", e);
            WebUtil.addErrorMessage("文件读取失败，请联系系统管理员！");
        }
    }
    public List<MvsOrgInfo> getOrgInfoList(){
        if(this.displayErrorsOnly){
            return errorOrgList;
        }
        return allOrgList;
    }
    public void importOrgData(){
        List<MvsOrgInfo> list = new ArrayList<>();
        for (MvsOrgInfo orgInfo : allOrgList) {
            if(orgInfo.isPass()){
                list.add(orgInfo);
            }
        }
        service.saveOrgInfo(list);
        this.file = null;
        this.allOrgList = new ArrayList<>();
        this.errorOrgList = new ArrayList<>();
        WebUtil.addSuccessMessage("导入成功！");
    }
}
