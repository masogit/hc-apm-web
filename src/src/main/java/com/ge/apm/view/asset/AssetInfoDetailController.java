/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.utils.TreeNodeUtil;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.StringUtils;
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
    private SupplierRepository supplierDao;
    private CustomConfigService ccService;
    private AssetInfoMgrService aimService;
    private Boolean terminate;
    private AssetInfo selected;
    private List<V2_BlobObject> attachements;
    
    private CustomConfigObject customConfig;
    
    private List<String> readableFieldsList;
    private TreeNode parentsTree;
    
    @PostConstruct
    protected void init() {
        assetDao = WebUtil.getBean(AssetInfoRepository.class);
        pmSchDao = WebUtil.getBean(V2_PmScheduleRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        supplierDao = WebUtil.getBean(SupplierRepository.class);
        ccService = WebUtil.getBean(CustomConfigService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        String encodeStr = WebUtil.getRequestParameter("str");
        String terminateStr = (String) UrlEncryptController.getValueFromMap(encodeStr, "terminate");
        terminate = terminateStr == null ? false : Boolean.parseBoolean(terminateStr);
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        selected = assetDao.findById(Integer.parseInt(selectedId));
        customConfig = ccService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        readableFieldsList = ccService.getReadAssetFieldsByRoles(customConfig, UserContextService.getRoles());

        if (selected.getSupplierId() != null) {
            Supplier supplier = supplierDao.findById(selected.getSupplierId());
            if (supplier != null) {
                selected.setVendorTel(supplier.getTel());
            }
        }
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
        return "/portal/asset/contract/ContractList?assetId=" + selected.getId();
    }

    public String getContractAddLink() {
        return "/portal/asset/contract/ContractEdit?assetId=" + selected.getId() + "&actionName=Create";
    }
    
    public String getListPageLink() {
        return "AssetList?faces-redirect=true" + (terminate ? "&terminate=true" : "");
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
    
    public boolean needShow(String itemKey, String fieldKey){
        return itemKey.equals(fieldKey) && readableFieldsList.contains(fieldKey);
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

}
