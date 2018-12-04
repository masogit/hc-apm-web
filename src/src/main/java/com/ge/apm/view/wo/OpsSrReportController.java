package com.ge.apm.view.wo;

import com.ge.apm.dao.AssetLifecycleEventRepository;
import com.ge.apm.domain.AssetLifecycleEvent;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author 212688574
 */
@ManagedBean
@ViewScoped
public class OpsSrReportController extends GenericCRUDUUIDController<AssetLifecycleEvent> implements Serializable {

    private AssetLifecycleEventRepository assetLifecycleEventRepository;

    private static final List<Integer> EVENT_TYPE_LIST = Arrays.asList(3, 4, 5);

    protected void init() {
        assetLifecycleEventRepository = WebUtil.getBean(AssetLifecycleEventRepository.class);
    }

    @Override
    protected GenericRepositoryUUID<AssetLifecycleEvent> getDAO() {
        return assetLifecycleEventRepository;
    }

    @Override
    protected Page<AssetLifecycleEvent> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        this.selected = null;
        searchFilters.add(new SearchFilter("eventType", SearchFilter.Operator.IN, EVENT_TYPE_LIST));
        searchFilters.add(new SearchFilter("status", SearchFilter.Operator.EQ, 2));
        searchFilters.add(new SearchFilter("reports", SearchFilter.Operator.IsEmpty, null));
        return assetLifecycleEventRepository.findBySearchFilter(searchFilters, pageRequest);
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

    public AssetLifecycleEvent getServiceRequest(String srId) {
        return assetLifecycleEventRepository.findById(srId);
    }
}