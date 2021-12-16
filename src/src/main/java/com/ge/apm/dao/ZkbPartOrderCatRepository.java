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

    @Query("select c from ZkbPartOrderCat c where c.isVaild <> ?1 ")
    List<ZkbPartOrderCat> queryByIsVaild(String isVaild);

    @Query("select c from ZkbPartOrderCat c where c.startDate <=?1 and c.endDate >= ?2  ")
    List<ZkbPartOrderCat> queryByDate(Date startDate,Date endDate);


    /**
     *
     * @param typeList 类型
     * @param isValid b
     * @return
     */
    List<ZkbPartOrderCat> getByValueTypeInAndIsVaildIsNot(List<String> typeList,String isValid);

    List<ZkbPartOrderCat> findByValueType(String valueType);

    @Query(value = "select cat.cat_id from (select zpo.cat_id from zkb_part_order zpo union all select zfpo.cat_id from zkb_fe_part_order zfpo) cat where exists(select 1 from zkb_part_order_cat zpoc where cat.cat_id = ?1)",nativeQuery = true)
    public List<String> queryInUsedIds(String id);

}
