package com.ge.apm.dao;

import com.ge.apm.domain.ZkbPartOrderCat;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.Date;
import java.util.List;

/**
 * @author : 503189362
 * @date : 2020/6/23
 * @description :
 */
public interface ZkbPartOrderCatRepository extends GenericRepositoryUUID<ZkbPartOrderCat> {

    List<ZkbPartOrderCat> findByValueTypeIsNot(String valueType);
    List<ZkbPartOrderCat> findByIsVaildIsNullOrIsVaild(String isVaild);

    @Query("select c from ZkbPartOrderCat c where c.startDate <=?1 and c.endDate >= ?2  ")
    List<ZkbPartOrderCat> queryByDate(Date startDate,Date endDate);

    /**
     * 根据开始时间 结束时间查询
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param listStr 类型集合
     * @return
     */
    List<ZkbPartOrderCat> getByStartDateLessThanEqualAndEndDateGreaterThanEqualAndValueTypeIn(Date startDate,Date endDate,List<String> listStr);

    List<ZkbPartOrderCat> findByValueType(String valueType);

}
