/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@Component
public class QrCodeService {

    @Autowired
    QrCodeLibRepository qrCodeLibDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    @Autowired
    AssetInfoRepository assetDao;

    @Autowired
    QrCodeTemplateService templateService;

    @Autowired
    QrCodeTemplateRepository templateDao;

    @Autowired
    QrCodeDrawService qrCodeDrawService;

    public QrCodeLib createOneQrCode(String tenantUID, String siteUID, Integer status) {
        if (tenantUID == null || siteUID == null) {
            return null;
        }
        OrgInfo currentOrg = orgInfoDao.getByUid(siteUID);
        return createOneQrCode(currentOrg, status);
    }

    public QrCodeLib createOneQrCode(OrgInfo currentOrg, Integer status) {
        if (currentOrg == null) {
            return null;
        }
        String dtStr = new DateTime().toString("yyMMdd");
        String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
        if (null == qrCodeLib) {
            qrCodeLib = new QrCodeLib();
            qrCodeLib.setSiteId(currentOrg.getSiteId());
            qrCodeLib.setHospitalId(currentOrg.getHospitalId());

            qrCodeLib.setSiteUID(currentOrg.getSiteUID());
            qrCodeLib.setHospitalUID(currentOrg.getHospitalUID());
            qrCodeLib.setTenantUID(currentOrg.getTenantUID());
            qrCodeLib.setInstitutionUID(currentOrg.getInstitutionUID());
            /*-- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)*/
            qrCodeLib.setStatus(status);
            qrCodeLib.setIssueDate(new Date());
            qrCodeLib.setQrCode(qrCode);
            qrCodeLibDao.save(qrCodeLib);
        }
        return qrCodeLib;
    }

    public List<QrCodeLib> createManyQrCode(String tenantUID, String siteUID, Integer count) {
        if (tenantUID == null || siteUID == null) {
            return null;
        }
        OrgInfo currentOrg = orgInfoDao.getByUid(siteUID);

        List<QrCodeLib> result = new ArrayList();
        for (int i = 0; i < count; i++) {
            result.add(createOneQrCode(currentOrg, 1));
        }
        return result;
    }

    public void discardQrCode(String qrCode) {
        updateQrCodeStatus(qrCode, 4);
    }

