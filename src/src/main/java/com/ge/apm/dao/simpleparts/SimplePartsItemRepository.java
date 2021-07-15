package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SimplePartsItemRepository extends GenericRepositoryUUID<SimplePartsItem>, JpaRepository<SimplePartsItem, String> {

    SimplePartsItem findById(String id);

//    Optional<SimplePartsItem> findByName(String name);

    @Query(value = "select * from simple_parts_item where category_code = ?1 and tenant_uid = ?2", nativeQuery = true)
    List<SimplePartsItem> findByCategoryCode(String encoded, String tenantUid);

    @Query(value = "select * from simple_parts_item where tenant_uid = ?1", nativeQuery = true)
    List<SimplePartsItem> findByTenantUid(String tenantUID);

    @Modifying
    @Query(value = "UPDATE simple_parts_item SET category_name = ?2,category_code = ?3 WHERE id IN ?1", nativeQuery = true)
    void updatePartsItemPartsCategoryByIds(Set<String> partsItemIds,String categoryName,String categoryCode);
}
