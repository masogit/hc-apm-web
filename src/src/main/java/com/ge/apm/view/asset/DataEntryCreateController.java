package com.ge.apm.view.asset;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.his.HisAssetGroup;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class DataEntryCreateController extends GenericCRUDUUIDController<DataEntry> implements ServerEventInterface {

    private HisAssetGroupAssetMatchRepository hisAssetGroupAssetMatchDao;
    private HisAssetGroupClinicalMatchRepository hisAssetGroupClinicalMatchDao;
    private HisAssetGroupRepository hisAssetGroupRepository;

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

    @Getter
    @Setter
    private AssetInfo onSelectAssetInfo;


    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;
    private AssetInfoRepository assetInfoRepository;

    @Getter
    @Setter
    private List<AssetInfo> assetInfos=new ArrayList<>();

    @Getter
    @Setter
    private List<AssetInfo> selectAssets=new ArrayList<>();

    @Getter
    @Setter
    private List<AssetInfo> selectAssets2=new ArrayList<>();

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
    List<HisAssetGroupAssetMatch> hisAssetGroupAssetMatches;

    private HisGroupDataEntryHistoryRepository hisGroupDataEntryHistoryRepository;
    @Getter
    @Setter
    private List<HisGroupDataEntryHistory> hisGroupDataEntryHistories;

    private DataEntryAssetDetailRepository dataEntryAssetDetailRepository;

    @Getter
    @Setter
    private boolean isSaveDisable=true;
    @Override
    protected GenericRepositoryUUID<DataEntry> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        hisAssetGroupAssetMatchDao = WebUtil.getBean(HisAssetGroupAssetMatchRepository.class);
        hisAssetGroupRepository = WebUtil.getBean(HisAssetGroupRepository.class);

        hisAssetGroupClinicalMatchDao = WebUtil.getBean(HisAssetGroupClinicalMatchRepository.class);
        dataEntryService = WebUtil.getBean(DataEntryService.class);
        i18nMessageService = WebUtil.getBean(I18nMessageService.class);
        user = UserContextService.getCurrentUserAccount();
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);

        userSiteOrgTreeComponentController = WebUtil.getBean(UserSiteOrgTreeComponentController.class);
        userSiteOrgTreeComponentController.registerController(this);

        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        hisGroupDataEntryHistoryRepository = WebUtil.getBean(HisGroupDataEntryHistoryRepository.class);
        dataEntryAssetDetailRepository = WebUtil.getBean(DataEntryAssetDetailRepository.class);

        getGetHisAssetGroups();
    }

    public void getGroupMatchAsset() {
        if(selectHisAssetGroupAssetMatch!=null){
            selected.setHisAssetGroupAssetMatchUid(selectHisAssetGroupAssetMatch.getUuid());
        }
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
                    BigDecimal divide = amount.divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_DOWN);
                    selectAssetInfo.setAmount(divide);
                }

            }


        }
    }

    public List<HisAssetGroupAssetMatch> getGetHisAssetGroups() {
        List<SearchFilter> searchFiltersGroup = new ArrayList<>();

        searchFiltersGroup.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, user.getTenantUID()));
        searchFiltersGroup.add(new SearchFilter("hisAssetGroup.type", SearchFilter.Operator.EQ, "Cost"));
        hisAssetGroupAssetMatches = hisAssetGroupAssetMatchDao.findBySearchFilter(searchFiltersGroup);
        return hisAssetGroupAssetMatches;
    }


    public List<HisAssetGroupAssetMatch> hisAssetGroupsString(String query) {
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(hisAssetGroupAssetMatches)){
            List<HisAssetGroupAssetMatch> list = hisAssetGroupAssetMatches.stream().filter(item->item.getHisAssetGroup().getName().contains(query)).collect(Collectors.toList());
            return list;
        }
        return new ArrayList<>();

    }


    @Override
    public void save() {
        sharedEntryAmount();
        if(selected.getUid()==null){
            selected.setCreatedBy(user.getUid());
            selected.setCreatedName(user.getName());
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
            selected.setHisAssetGroupAssetMatchUid(selectHisAssetGroupAssetMatch.getUuid());
            dataEntryService.save(selected,user,selectAssetInfos);
            WebUtil.addSuccessMessage("添加成功");
        }else{

            DataEntry dataEntry = dataEntryService.getByDataEntryUid(selected.getUid());

            LocalDate startDate = new Date(dataEntry.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = new Date(dataEntry.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            LocalDate startDate1 = new Date(selected.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate1 =  new Date(selected.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if(dataEntry.getEntryAmount().compareTo(selected.getEntryAmount())==0   &&  startDate.equals(startDate1) && endDate.equals(endDate1) ){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addSuccessMessage("修改的信息没变");
                return;
            }
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
        dataEntryService.saveAssetEntry(selectAssets.get(0),dataEntries,user);

        List<String> hisAssetGroupUidByAssetUid = hisAssetGroupClinicalMatchDao.getHisAssetGroupUidByAssetUid(selectAssets.get(0).getUid(), user.getTenantUID());
        if(CollectionUtils.isEmpty(hisAssetGroupUidByAssetUid)){

            WebUtil.addSuccessMessage("设备录入数据成功");
            return;
        }
        List<HisAssetGroup> byUuidIn = hisAssetGroupRepository.findByUuidIn(hisAssetGroupUidByAssetUid);

        List<String> groupName = byUuidIn.stream().map(HisAssetGroup::getName).collect(Collectors.toList());
        WebUtil.addWarnMessage("设备录入数据成功;设备已存在 "+ JSON.toJSONString(groupName)+"分组中");

    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        checkDuplicateMap=new HashMap<>();
        if (UserSiteOrgTreeComponentController.ORG_CHANGE.equals(eventName)) {
            //his org code changes
            this.selectedOrg = (OrgInfo)eventObject;

            loadClinicalDept();
            if(crudActionName==null|| !crudActionName.equals("Create")){
                onSelectSiteTreeNode((OrgInfo)eventObject);
            }

        }else{
            qrCode=null;
            assetName=null;
            serialNum=null;
            assetGroup=null;
            assetInfos=new ArrayList<>();

            if (eventObject == null) {
                //create
                this.selected = new DataEntry();
                selectHisAssetGroupAssetMatch=null;
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
                    selectHisAssetGroupAssetMatch=null;
                    getGroupMatchAsset();
                    getDataEntryAssetHistory();
                    for (HisAssetGroupClinicalMatch selectAssetInfo : selectAssetInfos) {
                            selectAssetInfo.setAmount(null);
                    }
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

    private void getDataEntryAssetHistory() {
        hisGroupDataEntryHistories= hisGroupDataEntryHistoryRepository.findByDataEntryUid(selected.getUid());
        for (HisGroupDataEntryHistory hisGroupDataEntryHistory : hisGroupDataEntryHistories) {
            List<HisGroupDataEntryHistory.AssetGroupDetail> assetGroupDetails = JSONObject.parseObject(hisGroupDataEntryHistory.getJsonString(), new TypeReference<List<HisGroupDataEntryHistory.AssetGroupDetail>>() {});
            List<String> assetUids = assetGroupDetails.stream().map(HisGroupDataEntryHistory.AssetGroupDetail::getAssetUid).collect(Collectors.toList());
            List<AssetInfo> assetInfos = assetInfoRepository.findByUidIn(assetUids);
            Map<String, AssetInfo> assetInfoMap = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getUid, Function.identity(), (existing, replacement) -> existing));

            Map<String, String> assetMap = assetInfos.stream().collect(Collectors.toMap(AssetInfo::getUid, AssetInfo::getName, (existing, replacement) -> existing));
            assetGroupDetails.forEach(i->{
                i.setGroupName(selectHisAssetGroupAssetMatch.getHisAssetGroup().getName());
                i.setAssetName(assetMap.get(i.getAssetUid()));
                i.setAssetInfo(assetInfoMap.get(i.getAssetUid()));
            });
            hisGroupDataEntryHistory.setAssetGroupDetailList(assetGroupDetails);
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
        if((selectedOrg==null ||  filterDeptOrg==null) && (qrCode==null && serialNum==null && assetName==null)){
            return;
        }

        List<SearchFilter> assetFilters = new ArrayList<>();
        assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE));
        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE));
        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        if(selectedOrg!=null){
            List<String> siteOrgUids = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
            assetFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
            assetFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
            assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
        }

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

        isAllSelected=assetInfos.stream().allMatch(item-> Optional.ofNullable(selectedMap.get(item.getUid())).orElse(Boolean.FALSE));

    }

    public void updateSelctedAssetList2(AjaxBehaviorEvent event) {
        SelectBooleanCheckbox box = (SelectBooleanCheckbox)event.getSource();
        AssetInfo asset = (AssetInfo)box.getAttributes().get("assetObj");
        if(box.isSelected()) {
            selectAssets2.add(asset);
        } else {
            selectAssets2.remove(asset);
        }

        isAllSelected2=selectAssets.stream().allMatch(item-> Optional.ofNullable(selectedMap2.get(item.getUid())).orElse(Boolean.FALSE));

    }

    public void confirm(){
//        assetInfos.stream().filter(i->i.getUid().equals())
    }

    public void addAssetsOfCurrentPage(AjaxBehaviorEvent event) {
        if(isAllSelected) {
            assetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.TRUE);
                selectAssets.add(t);

            });
        } else {
            assetInfos.stream().forEach(t -> {
                selectedMap.put(t.getUid(), Boolean.FALSE);
                selectAssets.remove(t);
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

    public void onSelectAsset(){
        selectAssets.clear();
        selectAssets.add(onSelectAssetInfo);
    }

    public void checkDataEntryUpdate(){
        DataEntry dataEntry = dataEntryService.getByDataEntryUid(selected.getUid());

        LocalDate startDate = new Date(dataEntry.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = new Date(dataEntry.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate startDate1 = new Date(selected.getStartDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate1 =  new Date(selected.getEndDate().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if(dataEntry.getEntryAmount().compareTo(selected.getEntryAmount())!=0   ||  !startDate.equals(startDate1) || !endDate.equals(endDate1) ){
            isSaveDisable=false;

        }
    }


    @Getter
    @Setter
    private Map<String,String> checkDuplicateMap=new HashMap<>();

    public void checkDuplicate(DataEntry dataEntry) {

        List<I18nMessage> hisEntryType = i18nMessageService.getByMsgType("HisEntryType");

        if (dataEntry.getEntryAmount() != null && dataEntry.getStartDate() != null && dataEntry.getEndDate() != null && !CollectionUtils.isEmpty(selectAssets)) {
            int count = dataEntryAssetDetailRepository.findByAssetUidAndEntryTypeAndDate(selectAssets.get(0).getUid(), dataEntry.getEntryType(), dataEntry.getStartDate(), dataEntry.getEndDate());
            if (count > 0) {
                List<I18nMessage> collect = hisEntryType.stream().filter(item -> item.getMsgKey().equals(dataEntry.getEntryType())).collect(Collectors.toList());
                checkDuplicateMap.put(dataEntry.getEntryType(), "设备在同一时间段内已录入过"+collect.get(0).getValueZh()+",建议核实后再录入数据");
            }else{
                if(checkDuplicateMap.containsKey(dataEntry.getEntryType())){
                    checkDuplicateMap.remove(dataEntry.getEntryType());
                }
            }

        } else {
            if(checkDuplicateMap.containsKey(dataEntry.getEntryType())){
                checkDuplicateMap.remove(dataEntry.getEntryType());

            }
        }
    }

    public void checkGroupDuplicate(){
        List<I18nMessage> hisEntryTypes = i18nMessageService.getByMsgType("HisEntryType");
        if(selected!=null && selected.getEntryType()!=null && selected.getStartDate()!=null && selected.getEndDate()!=null && !CollectionUtils.isEmpty(selectAssetInfos)){
            checkDuplicateMap=new HashMap<>();
            List<I18nMessage> dataEntryType = hisEntryTypes.stream().filter(item -> item.getMsgKey().equals(selected.getEntryType())).collect(Collectors.toList());
            Set<String> assetUids = selectAssetInfos.stream().map(HisAssetGroupClinicalMatch::getAssetUid).collect(Collectors.toSet());
            List<DataEntryAssetDetail> dataEntryAssetDetails = dataEntryAssetDetailRepository.findByAssetUidsAndEntryTypeAndDate(assetUids, selected.getEntryType(), selected.getStartDate(), selected.getEndDate());
            Set<String> dbAssetUids = dataEntryAssetDetails.stream().map(DataEntryAssetDetail::getAssetUid).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(dbAssetUids)){
                Set<HisAssetGroupClinicalMatch> existAssets = selectAssetInfos.stream().filter(item -> dbAssetUids.contains(item.getAssetUid())).collect(Collectors.toSet());
                StringBuilder message= new StringBuilder();
                for (HisAssetGroupClinicalMatch existAsset : existAssets) {
                    String serialNum=existAsset.getAssetInfo().getSerialNum()==null?"":existAsset.getAssetInfo().getSerialNum();
                    message.append("【").append(existAsset.getAssetInfo().getName()).append(",").append(serialNum).append("】");
                }
                checkDuplicateMap.put(selected.getEntryType(),message+"在同一时间段内已录入过"+dataEntryType.get(0).getValueZh()+",建议核实后再录入数据");
            }else{
                checkDuplicateMap.clear();
            }

        }else{
            checkDuplicateMap.clear();
        }
    }

}
