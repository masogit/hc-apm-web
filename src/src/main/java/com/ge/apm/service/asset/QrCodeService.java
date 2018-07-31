/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.ge.apm.dao.ArrivalListItemRepository;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.ArrivalListItem;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.QrCodeTemplateElement;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
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

    @Autowired
    ConfigService configService;

    @Autowired
    private OrgInfoRepository orgDao;
    
    @Autowired
    private BlobFileService blobObjectService;

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
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, currentOrg.getSiteUID());
        if (null == qrCodeLib) {
            qrCodeLib = new QrCodeLib();
//            qrCodeLib.setSiteId(currentOrg.getSiteId());
//            qrCodeLib.setHospitalId(currentOrg.getHospitalId());

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

    public void createSpecifiedQrCode(String qrCode, OrgInfo currentOrg, int status) {
        if (StringUtils.isEmpty(qrCode)) {
            throw new IllegalArgumentException("qrCode should not be empty");
        }
        if (currentOrg == null) {
            throw new IllegalArgumentException("current org should not be empty");
        }

        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, currentOrg.getSiteUID());
        if (qrCodeLib != null) {
            throw new IllegalArgumentException("current qrcode already exists");
        }

        qrCodeLib = new QrCodeLib();

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

    public void discardQrCode(String qrCode, String siteUid) {
        updateQrCodeStatus(qrCode, siteUid, 4);
    }

    public void updateQrCodeStatus(String qrCode, String siteUid, Integer status) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, siteUid);
        if (qrCodeLib != null) {
            qrCodeLib.setStatus(status);
            qrCodeLibDao.save(qrCodeLib);
        }
    }

    public BufferedImage getQrCodeImage(String qrCode, String siteUid) {
        return getQrCodeImage(qrCode, null, siteUid);
    }

    public BufferedImage getQrCodeImage(QrCodeLib qrCodeLib) {
        return getQrCodeImage(qrCodeLib.getQrCode(), null, qrCodeLib.getSiteUID());
    }

    public BufferedImage getQrCodeImage(String qrCode, AssetInfo asset, String siteUid) {
        QrCodeTemplate template = null;
        if (asset != null) {
            template = getTemplateBySiteUid(asset.getSiteUID());
        } else if (!Strings.isNullOrEmpty(qrCode)) {
            template = getTemplateBySiteUid(siteUid);
        }
        return getQrCodeImage(qrCode, asset, template, siteUid);
    }

    public BufferedImage getQrCodeImageWithTemplate(String qrCode, AssetInfo asset, String templateId, String siteUid) {
        if (Strings.isNullOrEmpty(templateId)) {
            return getQrCodeImage(qrCode, asset, asset.getSiteUID());
        }
        QrCodeTemplate template = templateDao.findById(templateId);
        return getQrCodeImage(qrCode, asset, template, siteUid);
    }

    private BufferedImage getQrCodeImage(String qrCode, AssetInfo asset, QrCodeTemplate template, String siteUid) {
        List<QrCodeTemplateElement> elementList;
        if (template == null) {
//            if (StringUtils.isEmpty(qrCode)) {
//                qrCode = asset.getQrCode();
//            }
//            if (StringUtils.isEmpty(qrCode)) {
//                return null;
//            }
            QrCodeLib qrCodeLib = this.findByQrCode(Strings.isNullOrEmpty(qrCode)?asset.getQrCode():qrCode, siteUid);
            template = templateService.getDefaultTemplateByQrCodeLib(qrCodeLib);
            elementList = templateService.getDefaultTemplateElements();
        } else {
            elementList = templateService.getElementsByTemplate(template.getId());
        }
        if (!Strings.isNullOrEmpty(qrCode)) {
            elementList = getElementsValue(template, elementList, qrCode, siteUid);
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

//    public QrCodeTemplate getTemplateByQrCode(String qrCode,String siteUid) {
//        QrCodeLib qrcodeLib = qrCodeLibDao.findByQrCode(qrCode,siteUid);
//        return qrcodeLib == null ? null : Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getSiteUID()))
//                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getHospitalUID()))
//                        .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getInstitutionUID()))
//                                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(qrcodeLib.getTenantUID())).orElse(null))));
//    }
    public QrCodeTemplate getTemplateBySiteUid(String siteUid) {
        OrgInfo siteOrg = orgDao.findByUid(siteUid);
        return siteOrg == null ? null : Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getSiteUID()))
                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getHospitalUID()))
                        .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getInstitutionUID()))
                                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getTenantUID())).orElse(null))));
    }

    public String getQrCodeImageBase64(String qrCode, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(qrCode, siteUid));
    }

    public String getQrCodeImageBase64(AssetInfo asset) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(null, asset, asset.getSiteUID()));
    }

    public String getQrCodeImageBase64WithTemplate(AssetInfo asset, String templateId, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImageWithTemplate(null, asset, templateId, siteUid));
    }

    public String getQrCodeImageBase64WithTemplate(String qrCode, String templateId, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImageWithTemplate(qrCode, null, templateId, siteUid));
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
            FieldsMap.put("installDate", WebUtil.getMessage("installDate"));           //启用日期  AssetInfo.installDate

        }
        return FieldsMap;
    }

    public List<QrCodeTemplateElement> getElementsValue(QrCodeTemplate template, List<QrCodeTemplateElement> elements, String qrCode, String SiteUid) {
        AssetInfo asset = assetDao.getByQrCodeAndSiteUid(qrCode, SiteUid);
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
            
			if(!"".equals(SiteUid)) asset.setSiteUID(SiteUid);
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
                    case "installDate":
                        item.setTextShowValue(item.getTextValue().concat(asset.getInstallDate() == null ? "" : TimeUtils.getStrDate(asset.getInstallDate(), "yyyy-MM-dd")));
                        break;
                }
            }
        }

        return elements;

    }

    public StreamedContent exportQrCodeListByAssets(List<AssetInfo> assetList, String templateId) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);
        QrCodeTemplate template = Strings.isNullOrEmpty(templateId) ? null : templateDao.findById(templateId);
        assetList.forEach(item -> {
            if (!Strings.isNullOrEmpty(item.getQrCode())) {
                try {
                    BufferedImage image = getQrCodeImage(item.getQrCode(), null, template == null ? getTemplateBySiteUid(item.getSiteUID()) : template, item.getSiteUID());
                    String fileName = Optional.ofNullable(item.getClinicalDeptName()).orElse("").concat("_").concat(item.getName()).concat("_").concat(item.getQrCode()).concat(".jpg");
                    out.putNextEntry(new ZipEntry(fileName));
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

    public StreamedContent exportQrCodeList(List<QrCodeLib> exportList) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);

        exportList.forEach(item -> {
            try {
                BufferedImage image = getQrCodeImage(item);
                out.putNextEntry(new ZipEntry(item.getQrCode().concat(".jpg")));
                ImageIO.write(image, "JPG", out);
                out.closeEntry();
            } catch (Exception e) {
                WebUtil.addErrorMessage(item.getQrCode() + "export fail;");
            }
        });
        out.close();

        DefaultStreamedContent zipFile = new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()));
        zipFile.setName("qrCodes.zip");
        return zipFile;
    }

    List<QrCodeTemplateElement> getElementsValue(QrCodeTemplate template, List<QrCodeTemplateElement> elements) {

        return getElementsValue(template, elements, "0123456789123456", "");
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
     * 设置时检查, 不合法返回空值, 三码合一时创建新码
     *
     * @param qrcode
     * @param siteUid
     * @return
     */
    public String getAvailableQrcode(String qrcode, String siteUid) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return "";
        }
        if (qrcode.length() > 16) {
            return "";
        }

        WorkflowConfig config = configService.findConfigBySite(siteUid);
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode, siteUid);
        if (config != null && "DFQ".equals(config.getAssetCodeConfig())) {
            if (qrCodeLib == null) {
                createSpecialQrCode(qrcode, siteUid);
                return qrcode;
            }
            if (qrCodeLib.getStatus() == 1) {
                return qrcode;
            }

        } else {
            if (qrCodeLib == null) {
                return "";
            }
            if (qrCodeLib.getStatus() == 1) {
                return qrcode;
            }
        }

        return "";
    }

    public String checkAvailableBeforeSave(String qrcode, String siteUid) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return "Normal";
        }
        if (qrcode.length() > 16) {
            return "TooLong";
        }
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode, siteUid);
        if (qrCodeLib == null) {
            return "InvalidQRCode";
        }
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
                return "AlreadyUsingQrCode";
        }
        return "Normal";
    }

    /**
     * Return "Normal" is available;
     *
     * @param qrcode
     * @return
     */
    public String isAvailableQrcode(String qrcode, String siteUid) {
        WorkflowConfig config = configService.findConfigBySite(siteUid);
        if (Strings.isNullOrEmpty(qrcode)) {
            return "Normal";
        }
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode, siteUid);
        if (config != null && "DFQ".equals(config.getAssetCodeConfig())) {
            if (qrCodeLib == null) {
                createSpecialQrCode(qrcode, siteUid);
                return "Normal";
            }

            if (!qrCodeLib.getSiteUID().equals(siteUid)) {
                return "WrongHospitalQRCode";
            }

            if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
                return "AlreadyUsingQrCode";
            }

            return "Normal";

        } else {
            if (Strings.isNullOrEmpty(qrcode)) {
                return "Normal";
            }
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

    public void createQrCodeIfNotExists(String qrCode, OrgInfo currentOrg, int status) {

        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, currentOrg.getUid());

        if (qrCodeLib == null) {
            createSpecifiedQrCode(qrCode, currentOrg, status);
        }

    }

    public boolean createSpecialQrCode(String qrcode, String siteUid) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode, siteUid);
        if (null != qrCodeLib) {
            return false;
        }
        OrgInfo org = orgInfoDao.findByUid(siteUid);
        qrCodeLib = new QrCodeLib();
        qrCodeLib.setQrCode(qrcode);
        qrCodeLib.setTenantUID(org.getTenantUID());
        qrCodeLib.setInstitutionUID(org.getInstitutionUID());
        qrCodeLib.setHospitalUID(org.getHospitalUID());
        qrCodeLib.setSiteUID(siteUid);
        qrCodeLib.setStatus(1);
        qrCodeLib.setIssueDate(new Date());
