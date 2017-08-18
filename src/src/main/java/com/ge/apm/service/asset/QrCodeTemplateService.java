/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.QrCodeTemplateElementRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class QrCodeTemplateService {

    @Autowired
    QrCodeLibRepository qrCodeLibDao;
    @Autowired
    QrCodeTemplateRepository templateDao;
    @Autowired
    QrCodeTemplateElementRepository elementDao;
    @Autowired
    QrCodeService qrCodeService;

    public QrCodeTemplate getDefaultTemplate() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        QrCodeTemplate template = new QrCodeTemplate();
        template.setHeight(180);
        template.setWidth(450);
        template.setHospitalUID(loginUser.getHospitalUID());
        template.setInstitutionUID(loginUser.getInstitutionUID());
        template.setTenantUID(loginUser.getTenantUID());
        template.setSiteUID(loginUser.getSiteUID());
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
        qrcodeElement.setSize(150);
        qrcodeElement.setPositionX(5);
        qrcodeElement.setPositionY(10);
        elementList.add(qrcodeElement);

        elementList.add(createTextElement("", "hospital", 160, 20, 20, 1, "TimesRoman", "000000"));
        elementList.add(createTextElement("设备名称   ", "name", 160, 120, 16, 1, "TimesRoman", "000000"));
        elementList.add(createTextElement("使用部门   ", "clinicalDept", 160, 60, 16, 1, "TimesRoman", "000000"));
        elementList.add(createTextElement("资产编号   ", "financingNum", 160, 90, 16, 1, "TimesRoman", "000000"));
//        elementList.add(createTextElement("设备类型   ", "assetGroup", 300, 130, 16, 1, "TimesRoman", "000000"));
//        elementList.add(createTextElement("序列号       ", "serialNum", 300, 190, 16, 1, "TimesRoman", "000000"));
//        elementList.add(createTextElement("设备型号   ", "functionType", 300, 250, 16, 1, "TimesRoman", "000000"));
//        elementList.add(createTextElement("系统编号   ", "modalityId", 300, 280, 16, 1, "TimesRoman", "000000"));
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

}
