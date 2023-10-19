package com.ge.apm.dao.mapper;

import com.ge.apm.domain.ZkbIbCrossUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author : 503189362
 * @date : 2021/4/23
 * @description :
 */
public interface ZkbIbCrossMapper {



    @Select("select ua.id as user_id,ua.*, case when pp.co > 0 then '2' else '1' end as user_type from user_account ua " +
            "inner join user_role r on ua.id = r.user_id inner join tenant_info t on t.uid = ua.tenant_uid " +
            "left join (select pp.login_name,count(*) as co from zkb_contact_person pp where pp.tenant_uid = #{tenantUid} and (pp.login_name !='' or pp.login_name is not null) and pp.position in " +
            " ('AfterSales','AfterSalesManager','AssetSalesManager','AssetSales') group by login_name ) pp on pp.login_name = ua.login_name " +
            " where ua.tenant_uid = #{tenantUid} and t.type = 3 and r.role_id=3 and (ua.is_deleted is null or ua.is_deleted =false)")
    List<ZkbIbCrossUser> findUsersByTenantUid(String tenantUid);

    @Select("select ua.id as user_id,ua.*, case when pp.co > 0 then '2' else '1' end as user_type from user_account ua " +
            "inner join user_role r on ua.id = r.user_id inner join tenant_info t on t.uid = ua.tenant_uid " +
            "left join (select pp.login_name,count(*) as co from zkb_contact_person pp where pp.login_name = #{loginName} and pp.position in " +
            " ('AfterSales','AfterSalesManager','AssetSalesManager','AssetSales') group by login_name ) pp on pp.login_name = ua.login_name " +
            " where ua.login_name = #{loginName} and t.type = 3 and r.role_id=3 and (ua.is_deleted is null or ua.is_deleted =false)")
    List<ZkbIbCrossUser> findUsersByLoginName(String loginName);

    @Select({"<script>" +
            "select ua.id as user_id,ua.*, case when pp.co > 0 then '2' else '1' end as user_type from user_account ua " +
            "inner join user_role r on ua.id = r.user_id inner join tenant_info t on t.uid = ua.tenant_uid " +
                    "left join (select pp.login_name,count(*) as co from zkb_contact_person pp where pp.tenant_uid in" +
            "<foreach item = 'item' index = 'index' collection = 'tenantUids' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            " and (pp.login_name !='' or pp.login_name is not null) and pp.position in " +
                    " ('AfterSales','AfterSalesManager','AssetSalesManager','AssetSales') group by login_name ) pp on pp.login_name = ua.login_name " +
                    " where ua.tenant_uid in " +
            "<foreach item = 'item' index = 'index' collection = 'tenantUids' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            " and t.type = 3 and r.role_id=3 and (ua.is_deleted is null or ua.is_deleted =false) " +
    "</script>"})
    List<ZkbIbCrossUser> findUsersByTenantSet(@Param("tenantUids") Set<String> tenantUids);

}
