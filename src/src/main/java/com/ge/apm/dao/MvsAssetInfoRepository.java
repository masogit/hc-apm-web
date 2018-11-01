package com.ge.apm.dao;

import com.ge.apm.domain.MvsAssetInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

public interface MvsAssetInfoRepository extends GenericRepository<MvsAssetInfo> {

    public MvsAssetInfo findById(String assetUid);

//    @Query("select o from MvsAssetInfo o where o.id = ?1")
//    public List<MvsAssetInfo> findByUid(String assetUid);
}
