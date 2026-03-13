package com.ge.apm.service.sharedasset;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetReqTypeRepository;
import com.ge.apm.dao.SharedAssetRequestRepository;
import com.ge.apm.dao.mapper.SharedAssetReqDetailMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetReqType;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.service.sharedasset.valuation.ValuationFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class SharedAssetReqDetailService {

    protected static final Logger logger = Logger.getLogger(SharedAssetReqDetailService.class);
    @Autowired
    private SharedAssetReqDetailMapper sharedAssetReqDetailMapper;


    public List<SharedAssetReqDetail> loadSharedAssets(PageRequest page,  Wrapper<SharedAssetReqDetail> wrapper){

        setSort(page,wrapper);
        logger.info("sql:"+wrapper.getSqlSegment());
        List<SharedAssetReqDetail> sharedAssets=sharedAssetReqDetailMapper.loadSharedAssets(new Pagination(page.getPageNumber()+1, page.getPageSize()), wrapper);
        sharedAssets.forEach(item->{
            ValuationFactory.valuation(null,item);
        });
        return sharedAssets;
    }

    private void setSort(PageRequest page, Wrapper<SharedAssetReqDetail> wrapper) {
        page.getSort();
        Iterator<Sort.Order> iterator = page.getSort().iterator();
        while (iterator.hasNext()){
            Sort.Order next = iterator.next();
            String property = next.getProperty();
            if(Sort.Direction.ASC.equals(next.getDirection())){
                wrapper.orderBy(true,property,true);
            }else{
                wrapper.orderBy(true,property,false);
            }
        }

    }

    public int getTotalRowCount(@Param("ew") Wrapper<SharedAssetReqDetail> wrapper){
        return  sharedAssetReqDetailMapper.getTotalRowCount(wrapper);
    }

    public Wrapper<SharedAssetReqDetail> buildDefaultFilters(List<SearchFilter> searchFilters) {
        Wrapper<SharedAssetReqDetail> ew = new EntityWrapper<SharedAssetReqDetail>();
        for (SearchFilter filter : searchFilters) {
                switch(filter.operator){
                    case EQ:
                        ew.eq(filter.fieldName, filter.value);
                        break;
                    case LIKE:
                        if(filter.value!=null){
                            ew.like("lower("+filter.fieldName+")", filter.value.toString().toLowerCase());
                        }
                        break;
                    case LT :
                        ew.lt(filter.fieldName, filter.value);
                        break;
                    case LTE:
                        ew.le(filter.fieldName, filter.value);
                        break;
                    case GT:
                        ew.gt(filter.fieldName, filter.value);
                        break;
                    case GTE:
                        ew.ge(filter.fieldName, filter.value);
                        break;
                    case IsNull:
                        ew.isNull(filter.fieldName);
                        break;
                    case IsNotNull:
                        ew.isNotNull(filter.fieldName);
                        break;
                    case IN:
                        ew.in(filter.fieldName, (Collection) filter.value);
                    default:
                        break;
            }
        }
        return ew;
    }

    @Transactional
    public void deleteRecord(SharedAssetReqDetail detail, SharedAssetReqDetailRepository dao, AssetInfoRepository asiDao, SharedAssetRequestRepository sharedDao, SharedAssetReqTypeRepository apdDao){
        List<SearchFilter> searchFilterList = new ArrayList<>();
        String assetInfoId = detail.getAssetInfoId();
        AssetInfo assetInfo = asiDao.findByUid(assetInfoId);
        List<String> ids = new ArrayList<>();
        String borrowId = detail.getBorrowId();
        ids.add(borrowId);
        String returnId = detail.getReturnId();
        if(StringUtils.isNotEmpty(returnId)){
            ids.add(returnId);
        }
        searchFilterList.add(new SearchFilter("id", SearchFilter.Operator.IN,ids));
        List<SharedAssetRequest> sharedAssetRequests = sharedDao.findBySearchFilter(searchFilterList);
        searchFilterList.clear();
        searchFilterList.add(new SearchFilter("sharedId", SearchFilter.Operator.IN,ids));
        List<SharedAssetReqType> sharedAssetReqTypes = apdDao.findBySearchFilter(searchFilterList);

        dao.delete(detail);
        searchFilterList.clear();
        searchFilterList.add(new SearchFilter("borrowId", SearchFilter.Operator.IN,ids));
        List<SharedAssetReqDetail> sharedAssetReqDetails = dao.findBySearchFilter(searchFilterList);
        if(CollectionUtils.isEmpty(sharedAssetReqDetails)){
            apdDao.delete(sharedAssetReqTypes);
            sharedDao.delete(sharedAssetRequests);
        }
        String clinicalDeptUID = assetInfo.getClinicalDeptUID();
        String assetDeptUID = assetInfo.getAssetDeptUID();
        if(!clinicalDeptUID.equals(assetDeptUID)){
            assetInfo.setAssetDeptUID(null);
            assetInfo.setAssetDeptId(null);
            assetInfo.setCleaned(Boolean.TRUE);
            asiDao.save(assetInfo);
        }

    }
}
