package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.asset.QrCodeTemplateService;
import com.ge.apm.service.common.QrcodeConfigConstant;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Strings;
import org.primefaces.context.RequestContext;
import webapp.framework.web.WebUtil;

@ManagedBean
@ViewScoped
public class QrCodeTemplateCreateController {
    
    QrCodeTemplateRepository templateDao = null;
    QrCodeTemplateElementRepository elementDao = null;
    OrgInfoRepository orgDao;
    QrCodeTemplateService templateService;
    QrCodeService qrCodeService;
    
    QrCodeTemplate thisTemplate;
    JSONObject templateJsonObject;
    
    UaaService uaaService;
    
    String importJsonString;

    Map<String, String> qrCodeEncodingMethods = QrcodeConfigConstant.getQrCodeEncodingMethod();

    private List<OrgInfo> siteList;

    private I18nMessageRepository i18nMessageRepository;

    private BlobFileService blobService;

    @Setter@Getter
    private V2_BlobObject backgroundBlob = null;

    String actionName ;
    @PostConstruct
    protected void init() {
        templateDao = WebUtil.getBean(QrCodeTemplateRepository.class);
        elementDao = WebUtil.getBean(QrCodeTemplateElementRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        templateService = WebUtil.getBean(QrCodeTemplateService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);

        initSiteList();
        
        actionName = WebUtil.getRequestParameter("action");
        if (actionName.equals("Create")) {
            String copyFrom = WebUtil.getRequestParameter("copyFrom");
            thisTemplate = templateService.getDefaultTemplate(getInitialSite());
            if (!Strings.isNullOrEmpty(copyFrom)) {
                QrCodeTemplate copyFromTemplate = templateDao.findById(copyFrom);
                if (null != copyFromTemplate) {
                    thisTemplate.setHeight(copyFromTemplate.getHeight());
                    thisTemplate.setWidth(copyFromTemplate.getWidth());
                    thisTemplate.setJSONObject(null == copyFromTemplate.getJSONObject() ? templateService.getJSONObjectByTemplate(copyFromTemplate) : copyFromTemplate.getJSONObject());
                    List<V2_BlobObject> backgroundList = blobService.findBlobObjectsByBoUid(copyFromTemplate.getId());
                    if(!backgroundList.isEmpty()){
                        backgroundBlob = backgroundList.get(0);
                    }
                }
            }
        } else if (actionName.equals("Edit")) {
            String templateId = WebUtil.getRequestParameter("templateId");
            thisTemplate = templateDao.findById(templateId);
            List<V2_BlobObject> backgroundList = blobService.findBlobObjectsByBoUid(templateId);
            if(!backgroundList.isEmpty()){
                backgroundBlob = backgroundList.get(0);
            }
        }

        //数据迁移后移除
        if (null == thisTemplate.getJSONObject()) {
            thisTemplate.setJSONObject(templateService.getJSONObjectByTemplate(thisTemplate));
        }
        templateJsonObject = thisTemplate.getJSONObject();

        // 设置跳转功能页面方式默认值
        JSONObject qrCodeElements = templateJsonObject.getJSONObject("QrCodeElements");
        if (qrCodeElements != null && Strings.isNullOrEmpty(qrCodeElements.getString("EncodingMethod"))) {
            qrCodeElements.put("EncodingMethod", "redirect2FuncPage");
        }
    }
    
    public void createNewElement() {
        
        JSONObject newElement = new JSONObject();
        newElement.put("PositionX", 0);
        newElement.put("PositionY", 0);
        newElement.put("FontType", 0);
        newElement.put("FontSize", 0);
        newElement.put("FontName", "Microsoft YaHei");
        newElement.put("ColorStr", "000000");
        templateJsonObject.getJSONArray("Elements").add(newElement);
    }
    
    public String getPreViewBase64() {
        thisTemplate.setJSONObject(templateJsonObject);
        thisTemplate.setHeight(templateJsonObject.getIntValue("Height"));
        thisTemplate.setWidth(templateJsonObject.getIntValue("Width"));

        thisTemplate.setV2BlobObject(backgroundBlob);
        return qrCodeService.getPreViewBase64(thisTemplate);
    }
    
    public void onSiteChange() {
        OrgInfo org = orgDao.getByUid(thisTemplate.getSiteUID());
        thisTemplate.setHospitalUID(org.getHospitalUID());
        thisTemplate.setInstitutionUID(org.getInstitutionUID());
        thisTemplate.setTenantUID(org.getTenantUID());
    }
    
    public void onRowEdit(RowEditEvent event) {
    }
    
    public void onRowCancel(RowEditEvent event) {
    }
    
    @Transactional
    public void saveChange() {
        thisTemplate.setJSONObject(templateJsonObject);
        thisTemplate.setHeight(templateJsonObject.getIntValue("Height"));
        thisTemplate.setWidth(templateJsonObject.getIntValue("Width"));
        thisTemplate = templateDao.save(thisTemplate);
        List<QrCodeTemplateElement> thisTemplateElementList = templateService.getElementsByTemplate(thisTemplate);
        
        elementDao.delete(templateService.getElementsByTemplate(thisTemplate.getId()));
        thisTemplateElementList.forEach(item -> {
            item.setTemplateId(thisTemplate.getId());
            item.setHospitalUID(thisTemplate.getHospitalUID());
            item.setInstitutionUID(thisTemplate.getInstitutionUID());
            item.setTenantUID(thisTemplate.getTenantUID());
            item.setSiteUID(thisTemplate.getSiteUID());
        });
        elementDao.save(thisTemplateElementList);
        if(backgroundBlob == null){//可能图片被删除了
            List<V2_BlobObject> list = blobService.findBlobObjectsByBoUid(thisTemplate.getId());
            if(!list.isEmpty()){
                blobService.deleteBlobFile(list);
            }
        }else if (StrUtil.isEmpty(backgroundBlob.getBoUuid())) {//图片重新上传了
            //现将老的背景图片删除
            List<V2_BlobObject> list = blobService.findBlobObjectsByBoUid(thisTemplate.getId());
            if(!list.isEmpty()){
                blobService.deleteBlobFile(list);
            }
            backgroundBlob.setBoUuid(thisTemplate.getId());
            blobService.saveBlobObject(backgroundBlob);
        }

        WebUtil.addSuccessMessage(WebUtil.getMessage("ModificationSaved"));
    }
    
    public Map<String, String> getFieldsMap() {
        Map<String,String> result=new LinkedHashMap<>();
        Integer siteId = UserContextService.getCurrentUserAccount().getSiteId();
        I18nMessage capitalSource2 = i18nMessageRepository.findFirstByMsgKeyAndSiteId("capitalSource2", siteId);
        Map<String, String> fieldsMap = QrcodeConfigConstant.getFieldsMap();
        result.putAll(fieldsMap);
        if(capitalSource2 != null){
            result.put(capitalSource2.getMsgKey(),capitalSource2.getValue());
        }
        return result;
    }
    
    public Map<String, String> getFontNameMap() {
        return QrcodeConfigConstant.getFontNameMap();
    }
    
    public void removeElement(JSONObject element) {
        templateJsonObject.getJSONArray("Elements").remove(element);
    }

    public void initSiteList() {

        UserAccount user = UserContextService.getCurrentUserAccount();
        this.siteList = uaaService.getUserAccessibleSiteList(user.getUid());
    }

    private Optional<OrgInfo> getInitialSite() {
        if (null == this.siteList || this.siteList.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(this.siteList.get(0));
        }
    }

//    public List<OrgInfo> getHospitalFileterList() {
//        List<OrgInfo> orgInfoList = null;
//        UserAccount user = UserContextService.getCurrentUserAccount();
//        orgInfoList = uaaService.getUserAccessibleSiteList(user.getUid());
//        return orgInfoList;
//    }
    
    public void onExportAction() {
        importJsonString = templateJsonObject.toJSONString();
    }
    
    public void onImportSave() {
        try {
            templateJsonObject = JSONObject.parseObject(importJsonString);
            if(!templateService.isValidJson(templateJsonObject)){
                WebUtil.addErrorMessage(WebUtil.getMessage("JSONIncorrect"));
                RequestContext.getCurrentInstance().addCallbackParam("JsonValid", false);
                return;
            };
            RequestContext.getCurrentInstance().addCallbackParam("JsonValid", true);
        } catch (JSONException e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("CannotParseJSON"));
            RequestContext.getCurrentInstance().addCallbackParam("JsonValid", false);
        }
    }

