package com.ge.apm.dao.simpleparts;

import com.ge.apm.domain.simpleparts.SimplePartsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author csh
 * @version 1.0
 * @date 2020/6/17
 * @descript 备件库存Repository
 * @package com.ge.apm.dao
 */
public interface SimplePartsTransactionRepository extends GenericRepositoryUUID<SimplePartsTransaction>, JpaRepository<SimplePartsTransaction, String> {

    @Query(value = "select * from simple_parts_transaction where parts_request_id = ?1 and request_code = ?2 and transaction_type = 1", nativeQuery = true)
    List<SimplePartsTransaction> findByPartsRequestIdAndRequestCode(String partsRequestId,String requestCode);

}
