package com.ge.apm.view.preview;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.view.asset.AssetFileImportController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class PreViewerBean implements ServerEventInterface {

    private static final Logger logger = LoggerFactory.getLogger(AssetFileImportController.class);
    private String baseUrl;
    private String url;

    private V2_BlobObject blobObject;

    private Integer type;  // 0 图片, 1 其他
    private GatewayURL fileToPdf = GatewayURL.SEARCH_FILE;
    private String objectId;
    private BlobObjectRepository blobObjectRepository;
    private MicroServiceUtil microServiceUtil;

    @PostConstruct
    public void init() {
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        microServiceUtil= WebUtil.getBean(MicroServiceUtil.class);
        baseUrl = microServiceUtil.getGatewayURL(fileToPdf);
        logger.info("baseUrl:{}",baseUrl);
    }

    public String getUrl() {
        logger.info("objectId:{}",objectId);
        if(StringUtils.isEmpty(objectId)) return url;
        url = baseUrl.concat("/").concat(objectId);
        return url;
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
                type = objectType.contains("image")?0:1;
            }
        }
    }

    public Integer getType() {
        return type;
    }

    public String getObjectId() {
        return objectId;
    }
}