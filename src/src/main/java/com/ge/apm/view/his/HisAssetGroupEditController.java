package com.ge.apm.view.his;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.service.his.HisAssetGroupService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.nurseSite.AssetSelectController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import webapp.framework.dao.GenericRepository;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class HisAssetGroupEditController extends JpaCRUDController<HisAssetGroupAssetMatch> implements ServerEventInterface {
    private static final Logger log = LoggerFactory.getLogger(HisAssetGroupEditController.class);

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

    @Getter
    @Setter
    private List<AssetInfo> selectAssetInfos=new ArrayList<>();

    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap;

    private Set<AssetInfo> selectedAssets;

    @Getter
    @Setter
    private Boolean isAllSelected = Boolean.FALSE;

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
        selectedMap=new HashMap<>(32);
        selectedAssets=new HashSet<>();
    }

    public void onAssetsTransfer(TransferEvent event) {
        //no need to use event to control pick list, leave it

//        for (Object item : event.getItems()) {
//            if (event.isAdd()) {
//                selectAssetInfo.add((AssetInfo) item);
//            } else {
//                selectAssetInfo.remove((AssetInfo) item);
//            }
//        }
    }
    public void addAssetInfo(){
        List<AssetInfo> target = availableAssets.getTarget();
        for (AssetInfo assetInfo : target) {
           if(!selectAssetInfos.contains(assetInfo)){
               selectAssetInfos.add(assetInfo);
               selectedMap.put(assetInfo.getUid(),false);
           }
        }
    }

    public void clearAssetInfo(){
        selectAssetInfos.clear();
        availableAssets.getSource().addAll(availableAssets.getTarget());
        availableAssets.getTarget().clear();
    }
    public void removeAssetInfo(){
        List<AssetInfo> target = availableAssets.getTarget();

        for (AssetInfo assetInfo : selectedAssets) {
            boolean b = selectAssetInfos.stream().anyMatch(item -> item.getUid().equals(assetInfo.getUid()));
            if(b){
                selectAssetInfos.remove(assetInfo);
            }
            if(target.contains(assetInfo)){
                target.remove(assetInfo);
                availableAssets.getSource().add(assetInfo);
            }
        }
        isAllSelected = Boolean.FALSE;
        availableAssets.setTarget(target);
    }

    public void updateSelctedAssetList(AjaxBehaviorEvent event) {
        log.debug("updateSelctedAssetList");
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectedAssets.add(asset);
        } else {
            selectedAssets.remove(asset);
        }
    }

    public void addAssetsOfCurrentPage5(AjaxBehaviorEvent event) {
        log.debug("value of isAllSelected5: {}", this.isAllSelected);
        if(isAllSelected) {
            selectAssetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.TRUE);
                selectedAssets.add(t);
            });
        } else {
            selectAssetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.FALSE);
                selectedAssets.remove(t);
            });
        }
        isAllSelected = !isAllSelected;
    }


    public boolean isAssetsUpdated() {
        if (!isEditMode()) {
            return false;
        }
        HisAssetGroup hisAssetGroup = hisAssetGroupRepository.findByUuid(selected.getAssetGroupUid());

        return hisAssetGroupService.needPromptInputDates(this.availableAssets.getTarget(), hisAssetGroup, filterDeptOrg);
    }


    public void setSubEquipment(Boolean subEquipment) {
        this.selected.setIsSubEquipment(subEquipment);
        getQueryAssetsList();
    }

    public List<HisAssetGroup> getHisAssetGroups() {
        if (null == hisAssetGroups || hisAssetGroups.isEmpty()) {

            UserAccount loginUser = UserContextService.getCurrentUserAccount();

            hisAssetGroups = hisAssetGroupRepository.findByTenantUid(loginUser.getTenantUID());
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

//        List<AssetInfo> targetAssets = availableAssets.getTarget();
        List<AssetInfo>  targetAssets=selectAssetInfos;
        UserAccount loginUser = UserContextService.getCurrentUserAccount();


        HisAssetGroup hisAssetGroup = hisAssetGroupRepository.findByUuid(selected.getAssetGroupUid());
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
            hisAssetGroupService.bindAssetsToGroup(targetAssets, hisAssetGroup, filterDeptOrg, this.selected.getIsSubEquipment(), updateStartDate, updateEndDate,selectedOrg);

            WebUtil.addSuccessMessage("修改成功");
        } else {
            hisAssetGroupService.bindAssetsToGroup(targetAssets, hisAssetGroup, filterDeptOrg, this.selected.getIsSubEquipment(), null, null,selectedOrg);
            WebUtil.addSuccessMessage("创建成功");
        }

    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

        if (UserSiteOrgTreeComponentController.ORG_CHANGE.equals(eventName)) {
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

                String ids = selected.getAssetGroupUid() + filterDeptOrg.getUid();
//                List<String> assetIds = hisAssetGroupClinicalMatchDao.findAssetIds(ids);
                List<HisAssetGroupClinicalMatch> byAssetGroupClinicalId = hisAssetGroupClinicalMatchDao.findByAssetGroupClinicalId(ids);
                List<String> assetIds = byAssetGroupClinicalId.stream().map(item -> item.getAssetUid()).collect(Collectors.toList());
                Map<String, Double> doubleMap = byAssetGroupClinicalId.stream().collect(Collectors.toMap(HisAssetGroupClinicalMatch::getAssetUid, HisAssetGroupClinicalMatch::getScaleAssessment));
                List<AssetInfo> targetAssets = assetInfoRepository.findAllByUidIn(assetIds);
                availableAssets.setTarget(targetAssets);

                getQueryAssetsList();
                selectAssetInfos=targetAssets;
                for (AssetInfo selectAssetInfo : selectAssetInfos) {
                    selectAssetInfo.setScaleAssessment(doubleMap.get(selectAssetInfo.getUid()));
                }


            }
        }

    }

    public void onSelectAllOfPage() {
        boolean isSelectedAll = !selectAssetInfos.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (AssetInfo item : selectAssetInfos) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }
}
