package com.ge.apm.view.IBUInfoImage;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.IBUInfoImageRepository;
import com.ge.apm.domain.IBUInfoImage;
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
public class IBUInfoImageEditController extends JpaCRUDController<IBUInfoImage> {

    private IBUInfoImageRepository dao;
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
        dao = WebUtil.getBean(IBUInfoImageRepository.class);
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
            uploadFlag = getAttachList(selected.getIbuOffer()).size() == 0;
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
    protected IBUInfoImageRepository getDAO() {
        return dao;
    }

    /**
     * 获取图片列表
     *
     * @param ibuOffer
     * @return
     */
    public List<V2_BlobObject> getAttachList(String ibuOffer) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(ibuOffer);
        }
        List<V2_BlobObject> collect = attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(V2_BlobObject.BO_SUB_TYPE_IMG)).collect(Collectors.toList());
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
        if (StringUtils.isEmpty(selected.getIbuOffer())) {
            WebUtil.addErrorMessage("请填写IBUOffer");
            return;
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_IBU_INFO);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(selected.getIbuOffer());
            blobService.saveBlobObject(blobObject);
            uploadImage = true;
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public String getListPageLink() {
        return "IBUInfoImageList?faces-redirect=true";
    }

    /**
     * 添加货号
     */
    @Transactional
    public String applyChange() {
        IBUInfoImage imageInfo = dao.findByIbuOffer(selected.getIbuOffer());
        if (null != imageInfo) {
            WebUtil.addErrorMessage("ibuOffer已存在！");
            return "";
        } else {
            newAdd = true;
            if (!uploadImage) {
                // 新增ibuOffer时删除未保存ibuOffer时上传的图片
                blobDao.deleteByBoUuid(selected.getIbuOffer());
            }
            // ibuOffer不存在，则新增
            selected.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
            selected.setCreateTime(new Date());
            this.create();
            if (selected != null && selected.getId() != null && selectedPictures != null) {
                createAttachments(selected, selectedPictures);
            }
            return "IBUInfoImageList?faces-redirect=true";
        }
    }

    private void createAttachments(IBUInfoImage image, String[] selectedPictures) {
        List<V2_BlobObject> attachList = new ArrayList<>();
        for (String item : selectedPictures) {
            V2_BlobObject blobObject = blobDao.findAllByObjectStorageId(item).stream().findFirst().get();
            blobObject.setBoType(V2_BlobObject.BO_TYPE_IBU_INFO);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(image.getIbuOffer());
            blobObject.setBoId(image.getId());
            attachList.add(blobObject);
        }
        blobDao.save(attachList);
    }
}
