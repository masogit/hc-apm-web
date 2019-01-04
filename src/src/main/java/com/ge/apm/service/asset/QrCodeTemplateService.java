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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.ge.apm.service.common.QrcodeConfigConstant;
import java.util.Objects;

/**
 *
 * @author 212579464
 */
@Component
public class QrCodeTemplateService {

    @Autowired
    private QrCodeTemplateRepository templateDao;
    @Autowired
    private QrCodeTemplateElementRepository elementDao;
    @Autowired
    private UaaService uaaService;

    public QrCodeTemplate getDefaultTemplate() {
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        return getDefaultTemplate(loginUser.getTenantUID(), loginUser.getInstitutionUID(), loginUser.getHospitalUID(), loginUser.getSiteUID());
    }

    public QrCodeTemplate getDefaultTemplate(String tenantUID, String institutionUID, String hospitalUID, String siteUID) {
        QrCodeTemplate template = new QrCodeTemplate();
        template.setHeight(360);
        template.setWidth(900);
        template.setHospitalUID(hospitalUID);
        template.setInstitutionUID(institutionUID);
        template.setTenantUID(tenantUID);
        template.setSiteUID(siteUID);
        template.setJSONObject(getDefaultJsonText(template));
        return template;
    }

    private JSONObject getDefaultJsonText(QrCodeTemplate template) {
        JSONObject jsonObject = JSONObject.parseObject(QrcodeConfigConstant.DEFAULT_TEMPLATE_JSON);
        jsonObject.put("Height", template.getHeight());
        jsonObject.put("Width", template.getWidth());

//        jsonObject.put("QrCodeElements", ImmutableMap.builder().putAll(ImmutableMap.of("Type", QrCodeTemplateElement.QrCodeElementType.QrCode, "Size", 300, "PositionX", 10))
//                .putAll(ImmutableMap.of("PositionY", 20, "FontPositionX", 14, "FontSize", 24, "FontName", "Microsoft YaHei")).build());
//
//        JSONArray elementsArray = new JSONArray();
//
//        elementsArray.add(ImmutableMap.builder().putAll(ImmutableMap.of("TextValue", "", "FieldKey", "hospital", "PositionX", 320))
//                .putAll(ImmutableMap.of("PositionY", 40, "FontType", 1, "FontSize", 40, "FontName", "Microsoft YaHei", "ColorStr", "000000")).build());
//
//        elementsArray.add(ImmutableMap.builder().putAll(ImmutableMap.of("TextValue", "设备名称   ", "FieldKey", "hospital", "PositionX", 320))
//                .putAll(ImmutableMap.of("PositionY", 240, "FontType", 1, "FontSize", 32, "FontName", "Microsoft YaHei", "ColorStr", "000000")).build());
//
//        elementsArray.add(ImmutableMap.builder().putAll(ImmutableMap.of("TextValue", "所属科室   ", "FieldKey", "hospital", "PositionX", 320))
//                .putAll(ImmutableMap.of("PositionY", 120, "FontType", 1, "FontSize", 32, "FontName", "Microsoft YaHei", "ColorStr", "000000")).build());
//
//        elementsArray.add(ImmutableMap.builder().putAll(ImmutableMap.of("TextValue", "资产编号   ", "FieldKey", "hospital", "PositionX", 320))
//                .putAll(ImmutableMap.of("PositionY", 180, "FontType", 1, "FontSize", 32, "FontName", "Microsoft YaHei", "ColorStr", "000000")).build());
//
//        jsonObject.put("Elements", elementsArray);
//        
//        
//        String defaultJson ="{\"Height\":360,\"Elements\":[{\"ColorStr\":\"000000\",\"PositionY\":40,\"FontSize\":40,\"TextValue\":\"\",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"hospital\"},{\"ColorStr\":\"000000\",\"PositionY\":240,\"FontSize\":32,\"TextValue\":\"设备名称   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"hospital\"},{\"ColorStr\":\"000000\",\"PositionY\":120,\"FontSize\":32,\"TextValue\":\"所属科室   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"hospital\"},{\"ColorStr\":\"000000\",\"PositionY\":180,\"FontSize\":32,\"TextValue\":\"资产编号   \",\"FontName\":\"Microsoft YaHei\",\"FontType\":1,\"PositionX\":320,\"FieldKey\":\"hospital\"}],\"Width\":900,\"QrCodeElements\":{\"Type\":\"QrCode\",\"PositionY\":20,\"FontSize\":24,\"Size\":300,\"FontName\":\"Microsoft YaHei\",\"FontPositionX\":14,\"PositionX\":10}}";
        return jsonObject;
    }

