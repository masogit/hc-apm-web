package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SimplePartsCategoryRepository extends JpaRepository<SimplePartsCategory, String> {

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.id, sag.name, sag.encoded, sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "where parent_uid is null and sag.tenant_uid = ?1\n" +
            "\n" +
            "union\n" +
            "\n" +
            "select sag.id, sag.name, sag.encoded, sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.id and sag.tenant_uid = ?1\n" +
            ") select  * from cte_auth_group", nativeQuery = true)
    List<SimplePartsCategory> listPartsCategoryInHierarchy(String tenantUid);

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.id, sag.name, sag.encoded,  sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "where parent_uid is null\n" +
            "\n" +
            "union\n" +
            "\n" +
            "select sag.id, sag.name, sag.encoded, sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.id\n" +
            ") select  * from cte_auth_group", nativeQuery = true)
    List<SimplePartsCategory> listPartsCategoryInHierarchy();

    SimplePartsCategory findById(String id);

    SimplePartsCategory findPartsCategoryByNameAndParentUidAndTenantUid(String name, String parentUid, String tenantUid);

    @Query(value = "with RECURSIVE cte as (select a.parent_uid,cast(a.name as varchar(500)) " +
            "from simple_parts_category a where a.id=?1 " +
            "union all select k.parent_uid,cast(k.name||' ->'||c.name as varchar(500)) as name  " +
            "from cte  c inner join simple_parts_category k on c.parent_uid = k.id)select name from cte where  parent_uid is null", nativeQuery = true)
    String listPartsCategoryPathInHierarchy(String parentUid);

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.id, sag.name, sag.encoded, sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "where parent_uid=?1\n" +
            "union\n" +
            "select sag.id, sag.name, sag.encoded, sag.parent_uid, sag.tenant_uid\n" +
            "from simple_parts_category sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.id) select  * from cte_auth_group", nativeQuery = true)
    List<SimplePartsCategory> findChildByParentUid(String id);

    @Query(value = "select sys.encoded from simple_parts_category sys where sys.encoded=?1 and sys.tenant_uid = ?2", nativeQuery = true)
    String findEncodedById(String id, String tenantUid);

    @Query(value = "select * from simple_parts_category cat where cat.encoded=?1 and cat.tenant_uid=?2", nativeQuery = true)
    SimplePartsCategory findByEncoded(String encoded, String tenantUid);
}
