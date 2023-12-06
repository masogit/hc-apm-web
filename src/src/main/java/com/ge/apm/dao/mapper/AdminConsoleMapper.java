package com.ge.apm.dao.mapper;

import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.RelRuleAsset;
import com.ge.apm.domain.V2_PmOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

public interface AdminConsoleMapper {
    @Select("select * from pm_order_rule")
    List<PmOrderRule> findAllPmOrderRule();

    @Select("select * from v2_pm_order limit #{limit} offset #{offset}")
    List<V2_PmOrder> limitPmOrder(@Param("limit") int limit, @Param("offset") int offset);

    @Select({
            "<script>" +
                    "select * from rel_rule_asset where asset_uid in " +
                    "<foreach item = 'item' index = 'index' collection = 'assetUidList' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
                    "</script>"})
    List<RelRuleAsset> findRelRuleAssetByAssetUidList(@Param("assetUidList") Collection<String> assetUidList);

    @Update("update v2_pm_order set pm_rule_uid = #{pmRuleUid}, rule_name = #{ruleName}, period = #{period}, period_unit = #{periodUnit} where id = #{id}")
    void updatePmOrder(V2_PmOrder pmOrder);

    @Update({
            "<script>" +
                    "update v2_pm_order set pm_rule_uid = #{pmOrderRule.uid}, rule_name = #{pmOrderRule.name}, period = #{pmOrderRule.period}, period_unit = #{pmOrderRule.periodUnit} where id in " +
                    "<foreach item = 'item' index = 'index' collection = 'orderIds' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
                    "</script>"})
    void updatePmOrderByIds(@Param("pmOrderRule") PmOrderRule pmOrderRule, @Param("orderIds") Collection<String> orderIds);
}
