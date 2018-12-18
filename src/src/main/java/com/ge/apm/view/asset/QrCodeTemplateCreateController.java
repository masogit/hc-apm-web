package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.QrCodeService;
import com.ge.apm.service.asset.QrCodeTemplateService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Strings;
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
    static Map<String, String> fontNameMap;
    
    UaaService uaaService;
    
    String importJsonString;
    
    @PostConstruct
    protected void init() {
        templateDao = WebUtil.getBean(QrCodeTemplateRepository.class);
        elementDao = WebUtil.getBean(QrCodeTemplateElementRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        templateService = WebUtil.getBean(QrCodeTemplateService.class);
        qrCodeService = WebUtil.getBean(QrCodeService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        
        String actionName = WebUtil.getRequestParameter("action");
        if (actionName.equals("Create")) {
            String copyFrom = WebUtil.getRequestParameter("copyFrom");
            thisTemplate = templateService.getDefaultTemplate();
            if (!Strings.isNullOrEmpty(copyFrom)) {
                QrCodeTemplate copyFromTemplate = templateDao.findById(copyFrom);
                if (null != copyFromTemplate) {
                    thisTemplate.setHeight(copyFromTemplate.getHeight());
                    thisTemplate.setWidth(copyFromTemplate.getWidth());
                    thisTemplate.setJSONObject(null == copyFromTemplate.getJSONObject() ? templateService.getJSONObjectByTemplate(copyFromTemplate) : copyFromTemplate.getJSONObject());
                }
            }
        } else if (actionName.equals("Edit")) {
            String templateId = WebUtil.getRequestParameter("templateId");
            thisTemplate = templateDao.findById(templateId);
        }

        //数据迁移后移除
        if (null == thisTemplate.getJSONObject()) {
            thisTemplate.setJSONObject(templateService.getJSONObjectByTemplate(thisTemplate));
        }
        templateJsonObject = thisTemplate.getJSONObject();
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
        return qrCodeService.getPreViewBase64(thisTemplate, templateService.getElementsByTemplate(thisTemplate));
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
        WebUtil.addSuccessMessage("修改已经保存");
    }
    
    public Map<String, String> getFieldsMap() {
        return qrCodeService.getFieldsMap();
    }
    
    public Map<String, String> getFontNameMap() {
        if (fontNameMap == null) {
            fontNameMap = new HashMap<String, String>();
            fontNameMap.put("TimesRoman", "TimesRoman");
            fontNameMap.put("Courier", "Courier");
            fontNameMap.put("Arial", "Arial");
            fontNameMap.put("Microsoft YaHei", "Microsoft YaHei");
            fontNameMap.put("SimHei", "SimHei");
            fontNameMap.put("SimSun", "SimSun");
        }
        return fontNameMap;
    }
    
    public void removeElement(JSONObject element) {
        templateJsonObject.getJSONArray("Elements").remove(element);
    }
    
    public List<OrgInfo> getHospitalFileterList() {
        List<OrgInfo> orgInfoList = null;
        UserAccount user = UserContextService.getCurrentUserAccount();
        orgInfoList = uaaService.getUserAccessibleSiteList(user.getUid());
        return orgInfoList;
    }
    
    public void onExportAction() {
        importJsonString = templateJsonObject.toJSONString();
    }
    
    public void onImportSave() {
        try {
            templateJsonObject = JSONObject.parseObject(importJsonString);
        } catch (JSONException e) {
            WebUtil.addErrorMessage("不能解析JSON");
        }
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
    
}
