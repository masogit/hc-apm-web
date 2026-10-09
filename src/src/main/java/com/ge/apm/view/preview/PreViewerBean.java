package com.ge.apm.view.preview;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean
@ViewScoped
public class PreViewerBean implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(PreViewerBean.class);

    Pattern pattern = Pattern.compile("^(.*\\.(?i)(jpg|jpeg|png|gif|bmp|tiff|pdf|JPG|JPEG|PNG|GIF|BMP|TIFF|PDF))$");

    @Getter @Setter
    private V2_BlobObject blobObject;
    private Integer type;  // 0 图片, 1 pdf    -1 其他
    private String objectId;
    private BlobObjectRepository blobObjectRepository;
    private MicroServiceUtil microServiceUtil;
    private FileDownloadService fileDownloadService;
    @Getter@Setter
    private StreamedContent streamedContent;
    @Getter@Setter
    private String pdfUrl;

    @PostConstruct
    public void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        microServiceUtil= WebUtil.getBean(MicroServiceUtil.class);
        String objectId = WebUtil.getRequestParameter("objectId");
        if (ObjectUtil.isNotEmpty(objectId)) {
            try {
                onServerEvent("", objectId);
            }catch (Exception e){
                logger.error("onServerEvent error:",e);
            }
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject){
        if(eventObject instanceof String){
            String  objectId= (String) eventObject;
            this.objectId = objectId;
            /*fileDownloadService = WebUtil.getBean(FileDownloadService.class);
            blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);*/
            List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(objectId);
            //ehome 数据  type 为空 需要做此操作
            if(!CollectionUtils.isEmpty(allByObjectStorageId)) {
                V2_BlobObject blobObject = allByObjectStorageId.get(0);
                if (StringUtils.isBlank(blobObject.getObjectType())) {
                    Collection<String> collection = new ArrayList<>();
                    collection.add(blobObject.getObjectStorageId());
                    List<FileInfoResult> fileInfoResults = fileDownloadService.getFileInfoForEhome(collection);
                    blobObject.setObjectType(fileInfoResults.get(0).getObjectType());
                    blobObject.setObjectName(fileInfoResults.get(0).getObjectName());
                }
            }
             if(!CollectionUtils.isEmpty(allByObjectStorageId)){
                blobObject = allByObjectStorageId.get(0);
                streamedContent = fileDownloadService.getFile(objectId);
                //type = objectType.contains("image") ? 0 : (objectType.contains("pdf") ? 1 : -1);
                 try {
                     getTypeByObject();
                 }catch (Exception e){
                     logger.error("onServerEvent error:",e);
                 }
            }
        }else if(eventObject instanceof StreamedContent){
            try {
                streamedContent = (StreamedContent) eventObject;
                getTypeByObjectStreamed();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(type == -1){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        }else{
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }
    }

    public void getTypeByObject() throws IOException {
        if(blobObject == null){
            type = -1;
        }else {
            if(blobObject.getObjectType().contains("image") ||
               blobObject.getObjectName().toLowerCase().contains(".jpg") ||
               blobObject.getObjectName().toLowerCase().contains(".png") ||
               blobObject.getObjectName().toLowerCase().contains(".jpeg")||
               blobObject.getObjectName().toLowerCase().contains(".jfif")
            ){
                type = 0;
            }else if(blobObject.getObjectType().contains("pdf") ||
                    blobObject.getObjectName().toLowerCase().contains(".pdf")
            ){
                type = 1;
                //pdfUrl = getHttpURL(objectId);
                getTypeByObjectStreamed();
                logger.info("pdfUrl:" + pdfUrl);
            }else{
                type = -1;
            }
        }
    }
    public void getTypeByObjectStreamed() throws IOException {
        if(streamedContent == null){
            type = -1;
        }else {
            if(streamedContent.getContentType().contains("image") ||
                    streamedContent.getName().toLowerCase().contains(".jpg") ||
                    streamedContent.getName().toLowerCase().contains(".png") ||
                    streamedContent.getName().toLowerCase().contains(".jpeg")||
                    streamedContent.getName().toLowerCase().contains(".jfif")
            ){
                type = 2;
            }else if(streamedContent.getContentType().contains("pdf") ||
                    streamedContent.getName().toLowerCase().contains(".pdf")
            ){
                type = 1;
                String key = UUID.randomUUID().toString();
                FileInfoResult fileInfoResult= new FileInfoResult();
                fileInfoResult.setObjectType("application/pdf");
                fileInfoResult.setObjectName(streamedContent.getName());
                fileInfoResult.setBytes(IOUtils.toByteArray(streamedContent.getStream()));
                fileInfoResult.setObjectSize((long)fileInfoResult.getBytes().length);
                DocumentInMemoryUtil.put(key,fileInfoResult);
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                pdfUrl = request.getContextPath() +"/web/preview/"+key;
            }else{
                type = -1;
            }
        }
    }
    public String getHttpURL(String objectId){
        String pdfPreViewUrl = microServiceUtil.getPdfPreViewUrl();
        StringBuilder sbPath = new StringBuilder();
        if(StrUtil.isNotEmpty(pdfPreViewUrl)){
            sbPath.append(pdfPreViewUrl).append("/hcapmobjecthubservice/api/apm/objectHub/objects/").append(objectId).append("/preView");
        }
        return sbPath.toString();
    }


    public void prepareDialogCallback(String objId){
        this.objectId = objId;
    }

    public Integer getType() {
        return type;
    }

    public String getObjectId() {
        return objectId;
    }

    /**
     *  是否支持预览
     * @param fileitem
     * @return
     */
    public boolean  isPreview(V2_BlobObject fileitem){
        String objectName = fileitem.getObjectName();
        String objectType = fileitem.getObjectType();
        Matcher matcher = pattern.matcher(objectName);
        return matcher.matches() || objectType.toLowerCase().contains("image");
    }
    public boolean  isPreview(StreamedContent fileitem){
        String objectName = fileitem.getName();
        String objectType = fileitem.getContentType();
        Matcher matcher = pattern.matcher(objectName);
        return matcher.matches() || objectType.toLowerCase().contains("image");
    }
    public String preViewerImg() {
        try {
            String res = "data:image/jpeg;base64,";
            byte[] result = IOUtils.toByteArray(streamedContent.getStream());
            res += Base64Utils.encodeToString(result);
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public void preViewerPDF() {
        try {
            String pdfStr ="<html>\n" +
                    "    <body style='height: 100%; width: 100%; overflow: hidden; margin:0px; background-color: rgb(40, 40, 40);'>\n" +
                    "        <embed name='B5B792F77CA913D04E30242F43E5EF78' style='position:absolute; left: 0; top: 0;' width='100%' height='100%' src='about:blank' type='application/pdf' internalid='B5B792F77CA913D04E30242F43E5EF78'>\n" +
                    "    </body>\n" +
                    "</html>";
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            PrintWriter writer = response.getWriter();
//            response.setHeader("Content-Disposition", "inline;filename=" + streamedContent.getName());
//            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType(streamedContent.getContentType().concat(";charset=utf-8"));
//            byte[] byteArray = IOUtils.toByteArray(streamedContent.getStream());
//            response.setContentLength(byteArray.length);
            writer.write(pdfStr);
            facesContext.responseComplete();
            facesContext.renderResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}