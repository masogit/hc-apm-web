package com.ge.apm.view.assetTypeGroup;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.AssetTypeGroupRepository;
import com.ge.apm.dao.UserAssetGroupRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.assetTypeGroup.AssetTypeService;
import com.ge.apm.service.uaa.AssetTypeGroupService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Data;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@Data
public class AssetTypeGroupController extends GenericCRUDController<AssetTypeGroup> implements Serializable, ServerEventInterface {


    private AssetTypeGroupRepository authGroupDao;
    private AssetTypeGroupService assetTypeGroupService;

    private TreeNode assetTypeGroupTree;
    private TreeNode selectedAssetTypeGroupNodes;

    private UserAccount loginUser;

    private String name;
    private String parentUid;
    private String uid;
    private String parentName;
    private String namePath;
    private String encoded;
    private String classificationCode;

    boolean createTopLevelNode=false;

    private String retrieval;
    private AssetTypeService assetTypeService;
    private AssetInfoRepository assetInfoRepository;

    private UserAssetGroupRepository userAssetGroupDao;

    private String  isReadOnly=null;

    private Boolean isCreate;

    public Boolean getCreate() {
        return isCreate == null ? false : isCreate.booleanValue();
    }

    @Override
    protected GenericRepository<AssetTypeGroup> getDAO() {
        return null;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }

    @Override
    protected void init(){
        loginUser = UserContextService.getCurrentUserAccount();
        assetTypeGroupService = WebUtil.getBean(AssetTypeGroupService.class);
        assetTypeService = WebUtil.getBean(AssetTypeService.class);
        authGroupDao = WebUtil.getBean(AssetTypeGroupRepository.class);
        assetInfoRepository=WebUtil.getBean(AssetInfoRepository.class);
        userAssetGroupDao=WebUtil.getBean(UserAssetGroupRepository.class);
        buildOrdTree();
    }

    public void buildOrdTree() {

        assetTypeGroupTree =assetTypeService.buildSysAuthGroupInTree(loginUser.getTenantUID());
    }


    public void filterNode() {
        assetTypeService.filterTreeNode(assetTypeGroupTree, retrieval, -1, -1);
    }

    public void onSelectTreeNode(NodeSelectEvent event) {
        isReadOnly=null;

        TreeNode node = event.getTreeNode();
        selected = (AssetTypeGroup) node.getData();
        name=selected.getName();
        classificationCode=selected.getClassificationCode();
        encoded=selected.getEncoded().trim();
        parentUid=selected.getParentUid();
        if(selected.getParentUid() != null) {
            namePath=authGroupDao.listAssetTypeGroupNamePathInHierarchy(selected.getParentUid());
        }else {
            namePath=null;
        }
        isCreate=false;
    }

    public void prepareCreateTopLevelNode() {
        isReadOnly="";

        createTopLevelNode=true;
        namePath =null;
        parentUid=null;
        encoded=null;
        name = null;
        classificationCode=null;
        AssetTypeGroup assetTypeGroup = new AssetTypeGroup();
        assetTypeGroup.setParentUid(parentUid);
        this.selected = assetTypeGroup;
        isCreate=true;
    }


    public void prepareCreateNextNode() {
        isReadOnly="";

        createTopLevelNode=false;
        namePath =authGroupDao.listAssetTypeGroupNamePathInHierarchy(selected.getUid());
        parentUid=selected.getUid();
        encoded=authGroupDao.findByUid(selected.getUid()).getEncoded().trim()+"-";
        name = null;
        classificationCode=null;
        AssetTypeGroup authGroup = new AssetTypeGroup();
        authGroup.setParentUid(parentUid);
        this.selected = authGroup;
        isCreate=true;
    }

    public void saveSelectedAuthGrouping() {


        if(!isCreate){
            if(selected.getParentUid()==null ? true : parentUid.equals(selected.getParentUid().trim())){
                selected.setName(name);
                selected.setEncoded(encoded);
                selected.setClassificationCode(classificationCode);
                authGroupDao.save(selected);
                buildOrdTree();
                return;
            }
        }

        //重复增加不被允许
        boolean isDuplicate = checkName();
        if (isDuplicate) return;

//        Integer maxId = authGroupDao.getMaxId();
//        if (null == maxId) {
//            maxId = 0;
//        }

        selected.setTenantUid(loginUser.getTenantUID());
//        selected.setId(++maxId);
        selected.setName(name);
        selected.setEncoded(encoded);
        selected.setClassificationCode(classificationCode);
        AssetTypeGroup authGroup=authGroupDao.findAuthGroupByNameAndParentUidAndTenantUid(name, selected.getParentUid(),loginUser.getTenantUID());
        String parentEncoded="";
        if(selected.getParentUid()!=null){
            AssetTypeGroup parentAuthGroup = authGroupDao.findByUid(selected.getParentUid());
            parentEncoded=parentAuthGroup.getEncoded().trim();
            selected.setParentAuthGroup(parentAuthGroup);
        }


        if(authGroup==null && encoded.contains(parentEncoded) && authGroupDao.findEncodedByUidAndTenantUid(encoded,loginUser.getTenantUID())==null){
            //super.save();
//            selected.onSave();
            authGroupDao.save(selected);
            isCreate=false;
            buildOrdTree();
        }else if(authGroupDao.findEncodedByUidAndTenantUid(encoded,loginUser.getTenantUID())!=null){
            WebUtil.addErrorMessage(encoded + "编码已存在");
        }else{
            WebUtil.addErrorMessage(encoded + "编码格式不正确");
        }
    }

    public boolean checkName() {
        AssetTypeGroup authGroup = authGroupDao.findAuthGroupByNameAndParentUidAndTenantUid(name,selected.getParentUid(),loginUser.getTenantUID());
        if (authGroup != null) {
            WebUtil.addErrorMessage(name + "已经存在");
            return true;
        }

        return false;
    }


    public void deleteSelectedAuthGroup() {
        uid=selected.getUid();
        classificationCode=selected.getClassificationCode();
        encoded=selected.getEncoded()==null ? "" : selected.getEncoded().trim();
        List<AssetInfo> assetInfos=assetInfoRepository.findByAssetType(uid);
        if(assetInfos.size()>=1){
            WebUtil.addErrorMessage(name + "已绑定资产");
            return;
        }
        List<AssetTypeGroup> childs= authGroupDao.findChildByParentUid(uid);
        if(childs==null || childs.size()==0){
            AssetTypeGroup aa= new AssetTypeGroup();
            authGroupDao.delete(selected);
        }else if(childs.size()>=1){
            //删除子节点
            for(int i=0;i<childs.size(); i++){
                authGroupDao.delete(childs.get(i));
                userAssetGroupDao.deleteByAssetGroupUid(childs.get(i).getUid());

            }
            //删除自己
            authGroupDao.delete(selected);
        }
        userAssetGroupDao.deleteByAssetGroupUid(selected.getUid());

        buildOrdTree();
        namePath=null;
        name=null;
        encoded=null;
        selected=null;
        classificationCode=null;
    }



}