    public void updateQrCodeStatus(String qrCode, Integer status) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
        if (qrCodeLib != null) {
            qrCodeLib.setStatus(status);
            qrCodeLibDao.save(qrCodeLib);
        }
    }

    public BufferedImage getQrCodeImage(String qrCode) {
        return getQrCodeImage(qrCode, null);
    }

    public BufferedImage getQrCodeImage(String qrCode, AssetInfo asset) {
        QrCodeTemplate template = getTemplateByQrCode(qrCode);
        return getQrCodeImage(qrCode, asset, template);
    }

    public BufferedImage getQrCodeImageWithTemplate(String qrCode, AssetInfo asset, String templateId) {
        QrCodeTemplate template = templateDao.findById(templateId);
        return getQrCodeImage(qrCode, asset, template);
    }

    private BufferedImage getQrCodeImage(String qrCode, AssetInfo asset, QrCodeTemplate template) {
        List<QrCodeTemplateElement> elementList;
        if (template == null) {
            template = templateService.getDefaultTemplate();
            elementList = templateService.getDefaultTemplateElements();
        } else {
            elementList = templateService.getElementsByTemplate(template.getId());
        }
        if (!Strings.isNullOrEmpty(qrCode)) {
            elementList = getElementsValue(template, elementList, qrCode);
        } else {
            elementList = getElementsValue(template, elementList, asset);
        }

        BufferedImage img = null;
        try {
            img = qrCodeDrawService.createQrCodeTag(template.getWidth(), template.getHeight(), elementList);
        } catch (Exception ex) {
            Logger.getLogger(QrCodeTemplateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

    public QrCodeTemplate getTemplateByQrCode(String qrCode) {
        QrCodeLib qrcodeLib = qrCodeLibDao.findByQrCode(qrCode);
        return qrcodeLib == null ? null : Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getSiteUID()))
                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getHospitalUID()))
                        .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getInstitutionUID()))
                                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getTenantUID())).orElse(null))));
    }

    public String getQrCodeImageBase64(String qrCode) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(qrCode));
    }

    public String getQrCodeImageBase64(AssetInfo asset) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(null, asset));
    }

    public String getQrCodeImageBase64WithTemplate(String qrCode, String templateId) {
        return qrCodeDrawService.getImageBase64(getQrCodeImageWithTemplate(qrCode, null, templateId));
    }

    private static Map<String, String> FieldsMap;

    public Map<String, String> getFieldsMap() {
        if (null == FieldsMap) {
            FieldsMap = new java.util.LinkedHashMap<>();
            FieldsMap.put("blank", "无");                                        //空白
            FieldsMap.put("name", WebUtil.getMessage("name"));                   //设备名称  AssetInfo.name
            FieldsMap.put("assetGroup", WebUtil.getMessage("assetGroup"));       //设备类型  AssetInfo.assetGroup
            FieldsMap.put("hospital", WebUtil.getMessage("hospitalId"));         //所属院区  AssetInfo.siteUID -> OrgInfo.name
            FieldsMap.put("functionType", WebUtil.getMessage("functionType"));   //设备型号  AssetInfo.functionType
            FieldsMap.put("clinicalDept", WebUtil.getMessage("clinicalDeptId")); //使用科室  AssetInfo.clinicalDeptUID -> OrgInfo.name
            FieldsMap.put("serialNum", WebUtil.getMessage("serialNum"));         //序列号(厂商SN)  AssetInfo.serialNum
            FieldsMap.put("financingNum", WebUtil.getMessage("financingNum"));   //资产编号  AssetInfo.financingNum
            FieldsMap.put("departNum", WebUtil.getMessage("departNum"));         //设备编号（院方）  AssetInfo.departNum
            FieldsMap.put("modalityId", WebUtil.getMessage("modalityId"));       //院内IT系统编号  AssetInfo.modalityId
            FieldsMap.put("makerShortName", WebUtil.getMessage("makerShortName"));       //品牌/制造商简称  AssetInfo.makerShortName
            FieldsMap.put("systemId", WebUtil.getMessage("systemId"));           //SystemID/厂商报修ID  AssetInfo.systemId

        }
        return FieldsMap;
    }

    public List<QrCodeTemplateElement> getElementsValue(QrCodeTemplate template, List<QrCodeTemplateElement> elements, String qrCode) {
        AssetInfo asset = assetDao.getByQrCode(qrCode);
        if (asset == null) {
            //Demo asset for preview
            asset = new AssetInfo();
            asset.setAssetGroup(1);
            asset.setName("(待定)");
            asset.setSerialNum("(待定)");
            asset.setFinancingNum("(待定)");
            asset.setDepartNum("(待定)");
            asset.setModalityId("(待定)");
            asset.setFunctionType("(待定)");
            asset.setQrCode(qrCode);
        }
        return getElementsValue(template, elements, asset);
    }

    public List<QrCodeTemplateElement> getElementsValue(QrCodeTemplate template, List<QrCodeTemplateElement> elements, AssetInfo asset) {
        for (QrCodeTemplateElement item : elements) {
            if (item.getType().equals(QrCodeTemplateElement.QrCodeElementType.QrCode)) {
                item.setTextValue(asset.getQrCode());
            }
            if (item.getFieldKey() != null && !item.getFieldKey().isEmpty()) {
                if (item.getTextValue() == null) {
                    item.setTextValue("");
                }
                switch (item.getFieldKey()) {
                    case "blank":
                        item.setTextShowValue(item.getTextValue());
                        break;
                    case "name":
                        item.setTextShowValue(item.getTextValue().concat(asset.getName() == null ? "" : asset.getName()));
                        break;
                    case "assetGroup":
                        item.setTextShowValue(item.getTextValue().concat(FieldValueMessageController.fieldValue("assetGroup", asset.getAssetGroup().toString())));
                        break;
                    case "hospital":
                        String siteUid = asset.getSiteUID();
                        if (siteUid == null) {
                            QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(asset.getQrCode());
                            siteUid = qrCodeLib == null ? null : qrCodeLib.getSiteUID();
                        }
                        if (siteUid == null) {
                            siteUid = template.getSiteUID();
                        }
                        if (siteUid == null) {
                            siteUid = UserContextService.getCurrentUserAccount().getSiteUID();
                        }
                        OrgInfo org = orgInfoDao.getByUid(siteUid);
                        item.setTextShowValue(item.getTextValue().concat(org.getName() == null ? "" : org.getName()));
                        break;
                    case "functionType":
                        item.setTextShowValue(item.getTextValue().concat(asset.getFunctionType() == null ? "" : asset.getFunctionType()));
                        break;
                    case "clinicalDept":
                        if (asset.getClinicalDeptUID() != null) {
                            OrgInfo clinicalDept = orgInfoDao.getByUid(asset.getClinicalDeptUID());
                            item.setTextShowValue(item.getTextValue().concat(clinicalDept.getName() == null ? "" : clinicalDept.getName()));
                        } else {
                            item.setTextShowValue(item.getTextValue().concat("(待定)"));
                        }
                        break;
                    case "serialNum":
                        item.setTextShowValue(item.getTextValue().concat(asset.getSerialNum() == null ? "" : asset.getSerialNum()));
                        break;
                    case "financingNum":
                        item.setTextShowValue(item.getTextValue().concat(asset.getFinancingNum() == null ? "" : asset.getFinancingNum()));
                        break;
                    case "departNum":
                        item.setTextShowValue(item.getTextValue().concat(asset.getDepartNum() == null ? "" : asset.getDepartNum()));
                        break;
                    case "modalityId":
                        item.setTextShowValue(item.getTextValue().concat(asset.getModalityId() == null ? "" : asset.getModalityId()));
                        break;
                    case "makerShortName":
                        item.setTextShowValue(item.getTextValue().concat(asset.getMakerShortName() == null ? "" : asset.getMakerShortName()));
                        break;
                    case "systemId":
                        item.setTextShowValue(item.getTextValue().concat(asset.getSystemId() == null ? "" : asset.getSystemId()));
                        break;
                }
            }
        }

        return elements;

    }

    public StreamedContent exportQrCodeListByAssets(List<AssetInfo> assetList, String templateId) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);
        QrCodeTemplate template = Strings.isNullOrEmpty(templateId)? null:templateDao.findById(templateId);
        assetList.forEach(item -> {
            if (!Strings.isNullOrEmpty(item.getQrCode())) {
                try {
                    BufferedImage image =  getQrCodeImage(item.getQrCode(), null, template==null?getTemplateByQrCode(item.getQrCode()) :template);
                    out.putNextEntry(new ZipEntry(Optional.ofNullable(item.getClinicalDeptName()).orElse("").concat("_").concat(item.getName()).concat("_").concat(item.getQrCode()).concat(".jpg")));
                    ImageIO.write(image, "JPG", out);
                    out.closeEntry();
                } catch (Exception e) {
                    WebUtil.addErrorMessage(item + "export fail;");
                }
            }
        });
        out.close();

        DefaultStreamedContent zipFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()));
        zipFile.setName("qrCodes.zip");
        return zipFile;
    }

    public StreamedContent exportQrCodeList(List<String> exportList) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);

        exportList.forEach(item -> {
            try {
                BufferedImage image = getQrCodeImage(item);
                out.putNextEntry(new ZipEntry(item.concat(".jpg")));
                ImageIO.write(image, "JPG", out);
                out.closeEntry();
            } catch (Exception e) {
                WebUtil.addErrorMessage(item + "export fail;");
            }
        });
        out.close();

        DefaultStreamedContent zipFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()));
        zipFile.setName("qrCodes.zip");
        return zipFile;
    }

    List<QrCodeTemplateElement> getElementsValue(QrCodeTemplate template, List<QrCodeTemplateElement> elements) {

        return getElementsValue(template, elements, "0123456789123456");
    }

    public String getPreViewBase64(QrCodeTemplate template, List<QrCodeTemplateElement> elementList) {

        elementList = getElementsValue(template, elementList);
        BufferedImage img = null;
        try {
            img = qrCodeDrawService.createQrCodeTag(template.getWidth(), template.getHeight(), elementList);
        } catch (Exception ex) {
            Logger.getLogger(QrCodeTemplateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return qrCodeDrawService.getImageBase64(img);
    }

    public String getPreViewBase64(String templateId) {
        QrCodeTemplate template = templateDao.findById(templateId);
        List<QrCodeTemplateElement> elementList = templateService.getElementsByTemplate(templateId);
        return getPreViewBase64(template, elementList);
    }

    /**
     * Return "Normal" is available;
     *
     * @param qrcode
     * @return
     */
    public String isAvailableQrcode(String qrcode, String siteUid) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return "Normal";
        }
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode);
        if (qrCodeLib == null) {
            return "InvalidQRCode";
        }
        if (!qrCodeLib.getSiteUID().equals(siteUid)) {
            return "WrongHospitalQRCode";
        }
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
            return "AlreadyUsingQrCode";
        }

        return "Normal";
    }

}
