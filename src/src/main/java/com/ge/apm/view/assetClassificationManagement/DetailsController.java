package com.ge.apm.view.assetClassificationManagement;


import com.baomidou.mybatisplus.annotations.TableField;
import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetClassificationModel;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.dto.SelectItemDto;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.PinYinComparatorByName;

import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@Data
public class DetailsController extends GenericCRUDController<AssetInfo> implements Serializable, ServerEventInterface {
    private static final Logger logger = LoggerFactory.getLogger(DetailsController.class);

    private AssetInfoRepository dao = null;
    private UserAccount user;
    @Getter
    @Setter
    private String oneSearchStr;

    private Set<String> assetDeptOrgUidSet;
    private Set<String> clinicalDeptOrgUidSet;

    private String filterClinicalDeptOrg;
    private String filterAssetDeptOrg;
    private List<OrgInfo> clinicalDeptList;
    //选定的院区
    private OrgInfo selectSite;

    private UaaService uaaService;
    private OrgInfoRepository orgInfoRepository;
    private OrgInfoTreeService orgInfoTreeService;

    private MasterDataService masterDataService;
    @Override
    protected void init() {


        dao = WebUtil.getBean(AssetInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        uaaService = WebUtil.getBean(UaaService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);

//        selectSite = orgInfoRepository.getByUid(user.getSiteUID());
        loadClinicalDeptList();
    }

    @Override
    protected Page<AssetInfo> loadData(PageRequest pageRequest) {
        long l = System.currentTimeMillis();
        logger.info("AssetInfoListController.loadData start time:{}",l);
        selected = null;
        setUserSearchFilters();
        Page<AssetInfo> res = dao.findBySearchFilter(this.searchFilters, pageRequest);
        addMisingClinicalDeptOrg(res.getContent());
        assetClassification32Dto(res.getContent());
        return res;
    }

    private void assetClassification32Dto(List<AssetInfo> content) {
        for (AssetInfo assetInfo : content) {
            assetInfo.setAssetClassification32Dto(new SelectItemDto(assetInfo.getAssetClassification32(),null));
        }


    }

    private void addMisingClinicalDeptOrg(List<AssetInfo> currentPageAssets) {
        Set<String> orgUids = currentPageAssets.stream().filter(item -> item.getClinicalDeptUID() != null).map(AssetInfo::getClinicalDeptUID).collect(Collectors.toSet());
        Set<String> orgUids2 = currentPageAssets.stream().filter(item -> item.getAssetDeptUID() != null).map(AssetInfo::getAssetDeptUID).collect(Collectors.toSet());
        orgUids.addAll(orgUids2);
        if (orgUids.isEmpty()) {
            return;
        }
        List<OrgInfo> orgInfoList = orgInfoRepository.findByUids(orgUids);
        Map<String, OrgInfo> orgInfoMap = orgInfoList.stream().collect(Collectors.toMap(OrgInfo::getUid, item -> item));
        currentPageAssets.stream().forEach(item -> {
            if (item.getClinicalDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getClinicalDeptUID()));
                if (found != null) {
                    item.setClinicalDeptOrg(found);
                }

            }
            if (item.getAssetDeptUID() != null) {
                OrgInfo found = orgInfoMap.get((item.getAssetDeptUID()));
                if (found != null) {
                    item.setAssetDeptOrg(found);
                }
            }
        });

    }
    public void setUserSearchFilters() {

        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID(), "1*"));
        if (!this.hasFilterOnField("siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }



        if (oneSearchStr != null && !oneSearchStr.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("name", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("namePyCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("departNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("financingNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("qrCode", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("serialNum", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("manufacture", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("clinicalDeptName", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
            this.updateSearchFilter(new SearchFilter("assetTags.tag", SearchFilter.Operator.LIKE, oneSearchStr, "1*3+"));
        }

        if (assetDeptOrgUidSet != null && !assetDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("assetDeptUID", SearchFilter.Operator.IN, new ArrayList<>(assetDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "assetDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }

        if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
            this.updateSearchFilter(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptUID".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
        }



        if (null != selectSite) {
            this.updateSearchFilter(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ,  selectSite.getUid(), "1*5+"));
            this.updateSearchFilter(new SearchFilter("institutionUID", SearchFilter.Operator.EQ,  selectSite.getUid(), "1*5+"));
//            this.updateSearchFilter(new SearchFilter("tenantUID", SearchFilter.Operator.IN, selectSite.getUid(), "1*5+"));
        } else {
            searchFilters.removeAll(searchFilters.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        }


    }
    private void loadClinicalDeptList() {
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public List<String> autoCompleteDeptName(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }

        List<OrgInfo> res = this.clinicalDeptList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                        || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase())))
                .collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }


    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterClinicalDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterClinicalDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterClinicalDeptOrg.equals(item.getName()))) {
            filterClinicalDeptOrg = null;
        }

        if (filterClinicalDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterClinicalDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }

    }

    public void onAssetDeptNameFilterChange(AjaxBehaviorEvent event) {

        if (event instanceof SelectEvent) {
            filterAssetDeptOrg = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(filterAssetDeptOrg) && !clinicalDeptList.stream().anyMatch(item -> filterAssetDeptOrg.equals(item.getName()))) {
            filterAssetDeptOrg = null;
        }

        if (filterAssetDeptOrg != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(filterAssetDeptOrg)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            assetDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            assetDeptOrgUidSet = Collections.emptySet();
        }

    }

    public void onSelectSiteTreeNode(NodeSelectEvent event) {

        this.searchFilters.clear();

        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        loadClinicalDeptList();

        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        assetDeptOrgUidSet = null;
        clinicalDeptOrgUidSet = null;
    }

    public void toggleEdit(AssetInfo item) {
        if (item.isEditing()) {

            dao.save(item);
        }
        item.setEditing(!item.isEditing());
    }

    public void cancelEdit(AssetInfo item) {
        item.setEditing(false);
    }

    @Override
    protected GenericRepository<AssetInfo> getDAO() {
        return dao;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }


    private boolean editing = false;
    public void edit(AssetInfo asset) {
        selected=asset;
        selected.setEditing(true);
        editing=true;
//        if(CollectionUtils.isEmpty(asset.getAvailableCategories())){
//            AssetClassificationModel assetClassificationModel=new AssetClassificationModel();
//            assetClassificationModel.setBrand(asset.getMakerShortName());
//            assetClassificationModel.setPattern(asset.getFunctionType());
//            assetClassificationModel.setSourceName(asset.getName());
//            assetClassificationModel.setFactory(asset.getManufacture());
//            List<SelectItemDto> selectItemDtos = masterDataService.autoAssetClassification32(assetClassificationModel);
//            if(!CollectionUtils.isEmpty(selectItemDtos)){
////                List<String> collect = selectItemDtos.stream().map(SelectItemDto::getLabel).collect(Collectors.toList());
//                asset.setAvailableCategories(selectItemDtos);
//            }
//        }

    }
    public  List<SelectItemDto>  autoAssetClassification32() {


        AssetClassificationModel assetClassificationModel=new AssetClassificationModel();
        assetClassificationModel.setId(selected.getUid());
        assetClassificationModel.setBrand(selected.getMakerShortName());
        assetClassificationModel.setPattern(selected.getFunctionType());
        assetClassificationModel.setSourceName(selected.getName());
        assetClassificationModel.setFactory(selected.getManufacture());
        // ONNX FULL 模型扩展字段
        assetClassificationModel.setDepartment(selected.getClinicalDeptName());
        String riskVal = selected.getRiskGrade() != null
            ? FieldValueMessageController.fieldValue("riskGrade", String.valueOf(selected.getRiskGrade())) : null;
        assetClassificationModel.setRiskLevel(StringUtils.isNotBlank(riskVal) ? riskVal : null);
        String meterVal = selected.getMeasureGrade() != null
            ? FieldValueMessageController.fieldValue("measureGrade", String.valueOf(selected.getMeasureGrade())) : null;
        assetClassificationModel.setMeterLevel(StringUtils.isNotBlank(meterVal) ? meterVal : null);
        assetClassificationModel.setRegistrationNo(selected.getRegistrationNo());
        assetClassificationModel.setIsEmergencyDevice(
                selected.getEmergencyEquipment() != null ? FieldValueMessageController.fieldValue("emergencyEquipment", String.valueOf(selected.getEmergencyEquipment())) : null);
        assetClassificationModel.setPurchasePrice(
            selected.getPurchasePrice() != null ? String.valueOf(selected.getPurchasePrice()) : null);

        return  masterDataService.autoAssetClassification32(assetClassificationModel);

    }


    public void cancel(AssetInfo assetInfo) {
        assetInfo.setEditing(false);
    }

    public void saveDetail(AssetInfo assetInfo) {
        assetInfo.setAssetClassification32(assetInfo.getAssetClassification32Dto().getLabel());
        dao.save(assetInfo);
        assetInfo.setEditing(false);

    }
}
