package com.ge.apm.view.his;


import com.ge.apm.dao.*;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.AssetInfoDto;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisAssetSfxmMatch;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DualListModel;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class HisAssetSfxmMatchController extends GenericCRUDUUIDController<HisAssetSfxmMatch> implements ServerEventInterface {


    private HisOrgComponentController orgComponentController;

    @Getter
    @Setter
    private AssetInfoDto assetInfoDto;

    @Getter
    @Setter
    private HisEquipmentSfxm hisEquipmentSfxm;
    private OrgInfoRepository orgInfoDao;
    private AssetInfoRepository assetInfoRepository;
    private HisEquipmentSfxmRepository hisEquipmentSfxmRepository;
    private HisAssetSfxmMatchRepository hisAssetSfxmMatchRepository;


    @Setter
    private DualListModel<AssetInfo> apmAvailableAssets = new DualListModel<>();
    @Setter
    private DualListModel<HisEquipmentSfxm> connAvailableAssets = new DualListModel<>();
    final PageRequest pageRequest = new PageRequest(0 ,100);

    @Override
    protected void init() {
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);
        orgComponentController.registerController(this);
        assetInfoDto = new AssetInfoDto();
        hisEquipmentSfxm=new HisEquipmentSfxm();


        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        hisEquipmentSfxmRepository = WebUtil.getBean(HisEquipmentSfxmRepository.class);
        hisAssetSfxmMatchRepository = WebUtil.getBean(HisAssetSfxmMatchRepository.class);

    }

    @Override
    protected GenericRepositoryUUID<HisAssetSfxmMatch> getDAO() {
        return null;
    }



    public void firstQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();

        searchFiltersAsset.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        searchFiltersAsset.add(new SearchFilter("isDeleted", SearchFilter.Operator.EQ, false));
        searchFiltersAsset.add(new SearchFilter("isValid", SearchFilter.Operator.EQ, true));

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        if (selectedOrg != null) {
            List<String> accessibleSites = orgInfoDao.getSiteOrgUidsByParentUid(selectedOrg.getUid());
            searchFiltersAsset.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
            searchFiltersAsset.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, accessibleSites, "1*2+"));
        }

        if (assetInfoDto.getPurchasePrice() != null && assetInfoDto.getPurchasePrice().compareTo(0d) > 0) {
            searchFiltersAsset.add(new SearchFilter("purchasePrice", SearchFilter.Operator.GTE, assetInfoDto.getPurchasePrice()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getDepartNum())) {
            searchFiltersAsset.add(new SearchFilter("departNum", SearchFilter.Operator.LIKE, assetInfoDto.getDepartNum().trim()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getName())) {
            searchFiltersAsset.add(new SearchFilter("name", SearchFilter.Operator.LIKE, assetInfoDto.getName().trim()));
        }
        if (StringUtils.isNotBlank(assetInfoDto.getFunctionType())) {
            searchFiltersAsset.add(new SearchFilter("functionType", SearchFilter.Operator.LIKE, assetInfoDto.getFunctionType().trim()));
        }

        List<AssetInfo> assetInfos = assetInfoRepository.findBySearchFilter(searchFiltersAsset,pageRequest).getContent();
        apmAvailableAssets.setSource(assetInfos);
    }


    public void secondQueryOrder() {
        List<SearchFilter> searchFiltersAsset = new ArrayList<>();


        if (StringUtils.isNotBlank(hisEquipmentSfxm.getSfxmId())) {
            searchFiltersAsset.add(new SearchFilter("sfxmId", SearchFilter.Operator.LIKE, hisEquipmentSfxm.getSfxmId().trim()));
        }
        if (StringUtils.isNotBlank(hisEquipmentSfxm.getSfxmName())) {
            searchFiltersAsset.add(new SearchFilter("sfxmName", SearchFilter.Operator.LIKE, hisEquipmentSfxm.getSfxmName().trim()));
        }
        if (StringUtils.isNotBlank(hisEquipmentSfxm.getSfxmLb())) {
            searchFiltersAsset.add(new SearchFilter("sfxmLb", SearchFilter.Operator.LIKE, hisEquipmentSfxm.getSfxmLb().trim()));
        }

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        if (selectedOrg != null) {
            searchFiltersAsset.add(new SearchFilter("hosId", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFiltersAsset.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        }
        searchFiltersAsset.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID()));
        List<HisEquipmentSfxm> conns = hisEquipmentSfxmRepository.findBySearchFilter(searchFiltersAsset,pageRequest).getContent();


        connAvailableAssets.setSource(conns);

    }

    public void match() {

        List<AssetInfo> apmTargetAssets = apmAvailableAssets.getTarget();
        List<HisEquipmentSfxm> connTargetAssets = connAvailableAssets.getTarget();

        if (apmTargetAssets.isEmpty() || connTargetAssets.isEmpty()) {
            WebUtil.addErrorMessageKey("请选择数据");
            return;
        }

        List<String> assetUids = apmTargetAssets.stream().map(AssetInfo::getUid).collect(Collectors.toList());
        List<HisAssetSfxmMatch> byAssetUids = hisAssetSfxmMatchRepository.findByAssetUids(assetUids);
        Map<String, List<HisAssetSfxmMatch>> group = byAssetUids.stream().collect(Collectors.groupingBy(HisAssetSfxmMatch::getAssetUid));

        List<HisAssetSfxmMatch> list = new ArrayList<>();
        for (AssetInfo apmTargetAsset : apmTargetAssets) {
            List<HisAssetSfxmMatch> hisAssetSfxmMatches = group.get(apmTargetAsset.getUid());
            List<String> collect = Optional.ofNullable(hisAssetSfxmMatches).map(lists -> lists.stream().map(HisAssetSfxmMatch::getSfxmId).collect(Collectors.toList())).orElse(new ArrayList<>());
            for (HisEquipmentSfxm connTargetAsset : connTargetAssets) {
                if(!collect.contains(connTargetAsset.getSfxmId())){
                    HisAssetSfxmMatch hisAssetSfxmMatch = getHisAssetSfxmMatch(apmTargetAsset, connTargetAsset);
                    list.add(hisAssetSfxmMatch);
                }
            }
        }

        hisAssetSfxmMatchRepository.save(list);
        clearSelectedInPickList();
        WebUtil.addSuccessMessage("匹配成功");
    }
    private void clearSelectedInPickList() {

        apmAvailableAssets.getTarget().clear();
        connAvailableAssets.getTarget().clear();
    }
    private static HisAssetSfxmMatch getHisAssetSfxmMatch(AssetInfo apmTargetAsset, HisEquipmentSfxm connTargetAsset) {
        HisAssetSfxmMatch hisAssetSfxmMatch = new HisAssetSfxmMatch();
        hisAssetSfxmMatch.setAssetName(apmTargetAsset.getName());
        hisAssetSfxmMatch.setAssetUid(apmTargetAsset.getUid());
        hisAssetSfxmMatch.setSfxmId(connTargetAsset.getSfxmId());
        hisAssetSfxmMatch.setSfxmName(connTargetAsset.getSfxmName());
        hisAssetSfxmMatch.setOrgCode(connTargetAsset.getHosId());
        hisAssetSfxmMatch.setTenantUid(connTargetAsset.getTenantUid());
        hisAssetSfxmMatch.setSiteUid(connTargetAsset.getSiteUid());
        hisAssetSfxmMatch.setCreateDate(new Date());
        hisAssetSfxmMatch.setCreateBy( UserContextService.getCurrentUserAccount().getUid());
        return hisAssetSfxmMatch;
    }

    public DualListModel<AssetInfo> getApmAvailableAssets() {
        return apmAvailableAssets;
    }

    public void setApmAvailableAssets(DualListModel<AssetInfo> apmAvailableAssets) {
        this.apmAvailableAssets = apmAvailableAssets;
    }

    public DualListModel<HisEquipmentSfxm> getConnAvailableAssets() {
        return connAvailableAssets;
    }

    public void setConnAvailableAssets(DualListModel<HisEquipmentSfxm> connAvailableAssets) {
        this.connAvailableAssets = connAvailableAssets;
    }
}
