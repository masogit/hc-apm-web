package com.ge.apm.view.adverseEvent;

import com.ge.apm.dao.AdverseEventsRepository;
import com.ge.apm.domain.AdverseEvents;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.view.asset.AssetInfoDetailController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;


@ManagedBean
@ViewScoped
public class AssetAdverseEventsController  extends GenericCRUDController<AdverseEvents> {


    private AdverseEventsRepository adverseEventsRepository;


    private AssetInfoDetailController assetInfoDetailController;
    @Override
    protected GenericRepository<AdverseEvents> getDAO() {
        return adverseEventsRepository;
    }

    @Override
    protected void init() {
        assetInfoDetailController =  WebUtil.getBean(AssetInfoDetailController.class);
        adverseEventsRepository =  WebUtil.getBean(AdverseEventsRepository.class);
    }


    @Override
    public Page<AdverseEvents> loadData(PageRequest pageRequest) {
        if(assetInfoDetailController.getSelected()!=null){
            AssetInfo assetInfo = assetInfoDetailController.getSelected();
            List<SearchFilter>  searchFilter=new ArrayList<>();
//        AssetInfo assetInfo = assetInfoRepository.getByUid(assetUid);
            searchFilter.add(new SearchFilter("deviceNum", SearchFilter.Operator.EQ,assetInfo.getDepartNum())); //设备编号
            Sort sort = new Sort(Sort.Direction.DESC, "reportDate");
            PageRequest  pageRequest1= new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(),sort);
            Page<AdverseEvents> page = adverseEventsRepository.findBySearchFilter(searchFilter, pageRequest1);
            return page;
        }
        return null;
    }

}
