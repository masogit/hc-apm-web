package com.ge.apm.view.measuring;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.aop.SystemEventAuditAopAspect;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.MeasuringCertificateDetailRepository;
import com.ge.apm.dao.OCRTemplateRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.service.OCRTemplateService;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import com.ge.healthcare.cn.common.llm.ApplicationType;
import com.ge.healthcare.cn.common.llm.Result;
import com.ge.healthcare.cn.common.llm.WorkflowResponse;
import com.ge.healthcare.cn.common.ocr.support.BusinessType;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class MeasuringCertificateDetailController extends GenericCRUDUUIDController<MeasuringCertificateDetail> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasuringCertificateDetailController.class);
    private static final String MEASURING_STATUS= "measuringStatus";

    private MeasuringCertificateDetailRepository dao;


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

    private List<StreamedContent> streamedContents = new ArrayList<>();

    private ConfigItemService configItemService;

    private String identifyPatterns;

    private String identifyPostProcessorRule;

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
        configItemService = WebUtil.getBean(ConfigItemService.class);
        identifyPatterns = configItemService.getConfigItemByKeyToString("identifyPatterns", currentUser);
        identifyPostProcessorRule = configItemService.getConfigItemByKeyToString("identifyPostProcessorRule", currentUser);
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        if("Create".equals(actionName)){
            try {
                super.prepareCreate();
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        getOCRTemplates();
    }

    public void uploadDataFile(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String fileName = file.getFileName();
        try {
            logger.info("uploadDataFile");
//            List<OCRTemplate> ocrTemplates = getOCRTemplates();
//            if(CollectionUtils.isEmpty(ocrTemplates)){
//                storageFile(file);
//                WebUtil.addSuccessMessage("上传成功");
//                return;
//            }
            Map<String, Object> params = new HashMap<>();
            if("AI".equalsIgnoreCase(identifyPatterns)){
                params.put("type", ApplicationType.FORM_OCR_WORKFLOWS.name());
                JSONObject jsonObject = templateService.upload(file, GatewayURL.FILES_UPLOAD_AND_WORKFLOWS_RUN,params,null);
                if(jsonObject != null){
                    WorkflowResponse workflowResponse = jsonObject.toJavaObject(WorkflowResponse.class);
                    Result result = workflowResponse.getData();
                    JSONObject outputs = result.getOutputs();
                    JSONObject output = outputs.getJSONObject("output");
                    JSONObject content = output.getJSONObject("content");
                    setObject(content);
                }
            }else if(fileName.toLowerCase().endsWith("pdf")){
                List<OCRTemplate> ocrTemplates = getOCRTemplates();
                if(CollectionUtils.isEmpty(ocrTemplates)){
                    storageFile(file);
                    WebUtil.addSuccessMessage("文件上传成功,缺少本地配置无法OCR识别!");
                    return;
                }
                params.put("type", BusinessType.MEASURE.name());
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
                params.put("type", BusinessType.MEASURE.name());
                JSONObject jsonObject = templateService.upload(file,GatewayURL.OCR_IMAGE,params);
                JSONObject data = jsonObject.getJSONObject("data");
                if(data != null){
                    Object remove = data.remove("source");
                    setObject(data);
                }

//                String source=JSON.toJSONString(remove,SerializerFeature.PrettyFormat);
//                System.out.println("source:" + source);
//                String  previewResult=JSON.toJSONString(data,SerializerFeature.PrettyFormat);
//                System.out.println("previewResult:" + previewResult);

            }
            storageFile(file);
            WebUtil.addSuccessMessage("计量报告上传识别成功");
        }catch (BusinessException e){
            logger.error(e);
            WebUtil.addErrorMessage(e.getMessage()+" 请上传计量报告文件!");
        }
        catch (Exception e) {
            logger.error(e);
            WebUtil.addErrorMessage("识别失败!");
        }
    }
    public void postProcessorJSON(JSONObject data){
        try {
            if(StringUtils.isEmpty(identifyPostProcessorRule)) return;
            JSONObject ruleJSON = JSONObject.parseObject(identifyPostProcessorRule);
            LOGGER.info("data:{},ruleJSON:{}",data,ruleJSON);
            if(ruleJSON == null || ruleJSON.isEmpty()) return;
            for (Map.Entry<String, Object> stringObjectEntry : ruleJSON.entrySet()) {
                String key = stringObjectEntry.getKey();
                String regex =(String) stringObjectEntry.getValue();
                String value =(String) data.get(key);
                if(StringUtils.isEmpty(value))continue;
                Pattern compile = Pattern.compile(regex);
                Matcher matcher = compile.matcher(value);
                if(matcher.find()){
                    String group = matcher.group(1);
                    data.put(key,group);
                }
            }
            LOGGER.info("data:{}",data);
        } catch (Exception e) {
        }

    }
    public void setObject(JSONObject object){
        postProcessorJSON(object);
        String certificateNo = object.getString("certificateNo");
        if(certificateNo == null){
            certificateNo = object.getString("cetification_id");
        }
        if(StringUtils.isEmpty(certificateNo)){
            this.selected = new MeasuringCertificateDetail();
            throw new BusinessException("证件编号为空");
        }
        this.selected.setCertificateNo(certificateNo);
        String assetManufacturer = object.getString("assetManufacturer");
        if(assetManufacturer == null){
            assetManufacturer = object.getString("manufactory");
        }
        this.selected.setAssetManufacturer(assetManufacturer);
        String checkPerson = object.getString("checkPerson");
        if(checkPerson == null){
            checkPerson = object.getString("checked_by");
        }
        this.selected.setCheckPerson(checkPerson);
        String assetModel = object.getString("assetModel");
        if(assetModel == null){
            assetModel = object.getString("pattern");
        }
        this.selected.setAssetModel(assetModel);
        String assetSN = object.getString("assetSN");
        if(assetSN == null){
            assetSN = object.getString("serial_num");
        }
        this.selected.setAssetSN(assetSN);
        String assetName = object.getString("assetName");
        if(assetName == null){
            assetName = object.getString("device_name");
        }
        this.selected.setAssetName(assetName);
        String checkCompany = object.getString("checkCompany");
        if(checkCompany == null){
            checkCompany = object.getString("check_company");
        }
        this.selected.setCheckCompany(checkCompany);
        String department = object.getString("department");
        if(department == null){
            department = object.getString("applicant");
        }
        if(StringUtils.isEmpty(department)){
            this.selected = new MeasuringCertificateDetail();
            throw new BusinessException("送检单位为空");
        }
        this.selected.setDepartment(department);
        String status = object.getString("status");
        if(ObjectUtil.isEmpty(status)){
             status = object.getString("conclusion");
        }
        if(ObjectUtil.isNotEmpty(status)){
            String conclusion = status;
            List<I18nMessage> byMsgType = i18nMessageService.getByMsgType(MEASURING_STATUS);
            Optional<I18nMessage> first = byMsgType.stream().filter(item -> item.getValue().equals(conclusion)).findFirst();
            if(first.isPresent()){
                I18nMessage i18nMessage = first.get();
                this.selected.setStatus(Integer.valueOf(i18nMessage.getMsgKey()));
            }
        }
        try {
            String validDateToStr = "";
            String validDateToVar = object.getString("validDateTo");
            if(StringUtils.isEmpty(validDateToVar)){
                validDateToVar = object.getString("valid_util");
            }
            if(ObjectUtil.isNotEmpty(validDateToVar)){
                Matcher matcher = pattern.matcher(validDateToVar);
                while (matcher.find()) {
                    validDateToStr += matcher.group();
                }
                Date validDateTo = format.parse(validDateToStr);
                this.selected.setValidDateTo(validDateTo);
            }


        } catch (ParseException e) {
           e.printStackTrace();
        }
        try {
            String checkDateStr = "";
            String checkDateVar = object.getString("checkDate");
            if(StringUtils.isEmpty(checkDateVar)){
                checkDateVar = object.getString("verify_date");
            }
            if(ObjectUtil.isNotEmpty(checkDateVar)) {
                Matcher matcher = pattern.matcher(checkDateVar);
                while (matcher.find()) {
                    checkDateStr += matcher.group();
                }
                Date checkDate = format.parse(checkDateStr);
                this.selected.setCheckDate(checkDate);
            }else{
                this.selected = new MeasuringCertificateDetail();
                throw new BusinessException("检定日期为空");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        handleValueChangeAssetSN();
    }


    @Override
    public void save() {
        try {
            MeasuringCertificateDetail detail = selected;
            logger.info("save:"+detail.toString());
//            if(assetInfo == null){
//                WebUtil.addErrorMessage("请输入 设备序列号SN|资产编号|二维码|设备编号 绑定设备信息");
//                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//                return;
//            }
            String certificateNo = detail.getCertificateNo();
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
                detail = measuringCertificateDetails.get(0);
                BeanUtils.copyProperties(selected, detail,measuringService.beanNullProperty(selected));
                dao.save(detail);
                eventAuditEnum = SystemEventAuditEnum.MEASURE_UPDATE;
            }else{
                dao.save(detail);
                eventAuditEnum = SystemEventAuditEnum.MEASURE_CREATE;
            }
            if(assetInfo!=null){
                measuringService.updateMeasuringDate(assetInfo);

            }
            for (StreamedContent item : streamedContents) {
                templateService.savFile(IOUtils.toByteArray(item.getStream()),item.getName(),item.getContentType(), V2_BlobObject.BO_SUB_TYPE_AssetInfo_Measuring,detail.getId());
            }
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
        this.streamedContents = new ArrayList<>();
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
    public String getAllowTypes(){
        List<OCRTemplate> ocrTemplates = getOCRTemplates();
        if(CollectionUtils.isEmpty(ocrTemplates)){
            return "/(\\.|\\/)([a-zA-Z0-9.]+)$/";
        }else{
            return "/(\\.|\\/)(png|PNG|jpeg|JPEG|pdf|PDF)$/";
        }

    }

    public void removeAttachment(StreamedContent streamedContent){
        streamedContents.remove(streamedContent);
    }

    public String createAction() {
        return UrlEncryptController.encodeUrlParam("measuringCreateDialog?action=Create");
    }
    public String getListPageLink() {
        return "measuringResultMain?faces-redirect=true";
    }


    public List<StreamedContent> getStreamedContents() {
        return streamedContents;
    }

    public void setStreamedContents(List<StreamedContent> streamedContents) {
        this.streamedContents = streamedContents;
    }

    private void storageFile(UploadedFile file){
        byte[] contents = file.getContents();
        ByteArrayContent fileStream = new ByteArrayContent(contents,file.getContentType(),file.getFileName(),contents.length);
        streamedContents.add(fileStream);
    }

    public void downloadFile(StreamedContent streamedContent) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + streamedContent.getName());
            StreamUtils.copy(streamedContent.getStream(), response.getOutputStream());
            facesContext.responseComplete();
            facesContext.renderResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public StreamedContent getStreamedContentImg(StreamedContent streamedContent) {
        if(streamedContent.getContentType().contains("image")){
            return streamedContent;
        }
        String name = streamedContent.getName();
        String type = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        String defaultImage = MicroServiceUtil.getDefaultImage(type);
        String replace = defaultImage.replace("data:image/png;base64,", "");
        byte[] bytes = Base64Utils.decodeFromString(replace);
        ByteArrayContent byteArrayContent = new ByteArrayContent(bytes,"image/jpeg");
        return byteArrayContent;
    }

}
