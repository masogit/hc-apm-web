package com.ge.apm.view.wo;

import com.ge.apm.dao.ZkbContractAssetRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
import com.ge.apm.domain.ZkbContractAsset;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@ManagedBean
@ViewScoped
public class AssetLifecycleEventZkbController extends AssetLifecycleEventController {

    ZkbContractAssetRepository zkbContractAssetRepository;
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");



    @Override
    protected void init() {
        super.init();
        zkbContractAssetRepository = WebUtil.getBean(ZkbContractAssetRepository.class);

    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        if (!this.hasFilterOnField("eventType")) {
            searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.EQ, tabStatus));// 3外部维修
        }
        boolean cancelled = searchFilters.stream().anyMatch(
                searchFilter -> searchFilter.value != null && StringUtils.contains(searchFilter.value.toString(), "Cancelled"));
        if(!cancelled){
            searchFilters.add(new SearchFilter("status", SearchFilter.Operator.NE, 3));
        }

        String td = ymd.format(new Date());
        Date d1 = null;
        try {
            d1 = ymd.parse(td);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ZkbContractAsset> zkbContractAssetList = zkbContractAssetRepository.findZkbContractAssetByTenantUID(UserContextService.getCurrentUserAccount().getTenantUID(),d1);
        List<String> assetUids = zkbContractAssetList.stream().map(ZkbContractAsset::getAssetId).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, assetUids));//根据有效期内的合同查询出设备

        pageRequest =addRequiredSortById(pageRequest);

        Page<AssetLifecycleEvent> pageResult = dao.findBySearchFilter(this.searchFilters, pageRequest);
        updateSelectStatus(pageResult);
        ignoreErrorDate(pageResult);
        return pageResult;
    }
}