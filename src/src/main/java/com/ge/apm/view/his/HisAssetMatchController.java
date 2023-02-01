package com.ge.apm.view.his;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.HisEquipmentConnectRepository;
import com.ge.apm.dao.HisEquipmentMatchRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.AssetInfoDto;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.domain.his.HisEquipmentMatch;
import com.ge.apm.service.his.HisAssetService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author
 */
@ManagedBean
@ViewScoped
public class HisAssetMatchController extends GenericCRUDUUIDController<HisEquipmentMatch> implements ServerEventInterface {

    public static final String FIELD_NAME_EQ_HOSID = "eqHosid";
    private OrgInfoRepository orgInfoDao;
    private HisEquipmentMatchRepository dao;
    private HisEquipmentConnectRepository connDao;
    private AssetInfoRepository assetInfoRepository;
    private HisAssetService hisAssetService;
    private HisOrgComponentController orgComponentController;

    @Setter
    private DualListModel<AssetInfo> apmAvailableAssets = new DualListModel<>();

    @Setter
    private DualListModel<HisEquipmentConnect> connAvailableAssets = new DualListModel<>();

    @Getter
    @Setter
    private AssetInfoDto assetInfoDto;

    @Getter
    @Setter
    private HisEquipmentConnect hisEquipmentConnect;

    //初始化定义pageRequest,提供给待匹配的页面使用
    final PageRequest pageRequest = new PageRequest(0 ,100);

    @Override
    protected HisEquipmentMatchRepository getDAO() {
        return dao;
    }

    @Override
    protected void init() {
        orgInfoDao = WebUtil.getBean(OrgInfoRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(HisEquipmentMatchRepository.class);
        connDao = WebUtil.getBean(HisEquipmentConnectRepository.class);
        hisAssetService = WebUtil.getBean(HisAssetService.class);

        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);

        hisEquipmentConnect = new HisEquipmentConnect();
        assetInfoDto = new AssetInfoDto();
        assetInfoDto.setPurchasePrice(500000d);

        orgComponentController.registerController(this);
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

        searchFiltersAsset.add(new SearchFilter("isMatched", SearchFilter.Operator.EQ, false));

        if (StringUtils.isNotBlank(hisEquipmentConnect.getEqName())) {
            searchFiltersAsset.add(new SearchFilter("eqName", SearchFilter.Operator.LIKE, hisEquipmentConnect.getEqName().trim()));
        }
        if (StringUtils.isNotBlank(hisEquipmentConnect.getEqId())) {
            searchFiltersAsset.add(new SearchFilter("eqId", SearchFilter.Operator.LIKE, hisEquipmentConnect.getEqId().trim()));
        }
        if (StringUtils.isNotBlank(hisEquipmentConnect.getEqHosid())) {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_EQ_HOSID, SearchFilter.Operator.LIKE, hisEquipmentConnect.getEqHosid().trim()));
        }
        if (StringUtils.isNotBlank(hisEquipmentConnect.getEqLb())) {
            searchFiltersAsset.add(new SearchFilter("eqLb", SearchFilter.Operator.EQ, hisEquipmentConnect.getEqLb().trim()));
        }

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        if (selectedOrg != null) {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_EQ_HOSID, SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
        } else {
            searchFiltersAsset.add(new SearchFilter(FIELD_NAME_EQ_HOSID, SearchFilter.Operator.IsNull, null));
        }

        List<HisEquipmentConnect> conns = connDao.findBySearchFilter(searchFiltersAsset,pageRequest).getContent();

        List<HisEquipmentConnect> targets = connAvailableAssets.getTarget();
        if(!targets.isEmpty()){
            //去除重新查询的数据中,不包含已加入的数据
            Set<String> uidSet = targets.stream().map(HisEquipmentConnect::getId).collect(Collectors.toSet());
            conns.stream().filter(item -> !uidSet.contains(item.getId())).collect(Collectors.toList());
        }

        connAvailableAssets.setSource(conns);

    }

    //提交匹配保存数据,狞龙说现在APM 与 联机数据是1对多关系，重新改造。
    public void match() {

        List<AssetInfo> apmTargetAssets = apmAvailableAssets.getTarget();
        List<HisEquipmentConnect> connTargetAssets = connAvailableAssets.getTarget();

        if (apmTargetAssets.size() !=1 || connTargetAssets.isEmpty()) {
            WebUtil.addErrorMessageKey("请选中一条匹配！");
            return;
        }

        List<String> ids = connTargetAssets.stream().map(HisEquipmentConnect::getId).collect(Collectors.toList());
        List<HisEquipmentConnect> lists = connDao.findHisEquipmentConnectByIds(ids);
        if(!lists.isEmpty()){
            WebUtil.addErrorMessageKey("选中的数据已经匹配，重新选择！");
            return;
        }

        hisAssetService.match(apmTargetAssets.get(0), connTargetAssets);

        clearSelectedInPickList();
    }

    private void clearSelectedInPickList() {

        apmAvailableAssets.getTarget().clear();
        connAvailableAssets.getTarget().clear();
    }

    public void apmOnAssetsTransfer(TransferEvent event) {
        for (Object item : event.getItems()) {
            if (event.isAdd()) {
                if (apmAvailableAssets.getTarget().size() > 1) {
                    WebUtil.addErrorMessageKey("APM已选设备只允许选中一条！");
                    apmAvailableAssets.getTarget().remove(item);
                    apmAvailableAssets.getSource().add((AssetInfo) item);

                } else {
                    apmAvailableAssets.getSource().remove(item);
                }

            }
        }
    }
    public void connOnAssetsTransfer(TransferEvent event) {
        //no need to control
    }

    public void refreshAfterOrgChange() {
        firstQueryOrder();
        secondQueryOrder();
    }

    public DualListModel<AssetInfo> getApmAvailableAssets() {

        return apmAvailableAssets;
    }

    public DualListModel<HisEquipmentConnect> getConnAvailableAssets() {

        return connAvailableAssets;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        clearSelectedInPickList();

        firstQueryOrder();
        secondQueryOrder();

    }
}