//        qrCodeLib.setSiteId(org.getSiteId());
//        qrCodeLib.setHospitalId(org.getId());
        qrCodeLibDao.save(qrCodeLib);
        return true;
    }

    public void forceRemoveQrcode(String qrCode, String siteUid) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, siteUid);
        if (null == qrCodeLib) {
            return;
        }
        qrCodeLibDao.delete(qrCodeLib);
    }

    public boolean deleteQrcode(String qrCode, String siteUid) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, siteUid);
        if (null == qrCodeLib) {
            return true;
        }
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2) {
            return false;
        }
        List<AssetInfo> assetList = assetDao.findByQrCodeAndSiteUID(qrCode, siteUid);
        if (null != assetList && !assetList.isEmpty()) {
            return false;
        }
//        List<SearchFilter> searchFilterList = Arrays.asList(new SearchFilter("arrivalList.siteUID", SearchFilter.Operator.EQ, siteUid),
//                new SearchFilter("qrCode", SearchFilter.Operator.EQ, qrCode));
//        List<ArrivalListItem> ArrivalListItemList = itemDao.findBySearchFilter(searchFilterList);
//        if (null != ArrivalListItemList && !ArrivalListItemList.isEmpty()) {
//            return false;
//        }
        //TODO v2_blobObject
        qrCodeLibDao.delete(qrCodeLib);
        return true;
    }

    public boolean isQrcodeAvailableInStatus(String qrCode, String siteUid, int status) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode, siteUid);
        if (null == qrCodeLib) {
            return false;
        }

        if (status == qrCodeLib.getStatus()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean updateExistingQrCodeNotUsed(String qrCode, OrgInfo currentOrg) {

        QrCodeLib qrCodeLib = findByQrCode(qrCode, currentOrg.getUid());

        if (qrCodeLib == null) {
            return false;
        }
        if (qrCodeLib.getStatus() != 1) {
            return false;
        }
        qrCodeLib.setStatus(3);
        qrCodeLibDao.save(qrCodeLib);

        return true;

    }

    public void releaseQrCodeInUse(String qrCode, String siteUid) {
        QrCodeLib qrCodeLib = findByQrCode(qrCode, siteUid);
        if (qrCodeLib != null) {
            int status = qrCodeLib.getStatus();
            if (status == 3) {
                qrCodeLib.setStatus(1);
                qrCodeLibDao.save(qrCodeLib);
            }

        }
    }
    
    public void releaseQrCode(String qrCode, String siteUid) {
        QrCodeLib qrCodeLib = findByQrCode(qrCode, siteUid);
        if (qrCodeLib != null) {
            if (qrCodeLib.getStatus() != 1) {
                qrCodeLib.setStatus(1);
                qrCodeLib.setAssetGroup(null);
                qrCodeLib.setAssetName(null);
                qrCodeLib.setComment(null);
                qrCodeLib.setFeedback(null);
                qrCodeLib.setModelNum(null);
                qrCodeLib.setIssueDate(new Date());
                qrCodeLib.setSerialNum(null);
                qrCodeLib.setSubmitDate(null);
                qrCodeLib.setSubmitWechatId(null);
                qrCodeLib.setUserId(null);
                qrCodeLibDao.save(qrCodeLib);
                List<V2_BlobObject> blobObjectsList=  blobObjectService.findBlobObjectsByBoUid(qrCodeLib.getUid());
                blobObjectsList.forEach(item->{
                    blobObjectService.deleteBlobFile(item);
                });
            }
        }
    }

    public QrCodeLib findByQrCode(String qrCode, String siteUid) {
        return qrCodeLibDao.findByQrCode(qrCode, siteUid);
    }
}
