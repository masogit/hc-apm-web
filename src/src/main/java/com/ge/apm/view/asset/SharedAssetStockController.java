package com.ge.apm.view.asset;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.service.sharedAsset.SharedAssetStockService;
import com.ge.apm.service.utils.UrlParamUtil;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.MyCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
public class SharedAssetStockController extends MyCRUDController<AssetInfo> {

    private static final Logger LOG = LoggerFactory.getLogger(SharedAssetStockController.class);

    @Getter
    @Setter
    private String searchCriteria;

    @Getter
    @Setter
    private int queryType = -1;

    private SharedAssetStockService sharedAssetStockService;

    @Override
    protected BaseMapper<AssetInfo> getDAO() {
        return null;
    }

    protected void init() {
        sharedAssetStockService = WebUtil.getBean(SharedAssetStockService.class);
    }

    protected List<AssetInfo> loadData(Pagination pageRequest, String sortField, boolean isAscending) {
        Wrapper ew = new EntityWrapper();
        sharedAssetStockService.buildDefaultFilters(ew);
        sharedAssetStockService.addFiltersByQueryType(ew, this.queryType);
        ew.andNew().isNotNull("shared_asset_type");

        wrapSearchFilter(ew);

        lazyModel.setRowCount(sharedAssetStockService.getTotalRowCount(ew));

        ew.orderBy(sortField, isAscending);
        List<AssetInfo> assetList = sharedAssetStockService.loadAllSharedAssets(pageRequest, ew);

        return assetList;
    }

    public void onSelectAsset() throws IOException {
        String url = "actionName=View&selectedid="
            + selected.getId().toString()
            + "&asset_name=" + selected.getName()
            + "&terminate=" + false;
        url = "AssetDetail.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
    }

}
