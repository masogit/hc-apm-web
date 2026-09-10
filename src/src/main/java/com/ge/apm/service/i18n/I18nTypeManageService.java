package com.ge.apm.service.i18n;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.dao.mapper.I18nTypeManageMapper;
import com.ge.apm.domain.AssetClassificationModel;
import com.ge.apm.domain.I18nTypeManage;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.utils.MicroServiceInvoker;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import webapp.framework.dao.SearchFilter;

import java.util.*;

@Service
public class I18nTypeManageService extends MicroServiceInvoker {

    protected static final Logger logger = Logger.getLogger(I18nTypeManageService.class);

    @Autowired
    private I18nTypeManageMapper i18nTypeManageMapper;


    public List<I18nTypeManage> loadI18nTypes(PageRequest page, Wrapper<I18nTypeManage> wrapper,String tenantUid){

        setSort(page,wrapper);
        logger.info("sql:"+wrapper.getSqlSegment());
        List<I18nTypeManage> sharedAssets=i18nTypeManageMapper.loadSharedAssets(new Pagination(page.getPageNumber()+1, page.getPageSize()), wrapper,tenantUid);
        return sharedAssets;
    }

    private void setSort(PageRequest page, Wrapper<I18nTypeManage> wrapper) {
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

    public int getTotalRowCount(@Param("ew") Wrapper<I18nTypeManage> wrapper,String tenantUid){
        return  i18nTypeManageMapper.getTotalRowCount(wrapper,tenantUid);
    }

    public Wrapper<I18nTypeManage> buildDefaultFilters(List<SearchFilter> searchFilters) {
        Wrapper<I18nTypeManage> ew = new EntityWrapper<I18nTypeManage>();
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

    public Map<String, Object> updateI18nRedis(Integer siteId) {
        HttpHeaders header = createHttpHeaders(UserContextService.getAccessToken());
        HttpEntity<AssetClassificationModel> requestEntity = new HttpEntity<>(header);
        String gatewayURL = getGatewayURL(GatewayURL.UPDATE_I18N_FOR_REDIS);
        String url = gatewayURL.concat("?siteId="+siteId);
        ResponseEntity<LinkedHashMap> response = template.exchange(url, HttpMethod.GET, requestEntity, LinkedHashMap.class);
        if (isOkay(response)) {
            LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response.getBody().get("data");
            logger.info("Message send " + (isOkay(response) ? "successfully." : "failed."));
            return responseMap;
        } else {
            return null;
        }
    }
}
