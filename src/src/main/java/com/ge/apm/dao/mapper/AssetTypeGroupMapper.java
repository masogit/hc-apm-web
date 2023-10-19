package com.ge.apm.dao.mapper;

import com.ge.apm.domain.AssetTypeGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AssetTypeGroupMapper {

	@Select(value = "WITH recursive child AS (\n" +
			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN user_asset_group uag ON atg.uid = uag.asset_group_uid WHERE uag.user_uid = #{userUid} \n" +
			"    union all\n" +
			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN child child ON atg.parent_uid = child.uid\n" +
			"), parent as (\n" +
			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN user_asset_group uag ON atg.uid = uag.asset_group_uid WHERE uag.user_uid = #{userUid} \n" +
			"    union all\n" +
			"    SELECT atg.*, false as accessible FROM asset_type_group atg JOIN parent parent ON atg.uid = parent.parent_uid\n" +
			")\n" +
			"select * from child\n" +
			"union\n" +
			"select * from parent")
	List<AssetTypeGroup> listAssetTypeGroupInHierarchyByUserWithAncestorIncluded(@Param("userUid") String userUid);

}
