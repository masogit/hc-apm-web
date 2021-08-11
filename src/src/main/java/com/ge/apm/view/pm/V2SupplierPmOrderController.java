/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;

import com.ge.apm.domain.V2_PmOrder;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * v
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2SupplierPmOrderController extends V2PmOrderController {


    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        super.filterOutPm(searchFilters);
        String currentStepId = getCurrentStepId(super.getQueryIndex());
        searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, currentStepId));
        searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));

        Sort sort = returnSort(pageRequest);
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        result = super.getDAO().findBySearchFilter(this.searchFilters, pageRequest);
        this.searchFilters.clear();
        List<V2_PmOrder> currentPagePmOrders = result.getContent();
        Map<String, Boolean> selectedMap = super.getSelectedMap();
        boolean isSelectedCurrentPage = currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        super.setIsSelectedCurrentPage(isSelectedCurrentPage);
        int queryIndex = super.getQueryIndex();
        if (queryIndex == 20 || queryIndex == 8) {
            pmIdList = new ArrayList<>();
        }
        return result;
    }

    private String getCurrentStepId(Integer intValue) {
        switch (intValue) {
            case 3:
                //待接单
                return "Accept";
            case 0:
                //已计划
                return "Maintain";
        }
        throw new BusinessException("未定义状态值:" + intValue);
    }

    public void saveSupplierPM() {
        PmOrderActionForm form = new PmOrderActionForm();
        form.setCreateType(2);
        super.savePM(form);
    }


    @Override
    protected void init() {
        super.init();
        String query = WebUtil.getRequestParameter("query");
        super.setQueryIndex(StringUtils.isEmpty(query) ? 3 : Integer.parseInt(query));
    }

    @Override
    public void setSearchFilter() {
        this.searchFilters = this.searchFilters == null ? new ArrayList() : this.searchFilters;
        removeFilterOnField("currentStepId");
        removeFilterOnField("currentPersonId");
        removeFilterOnField("isFinished");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("ownerId");
        removeFilterOnField("approvalStatus");
        removeFilterOnField("assetInfo.assetOwnerId");
        removeFilterOnField("assetInfo.assetOwnerId2");
        removeFilterOnField("assetInfo.assetOwnerId3");

    }
}
