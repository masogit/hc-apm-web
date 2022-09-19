package com.ge.apm.service.sharedasset;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.SharedAssetReqDetailMapper;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.service.sharedasset.valuation.ValuationFactory;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SharedAssetReqDetailService {

    protected static final Logger logger = Logger.getLogger(SharedAssetReqDetailService.class);
    @Autowired
    private SharedAssetReqDetailMapper sharedAssetReqDetailMapper;


    public List<SharedAssetReqDetail> loadSharedAssets(PageRequest page,  Wrapper wrapper){

        System.out.println(wrapper.getSqlSegment());
        List<SharedAssetReqDetail> sharedAssets=sharedAssetReqDetailMapper.loadSharedAssets(new Pagination(page.getPageNumber(), page.getPageSize()), wrapper);
        sharedAssets.forEach(item->{
            ValuationFactory.valuation(null,item);
        });
        return sharedAssets;
    }
    public int getTotalRowCount(@Param("ew") Wrapper<AssetInfo> wrapper){

        return 0;
    }

    public Wrapper buildDefaultFilters(List<SearchFilter> searchFilters) {
        Wrapper ew = new EntityWrapper();
        Map<String, SearchFilter> collect = searchFilters.stream().collect(Collectors.toMap((item) -> item.fieldName, (item) -> item));
        SearchFilter clinicalDeptUid = collect.get("clinicalDeptUid");
        if("1*2+".equals(clinicalDeptUid.conditionPath)){
            List<String> orgUidList =(List) clinicalDeptUid.value;
            ew.andNew()
                    .in("tenant_uid", orgUidList)
                    .or().in("institution_uid", orgUidList)
                    .or().in("hospital_uid", orgUidList)
                    .or().in("site_uid", orgUidList)
                    .or().in("clinical_dept_uid", orgUidList);
            collect.remove("clinicalDeptUid");
        }else{
            List<String> orgUidList =(List) collect.get("tenantUID");
            ew.andNew()
                    .in("tenant_uid", orgUidList)
                    .or().in("institution_uid", orgUidList)
                    .or().in("hospital_uid", orgUidList)
                    .or().in("site_uid", orgUidList);
        }
        collect.remove("tenantUID");
        collect.remove("institutionUID");
        collect.remove("hospitalUID");
        collect.remove("siteUID");
        for (SearchFilter filter : collect.values()) {
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
            }
        }
        return ew;
    }
}
