package com.ge.apm.view.measuring;

import com.ge.apm.dao.ConfigItemValueRepository;
import com.ge.apm.dao.OCRTemplateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.ocr.support.BusinessType;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 503310146
 */
@ManagedBean
@ViewScoped
public class MeasureFromZipController {

    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<MeasuringCertificateDetail> resultList;
    private List<MeasuringCertificateDetail> resultListOCR;
    private UserAuth customRoleAuth;

    private OCRTemplateRepository ocrTemplateRepository;

    private ConfigItemService configItemService;
    private int progress;

    private String tabId = "";
    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        measuringService = WebUtil.getBean(MeasuringService.class);
        ocrTemplateRepository = WebUtil.getBean(OCRTemplateRepository.class);
        configItemService = WebUtil.getBean(ConfigItemService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        measuringService.setProgress(0);
    }

    public void handleZipFileUpload(FileUploadEvent event) throws IOException {
        resultList = new ArrayList<>();
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        resultList = measuringService.handleZip(event.getFile(), currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    public void handleZipFileUploadOCR(FileUploadEvent event) throws IOException {
        measuringService.setProgress(0);
        resultListOCR = new ArrayList<>();
        String identifyPatterns = configItemService.getConfigItemByKeyToString("identifyPatterns",currentUser);
        if("local".equals(identifyPatterns)){
            List<OCRTemplate> ocrTemplates = getOCRTemplates();
            if(CollectionUtils.isEmpty(ocrTemplates)){
                FacesMessage message = new FacesMessage("上传完成"+event.getFile().getFileName() + " 处理失败,本地配置缺少无法OCR识别！");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return ;
            }
        }
        resultListOCR = measuringService.handleZipOCR(event.getFile(), currentUser,identifyPatterns);
        if(!CollectionUtils.isEmpty(resultListOCR)){
            FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    public List<OCRTemplate> getOCRTemplates() {
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,currentUser.getTenantUID()));
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, BusinessType.MEASURE.name()));
        return ocrTemplateRepository.findBySearchFilter(searchFilters);

    }

    public void onChange(TabChangeEvent event) {
        Tab activeTab = event.getTab();
        tabId = activeTab.getId();

    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<MeasuringCertificateDetail> getResultList() {
        if("t2".equals(tabId)){
            return resultListOCR;
        }
        return resultList;
    }

    public void setNoFoundList(List<MeasuringCertificateDetail> resultList) {
        this.resultList = resultList;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }

    public void handleComplete() {
        //Add a faces message
    }
    public int getProgress() {

        return measuringService.getProgress();
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
