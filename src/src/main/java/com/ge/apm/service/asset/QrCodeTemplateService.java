/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

/**
 *
 * @author 212579464
 */
@Component
public class QrCodeTemplateService {

    @Autowired
    private QrCodeLibRepository qrCodeLibDao;
    @Autowired
    private QrCodeTemplateRepository templateDao;
    @Autowired
    private QrCodeTemplateElementRepository elementDao;
    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private UaaService uaaService;

    public QrCodeTemplate getDefaultTemplate() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        QrCodeTemplate template = new QrCodeTemplate();
        template.setHeight(360);
        template.setWidth(900);
        template.setHospitalUID(loginUser.getHospitalUID());
        template.setInstitutionUID(loginUser.getInstitutionUID());
        template.setTenantUID(loginUser.getTenantUID());
        template.setSiteUID(loginUser.getSiteUID());
        return template;
    }
    
    public QrCodeTemplate getDefaultTemplateByQrCodeLib(QrCodeLib qrCodeLib) {
        QrCodeTemplate template = new QrCodeTemplate();
        template.setHeight(360);
        template.setWidth(900);
        template.setHospitalUID(qrCodeLib.getHospitalUID());
        template.setInstitutionUID(qrCodeLib.getInstitutionUID());
        template.setTenantUID(qrCodeLib.getTenantUID());
        template.setSiteUID(qrCodeLib.getSiteUID());
        return template;
    }
    

    private QrCodeTemplateElement createTextElement(String text, String fieldKey, Integer px, Integer py, Integer fs, Integer ft, String fn, String fc) {
        QrCodeTemplateElement element = new QrCodeTemplateElement();
        element.setType(QrCodeTemplateElement.QrCodeElementType.Text);
        element.setPositionX(px);
        element.setPositionY(py);
        element.setFontSize(fs);
        element.setFieldKey(fieldKey);
        element.setTextValue(text);
        element.setFontType(ft);
        element.setColorStr(fc);
        element.setFontName(fn);
        return element;
    }

    public List<QrCodeTemplateElement> getDefaultTemplateElements() {
        List<QrCodeTemplateElement> elementList = new ArrayList();
        QrCodeTemplateElement qrcodeElement = new QrCodeTemplateElement();
        qrcodeElement.setType(QrCodeTemplateElement.QrCodeElementType.QrCode);
        qrcodeElement.setSize(300);
        qrcodeElement.setPositionX(10);
        qrcodeElement.setPositionY(20);
        qrcodeElement.setFontType(14);
        qrcodeElement.setFontSize(24);
        qrcodeElement.setFontName("Microsoft YaHei");
        elementList.add(qrcodeElement);

        elementList.add(createTextElement("", "hospital", 320, 40, 40, 1, "Microsoft YaHei", "000000"));
        elementList.add(createTextElement("设备名称   ", "name", 320, 240, 32, 1, "Microsoft YaHei", "000000"));
        elementList.add(createTextElement("所属科室   ", "clinicalDept", 320, 120, 32, 1, "Microsoft YaHei", "000000"));
        elementList.add(createTextElement("资产编号   ", "financingNum", 320, 180, 32, 1, "Microsoft YaHei", "000000"));
        return elementList;
    }

    public List<QrCodeTemplateElement> getElementsByTemplate(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return elementDao.findByTemplateId(id);
    }

    public Map<String, Object> generateDefaultTemplate() {
        List<QrCodeTemplateElement> list = getDefaultTemplateElements();
        QrCodeTemplateElement qrcodeElement = list.stream().filter(item -> item.getType().equals(QrCodeTemplateElement.QrCodeElementType.QrCode)).findAny().orElse(null);
        return ImmutableMap.of("template", getDefaultTemplate(), "elementList", getDefaultTemplateElements(), "qrcodeElement", qrcodeElement);
    }

    public List<QrCodeTemplate> getTemplateByUser(UserAccount user) {
        List<SearchFilter> templateFilter = new ArrayList<>();
        List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getSiteUID()).collect(Collectors.toList());
        templateFilter.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        templateFilter.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        templateFilter.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        templateFilter.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));

        return templateDao.findBySearchFilter(templateFilter);
    }

}
