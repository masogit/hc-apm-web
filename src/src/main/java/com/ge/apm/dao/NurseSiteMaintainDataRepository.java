package com.ge.apm.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ge.apm.domain.NurseSiteMaintainData;

import webapp.framework.dao.GenericRepositoryUUID;
/**
 * Created by 212605082 on 2018/3/7.
 */
public interface NurseSiteMaintainDataRepository extends GenericRepositoryUUID<NurseSiteMaintainData> {
	
	@Query(value="select o.* from nurse_site_maintain_data o where o.parent_id in (select b.id from nurse_site_maintain_item where b.parent_id in (select c.id from nurse_site_maintain where c.id=?1))",nativeQuery=true)
	public Set<NurseSiteMaintainData> findMaintainDataByMaintainId(String maintainId);
}
