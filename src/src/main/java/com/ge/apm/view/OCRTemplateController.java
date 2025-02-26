package com.ge.apm.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ge.apm.dao.OCRTemplateRepository;
import com.ge.apm.domain.OCRTemplate;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.OCRTemplateService;
import com.ge.apm.service.asset.QrCodeDrawService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.ocr.support.MatcherType;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private String fileName;

    private static final double DEFAULT_WIDTH = 600.00;
    private static final double DEFAULT_HEIGHT = 850.00;

    String actionName ;
    @Override
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(OCRTemplateRepository.class);
        templateService = WebUtil.getBean(OCRTemplateService.class);
        this.searchFilters = new ArrayList<>();
        initActionData();
    }

    private void initActionData() {
        actionName = WebUtil.getRequestParameter("action");
        if(actionName == null) return;
        if("Create".equals(actionName)){
            thisTemplate = new OCRTemplate();
            thisTemplate.setTenantUID(currentUser.getTenantUID());
            thisTemplate.setCreatedBy(currentUser.getUid());
            thisTemplate.setCreatedDate(new Date());
            templateJsonObject = new JSONObject();
            templateJsonObject.put("Elements",new ArrayList<>());
        }else if("Edit".equals(actionName)){
            String templateId = WebUtil.getRequestParameter("templateId");
            thisTemplate = dao.findById(templateId);
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
        WebUtil.addSuccessMessage("配置模板保存成功");
    }
    private void conversionPercentage(){
        templateJsonObject.getJSONArray("Elements").forEach(item->{
            JSONObject jsonObject =(JSONObject) item;
            Double positionX = jsonObject.getDouble("PositionX");
            positionX = Objects.isNull(positionX)?0:positionX;
            Double positionY = jsonObject.getDouble("PositionY");
            positionY = Objects.isNull(positionY)?0:positionY;
            Double widthItem = jsonObject.getDouble("Width");
            widthItem = Objects.isNull(widthItem) || widthItem <=0 ?DEFAULT_WIDTH:widthItem;
            Double heightItem = jsonObject.getDouble("Height");
            heightItem = Objects.isNull(heightItem) || heightItem <=0 ? DEFAULT_HEIGHT:heightItem;
            List<Double[]> range = new ArrayList<>();
            Double[] topLeft = {positionX / DEFAULT_WIDTH,positionY / DEFAULT_HEIGHT};
            range.add(topLeft);
            Double[] topRight = {(positionX + widthItem) / DEFAULT_WIDTH,positionY / DEFAULT_HEIGHT};
            range.add(topRight);
            Double[] lowerLeft = {positionX / DEFAULT_WIDTH,(positionY + heightItem) / DEFAULT_HEIGHT};
            range.add(lowerLeft);
            Double[] lowerRight = {(positionX + widthItem) / DEFAULT_WIDTH,(positionY + heightItem) / DEFAULT_HEIGHT};
            range.add(lowerRight);
            jsonObject.put("Range",range);
        });
    }

    public void removeElement(JSONObject element) {
        templateJsonObject.getJSONArray("Elements").remove(element);
    }
    @Override
    public void delete() {
        super.delete();
    }
    public Map<String, String> getFieldsMap() {
        return Arrays.stream(MatcherType.values()).collect(Collectors.toMap(MatcherType::getCode, MatcherType::getName));
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
        BufferedImage image = new BufferedImage((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT, 1);
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
        templateJsonObject.getJSONArray("Elements").forEach(item -> {
            JSONObject object = (JSONObject) item;
            String textValue = object.getString("TextValue");
            String FieldKey = object.getString("FieldKey");
            int positionX = object.getIntValue("PositionX");
            int positionY = object.getIntValue("PositionY");
            int width = object.getIntValue("Width");
            width = width == 0 ? (int) DEFAULT_WIDTH : width;
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

    public void handleFileUpload(FileUploadEvent event) {
        try {
            previewResult = "";
            String id = this.selected.getId();
            UploadedFile file = event.getFile();
            fileName = file.getFileName();
            JSONObject jsonObject = templateService.readFileToObject(file,id);
            previewResult=JSON.toJSONString(jsonObject,SerializerFeature.PrettyFormat);
        } catch (Exception e) {
            e.printStackTrace();
            previewResult = e.getMessage();
        }
    }

    public void clearCache(){
        previewResult = "";
        fileName = "";
    }

    public String getPreviewResult() {
        return previewResult;
    }

    public String getFileName() {
        return fileName;
    }

}
