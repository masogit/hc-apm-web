package com.ge.apm.view.fealert;

import com.ge.apm.dao.AssetAlertConfigRepository;
import com.ge.apm.domain.AssetAlertConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class FeAlertReportController extends GenericCRUDUUIDController<AssetAlertConfig> implements Serializable {

    private AssetAlertConfigRepository repository;

    protected void init() {
        repository = WebUtil.getBean(AssetAlertConfigRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<AssetAlertConfig> getDAO() {
        return repository;
    }

    @Override
    protected Page<AssetAlertConfig> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        searchFilters.add(new SearchFilter("isDefault", SearchFilter.Operator.EQ, false));
        return repository.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
    }

    public void searchList(Integer buttonIndex) {
        cancel();
    }

    public void cancel() {
        selected = null;
    }
}