    public QrCodeTemplate getDefaultTemplateByQrCodeLib(QrCodeLib qrCodeLib) {
        return getDefaultTemplate(qrCodeLib.getTenantUID(), qrCodeLib.getInstitutionUID(), qrCodeLib.getHospitalUID(), qrCodeLib.getSiteUID());
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

    public JSONObject getJSONObjectByTemplate(QrCodeTemplate template) {
        if (null == template.getJSONObject()) {
            if (null == template.getId()) {
                template.setJSONObject(getDefaultJsonText(template));
            } else {
                List<QrCodeTemplateElement> templateElementList = getElementsByTemplate(template.getId());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Height", template.getHeight());
                jsonObject.put("Width",template.getWidth());
                jsonObject.put("Elements", new JSONArray());
                templateElementList.forEach(item -> {
                    JSONObject qrCodeElements = new JSONObject();
                    if (Objects.equals(item.getType(), QrCodeTemplateElement.QrCodeElementType.QrCode)) {
                        qrCodeElements.put("Size", item.getSize());
                        qrCodeElements.put("PositionX", item.getPositionX());
                        qrCodeElements.put("PositionY", item.getPositionY());
                        qrCodeElements.put("FontPositionX", item.getFontType());
                        qrCodeElements.put("FontSize", item.getFontSize());
                        qrCodeElements.put("FontName", item.getFontName());
                        jsonObject.put("QrCodeElements", qrCodeElements);
                    } else {
                        qrCodeElements.put("TextValue", item.getTextValue());
                        qrCodeElements.put("FieldKey", item.getFieldKey());
                        qrCodeElements.put("PositionX", item.getPositionX());
                        qrCodeElements.put("PositionY", item.getPositionY());
                        qrCodeElements.put("FontSize", item.getFontSize());
                        qrCodeElements.put("FontType", item.getFontType());
                        qrCodeElements.put("FontName", item.getFontName());
                        qrCodeElements.put("ColorStr", item.getColorStr());
                        jsonObject.getJSONArray("Elements").add(qrCodeElements);
                    }
                });
                template.setJSONObject(jsonObject);
            }
        }
        return template.getJSONObject();
    }

    public List<QrCodeTemplateElement> getElementsByTemplate(QrCodeTemplate template) {
        JSONObject jsonObject = template.getJSONObject();
        List<QrCodeTemplateElement> elementList = new ArrayList();
        QrCodeTemplateElement qrcodeElement = new QrCodeTemplateElement();
        qrcodeElement.setType(QrCodeTemplateElement.QrCodeElementType.QrCode);
        qrcodeElement.setSize(jsonObject.getJSONObject("QrCodeElements").getIntValue("Size"));
        qrcodeElement.setPositionX(jsonObject.getJSONObject("QrCodeElements").getIntValue("PositionX"));
        qrcodeElement.setPositionY(jsonObject.getJSONObject("QrCodeElements").getIntValue("PositionY"));
        qrcodeElement.setFontType(jsonObject.getJSONObject("QrCodeElements").getIntValue("FontPositionX"));
        qrcodeElement.setFontSize(jsonObject.getJSONObject("QrCodeElements").getIntValue("FontSize"));
        qrcodeElement.setFontName(jsonObject.getJSONObject("QrCodeElements").getString("FontName"));
        elementList.add(qrcodeElement);

        jsonObject.getJSONArray("Elements").forEach(item -> {
            JSONObject object = (JSONObject) item;
            elementList.add(createTextElement(object.getString("TextValue"), object.getString("FieldKey"), object.getIntValue("PositionX"), object.getIntValue("PositionY"),
                    object.getIntValue("FontSize"), object.getIntValue("FontType"), object.getString("FontName"), object.getString("ColorStr")));
        });

//        elementList.addAll(jsonObject.getJSONArray("Elements").stream().map((JSONObject)item->{
//            return new QrCodeTemplateElement();
////           return  createTextElement(item.getString("TextValue"),item.getString("FieldKey"),item.getIntValue("PositionX"),item.getIntValue("PositionY"),item.getIntValue("FontSize"),item.getIntValue("FontType"),item.getString("FontName"),item.getString("ColorStr"));
//        }).collect(Collectors.toList()));
        return elementList;
    }

    public boolean isValidJson(JSONObject jsonObject) {
        
        if(jsonObject.getJSONObject("QrCodeElements") ==null){
            return false;
        }
        
        return true;
    }
}
