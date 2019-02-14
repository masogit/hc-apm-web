/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.QrCodeTemplateRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import com.ge.apm.domain.QrCodeTemplate;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.TimeUtils;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        return getQrCodeImage(null, null, qrCode, siteUid);
    }

    public BufferedImage getQrCodeImage(QrCodeLib qrCodeLib) {
        return getQrCodeImage(null, null, qrCodeLib.getQrCode(), qrCodeLib.getSiteUID());
    }

    public BufferedImage getQrCodeImage(String qrCode, AssetInfo asset, String siteUid) {
        return getQrCodeImage(null, asset, qrCode, siteUid);
    }

    public BufferedImage getQrCodeImageWithTemplate(String qrCode, AssetInfo asset, String templateId, String siteUid) {
        if (Strings.isNullOrEmpty(templateId)) {
            return getQrCodeImage(null, asset, qrCode, asset.getSiteUID());
        }
        QrCodeTemplate template = templateDao.findById(templateId);
        return getQrCodeImage(template, asset, qrCode, siteUid);
    }

    private BufferedImage getQrCodeImage(QrCodeTemplate template, AssetInfo asset, String qrCode, String siteUid) {

        if (template == null) {
            if (null != asset && !Strings.isNullOrEmpty(asset.getSiteUID())) {
                template = getTemplateBySiteUid(asset.getSiteUID());
            } else if (!Strings.isNullOrEmpty(siteUid)) {
                template = getTemplateBySiteUid(siteUid);
            }

            if (template == null) {
                template = templateService.getDefaultTemplate();
            }
        }

        if (null == asset) {
            if (!Strings.isNullOrEmpty(siteUid) && !Strings.isNullOrEmpty(qrCode)) {
                asset = assetDao.getByQrCodeAndSiteUid(qrCode, siteUid);
            }
            if (null == asset) {
                asset = getDefaultAsset();
                asset.setQrCode(qrCode);
                asset.setSiteUID(Strings.isNullOrEmpty(siteUid) ? template.getSiteUID() : siteUid);
            }
        }

        JSONObject qrCodeObject = template.getJSONObject();

        //XXX 迁移后删除
        if (null == qrCodeObject) {
            qrCodeObject = templateService.getJSONObjectByTemplate(template);
        }

        BufferedImage img = null;
        try {
            qrCodeObject = fillElementsValue(qrCodeObject, asset);
            img = qrCodeDrawService.createQrCodeTag(qrCodeObject);
        } catch (Exception ex) {
            Logger.getLogger(QrCodeTemplateService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

    private AssetInfo getDefaultAsset() {
        //Demo asset for preview
        AssetInfo asset = new AssetInfo();
        asset.setAssetGroup(1);
        asset.setName("(待定)");
        asset.setSerialNum("(待定)");
        asset.setFinancingNum("(待定)");
        asset.setDepartNum("(待定)");
        asset.setModalityId("(待定)");
        asset.setFunctionType("(待定)");
        return asset;
    }

    private JSONObject fillElementsValue(JSONObject qrCodeObject, AssetInfo asset) {

        qrCodeObject.getJSONObject("QrCodeElements").put("TextValue", Strings.isNullOrEmpty(asset.getQrCode()) ? "" : asset.getQrCode());

        qrCodeObject.getJSONArray("Elements").forEach(item -> {
            JSONObject itemObject = (JSONObject) item;
            if (!Strings.isNullOrEmpty(itemObject.getString("FieldKey"))) {
                itemObject.putIfAbsent("TextValue", "");
                String textValue = itemObject.getString("TextValue");
                switch (itemObject.getString("FieldKey")) {
                    case "blank":
                        itemObject.put("TextShowValue", textValue);
                        break;
                    case "name":
                        itemObject.put("TextShowValue", textValue.concat(asset.getName() == null ? "" : asset.getName()));
                        break;
                    case "assetGroup":
                        itemObject.put("TextShowValue", textValue.concat(FieldValueMessageController.fieldValue("assetGroup", asset.getAssetGroup().toString())));
                        break;
                    case "hospital":
                        if (qrCodeObject.getString("HospitalName") != null) {
                            itemObject.put("TextShowValue", textValue.concat(qrCodeObject.getString("HospitalName")));
                            break;
                        }
                        String siteUid = asset.getSiteUID();
                        if (siteUid == null) {
                            siteUid = qrCodeObject.getString("SiteUID");
                        }
                        if (siteUid == null) {
                            siteUid = UserContextService.getCurrentUserAccount().getSiteUID();
                        }
                        OrgInfo org = orgInfoDao.getByUid(siteUid);
                        itemObject.put("TextShowValue", textValue.concat(org.getName() == null ? "" : org.getName()));
                        break;
                    case "functionType":
                        itemObject.put("TextShowValue", textValue.concat(asset.getFunctionType() == null ? "" : asset.getFunctionType()));
                        break;
                    case "clinicalDept":
                        if (asset.getClinicalDeptName() != null) {
                            itemObject.put("TextShowValue", textValue.concat(asset.getClinicalDeptName()));
                        } else if (asset.getClinicalDeptUID() != null) {
                            OrgInfo clinicalDept = orgInfoDao.getByUid(asset.getClinicalDeptUID());
                            itemObject.put("TextShowValue", textValue.concat(clinicalDept.getName() == null ? "" : clinicalDept.getName()));
                        } else {
                            itemObject.put("TextShowValue", textValue.concat("(待定)"));
                        }
                        break;
                    case "serialNum":
                        itemObject.put("TextShowValue", textValue.concat(asset.getSerialNum() == null ? "" : asset.getSerialNum()));
                        break;
                    case "financingNum":
                        itemObject.put("TextShowValue", textValue.concat(asset.getFinancingNum() == null ? "" : asset.getFinancingNum()));
                        break;
                    case "departNum":
                        itemObject.put("TextShowValue", textValue.concat(asset.getDepartNum() == null ? "" : asset.getDepartNum()));
                        break;
                    case "modalityId":
                        itemObject.put("TextShowValue", textValue.concat(asset.getModalityId() == null ? "" : asset.getModalityId()));
                        break;
                    case "makerShortName":
                        itemObject.put("TextShowValue", textValue.concat(asset.getMakerShortName() == null ? "" : asset.getMakerShortName()));
                        break;
                    case "systemId":
                        itemObject.put("TextShowValue", textValue.concat(asset.getSystemId() == null ? "" : asset.getSystemId()));
                        break;
                    case "installDate":
                        itemObject.put("TextShowValue", textValue.concat(asset.getInstallDate() == null ? "" : TimeUtils.getStrDate(asset.getInstallDate(), "yyyy-MM-dd")));
                        break;
                }
            }
        });

        return qrCodeObject;
    }

    public QrCodeTemplate getTemplateBySiteUid(String siteUid) {
        OrgInfo siteOrg = orgDao.findByUid(siteUid);
        return siteOrg == null ? null : Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getSiteUID()))
                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getHospitalUID()))
                        .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getInstitutionUID()))
                                .orElse(Optional.ofNullable(templateDao.findSiteTemplate(siteOrg.getTenantUID())).orElse(null))));
    }

    public String getQrCodeImageBase64(String qrCode, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(null, null, qrCode, siteUid));
    }

    public String getQrCodeImageBase64(AssetInfo asset) {
        return qrCodeDrawService.getImageBase64(getQrCodeImage(null, asset, asset.getQrCode(), asset.getSiteUID()));
    }

    public String getQrCodeImageBase64WithTemplate(AssetInfo asset, String templateId, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImageWithTemplate(null, asset, templateId, siteUid));
    }

    public String getQrCodeImageBase64WithTemplate(String qrCode, String templateId, String siteUid) {
        return qrCodeDrawService.getImageBase64(getQrCodeImageWithTemplate(qrCode, null, templateId, siteUid));
    }

    public StreamedContent exportQrCodeListByAssets(List<AssetInfo> assetList, String templateId) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(bout);
        QrCodeTemplate template = Strings.isNullOrEmpty(templateId) ? null : templateDao.findById(templateId);
        assetList.forEach(item -> {
            if (!Strings.isNullOrEmpty(item.getQrCode())) {
                try {
                    BufferedImage image = getQrCodeImage(template == null ? getTemplateBySiteUid(item.getSiteUID()) : template, item, null, null);
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

    public String getPreViewBase64(QrCodeTemplate template) {
        AssetInfo asset = getDefaultAsset();
        asset.setQrCode("0123456789123456");
        asset.setSiteUID(template.getSiteUID());
        return qrCodeDrawService.getImageBase64(getQrCodeImage(template, asset, null, null));
    }

    /**
     * 设置时检查, 不合法返回原因, 三码合一时创建新码
     *
     * @param qrcode
     * @param siteUid
     * @return
     */
    public String getAvailableQrcode(String qrcode, String siteUid) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return "";
        }
        if (qrcode.length() > 64) {
            WebUtil.addErrorMessage("二维码不能超过最大长度64位");
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
                WebUtil.addErrorMessage("无此二维码");
                return "";
            }
            if (qrCodeLib.getStatus() == 1) {
                return qrcode;
            }
        }

        WebUtil.addErrorMessage("二维码已被使用");
        return "";
    }

    public String checkAvailableBeforeSave(String qrcode, String siteUid) {
        if (Strings.isNullOrEmpty(qrcode)) {
            return "Normal";
        }
        if (qrcode.length() > 64) {
            return "TooLong";
        }
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrcode, siteUid);
        if (qrCodeLib == null) {
            return "InvalidQRCode";
        }
        if (qrCodeLib.getStatus() != 1 && qrCodeLib.getStatus() != 2 && qrCodeLib.getStatus() != 5) {
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

    public boolean createSpecialQrCodeWithStatus(String qrcode, String siteUid, int status) {
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
        qrCodeLib.setStatus(status);
        qrCodeLib.setIssueDate(new Date());
//        qrCodeLib.setSiteId(org.getSiteId());
//        qrCodeLib.setHospitalId(org.getId());
        qrCodeLibDao.save(qrCodeLib);
        return true;
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
                List<V2_BlobObject> blobObjectsList = blobObjectService.findBlobObjectsByBoUid(qrCodeLib.getUid());
                blobObjectsList.forEach(item -> {
                    blobObjectService.deleteBlobFile(item);
                });
            }
        }
    }

    public QrCodeLib findByQrCode(String qrCode, String siteUid) {
        return qrCodeLibDao.findByQrCode(qrCode, siteUid);
    }

    //for import Qrcode
    public Map<String, Map<String, Object>> getQrCodeMapFromTemplate(ExcelDocument doc) throws Exception {
        Map<String, Map<String, Object>> result = new LinkedHashMap();
        int keyRow = 1;
        int dataRow = 2;
        String sheetName = doc.getWorkBook().getSheetAt(0).getSheetName();

        for (Map< String, String> rowMap = doc.getStringDataRowMap(sheetName, keyRow, dataRow); rowMap != null; rowMap = doc.getStringDataRowMap(sheetName, keyRow, ++dataRow)) {

            String qrcodeItem = Optional.ofNullable(rowMap.get("二维码")).orElse("").toString();
            if (StringUtils.isEmpty(qrcodeItem)) {
                break; //return if go beyond the valid cell range
            }
            Map< String, Object> dataMap = new LinkedHashMap();
            dataMap.put("RowIndex", String.valueOf(dataRow));
            dataMap.put("QrcodeItem", qrcodeItem);
            result.put(String.valueOf(dataRow), dataMap);
        }

        return result;
    }

    public Map<String, Map<String, Object>> importQrCode(Map<String, Map<String, Object>> importQrCodeMap, OrgInfo site) {
        if (null == site) {
            WebUtil.addErrorMessage("所选院区无效");
            return importQrCodeMap;
        }

        List<QrCodeLib> newQrcodeList = new ArrayList();
        importQrCodeMap.entrySet().stream().forEach(item -> {
            String newQrcode = ((String) (item.getValue().get("QrcodeItem"))).trim();
            if (Strings.isNullOrEmpty(newQrcode)) {
                item.getValue().put("res", "二维码不能为空");
                item.getValue().put("status", "失败");
            } else if (newQrcode.length() > 64) {
                item.getValue().put("res", "二维码超长");
                item.getValue().put("status", "失败");
            } else if (newQrcode.length() < 4) {
                item.getValue().put("res", "二维码不能少于4位");
                item.getValue().put("status", "失败");
            } else if(newQrcodeList.stream().anyMatch(saveditem-> Objects.equals(saveditem.getQrCode(),newQrcode))){
                item.getValue().put("res", "文件中重复的二维码");
                item.getValue().put("status", "失败");
            } else {
                QrCodeLib qrcodeItem = this.findByQrCode(newQrcode, site.getUid());
                if (null != qrcodeItem) {
                    item.getValue().put("res", "已经存在二维码，状态：" + WebUtil.getFieldValueMessage("qrCodeLibStatus", String.valueOf(qrcodeItem.getStatus())));
                    item.getValue().put("status", "失败");
                } else {
                    qrcodeItem = new QrCodeLib();
                    qrcodeItem.setQrCode(newQrcode);
                    qrcodeItem.setTenantUID(site.getTenantUID());
                    qrcodeItem.setInstitutionUID(site.getInstitutionUID());
                    qrcodeItem.setHospitalUID(site.getHospitalUID());
                    qrcodeItem.setSiteUID(site.getSiteUID());
                    qrcodeItem.setStatus(1);
                    qrcodeItem.setIssueDate(new Date());
                    newQrcodeList.add(qrcodeItem);
                    item.getValue().put("res", "导入");
                    item.getValue().put("status", "导入成功");
                }
            }
            
            item.getValue().put("org", site.getName());

        });

        qrCodeLibDao.save(newQrcodeList);
        return importQrCodeMap;
    }
}
