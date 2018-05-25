/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.V2_PmScheduleRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.PmScheduleData;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.components.CustomPanelGrid;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetInfoDetailController {

    private AssetInfoRepository assetDao;
    private OrgInfoRepository orgDao;
    private V2_PmScheduleRepository pmSchDao;
    private BlobObjectRepository blobDao;
    private CustomConfigService ccService;
    private AssetInfoMgrService aimService;
    private Boolean terminate;
    private AssetInfo selected;
    private List<V2_BlobObject> attachements;
    
    private CustomConfigObject customConfig;
    
    private CustomPanelGrid basicFieldsPanelGrid;
    private CustomPanelGrid detailFieldsPanelGrid;
    
    private Map<String, UIComponent> detailFields = new HashMap();

    private List<String> readableFieldsList;
    private TreeNode parentsTree;
    
    @PostConstruct
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        pmSchDao = WebUtil.getBean(V2_PmScheduleRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
        terminate = terminateStr == null ? false : Boolean.parseBoolean(terminateStr);
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        selected = assetDao.findById(Integer.parseInt(selectedId));
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());
    }
    
    public OrgInfo getOrgInfoByUid(String uid) {
        return orgDao.getByUid(uid);
    }

    public List<V2_BlobObject> getAttachList(String assetuid, String type) {
        if (attachements == null || attachements.isEmpty()) {
            List<SearchFilter> sfs = new ArrayList();
            sfs.add(new SearchFilter("boUuid", SearchFilter.Operator.EQ, assetuid));
            attachements = blobDao.findBySearchFilter(sfs);
        }
        return attachements.stream().filter(item -> item.getBoSubType() != null && item.getBoSubType().equals(type)).collect(Collectors.toList());
    }

    public List<PmScheduleData> getPmScheduleData(String type) {
        if (this.selected == null) {
            return null;
        }
        List<PmScheduleData> pmsList = pmSchDao.getPmScheduleList(this.selected.getUid(), type);
        return pmsList;
    }
    
    public String getContractListLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/List?assetId=" + selected.getId() + "&actionName=Create";
    }
    
    public String getListPageLink() {
        return "List?faces-redirect=true" + (terminate ? "&terminate=true" : "");
    }
    
    public String getParentAssetName() {
        if (this.selected.getParentAssetId() != null) {
            AssetInfo assetInfo = this.assetDao.getById(this.selected.getParentAssetId());
            if (assetInfo != null) {
                if (StringUtils.isEmpty(assetInfo.getFunctionType())) {
                    return assetInfo.getName();
                } else {
                    return assetInfo.getName() + "(" + assetInfo.getFunctionType() + ")";
                }
            }
        }
        return "";
    }

    public String getCustomConfigString(){
        return JSONObject.toJSONString(customConfig);
    }
    
    public TreeNode getParentsRelationTree(){
        if(parentsTree==null){
            parentsTree = aimService.getParentAssetTree(selected);
            TreeNodeUtil.expandTreeNode(parentsTree);
        }
        return parentsTree;
    }
    
    public boolean showParentsTree(){
        TreeNode node = getParentsRelationTree();
        if(node==null || node.getChildCount()==0){
            return false;
        }
        return node.getChildren().stream().anyMatch(item-> item.getChildCount()>0);
    }
    
    public String getDetailPageLink(String uid){
        AssetInfo viewAsset = assetDao.findByUid(uid);
        String url = "actionName=View&selectedid="
                + viewAsset.getId().toString()
                + "&asset_name=" + viewAsset.getName()
                + "&terminate=" + this.terminate;
        url = "AssetDetail.xhtml?faces-redirect=true&str=" + UrlParamUtil.encodeUrlQueryString(url);
        return url;
    }
    
    //getter and setter
    public Boolean getTerminate() {
        return terminate;
    }

    public AssetInfo getSelected() {
        return selected;
    }

    public CustomConfigObject getCustomConfig() {
        return customConfig;
    }

    public CustomPanelGrid getBasicFieldsPanelGrid() {
        if(null==basicFieldsPanelGrid){
            basicFieldsPanelGrid=new CustomPanelGrid();
            basicFieldsPanelGrid.setCustomUIComponents(customConfig.getAssetsDetailpageBasicFields().stream()
                    .filter(item-> readableFieldsList.contains(item)).map(item-> detailFields.get(item)).collect(Collectors.toList()));
        }
        return basicFieldsPanelGrid;
    }

    public CustomPanelGrid getDetailFieldsPanelGrid() {
        if(null==detailFieldsPanelGrid){
            detailFieldsPanelGrid=new CustomPanelGrid();
            detailFieldsPanelGrid.setCustomUIComponents(customConfig.getAssetsDetailpageDetailFields().stream()
                    .filter(item-> readableFieldsList.contains(item)).map(item-> detailFields.get(item)).collect(Collectors.toList()));
        }
        return detailFieldsPanelGrid;
    }

    public void setBasicFieldsPanelGrid(CustomPanelGrid basicFieldsPanelGrid) {
        this.basicFieldsPanelGrid = basicFieldsPanelGrid;
    }

    public void setDetailFieldsPanelGrid(CustomPanelGrid detailFieldsPanelGrid) {
        this.detailFieldsPanelGrid = detailFieldsPanelGrid;
    }

    
    public Map<String, UIComponent> getDetailFields() {
        return detailFields;
    }

    public void setDetailFields(Map<String, UIComponent> detailFields) {
        this.detailFields = detailFields;
    }
    
}
