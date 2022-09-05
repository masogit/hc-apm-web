package com.ge.apm.service.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import webapp.framework.dao.SearchFilter;

import java.util.Collection;

/**
 * @author 503310146
 */
public class WrapperSearchFilterUtils {

    public static <T> Wrapper<T> convert(Collection<SearchFilter> searchFilters, Wrapper<T> wrapper, String paramAlias) {
        if (ObjectUtil.isEmpty(searchFilters)) {
            return new EntityWrapper<>();
        }
        for(SearchFilter filter: searchFilters){
            String fieldName = ZipUtil.camelToUnderline(filter.fieldName);
            if (ObjectUtil.isNotEmpty(paramAlias)) {
                fieldName = paramAlias + "." + fieldName;
            }
            switch(filter.operator){
                case EQ:
                    wrapper.eq(fieldName, filter.value);
                    break;
                case LIKE:
                    if(filter.value!=null){
                        wrapper.like("lower("+fieldName+")", filter.value.toString().toLowerCase());
                    }
                    break;
                case LT :
                    wrapper.lt(fieldName, filter.value);
                    break;
                case LTE:
                    wrapper.le(fieldName, filter.value);
                    break;
                case GT:
                    wrapper.gt(fieldName, filter.value);
                    break;
                case GTE:
                    wrapper.ge(fieldName, filter.value);
                    break;
                default:
                    break;
            }
        }
        return wrapper;
    }
}
