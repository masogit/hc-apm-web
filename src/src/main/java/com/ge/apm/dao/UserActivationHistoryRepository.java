package com.ge.apm.dao;

import com.ge.apm.domain.Brand;
import com.ge.apm.domain.UserActivationHistory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface UserActivationHistoryRepository extends GenericRepository<UserActivationHistory> {

}
