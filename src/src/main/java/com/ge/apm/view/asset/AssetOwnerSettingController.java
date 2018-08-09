/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetCreateService;
import com.ge.apm.service.asset.AssetOwnerService;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.UserContextService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.data.domain.Page;
import webapp.framework.web.WebUtil;

/**
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class AssetOwnerSettingController {

    private DualListModel<AssetInfo> availableAssets;
    private List<AssetInfo> sourceAssets = Collections.emptyList();
    private List<AssetInfo> targetAssets;
    private AssetCreateService acServie;

    private AssetOwnerService aoService;
    private UserCommonService userCommonService;

    private AssetInfo queryAsset;

    private Integer ownerId;
    private Integer ownerId2;
    private Integer ownerId3;
    private Boolean isChanegeOwner;
    private Boolean isChanegeOwner2;
    private Boolean isChanegeOwner3;

    @PostConstruct
    protected void init() {

        acServie = WebUtil.getBean(AssetCreateService.class);
        aoService = WebUtil.getBean(AssetOwnerService.class);
        userCommonService = WebUtil.getBean(UserCommonService.class);
        UserAccount currentUser = UserContextService.getCurrentUserAccount();
        queryAsset = new AssetInfo();
        queryAsset.setSiteId(currentUser.getSiteId());
        queryAsset.setHospitalId(currentUser.getHospitalId());

        targetAssets = new ArrayList();

        availableAssets = new DualListModel<>(Collections.emptyList(), targetAssets);
        //not display data initially
//        getQueryAssetsList();

    }

    public void getQueryAssetsList() {
        Page page = aoService.getAssetListEx(queryAsset);
        sourceAssets = new ArrayList<AssetInfo>(page.getContent());
        targetAssets.forEach((item)->{
            if(sourceAssets.contains(item)){
                sourceAssets.remove(item);
            }
        });

        availableAssets = new DualListModel<>(sourceAssets, targetAssets);

        boolean needFilterFurther = page.getTotalElements() > page.getSize();
        if (needFilterFurther) {
            WebUtil.addSuccessMessage(WebUtil.getMessage("needFilterFurthur"));
        }
    }

    public void onAssetsTransfer(TransferEvent event) {

        for(Object item : event.getItems()) {
            if(event.isAdd()){
                targetAssets.add((AssetInfo)item);
            }else{
                targetAssets.remove((AssetInfo)item);
            }
        }
    }

    public List<OrgInfo> getClinicalDeptList() {
        return acServie.getClinicalDeptList(queryAsset.getSiteUID());
    }

    public List<UserAccount> getOwnerList() {
//        List<UserAccount> res = acServie.getAssetOnwers(queryAsset.getSiteId(), queryAsset.getHospitalId());
        List<UserAccount> res = userCommonService.getWorkerList(null,"");
        return res;
    }

    public void saveChange() {
        if (targetAssets.isEmpty()) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssets"));
        } else if (!(isChanegeOwner || isChanegeOwner2)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ChooseAssetsOwner"));
        } else {
            aoService.updateAssetOwner(targetAssets, isChanegeOwner, ownerId, isChanegeOwner2, ownerId2, isChanegeOwner3, ownerId3);
            availableAssets.setTarget(targetAssets);
        }
    }

    public void onCheckChange(){
    }
    
    //getter and setter
    public DualListModel getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(DualListModel<AssetInfo> availableAssets) {
        this.availableAssets = availableAssets;
    }

    public AssetInfo getQueryAsset() {
        return queryAsset;
    }

    public void setQueryAsset(AssetInfo queryAsset) {
        this.queryAsset = queryAsset;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getOwnerId2() {
        return ownerId2;
    }

    public void setOwnerId2(Integer ownerId2) {
        this.ownerId2 = ownerId2;
    }

    public Integer getOwnerId3() {
        return ownerId3;
    }

    public void setOwnerId3(Integer ownerId3) {
        this.ownerId3 = ownerId3;
    }

    public Boolean getIsChanegeOwner() {
        return isChanegeOwner;
    }

    public void setIsChanegeOwner(Boolean isChanegeOwner) {
        this.isChanegeOwner = isChanegeOwner;
    }

    public Boolean getIsChanegeOwner2() {
        return isChanegeOwner2;
    }

    public void setIsChanegeOwner2(Boolean isChanegeOwner2) {
        this.isChanegeOwner2 = isChanegeOwner2;
    }

    public Boolean getIsChanegeOwner3() {
        return isChanegeOwner3;
    }

    public void setIsChanegeOwner3(Boolean chanegeOwner3) {
        isChanegeOwner3 = chanegeOwner3;
    }

}
