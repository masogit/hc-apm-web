package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.PermitInfoRepository;
import com.ge.apm.dao.SpecialAssetPermitInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.nurseSite.AssetSelectService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.nurseSite.AssetSelectController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ManagedBean
@ViewScoped
public class SpecialAssetPermitEditController extends JpaCRUDController<PermitInfo> {
    private static final Logger log = LoggerFactory.getLogger(SpecialAssetController.class);


    private PermitInfoRepository permitInfoRepository;

    private UserAccount user;
    private UaaService uaaService;

    private UserContextService userContextService;

    private AssetSelectController assetSelectController;
    private AssetSelectService assetSelectService;

    private SpecialAssetPermitInfoRepository specialAssetPermitInfoRepository;
    private AssetInfoRepository assetInfoRepository;

    private List<PermitInfo> permitInfos;

//    private List<OrgInfo> deptList;
//
//    private TreeNode orgTree;
//
//    private OrgInfo selectedOrg;


    @Override
    protected GenericRepository<PermitInfo> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        permitInfoRepository = WebUtil.getBean(PermitInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);


        assetSelectController = WebUtil.getBean(AssetSelectController.class);
        assetSelectController.setCallingController(this);

        assetSelectService = WebUtil.getBean(AssetSelectService.class);
        specialAssetPermitInfoRepository = WebUtil.getBean(SpecialAssetPermitInfoRepository.class);


        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");


        permitInfos=new ArrayList<>();
        if ("Create".equalsIgnoreCase(actionName)) {
            assetSelectController.loadData4Create();
            permitInfos.add(new PermitInfo());
//            this.orgTree = assetSelectService.loadOrgTree();
//            this.selectedOrg = assetSelectService.loadSelectedOrg();
//            this.deptList = assetSelectService.loadValidDeptList(selectedOrg);
        } else if ("Edit".equalsIgnoreCase(actionName)) {
            String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
            setSelected(permitInfoRepository.findById(selectedId));
            permitInfos.add(selected);
            crudActionName="Edit";
            List<AssetInfo> assetInfos = new ArrayList<>();
            List<SpecialAssetPermitInfo> specialAssetPermitInfos=specialAssetPermitInfoRepository.findByPermitUid(selected.getUid());
            if(!CollectionUtils.isEmpty(specialAssetPermitInfos)) {
                assetInfos  = assetInfoRepository.findByUidIn(specialAssetPermitInfos.stream().map(item -> item.getSpecialAssetId()).collect(Collectors.toList()));

            }
            assetSelectController.loadData4Edit(assetInfos, selected.getSiteUID());
        }
    }


    @Transactional
    public String savePermit(){
        List<PermitInfo> permitInfoList = permitInfos.stream().filter(item -> item.getCertificateName() != null ).collect(Collectors.toList());
        permitInfoRepository.save(permitInfoList);
        Set<AssetInfo> selectedAssets = assetSelectController.getSelectedAssets();
        if(crudActionName.equals("Edit")){
            specialAssetPermitInfoRepository.deleteByPermitUidIn(permitInfoList.stream().map(item->item.getUid()).collect(Collectors.toList()));
        }
        ArrayList<SpecialAssetPermitInfo> specialAssetPermitInfos = new ArrayList<>(selectedAssets.size());
        for (AssetInfo selectedAsset : selectedAssets) {
            for (PermitInfo info : permitInfoList) {
                SpecialAssetPermitInfo specialAssetPermitInfo=new SpecialAssetPermitInfo();
                specialAssetPermitInfo.setPermitInfoUid(info.getUid());
                specialAssetPermitInfo.setSpecialAssetId(selectedAsset.getUid());
                specialAssetPermitInfos.add(specialAssetPermitInfo);
            }
        }
        specialAssetPermitInfoRepository.save(specialAssetPermitInfos);
        this.selected=null;
        WebUtil.addSuccessMessage("保存成功");


        return "specialAssetPermitList?faces-redirect=true";

    }

    public List<PermitInfo> getPermitInfos() {
        return permitInfos;
    }

    public void setPermitInfos(List<PermitInfo> permitInfos) {
        this.permitInfos = permitInfos;
    }


//    public List<OrgInfo> getDeptList() {
//        return deptList;
//    }
//
//    public void setDeptList(List<OrgInfo> deptList) {
//        this.deptList = deptList;
//    }
//
//    public TreeNode getOrgTree() {
//        return orgTree;
//    }
//
//    public void setOrgTree(TreeNode orgTree) {
//        this.orgTree = orgTree;
//    }
//
//    public OrgInfo getSelectedOrg() {
//        return selectedOrg;
//    }
//
//    public void setSelectedOrg(OrgInfo selectedOrg) {
//        this.selectedOrg = selectedOrg;
//    }
//
//    public void onSelectOrgFromTree(NodeSelectEvent event) {
//        TreeNode node = event.getTreeNode();
//        selectedOrg = (OrgInfo) node.getData();
//        if (selectedOrg != null) {
//            log.info("selectedOrg: {}", selectedOrg.getShortName());
//            this.deptList = assetSelectService.loadDeptListByOrg(selectedOrg);
//        }
////        this.callingController.onServerEvent("changeSite",selectedOrg);
//        isSelectAll = false;
//    }
//
//    private Boolean isSelectAll = Boolean.FALSE;

    private PermitInfo permitInfo;
    public void onAddPermitInfo() {
        permitInfo = new PermitInfo();
    }



    public void onEditPermitInfo(String name) {
        for (PermitInfo permit : permitInfos) {
            if (null != permit.getCertificateName() && name.equals(permit.getCertificateName())) {
                permitInfo = permit;
            }
        }
    }

    public void savePermitInfo() {
        if(permitInfo!=null &&( permitInfo.getUid()!=null || permitInfo.getNodeId()!=null)){
            return;
        }

        if(permitInfos==null){
            permitInfos=new ArrayList<>();
        }
        permitInfo.setTenantUID(user.getTenantUID());
        permitInfo.setSiteUID(user.getSiteUID());
        permitInfo.setHospitalUID(user.getHospitalUID());
        permitInfo.setInstitutionUID(user.getInstitutionUID());
        permitInfo.setCreatedByName(user.getName());
        permitInfo.setCreatedByUid(user.getUid());
        permitInfo.setNodeId(permitInfos.size());

        permitInfos.add(permitInfo);
    }

    public PermitInfo getPermitInfo() {
        return permitInfo;
    }

    public void setPermitInfo(PermitInfo permitInfo) {
        this.permitInfo = permitInfo;
    }
}
