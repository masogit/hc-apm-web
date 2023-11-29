package com.ge.apm.dao;

import com.ge.apm.domain.ExternalTempUser;
import webapp.framework.dao.GenericRepository;

public interface ExternalTempUserRepository extends GenericRepository<ExternalTempUser> {

    ExternalTempUser findByWoId(String woId);

}
