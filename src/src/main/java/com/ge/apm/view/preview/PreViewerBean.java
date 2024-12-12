package com.ge.apm.view.preview;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.TempFileUtil;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@ManagedBean
@ViewScoped
public class PreViewerBean implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(PreViewerBean.class);

    private V2_BlobObject blobObject;
    private Integer type;  // 0 图片, 1 pdf    -1 其他
    private String objectId;
    private BlobObjectRepository blobObjectRepository;
    private MicroServiceUtil microServiceUtil;

    @Getter@Setter
    private String pdfUrl;

    @PostConstruct
    public void init() {
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        microServiceUtil= WebUtil.getBean(MicroServiceUtil.class);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if(eventObject instanceof String){
           String  objectId= (String) eventObject;
            this.objectId = objectId;
            List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(objectId);
            if(!CollectionUtils.isEmpty(allByObjectStorageId)){
                blobObject = allByObjectStorageId.get(0);
                String objectType = blobObject.getObjectType();
                type = objectType.contains("image") ? 0 : (objectType.contains("pdf") ? 1 : -1);
            }

            pdfUrl = getHttpURL(objectId);
            logger.info("pdfUrl:" + pdfUrl);
        }
    }

    public String getHttpURL(String objectId){
        StringBuilder sbPath = new StringBuilder();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        if(request == null){
            return "";
        }
        if("localhost".equals(request.getServerName())){
            sbPath.append("http://localhost:8090/hcapmobjecthubservice/api/apm/objectHub/objects/").append(objectId).append("/preView");
        }else{
            sbPath.append("https://").append(request.getServerName()).append("gateway/hcapmobjecthubservice/api/apm/objectHub/objects").append(objectId).append("/preView");
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
}