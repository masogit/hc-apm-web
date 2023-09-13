package com.ge.apm.dao;

import com.ge.apm.domain.AssetTypeGroup;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Collection;
import java.util.List;

public interface AssetTypeGroupRepository extends GenericRepository<AssetTypeGroup> {

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid ,sag.tenant_uid  \n" +
            "from asset_type_group sag\n" +
            "where parent_uid is null and tenant_uid =?1\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid,sag.tenant_uid \n" +
            "from asset_type_group sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.uid\n" +
            ") select  * from cte_auth_group", nativeQuery = true)
    List<AssetTypeGroup> listAssetTypeGroupInHierarchy(String tenantUid);

    @Query(value = "with recursive cte_child_auth_group as (\n" +
            "    select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid,sag.tenant_uid\n" +
            "    from asset_type_group sag join user_asset_group uag on uag.asset_group_uid = sag.uid\n" +
            "    where uag.user_uid = :userUid \n" +
            "    union all\n" +
            "    select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid,sag.tenant_uid\n" +
            "    from asset_type_group sag\n" +
            "    inner join cte_child_auth_group cte_child\n" +
            "    on sag.parent_uid = cte_child.uid\n" +
            ") \n" +
            "select * from cte_child_auth_group order by encoded\n", nativeQuery = true)
    List<AssetTypeGroup> listAssetTypeGroupInHierarchyByUser(@Param("userUid") String userUid);

    @Query(value = "    select sag.uid,  sag.name, sag.encoded, sag.classification_code, sag.parent_uid,sag.tenant_uid\n" +
            "    from asset_type_group sag join user_asset_group uag on uag.asset_group_uid = sag.uid\n" +
            "    where uag.user_uid = :userUid \n", nativeQuery = true)
    List<AssetTypeGroup> listAssetTypeGroupByUser(@Param("userUid") String userUid);

    AssetTypeGroup findByUid(String uid);
    AssetTypeGroup findByEncodedAndTenantUid(String encoded,String tenantUid);
    List<AssetTypeGroup> findByEncodedInAndTenantUid(Collection<String> codedList, String tenantUid);

    AssetTypeGroup findFirstByName(String name);

    AssetTypeGroup findAuthGroupByNameAndParentUid(String name, String parentUid);
    AssetTypeGroup findAuthGroupByNameAndParentUidAndTenantUid(String name, String parentUid,String tenantUid);


    @Query(value = "with RECURSIVE cte as (select a.parent_uid,cast(a.name as varchar(500)) " +
            "from asset_type_group a where a.uid=?1 " +
            "union all select k.parent_uid,cast(k.name||' ->'||c.name as varchar(500)) as name  " +
            "from cte  c inner join asset_type_group k on c.parent_uid = k.uid)select name from cte where  parent_uid is null", nativeQuery = true)
    String listAssetTypeGroupNamePathInHierarchy(String parentUid);

    @Query(value = "with RECURSIVE cte as (select a.parent_uid,cast(a.name as varchar(500)) " +
            "from asset_type_group a where a.encoded=?1 " +
            "union all select k.parent_uid,cast(k.name||' ->'||c.name as varchar(500)) as name  " +
            "from cte  c inner join asset_type_group k on c.parent_uid = k.uid)select name from cte where  parent_uid is null", nativeQuery = true)
    String flatternHierarchyByEncoded(String encoded);

    @Query(value = "with RECURSIVE cte as (select a.uid, a.encoded, a.parent_uid,cast(a.name as varchar(500)) from asset_type_group a where a.parent_uid " +
            "is null union all select k.uid, k.encoded, k.parent_uid,cast(c.name||' ->'||k.name as varchar(500)) as name " +
            "from asset_type_group k inner join  cte  c on k.parent_uid = c.uid)select  encoded,  name from cte\n", nativeQuery = true)
    List<Object> flatternHierarchy();


    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code, sag.parent_uid,sag.tenant_uid\n" +
            "from asset_type_group sag\n" +
            "where parent_uid=?1\n" +
            "union all\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid,sag.tenant_uid\n" +
            "from asset_type_group sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.uid) select  * from cte_auth_group", nativeQuery = true)
    List<AssetTypeGroup> findChildByParentUid(String uid);

    @Query(value = "with recursive cte_auth_group as (\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code,  sag.parent_uid,sag.tenant_uid\n" +
            "from asset_type_group sag\n" +
            "where uid=?1\n" +
            "union all\n" +
            "select sag.uid,  sag.name, sag.encoded, sag.classification_code, sag.parent_uid,sag.tenant_uid\n" +
            "from asset_type_group sag\n" +
            "inner join cte_auth_group cte on sag.parent_uid = cte.uid) select  * from cte_auth_group", nativeQuery = true)
    List<AssetTypeGroup> findChildrenIncludingCurrent(String uid);

    @Query(value = "select sys.encoded from asset_type_group sys where sys.encoded=?1", nativeQuery = true)
    String findEncodedByUid(String uid);
    @Query(value = "select sys.encoded from asset_type_group sys where sys.encoded=?1 and sys.tenant_uid=?2", nativeQuery = true)
    String findEncodedByUidAndTenantUid(String uid,String tenantUid);
    List<AssetTypeGroup> findByParentUidIn(List<String> parentUid);

    List<AssetTypeGroup> findByUidIn(List<String> uid);

    @Query(value = "select sag.* from asset_type_group sag where sag.parent_uid is null", nativeQuery = true)
    List<AssetTypeGroup> getTopAuthGroup();

    @Query(value = "select * from asset_type_group where parent_uid is null", nativeQuery = true)
    List<AssetTypeGroup> findAllTopNode();

    @Query(value = "select uid from asset_type_group where encoded like concat(?1,'%')", nativeQuery = true)
    List<String> findChildUidsByTopGroup(String encoded);

    @Query(value = "select * from asset_type_group where classification_code in ?1", nativeQuery = true)
    List<AssetTypeGroup> findByClassificationCode(List<String> codedList);

    @Query(value = "select * from asset_type_group where encoded in ?1 and tenant_uid =?2", nativeQuery = true)
    List<AssetTypeGroup> findByClassificationCodeAndTenantUid(List<String> codedList,String tenantUid);

    @Query(value = "select max(id) from asset_type_group", nativeQuery = true)
    Integer getMaxId();

    @Query(value = "with recursive cte_child_auth_group as ( \n" +
            "            select atg.* from asset_type_group atg where atg.encoded = :encoded\n" +
            "            union all \n" +
            "            select atg.* from asset_type_group atg \n" +
            "            inner join cte_child_auth_group cte_child \n" +
            "            on atg.uid = cte_child.parent_uid \n" +
            "        )  \n" +
            "        select uag.user_uid from cte_child_auth_group join user_asset_group uag on cte_child_auth_group.uid = uag.asset_group_uid", nativeQuery = true)
    List<String> getUserUidListByEncoded(@Param("encoded") String encoded);
}