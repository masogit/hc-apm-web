package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import webapp.framework.dao.GenericRepositoryUUID;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件库存Repository
 * @package com.ge.apm.dao
 */
public interface SimplePartsTransactionRepository extends GenericRepositoryUUID<SimplePartsTransaction>, JpaRepository<SimplePartsTransaction, String> {

}
