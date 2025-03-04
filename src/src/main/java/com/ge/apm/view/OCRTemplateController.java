package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ge.apm.dao.OCRTemplateRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OCRTemplate;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.OCRTemplateService;
import com.ge.apm.service.asset.QrCodeDrawService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.ocr.support.BusinessType;
import com.ge.healthcare.cn.common.ocr.support.MatcherType;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean(name = "ocrTemplateController")
@ViewScoped
public class OCRTemplateController extends GenericCRUDUUIDController<OCRTemplate> implements Serializable, ServerEventInterface {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OCRTemplateController.class);

    private UserAccount currentUser;

    private OCRTemplateRepository dao;

    private OCRTemplateService templateService;
    @Override
    protected GenericRepositoryUUID<OCRTemplate> getDAO() {
        return dao;
    }

    private OCRTemplate thisTemplate;

    private JSONObject templateJsonObject;

    private String previewResult;
    private String source;

    private String fileName;
    private byte[] contents;
    private String contentType;

    private final static int DEFAULT_WIDTH = 600;
    private final static int DEFAULT_HEIGHT = 850;

    private List<V2_BlobObject> attachList = new ArrayList<>();

    private Map<String,I18nMessage> i18nMessages;

    private Set<String> selectFiled = new HashSet<>();
    private String actionName ;
    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(OCRTemplateRepository.class);
        templateService = WebUtil.getBean(OCRTemplateService.class);
        this.searchFilters = new ArrayList<>();
        initActionData();
