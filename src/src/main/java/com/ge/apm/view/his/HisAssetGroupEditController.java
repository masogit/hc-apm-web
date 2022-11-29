package com.ge.apm.view.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.service.his.HisAssetGroupService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class HisAssetGroupEditController extends JpaCRUDController<HisAssetGroupAssetMatch> implements ServerEventInterface {

    private OrgInfoRepository orgInfoDao;
    private HisAssetGroupRepository hisAssetGroupRepository;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;
    private AssetInfoRepository assetInfoRepository;
    private HisAssetGroupService hisAssetGroupService;
    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;

    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private DualListModel<AssetInfo> availableAssets = new DualListModel<>();

    @Getter @Setter
    private OrgInfo filterDeptOrg;
    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;

    List<HisAssetGroup> hisAssetGroups;

    @Getter @Setter
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateStartDate;

    @Getter @Setter
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateEndDate;

    @Getter
    private boolean boundAssetsUpdated;

    @Override
    protected GenericRepository<HisAssetGroupAssetMatch> getDAO() {
        return null;
    }

    @Override
    protected void init(){
        hisAssetGroupRepository = WebUtil.getBean(HisAssetGroupRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        userSiteOrgTreeComponentController = WebUtil.getBean(UserSiteOrgTreeComponentController.class);
        hisAssetGroupService = WebUtil.getBean(HisAssetGroupService.class);
        userSiteOrgTreeComponentController.registerController(this);
    }

    public void onAssetsTransfer(TransferEvent event) {
        //no need to use event to control pick list, leave it
    }

    public boolean isAssetsUpdated() {
        if (!isEditMode()) {
            return false;
        }
        HisAssetGroup hisAssetGroup = hisAssetGroupRepository.findByName(selected.getAssetGroupName());

        return hisAssetGroupService.needPromptInputDates(this.availableAssets.getTarget(), hisAssetGroup, filterDeptOrg);
    }


    public void setSubEquipment(Boolean subEquipment) {
        this.selected.setIsSubEquipment(subEquipment);
        getQueryAssetsList();
    }

    public List<HisAssetGroup> getHisAssetGroups() {
        if (null == hisAssetGroups || hisAssetGroups.isEmpty()) {
            hisAssetGroups = hisAssetGroupRepository.findAll();
        }
        return hisAssetGroups;
    }

    public void clearAssetGroups() {
        this.hisAssetGroups = null;
    }

    public void onClinicalDeptChange() {

        getQueryAssetsList();
    }

    public void getQueryAssetsList() {
        List<AssetInfo> sourceAssets;
        if(selectedOrg != null){
            sourceAssets = hisAssetGroupService.getAssetListByDept(selectedOrg, filterDeptOrg, selected.getIsSubEquipment());
            sourceAssets = new ArrayList<>(sourceAssets);
            sourceAssets.removeAll(availableAssets.getTarget());
        }else{
            sourceAssets = new ArrayList<>();
        }

        availableAssets.setSource(sourceAssets);
    }

    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.emptyList();
        }else{
            clinicalDeptList = orgInfoDao.getUserAccessibleClinicalDeptsBySelectedSite(selectedOrg.getUid(), UserContextService.getCurrentUserAccount().getUid());
        }
    }

    public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? Boolean.FALSE : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }

    public boolean isEditMode() {
        if (null == this.selected || null == this.selected.getUuid()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    @Override
    public void save(){

        List<AssetInfo> targetAssets = availableAssets.getTarget();

        HisAssetGroup hisAssetGroup = hisAssetGroupRepository.findByName(selected.getAssetGroupName());
        for(AssetInfo assetInfo : targetAssets){
            List<HisAssetGroupClinicalMatch> hisAssetGroupClinicalMatchList = hisAssetGroupClinicalMatchDao.findByAssetUid(assetInfo.getUid());
            List<String> assetGroupUIds = hisAssetGroupClinicalMatchList.stream().map(HisAssetGroupClinicalMatch::getHisAssetGroupUId).collect(Collectors.toList());
            if(!hisAssetGroupClinicalMatchList.isEmpty() && !assetGroupUIds.contains(hisAssetGroup.getUuid())){
                String assetGroupName = hisAssetGroupRepository.findNameByUuid(hisAssetGroup.getUuid());
                WebUtil.addErrorMessage("设备:" + assetInfo.getName() +" 已绑定分组: "+ assetGroupName);
                return;
            }
        }

        if (isEditMode()) {
            if (hisAssetGroupService.needPromptInputDates(targetAssets, hisAssetGroup, filterDeptOrg) && (updateStartDate == null || updateEndDate == null || updateEndDate.compareTo(updateStartDate) <= 0)) {
                WebUtil.addSuccessMessage("更新设备填写开始时间或结束时间错误");
                return;
            }
            hisAssetGroupService.bindAssetsToGroup(targetAssets, hisAssetGroup, filterDeptOrg, this.selected.getIsSubEquipment(), updateStartDate, updateEndDate);

            WebUtil.addSuccessMessage("修改成功");
        } else {
            hisAssetGroupService.bindAssetsToGroup(targetAssets, hisAssetGroup, filterDeptOrg, this.selected.getIsSubEquipment(), null, null);
            WebUtil.addSuccessMessage("创建成功");
        }

    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

        if (UserSiteOrgTreeComponentController.EDIT_SITE_ORG_CHANGE.equals(eventName)) {
            //his org code changes
            this.selectedOrg = (OrgInfo)eventObject;

            loadClinicalDept();
        } else {

            updateStartDate = null;
            updateEndDate = null;

            if (eventObject == null) {
                //create
                this.selected = new HisAssetGroupAssetMatch();
                this.selected.setIsSubEquipment(false);

                this.selectedOrg = null;
                this.filterDeptOrg = null;

                userSiteOrgTreeComponentController.setSelectedOrg(null);

                availableAssets = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
            } else {
                //edit
                this.selected = (HisAssetGroupAssetMatch)eventObject;

                selectedOrg = orgInfoDao.findByUid(selected.getOrgInfoUid());
                filterDeptOrg = orgInfoDao.findByUid(selected.getClinicalDeptUid());

                String ids = hisAssetGroupRepository.findByName(selected.getAssetGroupName()).getUuid() + filterDeptOrg.getUid();
                List<String> assetIds = hisAssetGroupClinicalMatchDao.findAssetIds(ids);
                List<AssetInfo> targetAssets = assetInfoRepository.findAllByUidIn(assetIds);
                availableAssets.setTarget(targetAssets);

                getQueryAssetsList();
            }
        }

    }
}
