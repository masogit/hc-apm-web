package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.sharedAsset.SharedAssetBatchService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class SharedAssetBatchLeaseController extends MyCRUDController<AssetInfo> {

    private static final Logger logger = LoggerFactory.getLogger(SharedAssetBatchLeaseController.class);

    @Getter
    @Setter
    private String searchCriteria;

    private List<AssetInfo> selectedAssets = new ArrayList<>();

    private SharedAssetBatchService sharedAssetBatchService;

    @Override
    protected BaseMapper getDAO() {
        return null;
    }

    protected void init() {
        logger.info("Start to initialize component");
        sharedAssetBatchService = WebUtil.getBean(SharedAssetBatchService.class);
    }

    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        lazyModel.setRowCount(selectedAssets.size());
        return selectedAssets;
    }

    public void findAndSelectAsset() {
        boolean hasAdded = false;
        if(StringUtils.isNotEmpty(this.searchCriteria)) {
            hasAdded = selectedAssets.stream()
                                        .anyMatch(asset -> this.searchCriteria.equals(asset.getDepartNum())
                                                        || this.searchCriteria.equals(asset.getFinancingNum())
                                                        || this.searchCriteria.equals(asset.getQrCode()));
        }

        if(hasAdded) {

        } else {
            Wrapper ew = new EntityWrapper();
            sharedAssetBatchService.buildDefaultFilters(ew);
            AssetInfo ai = sharedAssetBatchService.findSharedAsset2Lease(ew, this.searchCriteria);
            selectedAssets.add(ai);
        }
        logger.info("findAndSelectAsset");
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }
}
