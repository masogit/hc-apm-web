package com.ge.apm.view.USOptionImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.USOptionImageRepository;
import com.ge.apm.domain.USOptionImage;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.view.asset.AssetInfoEditController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class USOptionImageEditController extends JpaCRUDController<USOptionImage> {

    private USOptionImageRepository dao;
    private BlobFileService blobService;
    private BlobObjectRepository blobDao;
    @Setter
    @Getter
    private List<V2_BlobObject> attachements;
    @Setter
    @Getter
    private String[] selectedPictures;
    //如果无图片 就显示请上传图片
    @Setter
    @Getter
    private boolean uploadFlag;
    @Setter
    @Getter
    private boolean newAdd;

    private boolean uploadImage;

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(USOptionImageRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(Integer.parseInt(selectedId));
            prepareEdit();
            uploadFlag = getAttachList(selected.getUsSegment(),selected.getProductName()).size() == 0;
        }
        if (selected == null) {
            try {
                prepareCreate();
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected USOptionImageRepository getDAO() {
        return dao;
    }

    /**
     * 获取图片列表
     *
     * @param USSegment
     * @return
     */
    public List<V2_BlobObject> getAttachList(String USSegment,String productName) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(USSegment+"_"+productName);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(V2_BlobObject.BO_SUB_TYPE_IMG) && item.getBoType().equals(V2_BlobObject.BO_TYPE_US_Option) ).collect(Collectors.toList());
        uploadFlag = collect.size() == 0;
        return collect;
    }

    /**
     * 删除图片
     *
     * @param blobId
     */
    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        attachements = attachements.stream().filter(item -> !item.getId().equals(blobId)).collect(Collectors.toList());
    }

    /**
     * 添加图片
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        if (StringUtils.isEmpty(selected.getUsSegment())) {
            WebUtil.addErrorMessage("请填写USSegment");
            return;
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_US_Option);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(selected.getUsSegment()+"_"+selected.getProductName());
            blobService.saveBlobObject(blobObject);
            uploadImage = true;
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public String getListPageLink() {
        return "USOptionImageList?faces-redirect=true";
    }


    @Transactional
    public String applyChange() {
        USOptionImage imageInfo = dao.findUS(selected.getUsSegment(),selected.getProductName());
        if (null != imageInfo) {
            WebUtil.addErrorMessage("USSegment,ProductName 已存在！");
            return "";
        } else {
            newAdd = true;
            if (!uploadImage) {
                blobDao.deleteByBoUuid(selected.getUsSegment()+"_"+selected.getProductName());
            }
            // USSegment ProductName不存在，则新增
            selected.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
            selected.setCreateTime(new Date());
            this.create();
            if (selected != null && selected.getId() != null && selectedPictures != null) {
                createAttachments(selected, selectedPictures);
            }
            return "USOptionImageList?faces-redirect=true";
        }
    }

    private void createAttachments(USOptionImage image, String[] selectedPictures) {
        List<V2_BlobObject> attachList = new ArrayList<>();
        for (String item : selectedPictures) {
            V2_BlobObject blobObject = blobDao.findAllByObjectStorageId(item).stream().findFirst().get();
            blobObject.setBoType(V2_BlobObject.BO_TYPE_US_Option);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(image.getUsSegment()+"_"+selected.getProductName());
            blobObject.setBoId(image.getId());
            attachList.add(blobObject);
        }
        blobDao.save(attachList);
    }
}
