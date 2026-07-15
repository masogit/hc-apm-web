package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.remote.ElasticService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.BatchFileController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@SuppressWarnings("all")
public class AssetHistoryController extends GenericCRUDUUIDController<JSONObject> {

    private static final Logger logger = LoggerFactory.getLogger(AssetHistoryController.class);


    private static final long serialVersionUID = -7414444826511361016L;
    private static final String FIELD_CREATED_DATE = "createdDate";

    private AssetHistoryService assetHistoryService;
    private AssetHistoryRepository assetHistoryRepository;
    private AssetInfoEditController assetInfoEditController;
    private UaaService uuaService;
    private CustomConfigService customConfigService;
    private CustomConfig customConfig;
    private CustomConfigObject customConfigObject;
    private ElasticService elasticService;

    private UserAccount userAccount;
    @Getter @Setter
    private String tips;

    @Getter
    @Setter
    private String oneSearchStr;

    public void doSearch() {
        List<SearchFilter> filters = buildSearchFilters();
        Page<AssetHistory> page = elasticService.searchAssetHistory(filters, UserContextService.getAccessToken(), 0, 1);
        if (page != null && page.getTotalElements() > 10000) {
            tips = "当前数据显示前10000行，如需查看全部数据请下载查看，谢谢。";
        } else {
            tips = null;
        }
    }

    @Override
    protected GenericRepositoryUUID<JSONObject> getDAO() {
        return null;
    }

    @Override
    protected void init() {
        assetHistoryService = WebUtil.getBean(AssetHistoryService.class);
        assetHistoryRepository = WebUtil.getBean(AssetHistoryRepository.class);
        uuaService = WebUtil.getBean(UaaService.class);
        customConfigService = WebUtil.getBean(CustomConfigService.class);
        elasticService=WebUtil.getBean(ElasticService.class);
        userAccount = UserContextService.getCurrentUserAccount();
        loadConfigForSite(userAccount.getSiteUID());
        doSearch();
    }


    private void loadConfigForSite(String siteUid) {
        customConfig = customConfigService.getCustomConfigBySite(siteUid);
        if (null == customConfig) {
            customConfigObject = customConfigService.getCurrentCustomConfig(siteUid);
        } else {
            customConfigObject = JSONObject.parseObject(customConfig.getConfig(), CustomConfigObject.class);
        }
    }

    @Override
    protected Page<JSONObject> loadData(PageRequest pageRequest) {
        searchFilters = buildSearchFilters();
        pageRequest = addDefaultSort(pageRequest);
        Page<AssetHistory> assetHistoryPage = elasticService.searchAssetHistory(searchFilters, UserContextService.getAccessToken(), pageRequest.getPageNumber(), pageRequest.getPageSize());
        if(assetHistoryPage==null || assetHistoryPage.getContent()==null){
            tips = null;
            return new PageImpl<>(new ArrayList<>(), pageRequest, 0);
        }
        if (assetHistoryPage.getTotalElements() > 10000){
            tips = "当前数据显示前10000行，如需查看全部数据请下载查看，谢谢。";
            assetHistoryPage = new PageImpl<>(assetHistoryPage.getContent(), pageRequest, 10000);
        } else {
            tips = null;
        }
        List<JSONObject> dataLists = assetHistoryService.parseFieldsByJson(assetHistoryPage.getContent());
        return new PageImpl<>(dataLists, pageRequest, assetHistoryPage.getTotalElements());
    }

    /**
     * 构建搜索过滤条件：保留已有的列筛选条件，重新构建基础过滤条件（避免重复）
     */
    private List<SearchFilter> buildSearchFilters() {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        List<SearchFilter> filters = new ArrayList<>(searchFilters);

        List<String> orgUidList = uuaService.getOrgListByUser(userAccount.getUid())
                .stream().map(item -> item.getUid()).collect(Collectors.toList());

        // 移除旧的动态过滤条件，防止重复
        filters.removeIf(f -> "tenant_uid".equals(f.fieldName)
                || "institution_uid".equals(f.fieldName)
                || "hospital_uid".equals(f.fieldName)
                || "site_uid".equals(f.fieldName)
                || "has_columns".equals(f.fieldName)
                || "asset_name".equals(f.fieldName));

        // 重新构建基础过滤条件
        filters.add(new SearchFilter("tenant_uid", SearchFilter.Operator.EQ, userAccount.getTenantUID(), "1*"));

        boolean hasSiteUidFilter = filters.stream().anyMatch(f -> "site_uid".equals(f.fieldName));
        if (!hasSiteUidFilter) {
            filters.add(new SearchFilter("tenant_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("institution_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("hospital_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filters.add(new SearchFilter("site_uid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }

        List<String> assetsImportantFields = customConfigObject.getAssetsImportantFields();
        filters.add(new SearchFilter("has_columns", SearchFilter.Operator.CONTAINS, assetsImportantFields));

        if (!StringUtils.isEmpty(oneSearchStr)) {
            filters.add(new SearchFilter("asset_name", SearchFilter.Operator.LIKE, oneSearchStr));
        }

        return filters;
    }

    private PageRequest addDefaultSort(PageRequest pageRequest) {
        Iterator<Sort.Order> orders = pageRequest.getSort().iterator();
        boolean exists = false;
        while(orders.hasNext()){
            if(orders.next().getProperty().equals(FIELD_CREATED_DATE)){
                exists = true;
                break;
            }
        }
        if (exists) {
            return pageRequest;
        } else {
            Sort defaultSort = new Sort(Sort.Direction.DESC,FIELD_CREATED_DATE);
            pageRequest= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), pageRequest.getSort().and(defaultSort));
        }
        return pageRequest;
    }

    public void onFilterByAssetHistory(FilterEvent event) {
        onFilterCustom(event, this::filters);
    }

    private boolean filters(String fieldName, String filterVal) {
        if ("createdDate".equals(fieldName)) {
            processTimeFiled(filterVal ,fieldName);
            return true;
        }
        return false;
    }
    private StreamedContent exportFile;


    public void exportData() {
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/info/APM_Asset_Modify_History_List.xlsx");
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            List<AssetHistory> results = elasticService.queryAllBySearchAfter(searchFilters, 10000, UserContextService.getAccessToken());
            if(CollectionUtils.isNotEmpty(results)){
                return;
            }
            List<JSONObject> dataLists = assetHistoryService.parseFieldsByJson(results);

            Boolean exportFlag = assetHistoryService.exportData(doc, assetHistoryService.createFileScanData(dataLists));
            if (exportFlag) {
                exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", "ImportFiles.xlsx");
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(BatchFileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }
}
