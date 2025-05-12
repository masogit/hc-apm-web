package com.ge.apm.view.measuring;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.dao.OCRTemplateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.OCRTemplateService;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.ocr.support.BusinessType;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class MeasuringCertificateDetailController extends GenericCRUDUUIDController<MeasuringCertificateDetail> {
    private static final String MEASURING_STATUS= "measuringStatus";

    private MeasuringCertificateDetailRepository dao;

    private UploadedFile file;

    private String fileName;
    private byte[] contents;
    private String contentType;

    private UserAccount currentUser;

    private AssetInfo assetInfo;

    private OCRTemplateService templateService;

    private AssetInfoRepository assetInfoRepository;

    private  I18nMessageService i18nMessageService;

    private MeasuringService measuringService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    private Pattern pattern = Pattern.compile("\\d+");

    private OCRTemplateRepository ocrTemplateRepository;

    private SystemEventAuditAopAspect systemEventAuditUtil;

    private List<OCRTemplate> ocrTemplates;

    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(MeasuringCertificateDetailRepository.class);
        templateService = WebUtil.getBean(OCRTemplateService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        measuringService = WebUtil.getBean(MeasuringService.class);
        ocrTemplateRepository = WebUtil.getBean(OCRTemplateRepository.class);
        systemEventAuditUtil = WebUtil.getBean(SystemEventAuditAopAspect.class);
    }

    public void uploadDataFile(FileUploadEvent event) {
        try {
            logger.info("uploadDataFile");
            file = event.getFile();
            fileName = file.getFileName();
            contentType = file.getContentType();
            contents = file.getContents();
            List<OCRTemplate> ocrTemplates = getOCRTemplates();
            if(CollectionUtils.isEmpty(ocrTemplates)){
                WebUtil.addSuccessMessage("计量报告上传成功");
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("type", BusinessType.MEASURE.name());
            if(fileName.toLowerCase().endsWith("pdf")){
               JSONObject jsonObject = templateService.upload(file, GatewayURL.OCR_DOCUMENT,params);
               JSONArray data = jsonObject.getJSONArray("data");
               if(data != null && data.size() > 0){
                   JSONObject object= data.getJSONObject(0);
                   setObject(object);
               }
//               String previewResult= JSON.toJSONString(data.subList(0, data.size() - 1), SerializerFeature.PrettyFormat);
//               System.out.println("previewResult:" + previewResult);
//               String source=JSON.toJSONString(data.getJSONObject(data.size() - 1),SerializerFeature.PrettyFormat);
//               System.out.println("source:" + source);
            }else{
                JSONObject jsonObject = templateService.upload(file,GatewayURL.OCR_IMAGE,params);
                JSONObject data = jsonObject.getJSONObject("data");
                Object remove = data.remove("source");
//                String source=JSON.toJSONString(remove,SerializerFeature.PrettyFormat);
//                System.out.println("source:" + source);
//                String  previewResult=JSON.toJSONString(data,SerializerFeature.PrettyFormat);
//                System.out.println("previewResult:" + previewResult);
                if(data != null && data.size() > 0){
                    setObject(data);
                }
            }
            WebUtil.addSuccessMessage("计量报告上传识别成功");
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
            WebUtil.addErrorMessage("计量报告上传失败");
        }
    }
    public void setObject(JSONObject object){
        MeasuringCertificateDetail selected1 = this.selected;
        this.selected.setCertificateNo(object.getString("certificateNo"));
        this.selected.setAssetManufacturer(object.getString("assetManufacturer"));
        this.selected.setCheckPerson(object.getString("checkPerson"));
        this.selected.setAssetModel(object.getString("assetModel"));
        this.selected.setAssetSN(object.getString("assetSN"));
        this.selected.setAssetName(object.getString("assetName"));
        this.selected.setCheckCompany(object.getString("checkCompany"));
        this.selected.setDepartment(object.getString("department"));
        String status = object.getString("status");
        List<I18nMessage> byMsgType = i18nMessageService.getByMsgType(MEASURING_STATUS);
        Optional<I18nMessage> first = byMsgType.stream().filter(item -> item.getValue().equals(status)).findFirst();
        if(first.isPresent()){
            I18nMessage i18nMessage = first.get();
            this.selected.setStatus(Integer.valueOf(i18nMessage.getMsgKey()));
        }
        try {
            String validDateToStr = "";
            String validDateToVar = object.getString("validDateTo");
            Matcher matcher = pattern.matcher(validDateToVar);
            while (matcher.find()) {
                validDateToStr += matcher.group();
            }
            Date validDateTo = format.parse(validDateToStr);
            this.selected.setValidDateTo(validDateTo);
        } catch (ParseException e) {
           e.printStackTrace();
        }
        try {
            String checkDateStr = "";
            String checkDateVar = object.getString("checkDate");
            Matcher matcher = pattern.matcher(checkDateVar);
            while (matcher.find()) {
                checkDateStr += matcher.group();
            }
            Date checkDate = format.parse(checkDateStr);
            this.selected.setCheckDate(checkDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        handleValueChangeAssetSN();
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    @Override
    public void save() {
        try {
            logger.info("save:"+selected.toString());
            if(assetInfo == null){
                WebUtil.addErrorMessage("请输入 设备序列号SN|资产编号|二维码|设备编号 绑定设备信息");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
            String certificateNo = selected.getCertificateNo();
            if(Objects.isNull(certificateNo)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("证书编号不能为空");
                return;
            }
            List<SearchFilter> searchFilterList = new ArrayList<>();
            searchFilterList.add(new SearchFilter("certificateNo", SearchFilter.Operator.EQ,certificateNo));
            searchFilterList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,currentUser.getTenantUID()));
            List<MeasuringCertificateDetail> measuringCertificateDetails = dao.findBySearchFilter(searchFilterList);
            SystemEventAuditEnum eventAuditEnum = null;
            if(!CollectionUtils.isEmpty(measuringCertificateDetails)){
                MeasuringCertificateDetail detail = measuringCertificateDetails.get(0);
                BeanUtils.copyProperties(selected, detail,measuringService.beanNullProperty(selected));
                dao.save(detail);
                eventAuditEnum = SystemEventAuditEnum.MEASURE_UPDATE;
            }else{
                dao.save(selected);
                eventAuditEnum = SystemEventAuditEnum.MEASURE_CREATE;
            }
            measuringService.updateMeasuringDate(assetInfo);
            templateService.saveTemplateFile(contents,fileName,contentType, V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring,selected.getId());
            WebUtil.addSuccessMessage("保存成功");
            SystemEventAudit systemEventAudit = systemEventAuditUtil.buildEventAudit(currentUser,eventAuditEnum, selected.getId(), Boolean.TRUE, "","measuring", JSONArray.toJSONString(selected));
            systemEventAuditUtil.sendSystemAuditMessage(systemEventAudit);
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("保存失败");
        }
    }



    @Override
    public void delete() {
        try {
            WebUtil.addSuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addSuccessMessage("删除失败");
        }
    }

    @Override
    public void onBeforeNewObject(MeasuringCertificateDetail object) {
        object.setCreatedBy(currentUser.getUid());
        object.setCreatedDate(new Date());
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
        this.assetInfo = null;
        this.file = null;
        this.contents = null;
        this.contentType = null;
        this.fileName = null;
    }

    public void handleValueChangeAssetSN() {
        // 设备序列号
        String assetSN = this.selected.getAssetSN();
        if(StringUtils.isEmpty(assetSN)) return;
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("serialNum", SearchFilter.Operator.EQ,assetSN));
        List<AssetInfo> assetInfos = getAssetInfos(searchFilterList);
        handlerAssetInfo(assetInfos,"设备序列号:"+assetSN);
    }
    public void handleValueChangeFinanceNo() {
        // 资产编码
        String financeNo = this.selected.getFinanceNo();
        if(StringUtils.isEmpty(financeNo)) return;
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("financingNum", SearchFilter.Operator.EQ,financeNo));
        List<AssetInfo> assetInfos = getAssetInfos(searchFilterList);
        handlerAssetInfo(assetInfos,"资产编码:"+financeNo);
    }
    public void handleValueChangeQrCode() {
        // 二维码
        String qrCode = this.selected.getQrCode();
        if(StringUtils.isEmpty(qrCode)) return;
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("qrCode", SearchFilter.Operator.EQ,qrCode));
        List<AssetInfo> assetInfos = getAssetInfos(searchFilterList);
        handlerAssetInfo(assetInfos,"二维码:"+qrCode);
    }
    public void handleValueChangeDepartNum() {
        // 设备编码
        String departNum = this.selected.getDepartNum();
        if(StringUtils.isEmpty(departNum)) return;
        List<SearchFilter> searchFilterList = new ArrayList<>();
        searchFilterList.add(new SearchFilter("departNum", SearchFilter.Operator.EQ,departNum));
        List<AssetInfo> assetInfos = getAssetInfos(searchFilterList);
        handlerAssetInfo(assetInfos,"设备编码:"+departNum);
    }
    public void handlerAssetInfo(List<AssetInfo> assetInfos,String msg){
        if(CollectionUtils.isEmpty(assetInfos)){
            WebUtil.addWarnMessage(msg+",未匹配到设备");
            return;
        }
        if(assetInfos.size() > 1){
            WebUtil.addWarnMessage(msg+",匹配到"+assetInfos.size()+"台设备,请确认后重新输入");
            return;
        }
        AssetInfo assetInfo = assetInfos.get(0);
        this.assetInfo =assetInfo;
        this.selected.setAssetUID(assetInfo.getUid());

        this.selected.setQrCode(assetInfo.getQrCode());
        this.selected.setDepartNum(assetInfo.getDepartNum());
        this.selected.setAssetName(assetInfo.getName());
        String assetManufacturer = this.selected.getAssetManufacturer();
        if(Objects.isNull(assetManufacturer)){
            this.selected.setAssetManufacturer(assetInfo.getManufacture());
        }
        String assetSN = this.selected.getAssetSN();
        if(Objects.isNull(assetSN)){
            this.selected.setAssetSN(assetInfo.getSerialNum());
        }
    }

    public List<AssetInfo> getAssetInfos(List<SearchFilter> searchFilterList ) {
        searchFilterList.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ,currentUser.getSiteUID()));
        return assetInfoRepository.findBySearchFilter(searchFilterList);
    }
    @Override
    protected GenericRepositoryUUID<MeasuringCertificateDetail> getDAO() {
        return dao;
    }

    public void setAssetInfo(AssetInfo assetInfo) {
        this.assetInfo = assetInfo;
    }

    public AssetInfo getAssetInfo() {
        return assetInfo;
    }

    public List<OCRTemplate> getOCRTemplates() {
        if(CollectionUtils.isEmpty(ocrTemplates)){
            List<SearchFilter> searchFilters = new ArrayList<>();
            searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ,currentUser.getTenantUID()));
            searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ,BusinessType.MEASURE.name()));
            ocrTemplates = ocrTemplateRepository.findBySearchFilter(searchFilters);

        }
        return ocrTemplates;
    }

    public void removeAttachment(){
        this.file = null;
        this.contents = null;
        this.contentType = null;
        this.fileName = null;
    }

}
