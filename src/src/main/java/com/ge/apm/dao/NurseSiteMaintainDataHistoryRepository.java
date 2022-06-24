package com.ge.apm.dao;

import com.ge.apm.domain.NurseSiteMaintainDataHistory;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;
import java.util.List;
import java.util.Set;

/**
 * Created by 503035627
 */
public interface NurseSiteMaintainDataHistoryRepository extends GenericRepositoryUUID<NurseSiteMaintainDataHistory> {
	
	@Query(value="select o.* from nurse_site_maintain_data_history o where o.parent_id in (select b.id from nurse_site_maintain_item_history where b.parent_id in (select c.id from nurse_site_maintain where c.id=?1))",nativeQuery=true)
	public Set<NurseSiteMaintainDataHistory> findMaintainDataByMaintainId(String maintainId);
	
	@Query(value="select o.* from nurse_site_maintain_data_history o where o.parent_id in ?1 order by index",nativeQuery=true)
	public List<NurseSiteMaintainDataHistory> findListMaintainDataByParentIds(Set<String> parentIds);

    public List<NurseSiteMaintainDataHistory> findLByParentId(String parentId);

}
