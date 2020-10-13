package com.ge.apm.dao;

import com.ge.apm.domain.AssetTagRule;
import com.ge.apm.domain.NurseSiteMaintainConfigItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface AssetTagRuleRepository extends GenericRepository<AssetTagRule> {

    public List<AssetTagRule> findByTagId(Integer tagId);
    public void deleteByTagId(Integer tagId);

    @Query(value="select o.* from asset_tag_rule o where o.asset_id in ?1",nativeQuery=true)
    public List<AssetTagRule> findByAssetUid(List<Integer> ids);

    @Transactional
    @Modifying
    @Query(value = "delete from asset_tag_rule where id in ?1", nativeQuery = true)
    public void deleteAssetTagRuleData(Set<Integer> ids);
}
