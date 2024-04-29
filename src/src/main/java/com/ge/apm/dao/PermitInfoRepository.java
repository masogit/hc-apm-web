package com.ge.apm.dao;

import com.ge.apm.domain.PermitInfo;
import com.ge.apm.domain.SpecialAssetPermitInfo;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

public interface PermitInfoRepository extends GenericRepositoryUUID<PermitInfo> {



    @Query(value = "select pi.* from special_asset_permit_info sa left join permit_info pi on sa.permit_info_uid=pi.uid where sa.special_asset_id=?1 ",nativeQuery = true)
    List<PermitInfo> findBySpecialAssetId(String assetId);

}
