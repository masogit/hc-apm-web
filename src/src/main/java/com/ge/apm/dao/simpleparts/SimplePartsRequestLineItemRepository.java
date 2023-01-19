package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsRequestLineItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;

import java.util.List;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/16
 * @descript 备件明细Repository
 * @package com.ge.apm.dao
 */
public interface SimplePartsRequestLineItemRepository extends GenericRepositoryUUID<SimplePartsRequestLineItem>, JpaRepository<SimplePartsRequestLineItem, String> {

    /**
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequestLineItem>
     * @params [requestCode]
     * @author csh
     * @createTime 2020/6/16 9:24
     * @description 根据申购单的编号查询
     */
    @Select(value = "select item.* from simple_parts_request_line_item item, simple_parts_request request where request.id = item.parts_request_id and request.request_code = #{requestCode}")
    List<SimplePartsRequestLineItem> findPartsRequestLineItemsByRequestCode(@Param("requestCode") String requestCode);

    @Override
    List<SimplePartsRequestLineItem> findBySearchFilter(final List<SearchFilter> filterFilter);

    @Override
    List<SimplePartsRequestLineItem> findBySearchFilter(final List<SearchFilter> filterFilter, Sort sort);

    /**
     * @param requestCode
     * @return java.util.List<com.ge.apm.domain.simpleparts.PartsRequestLineItem>
     * @date 2020/7/31 13:23
     * 根据编号查询
     */
    @Query(value = "select * from simple_parts_request_line_item where request_code = ?1", nativeQuery = true)
    List<SimplePartsRequestLineItem> findByRequestCode(String requestCode);

    void deleteByPartsRequestId(String requestId);
}
