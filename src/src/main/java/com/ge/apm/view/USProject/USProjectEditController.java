package com.ge.apm.view.USProject;

import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.USProUpRepository;
import com.ge.apm.dao.USProjectRepository;
import com.ge.apm.domain.USProUp;
import com.ge.apm.domain.USProject;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.view.asset.AssetInfoEditController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class USProjectEditController extends JpaCRUDController<USProject> {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(USProjectEditController.class);

    private USProjectRepository dao;
    private USProUpRepository uSProUpRepository;
    private BlobFileService blobService;
    private BlobObjectRepository blobDao;
    private FileScanService fileScanService;
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

    @Setter
    @Getter
    private String pdf;


    private boolean uploadImage;

    @Override
    protected void init() {
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        dao = WebUtil.getBean(USProjectRepository.class);
        uSProUpRepository = WebUtil.getBean(USProUpRepository.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        fileScanService = WebUtil.getBean(FileScanService.class);
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
            uploadFlag = getAttachList(selected.getName()).size() == 0;
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
    protected USProjectRepository getDAO() {
        return dao;
    }

    /**
     * 获取图片列表
     *
     * @param name
     * @return
     */
    public List<V2_BlobObject> getAttachList(String name) {
        if (attachements == null) {
            attachements = blobService.findBlobObjectsByBoUid(name);
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
        if (StringUtils.isEmpty(selected.getName())) {
            WebUtil.addErrorMessage("name");
            return;
        }
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            attachements.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_US_Option);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(selected.getName());
            blobService.saveBlobObject(blobObject);
            uploadImage = true;
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public String getListPageLink() {
        return "USProjectList?faces-redirect=true";
    }


    @Transactional
    public String applyChange() {
        USProject imageInfo = dao.findByName(selected.getName());
        if (null != imageInfo ) {
            WebUtil.addErrorMessage("name 已存在！");
            return "";
        } else {
            newAdd = true;
            if (!uploadImage) {
                blobDao.deleteByBoUuid(selected.getName());
            }
            // name 不存在，则新增
                selected.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
                selected.setCreateTime(new Date());
                selected.setPdf(this.pdf);
                this.create();
                if(StringUtils.isNotEmpty(selected.getPsiDescription())){ //保存关系表
                    String psi = selected.getPsiDescription();
                    String[] psi_s =  psi.split(";");
                    List<USProUp> list = new ArrayList<USProUp>();
                    for(int i=0;i<psi_s.length;i++){
                        USProUp usProUp = new USProUp();
                        usProUp.setName(selected.getName());
                        usProUp.setPsiDescription(psi_s[i]);
                        usProUp.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
                        usProUp.setCreateTime(new Date());
                        list.add(usProUp);
                    }
                    uSProUpRepository.save(list);
                }
            }
            return "USProjectList?faces-redirect=true";
        }


//修改
    @Transactional
    public String updateChange() {
            newAdd = true;
            if (!uploadImage) {
                blobDao.deleteByBoUuid(selected.getName());
            }
            //修改
                this.update();
                uSProUpRepository.deleteByName(selected.getName()); //删除关系表所有数据
                if (StringUtils.isNotEmpty(selected.getPsiDescription())) { //保存关系表
                    String psi = selected.getPsiDescription();
                    String[] psi_s = psi.split(";");
                    List<USProUp> list = new ArrayList<USProUp>();
                    for (int i = 0; i < psi_s.length; i++) {
                        USProUp usProUp = new USProUp();
                        usProUp.setName(selected.getName());
                        usProUp.setPsiDescription(psi_s[i]);
                        usProUp.setCreatorUID(UserContextService.getCurrentUserAccount().getUid());
                        usProUp.setCreateTime(new Date());
                        list.add(usProUp);
                    }
                    uSProUpRepository.save(list);
                }

            return "USProjectList?faces-redirect=true";

    }

    private void createAttachments(USProject image, String[] selectedPictures) {
        List<V2_BlobObject> attachList = new ArrayList<>();
        for (String item : selectedPictures) {
            V2_BlobObject blobObject = blobDao.findAllByObjectStorageId(item).stream().findFirst().get();
            blobObject.setBoType(V2_BlobObject.BO_TYPE_US_Option);
            blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
            blobObject.setBoUuid(image.getName());
            blobObject.setBoId(image.getId());
            attachList.add(blobObject);
        }
        blobDao.save(attachList);
    }

    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    private static Long TIME_OUT = 5000l;
    @Value("#{systemEnvironment['US_FILE_Folder'] ?: urlProperties.USFileFolder}")
    private String US_FILE_Folder;
    @Value("#{systemEnvironment['US_FILE_Folder_Local'] ?: urlProperties.USFileFolderLocal}")
    private String US_FILE_Folder_Local;
    //上传文件，返回文件全路径明
    public void  uploadDataFile(FileUploadEvent event) {

        String savedfilePath = null;
        try {
            savedfilePath = saveUSFile(event.getFile());
        } catch (Exception ex) {
            WebUtil.addErrorMessage(event.getFile().getFileName() + "上传失败!");
            return;
        }
        if (Strings.isNullOrEmpty(savedfilePath)) {
            WebUtil.addErrorMessage(event.getFile().getFileName() + "保存失败!");
            return;
        }
        pdf = event.getFile().getFileName();
        WebUtil.addSuccessMessage(event.getFile().getFileName() + "上传文件成功！");
    }


    public String saveUSFile(UploadedFile file) throws Exception {
        String folderName = "C:/US/";
        String folderNameLocal =  this.getClass().getResource("").getPath().substring(0,this.getClass().getResource("").getPath().indexOf("target"))
                +"src/main/webapp/admin/usProject/US";
        saveFile(file, folderNameLocal, file.getFileName());
        return saveFile(file, folderName, file.getFileName());
    }

    public String saveFile(UploadedFile file, String folderName, String saveFilename) throws Exception {

        createFolder(folderName, false);
        String fileName = folderName.concat(File.separator).concat(saveFilename);
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            InputStream inputStream = file.getInputstream();
            int count = 0;
            long starttime = new Date().getTime();
            long endtime;
            while (count == 0) {
                count = inputStream.available();
                endtime = new Date().getTime();
                if (endtime - starttime >= TIME_OUT) {
                    throw new Exception("Save file[" + fileName + "] timeout !");
                }
            }
            byte[] buff = new byte[count];
            int bytesRead;

            while (-1 != (bytesRead = inputStream.read(buff))) {
                out.write(buff, 0, bytesRead);
            }
            out.flush();

        } catch (Exception e) {
            throw e;
        }

        return fileName;
    }

    public static void createFolder(String folderPath, boolean deleteExistingFiles) {
        if (deleteExistingFiles) deleteFolder(folderPath);

        File file = new File(folderPath);
        if (!file.exists()) file.mkdirs();
    }

    public static void deleteFolder(String folderPath) {
        try {
            deleteAllFiles(folderPath);
            java.io.File myFilePath = new java.io.File(folderPath);
            myFilePath.delete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static boolean deleteAllFiles(String path) {
        boolean flag = false;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        String[] fileList = rootFile.list();
        File file;
        for (String fileName : fileList) {
            if (path.endsWith(File.separator)) {
                file = new File(path + fileName);
            } else {
                file = new File(path + File.separator + fileName);
            }
            if (file.isFile()) {
                file.delete();
            }
            if (file.isDirectory()) {
                deleteAllFiles(path + "/" + fileName);
                deleteFolder(path + "/" + fileName);
                flag = true;
            }
        }
        return flag;
    }

    public  void  removeFile(){
        if(StringUtils.isNotEmpty(pdf)){
            //删除之前，先查询 是否有其他方案用此说明书,有则不删
            if(dao.countByPdf(pdf) == 0) {
                String folderNameLocal = this.getClass().getResource("").getPath().substring(0, this.getClass().getResource("").getPath().indexOf("target"))
                        + "src/main/webapp/admin/usProject/US";
                deleteFile("C:/US/", pdf);
                deleteFile(folderNameLocal, pdf);
            }
            pdf = null;
            WebUtil.addSuccessMessage( "删除说明书成功！");
        }
    }
    public static boolean deleteFile(String path,String fileName) {
        boolean flag = false;
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return flag;
        }
        if (!rootFile.isDirectory()) {
            return flag;
        }
        File file;
        if (path.endsWith(File.separator)) {
            file = new File(path + fileName);
        } else {
            file = new File(path + File.separator + fileName);
        }
        if (file.isFile()) {
            file.delete();
        }
        return flag;
    }


}
