package com.ge.apm.dao.mapper;

import com.ge.apm.domain.SharedAssetRequest;
import org.apache.ibatis.annotations.Select;

public interface SharedAssetRequestMapper {

    @Select("SELECT * FROM shared_asset_request WHERE id=#{id}")
    SharedAssetRequest findRequestById(String id);
}
