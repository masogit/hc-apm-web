package com.ge.apm.view.asset;

import com.ge.apm.dao.*;

import com.ge.apm.dao.mapper.AssetCostAmountInfoMapper;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.dao.mapper.HisEquipmentDataResultMapper;
import com.ge.apm.dao.mapper.IotPerformanceInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.AssetCostDTO;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.EfficiencyEquipmentInfoService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.component.UserSiteOrgTreeComponentController;
import com.ge.apm.view.pm.RelRuleAssetController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class EfficiencyEquipmentInfoController extends GenericCRUDUUIDController<EfficiencyEquipmentInfo> implements Serializable, ServerEventInterface {

    private EfficiencyEquipmentInfoService efficiencyEquipmentInfoService;

    private EfficiencyEquipmentInfoRepository efficiencyEquipmentInfoRepository;

    private UserAccount user;
    private UaaService uaaService;

    private UserContextService userContextService;

    private AssetInfoRepository assetInfoRepository;

    private OrgInfoRepository orgInfoDao;

    private AssetInfoService assetInfoService;

    private AssetInfoMapper assetInfoMapper;

    //选定的院区
    private OrgInfo selectSite;

    private List<EfficiencyEquipmentInfo> currentPageAssets;
    @Getter
    @Setter
    private Integer filterAssetGroup;
    //复选的设备
    @Getter
    @Setter
    private Map<String, Boolean> selectedMap;

    @Getter @Setter
    private DualListModel<AssetInfo> assets = new DualListModel<>();


    @Getter @Setter
    private OrgInfo selectedOrg;

    @Getter @Setter
    private OrgInfo filterDeptOrg;


    //采购原值(元)
    @Getter @Setter
    private Double purchasePrice;

    @Getter @Setter
    private Integer assetGroup;//设备分类

    @Getter @Setter
    private List<OrgInfo> clinicalDeptList;


    private Date startDate=DateUtil.currentMonthFirstDay();

    private Date endDate=new Date();

    private UserSiteOrgTreeComponentController userSiteOrgTreeComponentController;

    private HisEquipmentDataResultMapper hisEquipmentDataResultMapper;


    private AssetCostAmountInfoMapper assetCostAmountInfoMapper;

    private IotPerformanceInfoMapper iotPerformanceInfoMapper;

    @Override
    protected GenericRepositoryUUID<EfficiencyEquipmentInfo> getDAO() {
        return efficiencyEquipmentInfoRepository;
    }

    @Override
    protected void init() {
        efficiencyEquipmentInfoService = WebUtil.getBean(EfficiencyEquipmentInfoService.class);
        efficiencyEquipmentInfoRepository = WebUtil.getBean(EfficiencyEquipmentInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);

        userSiteOrgTreeComponentController = WebUtil.getBean(UserSiteOrgTreeComponentController.class);
        userSiteOrgTreeComponentController.registerController(this);
        hisEquipmentDataResultMapper = WebUtil.getBean(HisEquipmentDataResultMapper.class);
        assetCostAmountInfoMapper = WebUtil.getBean(AssetCostAmountInfoMapper.class);
        iotPerformanceInfoMapper = WebUtil.getBean(IotPerformanceInfoMapper.class);

        this.selectedMap = new HashMap();
    }



    @Override
    protected Page<EfficiencyEquipmentInfo> loadData(PageRequest pageRequest) {

        if (searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));

        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }
        Page<EfficiencyEquipmentInfo> page = efficiencyEquipmentInfoRepository.findBySearchFilter(this.searchFilters, pageRequest);
        currentPageAssets=page.getContent();
        if(!CollectionUtils.isEmpty(currentPageAssets)){

            calculationData(currentPageAssets);
        }
        return page;
    }

    private void calculationData(List<EfficiencyEquipmentInfo> currentPageAssets) {
        List<String> assetUids = currentPageAssets.stream().map(EfficiencyEquipmentInfo::getAssetUid).collect(Collectors.toList());
        List<AssetCostDTO> hisResult= hisEquipmentDataResultMapper.findByAssetUidAndDateInSunAmount(assetUids,startDate,endDate);
        Map<String, AssetCostDTO> hisResultMap = hisResult.stream().collect(Collectors.toMap(data -> data.getAssetUid(), data -> data));
        List<AssetCostDTO> assetCost=  assetCostAmountInfoMapper.findByAssetUidAndDateInSunAmount(assetUids,startDate,endDate);
        Map<String, AssetCostDTO> assetCostMap = assetCost.stream().collect(Collectors.toMap(data -> data.getAssetUid(), data -> data));
        List<AssetCostDTO>  iotList= iotPerformanceInfoMapper.findByAssetUidAndDateInSunAmount(assetUids,startDate,endDate);
        Map<String, AssetCostDTO> iotMap = iotList.stream().collect(Collectors.toMap(data -> data.getAssetUid(), data -> data));
        for (EfficiencyEquipmentInfo currentPageAsset : currentPageAssets) {
            AssetCostDTO hisResultDto= hisResultMap.get(currentPageAsset.getAssetUid());
            AssetCostDTO iot = iotMap.get(currentPageAsset.getAssetUid());
            AssetCostDTO assetCostDTO = assetCostMap.get(currentPageAsset.getAssetUid());
            BigDecimal income=hisResultDto!=null?hisResultDto.getAmount()!=null?hisResultDto.getAmount():BigDecimal.ZERO:BigDecimal.ZERO;
            BigDecimal cost=assetCostDTO!=null?assetCostDTO.getAmount()!=null?assetCostDTO.getAmount():BigDecimal.ZERO:BigDecimal.ZERO;
            BigDecimal checkNumberPeople=iot!=null?iot.getCheckNumberPeople()!=null?iot.getCheckNumberPeople():BigDecimal.ZERO:BigDecimal.ZERO;
            currentPageAsset.setIncome(income);
            currentPageAsset.setCost(cost);
            currentPageAsset.setCheckNumberPeople(checkNumberPeople);
            currentPageAsset.setProfits(income.subtract(cost));
            if(!income.equals(BigDecimal.ZERO)){
                currentPageAsset.setProfitsRate((income.subtract(cost)).divide(income,2));
            }else{
                currentPageAsset.setProfitsRate(BigDecimal.ZERO);
            }
        }

    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }

    }


    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPageAssets.stream().anyMatch(item -> !selectedMap.containsKey(item.getUid()) || !selectedMap.get(item.getUid()));
        for (EfficiencyEquipmentInfo item : currentPageAssets) {
            selectedMap.put(item.getUid(), !isSelectedAll);
        }
    }
    public void setSelectedAssetsUids() {
        List<String> selectedAssetUids = new ArrayList<>();
        selectedMap.keySet().stream().forEach(assetUid -> { if(selectedMap.get(assetUid)) selectedAssetUids.add(assetUid); });
        RelRuleAssetController.assetInfoUids = selectedAssetUids;
    }
    @Override
    public void onFilter(FilterEvent event) {
        super.onFilter(event);
        this.selectedMap.clear();
    }

    public boolean isChecked() {
        return selectedMap.containsValue(true);
    }


    public void deleteAsset(EfficiencyEquipmentInfo efficiencyEquipmentInfo) {
        efficiencyEquipmentInfoRepository.delete(efficiencyEquipmentInfo);
    }


    @Override
    public void save() {
        try {
            List<AssetInfo> target = assets.getTarget();
            if(CollectionUtils.isEmpty(target)){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage("请选择添加效益设备");
                return;
            }
            List<EfficiencyEquipmentInfo> efficiencyEquipmentInfos = initializationEfficiencyEquipmentInfo(target, user);
            efficiencyEquipmentInfoRepository.save(efficiencyEquipmentInfos);
            clearSelectedInPickList();
            assets.setSource(new ArrayList<>());
            assets.setTarget(new ArrayList<>());
            WebUtil.addErrorMessage("新增效益设备成功");
            crudActionName=null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<EfficiencyEquipmentInfo> initializationEfficiencyEquipmentInfo(List<AssetInfo> assetInfos, UserAccount user) {

        if (CollectionUtils.isEmpty(assetInfos)) return ListUtil.empty();
        return  assetInfos.stream().map(item->{
            item.setSpecialAsset(Boolean.TRUE);
            EfficiencyEquipmentInfo efficiencyEquipmentInfo= new EfficiencyEquipmentInfo();
            efficiencyEquipmentInfo.setAssetUid(item.getUid());
            efficiencyEquipmentInfo.setCreatedByUid(user.getUid());
            efficiencyEquipmentInfo.setCreatedDate(new Date());
            efficiencyEquipmentInfo.setAssetName(item.getName());
            efficiencyEquipmentInfo.setQrCode(item.getQrCode());
            efficiencyEquipmentInfo.setSerialNum(item.getSerialNum());
            efficiencyEquipmentInfo.setFunctionType(item.getFunctionType());
            return efficiencyEquipmentInfo;
        }).collect(Collectors.toList());
    }


    private void clearSelectedInPickList() {

        assets.getTarget().clear();

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

    public void onClinicalDeptChange() {

        getQueryAssetsList();
    }

    public void getQueryAssetsList() {
        List<AssetInfo> sourceAssets;
        if(selectedOrg != null){
            sourceAssets = getAssetListByDept(selectedOrg, filterDeptOrg);
            sourceAssets = new ArrayList<>(sourceAssets);
            sourceAssets.removeAll(assets.getTarget());
        }else{
            sourceAssets = new ArrayList<>();
        }

        assets.setSource(sourceAssets);
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        selectedOrg=null;
        userSiteOrgTreeComponentController.setSelectedOrg(null);
        filterDeptOrg=null;
        assetGroup=null;
        purchasePrice=null;
        assets= new DualListModel<>();
        super.prepareCreate();
    }


    public List<AssetInfo> getAssetListByDept(OrgInfo siteOrg, OrgInfo clinicalDept) {
        if (null == clinicalDept) {
            return new ArrayList<>();
        }
        List<String> siteOrgUids = orgInfoDao.getSiteOrgUidsByParentUid(siteOrg.getUid());
//
//        List<SearchFilter> assetFilters = new ArrayList<>();
//
//        assetFilters.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, Boolean.TRUE));
//        assetFilters.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, Boolean.FALSE));
//        assetFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, siteOrg.getTenantUID()));
//
//        assetFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
//        assetFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
//        assetFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, siteOrgUids, "1*2+"));
//
//        if (null != clinicalDept) {
//            assetFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, clinicalDept.getUid()));
//        }
//
//        if(assetGroup!=null){
//            assetFilters.add(new SearchFilter("assetGroup", SearchFilter.Operator.EQ, assetGroup));
//        }
//
//        if(purchasePrice!=null){
//            assetFilters.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, purchasePrice));
//        }
//
//        assetFilters.add(new SearchFilter("efficiencyEquipmentInfo.uid", SearchFilter.Operator.IsNull,null));
//
//        Pageable pageable =  new PageRequest(0, 5000); //PageRequest.of(0, 5000);
//        return assetInfoRepository.findBySearchFilter(assetFilters, pageable).getContent();

        List<AssetInfo> assetInfoIsNotEfficiencyEquipment = assetInfoMapper.getAssetInfoIsNotEfficiencyEquipment(siteOrg.getTenantUID(), siteOrgUids, clinicalDept.getUid(),assetGroup,purchasePrice);
        return assetInfoIsNotEfficiencyEquipment;


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

        }

    }

    public void onSelectSiteTreeNode(OrgInfo selectSite) {

        this.searchFilters.clear();

        if (null != selectSite) {
            this.selectSite = selectSite;
        } else {
            this.selectSite = null;
        }


    }

    private void loadClinicalDept(){
        if(null == selectedOrg){
            clinicalDeptList = Collections.emptyList();
        }else{
            clinicalDeptList = orgInfoDao.getUserAccessibleClinicalDeptsBySelectedSite(selectedOrg.getUid(), UserContextService.getCurrentUserAccount().getUid());
        }
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
