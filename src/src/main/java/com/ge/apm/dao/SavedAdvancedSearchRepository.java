package com.ge.apm.dao;

import com.ge.apm.domain.SavedAdvancedSearch;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface SavedAdvancedSearchRepository extends GenericRepositoryUUID<SavedAdvancedSearch> {

    @Query(value = "SELECT s FROM SavedAdvancedSearch s WHERE s.userUid = ?1 ORDER BY s.createdDate DESC")
    List<SavedAdvancedSearch> findByUserUid(String userUid);

    @Query(value = "SELECT COUNT(s) FROM SavedAdvancedSearch s WHERE s.userUid = ?1")
    long countByUserUid(String userUid);
}
