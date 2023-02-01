package com.ge.apm.view.asset;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AssetHistoryRepository;
import com.ge.apm.domain.AssetHistory;
import com.ge.apm.domain.CustomConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.service.asset.AssetHistoryService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @Getter
    @Setter
    private String oneSearchStr;

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
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(userAccount.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        loadConfigForSite(userAccount.getSiteUID());
//        CustomConfigObject  customConfig = customConfigService.getCurrentCustomConfig(userAccount.getSiteUID());
        List<String> assetsImportantFields = customConfigObject.getAssetsImportantFields();
        logger.info("assetsImportantFields siteuid:{}, size:{},assetsImportantFields:{}",userAccount.getSiteUID(),assetsImportantFields.size(),assetsImportantFields.toString());
        searchFilters.add(new SearchFilter("jsonData", SearchFilter.Operator.CONTAINS, assetsImportantFields));
        if(!StringUtils.isEmpty(oneSearchStr)) {
            searchFilters.add(new SearchFilter("jsonData", SearchFilter.Operator.LIKE, oneSearchStr));
        }

        //convert to json data page
        pageRequest = addDefaultSort(pageRequest);
        Page<AssetHistory> assetHistoryPage = assetHistoryRepository.findBySearchFilter(this.searchFilters, pageRequest);
        List<JSONObject> dataLists = assetHistoryService.parseFieldsByJson(assetHistoryPage.getContent());
        Page<JSONObject> jsonPage = new PageImpl<>(dataLists, pageRequest, assetHistoryPage.getTotalElements());
        searchFilters = new ArrayList<SearchFilter>();
        return jsonPage;
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

}
