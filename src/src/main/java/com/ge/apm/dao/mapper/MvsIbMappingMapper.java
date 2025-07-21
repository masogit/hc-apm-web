package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ge.apm.domain.MvsIbMapping;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MvsIbMappingMapper extends BaseMapper<MvsIbMapping> {

    @Select("select distinct mvs.mvs_ib_id from mvs_ib_mapping mvs where mvs.ib_id = #{assetId} and mvs.mvs_ib_id is not null")
    List<Integer> findMvsAssetIds(@Param("assetId") Integer assetId);

    @Select("select  DISTINCT COALESCE(mvs.mvs_ib_uid, mvs.e_home_ib_uid) AS mvs_ib_uid from mvs_ib_mapping mvs where mvs.ib_uid = #{assetUId} ")
    List<String> findMvsAssetUIds(@Param("assetUId") String assetUId);

    @Insert("INSERT INTO mvs_ib_mapping(" +
            "  create_time,  " +
            "  created_by,  " +
            "  ib_id,  " +
            "  ib_system_id,  " +
            "  ib_system_num,  " +
            "  ib_tenant_name,  " +
            "  ib_tenant_uid,  " +
            "  modify_time,  " +
            "  mvs_ib_id,  " +
            "  mvs_qrcode  " +
            ")  " +
            " SELECT  " +
            "  CURRENT_TIMESTAMP,         " +
            "  'system',             " +
            "  ai.id as ib_id,  " +
            "  ai.system_id as ib_system_id,  " +
            "  ai.system_num1 as ib_system_num,  " +
            "  ti.name as ib_tenant_name,  " +
            "  ti.uid as ib_tenant_uid,  " +
            "  CURRENT_TIMESTAMP,         " +
            "  mvs.id as mvs_ib_id,  " +
            "  mvs.qr_code as mvs_qrcode  " +
            " FROM   " +
            "  asset_info ai,  " +
            "  asset_info mvs,  " +
            "  tenant_info ti   " +
            " WHERE   " +
            "  mvs.tenant_uid ='68fc4f19938546d7bdfaf2d405cd6e6d' and ai.tenant_uid !='68fc4f19938546d7bdfaf2d405cd6e6d'" +
            "  AND ai.tenant_uid = ti.uid  " +
            "  AND (  " +
            "    (mvs.qr_code = SUBSTRING(ai.system_id, 5) AND ai.system_id LIKE 'MVS-%')  " +
            "    OR (mvs.qr_code = SUBSTRING(ai.system_num1, 5) AND ai.system_num1 LIKE 'MVS-%')  " +
            ")")
    void initMvsMapping();
}
