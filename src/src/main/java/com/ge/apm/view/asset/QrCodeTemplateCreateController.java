package com.ge.apm.view.asset;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.springframework.transaction.annotation.Transactional;

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
    List<QrCodeTemplateElement> thisTemplateElementList;
    List<QrCodeTemplateElement> removedElementList = new ArrayList<>();

    QrCodeTemplateElement qrcodeElement;

    static Map<String, String> fontNameMap;

    UaaService uaaService;

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
            Map<String, Object> defaultTemplate = templateService.generateDefaultTemplate();
            thisTemplate = (QrCodeTemplate) defaultTemplate.get("template");
            thisTemplateElementList = (List<QrCodeTemplateElement>) defaultTemplate.get("elementList");
        } else if (actionName.equals("Edit")) {
            String templateId = WebUtil.getRequestParameter("templateId");
            thisTemplate = templateDao.findById(templateId);
            thisTemplateElementList = templateService.getElementsByTemplate(thisTemplate.getId());
        }
        qrcodeElement = thisTemplateElementList.stream().filter(item -> item.getType().equals(QrCodeTemplateElement.QrCodeElementType.QrCode)).findAny().orElse(null);
    }

    public void createNewElement() {
        QrCodeTemplateElement newElement = new QrCodeTemplateElement();
        newElement.setType(QrCodeTemplateElement.QrCodeElementType.Text);
        thisTemplateElementList.add(newElement);
    }

    public String getPreViewBase64() {
        return qrCodeService.getPreViewBase64(thisTemplate, thisTemplateElementList);
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

    public List<QrCodeTemplateElement> getTextElementList() {
        return thisTemplateElementList.stream().filter(item -> item.getType().equals(QrCodeTemplateElement.QrCodeElementType.Text)).collect(Collectors.toList());
    }

    @Transactional
    public void saveChange() {
        templateDao.save(thisTemplate);
        thisTemplateElementList.forEach(item -> {
            item.setTemplateId(thisTemplate.getId());
            item.setHospitalUID(thisTemplate.getHospitalUID());
            item.setInstitutionUID(thisTemplate.getInstitutionUID());
            item.setTenantUID(thisTemplate.getTenantUID());
            item.setSiteUID(thisTemplate.getSiteUID());
        });
        elementDao.save(thisTemplateElementList);
        if (!removedElementList.isEmpty()) {
            elementDao.delete(removedElementList);
        }

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

    public void removeElement(QrCodeTemplateElement element) {
        if (element.getId() == null || element.getId().isEmpty()) {
            WebUtil.addErrorMessage("请保存后修改");
        } else {
            thisTemplateElementList.remove(element);
            removedElementList.add(element);
        }
    }

    public List<OrgInfo> getHospitalFileterList() {
        List<OrgInfo> orgInfoList = null;
        UserAccount user = UserContextService.getCurrentUserAccount();
        orgInfoList = uaaService.getUserAccessibleSiteList(user.getUid());
        return orgInfoList;
    }

    //getter and setter
    public void setThisTemplate(QrCodeTemplate thisTemplate) {
        this.thisTemplate = thisTemplate;
    }

    public QrCodeTemplate getThisTemplate() {
        return thisTemplate;
    }

    public void setThisTemplateElementList(List<QrCodeTemplateElement> thisTemplateElementList) {
        this.thisTemplateElementList = thisTemplateElementList;
    }

    public List<QrCodeTemplateElement> getThisTemplateElementList() {
        return thisTemplateElementList;
    }

    public QrCodeTemplateElement getQrcodeElement() {
        return qrcodeElement;
    }

    public void setQrcodeElement(QrCodeTemplateElement qrcodeElement) {
        this.qrcodeElement = qrcodeElement;
    }

}
