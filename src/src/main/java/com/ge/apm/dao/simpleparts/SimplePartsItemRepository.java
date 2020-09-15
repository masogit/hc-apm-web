package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SimplePartsItemRepository extends JpaRepository<SimplePartsItem, String> {

    SimplePartsItem findById(String id);

    Optional<SimplePartsItem> findByName(String name);

    @Query(value = "select * from simple_parts_item where category_code = ?1", nativeQuery = true)
    List<SimplePartsItem> findByCategoryCode(String encoded);

    @Query(value = "select * from simple_parts_item where tenant_uid = ?1", nativeQuery = true)
    List<SimplePartsItem> findByTenantUid(String tenantUID);
}
