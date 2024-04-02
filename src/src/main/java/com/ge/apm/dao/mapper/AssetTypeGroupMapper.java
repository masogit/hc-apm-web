package com.ge.apm.dao.mapper;

import com.ge.apm.domain.AssetTypeGroup;
import com.ge.apm.domain.dto.AssetTypeGroupDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AssetTypeGroupMapper {

	@Select(value = "WITH recursive child AS (\n" +
			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN `user_asset_group` uag ON atg.uid = uag.asset_group_uid WHERE uag.user_uid = #{userUid} \n" +
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



//	@Select(value = "WITH recursive child AS (\n" +
//			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN user_asset_group uag ON atg.uid = uag.asset_group_uid WHERE uag.user_uid = #{userUid} \n" +
//			"    union all\n" +
//			"    SELECT atg.*, true as accessible FROM asset_type_group atg JOIN child child ON atg.parent_uid = child.uid\n" +
//			")" +
//			"select * from child"
//			)


	@Select({
			"<script>"+
			"WITH recursive child AS (\n" +
			"    SELECT atg.*,uag.user_uid   FROM asset_type_group atg JOIN user_asset_group uag ON atg.uid = uag.asset_group_uid WHERE uag.user_uid in " +
					"<foreach item = 'item' index = 'index' collection = 'userUids' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"    union all " +
					"    SELECT atg.*, child.user_uid as userUid   FROM asset_type_group atg JOIN child child ON atg.parent_uid = child.uid" +
					")" +
					"select * from child"+
					"</script>"})
	List<AssetTypeGroupDto> getAssetTypeByUserUids(@Param("userUids") List<String> userUids);



	@Select({
			"<script>"+
					"WITH recursive child AS (\n" +
					"    SELECT atg.*  FROM asset_type_group atg  WHERE atg.uid in " +
					"<foreach item = 'item' index = 'index' collection = 'uids' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"    union all " +
					"    SELECT atg.* as userUid   FROM asset_type_group atg JOIN child child ON atg.parent_uid = child.uid" +
					")" +
					"select * from child"+
					"</script>"})
	List<AssetTypeGroup> getAssetTypeByUids(@Param("uids") List<String> uids);

	@Select("SELECT atg.uid, atg.name, atg.encoded, atg.parent_uid parentUid, uag.user_uid FROM asset_type_group atg left join user_asset_group uag on atg.uid = uag.asset_group_uid where atg.tenant_uid = #{tenantUid}")
	List<AssetTypeGroupDto> findDtoByTenantUid(@Param("tenantUid") String tenantUid);

}
