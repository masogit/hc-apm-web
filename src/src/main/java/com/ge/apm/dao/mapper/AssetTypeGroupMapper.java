package com.ge.apm.dao.mapper;

import com.ge.apm.domain.SysAuthGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AssetTypeGroupMapper {

	@Select(value = "with recursive cte_child_auth_group as (\n" +
			"    select sag.uid, sag.id, sag.name, sag.parent_uid, sag.encoded, sag.classification_code, sag.classification_path, true as accessible \n" +
			"    from asset_type_group sag join user_auth_group uag on uag.auth_group_uid = sag.uid\n" +
			"    where uag.user_uid = #{userUid} \n" +
			"    union all\n" +
			"    select sag.uid, sag.id, sag.name, sag.parent_uid, sag.encoded, sag.classification_code, sag.classification_path, true as accessible \n" +
			"    from asset_type_group sag\n" +
			"    inner join cte_child_auth_group cte_child\n" +
			"    on sag.parent_uid = cte_child.uid\n" +
			"), cte_parent_auth_group as (\n" +
			"    select sag.uid, sag.id, sag.name, sag.parent_uid, sag.encoded, sag.classification_code, sag.classification_path, true as accessible\n" +
			"    from asset_type_group sag join user_auth_group uag on uag.auth_group_uid = sag.uid\n" +
			"    where uag.user_uid = #{userUid} \n" +
			"    union all\n" +
			"    select sag.uid, sag.id, sag.name, sag.parent_uid, sag.encoded, sag.classification_code, sag.classification_path, false as accessible\n" +
			"    from asset_type_group sag\n" +
			"    inner join cte_parent_auth_group cte_parent\n" +
			"    on sag.uid = cte_parent.parent_uid\n" +
			")\n" +
			"select * from cte_child_auth_group\n" +
			"union\n" +
			"select * from cte_parent_auth_group")
	List<SysAuthGroup> listSysAuthGroupInHierarchyByUserWithAncestorIncluded(@Param("userUid") String userUid);

}
