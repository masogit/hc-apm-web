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
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collection;
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
    private FileDownloadService fileDownloadService;
    @Getter@Setter
    private String pdfUrl;

    @PostConstruct
    public void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        microServiceUtil= WebUtil.getBean(MicroServiceUtil.class);
        String objectId = WebUtil.getRequestParameter("objectId");
        if (ObjectUtil.isNotEmpty(objectId)) {
            onServerEvent("", objectId);
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        if(eventObject instanceof String){
            String  objectId= (String) eventObject;
            this.objectId = objectId;
            List<V2_BlobObject> allByObjectStorageId = blobObjectRepository.findAllByObjectStorageId(objectId);
            //ehome 数据  type 为空 需要做此操作
            if(!CollectionUtils.isEmpty(allByObjectStorageId)) {
                V2_BlobObject blobObject = allByObjectStorageId.get(0);
                if (StringUtils.isBlank(blobObject.getObjectType())) {
                     Collection<String> collection = new ArrayList<>();
                     collection.add(blobObject.getObjectStorageId());
                     List<FileInfoResult> fileInfoResults = fileDownloadService.getFileInfoForEhome(collection);
                     blobObject.setObjectType(fileInfoResults.get(0).getObjectType());
                }
            }
             if(!CollectionUtils.isEmpty(allByObjectStorageId)){
                blobObject = allByObjectStorageId.get(0);
                //type = objectType.contains("image") ? 0 : (objectType.contains("pdf") ? 1 : -1);
                getTypeByObject();
            }
        }
        if(type == -1){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        }else{
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        }
    }

    public void getTypeByObject(){
        if(blobObject == null){
            type = -1;
        }else {
            if(blobObject.getObjectType().contains("image") ||
               blobObject.getObjectName().toLowerCase().contains(".jpg") ||
               blobObject.getObjectName().toLowerCase().contains(".png") ||
               blobObject.getObjectName().toLowerCase().contains(".jpeg")
            ){
                type = 0;
            }else if(blobObject.getObjectType().contains("pdf") ||
                    blobObject.getObjectName().toLowerCase().contains(".pdf")
            ){
                type = 1;
                pdfUrl = getHttpURL(objectId);
                logger.info("pdfUrl:" + pdfUrl);
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
}