    //背景图片上传
    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            blobObject.setBoType(V2_BlobObject.BO_TYPE_QRCODE_BACKGROUND);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            //blobObject.setBoUuid(thisTemplate.getId());
            blobService.saveBlobObject(blobObject);
            backgroundBlob = blobObject;
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }

    }

    public void removeAttachmentById(String blobId){
        //blobService.deleteBlobFileById(blobId);
        backgroundBlob = null;
    }


    //getter and setter
    public void setThisTemplate(QrCodeTemplate thisTemplate) {
        this.thisTemplate = thisTemplate;
    }
    
    public QrCodeTemplate getThisTemplate() {
        return thisTemplate;
    }
    
    public JSONObject getTemplateJsonObject() {
        return templateJsonObject;
    }
    
    public void setTemplateJsonObject(JSONObject templateJsonObject) {
        this.templateJsonObject = templateJsonObject;
    }
    
    public String getImportJsonString() {
        return importJsonString;
    }
    
    public void setImportJsonString(String importJsonString) {
        this.importJsonString = importJsonString;
    }

    public Map<String, String> getQrCodeEncodingMethods() {
        return qrCodeEncodingMethods;
    }

    public void setQrCodeEncodingMethods(Map<String, String> qrCodeEncodingMethods) {
        this.qrCodeEncodingMethods = qrCodeEncodingMethods;
    }

    public List<OrgInfo> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<OrgInfo> siteList) {
        this.siteList = siteList;
    }
}