//        List<I18nMessage> fieldName = I18nMessageService.getMessageUsingDefaultConfigAsLowestPriority("field_name", currentUser.getSiteId());
//        i18nMessages = fieldName.stream().collect(Collectors.toMap(I18nMessage::getMsgKey,item->item));
    }

    private void initActionData() {
        actionName = WebUtil.getRequestParameter("action");
        if(actionName == null) return;
        if("Create".equals(actionName)){
            thisTemplate = new OCRTemplate();
            thisTemplate.setTenantUID(currentUser.getTenantUID());
            thisTemplate.setCreatedBy(currentUser.getUid());
            thisTemplate.setWidth(DEFAULT_WIDTH);
            thisTemplate.setHeight(DEFAULT_HEIGHT);
            thisTemplate.setCreatedDate(new Date());
            thisTemplate.setBlobObject(new V2_BlobObject());
            templateJsonObject = new JSONObject();
            templateJsonObject.put("Elements",new ArrayList<>());
        }else if("Edit".equals(actionName)){
            String templateId = WebUtil.getRequestParameter("templateId");
            thisTemplate = dao.findById(templateId);
            if(thisTemplate != null){
                List<V2_BlobObject> blobObjectsByBoUid = templateService.findBlobObjectsByBoUid(thisTemplate.getId());
                if(!CollectionUtils.isEmpty(blobObjectsByBoUid)){
                    attachList = blobObjectsByBoUid;
                }
            }
        }
        String jsonText = thisTemplate.getJsonText();
        if(jsonText != null){
            templateJsonObject = JSONObject.parseObject(jsonText);
        }else{
            templateJsonObject = new JSONObject();
            templateJsonObject.put("Elements",new ArrayList<>());
        }
    }


    @Override
    protected Page<OCRTemplate> doLoadData(PageRequest pageRequest) {
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, Arrays.asList(currentUser.getTenantUID(),"-1")));
        Page<OCRTemplate> templatePage = super.doLoadData(pageRequest);
        this.searchFilters.clear();
        return templatePage;
    }
    public void createNewElement() {
        JSONObject newElement = new JSONObject();
        newElement.put("TextValue", "");
        newElement.put("FieldKey", "");
        newElement.put("PositionX", 0);
        newElement.put("PositionY", 0);
        newElement.put("Width", 0);
        newElement.put("Height", 0);
        templateJsonObject.getJSONArray("Elements").add(newElement);
    }
    @Override
    public void save() {
        super.save();
    }
    public void saveChange() {
        conversionPercentage();
        thisTemplate.setJsonText(templateJsonObject.toJSONString());
        thisTemplate.setLastModifiedBy(currentUser.getUid());
        thisTemplate.setLastModifiedDate(new Date());
        dao.save(thisTemplate);
        templateService.saveTemplateFile(contents,fileName,contentType,thisTemplate.getId());
        WebUtil.addSuccessMessage("配置模板保存成功");
    }

    private void conversionPercentage(){
        Integer width = thisTemplate.getWidth();
        Integer height = thisTemplate.getHeight();
        templateJsonObject.getJSONArray("Elements").forEach(item->{
            JSONObject jsonObject =(JSONObject) item;
            Double positionX = jsonObject.getDouble("PositionX");
            positionX = Objects.isNull(positionX)?0:positionX;
            Double positionY = jsonObject.getDouble("PositionY");
            positionY = Objects.isNull(positionY)?0:positionY;
            Double widthItem = jsonObject.getDouble("Width");
            widthItem = Objects.isNull(widthItem) || widthItem <=0 ?width:widthItem;
            Double heightItem = jsonObject.getDouble("Height");
            heightItem = Objects.isNull(heightItem) || heightItem <=0 ? height:heightItem;
            List<Double[]> range = new ArrayList<>();
            Double[] topLeft = {positionX / width,positionY / height};
            range.add(topLeft);
            Double[] topRight = {(positionX + widthItem) / width,positionY / height};
            range.add(topRight);
            Double[] lowerLeft = {positionX / width,(positionY + heightItem) / height};
            range.add(lowerLeft);
            Double[] lowerRight = {(positionX + widthItem) / width,(positionY + heightItem) / height};
            range.add(lowerRight);
            jsonObject.put("Range",range);
        });
    }

    public void removeElement(JSONObject element) {
        templateJsonObject.getJSONArray("Elements").remove(element);
    }
    @Override
    public void delete() {
        List<V2_BlobObject> blobObjectsByBoUid = templateService.findBlobObjectsByBoUid(selected.getId());
        super.delete();
        if(!CollectionUtils.isEmpty(blobObjectsByBoUid)){
            templateService.deleteTemplateFile(blobObjectsByBoUid);
        }
    }
    public Map<String,String> getPatternMap(){
        return Arrays.stream(MatcherType.values()).collect(Collectors.toMap(MatcherType::getCode, MatcherType::getName));
    }
    public Map<String, String> getBusinessType() {
        return Arrays.stream(BusinessType.values()).collect(Collectors.toMap(BusinessType::name, BusinessType::getBusinessName));
    }
    public Map<String,String> fieldsList() {
        String type = thisTemplate.getType();
        if(StringUtils.isEmpty(type) ) return new HashMap<>();
        BusinessType businessType = BusinessType.valueOf(type);
        if(businessType == null) return new HashMap<>();
        List<String> fields = Arrays.asList(BusinessType.valueOf(type).getFields());
        Map<String, String> field = fields.stream().collect(Collectors.toMap(item -> item, item -> item));
        return field;
    }

    public void onRowEdit(RowEditEvent event) {
        Object object = event.getObject();
        if(object instanceof JSONObject){
            JSONObject item = (JSONObject) object;
            int serialNumber = item.getIntValue("SerialNumber");
            String fieldKey = item.getString("FieldKey");
            JSONArray elements = templateJsonObject.getJSONArray("Elements");
            boolean fieldKeyRepeat = elements.stream().filter(element -> {
                JSONObject ele = (JSONObject) element;
                int serialNumberEle = ele.getIntValue("SerialNumber");
                return serialNumber != serialNumberEle;
            }).anyMatch(element -> {
                JSONObject ele = (JSONObject) element;
                String fieldKeyEle = ele.getString("FieldKey");
                return Objects.equals(fieldKey, fieldKeyEle);
            });
            if(fieldKeyRepeat){
                item.put("FieldKey","");
                WebUtil.addErrorMessage("字段重复设置");
            }
        }
    }

    public OCRTemplate getThisTemplate() {
        return thisTemplate;
    }

    public void setThisTemplate(OCRTemplate thisTemplate) {
        this.thisTemplate = thisTemplate;
    }

    public JSONObject getTemplateJsonObject() {
        return templateJsonObject;
    }

    public void setTemplateJsonObject(JSONObject templateJsonObject) {
        this.templateJsonObject = templateJsonObject;
    }
    public String getEditLink() {
        return "CreateTemplate.xhtml?faces-redirect=true&action=Edit&templateId=" + selected.getId();
    }
    public String getPreViewBase64(){
        Integer width = thisTemplate.getWidth();
        Integer height = thisTemplate.getHeight();
        BufferedImage image = new BufferedImage( width == null || width ==0?DEFAULT_WIDTH:width,  height == null || height ==0?DEFAULT_HEIGHT:height, 1);
        Graphics g = image.createGraphics();
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.WHITE);
        render(g);
        g.dispose();
        return getImageBase64(image);
    }
    private void render(Graphics g){
        Font font = new Font("Microsoft YaHei",0,16);
        g.setFont(font);
        Integer defalutWidth = thisTemplate.getWidth();
        templateJsonObject.getJSONArray("Elements").forEach(item -> {
            JSONObject object = (JSONObject) item;
            String textValue = object.getString("TextValue");
            String FieldKey = object.getString("FieldKey");
            int positionX = object.getIntValue("PositionX");
            int positionY = object.getIntValue("PositionY");
            int width = object.getIntValue("Width");
            width = width == 0 ?  defalutWidth : width;
            int height = object.getIntValue("Height");
            g.setColor(Color.BLACK);
            // x , y 其实坐标左上 宽 ，高
            g.drawRect(positionX, positionY, width, height);
            if (textValue != null) {
                g.drawString(textValue,positionX, positionY+16);
            }
        });
    }
    public String getImageBase64(BufferedImage src) {
        Base64 encoder = new Base64();
        String res = "data:image/jpeg;base64,";
        try (ByteArrayOutputStream byteout = new ByteArrayOutputStream()) {
            ImageIO.write(src, "JPG", byteout);
            try (ByteArrayInputStream bytein = new ByteArrayInputStream(byteout.toByteArray())) {
                byte[] bytes = new byte[bytein.available()];
                bytein.read(bytes);
                res += encoder.encodeToString(bytes);
            }
        } catch (IOException ex) {
            Logger.getLogger(QrCodeDrawService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    public void  handleFileUploadTemplate(FileUploadEvent event){
        try {
            UploadedFile file = event.getFile();
            fileName = file.getFileName();
            contentType = file.getContentType();
            contents = file.getContents();
            Object obj= templateService.uploadTemplate(file);
            templateJsonObject = templateService.generateTemplateConfiguration(obj,thisTemplate);
            attachList = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("上传模板解析失败: "+e.getMessage());

        }

    }
    /**
     *  测试模板
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        try {
            previewResult = "";
            String id = this.selected.getId();
            UploadedFile file = event.getFile();
            fileName = file.getFileName();
            JSONObject jsonObject = null;
            Map<String, Object> params = new HashMap<>();
            params.put("templateId",id);
            if(fileName.toLowerCase().endsWith("pdf")){
                 jsonObject = templateService.upload(file, GatewayURL.OCR_DOCUMENT,params);
                JSONArray data = jsonObject.getJSONArray("data");
                previewResult=JSON.toJSONString(data.subList(0, data.size() - 1),SerializerFeature.PrettyFormat);
                source=JSON.toJSONString(data.getJSONObject(data.size() - 1),SerializerFeature.PrettyFormat);
            }else{
                 jsonObject = templateService.upload(file,GatewayURL.OCR_IMAGE,params);
                JSONObject data = jsonObject.getJSONObject("data");
                Object remove = data.remove("source");
                source=JSON.toJSONString(remove,SerializerFeature.PrettyFormat);
                previewResult=JSON.toJSONString(data,SerializerFeature.PrettyFormat);

            }
        } catch (Exception e) {
            e.printStackTrace();
            source = e.getMessage();
            previewResult = e.getMessage();
        }
    }

    public void clearCache(){
        previewResult = "";
        fileName = "";
        source="";
    }

    public String getSource() {
        return source;
    }

    public String getPreviewResult() {
        return previewResult;
    }

    public String getFileName() {
        return fileName;
    }

    public List<V2_BlobObject> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<V2_BlobObject> attachList) {
        this.attachList = attachList;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
