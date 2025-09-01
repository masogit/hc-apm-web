package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbTrainPublishRegisterService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ZkbTrainRegisterMainController<E> extends GenericCRUDUUIDController<E> {


    ZkbTrainPublishRegisterService zkbTrainPublishRegisterService;

    ZkbTrainPublishRepository dao;

    UserAccount currentUser = UserContextService.getCurrentUserAccount();

    @Getter@Setter
    private ZkbEngineer selectedEngineer;

    @Getter@Setter
    private ZkbTrainPublishRegisterDetail selectedDetail;

    @Getter@Setter
    private ZkbTrainPublishRegister zkbTrainPublishRegister;

    @Getter@Setter
    private List<ZkbEngineer> engineerList;
    @Getter@Setter
    private  List<ZkbTrainPublishRegisterDetail> zkbTrainPublishRegisterDetailList = new ArrayList<>();

    @Getter@Setter
    private String actionName = "";

    @Getter@Setter
    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();
    private BlobFileService blobService;

    @Override
    protected void init() {
        super.init();
        zkbTrainPublishRegisterService = WebUtil.getBean(ZkbTrainPublishRegisterService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
    }


    public void prepareRegister(String pid){
        zkbTrainPublishRegister = new ZkbTrainPublishRegister();
        zkbTrainPublishRegister.setPid(pid);
        this.actionName = "Register";
    }

    public void prepareModifyRegister(String id){
        zkbTrainPublishRegister = zkbTrainPublishRegisterService.getRegisterById(id);
        zkbTrainPublishRegisterDetailList = zkbTrainPublishRegister.getZkbTrainPublishRegisterDetails();
        this.actionName = "Modify";
    }

    public void prepareDetailCreate(){
        initEngineerList();
        selectedEngineer = null;
        selectedDetail = new ZkbTrainPublishRegisterDetail();
        v2_blobObjectList = new ArrayList<>();
    }

    public void prepareDetailEdit(){
        initEngineerList();
        selectedEngineer = zkbTrainPublishRegisterService.findEngineerById(selectedDetail.getEngineerId());
        if("Register".equals(actionName)){
            v2_blobObjectList = selectedDetail.getV2_blobObjectList();
        }else{
            v2_blobObjectList = zkbTrainPublishRegisterService.queryBlobObject(selectedDetail.getId());
        }
    }

    public List<ZkbEngineer> autoCompleteEngineer(String query){
        List<ZkbEngineer> res = engineerList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query) || item.getSso().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void initEngineerList(){
        engineerList = zkbTrainPublishRegisterService.queryEngineerListByTenant(currentUser);
    }

    public void deleteDetail(){
        if("Register".equals(actionName)){
            zkbTrainPublishRegisterDetailList.remove(selectedDetail);
        }else{
            zkbTrainPublishRegisterService.deleteDetail(selectedDetail);
        }
        selectedDetail = null;
    }

    public void removeFile(V2_BlobObject v2BlobObject){
        v2_blobObjectList.remove(v2BlobObject);
        blobService.deleteBlobFile(v2BlobObject);
    }

    public void chooseEngineerData(){

    }

    public void engineerChange(){
        selectedDetail.setEngineerId(selectedEngineer.getId());
        selectedDetail.setEngineerName(selectedEngineer.getName());
        selectedDetail.setEngineerPhone(selectedEngineer.getPhone());
        selectedDetail.setEngineerEmail(selectedEngineer.getEmail());
        selectedDetail.setEngineerAge(selectedEngineer.getAge());
        selectedDetail.setEngineerSso(selectedEngineer.getSso());
    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void saveEngineer(){
        if("Register".equals(actionName)){
            selectedDetail.setId(UUID.randomUUID().toString());
            selectedDetail.setV2_blobObjectList(v2_blobObjectList);
            zkbTrainPublishRegisterDetailList.add(selectedDetail);
            selectedDetail = null;
            v2_blobObjectList = new ArrayList<>();
        }else{
            selectedDetail.setRegisterId(zkbTrainPublishRegister.getId());
            zkbTrainPublishRegisterService.modifyDetail(selectedDetail, v2_blobObjectList);
            zkbTrainPublishRegisterDetailList = zkbTrainPublishRegisterService.queryDetailByRegister(zkbTrainPublishRegister.getId());
        }

    }

    public void saveData(){
        zkbTrainPublishRegisterService.saveData(zkbTrainPublishRegister,zkbTrainPublishRegisterDetailList,currentUser);
    }


    @Override
    protected GenericRepositoryUUID<E> getDAO() {
        return null;
    }
}
