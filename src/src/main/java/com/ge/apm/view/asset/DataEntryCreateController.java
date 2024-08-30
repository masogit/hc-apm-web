package com.ge.apm.view.asset;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisAssetGroupAssetMatchRepository;
import com.ge.apm.dao.HisAssetGroupClinicalMatchRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.his.HisAssetGroupAssetMatch;
import com.ge.apm.domain.his.HisAssetGroupClinicalMatch;
import com.ge.apm.service.asset.DataEntryService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.common.I18nMessageService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class DataEntryCreateController extends GenericCRUDUUIDController<DataEntry> implements ServerEventInterface {

    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchDao;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;

    private DataEntryService dataEntryService;

    private UserAccount user;
    private I18nMessageService i18nMessageService;

    @Getter
    @Setter
    private List<DataEntry> assetDataEntrys;

    @Getter
    @Setter
    private HisAssetGroupAssetMatch  selectHisAssetGroupAssetMatch;

    @Getter
    @Setter
    private List<HisAssetGroupClinicalMatch> selectAssetInfos = new ArrayList<>();

    @Getter @Setter
    private OrgInfo selectedOrg;


    //选定的院区
    private OrgInfo selectSite;

    @Getter @Setter
    private OrgInfo filterDeptOrg;

    @Getter @Setter
    private Integer assetGroup;//设备分类

    @Getter @Setter
    private String assetName;//

    @Getter @Setter
    private String qrCode;//
    @Getter @Setter
    private String serialNum;//

    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;

    private OrgInfoRepository orgInfoDao;


    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;
    private AssetInfoRepository assetInfoRepository;

    @Getter
    @Setter
    private List<AssetInfo> assetInfos=new ArrayList<>();

    @Getter
    @Setter
    private List<AssetInfo> selectAssets=new ArrayList<>();

    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap=new HashMap<>();
    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap2=new HashMap<>();
    @Getter
    @Setter
    private Boolean isAllSelected = Boolean.FALSE;
    @Getter
    @Setter
    private Boolean isAllSelected2 = Boolean.FALSE;


    private UserContextService userContextService;
    @Override
    protected GenericRepositoryUUID<DataEntry> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        hisAssetGroupAssetMatchDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);

        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        dataEntryService = WebUtil.getBean(DataEntryService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        user = UserContextService.getCurrentUserAccount();
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);

        userSiteOrgTreeComponentController = WebUtil.getBean(UserSiteOrgTreeComponentController.class);
        userSiteOrgTreeComponentController.registerController(this);

        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);


    }

    public void getGroupMatchAsset() {
        String hisAssetGroupAssetMatchUid = this.selected.getHisAssetGroupAssetMatchUid();
        HisAssetGroupAssetMatch hisAssetGroupAssetMatch = hisAssetGroupAssetMatchDao.findById(hisAssetGroupAssetMatchUid);
        selectHisAssetGroupAssetMatch=hisAssetGroupAssetMatch;
        String groupUidClinicdepUid = hisAssetGroupAssetMatch.getAssetGroupUid() + hisAssetGroupAssetMatch.getOrgInfoUid();
//        List<HisAssetGroupClinicalMatch> byAssetGroupClinicalId = hisAssetGroupClinicalMatchDao.findByAssetGroupClinicalId(groupUidClinicdepUid);
        List<HisAssetGroupClinicalMatch> byAssetGroupClinicalId = hisAssetGroupClinicalMatchDao.findByHisAssetGroupUIdAndOrgInfoUid(hisAssetGroupAssetMatch.getAssetGroupUid(),hisAssetGroupAssetMatch.getOrgInfoUid());
        selectAssetInfos = byAssetGroupClinicalId;
        sharedEntryAmount();
    }

    public void sharedEntryAmount() {
        BigDecimal entryAmount = this.getSelected().getEntryAmount();
        if (entryAmount != null) {
            for (HisAssetGroupClinicalMatch selectAssetInfo : selectAssetInfos) {
                BigDecimal scaleAssessment = selectAssetInfo.getScaleAssessment();
                if (scaleAssessment != null) {
                    BigDecimal amount = entryAmount.multiply(scaleAssessment);
                    BigDecimal divide = amount.divide(BigDecimal.valueOf(100), 6);
                    selectAssetInfo.setAmount(divide);
                }

            }


        }
    }

    public List<HisAssetGroupAssetMatch> getGetHisAssetGroups() {
        List<SearchFilter> searchFiltersGroup = new ArrayList<>();

        searchFiltersGroup.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, user.getTenantUID()));
        searchFiltersGroup.add(new SearchFilter("hisAssetGroup.type", SearchFilter.Operator.EQ, "Cost"));
        List<HisAssetGroupAssetMatch> hisAssetGroupAssetMatches = hisAssetGroupAssetMatchDao.findBySearchFilter(searchFiltersGroup);
        return hisAssetGroupAssetMatches;
    }


    @Override
    public void save() {
        sharedEntryAmount();
        if(selected.getUid()==null){
            selected.setCreatedDate(new Date());
            selected.setTenantUid(user.getTenantUID());
            selected.setInstitutionUid(user.getInstitutionUID());
            selected.setHospitalUid(user.getHospitalUID());
            selected.setSiteUid(user.getSiteUID());
            selected.setLastModifiedDate(new Date());
            selected.setOperationUserUid(user.getUid());
            selected.setLastModifiedBy(user.getUid());
            selected.setGroupOrSingle(1);
            selected.setAssetGroupUid(selectHisAssetGroupAssetMatch.getAssetGroupUid());
            selected.setAssetGroupOrgInfoUid(selectHisAssetGroupAssetMatch.getOrgInfoUid());
            selected.setClinicalDeptUid(selectHisAssetGroupAssetMatch.getClinicalDeptUid());
            dataEntryService.save(selected,user,selectAssetInfos);
            WebUtil.addSuccessMessage("添加成功");
        }else{

            selected.setLastModifiedDate(new Date());
            selected.setLastModifiedBy(user.getUid());

            dataEntryService.save(selected,user,selectAssetInfos);
            WebUtil.addSuccessMessage("修改成功");
        }
    }

    public void saveAssetEntry(){
        if(CollectionUtils.isEmpty(selectAssets)){
            WebUtil.addWarnMessage("请选择设备才能提交录入数据");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);

            return;
        }
        List<DataEntry> dataEntries = assetDataEntrys.stream().filter(item -> item.getEntryAmount() != null && item.getStartDate() != null && item.getEndDate() != null).collect(Collectors.toList());

        if(CollectionUtils.isEmpty(dataEntries)){
            WebUtil.addWarnMessage("数据录入的信息需填写");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);

            return;
        }
        dataEntryService.saveAssetEntry(selectAssets,dataEntries,user);
        WebUtil.addSuccessMessage("设备录入数据成功");
    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {

        if (UserSiteOrgTreeComponentController.ORG_CHANGE.equals(eventName)) {
            //his org code changes
            this.selectedOrg = (OrgInfo)eventObject;

            loadClinicalDept();
            if(crudActionName==null|| !crudActionName.equals("Create")){
                onSelectSiteTreeNode((OrgInfo)eventObject);
            }

        }else{
            if (eventObject == null) {
                //create
                this.selected = new DataEntry();
                selectAssetInfos = new ArrayList<>();

                assetDataEntrys=new ArrayList<>();
//                List<I18nMessage> hisEntryType = i18nMessageService.getByMsgType("HisEntryType");
//                for (I18nMessage i18nMessage : hisEntryType) {
//                    DataEntry dataEntry = new DataEntry();
//                    dataEntry.setEntryType(i18nMessage.getMsgKey());
//                    assetDataEntrys.add(dataEntry);
//                }
                List<Map<String, String>> costTypeList = getCostType();
                List<String> costTypes = costTypeList.stream().map(i -> i.get("value")).collect(Collectors.toList());
                for (String  costType : costTypes) {
                    DataEntry dataEntry = new DataEntry();
                    dataEntry.setEntryType(costType);
                    assetDataEntrys.add(dataEntry);
                }
                userSiteOrgTreeComponentController.setSelectedOrg(null);
                crudActionName = "Create";
                filterDeptOrg=null;
                selectAssets=new ArrayList<>();


            } else {
                //edit
                this.selected = (DataEntry) eventObject;
                if(selected.getGroupOrSingle()==1){
                    getGroupMatchAsset();
                }else{

                    AssetInfo byUid = assetInfoRepository.findByUid(selected.getAssetUid());
                    selectAssets=new ArrayList<>();
                    selectAssets.add(byUid);
                    selectedOrg = orgInfoDao.findByUid(byUid.getSiteUID());
                    userSiteOrgTreeComponentController.setSelectedOrg(selectedOrg);

                    filterDeptOrg=orgInfoDao.findByUid(byUid.getClinicalDeptUID());
                    assetDataEntrys=new ArrayList<>();
                    assetDataEntrys.add(selected);
                    crudActionName = "Edit";


                }

            }
        }

    }

    public boolean isEditMode() {
        if (null == this.selected || null == this.selected.getUid()) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
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


    public void onSelectSiteTreeNode(OrgInfo selectSite) {
//
//        this.searchFilters.clear();
//
//        if (null != selectSite) {
//            this.selectSite = selectSite;
//        } else {
//            this.selectSite = null;
//        }


    }

    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.emptyList();
        }else{
            clinicalDeptList = orgInfoDao.getUserAccessibleClinicalDeptsBySelectedSite(selectedOrg.getUid(), UserContextService.getCurrentUserAccount().getUid());
        }
    }

    public void getQueryAssetsList(){
        if(selectedOrg==null){
            return;
        }
        List<String> siteOrgUids = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        assetFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        assetFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        if (null != filterDeptOrg) {
            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, filterDeptOrg.getUid()));
        }

        if(assetGroup!=null){
            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup));
        }
        if(assetName!=null){
            assetFilters.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetName));
        }
        if(qrCode!=null){
            assetFilters.add(new SearchFilter("qrCode", SearchFilter.Operator.EQ, qrCode));
        }
        if(serialNum!=null){
            assetFilters.add(new SearchFilter("serialNum", SearchFilter.Operator.EQ, serialNum));
        }
        Pageable pageable =  new PageRequest(0, 5000); //PageRequest.of(0, 5000);
        assetInfos= assetInfoRepository.findBySearchFilter(assetFilters, pageable).getContent();

    }
    public void updateSelctedAssetList(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectAssets.add(asset);
        } else {
            selectAssets.remove(asset);
        }
    }

    public void updateSelctedAssetList2(AjaxBehaviorEvent event) {
//        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
//        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
//        if(box.isSelected()) {
//            selectAssets.add(asset);
//        } else {
//            selectAssets.remove(asset);
//        }
    }

    public void confirm(){
//        assetInfos.stream().filter(i->i.getUid().equals())
    }

    public void addAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        if(isAllSelected) {
            assetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.TRUE);

            });
        } else {
            assetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.FALSE);

            });
        }
        isAllSelected = !isAllSelected;
    }
    public void addAssetsOfCurrentPage2(AjaxBehaviorEvent event) {
        if(isAllSelected2) {
            selectAssets.stream().forEach(t -> {
                selectedMap2.put(t.getUid(), Boolean.TRUE);

            });
        } else {
            selectAssets.stream().forEach(t -> {
                selectedMap2.put(t.getUid(), Boolean.FALSE);

            });
        }
        isAllSelected2 = !isAllSelected2;
    }

    public List<Map<String,String>> getCostType(){
        String configItemValue = userContextService.getConfigItemValue("costType");
        List<Map<String, String>> mapList = JSON.parseObject(configItemValue, new TypeReference<List<Map<String,String>>>(){});
        return mapList;
    }
}
