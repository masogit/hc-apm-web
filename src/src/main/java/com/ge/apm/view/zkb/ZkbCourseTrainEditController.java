package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbCourseTrainRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.ZkbCourseTrain;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbCourseTrainService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 503189362
 * @date : 2021/4/9
 * @description :
 */
@ManagedBean
@ViewScoped
public class ZkbCourseTrainEditController extends GenericCRUDUUIDController<ZkbCourseTrain> {

    private ZkbCourseTrainRepository zkbCourseTrainRepository;
    private UserContextService userContextService;
    private ZkbCourseTrainService zkbCourseTrainService;
    private List<V2_BlobObject> v2_blobObjectList;
    private BlobFileService blobFileService;

    @Override
    protected void init() {
        super.init();
        zkbCourseTrainRepository =  WebUtil.getBean(ZkbCourseTrainRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        zkbCourseTrainService = WebUtil.getBean(ZkbCourseTrainService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);

        String encodeStr = WebUtil.getRequestParameter("str");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        if(StringUtils.isNotEmpty(selectedId)) {
            selected = zkbCourseTrainRepository.findById(selectedId);
            v2_blobObjectList = zkbCourseTrainService.queryBlobObjectByPid(selectedId);
        }
    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobFileService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_COURSE);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_COURSE_TRAIN);
            v2_blobObject.setBoUuid(selected.getId());
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeFile(V2_BlobObject v2_blobObject) {
        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
    }

    @Override
    protected GenericRepositoryUUID<ZkbCourseTrain> getDAO() {
        return zkbCourseTrainRepository;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }
}
