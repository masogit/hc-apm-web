package com.ge.apm.service.utils;

import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import webapp.framework.dao.SearchFilter;

import java.util.Collection;

/**
 * @author 503310146
 */
public class WrapperSearchFilterUtils {

    private WrapperSearchFilterUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> Wrapper<T> convert(Collection<SearchFilter> searchFilters, Wrapper<T> wrapper, String paramAlias) {
        if (ObjectUtil.isEmpty(searchFilters)) {
            return new EntityWrapper<>();
        }
        for (SearchFilter filter : searchFilters) {
            String fieldName = ZipUtil.camelToUnderline(filter.fieldName);
            if (ObjectUtil.isNotEmpty(paramAlias)) {
                fieldName = paramAlias + "." + fieldName;
            }
            Object filterValue = filter.value;
            switch (filter.operator) {
                case EQ:
                    wrapper.eq(fieldName, filterValue);
                    break;
                case LIKE:
                    if (filterValue != null) {
                        wrapper.like("lower(" + fieldName + ")", filterValue.toString().toLowerCase());
                    }
                    break;
                case LT:
                    wrapper.lt(fieldName, filterValue);
                    break;
                case LTE:
                    wrapper.le(fieldName, filterValue);
                    break;
                case GT:
                    wrapper.gt(fieldName, filterValue);
                    break;
                case GTE:
                    wrapper.ge(fieldName, filterValue);
                    break;
                case IsNull:
                    wrapper.isNull(fieldName);
                    break;
                case IsNotNull:
                    wrapper.isNotNull(fieldName);
                    break;
                case IN:
                    wrapper.in(fieldName, (Collection) filterValue);
                    break;
                case NotIN:
                    wrapper.notIn(fieldName, (Collection) filterValue);
                    break;
                default:
                    break;
            }
        }
        return wrapper;
    }



    /*
     *     fieldName不需要转换，直接在页面上写成sql中的字段
     */
    public static <T> Wrapper<T> convert(Collection<SearchFilter> searchFilters, Wrapper<T> wrapper) {
        if (ObjectUtil.isEmpty(searchFilters)) {
            return new EntityWrapper<>();
        }
        for (SearchFilter filter : searchFilters) {
            String fieldName = filter.fieldName;
            Object filterValue = filter.value;
            switch (filter.operator) {
                case EQ:
                    wrapper.eq(fieldName, filterValue);
                    break;
                case LIKE:
                    if (filterValue != null) {
                        wrapper.like("lower(" + fieldName + ")", filterValue.toString().toLowerCase());
                    }
                    break;
                case LT:
                    wrapper.lt(fieldName, filterValue);
                    break;
                case LTE:
                    wrapper.le(fieldName, filterValue);
                    break;
                case GT:
                    wrapper.gt(fieldName, filterValue);
                    break;
                case GTE:
                    wrapper.ge(fieldName, filterValue);
                    break;
                case IsNull:
                    wrapper.isNull(fieldName);
                    break;
                case IsNotNull:
                    wrapper.isNotNull(fieldName);
                    break;
                case IN:
                    wrapper.in(fieldName, (Collection) filterValue);
                    break;
                case NotIN:
                    wrapper.notIn(fieldName, (Collection) filterValue);
                    break;
                default:
                    break;
            }
        }
        return wrapper;
    }
}
